/**
 * Copyright 2011 Rowan Seymour
 *
 * This file is part of Refract.
 *
 * Refract is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Refract is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Refract. If not, see <http://www.gnu.org/licenses/>.
 */

#include "inc/refract.h"

static int g_last_renderer_id = 0;
static int g_pal_cycle_offset = 0;

/**
 * Iteration functions from iterate.h
 */
void refract_renderer_iterate_m2(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);
void refract_renderer_iterate_m3(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);
void refract_renderer_iterate_m4(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);

void refract_renderer_histogram(renderer_t* renderer);
void refract_renderer_histogram_autoscale(renderer_t* renderer, iterc_t* min, iterc_t* max);
uint32_t refract_renderer_histogram_total(renderer_t* renderer);

bool refract_params_equal(params_t* p1, params_t* p2);

/**
 * Allocates a renderer
 */
bool refract_renderer_init(renderer_t* renderer, int width, int height) {
	// Initialize renderer
	memset(renderer, 0, sizeof (renderer_t));
	renderer->id = ++g_last_renderer_id;

	// Allocate palette index buffer
	if ((renderer->palette_indexes = malloc(ITERC_MAX * sizeof (int))) == NULL)
		return false;

	// Allocate iters histogram buffer
	if ((renderer->iter_histogram = malloc((ITERC_MAX + 1) * sizeof (int))) == NULL)
		return false;

	// Allocate screen buffers
	if (!refract_renderer_resize(renderer, width, height))
		return false;

	// Initialize mutexes
	pthread_mutex_init(&renderer->buffers_mutex, NULL);

	return true;
}

/**
 * Resizes a renderer
 */
bool refract_renderer_resize(renderer_t* renderer, int width, int height) {
	// Lock access to buffers so they can't be drawn on or freed while we're iterating
	pthread_mutex_lock(&renderer->buffers_mutex);

	// Free screen buffers
	SAFE_FREE(renderer->iter_buffer);
	SAFE_FREE(renderer->z_cache);

	renderer->width = width;
	renderer->height = height;

	// Allocate buffers
	renderer->iter_buffer = malloc(sizeof (iterc_t) * width * height);
	renderer->z_cache = malloc(sizeof (complex_t) * width * height);

	// Check buffers were allocated
	if (!renderer->iter_buffer || !renderer->z_cache) {
		refract_renderer_free(renderer);
		return false;
	}

	// Unlock access to buffers
	pthread_mutex_unlock(&renderer->buffers_mutex);

	return true;
}

/**
 * Iterates the renderer by the given number of iterations
 */
iterc_t refract_renderer_iterate(renderer_t* renderer, params_t* params, iterc_t iters) {

	bool use_cache = refract_params_equal(&renderer->cache_params, params) && renderer->cache_valid;

	// Lock access to buffers so they can't be resized or freed while we're iterating
	pthread_mutex_lock(&renderer->buffers_mutex);

	// Increment or reset max-iters depending on whether we'll be using the cache
	iterc_t max_iters = use_cache ? (renderer->cache_max_iters + iters) : iters;

	switch (params->func) {
	case MANDELBROT:
		refract_renderer_iterate_m2(renderer, params->offset, params->zoom, max_iters, use_cache);
		break;
	case MANDELBROT_3:
		refract_renderer_iterate_m3(renderer, params->offset, params->zoom, max_iters, use_cache);
		break;
	case MANDELBROT_4:
		refract_renderer_iterate_m4(renderer, params->offset, params->zoom, max_iters, use_cache);
		break;
	}

	// Update cache status
	renderer->cache_max_iters = max_iters;
	renderer->cache_params = *params;
	renderer->cache_valid = true;

	// Unlock access to buffers
	pthread_mutex_unlock(&renderer->buffers_mutex);

	return renderer->cache_max_iters;
}

/**
 * Renders a renderer to the given pixel buffer
 */
void refract_renderer_render(renderer_t* renderer, color_t* pixels, int stride, mapping_t mapping) {
	// Lock access to buffers
	pthread_mutex_lock(&renderer->buffers_mutex);

	// Number of iters to be considered in the set
	const iterc_t max_iters = renderer->cache_max_iters;

	// Gather up frequently used items
	const iterc_t* restrict iter_buffer = renderer->iter_buffer;
	const int pal_size = renderer->palette.size;
	const int pal_index_max = pal_size - 1;
	const int pal_offset = g_pal_cycle_offset++;
	int* restrict indexes = renderer->palette_indexes;

	switch (mapping) {
	case REPEAT:
		for (int i = 0; i < max_iters; ++i)
			indexes[i] = i % pal_size;
		break;
	case REPEAT_CYCLE:
			for (int i = 0; i < max_iters; ++i)
				indexes[i] = (i + pal_offset) % pal_size;
			break;
	case CLAMP:
		for (int i = 0; i < max_iters; ++i)
			indexes[i] = MIN(i, pal_index_max); // TODO optimize?
		break;
	case SCALE_GLOBAL:
		for (int i = 0; i < max_iters; ++i)
			indexes[i] = pal_size * i / max_iters;
		break;
	case SCALE_AUTO: {
			iterc_t min, max;
			refract_renderer_histogram(renderer);
			refract_renderer_histogram_autoscale(renderer, &min, &max);
			int range = max - min;
			for (int i = min; i < max_iters; ++i)
				indexes[i] = MIN(pal_size * (i - min) / range, pal_size);
			break;
		}
	case HISTOGRAM: {
			refract_renderer_histogram(renderer);
			uint32_t total = refract_renderer_histogram_total(renderer);
			uint32_t pal_item_size = total / pal_size;
			uint32_t histo_acc = 0;
			const uint32_t* restrict histo = renderer->iter_histogram;

			for (int i = 0; i < max_iters; ++i) {
				histo_acc += histo[i];
				indexes[i] = histo_acc / pal_item_size;
			}
		}
	}

	const color_t* restrict colors = renderer->palette.colors;
	color_t* restrict line = pixels;
	color_t set_color = renderer->palette.set_color;

	// Fill pixel buffer based on palette indexes
	for (int y = 0, index = 0; y < renderer->height; ++y) {
		for (int x = 0; x < renderer->width; ++x, ++index) {
			iterc_t iterc = iter_buffer[index];
			line[x] = (iterc == max_iters) ? set_color : colors[indexes[iterc]];
		}
		line = (color_t*)((char*)line + stride);
	}

	// Unlock access to buffers
	pthread_mutex_unlock(&renderer->buffers_mutex);
}

/**
 * Frees a renderer
 */
void refract_renderer_free(renderer_t* renderer) {
	// Lock access to buffers
	pthread_mutex_lock(&renderer->buffers_mutex);

	// Free palette
	refract_palette_free(&renderer->palette);

	// Free palette indexes
	SAFE_FREE(renderer->palette_indexes);

	// Free iters histogram
	SAFE_FREE(renderer->iter_histogram);

	// Free screen buffers
	SAFE_FREE(renderer->iter_buffer);
	SAFE_FREE(renderer->z_cache);

	// Unlock access to buffers
	pthread_mutex_unlock(&renderer->buffers_mutex);

	// Free mutexes
	pthread_mutex_destroy(&renderer->buffers_mutex);
}

/**
 * Calculates a histogram of iteration values
 */
void refract_renderer_histogram(renderer_t* renderer) {
	const iterc_t* restrict iters = renderer->iter_buffer;
	uint32_t* restrict histo = renderer->iter_histogram;

	// Zeroize iter counts
	for (int c = 0; c < renderer->cache_max_iters; ++c)
		histo[c] = 0;

	// Accumulate iter counts
	for (int i = 0; i < (renderer->width * renderer->height); ++i)
		++histo[iters[i]];
}

/**
 * Analyzes a histogram to find minimum, maximum
 */
void refract_renderer_histogram_autoscale(renderer_t* renderer, iterc_t* min, iterc_t* max) {
	const uint32_t* restrict histo = renderer->iter_histogram;

	// Find minimum iteration value
	for (int i = 0; i <= ITERC_MAX; ++i) {
		if (histo[i] > 0) {
			*min = i;
			break;
		}
	}

	// Find value that covers all but top 0.5% of remaining iteration values for palette end
	uint32_t cumul_histo = 0;
	uint32_t threshold = (5 * renderer->width * renderer->height) / 1000;

	// Don't include pixels which are in the set
	for (int i = renderer->cache_max_iters - 1; i >= 0; --i) {
		cumul_histo += histo[i];
		if (cumul_histo >= threshold) {
			*max = i;
			break;
		}
	}
}

/**
 * Calculates the total of a histogram
 */
uint32_t refract_renderer_histogram_total(renderer_t* renderer) {
	const uint32_t* restrict histo = renderer->iter_histogram;
	uint32_t total = 0;

	for (int i = 0; i < renderer->cache_max_iters; ++i)
		total += histo[i];

	return total;
}

/**
 * Checks if two params objects are equal
 */
bool refract_params_equal(params_t* p1, params_t* p2) {
	return p1->func == p2->func && p1->offset.re == p2->offset.re && p1->offset.im == p2->offset.im && p1->zoom == p2->zoom;
}

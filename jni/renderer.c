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

/**
 * Iteration functions from iterate.h
 */
void refract_renderer_iterate_m2(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);
void refract_renderer_iterate_m3(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);
void refract_renderer_iterate_m4(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);

/**
 * Allocates a renderer
 */
bool refract_renderer_init(renderer_t* renderer, int width, int height) {
	// Initialize renderer
	memset(renderer, 0, sizeof (renderer_t));
	renderer->id = ++g_last_renderer_id;
	renderer->params.func = MANDELBROT;
	renderer->params.offset.re = 0;
	renderer->params.offset.im = 0;
	renderer->params.zoom = width / 2;

	// Allocate buffers
	if (!refract_renderer_resize(renderer, width, height))
		return false;

	// Initialize mutexes
	pthread_mutex_init(&renderer->params_mutex, NULL);
	pthread_mutex_init(&renderer->buffers_mutex, NULL);

	return true;
}

/**
 * Resizes a renderer
 */
bool refract_renderer_resize(renderer_t* renderer, int width, int height) {
	// Lock access to buffers so they can't be drawn on or freed while we're iterating
	pthread_mutex_lock(&renderer->buffers_mutex);

	// Free buffers
	if (renderer->iter_buffer)
		free(renderer->iter_buffer);
	if (renderer->z_cache)
		free(renderer->z_cache);

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

	// Zeroize cache buffers
	//memset(renderer->iter_buffer, 0, sizeof (iterc_t) * width * height);
	//memset(renderer->z_cache, 0, sizeof (complex_t) * width * height);

	// Unlock access to buffers
	pthread_mutex_unlock(&renderer->buffers_mutex);

	return true;
}

/**
 * Sets the function which invalidates the caches
 */
void refract_renderer_setfunction(renderer_t* renderer, func_t func) {
	pthread_mutex_lock(&renderer->params_mutex);

	renderer->params.func = func;
	renderer->cache_valid = false;

	pthread_mutex_unlock(&renderer->params_mutex);
}

/**
 * Sets the offset which invalidates the caches
 */
void refract_renderer_setoffset(renderer_t* renderer, complex_t offset) {
	pthread_mutex_lock(&renderer->params_mutex);

	renderer->params.offset = offset;
	renderer->cache_valid = false;

	pthread_mutex_unlock(&renderer->params_mutex);
}

/**
 * Sets the zoom factor which invalidates the caches
 */
void refract_renderer_setzoom(renderer_t* renderer, float_t zoom) {
	pthread_mutex_lock(&renderer->params_mutex);

	renderer->params.zoom = zoom;
	renderer->cache_valid = false;

	pthread_mutex_unlock(&renderer->params_mutex);
}

/**
 * Iterates the renderer by the given number of iterations
 */
iterc_t refract_renderer_iterate(renderer_t* renderer, iterc_t iters) {
	// Another thread might try to change the render parameters
	pthread_mutex_lock(&renderer->params_mutex);

	// Gather the parameters for these iterations while we have exclusive access
	params_t params = renderer->params;
	bool use_cache = renderer->cache_valid;
	renderer->cache_valid = true;

	pthread_mutex_unlock(&renderer->params_mutex);

	// Lock access to buffers so they can't be resized or freed while we're iterating
	pthread_mutex_lock(&renderer->buffers_mutex);

	// Increment or reset max-iters depending on whether we'll be using the cache
	iterc_t max_iters = use_cache ? (renderer->cache_max_iters + iters) : iters;

	switch (params.func) {
	case MANDELBROT:
		refract_renderer_iterate_m2(renderer, params.offset, params.zoom, max_iters, use_cache);
		break;
	case MANDELBROT_3:
		refract_renderer_iterate_m3(renderer, params.offset, params.zoom, max_iters, use_cache);
		break;
	case MANDELBROT_4:
		refract_renderer_iterate_m4(renderer, params.offset, params.zoom, max_iters, use_cache);
		break;
	}

	// Update cache status
	renderer->cache_max_iters = max_iters;

	// Unlock access to buffers
	pthread_mutex_unlock(&renderer->buffers_mutex);

	return renderer->cache_max_iters;
}

/**
 * Renders a renderer to the given pixel buffer
 */
void refract_renderer_render(renderer_t* renderer, color_t* pixels, int stride) {
	// Lock access to buffers
	pthread_mutex_lock(&renderer->buffers_mutex);

	// Number of iters to be considered in the set
	const iterc_t max_iters = renderer->cache_max_iters;

	// Gather up frequently used items
	const iterc_t* restrict iter_buffer = renderer->iter_buffer;
	const color_t* restrict colors = renderer->palette.colors;
	const int pal_size = renderer->palette.size;

	// Render cached iteration values into pixels
	color_t* restrict line = pixels;

	switch (renderer->palette_mapping) {
	case REPEAT:
		for (int y = 0, index = 0; y < renderer->height; ++y) {
			for (int x = 0; x < renderer->width; ++x, ++index) {
				iterc_t iterc = iter_buffer[index];
				int pal_index = iterc % pal_size;
				line[x] = (iterc == max_iters) ? BLACK : colors[pal_index];
			}
			line = (color_t*)((char*)line + stride);
		}
		break;
	case CLAMP:
		for (int y = 0, index = 0; y < renderer->height; ++y) {
			for (int x = 0; x < renderer->width; ++x, ++index) {
				iterc_t iterc = iter_buffer[index];
				int pal_index = MIN(iterc, pal_size);
				line[x] = (iterc == max_iters) ? BLACK : colors[pal_index];
			}
			line = (color_t*)((char*)line + stride);
		}
		break;
	case SCALE:
		// TODO
		break;
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

	// Free renderer's palette
	refract_palette_free(&renderer->palette);

	// Free buffers
	if (renderer->iter_buffer) {
		free(renderer->iter_buffer);
		renderer->iter_buffer = NULL;
	}
	if (renderer->z_cache) {
		free(renderer->z_cache);
		renderer->z_cache = NULL;
	}

	// Unlock access to buffers
	pthread_mutex_unlock(&renderer->buffers_mutex);

	// Free mutexes
	pthread_mutex_destroy(&renderer->params_mutex);
	pthread_mutex_destroy(&renderer->buffers_mutex);
}

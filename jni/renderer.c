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
	renderer->width = width;
	renderer->height = height;
	renderer->params.func = MANDELBROT;
	renderer->params.offset.re = 0;
	renderer->params.offset.im = 0;
	renderer->params.zoom = width / 2;

	// Allocate buffers
	renderer->iter_buffer = malloc(sizeof (iterc_t) * width * height);
	renderer->z_cache = malloc(sizeof (complex_t) * width * height);

	// Check buffers were allocated
	if (!renderer->iter_buffer || !renderer->z_cache) {
		refract_renderer_free(renderer);
		return false;
	}

	// Zeroize cache buffers
	memset(renderer->iter_buffer, 0, sizeof (iterc_t) * width * height);
	memset(renderer->z_cache, 0, sizeof (complex_t) * width * height);

	// Initialize renderer mutex
	pthread_mutex_init(&renderer->mutex, NULL);

	return true;
}

/**
 * Sets the function which invalidates the caches
 */
void refract_renderer_setfunction(renderer_t* renderer, func_t func) {
	refract_renderer_acquire_lock(renderer);

	renderer->params.func = func;
	renderer->cache_valid = false;

	refract_renderer_release_lock(renderer);
}

/**
 * Sets the offset which invalidates the caches
 */
void refract_renderer_setoffset(renderer_t* renderer, complex_t offset) {
	refract_renderer_acquire_lock(renderer);

	renderer->params.offset = offset;
	renderer->cache_valid = false;

	refract_renderer_release_lock(renderer);
}

/**
 * Sets the zoom factor which invalidates the caches
 */
void refract_renderer_setzoom(renderer_t* renderer, float_t zoom) {
	refract_renderer_acquire_lock(renderer);

	renderer->params.zoom = zoom;
	renderer->cache_valid = false;

	refract_renderer_release_lock(renderer);
}

/**
 * Performs iterations
 */
void refract_renderer_iterate(renderer_t* renderer, iterc_t iters, params_t params, bool use_cache) {
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
}

/**
 * Renders a renderer to the given pixel buffer
 */
void refract_renderer_render(renderer_t* renderer, color_t* pixels, int stride) {
	// Number of iters to be considered in the set
	const iterc_t max_iters = renderer->cache_max_iters;

	// Gather up frequently used items
	const iterc_t* restrict iter_buffer = renderer->iter_buffer;
	const color_t* restrict colors = renderer->palette.colors;
	const int pal_size = renderer->palette.size;
	const int pal_offset = renderer->palette.offset;

	// Render cached iteration values into pixels
	color_t* restrict line = pixels;

	for (int y = 0, index = 0; y < renderer->height; ++y) {
		for (int x = 0; x < renderer->width; ++x, ++index) {
			iterc_t iterc = iter_buffer[index];
			int pal_index = (iterc + pal_offset) % pal_size;
			line[x] = (iterc == max_iters) ? BLACK : colors[pal_index];
		}

		// go to next line
		line = (color_t*)((char*)line + stride);
	}
}

/**
 * Acquires lock on the renderer
 */
bool refract_renderer_acquire_lock(renderer_t* renderer) {
	return pthread_mutex_lock(&renderer->mutex) == 0;
}

/**
 * Releases lock on the renderer
 */
bool refract_renderer_release_lock(renderer_t* renderer) {
	return pthread_mutex_unlock(&renderer->mutex) == 0;
}

/**
 * Frees a renderer
 */
void refract_renderer_free(renderer_t* renderer) {
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

	// Free render params mutex
	pthread_mutex_destroy(&renderer->mutex);
}

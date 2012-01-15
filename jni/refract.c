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
 * Allocates a context
 */
refract_context* refract_init(int width, int height) {
	// Allocate context
	refract_context* context = (refract_context*)malloc(sizeof (refract_context));
	if (!context)
		return NULL;

	// Initialize context
	memset(context, 0, sizeof (refract_context));
	context->width = width;
	context->height = height;
	context->params.func = MANDELBROT;
	context->params.offset.re = 0;
	context->params.offset.im = 0;
	context->params.zoom = width / 2;

	// Allocate buffers
	context->iter_buffer = malloc(sizeof (iterc_t) * width * height);
	context->z_cache = malloc(sizeof (complex_t) * width * height);

	// Check buffers were allocated
	if (!(context->iter_buffer && context->z_cache)) {
		refract_free(context);
		return NULL;
	}

	// Zeroize cache buffers
	memset(context->iter_buffer, 0, sizeof (iterc_t) * width * height);
	memset(context->z_cache, 0, sizeof (complex_t) * width * height);

	return context;
}

/**
 * Performs iterations
 */
void refract_iterate(refract_context* context, iterc_t iters, params_t params, bool use_cache) {
	// Increment or reset max-iters depending on whether we'll be using the cache
	iterc_t max_iters = use_cache ? (context->cache_max_iters + iters) : iters;

	switch (params.func) {
	case MANDELBROT:
		refract_iterate_m2(context, params.offset, params.zoom, max_iters, use_cache);
		break;
	case MANDELBROT_3:
		refract_iterate_m3(context, params.offset, params.zoom, max_iters, use_cache);
		break;
	case MANDELBROT_4:
		refract_iterate_m4(context, params.offset, params.zoom, max_iters, use_cache);
		break;
	}

	// Update cache status
	context->cache_max_iters = max_iters;
}

/**
 * Renders a context to the given pixel buffer
 */
void refract_render(refract_context* context, color_t* pixels, int stride) {
	// Number of iters to be considered in the set
	const iterc_t max_iters = context->cache_max_iters;

	// Gather up frequently used items
	const iterc_t* restrict iter_buffer = context->iter_buffer;
	const color_t* restrict colors = context->palette.colors;
	const int pal_size = context->palette.size;
	const int pal_offset = context->palette.offset;

	// Render cached iteration values into pixels
	color_t* restrict line = pixels;

	for (int y = 0, index = 0; y < context->height; ++y) {
		for (int x = 0; x < context->width; ++x, ++index) {
			iterc_t iterc = iter_buffer[index];
			int pal_index = (iterc + pal_offset) % pal_size;
			line[x] = (iterc == max_iters) ? BLACK : colors[pal_index];
		}

		// go to next line
		line = (color_t*)((char*)line + stride);
	}
}

/**
 * Frees a context
 */
void refract_free(refract_context* context) {
	// Free context's palette
	refract_palette_free(&context->palette);

	// Free buffers
	if (context->iter_buffer)
		free(context->iter_buffer);
	if (context->z_cache)
		free(context->z_cache);

	// Free context itself
	free(context);
}

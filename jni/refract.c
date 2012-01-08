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
refract_context* refract_init(uint16_t width, uint16_t height) {
	// Allocate context
	refract_context* context = (refract_context*)malloc(sizeof (refract_context));
	if (!context)
		return NULL;

	// Initialize context
	memset(context, 0, sizeof (refract_context));
	context->width = width;
	context->height = height;
	context->iters_per_frame = DEF_ITERSPERFRAME;

	// Allocate cache buffers
	context->iter_cache = malloc(sizeof (iterc_t) * width * height);
	context->z_cache = malloc(sizeof (complex_t) * width * height);

	// Check buffers were allocated
	if (!(context->iter_cache && context->z_cache)) {
		refract_free(context);
		return NULL;
	}

	// Zeroize cache buffers
	memset(context->iter_cache, 0, sizeof (iterc_t) * width * height);
	memset(context->z_cache, 0, sizeof (complex_t) * width * height);

	return context;
}

/**
 * Renders a context to the given pixel buffer
 */
void refract_render(refract_context* context, color_t* pixels, int stride, complex_t offset, float_t zoom) {
	// Iterate fractal rendering
	refract_iterate(context, FUNC_MANDELBROT, offset, zoom);

	// Number of iters to be considered in the set
	iterc_t max_iters = context->cache_max_iters;

	iterc_t* iters = context->iter_cache;
	refract_palette* palette = context->palette;

	// Render cached iteration values into pixels
	for (int y = 0, index = 0; y < context->height; ++y) {
		color_t* line = (color_t*)pixels;

		for (int x = 0; x < context->width; ++x, ++index) {
			iterc_t iterc = iters[index];

			line[x] = (iterc == max_iters) ? BLACK : palette->colors[iterc % palette->size];
		}

		// go to next line
		pixels = (color_t*)((char*)pixels + stride);
	}
}

/**
 * Frees a context
 */
void refract_free(refract_context* context) {
	// Free palette
	refract_palette_free(context->palette);

	// Free cache buffers
	if (context->iter_cache)
		free(context->iter_cache);
	if (context->z_cache)
		free(context->z_cache);

	// Free context itself
	free(context);
}

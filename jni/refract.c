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

#include "refract.h"
#include "iterate.h"

/**
 * Allocates a context
 */
refract_context* refract_init(uint32_t width, uint32_t height) {
	// Allocate context
	refract_context* context = (refract_context*)malloc(sizeof (refract_context));
	if (!context)
		return 0;

	// Initialize context
	memset(context, 0, sizeof (refract_context));
	context->width = width;
	context->height = height;

	// Allocate cache buffers
	context->iter_cache = malloc(sizeof (iterc_t) * width * height);
	context->real_cache = malloc(sizeof (float_t) * width * height);
	context->imag_cache = malloc(sizeof (float_t) * width * height);

	// Check buffers were allocated
	if (!(context->iter_cache && context->real_cache && context->imag_cache)) {
		refract_free(context);
		return 0;
	}

	// Zeroize cache buffers
	memset(context->iter_cache, 0, sizeof (iterc_t) * width * height);
	memset(context->real_cache, 0, sizeof (float_t) * width * height);
	memset(context->imag_cache, 0, sizeof (float_t) * width * height);

	return context;
}

/**
 * Renders a context to the given pixel buffer
 */
void refract_render(refract_context* context, pixel_t* pixels, int stride, float_t real, float_t imag, float_t zoom) {
	// Iterate fractal rendering
	refract_iterate(context, FUNC_MANDELBROT, real, imag, zoom);

	// Number of iters to be considered in the set
	int max_iters = context->last_max_iters;

	// Render iteration values into pixels
	for (int y = 0, index = 0; y < context->height; ++y) {
		pixel_t* line = (pixel_t*)pixels;

		for (int x = 0; x < context->width; ++x, ++index) {
			iterc_t niters = context->iter_cache[index];

			if (niters == max_iters)
				line[x] = BLACK;
			else {
				line[x] = (niters % 2 == 0) ? RED : BLUE;
			}
		}

		// go to next line
		pixels = (pixel_t*)((char*)pixels + stride);
	}
}

/**
 * Frees a context
 */
void refract_free(refract_context* context) {
	// Free cache buffers
	if (context->iter_cache)
		free(context->iter_cache);
	if (context->real_cache)
		free(context->real_cache);
	if (context->imag_cache)
		free(context->imag_cache);

	// Free context itself
	free(context);
}

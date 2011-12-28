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
	context->real_cache = malloc(sizeof (double) * width * height);
	context->imag_cache = malloc(sizeof (double) * width * height);

	// Check buffers were allocated
	if (!(context->iter_cache && context->real_cache && context->imag_cache)) {
		refract_free(context);
		return 0;
	}

	// Zeroize cache buffers
	memset(context->iter_cache, 0, sizeof (iterc_t) * width * height);
	memset(context->real_cache, 0, sizeof (double) * width * height);
	memset(context->imag_cache, 0, sizeof (double) * width * height);

	return context;
}

/**
 * Renders a context to the given pixel buffer
 */
void refract_render(refract_context* context, pixel_t* pixels, int stride, double real, double imag, double zoom) {
	int half_cx = context->width / 2;
	int half_cy = context->height / 2;

	for (int y = 0, index = 0; y < context->height; ++y) {

		pixel_t* line = (pixel_t*)pixels;

		for (int x = 0; x < context->width; ++x, ++index) {
			double zr, zi;
			int niters;

			// Convert from pixel space to complex space
			double cr = (x - half_cx) / zoom + real;
			double ci = (y - half_cy) / zoom - imag;

			zr = cr;
			zi = ci;
			niters = 0;

			// Precalculate squares
			double zr2 = zr * zr;
			double zi2 = zi * zi;

			// Iterate z = z^2 + c
			while ((zr2 + zi2 < 4) && niters < 10) {
				zi = 2 * zr * zi + ci;
				zr = zr2 - zi2 + cr;
				zr2 = zr * zr;
				zi2 = zi * zi;
				++niters;
			}

			line[x] = niters == 10 ? BLACK : WHITE;
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

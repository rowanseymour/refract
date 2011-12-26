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
refract_context* refract_init(uint32_t width, uint32_t height, uint32_t stride) {
	refract_context* context = (refract_context*)malloc(sizeof (refract_context));

	context->width = width;
	context->height = height;
	context->stride = stride;
	context->real = 0.0;
	context->imag = 0.0;
	context->zoom = 200.0;

	return context;
}

/**
 * Renders a context to the given pixel buffer
 */
void refract_render(refract_context* context, pixel_t* pixels) {
	int half_cx = context->width / 2;
	int half_cy = context->height / 2;
	double real = context->real;
	double imag = context->imag;
	double zoom = context->zoom;

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
		pixels = (pixel_t*)((char*)pixels + context->stride);
	}
}

/**
 * Frees a context
 */
void refract_free(refract_context* context) {
	free(context);
}

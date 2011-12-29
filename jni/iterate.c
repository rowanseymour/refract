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

void refract_iterate(refract_context* context, int func, float_t real, float_t imag, float_t zoom) {
	// Have parameters changed thus invalidating the cache?
	int use_cache = (func == context->last_func && real == context->last_real && imag == context->last_imag && zoom == context->last_zoom);
	int max_iters;

	if (use_cache) {
		// We haven't moved, so increase the max iters value for more detail
		max_iters = context->last_max_iters + DEF_INC_ITERATIONS;
	}
	else {
		// We have moved, so drop the max iters value to speed up rendering
		max_iters = DEF_MIN_ITERATIONS;
	}

	switch (func) {
	case FUNC_MANDELBROT:
		refract_iterate_m2(context, real, imag, zoom, max_iters, use_cache);
		break;
	}

	// Update cache params
	context->last_func = func;
	context->last_real = real;
	context->last_imag = imag;
	context->last_zoom = zoom;
	context->last_max_iters = max_iters;
}

void refract_iterate_m2(refract_context* context, float_t real, float_t imag, float_t zoom, int max_iters, int use_cache) {
	int half_cx = context->width / 2;
	int half_cy = context->height / 2;

	for (int y = 0, index = 0; y < context->height; ++y) {
		for (int x = 0; x < context->width; ++x, ++index) {
			float_t zr, zi;
			int niters;

			// Convert from pixel space to complex space
			float_t cr = (x - half_cx) / zoom + real;
			float_t ci = (y - half_cy) / zoom - imag;

			if (use_cache) {
				// Load X, Y and ITERS from cache if refinement
				zr = context->real_cache[index];
				zi = context->imag_cache[index];
				niters = context->iter_cache[index];
			}
			else {
				zr = cr;
				zi = ci;
				niters = 0;
			}

			// Precalculate squares
			float_t zr2 = zr * zr;
			float_t zi2 = zi * zi;

			// Iterate z = z^2 + c
			while ((zr2 + zi2 < 4) && niters < max_iters) {
				zi = 2 * zr * zi + ci;
				zr = zr2 - zi2 + cr;
				zr2 = zr * zr;
				zi2 = zi * zi;
				++niters;
			}

			// Store X, Y and ITERS in cache for next frame which maybe a refinement
			context->real_cache[index] = zr;
			context->imag_cache[index] = zi;
			context->iter_cache[index] = niters;
		}
	}
}

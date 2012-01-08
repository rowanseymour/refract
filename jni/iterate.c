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

void refract_iterate(refract_context* context, uint8_t func, complex_t offset, float_t zoom) {
	// Have parameters changed thus invalidating the cache?
	refract_params* params = &context->cache_params;
	bool use_cache = (func == params->func && offset.re == params->offset.re && offset.im == params->offset.im && zoom == params->zoom);

	// Increment or reset max-iters depending on whether we'll be using the cache
	iterc_t max_iters = use_cache ? (context->cache_max_iters + context->iters_per_frame) : context->iters_per_frame;

	switch (func) {
	case FUNC_MANDELBROT:
		refract_iterate_m2(context, offset, zoom, max_iters, use_cache);
		break;
	}

	// Update cache params
	params->func = func;
	params->offset.re = offset.re;
	params->offset.im = offset.im;
	params->zoom = zoom;

	context->cache_max_iters = max_iters;
}

void refract_iterate_m2(refract_context* context, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache) {
	uint16_t half_cx = context->width / 2;
	uint16_t half_cy = context->height / 2;

	for (int y = 0, index = 0; y < context->height; ++y) {
		for (int x = 0; x < context->width; ++x, ++index) {
			complex_t z, c;
			iterc_t iters;

			// Convert from pixel space to complex space
			c.re = (x - half_cx) / zoom + offset.re;
			c.im = (y - half_cy) / zoom - offset.im;

			if (use_cache) {
				// Load iteration data from cache if doing refinement
				z = context->z_cache[index];
				iters = context->iter_cache[index];
			}
			else {
				z = c;
				iters = 0;
			}

			// Pre-calculate squares
			float_t zr2 = z.re * z.re;
			float_t zi2 = z.im * z.im;

			// Iterate z = z^2 + c
			while ((zr2 + zi2 < 4) && iters < max_iters) {
				z.im = 2 * z.re * z.im + c.im;
				z.re = zr2 - zi2 + c.re;
				zr2 = z.re * z.re;
				zi2 = z.im * z.im;
				++iters;
			}

			// Store iteration data in cache for possible refinement in next frame
			context->z_cache[index] = z;
			context->iter_cache[index] = iters;
		}
	}
}

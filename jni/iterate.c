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
	// Calculate screen dimensions
	const uint16_t half_cx = context->width / 2;
	const uint16_t half_cy = context->height / 2;
	const float_t offset_x = offset.re;
	const float_t offset_y = offset.im;
	const float_t inv_zoom = 1 / zoom;
	const iterc_t cache_max_iters = context->cache_max_iters;

	// Allow optimized access to memory locations
	complex_t* restrict z_cache = context->z_cache;
	iterc_t* restrict iter_cache = context->iter_cache;

	for (int y = 0, index = 0; y < context->height; ++y) {
		for (int x = 0; x < context->width; ++x, ++index) {
			iterc_t iters;
			float_t zr, zi;

			// Only refine locations that reached maximum iterations previously
			if (use_cache) {
				iters = iter_cache[index];
				if (iters != cache_max_iters)
					continue;

				// Load z value from cache
				complex_t z = z_cache[index];
				zr = z.re;
				zi = z.im;
			}

			// Convert from pixel space to complex space
			float_t cr = (x - half_cx) * inv_zoom + offset_x;
			float_t ci = (y - half_cy) * inv_zoom - offset_y;

			// If not doing refinement then initialize values
			if (!use_cache) {
				iters = 0;
				zr = cr;
				zi = ci;
			}

			// Pre-calculate squares
			float_t zr_2 = zr * zr;
			float_t zi_2 = zi * zi;

			// Iterate z = z^2 + c
			while ((zr_2 + zi_2 < 4) && iters < max_iters) {
				zi = 2 * zr * zi + ci;
				zr = zr_2 - zi_2 + cr;
				zr_2 = zr * zr;
				zi_2 = zi * zi;
				++iters;
			}

			// Store iteration data in cache for possible refinement in next frame
			complex_t z = { zr, zi };
			z_cache[index] = z;
			iter_cache[index] = iters;
		}
	}
}

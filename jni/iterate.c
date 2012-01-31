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
 * Iterates z = z^2 + c
 */
void refract_renderer_iterate_m2(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache) {
	// Calculate screen dimensions
	const int half_cx = renderer->width / 2;
	const int half_cy = renderer->height / 2;
	const float_t offset_re = offset.re;
	const float_t offset_im = offset.im;
	const float_t inv_zoom = 1 / zoom;
	const iterc_t cache_max_iters = renderer->cache_max_iters;

	// Allow optimized access to memory locations
	iterc_t* restrict iter_buffer = renderer->iter_buffer;
	complex_t* restrict z_cache = renderer->z_cache;

	for (int y = 0, index = 0; y < renderer->height; ++y) {
		for (int x = 0; x < renderer->width; ++x, ++index) {
			iterc_t iters;
			float_t zr, zi;

			// Only refine locations that reached maximum iterations previously
			if (use_cache) {
				iters = iter_buffer[index];
				if (iters != cache_max_iters)
					continue;

				// Load z value from cache
				complex_t z = z_cache[index];
				zr = z.re;
				zi = z.im;
			}

			// Convert from pixel space to complex space
			float_t cr = (x - half_cx) * inv_zoom + offset_re;
			float_t ci = (half_cy - y) * inv_zoom + offset_im;

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

			// Store result in iteration buffer
			iter_buffer[index] = iters;

			// Store z in cache for possible refinement in next frame
			complex_t z = { zr, zi };
			z_cache[index] = z;
		}
	}
}

/**
 * Iterates z = z^3 + c
 */
void refract_renderer_iterate_m3(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache) {
	// Calculate screen dimensions
	const int half_cx = renderer->width / 2;
	const int half_cy = renderer->height / 2;
	const float_t offset_re = offset.re;
	const float_t offset_im = offset.im;
	const float_t inv_zoom = 1 / zoom;
	const iterc_t cache_max_iters = renderer->cache_max_iters;

	// Allow optimized access to memory locations
	iterc_t* restrict iter_buffer = renderer->iter_buffer;
	complex_t* restrict z_cache = renderer->z_cache;

	for (int y = 0, index = 0; y < renderer->height; ++y) {
		for (int x = 0; x < renderer->width; ++x, ++index) {
			iterc_t iters;
			float_t zr, zi;

			// Only refine locations that reached maximum iterations previously
			if (use_cache) {
				iters = iter_buffer[index];
				if (iters != cache_max_iters)
					continue;

				// Load z value from cache
				complex_t z = z_cache[index];
				zr = z.re;
				zi = z.im;
			}

			// Convert from pixel space to complex space
			float_t cr = (x - half_cx) * inv_zoom + offset_re;
			float_t ci = (half_cy - y) * inv_zoom + offset_im;

			// If not doing refinement then initialize values
			if (!use_cache) {
				iters = 0;
				zr = cr;
				zi = ci;
			}

			// Pre-calculate squares
			float_t zr_2 = zr * zr;
			float_t zi_2 = zi * zi;

			// Iterate z = z^3 + c
			while ((zr_2 + zi_2 < 4) && iters < max_iters) {
				zi = zi * (3 * zr_2 - zi_2) + ci;
				zr = zr * (zr_2 - 3 * zi_2) + cr;
				zr_2 = zr * zr;
				zi_2 = zi * zi;
				++iters;
			}

			// Store result in iteration buffer
			iter_buffer[index] = iters;

			// Store z in cache for possible refinement in next frame
			complex_t z = { zr, zi };
			z_cache[index] = z;
		}
	}
}

/**
 * Iterates z = z^4 + c
 */
void refract_renderer_iterate_m4(renderer_t* renderer, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache) {
	// Calculate screen dimensions
	const int half_cx = renderer->width / 2;
	const int half_cy = renderer->height / 2;
	const float_t offset_re = offset.re;
	const float_t offset_im = offset.im;
	const float_t inv_zoom = 1 / zoom;
	const iterc_t cache_max_iters = renderer->cache_max_iters;

	// Allow optimized access to memory locations
	iterc_t* restrict iter_buffer = renderer->iter_buffer;
	complex_t* restrict z_cache = renderer->z_cache;

	for (int y = 0, index = 0; y < renderer->height; ++y) {
		for (int x = 0; x < renderer->width; ++x, ++index) {
			iterc_t iters;
			float_t zr, zi;

			// Only refine locations that reached maximum iterations previously
			if (use_cache) {
				iters = iter_buffer[index];
				if (iters != cache_max_iters)
					continue;

				// Load z value from cache
				complex_t z = z_cache[index];
				zr = z.re;
				zi = z.im;
			}

			// Convert from pixel space to complex space
			float_t cr = (x - half_cx) * inv_zoom + offset_re;
			float_t ci = (half_cy - y) * inv_zoom + offset_im;

			// If not doing refinement then initialize values
			if (!use_cache) {
				iters = 0;
				zr = cr;
				zi = ci;
			}

			// Pre-calculate squares
			float_t zr_2 = zr * zr;
			float_t zi_2 = zi * zi;

			// Iterate z = z^4 + c
			while ((zr_2 + zi_2 < 4) && iters < max_iters) {
				zi = 4 * zr * zi * (zr_2 - zi_2) + ci;
				zr = zr_2 * zr_2 - 6 * zr_2 * zi_2 + zi_2 * zi_2 + cr;
				zr_2 = zr * zr;
				zi_2 = zi * zi;
				++iters;
			}

			// Store result in iteration buffer
			iter_buffer[index] = iters;

			// Store z in cache for possible refinement in next frame
			complex_t z = { zr, zi };
			z_cache[index] = z;
		}
	}
}

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

#include <stdlib.h>
#include <stdbool.h>
#include <math.h>

#include "color.h"
#include "palette.h"

#define DEF_ITERSPERFRAME	5		// The default iterations per frame value

/**
 * Counts of iterations
 */
typedef uint16_t iterc_t;

/**
 * Real and imaginary floating point values
 */
typedef float float_t;

/**
 * Complex values
 */
typedef struct {
	float_t re;
	float_t im;

} complex_t;

/**
 * Parameters of a fractal render
 */
typedef struct {
	uint8_t func;
	complex_t offset;
	float_t zoom;

} refract_params;

typedef struct {
	uint16_t width;
	uint16_t height;
	iterc_t iters_per_frame;

	refract_params cache_params;
	iterc_t cache_max_iters;
	iterc_t* iter_cache;
	complex_t* z_cache;

	refract_palette* palette;

} refract_context;

refract_context* refract_init(uint16_t width, uint16_t height);
void refract_render(refract_context* context, color_t* pixels, int stride, complex_t offset, float_t zoom);
void refract_free(refract_context* context);

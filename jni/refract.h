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

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

typedef uint32_t pixel_t;
typedef uint32_t iterc_t;
typedef float float_t;

typedef struct {
	int func;
	float_t real;
	float_t imag;
	float_t zoom;

} refract_params;

typedef struct {
	int size;
	pixel_t* colors;

} refract_palette;

typedef struct {
	uint32_t width;
	uint32_t height;

	refract_params cache_params;
	int cache_max_iters;
	iterc_t* cache_iters;
	float_t* cache_reals;
	float_t* cache_imags;

	refract_palette* palette;

} refract_context;

refract_context* refract_init(uint32_t width, uint32_t height);
void refract_render(refract_context* context, pixel_t* pixels, int stride, float_t real, float_t imag, float_t zoom);
void refract_free(refract_context* context);

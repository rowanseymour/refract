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
#include <pthread.h>

#include "color.h"

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
 * Enumeration of set functions
 */
typedef enum {
	MANDELBROT, 	// z^2 + c
	MANDELBROT_3, 	// z^3 + c
	MANDELBROT_4 	// z^4 + c
} func_t;

/**
 * Parameters of a fractal render
 */
typedef struct {
	func_t func;
	complex_t offset;
	float_t zoom;

} params_t;

/**
 * Palette
 */
typedef struct {
	int size;
	int offset;
	color_t* colors;

} palette_t;

/**
 * Rendering context
 */
typedef struct {
	int width;
	int height;

	params_t params;
	pthread_mutex_t mutex;
	palette_t palette;
	iterc_t* iter_buffer;

	bool cache_valid;
	iterc_t cache_max_iters;
	complex_t* z_cache;

} refract_context;

/**
 * Context functions
 */
bool refract_init(refract_context* context, int width, int height);
void refract_iterate(refract_context* context, iterc_t iters, params_t params, bool use_cache);
void refract_render(refract_context* context, color_t* pixels, int stride);
bool refract_acquire_lock(refract_context* context);
bool refract_release_lock(refract_context* context);
void refract_free(refract_context* context);

/**
 * Iteration functions
 */
void refract_iterate_m2(refract_context* context, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);
void refract_iterate_m3(refract_context* context, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);
void refract_iterate_m4(refract_context* context, complex_t offset, float_t zoom, iterc_t max_iters, bool use_cache);


/**
 * Palette functions
 */
bool refract_palette_init(palette_t* palette, color_t* colors, float* anchors, int points, int size);
void refract_palette_free(palette_t* palette);

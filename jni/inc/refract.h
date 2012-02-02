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
 * General purpose macros
 */
#define MAX(a, b) ((a) > (b) ? a : b)
#define MIN(a, b) ((a) < (b) ? a : b)
#define FREE(p) if (p) { free(p); p = NULL; }

/**
 * Counts of iterations
 */
typedef uint16_t iterc_t;

/**
 * Max count of iterations
 */
#define ITERC_MAX 0xFFFF

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
	color_t* colors;
	color_t set_color;

} palette_t;

/**
 * Mapping options
 */
typedef enum {
	REPEAT,
	REPEAT_CYCLE,
	CLAMP,
	SCALE_GLOBAL,
	SCALE_LOCAL

} mapping_t;

/**
 * Rendering context
 */
typedef struct {
	int id;
	int width;
	int height;

	palette_t palette;
	int* palette_indexes;

	iterc_t* iter_buffer;
	int* iter_histogram;

	bool cache_valid;
	params_t cache_params;
	iterc_t cache_max_iters;
	complex_t* z_cache;

	pthread_mutex_t buffers_mutex;

} renderer_t;

/**
 * Renderer functions
 */
bool refract_renderer_init(renderer_t* renderer, int width, int height);
bool refract_renderer_resize(renderer_t* renderer, int width, int height);
iterc_t refract_renderer_iterate(renderer_t* renderer, params_t* params, iterc_t iters);
void refract_renderer_render(renderer_t* renderer, color_t* pixels, int stride, mapping_t mapping);
void refract_renderer_free(renderer_t* renderer);

/**
 * Palette functions
 */
bool refract_palette_init(palette_t* palette, color_t* colors, float* anchors, int points, int size, color_t set_color);
void refract_palette_free(palette_t* palette);

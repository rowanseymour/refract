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

#define RGB(r,g,b)		((pixel_t)(((uint8_t)(r)|((uint16_t)(g)<<8))|(((uint32_t)(uint8_t)(b))<<16)))
#define RGBA(r,g,b,a)	((pixel_t)(((uint8_t)(r)|((uint16_t)(g)<<8))|(((uint32_t)(uint8_t)(b))<<16)|(((uint32_t)(uint8_t)(a))<<24)))
#define BLACK 			0xFF000000
#define WHITE			0xFFFFFFFF
#define RED				0xFF0000FF
#define GREEN			0xFF00FF00
#define BLUE			0xFFFF0000

typedef uint32_t pixel_t;
typedef uint32_t iterc_t;
typedef float float_t;

typedef struct {
	uint32_t width;
	uint32_t height;
	int last_func;
	float_t last_real;
	float_t last_imag;
	float_t last_zoom;
	int last_max_iters;
	iterc_t* iter_cache;
	float_t* real_cache;
	float_t* imag_cache;

} refract_context;

refract_context* refract_init(uint32_t width, uint32_t height);
void refract_render(refract_context* context, pixel_t* pixels, int stride, float_t real, float_t imag, float_t zoom);
void refract_free(refract_context* context);

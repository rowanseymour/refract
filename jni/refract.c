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
#include "palette.h"

/**
 * Allocates a context
 */
refract_context* refract_init(uint32_t width, uint32_t height) {
	// Allocate context
	refract_context* context = (refract_context*)malloc(sizeof (refract_context));
	if (!context)
		return NULL;

	// Initialize context
	memset(context, 0, sizeof (refract_context));
	context->width = width;
	context->height = height;
	context->iters_per_frame = DEF_ITERSPERFRAME;

	// Allocate cache buffers
	context->cache_iters = malloc(sizeof (iterc_t) * width * height);
	context->cache_reals = malloc(sizeof (float_t) * width * height);
	context->cache_imags = malloc(sizeof (float_t) * width * height);

	// Check buffers were allocated
	if (!(context->cache_iters && context->cache_reals && context->cache_imags)) {
		refract_free(context);
		return NULL;
	}

	// Zeroize cache buffers
	memset(context->cache_iters, 0, sizeof (iterc_t) * width * height);
	memset(context->cache_reals, 0, sizeof (float_t) * width * height);
	memset(context->cache_imags, 0, sizeof (float_t) * width * height);

	return context;
}

/**
 * Renders a context to the given pixel buffer
 */
void refract_render(refract_context* context, color_t* pixels, int stride, float_t real, float_t imag, float_t zoom) {
	// Iterate fractal rendering
	refract_iterate(context, FUNC_MANDELBROT, real, imag, zoom);

	// Number of iters to be considered in the set
	uint16_t max_iters = context->cache_max_iters;

	refract_palette* palette = context->palette;

	// Render cached iteration values into pixels
	for (int y = 0, index = 0; y < context->height; ++y) {
		color_t* line = (color_t*)pixels;

		for (int x = 0; x < context->width; ++x, ++index) {
			iterc_t iters = context->cache_iters[index];

			line[x] = (iters == max_iters) ? BLACK : palette->colors[iters % palette->size];
		}

		// go to next line
		pixels = (color_t*)((char*)pixels + stride);
	}
}

/**
 * Frees a context
 */
void refract_free(refract_context* context) {
	// Free palette
	refract_palette_free(context->palette);

	// Free cache buffers
	if (context->cache_iters)
		free(context->cache_iters);
	if (context->cache_reals)
		free(context->cache_reals);
	if (context->cache_imags)
		free(context->cache_imags);

	// Free context itself
	free(context);
}

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

refract_context* refract_init(uint32_t width, uint32_t height, uint32_t stride) {
	refract_context* context = (refract_context*)malloc(sizeof (refract_context));

	context->width = width;
	context->height = height;
	context->stride = stride;

	return context;
}

void refract_render(refract_context* context, pixel_t* pixels) {
	for (int y = 0; y < context->height; ++y) {
		pixel_t*  line = (pixel_t*)pixels;

		for (int x = 0; x < context->width; ++x) {

			line[x] = rand();//RGB(255, 128, 0);
		}

		// go to next line
		pixels = (pixel_t*)((char*)pixels + context->stride);
	}
}

void refract_free(refract_context* context) {
	free(context);
}

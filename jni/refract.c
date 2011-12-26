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
	refract_context* context = malloc(sizeof (refract_context));

	context->width = width;
	context->height = height;
	context->stride = stride;

	return context;
}

void refract_render(refract_context* context, void* pixels) {
	int  y;
	for (y = 0; y < context->height; ++y) {
		PIXEL32*  line = (PIXEL32*)pixels;

		int x;
		for (x = 0; x < context->width; ++x) {

			line[x] = rand();//RGB(255, 128, 0);
		}

		// go to next line
		pixels = (char*)pixels + context->stride;
	}
}

void refract_free(refract_context* context) {
	free(context);
}

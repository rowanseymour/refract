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
#include "palette.h"

refract_palette* refract_palette_init(pixel_t* colors, float* anchors, int size) {
	refract_palette* palette = malloc(sizeof (refract_palette));

	palette->size = size;
	palette->colors = malloc(sizeof (pixel_t) * size);

	return palette;
}

void refract_palette_free(refract_palette* palette) {
	if (palette->colors)
		free(palette->colors);

	free(palette);
}

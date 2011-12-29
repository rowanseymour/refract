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

refract_palette* refract_palette_init(pixel_t* colors, float* anchors, int points, int size) {
	refract_palette* palette = malloc(sizeof (refract_palette));

	palette->size = size;
	palette->colors = malloc(sizeof (pixel_t) * size);

	int index = -1;

	for (int i = 0; i < size; ++i) {
		float ipos = (float)i / (size - 1); // palette index 0.0...1.0

		if ((index < points - 1) && (ipos > anchors[index + 1]))
			++index;

		if (index < 0) {
			palette->colors[i] = colors[0];
		}
		else if (index >= points - 1) {
			palette->colors[i] = colors[points - 1];
		}
		else {
			float segment_min = anchors[index];
			float segment_max = anchors[index + 1];
			float segment_len = segment_max - segment_min;
			float weight2 = (ipos - segment_min) / segment_len;
			float weight1 = 1.0f - weight2;

			int r = (int)(weight1 * GET_R(colors[index]) + weight2 * GET_R(colors[index + 1]));
			int g = (int)(weight1 * GET_G(colors[index]) + weight2 * GET_G(colors[index + 1]));
			int b = (int)(weight1 * GET_B(colors[index]) + weight2 * GET_B(colors[index + 1]));

			palette->colors[i] = RGB(r, g, b);
		}
	}

	return palette;
}

void refract_palette_free(refract_palette* palette) {
	if (palette->colors)
		free(palette->colors);

	free(palette);
}

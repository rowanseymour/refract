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

#define RGB(r,g,b)		((pixel_t)(((uint8_t)(r)|((uint16_t)(g)<<8))|(((uint32_t)(uint8_t)(b))<<16)))
#define RGBA(r,g,b,a)	((pixel_t)(((uint8_t)(r)|((uint16_t)(g)<<8))|(((uint32_t)(uint8_t)(b))<<16)|(((uint32_t)(uint8_t)(a))<<24)))
#define BLACK 			0xFF000000
#define WHITE			0xFFFFFFFF
#define RED				0xFF0000FF
#define GREEN			0xFF00FF00
#define BLUE			0xFFFF0000

#define SET_COLOR		BLACK

refract_palette* refract_palette_init(pixel_t* colors, float* anchors, int size);
void refract_palette_free(refract_palette* palette);

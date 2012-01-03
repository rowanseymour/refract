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

typedef uint32_t 			color_t;

/**
 * ARGB macros and constants
 */

#define ARGB_PACK(r,g,b,a)	((color_t)(((uint8_t)(b) | ((uint16_t)(g) << 8)) | (((uint32_t)(uint8_t)(r)) << 16) | (((uint32_t)(uint8_t)(a)) << 24)))
#define ARGB_GETA(c)		(((c) >> 24) & 0x000000FF)
#define ARGB_GETR(c)		(((c) >> 16) & 0x000000FF)
#define ARGB_GETG(c)		(((c) >> 8) & 0x000000FF)
#define ARGB_GETB(c)		((c) & 0x000000FF)

/**
 * ABGR macros and constants
 */

#define ABGR_PACK(r,g,b,a)	((color_t)(((uint8_t)(r) | ((uint16_t)(g) << 8)) | (((uint32_t)(uint8_t)(b)) << 16) | (((uint32_t)(uint8_t)(a)) << 24)))
#define ARGB_GETA(c)		(((c) >> 24) & 0x000000FF)
#define ABGR_GETB(c)		(((c) >> 16) & 0x000000FF)
#define ABGR_GETG(c)		(((c) >> 8) & 0x000000FF)
#define ABGR_GETR(c)		((c) & 0x000000FF)

/**
 * Conversion macros
 */

#define RGB_TO_ABGR(c)		(((c) & 0x0000FF00) | (ARGB_GETB(c) << 16) | ARGB_GETR(c) | 0xFF000000)
#define ARGB_TO_ABGR(c)		(((c) & 0xFF00FF00) | (ARGB_GETB(c) << 16) | ARGB_GETR(c))
#define ABGR_TO_ARGB(c)		(((c) & 0xFF00FF00) | (ARGB_GETR(c) << 16) | ARGB_GETB(c))

/**
 * Colors
 */

#define WHITE				0xFFFFFFFF
#define BLACK				0xFF000000

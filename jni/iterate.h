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

#define FUNC_MANDELBROT		2		// z^2 + c
#define FUNC_MANDELBROT_3	3		// z^3 + c
#define FUNC_MANDELBROT_4	4		// z^4 + c

void refract_iterate(refract_context* context, uint8_t func, complex_t offset, float_t zoom);
void refract_iterate_m2(refract_context* context, complex_t offset, float_t zoom, iterc_t max_iters, int use_cache);

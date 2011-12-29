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

#define DEF_MIN_ITERATIONS	25		// The default min/initial iterations value
#define DEF_INC_ITERATIONS	10		// The default iterations increment value

#define FUNC_MANDELBROT		2		// z^2 + c
#define FUNC_MANDELBROT_3	3		// z^3 + c

void refract_iterate(refract_context* context, int func, float_t real, float_t imag, float_t zoom);
void refract_iterate_m2(refract_context* context, float_t real, float_t imag, float_t zoom, int max_iters, int use_cache);
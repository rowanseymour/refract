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

package com.ijuru.refract;

/**
 * Iteration function types
 */
public enum Function {
	MANDELBROT,		// z = z^2 + c
	MANDELBROT_3,	// z = z^3 + c
	MANDELBROT_4;	// z = z^4 + c
	
	/**
	 * Parses a function from a string
	 * @param str the string to parse
	 * @return the function or null
	 */
	public static Function parseString(String str) {
		for (Function fn : values()) {
			if (fn.name().equalsIgnoreCase(str))
				return fn;
		}
		return null;
	}
}

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

package com.ijuru.refract.utils;

import android.graphics.PointF;
import android.util.FloatMath;

/**
 * Methods that don't belong anywhere else...
 */
public class Utils {
	
	/**
	 * Calculates the distance between two points
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return the distance
	 */
	public static float distanceBetween(PointF p1, PointF p2) {
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		return FloatMath.sqrt(dx * dx + dy * dy);
	}
}

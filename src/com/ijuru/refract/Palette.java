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
 * Palette presets
 */
public class Palette {
	
	private int[] colors;
	private float[] anchors;
	
	public static Palette SUNSET = new Palette(
			new int[] { 0xFF320064, 0xFF0000FF, 0xFF00FFFF },
			new float[] { 0.0f, 0.3f, 1.0f}
	);
	
	/**
	 * Creates a new palette
	 * @param colors the color points
	 * @param anchors the point positions 0.0...1.0
	 */
	private Palette(int[] colors, float[] anchors) {
		this.colors = colors;
		this.anchors = anchors;
	}
	
	/**
	 * Gets the colors
	 * @return the colors
	 */
	public int[] getColors() {
		return colors;
	}
	
	/**
	 * Gets the anchor point positions
	 * @return the positions
	 */
	public float[] getAnchors() {
		return anchors;
	}
}

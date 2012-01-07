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

import java.util.HashMap;
import java.util.Map;

/**
 * Palette used to render a fractal as a bitmap. These use RGB codes for colors rather than the ABGR used by the JNI code.
 */
public class Palette {
	
	private int[] colors;
	private float[] anchors;
	
	private static Map<String, Palette> presets = new HashMap<String, Palette>();
	
	static {
		presets.put("sunset", new Palette(
			new int[] { 0x640032, 0xFF0000, 0xFFFF00 },
			new float[] { 0.0f, 0.3f, 1.0f }
		));
		presets.put("hubble", new Palette(
			new int[] { 0x0D1C40, 0x46787A, 0xFFFF00 },
			new float[] { 0.0f, 0.4f, 1.0f }
		));
		presets.put("rainbow", new Palette(
			new int[] { 0xFF0000, 0xFF8000, 0xFFFF00, 0x00FF00, 0x00FFFF, 0x0080FF, 0x0000FF, 0xFF0000 },
			new float[] { 0.0f, 0.143f, 0.286f, 0.429f, 0.571f, 0.714f, 0.857f, 1.0f }
		));
		presets.put("chrome", new Palette(
			new int[] { 0x2989CC, 0xFFFFFF, 0x906A00, 0xD99F00, 0xFFFFFF },
			new float[] { 0.0f, 0.49f, 0.5f, 0.625f, 1.0f }
		));
		presets.put("evening", new Palette(
			new int[] { 0x002874, 0xFD7C00, 0x6F156C, 0xF9E600 },
			new float[] { 0.0f, 0.333f, 0.666f, 1.0f }
		));
		presets.put("electric", new Palette(
			new int[] { 0x000010, 0x1000F0, 0xFFFFFF },
			new float[] { 0.0f, 0.3f, 1.0f }
		));
	}
	
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
	 * Gets the preset palette with the given name
	 * @param name the name
	 * @return the palette
	 */
	public static Palette getByName(String name) {
		return presets.get(name);
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

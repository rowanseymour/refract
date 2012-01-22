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

import java.util.ArrayList;
import java.util.List;

/**
 * Palette used to render a fractal as a bitmap. These use ARGB codes for colors rather than the ABGR used by the JNI code.
 */
public class Palette {
	
	private String name;
	private int[] colors;
	private float[] anchors;
	
	private static List<Palette> presets = new ArrayList<Palette>();
	
	static {
		presets.add(new Palette("sunset",
			new int[] { 0xFF640032, 0xFFFF0000, 0xFFFFFF00 },
			new float[] { 0.0f, 0.3f, 1.0f }
		));
		presets.add(new Palette("hubble",
			new int[] { 0xFF0D1C40, 0xFF46787A, 0xFFFFFF00 },
			new float[] { 0.0f, 0.4f, 1.0f }
		));
		presets.add(new Palette("rainbow",
			new int[] { 0xFFFF0000, 0xFFFF8000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0080FF, 0xFF0000FF, 0xFFFF0000 },
			new float[] { 0.0f, 0.143f, 0.286f, 0.429f, 0.571f, 0.714f, 0.857f, 1.0f }
		));
		presets.add(new Palette("chrome",
			new int[] { 0xFF2989CC, 0xFFFFFFFF, 0xFF906A00, 0xFFD99F00, 0xFFFFFFFF },
			new float[] { 0.0f, 0.49f, 0.5f, 0.625f, 1.0f }
		));
		presets.add(new Palette("evening",
			new int[] { 0xFF002874, 0xFFFD7C00, 0xFF6F156C, 0xFFF9E600 },
			new float[] { 0.0f, 0.333f, 0.666f, 1.0f }
		));
		presets.add(new Palette("electric",
			new int[] { 0xFF000010, 0xFF1000F0, 0xFFFFFFFF },
			new float[] { 0.0f, 0.3f, 1.0f }
		));
	}
	
	/**
	 * Creates a new palette
	 * @param name the name
	 * @param colors the color points
	 * @param anchors the point positions 0.0...1.0
	 */
	private Palette(String name, int[] colors, float[] anchors) {
		this.name = name;
		this.colors = colors;
		this.anchors = anchors;
	}
	
	/**
	 * Gets the preset palette with the given name
	 * @param name the name
	 * @return the palette
	 */
	public static Palette getPresetByName(String name) {
		for (Palette preset : presets) {
			if (preset.name.equals(name))
				return preset;
		}
		return null;
	}
	
	/**
	 * Gets all of the presets
	 * @return the presets
	 */
	public static List<Palette> getPresets() {
		return presets;
	}
	
	/**
	 * Gets the name
	 * @return the name
	 */
	public String getName() {
		return name;
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

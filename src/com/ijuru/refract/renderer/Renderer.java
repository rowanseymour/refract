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

package com.ijuru.refract.renderer;

import android.graphics.Bitmap;

/**
 * Interface of a fractal renderer
 */
public interface Renderer {

	/**
	 * Allocates resources for a renderer with the given dimensions
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public void allocate(int width, int height);

	/**
	 * Renders a fractal to the given bitmap
	 * @param bitmap the bitmap to render to
	 * @param real the real coordinate
	 * @param imag the imaginary coordinate
	 * @param zoom the zoom factor
	 * @return the number of iterations used
	 */
	public int render(Bitmap bitmap, double real, double imag, double zoom);

	/**
	 * Frees resources
	 */
	public void free();
}
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

import com.ijuru.refract.Function;
import com.ijuru.refract.Palette;

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
	public boolean allocate(int width, int height);
	
	/**
	 * Sets the function
	 * @param function the function
	 */
	public void setFunction(Function function);
	
	/**
	 * Sets the offset in complex space
	 * @param real the real component
	 * @param imag the imaginary component
	 */
	public void setOffset(double real, double imag);
	
	/**
	 * Gets the zoom factor in complex space
	 * @return the zoom factor
	 */
	public double getZoom();
	
	/**
	 * Sets the zoom factor in complex space
	 * @param zoom the zoom factor
	 */
	public void setZoom(double zoom);
	
	/**
	 * Sets the palette for renders
	 * @param palette the palette
	 */
	public void setPalette(Palette palette);
	
	/**
	 * Sets the number of iterations per frame
	 * @param iters the number of iterations
	 * @return the accumulative number of iterations used
	 */
	public int iterate(int iters);

	/**
	 * Renders a fractal to the given bitmap
	 * @param bitmap the bitmap to render to
	 * @return true if successful
	 */
	public boolean render(Bitmap bitmap);

	/**
	 * Frees resources
	 */
	public void free();
}
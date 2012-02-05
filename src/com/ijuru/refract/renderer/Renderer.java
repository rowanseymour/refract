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
	 * Allocates the renderer with the given dimensions
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public boolean allocate(int width, int height);
	
	/**
	 * Resizes the renderer to the given dimensions
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public boolean resize(int width, int height);
	
	/**
	 * Iterates the renderer
	 * @param function the function
	 * @param offset the offset in complex space
	 * @param zoom the zoom factor
	 * @param iters the number of iterations to perform
	 * @return the overall number of iterations performed on the current render
	 */
	public int iterate(Function function, Complex offset, double zoom, int iters);

	/**
	 * Renders a fractal to the given bitmap
	 * @param bitmap the bitmap to render to
	 * @param mapping the palette mapping mode
	 * @return true if successful
	 */
	public boolean render(Bitmap bitmap, Mapping mapping);

	/**
	 * Frees resources
	 */
	public void free();
	
	/**
	 * Gets the width
	 * @return the width
	 */
	public int getWidth();
	
	/**
	 * Gets the height
	 * @return the height
	 */
	public int getHeight();
	
	/**
	 * Sets the palette for renders
	 * @param palette the palette
	 * @param size the size
	 * @param the RGB color of the set
	 */
	public void setPalette(Palette palette, int size, float bias, int setColor);
}
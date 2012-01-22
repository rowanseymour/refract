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

import com.ijuru.refract.Complex;
import com.ijuru.refract.Function;
import com.ijuru.refract.Mapping;
import com.ijuru.refract.Palette;

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
	 * Gets the function
	 * @return the function
	 */
	public Function getFunction();
	
	/**
	 * Sets the function
	 * @param function the function
	 */
	public void setFunction(Function function);
	
	/**
	 * Gets the offset in complex space
	 * @return the offset
	 */
	public Complex getOffset();
	
	/**
	 * Sets the offset in complex space
	 * @param offset the offset
	 */
	public void setOffset(Complex offset);
	
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
	 * @param size the size
	 */
	public void setPalette(Palette palette, int size);
	
	/**
	 * Gets the palette mapping type
	 * @return the mapping
	 */
	public Mapping getPaletteMapping();
	
	/**
	 * Sets the palette mapping type
	 * @param mapping the mapping
	 */
	public void setPaletteMapping(Mapping mapping);
}
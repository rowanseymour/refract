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

import com.ijuru.refract.Palette;

import android.graphics.Bitmap;

/**
 * Native implementation of a fractal renderer
 */
public class NativeRenderer implements Renderer {
	
	private long context;
	
	/**
	 * @see com.ijuru.refract.renderer.Renderer#allocate(int, int)
	 */
	@Override
	public native boolean allocate(int width, int height);
	
	/**
	 * @see com.ijuru.refract.renderer.Renderer#setPalette(Palette)
	 */
	@Override
	public native void setPalette(Palette palette);
	
	/**
	 * @see com.ijuru.refract.renderer.Renderer#setItersPerFrame(int)
	 */
	@Override
	public native void setItersPerFrame(int iters);

	/**
	 * @see com.ijuru.refract.renderer.Renderer#render(android.graphics.Bitmap, double, double, double)
	 */
	@Override
	public native int render(Bitmap bitmap, double real, double imag, double zoom);
	
	/**
	 * @see com.ijuru.refract.renderer.Renderer#free()
	 */
	@Override
	public native void free();
}

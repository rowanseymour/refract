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
 * Native implementation of a fractal renderer
 */
public class NativeRenderer implements Renderer {
	
	/**
	 * Used by native code
	 */
	private long renderer;
	
	@Override public native boolean allocate(int width, int height);
	@Override public native boolean resize(int width, int height);
	@Override public native int iterate(int iters);
	@Override public native boolean render(Bitmap bitmap);
	@Override public native void free();
	@Override public native int getWidth();
	@Override public native int getHeight();
	@Override public native Function getFunction();
	@Override public native void setFunction(Function function);
	@Override public native Complex getOffset();
	@Override public native void setOffset(Complex offset);
	@Override public native double getZoom();
	@Override public native void setZoom(double zoom);
	@Override public native void setPalette(Palette palette, int size, int setColor);
	@Override public native Mapping getPaletteMapping();
	@Override public native void setPaletteMapping(Mapping mapping);
}

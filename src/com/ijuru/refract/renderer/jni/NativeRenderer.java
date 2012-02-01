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

package com.ijuru.refract.renderer.jni;

import com.ijuru.refract.renderer.Complex;
import com.ijuru.refract.renderer.Function;
import com.ijuru.refract.renderer.Mapping;
import com.ijuru.refract.renderer.Palette;
import com.ijuru.refract.renderer.Renderer;

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
	@Override public native int iterate(Function function, Complex offset, double zoom, int iters);
	@Override public native boolean render(Bitmap bitmap, Mapping mapping);
	@Override public native void free();
	@Override public native int getWidth();
	@Override public native int getHeight();
	@Override public native void setPalette(Palette palette, int size, int setColor);
}

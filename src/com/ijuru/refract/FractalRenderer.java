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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class FractalRenderer {
	
	private Bitmap bitmap;
	private double real, imag, zoom;
	private long context;
	
	public void allocate(int width, int height) {
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		allocateInternal();
	}
	
	public void update() {
		updateInternal();
	}
	
	public void free() {
		bitmap = null;
		
		freeInternal();
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setCoords(double real, double imag) {
		this.real = real;
		this.imag = imag;
	}
	
	public double getZoom() {
		return zoom;
	}
	
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}
	
	private native void allocateInternal();
	private native void updateInternal();
	private native void freeInternal();
}

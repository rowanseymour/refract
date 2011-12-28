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

import com.ijuru.refract.renderer.NativeRenderer;
import com.ijuru.refract.renderer.Renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * View which displays the fractal rendering
 */
public class RendererView extends SurfaceView {
	
	private Bitmap bitmap;
	private SurfaceHolder holder;
	private Renderer renderer;
	private RendererThread rendererThread;
	
	public RendererView(Context context) {
		super(context);
		
		rendererThread = new RendererThread(this);
		holder = getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {		
				bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
				renderer = new NativeRenderer();
				renderer.allocate(getWidth(), getHeight());
				
				rendererThread.setRunning(true);
				rendererThread.start();
				
				Log.i("refract", "Render surface created [" + getWidth() + ", " + getHeight() + "]");
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				boolean retry = true;
				rendererThread.setRunning(false);
				while (retry) {
					try {
						rendererThread.join();
						retry = false;
					} catch (InterruptedException e) {
					}
				}
				renderer.free();
				
				Log.i("refract", "Render surface destroyed");
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				renderer.free();
				renderer.allocate(width, height);
				
				Log.i("refract", "Render surface resized [" + width + ", " + height + "]");
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		renderer.render(bitmap, 0, 0, 200);
		
		canvas.drawBitmap(bitmap, 0, 0, null);
	}
}

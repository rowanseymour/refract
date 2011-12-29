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
public class RendererView extends SurfaceView implements SurfaceHolder.Callback {
	
	private Bitmap bitmap;
	private SurfaceHolder holder;
	private Renderer renderer;
	private RendererThread rendererThread = new RendererThread(this);
	private StatusPanel statusPanel;
	private double zoom;
	
	public RendererView(Context context) {
		super(context);
		
		holder = getHolder();
		holder.addCallback(this);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("refract", "Render surface created [" + getWidth() + ", " + getHeight() + "]");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		if (renderer == null)
			renderer = new NativeRenderer();
		else
			renderer.free();
		
		renderer.allocate(width, height);
		
		if (!rendererThread.isAlive())
			rendererThread.start();
		
		Log.i("refract", "Render surface resized [" + width + ", " + height + "]");
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		rendererThread.interrupt();
		
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
	protected void onDraw(Canvas canvas) {
		renderer.render(bitmap, 0, 0, 200);
		
		canvas.drawBitmap(bitmap, 0, 0, null);
		
		statusPanel.setZoom(zoom);
	}

	/**
	 * Sets the status panel
	 * @param statusPanel the status panel
	 */
	public void setStatusPanel(StatusPanel statusPanel) {
		this.statusPanel = statusPanel;
	}
}

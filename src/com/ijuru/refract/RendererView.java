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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * View which displays the fractal rendering
 */
public class RendererView extends SurfaceView implements SurfaceHolder.Callback {
	
	private Bitmap bitmap;
	private Renderer renderer;
	private RendererThread rendererThread;
	private FractalViewer viewer;
	
	// Rendering parameters
	private double real, imag, zoom = 200;
	
	// For dragging
	private double oldMouseX, oldMouseY;
	private double oldReal, oldImag;
	
	public RendererView(Context context, FractalViewer viewer) {
		super(context);
		
		this.viewer = viewer;
		
		getHolder().addCallback(this);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		rendererThread = new RendererThread(this);
		
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
	
	/**
	 * Updates the renderer
	 */
	public void update() {
		// Render into off screen bitmap
		int iters = renderer.render(bitmap, real, imag, zoom);
		
		// Lock canvas to draw to it
		Canvas c = null;
		try {
			c = getHolder().lockCanvas();
			synchronized (getHolder()) {
				onDraw(c);
			}
		} finally {
			if (c != null) {
				getHolder().unlockCanvasAndPost(c);
			}
		}
		
		// Update status
		StatusPanel status = viewer.getStatusPanel();
		status.setZoom(zoom);
		long avgFrameTime = rendererThread.calcAverageFrameTime();
		status.setPerfInfo(iters, avgFrameTime > 0 ? 1000.0 / avgFrameTime : 0);
	}

	/**
	 * @see android.view.View#onDraw(Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	/**
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int action = event.getAction();
		switch (action) {
		case (MotionEvent.ACTION_DOWN): // Touch screen pressed
			oldReal = real;
			oldImag = imag;
			oldMouseX = event.getX();
			oldMouseY = event.getY();
			break;
		case (MotionEvent.ACTION_MOVE): // Dragged finger
			real = oldReal + (oldMouseX - event.getX()) / zoom;
			imag = oldImag - (oldMouseY - event.getY()) / zoom;
			break;
		}
		
		return true;
	}
}

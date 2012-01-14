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

package com.ijuru.refract.ui;

import com.ijuru.refract.Complex;
import com.ijuru.refract.Function;
import com.ijuru.refract.Palette;
import com.ijuru.refract.RendererThread;
import com.ijuru.refract.renderer.NativeRenderer;
import com.ijuru.refract.renderer.Renderer;
import com.ijuru.refract.utils.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * View which displays the fractal rendering
 */
public class RendererView extends SurfaceView implements SurfaceHolder.Callback {
	
	private Bitmap bitmap;
	private Renderer renderer;
	private RendererThread rendererThread;
	private ExplorerView viewer;
	
	// Rendering parameters
	private int itersPerFrame;
	
	// For dragging / panning
	private double oldMouseX, oldMouseY;

	// For pinch zooming
	private ScaleGestureDetector scaleDetector;
	
	public RendererView(Context context, ExplorerView viewer) {
		super(context);
		
		this.viewer = viewer;
		
		getHolder().addCallback(this);
		
		scaleDetector = new ScaleGestureDetector(context, new ZoomListener());
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		rendererThread = new RendererThread(this);
		
		Log.d("refract", "Render surface created [" + getWidth() + ", " + getHeight() + "]");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		if (renderer == null)
			renderer = new NativeRenderer();
		else
			renderer.free();
		
		if (!renderer.allocate(width, height)) {
			Toast.makeText(getContext(), "Unable to allocate resources", Toast.LENGTH_LONG).show();
			renderer = null;
			return;
		}
		
		// Get rendering parameters from preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		Function iterFunc = Function.parseString(prefs.getString("iterfunc", "mandelbrot"));
		itersPerFrame = Utils.parseInteger(prefs.getString("itersperframe", "5"));
		Palette palette = Palette.getPresetByName(prefs.getString("palette", "sunset").toLowerCase());
		
		renderer.setFunction(iterFunc);
		renderer.setPalette(palette);
			
		if (!rendererThread.isAlive())
			rendererThread.start();
		
		Log.d("refract", "Render surface resized [" + width + ", " + height + "]");
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
		
		if (renderer != null) {
			renderer.free();
			renderer = null;
		}
		
		Log.d("refract", "Render surface destroyed");
	}
	
	/**
	 * Updates the renderer
	 */
	public void update() {
		int iters = renderer.iterate(itersPerFrame);
		
		// Render into off screen bitmap
		renderer.render(bitmap);
		
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
		
		// Update performance status info
		long avgFrameTime = rendererThread.calcSmoothedFrameTime();
		viewer.getStatusPanel().setPerformanceInfo(iters, avgFrameTime > 0 ? 1000.0 / avgFrameTime : 0);
		
		//Log.v("refract", "Render surface updated [" + rendererThread.getLastFrameTime() + "ms]");
	}

	/**
	 * @see android.view.View#onDraw(Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (renderer != null)
			canvas.drawBitmap(bitmap, 0, 0, null);
	}

	/**
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		scaleDetector.onTouchEvent(event);
		
		if (renderer == null)
			return false;
		
		switch (event.getAction()) {
		case (MotionEvent.ACTION_DOWN): // Touch screen pressed
			oldMouseX = event.getX();
			oldMouseY = event.getY();
			break;
		case (MotionEvent.ACTION_MOVE): // Dragged finger
			if (!scaleDetector.isInProgress()) {
				double zoom = renderer.getZoom();
				Complex offset = renderer.getOffset();
				offset.re += (oldMouseX - event.getX()) / zoom;
				offset.im -= (oldMouseY - event.getY()) / zoom;
				renderer.setOffset(offset);
				oldMouseX = event.getX();
				oldMouseY = event.getY();
				
				// Update status info
				viewer.getStatusPanel().setCoords(offset.re, offset.im);
			}
			break;
		}
		
		return true;
	}
	
	/**
	 * Listener for pinch-zoom gestures
	 */
	private class ZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		/**
		 * @see ScaleGestureDetector.SimpleOnScaleGestureListener#onScale(ScaleGestureDetector)
		 */
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (renderer == null)
				return false;
			
			double newZoom = renderer.getZoom() * detector.getScaleFactor();
			renderer.setZoom(newZoom);
			
			// Update status info
			viewer.getStatusPanel().setZoom(newZoom);
			
			return true;
		}
	}

	/**
	 * Gets the renderer thread
	 * @return the renderer thread
	 */
	public RendererThread getRendererThread() {
		return rendererThread;
	}
}

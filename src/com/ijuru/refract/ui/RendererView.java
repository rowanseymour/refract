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
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
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
	private RendererListener listener;
	
	// Rendering parameters
	private int itersPerFrame;

	// For panning and zooming
	private GestureDetector panDetector;
	private ScaleGestureDetector scaleDetector;
	
	public RendererView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		getHolder().addCallback(this);
		
		panDetector = new GestureDetector(context, new PanListener());
		scaleDetector = new ScaleGestureDetector(context, new ZoomListener());
	}
	
	/**
	 * Listener interface
	 */
	public interface RendererListener {
		public void onRendererCreated(RendererView view, Renderer renderer);
		public void onRendererOffsetChanged(RendererView view, Complex offset);
		public void onRendererZoomChanged(RendererView view, double zoom);
		public void onRendererUpdate(RendererView view, int iters);
	}
	
	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		renderer = new NativeRenderer();
		
		/**
		 * TODO do something appropriate if renderer allocation fails
		 */
		if (!renderer.allocate(getWidth(), getHeight())) {
			Toast.makeText(getContext(), "Unable to allocate resources", Toast.LENGTH_LONG).show();
			return;
		}
		
		// Get rendering parameters from preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		Function iterFunc = Function.parseString(prefs.getString("iterfunc", "mandelbrot"));
		itersPerFrame = Utils.parseInteger(prefs.getString("itersperframe", "5"));
		Palette palette = Palette.getPresetByName(prefs.getString("palette", "sunset").toLowerCase());
		
		renderer.setFunction(iterFunc);
		renderer.setPalette(palette, 128);
		
		// Start renderer thread
		rendererThread = new RendererThread(this);
		rendererThread.start();
		
		if (listener != null)
			listener.onRendererCreated(this, renderer);
		
		Log.d("refract", "Render surface created [" + getWidth() + ", " + getHeight() + "]");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// Reallocate renderer and off-screen bitmap only if size has changed
		if (renderer.getWidth() != width || renderer.getHeight() != height) {
			renderer.resize(width, height);
			bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		}
		
		Log.d("refract", "Render surface resized [" + width + ", " + height + "]");
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		// Stop renderer thread
		if (rendererThread != null) {
			boolean retry = true;
			rendererThread.interrupt();
			
			while (retry) {
				try {
					rendererThread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}
		
		// Deallocate renderer
		renderer.free();
		
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
		
		if (listener != null)
			listener.onRendererUpdate(RendererView.this, iters);
		
		//Log.v("refract", "Render surface updated [" + rendererThread.getLastFrameTime() + "ms]");
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
		// Let the gesture detectors handle the event
		panDetector.onTouchEvent(event);
		scaleDetector.onTouchEvent(event);
		
		return true;
	}
	
	/**
	 * Listener for drag-pan gestures
	 */
	private class PanListener extends GestureDetector.SimpleOnGestureListener {
		
		/**
		 * @see GestureDetector.SimpleOnGestureListener#onScroll(MotionEvent, MotionEvent, float, float)
		 */
		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
			double zoom = renderer.getZoom();
			Complex offset = renderer.getOffset();
			Complex delta = new Complex(distanceX / zoom, -distanceY / zoom);
			setOffset(offset.add(delta));		
			return true;
		}
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
			setZoom(renderer.getZoom() * detector.getScaleFactor());			
			return true;
		}
	}
	
	/**
	 * Resets this view so that it is centered on the origin
	 */
	public void reset() {
		setOffset(Complex.ORIGIN);
		setZoom(renderer.getWidth() / 2);
	}
	
	/**
	 * Gets the offset of the renderer
	 * @return the offset
	 */
	public Complex getOffset() {
		return renderer.getOffset();
	}
	
	/**
	 * Sets the offset of the renderer
	 * @param offset the offset
	 */
	public void setOffset(Complex offset) {
		renderer.setOffset(offset);
		
		if (listener != null)
			listener.onRendererOffsetChanged(this, offset);
	}
	
	/**
	 * Gets the zoom of the renderer
	 * @return the zoom
	 */
	public double getZoom() {
		return renderer.getZoom();
	}
	
	/**
	 * Sets the zoom of the renderer
	 * @param zoom the zoom
	 */
	public void setZoom(double zoom) {
		renderer.setZoom(zoom);
		
		if (listener != null)
			listener.onRendererZoomChanged(this, zoom);
	}
	
	/**
	 * Sets the renderer listener
	 * @param listener the listener
	 */
	public void setRendererListener(RendererListener listener) {
		this.listener = listener;
	}

	/**
	 * Gets the renderer thread
	 * @return the renderer thread
	 */
	public RendererThread getRendererThread() {
		return rendererThread;
	}
}

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

import com.ijuru.refract.R;
import com.ijuru.refract.renderer.Complex;
import com.ijuru.refract.renderer.Renderer;
import com.ijuru.refract.renderer.RendererFactory;
import com.ijuru.refract.renderer.RendererListener;
import com.ijuru.refract.renderer.RendererParams;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * View which displays a rendering
 */
public class RendererView extends SurfaceView implements SurfaceHolder.Callback {
	
	private boolean navigationEnabled;
	private Bitmap bitmap;
	private Renderer renderer;
	private RendererThread rendererThread;
	private RendererListener listener;

	// Rendering parameters
	private int itersPerFrame = 5;

	// For panning and zooming
	private GestureDetector panDetector;
	private ScaleGestureDetector scaleDetector;
	private boolean navigationInProgress;
	private RendererParams bitmapParams;
	
	/**
	 * Constructs a renderer view whose renderer scales it's internal storage with the view
	 * @param context the context
	 * @param attrs the attributes from the layout resource
	 */
	public RendererView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		getHolder().addCallback(this);
		
		// Get custom attribute values
		TypedArray arrAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.RendererView);
		navigationEnabled = arrAttrs.getBoolean(R.styleable.RendererView_navigationEnabled, true);
		arrAttrs.recycle();
		
		// Optionally enable touch based navigation
		if (navigationEnabled) {
			panDetector = new GestureDetector(context, new PanListener());
			scaleDetector = new ScaleGestureDetector(context, new ZoomListener());
		}
	}
	
	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {		
		int rendererWidth = getDesiredRendererWidth(getWidth());
		int rendererHeight = getDesiredRendererHeight(getHeight());
		
		bitmap = Bitmap.createBitmap(rendererWidth, rendererHeight, Config.ARGB_8888);
		renderer = RendererFactory.createRenderer();
		
		/**
		 * TODO do something appropriate if renderer allocation fails
		 */
		if (!renderer.allocate(rendererWidth, rendererHeight)) {
			Toast.makeText(getContext(), "Unable to allocate resources", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (listener != null)
			listener.onRendererCreated(this, renderer);
		
		// Start renderer thread
		rendererThread = new RendererThread(this);
		rendererThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {	
		int rendererWidth = getDesiredRendererWidth(width);
		int rendererHeight = getDesiredRendererHeight(height);
		
		// Reallocate renderer and off-screen bitmap only if size has changed 
		if (renderer.getWidth() != rendererWidth || renderer.getHeight() != rendererHeight) {
			renderer.resize(width, height);
			bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {	
		// Stop renderer thread
		stopRendering();
		
		// Notify listener that renderer is about to be destroyed
		if (listener != null)
			listener.onRendererDestroy(this, renderer);
		
		// Deallocate renderer
		renderer.free();
	}
	
	/**
	 * Updates the renderer
	 */
	public void update() {
		// Only iterate if we're not panning/zooming
		if (!navigationInProgress) {
			int iters = renderer.iterate(itersPerFrame);
			
			if (listener != null)
				listener.onRendererIterated(this, renderer, iters);
			
			// Render into off screen bitmap
			synchronized (this) {
				renderer.render(bitmap);
				bitmapParams = getRendererParams();
			}
		}
		
		// Lock canvas to draw to it
		Canvas canvas = null;
		try {
			canvas = getHolder().lockCanvas();
			synchronized (getHolder()) {
				onDraw(canvas);
			}
		} finally {
			if (canvas != null) {
				getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}

	/**
	 * @see android.view.View#onDraw(Canvas)
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		RendererParams rendererParams = getRendererParams();
		
		// Are the params rendered in the bitmap the same as the renderer's params?
		if (rendererParams.equals(bitmapParams)) {
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
		else {
			// Calculate the complex space covered by the bitmap
			Complex bitmap_c1 = pixelsToComplex(bitmapParams, new Point(0, 0));
			Complex bitmap_c2 = pixelsToComplex(bitmapParams, new Point(bitmap.getWidth(), bitmap.getHeight()));
			
			// Map those complex points back into pixel space according to the current renderer params
			Point bitmap_p1 = complexToPixels(rendererParams, bitmap_c1);
			Point bitmap_p2 = complexToPixels(rendererParams, bitmap_c2);
			
			// Draw pre-navigation bitmap where render would be
			canvas.drawARGB(255, 0, 0, 0);
			canvas.drawBitmap(bitmap, null, new Rect(bitmap_p1.x, bitmap_p1.y, bitmap_p2.x, bitmap_p2.y), null);
		}
	}
	
	/**
	 * Stops the rendering thread and doesn't return until it does
	 */
	public void stopRendering() {
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
	}

	/**
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (navigationEnabled) {
			// Let the gesture detectors handle the event
			panDetector.onTouchEvent(event);
			scaleDetector.onTouchEvent(event);
			
			if (event.getAction() == MotionEvent.ACTION_DOWN)
				navigationInProgress = true;
			if (event.getAction() == MotionEvent.ACTION_UP)
				navigationInProgress = false;
		}
		
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
	 * Gets the current renderer parameters
	 * @return the parameters
	 */
	public RendererParams getRendererParams() {
		return new RendererParams(renderer.getFunction(), renderer.getOffset(), renderer.getZoom());
	}
	
	/**
	 * Sets the offset of the renderer
	 * @param offset the offset
	 */
	public void setOffset(Complex offset) {
		renderer.setOffset(offset);
		
		if (listener != null)
			listener.onRendererOffsetChanged(this, renderer, offset);
	}
	
	/**
	 * Sets the zoom of the renderer
	 * @param zoom the zoom
	 */
	public void setZoom(double zoom) {
		renderer.setZoom(zoom);
		
		if (listener != null)
			listener.onRendererZoomChanged(this, renderer, zoom);
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
	
	/**
	 * Gets the off-screen bitmap
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	/**
	 * Gets the desired renderer width
	 * @param viewWidth the width of the view
	 * @return the width
	 */
	protected int getDesiredRendererWidth(int viewWidth) {
		return viewWidth;
	}
	
	/**
	 * Gets the desired renderer height
	 * @param viewHeight the height of the view
	 * @return the height
	 */
	protected int getDesiredRendererHeight(int viewHeight) {
		return viewHeight;
	}
	
	/**
	 * Gets the number of iterations per frame
	 * @return the number of iterations
	 */
	public int getIterationsPerFrame() {
		return itersPerFrame;
	}

	/**
	 * Sets the number of iterations per frame
	 * @param itersPerFrame the number of iterations
	 */
	public void setIterationsPerFrame(int itersPerFrame) {
		this.itersPerFrame = itersPerFrame;
	}
	
	/**
	 * Converts the pixel space point to coordinate in complex-space
	 * @param params the renderer params
	 * @param p the point in pixel space
	 * @return the coordinate in complex space
	 */
	protected Complex pixelsToComplex(RendererParams params, Point p) {
		Complex offset = params.getOffset();
		double inv_zoom = 1 / params.getZoom();
		int half_w = getWidth() / 2;
		int half_h = getHeight() / 2;
		double re = (p.x - half_w) * inv_zoom + offset.re;
		double im = (half_h - p.y) * inv_zoom + offset.im;
		return new Complex(re, im);
	}
	
	/**
	 * Converts the pixel space point to coordinate in complex space
	 * @param params the renderer params
	 * @param c the coordinate in complex space
	 * @return the point in pixel space
	 */
	private Point complexToPixels(RendererParams params, Complex c) {
		Complex offset = params.getOffset();
		double zoom = params.getZoom();
		int half_w = getWidth() / 2;
		int half_h = getHeight() / 2;
		double x = (c.re - offset.re) * zoom + half_w;
		double y = half_h - (c.im - offset.im) * zoom;
		return new Point((int)x, (int)y);
	}
}

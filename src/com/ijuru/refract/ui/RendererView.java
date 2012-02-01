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
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * View which displays a rendering
 */
public class RendererView extends SurfaceView implements SurfaceHolder.Callback {
	
	private Bitmap bitmap;
	private Renderer renderer;
	private RendererThread rendererThread;
	private RendererListener listener;

	// Rendering parameters
	private int itersPerFrame = 5;

	// For panning and zooming
	private boolean navigationEnabled;
	private MultiTouchGestureDetector navigationDetector;
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
		if (navigationEnabled)
			navigationDetector = new MultiTouchGestureDetector(new NavigationListener());
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
		if (!navigationDetector.isInProgress()) {
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
			Complex bitmap_c1 = pixelsToComplex(bitmapParams, new PointF(0, 0));
			Complex bitmap_c2 = pixelsToComplex(bitmapParams, new PointF(bitmap.getWidth(), bitmap.getHeight()));
			
			// Map those complex points back into pixel space according to the current renderer params
			PointF bitmap_p1 = complexToPixels(rendererParams, bitmap_c1);
			PointF bitmap_p2 = complexToPixels(rendererParams, bitmap_c2);
			
			// Draw pre-navigation bitmap where render would be
			canvas.drawARGB(255, 0, 0, 0);
			canvas.drawBitmap(bitmap, null, new RectF(bitmap_p1.x, bitmap_p1.y, bitmap_p2.x, bitmap_p2.y), null);
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
		if (navigationEnabled)
			// Let the gesture detector handle the event
			return navigationDetector.onTouchEvent(event);
		
		return false;
	}
	
	/**
	 * Listener for panning and zooming gestures
	 */
	private class NavigationListener implements MultiTouchGestureDetector.OnMultiTouchGestureListener {
		
		/**
		 * @see com.ijuru.refract.ui.MultiTouchGestureDetector.OnMultiTouchGestureListener#onMultiTouchGesture(PointF[], PointF[])
		 */
		@Override
		public void onMultiTouchGesture(PointF[] prevPoints, PointF[] currPoints) {
			if (prevPoints.length == 1)
				panGesture(prevPoints[0], currPoints[0]);
			else if (prevPoints.length == 2)
				zoomGesture(prevPoints[0], currPoints[0], prevPoints[1], currPoints[1]);
		}
		
		private void panGesture(PointF prevPoint, PointF currPoint) {
			RendererParams params = getRendererParams();
			Complex prevC = pixelsToComplex(params, prevPoint);
			Complex currC = pixelsToComplex(params, currPoint);
			Complex diff = currC.sub(prevC);
			setOffset(renderer.getOffset().sub(diff));
		}
		
		private void zoomGesture(PointF prevPoint1, PointF currPoint1, PointF prevPoint2, PointF currPoint2) {
			float prevDistX = prevPoint1.x - prevPoint2.x;
			float prevDistY = prevPoint1.y - prevPoint2.y;
			float currDistX = currPoint1.x - currPoint2.x;
			float currDistY = currPoint1.y - currPoint2.y;
			float prevDist = FloatMath.sqrt(prevDistX * prevDistX + prevDistY * prevDistY);
			float currDist = FloatMath.sqrt(currDistX * currDistX + currDistY * currDistY);
			float scaleFactor = currDist / prevDist;
			
			// Map previous points into complex space using current params
			RendererParams prevParams = getRendererParams();
			Complex prevC1 = pixelsToComplex(prevParams, prevPoint1);
			Complex prevC2 = pixelsToComplex(prevParams, prevPoint2);
			
			// Map current points into complex space using params with new scale applied
			double newZoom = prevParams.getZoom() * scaleFactor;
			RendererParams currParams = new RendererParams(prevParams.getFunction(), prevParams.getOffset(), newZoom);
			Complex currC1 = pixelsToComplex(currParams, currPoint1);
			Complex currC2 = pixelsToComplex(currParams, currPoint2);
			
			// Calculate mid-points and their diff
			Complex prevMP = prevC1.add(prevC2).scale(0.5);
			Complex currMP = currC1.add(currC2).scale(0.5);
			Complex diff = currMP.sub(prevMP);
			
			// Update renderer offset and zoom
			setOffset(renderer.getOffset().sub(diff));
			setZoom(renderer.getZoom() * scaleFactor);
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
	protected Complex pixelsToComplex(RendererParams params, PointF p) {
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
	private PointF complexToPixels(RendererParams params, Complex c) {
		Complex offset = params.getOffset();
		double zoom = params.getZoom();
		int half_w = getWidth() / 2;
		int half_h = getHeight() / 2;
		double x = (c.re - offset.re) * zoom + half_w;
		double y = half_h - (c.im - offset.im) * zoom;
		return new PointF((float)x, (float)y);
	}
}

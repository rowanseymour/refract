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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * A renderer view which maintains a fixed size renderer and scales it's image when drawing
 */
public abstract class ScaledRenderView extends RenderView {
	
	/**
	 * Constructs a scaled renderer view
	 * @param context the context
	 * @param attrs the attributes
	 */
	public ScaledRenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
		
	/**
	 * @see com.ijuru.refract.ui.RenderView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		Bitmap bitmap = getBitmap();
		double rendererAspectRatio = bitmap.getWidth() / (double)bitmap.getHeight();
		double viewAspectRatio = getWidth() / (double)getHeight();
			
		// Calculate the largest size the render can be in the view
		// while maintaining the aspect ratio of the render
		int dstWidth, dstHeight;
		if (rendererAspectRatio > viewAspectRatio) {
			dstWidth = getWidth();
			dstHeight = (int)(dstWidth / rendererAspectRatio);
		}
		else {
			dstHeight = getHeight();
			dstWidth = (int)(dstHeight * rendererAspectRatio);
		}
		
		// Calculate source and destination rectangles that will center the render
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;	
		Rect dstRect = new Rect(centerX - dstWidth / 2, centerY - dstHeight / 2, centerX + dstWidth / 2, centerY + dstHeight / 2);
		Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		
		canvas.drawBitmap(bitmap, srcRect, dstRect, null);
	}

	/**
	 * Must be overridden by subclasses to specify renderer width
	 * @see com.ijuru.refract.ui.RenderView#getDesiredRendererWidth(int)
	 */
	@Override
	protected abstract int getDesiredRendererWidth(int viewWidth);

	/**
	 * Must be overridden by subclasses to specify renderer height
	 * @see com.ijuru.refract.ui.RenderView#getDesiredRendererHeight(int)
	 */
	@Override
	protected abstract int getDesiredRendererHeight(int viewHeight);
}

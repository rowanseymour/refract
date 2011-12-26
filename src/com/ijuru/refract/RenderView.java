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

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * The view for rendering fractals
 */
public class RenderView extends View {

	private FractalRenderer renderer;

	/**
	 * Constructs a render view
	 * @param context the context
	 * @param attrs the attributes
	 */
	public RenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		initRenderer();

		super.onSizeChanged(width, height, oldw, oldh);
	}
	
	private void initRenderer() {
		if (renderer != null)
			renderer.free();
		
		renderer = new FractalRenderer();
		
		renderer.allocate(getWidth(), getHeight());
	}

	/**
	 * @see android.view.View#onDraw(Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (renderer == null)
			initRenderer();
		
		renderer.update();

		canvas.drawBitmap(renderer.getBitmap(), 0, 0, null);

		invalidate();
	}
}

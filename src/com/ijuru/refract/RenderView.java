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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * The view for rendering fractals
 */
public class RenderView extends View {

	private Bitmap bitmap;

	/**
	 * Constructs a render view
	 * @param context the context
	 * @param attrs the attributes
	 */
	public RenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Renders a fractal image
	 * @param bitmap the bitmap to render to
	 */
	private static native void render(Bitmap bitmap);

	private void init() {
		bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
	}

	/**
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		init();

		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * @see android.view.View#onDraw(Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (bitmap == null)
			init();

		render(bitmap);
		canvas.drawBitmap(bitmap, 0, 0, null);

		invalidate();
	}
}

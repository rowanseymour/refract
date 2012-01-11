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

import com.ijuru.refract.Palette;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * View to display a preview of a palette
 */
public class PaletteView extends View {

	private Palette palette;
	
	public PaletteView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (palette != null) {
		    Paint p = new Paint();
		    p.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), palette.getColors(), palette.getAnchors(), Shader.TileMode.CLAMP));
		    canvas.drawPaint(p);
		}
	}
	
	/**
	 * Sets the palette to be displayed
	 * @param palette the palette
	 */
	public void setPalette(Palette palette) {
		this.palette = palette;
	}
}
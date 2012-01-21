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

import android.app.WallpaperManager;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Renderer view whose renderer size matches the system wallpaper size
 */
public class WallpaperRendererView extends ScaledRendererView {
	
	private WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
	
	/**
	 * Constructs a wallpaper renderer view
	 * @param context the context
	 * @param attrs the attributes
	 */
	public WallpaperRendererView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * @see com.ijuru.refract.ui.ScaledRendererView#getDesiredRendererWidth(int)
	 */
	@Override
	protected int getDesiredRendererWidth(int viewWidth) {
		return wallpaperManager.getDesiredMinimumWidth();
	}

	/**
	 * @see com.ijuru.refract.ui.ScaledRendererView#getDesiredRendererHeight(int)
	 */
	@Override
	protected int getDesiredRendererHeight(int viewHeight) {
		return wallpaperManager.getDesiredMinimumHeight();
	}
}
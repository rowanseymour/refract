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

package com.ijuru.refract.renderer;

import android.graphics.Bitmap;

/**
 * A bookmark - i.e. a location in particular set
 */
public class Bookmark {

	private RendererParams params;
	private Bitmap image;
	
	/**
	 * Constructs a new bookmark
	 * @param params
	 * @param image
	 */
	public Bookmark(RendererParams params, Bitmap image) {
		this.params = params;
		this.image = image;
	}

	/**
	 * Gets the renderer parameters
	 * @return the parameters
	 */
	public RendererParams getParams() {
		return params;
	}

	/**
	 * Gets the preview image
	 * @return the image
	 */
	public Bitmap getImage() {
		return image;
	}
}

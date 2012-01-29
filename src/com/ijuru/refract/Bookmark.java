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

import java.util.Date;

import com.ijuru.refract.renderer.RendererParams;

import android.graphics.Bitmap;

/**
 * A bookmark - i.e. a location in particular set
 */
public class Bookmark {

	public static final int THUMBNAIL_WIDTH = 128;
	public static final int THUMBNAIL_HEIGHT = 128;
	
	private RendererParams params;
	private Bitmap thumbnail;
	private Date timestamp;
	
	/**
	 * Constructs a new bookmark
	 * @param params the renderer parameters
	 * @param thumbnail the thumbnail image
	 * @param timestamp the creation timestamp
	 */
	public Bookmark(RendererParams params, Bitmap thumbnail, Date timestamp) {
		this.params = params;
		this.thumbnail = thumbnail;
		this.timestamp = timestamp;
	}

	/**
	 * Gets the renderer parameters
	 * @return the parameters
	 */
	public RendererParams getParams() {
		return params;
	}

	/**
	 * Gets the thumbnail image
	 * @return the thumbnail
	 */
	public Bitmap getThumbnail() {
		return thumbnail;
	}

	/**
	 * Gets the creation timestamp
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
}

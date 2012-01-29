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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * Manager for bookmarks
 */
public class BookmarkManager {

	private static final String BOOKMARKS_DIR_NAME = "bookmarks";
	private Context context;
	
	/**
	 * Creates a new bookmark manager
	 * @param context the context
	 */
	public BookmarkManager(Context context) {
		this.context = context;
	}
	
	/**
	 * Loads all existing bookmarks
	 * @return the bookmarks
	 */
	public List<Bookmark> loadAllBookmarks() {
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		File bookmarksDir = getBookmarksDirectory();
		
		return bookmarks;
	}
	
	public boolean addBookmark(Bookmark bookmark) {
		return false;
	}
	
	public boolean deleteBookmark(Bookmark bookmark) {
		return false;
	}
	
	/**
	 * Gets the bookmarks directory, creating it if it doesn't exist
	 * @return the directory or null if it doesn't exist and couldn't be created
	 */
	private File getBookmarksDirectory() {
		File bookmarksDir = new File(context.getFilesDir(), BOOKMARKS_DIR_NAME);
		if (!bookmarksDir.exists()) {
			if (bookmarksDir.mkdir())
				return bookmarksDir;
		}
		return null;
	}
}

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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ijuru.refract.renderer.RendererParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

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
	public List<Bookmark> loadBookmarks() {
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		File bookmarksDir = getBookmarksDirectory();
		File[] paramsFiles = bookmarksDir.listFiles(new FilenameFilter() {	
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".dat");
			}
		});
		for (File paramsFile : paramsFiles) {
			// Strip extension to get base name of this bookmark
			String baseName = paramsFile.getName().substring(0, paramsFile.getName().lastIndexOf('.'));
			
			File thumbFile = new File(bookmarksDir, baseName + ".png");
			long timestamp = Long.parseLong(baseName);
			
			Bookmark bookmark = loadBookmark(paramsFile, thumbFile, timestamp);
			if (bookmark != null)
				bookmarks.add(bookmark);
		}
		
		return bookmarks;
	}
	
	/**
	 * Adds a new bookmark
	 * @param bookmark the bookmark
	 * @return true if successful, else false
	 */
	public boolean addBookmark(Bookmark bookmark) {
		File bookmarksDir = getBookmarksDirectory();
		File bookmarkParamFile = new File(bookmarksDir, bookmark.getParamsFilename());
		File bookmarkThumbFile = new File(bookmarksDir, bookmark.getThumbnailFilename());
		
		try {
			// Create files for this bookmark
			if (!bookmarkParamFile.createNewFile() || !bookmarkThumbFile.createNewFile())
				return false;
			
			// Write params as text file
			Writer paramOut = new FileWriter(bookmarkParamFile);
			bookmark.getParams().write(paramOut);
			paramOut.close();
			
			// Write thumbnail as PNG
			OutputStream thumbOut = new FileOutputStream(bookmarkThumbFile);
			bookmark.getThumbnail().compress(CompressFormat.PNG, 80, thumbOut);
			thumbOut.close();
			
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Deletes the given bookmark
	 * @param bookmark the bookmark
	 * @return true if successful, else false
	 */
	public boolean deleteBookmark(Bookmark bookmark) {
		File bookmarksDir = getBookmarksDirectory();
		File bookmarkParamFile = new File(bookmarksDir, bookmark.getParamsFilename());
		File bookmarkThumbFile = new File(bookmarksDir, bookmark.getThumbnailFilename());
		return bookmarkParamFile.delete() && bookmarkThumbFile.delete();
	}
	
	/**
	 * Gets the bookmarks directory, creating it if it doesn't exist
	 * @return the directory or null if it doesn't exist and couldn't be created
	 */
	private File getBookmarksDirectory() {
		File bookmarksDir = new File(context.getFilesDir(), BOOKMARKS_DIR_NAME);
		if (!bookmarksDir.exists()) {
			if (!bookmarksDir.mkdir())
				return null;
		}
		return bookmarksDir;
	} 
	
	/**
	 * Loads a bookmark
	 * @param paramsFile the renderer params file
	 * @param thumbFile the thumbnail image file
	 * @param timestamp the timestamp
	 * @return the bookmark
	 */
	private Bookmark loadBookmark(File paramsFile, File thumbFile, long timestamp) {
		try {
			// Load renderer params
			Reader paramsReader = new FileReader(paramsFile);
			RendererParams params = RendererParams.read(paramsReader);
			paramsReader.close();
			
			// Load thumbnail image
			Bitmap thumbnail = BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
			
			return new Bookmark(params, thumbnail, new Date(timestamp));
		} catch (Exception e) {
		}
		
		return null;
	}
}

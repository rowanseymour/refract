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

package com.ijuru.refract.activity;

import java.util.Date;
import java.util.List;

import com.ijuru.refract.Bookmark;
import com.ijuru.refract.BookmarkManager;
import com.ijuru.refract.R;
import com.ijuru.refract.RefractApplication;
import com.ijuru.refract.renderer.RendererParams;
import com.ijuru.refract.ui.BookmarkAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.GridView;

/**
 * Activity to manage bookmarks
 */
public class BookmarksActivity extends Activity {

	private BookmarkManager bookmarkManager;
	private Bookmark newBookmark;
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bookmarks);
		
		Intent intent = getIntent();
		if (intent != null) {
			RendererParams newBookmarkParams = (RendererParams)(intent.hasExtra("params") ? intent.getExtras().getParcelable("params") : null);
			Bitmap newBookmarkThumbnail = (Bitmap)(intent.hasExtra("thumbnail") ? intent.getExtras().getParcelable("thumbnail") : null);
			newBookmark = new Bookmark(newBookmarkParams, newBookmarkThumbnail, new Date());
		}
		
		// Get all existing bookmarks
		bookmarkManager = ((RefractApplication)getApplication()).getBookmarkManager();
		List<Bookmark> bookmarks = bookmarkManager.loadAllBookmarks();
		
		// Insert current params as potential bookmark at beginning
		if (newBookmark != null)
			bookmarks.add(0, newBookmark);
		
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		bookmarks.add(newBookmark);
		
		GridView gridview = (GridView)findViewById(R.id.gridview);
	    gridview.setAdapter(new BookmarkAdapter(this, bookmarks));
	}
	
	public void onAdd() {
		bookmarkManager.addBookmark(newBookmark);
		finish();
	}
}

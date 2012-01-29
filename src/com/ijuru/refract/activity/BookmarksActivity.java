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
import com.ijuru.refract.utils.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Activity to manage bookmarks
 */
public class BookmarksActivity extends Activity implements OnItemClickListener {

	private static final int MENU_DELETE = 1;
	
	private GridView gridview;
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
		List<Bookmark> bookmarks = bookmarkManager.loadBookmarks();
		
		// Insert current params as potential bookmark at beginning
		if (newBookmark != null)
			bookmarks.add(0, newBookmark);
		
		gridview = (GridView)findViewById(R.id.gridview);
	    gridview.setAdapter(new BookmarkAdapter(this, bookmarks));
	    gridview.setOnItemClickListener(this);
	    
	    registerForContextMenu(gridview);
	}

	/**
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(AdapterView, View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View itemView, int position, long id) {
		if (newBookmark != null && position == 0) {
			if (bookmarkManager.addBookmark(newBookmark))
				Toast.makeText(this, R.string.str_bookmarksaved, Toast.LENGTH_SHORT).show();
		} else {
			Bookmark bookmark = (Bookmark)adapterView.getAdapter().getItem(position);
			Preferences.setParametersPreference(this, "params", bookmark.getParams());
		}
		finish();
	}

	/**
	 * @see android.app.Activity#onCreateContextMenu(ContextMenu, View, ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if (info.position != 0) {
			menu.setHeaderTitle(R.string.str_bookmark);
			menu.add(Menu.NONE, MENU_DELETE, 0, R.string.str_delete);
		}
	}
	
	/**
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE:
			Bookmark bookmark = (Bookmark)gridview.getSelectedItem();
			if (bookmarkManager.deleteBookmark(bookmark))
				Toast.makeText(this, R.string.str_bookmarkdeleted, Toast.LENGTH_SHORT);
			break;
		}
		
		return true;
	}
}

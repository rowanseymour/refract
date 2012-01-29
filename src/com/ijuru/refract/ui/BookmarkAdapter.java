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

import java.util.List;

import com.ijuru.refract.Bookmark;
import com.ijuru.refract.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for bookmarks
 */
public class BookmarkAdapter extends ArrayAdapter<Bookmark> {
	
	private LayoutInflater inflater;
	
	public BookmarkAdapter(Context context, List<Bookmark> bookmarks) {
		super(context, 0, bookmarks);
		
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = (convertView == null) ? inflater.inflate(R.layout.bookmark_item, parent, false) : convertView;			
		ImageView imageView = (ImageView)view.findViewById(R.id.thumbnail);
		TextView textView = (TextView)view.findViewById(R.id.thumbtext);

        imageView.setImageBitmap(getItem(position).getThumbnail());
        
        // First bookmark is special add function
        if (position == 0) {
        	textView.setVisibility(View.VISIBLE);
        	imageView.setColorFilter(0xAA000000);
        }
        else {
        	textView.setVisibility(View.INVISIBLE);
        	imageView.setColorFilter(0);
        }
        
        return view;
	}
}

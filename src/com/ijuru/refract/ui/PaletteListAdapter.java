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
import com.ijuru.refract.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * List adapter for palette objects
 */
public class PaletteListAdapter extends ArrayAdapter<Palette> implements OnClickListener {

	private PalettePreference preference;
	private LayoutInflater inflater;
	
	public PaletteListAdapter(Context context, PalettePreference preference) {
		super(context, R.layout.palette_list_item, Palette.getPresets());
		
		this.preference = preference;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.palette_list_item, parent, false);
		view.setOnClickListener(this);
		view.setId(position);

		Palette palette = this.getItem(position);
		PaletteView preview = (PaletteView)view.findViewById(R.id.preview);
		preview.setPalette(palette);
		
		return view;
	}

	@Override
	public void onClick(View itemView) {
		preference.setValueIndex(itemView.getId());
		preference.getDialog().dismiss();
	}
}
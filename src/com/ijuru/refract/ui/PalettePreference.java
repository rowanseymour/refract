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

import com.ijuru.refract.renderer.Palette;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * Preference for palette
 */
public class PalettePreference extends ListPreference {

	/**
	 * Constructs a palette preference
	 * @param context the context
	 * @param attrs the attributes
	 */
	public PalettePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Make array of palette names
		String[] palNames = new String[Palette.getPresets().size()];
		for (int p = 0; p < palNames.length; ++p) {
			palNames[p] = Palette.getPresets().get(p).getName();
		}
		
		setEntries(palNames);
		setEntryValues(palNames);
	}
	
	/**
	 * @see android.preference.ListPreference#onPrepareDialogBuilder(android.app.AlertDialog.Builder)
	 */
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		PaletteAdapter adapter = new PaletteAdapter(getContext(), this);
		
		builder.setAdapter(adapter, this);

		super.onPrepareDialogBuilder(builder);
	}
}

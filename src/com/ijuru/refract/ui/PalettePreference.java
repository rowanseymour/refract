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

import com.ijuru.refract.PaletteDefinition;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * Preference for palette
 */
public class PalettePreference extends ListPreference {

	public PalettePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Make array of palette names
		String[] palNames = new String[PaletteDefinition.getPresets().size()];
		for (int p = 0; p < palNames.length; ++p) {
			palNames[p] = PaletteDefinition.getPresets().get(p).getName();
		}
		
		setEntries(palNames);
		setEntryValues(palNames);
	}
	
	/**
	 * @see android.preference.ListPreference#onPrepareDialogBuilder(android.app.AlertDialog.Builder)
	 */
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		//int index = findIndexOfValue(getSharedPreferences().getString(getKey(), "sunset"));
		PaletteListAdapter adapter = new PaletteListAdapter(getContext(), this);
		
		builder.setAdapter(adapter, this);

		super.onPrepareDialogBuilder(builder);
	}

	/**
	 * @see android.preference.ListPreference#onDialogClosed(boolean)
	 */
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// TODO Auto-generated method stub
		super.onDialogClosed(positiveResult);
	}
}

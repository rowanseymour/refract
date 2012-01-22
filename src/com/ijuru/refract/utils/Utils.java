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

package com.ijuru.refract.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

/**
 * General utility methods
 */
public class Utils {
	/**
	 * Gets the version name from the manifest
	 * @param context the context
	 * @return the version name
	 */
	public static String getVersionName(Context context) {
		try {
			String packageName = context.getPackageName();
			return context.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Gets a shared preference as a string
	 * @param context the context
	 * @param key the preference key
	 * @param defResId the resource id of the default value
	 * @return the preference value
	 */
	public static String getStringPreference(Context context, String key, int defResId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String defValue = context.getResources().getString(defResId);
		return preferences.getString(key, defValue);
	}
	
	/**
	 * Gets a shared preference as an integer
	 * @param context the context
	 * @param key the preference key
	 * @param defResId the resource id of the default value
	 * @return the preference value
	 */
	public static int getIntegerPreference(Context context, String key, int defResId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String defValue = "" + context.getResources().getInteger(defResId);
		return Utils.parseInteger(preferences.getString(key, defValue));
	}
	
	/**
	 * Parses a string into an integer
	 * @param val the string
	 * @return the integer or null if not a valid integer
	 */
	public static Integer parseInteger(String val) {
		try {
			return Integer.parseInt(val);
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}
}

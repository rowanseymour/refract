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

import com.ijuru.refract.R;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Activity to set fractal rendering as device wallpaper
 */
public class SetAsWallpaperActivity extends Activity {
	
	/**
	 * @see com.ijuru.refract.activity.ExplorerActivity#onCreate(Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.set_as_wallpaper);
	}
	
	public void onOK(View view) {
		WallpaperManager manager = WallpaperManager.getInstance(this);
		
		int width = manager.getDesiredMinimumWidth();
		int height = manager.getDesiredMinimumHeight();
		
		Log.i("refract", "Desired wallpaper size: " + width + "x" + height);
	}
	
	public void onCancel(View view) {
		finish();
	}
}

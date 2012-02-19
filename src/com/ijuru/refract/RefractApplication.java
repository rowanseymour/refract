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

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

/**
 * Application class
 */
public class RefractApplication extends Application {
	
	private BookmarkManager bookmarkManager = new BookmarkManager(this);
	
	/**
	 * Load the native library
	 */
	static {
        System.loadLibrary("refract");
    }

	/**
	 * Gets the bookmark manager
	 * @return the manager
	 */
	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}
	
	/**
	 * Gets the directory for saving renders
	 * @return the directory
	 */
	public File getSaveDirectory() {
		File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		
		File rendersDir = new File(picturesDir, Constants.RENDERS_DIRNAME);
		if (!rendersDir.exists())
			rendersDir.mkdirs();
		
		return rendersDir;
	}
	
	/**
	 * Gets the version code from the manifest
	 * @return the version code
	 */
	public int getVersionCode() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}
	
	/**
	 * Gets the version name from the manifest
	 * @return the version name
	 */
	public String getVersionName() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}
}

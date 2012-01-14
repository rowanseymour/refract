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

import com.ijuru.refract.Complex;
import com.ijuru.refract.R;
import com.ijuru.refract.ui.RendererView;
import com.ijuru.refract.ui.StatusPanel;
import com.ijuru.refract.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Activity for exploring fractals
 */
public class ExplorerActivity extends Activity implements RendererView.RendererListener{
	
	private RendererView rendererView;
	private StatusPanel statusPanel;
	
	/**
	 * Load the native code
	 */
	static {
        System.loadLibrary("refract");
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.explorer);
        
        rendererView = (RendererView)findViewById(R.id.rendererView);
        statusPanel = (StatusPanel)findViewById(R.id.statusPanel);
        
        rendererView.setRendererListener(this);
    }

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.explorer, menu);
	    return true;
	}
	
	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menureset:
			rendererView.reset();
	    	break;
		case R.id.menusettings:
			startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
	    	break;
		case R.id.menusetas:
			// TODO
	    	break;
	    case R.id.menuabout:
	    	onMenuAbout();
	    	break;
		}
		return true;
	}
	
	/**
	 * Displays the about dialog
	 */
	private void onMenuAbout() {
		String title = getString(R.string.app_name) + " " + Utils.getVersionName(this);
		String message = 
				"Thank you for downloading " + getString(R.string.app_name) + "\n" +
				"\n" +
				"Enjoy exploring the world of Mandelbrot!";
		
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
			.setPositiveButton(android.R.string.ok, null).show();
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onOffsetChanged(RendererView, Complex)
	 */
	@Override
	public void onOffsetChanged(RendererView view, Complex offset) {
		statusPanel.setCoords(offset.re, offset.im);
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onZoomChanged(RendererView, double)
	 */
	@Override
	public void onZoomChanged(RendererView view, double zoom) {
		statusPanel.setZoom(zoom);
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onUpdate(RendererView, int)
	 */
	@Override
	public void onUpdate(RendererView view, int iters) {
		long avgFrameTime = view.getRendererThread().calcSmoothedFrameTime();
		
		statusPanel.setPerformanceInfo(iters, avgFrameTime > 0 ? 1000.0 / avgFrameTime : 0);
	}
}
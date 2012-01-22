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
import com.ijuru.refract.renderer.Renderer;
import com.ijuru.refract.ui.RenderView;
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
public class ExplorerActivity extends Activity implements RenderView.RendererListener {
	
	private RenderView rendererView;
	private StatusPanel statusPanel;
	private Bundle savedInstanceState;
	
	/**
	 * Load the native code
	 */
	static {
        System.loadLibrary("refract");
    }
	
	/**
	 * @see com.ijuru.refract.activity.ExplorerActivity#onCreate(Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.explorer);
		
		rendererView = (RenderView)findViewById(R.id.rendererView);
		statusPanel = (StatusPanel)findViewById(R.id.statusPanel);
		
		rendererView.setRendererListener(this);
		
		// Keep bundle for later when we have a renderer
		this.savedInstanceState = savedInstanceState;
    }
	
	/**
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		// Save rendering parameters to bundle
		saveParametersToBundle(bundle);
		
		super.onSaveInstanceState(bundle);
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
		case R.id.menuwallpaper:
			onMenuWallpaper();
	    	break;
	    case R.id.menuabout:
	    	onMenuAbout();
	    	break;
		}
		return true;
	}
	
	/**
	 * Displays the 'set as' dialog
	 */
	private void onMenuWallpaper() {
		Intent intent = new Intent(getApplicationContext(), WallpaperActivity.class);
		Bundle bundle = new Bundle();
		saveParametersToBundle(bundle);
		intent.putExtras(bundle);
		startActivity(intent);
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
	 * @see com.ijuru.refract.ui.RenderView.RendererListener#onRendererCreated(RenderView, Renderer)
	 */
	@Override
	public void onRendererCreated(RenderView view, Renderer renderer) {
		if (savedInstanceState != null) {
			// Load rendering parameters from bundle
			double offset_re = savedInstanceState.getDouble("offset_re");
			double offset_im = savedInstanceState.getDouble("offset_im");
			double zoom = savedInstanceState.getDouble("zoom");
			renderer.setOffset(new Complex(offset_re, offset_im));
			renderer.setZoom(zoom);
		}
	}
	
	/**
	 * SAves the render parameters to a bundle
	 * @param bundle the bundle
	 */
	private void saveParametersToBundle(Bundle bundle) {
		Complex offset = rendererView.getOffset();
		bundle.putDouble("offset_re", offset.re);
		bundle.putDouble("offset_im", offset.im);
		bundle.putDouble("zoom", rendererView.getZoom());
	}

	/**
	 * @see com.ijuru.refract.ui.RenderView.RendererListener#onOffsetChanged(RenderView, Complex)
	 */
	@Override
	public void onRendererOffsetChanged(RenderView view, Complex offset) {
		statusPanel.setCoords(offset.re, offset.im);
	}

	/**
	 * @see com.ijuru.refract.ui.RenderView.RendererListener#onZoomChanged(RenderView, double)
	 */
	@Override
	public void onRendererZoomChanged(RenderView view, double zoom) {
		statusPanel.setZoom(zoom);
	}

	/**
	 * @see com.ijuru.refract.ui.RenderView.RendererListener#onUpdate(RenderView, int)
	 */
	@Override
	public void onRendererUpdate(RenderView view, int iters) {
		long avgFrameTime = view.getRendererThread().calcSmoothedFrameTime();
		
		statusPanel.setPerformanceInfo(iters, avgFrameTime > 0 ? 1000.0 / avgFrameTime : 0);
	}
}
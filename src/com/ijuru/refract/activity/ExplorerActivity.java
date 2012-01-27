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
import com.ijuru.refract.Function;
import com.ijuru.refract.Mapping;
import com.ijuru.refract.Palette;
import com.ijuru.refract.Parameters;
import com.ijuru.refract.R;
import com.ijuru.refract.renderer.Renderer;
import com.ijuru.refract.renderer.RendererListener;
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
public class ExplorerActivity extends Activity implements RendererListener {
	
	private RendererView rendererView;
	private StatusPanel statusPanel;
	
	/**
	 * @see com.ijuru.refract.activity.ExplorerActivity#onCreate(Bundle)
	 */
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
		bundle.putParcelable("params", new Parameters(rendererView.getOffset(), rendererView.getZoom()));
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
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onRendererCreated(RendererView, Renderer)
	 */
	@Override
	public void onRendererCreated(RendererView view, Renderer renderer) {
		// Get renderer options from preferences
		Function iterFunction = Function.parseString(Utils.getStringPreference(this, "iterfunction", R.string.def_iterfunction));
		Palette palette = Palette.getPresetByName(Utils.getStringPreference(this, "palette", R.string.def_palette));
		Mapping paletteMapping = Mapping.parseString(Utils.getStringPreference(this, "palettemapping", R.string.def_palettemapping));
		int paletteSize = Utils.getIntegerPreference(this, "palettesize", R.integer.def_palettesize);
		
		renderer.setFunction(iterFunction);
		renderer.setPalette(palette, paletteSize);
		renderer.setPaletteMapping(paletteMapping);
		
		// Get renderer view options from preferences
		int itersPerFrame = Utils.getIntegerPreference(this, "itersperframe", R.integer.def_itersperframe);
		view.setIterationsPerFrame(itersPerFrame);
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onOffsetChanged(RendererView, Renderer, Complex)
	 */
	@Override
	public void onRendererOffsetChanged(RendererView view, Renderer renderer, Complex offset) {
		statusPanel.setCoords(offset.re, offset.im);
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onZoomChanged(RendererView, Renderer, double)
	 */
	@Override
	public void onRendererZoomChanged(RendererView view, Renderer renderer, double zoom) {
		statusPanel.setZoom(zoom);
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onUpdate(RendererView, Renderer, int)
	 */
	@Override
	public void onRendererIterated(RendererView view, Renderer renderer, int iters) {
		long avgFrameTime = rendererView.getRendererThread().calcSmoothedFrameTime();
		
		statusPanel.setPerformanceInfo(iters, avgFrameTime > 0 ? 1000.0 / avgFrameTime : 0);
	}
	
	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onRendererDestroy(RendererView, Renderer)
	 */
	@Override
	public void onRendererDestroy(RendererView view, Renderer renderer) {
		// TODO save renderer parameters
	}
}
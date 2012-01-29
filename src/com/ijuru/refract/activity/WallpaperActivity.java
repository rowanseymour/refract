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

import java.io.IOException;

import com.ijuru.refract.R;
import com.ijuru.refract.renderer.Complex;
import com.ijuru.refract.renderer.Mapping;
import com.ijuru.refract.renderer.Palette;
import com.ijuru.refract.renderer.RendererParams;
import com.ijuru.refract.renderer.Renderer;
import com.ijuru.refract.renderer.RendererListener;
import com.ijuru.refract.ui.RendererView;
import com.ijuru.refract.utils.Preferences;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

/**
 * Activity to set fractal rendering as device wallpaper
 */
public class WallpaperActivity extends Activity implements RendererListener {
	
	private RendererView rendererView;
	
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_wallpaper);
		
		rendererView = (RendererView)findViewById(R.id.rendererView);
		rendererView.setRendererListener(this);
	}
	
	/**
	 * Called when user presses OK button
	 * @param view the button
	 */
	public void onOK(View view) {
		rendererView.stopRendering();
		
		Bitmap bitmap = rendererView.getBitmap();
		
		try {
			WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
			wallpaperManager.setBitmap(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finish();
	}
	
	/**
	 * Called when user presses Cancel button
	 * @param view the button
	 */
	public void onCancel(View view) {
		finish();
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onRendererCreated(RendererView, Renderer)
	 */
	@Override
	public void onRendererCreated(RendererView view, Renderer renderer) {
		// Get renderer parameters from preferences
		Palette palette = Palette.getPresetByName(Preferences.getStringPreference(this, "palettepreset", R.string.def_palette));
		int paletteSize = Preferences.getIntegerPreference(this, "palettesize", R.integer.def_palettesize);
		int setColor = Preferences.getIntegerPreference(this, "setcolor", R.integer.def_setcolor);
		
		renderer.setPalette(palette, paletteSize, setColor);
		renderer.setPaletteMapping(Mapping.SCALE_GLOBAL);
			
		// Set render parameters from intent if they exist
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra("params")) {
			RendererParams params = (RendererParams)intent.getParcelableExtra("params");
			renderer.setFunction(params.getFunction());
			renderer.setOffset(params.getOffset());
			renderer.setZoom(params.getZoom());
		}
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onRendererOffsetChanged(RendererView, Renderer, Complex)
	 */
	@Override
	public void onRendererOffsetChanged(RendererView view, Renderer renderer, Complex offset) {	
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onRendererZoomChanged(RendererView, Renderer, double)
	 */
	@Override
	public void onRendererZoomChanged(RendererView view, Renderer renderer, double zoom) {	
	}

	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onRendererIterated(RendererView, Renderer, int)
	 */
	@Override
	public void onRendererIterated(RendererView view, Renderer renderer, int iters) {
		// TODO display number of iterations somewhere
	}
	
	/**
	 * @see com.ijuru.refract.ui.RendererView.RendererListener#onRendererDestroy(RendererView, Renderer)
	 */
	@Override
	public void onRendererDestroy(RendererView view, Renderer renderer) {
	}
}

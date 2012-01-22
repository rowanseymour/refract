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

import com.ijuru.refract.Complex;
import com.ijuru.refract.R;
import com.ijuru.refract.renderer.Renderer;
import com.ijuru.refract.ui.RenderView;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

/**
 * Activity to set fractal rendering as device wallpaper
 */
public class WallpaperActivity extends Activity implements RenderView.RendererListener {
	
	private RenderView rendererView;
	
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.set_as_wallpaper);
		
		rendererView = (RenderView)findViewById(R.id.rendererView);
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
	 * @see com.ijuru.refract.ui.RenderView.RendererListener#onRendererCreated(RenderView, Renderer)
	 */
	@Override
	public void onRendererCreated(RenderView view, Renderer renderer) {
		// Set render parameters from intent if they exist
		if (getIntent().hasExtra("offset_re") && getIntent().hasExtra("offset_im")) {
			double offset_re = getIntent().getDoubleExtra("offset_re", 0.0);
			double offset_im = getIntent().getDoubleExtra("offset_im", 0.0);
			rendererView.setOffset(new Complex(offset_re, offset_im));
		}
		if (getIntent().hasExtra("zoom"))
			rendererView.setZoom(getIntent().getDoubleExtra("zoom", 200));
	}

	/**
	 * @see com.ijuru.refract.ui.RenderView.RendererListener#onRendererOffsetChanged(RenderView, Complex)
	 */
	@Override
	public void onRendererOffsetChanged(RenderView view, Complex offset) {	
	}

	/**
	 * @see com.ijuru.refract.ui.RenderView.RendererListener#onRendererZoomChanged(RenderView, double)
	 */
	@Override
	public void onRendererZoomChanged(RenderView view, double zoom) {	
	}

	/**
	 * @see com.ijuru.refract.ui.RenderView.RendererListener#onRendererUpdate(RenderView, int)
	 */
	@Override
	public void onRendererUpdate(RenderView view, int iters) {
	}	
}

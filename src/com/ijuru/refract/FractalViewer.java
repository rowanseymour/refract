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

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Root component of the fractal renderer
 */
public class FractalViewer extends FrameLayout {

	private RendererView rendererView;
	private StatusPanel statusPanel;
	
	/**
	 * Creates a fractal viewer
	 * @param context the context
	 */
	public FractalViewer(Context context) {
		super(context);
		
		float dp = context.getResources().getDisplayMetrics().density;
		
		LinearLayout statusHolder = new LinearLayout(context);
		statusHolder.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		statusHolder.setGravity(Gravity.BOTTOM);
		statusHolder.setOrientation(LinearLayout.VERTICAL);
		statusHolder.setPadding((int)(5 * dp), (int)(5 * dp), (int)(5 * dp), (int)(5 * dp));
		
		statusPanel = new StatusPanel(context);
		statusPanel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		statusPanel.setPadding((int)(5 * dp), (int)(5 * dp), (int)(5 * dp), (int)(5 * dp));
		
		rendererView = new RendererView(context, this);
		rendererView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		statusHolder.addView(statusPanel);
		
		addView(rendererView);
		addView(statusHolder);
	}
	
	/**
	 * Gets the status panel
	 * @return the status panel
	 */
	public StatusPanel getStatusPanel() {
		return statusPanel;
	}
}

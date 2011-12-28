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
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * View component to display status information about the current rendering
 */
public class StatusPanel extends TableLayout {

	private TextView txtReal, txtImag, txtZoom, txtPerf;
	
	/**
	 * Creates a status panel
	 * @param context the context
	 */
	public StatusPanel(Context context) {
		super(context);
		
		setBackgroundColor(0x55000000);
		setColumnStretchable(1, true);
		setColumnStretchable(3, true);
		
		txtReal = new TextView(context);
		txtImag = new TextView(context);
		txtZoom = new TextView(context);
		txtPerf = new TextView(context);
		
		TableRow row1 = new TableRow(context);
		TableRow row2 = new TableRow(context);
		
		row1.addView(createLabel(context, "Real"));
		row1.addView(txtReal);
		row1.addView(createLabel(context, "Zoom"));
		row1.addView(txtZoom);
		
		row2.addView(createLabel(context, "Imag"));
		row2.addView(txtImag);
		row2.addView(createLabel(context, "Perf"));
		row2.addView(txtPerf);
		
		addView(row1);
		addView(row2);
		
		setCoords(0.0, 0.0);
		setZoom(0.0);
	}
	
	/**
	 * Sets the render coordinates
	 * @param real the real component
	 * @param imag the imaginary component
	 */
	public void setCoords(double real, double imag) {
		txtReal.setText("" + real);
		txtImag.setText("" + imag);
	}
	
	/**
	 * Sets the zoom factor
	 * @param zoom the zoom factor
	 */
	public void setZoom(double zoom) {
		txtZoom.setText("" + zoom);
	}
	
	/**
	 * Creates a text label
	 * @param context the context
	 * @param text the text of the label
	 * @return the label component
	 */
	private static View createLabel(Context context, String text) {
		TextView label = new TextView(context);
		label.setTextColor(0xFFFFFFFF);
		label.setText(text);
		return label;
	}
}

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

package com.ijuru.refract.ui;

import java.util.HashMap;
import java.util.Map;

import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * General purpose gesture detector for multi-touch events
 */
public class MultiTouchGestureDetector {
	
	// Map of pointer ids to locations
	private Map<Integer, PointF> pointerLocations = new HashMap<Integer, PointF>();
	
	private OnMultiTouchGestureListener listener;

	/**
	 * Constructs a new multi-touch gesture detector
	 * @param listener the listener to notify about multi-touch gestures
	 */
	public MultiTouchGestureDetector(OnMultiTouchGestureListener listener) {
		this.listener = listener;
	}

	/**
	 * Handles a touch event
	 * @param event the event
	 * @return true if event was handled, else false
	 */
	public boolean onTouchEvent(MotionEvent event) {
		int pointerId, pointerIndex;
		
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			pointerLocations.clear();
		case MotionEvent.ACTION_POINTER_DOWN:
			pointerIndex = event.getActionIndex();
			pointerId = event.getPointerId(pointerIndex);
			pointerLocations.put(pointerId, new PointF(event.getX(pointerIndex), event.getY(pointerIndex)));
			break;
		case MotionEvent.ACTION_MOVE:
			PointF[] currPoints = new PointF[event.getPointerCount()];
			PointF[] prevPoints = new PointF[event.getPointerCount()];
			
			// Get current and previous locations of all pointers
			for (pointerIndex = 0; pointerIndex < event.getPointerCount(); ++pointerIndex) {
				pointerId = event.getPointerId(pointerIndex);
				currPoints[pointerIndex] = new PointF(event.getX(pointerIndex), event.getY(pointerIndex));
				prevPoints[pointerIndex] = pointerLocations.get(pointerId);
			}
			
			listener.onMultiTouchGesture(prevPoints, currPoints);
			
			// Store current locations as previous locations to be used next time
			for (pointerIndex = 0; pointerIndex < event.getPointerCount(); ++pointerIndex) {
				pointerId = event.getPointerId(pointerIndex);
				pointerLocations.put(pointerId, currPoints[pointerIndex]);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			pointerId = event.getPointerId(event.getActionIndex());
			pointerLocations.remove(pointerId);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			pointerLocations.clear();
			break;
		default:
			return false;	
		}
		
		return true;
	}
	
	/**
	 * Gets whether there is a navigation gesture in progress
	 * @return true if in progress, else false
	 */
	public boolean isInProgress() {
		return pointerLocations.size() > 0;
	}

	/**
	 * Interface for navigation listeners
	 */
	public interface OnMultiTouchGestureListener {

		public void onMultiTouchGesture(PointF[] prevPoints, PointF[] currPoints);
	}
	
	/**
	 * Dumps event to log for debugging
	 * @param event the motion event
	 */
	/*private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d("refract", sb.toString());
	}*/
}

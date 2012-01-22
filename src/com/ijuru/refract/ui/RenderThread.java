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


import android.util.Log;

/**
 * Thread to update fractal rendering
 */
public class RenderThread extends Thread {
	
	private RenderView view;
	
	private static final int STAT_FRAMES = 10;
	private long lastFrameTime = 0;
	private long[] frameTimes = new long[STAT_FRAMES];
	private int updateNumber = 0;
	private long beginTime = 0;
	private long lastUpdateTime = 0;

	public RenderThread(RenderView view) {
		this.view = view;
	}

	@Override
	public void run() {
		beginTime = System.currentTimeMillis();
		
		while (!isInterrupted()) {				
			view.update();
			
			// Record frame render time
			long updateTime = System.currentTimeMillis();
			lastFrameTime =  updateTime - ((updateNumber > 0) ? lastUpdateTime : beginTime);
			frameTimes[updateNumber % STAT_FRAMES] = lastFrameTime;
			lastUpdateTime = updateTime;
			++updateNumber;
		}
		
		Log.d("refract", "Average frame time: " + calcAverageFrameTime() + "ms");
	}
	
	/**
	 * Gets the last frame time
	 * @return the last frame time (ms)
	 */
	public long getLastFrameTime() {
		return lastFrameTime;
	}
	
	/**
	 * Calculates the smoothed frame time 
	 * @return the frame time (ms)
	 */
	public long calcSmoothedFrameTime() {
		int numFrames = Math.min(updateNumber, STAT_FRAMES);
		if (numFrames == 0)
			return 0;
		
		long total = 0;
		for (long frameTime : frameTimes)
			total += frameTime;
		return total / numFrames;
	}
	
	/**
	 * Calculates the overall average frame time
	 * @return the frame time (ms)
	 */
	public long calcAverageFrameTime() {
		long time = System.currentTimeMillis() - beginTime;
		return updateNumber > 0 ? time / updateNumber : 0;
	}
}

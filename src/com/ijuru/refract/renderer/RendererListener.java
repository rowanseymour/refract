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

package com.ijuru.refract.renderer;

import com.ijuru.refract.ui.RendererView;

/**
 * Listener interface
 */
public interface RendererListener {
	
	/**
	 * Called after renderer has been created, but before the renderer thread has been started
	 * @param view the view holding the renderer
	 * @param renderer the renderer
	 */
	public void onRendererCreated(RendererView view, Renderer renderer);
	
	/**
	 * Called when renderer parameters have been changed
	 * @param view the view holding the renderer
	 * @param renderer the renderer
	 */
	public void onRendererParamsChanged(RendererView view, Renderer renderer);
	
	/**
	 * Called when renderer has been iterated
	 * @param view the view holding the renderer
	 * @param renderer the renderer
	 * @param iters the number of iterations
	 */
	public void onRendererIterated(RendererView view, Renderer renderer, int iters);
	
	/**
	 * Called when renderer is about to be destroyed
	 * @param view the view holding the renderer
	 * @param renderer the renderer
	 */
	public void onRendererDestroy(RendererView view, Renderer renderer);
	
	/**
	 * Called when renderer allocation fails
	 * @param view the view holding the renderer
	 * @param renderer the renderer
	 */
	public void onRendererAllocationFailed(RendererView view, Renderer renderer);
}

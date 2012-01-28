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


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Render parameters
 */
public class RendererParams implements Parcelable {
	
	private Complex offset;
	private double zoom;
	
	/**
	 * Constructs new render parameters
	 * @param offset the offset
	 * @param zoom the zoom
	 */
	public RendererParams(Complex offset, double zoom) {
		this.offset = offset;
		this.zoom = zoom;
	}
	
	/**
	 * Constructs new render parameters from a parcel
	 * @param parcel the parcel
	 */
	public RendererParams(Parcel parcel) {
		this.offset = new Complex(parcel.readDouble(), parcel.readDouble());
		this.zoom = parcel.readDouble();
	}

	/**
	 * Gets the offset
	 * @return the offset
	 */
	public Complex getOffset() {
		return offset;
	}

	/**
	 * Gets the zoom
	 * @return the zoom
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * @see android.os.Parcelable#writeToParcel(Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel parcel, int hint) {
		parcel.writeDouble(offset.re);
		parcel.writeDouble(offset.im);
		parcel.writeDouble(zoom);
	}
	
	/**
	 * Required by Android to create instances from parcels
	 */
	public static final Parcelable.Creator<RendererParams> CREATOR = new Parcelable.Creator<RendererParams>() {
		public RendererParams createFromParcel(Parcel parcel) {
			return new RendererParams(parcel);
		}
		public RendererParams[] newArray(int size) {
			return new RendererParams[size];
		}
	};
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Complex) {
			RendererParams p = (RendererParams)obj;
			return offset.equals(p.offset) && zoom == p.zoom;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return offset.hashCode() + new Double(zoom).hashCode();
	}
}

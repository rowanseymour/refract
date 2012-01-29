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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Renderer parameters
 */
public class RendererParams implements Parcelable {
	
	private Function function;
	private Complex offset;
	private double zoom;
	
	/**
	 * Constructs new render parameters
	 * @param function the set function
	 * @param offset the offset
	 * @param zoom the zoom
	 */
	public RendererParams(Function function, Complex offset, double zoom) {
		this.function = function;
		this.offset = offset;
		this.zoom = zoom;
	}
	
	/**
	 * Constructs new render parameters from a parcel
	 * @param parcel the parcel
	 */
	public RendererParams(Parcel parcel) {
		this.function = Function.parseString(parcel.readString());
		this.offset = new Complex(parcel.readDouble(), parcel.readDouble());
		this.zoom = parcel.readDouble();
	}

	/**
	 * Gets the set function
	 * @return the function
	 */
	public Function getFunction() {
		return function;
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
		parcel.writeString(function.toString());
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
	 * Reads renderer params from the given reader
	 * @param reader the reader
	 * @return the params
	 * @throws IOException
	 */
	public static RendererParams read(Reader reader) throws IOException {
		BufferedReader in = new BufferedReader(reader);
		Function function = Function.parseString(in.readLine());
		Complex offset = Complex.parseComplex(in.readLine());
		double zoom = Double.parseDouble(in.readLine());
		return new RendererParams(function, offset, zoom);
	}
	
	/**
	 * Writes the parameters to the given writer
	 * @param writer the writer
	 */
	public void write(Writer writer) {
		PrintWriter printer = new PrintWriter(writer);
		printer.println(function);
		printer.println(offset);
		printer.println(zoom);
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Complex) {
			RendererParams p = (RendererParams)obj;
			return function.equals(p.function) && offset.equals(p.offset) && zoom == p.zoom;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return function.hashCode() + offset.hashCode() + new Double(zoom).hashCode();
	}
}

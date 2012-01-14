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

/**
 * Double precision complex number
 */
public class Complex {
	public double re;
	public double im;
	
	public static final Complex ORIGIN = new Complex(0.0, 0.0);
	
	/**
	 * Constructs a new complex number
	 * @param re the real component
	 * @param im the imaginary component
	 */
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}
	
	/**
	 * Calculates the absolute value (i.e. magnitude)
	 * @return the absolute value
	 */
	public double abs() {
		return Math.sqrt(re * re + im * im);
	}
	
	/**
	 * Calculates the complex conjugate
	 * @return the conjugate
	 */
	public Complex conj() {
		return new Complex(re, -im);
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Complex) {
			Complex c = (Complex)obj;
			return c.re == this.re && c.im == this.im;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new Double(re).hashCode() + new Double(im).hashCode();
	}
}

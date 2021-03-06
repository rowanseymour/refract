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

/**
 * Double precision complex number
 */
public final class Complex {
	public final double re;
	public final double im;
	
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
	 * Adds a complex number to this one
	 * @param c the complex number
	 * @return the result
	 */
	public Complex add(Complex c) {
		return new Complex(this.re + c.re, this.im + c.im);
	}
	
	/**
	 * Subtracts a complex number from this one
	 * @param c the complex number
	 * @return the result
	 */
	public Complex sub(Complex c) {
		return new Complex(this.re - c.re, this.im - c.im);
	}
	
	/**
	 * Calculates the absolute value (i.e. magnitude)
	 * @return the absolute value
	 */
	public double abs() {
		return Math.sqrt(re * re + im * im);
	}
	
	/**
	 * Scales the complex number
	 * @param s the scalar
	 * @return the result
	 */
	public Complex scale(double s) {
		return new Complex(this.re * s, this.im * s);
	}
	
	/**
	 * Calculates the complex conjugate
	 * @return the conjugate
	 */
	public Complex conj() {
		return new Complex(re, -im);
	}
	
	/**
	 * Parses a complex value from a string
	 * @param str the string
	 * @return the value
	 * @throws NumberFormatException
	 */
	public static Complex parseComplex(String str) throws NumberFormatException {
		String[] components = str.split(",");
		if (components.length != 2)
			throw new NumberFormatException();
			
		double re = Double.parseDouble(components[0]);
		double im = Double.parseDouble(components[1]);
		return new Complex(re, im);
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

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return re + "," + im;
	}
}

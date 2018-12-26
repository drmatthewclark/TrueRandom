package prng;

/**
 * polynomial algorithm to generate random number sequences
 * 
 *  x(n) = M/2 * x(n-1)^3 + M * (x(n-1) + 1)^2 + 2*M*x(n-1) + M*cos(x);
 *  M ^= x(n)
 * 
 *   copyright 2019 Matthew Clark
 
     This file is part of TrueRandom.

    TrueRandom is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TrueRandom is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TrueRandom.  If not, see <https://www.gnu.org/licenses/>.

 * @author CLARKM
 *
 */
public class Polynomial extends ExtendedRandom {

	private static final long serialVersionUID = -3752716805248928524L;
	
	// product of two prime numbers.
	private long M =  29996224275833L * 22801285763L;
	
	/**
	 * provide the next random bits
	 * 
	 * @param bits - requested bits, ranges from 1 to 32
	 */
	protected synchronized final int next(int bits) {

		seed = (M/2)*(seed * seed * seed) + M*(seed * seed + 1)  + M*seed*(long)(M*Math.cos(seed));
		
		return (int) (seed >>> (64 - bits));
	}
}

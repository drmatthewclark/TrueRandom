package prng;

/**
 * Simple multiply with carry algorithm.
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

 * 
 * @author CLARKM
 *
 */
public class MultiplyWithCarry extends ExtendedRandom {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8752716801848928524L;
	private static final long multiplier = 0xffffda61L;
	

	/**
	 * provide the next random bits
	 * 
	 * @param bits - requested bits, ranges from 1 to 32
	 */
	protected synchronized final int next(int bits) {

		seed = (multiplier * (seed & 0xffffffffL)) + (seed >>> 32);
		return (int)(seed >>> (64 - bits));
	}

}

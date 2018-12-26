package prng;


/**
 * this is the same linear congruential algorithm used for the normal java.util.Random, however the
 * 48 bit mask has been removed.  The 32 bit result is from the high-end of the 64 bit
 * word, possibly reducing problems with non-randomness of the low bits.
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
public class Random64 extends ExtendedRandom {

	private static final long serialVersionUID = -3752716801248928524L;
	private static final long multiplier = 0x5DEECE66DL;
	private static final long addend = 0xBL;

	/**
	 * provide the next random bits
	 * 
	 * @param bits - requested bits, ranges from 1 to 32
	 */
	protected synchronized final int next(int bits) {

		seed = seed * multiplier + addend;
		return (int) (seed >>> (64 - bits));
	}

}

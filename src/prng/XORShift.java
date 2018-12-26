package prng;

/**
 * implementation of Marsiglia XOR shift random number generator.  
 * Although it may have the weakness that zero is never a native
 * value, since the result is shifted 32 bits, zero is in the result domain thus correcting this fault.
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
public class XORShift extends ExtendedRandom {

	private static final long serialVersionUID = -3429778336651270452L;
	private long[] state;
	private int p;
	private static final int STATE_SIZE = 32;
	
	/**
	 * set the seed.  If the seed has already been set add randomness to the state, without
	 * resetting
	 * 
	 * @param newSeed long seed
	 */
	public synchronized void setSeed(final long newSeed) {
		
		seed = newSeed;
		
		if (state == null) {
			state = new long[STATE_SIZE];
		} 
		/*
		 * initialize with "standard" LC random numbers
		 */
		for (int i = 0; i < state.length; i++) {
			seed = seed * 0x5DEECE66DL + 0xBL;
			state[i] ^= seed;
		}
	}
		
	/**
	 * xorshift* from wikipedia!
	 * 
	 * @return next random in sequence
	 */
	private final long xorshift1024star() {
		long s0 = state[p];
		long s1 = state[p = ( p + 1 ) & (state.length - 1 )];
		s1 ^= s1 << 31; // a
		s1 ^= s1 >> 11; // b
		s0 ^= s0 >> 30; // c
		return ( state[p] = s0 ^ s1 ) * 1181783497276652981L;
	}
	
	/**
	 * generate the next bits
	 * 
	 * @param bits - number of bits to return
	 */
	protected synchronized final int next(final int bits) {
		  return (int) (xorshift1024star() >>> (64 - bits)) ;
	}

	@Override
	void setSeed(byte[] seed) {
		setSeed(bytesToLong(seed));
	}


	@Override
	int entropySize() {
		return STATE_SIZE;
	}
}

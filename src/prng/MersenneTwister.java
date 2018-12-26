package prng;

import java.util.Arrays;

/**
 * <h3>MersenneTwister and MersenneTwisterFast</h3>
 * <p>
 * <b>Version 20</b>, based on version MT199937(99/10/29) of the Mersenne
 * Twister algorithm found at <a
 * href="http://www.math.keio.ac.jp/matumoto/emt.html"> The Mersenne Twister
 * Home Page</a>, with the initialization improved using the new 2002/1/26
 * initialization algorithm By Sean Luke, October 2004.
 * 
 *
 * <h3>About the Mersenne Twister</h3>
 * <p>
 * This is a Java version of the C-program for MT19937: Integer version. The
 * MT19937 algorithm was created by Makoto Matsumoto and Takuji Nishimura, who
 * ask: "When you use this, send an email to: matumoto@math.keio.ac.jp with an
 * appropriate reference to your work". Indicate that this is a translation of
 * their algorithm into Java.
 *
 * <p>
 * <b>Reference. </b> Makato Matsumoto and Takuji Nishimura, "Mersenne Twister:
 * A 623-Dimensionally Equidistributed Uniform Pseudo-Random Number Generator",
 * <i>ACM Transactions on Modeling and. Computer Simulation,</i> Vol. 8, No. 1,
 * January 1998, pp 3--30.
 *
 * <h3>About this Version</h3>
 *
 * <p>
 * <b>Changes since V19:</b> nextFloat(boolean, boolean) now returns float, not
 * double.
 *
 * <p>
 * <b>Changes since V18:</b> Removed old final declarations, which used to
 * potentially speed up the code, but no longer.
 *
 * <p>
 * <b>Changes since V17:</b> Removed vestigial references to &= 0xffffffff which
 * stemmed from the original C code. The C code could not guarantee that ints
 * were 32 bit, hence the masks. The vestigial references in the Java code were
 * likely optimized out anyway.
 *
 * <p>
 * <b>Changes since V16:</b> Added nextDouble(includeZero, includeOne) and
 * nextFloat(includeZero, includeOne) to allow for half-open, fully-closed, and
 * fully-open intervals.
 *
 * <p>
 * <b>Changes Since V15:</b> Added serialVersionUID to quiet compiler warnings
 * from Sun's overly verbose compilers as of JDK 1.5.
 *
 * <p>
 * <b>Changes Since V14:</b> made strictfp, with StrictMath.log and
 * StrictMath.sqrt in nextGaussian instead of Math.log and Math.sqrt. This is
 * largely just to be safe, as it presently makes no difference in the speed,
 * correctness, or results of the algorithm.
 *
 * <p>
 * <b>Changes Since V13:</b> clone() method CloneNotSupportedException removed.
 *
 * <p>
 * <b>Changes Since V12:</b> clone() method added.
 *
 * <p>
 * <b>Changes Since V11:</b> stateEquals(...) method added. MersenneTwisterFast
 * is equal to other MersenneTwisterFasts with identical state; likewise
 * MersenneTwister is equal to other MersenneTwister with identical state. This
 * isn't equals(...) because that requires a contract of immutability to compare
 * by value.
 *
 * <p>
 * <b>Changes Since V10:</b> A documentation error suggested that setSeed(int[])
 * required an int[] array 624 long. In fact, the array can be any non-zero
 * length. The new version also checks for this fact.
 *
 * <p>
 * <b>Changes Since V9:</b> readState(stream) and writeState(stream) provided.
 *
 * <p>
 * <b>Changes Since V8:</b> setSeed(int) was only using the first 28 bits of the
 * seed; it should have been 32 bits. For small-number seeds the behavior is
 * identical.
 *
 * <p>
 * <b>Changes Since V7:</b> A documentation error in MersenneTwisterFast (but
 * not MersenneTwister) stated that nextDouble selects uniformly from the
 * full-open interval [0,1]. It does not. nextDouble's contract is identical
 * across MersenneTwisterFast, MersenneTwister, and java.util.Random, namely,
 * selection in the half-open interval [0,1). That is, 1.0 should not be
 * returned. A similar contract exists in nextFloat.
 *
 * <p>
 * <b>Changes Since V6:</b> License has changed from LGPL to BSD. New timing
 * information to compare against java.util.Random. Recent versions of HotSpot
 * have helped Random increase in speed to the point where it is faster than
 * MersenneTwister but slower than MersenneTwisterFast (which should be the
 * case, as it's a less complex algorithm but is synchronized).
 * 
 * <p>
 * <b>Changes Since V5:</b> New empty constructor made to work the same as
 * java.util.Random -- namely, it seeds based on the current time in
 * milliseconds.
 *
 * <p>
 * <b>Changes Since V4:</b> New initialization algorithms. See (see <a
 * href="http://www.math.keio.ac.jp/matumoto/MT2002/emt19937ar.html"</a>
 * http://www.math.keio.ac.jp/matumoto/MT2002/emt19937ar.html</a>)
 *
 * <p>
 * The MersenneTwister code is based on standard MT19937 C/C++ code by Takuji
 * Nishimura, with suggestions from Topher Cooper and Marc Rieffel, July 1997.
 * The code was originally translated into Java by Michael Lecuyer, January
 * 1999, and the original code is Copyright (c) 1999 by Michael Lecuyer.
 *
 * <h3>Java notes</h3>
 * 
 *
 * <p>
 * Just like java.util.Random, this generator accepts a long seed but doesn't
 * use all of it. java.util.Random uses 48 bits. The Mersenne Twister instead
 * uses 32 bits (int size). So it's best if your seed does not exceed the int
 * range.
 *
 * <h3>License</h3>
 *
 * Copyright (c) 2003 by Sean Luke. <br>
 * Portions copyright (c) 1993 by Michael Lecuyer. <br>
 * All rights reserved. <br>
 *
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <ul>
 * <li>Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <li>Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <li>Neither the name of the copyright owners, their employers, nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * </ul>
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @version 20
 */

public class MersenneTwister extends ExtendedRandom {
	// Serialization
	private static final long serialVersionUID = -4035832775130174188L; // locked
																		// as of
																		// Version
																		// 15

	// Period parameters
	private static final int N = 624;
	private static final int M = 397;
	private static final int MATRIX_A = 0x9908b0df; // private static final *
													// constant vector a
	private static final int UPPER_MASK = 0x80000000; // most significant w-r
														// bits
	private static final int LOWER_MASK = 0x7fffffff; // least significant r
														// bits

	// Tempering parameters
	private static final int TEMPERING_MASK_B = 0x9d2c5680;
	private static final int TEMPERING_MASK_C = 0xefc60000;

	private int mt[] = new int[N]; // the array for the state vector
	private int mti; // mti==N+1 means mt[N] is not initialized
	private int mag01[] = new int[] { 0, MATRIX_A };

	// a good initial seed (of int size, though stored in a long)
	// private static final long GOOD_SEED = 4357;
	
	/**
	 * return the size of the entropy bool for this method.
	 */
	int entropySize() {
		return N*4;
	}

	/**
	 * Constructor using the default seed.
	 */
	public MersenneTwister() {
		this(System.currentTimeMillis());
	}

	/**
	 * Constructor using a given seed.
	 */
	public MersenneTwister(long seed) {
		setSeed(seed);
	}

	/**
	 * Constructor using an array of integers as seed. Your array must have a
	 * non-zero length. Only the first 624 integers in the array are used; if
	 * the array is shorter than this then integers are repeatedly used in a
	 * wrap-around fashion.
	 */
	public MersenneTwister(int[] array) {
		setSeed(array);
	}

	/**
	 * Initialize the pseudo random number generator.
	 */
	public void setSeed(final long seed) {

		if (mt == null) return;
		mt[0] = Long.valueOf(seed).hashCode();

		for (mti = 1; mti < N; mti++) {
			mt[mti] = 1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti;
		}
	}
	
	/**
	 * initialize with byte array, like secureRandom
	 * @param array
	 */
	public void setSeed(final byte[] array) {
		setSeed(bytesToIntArray(array));
	}

	
	/**
	 * Sets the seed of the MersenneTwister using an array of integers. Your
	 * array must have a non-zero length. Only the first 624 integers in the
	 * array are used; if the array is shorter than this then integers are
	 * repeatedly used in a wrap-around fashion.
	 */

	public void setSeed(final int[] array) {

		if (array.length == 0)
			throw new IllegalArgumentException(
					"Array length must be greater than zero");

		int i, j, k;

		/*
		 * in the original MersenneTwister this was a fixed value. Here it is based
		 * on the seed array.
		 */
		setSeed(Arrays.hashCode(array));

		i = 1;
		j = 0;

		k = (N > array.length ? N : array.length);

		for (; k != 0; k--) {
			// here another magic number in the original code 1664525
			mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1664525))
					+ array[j] + j; /* non linear */
			i++;
			j++;

			if (i >= N) {
				mt[0] = mt[N - 1];
				i = 1;
			}

			if (j >= array.length) {
				j = 0;
			}
		}

		for (k = N - 1; k != 0; k--) {

			mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1566083941))
					- i; /* non linear */
			i++;

			if (i >= N) {
				mt[0] = mt[N - 1];
				i = 1;
			}
		}

		mt[0] = 0x80000000; /* MSB is 1; assuring non-zero initial array */
	}
	
	
	/**
	 * Returns an integer with <i>bits</i> bits filled with a random number.
	 */
	protected synchronized final int next(final int bits) {

		int y;

		if (mti >= N) { // generate N words at one time

			int kk;
			final int[] mt = this.mt; // locals are slightly faster
			final int[] mag01 = this.mag01; // locals are slightly faster

			for (kk = 0; kk < N - M; kk++) {
				y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
				mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
			}

			for (; kk < N - 1; kk++) {
				y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
				mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
			}

			y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
			mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

			mti = 0;
		}

		y = mt[mti++];
		y ^= y >>> 11; // TEMPERING_SHIFT_U(y)
		y ^= (y << 7) & TEMPERING_MASK_B; // TEMPERING_SHIFT_S(y)
		y ^= (y << 15) & TEMPERING_MASK_C; // TEMPERING_SHIFT_T(y)
		y ^= (y >>> 18); // TEMPERING_SHIFT_L(y)

		return y >>> (32 - bits); // hope that's right!
	}

}

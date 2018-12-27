package prng;

import java.security.MessageDigest;


/**
 * A digest-based random number generator that allows use of any digest method, and 
 * any seed length.  It digests input text and uses bytes from the digest as random
 * numbers.
 * 
 * The input data is digested multiple times to make it difficult to predict the
 * next bytes.
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
 * @author mclark
 *
 */
public class DigestRandom extends ExtendedRandom {
	
	private MessageDigest digest;
	/* default digest to generate random numbers */
	private final static String DEFAULT_DIGEST = "SHA-512";
	/* internal state used for generating numbers */
	private transient byte[] state;
	private static int stateMultiplier = 4;
	private transient int index;

	private static final long serialVersionUID = 5243129533368229178L;
	
	/**
	 * default constructor.  Uses the SHA512 hash.
	 */
	public DigestRandom() {
		this(DEFAULT_DIGEST);
	}
	
	
	/**
	 * return the size of the entropy bool for this method.
	 */
	int entropySize() {
		return state.length;
	}
	
	/**
	 * constructor for new DigestRandom
	 * 
	 * @param digestName digest to use for this generator. It must be supported by the
	 * java environment.
	 */
	DigestRandom(String digestName)  {
		try {
			digest = MessageDigest.getInstance(digestName);
			state = new byte[digest.getDigestLength() * stateMultiplier];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * update the state used to generate random numbers
	 */
	private synchronized void updateState() {
		
		final byte[] result = new byte[state.length];
		final int length = digest.getDigestLength();
		
		for (int i = 0; i < stateMultiplier; i++)  {
			System.arraycopy(digest.digest(state), 0, result, length*i, length);
			digest.update(state);
		}
		index = 0;
		state = result;
	}
	
	/**
	 * set the seed bytes. This augments the entropy of the system and does not reset it. That is,
	 * the ending state is dependent on the initial state.
	 * 
	 * @param byte[] seed bytes to use as a seed
	 */
	public void setSeed(final byte[] seed) {
		if (digest == null) return; // when called before initialization
		digest.update(seed);
		updateState();
	}
	
	
	/**
	 * provides the next bytes from the random stream. It returns an int with
	 * a byte value.
	 * 
	 * @param bytes byte array to be filled with bytes
	 * @return integer with only first 8 bits set.
	 */
	private final int nextByte() {
		
		/*
		 * if index has run off the edge get more bytes
		 * and reset it.
		 */
		if (index >= state.length) {
			updateState();
		}
	
		return state[index++] & 0xFF;

	}
	
	/**
	 * required method to return a random number of bits
	 * 
	 * @param bits number of bits
	 * @return random integer composed of desired number of bits
	 */
	protected synchronized final int next(int bits) {
		
		final int val =	nextByte() << 24 | 
						nextByte() << 16 |
						nextByte() << 8  |
						nextByte();
		
		return val >>> (32 - bits);
	}

}

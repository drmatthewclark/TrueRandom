package prng;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
/**
 * abstract class with base functions for extended Random
 * 
 *    copyright 2019 Matthew Clark
 
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

 * @author mclark
 *
 */
public abstract class ExtendedRandom extends Random {


	private static final long serialVersionUID = 4705969117250773525L;
	long calls = 0;
	
	// used by those subclasses that use a long seed.  Ignored if not needed.
	protected long seed = -1;
	
	/**
	 * default setseed increments the seed.
	 * 
	 * @param nseed additional seed information
	 */
	public synchronized void setSeed(long nseed) {
		seed += nseed;
	}
	
	void setSeed(byte[] seed) {
		setSeed(bytesToLong(seed));
	};
	
	/**
	 * return size of entropy pool in bytes
	 * 
	 * @return bytes of entropy
	 */
	 int entropySize() {
		 return 8;
	 };
	
	protected abstract int next(int bits);
	
	public ExtendedRandom(long seed) {
		setSeed(seed);
	}
	
	public ExtendedRandom() {
		this(System.currentTimeMillis() ^ serialVersionUID);
	}
	
	
	int[] getSourceCounts() {
		return new int[1];
	}
	/**
	 * count calls to this rng.
	 * @return number of times this generator was called.
	 */
	public long getCalls() {
		return calls;
	}
	
	/**
	 * increment calls
	 */
	void incCalls() {
		calls++;
	}
	
	/**
	 * convert a long to a byte array.
	 * 
	 * @param value long value
	 * @return array of 8 bytes.
	 */
	final byte[] longToByteArray(final long value) {
		
		final byte[] bytes = new byte[8];
		
		bytes[0] = (byte) ((value      ) & 0xFF);
		bytes[1] = (byte) ((value >>  8) & 0xFF);
		bytes[2] = (byte) ((value >> 16) & 0xFF);
		bytes[3] = (byte) ((value >> 24) & 0xFF);
		bytes[4] = (byte) ((value >> 32) & 0xFF);
		bytes[5] = (byte) ((value >> 40) & 0xFF);
		bytes[6] = (byte) ((value >> 48) & 0xFF);
		bytes[7] = (byte) ((value >> 56) & 0xFF);
		
		return bytes;
	}
	

	
	/**
	 * return an array of long values constructed from an array of bytes.
	 * 
	 * @param byte array to convert to array of long values
	 * @return array of long values
	 */
	final static long[] bytesToLongArray(final byte[] bytes) {
		
		final int bytesPerValue = 8;
		// make sure there is enough room if the string length is not a multiple of 8
		final int resultLength = (bytes.length + bytesPerValue)/bytesPerValue;
		
		final long[] result = new long[resultLength];

		int byteCount = 0;

		for (int i = 0; i < resultLength; i++) {
			
			long value = 0;
			
			// pack each 8 bytes from the string into a long
			for (int j = 0; j < bytesPerValue && byteCount < bytes.length; j++) {
				value |= ((long)(bytes[byteCount++]) << (bytesPerValue * j));
			}
			
			result[i] = value;
		}
		
		return result;
	}
	
	/**
	 * return a long array from bytes in a UTF-8 string
	 * @param data  String to convert
	 * @return array of longs
	 */
	final static long[] stringToLongArray(String data) {
		
		return bytesToLongArray(data.getBytes(StandardCharsets.UTF_8));
	}
	
	
	/**
	 * return an array of int values constructed from an array of bytes.
	 * 
	 * @param byte array to convert to array of long values
	 * @return array of long values
	 */
	final static int[] bytesToIntArray(final byte[] bytes) {
				
		final int bytesPerValue = 4;
		// make sure there is enough room if the string length is not a multiple of 8
		final int resultLength = (bytes.length + bytesPerValue)/bytesPerValue;
		
		final int[] result = new int[resultLength];

		int byteCount = 0;

		for (int i = 0; i < resultLength; i++) {
			
			int value = 0;
			
			// pack each 8 bytes from the string into a long
			for (int j = 0; j < bytesPerValue && byteCount < bytes.length; j++) {
				value |= ((int)(bytes[byteCount++]) << (bytesPerValue * j));
			}
			
			result[i] = value;
		}
		return result;
	}
	

	/**
	 * return an integer made from the input bytes
	 * @param bytes array of bytes, at least 4 long
	 * @return integer
	 * 
	 */
	public static final int bytesToInt(final byte[] bytes) {
		
		assert (bytes.length > 3);
		
		return	(bytes[0] & 0xFF) << 24 |
				(bytes[1] & 0xFF) << 16 |
				(bytes[2] & 0xFF) << 8  | 
				(bytes[3] & 0xFF);
	}
	
	
	/**
	 * create a long from a byte array for random methods that use only
	 * a long value seed.  This does not actually return the Long value
	 * that corresponds to the bytes as it also treats the case where the
	 * array is longer than 8 bytes.
	 * 
	 * @param seed byte array
	 * @return a long value computed from byte array.
	 * 
	 */
	final static long bytesToLong(byte[] seed) {
		// use the deep hashcode of the array, and increase the length
		// to create a long hash.
		long hash = Arrays.hashCode(seed);
		// try to overflow to unreversible value, and use odd
		// number to preserve sign.
		long result = hash * hash * hash * hash * hash;
		return result;
	}
	
}

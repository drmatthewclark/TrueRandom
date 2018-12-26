package prng;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * CBRNG - counter-based random number generated using AES encryption. Adds a counter to each run
 * and encrypts so that the byte sequence is the output of AES encryption.  From the idea 
 * of D.E. Shaw group.
 * 
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
public class CBRNG extends ExtendedRandom {


	private static final long serialVersionUID = -2718039215048818753L;
	/*
	 * specification of cipher used for encryption
	 */
	private static final String CIPHERSPEC = "AES/CBC/PKCS5Padding";
	/*
	 * depending on the Java deliverable this could be larger, but for most 
	 * default installations 16 bytes is all the keysize you get. This is the key length for
	 * each round of encryption.  
	 */
	private static final int KEYLEN = 16;
	private Cipher cipher;
	
	/* use the standard Random class to increment counter */
	private long counter = Long.MIN_VALUE;
	
	private byte[] seed = new byte[KEYLEN];
	private static final byte[] initializationVector = new byte[KEYLEN];
	

	/**
	 * constructor for CBRNG
	 * 
	 */
	CBRNG() {
		
		/* use random to make some initialization vectors */
		Random random = new Random();
		try {
			// random initialization vector, using the base random method
			byte[] iv = new byte[KEYLEN];
			random.nextBytes(iv);
			// random seed, using the base Java random source.
			random.nextBytes(seed);
			cipher = getCipher(Cipher.ENCRYPT_MODE, seed, iv);
			
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * constructor with a seed and initialization vector
	 * 
	 * @param seed
	 * @param initializationVector
	 */
	CBRNG(byte[] seed, byte[] initializationVector) {
		
		try {
			cipher = getCipher(Cipher.ENCRYPT_MODE, seed, initializationVector);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(442);
		}
	}
	
	/**
	 * get the cipher for encryption.  This method sets the various parameters.
	 * The key and initialization vector are given as arguments, 
	 * 
	 * @param opmode
	 * @param keyValue
	 * @return Cipher object ready for encryption
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws InvalidKeyException 
	 * 
	 */
	private final Cipher getCipher(final int opmode, final byte[] keyValue, final byte[] initializationVector) 
	throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException  {

		final IvParameterSpec ivp = new IvParameterSpec(initializationVector);
		final Cipher result = Cipher.getInstance(CIPHERSPEC);
		final SecretKey key = new SecretKeySpec(keyValue, 0, KEYLEN, CIPHERSPEC.substring(0, CIPHERSPEC.indexOf("/")));
		result.init(opmode, key, ivp);

		return result;
	}

	@Override
	void setSeed(byte[] newSeed) {
		
		// because of poor implementations Sun java calls this from the root Random class and it
		// gets exectued before this class is initialized
		if (seed == null) seed = new byte[KEYLEN];;
		
		System.arraycopy(newSeed, 0, seed, 0, Math.min(seed.length, newSeed.length));
		
		try {
			// reinitialize cipher
			cipher = getCipher(Cipher.ENCRYPT_MODE, seed, initializationVector);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	int entropySize() {
		return 16*8;
	}

	@Override
	protected synchronized int next(int bits) {
		
		byte[] result = new byte[8]; 
		
		try {
			// use the seed as part of the encryption
			cipher.update(seed);
			// now add 8 bytes to the seed to create a unique result
			cipher.update(longToByteArray(counter++));
			// encrypt the total byte set.
			result = cipher.doFinal();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(10);
		} 
		
		return (int) (bytesToLong(result) >> (64 - bits));
	}

}

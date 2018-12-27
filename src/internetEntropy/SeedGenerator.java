package internetEntropy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

import prng.DigestRandom;
import prng.MultiRandom;
/**
 * class to generate random seeds using hardware random sources, and internet
 * sources.
 * 
 *      copyright 2019 Matthew Clark
 
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

public class SeedGenerator  {

	private static DigestRandom random = null;
	private static SecureRandom srand = null;	
	final static long INTERVAL = 1000 * 60 * 5; //update interval in milliseconds
	final static boolean DEBUG = false;
	final static int SEED_SIZE = 2048; // seed size for randomness updater

	/**
	 * generate a key using fairly random input. This is faster than the random device as it
	 * doesn't run out of entropy
	 * 
	 * @param size number of bytes for key
	 * @return random bytes
	 */
	public final byte[] generateSeed(final int size) {
		
		// keep using the same source to insure a different value every time
		// this is called.
		if (random == null) {
			random = new DigestRandom();
		}

		/*
 		* generate entropy from hardware as well. This is limited and
 		* will block if we try to read a lot from it.  It may read from
 		* the CPU or other hardware randomness sources.
 		*/	
		if (srand == null) {
			srand = new SecureRandom();
		}
	
		random.setSeed(getEntropy());
		
		final byte[] result = new byte[size];
		random.nextBytes(result);
		return result;
	}
	
	
	/**
	 * get some random entropy items from the environment.
	 * 
	 * @return random bytes for entropy.
	 */
	private final byte[] getEntropy() {

		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		// use the current time
		result.write((int)System.nanoTime());
		
		// get entropy from "hardware" algorithm implemented in
		// secureRandom.  This may include cpu-supported randomization
		// in some Java implementations
		
		try {
			result.write(srand.generateSeed(16));
		} catch (IOException e1) {
			System.err.println("error using secureRandom");
		}

		// get some entropy from web sources
		try {
			
			byte[] internet = new InternetEntropy().getBytes();
			result.write(internet);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * get some entropy from process state.  This is pretty weak entropy
		 * since it is likely to be similar on different computers. However it 
		 * provides a measure of extra data.
		 */
		final ThreadMXBean threadMXBean = 
				(ThreadMXBean) ManagementFactory.getThreadMXBean();

		final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		for (final Method beanMethod : runtimeMXBean.getClass().getMethods()) {
			if (beanMethod.getAnnotatedParameterTypes().length == 0) {
				try {
					beanMethod.setAccessible(true);
					final Object item = beanMethod.invoke(runtimeMXBean, (Object[])null);
					result.write(item.hashCode());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (final Method beanMethod : threadMXBean.getClass().getMethods()) {
			if (beanMethod.getAnnotatedParameterTypes().length == 0) {
				try {
					beanMethod.setAccessible(true);
					final Object item  = beanMethod.invoke(threadMXBean, (Object[])null);
					result.write(item.hashCode());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return result.toByteArray();
	}
	
	
	/**
	 * create a seed generator service that updates the random generators every hour with
	 * new random information from various sources.
	 * 
	 * @param random MultiRandom random number generator to update.
	 */
	public SeedGenerator(final MultiRandom random) {
		
		final long interval = INTERVAL; // update interval
		
		final Timer updater = new Timer("randomness updater");
		UpdateEntropy updateTask = new UpdateEntropy(this, random);
        updater.schedule(updateTask, 0, interval);
	}
	
	
	/**
	 * periodic entropy updater. Gathers entropy and reseeds generators at some 
	 * period.
	 * 
	 * @author mclark
	 *
	 */
	public class UpdateEntropy extends TimerTask  {

		   SeedGenerator seeder = null;
		   MultiRandom random = null;
		   long updates = 0;
		   
		   public UpdateEntropy(SeedGenerator seeder, MultiRandom random) {
			  this.seeder = seeder;
			  this.random = random;
		   }

		   public void run() {
		       try {
		    	   final byte[] seed = seeder.generateSeed(SEED_SIZE);
		    	   random.setSeed(seed);
		    	   updates++;
		    	   if (DEBUG) System.out.println("update " + updates + "updated seed: " + InternetEntropy.bytesToHex(seed));

		       } catch (Exception ex) {
		           System.err.println("error running thread " + ex.getMessage());
		           ex.printStackTrace();
		       }
		    }
		}
}

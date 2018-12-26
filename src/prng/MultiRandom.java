package prng;


import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * Multirandom uses a variable number of random number generators, and for each call for a random number one of the generators
 * is randomly selected to generate the number. This allows the keyspace to be unlimited bits; a separate random number generator is
 * created for each long value in the initialization array. Each generator then maintains its own separate state.  
 * 
 * The method may be cryptographically secure. Since the last generated random number is used to select the generator for the next number, 
 * in order to compute the next number one has to know the state of each generator, as well as the entire stream of past numbers to know
 * which generator will be used for the next value. This is because internal state used to select the next generator is XOR'd with the last random
 * number every time one is generated.
 * 
 * If a string is used to initialize the random number generator, a separate generator is created for each 8 bytes of 
 * the string. The string byte array is converted to a array of long values. Thus the variable amount of state held increases
 * the entropy of the random numbers generated as longer keys are used.
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

 * @author Matthew Clark
 *
 */
public class MultiRandom extends ExtendedRandom {


	private static final long serialVersionUID = -2821802106204980544L;
	
	/*
	 * mask for absolute value of an integer.
	 * value & signMask makes it positive.
	 */
	final int signMask = 0x7FFFFFFF;

	/*
	 * array of random number generators.  
	 */
	private ExtendedRandom[] sources = null;
	private int[] sourceCounts = null;
	
	/*
	 * class of random number generator to use. If one has other algorithms one can
	 * add them here to use a number of methods to generate the random numbers. 
	 * When initializing the generators they are used in rotating sequence.
	 * 
	 * This list could include SecureRandom classes, although they are much slower.
	 */
	private Class<? extends ExtendedRandom>[] randomClass = null;
	
	/*
	 * default classes to use to create random numbers.  It includes the very popular MersenneTwister, and extension of the Java algorithm to
	 * use 64 instead of 48 bits, and the very quick XORShift algorithm.  The use of several algorithms may increase the quality
	 * of the result, although it also works fine if all of the random number generators are the same class since they should have
	 * different initialization values.
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static final Class<? extends ExtendedRandom>[] defaultRandomGeneratorClasses 
		= new Class[] {
		
				BlumBlumShub.class,
				Random64.class,
				MersenneTwister.class,
				XORShift.class,
				DigestRandom.class,
				MultiplyWithCarry.class,
				Random64.class,
				XORShift.class,
				MersenneTwister.class,
				BlumBlumShub.class,
				Polynomial.class,
				Xoroshiro128.class,
				CBRNG.class
		};


	/*
	 * internal state. used to select the next random number generator to use
	 */
	private int state = 0;
	
	long entropyUpdates = 0;
	
	/**
	 * return the size of the entropy bool for this method.
	 */
	public int entropySize() {
		int length = 0;
		for (ExtendedRandom r : sources) {
			length += r.entropySize();
		}
		return length;
	}
	
	/**
	 * return count of entropy updates
	 * @return number of times entropy was added
	 */
	public long getEntropyUpdates() {
		return entropyUpdates;
	}

	/**
	 * get counts of each time one of the prng's are used
	 */
	public int[] getSourceCounts() {
		return sourceCounts;
	}
	
	
	public Class<? extends ExtendedRandom>[] getClasses() {
		return defaultRandomGeneratorClasses;
	}
	/**
	 * default constructor. Uses DEFAULT_SOURCES random number generators.
	 */
	public MultiRandom() {
		
		/*
		 * initialize random generators with current time and other semi-random initialization values.
		 * Leverage secureRandom's reading of the system source of entropy as part of the initialization.
		 */
		
		this(new SecureRandom().generateSeed(32), 
				defaultRandomGeneratorClasses);
	}
	
	
	/**
	 * generate sequence based on the string given as argument. It uses unlimited bits of the string by generating
	 * a different random number generator seeded by a long created by every 8 bytes of the string.
	 * 
	 * @param key String used as key.
	 */
	public MultiRandom(String key) {
		this(key.getBytes(), defaultRandomGeneratorClasses);
	}
	
	/**
	 * generate sequence based on the string given as argument. It uses unlimited bits of the string by generating
	 * a different random number generator seeded by a long created by every 8 bytes of the string.
	 * 
	 * @param key byte array used as key.
	 */
	public MultiRandom(final byte[] key) {
		this(key, defaultRandomGeneratorClasses);
	}
	
	/**
	 * generate sequence based on the string given as argument. It uses unlimited bits of the string by generating
	 * a different random number generator seeded by a long created by every 8 bytes of the string.
	 * 
	 * @param key byte array used as key.
	 * @param array of PRNG's
	 */
	public MultiRandom(final byte[] key, Class<? extends ExtendedRandom>[] randomGenerators) {
		
		randomClass = randomGenerators;
		sources = new ExtendedRandom[randomClass.length];
		sourceCounts = new int[randomClass.length];
	
		for (int i = 0; i < randomClass.length; i++) {
			try {
				// select which kind of random number generator to use
				sources[i] = randomClass[i].newInstance();
				
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
		init(key);

	}
	

	
	/**
	 * initialize the random number generators using the seeds provided. A separate random number
	 * generator will be created for each long seed.  If multiple random number algorithms are used
	 * even identical keys will produce good results since each value will come from a different
	 * algorithm.
	 * 
	 * @param seeds long[] array of seeds.
	 */
	private void init(final byte[] seeds) {

		
		// set a fairly unique initial state based on the seed.  
		//This is very significant to make sequences with 
		//similar seed arrays result in very different sequences of numbers
		state= Arrays.hashCode(seeds);
		
		for (int i = 0; i < randomClass.length; i++) {
			// initial seed for the generator
			sources[i].setSeed(seeds);
			// change the seeds so that each prng has a different seed.  This is important
			// because the same prng class can be used more than one time.
			// the hash is different after the change so that the xor doesn't 
			// revert back to the original value.
			final int hash = Arrays.hashCode(seeds);
			for (int j = 0; j < seeds.length; j++) {
				seeds[j] ^= hash;
			}
		}
		// generate some numbers to move from initial state
		preRun();

	}
	
	/**
	 * "spin up" the generators by filling in some initial values for the materials
	 * 	in case the initial seeds are not very strong.  This process makes sure
     * that all prng's are not starting from the first number.
	 * 
	 */
	private void preRun() {
		// preRun depends on the state value so is not the same for every run.
		final long initialRun = (state & 0xFFFF) + 1024 * randomClass.length;
		for (int i = 0; i < initialRun; i++) {
			this.nextInt();
		}
	}
	
	
	/**
	 * Alter the state of the system by adding entropy
	 * @param seeds byte[] to add entropy to the system.
	 */
	public void setSeed(byte[] seeds) {
		if (randomClass != null) init(seeds);
		entropyUpdates++;
	}
	
	
	/**
	 * seed seed with a single long
	 * @param seed set the seed
	 * 
	 */
	public void setSeed(final long seed) {
		
		if (sources == null) {
			super.setSeed(seed);
			return;
		}
		/*
		 * select one of the sources to generate
		 * seeds for the other sources
		 */
		state= state ^ Long.valueOf(seed).hashCode();
		
		final int index = (int) ((state & signMask)  % sources.length);
		final ExtendedRandom rand = sources[index];
		
		/*
		 * set the seeds
		 */
		for (Random source : sources) {
			source.setSeed(rand.nextLong());
		}
		super.setSeed(seed);
	}
	
	/**
	 * provides an integer from one of the random sources, chosen using
	 * the state variable which is pseudorandom itself
	 */
	public synchronized final int nextInt() {
		// select source
		final int index = (int) ((state & signMask) % sources.length);
		// get result from selected source
		final int result = sources[index].nextInt();
		
		sourceCounts[index]++;  // collect stats on sources used
		// update state by incrementing with result. the result is masked to be
		// positive so that the average isn't zero because the average result is 0.
		// this way it may increase and overflow to be negative at some point which 
		// is ok because we force it to be positive above.
		state += result & signMask;
		return result;
	}
	
	/**
	 * override of the next(bits) method
	 * 
	 * @param bits number of random bits to return
	 */
	public int next(final int bits) {
		incCalls();
		return nextInt() >>> (32 - bits);
		
	}
}

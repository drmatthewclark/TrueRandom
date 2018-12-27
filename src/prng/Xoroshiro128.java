package prng;

/**
 *
 * 
 * Written in 2016 by David Blackman and Sebastiano Vigna (vigna@acm.org)
 * To the extent possible under law, the author has dedicated all copyright
 *  and related and neighboring rights to this software to the public domain
 *  worldwide. This software is distributed without any warranty.
 * See <http://creativecommons.org/publicdomain/zero/1.0/>. 
 * 
 *  * A port of Blackman and Vigna's xoroshiro128+ generator; should be very fast and produce medium-quality output.
 * Testing shows it is within 5% the speed of LightRNG, sometimes faster and sometimes slower, and has a larger period.
 * It's called XoRo because it involves Xor as well as Rotate operations on the 128-bit pseudo-random state. Note that
 * xoroshiro128+ fails some statistical quality tests systematically, and fails others often; if this could be a concern
 * for you, {@link DiverRNG}, which is the default for {@link RNG}, will be faster and won't fail tests, and
 * though its period is shorter, it would still take years to exhaust on one core generating only random numbers.
 * <br>
 * {@link LightRNG} is also very fast, but relative to XoRoRNG it has a significantly shorter period (the amount of
 * random numbers it will go through before repeating), at {@code pow(2, 64)} as opposed to XorRNG and XoRoRNG's
 * {@code pow(2, 128) - 1}, but LightRNG also allows the current RNG state to be retrieved and altered with
 * {@code getState()} and {@code setState()}. For most cases, you should decide between DiverRNG, LightRNG, XoRoRNG,
 * and other RandomnessSource implementations based on your needs for period length and state manipulation (DiverRNG
 * is also used internally by almost all StatefulRNG objects). You might want significantly less predictable random
 * results, which {@link IsaacRNG} can provide, along with a large period. You may want a very long period of random
 * numbers, which  would suggest {@link LongPeriodRNG} as a good choice or {@link MersenneTwister} as a potential
 * alternative. You may want better performance on 32-bit machines or on GWT, where {@link Starfish32RNG} is currently
 * the best choice most of the time, and {@link Lathe32RNG} can be faster but has slightly worse quality (both of these
 * generators use a 32-bit variant on the xoroshiro algorithm but change the output scrambler). These all can generate
 * pseudo-random numbers in a handful of nanoseconds (with the key exception of 64-bit generators being used on GWT,
 * where they may take more than 100 nanoseconds per number), so unless you need a LOT of random numbers in a hurry,
 * they'll probably all be fine on performance. You may want to decide on the special features of a generator, indicated
 * by implementing {@link StatefulRandomness} if their state can be read and written to, and/or
 * {@link SkippingRandomness} if sections in the generator's sequence can be skipped in long forward or backward leaps.
 * <br>
 * <a href="http://xoroshiro.di.unimi.it/xoroshiro128plus.c">Original version here.</a>
 * <br>
 * Written in 2016 by David Blackman and Sebastiano Vigna (vigna@acm.org)
 *
 * @author Sebastiano Vigna
 * @author David Blackman
 * @author Tommy Ettinger (if there's a flaw, use SquidLib's issues and don't bother Vigna or Blackman, it's probably a mistake in SquidLib's implementation)

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
public class Xoroshiro128 extends ExtendedRandom {

	private static final long serialVersionUID = 1018744536171610262L;
    private long state0;
    private long state1;

    
    /**
     * Constructs this XoRoRNG by dispersing the bits of seed using {@link #setSeed(long)} across the two parts of state
     * this has.
     * @param seed a long that won't be used exactly, but will affect both components of state
     */
    public Xoroshiro128(final long seed) {
        super.setSeed(seed);
    }
    

    public Xoroshiro128() {
    	this(System.currentTimeMillis() ^ serialVersionUID);
    }
   
    
    /**
     * Sets the seed of this generator using one long, running that through LightRNG's 
     * algorithm twice to get the state. Filled with 'magic numbers'.
     * 
     * @param seed the number to use as the seed
     */
    public void setSeed(final long seed) {

        long state = seed + 0x9E3779B97F4A7C15L,
        z = state;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        state0 = z ^ (z >>> 31);
        state += 0x9E3779B97F4A7C15L;
        z = state;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        state1 = z ^ (z >>> 31);
    }

    
    @Override
    protected synchronized final int next(int bits) {
    	
        final long s0 = state0;
        long s1 = state1;
        final int result = (int)(s0 + s1) >>> (32 - bits);
        s1 ^= s0;
        state0 = (s0 << 55 | s0 >>> 9) ^ s1 ^ (s1 << 14); // a, b
        state1 = (s1 << 36 | s1 >>> 28); // c
        return result;
    }
    

	@Override
	int entropySize() {
		return 16;
	}
}

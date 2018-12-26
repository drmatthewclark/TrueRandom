package trueRandom;


import java.util.Date;
import internetEntropy.InternetEntropy;
import internetEntropy.SeedGenerator;
import prng.MultiRandom;
/**
 * this class attempts to create truly random numbers by gathering random entropy from the
 * internet and periodically injecting that into the seeds. That way the random number sequences
 * will be difficult to guess and reproduce.
 * 
     copyright 2019 Matthew Clark
 
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
public class TrueRandom extends MultiRandom {
	
	private static final long serialVersionUID = -7066899250421264044L;

	InternetEntropy bytes = new InternetEntropy();
	long entropyBytes = 0;
	long calls = 0;

	public TrueRandom() {
		
		new SeedGenerator(this);
		entropyBytes = this.entropySize();
	}
	
	public long getEntropyBytes() {
		return entropyBytes;
	}
	


	/**
	 * main for testing
	 * @param args ignored
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		TrueRandom random = new TrueRandom();
		System.out.println("entropy pool is " + random.entropySize() + " bytes");
		int value = random.nextInt();
		System.out.println(new Date() + " " + value);
		while(true) {
			Thread.sleep(1000*60*5);
			value = random.nextInt();
			System.out.println(new Date() + " " + value);
		}
	}

}

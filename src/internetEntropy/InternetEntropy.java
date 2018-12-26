package internetEntropy;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * class uses internet sources to get variable data for random entropy.
 * Most of them use weather maps/data, but google news is also used.
 * @author mclark
 * @version 1.0
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
 */
public class InternetEntropy {
	
	/* weather maps to use for randomness */
	final static String[]  urlList = { 
	"https://forecast.weather.gov/wwamap/png/US.png", // bytes from png image of weather
	"https://www.goes.noaa.gov/GIFS/HPIR.JPG",        // bytes from IR image of weather
	"https://www.goes.noaa.gov/GIFS/HUVS.JPG",        // bytes from UV image of weather
	"https://www.aviationweather.gov/metar/data/",  // summary list of weather reports from USA
	"https://www.aviationweather.gov/adds/metars/index?station_ids=KLAX",  // weather from LAX
	"https://www.aviationweather.gov/adds/metars/index?station_ids=KPHL",  // weather from Philadelphia
	"https://www.aviationweather.gov/adds/metars/index?station_ids=KMCO",  // weather from orlando
	"https://news.google.com"  // news feed from google
	};
	
	/* default digest to generate random numbers */
	private final static String DEFAULT_DIGEST = "SHA-512";
	
	final int BUFSIZE = 16384; // maximum bytes to read from any source
	final int CHUNK = 2048;    // buffer size for reading
	final boolean DEBUG = false;
	
	/**
	 * main for testing
	 * @param args not used
	 * @throws NoSuchAlgorithmException if SHA-512 digest not available
	 * @throws IOException on error
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		
		final int numExamples = 10;
		byte[] digest = null;
		
		for (int i = 0; i < numExamples; i++) {
			digest = new InternetEntropy().getBytes();
			System.out.println(bytesToHex(digest));
		}
	}
	
	/**
	 * get the digest of bytes from the list of sources
	 * 
	 * @return byte[] digest of bytes from random sources
	 * @throws NoSuchAlgorithmException if SHA-256 digest not available
	 * @throws IOException on io error
	 */
	public byte[] getBytes() throws NoSuchAlgorithmException, IOException {
		
		final MessageDigest digest = MessageDigest.getInstance(DEFAULT_DIGEST);

		for (final String url : urlList) {
			digest.update(getBytes(url));
			// add something to ensure uniqueness if the source didn't change
			digest.update(String.valueOf(System.nanoTime()).getBytes());
		}
		
		return digest.digest();
	}

	/**
	 * get bytes from a url.
	 * 
	 * @param url  URL to read to get bytes
	 * @return  byte[] read from the url
	 * @throws IOException on error
	 */
	private final byte[] getBytes(final String url) throws IOException  {
		
		final URL source = new URL(url);
		final URLConnection http = source.openConnection();
		final byte[] data = new byte[BUFSIZE];
		
		int totalBytes = 0;
		int bytesRead = 1; // set non zero for first run through
		InputStream reader = null;
		
		try {
			reader = http.getInputStream();

			while ((totalBytes + CHUNK) < BUFSIZE && bytesRead > 0) {
				bytesRead = reader.read(data, totalBytes, CHUNK);
				totalBytes += bytesRead;
			}
			
			if(DEBUG) System.out.println("read " + totalBytes + " bytes from " + url);
			
		} catch (IOException ioe) {
				System.err.println("error reading " + url);
		} finally {
			if (reader != null) reader.close();
		}

		return data;
	}
	
	
	/**
	 * convert array to string for viewing byte streams for debugging
	 * @param bytes byte[] array to print in hex format
	 * 
	 */

	static String bytesToHex(final byte[] bytes) {
		StringBuilder data = new StringBuilder();
		for (byte b : bytes) {
			data.append(String.format("%02X", b));
		}
	    return data.toString();
	}
}

import java.util.Date;

import trueRandom.TrueRandom;

public class UI {
	
	TrueRandom random;
	int rows = 0;
	
	public UI() {
		random = new TrueRandom();
	}
	
	public static void main(String[] args) throws InterruptedException {
		UI u = new UI();
		String r = u.getSample();
		System.out.println(r);
		while(true) {
			System.out.println(u.getSample());
			Thread.sleep(1000*60);
		}
		
	}
	String getSample() {
		StringBuilder result = new StringBuilder();
		result.append(new Date().toString() + "\nRandom numbers in RAND format:\n");
		int max = 1000000;
		for (int i = 0; i < 20; i++) {
			String r = String.format("%05d%9s%7s%9s%7s%9s%7s%9s%7s%9s%7s%n", ++rows,
					random.nextInt(max),random.nextInt(max),
					random.nextInt(max),random.nextInt(max),
					random.nextInt(max),random.nextInt(max),
					random.nextInt(max),random.nextInt(max),
					random.nextInt(max),random.nextInt(max)
					);
			result.append(r);
			if (rows > 999998) rows = 0;
		}
		
		result.append("\nRandom ascii passwords of length 8 to 24 characters:\n");
		for (int i = 0; i < 10; i++) {
			byte[] password = new byte[1];
			int counter = 0;
			int size = 8 + random.nextInt(12);
			
			while (counter < size) {
				random.nextBytes(password);
				byte c = password[0];
				if (c > 0x20 && c < 0x7f) {
					result.append((char)c);
					counter++;
				}
			}
			
			result.append("\n");
		}
		
		result.append("\nRandom alphanumeric strings from 16 to 48 characters:\n");
		for (int i = 0; i < 10; i++) {
			byte[] password = new byte[1];

			int counter = 0;
			int size = 16 + random.nextInt(32);
			
			while (counter < size) {
				random.nextBytes(password);
				byte c = password[0];
				
				if ( (c > 0x2f && c < 0x3a)  || (c > 0x40 && c < 0x5b) || (c > 0x60 && c < 0x7b) ) {
					result.append((char)c);
					counter++;
				}
			}
			
			result.append("\n");
		}
		result.append("\n");
		result.append("\n");
		result.append("total calls for random numbers: " + random.getCalls() + "\n");
		result.append("entropy pool size             : " + random.getEntropyBytes() + " bytes\n");
		result.append("entropy updates from weather  : " + random.getEntropyUpdates() + "\n");
		
		result.append("\nCalls on random generators:\n");
		int[] calls = random.getSourceCounts();
		for (int i = 0; i < calls.length; i++) {
			result.append(String.format("instance %5d%32s%9d%n", i, random.getClasses()[i], calls[i]));
		}
		result.append("\n");
		
		return result.toString();
	}

}

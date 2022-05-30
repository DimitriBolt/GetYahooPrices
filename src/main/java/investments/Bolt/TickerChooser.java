package investments.Bolt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TickerChooser {
	// Instance Variables
	@SuppressWarnings("serial")
	private ArrayList<String> tickerS = new ArrayList<String>() {
		{
//			add("SPY");
		}
	};
	// Initializer block
	{
//		tickerS.add("%5EDJI");
//		tickerS.add("GC=F");
	}

	// Create a class constructors for the TickerChooser class
	TickerChooser() {
		tickerS.add("CL=F");
		tickerS.add("NEBLQ");
		tickerS.add("BR");
		tickerS.add("BAX");
		tickerS.add("SMCI");
		tickerS.add("UVV");
		tickerS.add("GTT");

	}

	TickerChooser(File iFile) {
//		https://javadevblog.com/kak-schitat-csv-fajl-v-java.html !!
//		https://metanit.com/java/tutorial/6.9.php
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(iFile))) {
			String line = null;
			Scanner scanner = null;
			int index;
			bufferedReader.readLine(); // this will read the first line => skip first line
			while ((line = bufferedReader.readLine()) != null) {
				scanner = new Scanner(line);
				scanner.useDelimiter("\t");
				index = 0;
				while (scanner.hasNext()) {
					String data = scanner.next();
					if (index == 1)
						tickerS.add(data);
					index++;
				}
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}

//		this.iFile = "THNK.V";

//		tickerS.add("CL=F");
//		tickerS.add("TSLA");
//		tickerS.add(this.iFile);
	}

	// Accessor Methods
	ArrayList<String> getTickers() {
		return tickerS;
	}

}

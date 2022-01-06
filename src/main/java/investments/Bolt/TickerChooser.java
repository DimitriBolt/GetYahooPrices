package investments.Bolt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TickerChooser {
	// Instance Variables
	private ArrayList<String> tickers = new ArrayList<String>() {
		{
			add("SPY");
		}
	};
	private String iFile;
	// Initializer block
	{
		tickers.add("%5EDJI");
//		tickers.add("GC=F");
	}

	// Create a class constructors for the TickerChooser class
	TickerChooser() {
		tickers.add("CL=F");
		tickers.add("NEBLQ");
		tickers.add("BR");
		tickers.add("BAX");
		tickers.add("SMCI");
		tickers.add("UVV");
		tickers.add("GTT");

	}

	TickerChooser(File iFile) {
//		https://javadevblog.com/kak-schitat-csv-fajl-v-java.html !!
//		https://metanit.com/java/tutorial/6.9.php
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(iFile))) {
			// ������ ���������
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
						tickers.add(data);
					index++;
				}
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}

		this.iFile = "THNK.V";

//		tickers.add("CL=F");
		tickers.add("TSLA");
		tickers.add(this.iFile);
	}

	// Accessor Methods
	ArrayList<String> getTickers() {
		return tickers;
	}

}

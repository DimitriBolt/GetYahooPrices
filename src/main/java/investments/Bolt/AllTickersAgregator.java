package investments.Bolt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class AllTickersAgregator {
	// class variables
	// Initializer block
	// private Instance variable
	private SortedMap<String, OneTickerParser> allTickersMap = new TreeMap<String, OneTickerParser>();

	// Initializer block
	// Constructors
	AllTickersAgregator(ArrayList<String> tickerS) throws IOException {
		int iTickers = 1;
		for (String ticker : tickerS) {
			OneTickerThread threadName = new OneTickerThread(ticker, iTickers++, this.allTickersMap);
			threadName.start();
			try {
				threadName.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // End of for
	}// End of constructors

	// Methods
	// Accessor (= getter) methods
	SortedMap<String, OneTickerParser> getAllPrices() {
		return this.allTickersMap;
	}
}

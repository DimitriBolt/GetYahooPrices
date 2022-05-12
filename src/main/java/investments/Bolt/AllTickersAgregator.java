package investments.Bolt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
		HashSet<OneTickerThread> tickerThreadS = new HashSet<OneTickerThread>(tickerS.size()); // сюда надо складывать потоки.

		for (String ticker : tickerS) {
			// Запускаем 3000 потоков
			OneTickerThread threadName = new OneTickerThread(ticker, iTickers++, this.allTickersMap);
			threadName.start();
			tickerThreadS.add(threadName);
		} // End of for

		// Делаем так, чтобы потоки подождали друг друга.
		for (OneTickerThread threadName : tickerThreadS) {
			try {
				threadName.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // все потоки должны завершиться.

	}// End of constructors

	// Methods
	// Accessor (= getter) methods
	SortedMap<String, OneTickerParser> getAllPrices() {
		return this.allTickersMap;
	}
}

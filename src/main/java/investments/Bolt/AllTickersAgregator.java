package investments.Bolt;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class AllTickersAgregator {
	// class variables
	// Initializer block
	// private Instance variable
	private SortedMap<String, OneTickerParser> allTickersMap = new TreeMap<String, OneTickerParser>();

	// Initializer block
	// Constructors
	AllTickersAgregator(Set<String> tickerS) throws IOException {
		int iTickers = 1;
		HashSet<OneTickerThread> tickerThreadS = new HashSet<OneTickerThread>(tickerS.size()); // сюда надо складывать потоки.

		for (String ticker : tickerS) {
			// Запускаем 3000 потоков
			OneTickerThread threadName = new OneTickerThread(ticker, iTickers++, this.allTickersMap);
			threadName.start();
			tickerThreadS.add(threadName);
		} // End of for
		
		System.out.printf("this.allTickersMap.size() = %s\n", this.allTickersMap.size());

		// Делаем так, чтобы потоки подождали друг друга.
		for (OneTickerThread threadName : tickerThreadS) {
			try {
				threadName.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // все потоки должны завершиться. ! Тоько здесь мы собрали все потоки в кучу!
		System.out.printf("tickerThreadS.size() = %s\n", tickerThreadS.size());
		

	}// End of constructors

	// Methods
	// Accessor (= getter) methods
	SortedMap<String, OneTickerParser> getAllPrices() {
		System.out.printf("this.allTickersMap.size() = %s\n", this.allTickersMap.size());
		return this.allTickersMap;
	}
}

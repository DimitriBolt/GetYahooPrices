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
		HashSet<OneTickerThread> tickerThreadS = new HashSet<OneTickerThread>(); // сюда надо складывать потоки.
		System.out.printf("Class = %s | row = 35 | tickerS.size() = %s\n\n", this.getClass().getSimpleName(), tickerS.size());
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
		} // все потоки должны завершиться. ! Только здесь мы собрали все потоки в кучу!
		System.out.printf("Class = %s | row = 35 | tickerThreadS.size() = %s | Все потоки запущены, с-join-нены и работают ...\n\n", this.getClass().getSimpleName(), tickerThreadS.size());
		

	}// End of constructors

	// Methods
	// Accessor (= getter) methods
	SortedMap<String, OneTickerParser> getAllPrices() {
		System.out.printf("Class = %s | row = 43 | this.allTickersMap.size() = %s | Все цены получены, все потоки завершились.\n\n", this.getClass().getSimpleName(), this.allTickersMap.size());
		return this.allTickersMap;
	}
}

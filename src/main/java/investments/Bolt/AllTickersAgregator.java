package investments.Bolt;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AllTickersAgregator {
	// class variables
	// Initializer block
	// private Instance variable
	private SortedMap<String, OneTickerParser> allTickersMap = new TreeMap<String, OneTickerParser>();

	// Initializer block
	// Constructors
	AllTickersAgregator(Set<String> tickerS) throws IOException {
		int iTickers = 1;
		Set<Future> futureSet = new HashSet<Future>(); // сюда надо складывать потоки.
		System.out.printf("Class = %s | row = 35 | tickerS.size() = %s\n\n", this.getClass().getSimpleName(), tickerS.size());

		ExecutorService executorService = Executors.newCachedThreadPool();

		for (String ticker : tickerS) {
			// Запускаем 3000 потоков
			OneTickerRunnable oneTickerRunnable = new OneTickerRunnable(ticker, iTickers++, this.allTickersMap); // Runnable
			Future<?> future = executorService.submit(oneTickerRunnable);

			futureSet.add(future);
		}
		for (Future<?> future : futureSet) {
			try {
				future.get(2, TimeUnit.MINUTES);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				// Слава богу здесь срабатывает java.util.concurrent.TimeoutException
				// TODO но нужно понять, почему он срабатывает и как с этим бороться. 
				e.printStackTrace();
				System.out.printf("Class = %s \t| Row=41 | ?? %s  | strJsonUrl = %s\n", this.getClass().getSimpleName(), future.getClass().getSimpleName());
			}
		} // Тут все объединилось и, видимо, пока все не выполнился, дальше не идет. 
		executorService.shutdown();
		try {
			boolean done = executorService.awaitTermination(3, TimeUnit.MINUTES);
			System.out.printf("Class = %s | row = 49 | executorService.awaitTermination = %s\n\n", this.getClass().getSimpleName(), done);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// https://habr.com/ru/post/260953/
		System.out.printf("Class = %s | row = 35 | tickerThreadS.size() = %s | Все потоки запущены, с-join-нены и работают ...\n\n", this.getClass().getSimpleName(), futureSet.size());
	}// End of constructors
		// Methods
		// Accessor (= getter) methods

	SortedMap<String, OneTickerParser> getAllPrices() {
		System.out.printf("Class = %s | row = 61 | this.allTickersMap.size() = %s | Все цены получены, все потоки завершились.\n\n", this.getClass().getSimpleName(), this.allTickersMap.size());
		return this.allTickersMap;
	}
}

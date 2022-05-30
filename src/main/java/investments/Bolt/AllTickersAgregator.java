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
	private String dbTableName;

	// Initializer block
	// Constructors
	AllTickersAgregator(Set<String> tickerS, String dbTableName) throws IOException {
		// Этот класс служит только для управления потоками.
		this.dbTableName = dbTableName;
		{// TODO сделать TRUNCATE `yahoo_prices`.`prices`; как временное решение.
			DataBase.trancateTable(dbTableName);
		}
		int iTickers = 1;
		Set<Future> futureSet = new HashSet<Future>(); // сюда надо складывать потоки.
		System.out.printf("Class = %s\t| row = 26 | tickerS.size() = %s\n\n", this.getClass().getSimpleName(), tickerS.size());
		// ExecutorService: https://habr.com/ru/post/260953/
		ExecutorService executorService = Executors.newCachedThreadPool();

		for (String ticker : tickerS) {
			// Запускаем 3000 потоков
			// TODO Более логично OneTickerRunnable сделать Callable и в конце складывать складывать в this.allTickersMap
			OneTickerRunnable oneTickerRunnable = new OneTickerRunnable(ticker, iTickers++, this.allTickersMap, this.dbTableName); // Runnable
			Future<?> future = executorService.submit(oneTickerRunnable);
			futureSet.add(future);
		}
		// ☝ Все потоки запустились ☝ и начали успешно работать.
		for (Future<?> future : futureSet) {
			try {
				future.get(2, TimeUnit.MINUTES);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				// Слава богу здесь срабатывает java.util.concurrent.TimeoutException
				// TODO но нужно понять, почему он срабатывает и как с этим бороться.
				System.out.printf("Class = %s \t| Row = 47  | %s \t\t| %s\n", this.getClass().getSimpleName(), e.getClass().getSimpleName(), e.getCause());
			}
		} // Тут все объединилось и, видимо, пока все не выполнился, дальше не идет.
		executorService.shutdown();

		// TODO Это блок, судя по всему никогда не выполняется. Нужно его полностью переработать.
		try {
			boolean done = executorService.awaitTermination(2, TimeUnit.MINUTES);
			System.out.printf("\nClass = %s \t| row = 51 | executorService.awaitTermination = %s\n", this.getClass().getSimpleName(), done);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.printf("Class = %s\t| row = 55 | tickerThreadS.size() = %s | Все потоки с-join-нены и работают ...\n\n", this.getClass().getSimpleName(), futureSet.size());
		// TODO Если будет Callable, то здесь нужно будет сложить в this.allTickersMap все значения OneTickerRunnable.
	}// End of constructors
		// Methods
		// Accessor (= getter) methods

	SortedMap<String, OneTickerParser> getAllPrices() {
		// TODO Это не нужно будет.
		System.out.printf("Class = %s\t| row = 64 | this.allTickersMap.size() = %s | Все цены получены, все потоки завершились.\n\n", this.getClass().getSimpleName(), this.allTickersMap.size());
		return this.allTickersMap;
	}
}

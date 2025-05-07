package investments.Bolt.allTickersAggregator;

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

public class AllTickersAggregator {
	// class variables
	// Initializer block
	// private Instance variable
	private SortedMap<String, OneTickerParser> allTickersMap = new TreeMap<String, OneTickerParser>();
	// Initializer block
	// Constructors
    public AllTickersAggregator(Set<String> tickerS, String dbTableName) throws IOException {
		DataBase.truncateTable(dbTableName);
		int iTickers = 1;
		Set<Future> futureSet = new HashSet<Future>(); // сюда надо складывать потоки.
		System.out.printf("Class = %s\t| row = 26 | tickerS.size() = %s\n\n", this.getClass().getSimpleName(), tickerS.size());
		// ExecutorService: https://habr.com/ru/post/260953/
		ExecutorService executorService = Executors.newCachedThreadPool(); // Плохое временное решение, чтобы не разбираться с DeadLock.

		for (String ticker : tickerS) {
			// Запускаем 3000 потоков
			// Непонятно зачем потоку по одному тикеру иметь Map всх тиккеров?
			// !! Для отладки нужно влезать внутрь потока и смотреть, что там происходит. Для этого нужно поставить Break pont внутри !!
			OneTickerRunnable oneTickerRunnable = new OneTickerRunnable(ticker, iTickers++, this.allTickersMap, dbTableName); // Runnable
			Future<?> future = executorService.submit(oneTickerRunnable);
			futureSet.add(future);
//			System.out.printf("%s ", ticker);
		}
		System.out.printf("\rвсе потоки Создались и готовы к запуску\n", 1);
		// ☝ Все потоки запустились ☝ и начали успешно работать.
		
		for (Future<?> future : futureSet) {
			try {
				future.get(1, TimeUnit.MINUTES);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				// Слава богу здесь срабатывает java.util.concurrent.TimeoutException
				// TODO но нужно понять, почему он срабатывает и как с этим бороться.
				System.out.printf("Class = %1s \t| Row = 44  | %2s \t\t| %3s \t| isDone = %4s \t| isCancelled = %5s \t| %6s\n",
						this.getClass().getSimpleName(),
						e.getClass().getSimpleName(),
						e.getCause(),
						future.isDone(),
						future.isCancelled(),
						future.toString());
				future.cancel(true);
			}
		} // Тут все объединилось и, видимо?, пока все не выполнился, дальше не идет.
		executorService.shutdown();

		// TODO Это блок, судя по всему никогда не выполняется. Нужно его полностью переработать.
		try {
			boolean done = executorService.awaitTermination(2, TimeUnit.MINUTES);
			System.out.printf("\nClass = %s \t| row = 51 | executorService.awaitTermination = %s\n", this.getClass().getSimpleName(), done);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}// End of constructors
		// Methods
		// Accessor (= getter) methods

}

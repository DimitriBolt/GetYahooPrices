package investments.Bolt;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class OneTickerRunnable implements Runnable {
	// Class variables
	private String ticker;
	private Map<String, OneTickerParser> allTickersMap = new HashMap<String, OneTickerParser>();
	private int iTickers;
	// Initializer block

	// Constructors
	OneTickerRunnable(String ticker, int iTickers, SortedMap<String, OneTickerParser> allTickersMap) {
//		super(ticker);
		this.ticker = ticker;
		this.iTickers = iTickers;
		this.allTickersMap = allTickersMap;
	}

	public void run() {
		// 1. Скачиваем JsonElement для одного тикера.
		OneTickerFetcher oneTickerFetcher = new OneTickerFetcher(this.ticker, "1d");

		// 2.0 Проверка через исключение, что полученный JsonElement содержит .getAsJsonArray("timestamp") и его можно parse'ить.
		try {
			oneTickerFetcher.get1TikerPrices().getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonArray("timestamp").isJsonArray();
			// 2.1 Parse-им JsonElement для 1-го тикера.
			OneTickerParser oneTickerParser = new OneTickerParser(oneTickerFetcher.get1TikerPrices());
			// 3. Добавляем.
			// TODO хорошо бы понять, сколько раз я добавляю. Видимо не каждый поток сложил что-то в Map
			if (this.allTickersMap.put(ticker, oneTickerParser) != null) {
				System.out.printf("Хрень какая-то", ticker);
			}
			;
//			System.out.printf("Тиккер %7s, скачан и рас-parse'ен\t № %4d\r", Thread.currentThread().getName(), this.iTickers);
		} catch (NullPointerException ex) {
			// Это случается, если я в JsonFromUrl выставил jsonElement = null и нечего парсить, а фактически это значит, что тиккер умер.
//			System.out.println(ex);
			System.out.printf("Class = %s \t| Row=41 | Тиккер %s ничено не прислал. | strJsonUrl = %s\n", this.getClass().getSimpleName(), this.ticker, oneTickerFetcher.getStrJsonUrl());
		} catch (ClassCastException ex) {
			System.out.printf("\t%s\t => strJsonUrl = %s%n", this.getClass().getName(), oneTickerFetcher.getStrJsonUrl());
			System.out.println(ex);
			System.out.printf("Тиккер %s прислал, но отказ.\n-------------------\n\n", this.ticker);
		}
	}
	// Accessor (= getter) methods
}

package investments.Bolt;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class OneTickerThread extends Thread {
	// Class variables
	private String ticker;
	private Map<String, OneTickerParser> allTickersMap = new HashMap<String, OneTickerParser>();
	private int iTickers;
	// Initializer block

	// Constructors
	OneTickerThread(String ticker, int iTickers, SortedMap<String, OneTickerParser> allTickersMap) {
		super(ticker);
		this.ticker = ticker;
		this.iTickers = iTickers;
		this.allTickersMap = allTickersMap;
	}

	public void run() {
		// 1. Скачиваем JsonElement для одного тикера.
		OneTickerFetcher oneTickerFetcher = new OneTickerFetcher(this.ticker, "1d");

		// 2.0 Проверка, что полученный JsonElement содержит .getAsJsonArray("timestamp") и его можно parse'ить.
		try {
			oneTickerFetcher.get1TikerPrices().getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonArray("timestamp").isJsonArray();
			// 2.1 Parse-им JsonElement для 1-го тикера.
			OneTickerParser oneTickerParser = new OneTickerParser(oneTickerFetcher.get1TikerPrices());
			// 3. Добавляем.
			this.allTickersMap.put(ticker, oneTickerParser);
		} catch (NullPointerException ex) {
//			System.out.println(ex);
			System.out.printf("33 | %s | Тиккер %s ничено не прислал (почти наверняка оборвалось соединение и я выставил json=Null) .\n", this.getClass().getName(), this.ticker);
		} catch (ClassCastException ex) {
			System.out.printf("\t%s\t => strJsonUrl = %s%n", this.getClass().getName(), oneTickerFetcher.getStrJsonUrl());
			System.out.println(ex);
			System.out.printf("Тиккер %s прислал, но отказ.\n-------------------\n\n", this.ticker);
		}
		System.out.printf("Тиккер %7s, скачан и рас-parse'ен\t № %4d\r", this.ticker, this.iTickers);
	}
	// Accessor (= getter) methods
}

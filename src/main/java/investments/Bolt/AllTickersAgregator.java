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
	AllTickersAgregator(ArrayList<String> tickers) throws IOException {
		int iTickers = 1;
		for (String ticker : tickers) {
			OneTickerFetcher oneTickerFetcher = new OneTickerFetcher(ticker);
			try {
				oneTickerFetcher.get1TikerPrices().getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonArray("timestamp").isJsonArray(); // ��������, ��� ���������� Json ������������ parse-����.
				
				OneTickerParser oneTickerParser = new OneTickerParser(oneTickerFetcher.get1TikerPrices());
				allTickersMap.put(ticker, oneTickerParser);
			} catch (NullPointerException ex) {
				System.out.println(ex);
				System.out.printf("Тиккер %s ничено не прислал.\n-------------------\n\n", ticker);
			} catch (ClassCastException ex) {
				System.out.printf("\t%s\t => strJsonUrl = %s%n", this.getClass().getName(), oneTickerFetcher.getStrJsonUrl());
				System.out.println(ex);
				System.out.printf("Тиккер %s прислал, но отказ.\n-------------------\n\n", ticker);
			}
			System.out.printf("Тиккер %7s, скачан и рас-parse'ен\t № %4d\r", ticker, iTickers++);
		}

	}// End of constructors

	// Methods
	// Accessor (= getter) methods
	SortedMap<String, OneTickerParser> getAllPrices() {
		return this.allTickersMap;
	}

}

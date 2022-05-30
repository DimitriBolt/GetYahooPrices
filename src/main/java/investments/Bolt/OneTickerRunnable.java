package investments.Bolt;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import com.google.gson.JsonArray;

public class OneTickerRunnable implements Runnable {
	// Class variables
	private String ticker;
	private Map<String, OneTickerParser> allTickersMap = new HashMap<String, OneTickerParser>();
	private String dbTableName;

	// Constructors
	OneTickerRunnable(String ticker, int iTickers, SortedMap<String, OneTickerParser> allTickersMap, String dbTableName) {
		// По идее, хранит в себе
//		super(ticker);
		this.ticker = ticker;
		this.allTickersMap = allTickersMap;
		this.dbTableName = dbTableName;
	}

	public void run() {
		Thread.currentThread().setName(this.ticker);
// 1. Скачиваем JsonElement для одного тикера.
		OneTickerFetcher oneTickerFetcher = new OneTickerFetcher(this.ticker, "1d");

		try { // Проверка через исключение, что полученный JsonElement содержит .getAsJsonArray("timestamp") и его можно parse'ить.
			// TODO Совместить на проверку на последний день.
			oneTickerFetcher.get1TikerPrices().getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonArray("timestamp").isJsonArray();
// 2. Parse-им JsonElement для 1-го тикера.
			OneTickerParser oneTickerParser = new OneTickerParser(oneTickerFetcher.get1TikerPrices());

//			if (isLastDay(oneTickerParser.getTimeStampJsonArray())) { // TODO поверить, что есть последний день.
//				// Shit happened
//			}

// 2. Добавляем.
			// TODO хорошо бы понять, сколько раз я добавляю. Видимо не каждый поток сложил что-то в Map
			if (this.allTickersMap.put(ticker, oneTickerParser) != null) {
				System.out.printf("Хрень какая-то", ticker);
			}
// TODO вместо добавления в словарь, нужно сразу добавлять в БД.
// 2. Добавляем в БД.
			 DataBase dataBase = new DataBase (oneTickerParser, this.dbTableName);
			 dataBase.insert();

//			System.out.printf("Тиккер %7s, скачан и рас-parse'ен\t № %4d\r", Thread.currentThread().getName(), this.iTickers);
		} catch (NullPointerException ex) {
			// Это случается, если я в JsonFromUrl выставил jsonElement = null и нечего парсить, а фактически это значит, что тиккер умер.
//			System.out.println(ex);
			System.out.printf("Class = %s\t| Row = 55 | %s \t\t| strJsonUrl = %s\n", this.getClass().getSimpleName(), ex.getClass().getSimpleName(), oneTickerFetcher.getStrJsonUrl());
		} catch (ClassCastException ex) {
			System.out.printf("\t%s\t => strJsonUrl = %s%n", this.getClass().getName(), oneTickerFetcher.getStrJsonUrl());
			System.out.println(ex);
			System.out.printf("Тиккер %s прислал, но отказ.\n-------------------\n\n", this.ticker);
		}
	}

	Boolean isLastDay(JsonArray jsonArray) {
		int i = jsonArray.size();
		long timestamp = jsonArray.get(i - 1).getAsLong();
		LocalDate date = Instant.ofEpochMilli(timestamp * 1000).atZone(ZoneId.systemDefault()).toLocalDate();
		System.out.printf("%s %s %s\n", timestamp, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), this.ticker);
		return true;
	}
	// Accessor (= getter) methods
}

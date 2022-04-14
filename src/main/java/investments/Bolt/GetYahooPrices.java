package investments.Bolt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class GetYahooPrices {
	public static void main(String[] args) throws IOException {

		// tickerChooser хранит в себе список tickers.
		// https://www.nasdaq.com/market-activity/stocks/screener
		File iFile;
		if (args.length != 0) {
			iFile = new File(args[0]);
		} else {
			String userHomePath = System.getProperty("user.home");
			iFile = new File(userHomePath, "/Downloads/pricing/ifile.txt");
		}

		TickerChooser tickerChooser;
		tickerChooser = new TickerChooser(iFile);
		ArrayList<String> tickerS = tickerChooser.getTickers();
		System.out.printf("Main: Получили список tickers = %s\n\n", tickerS);

		// allTickersAgregator хранит в себе словарь списков с ценами. На входе получает список тикеров tickers 
		AllTickersAgregator allTickersAgregator = new AllTickersAgregator(tickerS);
		SortedMap<String, OneTickerParser> allTickersMap = new TreeMap<String, OneTickerParser>();
		allTickersMap = allTickersAgregator.getAllPrices();
//		System.out.printf("Main: Получили словарь со всеми данными = %s\n\n", allTickersMap);

		insertToDB2(allTickersMap);
	}

	static void insertToDB2(SortedMap<String, OneTickerParser> allTickersMap) {
		// java -classpath c:\Java\mysql-connector-java-8.0.11.jar;c:\Java Program
		try {
			// http://it.kgsu.ru/JA_OS/ja_os125.html
			// https://metanit.com/java/database/1.1.php
			String userHomePath = System.getProperty("user.home");
			File iniFile = new File(userHomePath, "/Documents/DBCredentials.ini");
			DBCredentials dbCredentials = new DBCredentials(iniFile);
			String mysqlUrlConnection = dbCredentials.getMySqlUrlConnection();
			try (Connection conn = DriverManager.getConnection(mysqlUrlConnection); // Подключился к БД из DBCredentials.ini
					BufferedWriter bufferedWriter = new BufferedWriter(
							new FileWriter(userHomePath + "/Documents/DurationAnalisys1BigInsert.csv"))) { // Подключился заодно и к файлу DurationAnalisys1BigInsert.csv
				// https://www.examclouds.com/ru/java/java-core-russian/try-with-resources
				bufferedWriter.write("Tikker" + "," // Записываем Заголовок таблицы в файл для анализа времени INSERT.
						+ "String sqlCommand = String.format(INSERT localcrypto.prices(ticker ... " + ","
						+ "statement.executeUpdate(sqlCommand);" + "\n");
				Statement statement = conn.createStatement();
				statement.executeUpdate("TRUNCATE `GetYahooPrices`.`prices`"); // Очистил всю таблицу, чтобы не было дубликатов.  
				int iTickers = 1;
				long durationStringComposung;
				long durationSqlInsert;
				for (Map.Entry<String, OneTickerParser> entry : allTickersMap.entrySet()) {
					// Цикл по тиккерам.
					System.out.printf("\rТиккер: %7s, будет добавлен в БД.\t №: %4d", entry.getKey(), iTickers++);
					try {
						entry.getValue().getTimeStampJsonArray().size(); // Просто проверка!!!
						long startStringComposing = System.nanoTime();
						// https://chartio.com/resources/tutorials/how-to-insert-if-row-does-not-exist-upsert-in-mysql/
						String sqlCommand = String.format(Locale.US,
								"INSERT prices(ticker, dealTime, volume, openPrice, high, low, closePrice, adjClose) VALUES ('%1$s', %2$d, %3$s, %4$s, %5$s, %6$s, %7$s, %8$s)", // Сформировано начало строки
								entry.getKey(), // Текстовое значение тиккера. Остальные поля - численные.
								entry.getValue().getTimeStampJsonArray().get(0).getAsInt(),
								entry.getValue().getVolumeJsonArray().get(0),
								entry.getValue().getOpenJsonArray().get(0), // Первый элемент массива getOpenJsonArray
								entry.getValue().getHighJsonArray().get(0), //
								entry.getValue().getLowJsonArray().get(0), // Первый элемент массива с ценами Low
								entry.getValue().getCloseJsonArray().get(0),
								entry.getValue().getAdjCloseJsonArray().get(0)); // Если нет adjClose, то не возможно взять 0-й элемент массива.

						for (int i = 1; i < entry.getValue().getTimeStampJsonArray().size(); i++) {
							// Цикл по TimeStamp'ам. Дополняю строку.
							sqlCommand = sqlCommand
									+ String.format(Locale.US, ", ('%1$s', %2$d, %3$s, %4$s, %5$s, %6$s, %7$s, %8$s)", // Довесок строки
											entry.getKey(), // Текстовое значение тиккера. Остальные поля - численные.
											entry.getValue().getTimeStampJsonArray().get(i).getAsInt(),
											entry.getValue().getVolumeJsonArray().get(i),
											entry.getValue().getOpenJsonArray().get(i),
											entry.getValue().getHighJsonArray().get(i),
											entry.getValue().getLowJsonArray().get(i),
											entry.getValue().getCloseJsonArray().get(i),
											entry.getValue().getAdjCloseJsonArray().get(i)); // Если нет adjClose, то не возможно взять i-й элемент массива. 
						}
						durationStringComposung = System.nanoTime() - startStringComposing;

						long startSqlInsert = System.nanoTime();
						statement.executeUpdate(sqlCommand);
						durationSqlInsert = System.nanoTime() - startSqlInsert;

						String stringForAnalisys = entry.getKey() + ";" // Строка файла /Documents/DurationAnalisys1BigInsert.csv 
								+ durationStringComposung + ";" 		// для анализа скорости записи в БД.
								+ durationSqlInsert + "\n";
						bufferedWriter.write(stringForAnalisys);

					} catch (NullPointerException ex) {
						System.out.printf("\nТиккер %7s не имеет одного из массивов!!!!!!!!!!!!!!!!!\n", entry.getKey());
					}
				}
			} // Закрылась БД
			System.out.println("\nConnection succesfully closed!");
		} catch (Exception ex) {
			System.out.println("\nConnection failed...");
			ex.printStackTrace();
		}
	} // insertToDB2

}

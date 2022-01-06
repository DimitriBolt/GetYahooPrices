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
		ArrayList<String> tickers = tickerChooser.getTickers();
		System.out.printf("Main: Получили список tickers = %s\n\n", tickers);

		// allTickersAgregator хранит в себе словарь списков с ценами.
		AllTickersAgregator allTickersAgregator = new AllTickersAgregator(tickers);
		SortedMap<String, OneTickerParser> allTickersMap = new TreeMap<String, OneTickerParser>();
		allTickersMap = allTickersAgregator.getAllPrices();
		System.out.printf("Main: Получили словарь со всеми данными = %s\n\n", allTickersMap);

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
			try (Connection conn = DriverManager.getConnection(mysqlUrlConnection);
					BufferedWriter bufferedWriter = new BufferedWriter(
							new FileWriter(userHomePath + "/Documents/DurationAnalisys1BigInsert.csv"))) {
				// https://www.examclouds.com/ru/java/java-core-russian/try-with-resources
				bufferedWriter.write("Tikker" + "," // Записываем Заголовок таблицы в файл для анализа времени INSERT.
						+ "String sqlCommand = String.format(INSERT localcrypto.prices(ticker ... " + ","
						+ "statement.executeUpdate(sqlCommand);" + "\n");
				Statement statement = conn.createStatement();
				int iTickers = 1;
				long durationStringComposung;
				long durationSqlInsert;
				for (Map.Entry<String, OneTickerParser> entry : allTickersMap.entrySet()) {
					// Цикл по тиккерам.
					System.out.printf("\rТиккер: %7s, будет добавлен в БД.\t №: %4d", entry.getKey(), iTickers++);
					try {
						entry.getValue().getTimeStampJsonArray().size(); // Просто проверка!!!

						// TODO Сформировать начало строки.
						long startStringComposing = System.nanoTime();
						String sqlCommand = String.format(Locale.US,
								"INSERT prices(ticker, dealTime, volume, openPrice, high, low, closePrice) VALUES ('%1$s', %2$d, %3$s, %4$s, %5$s, %6$s, %7$s)",
								entry.getKey(), // Текстовое значение тиккера. Остальные поля - численные.
								entry.getValue().getTimeStampJsonArray().get(0).getAsInt(),
								entry.getValue().getVolumeJsonArray().get(0),
								entry.getValue().getOpenJsonArray().get(0), // Первый элемент массива getOpenJsonArray
								entry.getValue().getHighJsonArray().get(0), //
								entry.getValue().getLowJsonArray().get(0), // Первый элемент массива с ценами Low
								entry.getValue().getCloseJsonArray().get(0));

						for (int i = 1; i < entry.getValue().getTimeStampJsonArray().size(); i++) {
							// Цикл по TimeStamp'ам. Дополняю строку.
							sqlCommand = sqlCommand
									+ String.format(Locale.US, ", ('%1$s', %2$d, %3$s, %4$s, %5$s, %6$s, %7$s)", //
											entry.getKey(), // Текстовое значение тиккера. Остальные поля - численные.
											entry.getValue().getTimeStampJsonArray().get(i).getAsInt(),
											entry.getValue().getVolumeJsonArray().get(i),
											entry.getValue().getOpenJsonArray().get(i),
											entry.getValue().getHighJsonArray().get(i),
											entry.getValue().getLowJsonArray().get(i),
											entry.getValue().getCloseJsonArray().get(i));
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
						System.out.printf("\nТиккер %7s не имеет TimeStamp !!!!!!!!!!!!!!!!!\n", entry.getKey());
					}
				}
			}
			System.out.println("\nConnection succesfully closed!");
		} catch (Exception ex) {
			System.out.println("\nConnection failed...");
			ex.printStackTrace();
		}
	} // insertToDB2

}

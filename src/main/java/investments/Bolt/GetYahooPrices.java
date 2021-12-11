package investments.Bolt;

import java.io.File;
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

		// ������ tickerChooser ������ ����� tickers.
		String userHomePath = System.getProperty("user.home");
		File iFile = new File(userHomePath, "/Downloads/pricing/ifile-Test.txt");
		TickerChooser tickerChooser;
		tickerChooser = new TickerChooser(iFile);
		ArrayList<String> tickers = tickerChooser.getTickers();
		System.out.printf("Main: Получили список tickers = %s\n\n", tickers);

		// ������ allTickersAgregator ������� � ������ ���� � ������ �� ���� tickers.
		AllTickersAgregator allTickersAgregator = new AllTickersAgregator(tickers);
		SortedMap<String, OneTickerParser> allTickersMap = new TreeMap<String, OneTickerParser>();
		allTickersMap = allTickersAgregator.getAllPrices();
		System.out.printf("%nMain:  allTickersMap" + " = %s\n", allTickersMap);

		insertToDB(allTickersMap);
		saveToCSV(allTickersMap, "");
	}

	static void insertToDB(SortedMap<String, OneTickerParser> allTickersMap) {
		// java -classpath c:\Java\mysql-connector-java-8.0.11.jar;c:\Java Program
		try {
			// http://it.kgsu.ru/JA_OS/ja_os125.html
			// https://metanit.com/java/database/1.1.php
			String userHomePath = System.getProperty("user.home");
			File iniFile = new File(userHomePath, "/Documents/DBCredentials.ini");
			DBCredentials dbCredentials = new DBCredentials (iniFile);
			String mysqlUrlConnection = dbCredentials.getMySqlUrlConnection();
			try (Connection conn = DriverManager.getConnection(mysqlUrlConnection)) {
				Statement statement = conn.createStatement();
				int iTickers = 1;
				for (Map.Entry<String, OneTickerParser> entry : allTickersMap.entrySet()) {
					// Цикл по тиккерам.
					System.out.printf("\rТиккер: %7s, добавлен в БД.\t №: %4d", entry.getKey(), iTickers++);
					try {
						entry.getValue().getTimeStampJsonArray().size(); //Просто проверка!!!

						for (int i = 0; i < entry.getValue().getTimeStampJsonArray().size(); i++) {
							// Цикл по TimeStamp'ам.
							String sqlCommand = String.format(Locale.US,
									"INSERT localcrypto.prices(ticker, dealTime, volume, openPrice, high, low, closePrice) VALUES ('%1$s', %2$d, %3$s, %4$s, %5$s, %6$s, %7$s)",
									entry.getKey(),
									entry.getValue().getTimeStampJsonArray().get(i).getAsInt(),
									entry.getValue().getVolumeJsonArray().get(i),
									entry.getValue().getOpenJsonArray().get(i),
									entry.getValue().getHighJsonArray().get(i),
									entry.getValue().getLowJsonArray().get(i),
									entry.getValue().getCloseJsonArray().get(i));
							statement.executeUpdate(sqlCommand);
						}

					} catch (NullPointerException ex) {
						System.out.printf("\nТиккер %7s не имеет TimeStamp !!!!!!!!!!!!!!!!!\n", entry.getKey());
					}

				}
			}
			System.out.println("\nConnection succesfull!");
		} catch (Exception ex) {
			System.out.println("Connection failed...");
			System.out.println(ex);
		}
	}

	static void saveToCSV(SortedMap<String, OneTickerParser> allTickersMap, String csvFolder) {
	}
}

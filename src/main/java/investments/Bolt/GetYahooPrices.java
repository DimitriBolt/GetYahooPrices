package investments.Bolt;

import investments.Bolt.allTickersAggregator.AllTickersAggregator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
		String dbTableName = args.length >= 1 ? args[1] : "prices";

		// Получаем список тикеров.
		TickerChooser tickerChooser;
		tickerChooser = new TickerChooser(iFile);
		Set<String> tickerS = new HashSet<String>(tickerChooser.getTickers());
		System.out.printf("Main: Получили %s тиккеров из файла %s\n", tickerS.size(), iFile);

		// allTickersAggregator Запускает и управляет потоками.
		AllTickersAggregator allTickersAggregator = new AllTickersAggregator(tickerS, dbTableName);
	}
}

package investments.Bolt;

import java.io.IOException;
import com.google.gson.JsonElement;

public class OneTickerFetcher {

	// Class variables
	private static final String YAHOO_QUOTE_URL_FMT = "https://query2.finance.yahoo.com/v8/finance/chart/%s?interval=%s&range=3y&includeTimestamps=true";
	private JsonElement jsonElement;
	private String strJsonUrl;
	private JsonFromUrl jsonFromUrl;

	// Initializer block
	// Constructors
	public OneTickerFetcher(String ticker, String frequency) {
		// https://stackoverflow.com/questions/44030983/yahoo-finance-url-not-working/47505102#47505102
		String strJsonUrl = String.format(YAHOO_QUOTE_URL_FMT, ticker, frequency);
		this.strJsonUrl = strJsonUrl; // Только для анализа исключения ClassCastException в вышестоящем классе
		try {
			// там внутри будет происходить все соединение-скачивание.
			this.jsonFromUrl = new JsonFromUrl(strJsonUrl);
			this.jsonElement = this.jsonFromUrl.getJsonElement();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// http://it.kgsu.ru/JA_OS/ja_os145.html
	}

	// Methods
	// Mutator (= setter) methods
	// Accessor (= getter) methods
	JsonElement get1TikerPrices() {
		return this.jsonElement;
	}

	public String getStrJsonUrl() {
		// Только для анализа исключения ClassCastException
		return this.strJsonUrl;
	}
}

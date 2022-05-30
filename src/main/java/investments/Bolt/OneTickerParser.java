package investments.Bolt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

class OneTickerParser {
	// static Class variables
	// private Instance variable
	private JsonArray timeStampJsonArray;
	private JsonArray volumeJsonArray;
	private JsonArray openJsonArray;
	private JsonArray highJsonArray;
	private JsonArray lowJsonArray;
	private JsonArray closeJsonArray;
	private JsonArray adjCloseJsonArray;
	String symbol;
	String exchangeName;
	

	// Initializer block
	// Constructors
	OneTickerParser(JsonElement jsonElement) {
		// http://it.kgsu.ru/JA_OS/ja_os138.html
		symbol					= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("meta").getAsJsonPrimitive("symbol").getAsString();
		exchangeName			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("meta").getAsJsonPrimitive("exchangeName").getAsString();
		timeStampJsonArray 		= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonArray("timestamp");
		volumeJsonArray 		= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("volume");
		openJsonArray 			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("open");
		highJsonArray 			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("high");
		lowJsonArray 			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("low");
		closeJsonArray 			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("close");
		try {
			adjCloseJsonArray 	= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("adjclose").get(0).getAsJsonObject().getAsJsonArray("adjclose");
		} catch (NullPointerException exception) {
			adjCloseJsonArray 	= closeJsonArray;
		}
	}

	// Methods
	// Mutator (= setter) methods
	// Accessor (= getter) methods
	JsonArray getVolumeJsonArray() {
		return volumeJsonArray;
	}

	JsonArray getTimeStampJsonArray() {
		return timeStampJsonArray;
	}

	JsonArray getOpenJsonArray() {
		return openJsonArray;
	}

	JsonArray getHighJsonArray() {
		return highJsonArray;
	}

	JsonArray getLowJsonArray() {
		return lowJsonArray;
	}

	JsonArray getCloseJsonArray() {
		return closeJsonArray;
	}

	JsonArray getAdjCloseJsonArray() {
		return adjCloseJsonArray;
	}

}

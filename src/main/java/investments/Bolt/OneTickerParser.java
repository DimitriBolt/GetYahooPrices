/**
 * 
 */
package investments.Bolt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * @author dimitri
 *
 */
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

	// Initializer block
	// Constructors
	OneTickerParser(JsonElement jsonElement) {
		// http://it.kgsu.ru/JA_OS/ja_os138.html
		timeStampJsonArray 		= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonArray("timestamp");
		volumeJsonArray			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("volume");
		openJsonArray			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("open");
		highJsonArray			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("high");
		lowJsonArray			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("low");
		closeJsonArray			= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("close");
		adjCloseJsonArray		= jsonElement.getAsJsonObject().getAsJsonObject("chart").getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject().getAsJsonArray("adjClose");
	}

	// Methods
	// Mutator (= setter) methods
	// Accessor (= getter) methods

	/**
	 * @return volumeJsonArray
	 */
	JsonArray getVolumeJsonArray() {
		return volumeJsonArray;
	}

	/**
	 * @return timeStampJsonArray
	 */
	JsonArray getTimeStampJsonArray() {
		return timeStampJsonArray;
	}

	/**
	 * @return openJsonArray
	 */
	JsonArray getOpenJsonArray() {
		return openJsonArray;
	}

	/**
	 * @return highJsonArray
	 */
	JsonArray getHighJsonArray() {
		return highJsonArray;
	}

	/**
	 * @return lowJsonArray
	 */
	JsonArray getLowJsonArray() {
		return lowJsonArray;
	}

	/**
	 * @return closeJsonArray
	 */
	JsonArray getCloseJsonArray() {
		return closeJsonArray;
	}

	/**
	 * @return adjCloseJsonArray
	 */
	JsonArray getAdjCloseJsonArray() {
		return adjCloseJsonArray;
	}
	
	
}

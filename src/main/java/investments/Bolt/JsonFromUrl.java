/**
 * 
 */
package investments.Bolt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author dimitri
 *
 */
class JsonFromUrl {
	// static Class variables
	// private Instance variable
	private JsonElement jsonElement;

	// Initializer block
	// Constructors
	JsonFromUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(20000);
		connection.setDoInput(true);
		connection.connect(); // Exception in thread "main" java.net.SocketTimeoutException: Connect timed out
		try {
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.printf("%s\t", connection.getResponseMessage());
				System.out.printf("%s\t => strJsonUrl = %s%n", this.getClass().getName(), urlString);
				this.jsonElement = null;
			} else {
				InputStream in = connection.getInputStream();
				JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(in));
//			JsonObject jsonObject = jsonElement.getAsJsonObject(); // ??
//			this.json = (jsonObject.has("response")) ? jsonObject.get("response").toString() : "[]"; //?
				this.jsonElement = jsonElement;
			}
		// TODO Сделать реальную обработку исключения 
			// javax.net.ssl.SSLException: Программа на вашем хост-компьютере разорвала установленное подключение
			//
		} finally {
			connection.disconnect();
		}
	}

	// Methods
	// Mutator (= setter) methods
	// Accessor (= getter) methods
	JsonElement getJsonElement() {
		return this.jsonElement;
	}
}

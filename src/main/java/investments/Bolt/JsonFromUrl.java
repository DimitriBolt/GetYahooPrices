package investments.Bolt;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Random;

class JsonFromUrl {
	// static Class variables
	// private Instance variable
	private JsonElement jsonElement;
	private boolean hangState = false;

	// Initializer block
	// Constructors
	JsonFromUrl(String urlString) throws IOException {
		// https://pro-java.ru/rabota-s-setyu-java/obzor-klassa-httpurlconnection-java-primery-rabotayushhix-programm/

		int i = 1; // Счетчик попыток соединения.
		do {
			HttpURLConnection connection = (new Connection(urlString)).connection;

//			java.util.Map<String, List<String>> requestProperties = connection.getRequestProperties(); // Это просто для экспериментов для подражания browser.
			try {
				connection.connect();
//				requestProperties = connection.getRequestProperties(); // Это просто для экспериментов для подражания browser.
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// Этот блок случается, когда тикер не найден?
					System.out.printf("Class = %s\t\t| row = 36 | getResponseMessage() = %s\t| Попытка соединения = %s | strJsonUrl = %s%n", this.getClass().getSimpleName(), connection.getResponseMessage(), i, urlString);
//					this.jsonElement = null;
					this.hangState = true; // В реальности нет зависания, просто используем туже переменную статуса.
				} else {
					// Суть!!
					System.out.printf("Поток %7s, начинает закачку ... strJsonUrl = %s\r", Thread.currentThread().getName(), urlString);
					InputStream inputStream = connection.getInputStream();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					// Именно здесь происходит само скачивание ↓
					String line = bufferedReader.readLine(); // А теперь здесь происходит реальное скачивание.
					// TODO в multithreading режиме может зависать. Фактически срабатывает SocketTimeoutException, но не отрабатывается catch, а
					// убивается поток!
					JsonElement jsonElement = new JsonParser().parse(line); // parsing происходит, почему-то весьма медленно.
					System.out.printf("Поток %7s, закончил закачку ... strJsonUrl = %s\r", Thread.currentThread().getName(), urlString);
					this.jsonElement = jsonElement;
					this.hangState = false;
				}
			} catch (SocketTimeoutException | ConnectException socketTimeoutException) {
				// Повисли....
				this.hangState = true;
				try {
					int slieepTime = (new Random()).nextInt(10) + 15;
					Thread.sleep(slieepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				this.hangState = true;
				System.out.printf("Class = %s\t\t| row = 68 | %s\t\t\t| Попытка соединения = %s | strJsonUrl = %s%n", this.getClass().getSimpleName(), ex.getClass().getSimpleName(), i, urlString);
			} finally {
				connection.disconnect();
			}

			if (i++ >= 6 & this.hangState) {
				// После 3- попыток хороша бы поднимать статус наверх. ??
				System.out.printf("Class = %s\t\t| row = 69 | После %s попыток ticker dropped! \t| strJsonUrl = %s%n", this.getClass().getSimpleName(), i - 1, urlString);
				this.hangState = false; // Принудительный выход из цикла попыток соединения.
				break; // Выход из бесконечного цикла при превышении числа попыток соединения.
				// TODO нужно понять, что присваивать this.jsonElement после 4-х неудачных попыток.
			}
		} while (this.hangState);
	}

	// Methods
	void safeJsonToDisk(String urlString, String line) {
		if (urlString.endsWith("Timestamps=true")) {
			try (FileWriter writer = new FileWriter("C:\\Users\\DBolt\\Downloads\\pricing\\JSON.json", true)) {
				writer.write(line + "\n");
				// writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Mutator (= setter) methods
	// Accessor (= getter) methods
	JsonElement getJsonElement() {
		return this.jsonElement;
	}
}

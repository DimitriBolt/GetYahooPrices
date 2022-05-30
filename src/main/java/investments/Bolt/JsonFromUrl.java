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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLException;

class JsonFromUrl {
	// static Class variables
	// private Instance variable
	private JsonElement jsonElement;
	private boolean hangState = false;

	// Initializer block
	// Constructors
	JsonFromUrl(String urlString) throws IOException {
		// https://pro-java.ru/rabota-s-setyu-java/obzor-klassa-httpurlconnection-java-primery-rabotayushhix-programm/

		URL url = new URL(urlString);
		int i = 1; // Счетчик попыток соединения.
		do {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// TODO снести все это в отдельный класс
			connection.setConnectTimeout(40000);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setReadTimeout(15000); // установка таймаута перед выполнением - 10 000 миллисекунд

			{ // TODO и это снести в отдельный класс
				connection.setRequestProperty("x-forwarded-proto", "https");
				connection.setRequestProperty("x-forwarded-port", "443");
				connection.setRequestProperty("cache-control", "max-age=0");
				connection.setRequestProperty("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"102\", \"Google Chrome\";v=\"102\"");
				connection.setRequestProperty("sec-ch-ua-mobile", "?0");
				connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
				connection.setRequestProperty("upgrade-insecure-requests", "1");
				connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
				connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
				connection.setRequestProperty("sec-fetch-site", "none");
				connection.setRequestProperty("sec-fetch-mode", "navigate");
				connection.setRequestProperty("sec-fetch-user", "?1");
				connection.setRequestProperty("sec-fetch-dest", "document");
//				connection.setRequestProperty("accept-encoding", "deflate, br");
//				connection.setRequestProperty("accept-encoding", "gzip"); // Не работает. 
//				connection.setRequestProperty("accept-encoding", "br");
				connection.setRequestProperty("accept-language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7");
				connection.setRequestProperty("if-none-match", "W/\"412-g6Fto3CZN+PKBEgEJhmGEl3ZCf8\"");
				connection.setRequestProperty("cookie", "sails.sid=s%3AQwso_vg5zSTKC4B_8z1UnyvKK1bBuXVD.7AF9u%2FiKTk4%2FZyFXVqlQOrzjaTw150F1cTyjKcanW7E");
//				connection.setRequestProperty("authority", "finance.yahoo.com");
//				connection.setRequestProperty("scheme", "https");
//				connection.setRequestProperty("dnt", "1");
//				connection.setRequestProperty("pragma", "no-cache");
			}
			java.util.Map<String, List<String>> requestProperties = connection.getRequestProperties(); // Это просто для экспериментов для подражания browser.

			try {
				connection.connect();
//				requestProperties = connection.getRequestProperties(); // Это просто для экспериментов для подражания browser.
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// Этот блок случается, когда тикер не найден?
					System.out.printf("Class = %s\t\t| row = 59 | getResponseMessage() = %s\t| Попытка соединения = %s | strJsonUrl = %s%n", this.getClass().getSimpleName(), connection.getResponseMessage(), i, urlString);
//					this.jsonElement = null;
					this.hangState = true; // В реальности нет зависания, просто используем туже переменную статуса.
				} else {
					// Суть!!
					System.out.printf("Поток %7s, начинает закачку ... strJsonUrl = %s\r", Thread.currentThread().getName(), urlString);
					InputStream inputStream = connection.getInputStream();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

					String line = bufferedReader.readLine(); // А теперь здесь происходит реальное скачивание.

					if (false) { // Для контроля скидываю полученный JSON на диск.
						if (urlString.endsWith("Timestamps=true")) {
							try (FileWriter writer = new FileWriter("C:\\Users\\DBolt\\Downloads\\pricing\\JSON.json", true)) {
								writer.write(line + "\n");
//								writer.flush();
							}
						}
					}

					// Именно здесь происходит само скачивание ↓
					// TODO в multithreading режиме может зависать. Фактически срабатывает SocketTimeoutException, но не отрабатывается catch, а
					// убивается поток!
					JsonElement jsonElement = new JsonParser().parse(line); // parsing происходит, почему-то весьма медленно.
					System.out.printf("Поток %7s, закончил закачку ... strJsonUrl = %s\r", Thread.currentThread().getName(), urlString);
					this.jsonElement = jsonElement;
					this.hangState = false;
				}
			} catch (Exception ex) {
				// Повисли....
				this.hangState = true;
				try {
					int slieepTime = (new Random()).nextInt(10) + 15;
					Thread.sleep(slieepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.printf("Class = %s\t\t| row =113 | %s\t\t\t| Попытка соединения = %s | strJsonUrl = %s%n", this.getClass().getSimpleName(), ex.getClass().getSimpleName(), i, urlString);
			} finally {
				connection.disconnect();
			}

			if (i++ >= 6 & this.hangState) {
				// После 3- попыток хороша бы поднимать статус наверх.
				System.out.printf("Class = %s\t\t| row =118 | После %s попыток ticker dropped! \t| strJsonUrl = %s%n", this.getClass().getSimpleName(), i - 1, urlString);
				this.hangState = false; // Принудительный выход из цикла попыток соединения.
				break; // Выход из бесконечного цикла при превышении числа попыток соединения.
				// TODO нужно понять, что присваивать this.jsonElement после 4-х неудачных попыток.
			}
		} while (this.hangState);
	}

	// Methods
	// Mutator (= setter) methods
	// Accessor (= getter) methods
	JsonElement getJsonElement() {
		return this.jsonElement;
	}
}

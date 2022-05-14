package investments.Bolt;

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
		int i = 1; // проверка от бесконечного цикла.
		do {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(40000);
			connection.setDoInput(true);
			// Попытка соединения
			try {
				connection.connect();
			} catch (SocketTimeoutException e) {
				System.out.printf("32 %s\t%s\t => strJsonUrl = %s%n", e.getMessage(), this.getClass().getName(), urlString);
			} // Exception in thread "main" java.net.SocketTimeoutException: Connect timed out
			catch (ConnectException ex) {
//				System.out.printf("%s\t |36 \t |%s\t |strJsonUrl = %s%n", this.getClass().getSimpleName(), ex.getClass().getSimpleName(), urlString);
				// Это значит началась жопа, сервер тормозит.
				// TODO Что-то сделать с ConnectException?? Подождать??? Заново подключиться??
			} catch (SSLException ex) {
				System.out.printf("45 %s\t%s\t => strJsonUrl = %s%n", ex.getMessage(), this.getClass().getName(), urlString);
				this.hangState = true;
			} catch (SocketException ex) {
				System.out.printf("48 %s\t%s\t => strJsonUrl = %s%n", ex.getMessage(), this.getClass().getName(), urlString);
				this.hangState = true;
			}

			// Попытка работы с установленным выше соединением
			try {
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					// Этот блок случается, когда тиккер не найден.
					System.out.printf("Class = %s \t| row = 56 | getResponseMessage() = %s | Попытка соединения = %s | strJsonUrl = %s%n", this.getClass().getSimpleName(), connection.getResponseMessage(), i, urlString);
//					this.jsonElement = null;
					this.hangState = true; // В реальности нет зависания, просто используем туже переменную статуса.
				} else {
					// Суть!!
					InputStream in = connection.getInputStream();
					// В multi threading режиме может зависать.
					JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(in));
					this.jsonElement = jsonElement;
					this.hangState = false;
				}
				// javax.net.ssl.SSLException: Программа на вашем хост-компьютере разорвала установленное подключение
			} catch (ConnectException ex) {
				// Повисли....
				System.out.printf("Class = %s | row = 63 | %s | Попытка соединения = %s | strJsonUrl = %s%n", this.getClass().getSimpleName(), ex.getClass().getSimpleName(), i, urlString);
				this.hangState = true;
				try {
					Thread.currentThread();
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				connection.disconnect();
			}

			if (i++ >= 3 & this.hangState) {
				// После 3- попыток хороша бы поднимать статус наверх. 
				System.out.printf("%s\t |85 \t | i=%s, ticker dropped! |strJsonUrl = %s%n", this.getClass().getSimpleName(), i, urlString);
				this.hangState = false; // Принудительный выход из цикла
				break;
				// TODO нужно понять, что присваивать this.jsonElement после 3-х неудачных попыток. 
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

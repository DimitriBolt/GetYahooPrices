package investments.Bolt.allTickersAggregator;

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
import java.util.Objects;
import java.util.Random;

class JsonFromUrl {
    // static Class variables
    // private Instance variable
    private JsonElement jsonElement;
    private boolean hangState = false; // По умолчанию зависания нет и повторов нет.
    String responseMessage;

    // Initializer block
    // Constructors
    JsonFromUrl(String urlString) throws IOException {
        // https://pro-java.ru/rabota-s-setyu-java/obzor-klassa-httpurlconnection-java-primery-rabotayushhix-programm/

        int i = 1; // Счетчик попыток соединения.
        do {
            HttpURLConnection connection = (new Connection(urlString)).connection;
            //!!! Очень важно периодически менять browser-agent, иначе Yahoo блокирует соединение.!!!!
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			java.util.Map<String, List<String>> requestProperties = connection.getRequestProperties(); // Это просто для экспериментов для подражания browser.
            try {
                // requestProperties = connection.getRequestProperties(); // Это просто для экспериментов для подражания browser.
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    // Соединение есть, но данных нет, например тикер "Not Found".
                    responseMessage = connection.getResponseMessage();
                    System.out.printf("Class = %1$s\t\t\t| row = 39 | getResponseMessage() = %2$s\t| Попытка соединения = %3$s\t| strJsonUrl = %4$s%n", this.getClass().getSimpleName(), connection.getResponseMessage(), i, urlString);
//					this.jsonElement = null;
                    this.hangState = true; // В реальности нет зависания, просто используем туже переменную статуса. Идём на повтор.
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
                // Соединения нет
                // Повисли....
                this.hangState = true; // Идём на повтор
                try {
                    int slieepTime = (new Random()).nextInt(2000) + 1000;
                    Thread.sleep(slieepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                // Соединения нет
                ex.printStackTrace();
                this.hangState = true; // Идём на повтор
                System.out.printf("Class = %s\t\t| row = 68 | %s\t\t\t| Попытка соединения = %s| strJsonUrl = %s%n", this.getClass().getSimpleName(), ex.getClass().getSimpleName(), i, urlString);
            } finally {
                connection.disconnect();
                requestProperties = connection.getRequestProperties(); // Это просто для экспериментов для подражания browser.
            }

            // Проверки условий не пора ли выходить из цикла.
            if ((i++ >= 6 || Objects.equals(responseMessage, "Not Found")) & this.hangState) {
                // После 6-ти попыток хорошо бы поднимать статус наверх. ??
                System.out.printf("Class = %s\t\t\t| row = 78 | После %s попыток ticker dropped! \t\t\t\t\t\t\t\t| strJsonUrl = %s%n", this.getClass().getSimpleName(), i - 1, urlString);
                this.hangState = false; // Принудительный выход из цикла попыток соединения.
                break; // Выход из бесконечного цикла при превышении числа попыток соединения.
                // TODO нужно понять, что присваивать this.jsonElement после 6-х неудачных попыток. JSON-константу с именем тиккера и NULL-ами?
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

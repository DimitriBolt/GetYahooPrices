package investments.Bolt.allTickersAggregator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

class Connection {

	// static Class variables
	// private Instance variable
	HttpURLConnection connection;
	// Initializer block
	// Constructors
	Connection (String urlString){
		// TODO и это снести в отдельный класс
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		connection.setConnectTimeout(40000);
		connection.setDoInput(true);
		try {
			connection.setRequestMethod("GET");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connection.setReadTimeout(15000); // установка таймаута перед выполнением - 10 000 миллисекунд
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
//		connection.setRequestProperty("accept-encoding", "deflate, br");
//		connection.setRequestProperty("accept-encoding", "gzip"); // Не работает. 
//		connection.setRequestProperty("accept-encoding", "br");
		connection.setRequestProperty("accept-language", "en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7");
		connection.setRequestProperty("if-none-match", "W/\"412-g6Fto3CZN+PKBEgEJhmGEl3ZCf8\"");
		connection.setRequestProperty("cookie", "sails.sid=s%3AQwso_vg5zSTKC4B_8z1UnyvKK1bBuXVD.7AF9u%2FiKTk4%2FZyFXVqlQOrzjaTw150F1cTyjKcanW7E");
//		connection.setRequestProperty("authority", "finance.yahoo.com");
//		connection.setRequestProperty("scheme", "https");
//		connection.setRequestProperty("dnt", "1");
//		connection.setRequestProperty("pragma", "no-cache");
	}

	// Methods
	// Mutator (= setter) methods
	// Accessor (= getter) methods
	String getInstanceVariable() {
		return "this.InstanceVariable";
	}

}

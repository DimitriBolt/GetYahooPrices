package investments.Bolt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;

import com.google.gson.JsonArray;

class DataBase {

	// static Class variables
	// private Instance variable
	String dbTableName;
	private JsonArray timeStampJsonArray;
	private JsonArray volumeJsonArray;
	private JsonArray openJsonArray;
	private JsonArray highJsonArray;
	private JsonArray lowJsonArray;
	private JsonArray closeJsonArray;
	private JsonArray adjCloseJsonArray;
	private String symbol;
	String exchangeName;

	// Initializer block
	// Constructors
	DataBase(OneTickerParser oneTickerParser, String dbTableName) {
		this.dbTableName = dbTableName;
		this.timeStampJsonArray = oneTickerParser.getTimeStampJsonArray();
		this.volumeJsonArray = oneTickerParser.getVolumeJsonArray();
		this.openJsonArray = oneTickerParser.getOpenJsonArray();
		this.highJsonArray = oneTickerParser.getHighJsonArray();
		this.lowJsonArray = oneTickerParser.getLowJsonArray();
		this.closeJsonArray = oneTickerParser.getCloseJsonArray();
		this.adjCloseJsonArray = oneTickerParser.getAdjCloseJsonArray();
		this.symbol = oneTickerParser.symbol;
		exchangeName = oneTickerParser.exchangeName;

	}

	// Methods
	public void insert() {
		// http://it.kgsu.ru/JA_OS/ja_os125.html
		// https://metanit.com/java/database/1.1.php
		String mysqlUrlConnection = (new DB_Credentials()).getProperty("mySqlUrlConnection");
		String exstingDBTableName = (new IsDBTableExists(dbTableName)).exstingDBTableName;
		try (Connection connection = DriverManager.getConnection(mysqlUrlConnection)) { // Подключился к БД из DB_Credentials()
			// https://www.examclouds.com/ru/java/java-core-russian/try-with-resources
			connection.setAutoCommit(false);
			// https://chartio.com/resources/tutorials/how-to-insert-if-row-does-not-exist-upsert-in-mysql/
			String sqlCommand;
			{ // Объединяем все запросы по каждому дня в пакет.
				// sqlCommand = String.format("INSERT INTO %1$s VALUES (?, '%2$s', ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE volume = ?, openPrice = ?, high = ?, low =?, closePrice= ?, adjClose= ?;", exstingDBTableName, this.symbol);
				// sqlCommand = String.format("REPLACE INTO %1$s VALUES (?, '%2$s', ?, ?, ?, ?, ?, ?);", exstingDBTableName, this.symbol);
				sqlCommand = String.format("INSERT INTO %1$s VALUES (?, '%2$s', ?, ?, ?, ?, ?, ?);", exstingDBTableName, this.symbol);
				// https://habr.com/ru/post/501756/
				PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
				for (int i = 0; i < timeStampJsonArray.size(); i++) {
					try {
						preparedStatement.setInt(1, timeStampJsonArray.get(i).getAsInt());
						preparedStatement.setLong(2, volumeJsonArray.get(i).getAsLong());
						preparedStatement.setFloat(3, openJsonArray.get(i).getAsFloat());
						preparedStatement.setFloat(4, highJsonArray.get(i).getAsFloat());
						preparedStatement.setFloat(5, lowJsonArray.get(i).getAsFloat());
						preparedStatement.setFloat(6, closeJsonArray.get(i).getAsFloat());
						preparedStatement.setFloat(7, adjCloseJsonArray.get(i).getAsFloat());
						/*
						 * pstmt.setLong(8, volumeJsonArray.get(i).getAsLong()); pstmt.setFloat(9, openJsonArray.get(i).getAsFloat()); pstmt.setFloat(10,
						 * highJsonArray.get(i).getAsFloat()); pstmt.setFloat(11, lowJsonArray.get(i).getAsFloat()); pstmt.setFloat(12,
						 * closeJsonArray.get(i).getAsFloat()); pstmt.setFloat(13, adjCloseJsonArray.get(i).getAsFloat());
						 */
						preparedStatement.addBatch();
					} catch (UnsupportedOperationException exception) {
						Date date = new Date(timeStampJsonArray.get(i).getAsLong() * 1000);
//					System.out.printf("\r По тикеру %s за %tc данных нет.\t i= %d\n", symbol, date, i);
					}
//				System.out.printf("\rТикер = %s, i = %d", this.symbol, i);
				}
				int[] result = preparedStatement.executeBatch();
				preparedStatement.close();
//			System.out.printf("\n Для тикера %s вставлено %d строк. А всего обработано %d временных меток.\n", symbol, Arrays.stream(result).sum(), timeStampJsonArray.size());
			}
			{ // Собираем свойства бумаги
				// https://metanit.com/java/database/1.1.php
				sqlCommand = "insert IGNORE  into security_detail values (?, ?,?);";
				PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand);
				preparedStatement.setString(1, this.symbol);
				preparedStatement.setString(2, "exchangeName");
				preparedStatement.setString(3, exchangeName);
				preparedStatement.addBatch();
				int[] result = preparedStatement.executeBatch();
				preparedStatement.close();
			}
			connection.commit();
			connection.close(); // В данном случае не нужно
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void trancateTable(String dbTableName) {
		// http://it.kgsu.ru/JA_OS/ja_os125.html
		// https://metanit.com/java/database/1.1.php
		// https://habr.com/ru/post/501756/
		String mysqlUrlConnection = (new DB_Credentials()).getProperty("mySqlUrlConnection");
		// TODO Заглушка!!
		mysqlUrlConnection = "jdbc:oracle:thin:@testdb_high?TNS_ADMIN=D:/Distrib/sqldeveloper-23.1.1.345.2114-x64/Wallet_testDB";
		String exstingDBTableName = (new IsDBTableExists(dbTableName)).exstingDBTableName;
		Connection connection = null;
		try {
			// connection = DriverManager.getConnection(mysqlUrlConnection);
			connection = DriverManager.getConnection(mysqlUrlConnection, "ADMIN", "Db123456!@#$%^");
			
			connection.setAutoCommit(false);
			String sqlCommand = String.format("TRUNCATE TABLE ADMIN.%s drop STORAGE;", exstingDBTableName);
			
			/*
			PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
			{
				// setString(1, exstingDBTableName);
				pstmt.addBatch();
			}
			int[] result = pstmt.executeBatch();
			pstmt.close();
			*/
			
			
			
			connection.close(); // В данном случае нужно обязательно!
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Mutator (= setter) methods
	// Accessor (= getter) methods
	String getInstanceVariable() {
		return "this.InstanceVariable";
	}

}

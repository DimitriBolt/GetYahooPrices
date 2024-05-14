package investments.Bolt.allTickersAggregator;

import java.sql.*;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

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
        // Объект хранит в себе все массивы и прочие данные, которые потом нужно будет вставить.
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
        // https://habr.com/ru/post/501756/
        boolean hangState = false; // Флаг того, что соединение не состоялось. Если True - то нужно продолжать попытки.
        String mysqlUrlConnection = (new DB_Credentials()).getProperty("mySqlUrlConnection");
        String user = (new DB_Credentials()).getProperty("user");
        String password = (new DB_Credentials()).getProperty("password");
        String existingDBTableName = (new IsDBTableExists(dbTableName)).exstingDBTableName;
        int j = 1; // Счетчик попыток соединения.
        do {
            try (Connection connection = DriverManager.getConnection(mysqlUrlConnection, user, password)) { // Подключился к БД из DB_Credentials()
                // https://www.examclouds.com/ru/java/java-core-russian/try-with-resources
                connection.setAutoCommit(false);
                // https://chartio.com/resources/tutorials/how-to-insert-if-row-does-not-exist-upsert-in-mysql/
                String sqlCommand;
                { // Объединяем все запросы по каждому дня в пакет.
                    // sqlCommand = String.format("INSERT INTO %1$s VALUES (?, '%2$s', ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE volume = ?, openPrice = ?, high = ?, low =?, closePrice= ?, adjClose= ?;", existingDBTableName, this.symbol);
                    // sqlCommand = String.format("REPLACE INTO %1$s VALUES (?, '%2$s', ?, ?, ?, ?, ?, ?);", existingDBTableName, this.symbol);
                    sqlCommand = String.format("INSERT INTO %1$s VALUES (?, '%2$s', ?, ?, ?, ?, ?, ?)", existingDBTableName, this.symbol);
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
                    System.out.printf("Class = %1$s\t\t\t| row = 90 | Для тикера %2$s вставлено %3$d строк.\t\t\tА всего обработано %4$d временных меток.\n", this.getClass().getSimpleName(), symbol, Arrays.stream(result).sum(), timeStampJsonArray.size());
                }
                /**
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
                 **/
                connection.commit();
                connection.close(); // В данном случае не нужно
                hangState = false; // Всё в порядке, продолжать попытки не нужно.
            } catch (SQLRecoverableException exception) {
                System.out.println("Жопа началась");
                // exception.printStackTrace(System.out);
                hangState = true; // Идём на повтор
                int sleepTime = (new Random()).nextInt(4000) + 3000;
                System.out.printf("Жопа случилась. Идём на повтор после паузы в %1$d миллисекунд\n", sleepTime);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    // Не сработает никогда
                }
            } catch (SQLException exception) {
                System.out.println("Какая-то другая жопа с БД");
                throw new RuntimeException(exception);
            }
            // Проверки условий не пора ли выходить из цикла.
            if (j++ >= 6 & hangState) {
                // После 6-ти попыток хорошо бы поднимать статус наверх. ??
                System.out.printf("Class = %1$s\t\t\t| row = 125 | После %2$d попыток ticker dropped!", this.getClass().getSimpleName(), j - 1);
                hangState = false; // Принудительный выход из цикла попыток соединения.
                break; // Выход из бесконечного цикла при превышении числа попыток соединения.
                // TODO нужно понять, что присваивать this.jsonElement после 6-х неудачных попыток. JSON-константу с именем тиккера и NULL-ами?
            }
        } while (hangState);
    }

    static void truncateTable(String dbTableName) {
        // http://it.kgsu.ru/JA_OS/ja_os125.html
        // https://metanit.com/java/database/1.1.php
        String mysqlUrlConnection = (new DB_Credentials()).getProperty("mySqlUrlConnection");
        String user = (new DB_Credentials()).getProperty("user");
        String password = (new DB_Credentials()).getProperty("password");
        String existingDBTableName = (new IsDBTableExists(dbTableName)).exstingDBTableName;
        Connection connection = null;
        try {
            // connection = DriverManager.getConnection(mysqlUrlConnection);
            connection = DriverManager.getConnection(mysqlUrlConnection, user, password);
            connection.setAutoCommit(false); // Непонятно, что это
            // String sqlCommand = String.format("TRUNCATE TABLE ADMIN.%s", existingDBTableName);
            String sqlCommand = String.format("TRUNCATE TABLE ADMIN.%s", existingDBTableName);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlCommand);
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

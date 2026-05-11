package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conn {

    private static final String URL = "jdbc:mysql://192.168.10.5:3306/erronka2?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "2MG2024";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
            System.out.println("MySQL driver-a ondo kargatu da");
        } catch (ClassNotFoundException e) {
            System.err.println("Errorea MySQL driver-a kargatzean: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conn != null) {
                System.out.println("Datu-basearekin konexioa ondo ezarri da");
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("Errorea datu-basearekin konexioan: " + e.getMessage());
            throw new SQLException("Ezin izan da datu-basearekin konektatu: " + e.getMessage(), e);
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Konexio proba: ARRAKASTATSUA");
            }
        } catch (SQLException e) {
            System.err.println("Konexio proba: HUTS - " + e.getMessage());
        }
    }
}

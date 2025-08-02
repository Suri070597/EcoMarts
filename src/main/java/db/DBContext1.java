/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author HuuDuc
 */

public class DBContext1 {

    // Đọc biến môi trường từ .env
    private static final Dotenv dotenv = Dotenv.configure().load();

    private static final String JDBC_URL = dotenv.get("JDBC_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            // Nếu driver không tìm thấy, ném IllegalStateException
            throw new IllegalStateException("SQL Server JDBC Driver not found", e);
        }
    }

    public DBContext1() {
    }

    public Connection getConnection() throws SQLException {
        if (JDBC_URL == null || DB_USER == null || DB_PASSWORD == null) {
            throw new IllegalStateException("Database configuration is missing in .env file");
        }
        // Thiết lập kết nối
        Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
        System.out.println("Database connection established");
        return conn;
    }
}

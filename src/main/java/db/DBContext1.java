/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext1 {
    private static final Logger LOGGER = Logger.getLogger(DBContext1.class.getName());
    private static final Dotenv dotenv = Dotenv.configure().load();

    private static final String JDBC_URL = dotenv.get("JDBC_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "SQL Server JDBC Driver not found: " + e.getMessage(), e);
            throw new IllegalStateException("SQL Server JDBC Driver not found", e);
        }
    }

    public DBContext1() {
    }

    public Connection getConnection() throws SQLException {
        if (JDBC_URL == null || DB_USER == null || DB_PASSWORD == null) {
            LOGGER.severe("Database configuration is missing in .env file");
            throw new IllegalStateException("Database configuration is missing");
        }
        Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
        LOGGER.info("Database connection established");
        return conn;
    }
}

package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbconnect {

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                establishConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

 
    private static void establishConnection() {
        try {
     
            Class.forName("com.mysql.cj.jdbc.Driver");

        
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/estrandyante", "root", "root");

            System.out.println("Database connected successfully.");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error establishing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isConnectionValid() {
        try {
            boolean valid = connection != null && !connection.isClosed() && connection.isValid(2); // Timeout of 2 seconds
            System.out.println("Connection valid: " + valid);
            return valid;
        } catch (SQLException e) {
            System.err.println("Error checking connection validity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection is successful.");
                return true;
            } else {
                System.out.println("Database connection failed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error testing connection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

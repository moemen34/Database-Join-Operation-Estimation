package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectToPgJDBC {
	private final String url = "jdbc:postgresql://localhost/Project";//your link
	private final String user = "postgres"; //your user
	private final String password = "PASSWORD";//your password
	
	
	public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println("Connection Failed\n"+e.getMessage());
        }

        return conn;
    }
}

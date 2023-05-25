package project;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class project {

	public static void main(String[] args) throws Exception {
		
		if(args.length != 2) {
			throw new Exception("You need to provide 2 table names as 2 arguments!!");
		}
		
		//get table names from arguments
		String table1 =args[0]; 
		String table2 =args[1]; 

		//Create new connection object and connect to the DB
		ConnectToPgJDBC connect = new ConnectToPgJDBC();
		Connection conn = connect.connect();
			
		int estimated = estimatedJoinSize(conn, table1, table2);
		int actual = actualJoinSize(conn, table1, table2);
		
		System.out.println("Estimated Join Size: " + estimated);
		System.out.println("Actual Join Size: " + actual);
		System.out.println("Estimation Error: " + (estimated - actual));
	}
	
	
	/**
	 * Method that returns the attributes that make up the PK of the table
	 * @param conn connection to the DB
	 * @param table table name
	 * @return arraylist that holds the attributes that make up the PK
	 */
	public static ArrayList<String> getPrimaryKey(Connection conn, String table){
		ArrayList<String> primaryKey = new ArrayList<String>();
		
		try {
			DatabaseMetaData dbMeta = conn.getMetaData();
			ResultSet rs = dbMeta.getPrimaryKeys(null, null, table);
			
			while(rs.next()) {
				primaryKey.add(rs.getString("COLUMN_NAME"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return primaryKey;
	}
	
	/**
	 * Method that returns the attributes of a relation
	 * @param conn connection to DB
	 * @param table table to get the attribute of
	 * @return arrayList with attributes of table passed as param
	 */
	public static ArrayList<String> getAttributes(Connection conn, String table){
		ArrayList<String> attributes = new ArrayList<String>();
		
		Statement statement;//SQL statement                     
		try {
			statement = conn.createStatement();
			
			ResultSet resultSet = statement.executeQuery("SELECT column_name\r\n"
					+ "FROM information_schema.columns \r\n"
					+ "WHERE table_name = '" + table + "'");			
			
			while(resultSet.next()) {
				attributes.add(resultSet.getString(1));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return attributes;
	}
	
	/**
	 * Method that gets the intersection of 2 lists of attributes
	 * @param Attributes1
	 * @param Attributes2
	 * @return
	 */
	public static ArrayList<String> AttributesIntersection(ArrayList<String> Attributes1, ArrayList<String> Attributes2) {
		ArrayList<String> intersection = new ArrayList<String>();
		
		for(String att : Attributes1) {
			if(Attributes2.contains(att)) {
				intersection.add(att);
			}
		}		
		return intersection;
	}
	
	/**
	 * Method that returns the number of tuples a table holds
	 * @param conn
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public static int getTableSize(Connection conn, String table) throws Exception {
		
		Statement statement;//SQL statement                     
		try {
			statement = conn.createStatement();
			
			ResultSet resultSet = statement.executeQuery("SELECT COUNT(*)FROM "+ table);			
			
			if(resultSet.next()) {
				return Integer.parseInt(resultSet.getString(1));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		throw new Exception("Unable to get Table '"+table+"' size!");		
	}
	
	/**
	 * Method that gets the number of distinct values that appear in the table for some attributes
	 * @param conn
	 * @param attributes
	 * @param table
	 * @return
	 * @throws Exception 
	 */
	public static int getNumberOfDistinctValues(Connection conn, ArrayList<String> attributes, String table) throws Exception {
		//get anf format the arraylist to be used in the query
		String att = attributes.toString();
		att = att.substring(1,att.length()-1);
		
		String query = "SELECT count(*) FROM (SELECT count(*) FROM " + table
				+ " GROUP BY "+ att +") as X";
		
		Statement statement;//SQL statement                     
		try {
			statement = conn.createStatement();
			
			ResultSet resultSet = statement.executeQuery(query);			
			
			if(resultSet.next()) {
				return Integer.parseInt(resultSet.getString(1));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		throw new Exception("Unable to get number of distinct values ("+ att +") from table '"+table+"' size!");
	}
	
	/**
	 * Method that estimates the size of table resulting from joining 2 tables
	 * @param conn
	 * @param table1
	 * @param table2
	 * @return int size of table
	 * @throws Exception
	 */
	public static int estimatedJoinSize(Connection conn, String table1, String table2) throws Exception {
		
		ArrayList<String> intersection = AttributesIntersection(getAttributes(conn, table1), getAttributes(conn, table2));
		
		if(intersection.isEmpty()) {
			System.out.println("case 1:");
			return getTableSize(conn,table1) * getTableSize(conn,table1);
		}else if(intersection.containsAll(getPrimaryKey(conn, table1))) {
			System.out.println("case 2:");
			return getTableSize(conn,table2);
		}else if(intersection.containsAll(getPrimaryKey(conn, table2))) {
			System.out.println("case 2:");
			return getTableSize(conn,table1);
		}else {
			System.out.println("case 3:");
			return Math.min(((getTableSize(conn, table1)*getTableSize(conn, table2)) / getNumberOfDistinctValues(conn, intersection, table1)),
					        ((getTableSize(conn, table1)*getTableSize(conn, table2)) / getNumberOfDistinctValues(conn, intersection, table2)));
		}		
	}
	
	/**
	 * MEthod that executes the join statement and returns it's actual size
	 * @param conn
	 * @param table1
	 * @param table2
	 * @return
	 * @throws Exception
	 */
	public static int actualJoinSize(Connection conn, String table1, String table2) throws Exception {
		
		Statement statement;//SQL statement                     
		try {
			statement = conn.createStatement();
			
			ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) "
														+ "FROM (SELECT * FROM "
														+ table1+" NATURAL JOIN " + table2 + " ) AS X");				
			if(resultSet.next()) {
				return Integer.parseInt(resultSet.getString(1));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new Exception("Unable to get actual join size of tables '"+table1+"' and"+ table2 + "'");
	}
}

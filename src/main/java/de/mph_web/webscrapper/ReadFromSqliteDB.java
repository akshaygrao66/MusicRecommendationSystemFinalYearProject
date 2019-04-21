package de.mph_web.webscrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReadFromSqliteDB {

	public static void main(String[] args) {
		Connection conn=null;
		try
		{
			Class.forName("org.sqlite.JDBC");
			conn=DriverManager.getConnection("jdbc:sqlite:D:\\projectdatabases\\MUSIXMATCH\\mxm_dataset.db");
			System.out.println("SQlite Musixmatch Connected");
		}
		catch(Exception e)
		{
			System.out.println("Musixmatch SQLite failed");
		}
		String query = "SELECT * from lyrics where track_id=?";
		try
		{
			// create the preparedstatement and add the criteria
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1,"TRAAAAV128F421A320");

			// process the results
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				System.out.println("No records found");
			}
		}	
		catch(Exception e)
		{
			System.out.println("Error executing query"+e.getMessage());
		}
	}
}

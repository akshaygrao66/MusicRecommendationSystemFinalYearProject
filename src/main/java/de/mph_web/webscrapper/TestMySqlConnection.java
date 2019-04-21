package de.mph_web.webscrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestMySqlConnection {
	static Connection connsqlite=null;
	static Connection conmysql=null;
	public static void main(String[] args) throws NumberFormatException, SQLException { 
		if(!establishMySqlConnectionWithNewMusixMatchDB() || !establishSqliteConnectionWithMusixmatch()) {
			return;
		}
		transferFromOldDatabaseToNewDatabase("TRAHYMJ128E0787801");
	}
	private static boolean transferFromOldDatabaseToNewDatabase(String msdTrackID) {
		
		String query = "SELECT * from lyrics where track_id=?";
		try
		{
			// create the preparedstatement and add the criteria
			PreparedStatement ps = connsqlite.prepareStatement(query);
			ps.setString(1,msdTrackID);

			// process the results
			ResultSet rs = ps.executeQuery();
			System.out.println(rs);
			while(rs.next()) {
				int mxmId=rs.getInt("mxm_tid");
				String word=rs.getString("word");
				int count=rs.getInt("count");
				if(writeToMySQLMusixMatchDB(msdTrackID, mxmId, word, count)==false) {
					return false;
				}
			}
		}	
		catch(Exception e)
		{
			System.out.println("Error executing query"+e.getMessage());
		}
		return true;
	}
	public static boolean establishMySqlConnectionWithNewMusixMatchDB(){
		try{  
			conmysql=DriverManager.getConnection("jdbc:mysql://localhost:3306/musixmatch_new","root","root");  
			if(conmysql!=null)
			{
				System.out.println("Connected to mysql");
			}
		}catch(Exception e){ System.out.println(e); return false;}
		return true;
	}
	public static boolean establishSqliteConnectionWithMusixmatch(){
		try{
			Class.forName("org.sqlite.JDBC");
			connsqlite=DriverManager.getConnection("jdbc:sqlite:D:\\projectdatabases\\MUSIXMATCH\\mxm_dataset.db");
			System.out.println("SQlite Musixmatch Connected");
			return true;
		}
		catch(Exception e){
			System.out.println("Musixmatch SQLite failed");
		}
		return false;
	}
	private static boolean writeToMySQLMusixMatchDB(String msdTrackID,int musixMatchID,String word,int count) throws SQLException{
		String insertTableSQL = "INSERT INTO lyrics"
				+ "(track_id,mxm_tid,word,count) VALUES"
				+ "(?,?,?,?)";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conmysql.prepareStatement(insertTableSQL);
		} catch (SQLException e) {
			return false;
		}

		preparedStatement.setString(1,msdTrackID);
		preparedStatement.setInt(2,musixMatchID);
		preparedStatement.setString(3, word);
		preparedStatement.setInt(4, count);
		preparedStatement.executeUpdate();
		return true;
	}
}


package de.finalyearproject.preprocessing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
public class GetLyricsFromMM {

	
	static Connection connsqlite=null;
	static Connection conmysql=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	final static String MSD_DATASET_SOURCE="D:\\projectdatabases\\MSD\\MillionSongSubset\\data";
	public static void main(String[] args) {
		HashMap<String ,Integer> wordCountMapPerSong = new HashMap<String ,Integer>();
		try {
			if(!establishMySqlConnectionWithNewMusixMatchDB()) {
				System.out.println("MYSQL connection failed");
				return;
			}
			if(!establishSqliteConnectionWithMusixmatch()){
				System.out.println("SQLite connection failed");
				return;
			}	
			
			LyricsModel m=readLyricsFromMM("TRBFQPI128F4280CDA",wordCountMapPerSong);
			System.out.println("===========>"+m);
			System.out.println(m.getMap().size());
		}catch(Exception e ) {
			System.out.println(e);
		}
	}
	
	//functions...
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
	
	public static LyricsModel readLyricsFromMM(String filename,HashMap<String ,Integer> wordCountMapPerSong){
		//System.out.println("Checking Musixmatch for song"+filename);
		wordCountMapPerSong.clear();
		LyricsModel obj = new LyricsModel();
		
		
		String query = "SELECT * from lyrics where track_id=?";
		try{
			// create the preparedstatement and add the criteria
			PreparedStatement ps = conmysql.prepareStatement(query);
			ps.setString(1,filename);

			// process the results
			ResultSet rs = ps.executeQuery();
			int countW = 0;
			
			while(rs.next()) {
				String mxId = rs.getString(3);
				String word = rs.getString(4);
				int count = rs.getInt(5);
				
				//System.out.println(mxId+":"+word+":"+count);
				//hp.clear();
				obj.setMxId(mxId);				
				wordCountMapPerSong.put(word, count);
				
				//System.out.println(++countW);
				
			}
			obj.setMap(wordCountMapPerSong);			
			return obj;
		}	
		catch(Exception e){
			System.out.println("Error executing query"+e.getMessage());
		}
		return obj;
	}
	//functions..

}

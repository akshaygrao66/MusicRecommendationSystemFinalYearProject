package de.finalyearproject.preprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StoreAllAttributesIntoDB {

	static FileWriter logger=null;
	static BufferedWriter logwriter = new BufferedWriter(returnlogger());
	static Connection conmysql=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	final static String MSD_DATASET_SOURCE="D:\\projectdatabases\\MSD\\MillionSongSubset\\data";

	public static void main(String[] args) throws IOException {
		if(!establishMySqlConnectionWithNewMusixMatchDB()) {
			System.out.println("MYSQL connection failed");
			return;
		}

		GetAttributeFromMSD getAttributesFromMSD;
		ArrayList<String> allMSDTrackIDs=null;
		try {
			allMSDTrackIDs=getAllUniqueTrackIDs();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int missed=3,stored=2040;
		boolean enter=false;
		String startPoint="TRBDMAU128F935960B";
		for(String eachMSDTrack:allMSDTrackIDs) {
			if(enter) {
				PreparedStatement prepLyrics;
				try {
					prepLyrics = conmysql.prepareStatement("select score from lyrics_score where track_id=?");
					prepLyrics.setString(1, eachMSDTrack);
					ResultSet res=prepLyrics.executeQuery();
					Double lyricsScore=0.0;
					if(res.next()) {
						lyricsScore=res.getDouble("score");
					}
					else {
						System.out.println("Error in fetching lyrics score...in track"+eachMSDTrack);
						logwriter.write("Error in fetching lyrics score...in track"+eachMSDTrack);
						missed++;
						continue;
					}

					prepLyrics = conmysql.prepareStatement("select mxm_tid from lyrics where track_id=?");
					prepLyrics.setString(1, eachMSDTrack);
					res=prepLyrics.executeQuery();
					int mxm_tid=0;
					if(res.next()) {
						mxm_tid=res.getInt("mxm_tid");
					}
					else{
						System.out.println("Error in musixmatch ID...in track"+eachMSDTrack);
						logwriter.write("Error in musixmatch ID...in track"+eachMSDTrack);
						missed++;
						continue;
					}

					getAttributesFromMSD=new GetAttributeFromMSD(eachMSDTrack);

					PreparedStatement prepInsert=conmysql.prepareStatement("INSERT INTO `musixmatch_new`.`allattributes` (`track_id`, `mxm_tid`, `tempo`, `mode_confidence`, `duration`, `loudness`, `song_hottness`, `year`, `artist_familiarity`, `artist_name`, `artist_hottness`, `song_title`, `lyrics_score`) VALUES (?, ? , ? , ? , ? , ? , ? , ? , ? ,? , ?, ?, ?)");
					prepInsert.setString(1,eachMSDTrack );
					prepInsert.setInt(2,mxm_tid);
					prepInsert.setDouble(3, getAttributesFromMSD.getTempo());
					prepInsert.setDouble(4, getAttributesFromMSD.getMode_confidence());
					prepInsert.setDouble(5, getAttributesFromMSD.getDuration());
					prepInsert.setDouble(6, getAttributesFromMSD.getLoudness());
					prepInsert.setDouble(7, getAttributesFromMSD.getSong_hottness());
					prepInsert.setInt(8, Integer.parseInt(getAttributesFromMSD.getYear()));
					prepInsert.setDouble(9, getAttributesFromMSD.getArtist_familiarity());
					prepInsert.setString(10, getAttributesFromMSD.getArtist_name());
					prepInsert.setDouble(11, getAttributesFromMSD.getArtist_hotness());
					prepInsert.setString(12, getAttributesFromMSD.getSong_title());
					prepInsert.setDouble(13, lyricsScore);

					prepInsert.executeUpdate();
					System.out.println("TrackID stored is "+eachMSDTrack+" of number "+stored+" and missed is"+missed);
					logwriter.write("TrackID stored is "+eachMSDTrack+" of number "+stored+" and missed is"+missed);
					stored++;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logwriter.close();
					e.printStackTrace();
				}
			}
			if(eachMSDTrack.equals(startPoint)) {
				enter=true;
			}
		}
		logwriter.close();
	}

	public static ArrayList<String> getAllUniqueTrackIDs() throws SQLException{
		ArrayList<String> trackIdsUnique=new ArrayList<String>();
		trackIdsUnique.clear();
		Statement st1 = conmysql.createStatement();
		ResultSet rs1 = st1.executeQuery("select distinct track_id from lyrics");
		while(rs1.next()) {
			trackIdsUnique.add(rs1.getString(1));
		}
		return trackIdsUnique;
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

	private static Writer returnlogger() {
		try {
			logger=new FileWriter("D:\\projectdatabases\\logWhereDidStoreAllAttributesStop.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}

}

package de.mph_web.webscrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mph_web.webscrapper.JSONModel.Lastfm;

public class ComputeStats {

	static int terminate=0;
	static int musixMatchAPICallCount=0;
	static Connection connsqlite=null;
	static Connection conmysql=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	final static String MSD_DATASET_SOURCE="D:\\projectdatabases\\MSD\\MillionSongSubset\\data";
	static Lastfm value = null;
	static ObjectMapper mapper = new ObjectMapper();
	static FileWriter logger=null;
	static BufferedWriter logwriter = new BufferedWriter(returnlogger());
	static int totalRootCommonSongs=0;
	static int totalSimilarCommonSongs=0;
	public static void main(String[] args) throws IOException, SQLException {
		int extraRootFilesAdded=0;
		int extraSimilarsAdded=0;
		boolean rootPresentInMsD=false;
		boolean rootPresentInMusixMatch=false;
		boolean similarPresentInMSD=false;
		boolean similarPresentInMusixMatch=false;
		File lastfmDirectory=new File(LASTFM_DATASET_SOURCE);
		Set<String> hash_Set = new HashSet<String>(); 


		if(!establishMySqlConnectionWithNewMusixMatchDB()) {
			System.out.println("MYSQL connection failed");
			return;
		}
		if(!establishSqliteConnectionWithMusixmatch()){
			System.out.println("SQLite connection failed");
			return;
		}
		for(File lastfmRootFile:lastfmDirectory.listFiles()){
			String lastfmRootFileName=lastfmRootFile.getName().replaceFirst("[.][^.]+$", "");
			if(connsqlite!=null){
				rootPresentInMsD=checkMSD(lastfmRootFile);
				rootPresentInMusixMatch=checkMusixMatch(lastfmRootFileName);
				if(rootPresentInMsD && rootPresentInMusixMatch){
					hash_Set.add(lastfmRootFileName);
					totalRootCommonSongs++;
					value = mapper.readValue(new File(lastfmDirectory,lastfmRootFile.getName()), Lastfm.class);
					String filesimilar=null;
					if(value.getSimilars().length>0)
					{
						for(int j=0;j<value.getSimilars().length;j++)
						{
							filesimilar = (String)value.getSimilars()[j][0];
							filesimilar=filesimilar.replaceFirst("[.][^.]+$", "");
							if(checkSimilarMSD(filesimilar)&& (checkMusixMatch(filesimilar)))
							{
								hash_Set.add(filesimilar);
								totalSimilarCommonSongs++;
							}
						}
					}
				}
			}
		}
		System.out.println("Total root common songs present is "+totalRootCommonSongs+" and similar common present is "+totalSimilarCommonSongs);
		System.out.println("Total unique songs present in both musixmatch and MSD is"+hash_Set.size());
		logwriter.write("Total root common songs present is "+totalRootCommonSongs+" and similar common present is "+totalSimilarCommonSongs+"\n");
		logwriter.write("Total unique songs present in both musixmatch and MSD is"+hash_Set.size());
		logwriter.close();
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
			logger=new FileWriter("D:\\projectdatabases\\statisticsAfterDataMigration.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
	public static boolean checkMSD(File f){
		String filenameWithoutExtension=f.getName().replaceFirst("[.][^.]+$", "");
		File temp=new File(MSD_DATASET_SOURCE+"\\"+filenameWithoutExtension+".h5");
		//		System.out.println("Check file at"+temp.getAbsolutePath());
		return temp.exists();
	}
	public static boolean checkSimilarMSD(String trackID){
		File temp=new File(MSD_DATASET_SOURCE+"\\"+trackID+".h5");
		//System.out.println("Check similar file in MSD at"+temp.getAbsolutePath());
		return temp.exists();
	}
	public static boolean checkMusixMatch(String filename){
		System.out.println("Checking Musixmatch for song"+filename);
		String query = "SELECT * from lyrics where track_id=? limit 10";
		try{
			// create the preparedstatement and add the criteria
			PreparedStatement ps = conmysql.prepareStatement(query);
			ps.setString(1,filename);

			// process the results
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				return false;
			}
			return true;
		}	
		catch(Exception e){
			System.out.println("Error executing query"+e.getMessage());
		}
		return false;
	}
}

package de.finalyearproject.musicrecommendation.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mph_web.webscrapper.JSONModel.Lastfm;
import de.mph_web.webscrapper.JSONModel.MusixMatchResponseRoot;


public class CreateNewDatabaseForRNN {
	static int terminate=0;
	static int musixMatchAPICallCount=0;
	static Connection connsqlite=null;
	static Connection conmysql=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	final static String MSD_DATASET_SOURCE="D:\\projectdatabases\\MSD\\MillionSongSubset\\data";
	static List<String> wordlist=null;
	static HashMap<String, String> msdMusixMatchMap=new HashMap<String, String>();
	static Lastfm value = null;
	static ObjectMapper mapper = new ObjectMapper();
	static FileWriter logger=null;
	static BufferedWriter logwriter = new BufferedWriter(returnlogger());
	public static void main(String[] args) throws IOException, SQLException {
		int extraRootFilesAdded=0;
		int extraSimilarsAdded=0;
		boolean rootPresentInMsD=false;
		boolean rootPresentInMusixMatch=false;
		boolean similarPresentInMSD=false;
		boolean similarPresentInMusixMatch=false;
		File lastfmDirectory=new File(LASTFM_DATASET_SOURCE);



		readWordsFromMusixMatchAndCacheIt();
		readMSDAndMusixMatchMappingAndCacheIt();
		if(!establishMySqlConnectionWithNewMusixMatchDB()) {
			System.out.println("MYSQL connection failed");
			return;
		}
		if(!establishSqliteConnectionWithMusixmatch()){
			System.out.println("SQLite connection failed");
			return;
		}
		boolean g=false;;
		for(File lastfmRootFile:lastfmDirectory.listFiles()){
			String lastfmRootFileName=lastfmRootFile.getName().replaceFirst("[.][^.]+$", "");
			if(lastfmRootFileName.equals("TRBIJMU12903CF892B")) {
				g=true;
			}
			if(g)
			{
				if(connsqlite!=null){
					rootPresentInMsD=checkMSD(lastfmRootFile);
					rootPresentInMusixMatch=checkMusixMatch(lastfmRootFileName);
					if(rootPresentInMsD && !rootPresentInMusixMatch){
						if(insertNewSongLyricsIntoMusixMatchDB(lastfmRootFileName)) {
							extraRootFilesAdded++;
							System.out.println("Inserted lyrics count of MSD "+lastfmRootFile.getName()+" to database");
						}
						if(terminate==0)
						{
							value = mapper.readValue(new File(lastfmDirectory,lastfmRootFile.getName()), Lastfm.class);
							String filesimilar=null;
							if(value.getSimilars().length>0)
							{
								for(int j=0;j<value.getSimilars().length;j++)
								{
									filesimilar = (String)value.getSimilars()[j][0];
									filesimilar=filesimilar.replaceFirst("[.][^.]+$", "");
									if(checkSimilarMSD(filesimilar)&& (!checkMusixMatch(filesimilar)))
									{
										if(insertNewSongLyricsIntoMusixMatchDB(filesimilar)) {
											System.out.println("Similar file lyrics count added for MSD file "+filesimilar);
											extraSimilarsAdded++;
										}
										if(terminate==1) {
											System.out.println("Failure occurred at similar file "+filesimilar+" under root file "+lastfmRootFileName);
											String fileContent="Failure occurred at similar file "+filesimilar+" under root file "+lastfmRootFileName;
											logwriter.write(fileContent);
											System.out.println("Root added from API is "+extraRootFilesAdded);
											System.out.println("Similars added from API is "+extraSimilarsAdded);
											logwriter.write("Root added from API is "+extraRootFilesAdded);
											logwriter.write("Similars added from API is "+extraSimilarsAdded);
											logwriter.close();
											return;
										}
									}
									else if(checkSimilarMSD(filesimilar)&& checkMusixMatch(filesimilar)){
										if(transferFromOldDatabaseToNewDatabase(filesimilar)) {
											System.out.println("Transferred lyrics count of similar file "+filesimilar+" to new database");
										}
									}
								}
							}
						}
						else {
							System.out.println("Failure occurred at root file "+lastfmRootFileName);
							String fileContent="Failure occurred at root file "+lastfmRootFileName;
							System.out.println("Root added from API is "+extraRootFilesAdded);
							System.out.println("Similars added from API is "+extraSimilarsAdded);
							logwriter.write("Root added from API is "+extraRootFilesAdded);
							logwriter.write("Similars added from API is "+extraSimilarsAdded);
							logwriter.write(fileContent);
							logwriter.close();
							return;
						}
					}
					else if(rootPresentInMsD && rootPresentInMusixMatch) {
						if(transferFromOldDatabaseToNewDatabase(lastfmRootFileName)) {
							System.out.println("Transferred lyrics count of root file "+lastfmRootFileName+" to new database");
						}
					}
				}
			}
		}
		System.out.println("Root added from API is "+extraRootFilesAdded);
		System.out.println("Similars added from API is "+extraSimilarsAdded);
		logger.write("Root added from API is "+extraRootFilesAdded);
		logger.write("Similars added from API is "+extraSimilarsAdded);
		logger.close();
	}

	private static Writer returnlogger() {
		try {
			logger=new FileWriter("D:\\projectdatabases\\logofwherexecutionstopped.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
	private static boolean transferFromOldDatabaseToNewDatabase(String msdTrackID) {
		if(!checkDuplicateInMySQL(msdTrackID))
		{
			String query = "SELECT * from lyrics where track_id=?";
			try
			{
				// create the preparedstatement and add the criteria
				PreparedStatement ps = connsqlite.prepareStatement(query);
				ps.setString(1,msdTrackID);

				// process the results
				ResultSet rs = ps.executeQuery();
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
		}
		else {
			return false;
		}
		return true;
	}
	public static boolean checkDuplicateInMySQL(String msdTrackID)
	{
		String query = "SELECT * from lyrics where track_id=?";
		try
		{
			// create the preparedstatement and add the criteria
			PreparedStatement ps = conmysql.prepareStatement(query);
			ps.setString(1,msdTrackID);

			// process the results
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				return false;
			}
			return true;
		}	
		catch(Exception e)
		{
			System.out.println("Error executing query"+e.getMessage());
		}
		return true;
	}
	public static boolean insertNewSongLyricsIntoMusixMatchDB(String msdTrackID) throws SQLException, IOException{
		String lyrics=null;
		String musixMatchID=checkMappingWithMusixMatch(msdTrackID);
		if(musixMatchID!=null){
			lyrics=callMusixMatchAPI(musixMatchID);
			if(lyrics!=null){
				HashMap<String,Integer> lyricsWordCountMap=createLyricsWordCounts(musixMatchID,lyrics);
				if(lyricsWordCountMap!=null){
					return writeMappingsToDatabase(msdTrackID,musixMatchID,lyricsWordCountMap);
				}
				else {
					System.out.println("Lyrics word count error causing termination");
					logwriter.write("Lyrics word count error causing termination");
					terminate=1;
				}
			}
			else {
				System.out.println("Lyrics null possibly because API didn't recognise song"+musixMatchID);
				//logwriter.write("Lyrics null possibly because API didn't recognise song"+musixMatchID);
				return false;
			}
		}
		else {
			System.out.println("No mapping found with musixmatch for msd ID"+msdTrackID);
			return false;
		}
		return false;
	}
	private static boolean writeMappingsToDatabase(String msdTrackID, String musixMatchID,HashMap<String, Integer> lyricsWordCountMap) throws SQLException, IOException {

		if(!checkDuplicateInMySQL(msdTrackID))
		{
			for (Entry<String, Integer> entry : lyricsWordCountMap.entrySet()) {
				if(writeToMySQLMusixMatchDB(msdTrackID, Integer.parseInt(musixMatchID), entry.getKey(), entry.getValue())==false) {
					System.out.println("Write error to SQLite database error vausing termination");
					//logwriter.write("Write error to SQLite database error vausing termination");
					return false;
				}
			}
		}
		return true;
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
	private static HashMap<String,Integer> createLyricsWordCounts(String musixMatchID,String lyrics) {
		String[] arrlyrics=wordifyLyrics(lyrics);
		HashMap<String,Integer> lyricsWordCountMap=findLyricsWordCountMap(arrlyrics);
		return lyricsWordCountMap;
	}
	private static HashMap<String, Integer> findLyricsWordCountMap(String[] arrlyrics) {
		HashMap<String,Integer> map=new HashMap<String, Integer>();
		for(String a:arrlyrics) {
			if(wordlist.contains(a)){
				if(map.get(a) == null){
					map.put(a, 1);
				}
				else{
					Integer v=map.get(a).intValue();
					map.put(a, v+1);
				}
			}
		}
		return map;
	}
	private static String[] wordifyLyrics(String lyrics) {
		//String cutstring="******* This Lyrics is NOT for Commercial use *******";
		//lyrics=lyrics.substring(0,lyrics.length()-cutstring.length());
		String[] arrlyrics=lyrics.split(" ");
		for(int u=0;u<arrlyrics.length;u++)
		{
			arrlyrics[u]=arrlyrics[u].toLowerCase();
		}
		return arrlyrics;
	}
	private static String callMusixMatchAPI(String musixMatchID) throws SQLException, IOException {
		String lyrics=null;
		if(musixMatchAPICallCount++<1900)
		{
			System.out.println("API call number=======================>"+musixMatchAPICallCount);
			try {
				URL url = new URL("https://api.musixmatch.com/ws/1.1/track.lyrics.get?format=json&callback=callback&track_id="+musixMatchID+"&apikey=474c3dc8f7d5081a86a6fa70a1b02569s");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				if(conn.getResponseCode()==401)
				{
					terminate=1;
					System.out.println("API call limit exceeded causing termination");
					logger.write("API call limit exceeded causing termination");
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String output;
				String out="";
				while ((output = br.readLine()) != null) {
					out=out+output;
				}
				lyrics=readLyricsWithMusixMatchAPI(out);
				conn.disconnect();

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
		}
		else
		{
			terminate=1;
			System.out.println("API call limit exceeded detected by code causing termination");
			logger.write("API call limit exceeded detected by codecausing termination");
		}
		return lyrics;
	}
	public static String readLyricsWithMusixMatchAPI(String inputjson) throws JsonParseException, JsonMappingException, IOException, SQLException
	{
		ObjectMapper mapper = new ObjectMapper();
		MusixMatchResponseRoot apiResponseObject=null;

		apiResponseObject=mapper.readValue(inputjson, MusixMatchResponseRoot.class);
		//		System.out.println("Lyrics retreived from MusixMatch API is\n========>\n"+apiResponseObject.getLyrics_body());
		return apiResponseObject.getLyrics_body();
	}
	public static String checkMappingWithMusixMatch(String msdTrackID){
		if(!msdMusixMatchMap.isEmpty())
		{
			if(msdMusixMatchMap.get(msdTrackID)!=null)
			{
				return msdMusixMatchMap.get(msdTrackID);
			}
		}
		return null;
	}
	public static void readMSDAndMusixMatchMappingAndCacheIt() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("D:\\projectdatabases\\MUSIXMATCH\\mxm_779k_matches.txt"));
		StringBuilder stringBuilder = new StringBuilder();
		char[] buffer = new char[10];
		while (reader.read(buffer) != -1) {
			stringBuilder.append(new String(buffer));
			buffer = new char[10];
		}
		reader.close();

		String val = stringBuilder.toString();

		val=val.replace("\n", "<SEP>").replace("\r", "<SEP>");
		String[] msdMusixMatchMappingarr=val.split("<SEP>");
		boolean flag=true;
		String key=null;
		String value=null;
		for(int j=0;j<msdMusixMatchMappingarr.length;j=j+3)
		{
			if(flag)
			{
				key=msdMusixMatchMappingarr[j];
			}
			else
			{
				value=msdMusixMatchMappingarr[j];
			}
			msdMusixMatchMap.put(key, value);
			flag=!flag;
		}
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
		//System.out.println("Checking Musixmatch for song"+filename);
		String query = "SELECT * from lyrics where track_id=?";
		try{
			// create the preparedstatement and add the criteria
			PreparedStatement ps = connsqlite.prepareStatement(query);
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
	public static void readWordsFromMusixMatchAndCacheIt() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader("D:\\projectdatabases\\MUSIXMATCH\\mxm_train_word2.txt"));
		StringBuilder stringBuilder = new StringBuilder();
		char[] buffer = new char[10];
		while (reader.read(buffer) != -1) {
			stringBuilder.append(new String(buffer));
			buffer = new char[10];
		}
		reader.close();

		String val = stringBuilder.toString();
		String[] arr=val.split(",");
		
		
		
		for(int u=0;u<arr.length;u++){
			arr[u]=arr[u].toLowerCase();
		}
		wordlist = Arrays.asList(arr);
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
}

package de.finalyearproject.preprocessing;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
public class StoreLyricsScoringIntoDB {
	static ArrayList<String> trackIdsUnique = new ArrayList<String>();
	static ArrayList<String> Keywords = new ArrayList<String>();
	static ArrayList<Integer> KeywordsCounts = new ArrayList<Integer>();
	
	static ArrayList<Integer> KeywordsConsolidatedCounts = new ArrayList<Integer>();
	static FileWriter logger=null;
	static BufferedWriter logwriter = new BufferedWriter(returnlogger());
	static Connection conmysql=null;
	public static boolean establishMySqlConnectionWithNewMusixMatchDB(){
		try{  
			conmysql=DriverManager.getConnection("jdbc:mysql://localhost:3306/musixmatch_new","root","root");  
			if(conmysql!=null){
				System.out.println("Connected to mysql");
			}
		}
		catch(Exception e){
			System.out.println(e);
			return false;
		}
		return true;
	}
	public static void main(String[] args) throws IOException {
		int i=0;
		HashSet<String> skippedWords=new HashSet<String>();
		//Hashmap stores trackID and lyrics_score mappings
		HashMap<String,Double> lyricsScorePerTrack = new HashMap<String,Double>();
		//HashMap stores word and IDF mapping
		HashMap<String,Double> wordIdfMap = new HashMap<String,Double>();
		try {
			if(!establishMySqlConnectionWithNewMusixMatchDB()) {
				System.out.println("MYSQL connection failed");
				return;
			}
			//Get all Unique TrackIDs
			trackIdsUnique=new ArrayList<String>();
			trackIdsUnique.clear();
			Statement st1 = conmysql.createStatement();
			ResultSet rs1 = st1.executeQuery("select distinct track_id from lyrics");
			while(rs1.next()) {
				trackIdsUnique.add(rs1.getString(1));
			}
			lyricsScorePerTrack.clear();
			
			wordIdfMap.clear();
			//Store Word and IDF mappings into hashmap
			ResultSet rs3 = st1.executeQuery("select * from unique_words_document_count");
			while(rs3.next()) {
				wordIdfMap.put(rs3.getString(2),Math.log(rs3.getFloat(4)));
			}
			System.out.println(trackIdsUnique.size());
			//For each trackID find the lyrics Scoring
			for(i=581;i<trackIdsUnique.size();i++) {
				if(i!=0 && i%4==0) {
					storeToDb(lyricsScorePerTrack);
					lyricsScorePerTrack.clear();
					logwriter.write("================>First "+i+" values inserted into Database\n");
					System.out.println("================>First "+i+" values inserted into Database");
				}
				ArrayList<String> allWordsPerTrack = getWords(trackIdsUnique.get(i));
				HashMap<String,Double> wordWeightMapping = new HashMap<String,Double>();
				//For each word in the track do..
				for(int j=0;j<allWordsPerTrack.size();j++) {
					float tfreq = getTermFrequency(trackIdsUnique.get(i),allWordsPerTrack.get(j));
					if(!wordIdfMap.containsKey(allWordsPerTrack.get(j))) {
						skippedWords.add(allWordsPerTrack.get(j));
						logwriter.write("===>Skipped word "+allWordsPerTrack.get(j)+" in trackID:"+trackIdsUnique.get(i)+"\n");
						System.out.println("===>Skipped word "+allWordsPerTrack.get(j)+" in trackID:"+trackIdsUnique.get(i));
						continue;
					}
					else {
						double idf = wordIdfMap.get(allWordsPerTrack.get(j));
						double weight = tfreq * idf;
						wordWeightMapping.put(allWordsPerTrack.get(j), weight);
					}
				}
				//Get word count and find lyric score for each trackID
				double sum =  0.0d;
				for(int k=0;k<wordWeightMapping.size();k++) {
					int wordCount = getWordCount(trackIdsUnique.get(i),allWordsPerTrack.get(k));
					if(wordWeightMapping.containsKey(allWordsPerTrack.get(k))) {
						sum += wordCount * wordWeightMapping.get(allWordsPerTrack.get(k));
					}
					
				}
				lyricsScorePerTrack.put(trackIdsUnique.get(i), sum);
				
				sum = 0.0d;
				allWordsPerTrack.clear();
				wordWeightMapping.clear();
				System.out.println(lyricsScorePerTrack);
			}
			logwriter.write("The skipped word partial list is:\n"+skippedWords);
			storeToDb(lyricsScorePerTrack);
		}catch(Exception e) {
			logwriter.write("I stopped at 'i' value: "+i+" and trackID value: "+trackIdsUnique.get(i));
			System.out.println("I stopped at 'i' value: "+i+" and trackID value: "+trackIdsUnique.get(i));
			System.out.println(e);
			logwriter.close();
		}
		logwriter.close();
	}
	//
	static ArrayList<String> allWordsPerTrack = new ArrayList<String>();
	static float tf = 0.0f;
	public static ArrayList<String> getWords(String trackId) {
		try {
		allWordsPerTrack.clear();
		Statement st4 = conmysql.createStatement();
		ResultSet rs4 = st4.executeQuery("select word from lyrics where track_id='"+trackId+"'");
		
		while(rs4.next()) {
			allWordsPerTrack.add(rs4.getString(1))	;
		}
		}catch(Exception e) {
			System.out.println(e);
		}
		
		return allWordsPerTrack;
	}
	
	public static float getTermFrequency(String trackId,String word) {
		try {
			float count = 0;
			Statement st4 = conmysql.createStatement();
			ResultSet rs4 = st4.executeQuery("select count from lyrics where track_id='"+trackId+"' and word='"+word+"'");	
			while(rs4.next()) {
			  count = (float)rs4.getInt(1);	
			}			
			Statement st5 = conmysql.createStatement();
			ResultSet rs5 = st5.executeQuery("select sum(count) from lyrics where track_id='"+trackId+"'");
			while(rs5.next()) {
				tf = count/rs5.getInt(1);
			}
		}catch(Exception e) {
			System.out.println(e);
		}
		
		return tf;
	}
	
	public static int getWordCount(String trackId,String word) {
		int  count = 0;
		try {
			
			Statement st4 = conmysql.createStatement();
			ResultSet rs4 = st4.executeQuery("select count from lyrics where track_id='"+trackId+"' and word='"+word+"'");	
			while(rs4.next()) {
			  count = rs4.getInt(1);	
			}			
			
		}catch(Exception e) {
			System.out.println(e);
		}
		
		return count;
		
			}
	
	public static void storeToDb(HashMap<String,Double> scores) {
		try {
			
			for(Map.Entry<String,Double> map:scores.entrySet()) {
				
				PreparedStatement ps4 = conmysql.prepareStatement("insert into lyrics_score(track_id,score) values(?,?)");
				ps4.setString(1, map.getKey());
				ps4.setDouble(2, map.getValue());
				ps4.executeUpdate();
				
			}			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	private static Writer returnlogger() {
		try {
			logger=new FileWriter("D:\\projectdatabases\\logForLyricsScore.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
}

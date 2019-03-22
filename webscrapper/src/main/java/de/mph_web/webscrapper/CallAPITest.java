package de.mph_web.webscrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mph_web.webscrapper.JSONModel.MusixMatchResponseRoot;

public class CallAPITest {
	static String trackID="6477168";
	public static void main(String[] args) throws SQLException {
		try {
			
			URL url = new URL("https://api.musixmatch.com/ws/1.1/track.lyrics.get?format=json&callback=callback&track_id="+trackID+"&apikey=790ce5396dda6c305263f2e96fc0c40e");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			String out="";
			while ((output = br.readLine()) != null) {
				out=out+output;
			}
			readLyricsWithMusixMatchAPI(out);
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
	public static void readLyricsWithMusixMatchAPI(String inputjson) throws JsonParseException, JsonMappingException, IOException, SQLException
	{
		ObjectMapper mapper = new ObjectMapper();
		MusixMatchResponseRoot apiResponseObject=null;
		
		apiResponseObject=mapper.readValue(inputjson, MusixMatchResponseRoot.class);
//		System.out.println("Lyrics retreived from MusixMatch API is\n========>\n"+apiResponseObject.getLyrics_body());
		callWordMappingWithMusixMatchDB(apiResponseObject.getLyrics_body());
	}
	public static void callWordMappingWithMusixMatchDB(String lyrics_body) throws IOException, SQLException
	{
		String cutstring="                  ******* This Lyrics is NOT for Commercial use *******";
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
		for(int u=0;u<arr.length;u++)
		{
			arr[u]=arr[u].toLowerCase();
		}
		List<String> wordlist = Arrays.asList(arr);
		lyrics_body=lyrics_body.substring(0,lyrics_body.length()-cutstring.length());
		String[] arrlyrics=lyrics_body.split(" ");
		for(int u=0;u<arrlyrics.length;u++)
		{
			arrlyrics[u]=arrlyrics[u].toLowerCase();
		}
		System.out.println("Lyrics words are=================================================>");
		for(String o:arrlyrics)
		{
			System.out.println(o);
		}
		
		HashMap<String,Integer> map=new HashMap<String, Integer>();
		System.out.println("Common words==============================================================>");
		int y=0;
		 for(String a:arrlyrics)
		 {
			 if(wordlist.contains(a))
			 {
				 System.out.println(a);
				 if(map.get(a) == null)
				 {
					 map.put(a, 1);
					 y++;
				 }
				 else
				 {
					 Integer v=map.get(a).intValue();
					 map.put(a, v+1);
				 }
			 }
		 }
		 Set set = map.entrySet();
	      Iterator iterator = set.iterator();
	      while(iterator.hasNext()) {
	         Map.Entry mentry = (Map.Entry)iterator.next();
	         System.out.println("key is: "+ mentry.getKey() + " & Value is: "+mentry.getValue());
	      }
	      System.out.println("Count is "+y);
	      Connection con=null;
	      try{  
				con=DriverManager.getConnection(  
						"jdbc:mysql://localhost:3306/musixmatch_new","root","root");  
				if(con!=null)
				{
					System.out.println("Connected");
				}
			}catch(Exception e){ System.out.println(e);} 
	      
	      String insertTableSQL = "INSERT INTO lyrics"
	    			+ "(track_id,mxm_tid,word,count) VALUES"
	    			+ "('EXAMPLE',?,?,?)";
	    	PreparedStatement preparedStatement = con.prepareStatement(insertTableSQL);
	    	preparedStatement.setInt(1, Integer.parseInt(trackID));
	    	preparedStatement.setString(2,"EYES");
	    	preparedStatement.setInt(3, 5);
	    	
	    	preparedStatement.executeUpdate();
	    	
	}
}

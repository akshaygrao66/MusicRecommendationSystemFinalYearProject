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

public class ReComputeStats {
	static Connection conmysql=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	static Lastfm value = null;
	static ObjectMapper mapper = new ObjectMapper();
	static FileWriter logger=null;
	static BufferedWriter logwriter = new BufferedWriter(returnlogger());
	public static void main(String[] args) throws IOException, SQLException {
		File lastfmDirectory=new File(LASTFM_DATASET_SOURCE);
		Set<String> hash_Set = new HashSet<String>(); 
		int mapping=0;

		if(!establishMySqlConnectionWithNewMusixMatchDB()) {
			System.out.println("MYSQL connection failed");
			return;
		}
		for(File lastfmRootFile:lastfmDirectory.listFiles()){
			String lastfmRootFileName=lastfmRootFile.getName().replaceFirst("[.][^.]+$", "");
			if(checkMusixMatch(lastfmRootFileName)){
				hash_Set.add(lastfmRootFileName);
				value = mapper.readValue(new File(lastfmDirectory,lastfmRootFile.getName()), Lastfm.class);
				String filesimilar=null;
				if(value.getSimilars().length>0)
				{
					for(int j=0;j<value.getSimilars().length;j++)
					{
						filesimilar = (String)value.getSimilars()[j][0];
						filesimilar=filesimilar.replaceFirst("[.][^.]+$", "");
						if((checkMusixMatch(filesimilar)))
						{
							hash_Set.add(filesimilar);
							mapping++;
						}
					}
				}
			}
		}
		System.out.println("Total pairs availabe is:"+mapping+" and unique songs used are: "+hash_Set.size());
		logwriter.write("Total pairs availabe is:"+mapping+" and unique songs used are: "+hash_Set.size());
		logwriter.close();
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
			logger=new FileWriter("D:\\projectdatabases\\RecomputeStatisticsAfterDataMigrationToAllAttributes.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}

	public static boolean checkMusixMatch(String filename){
		System.out.println("Checking Musixmatch for song"+filename);
		String query = "SELECT * from allattributes where track_id=?";
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

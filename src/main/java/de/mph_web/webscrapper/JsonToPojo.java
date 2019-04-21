package de.mph_web.webscrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mph_web.webscrapper.JSONModel.Lastfm;

public class JsonToPojo {
	static Connection conn=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	final static String MSD_DATASET_SOURCE="D:\\projectdatabases\\MSD\\MillionSongSubset\\data";
	public static void main(String args[]) throws IOException{
		String fileroot=null;
		String filesimilar=null;
		boolean isPresentInMSD=true;
		Double similarity_score=0.0;
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<Lastfm> allValues = new ArrayList<Lastfm>();
		allValues.clear();
		Lastfm value = null;
		File myDir=new File(LASTFM_DATASET_SOURCE);
		int i=0,k=0;
		int presentCount=0;
		int similarfileCount=0;
		int notexistingsimilarfileCount=0;
		int existingsimilarfileCount=0;
		int notPresentCount=0;
		int rootPresentInMusixMatch=0;
		int rootNotPresentInMusixMatch=0;
		int similarsPresentInMusixMatch=0;
		int similarsNotPresentInMusixMatch=0;
		String tempstr;
		boolean rp;
		BufferedWriter similarsPresentInMSDNotInMusix = new BufferedWriter(new FileWriter("D:\\projectdatabases\\similarsPresentInMSDNotInMusix.txt"));
		BufferedWriter rootPresentInMSDNotInMusix = new BufferedWriter(new FileWriter("D:\\projectdatabases\\rootPresentInMSDNotInMusix.txt"));
		int similarsPresentInMSDNotInMusixCount=0;
		int rootPresentInMSDNotInMusixCount=0;
		if(establishConnectionWithMusixmatch())
		{
			for(File f:myDir.listFiles())
			{
				tempstr=f.getName().replaceFirst("[.][^.]+$", "");
				rp=checkMusixMatch(tempstr);
				isPresentInMSD=checkMSD(f);
				if(isPresentInMSD && rp)
				{
					presentCount++;
					rootPresentInMusixMatch++;
					i++;
					System.out.println("Filename is"+f.getName());
					if(i>100000)
					{
						break;
					}
					try {
						value = mapper.readValue(new File(myDir,f.getName()), Lastfm.class);
						allValues.add(value);
						//						System.out.println(value.getArtist());
						//						System.out.println(value.getTitle());
						if(value.getSimilars().length>0)
						{
							for(int j=0;j<value.getSimilars().length;j++)
							{
								similarfileCount++;
								filesimilar=(String)value.getSimilars()[j][0];
								if(checkSimilarMSD(filesimilar))
								{
									existingsimilarfileCount++;
									if(checkMusixMatch(filesimilar))
									{
										similarsPresentInMusixMatch++;
									}
									else
									{
										similarsPresentInMSDNotInMusixCount++;
										similarsPresentInMSDNotInMusix.write(filesimilar+"\n");
										similarsNotPresentInMusixMatch++;
									}
								}
								else
								{
									notexistingsimilarfileCount++;
								}
								if(value.getSimilars()[j][1] instanceof Integer)
								{
									similarity_score=((Integer)value.getSimilars()[j][1]).doubleValue();
									//									System.out.println(filesimilar+"  Similarity scoring is"+similarity_score);
								}
								else if(value.getSimilars()[j][1] instanceof Double)
								{
									similarity_score=(Double)value.getSimilars()[j][1];
									//									System.out.println(filesimilar+"  Similarity scoring is"+similarity_score);
								}
							}
						}

						if(value.getTags().length>0)
						{
							//System.out.println(value.getTags()[0][1]);
						}

						//System.out.println(value.getTimestamp());
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(isPresentInMSD && !rp)
				{
					rootPresentInMSDNotInMusix.write(tempstr+"\n");
					rootPresentInMSDNotInMusixCount++;
				}
				else
				{
					rootNotPresentInMusixMatch++;
					notPresentCount++;
					i--;
					System.out.println("Delete File"+f.getName()+"=========>");
				}
				System.out.println("MSD Present count is"+presentCount+",Not present count is"+notPresentCount);
				System.out.println("Out of totally "+similarfileCount+"similar songs in entire last.fm dataset,"+existingsimilarfileCount+" songs details exist in MSD dataset and"+notexistingsimilarfileCount+" song details doesn't exist in MSD dataset");
				System.out.println("Musix match root files present count is"+rootPresentInMusixMatch+",Not present count is"+rootNotPresentInMusixMatch);
				System.out.println("Out of totally "+similarfileCount+"similar songs in entire last.fm dataset,"+similarsPresentInMusixMatch+" songs details exist in both MSD and MusixMatch dataset and"+similarsNotPresentInMusixMatch+" song details doesn't exist in both MSD and  MusixMatch dataset");
			}
			String fileContent1 = "MSD Present count is"+presentCount+",Not present count is"+notPresentCount+"\n";
			String fileContent2="Out of totally "+similarfileCount+"similar songs in entire last.fm dataset,"+existingsimilarfileCount+" songs details exist in MSD dataset and"+notexistingsimilarfileCount+" song details doesn't exist in MSD dataset\n";
			String fileContent3="Musix match root files present count is"+rootPresentInMusixMatch+",Not present count is"+rootNotPresentInMusixMatch+"\n";
			String fileContent4="Out of totally"+similarfileCount+"similar songs in entire last.fm dataset,"+similarsPresentInMusixMatch+" songs details exist in both MSD and MusixMatch dataset and"+similarsNotPresentInMusixMatch+" song details doesn't exist in both MSD and MusixMatch dataset\n";
			String fileContent5="Root songs present in MSD but not in musixmatch is "+rootPresentInMSDNotInMusixCount+"\n";
			String fileContent6="Similar songs present in MSD but not in musixmatch is "+similarsNotPresentInMusixMatch+"\n";
			BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\projectdatabases\\stats4.txt"));
			writer.write(fileContent1);
			writer.write(fileContent2);
			writer.write(fileContent3);
			writer.write(fileContent4);
			writer.write(fileContent5);
			writer.write(fileContent6);
			writer.close();
			
			
			rootPresentInMSDNotInMusix.write(fileContent5);
			
			similarsPresentInMSDNotInMusix.write(fileContent6);
			rootPresentInMSDNotInMusix.close();
			similarsPresentInMSDNotInMusix.close();
			
			System.out.println("Finally MSD Present count is"+presentCount+",Not present count is"+notPresentCount);
			System.out.println("Finally Out of totally "+similarfileCount+"similar songs in entire last.fm dataset,"+existingsimilarfileCount+" songs details exist in MSD dataset and"+notexistingsimilarfileCount+" song details doesn't exist in MSD dataset");
			System.out.println("Finally Musix match root files present count is"+rootPresentInMusixMatch+",Not present count is"+rootNotPresentInMusixMatch);
			System.out.println("Finally Out of totally "+similarfileCount+"similar songs in entire last.fm dataset,"+similarsPresentInMusixMatch+" songs details exist in both MSD and MusixMatch dataset and"+similarsNotPresentInMusixMatch+" song details doesn't exist in both MSD and MusixMatch dataset");
			System.out.println("Root songs present in MSD but not in musixmatch is "+rootPresentInMSDNotInMusixCount);
			System.out.println("Similar songs present in MSD but not in musixmatch is "+similarsNotPresentInMusixMatch);
			
			
		}

	}
	public static boolean checkMSD(File f)
	{
		String filenameWithoutExtension=f.getName().replaceFirst("[.][^.]+$", "");
		File temp=new File(MSD_DATASET_SOURCE+"\\"+filenameWithoutExtension+".h5");
		//		System.out.println("Check file at"+temp.getAbsolutePath());
		return temp.exists();
	}
	public static boolean checkSimilarMSD(String trackID)
	{
		File temp=new File(MSD_DATASET_SOURCE+"\\"+trackID+".h5");
		System.out.println("Check similar file in MSD at"+temp.getAbsolutePath());
		return temp.exists();
	}
	public static boolean checkMusixMatch(String filename)
	{
		System.out.println("Checking Musixmatch for song"+filename);
		String query = "SELECT * from lyrics where track_id=?";
		try
		{
			// create the preparedstatement and add the criteria
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1,filename);

			// process the results
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				return false;
			}
			return true;
		}	
		catch(Exception e)
		{
			System.out.println("Error executing query"+e.getMessage());
		}
		return false;
	}
	public static boolean establishConnectionWithMusixmatch()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			conn=DriverManager.getConnection("jdbc:sqlite:D:\\projectdatabases\\MUSIXMATCH\\mxm_dataset.db");
			System.out.println("SQlite Musixmatch Connected");
			return true;
		}
		catch(Exception e)
		{
			System.out.println("Musixmatch SQLite failed");
		}
		return false;
	}
}

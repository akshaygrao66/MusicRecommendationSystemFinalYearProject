package de.finalyearproject.firebaseUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.opencsv.CSVReader;

public class ExtractPredictionsAndUpload {
	public static void extractPredictionsFromCSVFile(String filepath) {
		try { 
			// Create an object of filereader 
			// class with CSV file as a parameter. 
			FileReader filereader = new FileReader(filepath); 

			// create csvReader object passing 
			// file reader as a parameter 
			CSVReader csvReader = new CSVReader(filereader); 
			String[] nextRecord; 

			String[] allSongsInCSVFile=null; //All songs has the first entry blank(important note)
			HashMap<String,HashMap<String,Float>> allSongsPredictions=new HashMap<String,HashMap<String,Float>>();
			// we are going to read data line by line
			int i=0;
			FileWriter flog=new FileWriter(new File("D:\\projectdatabases\\logOfCSVFileRead"));
			while ((nextRecord = csvReader.readNext()) != null) { 
				for (String cell : nextRecord) { 
					//					flog.write(cell + "\t");
					//					System.out.print(cell + "\t"); 
				}
				if(i==0) {
					allSongsInCSVFile=nextRecord;
				}
				else{
					HashMap<String,Float> eachSongPrediction=new HashMap<String,Float>();
					String songParent=nextRecord[0];
					for(int k=1;k< nextRecord.length;k++) {
						eachSongPrediction.put(allSongsInCSVFile[k],Float.parseFloat(nextRecord[k]));
					}
					allSongsPredictions.put(songParent, eachSongPrediction);
				}
				i++;
				System.out.println(i);
				//				flog.write("\n");
				//				System.out.println("\n");
			} 
			for(String temp:allSongsInCSVFile) {
				flog.write(temp+" ");
			}
			//			flog.write("\n");
			//			System.out.println(allSongsInCSVFile);
//			System.out.println(allSongsPredictions.get("TRAAABD128F429CF47"));
			serializeHashMap(allSongsPredictions,"D:\\projectdatabases\\serialized Maps\\hashmapofonlyann.ser");
			flog.close();
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	}

	public static void serializeHashMap(HashMap<String,HashMap<String,Float>> allSongsPredictions,String serializeLocation) {

		FileOutputStream fos;
		try {
			System.out.println("Serialization under progress!");
			fos = new FileOutputStream(serializeLocation);

			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(allSongsPredictions);
			oos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Serialization Complete!");
	}

	public static HashMap<String,HashMap<String,Float>> deserializeHashMap(String filepath) {
		try {
			FileInputStream fis = new FileInputStream(filepath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			System.out.println("Extracting phase1");
			HashMap<String,HashMap<String,Float>> hashlist = (HashMap<String,HashMap<String,Float>>) ois.readObject();
			System.out.println("Extracting phase 2");
			ois.close();
			System.out.println("Extraction done");
//			System.out.println("Retrieved map is: "+hashlist.get("TRAAABD128F429CF47"));
			return hashlist;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		extractPredictionsFromCSVFile("D:\\projectdatabases\\Numpy results\\predictedsimilarityscorings(onlyannbased).csv");
//		deserializeHashMap("D:\\projectdatabases\\serialized Maps\\hashmapofmeanofknnandann.ser");
	}
}

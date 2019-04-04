package de.finalyearproject.preprocessing;
import java.io.*;
import  java.util.*;
public class RnnFinal {
	
	
	
	
	public static void main(String []args) {
		
		try {
			ArrayList<String> allTrackIds = new ArrayList<String>();	
			ArrayList<String> allJsonFileNames = new ArrayList<String>();
			ArrayList<String> neighbourTrackIds = new ArrayList<String>();
			allTrackIds.clear();
			allJsonFileNames.clear();
			neighbourTrackIds.clear();
			String jsonFilepath = "D:\\projectdatabases\\LASTFM\\lastfm_subset";//path can be variant
			File file = new File(jsonFilepath);
			String filenames[]=  null;
			if(file.isDirectory()) {
				filenames = file.list();
			}
			
			for(int i=0;i<filenames.length;i++) {
				allJsonFileNames.add(jsonFilepath+"\\"+filenames[i]);
			}
			
			//track id extraction starts..
			for(int i=0;i<allJsonFileNames.size();i++) {
				String fileName = allJsonFileNames.get(i);
				
				String trackId = fileName.substring(fileName.lastIndexOf("\\")+1, fileName.lastIndexOf("."));
				allTrackIds.add(trackId);
			}
			System.out.println(allTrackIds.get(0));
			//track id extraction ends..
		}catch(Exception e) {
			System.out.println(e);
		}
		
	}

}

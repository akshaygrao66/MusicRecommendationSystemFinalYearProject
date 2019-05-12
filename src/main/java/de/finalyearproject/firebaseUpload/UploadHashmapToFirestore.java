package de.finalyearproject.firebaseUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import de.mph_web.webscrapper.JSONModel.Lastfm;


public class UploadHashmapToFirestore {
	
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	static Lastfm value = null;
	static ObjectMapper mapper = new ObjectMapper();
	static File lastfmDirectory=new File(LASTFM_DATASET_SOURCE);

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		FileInputStream serviceAccount = new FileInputStream("D:\\projectdatabases\\key\\finalyearproject-f9cf5-af966b27d309.json");
		GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setProjectId("finalyearproject-f9cf5")
				.build();
		FirebaseApp.initializeApp(options);
		Firestore db = FirestoreClient.getFirestore();
		int stored = 1;
		HashMap<String,HashMap<String,Float>> entireHashMap = ExtractPredictionsAndUpload.deserializeHashMap("D:\\projectdatabases\\serialized Maps\\hashmapofonlyknn.ser");
		for (String eachMSDTrack : entireHashMap.keySet()) {
//			HashMap<String,Float> firebaseEachSongStructure=sortByValueReturnTopTen(entireHashMap.get(eachMSDTrack));
			HashMap<String,Float> firebaseEachSongStructure=returnFirstTenSimilarSongsFromLastFm(eachMSDTrack,entireHashMap);
			HashMap<String,HashMap<String,Float>> firebasePredictionHashmap=new HashMap<String, HashMap<String,Float>>();
			firebasePredictionHashmap.put("TempPredictionMaps", firebaseEachSongStructure);
			ApiFuture<WriteResult> predictedUploadFuture = db.collection("PredictionTemp").document(eachMSDTrack).set(firebasePredictionHashmap);
			System.out.println("Update time of music details for song number: "+stored+" for song: "+eachMSDTrack+" is:" + predictedUploadFuture.get().getUpdateTime());
			firebasePredictionHashmap.clear();
			firebaseEachSongStructure.clear();
			stored++;
		}
	}
	
	public static HashMap<String, Float> returnFirstTenSimilarSongsFromLastFm(String track_id,HashMap<String,HashMap<String,Float>> entireHashMap) throws JsonParseException, JsonMappingException, IOException{
		File lastfmRootFile=new File(track_id+".json");
		value = mapper.readValue(new File(lastfmDirectory,lastfmRootFile.getName()), Lastfm.class);
		HashMap<String,Float> mapOfSongsAndSimilarScoring=new HashMap<String, Float>();
		int count=0;
		for(int j=0;j<value.getSimilars().length && count <10;j++)
		{
			String similarTrackID = (String)value.getSimilars()[j][0];
			Float similarScoring=Float.parseFloat(value.getSimilars()[j][1]+"");
			if(entireHashMap.get(similarTrackID) != null) {
				mapOfSongsAndSimilarScoring.put(similarTrackID, similarScoring);
				count++;
			}
		}
		return mapOfSongsAndSimilarScoring;
	}

	public static HashMap<String, Float> sortByValueReturnTopTen(HashMap<String, Float> hm) 
	{ 
		// Create a list from elements of HashMap 
		List<Map.Entry<String, Float> > list = 
				new LinkedList<Map.Entry<String, Float> >(hm.entrySet()); 

		// Sort the list 
		Collections.sort(list, new Comparator<Map.Entry<String, Float> >() { 
			public int compare(Map.Entry<String, Float> o1,  
					Map.Entry<String, Float> o2) 
			{ 
				return (o2.getValue()).compareTo(o1.getValue()); 
			}
		}); 

		// put data from sorted list to hashmap  
		HashMap<String, Float> temp = new LinkedHashMap<String, Float>(); 
		int i=0;
		for (Map.Entry<String, Float> aa : list) { 
			i++;
			if(i>10) {
				break;
			}
			temp.put(aa.getKey(), aa.getValue()); 
		} 
		return temp;
	}

}

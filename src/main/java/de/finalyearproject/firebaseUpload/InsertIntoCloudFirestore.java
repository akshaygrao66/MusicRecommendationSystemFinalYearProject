package de.finalyearproject.firebaseUpload;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.beanutils.BeanUtils;

//import org.apache.commons.beanutils.BeanUtils;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.core.Tag;

import de.finalyearproject.preprocessing.GetAllAttributesFromDB;
import de.finalyearproject.preprocessing.GetAttributeFromMSD;
import de.finalyearproject.uploadmodel.FirestoreArtistIdModel;
import de.finalyearproject.uploadmodel.FirestoreMusicValueModel;

public class InsertIntoCloudFirestore {
	static FileWriter logger=null;
	static BufferedWriter logwriter = new BufferedWriter(returnlogger());
	static Connection conmysql=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	final static String MSD_DATASET_SOURCE="D:\\projectdatabases\\MSD\\MillionSongSubset\\data";

	public static void main(String[] args) throws IOException, IllegalAccessException {
		FileInputStream serviceAccount = new FileInputStream("D:\\projectdatabases\\key\\finalyearproject-f9cf5-af966b27d309.json");
		GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setProjectId("finalyearproject-f9cf5")
				.build();
		FirebaseApp.initializeApp(options);

		if(!establishMySqlConnectionWithNewMusixMatchDB()) {
			System.out.println("MYSQL connection failed");
			return;
		}

		GetAttributeFromMSD getAttributesFromMSD;
		ArrayList<String> allMSDTrackIDs=null;
		try {
			allMSDTrackIDs=getAllUniqueTrackIDs();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		int missed=0,stored=0;
		boolean enter=true;
		String startPoint="";

		Firestore db = FirestoreClient.getFirestore();
		for(String eachMSDTrack:allMSDTrackIDs) {
			if(enter) {
				GetAllAttributesFromDB attributesFromMsd=null;
				try {
					attributesFromMsd = new GetAllAttributesFromDB(eachMSDTrack);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println(attributesFromMsd);
				try {
					String art_id=attributesFromMsd.getArtist_id();
					if(art_id==null || art_id=="") {
						missed++;
						continue;
					}
					logwriter = new BufferedWriter(returnlogger());
					//Prepare firebase music value model
					FirestoreMusicValueModel musicValueModel=new FirestoreMusicValueModel();
					BeanUtils.copyProperties(musicValueModel, attributesFromMsd);
					musicValueModel.setSequence_number(stored);
					musicValueModel.trimQuotes();
					System.out.println("Music value model of song "+eachMSDTrack+"is: "+musicValueModel);
					//Prepare structure to upload music values to firebase
					Map<String, FirestoreMusicValueModel> firebaseMusicStructure = new HashMap<String, FirestoreMusicValueModel>();
					firebaseMusicStructure.put("FireStoreMusicValueModel", musicValueModel);
					//Write the created music structure to firebase CloudFireStore
					ApiFuture<WriteResult> musicUploadFuture = db.collection("MusicIdNamePairs").document(eachMSDTrack).set(firebaseMusicStructure);
					System.out.println("Update time of music details for song number:"+stored+" for song: "+eachMSDTrack+" is:" + musicUploadFuture.get().getUpdateTime());
					logwriter.write("Update time of music details for song number "+stored+" for song: "+eachMSDTrack+" is:" + musicUploadFuture.get().getUpdateTime()+"\n");
					//Prepare firebase artist upload model
					FirestoreArtistIdModel artistValueModel = new FirestoreArtistIdModel();
					artistValueModel.setArtist_name(attributesFromMsd.getArtist_name());
					artistValueModel.trimQuotes();
					System.out.println("Artist value model of song "+eachMSDTrack+"is: "+artistValueModel);
					//Prepare structure to upload artist values to firebase
					Map<String, FirestoreArtistIdModel> firebaseArtistStructure = new HashMap<String, FirestoreArtistIdModel>();
					firebaseArtistStructure.put("FirestoreArtistIdModel", artistValueModel);
					//Write the created artist structure to firebase CloudFireStore
					ApiFuture<WriteResult> artistUploadFuture = db.collection("ArtistIdNamePairs").document(art_id).set(firebaseArtistStructure);
					System.out.println("Update time of artist details for song number:"+stored+" for song: "+eachMSDTrack+" is:" + artistUploadFuture.get().getUpdateTime());
					logwriter.write("Update time of artist details for song number:"+stored+" for song: "+eachMSDTrack+" is:" + artistUploadFuture.get().getUpdateTime()+"\n");
					logwriter.write("Missed:"+missed+" Stored:"+stored+"\n\n");
					logwriter.close();
					stored++;
				}
				catch(Exception e) {
					missed++;
					logger=new FileWriter("D:\\projectdatabases\\logWhereDidFirebaseUploadStop.txt",true);
					logwriter=new BufferedWriter(logger);
					logwriter.write("Error at song number:"+stored+" for song:"+eachMSDTrack+" and number of songs missed is:"+missed+".Reason being"+e.getLocalizedMessage()+"\n");
					logwriter.close();
				}
			}
			if(eachMSDTrack.equals(startPoint)) {
				enter=true;
			}
		}
	}

	public static boolean checkArtistExists(Firestore db,String artist_id) {
		DocumentReference docIdRef = db.collection("ArtistIdNamePairs").document(artist_id);
		docIdRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			public void onEvent( DocumentSnapshot snapshot, FirestoreException e) {
				if (e != null) {
					System.err.println("Listen failed: " + e);
					return;
				}
				if (snapshot != null && snapshot.exists()) {
					System.out.println("Current data: " + snapshot.getData());
				} else {
					System.out.print("Current data: null");
				}
			}
		});
		return true;
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
			logger=new FileWriter("D:\\projectdatabases\\logWhichSongsWhereUploadedToFireStore.txt",true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
}

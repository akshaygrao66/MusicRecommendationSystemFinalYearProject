package de.finalyearproject.firebaseUpload;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import de.finalyearproject.preprocessing.GetAllAttributesFromDB;
import de.finalyearproject.preprocessing.GetAttributeFromMSD;
import de.finalyearproject.uploadmodel.FirestoreArtistIdModel;
import de.finalyearproject.uploadmodel.FirestoreMusicValueModel;

public class InsertArtistImageToFirebase {
	static FileWriter logger=null;
	static BufferedWriter logwriter = new BufferedWriter(returnlogger());
	static Connection conmysql=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";

	public static void main(String[] args) throws IOException {
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

		int missed=0,stored=0,imageNotFound=0;
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
					
					//Prepare firebase artist upload model
					FirestoreArtistIdModel artistValueModel = new FirestoreArtistIdModel();
					String artistImageUrl=getArtistImageUrlFromAllMusic(attributesFromMsd.getArtist_name());
					if(artistImageUrl==null) {
						artistImageUrl="http://quotepixel.com/images/authors/170w/unknown_quotes.jpg";
						imageNotFound++;
					}
					artistValueModel.setArtist_name(attributesFromMsd.getArtist_name());
					artistValueModel.setUrl1(artistImageUrl);
					artistValueModel.setUrl2(artistImageUrl);
					artistValueModel.trimQuotes();
					System.out.println("Artist value model of song "+eachMSDTrack+"is: "+artistValueModel);
					//Prepare structure to upload artist values to firebase
					Map<String, FirestoreArtistIdModel> firebaseArtistStructure = new HashMap<String, FirestoreArtistIdModel>();
					firebaseArtistStructure.put("FirestoreArtistIdModel", artistValueModel);
					//Write the created artist structure to firebase CloudFireStore
					ApiFuture<WriteResult> artistUploadFuture = db.collection("ArtistIdNamePairs").document(art_id).set(firebaseArtistStructure);
					System.out.println("Update time of artist details for song number:"+stored+" for song: "+eachMSDTrack+" is:" + artistUploadFuture.get().getUpdateTime());
					System.out.println("Missed:"+missed+" Stored:"+stored+"Url not found:"+imageNotFound+"\n");
					logwriter.write("Update time of artist details for song number:"+stored+" for song: "+eachMSDTrack+" is:" + artistUploadFuture.get().getUpdateTime()+"\n");
					logwriter.write("Missed:"+missed+" Stored:"+stored+" Url not found: "+imageNotFound+"\n\n");
					logwriter.close();
					stored++;
				}
				catch(Exception e) {
					missed++;
					logger=new FileWriter("D:\\projectdatabases\\logWhereDidFirebaseArtistImageUrlUploadStop.txt",true);
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
	public static String getArtistImageUrlFromAllMusic(String query) {
		try {
			String url = "https://www.allmusic.com/search/artists/" + query;
			System.out.println("===================>"+url);
			Document document = Jsoup.connect(url).get();

			int missed=0;
			Elements eachArtistContainer=document.select("li.artist");
			for(Element eachArtistDiv:eachArtistContainer) {
				Elements photoDiv=eachArtistDiv.select("div.photo");
				if(photoDiv!=null && photoDiv.size()!=0) {
					for(Element photo:photoDiv) {
						Elements images=photo.select("img");
						for(Element img:images) {
							String imageUrl=img.attr("src");
							System.out.println(imageUrl);
							return imageUrl;
						}
					}
				}
				else {
					missed++;
					if(missed==5) {
						System.out.println("Didn't find any images returning null");
						return null;
					}
				}
			}
		} catch (HttpStatusException e) {
			if(e.getStatusCode()==404) {
				System.out.println("Image not found!");
				return null;
			}
		}
		catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
		return null;
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
			logger=new FileWriter("D:\\projectdatabases\\logWhichArtistImageUrlWereUploadedToFireStore.txt",true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}

}

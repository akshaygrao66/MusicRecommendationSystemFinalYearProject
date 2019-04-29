package de.finalyearproject.firebaseUpload;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.beanutils.BeanUtils;

//import org.apache.commons.beanutils.BeanUtils;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import de.finalyearproject.preprocessing.GetAllAttributesFromDB;
import de.finalyearproject.preprocessing.GetAttributeFromMSD;
import de.finalyearproject.uploadmodel.FirestoreMusicValueModel;

public class InsertIntoCloudFirestore {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, IllegalAccessException, InvocationTargetException {
		FileInputStream serviceAccount = new FileInputStream("D:\\projectdatabases\\key\\finalyearproject-f9cf5-af966b27d309.json");
		GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
		FirebaseOptions options = new FirebaseOptions.Builder()
		    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
		    .setProjectId("finalyearproject-f9cf5")
		    .build();
		FirebaseApp.initializeApp(options);
		final String dname="TRAAABD128F429CF47";
		GetAllAttributesFromDB attributesFromMsd=new GetAllAttributesFromDB(dname);
		Firestore db = FirestoreClient.getFirestore();
		FirestoreMusicValueModel musicValueModel=new FirestoreMusicValueModel();
		BeanUtils.copyProperties(musicValueModel, attributesFromMsd);
		System.out.println(musicValueModel);
		Map<String, FirestoreMusicValueModel> docData = new HashMap<String, FirestoreMusicValueModel>();
		docData.put("FireStoreMusicValueModel", musicValueModel);
		
		// Add a new document (asynchronously) in collection "cities" with id "LA"
		ApiFuture<WriteResult> future = db.collection("MusicIdNamePairs").document("example").set(docData);
		// ...
		// future.get() blocks on response
		System.out.println("Update time : " + future.get().getUpdateTime());
	}

}

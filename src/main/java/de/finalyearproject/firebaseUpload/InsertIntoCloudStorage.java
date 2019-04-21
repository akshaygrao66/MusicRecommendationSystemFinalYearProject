package de.finalyearproject.firebaseUpload;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;




public class InsertIntoCloudStorage {

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
		// TODO Auto-generated method stub
		FileInputStream serviceAccount = new FileInputStream("D:\\projectdatabases\\key\\finalyearproject-f9cf5-af966b27d309.json");

		FirebaseOptions options = null;
		try {
			options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setStorageBucket("finalyearproject-f9cf5.appspot.com")
					.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FirebaseApp.initializeApp(options);
		Bucket bucket = StorageClient.getInstance().bucket();
		System.out.println(bucket.getGeneratedId());
		//bucket.create("blob", "Hello!Iam akshay", contentType, options)
		Storage storage = StorageOptions.getDefaultInstance().getService();
		BlobId blobId = BlobId.of("finalyearproject-f9cf5.appspot.com", "audioSample1");
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/mpeg").build();
//		Blob blob = storage.create(blobInfo, "Hello!".getBytes(UTF_8));
		
		File file = new File("F:\\music\\kannada\\akshay kannada\\[Songs.PK] Raaz 3 - 03 - Rafta Rafta.mp3");
		AudioInputStream in= AudioSystem.getAudioInputStream(file);
		Blob blob = storage.create(blobInfo, in);
		
		
		// Instantiate a Google Cloud Storage client
//		Storage storage = StorageOptions.getDefaultInstance().getService();

		// The name of a bucket, e.g. "my-bucket"
		// String bucketName = "my-bucket";

		// The name of a blob, e.g. "my-blob"
		// String blobName = "my-blob";

		// Select all fields
		// Fields can be selected individually e.g. Storage.BlobField.CACHE_CONTROL
//		Blob blob = storage.get(bucketName, blobName, BlobGetOption.fields(Storage.BlobField.values()));

		// Print blob metadata
		System.out.println("Bucket: " + blob.getBucket());
		System.out.println("CacheControl: " + blob.getCacheControl());
		System.out.println("ComponentCount: " + blob.getComponentCount());
		System.out.println("ContentDisposition: " + blob.getContentDisposition());
		System.out.println("ContentEncoding: " + blob.getContentEncoding());
		System.out.println("ContentLanguage: " + blob.getContentLanguage());
		System.out.println("ContentType: " + blob.getContentType());
		System.out.println("Crc32c: " + blob.getCrc32c());
		System.out.println("Crc32cHexString: " + blob.getCrc32cToHexString());
		System.out.println("ETag: " + blob.getEtag());
		System.out.println("Generation: " + blob.getGeneration());
		System.out.println("Id: " + blob.getBlobId());
		System.out.println("KmsKeyName: " + blob.getKmsKeyName());
		System.out.println("Md5Hash: " + blob.getMd5());
		System.out.println("Md5HexString: " + blob.getMd5ToHexString());
		System.out.println("MediaLink: " + blob.getMediaLink());
		System.out.println("Metageneration: " + blob.getMetageneration());
		System.out.println("Name: " + blob.getName());
		System.out.println("Size: " + blob.getSize());
		System.out.println("StorageClass: " + blob.getStorageClass());
		System.out.println("TimeCreated: " + new Date(blob.getCreateTime()));
		System.out.println("Last Metadata Update: " + new Date(blob.getUpdateTime()));
		Boolean temporaryHoldIsEnabled = (blob.getTemporaryHold() != null && blob.getTemporaryHold());
		System.out.println("temporaryHold: " + (temporaryHoldIsEnabled ? "enabled" : "disabled"));
		Boolean eventBasedHoldIsEnabled =
		    (blob.getEventBasedHold() != null && blob.getEventBasedHold());
		System.out.println("eventBasedHold: " + (eventBasedHoldIsEnabled ? "enabled" : "disabled"));
		if (blob.getRetentionExpirationTime() != null) {
		  System.out.println("retentionExpirationTime: " + new Date(blob.getRetentionExpirationTime()));
		}
		if (blob.getMetadata() != null) {
		  System.out.println("\n\n\nUser metadata:");
		  for (Map.Entry<String, String> userMetadata : blob.getMetadata().entrySet()) {
		    System.out.println(userMetadata.getKey() + "=" + userMetadata.getValue());
		  }
		}
		

	}

}

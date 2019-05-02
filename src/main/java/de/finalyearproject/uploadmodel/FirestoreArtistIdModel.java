package de.finalyearproject.uploadmodel;

public class FirestoreArtistIdModel {

	private String artist_name;
	private String url1;
	private String url2;
	@Override
	public String toString() {
		return "FirestoreArtistIdModel [artist_name=" + artist_name + ", url1=" + url1 + ", url2=" + url2 + "]";
	}
	
	public String getArtist_name() {
		return artist_name;
	}
	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}
	public String getUrl1() {
		return url1;
	}
	public void setUrl1(String url1) {
		this.url1 = url1;
	}
	public String getUrl2() {
		return url2;
	}
	public void setUrl2(String url2) {
		this.url2 = url2;
	}
	
	public FirestoreArtistIdModel()
	{
		
	}
	
	public void trimQuotes() {
		this.artist_name=trimFunction(this.artist_name);
//		this.url1=trimFunction(this.url1);
//		this.url2=trimFunction(this.url2);
	}
	
	private String trimFunction(String a) {
		a=a.substring(1);
		a=a.substring(0, a.length()-1);
		return a;
	}

}

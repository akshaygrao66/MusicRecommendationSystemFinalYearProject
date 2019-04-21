package de.finalyearproject.uploadmodel;

public class FirestoreMusicValueModel {
	private String tempo;
	private String mode_confidence;
	private String duration;
	private String loudness;
	private String song_hottness;
	private String year;
	private String artist_familiarity;
	private String artist_name;
	private String artist_hotness;
	private String song_title;
	private String artist_id;
	private String smallImageStorageLocation;
	private String smallImageDownloadLocation;
	private String largeImageStorageLocation;
	private String largeImageDownloadLocation; 	
	private String storagelocation;
	private String downloadlocation;
	
	
	public String getTempo() {
		return tempo;
	}
	public void setTempo(String tempo) {
		this.tempo = tempo;
	}
	public String getMode_confidence() {
		return mode_confidence;
	}
	public void setMode_confidence(String mode_confidence) {
		this.mode_confidence = mode_confidence;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getLoudness() {
		return loudness;
	}
	public void setLoudness(String loudness) {
		this.loudness = loudness;
	}
	public String getSong_hottness() {
		return song_hottness;
	}
	public void setSong_hottness(String song_hottness) {
		this.song_hottness = song_hottness;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getArtist_familiarity() {
		return artist_familiarity;
	}
	public void setArtist_familiarity(String artist_familiarity) {
		this.artist_familiarity = artist_familiarity;
	}
	public String getArtist_name() {
		return artist_name;
	}
	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}
	public String getArtist_hotness() {
		return artist_hotness;
	}
	public void setArtist_hotness(String artist_hotness) {
		this.artist_hotness = artist_hotness;
	}
	public String getSong_title() {
		return song_title;
	}
	public void setSong_title(String song_title) {
		this.song_title = song_title;
	}
	public String getArtist_id() {
		return artist_id;
	}
	public void setArtist_id(String artist_id) {
		this.artist_id = artist_id;
	}
	public String getImageurlsmalll() {
		return smallImageStorageLocation;
	}
	public void setImageurlsmalll(String imageurlsmalll) {
		this.smallImageStorageLocation = imageurlsmalll;
	}
	public String getImageurlbig() {
		return smallImageDownloadLocation;
	}
	public void setImageurlbig(String imageurlbig) {
		this.smallImageDownloadLocation = imageurlbig;
	}
	public String getStoragelocation() {
		return storagelocation;
	}
	public void setStoragelocation(String storagelocation) {
		this.storagelocation = storagelocation;
	}
	public String getDownloadlocation() {
		return downloadlocation;
	}
	public void setDownloadlocation(String downloadlocation) {
		this.downloadlocation = downloadlocation;
	}
	
	public String getSmallImageStorageLocation() {
		return smallImageStorageLocation;
	}
	public void setSmallImageStorageLocation(String smallImageStorageLocation) {
		this.smallImageStorageLocation = smallImageStorageLocation;
	}
	public String getSmallImageDownloadLocation() {
		return smallImageDownloadLocation;
	}
	public void setSmallImageDownloadLocation(String smallImageDownloadLocation) {
		this.smallImageDownloadLocation = smallImageDownloadLocation;
	}
	public String getLargeImageStorageLocation() {
		return largeImageStorageLocation;
	}
	public void setLargeImageStorageLocation(String largeImageStorageLocation) {
		this.largeImageStorageLocation = largeImageStorageLocation;
	}
	public String getLargeImageDownloadLocation() {
		return largeImageDownloadLocation;
	}
	public void setLargeImageDownloadLocation(String largeImageDownloadLocation) {
		this.largeImageDownloadLocation = largeImageDownloadLocation;
	}
	
	
	
	public FirestoreMusicValueModel(String tempo, String mode_confidence, String duration, String loudness,
			String song_hottness, String year, String artist_familiarity, String artist_name, String artist_hotness,
			String song_title, String artist_id, String smallImageStorageLocation, String smallImageDownloadLocation,
			String largeImageStorageLocation, String largeImageDownloadLocation, String storagelocation,
			String downloadlocation) {
		this.tempo = tempo;
		this.mode_confidence = mode_confidence;
		this.duration = duration;
		this.loudness = loudness;
		this.song_hottness = song_hottness;
		this.year = year;
		this.artist_familiarity = artist_familiarity;
		this.artist_name = artist_name;
		this.artist_hotness = artist_hotness;
		this.song_title = song_title;
		this.artist_id = artist_id;
		this.smallImageStorageLocation = smallImageStorageLocation;
		this.smallImageDownloadLocation = smallImageDownloadLocation;
		this.largeImageStorageLocation = largeImageStorageLocation;
		this.largeImageDownloadLocation = largeImageDownloadLocation;
		this.storagelocation = storagelocation;
		this.downloadlocation = downloadlocation;
	}
	
	
	@Override
	public String toString() {
		return "FirestoreMusicValueModel [tempo=" + tempo + ", mode_confidence=" + mode_confidence + ", duration="
				+ duration + ", loudness=" + loudness + ", song_hottness=" + song_hottness + ", year=" + year
				+ ", artist_familiarity=" + artist_familiarity + ", artist_name=" + artist_name + ", artist_hotness="
				+ artist_hotness + ", song_title=" + song_title + ", artist_id=" + artist_id
				+ ", smallImageStorageLocation=" + smallImageStorageLocation + ", smallImageDownloadLocation="
				+ smallImageDownloadLocation + ", largeImageStorageLocation=" + largeImageStorageLocation
				+ ", largeImageDownloadLocation=" + largeImageDownloadLocation + ", storagelocation=" + storagelocation
				+ ", downloadlocation=" + downloadlocation + ", getTempo()=" + getTempo() + ", getMode_confidence()="
				+ getMode_confidence() + ", getDuration()=" + getDuration() + ", getLoudness()=" + getLoudness()
				+ ", getSong_hottness()=" + getSong_hottness() + ", getYear()=" + getYear()
				+ ", getArtist_familiarity()=" + getArtist_familiarity() + ", getArtist_name()=" + getArtist_name()
				+ ", getArtist_hotness()=" + getArtist_hotness() + ", getSong_title()=" + getSong_title()
				+ ", getArtist_id()=" + getArtist_id() + ", getImageurlsmalll()=" + getImageurlsmalll()
				+ ", getImageurlbig()=" + getImageurlbig() + ", getStoragelocation()=" + getStoragelocation()
				+ ", getDownloadlocation()=" + getDownloadlocation() + ", getSmallImageStorageLocation()="
				+ getSmallImageStorageLocation() + ", getSmallImageDownloadLocation()="
				+ getSmallImageDownloadLocation() + ", getLargeImageStorageLocation()=" + getLargeImageStorageLocation()
				+ ", getLargeImageDownloadLocation()=" + getLargeImageDownloadLocation() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	public FirestoreMusicValueModel()
	{
		
	}
	
	
	
	
}

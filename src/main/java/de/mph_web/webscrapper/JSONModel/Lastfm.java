package de.mph_web.webscrapper.JSONModel;

import java.util.Arrays;

public class Lastfm {
	String artist;
	String timestamp;
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public Object[][] getSimilars() {
		return similars;
	}
	public void setSimilars(Object[][] similars) {
		this.similars = similars;
	}
	String[][] tags;
	String track_id;
	String title;
	Object[][] similars;
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String[][] getTags() {
		return tags;
	}
	public void setTags(String[][] tags) {
		this.tags = tags;
	}
	public String getTrack_id() {
		return track_id;
	}
	public void setTrack_id(String track_id) {
		this.track_id = track_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return "Lastfm [artist=" + artist + ", timestamp=" + timestamp + ", tags=" + Arrays.toString(tags)
				+ ", track_id=" + track_id + ", title=" + title + ", similars=" + Arrays.toString(similars) + "]";
	}
	

}

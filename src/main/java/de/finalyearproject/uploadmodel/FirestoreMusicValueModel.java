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
	
	@Override
	public String toString() {
		return "FirestoreMusicValueModel [tempo=" + tempo + ", mode_confidence=" + mode_confidence + ", duration="
				+ duration + ", loudness=" + loudness + ", song_hottness=" + song_hottness + ", year=" + year
				+ ", artist_familiarity=" + artist_familiarity + ", artist_name=" + artist_name + ", artist_hotness="
				+ artist_hotness + ", song_title=" + song_title + ", artist_id=" + artist_id + "]";
	}


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

	public FirestoreMusicValueModel(String tempo, String mode_confidence, String duration, String loudness,
			String song_hottness, String year, String artist_familiarity, String artist_name, String artist_hotness,
			String song_title, String artist_id) {
		super();
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


	public FirestoreMusicValueModel()
	{
		
	}
}

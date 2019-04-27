package de.finalyearproject.preprocessing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetAllAttributesFromDB {
	static Connection conmysql=null;
	private Double tempo;
	private Double mode_confidence;
	private Double duration;
	private Double loudness;
	private Double song_hottness;
	private String year;
	private Double artist_familiarity;
	private String artist_name;
	private Double artist_hotness;
	private String song_title;
	private Double lyrics_score;
	private int mxm_tid;


	public Double getLyrics_score() {
		return lyrics_score;
	}


	public void setLyrics_score(Double lyrics_score) {
		this.lyrics_score = lyrics_score;
	}


	public int getMxm_tid() {
		return mxm_tid;
	}


	public void setMxm_tid(int mxm_tid) {
		this.mxm_tid = mxm_tid;
	}


	public Double getTempo() {
		return tempo;
	}



	@Override
	public String toString() {
		return "GetAllAttributesFromDB [tempo=" + tempo + ", mode_confidence=" + mode_confidence + ", duration="
				+ duration + ", loudness=" + loudness + ", song_hottness=" + song_hottness + ", year=" + year
				+ ", artist_familiarity=" + artist_familiarity + ", artist_name=" + artist_name + ", artist_hotness="
				+ artist_hotness + ", song_title=" + song_title + ", lyrics_score=" + lyrics_score + ", mxm_tid="
				+ mxm_tid + "]";
	}


	public void setTempo(Double tempo) {
		this.tempo = tempo;
	}


	public Double getMode_confidence() {
		return mode_confidence;
	}


	public void setMode_confidence(Double mode_confidence) {
		this.mode_confidence = mode_confidence;
	}


	public Double getDuration() {
		return duration;
	}


	public void setDuration(Double duration) {
		this.duration = duration;
	}


	public Double getLoudness() {
		return loudness;
	}


	public void setLoudness(Double loudness) {
		this.loudness = loudness;
	}


	public Double getSong_hottness() {
		return song_hottness;
	}


	public void setSong_hottness(Double song_hottness) {
		this.song_hottness = song_hottness;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {


		this.year = year;
	}


	public Double getArtist_familiarity() {
		return artist_familiarity;
	}


	public void setArtist_familiarity(Double artist_familiarity) {
		this.artist_familiarity = artist_familiarity;
	}


	public String getArtist_name() {
		return artist_name;
	}


	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}


	public Double getArtist_hotness() {
		return artist_hotness;
	}


	public void setArtist_hotness(Double artist_hotness) {
		this.artist_hotness = artist_hotness;
	}


	public String getSong_title() {
		return song_title;
	}


	public void setSong_title(String song_title) {
		this.song_title = song_title;
	}

	public boolean checkForNan(String value) {
		boolean status = false;
		if(value.equals("nan")) {
			status = true;
		}

		return status  ;
	}


	public void setAttributes(String eachMSDTrack) {
		try {
			PreparedStatement prepLyrics;
			prepLyrics = conmysql.prepareStatement("select * from allattributes where track_id=?");
			prepLyrics.setString(1, eachMSDTrack);
			
			ResultSet result=prepLyrics.executeQuery();
			if(result.next()) {
				this.tempo=result.getDouble("tempo");
				this.mode_confidence=result.getDouble("mode_confidence");
				this.duration=result.getDouble("duration");
				this.loudness=result.getDouble("loudness");
				this.song_hottness=result.getDouble("song_hottness");
				this.year=result.getString("year");
				this.artist_familiarity=result.getDouble("artist_familiarity");
				this.artist_name=result.getString("artist_name");
				this.artist_hotness=result.getDouble("artist_hottness");
				this.song_title=result.getString("song_title");
				this.lyrics_score=result.getDouble("lyrics_score");
				this.mxm_tid=result.getInt("mxm_tid");
			}

		}catch(Exception e) {
			System.out.println(e);
		}
	}


	public  GetAllAttributesFromDB(String track_ID) {
		if(!establishMySqlConnectionWithNewMusixMatchDB()) {
			System.out.println("MYSQL connection failed");
			return;
		}
		setAttributes(track_ID);
	}


	public static String getAttributeFromMsdDB(String name) {
		return name;

	}

	public static boolean establishMySqlConnectionWithNewMusixMatchDB(){
		try{  
			conmysql=DriverManager.getConnection("jdbc:mysql://localhost:3306/musixmatch_new","root","root");  
		}catch(Exception e){ System.out.println(e); return false;}
		return true;
	}
}

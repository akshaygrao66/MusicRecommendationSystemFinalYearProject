package de.finalyearproject.preprocessing;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GetAttributeFromMSD {

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


	public Double getTempo() {
		return tempo;
	}


	@Override
	public String toString() {
		return "GetAttributeFromMSD [tempo=" + tempo + ", mode_confidence=" + mode_confidence + ", duration=" + duration
				+ ", loudness=" + loudness + ", song_hottness=" + song_hottness + ", year=" + year
				+ ", artist_familiarity=" + artist_familiarity + ", artist_name=" + artist_name + ", artist_hotness="
				+ artist_hotness + ", song_title=" + song_title + "]";
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


	public void setAttributes(String fileName) {
		try {
			boolean tempoStatus = checkForNan(getAttributeFromMsdFile(fileName,"get_tempo"));
			boolean artistfemilarity = checkForNan(getAttributeFromMsdFile(fileName,"get_artist_familiarity"));
			boolean artisthotness = checkForNan(getAttributeFromMsdFile(fileName,"get_artist_hotnesss"));
			boolean durationStatus = checkForNan(getAttributeFromMsdFile(fileName,"get_duration"));
			boolean loudNessStatus = checkForNan(getAttributeFromMsdFile(fileName,"get_loudness"));
			boolean modeConfidenceStatus  = checkForNan(getAttributeFromMsdFile(fileName,"get_mode_confidence"));
			boolean hottnesStatus = checkForNan(getAttributeFromMsdFile(fileName,"get_song_hotness"));


			if(tempoStatus) {
				this.setTempo(0.0d);
			}
			else {
				this.setTempo(Double.parseDouble(getAttributeFromMsdFile(fileName,"get_tempo")));
			}

			if(artistfemilarity) {
				this.artist_familiarity = 0.0d;	
			}
			else {
				this.artist_familiarity = Double.parseDouble(getAttributeFromMsdFile(fileName,"get_artist_familiarity"));
			}

			if(artisthotness) {
				this.artist_hotness = 0.0d;
			}
			else {
				this.artist_hotness = Double.parseDouble(getAttributeFromMsdFile(fileName,"get_artist_hotnesss"));
			}
			this.artist_name = (String)getAttributeFromMsdFile(fileName,"get_artist_name");
			this.artist_name=this.artist_name.substring(1);

			if(durationStatus) {
				this.duration = 0.0d;
			}
			else {
				this.duration = Double.parseDouble(getAttributeFromMsdFile(fileName,"get_duration"));
			}

			if(loudNessStatus) {
				this.loudness = 0.0d;
			}
			else {
				this.loudness = Double.parseDouble(getAttributeFromMsdFile(fileName,"get_loudness"));
			}

			if(modeConfidenceStatus) {
				this.mode_confidence = 0.0d;	
			}
			else {
				this.mode_confidence = Double.parseDouble(getAttributeFromMsdFile(fileName,"get_mode_confidence"));
			}

			if(hottnesStatus) {
				this.song_hottness = 0.0d;	
			}
			else {	
				this.song_hottness = Double.parseDouble(getAttributeFromMsdFile(fileName,"get_song_hotness"));
			}
			this.song_title = (String)getAttributeFromMsdFile(fileName,"get_title");
			this.song_title=this.song_title.substring(1);
			this.year = (String)(getAttributeFromMsdFile(fileName,"get_year"));

		}catch(Exception e) {
			System.out.println(e);
		}
	}


	public  GetAttributeFromMSD(String fileName) {
		setAttributes(fileName);
	}


	public static String getAttributeFromMsdFile(String name,String commandFromCaller) {
		String s=null;
		String output="";
		try {

			String command = "python C:\\Users\\aksha\\OneDrive\\Desktop\\Million-Song-Dataset-HDF5-to-CSV-master\\hdf5_getters.py D:\\projectdatabases\\MSD\\MillionSongSubset\\data\\"+name+".h5 "+commandFromCaller;	
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((s = stdInput.readLine()) != null) {
				output=output+s;	               
			}
		}catch(Exception e ) {
			System.out.println(e);
		}
		return output;
	}
}

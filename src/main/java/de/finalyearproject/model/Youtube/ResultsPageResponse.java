package de.finalyearproject.model.Youtube;

public class ResultsPageResponse {
	private String song_title;
	private String channel;
	private String imageLink;
	private String audioLink;
	private int duration;
	private int views;
	public String getSong_title() {
		return song_title;
	}
	public void setSong_title(String song_title) {
		this.song_title = song_title;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getImageLink() {
		return imageLink;
	}
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}
	public String getAudioLink() {
		return audioLink;
	}
	public void setAudioLink(String audioLink) {
		this.audioLink = audioLink;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}
	@Override
	public String toString() {
		return "ResultsPageResponse [song_title=" + song_title + ", channel=" + channel + ", imageLink=" + imageLink
				+ ", audioLink=" + audioLink + ", duration=" + duration + ", views=" + views + "]";
	}
	public ResultsPageResponse(String song_title, String channel, String imageLink, String audioLink, int duration,
			int views) {
		this.song_title = song_title;
		this.channel = channel;
		this.imageLink = imageLink;
		this.audioLink = audioLink;
		this.duration = duration;
		this.views = views;
	}
	
}

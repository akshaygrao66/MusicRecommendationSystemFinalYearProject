package de.mph_web.webscrapper.JSONModel;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MusixMatchResponseRoot {
	private Map<String,Object> message;
	private Map<String,Integer> header;
	private Map<String,Object> body;
	private Integer status_code;
	private Map<String,Object> lyrics;
	private Integer lyrics_id;
	private String lyrics_body;

	
	@SuppressWarnings("unchecked")
	@JsonProperty("message")
	private void unpackMessageFromMusixMatchAPI(Map<String,Object> message)
	{
		this.message=message;
		this.header=(Map<String,Integer>)message.get("header");
		if(message.get("body") instanceof Map<?,?>)
		{
			this.body=(Map<String,Object>)message.get("body");
		}
		else
		{
			this.body=null;
			return;
		}
		this.status_code=(Integer)header.get("status_code");		
		
		this.lyrics=(Map<String,Object>)this.body.get("lyrics");
		this.lyrics_id=(Integer)this.lyrics.get("lyrics_id");
		this.lyrics_body=(String)this.lyrics.get("lyrics_body");
	}


	public Map<String, Object> getMessage() {
		return message;
	}


	public void setMessage(Map<String, Object> message) {
		this.message = message;
	}


	public Map<String, Integer> getHeader() {
		return header;
	}


	public void setHeader(Map<String, Integer> header) {
		this.header = header;
	}


	public Map<String, Object> getBody() {
		return body;
	}


	public void setBody(Map<String, Object> body) {
		this.body = body;
	}


	public Integer getStatus_code() {
		return status_code;
	}


	public void setStatus_code(Integer status_code) {
		this.status_code = status_code;
	}


	public Map<String, Object> getLyrics() {
		return lyrics;
	}


	public void setLyrics(Map<String, Object> lyrics) {
		this.lyrics = lyrics;
	}


	public Integer getLyrics_id() {
		return lyrics_id;
	}


	public void setLyrics_id(Integer lyrics_id) {
		this.lyrics_id = lyrics_id;
	}


	public String getLyrics_body() {
		return lyrics_body;
	}


	public void setLyrics_body(String lyrics_body) {
		this.lyrics_body = lyrics_body;
	}


	@Override
	public String toString() {
		return "MusixMatchResponseRoot [message=" + message + ", header=" + header + ", body=" + body + ", status_code="
				+ status_code +", lyrics=" + lyrics + ", lyrics_id=" + lyrics_id
				+ ", lyrics_body=" + lyrics_body + "]";
	}
	
}

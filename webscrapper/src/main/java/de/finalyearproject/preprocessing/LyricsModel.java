package de.finalyearproject.preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LyricsModel {
	
	@Override
	public String toString() {
		
		String temp="";
		
			for(Map.Entry<String, Integer> entry : map.entrySet())
			{
				temp=temp+(String)entry.getKey()+":";
				temp=temp+entry.getValue()+", ";
			}
		
		return "LyricsModel [mxId=" + mxId + ", map=" + temp + "]";
	}
	private String mxId ;
	private HashMap<String , Integer> map = new HashMap<String,Integer>();
	public String getMxId() {
		return mxId;
	}
	public void setMxId(String mxId) {
		this.mxId = mxId;
	}
	public HashMap<String, Integer> getMap() {
		return map;
	}
	public void setMap(HashMap<String, Integer> map) {
		this.map = map;
	}
	
	

}

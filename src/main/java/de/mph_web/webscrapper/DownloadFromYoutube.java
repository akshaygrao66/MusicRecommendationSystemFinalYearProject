package de.mph_web.webscrapper;

import java.io.PrintWriter;

public class DownloadFromYoutube {
	public void downloadAudioFromYouTube(String url,String title) {
		String download_path="D:\\projectAudioFiles";
		String[] command =
	    {
	        "cmd",
	    };
	    Process p;
		try {
			p = Runtime.getRuntime().exec(command); 
		        new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
	                new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
	                PrintWriter stdin = new PrintWriter(p.getOutputStream());
	                stdin.println("cd \""+download_path+"\"");
	                stdin.println(download_path+"\\youtube-dl --extract-audio --audio-format mp3 "+url);
	                stdin.close();
	                p.waitFor();
	    	} catch (Exception e) {
	 		e.printStackTrace();
		}
	}
}

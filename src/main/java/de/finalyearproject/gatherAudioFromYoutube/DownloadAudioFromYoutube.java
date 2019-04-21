package de.finalyearproject.gatherAudioFromYoutube;

import java.io.PrintWriter;

public class DownloadAudioFromYoutube {
	public void downloadAudioFromYoutube(String url,String path) {
		String download_path="F:\\temporary_audio_image_folder";
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
	                stdin.println(download_path+"\\youtube-dl --extract-audio --audio-format mp3 -o \""+download_path+"\\songs\\"+path+".mp3\" "+url);
	                stdin.close();
	                p.waitFor();
	    	} catch (Exception e) {
	 		e.printStackTrace();
		}
	}
}

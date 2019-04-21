package de.mph_web.webscrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MyScrapper {

	public static void main(String[] args) throws Exception{
       // final Document document = Jsoup.connect("https://www.youtube.com/results?search_query=faded").get();
        String url   = "http://www.youtube.com/results";
        String query = "Despacito";
        
        String targetTitle="";
        String targetPrefix="http://www.youtube.com";
        String targetUrl="";
        Document document = Jsoup.connect(url)
            .data("search_query", query)
            .get();
        DownloadFromYoutube downloadFromYoutube=new DownloadFromYoutube();
        int i=0;
        for (Element a : document.select(".yt-lockup-title > a[title]")) {
        	if(i==0)
        	{
        		targetUrl=a.attr("href");
        		targetTitle=a.attr("title");
        	}
        	i++;
            System.out.println(a.attr("href") + " " + a.attr("title"));
        }
        i=0;
        downloadFromYoutube.downloadAudioFromYouTube(targetPrefix+""+targetUrl,targetTitle);
	}

}

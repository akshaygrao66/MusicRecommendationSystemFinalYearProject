package de.finalyearproject.gatherAudioFromYoutube;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.finalyearproject.model.Youtube.ResultsPageResponse;
import de.finalyearproject.preprocessing.GetAttributeFromMSD;

public class DownloadAndStoreToDB {
	static FileWriter logger=null;
	static BufferedWriter logwriter = new BufferedWriter(returnlogger());
	static Connection connsqlite=null;
	static Connection conmysql=null;
	final static String LASTFM_DATASET_SOURCE="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset";
	final static String MSD_DATASET_SOURCE="D:\\projectdatabases\\MSD\\MillionSongSubset\\data";
	public static void main(String[] args) throws SQLException, IOException, UnsupportedAudioFileException, LineUnavailableException{
		if(!establishMySqlConnectionWithNewMusixMatchDB()) {
			System.out.println("MYSQL connection failed");
			return;
		}

		//		Environment.defaultConfig().connectTimeout = 50000;
		//		Environment.defaultConfig().readTimeout = 80000;
		ArrayList<String> trackSongMissedDownload=new ArrayList<String>();
		GetAttributeFromMSD getAttributesFromMSD;
		ArrayList<String> allMSDTrackIDs=getAllUniqueTrackIDs();
		int i=103;
		boolean enter=false;
		String startPoint="TRAAQIH128F428BDEA";
		for(String eachMSDTrack:allMSDTrackIDs) {
			if(eachMSDTrack.equals(startPoint)) {
				enter=true;
			}
			if(enter) {
				getAttributesFromMSD=new GetAttributeFromMSD(eachMSDTrack);

				ResultsPageResponse optimalResponse=new ResultsPageResponse("", "", "", "", 1, 0);
				try {
					System.out.println(getAttributesFromMSD);
					optimalResponse = returnLinksForAudioAndImageDownload(getAttributesFromMSD.getSong_title(),getAttributesFromMSD.getArtist_name(),getAttributesFromMSD.getDuration(),Integer.parseInt(getAttributesFromMSD.getYear()));
					if(optimalResponse!=null) {
						if(optimalResponse.getChannel()=="" && optimalResponse.getDuration()==0 && optimalResponse.getSong_title()=="" && optimalResponse.getViews()==0) {
							System.out.println("Song results cannot be reached.So added to missed tracks");
							BufferedWriter logMissedResultsPageDownload = new BufferedWriter(new FileWriter("D:\\projectdatabases\\logMissedResultPageDownload.txt", true));  //Set true for append mode
							logMissedResultsPageDownload.newLine();
							logMissedResultsPageDownload.write(eachMSDTrack);
							logMissedResultsPageDownload.close();
						}
						else
						{
							System.out.println(optimalResponse);
							downloadImageAndStoreAtSpecifiedPath(optimalResponse.getAudioLink(),optimalResponse.getImageLink(),eachMSDTrack);
							logwriter.write("Image number "+i+" with trackID:"+eachMSDTrack+" downloaded\n");
							if(!downloadAudioAndStoreAtSpecifiedPath(optimalResponse.getAudioLink(),eachMSDTrack)) {
								System.out.println("track"+eachMSDTrack+"song downloads cannot be reached.So added to missed tracks");
								BufferedWriter logMissedSongDownload = new BufferedWriter(new FileWriter("D:\\projectdatabases\\logSongDownloadMissed.txt", true));  //Set true for append mode
								logMissedSongDownload.newLine();
								logMissedSongDownload.write(eachMSDTrack);
								logMissedSongDownload.close();
							}
							logwriter.write("Song number "+i+" with trackID:"+eachMSDTrack+" downloaded\n");
							System.out.println("Song number "+i+" with trackID:"+eachMSDTrack+" downloaded");
						}
					}
					else {
						deleteFromDB(eachMSDTrack);
					}
				} catch (NumberFormatException e) {
					PlayAudioOnException.playSongOnError();
					logwriter.write("Error====================>"+e.getLocalizedMessage()+"\n");
					logwriter.write("Error at song number"+i+" with trackID:"+eachMSDTrack+"\n"+optimalResponse);
					logwriter.close();
					logger.close();
					e.printStackTrace();
				} catch (Exception e) {
					PlayAudioOnException.playSongOnError();
					logwriter.write("Error====================>"+e.getLocalizedMessage()+"\n");
					logwriter.write("Error at song number"+i+" with trackID:"+eachMSDTrack+"\n"+optimalResponse);
					logwriter.close();
					logger.close();
					e.printStackTrace();
				}
				i++;
			}
		}
		logwriter.close();
		logger.close();
	}

	public static void deleteFromDB(String eachMSDTrack) throws SQLException, IOException {
		PreparedStatement delete=conmysql.prepareStatement("delete from lyrics where track_id=?");
		delete.setString(1, eachMSDTrack);
		delete.executeUpdate();
		System.out.println("Deleted trackID:"+eachMSDTrack+" from Database");
		FileWriter deleteLog=new FileWriter("D:\\projectdatabases\\logTrackDeletedDueToYoutubeResultsNotFound.txt");
		deleteLog.write("Deleted trackID:"+eachMSDTrack+" from Database");
		deleteLog.close();
	}

	@SuppressWarnings("resource")
	public static void downloadImageAndStoreAtSpecifiedPath(String audioLink,String imageLink,String path) throws IOException {
		if(!downloadLargerImage(audioLink,path)) {
			System.out.println("track"+path+"larger Image downloads cannot be reached.So added to missed tracks");
			BufferedWriter logMissedLargerImageDownload = new BufferedWriter(new FileWriter("D:\\projectdatabases\\logMissedLargerImageDownloadMissed.txt", true));  //Set true for append mode
			logMissedLargerImageDownload.newLine();
			logMissedLargerImageDownload.write(path);
			logMissedLargerImageDownload.close();
		}
		if(!imageLink.toLowerCase().contains("gif")) {
			if(!downloadSmallerImage(imageLink, path)) {
				System.out.println("track"+path+"smaller image downloads cannot be reached.So added to missed tracks");
				BufferedWriter logMissedSmallerImageDownload = new BufferedWriter(new FileWriter("D:\\projectdatabases\\logMissedSmallerImageDownloadMissed.txt", true));  //Set true for append mode
				logMissedSmallerImageDownload.newLine();
				logMissedSmallerImageDownload.write(path);
				logMissedSmallerImageDownload.close();
			}
		}
	}
	public static boolean downloadLargerImage(String imageLink,String path) {

		for (int i = 0; i < 10; i++) {
			try {
				imageLink="https://www.youtube.com"+imageLink;
				URL url;
				String r;
				url = new URL(imageLink);
				String s=url.getQuery();
				r=s.substring(2);
				URL rurl = new URL("http://img.youtube.com/vi/" + r +"/0.jpg");
				InputStream inputStream = null;
				OutputStream outputStream = null;
				inputStream = rurl.openStream();
				outputStream = new FileOutputStream("F:\\temporary_audio_image_folder\\images\\Larger Image\\"+path+".jpg");

				byte[] buffer = new byte[2048];
				int length;

				while ((length = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, length);

				}
				System.out.println(path+" Larger Image Downloaded!!!");
				Thread.sleep(1000);
				return true;
			}
			catch(Exception e) {
				try {
					Thread.sleep(500*i);
					System.out.println("Retrying getting larger image from youtube......."+i+"because"+e.getLocalizedMessage());
				} catch (InterruptedException e1) {

				}
				continue;
			}
		}
		return false;
	}

	public static boolean downloadSmallerImage(String imageLink,String path) throws IOException{
		for (int i = 0; i < 10; i++) {
			try {
				URL rurl = new URL(imageLink);
				InputStream inputStream = null;
				OutputStream outputStream = null;

				inputStream = rurl.openStream();
				outputStream = new FileOutputStream("F:\\temporary_audio_image_folder\\images\\Smaller Image\\"+path+".jpg");

				byte[] buffer = new byte[2048];
				int length;

				while ((length = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, length);

				}
				System.out.println(path+" Smaller Image Downloaded!!!");
				Thread.sleep(1000);
				return true;
			}
			catch(Exception e) {
				try {
					Thread.sleep(500*i);
					System.out.println("Retrying getting smaller image from youtube......."+i+"because"+e.getLocalizedMessage());
				} catch (InterruptedException e1) {

				}
				continue;
			}
		}
		return false;
	}
	public static boolean downloadAudioAndStoreAtSpecifiedPath(String audioLink,String path) {
		DownloadAudioFromYoutube downloadAudio=new DownloadAudioFromYoutube();
		return downloadAudio.downloadAudioFromYoutube("https://www.youtube.com"+audioLink,path);
	}

	public static ArrayList<String> getAllUniqueTrackIDs() throws SQLException{
		ArrayList<String> trackIdsUnique=new ArrayList<String>();
		trackIdsUnique.clear();
		Statement st1 = conmysql.createStatement();
		ResultSet rs1 = st1.executeQuery("select distinct track_id from lyrics");
		while(rs1.next()) {
			trackIdsUnique.add(rs1.getString(1));
		}
		return trackIdsUnique;
	}

	public static ResultsPageResponse returnLinksForAudioAndImageDownload(String songTitle,String artist_Name,Double duration,int year) throws Exception {
		// final Document document = Jsoup.connect("https://www.youtube.com/results?search_query=faded").get();
		String url   = "http://www.youtube.com/results";
		String query = songTitle+" "+artist_Name; //This is the search string which is searched in youtube
		int retry=0;
		boolean flag=true;
		Document document=null;
		for (retry = 0; retry < 5; retry++) {
			try {
				document = Jsoup.connect(url)
						.data("search_query", query)
						.get();
				Thread.sleep(1000);
				flag=true;
			} catch (IOException e) {
				try {
					Thread.sleep(500*retry);
					System.out.println("Retrying getting results page......."+retry+"because"+e.getLocalizedMessage());
					flag=false;
				} catch (InterruptedException e1) {
				}
				continue;
			}
		}
		if(flag==false) {
			ResultsPageResponse dummyResponse=new ResultsPageResponse("", "", "", "", 0, 0);
			return dummyResponse;
		}

		ArrayList<ResultsPageResponse> resultPageResponsesFromYoutube=new ArrayList<ResultsPageResponse>();
		resultPageResponsesFromYoutube.clear();
		Elements songContainers=document.select("div.yt-lockup.yt-lockup-tile.yt-lockup-video.vve-check.clearfix");
		if(songContainers.isEmpty()) {
			System.out.println("No results in youtube");
			return null;
		}
		int count=0;
		for (int i=0;i<songContainers.size() && count<8;i++) {
			Element songContainer=songContainers.get(i);
			if(!checkIfResultIsPlaylistUsingJSoup(songContainer) && !checkDurationGreaterThanAnHourFromYouTube(songContainer)) {
				ResultsPageResponse eachSongResponseFromYoutube=getResultsPageResponseForEachSong(songContainer);
				resultPageResponsesFromYoutube.add(eachSongResponseFromYoutube);
				count++;
				int differenceInDuration=(int) Math.abs(eachSongResponseFromYoutube.getDuration()-Math.round(duration));
				if(checkWhetherSongIsFromYoutubeVerifiedChannel(songContainer) && differenceInDuration<10) { //If the song is from verified then directly return it
					if(checkStringParamInYoutubeTitleInResultsPage(eachSongResponseFromYoutube, songTitle)) {
						return eachSongResponseFromYoutube;
					}
					else if(checkStringParamInYoutubeTitleInResultsPage(eachSongResponseFromYoutube, artist_Name)) {
						return eachSongResponseFromYoutube;
					}
				}
			}
		}
		return returnOptimalSongFromResultsResponseInResultsPageInYoutube(resultPageResponsesFromYoutube,songTitle,artist_Name,duration,year);
	}

	public static ResultsPageResponse returnOptimalSongFromResultsResponseInResultsPageInYoutube(ArrayList<ResultsPageResponse> resultPageResponsesFromYoutube,String songTitle,String artist_Name,Double duration,int year) {
		int durationFromDB=(int)Math.round(duration);
		HashMap<ResultsPageResponse,Integer> rankResponseMap=new HashMap<ResultsPageResponse,Integer>();
		int j=0;
		for(ResultsPageResponse eachSong:resultPageResponsesFromYoutube) {
			//			System.out.println("Song "+j+"=====================================>"+eachSong.getSong_title());
			int rank=0;
			int differenceInDuration=Math.abs(eachSong.getDuration()-durationFromDB);
			if(checkStringParamInYoutubeTitleInResultsPage(eachSong, songTitle)) { //Song title present- Weight of 11 is assigned
				//				System.out.println("Title Matched with title");
				rank+=11;
			}
			if(checkStringParamInYoutubeTitleInResultsPage(eachSong, artist_Name)) { //Artist name present- Weight=5
				//				System.out.println("Artist Matched with title");
				rank+=5;
			}
			if(checkStringParamInYoutubeTitleInResultsPage(eachSong, Integer.toString(year))) { //Year present -Weight=3
				//				System.out.println("Year Matched with title");
				rank+=3;
			}
			if(checkStringParamInYoutubeChannelInResultsPage(eachSong, songTitle)) { //Song title present in channel name-Weight=2
				//				System.out.println("Title Matched with channel");
				rank+=2;
			}
			if(checkStringParamInYoutubeChannelInResultsPage(eachSong, artist_Name)) { //Artist present in channel- Weight=8
				//				System.out.println("artist Matched with channel");
				rank+=8;
			}
			if(differenceInDuration<=5) { //If duration gap is less -Weight=5
				//				System.out.println("Duration difference is less than 5 at:"+differenceInDuration);
				rank+=5;
			}
			rankResponseMap.put(eachSong,rank);
			//			System.out.println("Rank for song "+j+" is "+rank);
			j++;
		}
		Map<ResultsPageResponse,Integer> sortedByRanks=sortByValue(rankResponseMap);
		System.out.println("***********************"+sortedByRanks);
		int maxrank=0;
		for (Map.Entry<ResultsPageResponse, Integer> en : sortedByRanks.entrySet()) { 
			maxrank=en.getValue();
		}
		ArrayList<ResultsPageResponse> bucketSameMaximumRank=new ArrayList<ResultsPageResponse>();
		bucketSameMaximumRank.clear();
		for (Map.Entry<ResultsPageResponse, Integer> en : sortedByRanks.entrySet()) { 
			if(en.getValue()==maxrank) {
				bucketSameMaximumRank.add(en.getKey());
			}
		}
		if(bucketSameMaximumRank.size()>1) {
			return returnSongBasedOnDurationAndViews(bucketSameMaximumRank, duration);
		}
		return bucketSameMaximumRank.get(0);
	}

	public static HashMap<ResultsPageResponse, Integer> sortByValue(HashMap<ResultsPageResponse, Integer> hm) 
	{ 
		// Create a list from elements of HashMap 
		List<Map.Entry<ResultsPageResponse, Integer> > list = 
				new LinkedList<Map.Entry<ResultsPageResponse, Integer> >(hm.entrySet()); 

		// Sort the list 
		Collections.sort(list, new Comparator<Map.Entry<ResultsPageResponse, Integer> >() { 
			public int compare(Map.Entry<ResultsPageResponse, Integer> o1,  
					Map.Entry<ResultsPageResponse, Integer> o2) 
			{ 
				return (o1.getValue()).compareTo(o2.getValue()); 
			} 
		}); 

		// put data from sorted list to hashmap  
		HashMap<ResultsPageResponse, Integer> temp = new LinkedHashMap<ResultsPageResponse, Integer>(); 
		for (Map.Entry<ResultsPageResponse, Integer> aa : list) { 
			temp.put(aa.getKey(), aa.getValue()); 
		} 
		return temp; 
	}

	public static boolean checkStringParamInYoutubeTitleInResultsPage(ResultsPageResponse response,String param) {
		String inp1=removePunctuations(param);
		String inp2=removePunctuations(response.getSong_title());
		if(inp2.contains(inp1)) {
			return true;
		}
		return false;
	}

	public static boolean checkStringParamInYoutubeChannelInResultsPage(ResultsPageResponse response,String param) {
		String inp1=removePunctuations(param);
		String inp2=removePunctuations(response.getChannel());
		if(inp2.contains(inp1)) {
			return true;
		}
		return false;
	}

	public static String removePunctuations(String param) {
		String inp=param.toLowerCase().trim();
		inp=inp.replace(".", "");
		inp=inp.replace(",","");
		inp=inp.replace("(","");
		inp=inp.replace(")","");
		inp=inp.replace("'","");
		inp=inp.replace(":","");

		return inp;
	}

	public static ResultsPageResponse returnSongBasedOnDurationAndViews(
			ArrayList<ResultsPageResponse> resultPageResponsesFromYoutube,Double duration) {

		ArrayList<ResultsPageResponse> zeroDifference=new ArrayList<ResultsPageResponse>();
		zeroDifference.clear();

		ArrayList<ResultsPageResponse> belowTenSecondsDifference=new ArrayList<ResultsPageResponse>();
		belowTenSecondsDifference.clear();

		ArrayList<ResultsPageResponse> aboveTenSecondDifference=new ArrayList<ResultsPageResponse>();
		aboveTenSecondDifference.clear();

		for(ResultsPageResponse eachSong:resultPageResponsesFromYoutube) {
			int diff=(int)Math.round(duration)-eachSong.getDuration();
			diff=Math.abs(diff);
			if(diff==0) {
				zeroDifference.add(eachSong);
			}
			else if(diff<=10) {
				belowTenSecondsDifference.add(eachSong);
			}
			else if(diff >10) {
				aboveTenSecondDifference.add(eachSong);
			}
		}
		if(zeroDifference.size()==1) {
			return zeroDifference.get(0);
		}
		else if(zeroDifference.size()>1) {
			return returnSongsBasedOnViews(zeroDifference);
		}
		else{
			if(belowTenSecondsDifference.size()==1) {
				return belowTenSecondsDifference.get(0);
			}
			else if(belowTenSecondsDifference.size()>1) {
				return returnSongsBasedOnViews(belowTenSecondsDifference);
			}
			else {
				if(aboveTenSecondDifference.size()==1) {
					return aboveTenSecondDifference.get(0);
				}
				else if(aboveTenSecondDifference.size()>1) {
					return returnSongsBasedOnViews(aboveTenSecondDifference);
				}
				else {
					return  null;
				}
			}
		}
	}

	public static ResultsPageResponse returnSongsBasedOnViews(ArrayList<ResultsPageResponse> input) {
		int max=0;
		ResultsPageResponse ret=null;
		for(ResultsPageResponse temp:input) {
			int val=temp.getViews();
			if(val>max) {
				max=val;
				ret=temp;
			}
		}
		return ret;
	}

	public static ResultsPageResponse getResultsPageResponseForEachSong(Element songContainer) throws Exception {
		String songTitleFromYoutube=getTitleFromYoutubeResultsPage(songContainer);
		String songChannelFromYoutube=getChannelFromYoutubeResultsPage(songContainer);
		int songViewsFromYoutube=getNumberOfViewsFromYoutubeResultsPage(songContainer);
		String songAudioLinkFromYoutube=getAudioLinkFromYoutubeResultsPage(songContainer);
		String songImageLinkFromYoutube=getImageLinkFromYoutubeResultsPage(songContainer);
		int songDuration=getDurationFromYouTube(songContainer);

		ResultsPageResponse returnResponseFromYoutube=new ResultsPageResponse(songTitleFromYoutube, songChannelFromYoutube, songImageLinkFromYoutube, songAudioLinkFromYoutube, songDuration, songViewsFromYoutube);
		return returnResponseFromYoutube;
	}

	public static String getTitleFromYoutubeResultsPage(Element songContainer) throws Exception {
		//		Elements ele=songContainer.select("a#video-title.yt-simple-endpoint.style-scope.ytd-video-renderer[title]");
		Elements ele=songContainer.select("h3.yt-lockup-title > a[title]");
		if(ele.size()>1 || ele.size()==0) {
			System.out.println("Error!"+ele.size()+"elements found for title");
			logwriter.write("Error!"+ele.size()+"elements found for title");
			throw new Exception();
		}
		String songTitleFromYoutube=ele.get(0).attr("title");

		return songTitleFromYoutube;
	}

	public static boolean checkWhetherSongIsFromYoutubeVerifiedChannel(Element songContainer)throws Exception {
		Elements ele=songContainer.select("div.yt-lockup-byline  > span.yt-uix-tooltip.yt-channel-title-icon-verified.yt-sprite");
		if(ele.size()>1) {
			System.out.println("Error!"+ele.size()+"elements found for checking verification");
			logwriter.write("Error!"+ele.size()+"elements found for checking verification");
			throw new Exception();
		}
		if(ele.size()==0) {
			return false;
		}
		String verifiedTag=ele.attr("title");
		if(verifiedTag.toLowerCase().contains("verified")) {
			return true;
		}

		return false;
	}

	public static String getAudioLinkFromYoutubeResultsPage(Element songContainer) throws Exception{
		//		Elements ele=songContainer.select("a#video-title.yt-simple-endpoint.style-scope.ytd-video-renderer[title]");
		Elements ele=songContainer.select("h3.yt-lockup-title > a[title]");
		if(ele.size()>1 || ele.size()==0) {
			System.out.println("Error!"+ele.size()+"elements found for Audio link");
			logwriter.write("Error!"+ele.size()+"elements found for Audio link");
			throw new Exception();
		}
		String songAudioLinkFromYoutube=ele.get(0).attr("href");

		return songAudioLinkFromYoutube;
	}

	public static String getImageLinkFromYoutubeResultsPage(Element songContainer) throws Exception{
		Elements ele=songContainer.select("span.yt-thumb-simple > img[alt]");
		if(ele.size()>1 || ele.size()==0) {
			System.out.println("Error!"+ele.size()+"elements found for Image link");
			logwriter.write("Error!"+ele.size()+"elements found for Image link");
			throw new Exception();
		}
		String songImageLinkFromYoutube=ele.get(0).attr("src");

		return songImageLinkFromYoutube;
	}

	public static int getNumberOfViewsFromYoutubeResultsPage(Element songContainer) throws Exception{
		//		Elements ele=songContainer.select("span.style-scope.ytd-video-meta-block");
		String songViewsFromYoutube="";
		int views;
		Elements ele=songContainer.select("ul.yt-lockup-meta-info > li");
		if(ele.size()>2 || ele.size()==0) {
			System.out.println("Error!"+ele.size()+"elements found for Number of Views");
			throw new Exception();
		}
		if(ele.size()==1) {
			songViewsFromYoutube=ele.get(0).text();
			if(!songViewsFromYoutube.contains("view")) {
				System.out.println("Views absent!");
				songViewsFromYoutube="0 views";
			}
		}
		else if(ele.size()==2) {
			songViewsFromYoutube=ele.get(1).text();
			if(!songViewsFromYoutube.contains("view")) {
				System.out.println("Views absent!");
				songViewsFromYoutube="0 views";
			}
		}
		if(songViewsFromYoutube.toLowerCase().contains("no")) {
			return 0;
		}
		songViewsFromYoutube=songViewsFromYoutube.substring(0, songViewsFromYoutube.indexOf("view")).trim();
		songViewsFromYoutube=removeCommasAndReturnNumberFromString(songViewsFromYoutube);
		views=Integer.parseInt(songViewsFromYoutube);
		return views;
	}

	public static String removeCommasAndReturnNumberFromString(String songViewsFromYoutube) {
		int i=0,index=0;
		while((index=songViewsFromYoutube.indexOf(",", i))!=-1) {
			songViewsFromYoutube=songViewsFromYoutube.substring(0, index) + songViewsFromYoutube.substring(index + 1);
			i=index;
		}
		return songViewsFromYoutube;
	}

	public static String getChannelFromYoutubeResultsPage(Element songContainer) throws Exception{
		//		Elements ele=songContainer.select("a.yt-simple-endpoint.style-scope.yt-formatted-string[spellcheck=\"false\"]");
		Elements ele=songContainer.select("div.yt-lockup-byline > a.yt-uix-sessionlink.spf-link");
		if(ele.size()>1 || ele.size()==0) {
			System.out.println("Error!"+ele.size()+"elements found for channel");
			logwriter.write("Error!"+ele.size()+"elements found for channel");
			throw new Exception();
		}
		String songChannelFromYoutube=ele.get(0).text();

		return songChannelFromYoutube;
	}

	public static boolean checkIfResultIsPlaylistUsingJSoup(Element songContainer) {
		Elements playlistContainer=songContainer.select("ol.yt-lockup-meta.yt-lockup-playlist-items");
		if(playlistContainer.size()>0) {
			return true;
		}
		return false;
	}

	public static int calculateDurationDifferenceBetweenYoutubeAndMSD(Element songContainer,int durationFromDB) throws Exception {
		int durationFromYouTube=getDurationFromYouTube(songContainer);
		return Math.abs(durationFromYouTube-durationFromDB);
	}

	public static int getDurationFromYouTube(Element songContainer) throws Exception {
		//		Elements ele=songContainer.select("span.style-scope.ytd-thumbnail-overlay-time-status-renderer");
		Elements ele=songContainer.select("h3.yt-lockup-title > span.accessible-description");
		if(ele.size()>1 || ele.size()==0) {
			System.out.println("Error!"+ele.size()+"elements found for duration");
			logwriter.write("Error!"+ele.size()+"elements found for duration");
			throw new Exception();
		}
		String durationFromYoutube=ele.get(0).text();
		int i = 0;
		while (i < durationFromYoutube.length() && !Character.isDigit(durationFromYoutube.charAt(i))) i++;
		durationFromYoutube=durationFromYoutube.substring(i,durationFromYoutube.length());
		durationFromYoutube=durationFromYoutube.substring(0, durationFromYoutube.indexOf(".")) + durationFromYoutube.substring(durationFromYoutube.indexOf(".") + 1);
		String[] splitArray=durationFromYoutube.split(":");
		if(splitArray.length>2 || splitArray.length==0) {
			System.out.println("Error!Split array for duration is"+splitArray.length+"..The duration was"+durationFromYoutube);
			logwriter.write("Error!Split array for duration is"+splitArray.length+"..The duration was"+durationFromYoutube);
			throw new Exception();
		}
		int minutes=Integer.parseInt(splitArray[0]);
		int seconds=Integer.parseInt(splitArray[1]);

		int totalSeconds=minutes*60+seconds;
		return totalSeconds;
	}

	public static boolean checkDurationGreaterThanAnHourFromYouTube(Element songContainer) throws Exception {
		Elements ele=songContainer.select("h3.yt-lockup-title > span.accessible-description");
		if(ele.size()>1 || ele.size()==0) {
			System.out.println("Error!"+ele.size()+"elements found for duration");
			logwriter.write("Error!"+ele.size()+"elements found for duration");
			return true;
		}
		String durationFromYoutube=ele.get(0).text();
		int i = 0;
		while (i < durationFromYoutube.length() && !Character.isDigit(durationFromYoutube.charAt(i))) i++;
		durationFromYoutube=durationFromYoutube.substring(i,durationFromYoutube.length());
		durationFromYoutube=durationFromYoutube.substring(0, durationFromYoutube.indexOf(".")) + durationFromYoutube.substring(durationFromYoutube.indexOf(".") + 1);
		String[] splitArray=durationFromYoutube.split(":");
		if(splitArray.length>2 || splitArray.length==0) {
			return true;
		}
		return false;
	}

	public static boolean establishMySqlConnectionWithNewMusixMatchDB(){
		try{  
			conmysql=DriverManager.getConnection("jdbc:mysql://localhost:3306/musixmatch_new","root","root");  
			if(conmysql!=null)
			{
				System.out.println("Connected to mysql");
			}
		}catch(Exception e){ System.out.println(e); return false;}
		return true;
	}

	private static Writer returnlogger() {
		try {
			logger=new FileWriter("D:\\projectdatabases\\logWhereDidDownloadStop.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
}

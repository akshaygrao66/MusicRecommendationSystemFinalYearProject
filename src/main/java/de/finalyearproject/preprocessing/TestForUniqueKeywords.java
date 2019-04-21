package de.finalyearproject.preprocessing;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
public class TestForUniqueKeywords {
	
	public static void main(String []args) {
		
		try {
			ArrayList<String> allLyricsKeywords = new ArrayList<String>();			
			ArrayList<String> allBadKeywords = new ArrayList<String>();			
			ArrayList<String> allLyricsKeywordsUnique = new ArrayList<String>();
			FileInputStream fis = new FileInputStream("D:\\projectdatabases\\MUSIXMATCH\\mxm_train_word2.txt");
			byte bb[] = new byte[fis.available()];
			fis.read(bb);
			fis.close();
			
			String data = new String(bb);
			data = data.trim();
			allLyricsKeywords.clear();
			StringTokenizer st = new StringTokenizer(data,"\r\n");
			while(st.hasMoreTokens()) {
				String token = st.nextToken().toString();
				StringTokenizer st1 = new StringTokenizer(token,",");
					while(st1.hasMoreTokens()) {
						allLyricsKeywords.add(st1.nextToken())	;
				}
			}
			
			System.out.println(allLyricsKeywords.size());
			allLyricsKeywordsUnique.clear();
			allLyricsKeywordsUnique = (ArrayList<String>)allLyricsKeywords.clone();
			
			Set set = new HashSet(allLyricsKeywordsUnique);
			allLyricsKeywordsUnique.clear();
			allLyricsKeywordsUnique.addAll(set);
			System.out.println(allLyricsKeywordsUnique.size());
			allBadKeywords.clear();
			System.out.println("\'Akshay\'");
			for(int i=0;i<allLyricsKeywordsUnique.size();i++) {
				String keyword = allLyricsKeywordsUnique.get(i);
				int freq = Collections.frequency(allLyricsKeywords,keyword);
				if(freq>1) {
					System.out.println(keyword+":"+freq+":"+i);
					//allLyricsKeywordsUnique.remove(i);
					allBadKeywords.add(keyword);
				}
			}
			
			allLyricsKeywordsUnique.removeAll(allBadKeywords);			
			System.out.println("----------------");
			for(int i=0;i<allLyricsKeywordsUnique.size();i++) {
				String keyword = allLyricsKeywordsUnique.get(i);
				int freq = Collections.frequency(allLyricsKeywords,keyword);
				if(freq==1) {
					System.out.println(keyword+":"+freq+":"+i);
					
				}
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	}

}

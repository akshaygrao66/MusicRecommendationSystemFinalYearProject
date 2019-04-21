package de.mph_web.webscrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestMusixmatchMSDMapping {

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("D:\\projectdatabases\\MUSIXMATCH\\mxm_779k_matches.txt"));
		StringBuilder stringBuilder = new StringBuilder();
		char[] buffer = new char[10];
		while (reader.read(buffer) != -1) {
			stringBuilder.append(new String(buffer));
			buffer = new char[10];
		}
		reader.close();

		String val = stringBuilder.toString();
		
		
		//String val="enbnb<SEP>djnbsbnns<SEP>wenwogn\n"+"bvawvb<SEP>nbsanb<SEP>esvnbn\n";
		val=val.replace("\n", "<SEP>").replace("\r", "<SEP>");
		String[] arr=val.split("<SEP>");
		int i=0;
		boolean flag=true;
		for(int j=0;j<41;j=j+3)
		{
			if(flag)
			{
				System.out.println("TrackID -"+arr[j]);
			}
			else
			{
				System.out.println("mXm ID -"+arr[j]);
			}
			flag=!flag;
		}
		System.out.println("\n"+arr.length);
	}

}

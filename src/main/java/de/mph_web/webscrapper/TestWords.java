package de.mph_web.webscrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestWords {

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("D:\\projectdatabases\\MUSIXMATCH\\mxm_train_word2.txt"));
		StringBuilder stringBuilder = new StringBuilder();
		char[] buffer = new char[10];
		while (reader.read(buffer) != -1) {
			stringBuilder.append(new String(buffer));
			buffer = new char[10];
		}
		reader.close();

		String val = stringBuilder.toString();
		String[] arr=val.split(",");
		for(String s:arr)
		{
			System.out.println(s);
		}
		System.out.println(arr.length);
	}
}

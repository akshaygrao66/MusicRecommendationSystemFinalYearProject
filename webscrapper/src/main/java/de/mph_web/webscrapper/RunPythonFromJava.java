package de.mph_web.webscrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunPythonFromJava {
	public static void main(String[] argv) throws IOException
	{
		String s = null;
		String command = "python C:\\Users\\aksha\\OneDrive\\Desktop\\Million-Song-Dataset-HDF5-to-CSV-master\\hdf5_getters.py D:\\projectdatabases\\MSD\\MillionSongSubset\\data\\A\\A\\A\\TRAAABD128F429CF47.h5 get_tempo";
		try {
		Process p = Runtime.getRuntime().exec(command);
		BufferedReader stdInput = new BufferedReader(new 
                InputStreamReader(p.getInputStream()));

           BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

           // read the output from the command
           System.out.println("Here is the standard output of the command:\n");
           while ((s = stdInput.readLine()) != null) {
               System.out.println(s);
           }
           
           // read any errors from the attempted command
           System.out.println("Here is the standard error of the command (if any):\n");
           while ((s = stdError.readLine()) != null) {
               System.out.println(s);
           }
           
           System.exit(0);
       }
       catch (IOException e) {
           System.out.println("exception happened - here's what I know: ");
           e.printStackTrace();
           System.exit(-1);
       }
	}
}

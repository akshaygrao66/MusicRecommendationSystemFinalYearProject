package de.mph_web.webscrapper;

import java.io.File;

public class PutAllFilesInOneDirectory {

	public static void main(String[] args) {
		File currDir=new File("D:\\projectdatabases\\LASTFM\\lastfm_subset\\B");
		File toDir=new File("D:\\projectdatabases\\LASTFM\\lastfm_subset");
		move(currDir,toDir,0);
	}
	public static void move(File currDir,File toDir,int i)
	{
		System.out.println("*******"+i+"***"+currDir.getPath());
		for (File file : currDir.listFiles()) {
            if (file.isDirectory()) {
                move(file, toDir,i+1);
                file.delete();
            } else {
                file.renameTo(new File(toDir, file.getName()));
            }
        }
	}
}

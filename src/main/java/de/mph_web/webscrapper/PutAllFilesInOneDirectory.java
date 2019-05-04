package de.mph_web.webscrapper;

import java.io.File;
import java.io.FilenameFilter;

public class PutAllFilesInOneDirectory {

	public static void main(String[] args) {
		File currDir=new File("D:\\projectdatabases\\LASTFM\\lastfm_train");
		File toDir=new File("D:\\projectdatabases\\LASTFM\\lastfm_train");
		String[] directories=currDir.list(new FilenameFilter() {
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
			});
		for(int i=0;i<directories.length;i++) {
			File temp=new File("D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_train\\"+directories[i]);
			move(temp,toDir,0);
		}
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

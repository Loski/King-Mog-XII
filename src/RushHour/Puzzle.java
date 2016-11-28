package RushHour;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class Puzzle {

	private String difficulty;
	private String txtFileLocation;
	
	public Puzzle(String difficulty,String path)
	{
		this.txtFileLocation=path;
		this.difficulty=difficulty;
	}
	
	public String getDifficulty()
	{
		return this.difficulty;
	}
	
	public String getTxtFileLocation()
	{
		return this.txtFileLocation;
	}
	
	public String toString()
	{
		String s = String.format("%-20s", "["+this.difficulty+"]");
		return  s + getTxtFileName();
	}
	
	public String getTxtFileName()
	{
		return this.txtFileLocation.substring(this.txtFileLocation.lastIndexOf("\\")+1);
	}
	
	public static ArrayList<Puzzle> getListofPuzzle()
	{
		ArrayList<Puzzle> list = new ArrayList<Puzzle>();	
		
	      /*File location = new File("./puzzles/");
	      File[] dir = location.listFiles();
	      FileFilter fileFilter = new FileFilter() {
	         public boolean accept(File file) {
	            return file.isDirectory();
	         }
	      };
	      
	      dir = location.listFiles(fileFilter);
	      */
	      
		 File[] dir = new File[4];
		 dir[0]=new File("./puzzles/débutant/");
		 dir[1]=new File("./puzzles/intermédiaire/");
		 dir[2]=new File("./puzzles/avancé/");
		 dir[3]=new File("./puzzles/expert/");
		 //dir[4]=new File("./puzzles/debug/");
		
	      FileFilter fileTextFilter = new FileFilter() {
		         public boolean accept(File file) {
		            return file.getName().endsWith(".txt");
		         }
		      };
	      
	      for(File directory :dir)
	      {
	    	  File difficulty = new File(directory.getPath());
		      File[] rusHoursFromDifficulty = difficulty.listFiles(fileTextFilter);
		      
		      if(rusHoursFromDifficulty!=null)
		    	  for(File rushHourInstance : rusHoursFromDifficulty)
		    		  list.add(new Puzzle(difficulty.getName(),rushHourInstance.getPath()));
	      }
	      
		return list;
	}
}

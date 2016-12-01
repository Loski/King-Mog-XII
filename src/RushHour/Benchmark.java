package RushHour;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public abstract class Benchmark {
	
	public static void main(String[] args)
	{
		ArrayList<Puzzle> puzzles = Puzzle.getListofPuzzle();
		Object[] puzzArr = puzzles.toArray();
		 Arrays.sort(puzzArr, new Comparator<Object>()
		 {
			@Override
			public int compare(Object o1, Object o2) {
		         String fileName1 = (((Puzzle)o1).getTxtFileName());
		         String fileName2 = (((Puzzle)o2).getTxtFileName());
		         
		         int fileId1 = Integer.parseInt(fileName1.split("m")[1].split(".txt")[0]);
		         int fileId2 = Integer.parseInt(fileName2.split("m")[1].split(".txt")[0]);

		         return fileId1 - fileId2;
			}
		 });
		 
		 File f = new File ("Bench.csv");
		 
		 long startTime,endTime,duration;
		 
		 Object[] result;
		 
		 PrintWriter pw=null;;
		 
		 
		try {
			pw = new PrintWriter (new BufferedWriter (new FileWriter (f)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		 
		if(pw!=null)
		{
		 try
		 {
		     pw.println ("Nom du Fichier;Difficulté;Nombre de sommets;tempsCreationConfiguration;tempsRHCDij;ValRHCDij;tempsRHMDij;valRHMDij;tempsRHCGuro;valRHCGuro;tempsRHMGuro;valRHMGuro");
			 
		     for(int i=20;i<31;i++){
		    	 Puzzle p = (Puzzle)puzzArr[i];
		    	 
		    	 System.out.println("I DO : "+p.getTxtFileName());
		    	 
				 RushHour r = new RushHour(p.getTxtFileLocation());
				 pw.print(String.format("\n%s;%s;", p.getTxtFileName(), p.getDifficulty()));
				 
				 startTime = System.nanoTime();
				 
				 
				 GrapheConfiguration g = new GrapheConfiguration(r);
				 
				 endTime = System.nanoTime();
				 duration = (endTime - startTime); 
				 
				 pw.print(g.getConfigurations().size()+";");
				 pw.print((duration/1000000)+";");
				 

				 startTime = System.nanoTime();
				 
				 result = DijkstraSolver.resolveRHC(g.getListe_adj(),g.getConfigurations());
				 
				 endTime = System.nanoTime();
				 duration = (endTime - startTime); 

				 pw.print((duration/1000000)+";");
				 pw.print(result[0]+";");
				 
				 startTime = System.nanoTime();
				 
				 result = DijkstraSolver.resolveRHM(g.getListe_adj(),g.getConfigurations());
				 
				 endTime = System.nanoTime();
				 duration = (endTime - startTime); 

				 pw.print((duration/1000000)+";");
				 pw.print((((ArrayList<RushHour>) result[1]).size()-1)+";");
				 
				 int N = p.getN();
				 startTime = System.nanoTime();
				 result = new GurobiSolver(r, N).solve(RushHour.RHC);
				 endTime = System.nanoTime();
				 duration = (endTime - startTime);
				 pw.print((duration/1000000)+";");
				 pw.print(result[0]+";");
				 startTime = System.nanoTime();
				 result = new GurobiSolver(r, N).solve(RushHour.RHM);
				 endTime = System.nanoTime();
				 duration = (endTime - startTime);
				 pw.print((duration/1000000)+";");
				 if(result[2]!=null)
					 pw.print(result[2]+";");
				 else
					 pw.print(result[0]+";");
				 
			 }
		  
		     pw.close();
		 }
		 catch (Exception exception)
		 {
		     System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
		     pw.close();
		 }
		}

			 
		System.out.println("FINISHED");
	}
}

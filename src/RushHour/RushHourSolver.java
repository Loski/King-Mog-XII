package RushHour;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class RushHourSolver {
	private RushHour r;
	private GrapheConfiguration g;
	private ArrayList<RushHour> sequence;
	
	public RushHourSolver()
	{
		do
		{
			Scanner sc = new Scanner(System.in);
			System.out.print("Nom du fichier :");
			String filename = sc.nextLine();
			File puzzle = new File("puzzles/" + filename + ".txt");
			if(puzzle.exists())
				this.r = new RushHour("puzzles/" + filename + ".txt");
			else
				System.out.println("Le fichier n'existe pas");
				
		}while(r==null);
	}
	
	public RushHourSolver(String filename)
	{
		this.r = new RushHour(filename);
	}
	
	private void waitForNext()
	{
		Scanner sc = new Scanner(System.in);
	    System.out.print("Press any key to continue . . . ");
	    sc.nextLine();		
	}
	
	public void chosirProbleme()
	{
		boolean badChoice = false;
		do
		{
			badChoice =false;
			try
			{
			    System.out.println("Résoudre un problème de : ");
			    System.out.println("(1) Rush Hour Mouvements");
			    System.out.println("(2) Rush Hour Cases\n");
				
			    Scanner scanner = new Scanner(System.in);
			    int choix = scanner.nextInt();
		
			    switch (choix) {
			        case 1:
			        	chosirMethode(1);
			            break;
			        case 2:
			        	chosirMethode(2);
			            break;
			        case 0: //Quitter
			            break;
			        default:
			            badChoice=true;
			            break;
			    }
			}catch(Exception e){badChoice=true;};
			
		}while(badChoice);
	}
	
	public void chosirMethode(int probleme)
	{
		boolean badChoice = false;
		do
		{
			badChoice =false;
			
			try
			{
				System.out.println("Utiliser la méthode : ");
			    System.out.println("(1) Programmation linéaire");
			    System.out.println("(2) Algorithme de Dijkstra\n");
				
			    Scanner scanner = new Scanner(System.in);
			    int choix = scanner.nextInt();
		
			    switch (choix) {
			        case 1:
			        	resolveLinear(probleme);			        	
			            break;
			        case 2:
			        	resolveGraphe(probleme);
			            break;
			        case 0: //Quitter
			            break;
			        default:
			            badChoice=true;
			            break;
			    }
			}catch(Exception e){badChoice=true;e.printStackTrace();};
			
		}while(badChoice);
	}
	
	public void resolveLinear(int probleme)
	{
		if(probleme == 1)
		{
			
		}
		
		else
		{
			
		}
	}
	
	public void resolveGraphe(int probleme)
	{
		long startTime = System.nanoTime();
		int nbCaseDeplace=-1;
		
		//Recréer le graphe des configs
		if(this.g==null || ! this.g.getConfigurations().get(0).equals(r))
			this.g = new GrapheConfiguration(r);
		
		if(probleme == 1)
		{
			Object[] result = DijkstraSolver.resolveRHM(g.getMatrice_adj(), g.getConfigurations(), g.getIndexOfSolutions().get(0));
			nbCaseDeplace=(int) result[0];
			this.sequence=(ArrayList<RushHour>) result[1];
		}
		
		else
		{
			System.out.println(g.getConfigurations().size());
			Object[] result = DijkstraSolver.resolveRHC(g.getMatrice_adj(), g.getConfigurations(), g.getIndexOfSolutions().get(0));
			nbCaseDeplace=(int) result[0];
			this.sequence=(ArrayList<RushHour>) result[1];
		
		}
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
		
		System.out.println("Nombre de Configuration-But : "+this.g.getIndexOfSolutions().size());
		System.out.println("Nombre minimal de mouvement : "+(sequence.size()-1));
		System.out.println("Nombre minimal de case : "+nbCaseDeplace);
		
		System.out.println("ALGO WAS DONE IN "+duration/1000000+" ms");
		
		beginSequence(sequence);
		

	}
	
	public void beginSequence(ArrayList<RushHour> sequence)
	{
		int i =1;
		
		for(RushHour rh: sequence)
		{
			System.out.println(String.format("-------ETAPE(%d)---------",i));
			System.out.println((rh));
			//Dire quelle pièce a bougée ??
			System.out.println("-----------------------");
			waitForNext();
			i++;
		}
		
		i++;
	}
	
	public RushHour getRushHour()
	{
		return this.r;
	}
	
	public static void main(String[] args)
	{
		RushHourSolver solver;
		
		if(args.length>0)
			solver = new RushHourSolver(args[0]);
		else 
			solver = new RushHourSolver();
				
		solver.getRushHour().afficher();
		
		solver.chosirProbleme();
		
	}
}

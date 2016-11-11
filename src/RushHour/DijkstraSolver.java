package RushHour;

import java.util.ArrayList;
import java.util.Collections;

public abstract class DijkstraSolver {
	
	private static int[] resolve(ArrayList<ArrayList<Integer>> matrice_adj, ArrayList<RushHour> configurations)
	{
		int[] distances = new int[matrice_adj.size()];
		int[] predecesseurs = new int[matrice_adj.size()];
		ArrayList<Integer> sommetsNonMarques = new ArrayList<Integer>();
		
		for(int i=0;i<distances.length;i++)
		{
			sommetsNonMarques.add(i);
			distances[i] = Integer.MAX_VALUE;
			predecesseurs[i] = i;
		}
		
		distances[0]=0;
		
		boolean finished = false;
		
		int nvSommetMarque=0;
		
		while((!finished) && sommetsNonMarques.size()>0)
		{		
			int minDistance=Integer.MAX_VALUE;
			
			for(Integer sommet : sommetsNonMarques)
			{
				if(distances[sommet.intValue()]<=minDistance)
				{
					nvSommetMarque=sommet.intValue();
					minDistance=distances[sommet.intValue()];					
				}
			}
			
			if(configurations.get(nvSommetMarque).isSolution())
			{
				finished=true;
			}
			
			else
			{
				sommetsNonMarques.remove(Integer.valueOf(nvSommetMarque));
				int i=0;
				
				
				for(Integer poids : matrice_adj.get(nvSommetMarque))
				{
					//si le poids à [i][j]>=0 alors j est voisin de i			
					if(poids.intValue()>=0)
					{						
						if(distances[i]> distances[nvSommetMarque] + poids.intValue())
							predecesseurs[i]=nvSommetMarque;
						
						distances[i]=Math.min(distances[i],distances[nvSommetMarque] + poids.intValue());					

					}
					
					i++;
				}
			}
			
			/*System.out.println("DISTANCE + sommet( "+ nvSommetMarque +" )");
			for(int i=0;i<distances.length;i++)
			{
				System.out.print(distances[i]+"\t");
			}
			
			System.out.println();
			
			System.out.println("SUCCESEUR + sommet( "+ nvSommetMarque +" )");
			for(int i=0;i<distances.length;i++)
			{
				System.out.print(predecesseurs[i]+"\t");
			}
			
			System.out.println("\n");*/
			
		}		
		return predecesseurs;
	}
	
	private static ArrayList<RushHour> createSequence(int[] predecesseurs,ArrayList<RushHour> configurations,int indexOfSolution)
	{
		boolean configDepart=false;
		ArrayList<RushHour> sequence = new ArrayList<RushHour>();
		sequence.add(configurations.get(indexOfSolution));
		int currentRushHour=0;
		
		while(!configDepart)
		{
			currentRushHour=predecesseurs[currentRushHour];
			sequence.add(configurations.get(currentRushHour));
			
			if(configurations.get(currentRushHour).equals(configurations.get(0)))
				configDepart=true;		
			
		}
		
		Collections.reverse(sequence);
		
		return sequence;
	}
	
	
	public static ArrayList<RushHour> resolveRHC(ArrayList<ArrayList<Integer>> matrice_adj, ArrayList<RushHour> configurations,int indexOfSolution)
	{
		int[] predecesseurs = resolve(matrice_adj, configurations);
		
		return createSequence(predecesseurs, configurations, indexOfSolution);
	}
	
	public static ArrayList<RushHour> resolveRHM(ArrayList<ArrayList<Integer>> matrice_adj, ArrayList<RushHour> configurations,int indexOfSolution)
	{
		ArrayList<ArrayList<Integer>> copy = new ArrayList<ArrayList<Integer>>();
		
		for(int i=0;i<matrice_adj.size();i++)
		{
			ArrayList<Integer> line = new ArrayList<Integer>();
			
			for(int j=0;j<matrice_adj.get(i).size();j++)
			{
				if(matrice_adj.get(i).get(j)>=0)
					line.add(1);	
				else
					line.add(-1);
			}
			
			copy.add(line);
		}
		
		
		int[] predecesseurs = resolve(copy, configurations);
		return createSequence(predecesseurs, configurations, indexOfSolution);
	}
	
	
}

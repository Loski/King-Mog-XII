package RushHour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class DijkstraSolver {
	
	private static int[][] resolve(HashMap<Integer,HashMap<Integer,Integer>> liste_adj, ArrayList<RushHour> configurations)
	{
		int[] distances = new int[configurations.size()];
		int[] predecesseurs = new int[configurations.size()];
		ArrayList<Integer> sommetsNonMarques = new ArrayList<Integer>();
		
		for(int i=0;i<distances.length;i++)
		{
			sommetsNonMarques.add(i);
			distances[i] = Integer.MAX_VALUE-1;
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
				if(distances[sommet.intValue()]<minDistance)
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
				
				for (Entry<Integer, Integer> successeur : liste_adj.get(Integer.valueOf(nvSommetMarque)).entrySet()) 
				{					
						int distanceToSucc = successeur.getValue().intValue();
						int indexOfSucc = successeur.getKey().intValue();
						
						int distanceBefore = distances[indexOfSucc];
						
						distances[indexOfSucc]=Math.min(distances[indexOfSucc],distances[nvSommetMarque] + distanceToSucc);
						
						if(distances[indexOfSucc]!=distanceBefore)
							predecesseurs[indexOfSucc]=nvSommetMarque;
				}
				
			}		
		}		
		
		int[][] result = new int[3][];
		result[0] = distances; 
		result[1] = predecesseurs;
		int [] sommet = new int[1];
		sommet[0] = nvSommetMarque;
		result[2] = sommet;
		
		return result;
	}
	
	private static ArrayList<RushHour> createSequence(int[] predecesseurs,ArrayList<RushHour> configurations,int indexOfSolution)
	{
		boolean configDepart=false;
		ArrayList<RushHour> sequence = new ArrayList<RushHour>();
		sequence.add(configurations.get(indexOfSolution));
		int currentRushHour=indexOfSolution;
		
		while(!configDepart)
		{
			currentRushHour=predecesseurs[currentRushHour];
			sequence.add(configurations.get(currentRushHour));
			
			if(currentRushHour==0)
				configDepart=true;		
			
		}
		
		//System.out.println("\n"+sequence.size());
		
		Collections.reverse(sequence);
		
		return sequence;
	}
	
	
	public static Object[] resolveRHC(HashMap<Integer,HashMap<Integer,Integer>> liste_adj, ArrayList<RushHour> configurations)
	{	
		long startTime = System.nanoTime();
		
		int[][] resultDij = resolve(liste_adj, configurations);
		
		int[] predecesseurs = resultDij[1];
		int[] distance = resultDij[0];
		int indexOfSolution = resultDij[2][0];
		
		int nbCaseDeplace = distance[indexOfSolution];
			
		ArrayList<RushHour> sequence = createSequence(predecesseurs, configurations, indexOfSolution);
		
		Object[] result = new Object[2];
		result[0] = nbCaseDeplace;
		result[1] = sequence;
		
		long endTime = System.nanoTime();

		long duration = (endTime - startTime); 
		
		/*System.out.println("DIJKSTRA WAS DONE IN "+duration/1000000+" ms");
		System.out.println("Nombre de Sommet = " + configurations.size());*/
		
		return result;
	}
	
	public static Object[] resolveRHM(HashMap<Integer,HashMap<Integer,Integer>> liste_adj, ArrayList<RushHour> configurations)
	{
		HashMap<Integer,HashMap<Integer,Integer>> copy = new HashMap<Integer,HashMap<Integer,Integer>>();
		
		for (Entry<Integer, HashMap<Integer, Integer>> successeurs : liste_adj.entrySet()) {
			
			copy.put(successeurs.getKey(),new HashMap<Integer,Integer>());
			for (Entry<Integer, Integer> sommet : successeurs.getValue().entrySet()) {
				copy.get(successeurs.getKey()).put(sommet.getKey(),1);
			}
		}
		
		/*for (Entry<Integer, HashMap<Integer, Integer>> successeurs : liste_adj.entrySet()) {
			for (Entry<Integer, Integer> sommet : successeurs.getValue().entrySet()) {
				sommet.setValue(1);
			}
		}*/
		
		
		int[][] resultDij = resolve(copy, configurations);
		
		int[] predecesseurs = resultDij[1];
		int indexOfSolution = resultDij[2][0];
		
		
		int nbCaseDeplace = calculCaseDeplaceeRHM(liste_adj,predecesseurs,indexOfSolution);
			
		ArrayList<RushHour> sequence = createSequence(predecesseurs, configurations, indexOfSolution);
		
		Object[] result = new Object[2];
		result[0] = nbCaseDeplace;
		result[1] = sequence;
		
		return result;
	}

	private static int calculCaseDeplaceeRHM(HashMap<Integer, HashMap<Integer, Integer>> liste_adj, int[] predecesseurs,int indexOfSolution) {

		boolean configDepart=false;
		int currentRushHour=indexOfSolution;
		
		int nbCase=0;
		
		while(!configDepart)
		{			
			nbCase+=liste_adj.get(Integer.valueOf(currentRushHour)).get(Integer.valueOf(predecesseurs[currentRushHour]));
			currentRushHour=predecesseurs[currentRushHour];
			
			if(currentRushHour==0)
				configDepart=true;		
			
		}
		
		return nbCase;
		
	}
	
	
}

import java.util.ArrayList;

public abstract class Dijkstra {
	
	public static void resolveRHM(ArrayList<ArrayList<Integer>> matrice_adj, ArrayList<RushHour> configurations)
	{
		int[] distances = new int[matrice_adj.size()];
		int[] predecesseur = new int[matrice_adj.size()];
		ArrayList<Integer> sommetsNonMarques = new ArrayList<Integer>();
		
		for(int i=1;i<distances.length;i++)
		{
			sommetsNonMarques.add(i);
			distances[i] = Integer.MAX_VALUE;
		}
		
		distances[0]=0;
		
		boolean finished = false;
		
		while(!finished)
		{
			int minDistance=Integer.MAX_VALUE;
			int nvSommetMarque=0;
			
			for(Integer sommet : sommetsNonMarques)
			{
				if(distances[sommet.intValue()]<minDistance)
				{
					minDistance=distances[sommet.intValue()];
					nvSommetMarque=sommet.intValue();
					predecesseur[sommet.intValue()]=nvSommetMarque;
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
						distances[i]=Math.min(distances[i], poids.intValue());
						i++;
					}
				}
			}
			
		}
		
	}
}

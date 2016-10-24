import java.util.ArrayList;

public class GrapheConfiguration {
	
	private ArrayList<ArrayList<int[]>> matrice_adj; 
	private ArrayList<RushHour> configurations;
	
	public GrapheConfiguration(RushHour configDepart)
	{
		this.configurations=new ArrayList<RushHour>();
		this.matrice_adj=new ArrayList<ArrayList<int[]>>();
		
		int[] arrete = {0,0};
		ArrayList<int[]> intersect = new ArrayList<int[]>();
		intersect.add(arrete);
		this.matrice_adj.add(intersect);
		
		addSommet(configDepart);
	}
	
	public void addSommet(RushHour r)
	{
		//TESTER SI LE SOMMET a.k.a la grille de r EXISTE ??
		
		this.configurations.add(r);
		ArrayList<int[]> newSommet = new ArrayList<int[]>();
		this.matrice_adj.add(newSommet);
		
		
		for(int i=0;i<this.matrice_adj.size();i++)
		{
			int[] arrete = {0,0};
			ArrayList<int[]> intersect = new ArrayList<int[]>();
			intersect.add(arrete);
				
			 // ajout nv colonne correspondant au nouveau sommet pour tous les sommets existants
			this.matrice_adj.get(i).add(arrete);			
			
			//ajout chaque cellule pour la ligne du nouveau sommet sauf la dernière qui est faite avant
			if(i!=this.matrice_adj.size()-1)
				newSommet.add(arrete);
		}		
	}
	
	public void setSuccesseur(int sommetPere, int sommetFils, int cout)
	{
		int[] arrete = {1,cout};
		this.matrice_adj.get(sommetPere).set(sommetFils, arrete);
	}
	
	public boolean isSuccesseur(int sommetPere,int sommetFils)
	{
		if(this.matrice_adj.get(sommetPere).get(sommetFils)[0]==1)
			return true;
			
		return false;
	}
	
	public int getCout(int sommetPere,int sommetFils)
	{
		if(!isSuccesseur(sommetPere, sommetFils))
			return -1;
		
		return this.matrice_adj.get(sommetPere).get(sommetFils)[1];
	}
	
	public void afficherMatrice()
	{		
		for(int i=0;i<this.matrice_adj.size();i++)
		{
			for(int j=0;j<this.matrice_adj.get(i).size();j++)
			{
				System.out.print(String.format("%d[%d]\t", this.matrice_adj.get(i).get(j)[0], this.matrice_adj.get(i).get(j)[1]));
			}
			
			System.out.println();
		}
	}
	
	public static void main(String[] args)
	{
		//RushHour r1 = new RushHour("puzzles/débutant/jam1.txt");
		RushHour r1 = new RushHour("puzzles/debug.txt"); 
		GrapheConfiguration g1 = new GrapheConfiguration(r1);
		g1.afficherMatrice();
	}
	
	
}

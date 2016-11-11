package RushHour;
import java.util.ArrayList;

import RushHour.RushHour.Direction;
public class GrapheConfiguration {
	
	private ArrayList<ArrayList<Integer>> matrice_adj; //contient les poids si poids[i][j]>=0 alors arrête entre i et j  
	private ArrayList<RushHour> configurations;
	public ArrayList<ArrayList<Integer>> getMatrice_adj() {
		return matrice_adj;
	}

	public void setMatrice_adj(ArrayList<ArrayList<Integer>> matrice_adj) {
		this.matrice_adj = matrice_adj;
	}

	public ArrayList<RushHour> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(ArrayList<RushHour> configurations) {
		this.configurations = configurations;
	}

	public GrapheConfiguration(RushHour configDepart)
	{
		this.configurations=new ArrayList<RushHour>();
		this.matrice_adj=new ArrayList<ArrayList<Integer>>();
		
		/*int[] arrete = {0,0};
		ArrayList<Integer> intersect = new ArrayList<Integer>();
		intersect.add(-1);
		this.matrice_adj.add(intersect);*/
		addSommet(configDepart);
	}
	
	public void creerGraphe(RushHour r){
		int[] all_direction = {Direction.BACKWARD, Direction.FORWARD};
		int[] all_orientation = {Orientation.HORIZONTAL, Orientation.VERTICAL};
		System.out.println(r);
		for(Vehicule v: r.getVehicules())
			for(int direction:all_direction){
				for(int orientation:all_orientation){
					RushHour tmp = (RushHour) r.clone();
					while(tmp.deplacement_1(v, direction, orientation)){
						if(!this.configurations.contains(tmp)){
							addSommet(tmp);
							System.out.println(tmp);
							creerGraphe(tmp);
						}
					}
				}
			}
	}
	public void addSommet(RushHour r)
	{
		//TESTER SI LE SOMMET a.k.a la grille de r EXISTE ??
		
		this.configurations.add(r);
		ArrayList<Integer> newSommet = new ArrayList<Integer>();
		this.matrice_adj.add(newSommet);
		
		
		for(int i=0;i<this.matrice_adj.size();i++)
		{
			//int[] arrete = {0,0};
			ArrayList<Integer> intersect = new ArrayList<Integer>();
			intersect.add(-1);
				
			 // ajout nv colonne correspondant au nouveau sommet pour tous les sommets existants
			this.matrice_adj.get(i).add(-1);			
			
			//ajout chaque cellule pour la ligne du nouveau sommet sauf la dernière qui est faite avant
			if(i!=this.matrice_adj.size()-1)
				newSommet.add(-1);
		}		
	}
	
	public void setSuccesseur(int sommetPere, int sommetFils, int cout)
	{
		//int[] arrete = {1,cout};
		this.matrice_adj.get(sommetPere).set(sommetFils, cout);
	}
	
	public boolean isSuccesseur(int sommetPere,int sommetFils)
	{
		if(this.matrice_adj.get(sommetPere).get(sommetFils)>=0)
			return true;
			
		return false;
	}
	
	/*public int getCout(int sommetPere,int sommetFils)
	{
		if(!isSuccesseur(sommetPere, sommetFils))
			return -1;
		
		return this.matrice_adj.get(sommetPere).get(sommetFils);
	}*/
	
	public void afficherMatrice()
	{		
		for(int i=0;i<this.matrice_adj.size();i++)
		{
			for(int j=0;j<this.matrice_adj.get(i).size();j++)
			{
				System.out.print(String.format("%d\t", this.matrice_adj.get(i).get(j)));
			}
			
			System.out.println();
		}
	}
	
	public static void main(String[] args)
	{
		//RushHour r1 = new RushHour("puzzles/débutant/jam1.txt");
		RushHour r1 = new RushHour("puzzles/debug.txt"); 
		GrapheConfiguration g1 = new GrapheConfiguration(r1);		
		g1.creerGraphe(g1.getConfigurations().get(0));
	}
	
	
}

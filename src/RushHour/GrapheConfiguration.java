package RushHour;

import java.util.ArrayList;
import RushHour.RushHour.Direction;

public class GrapheConfiguration {
	
	private ArrayList<ArrayList<Integer>> matrice_adj; //contient les poids si poids[i][j]>=0 alors arrête entre i et j  
	private ArrayList<Integer> indexOfSolutions; 
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
	
	public ArrayList<Integer> getIndexOfSolutions()
	{
		return this.indexOfSolutions;
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
		creerGraphe(0);
		System.out.println(this.configurations.size());
	}
	
	public void creerGraphe(int index){
				
		RushHour r = this.configurations.get(index);
		
		int[] all_direction = {Direction.FORWARD, Direction.BACKWARD};
		int[] all_orientation = {Orientation.HORIZONTAL, Orientation.VERTICAL};
		//System.out.println(r);
		for(Vehicule v: r.getVehicules())
			for(int direction:all_direction){
					
					for(int j = 1; j < 6; j++){
						RushHour tmp = (RushHour) r.clone();
						boolean s = tmp.deplacement_multiple(v, direction, v.getOrientation(), j);
						if(s){
							//if(!this.configurations.contains(tmp)){
							if(!this.configurations.contains(tmp)){
							//System.out.println(tmp);		
							/*	System.out.println("MUST ADD : "+tmp);
								System.out.println("IN :");
								for(RushHour r0 : this.configurations)
									System.out.println(r0);
								System.out.println("\n COMPARE TO ("+this.configurations.size()+") :"+this.configurations.contains(tmp));*/
								addSommet(tmp);	
								setSuccesseur(index, this.configurations.size()-1,1);
								setSuccesseur(this.configurations.size()-1,index,j);
							}
						}
						else
							j=10;
				}
			}
		
		//Si on a ajouté une config
		if(index+1<this.configurations.size())
			creerGraphe(index+1);
		
		return;
	}
	
	public void addSommet(RushHour r)
	{
		
		if(r == null)
			System.out.println("CAY NULL");
		//System.out.println(this.configurations.size()+ " : \n" +r);
		
		//System.out.println("J'ai ajouté : "+r);
		
		if(r.isSolution())
			indexOfSolutions.add(this.configurations.size());
		
		this.configurations.add(r);
		
		/*System.out.println("NV TABLEAU :");
		
		for(RushHour r0 : this.configurations)
			System.out.println(r0);*/
		
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
	}
	
	
}

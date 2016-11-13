package RushHour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import RushHour.RushHour.Direction;

public class GrapheConfiguration {
	
	private ArrayList<ArrayList<Integer>> matrice_adj; //contient les poids si poids[i][j]>=0 alors arrête entre i et j  
	private HashMap<Integer,HashMap<Integer,Integer>> liste_adj;
	private ArrayList<Integer> indexOfSolutions; 
	private ArrayList<RushHour> configurations;
	private static final int[] all_direction = {Direction.FORWARD, Direction.BACKWARD};
	
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
		this.indexOfSolutions = new ArrayList<Integer>();
		
		this.liste_adj= new HashMap<>();
		
		/*int[] arrete = {0,0};
		ArrayList<Integer> intersect = new ArrayList<Integer>();
		intersect.add(-1);
		this.matrice_adj.add(intersect);*/
		
		addSommet(configDepart);
		int nodeToTest = 0;
		do{
			creerGraphe(nodeToTest);
			nodeToTest++;
		}while(nodeToTest<this.configurations.size());
		System.out.println("OVER");
		//System.out.println(this.configurations.size());
	}
	
	public void creerGraphe(int index){	
		//System.out.println(configurations.size());
		RushHour r = this.configurations.get(index);
		ArrayList<RushHour> tmp = new ArrayList<RushHour>();
		int i = 0;
		for(Vehicule v :this.configurations.get(index).getVehicules()){
			for(int direction:all_direction){
				boolean changement = true;
				int taille_max = 6;
				int position_initial = this.configurations.get(index).getVehicules().get(i).getPosition();
				if(this.configurations.get(index).getVehicules().get(i).getOrientation() == Orientation.HORIZONTAL){
					if(direction == Direction.FORWARD)
						taille_max = r.getNbColonne() - (position_initial%r.getNbColonne() + this.configurations.get(index).getVehicules().get(i).getTaille());
					else
						taille_max = position_initial%r.getNbColonne();
				}
				else{
					if(direction == Direction.FORWARD)
						taille_max = r.getNbLigne() -((int)position_initial/r.getNbColonne() + this.configurations.get(index).getVehicules().get(i).getTaille());
					else taille_max = (int)position_initial/r.getNbColonne();
				}
					if(tmp == null)
						tmp = new ArrayList<RushHour>();
					if(tmp.isEmpty())
						tmp.add(r);
					tmp = tmp.get(0).deplacement_multiple(i, direction, taille_max);
					int j = 0;
					if(tmp != null){
					while(!tmp.isEmpty()){
						j++;
						RushHour tmp_config = tmp.remove(0);
						if(!this.configurations.contains(tmp_config)){
							addSommet(tmp_config);	
						}
						if(index!=this.configurations.size()-1)
						{
							setSuccesseur(index, this.configurations.indexOf(tmp_config),j);
							setSuccesseur(this.configurations.indexOf(tmp_config),index,j);
						}
					}
				}
			}
		i++;
	}
		
		return;
	}
	
	public void addSommet(RushHour r)
	{
		//System.out.println(this.configurations.size());

		if(r.isSolution())
			indexOfSolutions.add(this.configurations.size());
		
		this.configurations.add(r);
		
		this.liste_adj.put(Integer.valueOf(this.configurations.size()-1), new HashMap<>());
	}
	
	public void setSuccesseur(int sommetPere, int sommetFils, int cout)
	{		
		HashMap<Integer,Integer> succ = this.liste_adj.get(Integer.valueOf(sommetPere));
		if(succ!=null)
			succ.put(Integer.valueOf(sommetFils), cout);
	}
	
	public boolean isSuccesseur(int sommetPere,int sommetFils)
	{
		if(this.matrice_adj.get(sommetPere).get(sommetFils)>=0)
			return true;
			
		return false;
	}
	
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
	
	public int getNBofIntInMatrice(int a)
	{
		int compteur=0;
		
		for(int i=0;i<this.matrice_adj.size();i++)
		{
			for(int j=0;j<this.matrice_adj.get(i).size();j++)
			{
				if(matrice_adj.get(i).get(j).equals(a))
					compteur++;
			}
		}
		
		return compteur;
	}
	
	public HashMap<Integer,HashMap<Integer,Integer>> getListe_adj()
	{
		return this.liste_adj;
	}
	
	public static void main(String[] args)
	{
		RushHour r1 = new RushHour("puzzles/débutant/jam1.txt");		
		GrapheConfiguration g1 = new GrapheConfiguration(r1);
		//System.out.println(g1.getIndexOfSolutions().get(0));
		DijkstraSolver.resolveRHC(g1.getListe_adj(), g1.getConfigurations(), g1.getIndexOfSolutions().get(0));
		//g1.afficherMatrice();
		//System.out.println(g1.getNBofIntInMatrice(2));
	}
	
	
}

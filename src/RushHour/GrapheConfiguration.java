package RushHour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class GrapheConfiguration {
	
	private HashMap<Integer,HashMap<Integer,Integer>> liste_adj;
	private ArrayList<Integer> indexOfSolutions; 
	private ArrayList<RushHour> configurations;
	private static final byte[] all_direction = {RushHour.FORWARD, RushHour.BACKWARD};

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
		this.indexOfSolutions = new ArrayList<Integer>();
		
		this.liste_adj= new HashMap<>();
		
		/*int[] arrete = {0,0};
		ArrayList<Integer> intersect = new ArrayList<Integer>();
		intersect.add(-1);
		this.matrice_adj.add(intersect);*/
		
		addFirstSommet(configDepart);
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
		byte i = 0;
		byte taille_max = 6;
		for(Vehicule v :r.getVehicules())
		{		
			byte position_initial = v.getPosition();
			
			for(byte direction:all_direction){
				if(v.getOrientation() == RushHour.HORIZONTAL){
					if(direction == RushHour.FORWARD)
						taille_max = (byte) (r.getNbColonne() - (position_initial%r.getNbColonne() + v.getTaille()));
					else
						taille_max = (byte) (position_initial%r.getNbColonne());
				}
				else{
					if(direction == RushHour.FORWARD)
						taille_max = (byte) (r.getNbLigne() -((int)position_initial/r.getNbColonne() + v.getTaille()));
					else taille_max = (byte) ((int)position_initial/r.getNbColonne());
				}
					//boolean quit = false;
					RushHour result = r;
					for(int j=1;j<=taille_max;j++)
					{						
						result = result.deplacement_1(v, (v.getHash()-1), direction);
						if(result==null || this.configurations.contains(result))
						{
							//quit=true;
							break;
						}
						else
						{
							addNextSommet(result,index,j);
							/*if(index!=lastIndex)
							{
								setSuccesseur(index, lastIndex,j);
								setSuccesseur(lastIndex,index,j);
							}*/
						}
					}
					
				/*if(quit)
					break;*/
			}
			i++;
		}
	}
	
	public void addFirstSommet(RushHour r)
	{
		this.configurations.add(r);
		
		if(r.isSolution())
			indexOfSolutions.add(0);
		
		this.liste_adj.put(Integer.valueOf(0), new HashMap<>());
	}
	
	public void addNextSommet(RushHour r,int previousNode,int costToReach)
	{
		//System.out.println(this.configurations.size());
		
		int lastIndex=this.configurations.size();
		
		this.configurations.add(r);
		
		if(r.isSolution())
			indexOfSolutions.add(lastIndex);
		
		this.liste_adj.put(Integer.valueOf(lastIndex), new HashMap<>());
		
		HashMap<Integer,Integer> succ = this.liste_adj.get(Integer.valueOf(previousNode));
		succ.put(Integer.valueOf(lastIndex), costToReach);
		
		HashMap<Integer,Integer> succReverse = this.liste_adj.get(Integer.valueOf(lastIndex));
			succReverse.put(Integer.valueOf(previousNode), costToReach);
	}
	
	/*public void setSuccesseur(int sommetPere, int sommetFils, int cout)
	{		
		HashMap<Integer,Integer> succ = this.liste_adj.get(Integer.valueOf(sommetPere));
		if(succ!=null)
			succ.put(Integer.valueOf(sommetFils), cout);
	}*/
	
	public void afficherMatrice()
	{		
		for (Entry<Integer, HashMap<Integer, Integer>> successeurs : liste_adj.entrySet()) {
			for (Entry<Integer, Integer> sommet : successeurs.getValue().entrySet()) {
				System.out.println(successeurs.getKey()+" -> "+sommet.getKey()+" = "+sommet.getValue());
			}
			System.out.println("");
		}
	}
	
	/*public int getNBofIntInMatrice(int a)
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
	}*/
	
	public HashMap<Integer,HashMap<Integer,Integer>> getListe_adj()
	{
		return this.liste_adj;
	}
	
	public static void main(String[] args)
	{
		RushHour r1 = new RushHour("puzzles/debug.txt");		
		GrapheConfiguration g1 = new GrapheConfiguration(r1);
		//System.out.println(g1.getIndexOfSolutions().get(0));
		//DijkstraSolver.resolveRHC(g1.getListe_adj(), g1.getConfigurations(), g1.getIndexOfSolutions().get(0));
		g1.afficherMatrice();
	}
	
	
}

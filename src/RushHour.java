import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RushHour {

	public static final int caseSortie = 8; //17;
	
	private ArrayList<ArrayList<String>> grille;
	private HashMap<String,Integer> marqueurs;
	private ArrayList<Vehicule> vehicules;
	private int nbLigne;
	private int nbColonne;
	
	
	
	public boolean deplacement_1(Vehicule vehicule, int direction, int orientation_deplacement){
		if(orientation_deplacement != vehicule.getOrientation())
			return false;
		boolean deplacement_possible = true;
		int sommet_depart = marqueurs.get(vehicule.getCode()).intValue();
		int taille = vehicule.getTaille();
		
		if(vehicule.getOrientation() == Orientation.HORIZONTAL){ 
			if(direction == Direction.FORWARD)
				if(sommet_depart%nbColonne + taille + direction > nbColonne)
					deplacement_possible = false;
			else{ //BACKWARD
				if(sommet_depart%nbColonne + direction < 0){  //Hors tableau
					deplacement_possible = false;
				}
			}
		}
		else{  // VERTICAL
			if(direction == Direction.FORWARD)
				if(sommet_depart%nbLigne + taille + direction > nbLigne)
					deplacement_possible = false;
			else{ //BACKWARD
				if(sommet_depart%nbLigne + direction < 0){  //Hors tableau
					deplacement_possible = false;
				}
			}
		}
		if(deplacement_possible){
			deplacementRushHour(vehicule, direction, orientation_deplacement);
		}
		return deplacement_possible;
	}
	
	
	
	private void deplacementRushHour(Vehicule vehicule, int direction, int orientation){
		String nom_vehicule = vehicule.getCode();
		
		//On supprime la voiture de la grille 
		supprimerVehiculeGrille(vehicule, this.marqueurs.get(nom_vehicule).intValue(), orientation);
		
		this.marqueurs.replace(nom_vehicule, this.marqueurs.get(nom_vehicule).intValue()+direction);// old value + direction
		//on recréé la voiture dans la grille !
		creerVehiculeGrille(vehicule, this.marqueurs.get(nom_vehicule).intValue(), orientation);
	}
	
	public void supprimerVehiculeGrille(Vehicule vehicule, int initialPosition, int orientation){
		modifieVehiculeGrille(vehicule, initialPosition, orientation, "0");
	}
	public void creerVehiculeGrille(Vehicule vehicule, int initialPosition, int orientation){
		modifieVehiculeGrille(vehicule, initialPosition, orientation, vehicule.getCode());
	}
	
	public void modifieVehiculeGrille(Vehicule vehicule, int initialPosition, int orientation, String new_value){
		if(orientation == Orientation.HORIZONTAL){
			for(int i = 0; i < vehicule.getTaille(); i++){
				grille.get(initialPosition%nbColonne).set(i + initialPosition, new_value);
			}
		}
		else{
			for(int i = 0; i < vehicule.getTaille(); i++){
				grille.get(initialPosition%nbColonne).set(i*nbLigne + initialPosition, new_value);
			}
		}
	}
	
	public Vehicule findVehicule(Vehicule voiture){
		return null;
	}
	public RushHour(String filename)
	{
		BufferedReader buffer;
		String x;
		this.grille = new ArrayList<ArrayList<String>>();
		this.marqueurs = new HashMap<String,Integer>();
		this.vehicules = new ArrayList<Vehicule>();
		
        try 
        {
        	int i=0;
            buffer = new BufferedReader(new FileReader(filename));
            
            if((x = buffer.readLine()) != null)	
            {
            	//System.out.println(x);
            	
            	Scanner scanner = new Scanner(x);
            	this.nbLigne = scanner.nextInt();
            	this.nbColonne = scanner.nextInt();
            	scanner.close();
            }
            
            while ( (x = buffer.readLine()) != null ) {
                // printing out each line in the file
                //System.out.println(x);
            	
            	ArrayList<String> ar = new ArrayList<String>();
            	
            	StringTokenizer st = new StringTokenizer(x," ");
            	while(st.hasMoreElements())
            	{
            		String s = (String)st.nextElement();
            		ar.add(s);
            		if(!this.marqueurs.containsKey(s) && !s.equals("0"))
            		{
            			this.marqueurs.put(s,i);
            			if(s.startsWith("c"))
            				this.vehicules.add(new Voiture(s));
            			else
            				this.vehicules.add(new Camion(s));
            		}
            		
            		//Ajout orientation
            		else if(this.marqueurs.containsKey(s))
            		{
            			boolean find = false;
            			int j=0;
            			while(!find)
            			{
            				if(vehicules.get(j).getCode().equals(s))
            				{
            					find=true;
            					if( i - marqueurs.get(s) == this.nbColonne)
            						vehicules.get(j).setOrientation(Orientation.VERTICAL);
            					else
            						vehicules.get(j).setOrientation(Orientation.HORIZONTAL);
            				}
            				
            				j++;
            			}
            		}
            		i++;
            	}
            	
            	this.grille.add(ar);
            }
            
        }catch(Exception e){e.printStackTrace();}
	}
	
	public void afficher()
	{
		for(int i=0;i<this.nbLigne;i++)
		{			
			for(int j=0;j<this.nbColonne;j++)
			{
				System.out.print(this.grille.get(i).get(j)+"\t");
			}
			
			System.out.println();
		}
		
		//DEBUG MARQUEUR
		System.out.println();
		Iterator<String> ite = marqueurs.keySet().iterator();
		 
		while (ite.hasNext())
		{
		    String key = ite.next();
		    System.out.println(key+" :" + this.marqueurs.get(key));
		}
		
		//DEBUG VEHICULES
		System.out.println();
		for(Vehicule v : vehicules)
		{
			System.out.println(v.getCode() +" "+v.getOrientation());
		}
	}
	
	public String toString()
	{
		String s ="";
		
		for(int i=0;i<this.nbLigne;i++)
		{			
			for(int j=0;j<this.nbColonne;j++)
			{
				s+=this.grille.get(i).get(j)+"\t";
			}
			
			s+="\n";
		}	
		
		return s;
	}
	
	public boolean equals(RushHour r2)
	{
		for(int i=0;i<this.nbLigne;i++)
		{			
			for(int j=0;j<this.nbColonne;j++)
			{
				if(!this.grille.get(i).get(j).equals(r2.grille.get(i).get(j)))
					return false;
			}
		}
		
		return true;
	}
	
	public ArrayList<ArrayList<String>> getGrille()
	{
		return this.grille;
	}
	
	public HashMap<String,Integer> getMarqueurs()
	{
		return this.marqueurs;
	}
	
	public boolean isSolution()
	{
		if(this.marqueurs.get("g")==RushHour.caseSortie)
			return true;
		else
			return false;
	}
	
	public static void main(String[] args)
	{
		//RushHour r1 = new RushHour("puzzles/débutant/jam1.txt");
		RushHour r1 = new RushHour("puzzles/debug.txt"); 
		r1.afficher();
		r1.deplacement_1(r1.vehicules.get(1), Direction.FORWARD, Orientation.VERTICAL);
		r1.afficher();
		System.out.println(r1.vehicules.get(1));
	}

	static class Direction{
		 public final static int BACKWARD = 1;
		 public final static int FORWARD = -1;
	}
}

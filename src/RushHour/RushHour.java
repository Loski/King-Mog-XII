package RushHour;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RushHour implements Cloneable {

	public static final int caseSortie = 16;
	
	private int[] grille;
	private ArrayList<Vehicule> vehicules;
	public static final int taille_matrice =6;
	public static int indice_solution_g;
	public static final int EMPTY = 0;

	public ArrayList<RushHour> deplacement_multiple(Vehicule v, int index, int direction, int nombre_deplacement){
        ArrayList<RushHour> sommetAccessible = new ArrayList<RushHour>();
        RushHour tmp = this;
		for(int i = 0; i < nombre_deplacement; i++){
			tmp = tmp.deplacement_1(v, index, direction);
			if(tmp!= null)
				sommetAccessible.add(tmp);
			else
				break;
            
       }
       return (sommetAccessible.isEmpty())?new ArrayList<RushHour>():sommetAccessible;
	}
	
	public RushHour deplacement_1(Vehicule v, int index, int direction){
		boolean deplacement_possible = false;
		int sommet_depart = v.getPosition();
		int taille = v.getTaille();
		
		if(v.getOrientation() == Orientation.HORIZONTAL){ 
			
			if(direction == Direction.FORWARD){
				if(sommet_depart%taille_matrice + taille < taille_matrice && this.grille[sommet_depart + taille] == RushHour.EMPTY)
					deplacement_possible = true;
			}
			else{ //BACKWARD
				if(sommet_depart%taille_matrice + Direction.BACKWARD >= 0  && this.grille[sommet_depart + Direction.BACKWARD] == RushHour.EMPTY){  //Hors tableau
					deplacement_possible = true;
				}
			}
		}
		else{  // VERTICAL
			if(direction == Direction.FORWARD){
				if((int)sommet_depart/taille_matrice + taille < taille_matrice && this.grille[sommet_depart  + (taille) * taille_matrice] == RushHour.EMPTY)
					deplacement_possible = true;
			}
				else{ //BACKWARD
					if((int)(sommet_depart/taille_matrice + Direction.BACKWARD) >= 0 && (this.grille[sommet_depart + Direction.BACKWARD * taille_matrice] == RushHour.EMPTY )){  //Hors tableau
						deplacement_possible = true;
					}
				}
		}
		if(deplacement_possible){
			RushHour clone = (RushHour) this.clone();
			clone.deplacementRushHour(v, index, direction);
			return clone;
		}
		return null;
	}
		
	
	private void deplacementRushHour(Vehicule vBase, int index, int direction){		
		
		supprimerVehiculeGrille(vBase, index);
		//Vehicule v = this.vehicules.get(index);
		Vehicule v = (Vehicule) vBase.clone();
		int old_value = v.getPosition();
		if(v.getOrientation() == Orientation.HORIZONTAL)
			v.setPosition(old_value + direction); //OLD + DEPLACEMENTS
		else
			v.setPosition(old_value +(direction*taille_matrice));
		this.vehicules.set(index, v);
		//on recréé la voiture dans la grille !
		creerVehiculeGrille(v, index);
	}
	
	public void supprimerVehiculeGrille(Vehicule v, int index){
		modifieVehiculeGrille(v, index, RushHour.EMPTY);
	}
	public void creerVehiculeGrille(Vehicule v, int index){
		modifieVehiculeGrille(v, index, v.getHash());
	}
	
	public void modifieVehiculeGrille(Vehicule v, int index, int new_value){
		int initialPosition = v.getPosition();
		this.grille = Arrays.copyOf(this.grille, taille_matrice * taille_matrice);
		if(v.getOrientation() == Orientation.HORIZONTAL){
			for(int i = 0; i < v.getTaille(); i++){
				this.grille[initialPosition + i] = new_value;
			}
		}
		else{
			for(int i = 0; i < v.getTaille(); i++){
				this.grille[initialPosition + i * getNbLigne()] = new_value;
			}
		}
	}
	
	public RushHour(RushHour r){	
		this.grille = Arrays.copyOf(r.getGrille(),  RushHour.taille_matrice*RushHour.taille_matrice);
		this.vehicules = new ArrayList<Vehicule>();
		for(Vehicule v : r.vehicules){
			this.vehicules.add((Vehicule) v.clone());
		}
		
		/*r.vehicules = new ArrayList<Vehicule>();
		for(Vehicule v : this.vehicules){
			r.vehicules.add((Vehicule) v.clone());
		}*/
	}
	public RushHour(){}
	public RushHour(String filename)
	{
		BufferedReader buffer;
		String x;
		ArrayList<ArrayList<String>>  tmp = new ArrayList<ArrayList<String>>();
		this.grille = new int[RushHour.taille_matrice * RushHour.taille_matrice];
		this.vehicules = new ArrayList<Vehicule>();
        try 
        {
            buffer = new BufferedReader(new FileReader(filename));
            
            if((x = buffer.readLine()) != null)	
            {
            	//System.out.println(x);
            	int walid;
            	Scanner scanner = new Scanner(x);
            	walid = scanner.nextInt();
            	walid = scanner.nextInt();
            	scanner.close();
            }
            
            while ( (x = buffer.readLine()) != null && !x.equals("")) {
                // printing out each line in the file
                //System.out.println(x);
            	
            	ArrayList<String> ar = new ArrayList<String>();	
            	StringTokenizer st = new StringTokenizer(x," ");
            	while(st.hasMoreElements())
            	{
            		String s = (String)st.nextElement();
            		ar.add(s);
            	}
            	tmp.add(ar);
            }
            			
            		//Ajout orientation
            /*		else if(marqueurs.containsKey(s) && !s.equals("0"))
            		{          			
            			boolean find = false;
            			int j=0;
            			while(!find)
            			{
            				if(vehicules.get(j).getCode().equals(s) && vehicules.get(j).getOrientation()!= Orientation.NO_DIRECTION)
            				{
            					find=true;
            					break;
            				}
            				
            				if(vehicules.get(j).getCode().equals(s))
            				{
            					find=true;
            					
            					if(i%taille_matrice!=0 && ar.get((int)(i-1)%taille_matrice).equals(s))
            						vehicules.get(j).setOrientation(Orientation.HORIZONTAL);
            					else
            						vehicules.get(j).setOrientation(Orientation.VERTICAL);
            				}
            				
            				j++;
            			}
            		}
            		i++;
            	}*/
            int indice;
            	
            for(int v =0; v < RushHour.taille_matrice; v++)
            	for(int j = 0; j < RushHour.taille_matrice;j++){
            		boolean vehicule = true;
            		String s = tmp.get(v).get(j);
            		if(s.startsWith("c")){
        				this.vehicules.add(new Voiture(v*getNbLigne()+j));
        			}
        			else if(s.startsWith("t")){
        				this.vehicules.add(new Camion(v*getNbLigne()+j));
        			}
        			else if(s.startsWith("g")){
        				this.vehicules.add(new Voiture(v*getNbLigne()+j));
        				RushHour.indice_solution_g = this.vehicules.size()-1;
        			}
        			else
        				vehicule = false;
            		if(vehicule){
    					if(tmp.get(v).get((int)(j+1)%taille_matrice).equals(s))
    						vehicules.get(this.vehicules.size()-1).setOrientation(Orientation.HORIZONTAL);
    					else
    						vehicules.get(this.vehicules.size()-1).setOrientation(Orientation.VERTICAL);
            			for(int z = 0; z < 6; z++)
            			while(( indice = tmp.get(z).indexOf(s)) != -1){
            				tmp.get(z).set(indice,new Integer(Vehicule.getCompteur_voiture()-1).toString());
            			}
            		}
            	}
            for(int v =0; v < RushHour.taille_matrice; v++)
            	for(int j = 0; j < RushHour.taille_matrice;j++){
            		this.grille[v*RushHour.taille_matrice + j] = Integer.parseInt(tmp.get(v).get(j).toString());
            	}           
        }catch(Exception e){e.printStackTrace();}
	}
	
	
	public void afficher()
	{
		for(int i=0;i<RushHour.taille_matrice;i++)
		{			
			for(int j=0;j<RushHour.taille_matrice;j++)
			{
				System.out.print(this.grille[i*getNbColonne() + j]+"\t");
			}
			
			System.out.println();
		}
	}
	public Object clone(){
		try {
			RushHour r = new RushHour();
			r.grille = Arrays.copyOf(this.grille,  RushHour.taille_matrice*RushHour.taille_matrice);
			r.vehicules = new ArrayList<Vehicule>();
			for(Vehicule v : this.vehicules){
				r.vehicules.add((Vehicule) v.clone());
			}
			
			return r;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(this.grille);
		}
		return null;
	}
	public String toString()
	{
		String s ="";
		
		for(int i=0;i<RushHour.taille_matrice;i++)
		{			
			for(int j=0;j<RushHour.taille_matrice;j++)
			{
				s+=this.grille[i*getNbColonne() + j]+"\t";
			}
			
			s+="\n";
		}	
		return s;
	}
	
	public boolean equals(Object other)
	{	      		
		RushHour r2 = (RushHour) other;
		
		if(r2==this)
			return true;		
		
		for(int i =0; i < RushHour.taille_matrice*RushHour.taille_matrice;i++)
		{			
				if(this.grille[i] != r2.grille[i])
					return false;
		}
		return true;
	}
	
	public boolean isSolution()
	{
		if(this.vehicules.get(indice_solution_g).getPosition() ==RushHour.caseSortie)
			return true;
		else
			return false;
	}
	

	public ArrayList<Vehicule> getVehicules() {
		return vehicules;
	}

	public int getNbLigne() {
		return taille_matrice;
	}

	public int getNbColonne() {
		return taille_matrice;
	}
	
	public int[] getGrille()
	{
		return this.grille;
	}

	public void setVehicules(ArrayList<Vehicule> vehicules) {
		this.vehicules = vehicules;
	}
	
	static class Direction{
		 public final static int BACKWARD = -1;
		 public final static int FORWARD = 1;
	}
	
	static class Orientation {
		 public final static int HORIZONTAL = 0;
		 public final static int VERTICAL = 1;
		 public final static int NO_DIRECTION = -1;
	}
}

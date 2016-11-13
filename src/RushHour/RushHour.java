package RushHour;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RushHour implements Cloneable {

	public static final int caseSortie = 16;
	
	private String[][] grille;
	private ArrayList<Vehicule> vehicules;
	public static final int taille_matrice =6;
	public static int indice_solution_g;

	public ArrayList<RushHour> deplacement_multiple(int index, int direction, int nombre_deplacement){
        ArrayList<RushHour> sommetAccessible = new ArrayList<RushHour>();
        RushHour tmp = this;
		for(int i = 0; i < nombre_deplacement; i++){
			tmp = tmp.deplacement_1(index, direction);
			if(tmp!= null)
				sommetAccessible.add(tmp);
			else
				break;
            
       }
       return (sommetAccessible.isEmpty())?new ArrayList<RushHour>():sommetAccessible;
	}
	
	public RushHour deplacement_1(int index, int direction){
		boolean deplacement_possible = false;
		int sommet_depart = this.vehicules.get(index).getPosition();
		int taille = this.vehicules.get(index).getTaille();
		
		if(this.vehicules.get(index).getOrientation() == Orientation.HORIZONTAL){ 
			
			if(direction == Direction.FORWARD){
				if(sommet_depart%taille_matrice + taille < taille_matrice && this.grille[(int)sommet_depart/taille_matrice][sommet_depart%taille_matrice + taille].equals("0"))
					deplacement_possible = true;
			}
			else{ //BACKWARD
				if(sommet_depart%taille_matrice + direction >= 0  && this.grille[(int)sommet_depart/taille_matrice][sommet_depart%taille_matrice + direction].equals("0")){  //Hors tableau
					deplacement_possible = true;
				}
			}
		}
		else{  // VERTICAL
			if(direction == Direction.FORWARD){
				if((int)sommet_depart/taille_matrice + taille < taille_matrice && this.grille[(int)sommet_depart/taille_matrice + taille][sommet_depart%taille_matrice].equals("0"))
					deplacement_possible = true;
			}
				else{ //BACKWARD
					if((int)(sommet_depart/taille_matrice + Direction.BACKWARD) >= 0 && (this.grille[(int)sommet_depart/taille_matrice + Direction.BACKWARD][sommet_depart%taille_matrice].equals("0"))){  //Hors tableau
						deplacement_possible = true;
					}
				}
		}
		if(deplacement_possible){
			RushHour clone = (RushHour) this.clone();
			clone.deplacementRushHour(index, direction);
			return clone;
		}
		return null;
	}
		
	
	private void deplacementRushHour(int index, int direction){		
		
		supprimerVehiculeGrille(index);
		Vehicule v = this.vehicules.get(index);
		int old_value = v.getPosition();
		if(v.getOrientation() == Orientation.HORIZONTAL)
			v.setPosition(old_value + direction); //OLD + DEPLACEMENTS
		else
			v.setPosition(old_value +(direction*taille_matrice));
		this.vehicules.set(index, (Vehicule) v.clone());
		//on recréé la voiture dans la grille !
		creerVehiculeGrille(index);
	}
	
	public void supprimerVehiculeGrille(int index){
		modifieVehiculeGrille(index, "0");
	}
	public void creerVehiculeGrille(int index){
		modifieVehiculeGrille(index, this.vehicules.get(index).getCode());
	}
	
	public void modifieVehiculeGrille(int index, String new_value){
		Vehicule v = this.vehicules.get(index);
		int initialPosition = v.getPosition();
		int ligne =(int)initialPosition/taille_matrice;
		if(v.getOrientation() == Orientation.HORIZONTAL){
			String[] s = grille[ligne];
			for(int i = 0; i < v.getTaille(); i++){
				s[i + initialPosition%taille_matrice] = new_value;
			}
		}
		else{
			for(int i = 0; i < v.getTaille(); i++){
				String[] s = grille[ligne];
				s[initialPosition%taille_matrice] = new_value;
				ligne+=1; // changement de ligne
			}
		}
	}
	
	public RushHour(RushHour r){
		this.grille = new String[6][6];
		for(int i = 0; i < RushHour.taille_matrice; i++){
			for(int j = 0; j < RushHour.taille_matrice; j++){
				this.grille[i][j] = new String(r.grille[i][j]);
			}
		}
		r.vehicules = new ArrayList<Vehicule>();
		for(Vehicule v : this.vehicules){
			r.vehicules.add((Vehicule) v.clone());
		}
	}
	public RushHour(){}
	public RushHour(String filename)
	{
		BufferedReader buffer;
		String x;
		this.grille = new String[6][6];
		HashMap<String,Integer> marqueurs = new HashMap<String,Integer>();
		this.vehicules = new ArrayList<Vehicule>();
		ArrayList<ArrayList<String>> tmp = new ArrayList<ArrayList<String>>();
        try 
        {
        	int i=0;
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
            		if(!marqueurs.containsKey(s) && !s.equals("0"))
            		{
            			marqueurs.put(s,i);
            			if(s.startsWith("c"))
            				this.vehicules.add(new Voiture(s, Orientation.NO_DIRECTION, i));
            			else if(s.startsWith("t"))
            				this.vehicules.add(new Camion(s, Orientation.NO_DIRECTION, i));
            			else if(s.startsWith("g")){
            				this.vehicules.add(new Voiture(s, Orientation.NO_DIRECTION, i));
            				RushHour.indice_solution_g = this.vehicules.size()-1;
            			}
            		}
            		
            		//Ajout orientation
            		else if(marqueurs.containsKey(s) && !s.equals("0"))
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
            	}
            	tmp.add(ar);
            }
            for(int v =0; v < RushHour.taille_matrice; v++)
            	for(int j = 0; j < RushHour.taille_matrice;j++)
            		this.grille[v][j] = tmp.get(v).get(j);
            		
            
        }catch(Exception e){e.printStackTrace();}
	}
	
	public void afficher()
	{
		for(int i=0;i<RushHour.taille_matrice;i++)
		{			
			for(int j=0;j<RushHour.taille_matrice;j++)
			{
				System.out.print(this.grille[i][j]+"\t");
			}
			
			System.out.println();
		}
		
		
		//DEBUG VEHICULES
		System.out.println();
		for(Vehicule v : vehicules)
		{
			System.out.println(v.getCode() +" "+v.getOrientation());
		}
	}
	public Object clone(){
		try {
			RushHour r = new RushHour();
			r.grille = new String[6][6];
			for(int i = 0; i < RushHour.taille_matrice; i++){
				for(int j = 0; j < RushHour.taille_matrice; j++){
					r.grille[i][j] = new String(grille[i][j]);
				}
			}
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
				s+=this.grille[i][j]+"\t";
			}
			
			s+="\n";
		}	
		return s;
	}
	
	public boolean equals(Object other)
	{	
		if (other== null || (other.getClass() != RushHour.class))
			return false;
       		
		RushHour r2 = (RushHour) other;
		
		if(r2==this)
			return true;
		if(r2.vehicules.size()!=this.vehicules.size())
			return false;
		
		
		for(int i =0; i < RushHour.taille_matrice;i++)
		{			
			for(int j = 0; j<RushHour.taille_matrice; j++){
				if(!this.grille[i][j].equals(r2.grille[i][j]))
					return false;
			}	
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
	
	public String[][] getGrille()
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
}

package RushHour;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RushHour implements Cloneable {

	public static final int caseSortie = 16;
	
	private String[][] grille;
	private HashMap<String,Integer> marqueurs;
	private ArrayList<Vehicule> vehicules;
	public static final int taille_matrice =6;
	

	public boolean deplacement_multiple(Vehicule vehicule, int direction, int orientation_deplacement, int nombre_deplacement){
        for(int i = 0; i < nombre_deplacement; i++){
            boolean tmp = deplacement_1(vehicule, direction, orientation_deplacement);
            if(!tmp)
               return false;
       }
       return true;
	}
	
	public boolean deplacement_1(Vehicule vehicule, int direction, int orientation_deplacement){
		if(orientation_deplacement != vehicule.getOrientation())
			return false;
		boolean deplacement_possible = false;
		int sommet_depart = marqueurs.get(vehicule.getCode()).intValue();
		int taille = vehicule.getTaille();
		
		if(vehicule.getOrientation() == Orientation.HORIZONTAL){ 
			
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
			deplacementRushHour(vehicule, direction, orientation_deplacement);
		}
		return deplacement_possible;
	}
	
	
	
	private void deplacementRushHour(Vehicule vehicule, int direction, int orientation){		
		String nom_vehicule = vehicule.getCode();
		
		//On supprime la voiture de la grille 
		supprimerVehiculeGrille(vehicule, this.marqueurs.get(nom_vehicule).intValue(), orientation);
		if(orientation == Orientation.HORIZONTAL)
			this.marqueurs.replace(nom_vehicule, this.marqueurs.get(nom_vehicule).intValue()+direction);// old value + direction
		else
			this.marqueurs.replace(nom_vehicule, this.marqueurs.get(nom_vehicule).intValue()+(direction*taille_matrice));
		//on recréé la voiture dans la grille !
		creerVehiculeGrille(vehicule, this.marqueurs.get(nom_vehicule).intValue(), orientation);
	}
	
	public void supprimerVehiculeGrille(Vehicule vehicule, int initialPosition, int orientation){
		modifieVehiculeGrille(vehicule, initialPosition, orientation, "0");
	}
	public void creerVehiculeGrille(Vehicule vehicule, int initialPosition, int orientation){
		modifieVehiculeGrille(vehicule, initialPosition, orientation, vehicule.getCode());
	}
	public HashMap<String,Integer> nouvelleHashMapClone(){
		HashMap<String,Integer> tmp = new HashMap<String,Integer>();
		for(String s : this.marqueurs.keySet()){
			tmp.put(new String(s), Integer.valueOf(this.marqueurs.get(s))); 
		}
		return tmp;
	}
	
	public void modifieVehiculeGrille(Vehicule vehicule, int initialPosition, int orientation, String new_value){
				
		int ligne =(int)initialPosition/taille_matrice;
		if(orientation == Orientation.HORIZONTAL){
			String[] s = grille[ligne];
			for(int i = 0; i < vehicule.getTaille(); i++){
				s[i + initialPosition%taille_matrice] = new_value;
			}
		}
		else{
			for(int i = 0; i < vehicule.getTaille(); i++){
				String[] s = grille[ligne];
				s[initialPosition%taille_matrice] = new_value;
				ligne+=1; // changement de ligne
			}
		}
	}
	
	public Vehicule findVehicule(Vehicule voiture){
		return null;
	}
	public RushHour(RushHour r){
		this.grille = new String[6][6];
		this.marqueurs = new HashMap<String,Integer>();
		this.vehicules = r.vehicules;
		for(String s : r.marqueurs.keySet()){
			this.marqueurs.put(new String(s), Integer.valueOf(r.marqueurs.get(s))); 
		}
		for(int i = 0; i < RushHour.taille_matrice; i++){
			for(int j = 0; j < RushHour.taille_matrice; j++){
				this.grille[i][j] = new String(r.grille[i][j]);
			}
		}
	}
	public RushHour(String filename)
	{
		BufferedReader buffer;
		String x;
		this.grille = new String[6][6];
		this.marqueurs = new HashMap<String,Integer>();
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
            		if(!this.marqueurs.containsKey(s) && !s.equals("0"))
            		{
            			this.marqueurs.put(s,i);
            			if(s.startsWith("c") || s.startsWith("g"))
            				this.vehicules.add(new Voiture(s));
            			else
            				this.vehicules.add(new Camion(s));
            		}
            		
            		//Ajout orientation
            		else if(this.marqueurs.containsKey(s) && !s.equals("0"))
            		{          			
            			boolean find = false;
            			int j=0;
            			while(!find)
            			{
            				if(vehicules.get(j).getCode().equals(s) && vehicules.get(j).getOrientation()!=-1)
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
            for(int v =0; v < 6; v++)
            	for(int j = 0; j < 6;j++)
            		this.grille[v][j] = tmp.get(v).get(j);
            		
            
        }catch(Exception e){e.printStackTrace();}
	}
	
	public void afficher()
	{
		for(int i=0;i<this.taille_matrice;i++)
		{			
			for(int j=0;j<this.taille_matrice;j++)
			{
				System.out.print(this.grille[i][j]+"\t");
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
	public Object clone(){
		try {
			RushHour r = (RushHour)super.clone();
			r.marqueurs = new HashMap<String,Integer>();
			for(String s : this.marqueurs.keySet()){
				r.marqueurs.put(new String(s), Integer.valueOf(this.marqueurs.get(s))); 
			}
			r.grille = new String[6][6];
			for(int i = 0; i < this.taille_matrice; i++){
				for(int j = 0; j < this.taille_matrice; j++){
					r.grille[i][j] = new String(grille[i][j]);
				}
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
		
		for(int i=0;i<this.taille_matrice;i++)
		{			
			for(int j=0;j<this.taille_matrice;j++)
			{
				s+=this.grille[i][j]+"\t";
			}
			
			s+="\n";
		}	
		
		//DEBUG MARQUEUR
		System.out.println();
		Iterator<String> ite = marqueurs.keySet().iterator();
		 
		while (ite.hasNext())
		{
		    String key = ite.next();
		    s+=key+" :" + this.marqueurs.get(key)+"\t";
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
		
		if(this.marqueurs.size()!=r2.marqueurs.size())
			return false;
		
		int i=0;
		for(Vehicule v:this.vehicules)
		{			
			if(! v.equals(r2.vehicules.get(i)))
				return false;
			
			if(!r2.marqueurs.containsKey(v.getCode()))
				return false;
			
			if(r2.marqueurs.get(v.getCode()).intValue()!=this.marqueurs.get(v.getCode()).intValue())
			{
				//System.out.println("PAS MÊME INT POUR : " +r2 + "\n" + this);
				return false;
			}
			
			i++;
		}

		return true;
		
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
		RushHour r1 = new RushHour("puzzles/expert/jam40.txt"); 
		r1.afficher();
		
		System.out.println("V :"+r1.getVehicules().size());

		r1.deplacement_1(r1.getVehicules().get(1), Direction.FORWARD, Orientation.VERTICAL);

		r1.afficher();
		System.out.println(r1.vehicules.get(0).toString() + r1.vehicules.get(0).getTaille());
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

	public void setVehicules(ArrayList<Vehicule> vehicules) {
		this.vehicules = vehicules;
	}
	
	static class Direction{
		 public final static int BACKWARD = -1;
		 public final static int FORWARD = 1;
	}
}

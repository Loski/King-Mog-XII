package RushHour;


import RushHour.RushHour.Orientation;

public abstract class Vehicule implements Cloneable{

	private int hash;
	private int orientation;
	private int position;
	private static int compteur_voiture = 1;
	public Vehicule(Vehicule v){
		this.hash=v.hash;
		this.orientation=v.orientation;
		this.setPosition(v.position);
	}
	public Vehicule(int position)
	{
		this.hash = compteur_voiture;
		this.orientation= Orientation.NO_DIRECTION;
		compteur_voiture++;
		this.position = position;
	}
	
	@Override
	protected Object clone() {
		if(this instanceof Camion)
			return new Camion(this);
		else if(this instanceof Voiture)
			return new Voiture(this);
		return null;
	}
	public int getOrientation()
	{
		return this.orientation;
	}
	

	
	public abstract int getTaille();
	
	public void setOrientation(int orientation)
	{
		this.orientation=orientation;
	}
	
	public boolean equals(Vehicule v2)
	{
		if(this.hash == v2.hash && this.position == v2.position)
			return true;
		
		return false;
				
	}
	
	public String toString(){
		return "Mon véhicule :" + this.hash + "\t";
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	public int getHash() {
		return hash;
	}
	public void setHash(int hash) {
		this.hash = hash;
	}
	public static int getCompteur_voiture() {
		return compteur_voiture;
	}
	
}

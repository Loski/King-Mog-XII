package RushHour;

public abstract class Vehicule implements Cloneable{

	private byte hash;
	private byte orientation;
	private byte position;
	private static byte compteur_voiture = 1;
	public Vehicule(Vehicule v){
		this.hash=v.hash;
		this.orientation=v.orientation;
		this.setPosition(v.position);
	}
	public Vehicule(byte position)
	{
		this.hash = compteur_voiture;
		this.orientation= RushHour.NO_DIRECTION;
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
	public byte getOrientation()
	{
		return this.orientation;
	}
	

	
	public abstract byte getTaille();
	
	public void setOrientation(byte orientation)
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

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}
	public byte getHash() {
		return hash;
	}
	public void setHash(byte hash) {
		this.hash = hash;
	}
	public static byte getCompteur_voiture() {
		return compteur_voiture;
	}
	
	public static void resetCompteur()
	{
		Vehicule.compteur_voiture=1;
	}
	
}

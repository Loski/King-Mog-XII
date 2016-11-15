package RushHour;
public class Camion extends Vehicule implements Cloneable{
	private final static byte TAILLE = 3;
	public Camion(Vehicule c){
		super(c);
	}
	
	public Camion(byte position)
	{
		super(position);
	}
	public Object clone(){
		return new Camion(this);
	}

	@Override
	public byte getTaille() {
		return Camion.TAILLE;
	}
}

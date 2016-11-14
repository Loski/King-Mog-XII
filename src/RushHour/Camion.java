package RushHour;
public class Camion extends Vehicule implements Cloneable{
	private final static int TAILLE = 3;
	public Camion(Vehicule c){
		super(c);
	}
	
	public Camion(int position)
	{
		super(position);
	}
	public Object clone(){
		return new Camion(this);
	}

	@Override
	public int getTaille() {
		return Camion.TAILLE;
	}
}

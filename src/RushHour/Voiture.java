package RushHour;

import java.util.ArrayList;
import java.util.HashMap;

public class Voiture extends Vehicule implements Cloneable{
	public static final int TAILLE = 2;
	public Voiture(Vehicule v){
		super(v);
	}
	
	public Voiture(int position)
	{
		super(position);
	}
	public Object clone(){
		return new Voiture(this);
	}
	@Override
	public int getTaille() {
		return Voiture.TAILLE;
	}
}

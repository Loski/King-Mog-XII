package RushHour;

import java.util.ArrayList;
import java.util.HashMap;

public class Voiture extends Vehicule implements Cloneable{
	public static final byte TAILLE = 2;
	public Voiture(Vehicule v){
		super(v);
	}
	
	public Voiture(byte position)
	{
		super(position);
	}
	public Object clone(){
		return new Voiture(this);
	}
	@Override
	public byte getTaille() {
		return Voiture.TAILLE;
	}
}

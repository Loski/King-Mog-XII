package RushHour;

import java.util.ArrayList;
import java.util.HashMap;

public class Voiture extends Vehicule implements Cloneable{
	
	public Voiture(String code,int direction) {
		super(code,direction);
		this.taille=2;
	}
	
	public Voiture(String code)
	{
		super(code);
		this.taille=2;
	}
	public Object clone(){
		return new Voiture(this.getCode(), this.getOrientation());
	}
}

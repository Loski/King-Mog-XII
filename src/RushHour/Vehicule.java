package RushHour;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Vehicule implements Cloneable{

	private String code;
	private int orientation;
	protected int taille;
	private int position;
	
	public Vehicule(String code,int orientation, int position)
	{
		this.code=code;
		this.orientation=orientation;
		this.setPosition(position);
	}
	public Vehicule(Vehicule v){
		this.code=v.code;
		this.orientation=v.orientation;
		this.setPosition(v.position);
		this.taille = v.taille;
	}
	public Vehicule(String code)
	{
		this.code=code;
		this.orientation=-1;
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
	
	public String getCode()
	{
		return this.code;
	}
	
	public int getTaille()
	{
		return this.taille;
	}
	
	public void setOrientation(int orientation)
	{
		this.orientation=orientation;
	}
	
	public boolean equals(Vehicule v2)
	{
		if(this.code.equals(v2.code) && this.position == v2.position && this.taille==v2.taille && this.orientation==v2.orientation)
			return true;
		
		return false;
				
	}
	
	public String toString(){
		return "Mon véhicule :" + this.code + "\t";
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}

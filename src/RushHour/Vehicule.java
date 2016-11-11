package RushHour;

public abstract class Vehicule {

	private String code;
	private int orientation;
	protected int taille;
	
	public Vehicule(String code,int orientation)
	{
		this.code=code;
		this.orientation=orientation;
	}
	
	public Vehicule(String code)
	{
		this.code=code;
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
		if(this.code.equals(v2.code) && this.taille==v2.taille && this.orientation==v2.orientation)
			return true;
		else
			return false;
				
	}
	
	public String toString(){
		return "Mon véhicule :" + this.code + "\t";
	}
}

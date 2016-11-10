
public abstract class Vehicule {

	private String code;
	private int direction;
	protected int taille;
	
	public Vehicule(String code,int direction)
	{
		this.code=code;
		this.direction=direction;
	}
	
	public int getDirection()
	{
		return this.direction;
	}
	
	public String getCode()
	{
		return this.code;
	}
	
	public int getTaille()
	{
		return this.taille;
	}
}

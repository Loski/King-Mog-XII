package RushHour;
public class Camion extends Vehicule implements Cloneable{

	public Camion(String code,int direction) {
		super(code,direction);
		this.taille=3;
	}
	
	public Camion(String code)
	{
		super(code);
		this.taille=3;
	}
	public Object clone(){
		return new Camion(this.getCode(), this.getOrientation());
	}
}

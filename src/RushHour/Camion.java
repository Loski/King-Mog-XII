package RushHour;
public class Camion extends Vehicule implements Cloneable{

	public Camion(String code,int direction, int position) {
		super(code,direction, position);
		this.taille=3;
	}
	public Camion(Vehicule c){
		super(c);
	}
	
	public Camion(String code)
	{
		super(code);
		this.taille=3;
	}
	public Object clone(){
		return new Camion(this);
	}
}

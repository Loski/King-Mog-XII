
public class Camion extends Vehicule{

	public Camion(String code,int direction) {
		super(code,direction);
		this.taille=3;
	}
	
	public Camion(String code)
	{
		super(code);
		this.taille=3;
	}
}

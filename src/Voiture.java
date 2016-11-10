
public class Voiture extends Vehicule{
	
	public Voiture(String code,int direction) {
		super(code,direction);
		this.taille=3;
	}
	
	public Voiture(String code)
	{
		super(code);
		this.taille=3;
	}
}

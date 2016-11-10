
public class Voiture extends Vehicule{
	
	public Voiture(String code,int direction) {
		super(code,direction);
		this.taille=2;
	}
	
	public Voiture(String code)
	{
		super(code);
		this.taille=2;
	}
}

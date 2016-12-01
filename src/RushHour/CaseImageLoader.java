package RushHour;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CaseImageLoader {
	
	private String theme ="default";
	private BufferedImage voitureG;
	private BufferedImage camion;
	private BufferedImage camion_h;
	private BufferedImage exit;
	private BufferedImage voiture;
	private BufferedImage voiture_h;
	
	public CaseImageLoader(String theme)
	{
		this.theme=theme;
		
		try {
			this.camion = ImageIO.read(new File("cars/"+this.theme+"/truck.png"));
			this.camion_h = ImageIO.read(new File("cars/"+this.theme+"/truck_h.png"));
		} catch (IOException e) {
		}
	}
	
	public BufferedImage[] getIMGOfCamion()
	{
		BufferedImage[] tab = new BufferedImage[2];
		tab[0]=this.camion_h;
		tab[1]=this.camion;
		
		return tab;
		
	}
	
}

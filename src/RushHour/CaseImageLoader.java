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
		
		try {this.camion = ImageIO.read(new File("cars/"+this.theme+"/truck.png"));} catch (IOException e) {}
		try {this.camion_h = ImageIO.read(new File("cars/"+this.theme+"/truck_h.png"));} catch (IOException e) {}
		try {this.exit = ImageIO.read(new File("cars/"+this.theme+"/exit.png"));} catch (IOException e) {}
		try {this.voitureG = ImageIO.read(new File("cars/"+this.theme+"/g.png"));} catch (IOException e) {}
		try {this.voiture = ImageIO.read(new File("cars/"+this.theme+"/car.png"));} catch (IOException e) {}
		try {this.voiture_h = ImageIO.read(new File("cars/"+this.theme+"/car_h.png"));} catch (IOException e) {}
		
	}
	
	public BufferedImage[] getIMGOfCamion()
	{
		BufferedImage[] tab = new BufferedImage[2];
		tab[0]=this.camion_h;
		tab[1]=this.camion;
		
		return tab;
		
	}
	
	public BufferedImage getIMGExit()
	{
		return this.exit;
		
	}
	
	public BufferedImage[] getIMGVoitureG()
	{
		BufferedImage[] buff = new BufferedImage[2];
		buff[0]=this.voitureG;
		return buff;
		
	}
	
	public BufferedImage[] getIMGOfVoiture()
	{
		BufferedImage[] tab = new BufferedImage[2];
		tab[0]=this.voiture_h;
		tab[1]=this.voiture;
		
		return tab;
		
	}
	
}

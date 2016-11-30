package RushHour;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class CaseCamionRepresentation extends JPanel{

	private int l;
	private int h;
	private byte orientation;
	private int partOfImg;
	private int tailleVoiture;
	private String theme;
	
	 public void paint(Graphics g)
	 {
		 g.setColor(Color.BLACK);
		 g.drawRect(0,0,this.getWidth(),this.getHeight());
		 
		 g.setColor(Color.DARK_GRAY);
		 g.fillRect(2,2,this.getWidth()-2,this.getHeight()-2);
		 
		 
    	 File file = null;
    	 
    	 if(this.orientation==RushHour.HORIZONTAL)
    		 file = new File("cars/"+theme+"/truck_h.png");
    	 else
    		 file = new File("cars/"+theme+"/truck.png");
    	 
		 if(file.exists() && !file.isDirectory()) { 

         BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Graphics2D g2 = (Graphics2D) g;
		
		int coupe = 0;
		if(this.orientation==RushHour.HORIZONTAL)
		{
			coupe = image.getWidth()/3;
			image = image.getSubimage(coupe*this.partOfImg,0,coupe,image.getHeight());
		}
		else
		{
			coupe = image.getHeight()/3;
			image = image.getSubimage(0,coupe*this.partOfImg,image.getWidth(),coupe);
		}
		
		
		/*if(this.orientation==RushHour.HORIZONTAL)
			g2.rotate(-Math.PI / 2, this.getWidth() / 2, this.getHeight() / 2);*/
		
			g2.drawImage(image, 1, 1, this.getWidth()-1, this.getHeight()-1, Color.WHITE, this);
		 }
	 }
	 
	  public CaseCamionRepresentation(int h, int l, byte orientation, int partOfImg,String theme)
	  {
		  this.l=l;
		  this.h=h;
		  this.orientation=orientation;
		  this.partOfImg=partOfImg;
		  this.theme=theme;
	  }
}

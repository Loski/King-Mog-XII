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

	private byte orientation;
	private int partOfImg;
	private BufferedImage IMGCamion;
	private BufferedImage IMGCamion_h;
	
	 public void paint(Graphics g)
	 {
		 g.setColor(Color.BLACK);
		 g.drawRect(0,0,this.getWidth(),this.getHeight());
		 
		 g.setColor(Color.DARK_GRAY);
		 g.fillRect(2,2,this.getWidth()-2,this.getHeight()-2);
		 
    	 BufferedImage image = null;
    	 
    	 if(this.orientation==RushHour.HORIZONTAL)
    	 {
    		 image=this.IMGCamion_h;
    	 }
    	 else
    	 {
    		 image=this.IMGCamion;
    	 }
    	 
		 if(image!=null) { 

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
	 
	  public CaseCamionRepresentation(byte orientation, int partOfImg,BufferedImage image[])
	  {
		  this.orientation=orientation;
		  this.partOfImg=partOfImg;
		  this.IMGCamion_h=image[0];
		  this.IMGCamion=image[1];
	  }
}

package RushHour;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class CaseVoitureRepresentation extends JPanel{

	private byte orientation;
	protected int partOfImg;
	protected BufferedImage imageCar;
	protected BufferedImage imageCar_h;
	
	 public void paint(Graphics g)
	 {
		 g.setColor(Color.BLACK);
		 g.drawRect(0,0,this.getWidth(),this.getHeight());
		 
		 g.setColor(Color.ORANGE);
		 g.fillRect(2,2,this.getWidth()-2,this.getHeight()-2);    	 
    	 
    	 BufferedImage image = null;
    	 
    	 if(this.orientation==RushHour.HORIZONTAL)
    	 {
    		 image=this.imageCar_h;
    	 }
    	 else
    	 {
    		 image=this.imageCar;
    	 }
    	 
		 if(image!=null) { 

		Graphics2D g2 = (Graphics2D) g;
		
		int coupe = 0;
		if(this.orientation==RushHour.HORIZONTAL)
		{
			coupe = image.getWidth()/2;
			image = image.getSubimage(coupe*this.partOfImg,0,coupe,image.getHeight());
		}
		else
		{
			coupe = image.getHeight()/2;
			image = image.getSubimage(0,coupe*this.partOfImg,image.getWidth(),coupe);
		}
		
		
		/*if(this.orientation==RushHour.HORIZONTAL)
			g2.rotate(-Math.PI / 2, this.getWidth() / 2, this.getHeight() / 2);*/
		
			g2.drawImage(image, 1, 1, this.getWidth()-1, this.getHeight()-1, Color.WHITE, this);
		 }
	 }
	 
	  public CaseVoitureRepresentation(byte orientation, int partOfImg,BufferedImage image[])
	  {
		  this.orientation=orientation;
		  this.partOfImg=partOfImg;
		  this.imageCar_h=image[0];
		  this.imageCar=image[1];
	  }
}

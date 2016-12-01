package RushHour;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CaseVoitureGRepresentation extends CaseVoitureRepresentation{

	public CaseVoitureGRepresentation(int partOfImg,BufferedImage[] img) {
		super(RushHour.HORIZONTAL, partOfImg,img);
		// TODO Auto-generated constructor stub
	}
	 
	 public void paint(Graphics g)
	 {
		 g.setColor(Color.BLACK);
		 g.drawRect(0,0,this.getWidth(),this.getHeight());
		 
		 g.setColor(Color.RED);
		 g.fillRect(2,2,this.getWidth()-2,this.getHeight()-2);
    	 
    	 BufferedImage image = image=this.imageCar_h;
    	 
		 if(image!=null) { 

		Graphics2D g2 = (Graphics2D) g;
		
			int coupe = image.getWidth()/2;
			image = image.getSubimage(coupe*this.partOfImg,0,coupe,image.getHeight());
			
			g2.drawImage(image, 1, 1, this.getWidth()-1, this.getHeight()-1, Color.WHITE, this);
		 }

	 }

}

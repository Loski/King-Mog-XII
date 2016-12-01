package RushHour;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class CaseExitRepresentation extends JPanel {
	
	private BufferedImage IMG;
	
	 public void paint(Graphics g)
	 {		 
		 if(this.IMG==null)
		 {
			 g.setColor(Color.BLACK);
			 g.drawRect(0,0, this.getWidth(), this.getHeight());
			
			 g.setColor(Color.GREEN);
			 
			 g.fillRect(2, 2, this.getWidth()-2, this.getHeight()-3);
			 
			 g.setFont(new Font("Verdana", Font.PLAIN, 30));
			   
			 g.setColor(Color.BLACK);
			 g.setFont(new Font("Verdana", Font.BOLD, 15));
			 g.drawString("EXIT", this.getWidth()/2-17,this.getHeight()/2+8);
		 }
		 
		 else
		 {
			Graphics2D g2 = (Graphics2D) g;
			
			//g2.rotate(-Math.PI / 2, this.getWidth() / 2, this.getHeight() / 2);
			g2.drawImage(this.IMG, 1, 1, this.getWidth()-1, this.getHeight()-1,this);
		 }
	 }
	
	  public CaseExitRepresentation(BufferedImage img)
	  {
		  this.IMG = img;
	  }
	
}

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

	public CaseVoitureGRepresentation(int h, int l,int partOfImg) {
		super(h, l,RushHour.HORIZONTAL, partOfImg);
		// TODO Auto-generated constructor stub
	}
	 
	 public void paint(Graphics g)
	 {
		 g.setColor(Color.BLACK);
		 g.drawRect(0,0,this.getWidth(),this.getHeight());
		 
		 g.setColor(Color.GRAY);
		 g.fillRect(2,2,this.getWidth()-2,this.getHeight()-2);
		 
		 
    	 File file = new File("cars/batman.png");
         BufferedImage image = null;
		try {
			image = ImageIO.read(file);

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int coupeL = image.getWidth()/2;
		int coupeH = image.getHeight()/2;
		
		image = image.getSubimage(0,coupeH*this.partOfImg,image.getWidth(),coupeH);
		
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.rotate(-Math.PI / 2, this.getWidth() / 2, this.getHeight() / 2);
		g2.drawImage(image, 1, 1, this.getWidth()-1, this.getHeight()-1, Color.WHITE, this);

	 }

}

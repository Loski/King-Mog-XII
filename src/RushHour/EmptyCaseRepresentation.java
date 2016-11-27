package RushHour;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class EmptyCaseRepresentation extends JPanel{

	 public void paint(Graphics g)
	 {
		 g.setColor(Color.BLACK);
		 g.drawRect(0,0,this.getWidth(),this.getHeight());
		 
		 g.setColor(Color.WHITE);
		 g.fillRect(2,2,this.getWidth()-2,this.getHeight()-2);
	 }
}

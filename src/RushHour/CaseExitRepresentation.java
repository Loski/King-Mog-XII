package RushHour;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class CaseExitRepresentation extends JPanel {
	
	private int l;
	private int h;
	
	 public void paint(Graphics g)
	 {
		 g.setColor(Color.BLACK);
		 g.drawRect(0,0, this.getWidth()/this.l, this.getHeight()-1);
		
		 g.setColor(Color.GREEN);
		 
		 g.fillRect(2, 2, this.getWidth()/this.l-2, this.getHeight()-3);
		 
		 g.setFont(new Font("Verdana", Font.PLAIN, 30));
		   
		 g.setColor(Color.BLACK);
		 g.setFont(new Font("Verdana", Font.BOLD, 15));
		 g.drawString("EXIT", this.getWidth()/this.l/2-17,this.getHeight()/2+8);
	 }
	
	  public CaseExitRepresentation(int h, int l)
	  {
		  this.l=l;
		  this.h=h;
	  }
	
}

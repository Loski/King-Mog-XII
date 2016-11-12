package RushHour;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CaseRepresentation extends JPanel {

	private String code;
	private ArrayList<JPanel> grille;
	private int l;
	private int h;
	
  public void paint(Graphics g) {
	  if(this.code!=null)
	  {
		g.setColor(Color.BLACK);
		
		if(this.code.equals("EXIT"))
			g.drawRect(0,0, this.getWidth()/this.l, this.getHeight()-1);
		else
			g.drawRect(0,0,this.getWidth(),this.getHeight());
		
	    if(this.code.equals("0"))
	    	g.setColor(Color.WHITE);
	    else if(this.code.equals("g"))
	    	g.setColor(Color.RED);
	    else if(this.code.equals("EXIT"))
	    	g.setColor(Color.GREEN);
	    else
	    	g.setColor(Color.BLACK);
	    
	    if(this.code.equals("EXIT"))
	    	g.fillRect(2, 2, this.getWidth()/this.l-2, this.getHeight()-3);
	    else
	    	g.fillRect(2,2,this.getWidth()-2,this.getHeight()-2);
	    
	    g.setColor(Color.WHITE);
	    g.setFont(new Font("Verdana", Font.PLAIN, 30));
	    
	    if(this.code.equals("EXIT"))
	    {
		    g.setColor(Color.BLACK);
		    g.setFont(new Font("Verdana", Font.BOLD, 15));
	    	g.drawString(this.code, this.getWidth()/this.l/2-16,this.getHeight()/2+8);
	    }
	    else
	    	g.drawString(this.code, this.getWidth()/2-15,this.getHeight()/2+15);
	  }
    
  }
  
  public void setCode(String s)
  {
	  this.code=s;
  }
  
  public CaseRepresentation(int h, int l,String code)
  {
	  this.grille = new ArrayList<JPanel>();
	  this.code=code;
	  this.l=l;
	  this.h=h;
  }
  
}
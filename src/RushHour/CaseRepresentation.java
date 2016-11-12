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
	
  public void paint(Graphics g) {
	  if(this.code!=null)
	  {
		g.setColor(Color.BLACK);
	    g.drawRect(0,0,this.getWidth(),this.getHeight());
		
	    if(this.code.equals("0"))
	    	g.setColor(Color.WHITE);
	    else if(this.code.equals("g"))
	    	g.setColor(Color.RED);
	    else if(this.code.equals(""))
	    	g.setColor(Color.GREEN);
	    else
	    	g.setColor(Color.BLACK);
	    
	    g.fillRect(2,2,this.getWidth()-2,this.getHeight()-2);
	    
	    g.setColor(Color.WHITE);
	    g.setFont(new Font("Verdana", Font.PLAIN, 30));
	    g.drawString(this.code, this.getWidth()/2-15,this.getHeight()/2);
	  }
    
  }
  
  public void setCode(String s)
  {
	  this.code=s;
  }
  
  public CaseRepresentation(int l, int h,String code)
  {
	  this.grille = new ArrayList<JPanel>();
	  this.code=code;
  }
  
}
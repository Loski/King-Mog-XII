package RushHour;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class RushHourSolverInteractive extends JFrame{
	private RushHour r;
	
	public RushHourSolverInteractive()
	{
		super();
	    this.setTitle("RushHour Solver");
	    this.setSize(800,800);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
	 
	    JPanel pan = new JPanel();
	    pan.setBackground(Color.WHITE);        
	    
		/*JComboBox puzzleList = new JComboBox(getListofPuzzle());
		
		pan.add(petList);
		
		petList.setSelectedIndex(4);*/
	    
	    JFileChooser fileChooser = new JFileChooser("./puzzles");
	    fileChooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "";
			}
	      
	    });
	    
	    pan.add(fileChooser);
	    
	    this.setContentPane(pan);              
	    this.setVisible(true);
	}
	
	private String[] getListofPuzzle()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		
		
		return (String[]) list.toArray();
	}
	
	public static void main(String[] args)
	{
		new RushHourSolverInteractive();
	}
}

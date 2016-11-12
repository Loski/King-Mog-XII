package RushHour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class RushHourSolverInteractive extends JFrame{
	private RushHour r;
	private GrapheConfiguration g;
	private ArrayList<RushHour> sequence;
	
	public RushHourSolverInteractive()
	{
		super();
		this.r=new RushHour("puzzles/débutant/jam1.txt");
	    this.setTitle("RushHour Solver");
	    this.setSize(800,800);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
	    this.setBackground(Color.WHITE);  
	 
	    JPanel pan = new JPanel();
	    pan.setLayout(new GridLayout(3,1));
	          
	    
		/*JComboBox puzzleList = new JComboBox(getListofPuzzle());
		
		pan.add(petList);
		
		petList.setSelectedIndex(4);*/
	    
	    /*JFileChooser fileChooser = new JFileChooser("./puzzles");
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
	    
	    pan.add(fileChooser);*/
	    
	    pan.add(drawGrille(r));
	    pan.add(createPanelInformations());  
	    
	    this.setContentPane(pan);  
	    setJMenuBar(createMenu());
	    this.setVisible(true);
	}
	
	private String[] getListofPuzzle()
	{
		ArrayList<String> list = new ArrayList<String>();	
		return (String[]) list.toArray();
	}
	
	public JPanel drawGrille(RushHour r)
	{
		JPanel pan = new JPanel();
	    pan.setLayout(new GridLayout(1, 3));
	    
	    JPanel grille = new JPanel();
	    System.out.println(r.getNbColonne());
	    grille.setLayout(new GridLayout(r.getNbLigne(), r.getNbColonne()));
		
		for(int i=0;i<RushHour.taille_matrice;i++)
		{
			for(int j=0;j<RushHour.taille_matrice;j++)
			{
				grille.add(new CaseRepresentation(r.getNbLigne(),r.getNbColonne(),"0"));
			}
		}
		
		for(Vehicule v : this.r.getVehicules())
		{
			int caseVehicule = v.getPosition();
			for(int i=0;i<v.getTaille();i++)
			{
				int n =0;
				
				if(v.getOrientation()==Orientation.HORIZONTAL)
					n=i;
				else
					n=i*this.r.getNbLigne();
				
				((CaseRepresentation)grille.getComponent(caseVehicule+n)).setCode(v.getCode());
				grille.getComponent(caseVehicule+n).repaint();
			}

		}
		
		pan.add(new JPanel());
		pan.add(grille);
		
		
		JPanel grilleSortie = new JPanel();
		grilleSortie.setLayout(new GridLayout(r.getNbLigne(),1));
		for(int i=0;i<this.r.getNbLigne();i++)
		{
			if(i==(int)(RushHour.caseSortie)/this.r.getNbColonne())
				grilleSortie.add(new CaseRepresentation(r.getNbLigne(),r.getNbColonne(),"EXIT"));
			else
				grilleSortie.add(new CaseRepresentation(r.getNbLigne(),r.getNbColonne(),null));
		}
		
		pan.add(grilleSortie);
		
		return pan;
	}
	
	public JMenuBar createMenu()
	{
		  JMenuBar menuBar = new JMenuBar();
		  JMenu menuRHC = new JMenu("Résoudre un problème RHC");
		  JMenu menuRHM = new JMenu("Résoudre un problème RHM");

		  JMenuItem RHCGuro = new JMenuItem("GUROBI");
		  JMenuItem RHCDij = new JMenuItem("Avec Dijkstra");
		  
		  JMenuItem RHMGuro = new JMenuItem("GUROBI");
		  JMenuItem RHMDij = new JMenuItem("Avec Dijkstra");
		  
		  menuRHC.add(RHCGuro);
		  menuRHC.add(RHCDij);
		  
		  menuRHM.add(RHMGuro);
		  menuRHM.add(RHMDij);
		  
		  menuBar.add(menuRHC);
		  menuBar.add(menuRHM);
		  
		  return menuBar;
	}
	
	public JPanel createPanelInformations()
	{
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(0,2));
		
		
		JLabel nbDeplacementText = new JLabel("Nombre de déplacement minimal : ");
		JLabel nbDeplacementValue = new JLabel("A REMPLIR");
		
		nbDeplacementValue.setForeground (Color.red);
		
		pan.add(nbDeplacementText);
		pan.add(nbDeplacementValue);
		
		JLabel nbCaseText = new JLabel("Nombre de case minimal : ");
		JLabel nbCaseValue = new JLabel("A REMPLIR");
		
		nbCaseValue.setForeground (Color.red);
		
		pan.add(nbCaseText);
		pan.add(nbCaseValue);
		
		
		JLabel nbVoituresText = new JLabel("Nombre de voitures : ");
		JLabel nbVoituresValue = new JLabel(""+this.r.getVehicules().size());
		
		pan.add(nbVoituresText);
		pan.add(nbVoituresValue);
		
		JLabel nbConfigurationsText = new JLabel("Nombre de Configurations : ");
		JLabel nbConfigurationsValue = new JLabel("A REMPLIR");
		
		pan.add(nbConfigurationsText);
		pan.add(nbConfigurationsValue);
		
		JLabel timeAlgoText = new JLabel("Temps d'éxécution de l'algorithme : ");
		JLabel timeAlgoValue = new JLabel("A REMPLIR");
		
		pan.add(timeAlgoText);
		pan.add(timeAlgoValue);
		
		JLabel nbSolutionText = new JLabel("Nombre de Configuration-but : ");
		JLabel nbSolutionValue = new JLabel("A REMPLIR");
		
		pan.add(nbSolutionText);
		pan.add(nbSolutionValue);
		
		
		return pan;
	}
	
	public JPanel createSettingsPanel()
	{
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(1,2));
		
	/*	pan.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()/3));
		pan.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()/3));
		pan.setMaximumSize(new Dimension(this.getWidth(), this.getHeight()/3));
		
		JButton RHCSolver = new JButton("RHC");
		JButton RHMSolver = new JButton("RHM");
	
		
		pan.add(RHCSolver);
		pan.add(RHMSolver);*/
		
		return pan;
	}
	
	
	public static void main(String[] args)
	{
		new RushHourSolverInteractive();
	}
}

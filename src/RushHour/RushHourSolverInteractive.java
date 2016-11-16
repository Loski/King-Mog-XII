package RushHour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RushHourSolverInteractive extends JFrame{
	private RushHour r;
	private GrapheConfiguration g;
	private ArrayList<RushHour> sequence;
	
	JPanel grille;
	JPanel loadRushHour;
	
	public RushHourSolverInteractive()
	{
		super();
		this.r=new RushHour("puzzles/débutant/jam1.txt");
	    this.setTitle("RushHour Solver");
	    this.setSize(800,800);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
	    this.setBackground(Color.WHITE);  
	 
	    
	    //pan.setLayout(new GridLayout(3,1));
	          
	    
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
	    
	    pan.add(fileChooser);
	    /*
	    pan.add(drawGrille(r));
	    pan.add(createPanelInformations());  
	    */
	    
	    
	   // setJMenuBar(createMenu());
	    
	    afficherMenuRushHourSelect();
	    
	    this.setVisible(true);
	}
	
	public JPanel createButtonLoad()
	{
		loadRushHour = new JPanel();
		
		JButton bouton = new JButton("Charger ce RushHour");
		
		bouton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				    afficherMenuRushHourSolver();
				  } 
				} );
		
		loadRushHour.add(bouton);
		
		return loadRushHour;
	}
	
	public void afficherMenuRushHourSelect()
	{
		this.getContentPane().removeAll();
		JPanel pan = new JPanel();
	    pan.setLayout(new GridLayout(3,1));
	    pan.add(this.loadFile());
	    
	    this.grille = new JPanel();
	    
	    this.drawGrille();
	    
	    pan.add(grille);
	    pan.add(this.createButtonLoad());
	    
	    this.setContentPane(pan);  
	    this.setJMenuBar(null);
	    this.revalidate();
	}
	
	public void afficherMenuRushHourSolver()
	{
		this.getContentPane().removeAll();
		this.setJMenuBar(createMenu());
		this.getContentPane().setLayout(new GridLayout(2,1));
		this.getContentPane().add(grille);
		this.getContentPane().add(createPanelInformations());
		
		this.setContentPane(this.getContentPane());
		this.revalidate();
	}
	
	public JPanel loadFile()
	{
		
		ArrayList<Puzzle> puzzleList = Puzzle.getListofPuzzle();

        Object[][] listData =  new Object[puzzleList.size()][2];
        
        int i=0;
        
        for(Puzzle p:puzzleList)
        {
        	listData[i][0]=p.getDifficulty();
        	listData[i][1]=p.getTxtFileName();
        	i++;
        }
        
        String[] columnNames = {"Difficulté","Nom du Fichier"};

        JTable table = new JTable(listData, columnNames)
        {
        	   @Override
        	    public boolean isCellEditable(int row, int column) {
        	        return false;
        	    } 
        };
        
        JScrollPane scroll = new JScrollPane(table);
        
        //scroll.setBorder(BorderFactory.createTitledBorder ("Liste des Puzzles"));
        
		JPanel pan = new JPanel();
		
		pan.setLayout(new BorderLayout());
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	
	            String difficulty = table.getValueAt(table.getSelectedRow(), 0).toString();
	            String name = table.getValueAt(table.getSelectedRow(), 1).toString();
	            
	            r = new RushHour("./puzzles/"+difficulty+"/"+name);
	            drawGrille();
	            getContentPane().remove(loadRushHour);
	            getContentPane().add(createButtonLoad());
	            revalidate();
	        }
	    });
		
		/*JList<String> puzzleList;
		DefaultListModel<String> model;
	    model = new DefaultListModel<String>();
	    puzzleList = new JList<String>(model);
	    JScrollPane scroll = new JScrollPane(list);
		
	    for(Puzzle p : Puzzle.getListofPuzzle())
	    	model.addElement(p.toString());*/
	    
	    pan.add(scroll);
	    
		return pan;
	}
	
	public void drawGrille()
	{
		JPanel pan = new JPanel();
	    pan.setLayout(new GridLayout(1, 3));
	    
	    JPanel grille = new JPanel();
	    //System.out.println(this.r.getNbColonne());
	    grille.setLayout(new GridLayout(this.r.getNbLigne(), this.r.getNbColonne()));
		
		for(int i=0;i<RushHour.DIMENSION_MATRICE;i++)
		{
			for(int j=0;j<RushHour.DIMENSION_MATRICE;j++)
			{
				grille.add(new CaseRepresentation(this.r.getNbLigne(),this.r.getNbColonne(),"0"));
			}
		}
		
		for(Vehicule v : this.r.getVehicules())
		{
			int caseVehicule = v.getPosition();
			for(int i=0;i<v.getTaille();i++)
			{
				int n =0;
				
				if(v.getOrientation()==RushHour.HORIZONTAL)
					n=i;
				else
					n=i*this.r.getNbLigne();
				
				((CaseRepresentation)grille.getComponent(caseVehicule+n)).setCode("0");
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
		
		this.getContentPane().remove(this.grille);
		this.grille = pan;
		this.getContentPane().add(pan);;
		
		this.setContentPane(this.getContentPane());
		this.revalidate();
		
	}
	
	public JMenuBar createMenu()
	{
		  JMenuBar menuBar = new JMenuBar();
		  JMenu menuFichier = new JMenu("Fichier");
		  JMenu menuRHC = new JMenu("Résoudre un problème RHC");
		  JMenu menuRHM = new JMenu("Résoudre un problème RHM");

		  JMenuItem loadFile = new JMenuItem("Charger un autre fichier");
		  
			loadFile.addActionListener(new ActionListener() { 
				  public void actionPerformed(ActionEvent e) { 
					  afficherMenuRushHourSelect();
					  } 
					} );
		  
		  JMenuItem RHCGuro = new JMenuItem("GUROBI");
		  JMenuItem RHCDij = new JMenuItem("Avec Dijkstra");
		  
		  JMenuItem RHMGuro = new JMenuItem("GUROBI");
		  JMenuItem RHMDij = new JMenuItem("Avec Dijkstra");
		  
		  menuFichier.add(loadFile);
		  
		  menuRHC.add(RHCGuro);
		  menuRHC.add(RHCDij);
		  
		  menuRHM.add(RHMGuro);
		  menuRHM.add(RHMDij);
		  
		  menuBar.add(menuFichier);
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

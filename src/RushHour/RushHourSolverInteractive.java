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
import java.util.HashMap;

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
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RushHourSolverInteractive extends JFrame{
	private RushHour r;
	private GrapheConfiguration g;
	private ArrayList<RushHour> sequence;
	int currentConfig = 0;
	
	JPanel grille;
	JPanel loadRushHour;
	
	JButton nextButton;
	JButton previousButton;
	
	JLabel currentConfigDisplay;
	
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
	
	public void nextConfiguration()
	{		
		if(this.currentConfig+1<this.sequence.size())
		{
			this.currentConfig++;
			this.r=this.sequence.get(currentConfig);	
		}
		
		if(this.currentConfig+1>=this.sequence.size())
		{
			//DIABLED NEXT
			this.nextButton.setEnabled(false);
		}		
		
		//SI PREVIOUS disabled : enabled
		this.previousButton.setEnabled(true);
		
		this.currentConfigDisplay.setText("ETAPE ("+this.currentConfig+")");		
		this.drawGrille();
	}
	
	public void previousConfiguration()
	{
		if(this.currentConfig-1>=0)
		{
			this.currentConfig--;
			this.r=this.sequence.get(currentConfig);	
		}
		
		if(this.currentConfig==0)
		{
			//DIABLED PREVIOUS
			this.previousButton.setEnabled(false);
		}
		
		this.nextButton.setEnabled(true);
		
		//SI NEXT disabled : next Enabled
		
		this.currentConfigDisplay.setText("ETAPE ("+this.currentConfig+")");
		this.drawGrille();
	}
	
	public void afficherMenuRushHourSolver()
	{
		this.getContentPane().removeAll();
		this.setJMenuBar(createMenu());
		this.getContentPane().setLayout(new GridLayout(2,1));
		this.getContentPane().add(grille);
		//this.getContentPane().add(createPane());
		
		this.setContentPane(this.getContentPane());
		this.revalidate();
	}
	
	public JPanel resultPanel()
	{
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		
		
		return pan;
		
	}
	
	public JPanel consoleArea()
	{
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		
		JTextArea consoleArea = new JTextArea();
		
		pan.add(consoleArea,BorderLayout.CENTER);
		
		return pan;
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
		//JPanel pan = new JPanel();
		//this.grille = new JPanel();
		this.grille.removeAll();
		//this.grille.setLayout(new GridLayout(1, 3));

	    
	    JPanel grille = new JPanel();
		grille.setPreferredSize(new Dimension(300,300));
		grille.setMinimumSize(new Dimension(300,300));
		grille.setMaximumSize(new Dimension(800,800));
	    //System.out.println(this.r.getNbColonne());
	    grille.setLayout(new GridLayout(this.r.getNbLigne(), this.r.getNbColonne()));
		
	    HashMap<String,Byte> map = new HashMap<>();
	    
		int compteur_voiture = 0;
		int compteur_camion = 0;
		int j=0;
		for(Vehicule v: this.r.getVehicules()){
			if(v instanceof Camion){
				compteur_camion++;
				map.put("t" + compteur_camion,v.getOrientation());
			}
			else if(j!=RushHour.indice_solution_g){
				compteur_voiture++;
				map.put("c" + compteur_voiture,v.getOrientation());
			}
			
			j++;
		}
	    
	    String[] grilleStr= this.r.TabIntToStrTab();
	    
	    HashMap<String,Byte> partsOfImg = new HashMap<>();
	    
		for(int i=0;i<RushHour.TAILLE_MATRICE;i++)
		{			
			if(grilleStr[i].equals("0"))
			{
				grille.add(new EmptyCaseRepresentation());
			}
			else
			{
				if(partsOfImg.containsKey(grilleStr[i]))
					partsOfImg.put(grilleStr[i], (byte) (partsOfImg.get(grilleStr[i])+1));
				else
					partsOfImg.put(grilleStr[i], (byte) 0);
				
				if(grilleStr[i].charAt(0)=='t')
					grille.add(new CaseCamionRepresentation(this.r.getNbLigne(),this.r.getNbColonne(),map.get(grilleStr[i]),partsOfImg.get(grilleStr[i])));
				else if(grilleStr[i].charAt(0)=='c')
					grille.add(new CaseVoitureRepresentation(this.r.getNbLigne(),this.r.getNbColonne(),map.get(grilleStr[i]),partsOfImg.get(grilleStr[i])));
				else
					grille.add(new CaseVoitureGRepresentation(this.r.getNbLigne(),this.r.getNbColonne(),partsOfImg.get(grilleStr[i])));
			}
		}
		
		//this.grille.add(new JPanel());
		this.grille.add(grille);
		
		
		JPanel grilleSortie = new JPanel();
		
		grilleSortie.setLayout(new GridLayout(r.getNbLigne(),1));
		for(int i=0;i<this.r.getNbLigne();i++)
		{
			if(i==(int)(RushHour.CASE_SORTIE)/this.r.getNbColonne())
			{
				JPanel caseExit = new CaseExitRepresentation(this.r.getNbLigne(),this.r.getNbColonne());
				grilleSortie.add(caseExit);
				grilleSortie.setPreferredSize(new Dimension(300,300));
				grilleSortie.setMinimumSize(new Dimension(300,300));
				grilleSortie.setMaximumSize(new Dimension(800,800));
			}
				
			else
				//grilleSortie.add(new CaseRepresentation(r.getNbLigne(),r.getNbColonne(),null));
				grilleSortie.add(new JPanel());
		}
		
		this.grille.add(grilleSortie);
		
		//this.getContentPane().remove(this.grille);
		//this.grille = pan;
		//this.getContentPane().add(pan);;
		
		//this.setContentPane(this.getContentPane());

		this.grille.revalidate();
		this.grille.repaint();
		this.revalidate();
		
	}
	
	public void afficherResultat(Object[] result)
	{
		this.sequence=(ArrayList<RushHour>) result[1];
		
		JPanel pan =  new JPanel();
		this.previousButton = new JButton("PREVIOUS");
		this.nextButton = new JButton("NEXT");
		this.currentConfigDisplay = new JLabel("ETAPE ("+this.currentConfig+")");
		previousButton.setEnabled(false);
		
		nextButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  nextConfiguration();
				  } 
				} );
		
		previousButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  previousConfiguration();
				  } 
				} );
	  
		
		pan.add(previousButton);
		pan.add(this.currentConfigDisplay);
		pan.add(nextButton);
		
		this.getContentPane().add(pan);
		this.revalidate();
	}
	
	
	public Object[] performRHCDij()
	{
		r.afficher();
		this.g = new GrapheConfiguration(r);
		Object[] result = DijkstraSolver.resolveRHC(g.getListe_adj(), g.getConfigurations());

		return result;
	}
	
	public Object[] performRHMDij()
	{
		r.afficher();
		this.g = new GrapheConfiguration(r);
		Object[] result = DijkstraSolver.resolveRHM(g.getListe_adj(), g.getConfigurations());

		return result;
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
		  
		  
		  RHCDij.addActionListener(new ActionListener() { 
				  public void actionPerformed(ActionEvent e) { 
					    Object[] result = performRHCDij();
					    afficherResultat(result);
					  } 
					} );
		  
		  RHMDij.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				    Object[] result = performRHMDij();
				    afficherResultat(result);
				  } 
				} );
		  
		  
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
	
	/*
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
	}*/
	
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

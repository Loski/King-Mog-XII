package RushHour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gurobi.GRBException;

public class RushHourSolverInteractive extends JFrame{
	
	public static byte GUROBI = 0;
	public static byte DIJKSTRA = 1;
	
	private String theme;
	
	private RushHour r;
	private GrapheConfiguration g;
	private ArrayList<RushHour> sequence;
	int currentConfig = 0;
	
	JPanel grille;
	JPanel loadRushHour;
	
	JButton nextButton;
	JButton previousButton;
	
	JLabel currentConfigDisplay;
	
	private int N;

	private String methode;
	
	private static boolean isGurobiRunnable = true;
	
	private int methodeUsed;
	private int logicielUsed;
	
	private String fileLoaded;
	
	
	static
	{
	    try
	    {
	       System.loadLibrary("GurobiJni70");
	    }
	    catch(java.lang.UnsatisfiedLinkError e)
	    {
	    	RushHourSolverInteractive.isGurobiRunnable=false;
	    }
	}

	
	public RushHourSolverInteractive(String string)
	{
		super();
		this.theme=string;
		this.r=new RushHour("puzzles/débutant/jam1.txt");
	    this.setTitle("RushHour Solver");
	    this.setSize(800,800);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
	    this.setBackground(Color.WHITE);  
	    
	    afficherMenuRushHourSelect();
	    
	    this.setVisible(true);
	}
	
	public JPanel createButtonLoad()
	{
		loadRushHour = new JPanel();
		
		JButton bouton = new JButton("Charger ce RushHour");
		
		bouton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  if(r.pos_g/r.getNbColonne() == RushHour.CASE_SORTIE/r.getNbColonne())
					  afficherMenuRushHourSolver();
				  else
					  JOptionPane.showMessageDialog(new JFrame(), "Le fichier n'est pas conforme : G n'est pas sur la bonne ligne", "ERREUR",
						        JOptionPane.ERROR_MESSAGE); 
				  } 
				} );
		
		loadRushHour.add(bouton);
		
		return loadRushHour;
	}
	
	public void afficherMenuRushHourSelect()
	{	
		this.getContentPane().removeAll();
		JPanel pan = new JPanel();
	    pan.setLayout(new GridLayout(2,1));
	    pan.add(this.loadFile());
	    
	    this.grille = new JPanel();
	    
	    this.drawGrille();
	    
	    pan.add(grille);
	    //pan.add(this.createButtonLoad());
	    
	    this.getContentPane().add(pan, BorderLayout.CENTER);
	    this.getContentPane().add(this.createButtonLoad(), BorderLayout.SOUTH);
	    this.setJMenuBar(null);
	    this.revalidate();
	    this.repaint();
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

		this.previousButton.setEnabled(true);
		
		this.currentConfigDisplay.setText("ETAPE ("+this.currentConfig+" / "+(this.sequence.size()-1)+")");		
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
		
		this.currentConfigDisplay.setText("ETAPE ("+this.currentConfig+" / "+(this.sequence.size()-1)+")");	
		this.drawGrille();
	}
	
	public void afficherMenuRushHourSolver()
	{
		this.getContentPane().removeAll();
		this.setJMenuBar(createMenu());		
		this.getContentPane().add(grille);
		//this.getContentPane().add(createPane());
		
		this.revalidate();
		this.repaint();
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
	            N=puzzleList.get(table.getSelectedRow()).getN();
	            r = new RushHour("./puzzles/"+difficulty+"/"+name);      	
	            fileLoaded=name;
	            drawGrille();
	            getContentPane().remove(loadRushHour);
	            getContentPane().add(createButtonLoad(),BorderLayout.SOUTH);
	            revalidate();
	            repaint();
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
		this.grille.setLayout(new BorderLayout());
	    
	    JPanel grille = new JPanel();
		/*grille.setPreferredSize(new Dimension(200,200));
		grille.setMinimumSize(new Dimension(200,200));
		grille.setMaximumSize(new Dimension(800,800));*/
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
	    
	    int ligneActuel=0;
	    
		for(int iTotal=0;iTotal<RushHour.TAILLE_MATRICE+RushHour.DIMENSION_MATRICE;iTotal++)
		{	
			if((iTotal+1)%(this.r.getNbColonne()+1)==0 && iTotal!=0)
			{
				if(ligneActuel==2)
					grille.add(new CaseExitRepresentation(this.r.getNbLigne(),this.r.getNbColonne(),this.theme));
				else
					grille.add(new JPanel());
				ligneActuel++;
			}
			else
			{
			
				int i= iTotal-ligneActuel;
				
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
						grille.add(new CaseCamionRepresentation(this.r.getNbLigne(),this.r.getNbColonne(),map.get(grilleStr[i]),partsOfImg.get(grilleStr[i]),this.theme));
					else if(grilleStr[i].charAt(0)=='c')
						grille.add(new CaseVoitureRepresentation(this.r.getNbLigne(),this.r.getNbColonne(),map.get(grilleStr[i]),partsOfImg.get(grilleStr[i]),this.theme));
					else
						grille.add(new CaseVoitureGRepresentation(this.r.getNbLigne(),this.r.getNbColonne(),partsOfImg.get(grilleStr[i]),this.theme));
				}
			}
		}
		
		this.grille.add(grille,BorderLayout.CENTER);
		
		//this.getContentPane().remove(this.grille);
		//this.grille = pan;
		//this.getContentPane().add(pan);;
		
		//this.setContentPane(this.getContentPane());

		this.grille.revalidate();
		this.grille.repaint();
		this.revalidate();
		this.repaint();
		
	}
	
	public void afficherResultat(Object[] result)
	{
		this.sequence=(ArrayList<RushHour>) result[1];
		
		JPanel panelConfig =  new JPanel();
		this.previousButton = new JButton("PREVIOUS");
		this.nextButton = new JButton("NEXT");
		this.currentConfigDisplay = new JLabel("ETAPE ("+this.currentConfig+" / "+(this.sequence.size()-1)+")");
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
	  
		
		panelConfig.add(previousButton);
		panelConfig.add(this.currentConfigDisplay);
		panelConfig.add(nextButton);
		
		this.getContentPane().removeAll();
		this.getContentPane().setLayout(new BorderLayout());
		
		JLabel methodePanel = new JLabel(this.methode,JLabel.CENTER);
		methodePanel.setFont(new Font("Verdana", Font.BOLD, 28));
		this.getContentPane().add(methodePanel,BorderLayout.NORTH);
		
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.add(grille,BorderLayout.CENTER);
		center.add(panelConfig,BorderLayout.SOUTH);
		this.getContentPane().add(center,BorderLayout.CENTER);
		
		JPanel objWrapper = new JPanel();
		
		JPanel obj = panelObj(result);
		objWrapper.setLayout(new BorderLayout());
		
		objWrapper.setPreferredSize(new Dimension(this.getWidth(),200));
		objWrapper.setMinimumSize(new Dimension(this.getWidth(),200));
		objWrapper.setMaximumSize(new Dimension(this.getWidth(),800));
		
		objWrapper.add(new JLabel("Résultat pour le fichier "+this.fileLoaded),BorderLayout.NORTH);
		objWrapper.add(obj,BorderLayout.CENTER);
		
		
		this.getContentPane().add(objWrapper,BorderLayout.SOUTH);
		this.revalidate();
		this.repaint();
	}
	
	public JPanel panelObj(Object[] result)
	{
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(0,2));
		
		JLabel nbCase = null;
		
		JLabel nbMV = null;
		
		if(this.methodeUsed == RushHour.RHC)
		{
			nbCase = new JLabel("Nombre minimal de cases : "+result[0]);
			nbMV = new JLabel("Avec "+(sequence.size()-1)+ " mouvements");
			
			pan.add(nbCase);
			pan.add(nbMV);
		}
		else
		{
			if(this.logicielUsed==RushHourSolverInteractive.GUROBI)
				nbMV = new JLabel("Nombre minimal de Mouvement: "+result[2]);
			else
				nbMV = new JLabel("Nombre minimal de Mouvement: "+(sequence.size()-1));
			
			nbCase = new JLabel("Avec "+result[0]+" cases déplacées");
			
			pan.add(nbMV);
			pan.add(nbCase);
		}
		
		if(this.logicielUsed==RushHourSolverInteractive.DIJKSTRA) 
		{
			pan.add(new JLabel("Nombre de Configuration-but : "+this.g.getIndexOfSolutions().size()));
			pan.add(new JLabel("Nombre de configurations réalisables : "+(this.g.getConfigurations().size()-1)));
		}
		
		pan.add(new JLabel("Temps d'éxécution de l'ALGO : SOON"));
		
		return pan;
	}
	
	
	public Object[] performRHCDij()
	{
		this.methodeUsed=RushHour.RHC;
		this.logicielUsed=RushHourSolverInteractive.DIJKSTRA;
		this.methode="Résolution de RHC par l'algorithme de Dijkstra";
		//r.afficher();
		if(this.g==null)
			this.g = new GrapheConfiguration(r);
		Object[] result = DijkstraSolver.resolveRHC(g.getListe_adj(), g.getConfigurations());

		return result;
	}
	
	public Object[] performRHMDij()
	{
		this.methodeUsed=RushHour.RHM;
		this.logicielUsed=RushHourSolverInteractive.DIJKSTRA;
		this.methode="Résolution de RHM par l'algorithme de Dijkstra";
		//r.afficher();
		if(this.g==null)
			this.g = new GrapheConfiguration(r);
		Object[] result = DijkstraSolver.resolveRHM(g.getListe_adj(), g.getConfigurations());

		return result;
	}
	
	public Object[] performRHCGuro()
	{
		Object[] result =null;
		this.methodeUsed=RushHour.RHC;
		this.logicielUsed=RushHourSolverInteractive.GUROBI;
		
		this.methode="Résolution de RHC par PL via Gurobi";
		
		GurobiSolver solver =  new GurobiSolver(this.r, this.N);
		result = solver.solve(RushHour.RHC);

		return result;
	}
	
	public Object[] performRHMGuro()
	{
		Object[] result =null;
		this.methodeUsed=RushHour.RHM;
		this.logicielUsed=RushHourSolverInteractive.GUROBI;
		this.methode="Résolution de RHC par PL via Gurobi";
		
		GurobiSolver solver =  new GurobiSolver(this.r, this.N);
		result = solver.solve(RushHour.RHM);
		
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
					  g=null;
					  afficherMenuRushHourSelect();
					  } 
					} );
		  
		  JMenuItem RHCGuro = new JMenuItem("Avec Gurobi");
		  JMenuItem RHCDij = new JMenuItem("Avec Dijkstra");
		  
		  JMenuItem RHMGuro = new JMenuItem("Avec Gurobi");
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
		  
		  RHMGuro.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  if(RushHourSolverInteractive.isGurobiRunnable)
				  {
				    Object[] result = performRHMGuro();
				    afficherResultat(result);
				  }
				  else
				  {
					  JOptionPane.showMessageDialog(new JFrame(), "Gurobi n'est pas installé", "ERREUR",
						        JOptionPane.ERROR_MESSAGE);  
				  }
				  } 
				} );
	  
		  RHCGuro.addActionListener(new ActionListener() { 
		  public void actionPerformed(ActionEvent e) { 
			  if(RushHourSolverInteractive.isGurobiRunnable)
			  {
			    Object[] result = performRHCGuro();
			    afficherResultat(result);
			  }
			  else
			  {
				  JOptionPane.showMessageDialog(new JFrame(), "Gurobi n'est pas installé", "ERREUR",
					        JOptionPane.ERROR_MESSAGE);
			  }
			  
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
	
	public static void main(String[] args)
	{
		if(args.length>0)
			new RushHourSolverInteractive(args[0]);
		else
			new RushHourSolverInteractive("default");
		
		return;
	}
}

package RushHour;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class GurobiSolver {
	
	private GRBEnv env;
    private GRBModel model;
    private GRBVar[][][] X;
    private GRBVar[][][] Z;
    private GRBVar[][][][] Y;
    private int N;
    private int iMax, jMax, lMax;
    private int positionPossible[][];
    private int[][][] pij;
    private int[][][] mij;
    private int positionMarqueurPossible[][];
    
    private RushHour rh;
    
	public GurobiSolver(RushHour rh, int N) {
		this.rh = rh;
		N++;
		this.N = N;
		this.iMax = rh.getVehicules().size();
		this.jMax = RushHour.DIMENSION_MATRICE;
		this.lMax = jMax;
		this.Y = new GRBVar[iMax][jMax][lMax][N+1];
		this.X = new GRBVar[iMax][jMax][N+1];
		this.Z = new GRBVar[iMax][jMax][N+1];
		this.positionMarqueurPossible = new int[iMax][];
		this.positionPossible = new int[iMax][RushHour.DIMENSION_MATRICE];
		this.calculMarqueurPossible();
		this.calculPositionPossible();
		this.calculPIJ();
		this.calculMij();
	}
	
	private void calculMij() {
		this.mij = new int[this.rh.getVehicules().size()][RushHour.TAILLE_MATRICE][0];
		
		
		int i=0;
		
		for(Vehicule vi : this.rh.getVehicules())
		{
			for(int pos:this.getMarqueurPossible(i))
			{	
				this.mij[i][pos] = new int[vi.getTaille()];
				
				for(int k=0;k<vi.getTaille();k++)
					this.mij[i][pos][k]=pos+k;
			}	
				
			i++;
		}

	}

	public void calculPositionPossible(){
		int vehicule = 0;
		for(Vehicule v: this.rh.getVehicules()){
			byte position = v.getPosition();
			byte orientation = v.getOrientation();
			byte saut = RushHour.DIMENSION_MATRICE;
			if(orientation == RushHour.VERTICAL){
				saut =  RushHour.DIMENSION_MATRICE;
				while(position - saut >= 0){
					position -= saut;
				}
			}else{
				saut = 1;
				while(position%RushHour.DIMENSION_MATRICE - saut >= 0){
					position-=saut;
				}
			}
			for(byte j = 0; j < RushHour.DIMENSION_MATRICE; j++){
				this.positionPossible[vehicule][j] = position  + j * saut;
			}
			vehicule++;
		}
	}
	
	private void calculPIJ(){
		this.pij = new int[RushHour.TAILLE_MATRICE][RushHour.TAILLE_MATRICE][];
		int saut;
		
		for(int iFor = 0; iFor < RushHour.TAILLE_MATRICE; iFor++){
			for(int jFor = 0; jFor < RushHour.TAILLE_MATRICE; jFor++){
				ArrayList<Integer> a = new ArrayList<Integer>();
				int k = 0;
				int j=jFor;
				int i=iFor;
				if(iFor>jFor){
					int tmp = i;
					i = j;
					j = tmp;
				}
				
				int compteur=0;
				
				if((int)i/RushHour.DIMENSION_MATRICE == (int) j/RushHour.DIMENSION_MATRICE){
					saut = 1;
					for(k = i;k<=j; k+=saut){
						a.add(k);
						compteur++;
					}
				}
				else if(i%RushHour.DIMENSION_MATRICE == j%RushHour.DIMENSION_MATRICE){
					saut = RushHour.DIMENSION_MATRICE;	
					for(k = i;k<=j; k+=saut){
						a.add(k);
						compteur++;
					}
				}
				
				this.pij[iFor][jFor] = new int[compteur];
				for(int z = 0; z < compteur; z++){
					this.pij[iFor][jFor][z] = a.get(z);
				}
			}
		}
	}
	public void calculMarqueurPossible(){
		int vehicule = 0;
		for(Vehicule v: this.rh.getVehicules()){
			byte position = v.getPosition();
			byte orientation = v.getOrientation();
			byte saut = 1;
			this.positionMarqueurPossible[vehicule] = new int[RushHour.DIMENSION_MATRICE +1 - v.getTaille()];
			if(orientation == RushHour.VERTICAL){
				saut =  RushHour.DIMENSION_MATRICE;
				while(position - saut >= 0){
					position -= saut;
				}
			}else{
				saut = 1;
				while(position%RushHour.DIMENSION_MATRICE - saut >= 0){
					position-=saut;
				}
			}
			for(byte j = 0; j < RushHour.DIMENSION_MATRICE - v.getTaille() +1 ; j++){
				this.positionMarqueurPossible[vehicule][j] = position  + j * saut;
			}
			vehicule++;
		}
	}
	
	private void lancementContrainte() throws GRBException{
		this.model.update();
		victoire();
		this.model.update();
		seulementViCase();
		this.model.update();
		onlyOneMvmByTurn();
		this.model.update();
		majMarqueur();
		this.model.update();
		this.model.update();
		defPosVehicule();
		this.model.update();
		caseByTurn();	
		this.model.update();
		contrainteDeDeplacement();
		
		
	}

	
	//Contrainte de victoire (15)
	private void victoire() throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		expr.addTerm(1.0,X[RushHour.indice_solution_g][RushHour.CASE_SORTIE][N-1]);
		this.model.addConstr(expr,  GRB.EQUAL, 1.0,  "C_Victoire");
	}
	
	private void seulementViCase() throws GRBException{
	        GRBLinExpr expr;
	        for(int i=0;i<iMax;i++)
	            for(int k=0;k<N;k++)
	            {
	                expr = new GRBLinExpr();
	                
	                int[] pos = this.getMarqueurPossible(i);
	                
	                    for(int j:pos)
	                        expr.addTerm(1.0,Z[i][j][k]);    
	                
	                if(pos.length>0)
	                	this.model.addConstr(expr, GRB.EQUAL, this.rh.getVehicules().get(i).getTaille(), "C_1VehiculeDeplace_"+k);
	            }
	    }
	  
	//1 mvm par tour (16)
	private void onlyOneMvmByTurn() throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		for(int k=1;k<N;k++)
		{
			expr = new GRBLinExpr();
			int[] pos = new int[0];
			
			for(int i=0;i<iMax;i++)
			{
				pos = this.getMarqueurPossible(i);
				for(int j:pos)
					for(int l:pos)
	            		{	
	            			expr.addTerm(1.0,Y[i][j][l][k]);	
	            		}
			}
			
			this.model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "C_1VehiculeDeplace_"+k);
		}
	}
	
	//Contrainte maj marqueur  (17)
	private void majMarqueur() throws GRBException{
		GRBLinExpr exprL, exprR;
		for(int i=0;i<iMax;i++)
		{
			int [] pos = this.getMarqueurPossible(i);
			
			for(int j:pos)
				for(int k=1;k<N;k++)
				{
					exprL = new GRBLinExpr();
					exprR = new GRBLinExpr();
					exprL.addTerm(1.0,X[i][j][k-1]);	
					exprR.addTerm(1.0,X[i][j][k]);
					
					for(int l:pos)
					{
						exprL.addTerm(-1.0, Y[i][j][l][k]);
						exprL.addTerm(1.0, Y[i][l][j][k]);
					}
					this.model.addConstr(exprL, GRB.EQUAL, exprR, "C_MajMarqueur_"+i+"_"+j+"_"+k);
				}
		}
	}
	
	//18
	private void defPosVehicule() throws GRBException{
		GRBLinExpr exprL, exprR;
		for(int i=0;i<iMax;i++)
		{
			Vehicule vi = this.rh.getVehicules().get(i);
			int [] pos = this.getMarqueurPossible(i);
			
			for(int j:pos)
			{				
				for(int k=0;k<N;k++)
				{
					exprL = new GRBLinExpr();
					exprR = new GRBLinExpr();
					int tailleVehicule = vi.getTaille();
					exprL.addTerm((double)tailleVehicule,X[i][j][k]);
					
					for(int m : this.getMIJ(i, j))
					{
						exprR.addTerm(1.0,Z[i][m][k]);
					}
					
					if(this.getMIJ(i, j).length>0)
						this.model.addConstr(exprL, GRB.LESS_EQUAL, exprR, "C_PosVehicule_"+i+"_"+j+"_"+k);
				}
			}
		}
	}
	
	// Un v√©hicule par case par tour ! (19)
	private void caseByTurn() throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		
		List<List<int[]>> listOfPos = new ArrayList<List<int[]>>();
		
		for(int i=0;i<iMax;i++)
		{
			listOfPos.add(Arrays.asList(this.getPositionPossible(i)));
		}
		
		
		for(int j = 0; j < RushHour.DIMENSION_MATRICE; j++)
			for(int k = 0; k < this.N;k++)
			{
				expr = new GRBLinExpr();
				boolean added = false;
				
				for(int i=0;i<iMax;i++)
				{
					if(listOfPos.get(i).contains(j))
					{
						expr.addTerm(1.0, Z[i][j][k]);
						added = true;
					}
				}
				
				if(added)
					this.model.addConstr(expr, GRB.LESS_EQUAL, 1.0,"C_1VehiculeParCase_"+j+"_"+k);
			}
	}
	
	//  20 
	private void contrainteDeDeplacement() throws GRBException{
		GRBLinExpr expr;
		for(int i=0;i<iMax;i++)
			for(int j:this.getMarqueurPossible(i))
				for(int l:this.getMarqueurPossible(i)){
					
					for(int k=1;k<N;k++)
					{					
						for(int p: this.getPIJ(j, l))
						{
							expr = new GRBLinExpr();
							expr.addTerm(1.0, Y[i][j][l][k]);
							
							for(int iPrime=0;iPrime<iMax;iPrime++)
								if(iPrime!=i)
								{
									expr.addTerm(1.0, Z[iPrime][p][k-1]);
								}
							
							model.addConstr(expr, GRB.LESS_EQUAL, 1.0,"C_CasePasTouchee_"+i+"_"+j+"_"+l+"_"+k+"_"+p);
						}
					}
				}

	}
	private void initialisation() throws GRBException{
		
		//CrÔøΩation des variables et de la fonction objectif		
		
		GRBLinExpr obj = new GRBLinExpr();
		for(int i=0;i<iMax;i++)
            for(int j:this.getMarqueurPossible(i))
                for(int l:this.getMarqueurPossible(i))
                    for(int k=0;k<N;k++){
						Y[i][j][l][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"Y_"+i+"_"+j+"_"+l+"_"+k);
						X[i][j][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"X_"+i+"_"+j+"_"+k);
						obj.addTerm(1.0,Y[i][j][l][k]);
                    }
		
		model.setObjective(obj, GRB.MINIMIZE);
		
		for(int i=0;i<iMax;i++)
            for(int j:this.getPositionPossible(i))
                    for(int k=0;k<N;k++){
						Z[i][j][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"Z_"+i+"_"+j+"_"+k);
	                }
		
		for(int i = 0; i <iMax; i++){
			int position_initial = this.rh.getVehicules().get(i).getPosition(); 
			this.model.addConstr(X[i][position_initial][0],  GRB.EQUAL, 1.0, "X"+i);
			int saut = RushHour.DIMENSION_MATRICE;
			if(this.rh.getVehicules().get(i).getOrientation() == RushHour.HORIZONTAL)
				saut = 1;
			int taille = this.rh.getVehicules().get(i).getTaille();
			for(int j = this.rh.getVehicules().get(i).getPosition(); j < position_initial + saut * taille ;j+=saut){
				this.model.addConstr(Z[i][j][0], GRB.EQUAL, 1.0, "ZDepart"+i +"_"+j);
				//System.out.println(Z[i][j][0].get(GRB.DoubleAttr.));
			}
				
		}
		
	}
	
	public int[] calculeP(int j, int l, Vehicule v){
		int tmpMax = Math.max(j, l);
		int tmpMin = Math.min(j, l);
		j = tmpMin;
		l = tmpMax;/*
		int saut = 1;
		if(v.getOrientation() == RushHour.VERTICAL)
			saut = 6;
		int tab[] = new int[l-j];
		int i = 0;
		while(j  < l){
			tab[i] = j;
			i++;
			j+=saut;
		}
		return tab;*/
		
		int[] p = new int[l-j+1];
		int index =0;
		
		for(int i=j;i<l;i++)
		{
			p[index]=i;
			index++;
		}
		
		return p;
	}
	
	public int[] calculMij(Vehicule vi, int j){
	
		int mij[] = new int[vi.getTaille()];
		int saut = RushHour.DIMENSION_MATRICE;
		
		if(vi.getOrientation()==RushHour.HORIZONTAL){
			saut = 1;
		}
		for(int z = 0; z < vi.getTaille();z++)
			if(j+z*saut < RushHour.TAILLE_MATRICE)
				mij[z]=j+z*saut;
		return (mij.length == 0) ? null : mij;
	}
	
	public void solve()
	{
		try
		{
			this.env = new GRBEnv("RH.log");
			this.model = new GRBModel(env);			
			this.initialisation();
			
			//AJOUT CONTRAINTES
		
			lancementContrainte();


		             System.out.println("j'optimise ");       
			this.model.optimize();
			
			    PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
			for(int k = 0; k < N; k++)
			for(int i=0;i<iMax;i++)
	            for(int j=0;j<jMax;j++)
	             {
	            	for(int l=0;l<jMax;l++)
	            		if(this.Y[i][j][l][k].get(GRB.DoubleAttr.X) == 1.0){
	            			System.out.println("\nTour " + k + "  i=" + i + "\tj=" +j + "\tl="+ l +"Y="+this.Y[i][j][l][k].get(GRB.DoubleAttr.X));
	            		}
	             }
			this.model.dispose();
			this.env.dispose();
			
		}catch(Exception e){
			System.out.println("Execption"  + e.getMessage());
			e.printStackTrace();
		}
	
	}
	
	public int[] getMarqueurPossible(int i)
	{
		return this.positionMarqueurPossible[i];
	}
	
	public int[] getPositionPossible(int i)
	{
		return this.positionPossible[i];
	}
	
	public int[] getPIJ(int i,int j)
	{
		return this.pij[i][j];
	}
	
	public int[] getMIJ(int i,int j)
	{
		return this.mij[i][j];
	}
	
	/*public static void main(String args[])
	{
		RushHour r = new RushHour("./puzzles/dÈbutant/jam1.txt");
		GurobiSolver g = new GurobiSolver(r,50);
		for(Integer i:g.getPIJ(0, 0))
			System.out.println(i.intValue());
	}*/
	
	
}

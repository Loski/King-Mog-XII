package RushHour;

import java.io.PrintWriter;
import java.util.ArrayList;
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
    private int positionMarqueurPossible[][];
    
    private RushHour rh;
    
	public GurobiSolver(RushHour rh, int N) {
		this.rh = rh;
		N++;
		this.N = N;
		this.iMax = rh.getVehicules().size();
		this.jMax = RushHour.DIMENSION_MATRICE;
		this.lMax = jMax;
		this.Y = new GRBVar[iMax][][][];
		this.X = new GRBVar[iMax][][];
		this.Z = new GRBVar[iMax][][];
		this.positionMarqueurPossible = new int[iMax][];
		this.positionPossible = new int[iMax][6];
		this.calculMarqueurPossible();
		this.calculPositionPossible();
		this.calculPIJ();
	}
	
	public void calculPositionPossible(){
		int vehicule = 0;
		for(Vehicule v: this.rh.getVehicules()){
			byte position = v.getPosition();
			byte orientation = v.getOrientation();
			byte saut = 6;
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
		this.pij = new int[36][36][];
		int saut;
		
		for(int iFor = 0; iFor < 36; iFor++){
			for(int jFor = 0; jFor < 36; jFor++){
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
				
				if((int)i/6 == (int) j/6){
					saut = 1;
					for(k = i;k<=j; k+=saut){
						a.add(k);
						compteur++;
					}
				}
				else if(i%6 == j%6){
					saut = 6;	
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
	                
	                    for(int j=0;j<jMax;j++)
	                        expr.addTerm(1.0,Z[i][j][k]);    
	                
	                this.model.addConstr(expr, GRB.EQUAL, this.rh.getVehicules().get(i).getTaille(), "C_1VehiculeDeplace_"+k);
	            }
	    }
	  
	//1 mvm par tour (16)
	private void onlyOneMvmByTurn() throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		for(int k=1;k<N;k++)
		{
			expr = new GRBLinExpr();
			
			for(int i=0;i<iMax;i++)
				for(int j=0;j<jMax;j++)
					for(int l=0;l<lMax;l++)
	            		{	
	            			expr.addTerm(1.0,Y[i][j][l][k]);	
	            		}
			
			this.model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "C_1VehiculeDeplace_"+k);
		}
	}
	
	//Contrainte maj marqueur  (17)
	private void majMarqueur() throws GRBException{
		GRBLinExpr exprL, exprR;
		for(int i=0;i<iMax;i++)
			for(int j=0;j<jMax;j++)
				for(int k=1;k<N;k++)
				{
					exprL = new GRBLinExpr();
					exprR = new GRBLinExpr();
					exprL.addTerm(1.0,X[i][j][k-1]);	
					exprR.addTerm(1.0,X[i][j][k]);
					
					for(int l=0;l<lMax;l++)
					{
						exprL.addTerm(-1.0, Y[i][j][l][k]);
						exprL.addTerm(1.0, Y[i][l][j][k]);
					}
					this.model.addConstr(exprL, GRB.EQUAL, exprR, "C_MajMarqueur_"+i+"_"+j+"_"+k);
				}
	}
	
	//18
	private void defPosVehicule() throws GRBException{
		GRBLinExpr exprL, exprR;
		for(int i=0;i<iMax;i++)
		{
			Vehicule vi = this.rh.getVehicules().get(i);
			/*int caseMax=-1;	
			if(vi.getOrientation()==RushHour.HORIZONTAL)
				caseMax=RushHour.TAILLE_MATRICE - vi.getTaille();
			else
				caseMax=RushHour.TAILLE_MATRICE - RushHour.DIMENSION_MATRICE * vi.getTaille()+ RushHour.DIMENSION_MATRICE-1;*/
			
			for(int j=0;j<jMax;j++)
			{
				/*int saut = 6;
				if(vi.getOrientation()==RushHour.HORIZONTAL){
					saut = 1;
					if(j%6 + saut * vi.getTaille() > 6)
						continue;
				}
				else
					if(j + saut * vi.getTaille() > 36)
						continue;*/
				
				for(int k=0;k<N;k++)
				{
					exprL = new GRBLinExpr();
					exprR = new GRBLinExpr();
					int tailleVehicule = vi.getTaille();
					int[] mij = calculMij(vi,j);
					exprL.addTerm((double)tailleVehicule,X[i][j][k]);
					if(mij == null)
						continue;
					for(Integer m : mij)
					{
						exprR.addTerm(1.0,Z[i][m][k]);
					}
					this.model.addConstr(exprL, GRB.LESS_EQUAL, exprR, "C_PosVehicule_"+i+"_"+j+"_"+k);
				}
			}
		}
	}
	
	// Un vÃ©hicule par case par tour ! (19)
	private void caseByTurn() throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		for(int j = 0; j < jMax; j++)
			for(int k = 0; k < this.N;k++)
			{
				expr = new GRBLinExpr();
				
				for(int i=0;i<iMax;i++)
				{
					expr.addTerm(1.0, Z[i][j][k]);
				}
				
				this.model.addConstr(expr, GRB.LESS_EQUAL, 1.0,"C_1VehiculeParCase_"+j+"_"+k);
			}
	}
	
	//  20 
	private void contrainteDeDeplacement() throws GRBException{
		GRBLinExpr expr;
		for(int i=0;i<iMax;i++)
			for(int j=0;j<jMax;j++)
				for(int l=0;l<lMax;l++){
					/*if(j==l)
						continue;*/
					int[] pJL = calculeP(j, l,this.rh.getVehicules().get(i));
					for(int k=1;k<N;k++)
					{					
						for(Integer p : pJL)
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
		
		//Crï¿½ation des variables et de la fonction objectif		
		
		GRBLinExpr obj = new GRBLinExpr();
		for(int i=0;i<iMax;i++)
            for(int j=0;j<jMax;j++)
                for(int l=0;l<lMax;l++)
                    for(int k=0;k<N;k++){
						Y[i][j][l][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"Y_"+i+"_"+j+"_"+l+"_"+k);
						obj.addTerm(1.0,Y[i][j][l][k]);
                    }
		
		model.setObjective(obj, GRB.MINIMIZE);
		
		for(int i=0;i<iMax;i++)
	        for(int j=0;j<jMax;j++)
	            for(int k=0;k<N;k++){
						X[i][j][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"X_"+i+"_"+j+"_"+k);
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
	
	/*public static void main(String args[])
	{
		RushHour r = new RushHour("./puzzles/débutant/jam1.txt");
		GurobiSolver g = new GurobiSolver(r,50);
		for(Integer i:g.getPIJ(0,7))
			System.out.println(i.intValue());
	}*/
	
	
}

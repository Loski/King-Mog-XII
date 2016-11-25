package RushHour;

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

    private RushHour rh;
    private int nombreVoiture;
    
	public GurobiSolver(RushHour rh, int N) {
		this.rh = rh;
		this.N = N;
		this.iMax = rh.getVehicules().size();
		this.jMax = RushHour.TAILLE_MATRICE;
		this.lMax = jMax;
		this.Y = new GRBVar[iMax][jMax][lMax][N];
		this.X = new GRBVar[iMax][jMax][N];
		this.Z = new GRBVar[iMax][jMax][N];
	}
	/*
    public boolean IsWin(){
    	return (this.X[this.rh.indice_solution_g][RushHour.CASE_SORTIE][N] == 1)? true: false;
    }*/
	private void lancementContrainte() throws GRBException{
		caseByTurn();
		majMarqueur();
		defPosVehicule();
		contrainteDeDeplacement();
		victoire();
		onlyOneMvmByTurn();
		
	}

	
	//Contrainte de victoire (15)
	private void victoire() throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		expr.addTerm(1.0,X[RushHour.indice_solution_g][RushHour.CASE_SORTIE][N-1]);
		this.model.addConstr(expr,  GRB.EQUAL, 1.0,  "C_Victoire");
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
					exprR.addTerm(-1.0,X[i][j][k]);
					for(int l=0;l<lMax;l++)
					{
						exprL.addTerm(1.0, Y[i][j][l][k]);
						exprL.addTerm(-1.0, Y[i][l][j][k]);
					}
					this.model.addConstr(exprL, GRB.EQUAL, exprR, "C_MajMarqueur_"+i+"_"+j+"_"+k);
				}
	}
	
	//18
	private void defPosVehicule() throws GRBException{
		GRBLinExpr exprL, exprR;
		for(int i=0;i<iMax;i++)
		{
			int caseMax=-1;
			
			Vehicule v = this.rh.getVehicules().get(i);
			
			if(v.getOrientation()==RushHour.HORIZONTAL)
				caseMax=RushHour.TAILLE_MATRICE - v.getTaille();
			else
				caseMax=RushHour.TAILLE_MATRICE - RushHour.DIMENSION_MATRICE * v.getTaille()+ RushHour.DIMENSION_MATRICE-1;
			
			for(int j=0;j<=caseMax;j++)
				for(int k=0;k<N;k++)
				{
					exprL = new GRBLinExpr();
					exprR = new GRBLinExpr();
					Vehicule vi = this.rh.getVehicules().get(i);
					int tailleVehicule = vi.getTaille();
					int[] mij = calculMij(vi,j);
					exprL.addTerm((double)tailleVehicule,X[i][j][k]);
					for(Integer m : mij)
					{
						exprR.addTerm(1.0,Z[i][m][k]);
					}
					this.model.addConstr(exprL, GRB.LESS_EQUAL, exprR, "C_PosVehicule_"+i+"_"+j+"_"+k);
				}
		}
	}
	
	// Un véhicule par case par tour ! (19)
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
		
		//Cr�ation des variables et de la fonction objectif		
		
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
						Z[i][j][k].set(GRB.DoubleAttr.Start, 0.0);
						X[i][j][k].set(GRB.DoubleAttr.Start, 0.0);
	                }
		
		for(int i = 0; i <iMax; i++){
			int position_initial = this.rh.getVehicules().get(i).getPosition(); 
			X[i][position_initial][0].set(GRB.DoubleAttr.Start, 1.0);
			int saut = 6;
			if(this.rh.getVehicules().get(i).getOrientation() == RushHour.HORIZONTAL)
				saut = 1;
			int taille = this.rh.getVehicules().get(i).getTaille();
			for(int j = this.rh.getVehicules().get(i).getPosition(); j < position_initial + saut * taille ;j+=saut){
				Z[i][j][0].set(GRB.DoubleAttr.Start, 1.0);
				//System.out.println(Z[i][j][0].get(GRB.DoubleAttr.));
			}
				
		}
		
	}
	
	public int[] calculeP(int j, int l, Vehicule v){
		int tmpMax = Math.max(j, l);
		int tmpMin = Math.min(j, l);
		j = tmpMin;
		l = tmpMax;
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
		return tab;		
	}
	
	public int[] calculMij(Vehicule vi, int j){
	
		int mij[] = new int[vi.getTaille()];
		int saut = RushHour.DIMENSION_MATRICE;
		
		if(vi.getOrientation()==RushHour.HORIZONTAL){
			saut = 1;
		}
		for(int z = 0; z < vi.getTaille();z++)
			mij[z]=j+z*saut;
		return mij;
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

			for(int i=0;i<iMax;i++)
	            for(int j=0;j<jMax;j++)
	             {
	                System.out.println("\n" + Z[i][j][0].get(GRB.DoubleAttr.X));
	             }
			this.model.dispose();
			this.env.dispose();
			
		}catch(GRBException e){
			System.out.println("Execption" + e.getErrorCode() + e.getMessage());
			e.printStackTrace();
		}
		
	}
}

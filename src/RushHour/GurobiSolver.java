package RushHour;

import java.util.ArrayList;
import java.util.HashMap;
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
	// Un véhicule par case par tour !
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
	
	//Contrainte de victoire
	private void victoire() throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		expr.addTerm(1.0,X[RushHour.indice_solution_g][RushHour.CASE_SORTIE][N-1]);
		this.model.addConstr(expr,  GRB.EQUAL, 1.0,  "C_Victoire");
	}
	
	//1 mvm par tour
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
	
	//Contrainte maj marqueur
	private void majMarqueur() throws GRBException{
		GRBLinExpr expr;
		for(int i=0;i<iMax;i++)
			for(int j=0;j<jMax;j++)
				for(int k=1;k<N;k++)
				{
					expr = new GRBLinExpr();
					expr.addTerm(1.0,X[i][j][k-1]);					
					for(int l=0;l<lMax;l++)
					{
						expr.addTerm(-1.0, Y[i][j][l][k]);
						expr.addTerm(1.0, Y[i][l][j][k]);
					}
					expr.addTerm(-1.0,X[i][j][k]);
					this.model.addConstr(expr, GRB.EQUAL,0.0, "C_MajMarqueur_"+i+"_"+j+"_"+k);
				}
	}
	private void defPosVehicule() throws GRBException{
		GRBLinExpr expr;
		for(int i=0;i<iMax;i++)
			for(int j=0;j<jMax;j++)
				for(int k=0;k<N;k++)
				{
					expr = new GRBLinExpr();
					Vehicule vi = this.rh.getVehicules().get(i);
					int tailleVehicule = vi.getTaille();		
					int [] mij = new int[tailleVehicule];
					int saut = 6;
					if(vi.getOrientation()==RushHour.HORIZONTAL){
						saut = 1;
						if(j%6 + saut * vi.getTaille() >= 6)
							continue;
					}
					else
						if(j + saut * vi.getTaille() >= 36)
							continue;
					for(int z = 0; z < vi.getTaille();z++)
						mij[z]=j+z*saut;
					//double somme = 0;				
					expr.addTerm((double)tailleVehicule,X[i][j][k]);
					for(Integer m : mij)
					{
						//somme+=Z[i][m][k].get(GRB.DoubleAttr.X);
						expr.addTerm(-1.0,Z[i][m][k]);
					}
					this.model.addConstr(expr, GRB.LESS_EQUAL,0.0, "C_PosVehicule_"+i+"_"+j+"_"+k);
				}
	}
	private void contrainteDeDeplacement() throws GRBException{
		GRBLinExpr expr;
		for(int i=0;i<iMax;i++)
			for(int j=0;j<jMax;j++)
				for(int l=0;l<lMax;l++){
					if(j==l)
						continue;
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
	public int[] calculMij(int i, int j, int k) throws GRBException{
		double z = this.X[i][j][k].get(GRB.DoubleAttr.Start);
		if(z == 0.0)
			return null;
		Vehicule v = this.rh.getVehicules().get(i);
		int tab[] = new int[v.getTaille()];
		int saut = RushHour.FORWARD;
		if(v.getOrientation() == RushHour.VERTICAL)
			saut = RushHour.DOWN;
		int position_initial = v.getPosition();
		int b = 0;
		for(int h = position_initial; h < position_initial + v.getTaille() * saut; h+= saut){
			tab[b] = j;
		}
		return tab;
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
			for(int k = 0; k < N; k++){
				System.out.println("\n\n\n");
			for(int i=0;i<iMax;i++)
	            for(int j=0;j<jMax;j++)
	             {
	                    	System.out.println("\n" + model.getVarByName("Z_"+i+"_"+j+"_"+k).get(GRB.DoubleAttr.X));
	                    }
			}
			this.model.dispose();
			this.env.dispose();
			
		}catch(GRBException e){
			System.out.println("Execption" + e.getErrorCode() + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	// Y Initialisation
	/*	for(int i = 0; i < this.nombreVoiture; i++){
			this.Y[i] = new GRBVar[RushHour.TAILLE_MATRICE][][];
			for(int j=0; j <RushHour.TAILLE_MATRICE;j++){
				this.Y[i][j] = new GRBVar[RushHour.DIMENSION_MATRICE][];
				for(int l = 0; l < RushHour.DIMENSION_MATRICE - this.rh.getVehicules().get(i).getTaille();l++){
					this.Y[i][j][l] = new GRBVar[N];
					for(int k = 0;k <N;k++){
						String st = "G_" + String.valueOf(i) + "_" + String.valueOf(j)
                        + "_" + String.valueOf(k);
						try {
							this.Y[i][k][l][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, st);
						} catch (GRBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}*/
}

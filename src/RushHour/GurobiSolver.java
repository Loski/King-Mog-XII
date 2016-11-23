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
	private void initialisation() throws GRBException{
		
		//Création des variables et de la fonction objectif		
		
		GRBLinExpr obj = new GRBLinExpr();
		for(int i=0;i<iMax;i++)
            for(int j=0;j<jMax;j++)
                for(int l=0;l<lMax;l++)
                    for(int k=0;k<N;k++){
						Y[i][j][l][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"Y_"+i+"_"+j+"_"+l+"_"+k);
						obj.addTerm(1.0,Y[i][j][l][k]);
						model.setObjective(obj, GRB.MINIMIZE);
                    }
		
		for(int i=0;i<iMax;i++)
	        for(int j=0;j<jMax;j++)
	            for(int k=0;k<N;k++){
						X[i][j][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"X_"+i+"_"+j+"_"+k);
						Z[i][j][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"Z_"+i+"_"+j+"_"+k);
	                }
	}
	public int[] calculeP(int j, int l, Voiture v){
		int tmpMax = Math.max(j, l);
		int tmpMin = Math.min(j, l);
		j = tmpMin;
		l = tmpMax;
		int saut = 1;
		if(v.getOrientation() == RushHour.VERTICAL)
			saut = 6;
		int tab[] = new int[6];
		int i = 0;
		while(j  < l){
			tab[i] = j;
			i++;
			j+=saut;
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
			
			GRBLinExpr expr = new GRBLinExpr();
			
			//Contrainte de victoire
			expr.addTerm(1.0,X[RushHour.indice_solution_g][RushHour.CASE_SORTIE][N-1]);
			this.model.addConstr(expr,  GRB.EQUAL, 1.0,  "C_Victoire");
			
			//Contrainte : 1 véhicule déplacée par mouvement
			for(int k=1;k<N;k++)
			{
				expr = new GRBLinExpr();
				
				for(int i=0;i<iMax;i++)
					for(int j=0;j<jMax;j++)
						for(int l=0;l<lMax;l++)
		            		{	
		            			expr.addTerm(1.0,Y[i][j][l][k]);
		            			this.model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "C_1VehiculeDeplace_"+k);	
		            		}
			}
			
			//Contrainte : MAJ du marqueur
			
			for(int i=0;i<iMax;i++)
				for(int j=0;j<jMax;j++)
					for(int k=1;k<N;k++)
					{
						expr = new GRBLinExpr();
						
						double val = X[i][j][k-1].get(GRB.DoubleAttr.X);
						for(int l=0;l<lMax;l++)
						{
							val-=Y[i][j][l][k].get(GRB.DoubleAttr.X);
							val+=Y[i][l][j][k].get(GRB.DoubleAttr.X);
						}
						
						expr.addTerm(1.0,X[i][j][k]);
						this.model.addConstr(expr, GRB.EQUAL,val, "C_MajMarqueur_"+i+"_"+j+"_"+k);
						
					}
			
			
			//Contrainte : définir les positions d'un véhicule
			
			for(int i=0;i<iMax;i++)
				for(int j=0;j<jMax;j++)
					for(int k=1;k<N;k++)
					{
						expr = new GRBLinExpr();
						
						Vehicule vi = this.rh.getVehicules().get(i);
						int tailleVehicule = vi.getTaille();
						
						int [] mij = new int[tailleVehicule];
						
						if(vi.getOrientation()==RushHour.HORIZONTAL)
							for(int s=0;s<tailleVehicule;s++)
								mij[s]=j+s;
						else
							for(int s=0;s<tailleVehicule;s++)
								mij[s]=j+s*RushHour.DIMENSION_MATRICE;
						
						double somme = 0;
						
						for(Integer m : mij)
						{
							//A VERIFIER
							somme+=Z[i][m][k].get(GRB.DoubleAttr.X);
						}
						
						expr.addTerm((double)tailleVehicule,X[i][j][k]);
						this.model.addConstr(expr, GRB.LESS_EQUAL,somme, "C_PosVehicule_"+i+"_"+j+"_"+k);
					}
			

			int v= 0;

			// Un véhicule par case par tours 
			
			
				/*for(int k = 0; k < this.N; k++)
					for(int i = 0; i < iMax;i++){
						expr = new GRBLinExpr();
						for(int j = 0; j < jMax; j++){
							expr.addTerm(1.0, this.Z[i][j][k]);
						}
						model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "marq"+v);
					}*/
			
			for(int j = 0; j < jMax; j++)
				for(int k = 0; k < this.N;k++)
				{
					expr = new GRBLinExpr();
					
					for(int i=0;i<iMax;i++)
					{
						expr.addTerm(1.0, Z[i][j][k]);
					}
					
					model.addConstr(expr, GRB.LESS_EQUAL, 1.0,"C_1VehiculeParCase_"+"_"+j+"_"+k);
				}
				
				GRBLinExpr obj = new GRBLinExpr();
				for(int i=0;i<iMax;i++)
		            for(int j=0;j<jMax;j++)
		                for(int l=0;l<lMax;l++)
		                    for(int k=0;k<N;k++){
								
		                    }	


		    //Contrainte : ne pas toucher qqchose pendant un déplacement



		                    
			this.model.optimize();
			
			this.model.dispose();
			this.env.dispose();
			
		}catch(Exception e){System.out.println(e.getMessage());}
		
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

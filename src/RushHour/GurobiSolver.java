package RushHour;

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
		
		//Cr�ation des variables et de la fonction objectif		
		
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
	
    private void addContrainte(HashMap<GRBVar,Double> vars,char comparaison,double compareTo,String nomContrainte) throws GRBException
    {
        GRBLinExpr expr = new GRBLinExpr();
        for (Entry<GRBVar, Double> var : vars.entrySet())
        {
            expr.addTerm(var.getValue().doubleValue(),var.getKey());
        }
        
        model.addConstr(expr, comparaison, compareTo, nomContrainte);
    }
	
	public void solve()
	{
		try
		{
			this.env = new GRBEnv("RH.log");
			this.model = new GRBModel(env);
			
			this.initialisation();
			int v = 0;
			HashMap<GRBVar,Double> vars;
			
			//Contrainte de victoire
			vars = new HashMap<GRBVar,Double>();
			vars.put(X[RushHour.indice_solution_g][RushHour.CASE_SORTIE][N-1],1.0);
			this.addContrainte(vars, GRB.EQUAL, 1.0, "cVictoire");
			
			// Contrainte 1 marqueur par voiture
			
			for(int i = 0; i < iMax;i++){
				GRBLinExpr expr = new GRBLinExpr();
				for(int j = 0; j < jMax; j++){
					for(int k = 0; k < this.N; k++)
					expr.addTerm(1.0, this.X[i][j][k]);
				}
				model.addConstr(expr, GRB.LESS_EQUAL, arg2, "marq"+v);
			}
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

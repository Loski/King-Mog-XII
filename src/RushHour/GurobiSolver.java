package RushHour;

import java.util.ArrayList;

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
    private int[][][] pjl;
    private int[][][] mij;
    private int positionMarqueurPossible[][];
    
    private RushHour rh;
    
	public GurobiSolver(RushHour rh, int N) {
		this.rh = rh;
		N++;
		this.N = N;
		this.iMax = rh.getVehicules().size();
		this.jMax = RushHour.TAILLE_MATRICE;
		this.lMax = jMax;
		this.Y = new GRBVar[iMax][jMax][lMax][this.N];
		this.X = new GRBVar[iMax][jMax][this.N];
		this.Z = new GRBVar[iMax][jMax][this.N];
		this.positionMarqueurPossible = new int[iMax][];
		this.positionPossible = new int[iMax][RushHour.DIMENSION_MATRICE];
		this.calculMarqueurPossible();
		this.calculPositionPossible();
		this.calculPjl();
		this.calculMij();
	}
	
	private void calculMij() {
		this.mij = new int[this.rh.getVehicules().size()][RushHour.TAILLE_MATRICE][0];
		int i=0;
		int saut;
		for(Vehicule vi : this.rh.getVehicules())
		{
			for(int pos:this.getMarqueurPossible(i))
			{	
				this.mij[i][pos] = new int[vi.getTaille()];
				if(vi.getOrientation() == RushHour.VERTICAL)
					saut = RushHour.DIMENSION_MATRICE;
				else
					saut = 1;
				for(int k=0;k<vi.getTaille();k++)
					this.mij[i][pos][k]=pos+k * saut;
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
	
	private void calculPjl(){
		this.pjl = new int[RushHour.TAILLE_MATRICE][RushHour.TAILLE_MATRICE][0];
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
				
				this.pjl[iFor][jFor] = new int[compteur];
				for(int z = 0; z < compteur; z++){
					this.pjl[iFor][jFor][z] = a.get(z);
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
		this.model.update();
		
		
	}

	
	//Contrainte de victoire
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
	                
	                int[] pos = this.getPositionPossible(i);
	                
	                    for(int j:pos)
	                        expr.addTerm(1.0,Z[i][j][k]);    
	                
	                if(pos.length>0)
	                	this.model.addConstr(expr, GRB.EQUAL, this.rh.getVehicules().get(i).getTaille(), "C_1VehiculeDeplace_"+k);
	            }
	    }
	  
	//1 mvm par tour
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
			
			if(pos.length>0)
				this.model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "C_1VehiculeDeplace_"+k);
		}
	}
	
	//Contrainte maj marqueur
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
					
					if(pos.length>0)
						this.model.addConstr(exprL, GRB.EQUAL, exprR, "C_MajMarqueur_"+i+"_"+j+"_"+k);
				}
		}
	}
	
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
					
					
					if(this.getMIJ(i, j).length > 0){
						this.model.addConstr(exprL, GRB.LESS_EQUAL, exprR, "C_PosVehicule_"+i+"_"+j+"_"+k+"_"+this.getMIJ(i, j)[0]);
					}
				}
			}
		}
	}
	
	// Un véhicule par case par tour
	private void caseByTurn() throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		
		ArrayList<ArrayList<Integer>> listOfPos = new ArrayList<ArrayList<Integer>>();
		
		for (int i=0;i<iMax;i++)
		{	
			ArrayList<Integer> liste = new ArrayList<Integer>();
			int[] tmp = this.getPositionPossible(i);
			for(Integer t : tmp){
				liste.add(t);
			}
			listOfPos.add(liste);
		}
		
		
		
		for(int j = 0; j < RushHour.TAILLE_MATRICE; j++)
			for(int k = 0; k < this.N;k++)
			{
				expr = new GRBLinExpr();
				boolean added = false;
				
				for(int i=0;i<iMax;i++)
				{

					if(listOfPos.get(i).contains(Integer.valueOf(j)))
					{
						expr.addTerm(1.0, Z[i][j][k]);
						added = true;
					}
				}
				
				if(added)
					this.model.addConstr(expr, GRB.LESS_EQUAL, 1.0,"C_1VehiculeParCase_"+j+"_"+k);
			}
	}
	
	private void contrainteDeDeplacement() throws GRBException{
		GRBLinExpr expr;
		ArrayList<ArrayList<Integer>> listOfPos = new ArrayList<ArrayList<Integer>>();
		
		for (int i=0;i<iMax;i++)
		{	
			ArrayList<Integer> liste = new ArrayList<Integer>();
			int[] tmp = this.getPositionPossible(i);
			for(Integer t : tmp){
				liste.add(t);
			}
			listOfPos.add(liste);
		}
		
		
		for(int i=0;i<iMax;i++)
		{			
			for(int j:this.getMarqueurPossible(i))
				for(int l:this.getMarqueurPossible(i)){
					
					for(int k=1;k<N;k++)
					{					
						for(int p: this.getPJL(j, l))
						{
							expr = new GRBLinExpr();
							expr.addTerm(1.0, Y[i][j][l][k]);
							boolean possible = false;
							for(int iPrime=0;iPrime<iMax;iPrime++)
								if(iPrime!=i)
								{
									if(listOfPos.get(iPrime).contains(Integer.valueOf(p))){
										expr.addTerm(1.0, Z[iPrime][p][k-1]);
										possible = true;
									}
								}
							if(possible)
								model.addConstr(expr, GRB.LESS_EQUAL, 1.0,"C_CasePasTouchee_"+i+"_"+j+"_"+l+"_"+k+"_"+p);
						}
					}
				}
		}

	}
	private void initialisation(byte methode) throws GRBException{
		
		//Cr�ation des variables et de la fonction objectif		
		
		GRBLinExpr obj = new GRBLinExpr();
		for(int i=0;i<iMax;i++)
            for(int j:this.getMarqueurPossible(i))
            {
            	for(int k=0;k<N;k++)
            	{
            		X[i][j][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"X_"+i+"_"+j+"_"+k);
            		
            		for(int l:this.getMarqueurPossible(i))
                    {
            				Y[i][j][l][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"Y"+i+""+j+""+l+""+k);

                        int nbCase = this.getPJL(j, l).length-1; 
                        
	                        if(methode==RushHour.RHM)
	                        	obj.addTerm(1.0,Y[i][j][l][k]);
	                        else
	                            obj.addTerm(nbCase,Y[i][j][l][k]);
                    }
            	}            	
            }
		
		model.update();
		
		model.setObjective(obj, GRB.MINIMIZE);
		
		model.update();
		
		for(int i=0;i<iMax;i++)
            for(int j:this.getPositionPossible(i))
                    for(int k=0;k<N;k++){
						Z[i][j][k]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY,"Z_"+i+"_"+j+"_"+k);
						model.update();
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
				model.update();
			}
				
		}
		
	}

	
	public Object[] solve(byte methode)
	{
		try
		{
			this.env = new GRBEnv("RH.log");
			this.model = new GRBModel(env);	
			
			this.model.getEnv().set(GRB.DoubleParam.TimeLimit, 120.0);
			this.initialisation(methode);
			
			//AJOUT CONTRAINTES
		
			lancementContrainte();

			this.model.optimize();
			Object result[] = new Object[3];
			
			int status = model.get(GRB.IntAttr.Status);
		    if (status == GRB.Status.TIME_LIMIT){
		    	result[0] = (Integer)(int)model.get(GRB.DoubleAttr.ObjVal);
		    	System.out.println("TIME LIMIT EXCEED");
		    	return result;
		    }
		    
		    
		    if (status == GRB.Status.INF_OR_UNBD ||
			        status == GRB.Status.INFEASIBLE  ||
			        status == GRB.Status.UNBOUNDED     ){
		    		result[0] = (Integer)(int)model.get(GRB.DoubleAttr.ObjVal)+1000;
			    	this.model.dispose();
					this.env.dispose();
			    	System.out.println("N TROP PETIT");
			    	return result;
			    }
		    
			ArrayList<RushHour> graphe = new ArrayList<RushHour>();
			graphe.add(rh);
			RushHour precedent = rh;
			int nbCase = 0;
			for(byte k = 0; k < N; k++)
				for(byte i=0;i<iMax;i++)
					for(byte j=0;j<jMax;j++)
					{
						for(byte l=0;l<jMax;l++)
							if(this.Y[i][j][l][k] != null && this.Y[i][j][l][k].get(GRB.DoubleAttr.X) == 1.0){
		            			 RushHour tmp = new RushHour(precedent);
		            			 tmp.getVehicules().get(i).setPosition(l);
		            			 tmp.majGrille();
		            			 graphe.add(tmp);
		            			 precedent = tmp;
		            			 nbCase+=this.getPJL(j, l).length-1;
							}
					}
		    	
		    
		    if(methode==RushHour.RHC)
		    	result[0] = (Integer)(int)model.get(GRB.DoubleAttr.ObjVal);
		    else
		    	result[0] = nbCase;
			result[1] = graphe;
			result[2]=(Integer)(int)model.get(GRB.DoubleAttr.ObjVal);
			this.model.dispose();
			this.env.dispose();
				
			return result;
			}catch(Exception e){
				System.out.println("Execption"  + e.getMessage());
				e.printStackTrace();
			}
		return null;
	}
	public int[] getMarqueurPossible(int i)
	{
		return this.positionMarqueurPossible[i];
	}
	
	public int[] getPositionPossible(int i)
	{
		return this.positionPossible[i];
	}
	
	public int[] getPJL(int j,int l)
	{
		return this.pjl[j][l];
	}
	
	public int[] getMIJ(int i,int j)
	{
		return this.mij[i][j];
	}
	
	/*public static void main(String[] args)
	{
		RushHour r = new RushHour("./puzzles/débutant/jam1.txt");
		GurobiSolver g = new GurobiSolver(r,10);
		
		System.out.println(g.getPJL(0,12).length-1);
	}*/

	
}

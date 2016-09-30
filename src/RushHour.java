import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RushHour {

	private ArrayList<ArrayList<String>> grille;
	private int nbLigne;
	private int nbColonne;
	
	public RushHour(String filename)
	{
		BufferedReader buffer;
		String x;
		grille = new ArrayList<ArrayList<String>>();
		
        try 
        {
            buffer = new BufferedReader(new FileReader(filename));
            
            if((x = buffer.readLine()) != null)	
            {
            	//System.out.println(x);
            	
            	Scanner scanner = new Scanner(x);
            	this.nbLigne = scanner.nextInt();
            	this.nbColonne = scanner.nextInt();
            	scanner.close();
            }
            
            while ( (x = buffer.readLine()) != null ) {
                // printing out each line in the file
                //System.out.println(x);
            	
            	ArrayList<String> ar = new ArrayList<String>();
            	
            	StringTokenizer st = new StringTokenizer(x," ");
            	while(st.hasMoreElements())
            	{
            		ar.add((String)st.nextElement());
            	}
            	
            	grille.add(ar);
            }
            
        }catch(Exception e){e.printStackTrace();}
	}
	
	public void afficher()
	{
		for(int i=0;i<this.nbLigne;i++)
		{			
			for(int j=0;j<this.nbColonne;j++)
			{
				System.out.print(this.grille.get(i).get(j)+"\t");
			}
			
			System.out.println();
		}	
	}
	
	public static void main(String[] args)
	{
		RushHour r1 = new RushHour("puzzles/débutant/jam1.txt");
		r1.afficher();
	}

	
}

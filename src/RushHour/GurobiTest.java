package RushHour;

public class GurobiTest {

	public static void main(String[] args) {
		RushHour rh = new RushHour("puzzles/test/test3.txt");
		GurobiSolver g = new GurobiSolver(rh, 12);
		g.solve(RushHour.RHC);
	}

}

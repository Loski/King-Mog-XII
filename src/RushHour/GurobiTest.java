package RushHour;

public class GurobiTest {

	public static void main(String[] args) {
		RushHour rh = new RushHour("puzzles/debug.txt");
		GurobiSolver g = new GurobiSolver(rh, 6);
		g.solve();
	}

}

package RushHour;

public class GurobiTest {

	public static void main(String[] args) {
		RushHour rh = new RushHour("puzzles/débutant/jam1.txt");
		GurobiSolver g = new GurobiSolver(rh, 12);
		g.solve();
	}

}

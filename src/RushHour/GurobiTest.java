package RushHour;

public class GurobiTest {

	public static void main(String[] args) {
		RushHour rh = new RushHour("puzzles/intermédiaire/jam14.txt");
		GurobiSolver g = new GurobiSolver(rh, 20);
		g.solve();
	}

}

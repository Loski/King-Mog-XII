package RushHour;

public class GurobiTest {

	public static void main(String[] args) {
		RushHour rh = new RushHour("débutant/jam1");
		GurobiSolver g = new GurobiSolver(rh, 12);
	}

}

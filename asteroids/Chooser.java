package asteroids;

import javax.swing.*;

/**
 * Dialog to choose number of players in MPAsteroids.
 */
public class Chooser {
	public static void main(String[] args) {
		AbstractGame game = null;
		switch (choose()) {
			case 0: game = new MPAsteroids(2); break;
			case 1: game = new MPAsteroids(3); break;
			case 2: game = new MPAsteroids(4); break;
			case 3: game = new MPAsteroids(16); break;
		}
		game.mainLoop();
	}

	private static int choose() {
		String[] options = {"2", "3", "4", "16"};
		int n = JOptionPane.showOptionDialog(new JFrame(),
			"Number of players? (2 humans controllers)",
			"MPAsteroids",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null, options, options[0]);
		if (n == JOptionPane.CLOSED_OPTION)
			System.exit(0);
		return n;
	}
}

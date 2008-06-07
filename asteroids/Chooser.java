package asteroids;
import javax.swing.*;

public class Chooser {
	public static void main(String[] args) {
		AbstractGame game = multiplayer() ? new MPAsteroids() : new Asteroids();
		game.mainLoop();
	}

	private static boolean multiplayer() {
		String[] options = {"Multiplayer", "Single-player"};
		int n = JOptionPane.showOptionDialog(new JFrame(),
			"Choose a game mode:",
			"Asteroids",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null, options, options[1]);
		if (n == JOptionPane.CLOSED_OPTION)
			System.exit(0);
		return n == 0;
	}
}

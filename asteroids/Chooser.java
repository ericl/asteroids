package asteroids;
import javax.swing.*;

public class Chooser {
	public static void main(String[] args) {
		AbstractGame game = null;
		switch (choose()) {
			case 0: game = new MPAsteroids(); break;
			case 1: game = new Asteroids(); break;
			case 2: game = new Protect(); break;
		}
		game.mainLoop();
	}

	private static int choose() {
		String[] options = {"Multiplayer", "Single-player", "Defend Europa"};
		int n = JOptionPane.showOptionDialog(new JFrame(),
			"Choose a game mode:",
			"Asteroids",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null, options, options[1]);
		if (n == JOptionPane.CLOSED_OPTION)
			System.exit(0);
		return n;
	}
}

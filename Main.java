
/** JFrame that starts the program and contains the game Panel
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Main extends JFrame {
	public static final int WIDTH = 1100;
	public static final int HEIGHT = 800;

	private GamePanel game;

	public static void main(String[] args) {
		Main main = new Main();

		// (warning here that says:
		// lambda expressions are only allowed level 1.8 and above)
		SwingUtilities.invokeLater(main::createFrame);
	}

	public void createFrame() {
		// Sets the title
		this.setTitle("Settlers of Catan");

		// Get screen size to set the panel size correctly
		// https://stackoverflow.com/questions/3680221/how-can-i-get-screen-resolution-in-java
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//System.out.println(screenSize);
		this.setSize(screenSize);

		// Allows the application to close
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//addMenuBar();

		this.game = new GamePanel();
		game.setBounds(0, 0, Main.WIDTH, Main.HEIGHT);
		game.setPreferredSize(new Dimension(Main.WIDTH, Main.HEIGHT));

		// Puts game Panel into a scrolling container
		JScrollPane scrollFrame = new JScrollPane(game);
		game.setAutoscrolls(true);
		scrollFrame.setPreferredSize(new Dimension(screenSize));
		this.add(scrollFrame);


		scrollFrame.setVisible(true);
		this.setVisible(true);

		game.howToPlay();
	}

	private void addMenuBar() {

		JMenuBar bar = new JMenuBar();
		// Add the menu bar to the JFrame
		this.setJMenuBar(bar);

		JMenu menu = new JMenu("Help");
		menu.setMnemonic('H');
		JMenuItem item = new JMenuItem("How to Play", 'P');
		item.addActionListener(e -> game.howToPlay());
		menu.add(item);

		item = new JMenuItem("Rules", 'R');
		item.addActionListener(e -> showRules());
		menu.add(item);

		bar.add(menu);
		
	}

	private void showRules() {
		try {
       		File file = new File("rules.pdf");
        	Desktop.getDesktop().open(file);
   		} catch (Exception ex) {
			System.err.println(ex.getMessage() + "\n");
        	// no application registered for PDFs
   		}
	}
}
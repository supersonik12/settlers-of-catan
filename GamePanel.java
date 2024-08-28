
/** GamePanel is a JPanel that runs the game of Catan
 * Has all event handlers and runs trading and building
 * (ActionListener and MouseListener)
 * Draws the board and all buttons
 * Has 4 Players and keeps track of whose turn it is
 * Also has rolling dice! with actual images of the die faces
*/

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements ActionListener {

	private Player[] players;
	private int current;
	private int lookForClick = 0;

	private Color[] colors = new Color[] { new Color(255, 140, 25), new Color(244, 0, 0), new Color(255, 255, 255),
			new Color(0, 92, 204) };
	Color tan = new Color(234, 211, 171);

	private Board board;
	private int boardX;
	private int boardY;

	private BufferedImage[] dice;
	private int rolled1 = 3;
	private int rolled2 = 4;
	private int diceX = 50;
	private int diceY = 305;

	private JButton rollDiceBtn;
	private JButton endTurnBtn;
	private JButton dTradeBtn;
	private JButton mTradeBtn;
	private JButton bRoadBtn;
	private JButton bSettlementBtn;
	private JButton bCityBtn;
	private JButton bDevCardBtn;
	private ArrayList<JButton> buttons;

	public GamePanel() {
		setLayout(null);
		boardX = Main.WIDTH / 2 - 275;
		boardY = Main.HEIGHT / 2 - 340;
		// System.out.println("boardX = " + boardX + ", boardY = " + boardY);
		board = new Board();

		players = new Player[4];

		gameSetup();
		createComponents();

		// UIManager.put("OptionPane.background", tan);
		// UIManager.getLookAndFeelDefaults().put("Panel.background", tan);

		updatePhase(0);
	}

	// SETUP METHODS
	// Resets settlements, players, etc
	private void gameSetup() {
		// future: Prompt user for names
		// for now -
		int x = (int) (Math.random() * 3);
		players[0] = new Player("Player 1", colors[0], 1);
		x = (int) (Math.random() * 3);
		players[1] = new Player("Player 2", colors[1], 2);
		x = (int) (Math.random() * 3);
		players[2] = new Player("Player 3", colors[2], 0);
		x = (int) (Math.random() * 3);
		players[3] = new Player("Player 4", colors[3], 2);

		// Will become 0 once updatePhase is called
		current = 3;

		// DELETE AFTER TESTING
		players[3].editResource(0, 4);
		players[3].editResource(1, 4);
		players[3].editResource(2, 4);
		players[3].editResource(3, 4);
		players[3].editResource(4, 4);

		// Default starting setup:
		// Orange
		board.createSettlement(363, 134, players[0], true);
		board.createRoad(363, 134, 313, 109, players[0]);
		board.createSettlement(263, 407, players[0], true);
		board.createRoad(263, 407, 313, 432, players[0]);

		// Red
		board.createSettlement(213, 110, players[1], true);
		board.createRoad(213, 110, 263, 134, players[1]);
		board.createSettlement(113, 309, players[1], true);
		board.createRoad(113, 309, 163, 334, players[1]);

		// White
		board.createSettlement(163, 208, players[2], true);
		board.createRoad(163, 208, 113, 233, players[2]);
		board.createSettlement(413, 309, players[2], true);
		board.createRoad(413, 309, 413, 233, players[2]);

		// Blue
		board.createSettlement(163, 407, players[3], true);
		board.createRoad(163, 407, 213, 432, players[3]);
		board.createSettlement(363, 407, players[3], true);
		board.createRoad(363, 407, 363, 334, players[3]);

	}

	// Creates all JComponents
	private void createComponents() {
		this.setBackground(new Color(79, 166, 235));

		board.setBounds(boardX, boardY, Main.WIDTH, Main.HEIGHT);
		this.add(board);
		endTurnBtn = new JButton("End Turn");
		rollDiceBtn = new JButton("Roll Dice");
		dTradeBtn = new JButton("Domestic Trade");
		mTradeBtn = new JButton("Maritime Trade");
		bRoadBtn = new JButton("Build Road");
		bSettlementBtn = new JButton("Build Settlement");
		bCityBtn = new JButton("Build City");
		bDevCardBtn = new JButton("Development Card");

		buttons = new ArrayList<>();
		this.addButton(endTurnBtn, boardX + 575, 700, 190, 30);
		this.addButton(rollDiceBtn, 63, 515, 150, 30);

		this.addButton(dTradeBtn, boardX + 585, boardY + 90, 190, 30);
		dTradeBtn.setToolTipText("Trade with other players");
		this.addButton(mTradeBtn, boardX + 585, boardY + 140, 190, 30);
		mTradeBtn.setToolTipText("Trade with the bank");

		this.addButton(bRoadBtn, boardX + 585, boardY + 250, 190, 30);
		bRoadBtn.setToolTipText("1 Wood + 1 Brick");
		this.addButton(bSettlementBtn, boardX + 585, boardY + 300, 190, 30);
		bSettlementBtn.setToolTipText("1 Wood + 1 Brick + 1 Grain + 1 Wool");
		this.addButton(bCityBtn, boardX + 585, boardY + 350, 190, 30);
		bCityBtn.setToolTipText("3 Ore + 2 Grain");
		this.addButton(bDevCardBtn, boardX + 585, boardY + 400, 190, 30);
		bDevCardBtn.setToolTipText("1 Ore + 1 Grain + 1 Wool");

		// load the dice icons
		try {
			dice = new BufferedImage[6];
			// for larger dice try dice.png (in google drive for now)
			BufferedImage image = ImageIO.read(new File("small_dice.png"));
			for (int i = 0; i < 6; i++) {
				BufferedImage die = image.getSubimage(i * 90, 0, 90, 83);
				dice[i] = die;
			}
		} catch (IOException e) {
			System.out.println("Error");
		}

		// This MouseListener is used to add settlements and roads
		// Only does stuff when lookForClick > 0
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// System.out.println("Mouse clicked at " + e.getX() + ", " + e.getY());
				// When lookForClick != 0, an event is expected to happen
				// 1: Settlement, 2: Road, 3: Build City
				int x = e.getX() - boardX;
				int y = e.getY() - boardY;

				click(x, y);
			}
		});

	}

	// Helper method for createComponents()
	// Adds a button at the specified location
	private void addButton(JButton btn, int x, int y, int w, int h) {
		btn.setBounds(x, y, w, h);

		btn.setBackground(players[current].getColor());
		btn.setForeground(Color.BLACK);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setFont(new Font("Sans_Serif", Font.BOLD, 15));

		this.add(btn);
		btn.addActionListener(this);
		// Also adds the button to an array so formatting can be applied later
		buttons.add(btn);
	}

	public void howToPlay() {
		Border outline = BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK);
		Border space = BorderFactory.createEmptyBorder(15, 15, 15, 15);
		UIManager.put("OptionPane.border", BorderFactory.createCompoundBorder(outline, space));
		String text = "At the start of every turn, roll the dice to get resources!";
		text += "\nThen, use the trade and build buttons to expand your civilization.";
		text += "\n\nTrading - make an offer and let the other player accept or decline.";
		text += "\nBuilding - click the button, then click on the board to build.";
		text += "\nDevelopment Cards - manage your cards on your turn from the build menu.";
		text += "\n\nWhen you're done, click the End Turn button to move on to the next person.";
		JOptionPane.showConfirmDialog(this,
				text,
				"How to Play",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	// GUI METHODS

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(new Font("Sans_Serif", Font.BOLD, 12));

		rectangles(g, 50, boardY + 200, 175, 300, current);
		rectangles(g, boardX + 560, boardY + 50, 240, 470, current);

		g.drawImage(dice[rolled1 - 1], diceX, diceY, this);
		g.drawImage(dice[rolled2 - 1], diceX + 30 + rolled1 * 5, diceY + 65 + rolled2 * 5, this);

		rectangles(g, 50, 30, 220, 160, current);
		players[current].drawBanner(g, 65, 55, 80);
		g.drawString("" + players[current], 65, 50);
		for (int type = 0; type < 5; type++) {
			String s = Player.resourceTypes[type] + ": " + players[current].getResource(type);
			g.drawString(s, 165, 65 + (type) * 25);
		}

		int x = 50;
		int y = 675;
		int i = (current + 1) % players.length;
		while (i != current) {
			rectangles(g, x - 20, y - 30, 200, 150, i);
			players[i].drawBanner(g, x, y + 5, 60);

			g.setColor(players[i].getColor());
			g.drawString("" + players[i], x, y);
			for (int type = 0; type < 5; type++) {
				String s = Player.resourceTypes[type] + ": " + players[i].getResource(type);
				g.drawString(s, x + 70, y + (type) * 25);
			}
			x += 247;
			i = (i + 1) % players.length;
		}

		Border outline = BorderFactory.createMatteBorder(3, 3, 3, 3, players[current].getColor());
		Border space = BorderFactory.createEmptyBorder(20, 20, 20, 20);
		UIManager.put("OptionPane.border", BorderFactory.createCompoundBorder(outline, space));

	}

	// Painting method that draws a rectangle with a border
	// Border is a player's color; player is an int bc it's the index
	private void rectangles(Graphics g, int x, int y, int w, int h, int player) {
		g.setColor(players[player].getColor());
		g.fillRect(x - 3, y - 3, w + 6, h + 6);
		g.setColor(tan);
		g.fillRect(x, y, w, h);
	}

	// Simple dialog box (with OK button) that shows the text
	// Used to give information or error messages to the user
	private void message(Object text) {
		JOptionPane.showConfirmDialog(this,
				text,
				players[current].toString(),
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	// TRADE / BUILD METHODS
	// GamePanel implements actionListener
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e.getActionCommand() + " button clicked");
		if (lookForClick != 0) {
			// System.out.println("Something in progress");
			return;
		}

		switch (e.getActionCommand()) {
			case ("Domestic Trade"):
				// Trading with another player
				trade(false);
				break;

			case ("Maritime Trade"):
				// Trading with the bank at a 4:1 ratio
				trade(true);
				break;

			case ("Build Road"):
				updatePhase(2);
				// Check if player has enough materials
				if (players[current].getResource(Player.WOOD) < 1
						|| players[current].getResource(Player.BRICKS) < 1) {
					message("Not enough resources");
					return;
				} else {
					// System.out.println("click somewhere");
					bRoadBtn.setBackground(Color.BLACK);
					bRoadBtn.setForeground(Color.WHITE);
				}

				// Let user click to add the road
				// MouseListener will do the rest
				lookForClick = 2;
				break;

			case ("Build Settlement"):
				updatePhase(2);
				// Check if player has enough materials
				if (players[current].getResource(Player.WOOD) < 1
						|| players[current].getResource(Player.BRICKS) < 1
						|| players[current].getResource(Player.GRAIN) < 1
						|| players[current].getResource(Player.WOOL) < 1) {
					message("Not enough resources");
					return;
				} else {
					// System.out.println("click somehwere");
					bSettlementBtn.setBackground(Color.BLACK);
					bSettlementBtn.setForeground(Color.WHITE);
				}

				// Let user click to add the settlement
				// MouseListener will do the rest
				lookForClick = 1;
				break;

			case ("Build City"):
				updatePhase(2);
				if (players[current].getResource(Player.GRAIN) < 2
						|| players[current].getResource(Player.ORE) < 3) {
					message("Not enough resources");
					return;
				} else {
					// System.out.println("click somewhere");
					bCityBtn.setBackground(Color.BLACK);
					bCityBtn.setForeground(Color.WHITE);
				}
				lookForClick = 3;
				break;

			case ("End Turn"):
				updatePhase(0);
				break;

			case ("Roll Dice"):
				rollDice();
				break;

			default:
				// default case
		}
		repaint();
	}

	private void click(int x, int y) {
		switch (lookForClick) {
			case (0):
				return;
			case (1):
				if (board.createSettlement(x, y, players[current], false)) {
					// Subtract materials used for settlement (1 wood, brick, wheat and sheep)
					players[current].editResource(Player.WOOD, -1);
					players[current].editResource(Player.BRICKS, -1);
					players[current].editResource(Player.GRAIN, -1);
					players[current].editResource(Player.WOOL, -1);
				} else {
					message("Error - invalid location");
				}

				bSettlementBtn.setBackground(players[current].getColor());
				bSettlementBtn.setForeground(Color.BLACK);
				// Reset 'event expected' variable
				break;
			case (2):
				if (board.createRoad(x - 20, y - 20, x + 20, y + 20, players[current])) {
					players[current].editResource(Player.WOOD, -1);
					players[current].editResource(Player.BRICKS, -1);
				} else {
					message("Error - invalid location");
				}

				bRoadBtn.setBackground(players[current].getColor());
				bRoadBtn.setForeground(Color.BLACK);
				break;
			case (3):
				// upgradeSettlement returns false if the location was invalid
				if (board.upgradeSettlement(x, y, players[current])) {
					players[current].editResource(Player.GRAIN, -2);
					players[current].editResource(Player.ORE, -3);
				} else {
					message("Error - Settlement not found");
				}
				bCityBtn.setBackground(players[current].getColor());
				bCityBtn.setForeground(Color.BLACK);
				break;
			case (7):
				Settlement[] targets = board.steal(x, y);
				repaint();
				// ask user which settlement to rob
				Player selected = ((Settlement) JOptionPane.showInputDialog(this,
						"Pick a player to steal from:",
						"This is a robbery!",
						JOptionPane.PLAIN_MESSAGE, null,
						targets, "Player1")).getPlayer();

				updatePhase(1);
				rollDiceBtn.setBackground(players[current].getColor());
				rollDiceBtn.setForeground(Color.BLACK);

				// force users to discard half their cards?

				break;
		}
		// TODO: change this back
		lookForClick = 0;
		repaint();
	}

	// trade method - prompts players for trading resources
	// parameters: if maritime is true, runs trades with bank instead of other
	// player
	// called by event listener for both trade buttons
	private void trade(boolean maritime) {

		// Create a JPanel to add to the JOptionPane (dialog box)
		JTextField selling = new JTextField(5);
		selling.setText("1");
		JComboBox dropdown = new JComboBox(Player.resourceTypes);
		JTextField buying = new JTextField(5);
		buying.setText("1");
		JComboBox dropdown2 = new JComboBox(Player.resourceTypes);
		JPanel dialogPanel = new JPanel();

		dialogPanel.add(new JLabel(players[current].toString() + " wants to trade "));
		dialogPanel.add(selling);
		dialogPanel.add(dropdown);

		// Different text for domestic and maritime trade
		if (maritime) {
			selling.setText("4");
			dialogPanel.add(new JLabel(" for 1/4 "));
		} else {
			dialogPanel.add(new JLabel(" for "));
			dialogPanel.add(buying);
		}
		dialogPanel.add(dropdown2);

		// Prompt the user for how much / what to trade
		Player selected = null;
		if (maritime) {
			JOptionPane.showConfirmDialog(this,
					dialogPanel,
					"Maritime Trade",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			// For maritime trade; an imaginary player that represents the bank
			selected = new Player("the bank", players[current].getColor());
		} else {
			selected = (Player) JOptionPane.showInputDialog(this,
					dialogPanel,
					"Domestic Trade",
					JOptionPane.PLAIN_MESSAGE, null,
					players, players[current]);
		}

		try {
			String sellType = dropdown.getSelectedItem().toString();
			int sellAmt = Integer.parseInt(selling.getText());
			String buyType = dropdown2.getSelectedItem().toString();
			int buyAmt = Integer.parseInt(buying.getText());

			if (maritime) {
				// For maritime trade, the bank trades at a 4:1 ratio
				buyAmt = sellAmt / 4;
				sellAmt = sellAmt - (sellAmt % 4);
				// temporarily add enough resources to the 'bank'
				selected.editResource(Player.resource(buyType), buyAmt);
			}

			if (sellType.equals(buyType) || selected.equals(players[current])) {
				return;
			} else if ((sellAmt <= 0) || (buyAmt <= 0)) {
				return;
			}

			String prompt = String.format("%s wants to trade %d %s for %d %s with %s.",
					players[current], sellAmt, sellType, buyAmt, buyType, selected);

			// Allow the other user to accept or decline the trade
			Border outline = BorderFactory.createMatteBorder(3, 3, 3, 3, selected.getColor());
			Border space = BorderFactory.createEmptyBorder(20, 20, 20, 20);
			UIManager.put("OptionPane.border", BorderFactory.createCompoundBorder(outline, space));
			String[] options = { "Accept", "Decline" };
			int resp = JOptionPane.showOptionDialog(this, prompt, selected.toString(), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (resp == JOptionPane.NO_OPTION) {
				// The selected player declined the trade
				return;
			}
			// Player class runs trading - resources are added/subtracted from both players
			// returns false if either user has too few resources
			if (!players[current].trade(selected, Player.resource(sellType), sellAmt, Player.resource(buyType),
					buyAmt)) {
				message("Not enough resources");
			}

		} catch (NumberFormatException e) {
			message("Error - invalid input");
		}
	}

	// GAME METHODS

	// update to the next phase of the game
	// phases are 0 Roll Dice, 1 Trade, 2 Build
	// Called mostly by actionPerformed
	private void updatePhase(int phase) {
		if (players[current].getPoints() >= 10) {
			message("CONGRATS - YOU WON THE GAME!");
			message("Click OK to Play Again");
			gameSetup();
			repaint();
			phase = 0;
		}
		switch (phase) {
			// phase 0 - rolling dice, all other buttons deactivated
			case (0):
				// Switch to the next player's turn
				current = (current + 1) % 4;

				// Update look of buttons to match player color
				// Disable all buttons except Roll Dice
				for (JButton btn : buttons) {
					btn.setEnabled(false);
					btn.setBackground(players[current].getColor());
				}
				rollDiceBtn.setEnabled(true);
				break;
			// phase 1 - trading, build buttons also available
			case (1):
				for (JButton btn : buttons) {
					btn.setEnabled(!btn.isEnabled());
				}
				break;
			// phase 2 - building only no more trading
			case (2):
				dTradeBtn.setEnabled(false);
				mTradeBtn.setEnabled(false);
				break;
		}
	}

	// Rolls two six-sided dice and displays results
	// Then, calls hexes that have that number
	private void rollDice() {
		rolled1 = (int) (Math.random() * 5) + 1;
		rolled2 = (int) (Math.random() * 5) + 1;

		diceX = (int) (Math.random() * 20) + 40;
		diceY = (int) (Math.random() * 30) + 300;
		int d = (int) (Math.random() * 30);
		repaint();

		String output = "You rolled a " + (rolled1 + rolled2);

		int num = rolled1 + rolled2;
		// System.out.println(rolled1 + " " + rolled2 + " = " + num);
		if (num == 7) {
			message("You rolled a 7 - the robber attacks!" + "\nPick a hex to rob");
			rollDiceBtn.setBackground(Color.BLACK);
			rollDiceBtn.setForeground(Color.WHITE);
			lookForClick = 7;
			return;
		}
		for (Hex[] row : board.getBoard()) {
			for (Hex hex : row) {
				if (num == hex.getNumber()) {
					output += "\n" + hex.numberRolled();
					output += " players recieved " + Player.resourceTypes[hex.getResource()];
				}
			}
		}
		updatePhase(1);
		message(output);
	}

}
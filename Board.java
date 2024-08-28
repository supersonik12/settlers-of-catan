
/** The gameboard has a 2D array of Hexes, is a JComponent
 * Stores its coordinates and can draw itself with Graphics
 * Stores and draws Settlements and Roads
 * To add things correctly, has an arraylist of Points
 * which are found on the junctions/crossroads
*/

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Board extends JComponent {

	private Hex[][] gameboard;
	private Polygon[][] hexes;

	private ArrayList<Settlement> towns;
	private ArrayList<Road> roads;
	private ArrayList<Point> points;

	public Color background = new Color(234, 211, 171);

	public Board() {
		towns = new ArrayList<Settlement>();
		points = new ArrayList<Point>();
		createPoints();
		roads = new ArrayList<Road>();
		startingSetup();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(new Font("Sans_Serif", Font.BOLD, 20));

		// g.drawRect(0,0,550,550);

		int x = 120;
		int y = 20;
		// Draw hexagons and numbers
		for (int row = 0; row < gameboard.length; row++) {
			x = 120 - ((gameboard[row].length - 3) * 50);
			for (int col = 0; col < gameboard[row].length; col++) {
				g.setColor(background);
				g.fillPolygon(hexes[row][col]);
				g.setColor(gameboard[row][col].getColor());
				g.fillPolygon(createHexagon(x, y, 100));

				// If the hex is being robbed, show a black circle
				// Robber starts at Desert and is moved on a 7
				if (gameboard[row][col].robber) {
					g.setColor(Color.BLACK);
					g.fillOval(x + 21, y + 31, 40, 40);
				}

				g.setColor(Color.WHITE);
				// Center one digit numbers
				int numX = x + 35;
				// Center two-digit numbers
				if (gameboard[row][col].getNumber() > 9) {
					numX = x + 28;
				}
				g.drawString("" + gameboard[row][col].getNumber(), numX, y + 60);

				x += 100;
			}
			y += 100;
		}

		// Settlements and Roads have draw methods
		for (Road road : roads) {
			road.draw(g);
		}

		for (Settlement town : towns) {
			town.draw(g);
			// System.out.println("Town at " + town.getX() + ", " + town.getY());
		}

	}

	// Creates a Polygon with six sides
	// Used by paintComponent and to store hexes
	private Polygon createHexagon(int x, int y, int size) {
		Polygon p = new Polygon();
		// WARNING: GEOMETRY - PROCEED WITH CAUTION
		double a = Math.sqrt(3) / 2 * (size / 2);
		p.addPoint(x, y + size / 4);
		p.addPoint(x, y + 3 * size / 4);
		p.addPoint(x + (int) a, y + size);
		p.addPoint(x + (int) (2 * a), y + 3 * size / 4);
		p.addPoint(x + (int) (2 * a), y + size / 4);
		p.addPoint(x + (int) a, y);
		return p;
	}

	private void startingSetup() {
		Hex[] row1 = { new Hex(10, Player.ORE), new Hex(2, Player.WOOL), new Hex(9, Player.WOOD) };
		Hex[] row2 = { new Hex(12, Player.GRAIN), new Hex(6, Player.BRICKS), new Hex(4, Player.WOOL),
				new Hex(10, Player.BRICKS) };
		Hex[] row3 = { new Hex(9, Player.GRAIN), new Hex(11, Player.WOOD), new Desert(), new Hex(3, Player.WOOD),
				new Hex(8, Player.ORE) };
		Hex[] row4 = { new Hex(8, Player.WOOD), new Hex(3, Player.ORE), new Hex(4, Player.GRAIN),
				new Hex(5, Player.WOOL) };
		Hex[] row5 = { new Hex(5, Player.BRICKS), new Hex(6, Player.GRAIN), new Hex(11, Player.WOOL) };

		gameboard = new Hex[][] { row1, row2, row3, row4, row5 };

		hexes = new Polygon[][] { new Polygon[3], new Polygon[4], new Polygon[5], new Polygon[4], new Polygon[3] };
		int x = 120;
		int y = 20;
		// create hexagons
		for (int row = 0; row < gameboard.length; row++) {
			x = 120 - ((gameboard[row].length - 3) * 50);
			for (int col = 0; col < gameboard[row].length; col++) {
				hexes[row][col] = createHexagon(x - 17, y - 20, 140);
				x += 100;
			}
			y += 100;
		}

	}

	public boolean createRoad(int x1, int y1, int x2, int y2, Player player) {

		Point one = getPoint(x1, y1);
		Point two = getPoint(x2, y2);
		if (one == null || two == null) {
			return false;
		}
		// Check if location of road is valid
		// - connected to a Settlement or another road
		// - not in the same place as an existing road
		boolean valid = false;
		for (Road road : roads) {
			// If a road already exists, return false
			if (road.getStart().equals(one) && road.getEnd().equals(two) ||
					road.getEnd().equals(one) && road.getStart().equals(two)) {
				return false;
			}
			if (road.getPlayer().toString().equals(player.toString())) {
				if (road.getStart().equals(one) || road.getStart().equals(two)
						|| road.getEnd().equals(one) || road.getEnd().equals(two)) {
					valid = true;
				}
			}
		}
		for (Settlement town : towns) {
			if (town.getPlayer().toString().equals(player.toString())) {
				if (town.getX() == one.getX() && town.getY() == one.getY()) {
					valid = true;
				} else if (town.getX() == one.getX() && town.getY() == one.getY()) {
					valid = true;
				}
			}
		}
		if (!valid) {
			return false;
		}
		roads.add(new Road(one, two, player));
		return true;
	}

	public boolean createSettlement(int x, int y, Player player, boolean setup) {
		// System.out.println(x + " " + y);
		Point point = getPoint(x, y);
		try {
			x = point.x;
			y = point.y;
		} catch (NullPointerException e) {
			return false;
		}

		// Hex[] hexes = new Hex[3];
		// add to hexes
		// Check if valid
		boolean valid = setup;
		for (Settlement town : towns) {
			if (town.getX() == point.getX() && town.getY() == point.getY()) {
				return false;
			}
		}
		for (Road road : roads) {
			if (player.toString().equals(road.getPlayer().toString())) {
				if (road.getStart().equals(point) || road.getEnd().equals(point)) {
					valid = true;
				}
			}
		}
		if (!valid) {
			return false;
		}

		Settlement s = new Settlement(player, x, y);

		// add it to board's settlements list
		towns.add(s);

		for (int row = 0; row < hexes.length; row++) {
			for (int col = 0; col < hexes[row].length; col++) {
				if (hexes[row][col].contains(x, y)) {
					int actualX = x - hexes[row][col].getBounds().x;
					int actualY = y - hexes[row][col].getBounds().y;
					gameboard[row][col].addSettlement(Hex.corner(actualX, actualY, 140), s);
				}
			}
		}
		return true;

	}

	public boolean upgradeSettlement(int x, int y, Player player) {
		Point point = getPoint(x, y);
		try {
			x = point.x;
			y = point.y;
		} catch (NullPointerException e) {
			return false;
		}

		for (Settlement town : towns) {
			// System.out.println(town.getPlayer() + " " + player);
			if (town.getX() == x && town.getY() == y && town.getPlayer().toString().equals(player.toString())) {
				town.becomeCity();
				// System.out.println(town);
				return true;
			}
		}
		return false;
	}

	// Returns settlements on that hex
	public Settlement[] steal(int x, int y) {
		for (int row = 0; row < hexes.length; row++) {
			for (int col = 0; col < hexes[row].length; col++) {
				gameboard[row][col].robber = false;
			}
		}

		for (int row = 0; row < hexes.length; row++) {
			for (int col = 0; col < hexes[row].length; col++) {
				if (hexes[row][col].contains(x, y)) {
					gameboard[row][col].robber = true;
					return gameboard[row][col].getSettlements();
				}
			}
		}
		return null;
	}

	public Hex[][] getBoard() {
		return gameboard;
	}
	// public void createRoad(int x1, int y1, Player player) {
	// Point point = getPoint(x1, y1);
	// int x = point.x;
	// int y = point.y;
	// Settlement s = new Settlement(player, x, y);

	// // add it to board's settlements list
	// towns.add(s);

	// // add to hexes
	// for (int row = 0; row < hexes.length; row++) {
	// for (int col = 0; col < hexes[row].length; col++) {
	// if (hexes[row][col].contains(x, inputY)) {
	// int actualX = x - hexes[row][col].getBounds().x;
	// int actualY = inputY - hexes[row][col].getBounds().y;
	// gameboard[row][col].addRoad(Hex.corner(actualX, actualY, 140), s);
	// }
	// }
	// }

	// }

	private Point getPoint(int inputX, int inputY) {
		for (Point point : points) {
			if (inputX > point.x - 40 && inputX < point.x + 40) {
				if (inputY > point.y - 40 && inputY < point.y + 40) {
					return point;
				}
			}
		}
		return null;
	}

	private void createPoints() {
		int townX = 113;
		int townY = 35;
		int rowMax = 7;
		for (int row = 0; row < 3; row++) {
			townX = 113;
			townX -= row * 50;
			for (int col = 0; col < rowMax; col++) {
				points.add(new Point(townX, townY));
				// System.out.println("(" + townX + ", " + townY + ")");
				townX += 50;
				if (col % 2 == 1) {
					townY += 25;
				} else {
					townY -= 25;
				}
			}
			rowMax += 2;
			townY += 124;
		}

		townY = 309;
		for (int row = 3; row < 6; row++) {
			townX = 13;
			townX += (row - 3) * 50;
			rowMax -= 2;
			for (int col = 0; col < rowMax; col++) {
				points.add(new Point(townX, townY));
				// System.out.println("(" + townX + ", " + townY + ")");
				townX += 50;
				if (col % 2 == 1) {
					townY -= 25;
				} else {
					townY += 25;
				}
			}
			townY += 73;
		}
	}
}
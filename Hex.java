
/**
 * Represents one hexagon on the Catan gameboard.
 * Each hex has a number (that tells you when it gives resources)
 * and a resource type.
 * It also has an array of Settlements.
 * Knows if it is being robbed (currently-being-robbed hexes
 * don't produce resources).
 */
import java.awt.Color;

public class Hex {
	private int number;
	private int type;
	private Settlement[] settlements;
	public boolean robber;

	// type is an int - use Player constants
	public Hex(int number, int type) {
		this.number = number;
		this.type = type;
		settlements = new Settlement[6];
		robber = false;
	}

	// Adds resources to all settlements
	// Doesn't add if currently being robbed, and returns false
	public int numberRolled() {
		if (robber) {
			return 0;
		}

		int count = 0;
		for (Settlement s : settlements) {
			if (s != null) {
				s.addResource(type);
				count++;
			}
		}
		return count;
	}

	// Adds a settlement to that corner
	public void addSettlement(int corner, Settlement s) {
		settlements[corner] = s;
	}

	public Settlement[] getSettlements() {
		return settlements;
	}

	// Checks if a settlement location is valid
	// Used by GamePanel
	public boolean checkLocation(int corner, Player player) {
		int prev = (corner - 1 + 6) % 6;
		int next = (corner + 1 + 6) % 6;

		return (settlements[prev] == null && settlements[corner] == null && settlements[next] == null);
	}

	// doesnt work yet don't use
	// public boolean checkRoad(int road, Player player) {
	// int prev = (road - 1 + 6) % 6;
	// int next = (road + 1 + 6) % 6;
	// return ((settlements[prev] == null || settlements[next] == null)) &&
	// ((roads[prev].equals(player) || roads[corner].equals(player)));
	// }

	// Returns color by type of resource this hex produces
	public Color getColor() {
		switch (type) {
			case (Player.WOOD):
				return new Color(33, 119, 31);
			case (Player.BRICKS):
				return new Color(214, 95, 0);
			case (Player.ORE):
				return new Color(122, 112, 175);
			case (Player.GRAIN):
				return new Color(255, 206, 43);
			case (Player.WOOL):
				return new Color(150, 150, 150);
			default:
				return null;
		}
	}

	// Figures out which corner of the hexagon was clicked
	// based on the x and y coordinates
	// 0 is the top of the hexagon, corners are 0-5 clockwise
	public static int corner(int x, int y, int size) {
		if (x < size / 3) {
			if (y < size / 2) {
				return 5;
			} else {
				return 4;
			}

		} else if (x > 2 * size / 3) {
			if (y < size / 2) {
				return 1;
			} else {
				return 2;
			}

		} else {
			if (y < size / 2) {
				return 0;
			} else {
				return 3;
			}
		}
	}

	public int getNumber() {
		return number;
	}

	public int getResource() {
		return type;
	}

}
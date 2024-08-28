
/**
 * The Player object stores the player's resources, name, Color, and number of Victory Points
 * It handles adding resources and trading between players
 * It also can draw a banner! with unique styles
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class Player {

	// Static constants for resource indexes
	public static final int WOOD = 0;
	public static final int BRICKS = 1;
	public static final int ORE = 2;
	public static final int GRAIN = 3;
	public static final int WOOL = 4;

	// Static - names of the resources
	public static final String[] resourceTypes = new String[] { "Wood", "Bricks", "Ore", "Grain", "Wool" };

	// Player instance's current resources
	private int[] resources;

	private String name;
	private Color color;
	private Image symbol;
	private int banner;

	private int victoryPoints;

	public Player(String name, Color color) {
		this(name, color, 0);
	}

	public Player(String name, Color color, int bannerStyle) {
		this.name = name;
		this.color = color;

		this.resources = new int[5];
		this.symbol = null;
		this.banner = bannerStyle;

		victoryPoints = 2;
	}

	// Returns the index in resourceTypes of a String
	// If text is invalid, returns -1
	public static int resource(String text) {
		for (int i = 0; i < resourceTypes.length; i++) {
			if (text.equals(resourceTypes[i])) {
				return i;
			}
		}
		return -1;
	}

	// Draws a banner using the Graphics object
	// Used by GamePanel to display Players
	public void drawBanner(Graphics g, int x, int y, int width) {
		int height = width / 2 * 3;
		switch (banner) {
			case (1):
				// g.setColor(Color.BLACK);
				// g.fillPolygon(new int[]{x, x+width, x+width, x+(width/2), x}, new int[]{y, y,
				// y+height, y+height*3/4, y+height}, 5);
				g.setColor(color);
				g.fillPolygon(new int[] { x + 3, x + width - 3, x + width - 3, x + (width) / 2, x + 3 },
						new int[] { y + 3, y + 3, y + height - 6, y + (height - 6) * 3 / 4, y + height - 6 }, 5);
				break;
			case (2):
				// g.setColor(Color.BLACK);
				// g.fillPolygon(new int[]{x, x+width, x+width, x+(width/2), x}, new int[]{y, y,
				// y+height*3/4, y+height, y+height*3/4}, 5);
				g.setColor(color);
				g.fillPolygon(new int[] { x + 3, x + width - 3, x + width - 3, x + (width) / 2, x + 3 },
						new int[] { y + 3, y + 3, y + (height - 3) * 3 / 4, y + height - 6, y + (height - 3) * 3 / 4 },
						5);
				break;
			case (0):
			default:
				// g.setColor(Color.BLACK);
				// g.fillRect(x, y, width, height);
				g.setColor(color);
				g.fillRect(x + 3, y + 3, width - 6, height - 6);
				break;
		}
		if (symbol != null) {
			// do image stuff
		}
	}

	// Returns number of resources of that type
	// Use constants with this method!
	public int getResource(int type) {
		if (type < resources.length && type >= 0) {
			return resources[type];
		} else {
			return 0;
		}
	}

	// Edit resource of that type, add amt (can be -)
	// Use constants with this method!
	public void editResource(int type, int amt) {
		// System.out.println("adding resource");
		if (type < resources.length && type >= 0) {
			resources[type] += amt;
		}
	}

	// Trade with another player
	// this player gives away amt1 type1, gets amt2 type2
	// returns false if either player had too few resources
	public boolean trade(Player other, int type1, int amt1, int type2, int amt2) {
		if (this.getResource(type1) < amt1 || other.getResource(type2) < amt2) {
			return false;
		}
		this.editResource(type1, amt1 * -1);
		other.editResource(type1, amt1);

		other.editResource(type2, amt2 * -1);
		this.editResource(type2, amt2);

		return true;
	}

	public void addPoint() {
		victoryPoints++;
	}

	public int getPoints() {
		return victoryPoints;
	}

	public Color getColor() {
		return color;
	}

	public String toString() {
		return name;
	}

}
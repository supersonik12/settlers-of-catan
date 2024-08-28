
/** Represents one Settlement belonging to a player
 * Stores its coordinates and can draw itself with Graphics
 * Cities are just Settlements that give 2 resources instead of 1
 * 
*/

import java.awt.*;

public class Settlement {
	private Player player;
	private int x;
	private int y;
	private int amt = 1;

	public Settlement(Player player, int x, int y) {
		this.player = player;
		player.addPoint();
		this.x = x;
		this.y = y;
	}

	public void addResource(int type) {
		player.editResource(type, amt);
	}

	public void becomeCity() {
		if (amt == 1) {
			// System.out.println("Upgrading " + player + "'s settlement");
			amt = 2;
			player.addPoint();
		}
	}

	public Player getPlayer() {
		return player;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void draw(Graphics g) {
		g.setColor(player.getColor());
		if (amt == 1) {
			g.fillOval(this.x - 10, this.y - 10, 20, 20);
		} else {
			g.fillOval(this.x - 15, this.y - 15, 30, 30);
		}
	}

	public String toString() {
		return player.toString();
	}
}
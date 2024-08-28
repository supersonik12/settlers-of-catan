
/** 
 * Represents a road owned by a Player
 * Stores its own coordinates (relative to Board)
 * Can draw itself as a dashed line with a Graphics object 
 */
import java.awt.*;

public class Road {
	private Player player;
	private int x1;
	private int y1;
	private int x2;
	private int y2;

	public Road(Point one, Point two, Player player) {
		this.player = player;
		x1 = one.x;
		y1 = one.y;
		x2 = two.x;
		y2 = two.y;
	}

	public void draw(Graphics g) {
		g.setColor(player.getColor());

		// Code for dashed line copied from
		// https://stackoverflow.com/questions/21989082/drawing-dashed-line-in-java/21989406#21989406
		Graphics2D g2 = (Graphics2D) g;
		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
				0, new float[] { 8 }, 0);
		g2.setStroke(dashed);
		g2.drawLine(x1, y1, x2, y2);
	}

	public Point getStart() {
		return new Point(x1, y1);
	}

	public Point getEnd() {
		return new Point(x2, y2);
	}

	public Player getPlayer() {
		return player;
	}
}
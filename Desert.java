/**
 * The center Hex on the board; doesn't produce resources.
 * Starts with the robber.
 **/
public class Desert extends Hex {

	public Desert() {
		super(7, -1);
		super.robber = true;
	}

	// Desert doesn't produce resources on a 7
	// IDK why this is a warning, it IS overriding a superclass method
	@Override
	public int numberRolled() {
		// Does nothing (Board handles robber behavior)
		return -1;
	}
}
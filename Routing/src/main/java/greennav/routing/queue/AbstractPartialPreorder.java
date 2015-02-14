package greennav.routing.queue;


/**
 * Using this abstract class it is possible to define a partial preorder by just
 * defining the lessEqual-method. The other methods are derived from that.
 */
public abstract class AbstractPartialPreorder<S> implements IPartialPreorder<S> {
	@Override
	public boolean equiv(S a, S b) {
		// Notice, that a specialized equiv-method might be more efficient than
		// testing for both lessEquals.
		return lessEqual(a, b) && lessEqual(b, a);
	}

	@Override
	public boolean greaterEqual(S a, S b) {
		return lessEqual(b, a);
	}

	@Override
	public boolean improves(S a, S b) {
		return !lessEqual(b, a);
	}
}

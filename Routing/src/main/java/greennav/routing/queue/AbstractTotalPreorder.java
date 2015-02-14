package greennav.routing.queue;



/**
 * Using this abstract class it is possible to define a partial preorder by just
 * defining the lessEqual-method. The other methods are derived from that.
 */
public abstract class AbstractTotalPreorder<S> extends
		AbstractPartialPreorder<S> implements ITotalPreorder<S> {
}

package greennav.routing.queue;

/**
 * This interface represents a comparison function. You need to ensure at least
 * reflexivity and transitivity for partial preorders!
 * 
 * @param <K>
 *            The objects to be preordered.
 */
public interface IPartialPreorder<K> {

	/**
	 * Checks, if a is less or equal to b.
	 */
	public boolean lessEqual(K a, K b);

	/**
	 * Checks, if a is greater or equal to b.
	 */
	public boolean greaterEqual(K a, K b);

	/**
	 * Checks, if both objects are equivalent.
	 */
	public boolean equiv(K a, K b);

	/**
	 * Checks, if a improves b, that means if b is not less or equal to a.
	 * 
	 * @param a
	 *            The first preordered object.
	 * @param b
	 *            The second preordered object.
	 * @return True, if a improves b.
	 */
	public boolean improves(K a, K b);
}

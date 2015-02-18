package greennav.routing.queue;

/**
 * A total preorder is a partial preorder, where each element can be compared to
 * each other element. That is, for all a and b we have a <= b or b <= a, maybe
 * both (which does not imply equality but only congruence).
 * 
 * @param <K>
 *            The objects to be preordered.
 */
public interface ITotalPreorder<K> extends IPartialPreorder<K> {
}

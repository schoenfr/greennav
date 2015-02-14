package greennav.routing.queue;

import java.util.Set;

/**
 * Represents a partial preorder queue. The elements are ordered with respect to
 * a reflexive and transitive relation. However, the ordering is not
 * antisymmetric and more importantly not total. Because totality is missing, we
 * can not use Comparable or Comparator interfaces!
 */
public interface IPartialPreorderQueue<K, V> extends ITotalPreorderQueue<K, V> {
	/**
	 * Retrieve a set of all minimal elements
	 * 
	 * @return Set of minimal elements.
	 */
	public Set<Pair<K, V>> front();

}

package greennav.routing.queue;


/**
 * Represents a total order queue. The elements are ordered with respect to a
 * reflexive, transitive and total relation.
 * 
 * Notice, that the ordering is not necessarily antisymmetric. Therefore, the
 * pull operation is not necessarily deterministic.
 */
public interface ITotalPreorderQueue<K, V> {
	/**
	 * Insert an entry consisting of a key and its value.
	 * 
	 * @param k
	 *            The key is used to identify an object.
	 * @param v
	 *            The value is commonly used for ordering.
	 */
	public void insert(K k, V v);

	/**
	 * Change the value of an existing key.
	 * 
	 * @param k
	 *            The key is used to identify an object.
	 * @param v
	 *            The value is commonly used for ordering.
	 */
	public void change(K k, V v);

	/**
	 * Pull an arbitrary minimal entry from the queue.
	 * 
	 * @return An arbitrary entry having a minimal value.
	 */
	public Pair<K, V> pull();

	/**
	 * Peek at an arbitrary minimal entry from the queue.
	 * 
	 * @return An arbitrary entry having a minimal value.
	 */
	public Pair<K, V> peek();

	/**
	 * Check, if queue is empty.
	 * 
	 * @return True, if empty.
	 */
	public boolean isEmpty();

	/**
	 * Check, if queue contains a key.
	 * 
	 * @param k
	 *            Key to be searched for.
	 * @return True, if queue contains key.
	 */
	public boolean in(K k);

	/**
	 * Get the value corresponding to a specific key.
	 * 
	 * @param k
	 *            Key to be searched for.
	 * @return Value corresponding to the given key.
	 */
	public V get(K k);

	/**
	 * The number of entries in the queue.
	 * 
	 * @return Number of entries.
	 */
	public int getSize();

	/**
	 * Remove all elements from the queue.
	 */
	public void clear();

}

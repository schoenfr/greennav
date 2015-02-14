package greennav.routing.queue;

/**
 * Interface for PartialPreorderQueue factories.
 * 
 * @param K
 *            The type of the keys.
 * @param V
 *            The type of the partially preordered values.
 * @param Q
 *            The type of queue containing K-V-entries.
 */
public interface ITotalPreorderQueueFactory<K, V, Q extends ITotalPreorderQueue<K, V>> {

	/**
	 * An identifier describes the type of the queues produced by this factory.
	 * 
	 * @return The identifier describing the queue types.
	 */
	public String getIdentifier();

	/**
	 * Creates an empty partial preorder queue.
	 * 
	 * @return Empty partial preorder queue.
	 */
	public Q createEmpty(ITotalPreorder<V> preorder);
}

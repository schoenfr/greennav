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
public interface IPartialPreorderQueueFactory<K, V, Q extends IPartialPreorderQueue<K, V>>
		extends ITotalPreorderQueueFactory<K, V, Q> {

	/**
	 * Creates an empty partial preorder queue.
	 * 
	 * @return Empty partial preorder queue.
	 */
	public Q createEmpty(IPartialPreorder<V> preorder);
}

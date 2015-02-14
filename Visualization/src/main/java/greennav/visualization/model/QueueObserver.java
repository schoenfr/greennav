package greennav.visualization.model;


/**
 * Observers of this type recognize manipulations on partial preorder queues,
 * namely insertion, pulling, computing the front and throwing exceptions.
 */
public interface QueueObserver {

	/**
	 * This method is called when elements are inserted into the queue.
	 * 
	 * @param queue
	 *            The queue.
	 * @param key
	 *            The key to be inserted.
	 * @param value
	 *            The corresponding value represents the partially preordered
	 *            priority.
	 */
	public void insert(Object queue, Object key, Object value);

	/**
	 * This method is called when elements are pulled out of the queue.
	 * 
	 * @param queue
	 *            The queue.
	 * @param key
	 *            The key to be removed.
	 * @param value
	 *            The corresponding value represents the partially preordered
	 *            priority.
	 */
	public void pull(Object queue, Object key, Object value);

	/**
	 * This method is called when the front is computed.
	 * 
	 * @param queue
	 *            The queue.
	 * @param front
	 *            The computed front.
	 */
	public void front(Object queue, Object front);

	/**
	 * This method is called whenever anything unexpected is recognized within
	 * the queue.
	 * 
	 * @param queue
	 *            The queue.
	 */
	public void failed(Object queue);
}
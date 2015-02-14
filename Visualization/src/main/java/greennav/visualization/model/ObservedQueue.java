package greennav.visualization.model;

import greennav.model.modelling.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class decorates partial preorder queues with an observer pattern. The
 * observers get notified about all method invocations and corresponding results
 * of the queue functionality. This replaces the AspectJ functionality, which
 * hooked up undifferentiated with each instance of partial preorder queues.
 * 
 * @param <K>
 *            The type of keys for the queue.
 * @param <V>
 *            The type of values for the queue.
 */
public class ObservedQueue extends PriorityQueue {

	private List<QueueObserver> observers = new LinkedList<QueueObserver>();

	public void addObserver(QueueObserver observer) {
		observers.add(observer);
	}

	public void deleteObserver(QueueObserver observer) {
		observers.remove(observer);
	}

	/**
	 * The base queue.
	 */
	private PriorityQueue base;

	/**
	 * The constructor takes the base queue.
	 * 
	 * @param base
	 *            The queue all method invocations are forwarded to.
	 */
	public ObservedQueue(PriorityQueue base) {
		this.base = base;
	}
	
	@Override
	public boolean add(Object e) {
		return base.add(e);
	}
	
	@Override
	public void clear() {
		base.clear();
		// notifyObservers(new Object[] { "clear" });
	}

	@Override
	public boolean isEmpty() {
		boolean isEmpty = base.isEmpty();
		// notifyObservers(new Object[] { "isEmpty", isEmpty });
		return isEmpty;
	}

	public void setBase(PriorityQueue base) {
		// notifyObservers(new Object[] { "setBase", this.base, base });
		this.base = base;
	}

}

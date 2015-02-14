package greennav.routing.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
public class ObservedQueue<K, V> implements IPartialPreorderQueue<K, V> {

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
	private IPartialPreorderQueue<K, V> base;

	/**
	 * The constructor takes the base queue.
	 * 
	 * @param base
	 *            The queue all method invocations are forwared to.
	 */
	public ObservedQueue(IPartialPreorderQueue<K, V> base) {
		this.base = base;
	}

	@Override
	public void change(K k, V v) {
		base.change(k, v);
		// notifyObservers(new Object[] { "change", k, v });
	}

	@Override
	public void clear() {
		base.clear();
		// notifyObservers(new Object[] { "clear" });
	}

	@Override
	public Set<Pair<K, V>> front() {
		Set<Pair<K, V>> front = base.front();
		for (QueueObserver qo : observers)
			qo.front(this, front);
		return front;
	}

	@Override
	public V get(K k) {
		V v = base.get(k);
		// notifyObservers(new Object[] { "get", k, v });
		return v;
	}

	@Override
	public int getSize() {
		int size = base.getSize();
		// notifyObservers(new Object[] { "size", size });
		return size;
	}

	@Override
	public boolean in(K k) {
		boolean in = base.in(k);
		// notifyObservers(new Object[] { "in", k, in });
		return in;
	}

	@Override
	public void insert(K k, V v) {
		base.insert(k, v);
		for (QueueObserver qo : observers)
			qo.insert(this, k, v);
	}

	@Override
	public boolean isEmpty() {
		boolean isEmpty = base.isEmpty();
		// notifyObservers(new Object[] { "isEmpty", isEmpty });
		return isEmpty;
	}

	@Override
	public Pair<K, V> peek() {
		Pair<K, V> peek = base.peek();
		// notifyObservers(new Object[] { "peek", peek });
		return peek;
	}

	@Override
	public Pair<K, V> pull() {
		Pair<K, V> pull = base.pull();
		for (QueueObserver qo : observers)
			qo.pull(this, pull.getFirst(), pull.getSecond());
		return pull;
	}

	public void setBase(IPartialPreorderQueue<K, V> base) {
		// notifyObservers(new Object[] { "setBase", this.base, base });
		this.base = base;
	}

}

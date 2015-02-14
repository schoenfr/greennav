package greennav.routing.queue;

import java.util.LinkedList;
import java.util.List;

public class ObservedQueueFactory<K, V> implements
		IPartialPreorderQueueFactory<K, V, IPartialPreorderQueue<K, V>>,
		QueueObserver {

	IPartialPreorderQueueFactory<K, V, ? extends IPartialPreorderQueue<K, V>> base;

	private List<QueueObserver> observers = new LinkedList<QueueObserver>();

	public void addObserver(QueueObserver observer) {
		observers.add(observer);
	}

	public void deleteObserver(QueueObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void failed(IPartialPreorderQueue<?, ?> queue) {
		for (QueueObserver qo : observers)
			qo.failed(queue);
	}

	@Override
	public void front(Object queue, Object front) {
		for (QueueObserver qo : observers)
			qo.front(queue, front);
	}

	@Override
	public void insert(Object queue, Object key, Object value) {
		for (QueueObserver qo : observers)
			qo.insert(queue, key, value);
	}

	@Override
	public void pull(Object queue, Object key, Object value) {
		for (QueueObserver qo : observers)
			qo.pull(queue, key, value);
	}

	public ObservedQueueFactory(
			IPartialPreorderQueueFactory<K, V, ? extends IPartialPreorderQueue<K, V>> base) {
		this.base = base;
	}

	@Override
	public String getIdentifier() {
		return base.getIdentifier();
	}

	@Override
	public IPartialPreorderQueue<K, V> createEmpty(IPartialPreorder<V> preorder) {
		ObservedQueue<K, V> res = new ObservedQueue<K, V>(
				base.createEmpty(preorder));
		System.out.println("observedqueuefactory 60: observing queue");
		res.addObserver(this);
		return res;
	}

	@Override
	public IPartialPreorderQueue<K, V> createEmpty(ITotalPreorder<V> preorder) {
		ObservedQueue<K, V> res = new ObservedQueue<K, V>(
				base.createEmpty(preorder));
		System.out.println("observedqueuefactory 69: observing queue");
		res.addObserver(this);
		return res;
	}

	public IPartialPreorderQueueFactory<K, V, ? extends IPartialPreorderQueue<K, V>> getBase() {
		return base;
	}

}

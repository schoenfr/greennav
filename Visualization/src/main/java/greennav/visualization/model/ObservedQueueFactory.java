package greennav.visualization.model;

import greennav.routing.algorithms.QueueFactory;
import greennav.routing.data.Graph.Vertex;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class ObservedQueueFactory extends QueueFactory implements QueueObserver {

	QueueFactory base;

	private List<QueueObserver> observers = new LinkedList<QueueObserver>();

	public void addObserver(QueueObserver observer) {
		observers.add(observer);
	}

	public void deleteObserver(QueueObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void failed(Object queue) {
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

	public ObservedQueueFactory(QueueFactory base) {
		this.base = base;
	}

	public QueueFactory getBase() {
		return base;
	}

	@Override
	public PriorityQueue<Vertex> createQueue(Comparator<Vertex> comparator) {
		ObservedQueue oq = new ObservedQueue(base.createQueue(comparator));
		oq.addObserver(this);
		return oq;
	}

}

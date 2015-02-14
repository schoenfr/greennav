package greennav.routing.algorithms;

import greennav.routing.data.Graph.Vertex;

import java.util.Comparator;
import java.util.PriorityQueue;

public abstract class QueueFactory {

	public abstract PriorityQueue<Vertex> createQueue(
			Comparator<Vertex> comparator);

	public static QueueFactory getDefault() {
		return new QueueFactory() {

			@Override
			public PriorityQueue<Vertex> createQueue(
					Comparator<Vertex> comparator) {
				return new PriorityQueue<Vertex>(1, comparator);
			}
		};
	}

}

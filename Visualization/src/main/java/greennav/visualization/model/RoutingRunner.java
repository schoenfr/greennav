package greennav.visualization.model;

import greennav.routing.algorithms.Dijkstra;
import greennav.routing.data.Graph.Vertex;
import greennav.routing.data.path.IPath;
import greennav.routing.data.vehicle.Vehicle;
import greennav.routing.queue.IPartialPreorderQueue;
import greennav.routing.queue.IPartialPreorderQueueFactory;
import greennav.routing.queue.IQueue;
import greennav.routing.queue.ObservedQueueFactory;
import greennav.routing.queue.PartialPreorderTreeFactory;
import greennav.routing.queue.QueueObserver;
import greennav.routing.server.Server;
import greennav.visualization.data.TraceEvent;
import greennav.visualization.data.TraceEvent.PathFoundEvent;
import greennav.visualization.data.TraceEvent.SearchStartedEvent;
import greennav.visualization.data.TraceEvent.VertexEnqueuedEvent;
import greennav.visualization.data.TraceEvent.VertexVisitedEvent;
import greennav.visualization.data.TraceObserver;
import greennav.visualization.util.Gobservable;

import java.util.HashMap;
import java.util.Map;

/**
 * The routing runner is used to run the routing algorithms and to observe their
 * behavior via aspects defined in the corresponding package.
 */
public class RoutingRunner extends Gobservable<TraceObserver> implements
		Runnable, QueueObserver {

	/**
	 * The computation manager is used to invoke the routing algorithms.
	 */
	private Server server;

	/**
	 * The vehicle to be used for routing.
	 */
	private Vehicle vehicle;

	/**
	 * The start vertex.
	 */
	private Vertex start;

	/**
	 * The destination vertex.
	 */
	private Vertex destination;

	/**
	 * Mapping queues to integers in order to provide a distinction for
	 * coloring.
	 */
	private Map<Object, Integer> queueLabeling = new HashMap<>();

	/**
	 * The constructor takes a computation manager used for invoking routing
	 * methods.
	 * 
	 * @param computationManager
	 *            A computation manager.
	 */
	public RoutingRunner(Server server) {
		this.server = server;
	}

	/**
	 * Set the start vertex.
	 * 
	 * @param start
	 *            Start vertex.
	 */
	public void setStart(Vertex start) {
		this.start = start;
	}

	/**
	 * Set the destination vertex.
	 * 
	 * @param destination
	 *            Destination vertex.
	 */
	public void setDestination(Vertex destination) {
		this.destination = destination;
	}

	/**
	 * Set the vehicle used for routing.
	 * 
	 * @param vehicle
	 *            A vehicle.
	 */
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	private IPartialPreorderQueueFactory<Vertex, Double, ? extends IPartialPreorderQueue<Vertex, Double>> redecorate(
			IPartialPreorderQueueFactory<Vertex, Double, ? extends IPartialPreorderQueue<Vertex, Double>> qf) {
		ObservedQueueFactory<Vertex, Double> x = new ObservedQueueFactory<Vertex, Double>(
				qf);
		x.addObserver(this);
		return x;
	}

	/**
	 * The run method invokes the routing algorithm and observes its behavior
	 * via aspects. If no appropriate aspect was found, a runtime exception is
	 * thrown, which can be caught using a
	 * <code>Thread.UncaughtExceptionHandler</code>.
	 */
	public void run() {
		TraceEvent event;
		queueLabeling.clear();

		IPartialPreorderQueueFactory<Vertex, Double, ? extends IPartialPreorderQueue<Vertex, Double>> qf = (IPartialPreorderQueueFactory<Vertex, Double, ? extends IPartialPreorderQueue<Vertex, Double>>) new PartialPreorderTreeFactory<Vertex, Double>();

		qf = redecorate(qf);

		event = new SearchStartedEvent(start, destination, vehicle);

		Dijkstra d = new Dijkstra(server.graph, qf);

		for (TraceObserver observer : this)
			observer.traceEvent(event);
		IPath<Vertex> result = null;

		result = d.route(start, destination);

		if (result != null) {
			event = new PathFoundEvent(result);
			for (TraceObserver observer : this)
				observer.traceEvent(event);
		}

	}

	@Override
	public void failed(greennav.routing.queue.IPartialPreorderQueue<?, ?> queue) {

	}

	public void insert(Object queue, Object key, Object value) {
		if (key instanceof Vertex) {
			Integer i = queueLabeling.get(queue);
			if (i == null) {
				i = queueLabeling.size();
				queueLabeling.put(queue, queueLabeling.size());
			}
			VertexEnqueuedEvent event = new VertexEnqueuedEvent((Vertex) key, i);
			for (TraceObserver observer : this)
				observer.traceEvent(event);
		} else {
			throw new RuntimeException(
					"Inserted something different than an ENGVertex.");
		}
	}

	/**
	 * Every time a vertex is pulled from the queue, information is extracted
	 * and sent via a <code>VertexVisitedEvent</code>.
	 */
	public void pull(Object queue, Object key, Object value) {
		if (key instanceof Vertex) {
			int v = 1;
			int size = 0;
			if (queue instanceof IPartialPreorderQueue<?, ?>)
				size = ((IPartialPreorderQueue<?, ?>) queue).getSize();
			else if (queue instanceof IQueue<?>)
				size = ((IQueue<?>) queue).size();
			VertexVisitedEvent event = new VertexVisitedEvent((Vertex) key,
					size, v);
			for (TraceObserver observer : this)
				observer.traceEvent(event);
		} else {
			throw new RuntimeException(
					"Pulled something different than an ENGVertex.");
		}
	}

	@Override
	public void front(Object arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}

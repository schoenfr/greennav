package greennav.visualization.model;

import greennav.model.computations.ComputationException;
import greennav.model.computations.ComputationManager;
import greennav.model.computations.instances.RoutingInstance;
import greennav.model.computations.interfaces.HasPartialPreorderQueue;
import greennav.model.computations.interfaces.Problem;
import greennav.model.computations.interfaces.RoutingAlgorithm;
import greennav.model.data.structs.ENGEdge;
import greennav.model.data.structs.ENGVertex;
import greennav.model.data.structs.Vehicle;
import greennav.model.datastructures.partialpreorderqueue.IPartialPreorderQueue;
import greennav.model.datastructures.partialpreorderqueue.IPartialPreorderQueueFactory;
import greennav.model.datastructures.partialpreorderqueue.ObservedQueueFactory;
import greennav.model.datastructures.partialpreorderqueue.QueueObserver;
import greennav.model.modelling.graph.IPath;
import greennav.model.modelling.order.IQueue;
import greennav.model.statebased.algorithms.ProfileContractionHierarchies.CHVertex;
import greennav.model.statebased.energy.EnergyProfile;
import greennav.model.toolbox.Gobservable;
import greennav.visualization.data.TraceEvent;
import greennav.visualization.data.TraceEvent.ExceptionThrownEvent;
import greennav.visualization.data.TraceEvent.PathFoundEvent;
import greennav.visualization.data.TraceEvent.SearchStartedEvent;
import greennav.visualization.data.TraceEvent.VertexEnqueuedEvent;
import greennav.visualization.data.TraceEvent.VertexVisitedEvent;
import greennav.visualization.data.TraceObserver;

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
	private ComputationManager computationManager;

	/**
	 * The identifier of the problem to be solved.
	 */
	private Problem problem;

	/**
	 * The identifier of the preferred algorithm to be solved.
	 */
	private String algorithm;

	/**
	 * The vehicle to be used for routing.
	 */
	private Vehicle vehicle;

	/**
	 * The start vertex.
	 */
	private ENGVertex start;

	/**
	 * The destination vertex.
	 */
	private ENGVertex destination;

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
	public RoutingRunner(ComputationManager computationManager) {
		this.computationManager = computationManager;
	}

	/**
	 * Set the start vertex.
	 * 
	 * @param start
	 *            Start vertex.
	 */
	public void setStart(ENGVertex start) {
		this.start = start;
	}

	/**
	 * Set the destination vertex.
	 * 
	 * @param destination
	 *            Destination vertex.
	 */
	public void setDestination(ENGVertex destination) {
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

	/**
	 * Set the identifier of the problem to be solved.
	 * 
	 * @param problem
	 *            The problem identifier.
	 */
	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	/**
	 * Set the identifier of the preferred algorithm.
	 * 
	 * @param algorithm
	 *            The identifier of the algorithm.
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@SuppressWarnings("unchecked")
	private void redecorate(
			HasPartialPreorderQueue<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>, IPartialPreorderQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>, IPartialPreorderQueue<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>>> ppq) {
		IPartialPreorderQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>, ? extends IPartialPreorderQueue<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>> queueFactory = ppq
				.getQueueFactory();
		ObservedQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>> x = null;
		if (!(queueFactory instanceof ObservedQueueFactory)) {
			x = new ObservedQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>(
					queueFactory);
		} else {
			x = new ObservedQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>(
					((ObservedQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>) queueFactory)
							.getBase());
		}
		ppq.setQueueFactory(x);
		x.addObserver(this);
	}

	private void unregister(
			HasPartialPreorderQueue<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>, IPartialPreorderQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>, IPartialPreorderQueue<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>>> ppq) {
		IPartialPreorderQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>, ? extends IPartialPreorderQueue<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>> queueFactory = ppq
				.getQueueFactory();
		ObservedQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>> x = null;
		if (queueFactory instanceof ObservedQueueFactory) {
			((ObservedQueueFactory<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>) queueFactory)
					.deleteObserver(this);
		}
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
		RoutingAlgorithm alg = null;
		try {
			alg = computationManager.getAlgorithm(RoutingAlgorithm.class,
					algorithm, problem);
		} catch (ComputationException exc) {
			event = new ExceptionThrownEvent(exc);
			for (TraceObserver observer : this)
				observer.traceEvent(event);
		}

		if (alg instanceof HasPartialPreorderQueue) {
			// redecorate((HasPartialPreorderQueue<ENGVertex,
			// EnergyProfile<ENGVertex, ENGEdge>>) alg);
		}

		event = new SearchStartedEvent(start, destination, vehicle);
		for (TraceObserver observer : this)
			observer.traceEvent(event);
		RoutingInstance instance = new RoutingInstance(vehicle, vehicle
				.getType().getCapacity(), start, destination);
		IPath<ENGVertex> result = null;
		try {
			result = computationManager.getAlgorithm(RoutingAlgorithm.class,
					algorithm, problem).route(instance);
		} catch (ComputationException exc) {
			event = new ExceptionThrownEvent(exc);
			for (TraceObserver observer : this)
				observer.traceEvent(event);
		}
		if (result != null) {
			event = new PathFoundEvent(result);
			for (TraceObserver observer : this)
				observer.traceEvent(event);
		}

		if (alg instanceof HasPartialPreorderQueue) {
			// unregister((HasPartialPreorderQueue<ENGVertex,
			// EnergyProfile<ENGVertex, ENGEdge>>) alg);
		}
	}

	public void front(Object queue, Object front) {
		if (front instanceof EnergyProfile<?, ?>) {
			System.out.println(((EnergyProfile) front).getEnergyFunction()
					.getFunction().getSize());
		}
		// TODO: implement
	}

	public void insert(Object queue, Object key, Object value) {
		if (key instanceof CHVertex) {
			key = ((CHVertex<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>) key).base;
		}
		if (key instanceof ENGVertex) {
			Integer i = queueLabeling.get(queue);
			if (i == null) {
				i = queueLabeling.size();
				queueLabeling.put(queue, queueLabeling.size());
			}
			VertexEnqueuedEvent event = new VertexEnqueuedEvent(
					(ENGVertex) key, i);
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
		if (key instanceof CHVertex) {
			key = ((CHVertex<ENGVertex, EnergyProfile<ENGVertex, ENGEdge>>) key).base;
		}
		if (key instanceof ENGVertex) {
			int v = 1;
			if (value instanceof EnergyProfile<?, ?>) {
				EnergyProfile<ENGVertex, ENGEdge> profile = (EnergyProfile<ENGVertex, ENGEdge>) value;
				v = profile.getEnergyFunction().getFunction().getSize();
			}

			int size = 0;
			if (queue instanceof IPartialPreorderQueue<?, ?>)
				size = ((IPartialPreorderQueue<?, ?>) queue).getSize();
			else if (queue instanceof IQueue<?>)
				size = ((IQueue<?>) queue).size();
			VertexVisitedEvent event = new VertexVisitedEvent((ENGVertex) key,
					size, v);
			for (TraceObserver observer : this)
				observer.traceEvent(event);
		} else {
			throw new RuntimeException(
					"Pulled something different than an ENGVertex.");
		}
	}

	@Override
	public void failed(IPartialPreorderQueue<?, ?> queue) {
		TraceEvent event = new ExceptionThrownEvent(new Exception(
				"something happened with the algorithm"));
		for (TraceObserver observer : this)
			observer.traceEvent(event);
	}

}

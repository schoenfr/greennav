package greennav.visualization.model;

import greennav.routing.data.Graph.Vertex;
import greennav.routing.data.path.IPath;
import greennav.routing.server.Server;
import greennav.visualization.data.TraceEvent;
import greennav.visualization.data.TraceEvent.PathFoundEvent;
import greennav.visualization.data.TraceEvent.VertexEnqueuedEvent;
import greennav.visualization.data.TraceEvent.VertexVisitedEvent;
import greennav.visualization.data.TraceObserver;
import greennav.visualization.util.Gobservable;
import greennav.visualization.view.IView;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;

/**
 * The model of the visualization tool. It used a routing runner to invoke and
 * observe algorithms. The stream of observed information is handled using
 * callbacks. The graphical user interface can then observe this model to
 * perform painting operations.
 */
public class Model extends Gobservable<IView> implements TraceObserver,
		UncaughtExceptionHandler {

	/**
	 * The state of the model.
	 */
	public static enum State {
		/**
		 * The model is currently loading the computation manager (and other
		 * stuff).
		 */
		LOADING,

		/**
		 * The preparation state represents the input form letting the user
		 * decide the parameters.
		 */
		PREPARATION,

		/**
		 * The algorithm is running and the user can observe its behavior.
		 */
		OBSERVATION;
	}

	/**
	 * The current state of the visualization procedure.
	 */
	private State state = State.LOADING;

	/**
	 * The underlying computation manager used to access the algorithms.
	 */
	private Server server;

	/**
	 * Auto value is true, if automatic steps each second are activated.
	 */
	private boolean auto = false;

	/**
	 * Counter for steps, how many markers should be painted.
	 */
	private int stepCount = 0;

	/**
	 * Number of steps to go for each click.
	 */
	private int stepSize = 1;

	/**
	 * The current size of the search queue.
	 */
	private int remainingQueueSize = 0;

	/**
	 * The maximum size of the search queue.
	 */
	private int maxQueueSize = 0;

	/**
	 * The start vertex.
	 */
	private Vertex start;

	/**
	 * The destination vertex.
	 */
	private Vertex destination;

	/**
	 * The list of trace events recognized by the routing runner.
	 */
	private List<TraceEvent> trace = new LinkedList<>();

	/**
	 * The path found by the algorithm.
	 */
	private IPath<Vertex> path;

	/**
	 * The routing runner used to invoke and observe algorithms.
	 */
	private RoutingRunner routingRunner;

	/**
	 * The thread used to run the routing runner.
	 */
	Thread thread;

	private Gobservable<TraceObserver> traceObservers = new Gobservable<>();

	public Gobservable<TraceObserver> getTraceObservers() {
		return traceObservers;
	}

	@Override
	public void traceEvent(final TraceEvent event) {
		trace.add(event);
		if (event instanceof VertexVisitedEvent) {
			VertexVisitedEvent e = (VertexVisitedEvent) event;
			remainingQueueSize = e.getRemainingQueueSize();
			maxQueueSize = Math.max(maxQueueSize, remainingQueueSize);
		} else if (event instanceof VertexEnqueuedEvent) {
			// ...
		} else if (event instanceof PathFoundEvent) {
			PathFoundEvent e = (PathFoundEvent) event;
			path = e.getPath();
		}
		// Forward the event to the views
		for (IView view : this)
			view.traceEvent(event);
		for (TraceObserver o : traceObservers)
			o.traceEvent(event);
	}

	/**
	 * Setting the start vertex causes a message and an update for each view.
	 * 
	 * @param start
	 *            The start vertex.
	 */
	public void setStart(Vertex start) {
		this.start = start;
		for (IView view : this) {
			view.message("Chosen " + start.getID() + " (at " + start.getLat()
					+ "," + start.getLon() + ") as starting vertex.");
			view.update();
		}
	}

	/**
	 * Setting the destination vertex causes a message and an update for each
	 * view.
	 * 
	 * @param destination
	 *            The destination vertex.
	 */
	public void setDestination(Vertex destination) {
		this.destination = destination;
		for (IView view : this) {
			view.message("Chosen " + destination.getID() + " (at "
					+ destination.getLat() + "," + destination.getLon()
					+ ") as destination vertex.");
			view.update();
		}
	}

	/**
	 * Tries to interrupt the algorithm thread and waits for it to stop. After
	 * the thread was stopped, the state switches back to preparation.
	 */
	public void stopRouting() {
		if (thread != null && state == State.OBSERVATION) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException exc) {
				for (IView view : this)
					view.message("Could not wait for thread to stop.");
				return;
			}
			state = State.PREPARATION;
			for (IView view : this) {
				view.message("Stopped visualization.");
				view.update();
			}
		}
	}

	public void setAlgorithm(String algorithm) {
		if (state != State.PREPARATION)
			return;
		for (IView view : this)
			view.algorithmSet(algorithm);
	}

	/**
	 * On receiving an uncaught exception (coming from the algorithm thread), a
	 * message is sent to all views.
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		for (IView view : this)
			view.message("Exception throwsn: "
					+ e.getMessage()
					+ " Maybe the algorithm is not supported in visualization (maybe check the GreenNav-Redmine for Trouble-Shooting)");
	}

	/**
	 * Start the routing set with given parameters and switch to observation
	 * state.
	 */
	public void startRouting() {
		if (state != State.PREPARATION)
			return;
		state = State.OBSERVATION;
		for (IView view : this) {
			view.message("Starting visualization now.");
			view.update();
		}
		// routingRunner.setVehicle(new Vehicle(getComputationManager()
		// .getDataManager().getVehicleTypeList()
		// .getVehicleTypeByName("Smart Roadster"), 0));
		routingRunner.setStart(start);
		routingRunner.setDestination(destination);
		thread = new Thread(routingRunner);
		thread.setUncaughtExceptionHandler(this);
		thread.start();
	}

	/**
	 * Connects the model with a computation manager used for invoking routing
	 * algorithms.
	 * 
	 * @param server
	 *            A computation manager.
	 */
	public void setServer(Server server) {
		if (routingRunner != null)
			routingRunner.removeObserver(this);
		this.server = server;
		routingRunner = new RoutingRunner(server);
		routingRunner.addObserver(this);
		if (state == State.LOADING && server != null) {
			state = State.PREPARATION;
			for (IView view : this) {
				view.message("Finished loading");
				view.managerSet(server);
			}
		}
	}

	public int getQueueSize() {
		return remainingQueueSize;
	}

	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}

	public int getStepSize() {
		return stepSize;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	public Vertex getStart() {
		return start;
	}

	public Vertex getDestination() {
		return destination;
	}

	public State getState() {
		return state;
	}

	public IPath<Vertex> getPath() {
		return path;
	}

	public Server getServer() {
		return server;
	}

}

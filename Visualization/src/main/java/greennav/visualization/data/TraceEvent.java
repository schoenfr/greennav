package greennav.visualization.data;

import greennav.model.data.structs.ENGVertex;
import greennav.model.data.structs.Vehicle;
import greennav.model.modelling.graph.IPath;

import java.io.Serializable;

/**
 * This abstract event is sent by the aspects in order to be processed by the
 * view, which decides how to visualize these events.
 */
public class TraceEvent implements Serializable {

	/**
	 * A serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This event is sent whenever a new search is initialized.
	 */
	public static class SearchStartedEvent extends TraceEvent {

		/**
		 * A serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The start vertex of the current search.
		 */
		private final ENGVertex start;

		/**
		 * The destination vertex of the current search.
		 */
		private final ENGVertex destination;

		/**
		 * The vehicle to be used for routing.
		 */
		private final Vehicle vehicle;

		/**
		 * The constructor takes its attribute values.
		 * 
		 * @param start
		 *            The start vertex.
		 * @param destination
		 *            The destination vertex.
		 * @param vehicle
		 *            The vehicle.
		 */
		public SearchStartedEvent(ENGVertex start, ENGVertex destination,
				Vehicle vehicle) {
			this.start = start;
			this.destination = destination;
			this.vehicle = vehicle;
		}

		/**
		 * Get the start vertex.
		 * 
		 * @return Start vertex.
		 */
		public ENGVertex getStart() {
			return start;
		}

		/**
		 * Get the destination vertex.
		 * 
		 * @return Destination vertex.
		 */
		public ENGVertex getDestination() {
			return destination;
		}

		/**
		 * Get the vehicle.
		 * 
		 * @return Vehicle.
		 */
		public Vehicle getVehicle() {
			return vehicle;
		}

	}

	/**
	 * This event is sent whenever a vertex is visited by the algorithm.
	 */
	public static class VertexVisitedEvent extends TraceEvent {

		/**
		 * A serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The visited vertex.
		 */
		private final ENGVertex vertex;

		/**
		 * The remaining queue size.
		 */
		private final int remainingQueueSize;

		/**
		 * The size of the current profile associated with the vertex.
		 */
		private final int profileSize;

		/**
		 * The constructor takes its attribute values.
		 * 
		 * @param vertex
		 *            The visited vertex.
		 * @param remainingQueueSize
		 *            The remaining size of the queue.
		 * @param profileSize
		 *            The size of the current profile.
		 */
		public VertexVisitedEvent(ENGVertex vertex, int remainingQueueSize,
				int profileSize) {
			this.vertex = vertex;
			this.remainingQueueSize = remainingQueueSize;
			this.profileSize = profileSize;
		}

		/**
		 * Get the visited vertex.
		 * 
		 * @return The visited vertex.
		 */
		public ENGVertex getVertex() {
			return vertex;
		}

		/**
		 * Get the remaining queue size.
		 * 
		 * @return The remaining queue size.
		 */
		public int getRemainingQueueSize() {
			return remainingQueueSize;
		}

		/**
		 * Get the profile size.
		 * 
		 * @return The profile size.
		 */
		public int getProfileSize() {
			return profileSize;
		}
	}

	/**
	 * This event is sent whenever a vertex is enqueued by the algorithm.
	 */
	public static class VertexEnqueuedEvent extends TraceEvent {

		/**
		 * A serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The enqueued vertex.
		 */
		private final ENGVertex vertex;

		/**
		 * The label of the queue, the vertex is inserted into.
		 */
		private final int queue;

		/**
		 * The constructor takes its attribute values.
		 * 
		 * @param vertex
		 *            The vertex.
		 * @param queue
		 *            The queue.
		 */
		public VertexEnqueuedEvent(ENGVertex vertex, int queue) {
			this.vertex = vertex;
			this.queue = queue;
		}

		/**
		 * Get the enqueued vertex.
		 * 
		 * @return The enqueued vertex.
		 */
		public ENGVertex getVertex() {
			return vertex;
		}

		/**
		 * Get the label of the queue.
		 * 
		 * @return The label of the queue.
		 */
		public int getQueue() {
			return queue;
		}
	}

	/**
	 * This event is sent whenever the algorithm terminates. If the algorithm
	 * fails to find a solution, the path will be null, but the event is sent
	 * anyway.
	 */
	public static class PathFoundEvent extends TraceEvent {

		/**
		 * A serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The path is the result of the algorithm.
		 */
		private final IPath<ENGVertex> path;

		/**
		 * The constructor takes the result of the routing algorithm.
		 * 
		 * @param path
		 *            The path found by the algorithm.
		 */
		public PathFoundEvent(IPath<ENGVertex> path) {
			this.path = path;
		}

		/**
		 * Get the path.
		 * 
		 * @return The path.
		 */
		public IPath<ENGVertex> getPath() {
			return path;
		}
	}

	/**
	 * This event is sent whenever the algorithm throws an exception.
	 */
	public static class ExceptionThrownEvent extends TraceEvent {

		/**
		 * A serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The exception thrown by the algorithm.
		 */
		private final Exception cause;

		/**
		 * The constructor takes the exception thrown by the algorithm.
		 * 
		 * @param cause
		 *            The thrown exception.
		 */
		public ExceptionThrownEvent(Exception cause) {
			this.cause = cause;
		}

		/**
		 * Get the exception.
		 * 
		 * @return The exception.
		 */
		public Exception getCause() {
			return cause;
		}
	}

}

package greennav.visualization.data;

/**
 * The routing runner produces trace events, which can be observed by classes
 * implementing this interface.
 */
public interface TraceObserver {

	/**
	 * This method is called whenever the routing runner recognizes an
	 * interesting action within the execution of a routing algorithm.
	 * 
	 * @param event
	 *            A particular trace event produced by the routing runner.
	 */
	public void traceEvent(TraceEvent event);

}

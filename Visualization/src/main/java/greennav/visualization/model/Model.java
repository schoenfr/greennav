package greennav.visualization.model;

import greennav.model.computations.ComputationException;
import greennav.model.computations.ComputationManager;
import greennav.model.computations.interfaces.Problem;
import greennav.model.data.IDataManager;
import greennav.model.data.IGraphManager;
import greennav.model.data.structs.ENGVertex;
import greennav.model.data.structs.Vehicle;
import greennav.model.modelling.graph.IPath;
import greennav.model.toolbox.Gobservable;
import greennav.visualization.data.TraceEvent;
import greennav.visualization.data.TraceEvent.PathFoundEvent;
import greennav.visualization.data.TraceEvent.VertexEnqueuedEvent;
import greennav.visualization.data.TraceEvent.VertexVisitedEvent;
import greennav.visualization.data.TraceObserver;
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
	private ComputationManager computationManager;

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
	private ENGVertex start;

	/**
	 * The destination vertex.
	 */
	private ENGVertex destination;

	/**
	 * The list of trace events recognized by the routing runner.
	 */
	private List<TraceEvent> trace = new LinkedList<>();

	/**
	 * The path found by the algorithm.
	 */
	private IPath<ENGVertex> path;

	/**
	 * The routing runner used to invoke and observe algorithms.
	 */
	private RoutingRunner routingRunner;

	/**
	 * The thread used to run the routing runner.
	 */
	Thread thread;

	/**
	 * The problem to be solved.
	 */
	private Problem problem;

	/**
	 * The identifier of the algorithm to be invoked by the routing runner.
	 */
	private String algorithm;

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
	public void setStart(ENGVertex start) {
		this.start = start;
		for (IView view : this) {
			view.message("Chosen " + start.getGreenID() / 100 + " (at "
					+ start.getCoordinate().getLatLon()
					+ ") as starting vertex.");
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
	public void setDestination(ENGVertex destination) {
		this.destination = destination;
		for (IView view : this) {
			view.message("Chosen " + destination.getGreenID() / 100 + " (at "
					+ destination.getCoordinate().getLatLon()
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

	public void setProblem(String problem) {
		if (state != State.PREPARATION)
			return;
		try {
			this.problem = computationManager
					.getProblem(Problem.class, problem);
			for (IView view : this)
				view.problemSet(this.problem);
		} catch (ComputationException e) {
			e.printStackTrace();
		}
	}

	public void setAlgorithm(String algorithm) {
		if (state != State.PREPARATION)
			return;
		this.algorithm = algorithm;
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
		routingRunner.setProblem(problem);
		routingRunner.setAlgorithm(algorithm);
		routingRunner.setVehicle(new Vehicle(getComputationManager()
				.getDataManager().getVehicleTypeList()
				.getVehicleTypeByName("Smart Roadster"), 0));
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
	 * @param computationManager
	 *            A computation manager.
	 */
	public void setComputationManager(ComputationManager computationManager) {
		if (routingRunner != null)
			routingRunner.removeObserver(this);
		this.computationManager = computationManager;
		routingRunner = new RoutingRunner(computationManager);
		routingRunner.addObserver(this);
		if (state == State.LOADING && computationManager != null) {
			state = State.PREPARATION;
			for (IView view : this) {
				view.message("Finished loading");
				view.managerSet(computationManager);
			}
		}
	}

	public int getQueueSize() {
		return remainingQueueSize;
	}

	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	public ComputationManager getComputationManager() {
		return computationManager;
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

	public ENGVertex getStart() {
		return start;
	}

	public ENGVertex getDestination() {
		return destination;
	}

	public IGraphManager getGraphManager() {
		return computationManager.getDataManager().getGraphManager();
	}

	public IDataManager getDataManager() {
		return computationManager.getDataManager();
	}

	public State getState() {
		return state;
	}

	public IPath<ENGVertex> getPath() {
		return path;
	}

}

// gui.getMapViewer().getMercator().setZoom(6, new XY(0, 0));
// gui.getMapViewer()
// .getMercator()
// .moveTo(gui
// .getMapViewer()
// .getMercator()
// .latlonToLocalPoint(
// new LatLon(51, 9, AngleUnit.DEG))
// .sub(new XY(375, 350)));
//
// LatLon startZoom = dataVisualizationManager.getStartNode()
// .getLatLon();
// gui.getMapViewer().setZoom(10,
// gui.getMapViewer().getMercator().latlonToPoint(startZoom));
// gui.getMapViewer().setZoom(11,
// gui.getMapViewer().getMercator().latlonToPoint(startZoom));
// // viewStartDest();
//
//
//
// } else {
/* STOP */

/* remove all painted MapShapes */
// removeMapShapes(stepCount);
// /* set the labeltext to 0 */
// String s = "Current step: " + stepCount;
// stepLabel.setText(s);
// /* hide start and dest */
// if (startDestCheckBox.isSelected()) {
// hideStartDest();
// startDestCheckBox.setSelected(false);
// }
// /* hide backgroundmarker */
// if (heightInfoCheckBox.isSelected()) {
// showHeightMarker(false);
// heightInfoCheckBox.setSelected(false);
// }
/* Test boundaries to disable next/prev button */
// testBoundaries();
/**
 * Stores the lines of the final route stored in independentRouteMapShapes.
 */
// private LatLon startLatLon = null;
// private LatLon destLatLon = null;

// private Serializer serializer = new Persister();

// private void loadDataVisualizationManager() {
// LinkedList<NodeInfo> tmp = new LinkedList<NodeInfo>();
// tmp.addAll(vd.getForwardFlowList());
// tmp.addAll(vd.getBackwardFlowList());
// tmp.addAll(vd.getBidirectionalFlowList());
// tmp.addAll(vd.getFlowList());
//
// addHeightShapes(createHeightMarker(tmp));
// addMapShapes(createMarker(tmp));
// addRouteShapes(createRouteLines(vd.getFinalRoute()));
//
// if (startNode != null && destLatLon != null) {
// addStartDest(new StartMarker(startNode, Color.WHITE),
// new DestMarker(destLatLon, Color.BLACK));
// }
// }

// public void setData(String filename) {
// File xmlDatei = new File(filename);
// try {
// vd = serializer.read(VisualizationData.class, xmlDatei);
// } catch (Exception e) {
// e.printStackTrace();
// }
// this.destLatLon = vd.getDest();
// this.startNode = vd.getStart();
// }

// public void setRoute(List<ENGVertex> route) {

// if (this.vd == null) {
// this.vd = new VisualizationData();
// }
//
// LatLon ll = null;
// for (ENGVertex e : route) {
// ll = getCoordinatesByENGVertexID(e);
// vd.addFinalRouteNode(ll);
// }

// }

// /**
// * Creates MapShapes for all Nodes with coloring in respect to the height
// of
// * the node
// *
// * @param nodeList
// * List with node information
// * @return List with MapShapes, colored according to height
// */
// private MapShapeList<MapShape> createHeightMarker(
// LinkedList<NodeInfo> nodeList) {
//
// MapShapeList<MapShape> heightMarkerList = new MapShapeList<MapShape>();
//
// SRTMReader heightInfo = new SRTMReader(new File("."));
// double minHeight = Double.MAX_VALUE;
// double maxHeight = Double.MIN_VALUE;
// // get minimum and maximum value for the height
// LatLon tmpLatLon = new LatLon(0, 0, AngleUnit.DEG);
// double tmpHeight = 0;
// for (NodeInfo node : nodeList) {
// tmpLatLon = getCoordinatesByVertexID(node.getNodeId());
// try {
// tmpHeight = heightInfo.getLinearInterpolation(tmpLatLon)
// .getHeightAboveZero().in(LengthUnit.M);
// } catch (IOException e) {
// System.err.print("Could not get height Information");
// e.printStackTrace();
// }
// if (tmpHeight < minHeight)
// minHeight = tmpHeight;
// if (tmpHeight > maxHeight)
// maxHeight = tmpHeight;
// }
// Colormaker color = new Colormaker(minHeight, maxHeight);
//
// // boolean if the node is in the final route
// for (NodeInfo node : nodeList) {
// tmpLatLon = getCoordinatesByVertexID(node.getNodeId());
// try {
// tmpHeight = heightInfo.getLinearInterpolation(tmpLatLon)
// .getHeightAboveZero().in(LengthUnit.M);
// } catch (IOException e) {
// System.err.print("Could not get height Information");
// e.printStackTrace();
// }
//
// BackgroundMarker marker = new BackgroundMarker(tmpLatLon,
// color.getColor(tmpHeight));
// heightMarkerList.add(marker);
// }
//
// heightMarkerList.setLayer(2);
//
// return heightMarkerList;
// }

// /**
// * Creates MapShapes for all nodes which were observed by algorithm. Used
// * nodes are green, unused are red
// *
// *
// * @param l
// * List with node informations
// *
// * @return List with MapShapes
// */
// private CursoredMarkerList createMarker(LinkedList<NodeInfo> l) {
// CursoredMarkerList ms = new CursoredMarkerList();
// int[] inout = countElements(l);
// int in = inout[0];
// int out = inout[1];
// ColorControl c2 = new ColorControl(new Color(255, 255, 0), new Color(
// 255, 0, 0), out);
// ColorControl c1 = new ColorControl(new Color(0x90, 0xee, 0x90),
// new Color(0x6b, 0xc9, 0x6b), in);
// // boolean if the node is in the final route
// boolean nodeInFinalRoute = false;
// final Color color = Color.MAGENTA;
// for (NodeInfo node : l) {
// nodeInFinalRoute = false;
// LatLon ll = getCoordinatesByVertexID(node.getNodeId());
// // Check if node is in final route
// // for (SerializableLatLon routeLL : vd.getFinalRoute()) {
// // nodeInFinalRoute |= routeLL.equalsTo(ll);
// // }
// // get number of profiles
// int pz = node.getOptionalValue();
// if (pz == 0) {
// pz = 1;
// }
// Marker marker = new Marker(ll,
// (node.getInOut() ? c1 : c2).getNextColor(), pz);
// if (nodeInFinalRoute) {
// referencedRouteMarkers.put(marker, marker.getColor());
// Marker routeMarker = new Marker(ll, color);
// independentRouteMapShapes.add(routeMarker);
// }
// ms.add(marker);
// }
//
// return ms;
// }

// /**
// * Calculates the connection Lines between the given Points in the
// * finalRoute List
// *
// * @param finalRoute
// * List with SerializableLatLon as positions of the final route
// * on the map
// * @return a AllMarker List with BigLines as line object for the map
// */
// private MapShapeList<MapShape> createRouteLines(
// LinkedList<SerializableLatLon> finalRoute) {
// MapShapeList<MapShape> independentRouteLineShapes = new
// MapShapeList<MapShape>();
// LatLon lastMarker = null;
// LatLon currentMarker = null;
// Color color = Color.MAGENTA;
// for (SerializableLatLon s : finalRoute) {
// currentMarker = new LatLon(s.getLatitude(), s.getLongitude(),
// AngleUnit.DEG);
// if (lastMarker != null) {
// BigLine line = new BigLine(lastMarker, currentMarker, color);
// independentRouteLineShapes.add(line);
// }
// lastMarker = currentMarker;
// }
// independentRouteLineShapes.setLayer(-1);
// return independentRouteLineShapes;
// }

// /**
// * Locates geographical coordinates of a node by its ID
// *
// *
// * @param id
// * ID of node
// * @return Coordinates
// */
// private LatLon getCoordinatesByVertexID(int id) {
// return dataManager.getGraphManager().getEnergyGraph().getVertices()
// .get(id).getCoordinate().getLatLon();
// }

/**
 * This method increments or decrements the step counter and modifies the
 * labeltext. The counter can't be smaller than 0.
 * 
 * @param b
 *            increment (true) or decrement (false)
 * @param i
 *            The value to be incremented or decremented
 */
// private void setStepCounter(boolean b, int i) {
// String s = "";
// if (start) {
// if (b) {
// /* increase */
// dataVisualizationManager.setStepCount(dataVisualizationManager
// .getStepCount() + i);
// s = "Current step: " + dataVisualizationManager.getStepCount();
// stepLabel.setText(s);
// } else if (dataVisualizationManager.getStepCount() > 0) {
// /* decrease */
// dataVisualizationManager.setStepCount(dataVisualizationManager
// .getStepCount() - i);
// s = "Current step: " + dataVisualizationManager.getStepCount();
// stepLabel.setText(s);
// }
// }
// }

/**
 * Hides marker for start and dest.
 */
//
// @Override
// public void addMapShapes(AllMarker l) {
// setStatus("added MapShapes");
//
// /* save the MapShapes globally */
// this.shapes = l;
// mapViewer.addMapShape(l);
//
// getStartButton().setEnabled(true);
// loadXMLButton.setEnabled(true);
//
// /* modify the slider */
// modifySlider();
// }


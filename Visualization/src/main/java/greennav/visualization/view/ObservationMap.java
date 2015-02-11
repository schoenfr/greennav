package greennav.visualization.view;

import greennav.mapviewer.JMapViewer;
import greennav.mapviewer.shapes.DestinationMarker;
import greennav.mapviewer.shapes.MapShape;
import greennav.mapviewer.shapes.MapShapeList;
import greennav.mapviewer.shapes.Marker;
import greennav.mapviewer.shapes.StartMarker;
import greennav.model.data.structs.ENGVertex;
import greennav.visualization.data.TraceEvent;
import greennav.visualization.data.TraceEvent.PathFoundEvent;
import greennav.visualization.data.TraceEvent.SearchStartedEvent;
import greennav.visualization.data.TraceEvent.VertexEnqueuedEvent;
import greennav.visualization.data.TraceEvent.VertexVisitedEvent;
import greennav.visualization.data.TraceObserver;
import greennav.visualization.view.shapes.CursoredMarkerList;
import greennav.visualization.view.shapes.VertexMarker;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

public class ObservationMap extends JMapViewer implements TraceObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private View parent;

	/**
	 * List for the MapShapes (Marker).
	 */
	private CursoredMarkerList shapes = new CursoredMarkerList();

	/**
	 * List for the queue (Marker).
	 */
	private MapShapeList<Marker> queue = new MapShapeList<>();

	/**
	 * List for the path (Marker).
	 */
	private MapShapeList<Marker> path = new MapShapeList<>();

	/**
	 * Marker for the start node.
	 */
	private StartMarker startNode;

	/**
	 * Marker for the destination node.
	 */
	private DestinationMarker destinationNode;

	/**
	 * List for route connection lines.
	 */
	private MapShapeList<MapShape> routeLineShapes = new MapShapeList<>();

	/**
	 * List for height information nodes.
	 */
	private MapShapeList<MapShape> heightShapes;

	/**
	 * Stores marker for the route and it's current color for route
	 * visualization
	 */
	private Map<Marker, Color> referencedRouteMarkers = new HashMap<Marker, Color>();

	/**
	 * Stores independent map shapes for visualization of the whole route at
	 * every time
	 */
	private List<MapShape> independentRouteMapShapes = new ArrayList<MapShape>();

	HashMap<ENGVertex, Marker> queueMirror = new HashMap<>();

	private static final Color[] colors = new Color[] { Color.BLUE,
			Color.GREEN, Color.CYAN, Color.MAGENTA };

	public ObservationMap(View parent) {
		this.parent = parent;

		getMapShapes().add(path);
		path.setLayer(4);
	}

	@Override
	public void traceEvent(final TraceEvent message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (message != null) {
					if (message instanceof SearchStartedEvent) {
						path.clear();
					} else if (message instanceof VertexVisitedEvent) {
						VertexVisitedEvent event = (VertexVisitedEvent) message;
						shapes.add(new VertexMarker(event.getVertex()
								.getCoordinate().getLatLon(), Color.BLACK, 2));
						shapes.cursorForward();
						queue.remove(queueMirror.remove(event.getVertex()));
					} else if (message instanceof VertexEnqueuedEvent) {
						VertexEnqueuedEvent event = (VertexEnqueuedEvent) message;
						Marker m = new Marker(event.getVertex().getCoordinate()
								.getLatLon(), colors[event.getQueue()]);
						if (!queueMirror.containsKey(event.getVertex())) {
							queueMirror.put(event.getVertex(), m);
							queue.add(m);
						}
					} else if (message instanceof PathFoundEvent) {
						PathFoundEvent event = (PathFoundEvent) message;
						if (event.getPath() != null) {
							routeLineShapes.clear();
							for (ENGVertex vertex : event.getPath()
									.toVertexList()) {
								Marker m = new Marker(vertex.getCoordinate()
										.getLatLon(), Color.BLACK);
								routeLineShapes.add(m);
							}
							showRoute(true);
						}
					}
				}
			}
		});
	}

	public void updateTraceMarkers() {
		if (!getMapShapes().contains(shapes)) {
			shapes.setLayer(1);
			getMapShapes().add(shapes);
		}
		if (!getMapShapes().contains(queue)) {
			queue.setLayer(0);
			getMapShapes().add(queue);
		}
	}

	public void showHeightMarker(boolean show) {
		if (show && !getMapShapes().contains(heightShapes)) {
			getMapShapes().add(heightShapes);
		} else if (!show) {
			getMapShapes().remove(heightShapes);
		}
		repaint();
	}

	public void showRoute(boolean show) {
		if (show && !getMapShapes().contains(routeLineShapes)) {
			getMapShapes().add(routeLineShapes);
		} else if (!show) {
			getMapShapes().remove(routeLineShapes);
		}
		repaint();
	}

	/**
	 * Displays marker for start and destination.
	 */
	public void checkEndMarker() {
		ENGVertex start = parent.getModel().getStart();
		ENGVertex destination = parent.getModel().getDestination();
		if (start == null) {
			// No start defined, so maybe delete the marker
			if (startNode != null) {
				getMapShapes().remove(startNode);
				startNode = null;
			}
		} else {
			// Start defined, so maybe add or update the marker
			if (startNode != null) {
				startNode.setStartMarker(start.getCoordinate().getLatLon(),
						Color.black);
			} else {
				startNode = new StartMarker(start.getCoordinate().getLatLon(),
						Color.black);
				getMapShapes().add(startNode);
			}
		}
		if (destination == null) {
			// No start defined, so maybe delete the marker
			if (destinationNode != null) {
				getMapShapes().remove(destinationNode);
				destinationNode = null;
			}
		} else {
			// Start defined, so maybe add or update the marker
			if (destinationNode != null) {
				destinationNode.setDestinationMarker(destination
						.getCoordinate().getLatLon(), Color.black);
			} else {
				destinationNode = new DestinationMarker(destination
						.getCoordinate().getLatLon(), Color.black);
				getMapShapes().add(destinationNode);
			}
		}
	}

	public MapShapeList<MapShape> getRouteLineShapes() {
		return routeLineShapes;
	}

	/**
	 * Adds the mapShapes for the height information.
	 * 
	 * @param heightMarkerList
	 *            list of height markers
	 */
	public void addHeightShapes(MapShapeList<MapShape> heightMarkerList) {
		this.heightShapes = heightMarkerList;
	}

	/**
	 * @return the referenced route markers
	 */
	public Map<Marker, Color> getReferencedRouteMarkers() {
		return referencedRouteMarkers;
	}

	/**
	 * @return the independent route map shapes
	 */
	public List<MapShape> getIndependentRouteMapShapes() {
		return independentRouteMapShapes;
	}

	public CursoredMarkerList getShapes() {
		return shapes;
	}

	public MapShapeList<Marker> getQueue() {
		return queue;
	}

	public HashMap<ENGVertex, Marker> getQueueMirror() {
		return queueMirror;
	}
}

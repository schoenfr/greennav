package greennav.routing.server;

import greennav.routing.algorithms.Dijkstra;
import greennav.routing.data.Graph;
import greennav.routing.data.Graph.Vertex;
import greennav.routing.data.OSMReader;
import greennav.routing.data.path.IPath;
import greennav.routing.data.vehicle.Vehicle;
import greennav.routing.data.vehicle.VehicleTypeList;
import greennav.routing.queue.PartialPreorderTreeFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class Server {

	public Graph graph;
	public VehicleTypeList vehicleList;

	public Server() throws Exception {
		graph = new OSMReader().readOSM("schleswig-holstein.osm.pbf");
		// TODO: Handle exception
		vehicleList = VehicleTypeList.init("vehicles.xml");
	}

	public String getStatus() {
		return "server is running";
	}

	public Collection<String> getAlgorithmList() {
		Collection<String> result = new HashSet<String>();
		result.add("dijkstra");
		return result;
	}

	public String getAlgorithm(String algorithm) {
		switch (algorithm) {
		case "dijkstra":
			return "Dijkstra's Algorithm";
		}
		return "algorithm not found";
	}

	public VehicleTypeList getVehicleTypeList() {
		return vehicleList;
	}

	public long vertex(double lat, double lon) {
		return graph.getVertexByLatLon(lat, lon).getID();
	}

	public RoutingResponse route(Vehicle v, double battery, long from, long to,
			String optimization, String algorithm, boolean turns) {
		Vertex x = graph.getVertexByID(from);
		Vertex y = graph.getVertexByID(to);
		if (x == null || y == null)
			return null;
		// TODO: Consider parameters

		IPath<Vertex> path = null;

		path = new Dijkstra(graph,
				new PartialPreorderTreeFactory<Vertex, Double>()).route(x, y);

		RoutingResponse rp = new RoutingResponse();

		rp.setRoute(createRoute(path.toVertexList()));

		return rp;
	}

	private RouteNode[] createRoute(List<Vertex> vertices) {
		RouteNode[] route = new RouteNode[vertices.size()];
		// Edge previousEdge = null;
		// String previousStreet = null;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex y = vertices.get(i);
			route[i] = new RouteNode();
			route[i].latitude = y.getLat();
			route[i].longitude = y.getLon();
			// if (i + 1 < vertices.size()) {
			// Vertex z = vertices.get(i + 1);
			// Edge e = model.dataManager.getGraphManager()
			// .getEnergyGraph().getEdge(y, z);
			// if (previousEdge != null) {
			// Vertex x = previousEdge.getFrom();
			// double angle = y.getCoordinate().getAngle(
			// x.getCoordinate(), z.getCoordinate());
			// double d = computeDirection(x, y, z);
			//
			// int sumUndirectedValency = y.getOutgoing().size();
			// for (Edge countingEdge : y.getIncoming()) {
			// boolean count = true;
			// for (Edge countingEdge2 : y.getOutgoing()) {
			// if (countingEdge.getFrom().equals(
			// countingEdge2.getTo()))
			// count = false;
			// }
			// if (count)
			// sumUndirectedValency++;
			// }
			// route[i].street = "..";
			// if (!previousEdge.isRoundabout() && e.isRoundabout()) {
			// route[i].turn = TurnCode.ENTER_ROUNDABOUT.getCode();
			// previousStreet = e.getName();
			// } else if (previousEdge.isRoundabout() && !e.isRoundabout()) {
			// route[i].turn = TurnCode.LEAVE_ROUNDABOUT.getCode();
			// previousStreet = e.getName();
			// } else if (previousEdge.isRoundabout() && e.isRoundabout()) {
			// // do nothing in particular
			// } else if (sumUndirectedValency > 2
			// && angle < 150.0 * Math.PI / 180.0) {
			// if (d > 0 && angle < 50.0 * Math.PI / 180.0)
			// route[i].turn = TurnCode.HARD_LEFT_TURN.getCode();
			// else if (d > 0 && angle < 120.0 * Math.PI / 180.0)
			// route[i].turn = TurnCode.LEFT_TURN.getCode();
			// else if (d > 0 && angle < 150.0 * Math.PI / 180.0)
			// route[i].turn = TurnCode.SLIGHT_LEFT_TURN.getCode();
			// else if (d <= 0 && angle < 50.0 * Math.PI / 180.0)
			// route[i].turn = TurnCode.HARD_RIGHT_TURN.getCode();
			// else if (d <= 0 && angle < 120.0 * Math.PI / 180.0)
			// route[i].turn = TurnCode.RIGHT_TURN.getCode();
			// else if (d <= 0 && angle < 150.0 * Math.PI / 180.0)
			// route[i].turn = TurnCode.SLIGHT_RIGHT_TURN
			// .getCode();
			// previousStreet = e.getName();
			// } else if ((previousStreet != null && !previousStreet
			// .equalsIgnoreCase(e.getName()))
			// && e.getName() != null
			// && !e.getName().isEmpty()
			// && !e.getName().equalsIgnoreCase("undefined")
			// && !e.getName().equalsIgnoreCase("null")) {
			// System.out.println(e.getName());
			// previousStreet = e.getName();
			// route[i].turn = TurnCode.STRAIGHT.getCode();
			// } else {
			// route[i].street = null;
			// }
			// }
			// previousEdge = e;
			// }
		}
		return route;
	}

	public Object range(Vehicle v, double battery, long from) {
		// TODO Auto-generated method stub
		return null;
	}

}

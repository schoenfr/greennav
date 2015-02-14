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

import org.springframework.stereotype.Service;

@Service
public class Server {

	public Graph graph;
	public VehicleTypeList vehicleList;

	public Server() throws Exception {
		graph = new OSMReader().readOSM("schleswig-holstein.osm.pbf");
		// TODO: Handle exception
		vehicleList = new VehicleTypeList();
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

	public IPath<Vertex> route(Vehicle v, double battery, long from, long to,
			String optimization, String algorithm, boolean turns) {
		Vertex x = graph.getVertexByID(from);
		Vertex y = graph.getVertexByID(to);
		// TODO: Consider parameters
		return new Dijkstra(graph,
				new PartialPreorderTreeFactory<Vertex, Double>()).route(x, y);
	}

	public Object range(Vehicle v, double battery, long from) {
		// TODO Auto-generated method stub
		return null;
	}

}

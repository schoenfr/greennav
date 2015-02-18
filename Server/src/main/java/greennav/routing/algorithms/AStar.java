package greennav.routing.algorithms;

import greennav.routing.data.Graph;
import greennav.routing.data.Graph.Edge;
import greennav.routing.data.Graph.Vertex;
import greennav.routing.data.path.IPath;
import greennav.routing.data.path.VertexList;
import greennav.routing.queue.AbstractTotalPreorder;
import greennav.routing.queue.ITotalPreorderQueue;
import greennav.routing.queue.ITotalPreorderQueueFactory;
import greennav.routing.queue.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is Dijkstra's algorithm.
 */
public class AStar {

	private Graph graph;

	private ITotalPreorderQueueFactory<Vertex, Double, ? extends ITotalPreorderQueue<Vertex, Double>> qf;
	private ITotalPreorderQueue<Vertex, Double> q;

	public AStar(
			Graph graph,
			ITotalPreorderQueueFactory<Vertex, Double, ? extends ITotalPreorderQueue<Vertex, Double>> qf) {
		this.graph = graph;
		this.qf = qf;
	}

	/**
	 * Use air line distance as heuristic value.
	 */
	public double heuristic(Vertex x, Vertex y) {
		return x.distanceToInMeter(y.getLat(), y.getLon());
	}

	public IPath<Vertex> route(Vertex from, Vertex to) {

		final Map<Vertex, Vertex> pred = new HashMap<>();
		final Map<Vertex, Double> dist = new HashMap<>();

		pred.put(from, null);
		dist.put(to, Double.POSITIVE_INFINITY);
		dist.put(from, 0.0);

		q = qf.createEmpty(new AbstractTotalPreorder<Double>() {
			@Override
			public boolean lessEqual(Double a, Double b) {
				return a <= b;
			}
		});

		q.insert(from, heuristic(from, to));

		while (!q.isEmpty()) {

			Pair<Vertex, Double> pair = q.pull();
			Vertex a = pair.getFirst();
			double da = pair.getSecond();

			if (da >= dist.get(to))
				break;

			Iterator<Edge> it = graph.edgeIterator(a);
			while (it.hasNext()) {

				Edge e = it.next();
				Vertex b = e.getTo();
				double cost = e.getCosts();
				double db = Double.POSITIVE_INFINITY;
				if (dist.containsKey(b))
					db = dist.get(b);

				if (da + cost < db) {
					pred.put(b, a);
					dist.put(b, da + cost);
					q.insert(b, (da + cost) + heuristic(b, to));
				}

			}

		}

		List<Vertex> result = new LinkedList<>();
		if (pred.containsKey(to)) {
			Vertex curr = to;
			while (curr != null) {
				result.add(0, curr);
				curr = pred.get(curr);
			}
		}
		return new VertexList<Vertex>(result);

	}
}

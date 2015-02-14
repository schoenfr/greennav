package greennav.routing.algorithms;

import greennav.routing.data.Graph;
import greennav.routing.data.Graph.Edge;
import greennav.routing.data.Graph.Vertex;
import greennav.routing.data.path.IPath;
import greennav.routing.data.path.VertexList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * This is Dijkstra's algorithm.
 */
public class Dijkstra {

	private Graph graph;

	private QueueFactory qf;

	public Dijkstra(Graph graph, QueueFactory qf) {
		this.graph = graph;
		this.qf = qf;
	}

	public IPath<Vertex> route(Vertex from, Vertex to) {

		final Map<Vertex, Vertex> pred = new HashMap<>();
		final Map<Vertex, Double> dist = new HashMap<>();

		pred.put(from, null);
		dist.put(to, Double.POSITIVE_INFINITY);
		dist.put(from, 0.0);

		PriorityQueue<Vertex> q = qf.createQueue(new Comparator<Vertex>() {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				Double d1 = dist.get(o1);
				if (d1 == null)
					d1 = Double.POSITIVE_INFINITY;
				Double d2 = dist.get(o2);
				if (d2 == null)
					d2 = Double.POSITIVE_INFINITY;
				return (int) Math.signum(d1 - d2);
			}
		});

		q.add(from);

		int i = 0;

		while (!q.isEmpty()) {

			Vertex a = q.poll();
			double da = dist.get(a);

			if (i++ % 500 == 0) {
				System.out.println(da + " --- " + q.size());
				Iterator<Vertex> it = q.iterator();
				double av = 0;
				while (it.hasNext()) {
					Vertex bla = it.next();
					av += dist.get(bla);
				}
				System.out.println(av / q.size());
			}

			if (da >= dist.get(to))
				break;

			Iterator<Edge> it = graph.edgeIterator(a);
			while (it.hasNext()) {

				Edge e = it.next();

				if (e.getFrom() != a) {
					throw new RuntimeException("passt nicht");
				}
				Vertex b = e.getTo();

				double cost = e.getCosts();

				double db = Double.POSITIVE_INFINITY;
				if (dist.containsKey(b))
					db = dist.get(b);

				if (da + cost < db) {
					q.remove(b);
					dist.put(b, da + cost);
					pred.put(b, a);
					q.add(b);
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

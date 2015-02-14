package greennav.routing.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

public class Graph {

	/**
	 * Earth radius approximating the earth as a sphere, in meter.
	 */
	public static final double EARTH_RADIUS = 6378137;

	public class Vertex implements Comparable<Vertex> {
		int index;
		long id;
		double lat, lon;

		int edgeIndex = 0;
		int edges = 0;

		public Vertex(int index, long id, double lat, double lon) {
			this.index = index;
			this.id = id;
			this.lat = lat;
			this.lon = lon;
		}

		@Override
		public int compareTo(Vertex o) {
			return index - o.index;
		}

		/**
		 * Compute the great-circle distance on the unit sphere.
		 * 
		 * See: http://mathworld.wolfram.com/GreatCircle.html
		 * 
		 * @param other
		 *            Latitude and longitude of second point.
		 * @return The distance of both points on the surface of the unit
		 *         sphere.
		 */
		public double distanceToInMeter(double lat2, double lon2) {
			double rlat = lat * Math.PI / 180.0;
			double rlat2 = lat2 * Math.PI / 180.0;
			double rlon = lon * Math.PI / 180.0;
			double rlon2 = lon2 * Math.PI / 180.0;
			return EARTH_RADIUS
					* Math.acos(Math.cos(rlat) * Math.cos(rlat2)
							* (Math.cos(rlon - rlon2)) + Math.sin(rlat)
							* Math.sin(rlat2));
		}

		public long getID() {
			return id;
		}

		public double getLat() {
			return lat;
		}

		public double getLon() {
			return lon;
		}

	}

	public class Edge implements Comparable<Edge> {
		Vertex from;
		Vertex to;

		public Edge(Vertex from, Vertex to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public int compareTo(Edge o) {
			int v = from.compareTo(o.from);
			if (v == 0)
				v = to.compareTo(o.to);
			return v;
		}

		public Vertex getFrom() {
			return from;
		}

		public Vertex getTo() {
			return to;
		}

		public double getCosts() {
			return from.distanceToInMeter(to.lat, to.lon);
		}
	}

	private LinkedList<Vertex> vertices = new LinkedList<Vertex>();
	private Vector<Edge> edges = new Vector<Edge>();
	private Map<Long, Vertex> osmToVertex = new HashMap<>();

	public void addVertex(long id, double latitude, double longitude) {
		Vertex v = new Vertex(vertices.size(), id, latitude, longitude);
		vertices.add(v);
		osmToVertex.put(id, v);
	}

	public int getSize() {
		return vertices.size();
	}

	public Vertex getVertexByID(long id) {
		return osmToVertex.get(id);
	}

	public void addEdge(long fromID, long toID, boolean oneway) {
		Vertex from = getVertexByID(fromID);
		if (from == null)
			return;
		Vertex to = getVertexByID(toID);
		if (to == null)
			return;
		edges.add(new Edge(from, to));
		edges.add(new Edge(to, from));
		from.edges++;
		to.edges++;
	}

	public void cleanup() {
		System.out.println("starting cleanup");
		System.gc();
		System.out.println("graph contains " + vertices.size()
				+ " vertices and " + edges.size() + " edges");
		Collections.sort(vertices);
		System.out.println("vertices sorted");
		int index = 0;
		for (Vertex v : vertices) {
			v.index = index;
			index++;
		}
		System.out.println("vertices indexed");
		Collections.sort(edges);
		System.out.println("edges sorted");
		Iterator<Vertex> itv = vertices.iterator();
		Vertex prev = itv.next();
		prev.edgeIndex = 0;
		while (itv.hasNext()) {
			Vertex v = itv.next();
			v.edgeIndex = prev.edgeIndex + prev.edges;
			prev = v;
		}
		System.out.println("edges indexed");
		Iterator<Vertex> ite = vertices.iterator();
		while (ite.hasNext()) {
			Vertex v = ite.next();
			if (v.edges == 0)
				ite.remove();
		}
		System.out.println("unnecessary vertices removed");
		index = 0;
		for (Vertex v : vertices) {
			v.index = index;
			index++;
		}
		System.out.println("vertices indexed");
		System.gc();
		System.out.println("graph now contains " + vertices.size()
				+ " vertices and " + edges.size() + " edges");
	}

	public Vertex getVertexByLatLon(double lat, double lon) {
		Vertex res = null;
		double dist = 500;
		for (Vertex v : vertices) {
			double d = v.distanceToInMeter(lat, lon);
			if (d < dist) {
				res = v;
				dist = d;
			}
		}
		return res;
	}

	public Iterator<Edge> edgeIterator(final Vertex v) {
		final int from = v.edgeIndex;
		final int to = v.edgeIndex + v.edges;
		return new Iterator<Edge>() {
			int i = from;

			@Override
			public boolean hasNext() {
				return i < to;
			}

			@Override
			public Edge next() {
				Edge e = edges.get(i);
				i++;
				return e;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}

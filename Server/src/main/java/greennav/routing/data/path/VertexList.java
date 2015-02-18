package greennav.routing.data.path;

import java.util.List;

public class VertexList<V> extends Path<V> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 110639191553588655L;
	private final List<V> vertices;

	public VertexList(List<V> vertices) {
		if (vertices.isEmpty())
			throw new IllegalArgumentException(
					"Creating VertexList without any vertex is not allowed.");
		this.vertices = vertices;
	}

	@Override
	public V getFrom() {
		return vertices.get(0);
	}

	@Override
	public V getTo() {
		return vertices.get(vertices.size() - 1);
	}

	@Override
	public int getLength() {
		return vertices.size() - 1;
	}

	@Override
	public List<V> toVertexList() {
		return vertices;
	}

}

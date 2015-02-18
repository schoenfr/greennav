package greennav.routing.data.path;

import java.util.ArrayList;
import java.util.List;

public class SingletonPath<V> extends Path<V> {
	/**
	 * Serial version identification for object serialization.
	 */
	private static final long serialVersionUID = -81697379467929384L;
	private V vertex;

	public SingletonPath(V vertex) {
		this.vertex = vertex;
	}

	@Override
	public V getFrom() {
		return vertex;
	}

	@Override
	public V getTo() {
		return vertex;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public List<V> toVertexList() {
		List<V> res = new ArrayList<V>();
		res.add(vertex);
		return res;
	}

}

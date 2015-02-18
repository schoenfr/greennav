package greennav.routing.data.path;

import java.util.ArrayList;
import java.util.List;

public class EdgePath<V> extends Path<V> {

	/**
	 * Serial version identification for object serialization.
	 */
	private static final long serialVersionUID = 3639512755037788240L;
	private V from;
	private V to;

	public EdgePath(V from, V to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public V getFrom() {
		return from;
	}

	@Override
	public V getTo() {
		return to;
	}

	@Override
	public int getLength() {
		return 1;
	}

	@Override
	public List<V> toVertexList() {
		List<V> res = new ArrayList<V>();
		res.add(from);
		res.add(to);
		return res;
	}

}

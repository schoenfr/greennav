package greennav.routing.data.path;

import java.util.ArrayList;
import java.util.List;

public class ComposedPath<V> extends Path<V> {
	/**
	 * Serial version identification for object serialization.
	 */
	private static final long serialVersionUID = 1231136542964488002L;

	private V from;
	private V to;
	private IPath<V> sub1;
	private IPath<V> sub2;

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
		return sub1.getLength() + sub2.getLength();
	}

	public ComposedPath(IPath<V> first, IPath<V> second) {
		if (!first.getTo().equals(second.getFrom())) {
			throw new RuntimeException(
					"Can not compose paths of diff end vertices.");
		}
		this.sub1 = first;
		this.from = first.getFrom();
		this.sub2 = second;
		this.to = second.getTo();
	}

	@Override
	public List<V> toVertexList() {
		List<V> res = new ArrayList<V>();
		res.addAll(sub1.toVertexList());
		res.remove(res.size() - 1);
		res.addAll(sub2.toVertexList());
		return res;
	}

}

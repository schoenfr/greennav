package greennav.routing.data.path;

import java.io.Serializable;
import java.util.List;

public abstract class Path<V> implements IPath<V>, Serializable {

	/**
	 * Serial version identification for object serialization.
	 */
	private static final long serialVersionUID = 6177230116622815231L;

	@Override
	public IPath<V> compose(IPath<V> other) {
		return new ComposedPath<V>(this, other);
	}

	@Override
	public String toString() {
		List<V> vertices = toVertexList();
		StringBuilder sb = new StringBuilder();
		for (V v : vertices)
			sb.append(v.toString());
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Path) {
			Path other = (Path) obj;
			return toVertexList().equals(other.toVertexList());
		}
		return false;
	}

}

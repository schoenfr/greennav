package greennav.routing.data.path;

import java.util.List;

public interface IPath<V> extends IComposable<IPath<V>> {

	public V getFrom();

	public V getTo();

	public int getLength();

	public List<V> toVertexList();
}

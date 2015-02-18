package greennav.routing.queue;

public interface IQueue<V extends Comparable<V>> {
	public void add(V value);

	public boolean isEmpty();

	public int size();

	public V peek();

	public V poll();

	public void clear();
}

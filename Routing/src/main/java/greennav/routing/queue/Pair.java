package greennav.routing.queue;

/**
 * Mostly it is not a good practice to use any kind of tuple objects, but
 * especially for mathematical functions they are quite useful.
 */
public class Pair<A, B> {
	/**
	 * First object of pair.
	 */
	private final A first;
	/**
	 * Second object of pair.
	 */
	private final B second;

	/**
	 * Constructor taking both objects.
	 * 
	 * @param first
	 *            First object.
	 * @param second
	 *            Second object.
	 */
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Getter for first object.
	 * 
	 * @return First object.
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * Getter for second object.
	 * 
	 * @return Second object.
	 */
	public B getSecond() {
		return second;
	}

	/**
	 * Prints pair to a string object.
	 */
	@Override
	public String toString() {
		return "[" + first + ", " + second + "]";
	}

	/**
	 * The pair is equal to another object, if and only if the other object is a
	 * pair containing equal elements.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		Pair<?, ?> p = (Pair<?, ?>) obj;
		return first.equals(p.first) && second.equals(p.second);
	}

	/**
	 * Combines the hash codes of both objects. Suggestion of Joshua Bloch,
	 * Effective Java.
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + (first == null ? 0 : first.hashCode());
		hash = hash * 31 + (second == null ? 0 : second.hashCode());
		return hash;
	}

	/**
	 * This create method is used to save writing, you do not need to write down
	 * the generic parameters explicitly.
	 * 
	 * @param first
	 *            First object.
	 * @param second
	 *            Second object.
	 * @return Pair of both objects.
	 */
	public static <A, B> Pair<A, B> create(A first, B second) {
		return new Pair<A, B>(first, second);
	}
}

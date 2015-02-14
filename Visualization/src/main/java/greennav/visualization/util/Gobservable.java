package greennav.visualization.util;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This is a generic observable class wrapping a vector of observers. The
 * notification needs to be done manually on all observers. You can iterate the
 * observe by using the <code>Iterable</code> interface of this class.
 * 
 * @param <T>
 *            The type of the observers.
 */
public class Gobservable<T> implements Iterable<T> {

	/**
	 * The wrapped list of observers.
	 */
	private List<T> observers = new Vector<>();

	/**
	 * Add observer.
	 */
	public void addObserver(T observer) {
		observers.add(observer);
	}

	/**
	 * Remove observer.
	 */
	public void removeObserver(T observer) {
		observers.remove(observer);
	}

	/**
	 * Provides an iterator for the observers.
	 */
	@Override
	public Iterator<T> iterator() {
		return observers.iterator();
	}

}

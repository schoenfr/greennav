package greennav.routing.queue;

import java.util.HashSet;
import java.util.Set;

/**
 * The structure is used to build up partial preorder trees. It is an auxiliary
 * data structure.
 * 
 * @param <K>
 *            Key type.
 * @param <V>
 *            Value type, used for comparisons.
 */
class TreeElement<K, V> {

	/**
	 * The pair of this tree node.
	 */
	private Pair<K, V> pair;

	/**
	 * Reference to the left subtree.
	 */
	TreeElement<K, V> left;
	/**
	 * Reference to the right subtree.
	 */
	TreeElement<K, V> right;
	/**
	 * Reference to the parent element.
	 */
	TreeElement<K, V> parent;

	/**
	 * Constructor for tree elements.
	 * 
	 * @param pair
	 *            The pair this node shall carry.
	 */
	TreeElement(Pair<K, V> pair) {
		this.pair = pair;
		this.left = null;
		this.right = null;
		this.parent = null;
	}

	/**
	 * Check, if given key is within this tree element or in one of its
	 * subtrees.
	 * 
	 * @param k
	 *            The key to search for.
	 * @return True, if the key was found.
	 */
	boolean in(K k) {
		return this.pair.getFirst().equals(k)
				|| (this.left != null && this.left.in(k))
				|| (this.right != null && this.right.in(k));
	}

	/**
	 * Get the value of a given key, if the key is contained within this
	 * subtree.
	 * 
	 * @param k
	 *            The key to search for.
	 * @return The value, the key maps to.
	 */
	V get(K k) {
		if (this.pair.getFirst().equals(k)) {
			return this.pair.getSecond();
		} else {
			if (this.left != null) {
				V v = this.left.get(k);
				if (v != null) {
					return v;
				}
			}
			if (this.right != null) {
				V v = this.right.get(k);
				if (v != null) {
					return v;
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the key of this tree element's pair.
	 * 
	 * @return Key of this tree element's pair.
	 */
	K getK() {
		return pair.getFirst();
	}

	/**
	 * Getter for the value of this tree element's pair.
	 * 
	 * @return Value of this tree element's pair.
	 */
	V getV() {
		return pair.getSecond();
	}

	/**
	 * Getter for this tree element's pair.
	 * 
	 * @return This tree element's pair.
	 */
	Pair<K, V> getPair() {
		return pair;
	}

}

/**
 * Represents a partial preorder queue and implements respective methods for
 * manipulating and retrieving entries in the queue. The underlying data
 * structure is an unbalanced binary tree.
 * 
 * @author Christofer Krueger
 */
public class PartialPreorderTree<K, V> implements IPartialPreorderQueue<K, V> {

	private IPartialPreorder<V> p;
	private int size = 0;

	private TreeElement<K, V> top;
	private TreeElement<K, V> min;

	public PartialPreorderTree(IPartialPreorder<V> preorder) {
		this.p = preorder;
		this.top = null;
		this.min = null;
	}

	@Override
	public void clear() {
		size = 0;
		top = null;
		min = null;
	}

	@Override
	public void insert(K k, V v) {
		size++;
		TreeElement<K, V> e = new TreeElement<K, V>(new Pair<K, V>(k, v));
		if (this.top != null) {
			TreeElement<K, V> t = top;
			while (true) {
				// TODO: improve?
				if (!(!p.lessEqual(v, t.getV()) && p.lessEqual(t.getV(), v))) {
					if (t.left != null) {
						t = t.left;
					} else {
						// insert left of current node
						t.left = e;
						e.parent = t;
						break;
					}
				} else {
					if (t.right != null) {
						t = t.right;
					} else {
						// insert right of current node
						t.right = e;
						e.parent = t;
						break;
					}
				}
			}
		} else {
			// queue is empty
			top = e;
			min = e;
		}
		// set pointer to the new minimal element
		if (min.left != null) {
			min = min.left;
		}

	}

	@Override
	public void change(K k, V v) {
		if (in(k)) {
			insert(k, v);
		}
	}

	@Override
	public Pair<K, V> peek() {
		return this.min.getPair();
	}

	@Override
	public Pair<K, V> pull() {

		Pair<K, V> ret;

		if (this.min != null) {

			size--;

			if (this.min.right != null && this.min.parent != null) {

				Pair<K, V> p = new Pair<K, V>(min.getK(), min.getV());

				TreeElement<K, V> e = min;

				min.parent.left = min.right;
				min.right.parent = min.parent;

				// find the new minimal element
				min = e.right;
				while (min.left != null) {
					min = min.left;
				}

				e.parent = null;
				e.right = null;

				ret = p;

			} else if (this.min.right == null && this.min.parent != null) {

				Pair<K, V> p = new Pair<K, V>(min.getK(), min.getV());
				this.min = this.min.parent;
				this.min.left.parent = null;
				this.min.left = null;

				ret = p;

			} else if (this.min.right != null && this.min.parent == null) {
				// minimal element is the root element

				Pair<K, V> p = new Pair<K, V>(top.getK(), top.getV());

				TreeElement<K, V> e = top;

				top = min.right;

				// find the minimal element
				min = top;
				while (min.left != null) {
					min = min.left;
				}

				top.parent = null;
				e.right = null;

				ret = p;

			} else {
				// there is only one element in the queue

				Pair<K, V> p = new Pair<K, V>(top.getK(), top.getV());
				this.top = null;
				this.min = null;

				ret = p;
			}

		} else {
			ret = null;
		}

		return ret;
	}

	@Override
	public Set<Pair<K, V>> front() {

		Set<Pair<K, V>> t = new HashSet<Pair<K, V>>();

		TreeElement<K, V> e = min;

		boolean b;
		while (e != null) {

			b = true;
			for (Pair<K, V> v : t) {
				if (p.lessEqual(e.getV(), v.getSecond()) != p.lessEqual(
						v.getSecond(), e.getV())) {
					// current element is greater than an element in the front
					b = false;
					break;
				}
			}
			if (b) {
				// element is a front element
				t.add(e.getPair());
			}
			e = e.parent;
		}

		return t;
	}

	@Override
	public boolean isEmpty() {
		return this.top == null;
	}

	@Override
	public boolean in(K k) {
		if (top != null) {
			return top.in(k);
		} else {
			return false;
		}
	}

	@Override
	public V get(K k) {
		if (top != null) {
			return top.get(k);
		} else {
			return null;
		}
	}

	@Override
	public int getSize() {
		return size;
	}

}

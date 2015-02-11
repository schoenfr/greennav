package greennav.visualization.view.shapes;

import greennav.mapviewer.computations.Mercator;
import greennav.mapviewer.shapes.MapShape;
import greennav.mapviewer.shapes.Marker;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Data structure to store and paint Marker efficiently. The collection of
 * MapShapes is a MapShape itself.
 */
public class CursoredMarkerList extends MapShape {

	/**
	 * An array list is used to store the map shapes in the order of insertion.
	 * You should not use the <code>MapShapeList</code> here, because it is a
	 * sorted list using a comparator.
	 */
	private ArrayList<Marker> list = new ArrayList<Marker>();

	/**
	 * Reference of the last MapShape to paint. The value -1 indicates, that the
	 * first element (at position 0) and all consecutive elements are not drawn.
	 */
	private int cursor = -1;

	/**
	 * Return the current element at the position of the cursor.
	 * 
	 * @return The map shape at given cursor position.
	 */
	public MapShape getCursor() {
		return list.get(cursor);
	}

	/**
	 * Resets the cursor.
	 */
	public void resetCursor() {
		cursor = -1;
	}

	/**
	 * Add a new Element at the end
	 * 
	 * @param t
	 *            The element to add
	 */
	public void add(Marker t) {
		list.add(t);
	}

	/**
	 * Removes all elements.
	 */
	public void clear() {
		list.clear();
		cursor = -1;
	}

	/**
	 * Returns the number of MapShaped in data structure.
	 * 
	 * @return The number of added MapShapes
	 */
	public int size() {
		return list.size();
	}

	/**
	 * Moves the cursor one step right. If the end of data structure is reached,
	 * nothing will happen.
	 */
	public void cursorForward() {
		if (cursor < 0) {
			cursor = 0;
		} else if (cursor + 1 < list.size()) {
			cursor++;
		}
	}

	/**
	 * Moves the cursor one step left. If the end of data structure is reached,
	 * nothing will happen.
	 */
	public void cursorBackward() {
		if (cursor >= 0) {
			cursor--;
		}
	}

	/**
	 * Paint all the map shapes in the order of insertion up to the given cursor
	 * position. TODO: INCLUSIVE OF EXCLUSIVE?
	 */
	@Override
	public void paint(Mercator merc, Graphics g) {
		int c = 100;
		// paint all shapes between first and cursor (including)
		for (int i = cursor; i > Math.max(0, cursor - c); i--) {
			float alpha = ((float) i - Math.max(0, cursor - c)) / ((float) c);
			alpha = alpha * alpha * alpha;
			list.get(i).setColor(new Color(1 - alpha, 0f, alpha, 1));
			list.get(i).paint(merc, g);
		}
	}

}

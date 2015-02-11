package greennav.visualization.view.shapes;

import greennav.mapviewer.computations.Mercator;
import greennav.mapviewer.shapes.Marker;
import greennav.mapviewer.structures.XY;
import greennav.osmosis.structs.LatLon;

import java.awt.Color;
import java.awt.Graphics;

/**
 * A marker for vertices, such as those currently enqueued or last visited.
 */
public class VertexMarker extends Marker {

	/**
	 * The size of the vertex, this could be the profile size for example.
	 */
	private final int size;

	/**
	 * The constructor takes the coordinate, the color and the size.
	 * 
	 * @param latlon
	 *            The coordinate.
	 * @param color
	 *            The color.
	 * @param size
	 *            The size.
	 */
	public VertexMarker(LatLon latlon, Color color, int size) {
		super(latlon, color);
		this.size = size;
	}

	/**
	 * The paint method is overriden in order to paint a circle of the given
	 * size.
	 */
	@Override
	public void paint(Mercator merc, Graphics g) {
		XY c = merc.latlonToPoint(latlon);
		int s = (int) (merc.getZoom() * 3.0 / 4.0 * Math.sqrt((double) size));
		g.setColor(color);
		g.fillOval(c.x - s / 2, c.y - s / 2, s, s);
	}

}

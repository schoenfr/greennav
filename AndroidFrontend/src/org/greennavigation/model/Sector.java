package org.greennavigation.model;

import org.mapsforge.core.GeoPoint;
import android.location.Location;
import android.util.Log;

/**
 * Represents a Sector spanned around 2 Geopoints
 */
public class Sector {

	public GeoPoint topleft;
	public GeoPoint topright;
	public GeoPoint bottomleft;
	public GeoPoint bottomright;

	private final float SECTOR_WIDTH = 20.0f;

	/**
	 * 
	 * @param start
	 * @param end
	 */
	public Sector(GeoPoint start, GeoPoint end) {
		// direction vector between points
		double diry = end.getLatitude() - start.getLatitude();
		double dirx = end.getLongitude() - start.getLongitude();

		// get length and scale to apropriate length
		// convert geopoints to Location, so preimplemented function
		// "distanceTo" can be used
		Location startloc = new Location("start");
		Location endloc = new Location("end");

		startloc.setLatitude(start.getLatitude());
		startloc.setLongitude(start.getLongitude());
		endloc.setLatitude(end.getLatitude());
		endloc.setLongitude(end.getLongitude());

		float distance = startloc.distanceTo(endloc);
		float scalefactor = (SECTOR_WIDTH / 2) / distance;

		diry = diry * scalefactor;
		dirx = dirx * scalefactor;

		//add scaled direction vector along normal
		// normal vectors are (-diry,dirx) and (diry,-dirx)
		topleft = new GeoPoint(start.getLatitude() + dirx, start.getLongitude()
				- diry);
		bottomleft = new GeoPoint(start.getLatitude() - dirx,
				start.getLongitude() + diry);
		topright = new GeoPoint(end.getLatitude() + dirx, end.getLongitude()
				- diry);
		bottomright = new GeoPoint(end.getLatitude() - dirx, end.getLongitude()
				+ diry);
	}

	/**
	 * Checks if given point is located inside the sector
	 * @param point
	 * @return
	 */
	public boolean inSector(Location point) {
		// do bounding check as described at :
		// http://stackoverflow.com/a/2752753
		
		//Log.i("SECTOR","rect: "+topleft+" "+bottomleft+" "+bottomright+" "+topright);
		//Log.i("SECTOR","loc: "+point);
		
		if (checkEdge(topleft, bottomleft, point)) {
			if (checkEdge(bottomleft, bottomright, point)) {
				if (checkEdge(bottomright, topright, point)) {
					if (checkEdge(topright, topleft, point)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Check wether a given point is on the left or right side of the edge between a and b
	 * @param a Startingpoint of edge
	 * @param b Endpoint of edge
	 * @param point 
	 * @return
	 */
	private boolean checkEdge(GeoPoint a, GeoPoint b, Location point) {
		double A = -(b.getLatitude() - a.getLatitude());
		double B = b.getLongitude() - a.getLongitude();
		double C = -(A * a.getLongitude() + B * a.getLatitude());

		double D = A * point.getLongitude() + B * point.getLatitude() + C;
		if (D >= 0) {
			return true;
		} else {
			return false;
		}
	}
}

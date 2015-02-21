package org.greennavigation.model;

import java.util.LinkedList;
import java.util.List;

import org.greennavigation.ui.R;
import org.mapsforge.core.GeoPoint;

import android.content.Context;
import android.util.Log;

/**
 * model class presents a Request model.
 */

public class ResponseRouteModel {

	private String id;
	public String batteryCharge;
	public String distance;
	public String time;
	public String start;
	public String destination;
	private List<RouteNode> route;
	private GeoPoint rangPoint;
	private Context context;

	public ResponseRouteModel(Context context) {
		setContext(context);
	}



	/**
	 * @param batteryCharge
	 *            the batteryCharge to set
	 */
	public void setBatteryCharge(String batteryCharge) {
		this.batteryCharge = batteryCharge;
	}

	/**
	 * @return the batteryCharge
	 */
	public String getBatteryCharge() {
		return this.batteryCharge;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * @return the distance
	 */
	public String getDistance() {
		return this.distance;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setStringTime(String time) {
		this.time = time;
	}

	/**
	 * @return the time
	 */
	public String getStringTime() {
		return this.time;
	}

	/**
	 * @return the int time {@link Integer}
	 */
	public Integer getIntegerTime() {

		Integer intValue = null;

		String stringTime = getStringTime();
		try {
			intValue = Integer.parseInt(stringTime);
		} catch (NumberFormatException e) {
			Log.e(getClass().getSimpleName(), e.toString());
		}

		return intValue;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return this.start;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return this.destination;
	}

	/**
	 * @param route
	 *            the route to set
	 */
	public void setRoute(List<RouteNode> route) {
		this.route = route;
	}

	/**
	 * @return the route
	 */
	public List<GeoPoint> getRoute() {
		LinkedList<GeoPoint> geoPoints = new LinkedList<GeoPoint>();
		for (RouteNode node:route) {
			geoPoints.add(new GeoPoint(node.getLocation().latitudeE6, node.getLocation().longitudeE6));
		}
		return geoPoints;
	}
	
	/**
	 * @return the route with additional turn information
	 */
	public List<RouteNode> getRouteWithTurnInformation() {
		return route;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {

		return "ID : " + getId() + " start: " + getStart()
		+ getContext().getString(R.string.destination) + " : "
		+ getDestination() + getContext().getString(R.string.time)
		+ " : " + getStringTime() + " "
		+ getContext().getString(R.string.distance) + " : "
		+ getDistance() + " \n " +getContext().getString(R.string.route)+" : "+
									getRoute();

	}

	public String getRouteInfo() {
      String start = "\n"+getContext().getString(R.string.start);
		return start+"" + getStart()+"\n"
				+ getContext().getString(R.string.destination) + " : "
				+ getDestination() +"\n"+ getContext().getString(R.string.time)
				+ " : " + getStringTime() + " \n"
				+ getContext().getString(R.string.distance) + " : "
				+ getDistance() + " \n ";// +getContext().getString(R.string.route)+" : "+
											// getRoute();
	}
	
	public String getPolygonInfo() {
		 String start = null; 

		  if (getStart()!=null) {
			 start= "\n"+getContext().getString(R.string.start)+": "+getStart();
		}
		return start;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param rangPoint the rangPoint to set
	 */
	public void setRangPoint(GeoPoint rangPoint) {
		this.rangPoint = rangPoint;
	}

	/**
	 * @return the rangPoint
	 */
	public GeoPoint getRangPoint() {
		return rangPoint;
	}

}

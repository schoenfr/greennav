package org.greennavigation.model;

import java.util.Observer;

/**
 * This interface provides the String-Constants used in the communication
 * between {@link NavigationModel} and its {@link Observer}.
 * 
 */
public interface EventConstants {

	/**
	 * This event indicates that the location of the device has changed.
	 */
	public static final String LOCATION_CHANGED = "locChanged";
	/**
	 * This event indicates that the next turn has changed.
	 */
	public static final String TURN_CHANGED = "turnChanged";
	/**
	 * This event indicates that the device has left the route.
	 */
	public static final String OFF_ROUTE = "offRoute";
	/**
	 * This event indicates that the device as reached the route's destination.
	 */
	public static final String REACHED_TARGET = "reachedTarget";
	/**
	 * This event indicates that the overlay showing information about the next turn should be created.
	 */
	public static final String CREATED_OVERLAY = "createdOverlay";
	/**
	 * This event indicates that the overlay should be displayed.
	 */
	public static final String SHOW_OVERLAY = "showOverlay";
	/**
	 * This event indicates that the overlay should be removed now.
	 */
	public static final String NO_OVERLAY = "noOverlay";

	public static final int GOT_MODEL = 1;
	public static final int NO_MODEL = 0;
}

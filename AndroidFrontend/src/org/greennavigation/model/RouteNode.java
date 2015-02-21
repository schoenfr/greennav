package org.greennavigation.model;

import org.mapsforge.core.GeoPoint;

/**
 * This class represents a node on a route, including a location, a street name
 * and a turn direction.
 */
public class RouteNode {
	/**
	 * The location of the node.
	 */
	private GeoPoint location;
	/**
	 * The name of the street after this node.
	 */
	private String nextStreetName;
	/**
	 * The turn indicated by this node.
	 */
	private TurnDirection nextTurnDirection;

	public RouteNode(GeoPoint location, String nextStreetName,
			TurnDirection nextTurnDirection) {
		super();
		this.location = location;
		this.nextStreetName = nextStreetName;
		this.nextTurnDirection = nextTurnDirection;
	}

	/**
	 * @return the node's location
	 */
	public GeoPoint getLocation() {
		return location;
	}

	/**
	 * @return the name of the street after the node
	 */
	public String getNextStreetName() {
		return nextStreetName;
	}

	/**
	 * @return the kind of turn indicated by the node
	 */
	public TurnDirection getNextTurnDirection() {
		return nextTurnDirection;
	}

}

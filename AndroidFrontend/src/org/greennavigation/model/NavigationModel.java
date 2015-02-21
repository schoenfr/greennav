package org.greennavigation.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

import org.greennavigation.ui.activities.GreenNavigationMap;
import org.mapsforge.core.GeoPoint;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * This class is responsible for holding all information of a route and keeping
 * track of the device's position on it. As a response to certain condition it
 * sends events through its {@link Observable} methods which can be handled by a
 * view component like {@link GreenNavigationMap}.
 */
public class NavigationModel extends Observable implements LocationListener,
		EventConstants {

	/**
	 * If the device passes a route-node within this distance in meter, the node
	 * is considered passed.
	 */
	private final float MAX_DISTANCE_TO_REGISTER = 20.0f;

	/**
	 * The last known location of the device as given by the GPS.
	 */
	private Location current;
	/**
	 * The route as a list of {@link RouteNode}.
	 */
	private List<RouteNode> route;
	/**
	 * The {@link Iterator} pointing at the next {@link RouteNode} on the route.
	 */
	private Iterator<RouteNode> routeIterator;
	/**
	 * The {@link RouteNode} holding the information of the turn passed last.
	 */
	private RouteNode lastTurn;
	/**
	 * The {@link RouteNode} holding the information of the next turn.
	 */
	private RouteNode nextTurn;

	public NavigationModel(List<RouteNode> route) {
		super();
		this.route = route;
		for (RouteNode node : route) {
			Log.d("ROUTE", "<trkpt lat=\"" + node.getLocation().getLatitude()
					+ "\" lon=\"" + node.getLocation().getLongitude() + "\">");
		}
		lastTurn = null;
		nextTurn = route.get(0);
		routeIterator = route.iterator();
	}

	/**
	 * handle location changes and update the model and inform listeners
	 */
	@Override
	public void onLocationChanged(Location location) {
		current = location;
		Log.d("GPS", location.toString());
		// TODO: find out if we are off route.
		// float distanceToNext = getDistanceToRouteNode(current, nextTurn);
		// if ((distanceToNext < MAX_DISTANCE_TO_REGISTER && distanceToNext >=
		// 0.0f)
		// || (distanceToNext > -MAX_DISTANCE_TO_REGISTER && distanceToNext <
		// 0.0f)) {
		// // We are at the next turn.
		// routeIterator = route.listIterator(route
		// .indexOf(nextTurn));
		// if (routeIterator.hasNext()) {
		// // Still more nodes to go. Set new nextTurn and inform user
		// lastTurn = nextTurn;
		// nextTurn = routeIterator.next();
		// setChanged();
		// notifyObservers(EventConstants.TURN_CHANGED);
		// checkOverlay();
		// } else {
		// setChanged();
		// notifyObservers(EventConstants.SHOW_OVERLAY);
		// setChanged();
		// notifyObservers(EventConstants.REACHED_TARGET);
		// }
		// } else {
		// // Position changed, next turn remains the same
		// setChanged();
		// notifyObservers(EventConstants.LOCATION_CHANGED);
		// checkOverlay();
		// }

		// TODO: check if else ever reached
		// if RouteNodes are skipped then check if finish is reached and inform
		// listeners about changes
		if (checkForSkip()) {
		//Log.i("SECTOR", "SKIPPED SOMETHING");
			routeIterator = route.listIterator(route.indexOf(nextTurn));
			// if still nodes in route inform listeners and check if overlay
			// should be shown
			// if no nodes are left check if close enough to finish and inform
			// listeners
			if (routeIterator.hasNext()) {
				setChanged();
				notifyObservers(EventConstants.TURN_CHANGED);
				checkOverlay();
			} else {
				float distanceToFinish = getDistanceToRouteNode(current,
						nextTurn);
				if (distanceToFinish < MAX_DISTANCE_TO_REGISTER) {
					setChanged();
					notifyObservers(EventConstants.SHOW_OVERLAY);
					setChanged();
					notifyObservers(EventConstants.REACHED_TARGET);
				}
			}
		} else {
			setChanged();
			notifyObservers(EventConstants.LOCATION_CHANGED);
			checkOverlay();
		}
	}

	/**
	 * Creates a new Sector for 2 Geopoints and Checks if current location lies
	 * within this sectors If startnode or endnode are null no Sector will be
	 * constructed and the function will return false
	 * 
	 * @param lastnode
	 * @param nextnode
	 * @return true or false if current location lies in this Sector, always
	 *         returns false if startnode or endnode are null
	 */
	private boolean checkSector(RouteNode lastnode, RouteNode nextnode) {
		if (lastnode != null && nextnode != null) {
			Sector currentSector = new Sector(lastnode.getLocation(),
					nextnode.getLocation());
			if (currentSector.inSector(current)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if one or more RoutenNodes have been skipped and updates nextNode
	 * to new next Node.
	 * 
	 * @return true if one or more RouteNodes have been skipped
	 */
	private boolean checkForSkip() {
		// check if position is in sektor befor nextTurn
		// if in that sektor return false for no skips
		// else check for note we skipped to
		if (checkSector(lastTurn, nextTurn)) {
		//Log.i("SECTOR","IN START SECTOR");
			return false;
		} else {
		//Log.i("SECTOR","checking route");
			ListIterator<RouteNode> routeItr = route.listIterator(route
					.indexOf(nextTurn));
			RouteNode lastnode = nextTurn;

			// skip first index
			if (routeItr.hasNext())
				routeItr.next();

			RouteNode nextnode = null;
			if (routeItr.hasNext())
				nextnode = routeItr.next();

			if (nextnode != null) {
				// check first sector
				boolean result = false;
				result = checkSector(lastnode, nextnode);

				// if not in first sector loop over sectors till the sector
				// containing the current location is found or no sectors are
				// left
				while (result == false && routeItr.hasNext()) {
					lastnode = nextnode;
					nextnode = routeItr.next();
					result = checkSector(lastnode, nextnode);
				}
				// if sector is found skip to endnode of that sector
				if (result == true) {
				//Log.i("SECTOR","FOUND LOCATION ON ROUTE");
					lastTurn = nextTurn;
					nextTurn = nextnode;
				}else{
					Log.i("SECTOR","LOCATION NOT ON ROUTE");
				}
				return result;
			} else {
				Log.i("SECTOR","REACHED END");
				return false;
			}
		}
	}

	/**
	 * Sends an SHOW_OVERLAY event if closer than 200 meters to next turn else
	 * sends a NO_OVERLAY event
	 */
	private void checkOverlay() {
		if (getDistanceToTurn() <= 200.0f) {
			setChanged();
			notifyObservers(EventConstants.SHOW_OVERLAY);
		} else {
			setChanged();
			notifyObservers(EventConstants.NO_OVERLAY);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @return the last known location
	 */
	public Location getCurrentLocation() {
		return current;
	}

	/**
	 * 
	 * @return the name of the next Street
	 */
	public String getNextStreet() {
		return nextTurn.getNextStreetName();
	}

	/**
	 * Iterates over routnode list starting at nextTurn an returns the
	 * streetname after next turnchange
	 * 
	 * @return string containing the name of street after turn
	 */
	public String getNextTurnStreet() {
		ListIterator<RouteNode> routeItr = route.listIterator(route
				.indexOf(nextTurn));
		boolean turn = false;
		RouteNode nextNode;
		String name = "";

		while (routeItr.hasNext() && !turn) {
			nextNode = routeItr.next();
			name = nextNode.getNextStreetName();
			if (nextNode.getNextTurnDirection() != TurnDirection.STRAIGHT) {
				turn = true;
			}
		}
		return name;
	}

	/**
	 * 
	 * @return
	 */
	public TurnDirection getNextTurnDirection() {
		return nextTurn.getNextTurnDirection();
	}

	/**
	 * 
	 * @return
	 */
	public float getDistanceToNextTurn() {
		return getDistanceToRouteNode(current, nextTurn);
	}

	/**
	 * returns the distance between Location loc and RouteNode node
	 * 
	 * @param loc
	 * @param node
	 * @return
	 */
	private float getDistanceToRouteNode(Location loc, RouteNode node) {
		Location nxt = new Location("");
		nxt.setLatitude(node.getLocation().getLatitude());
		nxt.setLongitude(node.getLocation().getLongitude());
		nxt.setAltitude(loc.getAltitude());
		return loc.distanceTo(nxt);
	}

	/**
	 * Iterates over routnode list starting at nextTurn an returns the distance
	 * to next turnchange
	 * 
	 * @return distance to next turnchange
	 */
	public float getDistanceToTurn() {
		float distance = getDistanceToRouteNode(current, nextTurn);
		ListIterator<RouteNode> itr = route.listIterator(route
				.indexOf(nextTurn));
		boolean turn = false;
		RouteNode currentNode = nextTurn;
		RouteNode nextNode;

		while (itr.hasNext() && !turn) {
			nextNode = itr.next();
			if (nextNode.getNextTurnDirection() != TurnDirection.STRAIGHT) {
				distance += getDistanceBetweenNodes(currentNode, nextNode);
				turn = true;
			} else {
				distance += getDistanceBetweenNodes(currentNode, nextNode);
			}
			currentNode = nextNode;
		}
		if (turn) {
			return distance;
		} else {
			return Float.MAX_VALUE;
		}
	}

	/**
	 * Returns the distance between two RouteNodes
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	private float getDistanceBetweenNodes(RouteNode node1, RouteNode node2) {
		Location loc1 = new Location("");
		loc1.setLatitude(node1.getLocation().getLatitude());
		loc1.setLongitude(node2.getLocation().getLongitude());

		Location loc2 = new Location("");
		loc2.setLatitude(node2.getLocation().getLatitude());
		loc2.setLongitude(node2.getLocation().getLongitude());

		float distance = loc1.distanceTo(loc2);

		return distance;
	}

	/**
	 * Checks if current location is less than 200m to next turn. If so the
	 * TurnDirection (LEFT/RIGHT) of that turn is return. Otherwise STRAIGHT is
	 * returned
	 * 
	 * @return TurnDirection of next turn
	 */
	public TurnDirection getTurnDirection() {
		if (getDistanceToTurn() < 200.0) {
			ListIterator<RouteNode> itr = route.listIterator(route
					.indexOf(nextTurn));
			boolean turn = false;
			TurnDirection dir = TurnDirection.STRAIGHT;
			while (itr.hasNext() && !turn) {
				RouteNode next = itr.next();
				if (next.getNextTurnDirection() != TurnDirection.STRAIGHT) {
					turn = true;
					dir = next.getNextTurnDirection();
				}
			}
			return dir;
		} else {
			return TurnDirection.STRAIGHT;
		}
	}

	/**
	 * This method returns a List of all {@link RouteNode}s that are not marked
	 * as passed. the first node is the device's last known location.
	 * 
	 * @return all nodes not marked as passed
	 */
	public List<RouteNode> getRouteNodesToDestination() {
		boolean currentFlag = false;
		List<RouteNode> nodes = new LinkedList<RouteNode>();
		nodes.add(new RouteNode(new GeoPoint(current.getLatitude(), current
				.getLongitude()), "dummy", TurnDirection.STRAIGHT));
		for (RouteNode node : route) {
			if (node == nextTurn) {
				currentFlag = true;
			}
			if (currentFlag) {
				nodes.add(node);
			}
		}
		return nodes;
	}
}

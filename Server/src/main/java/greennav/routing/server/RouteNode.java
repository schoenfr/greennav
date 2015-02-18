package greennav.routing.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A route node is part of a route object and contains latitude and longitude as
 * well as turn information consisting of turn code, street name and further
 * information.
 */
@XmlRootElement(name = "routenode")
public class RouteNode {

	/**
	 * The turn code describes the type of turn at a particular route node.
	 */
	public static enum TurnCode {
		NO_TURN(0), STRAIGHT(1), LEFT_TURN(2), RIGHT_TURN(3), SLIGHT_LEFT_TURN(
				4), SLIGHT_RIGHT_TURN(5), HARD_LEFT_TURN(6), HARD_RIGHT_TURN(7), ENTER_ROUNDABOUT(
				20), LEAVE_ROUNDABOUT(21);
		private int code;

		private TurnCode(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}

	/**
	 * The latitude is the angle ranging from -90 degree (south pole) through 0
	 * degree (equator) to +90 degree (north pole).
	 */
	@XmlElement(name = "lat", required = true)
	public double latitude;

	/**
	 * The longitude specifies the east-west position of a point on the sphere.
	 */
	@XmlElement(name = "lon", required = true)
	public double longitude;

	/**
	 * The name of the street, that we turn into starting from this node. It may
	 * be null, if the street name doesn't change (with respect to the previous
	 * node).
	 */
	@XmlElement(name = "street", required = false)
	public String street = null;

	/**
	 * This is a code for turn descriptions. So far it is either: no turn (0),
	 * straight (1), left turn (2), right turn (3). In the future, we want to
	 * add codes for motorway exits and roundabout traffic.
	 */
	@XmlElement(name = "turn", required = false)
	public int turn = 0;

	/**
	 * This is further information regarding this route node.
	 */
	@XmlElement(name = "further", required = false)
	public String further = null;

}

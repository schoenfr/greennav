package greennav.routing.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response for route calculation.
 */
@XmlRootElement(name = "routingResponse")
public class RoutingResponse {

	/**
	 * Algorithm used for the routing.
	 */
	private String algorithm;

	/**
	 * Vehicle type used for the routing.
	 */
	private String vehicleType;

	/**
	 * Battery status at the end of the routing (range: 0 to 100 percent)
	 */
	private double batteryStatus;

	/**
	 * Optimization type used for the routing.
	 */
	private String optimization;

	/**
	 * Nodes of the generated route.
	 */
	private RouteNode[] route;

	/**
	 * Approximative travel time of the route (must be > 0).
	 */
	private double time;

	/**
	 * Distance of the route.
	 */
	private double distance;

	/**
	 * @return the algorithm
	 */
	@XmlElement(name = "algorithm", required = true)
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm
	 *            the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return the vehicleType
	 */
	@XmlElement(name = "vehicle", required = true)
	public String getVehicleType() {
		return vehicleType;
	}

	/**
	 * @param vehicleType
	 *            the vehicleType to set
	 */
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	/**
	 * @return the batteryStatus
	 */
	@XmlElement(name = "battery", required = true)
	public double getBatteryStatus() {
		return batteryStatus;
	}

	/**
	 * @param batteryStatus
	 *            the batteryStatus to set
	 */
	public void setBatteryStatus(double batteryStatus) {
		this.batteryStatus = batteryStatus;
	}

	/**
	 * @return the optimization
	 */
	@XmlElement(name = "optimization", required = true)
	public String getOptimization() {
		return optimization;
	}

	/**
	 * @param optimization
	 *            the optimization to set
	 */
	public void setOptimization(String optimization) {
		this.optimization = optimization;
	}

	/**
	 * @return the route
	 */
	@XmlElement(name = "route", type = RouteNode.class, required = true)
	public RouteNode[] getRoute() {
		return route;
	}

	/**
	 * @param route
	 *            the route to set
	 */
	public void setRoute(RouteNode[] route) {
		this.route = route;
	}

	/**
	 * @return the time
	 */
	@XmlElement(name = "time", required = true)
	public double getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(double time) {
		if (time < 0) {
			throw new IllegalArgumentException("time must greater 0");
		}
		this.time = time;
	}

	/**
	 * @return the distance
	 */
	@XmlElement(name = "distance", required = true)
	public double getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

}

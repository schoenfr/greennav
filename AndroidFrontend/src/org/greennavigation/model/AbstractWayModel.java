package org.greennavigation.model;

import org.mapsforge.core.GeoPoint;
 
/**
 * this is an abstract class . it presents a way from A to B :).
 */
public abstract class AbstractWayModel {

	private GreenNavAddress start;
	private GreenNavAddress destination;
	private String carType;

	private int batteryLevel,routingType; 
	private String name;
 
  public  AbstractWayModel(){}
  /**
   * 
   * @param start {@link GreenNavAddress}
   * @param destination {@link GreenNavAddress}
   * @param carType {@link String}
   * @param batteryLevel {@link Integer}
   * @param routingtype {@link Integer}
   */
	public AbstractWayModel(GreenNavAddress start,
			GreenNavAddress destination, String carType, int batteryLevel,
			int routingtype) {

		setDestination(destination);
		setCarType(carType);
		setBatteryLevel(batteryLevel);
		setStart(start);
		setRoutingType(routingtype);

	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(GreenNavAddress destination) {
		this.destination = destination;
	}

	/**
	 * @return the destination
	 */
	public GreenNavAddress getDestination() {
		return destination;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(GreenNavAddress start) {
		this.start = start;
	}

	/**
	 * @return the start
	 */
	public GreenNavAddress getStart() {
		return start;
	}

	/**
	 * @param carType
	 *            the carType to set
	 */
	public void setCarType(String carType) {
		this.carType = carType;
	}

	/**
	 * @return the carType
	 */
	public String getCarType() {
		return carType;
	}

	/**
	 * @param batteryLevel
	 *            the batteryLevel to set
	 */
	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	/**
	 * @return the batteryLevel
	 */
	public int getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * @param routingType
	 *            the routingType to set
	 */
	public void setRoutingType(int routingType) {
		this.routingType = routingType;
	}

	/**
	 * @return the routingType
	 */
	public int getRoutingType() {
		return routingType;
	}
 
	/**
	 * @return the fromAddr
	 */
	public String getFromAddr() {
		if(start!=null){
			return start.toString();
		}
		return null;
		
	}
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the toAdr
	 */
	public String getToAdr() {
		if (destination!=null) {
			return destination.toString();
		}
		return null;
	} 

	/**
	 * @return the fromGeoPoint
	 */
	public GeoPoint getFromGeoPoint() {
		if(start!=null){
			return start.getGeoPoint();
		}
		return null; 
	}
 
	/**
	 * @return the toGeopoint
	 */
	public GeoPoint getToGeopoint() {
		if(destination!=null){
			return destination.getGeoPoint();
		}
		return null;
	}
 
	
	
}

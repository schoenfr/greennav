package org.greennavigation.model;

import org.mapsforge.core.GeoPoint;

/**
 * this class is a subclass of {@link AbstractWayModel}. it presents a
 *         last calculated route .
 */
public class LastCalculatedRouteModel extends AbstractWayModel {
 
	
	
	
	/**
	 * 
	 * @param start
	 * @param destinantion
	 * @param carType
	 * @param batteryLevel
	 * @param routingtype
	 * @param fromAdr
	 * @param toAdr
	 * @param fromGeoPoint
	 * @param toGeopoint
	 */


	public LastCalculatedRouteModel(GreenNavAddress start, GreenNavAddress destinantion,
			String carType, int batteryLevel, int routingtype, String fromAdr,
			String toAdr, GeoPoint fromGeoPoint, GeoPoint toGeopoint) {

		super(start, destinantion, carType, batteryLevel, routingtype);
	}
/**
 * 
 * @param start
 * @param destinantion
 * @param carType
 * @param batteryLevel
 * @param routingtype
 */
	public LastCalculatedRouteModel(GreenNavAddress start, GreenNavAddress destinantion,
			String carType, int batteryLevel, int routingtype) {

		super(start, destinantion, carType, batteryLevel, routingtype);
	}
/**
 * 
 * @param name
 * @param start
 * @param destinantion
 * @param carType
 * @param batteryLevel
 * @param routingtype
 */
	public LastCalculatedRouteModel(String name, GreenNavAddress start,
			GreenNavAddress destinantion, String carType, int batteryLevel,
			int routingtype) {

		super(start, destinantion, carType, batteryLevel, routingtype);
		setName(name);

	}
/**
 * 
 * @param name
 * @param start
 * @param destinantion
 * @param carType
 * @param batteryLevel
 * @param routingtype
 * @param fromAdr
 * @param toAdr
 * @param fromGeoPoint
 * @param toGeopoint
 */
	public LastCalculatedRouteModel(String name, GreenNavAddress start,
			GreenNavAddress destinantion, String carType, int batteryLevel,
			int routingtype, String fromAdr, String toAdr,
			GeoPoint fromGeoPoint, GeoPoint toGeopoint) {
		super(start, destinantion, carType, batteryLevel, routingtype);
		setName(name);
	}


}

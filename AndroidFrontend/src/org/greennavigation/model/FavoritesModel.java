package org.greennavigation.model;

import org.mapsforge.core.GeoPoint;

/**
 *         this class is a subclass of {@link AbstractWayModel}. it presents a
 *         favorite way.
 */
public class FavoritesModel extends AbstractWayModel {

	 

	public FavoritesModel(GreenNavAddress start, GreenNavAddress destinantion,
			String carType, int batteryLevel, int routingtype, String fromAdr,
			String toAdr, GeoPoint fromGeoPoint, GeoPoint toGeopoint) {

		super(start, destinantion, carType, batteryLevel, routingtype);
	}

	public FavoritesModel(GreenNavAddress start, GreenNavAddress destinantion,
			String carType, int batteryLevel, int routingtype) {

		super(start, destinantion, carType, batteryLevel, routingtype);
	}

	public FavoritesModel(String name, GreenNavAddress start,
			GreenNavAddress destinantion, String carType, int batteryLevel,
			int routingtype) {

		super(start, destinantion, carType, batteryLevel, routingtype);
		setName(name);

	}

	public FavoritesModel(String name, GreenNavAddress start,
			GreenNavAddress destinantion, String carType, int batteryLevel,
			int routingtype, String fromAdr, String toAdr,
			GeoPoint fromGeoPoint, GeoPoint toGeopoint) {
		super(start, destinantion, carType, batteryLevel, routingtype);
		setName(name);
	}

	public FavoritesModel() {
		// TODO Auto-generated constructor stub
	}

	 
}

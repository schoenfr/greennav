package org.greennavigation.model;

import java.io.Serializable;

import org.greennavigation.services.RequestManager;
import org.mapsforge.core.GeoPoint;

import android.content.Context;

/**
 * presents a request model that will be used in {@link RequestManager}
 */
public class RequestModel implements Serializable {

	public String id;
	public String feature;
	public String vehicleType;
	private String batteryStatus;
	public GeoPoint start;

	private String energy;
	public GeoPoint destination;
    private GeoPoint stopover;
	private static final String nline = "\n";
/**
 * 
 * @param resultType
 * @param energy
 * @param start
 * @param destination
 * @param feature
 * @param batteryStatus
 * @param vehicleType
 * @param id
 */
	public RequestModel(Context context,String resultType, String energy,
			GeoPoint start, GeoPoint destination , String feature,
			String batteryStatus, String vehicleType, String id) {
		this.energy = energy;
		this.batteryStatus = batteryStatus;
		this.start = start;
		this.destination = destination;
		this.feature = feature;
		this.vehicleType = vehicleType; 
		this.id = id;
	}
/**
 * 
 * @param resultType
 * @param energy
 * @param start
 * @param destination
 * @param stopover
 * @param feature
 * @param batteryStatus
 * @param vehicleType
 * @param id
 */
	public RequestModel(Context context ,String resultType, String energy,
			GeoPoint start, GeoPoint destination, GeoPoint stopover, String feature,
			String batteryStatus, String vehicleType, String id) {
		
		 
		this(context,resultType, energy, start, destination, feature, batteryStatus, vehicleType, id);
		this.stopover = stopover;
		
		 
	}
	/**
	 * 
	 * @return {@link String}
	 */
	public String getRequestXMLScheme() {
		String xmlSheme = "";

		Double startLatitude = null;
		
		if (start!=null) {
			startLatitude = start.getLatitude();
		}
		
		Double startLongitude = null;
		
		if (getStart()!=null) {
			startLongitude= getStart().getLongitude();
		}
		
		
        Double targetLatitude = null;
		
		if (destination!=null) {
			targetLatitude = destination.getLatitude();
		}
		
		Double targetLongitude = null;
		
		if (destination!=null) {
			targetLongitude= destination.getLongitude();
		}
		
		String header = "<?xml version = \"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ nline;

		String rootStart = "<RoutingRequest>"
				+ nline;
		
		String protocolVersion = "<ProtocolVersion>3.0</ProtocolVersion>";
		
		String startNode = "<StartNode>" + nline 
				+ "<Latitude>" + Double.toString(startLatitude) + "</Latitude>" + nline 
				+ "<Longitude>"+ Double.toString(startLongitude) + "</Longitude>" + nline
				+ "</StartNode>" + nline;

		String targetNode = "<TargetNode>" + nline
				+ "<Latitude>" + Double.toString(targetLatitude) + "</Latitude>" + nline
				+ "<Longitude>" + Double.toString(targetLongitude) + "</Longitude>" + nline 
				+ "</TargetNode>" + nline;

		String vehicleType = "<VehicleType>Smart Roadster</VehicleType>" + nline;

		String batteryStatus = "<BatteryStatus>"
				+ this.batteryStatus + "</BatteryStatus>" + nline;

		String optimization = "<Optimization>" + this.energy
				+ "</Optimization>" + nline;

		String rootEnd = "</RoutingRequest>";
		
			xmlSheme = header + "" + rootStart + "" + protocolVersion + "" + startNode + "" + targetNode + "" + vehicleType + "" + batteryStatus + "" + optimization + "" + rootEnd;
		return xmlSheme;
	}

	/**
	 * @return the start
	 */
	public GeoPoint getStart() {
		return this.start;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(GeoPoint destination) {
		this.destination = destination;
	}


	/**
	 * @return the stopover
	 */
	public GeoPoint getStopover() {
		return stopover;
	}
	public String getId() {
		return id;
	}
	public String getFeature() {
		return feature;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public String getBatteryStatus() {
		return batteryStatus;
	}
	public String getEnergy() {
		return energy;
	}
}

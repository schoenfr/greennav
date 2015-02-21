package org.greennavigation.services;

import java.io.IOException;
import java.util.LinkedList;

import org.greennavigation.model.RequestModel;
import org.greennavigation.model.ResponseRouteModel;
import org.greennavigation.model.RouteNode;
import org.greennavigation.model.TurnDirection;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.mapsforge.core.GeoPoint;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

public class RequestManager {

	private static RequestManager instance;

	private static final String METHOD_NAME = "route";
	private static final String NAMESPACE = "http://wsserver.greennav/";
	private static final String SOAP_ACTION = "\"" + NAMESPACE + METHOD_NAME
			+ "\"";
	// TODO url anpassen
	private static final String URL = "http://10.0.2.2:8080/GreenNavService?wsdl";

	/**
	 * This method requests a route from the GreenNav-Server
	 * @param context the context
	 * @param requestModel the request information
	 * @return a ResponseRouteModel including the route
	 */
	public static ResponseRouteModel sendRequestToServer(Context context,
			RequestModel requestModel) {
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		
		// Set properties of the SOAP request
		SoapObject arg0 = new SoapObject("", "arg0");
		arg0.addProperty("Algorithm", "Energy Dijkstra of TUM")
				.addPropertyIfValue("BatteryStatus",
						requestModel.getBatteryStatus())
				.addProperty("Optimization", "energy")
				.addProperty("Payload", "0.0");
		// Sub-object for route-start
		SoapObject startNode = new SoapObject("", "StartNode");
		startNode.addProperty("Latitude",
				Double.toString(requestModel.getStart().getLatitude()));
		startNode.addProperty("Longitude",
				Double.toString(requestModel.getStart().getLongitude()));
		// Sub-object for route-end
		arg0.addProperty("StartNode", startNode);
		SoapObject targetNode = new SoapObject("", "TargetNode");

		targetNode.addProperty("Latitude",
				Double.toString(requestModel.destination.getLatitude()));
		targetNode.addProperty("Longitude",
				Double.toString(requestModel.destination.getLongitude()));

		arg0.addProperty("TargetNode", targetNode);
		arg0.addProperty("VehicleType", requestModel.vehicleType);
		ResponseRouteModel responseRoute = null;

		request.addProperty("arg0", arg0);
		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		soapEnvelope.dotNet = false;
		soapEnvelope.implicitTypes = true;
		soapEnvelope.setOutputSoapObject(request);

		HttpTransportSE aht = new HttpTransportSE(URL, 20000);
		aht.debug = true;

		// Send request
		try {
			aht.call(SOAP_ACTION, soapEnvelope);
		} catch (IOException e) {
			Log.e("SOAP", e + "");
			Log.e("SOAP", "SocketTimeOUT!?");
		} catch (XmlPullParserException e) {
			Log.e("SOAP",
					String.valueOf(e.getLineNumber()) + e.getLocalizedMessage());
		}
		SoapObject result = null;
		// Handle response
		try {
			result = (SoapObject) soapEnvelope.getResponse();
			Log.i("SOAP", result + "");
			// Get routing information from response
			responseRoute = parseRouteModel(result, context);
		} catch (SoapFault e) {
			Log.e("SOAP", e + "");
		} catch (NullPointerException e){
			Log.e("Model", e + "");
		}
		return responseRoute;
	}

	private RequestManager() {
	}

	public static RequestManager getInstance() {
		if (instance == null) {
			instance = new RequestManager();

		}
		return instance;

	}

	/**
	 * This Method extracts the necessary data from a SoapObject obtained from
	 * the routing webservice and stores it into a ResponseRouteModel.
	 * 
	 * @param response
	 *            the SoapObject obtained through the webservice
	 * @param ctx
	 *            the context
	 * @return a ResponseRouteModel containing the routing data
	 */
	private static ResponseRouteModel parseRouteModel(SoapObject response,
			Context ctx) {
		ResponseRouteModel route = new ResponseRouteModel(ctx);
		LinkedList<RouteNode> nodes = new LinkedList<RouteNode>();
		// Iterate over Properties, extracting the necessary information for
		// the ResponseRouteModel
		for (int i = 0; i < response.getPropertyCount(); i++) {
			PropertyInfo responseInfo = new PropertyInfo();
			response.getPropertyInfo(i, responseInfo);
			if (responseInfo.getName().compareTo("BatteryStatus") == 0) {
				route.setBatteryCharge(response.getPropertyAsString(i));
			} else if (responseInfo.getName().compareTo("Distance") == 0) {
				route.setDistance(response.getPropertyAsString(i));
			} else if (responseInfo.getName().compareTo("Route") == 0) {
				SoapObject soapNode = (SoapObject) response.getProperty(i);
				// Iterate over Node properties to create a RouteNode
				double longitude = 0.0;
				double latitude = 0.0;
				String street = "";
				TurnDirection turn = TurnDirection.STRAIGHT;
				for (int j = 0; j < soapNode.getPropertyCount(); j++) {
					PropertyInfo nodeInfo = new PropertyInfo();
					soapNode.getPropertyInfo(j, nodeInfo);
					if (nodeInfo.getName().compareTo("Longitude") == 0) {
						longitude = Double.parseDouble(soapNode
								.getPropertyAsString(j));
					} else if (nodeInfo.getName().compareTo("Latitude") == 0) {
						latitude = Double.parseDouble(soapNode
								.getPropertyAsString(j));
					} else if (nodeInfo.getName().compareTo("NextStreet") == 0) {
						street = soapNode.getPropertyAsString(j);
					} else if (nodeInfo.getName().compareTo("Turn") == 0) {
						turn = TurnDirection.parseTurnDirection(soapNode
								.getPropertyAsString(j));
					}
				}
				nodes.add(new RouteNode(new GeoPoint(latitude, longitude),
						street, turn));
			} else if (responseInfo.getName().compareTo("Time") == 0) {
				route.time = response.getPropertyAsString(i);
			}
		}
		route.setRoute(nodes);
		route.start = nodes.get(0).getLocation().toString();
		route.destination = nodes.getLast().getLocation().toString();
		return route;
	}
}

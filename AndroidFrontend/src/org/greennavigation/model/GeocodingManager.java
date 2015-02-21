package org.greennavigation.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * GeocodingManager offers methods to create and validate relations between
 * geocoordinates and adress strings.<br>
 * The nominatim service which is used by this class, gets a specific address string which is resolved to geocoordinates. <br>
 * <i>NOTE:</i> Nominatim is searching just within a defined viewbox (currently set to region 'Allgäu')
 */
public class GeocodingManager {

	private final String TAG = "GN";
	private final String viewbox = "7.95,49.37,14.99,46.87";
	private final String urlBaseSearch = "http://nominatim.openstreetmap.org/search?format=json&addressdetails=1&viewbox="
			+ viewbox + "&q=";
	private final String KEY_LON = "lon";
	private final String KEY_LAT = "lat";
	private final String KEY_ADDRESS = "address";
	private final String KEY_ROAD = "road";
	private final String KEY_CITY = "city";
	private final String KEY_COUNTRY = "country";
	private final String KEY_COUNTY = "county";
	private final String KEY_STATE = "state";
	private final String KEY_POSTCODE = "postcode";
	private final String KEY_HOUSE_NO = "house_number";

	LocationManager lm;
	boolean gps_enabled = false;
	boolean network_enabled = false;
	Timer timer;
	LocationResult locationResult;
	public ArrayList<GreenNavAddress> proposedAdresses;
	private Context context;

	/**
	 * Public constructor for GeocodingManager. Needs a context to work on.
	 * 
	 * @param context
	 *            - the context to work on
	 */
	public GeocodingManager(Context context) {
		this.context = context;
	}

	/**
	 * Returns a {@link ArrayList} of possible addresses which could be valid
	 * depending on the given string.
	 * 
	 * @param addressString
	 *            - the string that represents the address
	 * @return - an {@link ArrayList} which contain possible address candidates.
	 * @throws IllegalArgumentException
	 *             - thrown when the given address string is null
	 * @throws ConnectException
	 *             - thrown if no connection could be established
	 * @throws ClientProtocolException
	 *             - thrown if no connection could be established
	 */
	public ArrayList<GreenNavAddress> getAddressCandidates(String addressString)
			throws IllegalArgumentException, ConnectException,
			ClientProtocolException {

		ArrayList<GreenNavAddress> resultList = new ArrayList<GreenNavAddress>();

		if (addressString == null)
			throw new IllegalArgumentException(
					"Parameter addressString MUST NOT be null!");
		String encodedString = URLEncoder.encode(addressString);
		String complete = urlBaseSearch + encodedString;
		ArrayList<JSONObject> jsonObjectList = parseJSONString(getJSONString(complete));
		if (jsonObjectList == null) {
			Log.i("GN", "Nominatim found no potential adresses!");
			throw new ConnectException();
		}
		Log.i("GN", "Nominatim found " + jsonObjectList.size()
				+ " results for potential adresses!");
		for (JSONObject jsonObject : jsonObjectList) {
			try {
				double lat = jsonObject.getDouble(KEY_LAT);
				double lon = jsonObject.getDouble(KEY_LON);

				// GreenNavAddress greenNavAddress = getNominatimAddress(lat,
				// lon);
				JSONObject jsonObjectAddress = jsonObject
						.getJSONObject(KEY_ADDRESS);
				GreenNavAddress greenNavAddress = createAddress(
						jsonObjectAddress, lat, lon);

				resultList.add(greenNavAddress);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return resultList;
	}

	/**
	 * Creates addresses out of an given JSONObject - which should contain
	 * address details - and a longitude and latitude.
	 */
	private GreenNavAddress createAddress(JSONObject jsonObjectAddress,
			double lat, double lon) {
		String street = null;
		try {
			street = jsonObjectAddress.getString(KEY_ROAD);
		} catch (JSONException e) {
			Log.v(TAG, "Couldn't find string of key: " + KEY_ROAD);
		}
		String houseNo = null;
		try {
			houseNo = jsonObjectAddress.getString(KEY_HOUSE_NO);
		} catch (JSONException e) {
			Log.v(TAG, "Couldn't find string of key: " + KEY_HOUSE_NO);
		}
		String postcode = null;
		try {
			postcode = jsonObjectAddress.getString(KEY_POSTCODE);
		} catch (JSONException e) {
			Log.v(TAG, "Couldn't find string of key: " + KEY_POSTCODE);
		}
		String country = null;
		try {
			country = jsonObjectAddress.getString(KEY_COUNTRY);
		} catch (JSONException e) {
			Log.v(TAG, "Couldn't find string of key: " + KEY_COUNTRY);
		}
		String city = null;
		try {
			city = jsonObjectAddress.getString(KEY_CITY);
		} catch (JSONException e) {
			Log.v(TAG, "Couldn't find string of key: " + KEY_CITY);
			try {
				city = jsonObjectAddress.getString(KEY_COUNTY);
			} catch (Exception e2) {
				Log.v(TAG, "Couldn't find string of key: " + KEY_COUNTY);
			}
		}
		String state = null;
		try {
			state = jsonObjectAddress.getString(KEY_STATE);
		} catch (JSONException e) {
			Log.v(TAG, "Couldn't find String of key: " + KEY_STATE);
		}
		Log.i(TAG, "Found address details from Nominatim reverse search: "
				+ street + ", " + houseNo + ", " + city + ", " + postcode
				+ ", " + country + ", " + state);
		if (state != null) {
			return new GreenNavAddress(lat, lon, street, houseNo, city,
					postcode, country, state);
		} else {
			return new GreenNavAddress(lat, lon, street, houseNo, city,
					postcode, country);
		}
	}

	/**
	 * Returns a string that represents a JSON object or array from a given URL.
	 * 
	 * @param url
	 *            - the url the JSON object or array is to fetch from.
	 * @return - a string representing the JSON object or array.
	 * @throws ClientProtocolException
	 *             - thrown when a connection could not be established
	 * @throws ConnectException
	 *             - thrown when a connection could not be established
	 */
	public String getJSONString(String url) throws ClientProtocolException,
			ConnectException {
		Log.i("GN", url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;

		String result = null;

		try {
			response = httpClient.execute(httpGet);

			int status = response.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK) {
				Log.e(TAG, "Status " + status + ": "
						+ response.getStatusLine().toString());
				throw new ConnectException("HTTP status: "
						+ Integer.toString(status));
			}

			HttpEntity entity = response.getEntity();

			if (entity != null) {

				InputStream instream = null;
				try {
					instream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));
					result = reader.readLine();
				} catch (IOException e) {
					Log.e("GN", e.getMessage());
				} finally {
					if (instream != null)
						try {
							instream.close();
						} catch (IOException e) {
							Log.e("GN", e.getMessage());
						}
				}

			}

		} catch (IOException e) {
			Log.e("GN", e.getMessage());
		} finally {
			httpGet.abort();
		}
		return result;
	}

	/**
	 * Uses to parse JSON object from an string that represents a JSON array.
	 * 
	 * @param jsonString
	 *            - a string representing the JSON array to parse
	 * @return - a {@link ArrayList} which contains all JSON object of the given
	 *         JSON array.
	 */
	private ArrayList<JSONObject> parseJSONString(String jsonString) {
		ArrayList<JSONObject> jsonObjectList = null;
		if (jsonString != null) {
			JSONTokener tokener = new JSONTokener(jsonString);
			jsonObjectList = new ArrayList<JSONObject>();

			while (tokener.more()) {
				Object o = null;
				try {
					o = tokener.nextValue();
				} catch (JSONException e) {
					Log.e(TAG, "Tokener has no next value.");
				}
				if (o instanceof JSONArray) {
					JSONArray array = (JSONArray) o;
					for (int i = 0; i < array.length(); ++i) {
						JSONObject jsonObject = null;
						try {
							jsonObject = array.getJSONObject(i);
						} catch (JSONException e) {
							Log.e(TAG, e.getMessage());
						}
						jsonObjectList.add(jsonObject);
					}
				} else {
					Log.e(TAG, "Couldn't recognize token: " + o.getClass());
				}

			}
		}
		return jsonObjectList;
	}

	/**
	 * Returns the current position or the last known position via the interface
	 * {@link LocationResult#gotLocation(Location)} of the given class instance
	 * {@link LocationResult}.
	 * 
	 * @param result
	 *            - the class instance to call the method
	 *            {@link LocationResult#gotLocation(Location)} when a location
	 *            has been resolved
	 * @return - true when a location could be found; otherwise false.
	 * @throws Exception
	 *             - throws an exception is no location provider (whether gps
	 *             nor network) is enabled.
	 */
	public boolean getLocation(LocationResult result) throws Exception {
		locationResult = result;
		if (lm == null)
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled)
			throw new Exception();

		if (gps_enabled)
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					locationListenerGps);
		if (network_enabled)
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListenerNetwork);

		timer = new Timer();
		timer.schedule(new GetLastLocation(), 20000);

		return true;

	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);
		}

		// unnecessary
		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			timer.purge();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);
		}

		// unnecessary
		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/**
	 * Timer to remove update from location Manager.
	 * 
	 * @author ME
	 * 
	 */
	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			lm.removeUpdates(locationListenerGps);
			lm.removeUpdates(locationListenerNetwork);
		}
	}

	/**
	 * Public interface for an LocationResult. The only method
	 * {@link LocationResult#gotLocation(Location)} is called, when a location
	 * result has been found.
	 * 
	 * @author ME
	 * 
	 */
	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}

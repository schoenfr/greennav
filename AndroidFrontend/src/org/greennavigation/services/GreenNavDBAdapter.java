package org.greennavigation.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.greennavigation.model.FavoritesModel;
import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.ui.R;
import org.mapsforge.core.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Adapter class to interact with the GreenNav database.
 */
public class GreenNavDBAdapter {
	// Database parameter
	private final static String MY_DATABASE_NAME = "GN";
	private final static String MY_DATABASE_TABLE_FAVORITES = "TABLE_Favorites";
	private final static String MY_DATABASE_TABLE_LAST_REQUEST = "TABLE_Requests";
	private static final int DATABASE_VERSION = 1;

	// Table parameter
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "Name";
	public static final String KEY_IS_START_EXISTING = "isStartExisting";
	public static final String KEY_FROM_ADDR_STREET = "FromAddrStreet";
	public static final String KEY_FROM_ADDR_HNR = "FromAddrHNR";
	public static final String KEY_FROM_ADDR_POSTCODE = "FromAddrPC";
	public static final String KEY_FROM_ADDR_CITY = "FromAddrCity";
	public static final String KEY_FROM_ADDR_COUNTRY = "FromAddrCtry";
	public static final String KEY_FROM_ADDR_FL = "FromAddrFL";
	public static final String KEY_IS_DESTINATION_EXISTING = "isDestExisting";
	public static final String KEY_TO_ADDR_STREET = "ToAddrStreet";
	public static final String KEY_TO_ADDR_HNR = "ToAddrHNR";
	public static final String KEY_TO_ADDR_POSTCODE = "ToAddrPC";
	public static final String KEY_TO_ADDR_CITY = "ToAddrCity";
	public static final String KEY_TO_ADDR_COUNTRY = "ToAddrCtry";
	public static final String KEY_TO_ADDR_FL = "ToAddrFL";

	public static final String KEY_FROM_LON = "FromLon";
	public static final String KEY_FROM_LAT = "FromLat";
	public static final String KEY_TO_LON = "ToLon";
	public static final String KEY_TO_LAT = "ToLat";
	public static final String KEY_CAR = "Car";
	public static final String KEY_BATTERY = "Battery";
	public static final String KEY_RTYPE = "RType";

	public static final String KEY_FROM_ADDR_STRING = "FromAddrString";
	public static final String KEY_TO_ADDR_STRING = "ToAddrString";

	private static SQLiteDatabase db;
	private DatabaseHelper DBHelper;
	private Context context;

	/**
	 * Public constructor to create a database adapter for the GreenNav
	 * database.
	 * 
	 * @param ctx
	 */
	public GreenNavDBAdapter(Context ctx) {
		this.context = ctx;
		this.DBHelper = new DatabaseHelper(context);
	}

	/**
	 * Inner class that helps to handle initialization of the database. It
	 * creates - if necessary - the tables within the database.
	 */
	class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, MY_DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * Called when activity is launched for the first time. When necessary
		 * it creates both tables in the database. The created tables are the
		 * following:<br>
		 * <br>
		 * 
		 * <b>TABLE_Requests:</b><br>
		 * <table border="1">
		 * <tr align="center">
		 * <td><b><u>Name:</u></b></td>
		 * <td><b>_id</b></td>
		 * <td><b>FromAddrString</b></td>
		 * <td><b>FromLon</b></td>
		 * <td><b>FromLat</b></td>
		 * <td><b>ToAddrString</b></td>
		 * <td><b>ToLon</b></td>
		 * <td><b>ToLat</b></td>
		 * </tr>
		 * <tr>
		 * <td><b><u>Datatype:</u></b></td>
		 * <td>INTEGER (auto-increment)</td>
		 * <td>TEXT (String)</td>
		 * <td>REAL (Double)</td>
		 * <td>REAL (Double)</td>
		 * <td>TEXT (String)</td>
		 * <td>REAL (Double)</td>
		 * <td>REAL (Double)</td>
		 * </tr>
		 * </table>
		 * <br>
		 * <br>
		 * <b>TABLE_Favorites:</b><br>
		 * <table border="1">
		 * <tr align="center">
		 * <td><b><u>Name:</u></b></td>
		 * <td><b>_id</b></td>
		 * <td><b>Name</b></td>
		 * <td><b>isStartExisting</b></td>
		 * <td><b>FromAddrStreet</b></td>
		 * <td><b>FromAddrHNR</b></td>
		 * <td><b>FromAddrPC</b></td>
		 * <td><b>FromAddrCity</b></td>
		 * <td><b>FromAddrCtry</b></td>
		 * <td><b>FromAddrFL</b></td>
		 * <td><b>isDestExisting</b></td>
		 * <td><b>ToAddrStreet</b></td>
		 * <td><b>ToAddrHNR</b></td>
		 * <td><b>ToAddrPC</b></td>
		 * <td><b>ToAddrCity</b></td>
		 * <td><b>ToAddrCtry</b></td>
		 * <td><b>ToAddrFL</b></td>
		 * <td><b>FromLon</b></td>
		 * <td><b>FromLat</b></td>
		 * <td><b>ToLon</b></td>
		 * <td><b>ToLat</b></td>
		 * <td><b>Car</b></td>
		 * <td><b>Battery</b></td>
		 * <td><b>RType</b></td>
		 * </tr>
		 * <tr>
		 * <td><b><u>Datatype:</u></b></td>
		 * <td>INTEGER (auto-increment)</td>
		 * <td>TEXT (String)</td>
		 * <td>BOOLEAN</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>BOOLEAN</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>TEXT (String)</td>
		 * <td>REAL (Double)</td>
		 * <td>REAL (Double)</td>
		 * <td>REAL (Double)</td>
		 * <td>REAL (Double)</td>
		 * <td>TEXT (String)</td>
		 * <td>INTEGER</td>
		 * <td>INTEGER(1)</td>
		 * </tr>
		 * </table>
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE " + MY_DATABASE_TABLE_LAST_REQUEST + " ( "
					+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_FROM_ADDR_STRING + " TEXT," + KEY_FROM_LON + " REAL,"
					+ KEY_FROM_LAT + " REAL," + KEY_TO_ADDR_STRING + " TEXT,"
					+ KEY_TO_LON + " REAL," + KEY_TO_LAT + " REAL" + ");");
			if (db != null) {
				Log.i("GN", "Found DB and table: " + MY_DATABASE_NAME + " ("
						+ MY_DATABASE_TABLE_LAST_REQUEST + ")");
			}
			db.execSQL("CREATE TABLE " + MY_DATABASE_TABLE_FAVORITES + " ( "
					+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_NAME + " TEXT," + KEY_IS_START_EXISTING + " BOOLEAN,"
					+ KEY_FROM_ADDR_STREET + " TEXT," + KEY_FROM_ADDR_HNR
					+ " TEXT," + KEY_FROM_ADDR_POSTCODE + " TEXT,"
					+ KEY_FROM_ADDR_CITY + " TEXT," + KEY_FROM_ADDR_COUNTRY
					+ " TEXT," + KEY_FROM_ADDR_FL + " TEXT,"
					+ KEY_IS_DESTINATION_EXISTING + " BOOLEAN,"
					+ KEY_TO_ADDR_STREET + " TEXT," + KEY_TO_ADDR_HNR
					+ " TEXT," + KEY_TO_ADDR_POSTCODE + " TEXT,"
					+ KEY_TO_ADDR_CITY + " TEXT," + KEY_TO_ADDR_COUNTRY
					+ " TEXT," + KEY_TO_ADDR_FL + " TEXT," + KEY_FROM_LON
					+ " REAL," + KEY_FROM_LAT + " REAL," + KEY_TO_LON
					+ " REAL," + KEY_TO_LAT + " REAL," + KEY_CAR + " TEXT,"
					+ KEY_BATTERY + " INTEGER," + KEY_RTYPE + " INTEGER(1)"
					+ ");");
			if (db != null) {
				Log.i("GN", "Found DB and table: " + MY_DATABASE_NAME + " ("
						+ MY_DATABASE_TABLE_FAVORITES + ")");
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	/**
	 * Returns an writable instance of the database to work on
	 * 
	 * @return a writable instance of the database
	 * @throws SQLException
	 *             - if the database cannot be opened for writing
	 */
	public GreenNavDBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the database.
	 */
	public void close() {
		DBHelper.close();
	}

	/**
	 * Insert a new favorite into the corresponding database.
	 * 
	 * @param favoritesModel
	 *            - the {@link FavoritesModel} to store in database
	 * @return the database id to identify stored entry
	 */
	public long insertFavorite(FavoritesModel favoritesModel) {
		Log.i("GreenNavDBAdapter", "insertFavorite()");
		db.beginTransaction();
		ContentValues initialValues = new ContentValues();

		try {
			initialValues.putNull(KEY_ROWID);
			initialValues.put(KEY_NAME, favoritesModel.getName());

			if (!favoritesModel.getStart().isEmpty()) {
				initialValues.put(KEY_IS_START_EXISTING, true);
				initialValues.put(KEY_FROM_LON, favoritesModel
						.getFromGeoPoint().getLongitude());
				initialValues.put(KEY_FROM_LAT, favoritesModel
						.getFromGeoPoint().getLatitude());
				initialValues.put(KEY_FROM_ADDR_STREET, favoritesModel
						.getStart().getStreet());
				initialValues.put(KEY_FROM_ADDR_HNR, favoritesModel.getStart()
						.getHouseNumber());
				initialValues.put(KEY_FROM_ADDR_POSTCODE, favoritesModel
						.getStart().getPostcode());
				initialValues.put(KEY_FROM_ADDR_CITY, favoritesModel.getStart()
						.getCity());
				initialValues.put(KEY_FROM_ADDR_COUNTRY, favoritesModel
						.getStart().getCountry());
				initialValues.put(KEY_FROM_ADDR_FL, favoritesModel.getStart()
						.getFederalLand());
			} else {
				initialValues.put(KEY_IS_START_EXISTING, false);
				initialValues.putNull(KEY_FROM_LON);
				initialValues.putNull(KEY_FROM_LAT);
				initialValues.putNull(KEY_FROM_ADDR_STREET);
				initialValues.putNull(KEY_FROM_ADDR_HNR);
				initialValues.putNull(KEY_FROM_ADDR_POSTCODE);
				initialValues.putNull(KEY_FROM_ADDR_CITY);
				initialValues.putNull(KEY_FROM_ADDR_COUNTRY);
				initialValues.putNull(KEY_FROM_ADDR_FL);
			}
			if (!favoritesModel.getDestination().isEmpty()) {
				initialValues.put(KEY_IS_DESTINATION_EXISTING, true);
				initialValues.put(KEY_TO_LON, favoritesModel.getToGeopoint()
						.getLongitude());
				initialValues.put(KEY_TO_LAT, favoritesModel.getToGeopoint()
						.getLatitude());
				initialValues.put(KEY_TO_ADDR_STREET, favoritesModel
						.getDestination().getStreet());
				initialValues.put(KEY_TO_ADDR_HNR, favoritesModel
						.getDestination().getHouseNumber());
				initialValues.put(KEY_TO_ADDR_POSTCODE, favoritesModel
						.getDestination().getPostcode());
				initialValues.put(KEY_TO_ADDR_CITY, favoritesModel
						.getDestination().getCity());
				initialValues.put(KEY_TO_ADDR_COUNTRY, favoritesModel
						.getDestination().getCountry());
				initialValues.put(KEY_TO_ADDR_FL, favoritesModel
						.getDestination().getFederalLand());
			} else {
				initialValues.put(KEY_IS_DESTINATION_EXISTING, false);
				initialValues.putNull(KEY_TO_LON);
				initialValues.putNull(KEY_TO_LAT);
				initialValues.putNull(KEY_TO_ADDR_STREET);
				initialValues.putNull(KEY_TO_ADDR_HNR);
				initialValues.putNull(KEY_TO_ADDR_POSTCODE);
				initialValues.putNull(KEY_TO_ADDR_CITY); 
				initialValues.putNull(KEY_TO_ADDR_COUNTRY);
				initialValues.putNull(KEY_TO_ADDR_FL);
			}
			initialValues.put(KEY_CAR, favoritesModel.getCarType());
			initialValues.put(KEY_BATTERY, favoritesModel.getBatteryLevel());
			initialValues.put(KEY_RTYPE, favoritesModel.getRoutingType());
			if (db != null) {
				Log.i("GN", "DB found. Insert values.");
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("GN", "Error while storing favorite: " + e.getMessage());
		} finally {
			db.endTransaction();
		}
		return db.insert(MY_DATABASE_TABLE_FAVORITES, null, initialValues);
	}

	/**
	 * Returns a Cursor, which holds all favorites from database.
	 * 
	 * @return a Cursor, holding all favorites from database.
	 */
	public Cursor getAllFavorites() {
		return db.query(MY_DATABASE_TABLE_FAVORITES, null, null, null, null,
				null, null);
	}

	/**
	 * Returns a list of all {@link FavoritesModel} stored in the database.
	 * 
	 * @return a {@link ArrayList} holding all {@link FavoritesModel} from
	 *         database
	 */
	public ArrayList<FavoritesModel> getAllFavoritesAsList() {
		Cursor c = getAllFavorites();

		ArrayList<FavoritesModel> list = new ArrayList<FavoritesModel>();

		while (!c.isAfterLast()) {
			list.add(getFavoriteFromCursor(c));
			c.moveToNext();
		}
		c.close();
		return list;

	}

	/**
	 * Deletes a favorite from database.
	 * 
	 * @param id
	 *            - the database id to identify the favorite
	 */
	public void deleteFavoriteById(long id) {
		db.beginTransaction();
		try {
			db.delete(MY_DATABASE_TABLE_FAVORITES, "_id=" + id, null);
		} catch (Exception e) {
			Log.e("GN", "Error while deleting favorite: " + e.getMessage());
		} finally {
			db.endTransaction();
		}

	}
	
	/** Delete all
	 * 
	 */
	
	public void deleteAll(){
		db.beginTransaction();
		try {
			db.delete(MY_DATABASE_TABLE_FAVORITES, "", null);
		}catch(Exception e) {
			Log.e("GN", "Error while deleting favorites: " + e.getMessage());
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Returns a {@link FavoritesModel} from the database corresponding to the
	 * given database id.
	 * 
	 * @param id
	 *            - the database id to identify the {@link FavoritesModel}
	 * @return
	 */
	public FavoritesModel getFavoriteById(long id) {
		db.beginTransaction();

		Cursor c = null;
		try {
			c = db.rawQuery("SELECT * FROM " + MY_DATABASE_TABLE_FAVORITES
					+ " WHERE " + KEY_ROWID + " = ?", new String[] { String
					.valueOf(id) });
		} catch (Exception e) {
			Log.e("GN", "Error while fetching favorite: " + e.getMessage());
		} finally {
			db.endTransaction();
		}
		return getFavoriteFromCursor(c);
	}

	private FavoritesModel getFavoriteFromCursor(Cursor c) {

		int columnIndexName = c.getColumnIndex(KEY_NAME);

		int columnIndexIsStartExisting = c
				.getColumnIndex(KEY_IS_START_EXISTING);
		int columnIndexFromAddrStreet = c.getColumnIndex(KEY_FROM_ADDR_STREET);
		int columnIndexFromAddrHNR = c.getColumnIndex(KEY_FROM_ADDR_HNR);
		int columnIndexFromAddrPostCode = c
				.getColumnIndex(KEY_FROM_ADDR_POSTCODE);
		int columnIndexFromAddrCity = c.getColumnIndex(KEY_FROM_ADDR_CITY);
		int columnIndexFromAddrCountry = c
				.getColumnIndex(KEY_FROM_ADDR_COUNTRY);
		int columnIndexFromAddrFL = c.getColumnIndex(KEY_FROM_ADDR_FL);
		int columnIndexFromLon = c.getColumnIndex(KEY_FROM_LON);
		int columnIndexFromLat = c.getColumnIndex(KEY_FROM_LAT);

		int columnIndexIsDestinationExisting = c
				.getColumnIndex(KEY_IS_DESTINATION_EXISTING);
		int columnIndexToAddrStreet = c.getColumnIndex(KEY_TO_ADDR_STREET);
		int columnIndexToAddrHNR = c.getColumnIndex(KEY_TO_ADDR_HNR);
		int columnIndexToAddrPostCode = c.getColumnIndex(KEY_TO_ADDR_POSTCODE);
		int columnIndexToAddrCity = c.getColumnIndex(KEY_TO_ADDR_CITY);
		int columnIndexToAddrCountry = c.getColumnIndex(KEY_TO_ADDR_COUNTRY);
		int columnIndexToAddrFL = c.getColumnIndex(KEY_TO_ADDR_FL);

		int columnIndexToLon = c.getColumnIndex(KEY_TO_LON);
		int columnIndexToLat = c.getColumnIndex(KEY_TO_LAT);
		int columnIndexCar = c.getColumnIndex(KEY_CAR);
		int columnIndexRType = c.getColumnIndex(KEY_RTYPE);
		int columnIndexBattery = c.getColumnIndex(KEY_BATTERY);

		if (c.isBeforeFirst() || c.isAfterLast())
			c.moveToFirst();

		String name = c.getString(columnIndexName);

		boolean fromAddrIsExisting = c.getInt(columnIndexIsStartExisting) == 1 ? true
				: false;
		String fromAddrStreet = c.getString(columnIndexFromAddrStreet);
		String fromAddrHNR = c.getString(columnIndexFromAddrHNR);
		String fromAddrPostcode = c.getString(columnIndexFromAddrPostCode);
		String fromAddrCity = c.getString(columnIndexFromAddrCity);
		String fromAddrCountry = c.getString(columnIndexFromAddrCountry);
		String fromAddrFL = c.getString(columnIndexFromAddrFL);
		double fromLon = c.getDouble(columnIndexFromLon);
		double fromLat = c.getDouble(columnIndexFromLat);

		boolean toAddrIsExisting = c.getInt(columnIndexIsDestinationExisting) == 1 ? true
				: false;
		String toAddrStreet = c.getString(columnIndexToAddrStreet);
		String toAddrHNR = c.getString(columnIndexToAddrHNR);
		String toAddrPostcode = c.getString(columnIndexToAddrPostCode);
		String toAddrCity = c.getString(columnIndexToAddrCity);
		String toAddrCountry = c.getString(columnIndexToAddrCountry);
		String toAddrFL = c.getString(columnIndexToAddrFL);
		double toLon = c.getDouble(columnIndexToLon);
		double toLat = c.getDouble(columnIndexToLat);

		String car = c.getString(columnIndexCar);
		int rType = c.getInt(columnIndexRType);
		int battery = c.getInt(columnIndexBattery);

		GreenNavAddress startGNA = null;
		GreenNavAddress destGNA = null;

		if (fromAddrIsExisting) {
			startGNA = new GreenNavAddress(fromLat, fromLon, fromAddrStreet,
					fromAddrHNR, fromAddrCity, fromAddrCountry,
					fromAddrPostcode, fromAddrFL);
		} else {
			startGNA = new GreenNavAddress();
		}
		if (toAddrIsExisting) {
			destGNA = new GreenNavAddress(toLat, toLon, toAddrStreet,
					toAddrHNR, toAddrCity, toAddrPostcode, toAddrCountry,
					toAddrFL);
		} else {
			destGNA = new GreenNavAddress();
		}
		return new FavoritesModel(name, startGNA, destGNA, car, battery, rType);
	}

	/**
	 * Insert the last send requests into the corresponding database.
	 * 
	 * @param fromAddrString
	 *            - a string that represents the start address.
	 * @param toAddrString
	 *            - a string that represents the destination address.
	 * @param fromGP
	 *            - a {@link GeoPoint} that holds the coordinates of the start
	 *            address.
	 * @param toGP
	 *            - a {@link GeoPoint} that holds the coordinates of the
	 *            destination address.
	 * @return
	 */
	public long insertLastRequest(String fromAddrString, String toAddrString,
			GeoPoint fromGP, GeoPoint toGP) {
		Log.i("GreenNavDBAdapter", "insertLastRequest()");
		open(); 
		db.beginTransaction();
		deleteLastRequests();
		ContentValues initialValues = new ContentValues();
		try {

			initialValues.putNull(KEY_ROWID);

			initialValues.put(KEY_FROM_ADDR_STRING, fromAddrString);
			initialValues.put(KEY_FROM_LAT, fromGP.getLatitude());
			initialValues.put(KEY_FROM_LON, fromGP.getLongitude());

			if (toAddrString != null) {
				initialValues.put(KEY_TO_ADDR_STRING, toAddrString);
				initialValues.put(KEY_TO_LAT, toGP.getLatitude());
				initialValues.put(KEY_TO_LON, toGP.getLongitude());
			} else {
				initialValues.putNull(KEY_TO_ADDR_STRING);
				initialValues.putNull(KEY_TO_LAT);
				initialValues.putNull(KEY_TO_LON);
			}
			if (db != null) {
				Log.i("GN", "DB found. Insert values.");
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("GN", "Error while storing last request: " + e.getMessage());
		} finally {
			db.endTransaction();
		}
		
		long id = db.insert(MY_DATABASE_TABLE_LAST_REQUEST, null, initialValues);
		return id;
	}

	/**
	 * Makes a query on the database to return all last requests. The cursor is
	 * sorted descending, to display the latest result on first position.
	 * 
	 * @return - a cursor with latest requests in first position.
	 */
	public Cursor getAllLastRequests() {
		Cursor c = db.query(MY_DATABASE_TABLE_LAST_REQUEST, null, null, null, null,
				null, KEY_ROWID + " DESC");
		return c;
	}

	/**
	 * Deletes the last X request from table 'TABLE_Requests', where X is the
	 * maximum number of stored last request in the database as set in the
	 * 'res/values.xml'.
	 */
	private void deleteLastRequests() {
		int maxEntries = Integer.valueOf(context
				.getString(R.constant.maxLastRequests));

		Cursor c = db.query(MY_DATABASE_TABLE_LAST_REQUEST, null, null, null,
				null, null, null);

		int rowCount = c.getCount();
		if (rowCount >= maxEntries) {
			Log
					.i(getClass().getSimpleName(),
							"Maximum amount of entires has reached. Delete oldest entry.");
			c = db.query(MY_DATABASE_TABLE_LAST_REQUEST, new String[] { "min("
					+ KEY_ROWID + ")" }, null, null, null, null, null);
			c.moveToFirst();
			int rowID = c.getInt(0);
			db.delete(MY_DATABASE_TABLE_LAST_REQUEST, KEY_ROWID + "=" + rowID,
					null);
		}
		c.close();

	}

	/**
	 * Returns a HashMap with all last requests from the database. Herein the
	 * GeoPoints of the last requests are mapped to their database 'id'.
	 * GeoPoints will be an array wherein the first position (0) represents the
	 * coordinates of the start address and the second position (1) represents
	 * the coordinates of the destination address.
	 * 
	 * @return - a HashMap with an GeoPoint[] mapped to their database 'id'.
	 */
	public HashMap<Integer, GeoPoint[]> getAllLastRequestsAsMap() {
		Log.i("GreenNavDBAdapter", "getAllLastRequestsAsMap()");

		HashMap<Integer, GeoPoint[]> map = new HashMap<Integer, GeoPoint[]>();
		Cursor c = getAllLastRequests();

		int columnIndexID = c.getColumnIndex(KEY_ROWID);
		int columnIndexFromLat = c.getColumnIndex(KEY_FROM_LAT);
		int columnIndexFromLon = c.getColumnIndex(KEY_FROM_LON);
		int columnIndexToLat = c.getColumnIndex(KEY_TO_LAT);
		int columnIndexToLon = c.getColumnIndex(KEY_TO_LON);

		c.moveToFirst();
		while (!c.isAfterLast()) {
			GeoPoint gpFrom = new GeoPoint(c.getDouble(columnIndexFromLat), c
					.getDouble(columnIndexFromLon));
			GeoPoint gpTo = new GeoPoint(c.getDouble(columnIndexToLat), c
					.getDouble(columnIndexToLon));
			GeoPoint[] geoPoints = new GeoPoint[] { gpFrom, gpTo };
			map.put(c.getInt(columnIndexID), geoPoints);
			c.moveToNext();
		}
		c.close();
		return map;
	}
}

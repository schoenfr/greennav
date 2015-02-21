package org.greennavigation.services;

import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.ui.R;
import org.greennavigation.ui.activities.FavoritesActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Adapter class to display all stored favorites. It receives a database cursor
 * with all relevant information to display the favorites within a given layout.
 */
public class FavoritesActAdapter extends SimpleCursorAdapter {

	private Cursor gnCursor;
	private Context gnContext;
	private FavoritesActivity gnFavActivity;

	// public static final String KEY_ROWID = "_id";
	// public static final String KEY_NAME = "Name";
	// public static final String KEY_FROM_ADDR = "FromAddr";
	// public static final String KEY_TO_ADDR = "ToAddr";
	// public static final String KEY_CAR = "Car";
	// public static final String KEY_BATTERY = "Battery";
	// public static final String KEY_RTYPE = "RType";

	// private int columnIndexFromAdr;
	// private int columnIndexToAdr;

	// column positions within in the cursor
	private int columnIndexName;
	private int columnIndexIsFromAddrExisting;
	private int columnIndexFromAddrStreet;
	private int columnIndexFromAddrHNR;
	private int columnIndexFromAddrPostCode;
	private int columnIndexFromAddrCity;
	//private int columnIndexFromAddrCountry;
	private int columnIndexFromAddrFL;
	private int columnIndexFromLon;
	private int columnIndexFromLat;
	private int columnIndexIsToAddrExisting;
	private int columnIndexToAddrStreet;
	private int columnIndexToAddrHNR;
	private int columnIndexToAddrPostCode;
	private int columnIndexToAddrCity;
	private int columnIndexToAddrCountry;
	private int columnIndexToAddrFL;
	private int columnIndexToLon;
	private int columnIndexToLat;
	private int columnIndexCar;
	private int columnIndexRType;
	private int columnIndexBattery;
	private int columnIndexID;

	/**
	 * Constructor method of FavoritesActAdapter class. Initializes the class
	 * with all important attributes.
	 * 
	 * @param context
	 *            - the context this adapter is called from.
	 * @param layout
	 *            - <i>passed through to super class</i>
	 * @param c
	 *            - the (database) cursor to receive the data from
	 * @param from
	 *            - <i>passed through to super class</i>
	 * @param to
	 *            - <i>passed through to super class</i>
	 */
	public FavoritesActAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		gnCursor = c;
		gnContext = context;
		gnFavActivity = (FavoritesActivity) context;

		columnIndexID = gnCursor.getColumnIndex(GreenNavDBAdapter.KEY_ROWID);
		columnIndexName = gnCursor.getColumnIndex(GreenNavDBAdapter.KEY_NAME);

		columnIndexIsFromAddrExisting = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_IS_START_EXISTING);
		columnIndexFromAddrStreet = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_ADDR_STREET);
		columnIndexFromAddrHNR = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_ADDR_HNR);
		columnIndexFromAddrPostCode = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_ADDR_POSTCODE);
		columnIndexFromAddrCity = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_ADDR_CITY);
//		columnIndexFromAddrCountry = gnCursor
//				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_ADDR_COUNTRY);
		columnIndexFromAddrFL = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_ADDR_FL);
		columnIndexFromLon = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_LON);
		columnIndexFromLat = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_LAT);
		columnIndexIsToAddrExisting = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_IS_DESTINATION_EXISTING);
		columnIndexToAddrStreet = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_ADDR_STREET);
		columnIndexToAddrHNR = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_ADDR_HNR);
		columnIndexToAddrPostCode = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_ADDR_POSTCODE);
		columnIndexToAddrCity = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_ADDR_CITY);
		columnIndexToAddrCountry = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_ADDR_COUNTRY);
		columnIndexToAddrFL = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_ADDR_FL);
		columnIndexToLon = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_LON);
		columnIndexToLat = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_LAT);

		columnIndexCar = gnCursor.getColumnIndex(GreenNavDBAdapter.KEY_CAR);
		columnIndexRType = gnCursor.getColumnIndex(GreenNavDBAdapter.KEY_RTYPE);
		columnIndexBattery = gnCursor
				.getColumnIndex(GreenNavDBAdapter.KEY_BATTERY);

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		this.gnCursor = cursor;

		TextView tvName = (TextView) view.findViewById(R.id.favorite_name);
		TextView tvFrom = (TextView) view.findViewById(R.id.favorite_from);
		TextView tvTo = (TextView) view.findViewById(R.id.favorite_to);
		TextView tvCar = (TextView) view.findViewById(R.id.favorite_car_type);
		ImageView ivRType = (ImageView) view
				.findViewById(R.id.favorite_request_type);
		TextView tvBattery = (TextView) view
				.findViewById(R.id.favorite_battery_level);
		ImageView ivArrow = (ImageView) view.findViewById(R.id.favorite_arrow);
		ImageView ivDelete = (ImageView) view
				.findViewById(R.id.favorite_delete);

		final long id = gnCursor.getLong(columnIndexID);
		final String favName = gnCursor.getString(columnIndexName);

		final boolean isFromAddrExisting = gnCursor
				.getInt(columnIndexIsFromAddrExisting) == 0 ? false : true;
		final String fromAddrStreet = gnCursor
				.getString(columnIndexFromAddrStreet);
		final String fromAddrHNR = gnCursor.getString(columnIndexFromAddrHNR);
		final String fromAddrPostcode = gnCursor
				.getString(columnIndexFromAddrPostCode);
		final String fromAddrCity = gnCursor.getString(columnIndexFromAddrCity);
//		final String fromAddrCountry = gnCursor
//				.getString(columnIndexFromAddrCountry);
		final String fromAddrFederalland = gnCursor
				.getString(columnIndexFromAddrFL);
		final long fromLon = gnCursor.getLong(columnIndexFromLon);
		final long fromLat = gnCursor.getLong(columnIndexFromLat);

		final boolean isToAddrExisting = gnCursor
				.getInt(columnIndexIsToAddrExisting) == 0 ? false : true;
		final String toAddrStreet = gnCursor.getString(columnIndexToAddrStreet);
		final String toAddrHNR = gnCursor.getString(columnIndexToAddrHNR);
		final String toAddrPostcode = gnCursor
				.getString(columnIndexToAddrPostCode);
		final String toAddrCity = gnCursor.getString(columnIndexToAddrCity);
		final String toAddrCountry = gnCursor
				.getString(columnIndexToAddrCountry);
		final String toAddrFederalland = gnCursor
				.getString(columnIndexToAddrFL);
		final long toLon = gnCursor.getLong(columnIndexToLon);
		final long toLat = gnCursor.getLong(columnIndexToLat);

		final String car = gnCursor.getString(columnIndexCar);
		final int rType = gnCursor.getInt(columnIndexRType);
		final int battery = gnCursor.getInt(columnIndexBattery);

		GreenNavAddress startAddress = new GreenNavAddress(fromLat, fromLon,
				fromAddrStreet, fromAddrHNR, fromAddrCity, fromAddrPostcode,
				null, fromAddrFederalland);
		GreenNavAddress destAddress = new GreenNavAddress(toLat, toLon,
				toAddrStreet, toAddrHNR, toAddrCity, toAddrPostcode,
				toAddrCountry, toAddrFederalland);

		view.setTag(id);

		tvName.setText(favName);

		if (isFromAddrExisting) {
			String from = startAddress.toString();
			tvFrom.setText(from);
		} else {
			tvFrom.setText(gnContext.getString(R.string.gps_current_position));
		}
		if (isToAddrExisting) {
			String to = destAddress.toString();
			tvTo.setText(to);
		} else {
			tvTo.setText("");
		}

		tvBattery.setText(String.valueOf(battery) + "%");

		String[] carTypeValuesLong = context.getResources().getStringArray(
				R.array.carTypesValues);
		for (int i = 0; i < carTypeValuesLong.length; i++) {
			if (carTypeValuesLong[i].trim().equals(car.trim())) {
				String s = context.getResources().getStringArray(
						R.array.carTypesValuesShort)[i];
				tvCar.setText(s);
			}
		}

		switch (rType) {
		case 0:
			ivRType.setImageResource(R.drawable.route_alt);
			ivArrow.setVisibility(View.VISIBLE);
			break;
		case 1:
			ivRType.setImageResource(R.drawable.range_alt);
			ivArrow.setVisibility(View.INVISIBLE);
			break;
		default:
			Log.e(context.getString(R.constant.LOGTAG)
					+ getClass().getSimpleName(),
					"Couldn't recognize request type from db: " + rType);
			break;
		}

		ivDelete
				.setVisibility(FavoritesActivity.isDeleteIconVisible ? View.VISIBLE
						: View.GONE);
		ivDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(gnContext);
				builder.setTitle(favName);
				builder.setMessage(R.string.assert_deleting_favorite);
				builder.setPositiveButton(R.string.button_value_OK,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Log.i(gnContext.getString(R.constant.LOGTAG)
										+ FavoritesActAdapter.class
												.getSimpleName(),
										"Delete favorite " + favName
												+ " has been confirmed");
								gnFavActivity.deleteFavorite(id);

							}
						});
				builder.setNegativeButton(R.string.button_value_Cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Log.i(gnContext.getString(R.constant.LOGTAG)
										+ FavoritesActAdapter.class
												.getSimpleName(),
										"Delete favorite " + favName
												+ " has been canceled");
								dialog.cancel();
							}
						});
				builder.show();
			}
		});

		super.bindView(view, context, cursor);
	}
}

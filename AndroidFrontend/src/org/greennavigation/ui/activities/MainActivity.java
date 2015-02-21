package org.greennavigation.ui.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.greennavigation.model.GeocodingManager;
import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.model.RequestModel;
import org.greennavigation.model.GeocodingManager.LocationResult;
import org.greennavigation.services.GreenNavDBAdapter;
import org.greennavigation.services.LastRequestsAdapter;
import org.greennavigation.services.AddressThread.AddressResult;
import org.greennavigation.services.LastRequestsAdapter.ViewHolder;
import org.greennavigation.ui.R;
import org.greennavigation.utils.Constants;
import org.greennavigation.utils.HelperClass;
import org.mapsforge.core.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity implements OnItemClickListener,
		RequestModelListener {
	
	public static final String REQUEST_KEY = "requestModel";
	public static GeoPoint rangeradius;
	// Parameter to set in mainActivity
	// Type of car to choose (e.g. SUV, Sport)
	private String currentCarType;
	// Type of request to choose (route or radius)
	private String currentRequestType;
	// Remaining battery level
	private int currentBatteryLevel;
	// Type of route to choose (fastest, shortest, energy-efficient)
	private String currentRouteType;
	// Geopoints
	private GeoPoint currentStartGP;
	private GeoPoint currentStopoverGP;
	private GeoPoint currentDestinationGP;

	private LayoutInflater inflater;

	// Layout items (Views)
	// Actionbar
	protected ImageView ivCarType;
	protected TextView tvCarType;
	private ImageView ivRequestType;
	protected TextView tvBatteryLevel;
	private ImageView ivRouteType;

	// Listview
	private ListView listView;
	private View startRow;
	private FrameLayout flStopOver;
	private View stopOverRow;
	private View destRow;
	private View favRow;
	private View separator;

	private ImageView startPin;
	private TextView startText;
	private ImageView startContextIcon;

	private ImageView stopOverPin;
	private TextView stopOverText;
	private ImageView stopOverContextIcon;

	private ImageView destPin;
	private TextView destText;
	private ImageView destContextIcon;

	private ImageView favStar;
	private TextView favText;

	// Enumeration of dialog types.
	private enum Dialog {
		CarTypeDialog, BatteryLevelDialog, RouteTypeDialog, contextMenuStartDialog, contextMenuStopoverDialog, contextMenuDestinationDialog, AddressDialog, WarningCurrentPosDialog, GPSNotAvailableDialog
	};

	private GeocodingManager geocodingManager;
	private GreenNavDBAdapter greenNavDBAdapter;
	private Cursor dbCursor;
	private HashMap<Integer, GeoPoint[]> lastRequestMap;
	public static LastRequestsAdapter lastRequestAdapter;

	/**
	 * Private method which creates all dialogs offered by this activity. The
	 * dialog is specified by an enumeration. Possibilities:<br>
	 * <table border="1">
	 * <tr>
	 * <th>Enumeration constants</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>CarTypeDialog</td>
	 * <td>Dialog to choose the current type of car.</td>
	 * </tr>
	 * <tr>
	 * <td>BatteryLevelDialog</td>
	 * <td>Dialog to choose the remaining battery level of the car.</td>
	 * </tr>
	 * <tr>
	 * <td>RouteTypeDialog</td>
	 * <td>Dialog to choose the type of calculation (<i>route calculation</i> or
	 * <i>range prediction</i>).</td>
	 * </tr>
	 * <tr>
	 * <td>contextMenuStartDialog</td>
	 * <td>Dialog which shows up, when context menu of start row is requested.</td>
	 * </tr>
	 * <tr>
	 * <td>contextMenuStopoverDialog</td>
	 * <td>Dialog which shows up, when context menu of stopover row is
	 * requested.</td>
	 * </tr>
	 * <tr>
	 * <td>contextMenuDestinationDialog</td>
	 * <td>Dialog which shows up, when context menu of destination row is
	 * requested.</td>
	 * </tr>
	 ** 
	 * <tr>
	 * <td>AddressDialog</td>
	 * <td>Dialog which shows up, when setting a start, stopover or destination.
	 * It resolves the address string to geocoordinates.</td>
	 * </tr>
	 ** 
	 * <tr>
	 * <td>GPSNotAvailableDialog</td>
	 * <td>Dialog which shows up, when a the current position should be
	 * determined, but GPS localization is not allowed by the user.</td>
	 * </tr>
	 * </table>
	 * 
	 * @param dialog
	 *            - Enumeration constant to determine the dialog which is to be
	 *            created.
	 * @return Created {@link android.app#AlertDialog} to show, finally.
	 */
	private AlertDialog getDialog(Dialog dialog) {
		AlertDialog.Builder dialogBuilder;
		AlertDialog alertDialog;
		switch (dialog) {
		case CarTypeDialog:
			Log.i("GN", "Car type will be changed");

			dialogBuilder = new AlertDialog.Builder(this);
			alertDialog = null;

			dialogBuilder.setTitle(R.string.car_type_dialog_title);
			dialogBuilder.setItems(R.array.carTypesValues,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							String[] cTypes = getResources().getStringArray(
									R.array.carTypesValues);
							currentCarType = cTypes[item];
							Log.i("GN", "Car type has been changed to: "
									+ currentCarType);

							String carType = "";
							if (currentCarType
									.equals(getString(R.value.carType_Karabag_Fiat_500E))) {
								carType = getString(R.string.carType_Karabag_Fiat_500E_short);
								Log.i("GN",
										"Set new image: R.drawable.car_sport_alt;");
							} else if (currentCarType
									.equals(getString(R.value.carType_Luis))) {
								carType = getString(R.string.carType_Luis_short);
								Log.i("GN",
										"Set new image: R.drawable.car_sport_default");
							} else if (currentCarType
									.equals(getString(R.value.carType_Mute))) {
								carType = getString(R.string.carType_Mute_short);
								Log.i("GN",
										"Set new image: R.drawable.car_suv_alt");
							} else if (currentCarType
									.equals(getString(R.value.carType_Sam))) {
								carType = getString(R.string.carType_Sam_short);
								Log.i("GN",
										"Set new image: R.drawable.car_suv_default");
							} else if (currentCarType
									.equals(getString(R.value.carType_SmartRoadster))) {
								carType = getString(R.string.carType_SmartRoadster_short);
								Log.i("GN",
										"Set new image: R.drawable.car_suv_default");
							} else if (currentCarType
									.equals(getString(R.value.carType_Stromos))) {
								carType = getString(R.string.carType_Stromos_short);
								Log.i("GN",
										"Set new image: R.drawable.car_suv_default");
							} else {
								Log.e(getString(R.value.carType_Stromos)
										+ getClass().getSimpleName(),
										"Could not recognize choosen car type.");
								Log.e("GN",
										"Could not recognize choosen car type.");
							}
							tvCarType.setText(carType);
							ivCarType.invalidate();
						}
					});
			AlertDialog alert = dialogBuilder.create();
			return alert;

		case BatteryLevelDialog:
			Log.i("GN", "BatteryLevel will be changed");

			dialogBuilder = new AlertDialog.Builder(this);
			alertDialog = null;

			View layout = inflater.inflate(R.layout.battery_level_dialog,
					(ViewGroup) findViewById(R.id.batttery_level_layout_root));

			final TextView batteryLevelProgressText = (TextView) layout
					.findViewById(R.id.battery_level_progress_text);
			batteryLevelProgressText.setText(String
					.valueOf(currentBatteryLevel) + " %");

			final SeekBar seekbar = (SeekBar) layout
					.findViewById(R.id.battery_level_seekbar);
			seekbar.setProgress(currentBatteryLevel);
			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				public void onStopTrackingTouch(SeekBar seekBar) {
					// unnecessary
				}

				public void onStartTrackingTouch(SeekBar seekBar) {
					// unnecessary
				}

				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					batteryLevelProgressText.setText(String.valueOf(progress)
							+ " %");
				}
			});
			dialogBuilder.setPositiveButton(
					getString(R.string.button_value_OK), new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							int seekbarProg = seekbar.getProgress();
							Log.i("GN",
									"Positive Button has been pushed - Change current battery level to "
											+ seekbarProg);
							currentBatteryLevel = seekbarProg;
							tvBatteryLevel.setText(String
									.valueOf(currentBatteryLevel) + " %");

							dialog.dismiss();
						}
					});

			dialogBuilder.setNegativeButton(
					getString(R.string.button_value_Cancel),
					new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							Log.i("GN",
									"Negative Button has been pushed - No change of current battery level");
							dialog.cancel();

						}
					});

			dialogBuilder.setView(layout);
			dialogBuilder.setTitle(R.string.battery_level_title);
			dialogBuilder.setIcon(R.drawable.battery_white);
			alertDialog = dialogBuilder.create();
			return alertDialog;
		case RouteTypeDialog:
			Log.i("GN", "Route type will be changed");

			dialogBuilder = new AlertDialog.Builder(this);
			alertDialog = null;
			dialogBuilder.setTitle(R.string.routing_type_dialog_title);
			dialogBuilder.setItems(R.array.routingTypes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							String[] rTypes = getResources().getStringArray(
									R.array.routingTypesValues);
							currentRouteType = rTypes[item];
							Log.i("GN", "Route type has been changed to: "
									+ currentRouteType);
							int resId = 0;
							if (currentRouteType
									.equals(getString(R.value.route_type_energy))) {
								resId = R.drawable.energy_route_default;
								Log.i("GN",
										"Set new image: R.drawable.energy_route_default");
							} else if (currentRouteType
									.equals(getString(R.value.route_type_fastest))) {
								resId = R.drawable.fastest_route_default;
								Log.i("GN",
										"Set new image: R.drawable.fastest_route_default");
							} else if (currentRouteType
									.equals(getString(R.value.route_type_shortest))) {
								resId = R.drawable.shortest_route_default;
								Log.i("GN",
										"Set new image: R.drawable.shortest_route_default");
							} else {
								Log.e("GN",
										"Could not recognize choosen route type");
							}

							ivRouteType.setImageResource(resId);
							ivRouteType.invalidate();
						}
					});
			alert = dialogBuilder.create();
			return alert;
		case contextMenuStartDialog:
			dialogBuilder = new AlertDialog.Builder(this);
			alert = null;

			dialogBuilder.setTitle(R.string.start_dialog_title);
			dialogBuilder.setItems(R.array.contextMenuItemsStart,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							String[] cMenuItems = getResources()
									.getStringArray(
											R.array.contextMenuItemsStart);
							Log.i("GN",
									"Menu entry from start context menu has been choosen: "
											+ item);
							if (cMenuItems[item]
									.equals(getString(R.string.cMenu_GPS))) {
								getCurrentPosition();
							} else if (cMenuItems[item]
									.equals(getString(R.string.cMenu_contact_from))) {

								/**
								 * @author: abdelhamid barzali.
								 */
								Intent contactsIntent = new Intent(
										MainActivity.this,
										ContactAddressesListActivity.class);
								// Store values between activities here.
								PreferenceManager
										.getDefaultSharedPreferences(
												MainActivity.this)
										.edit()
										.putBoolean(
												Constants.startContacAdrtflag,
												true).commit();

								startActivityForResult(contactsIntent,
										Constants.Addresses);

								Log.i("GN", "Contact picker will be started");
							} else {
								Log.e("GN",
										"Could not recognize choosen menu entry");
							}

						}
					});
			alert = dialogBuilder.create();
			return alert;
		case contextMenuStopoverDialog:
			dialogBuilder = new AlertDialog.Builder(this);
			alert = null;

			dialogBuilder.setTitle(R.string.stopover_dialog_title);
			dialogBuilder.setItems(R.array.contextMenuItemsStopover,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							String[] cMenuItems = getResources()
									.getStringArray(
											R.array.contextMenuItemsStopover);
							Log.i("GN",
									"Menu entry from stopover context menu has been choosen: "
											+ item);
							if (cMenuItems[item]
									.equals(getString(R.string.cMenu_GPS))) {
								Log.i("GN", "GPS Locator will be started");
								try {
									final ProgressDialog pdGPS = ProgressDialog
											.show(MainActivity.this,
													null,
													MainActivity.this
															.getString(R.string.waiting_message));
									pdGPS.setCancelable(true);
									geocodingManager
											.getLocation(new LocationResult() {
												@Override
												public void gotLocation(
														Location location) {
													currentStopoverGP = new GeoPoint(
															location.getLatitude(),
															location.getLongitude());
													stopOverText
															.setText(MainActivity.this
																	.getString(R.string.gps_current_position));
													pdGPS.dismiss();
												}
											});
								} catch (Exception e) {
									Toast.makeText(MainActivity.this,
											R.string.gps_current_position,
											Toast.LENGTH_SHORT).show();
								}
							} else if (cMenuItems[item]
									.equals(getString(R.string.cMenu_contact_to))) {
								Log.i("GN", "Contact picker will be started");

							} else if (cMenuItems[item]
									.equals(getString(R.string.cMenu_remove_stopover))) {
								Log.i("GN", "StopOver has been removed");
								stopOverRow.setVisibility(View.GONE);
							} else {
								Log.e("GN",
										"Could not recognize choosen menu entry");
							}

						}
					});
			alert = dialogBuilder.create();
			return alert;

		case contextMenuDestinationDialog:
			dialogBuilder = new AlertDialog.Builder(this);
			alert = null;

			dialogBuilder.setTitle(R.string.dest_dialog_title);
			dialogBuilder.setItems(R.array.contextMenuItemsDest,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							String[] cMenuItems = getResources()
									.getStringArray(
											R.array.contextMenuItemsDest);
							Log.i("GN",
									"Menu entry from destination context menu has been choosen: "
											+ item);
							if (cMenuItems[item]
									.equals(getString(R.string.cMenu_contact_to))) {
								Log.i("GN", "Contact Picker will be started");
								Intent contactAdrsIntent = new Intent(
										MainActivity.this,
										ContactAddressesListActivity.class);

								// Store values between activities here.
								PreferenceManager
										.getDefaultSharedPreferences(
												MainActivity.this)
										.edit()
										.putBoolean(
												Constants.destContactAdrflag,
												true).commit();

								startActivityForResult(contactAdrsIntent,
										Constants.Addresses);
							} else {
								Log.e("GN",
										"Could not recognize choosen menu entry");
							}

						}
					});
			alert = dialogBuilder.create();
			return alert;
		case WarningCurrentPosDialog:
			dialogBuilder = new AlertDialog.Builder(this);
			alert = null;

			dialogBuilder.setTitle(R.string.attention);
			dialogBuilder.setMessage(R.string.warning_message_gps_postition);
			dialogBuilder.setPositiveButton(R.string.button_value_OK,
					new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			alert = dialogBuilder.create();
			return alert;
		case GPSNotAvailableDialog:
			dialogBuilder = new AlertDialog.Builder(this);
			alert = null;

			dialogBuilder.setTitle(R.string.attention);
			dialogBuilder.setMessage(R.string.warning_message_gps_needed);
			dialogBuilder.setPositiveButton(R.string.button_value_OK,
					new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							startActivity(new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					});
			dialogBuilder.setNegativeButton(R.string.button_value_Cancel,
					new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			alert = dialogBuilder.create();
			return alert;
		default:
			return null;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Remove application title because an actionbar is used
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		geocodingManager = new GeocodingManager(this);

		setContentView(R.layout.main_activity);

		// Initialize parameters to default values
		currentCarType = getString(R.default_value.default_car_type);
		currentRequestType = getString(R.default_value.default_request_type);
		currentBatteryLevel = getResources().getInteger(
				R.default_value.default_battery_level);
		currentRouteType = getString(R.default_value.route_type);

		Log.i("GN", "Default value car type: " + currentCarType);
		Log.i("GN", "Default value request type: " + currentRequestType);
		Log.i("GN", "Default value battery level: " + currentBatteryLevel);
		Log.i("GN", "Default value route type: " + currentRouteType);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Find all views of activity
		ivCarType = (ImageView) findViewById(R.id.car_type_image);
		tvCarType = (TextView) findViewById(R.id.car_type_text);
		ivRequestType = (ImageView) findViewById(R.id.request_type);
		tvBatteryLevel = (TextView) findViewById(R.id.battery_level_percent);
		ivRouteType = (ImageView) findViewById(R.id.route_type);

		tvCarType.setText(currentCarType);

		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		startRow = inflater.inflate(R.layout.main_activity_selection_row,
				listView, false);
		stopOverRow = inflater.inflate(R.layout.main_activity_selection_row,
				listView, false);
		destRow = inflater.inflate(R.layout.main_activity_selection_row,
				listView, false);
		favRow = inflater.inflate(R.layout.main_activity_selection_row,
				listView, false);
		separator = inflater.inflate(R.layout.main_activity_separator,
				listView, false);

		// Initialize views of startRow
		startPin = (ImageView) startRow.findViewById(R.id.sel_row_icon);
		startPin.setImageResource(R.drawable.pin_black);
		startText = (TextView) startRow.findViewById(R.id.sel_row_text);
		startText.setText(R.string.start);

		startContextIcon = (ImageView) startRow
				.findViewById(R.id.sel_row_context_icon);
		startContextIcon.setImageResource(R.drawable.context_button_black);
		// Initialize views of stopOverRow
		stopOverPin = (ImageView) stopOverRow.findViewById(R.id.sel_row_icon);
		stopOverPin.setImageResource(R.drawable.pin_black);
		stopOverText = (TextView) stopOverRow.findViewById(R.id.sel_row_text);
		stopOverText.setText(R.string.stopover);
		stopOverContextIcon = (ImageView) stopOverRow
				.findViewById(R.id.sel_row_context_icon);
		stopOverContextIcon.setImageResource(R.drawable.context_button_black);
		flStopOver = new FrameLayout(this);
		flStopOver.addView(stopOverRow);
		flStopOver.requestLayout();
		stopOverRow.setVisibility(View.GONE);
		// Initialize views of destRow
		destPin = (ImageView) destRow.findViewById(R.id.sel_row_icon);
		destPin.setImageResource(R.drawable.pin_black);
		destText = (TextView) destRow.findViewById(R.id.sel_row_text);
		destText.setText(R.string.destination);
		destContextIcon = (ImageView) destRow
				.findViewById(R.id.sel_row_context_icon);
		destContextIcon.setImageResource(R.drawable.context_button_black);
		// Initialize views of favRow
		favStar = (ImageView) favRow.findViewById(R.id.sel_row_icon);
		favStar.setImageResource(R.drawable.star_black);
		favText = (TextView) favRow.findViewById(R.id.sel_row_text);
		favText.setText(R.string.favorites);

		ImageView iv_contextMenu_start = (ImageView) startRow
				.findViewById(R.id.sel_row_context_icon);
		ImageView iv_contextMenu_stopover = (ImageView) stopOverRow
				.findViewById(R.id.sel_row_context_icon);
		ImageView iv_contextMenu_dest = (ImageView) destRow
				.findViewById(R.id.sel_row_context_icon);
		iv_contextMenu_start
				.setTag(getString(R.constant.TAG_contextMenu_start));
		iv_contextMenu_stopover
				.setTag(getString(R.constant.TAG_contextMenu_stopover));
		iv_contextMenu_dest.setTag(getString(R.constant.TAG_contextMenu_dest));

		// Set tags
		ivRequestType
				.setTag(getString(R.default_value.default_request_type_tag));
		startRow.setTag(getString(R.constant.TAG_start));
		stopOverRow.setTag(getString(R.constant.TAG_stopover));
		destRow.setTag(getString(R.constant.TAG_dest));
		favRow.setTag(getString(R.constant.TAG_favorite));

		// Initialize view of separator
		TextView separatorText = (TextView) separator
				.findViewById(R.id.separator);
		separatorText.setText(R.string.separatorText);

		// add all components to listview
		listView.addHeaderView(startRow);
		listView.addHeaderView(flStopOver);
		listView.addHeaderView(destRow);
		listView.addHeaderView(favRow);
		listView.addHeaderView(separator);

		// initialize database
		this.greenNavDBAdapter = new GreenNavDBAdapter(this);
		this.greenNavDBAdapter.open();
		this.dbCursor = this.greenNavDBAdapter.getAllLastRequests();
		this.lastRequestMap = this.greenNavDBAdapter.getAllLastRequestsAsMap();
		startManagingCursor(dbCursor);

		// set adapter for listview, which will fill the layout of 'last
		// requests' with entries from the database.
		lastRequestAdapter = new LastRequestsAdapter(this,
				R.layout.main_activity_last_request_row, dbCursor,
				new String[] { GreenNavDBAdapter.KEY_FROM_ADDR_STRING,
						GreenNavDBAdapter.KEY_TO_ADDR_STRING }, new int[] {
						R.id.from, R.id.to });
		listView.setAdapter(lastRequestAdapter);
	}

	/**
	 * Method which is specified in actionbar.xml as 'OnClick' parameter and
	 * will therefore be called when clicking on LinearLayout of 'car_type_box'.
	 * Calls the creator of the change car type dialog and shows it, finally.
	 * 
	 * @param v
	 *            - The view that caused the click (<i>unused here</i>).
	 */
	public void changeCarType(View v) {
		getDialog(Dialog.CarTypeDialog).show();
	}

	/**
	 * Method which is specified in actionbar.xml as 'OnClick' parameter and
	 * will therefore be called when clicking on ImageView of 'request_type'.
	 * Switches the current request type and the corresponding image of it and
	 * notifies the user through an toast message.
	 * 
	 * @param v
	 *            - The view that caused the click (<i>unused here</i>).
	 */
	public void changeRequestType(View v) {
		Log.i("GN", "Request type will be changed");

		if (((String) ivRequestType.getTag())
				.equals(getString(R.constant.TAG_route))) {
			Log.i("GN", "Set 'range'");

			ivRequestType.setImageResource(R.drawable.range_default);
			ivRequestType.setTag(getString(R.constant.TAG_range));
			ivRequestType.invalidate();

			currentRequestType = getString(R.value.request_type_range);

			stopOverRow.setVisibility(View.GONE);
			currentStopoverGP = null;
			setDestRowEnabled(false);

			Toast.makeText(this,
					R.string.toast_notification_text_request_type_range,
					Toast.LENGTH_SHORT).show();
		} else if (((String) ivRequestType.getTag())
				.equals(getString(R.constant.TAG_range))) {
			Log.i("GN", "Set 'route'");
			ivRequestType.setImageResource(R.drawable.route_default);
			ivRequestType.setTag(getString(R.constant.TAG_route));
			ivRequestType.invalidate();

			currentRequestType = getString(R.value.request_type_route);
			setDestRowEnabled(true);
			Toast.makeText(this,
					R.string.toast_notification_text_request_type_route,
					Toast.LENGTH_SHORT).show();
		} else {
			Log.e("GN", "Couldn't regconize tag of requestType!");
		}
	}

	/**
	 * Method which is specified in actionbar.xml as 'OnClick' parameter and
	 * will therefore be called when clicking on FrameLayout of
	 * 'battery_container"'. Calls the creator of the battery level dialog and
	 * shows it, finally.
	 * 
	 * @param v
	 *            - The view that caused the click (<i>unused here</i>).
	 */
	public void changeBatteryLevel(View v) {
		getDialog(Dialog.BatteryLevelDialog).show();
	}

	/**
	 * Method which is specified in actionbar.xml as 'OnClick' parameter and
	 * will therefore be called when clicking on ImageView of 'route_type'.
	 * Calls the creator of the route type dialog and shows it, finally.
	 * 
	 * @param v
	 *            - The view that caused the click (<i>unused here</i>).
	 */
	public void changeRouteType(View v) {
		getDialog(Dialog.RouteTypeDialog).show();
	}

	/**
	 * Method which is specified in actionbar.xml as 'OnClick' parameter and
	 * will therefore be called when clicking on 'GO'-Button. <br>
	 * It will create a RequestModel and hand it over via SharedPreferences to
	 * activity 'GreeNavigationMap'. If the RequestModel could not be created
	 * for any reason, a toast message will inform the user about this.
	 * 
	 * @param v
	 *            - The view that caused the click (<i>unused here</i>).
	 */
	public void sendRequest(View v) {
		Log.i("MainActivity", "sendRequest()");
		try {

			RequestModel model = getRequestmodel();

			if (model != null) {
				Log.i("MainActivity.sendRequest()", "model!=null");
				GeoPoint start = model.getStart();
				rangeradius = start;
				GeoPoint stopover = model.getStopover();
				if (stopover != null) {

					model.setDestination(stopover);

				}
				Log.e(getClass().getSimpleName(), getRequestmodel()
						.getRequestXMLScheme());
			} else {
				Log.i("MainActivity.sendRequest()", "model==null");
				Log.e(getClass().getSimpleName(), "Request model not found!");
			}

			if (HelperClass.isAppConnected(MainActivity.this)) {
				Log.i("MainActivity.sendRequest()", "isAppConnected=true");

				
				
				final Intent mapIntent = new Intent(MainActivity.this,
						GreenNavigationMap.class);
				// store request in intent to make it available to the next activity
				mapIntent.putExtra(REQUEST_KEY, model);
				// store request in database and get new hashmap
				final String start = this.startText.getText().toString() == getString(R.string.start) ? null
						: this.startText.getText().toString();
				final String destination = this.destText.getText().toString() == getString(R.string.destination) ? null
						: this.destText.getText().toString();

				Log.i("GN", "doingBG");
				greenNavDBAdapter.insertLastRequest(start, destination,
						currentStartGP, currentDestinationGP);
				lastRequestMap = greenNavDBAdapter.getAllLastRequestsAsMap();
				dbCursor.requery();

				runOnUiThread(new Runnable() {
					public void run() {
						Log.i(MainActivity.class.getSimpleName(),
								"Start MainActivity.lastRequestAdapter.notifyDataSetChanged()...");

						Log.i(MainActivity.class.getSimpleName(),
								"MainActivity.lastRequestAdapter.notifyDataSetChanged() ended.");
						startActivity(mapIntent);
					}
				});

			} else {
				Log.i("MainActivity.sendRequest()", "isAppConnected=false");
				Toast.makeText(getApplicationContext(),
						getString(R.string.internetNotpresent_message),
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.toString());
			// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}


	/**
	 * Method which is specified in actionbar.xml as 'OnClick' parameter and
	 * will therefore be called when clicking on one of each 'selection rows'.
	 * It decides - depending on the tag of the row which calls this method -
	 * which method to call. These methods again open a specific context menu
	 * for each row.
	 * 
	 * @param v
	 *            - The view that caused the click (<i>unused here</i>).
	 */
	public void openSelectionRowContextMenu(View v) {
		Log.i("GN",
				"Context menu of selection row has been requested by: "
						+ v.getTag());
		if (v.getTag() == getString(R.constant.TAG_contextMenu_start)) {
			Log.i("GN", "Context menu of start row has been lauched");
			openContextMenuStart();
		} else if (v.getTag() == getString(R.constant.TAG_contextMenu_stopover)) {
			Log.i("GN", "Context menu of stopover row has been lauched");
			openContextMenuStopOver();
		} else if (v.getTag() == getString(R.constant.TAG_contextMenu_dest)) {
			Log.i("GN", "Context menu of destination row has been lauched");
			openContextMenuDest();
		} else {
			Log.e("GN",
					"Couldn't recognize context menu request by: " + v.getTag());
		}

	}

	/**
	 * Method which calls the creator of the 'start' row context menu and shows
	 * it, finally.
	 */
	private void openContextMenuStart() {
		getDialog(Dialog.contextMenuStartDialog).show();
	}

	/**
	 * Method which calls the creator of the 'stopover' row context menu and
	 * shows it, finally.
	 */
	private void openContextMenuStopOver() {
		getDialog(Dialog.contextMenuStopoverDialog).show();
	}

	/**
	 * Method which calls the creator of the 'destination' row context menu and
	 * shows it, finally.
	 */
	private void openContextMenuDest() {
		getDialog(Dialog.contextMenuDestinationDialog).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_entry_addStopover:
			Log.i("GN",
					"Options menu entry has been choosen: Add/Remove stopover");
			if (currentRequestType == getString(R.value.request_type_route)) {
				switch (stopOverRow.getVisibility()) {
				case View.VISIBLE:
					Log.i("GN",
							"Stopover changed status from visible to invisible");
					stopOverRow.setVisibility(View.GONE);
					listView.requestLayout();
					break;
				case View.INVISIBLE:
				case View.GONE:
					Log.i("GN",
							"Stopover changed status from invisible to visible");
					stopOverRow.setVisibility(View.VISIBLE);
					stopOverRow
							.setTag(getString(R.constant.TAG_contextMenu_stopover));
					listView.requestLayout();
					break;
				default:
					Log.e("GN",
							"Unrecognized visibility status of stopover row");
					break;
				}
			} else {
				Toast.makeText(this, getString(R.string.adding_stopver_denied),
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.menu_entry_switchPoints:
			if (this.currentRequestType
					.equals(getString(R.value.request_type_range))) {
				break;
			}
			CharSequence oldStart = startText.getText();
			CharSequence oldDest = destText.getText();
			GeoPoint oldStartGP = currentStartGP;
			GeoPoint oldDestGP = currentDestinationGP;
			if (!oldStart.equals(getString(R.string.start))) {
				destText.setText(oldStart);
				currentDestinationGP = oldStartGP;

				startText.setText(getString(R.string.start));
				currentStartGP = null;
			}
			if (!oldDest.equals(getString(R.string.destination))) {
				startText.setText(oldDest);
				currentStartGP = oldDestGP;
				if (oldStart.equals(getString(R.string.start))) {
					destText.setText(getString(R.string.destination));
					currentDestinationGP = null;
				}

			}
			Log.i("GN", "destGP: " + currentDestinationGP + " startGP: "
					+ currentStartGP);
			break;
		case R.id.menu_entry_settings:

			// @author Abdelhamid Barzali.

			Intent intent = new Intent(
					android.provider.Settings.ACTION_LOCALE_SETTINGS);
			startActivity(intent);

			break;
		}
		return true;
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		Log.i("GN",
				"List item clicked: " + view.getTag() + " ("
						+ String.valueOf(i) + ")");
		AddingAddressDialog dialog;
		switch (i) {
		// start has been selected
		case 0:

			dialog = new AddingAddressDialog(this, new AddressResult() {
				public void gotErrorMessage(String msg) {
				}

				public void gotAdressCandidates(
						ArrayList<GreenNavAddress> addressCandidatesList) {
					GreenNavAddress gna = addressCandidatesList.get(0);
					if (startText != null) {
						startText.setText(gna.toString());
					}

					currentStartGP = gna.getGeoPoint();
				}
			});
			dialog.show();
			break;
		// stopover has been selected
		case 1:
			dialog = new AddingAddressDialog(this, new AddressResult() {
				public void gotErrorMessage(String msg) {
				}

				public void gotAdressCandidates(
						ArrayList<GreenNavAddress> addressCandidatesList) {
					GreenNavAddress gna = addressCandidatesList.get(0);
					stopOverText.setText(gna.toString());
					currentStopoverGP = gna.getGeoPoint();
				}
			});
			dialog.show();
			break;
		// destination has been selected
		case 2:
			if (this.destRow.isEnabled()) {
				dialog = new AddingAddressDialog(this, new AddressResult() {
					public void gotErrorMessage(String msg) {
					}

					public void gotAdressCandidates(
							ArrayList<GreenNavAddress> addressCandidatesList) {
						GreenNavAddress gna = addressCandidatesList.get(0);
						destText.setText(gna.toString());
						currentDestinationGP = gna.getGeoPoint();
					}
				});
				dialog.show();
			}
			break;
		// favorites has been selected
		case 3:
			Intent favoritesIntent = new Intent(MainActivity.this,
					FavoritesActivity.class);
			MainActivity.this.startActivityForResult(favoritesIntent,
					Constants.FAVORITE);
			break;
		case 4:
			// ignore item at position 4, as it is the header item
			break;
		// all last request entries
		default:
			// id is stored as tag of view
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			GeoPoint[] geoPoints = this.lastRequestMap.get(viewHolder.id);
			if (viewHolder.fromAddr.getText().equals(
					getString(R.string.gps_current_position))) {
				this.currentStartGP = null;
				getDialog(Dialog.WarningCurrentPosDialog).show();
			}
			this.currentStartGP = geoPoints[0];

			if (geoPoints[1].equals(new GeoPoint(0, 0))) {
				this.currentDestinationGP = null;
				setDestRowEnabled(false);
			} else {
				this.currentDestinationGP = geoPoints[1];
				setDestRowEnabled(true);
			}

			TextView tvFrom = viewHolder.fromAddr;
			TextView tvTo = viewHolder.toAddr;

			String startAddr = tvFrom.getText().toString();
			String destAddr = tvTo.getText().toString() == "" ? getString(R.string.destination)
					: tvTo.getText().toString();

			this.startText.setText(startAddr);
			this.destText.setText(destAddr);

			Log.i("GN", "Choosen last request:");
			Log.i("GN",
					"Start: "
							+ startAddr
							+ " ("
							+ (this.currentStartGP != null ? this.currentStartGP
									.toString() : "null") + ")");
			Log.i("GN",
					"Destination: "
							+ destAddr
							+ " ("
							+ (this.currentDestinationGP != null ? this.currentDestinationGP
									.toString() : "null") + ")");
		}
	}

	/**
	 * This method determines the devices current position and sets this
	 * coordinates as the start's GeoPoint.
	 */
	private void getCurrentPosition() {
		Log.i("GN", "GPS Locator will be started");
		final ProgressDialog pdGPS = ProgressDialog.show(MainActivity.this,
				null, MainActivity.this.getString(R.string.waiting_message));
		pdGPS.setCancelable(true);
		try {
			geocodingManager.getLocation(new LocationResult() {
				@Override
				public void gotLocation(Location location) {
					MainActivity.this.currentStartGP = new GeoPoint(location
							.getLatitude(), location.getLongitude());
					Log.i("GN", "Set new GeoPoint: "
							+ MainActivity.this.currentStartGP);
					startText.setText(MainActivity.this
							.getString(R.string.gps_current_position));
					pdGPS.dismiss();
				}
			});
		} catch (Exception e) {
			pdGPS.dismiss();
			Log.e("GN", "Couldn't find GPS position: " + e.getMessage());
			getDialog(Dialog.GPSNotAvailableDialog).show();
		}
	}

	/**
	 * Enables or disables the row 'destination' with all it's components.
	 * 
	 * @param flag
	 *            - boolean parameter to enable or disable the row
	 */
	private void setDestRowEnabled(boolean flag) {

		destRow.setEnabled(flag);
		destContextIcon.setClickable(flag);

		if (flag) {
			destPin.setImageResource(R.drawable.pin_black);
			destText.setTextColor(Color.BLACK);
			destContextIcon.setImageResource(R.drawable.context_button_black);
		} else {
			destPin.setImageResource(R.drawable.pin_grey);
			destText.setText(getString(R.string.destination));
			this.currentDestinationGP = null;
			destText.setTextColor(Color.GRAY);
			destContextIcon.setImageResource(R.drawable.context_button_grey);
		}
	}

	/**
	 * Called for, when a started activity returns with a result.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(":D)" + getClass().getSimpleName(), "reqcode: " + requestCode
				+ " rescode: " + resultCode + " id: " + data);
		switch (requestCode) {
		case Constants.FAVORITE:
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra("StartIsExisting", false)) {
					startText.setText(data.getStringExtra("From"));
					currentStartGP = new GeoPoint(data.getDoubleExtra(
							GreenNavDBAdapter.KEY_FROM_LAT, 0),
							data.getDoubleExtra(GreenNavDBAdapter.KEY_FROM_LON,
									0));
				} else {
					getCurrentPosition();
				}

				if (data.getBooleanExtra("DestinationIsExisting", false)) {
					destText.setText(data.getStringExtra("To"));
					setDestRowEnabled(true);
					currentDestinationGP = new GeoPoint(
							data.getDoubleExtra(GreenNavDBAdapter.KEY_TO_LAT, 0),
							data.getDoubleExtra(GreenNavDBAdapter.KEY_TO_LON, 0));
				} else {
					destText.setText(getString(R.string.destination));
					currentDestinationGP = null;
					if (data.getIntExtra(GreenNavDBAdapter.KEY_RTYPE, 0) == 1) {
						setDestRowEnabled(false);
					}
				}

				currentCarType = data.getStringExtra(GreenNavDBAdapter.KEY_CAR)
						.trim();
				String[] carTypes_long = getResources().getStringArray(
						R.array.carTypesValues);
				String[] carTypes_short = getResources().getStringArray(
						R.array.carTypesValuesShort);
				int i = 0;
				for (String string : carTypes_long) {
					if (string.trim().equals(currentCarType)) {
						break;
					}
					i++;
				}
				tvCarType.setText(carTypes_short[i]);

				currentBatteryLevel = data.getIntExtra(
						GreenNavDBAdapter.KEY_BATTERY, 0);
				tvBatteryLevel.setText(String.valueOf(currentBatteryLevel)
						+ " %");

				currentRequestType = data.getIntExtra(
						GreenNavDBAdapter.KEY_RTYPE, 0) == 0 ? getString(R.value.request_type_route)
						: getString(R.value.request_type_range);
				ivRequestType
						.setImageResource(currentRequestType == getString(R.value.request_type_route) ? R.drawable.route_default
								: R.drawable.range_default);
				ivRequestType
						.setTag(getString(currentRequestType == getString(R.value.request_type_route) ? R.constant.TAG_route
								: R.constant.TAG_range));
				ivRequestType.invalidate();

				Log.i("GN", "car: " + currentCarType);
				Log.i("GN", "bat: " + currentBatteryLevel);
				Log.i("GN", "rt: " + currentRequestType);
			}
			break;

		case Constants.Addresses:
			if (data != null) {

				if (resultCode == RESULT_OK) {
					try {
						Log.e("!" + getClass().getSimpleName(),
								"Constants.Addresses !!!!! result ok");
						if (data != null) {

							if (data.getBooleanExtra("StartIsExisting", false)) {
								startText.setText(data.getStringExtra("From"));
								currentStartGP = new GeoPoint(
										data.getDoubleExtra(
												GreenNavDBAdapter.KEY_FROM_LAT,
												0), data.getDoubleExtra(
												GreenNavDBAdapter.KEY_FROM_LON,
												0));
							} else {
								getCurrentPosition();
							}

							if (data.getBooleanExtra("DestinationIsExisting",
									false)) {
								destText.setText(data.getStringExtra("To"));
								currentDestinationGP = new GeoPoint(
										data.getDoubleExtra(
												GreenNavDBAdapter.KEY_TO_LAT, 0),
										data.getDoubleExtra(
												GreenNavDBAdapter.KEY_TO_LON, 0));
							} else {
								destText.setText(getString(R.string.destination));
								currentDestinationGP = null;
								if (data.getIntExtra(
										GreenNavDBAdapter.KEY_RTYPE, 0) == 1) {
									setDestRowEnabled(false);
								}
							}

							currentCarType = data.getStringExtra(
									GreenNavDBAdapter.KEY_CAR).trim();
							String[] carTypes_long = getResources()
									.getStringArray(R.array.carTypesValues);
							String[] carTypes_short = getResources()
									.getStringArray(R.array.carTypesValuesShort);
							int i = 0;
							if (currentCarType != null) {

								for (String string : carTypes_long) {
									if (string.trim().equals(currentCarType)) {
										break;
									}
									i++;
								}
							} else {
								Log.e(getClass().getSimpleName(),
										"currentCarType is null");
							}
							if (tvCarType != null) {
								tvCarType.setText(carTypes_short[i]);
							} else {
								Log.e(getClass().getSimpleName(),
										"tvCarType is null");
							}

							currentBatteryLevel = data.getIntExtra(
									GreenNavDBAdapter.KEY_BATTERY, 0);
							String batteryStr = currentBatteryLevel + " %";
							if (tvBatteryLevel != null) {
								tvBatteryLevel.setText(batteryStr);
							} else {
								Log.e(getClass().getSimpleName(),
										"tvBatteryLevel is null");
							}

							currentRequestType = data.getIntExtra(
									GreenNavDBAdapter.KEY_RTYPE, 0) == 0 ? getString(R.value.request_type_route)
									: getString(R.value.request_type_range);
							if (ivRequestType != null) {
								ivRequestType
										.setImageResource(currentRequestType == getString(R.value.request_type_route) ? R.drawable.route_default
												: R.drawable.range_default);
								ivRequestType
										.setTag(getString(currentRequestType == getString(R.value.request_type_route) ? R.constant.TAG_route
												: R.constant.TAG_range));
								ivRequestType.invalidate();

							} else {
								Log.e(getClass().getSimpleName(),
										"ivRequestType is null ;( ");
							}

							Log.i("GN", "car: " + currentCarType);
							Log.i("GN", "bat: " + currentBatteryLevel);
							Log.i("GN", "rt: " + currentRequestType);
						}

					} catch (NullPointerException e) {
						Log.e(getClass().getSimpleName(),
								"Result by Adresses result NOT ok"
										+ e.toString());
					}
				}

			}
			break;
		default:
			break;
		}
	}

	/**
	 * Returns a valid RequestModel of the parameter that have been set during
	 * in the user interface.
	 * 
	 * @throws - a Exception if start, stopover or destination are missing.
	 */
	public RequestModel getRequestmodel() throws Exception {

		RequestModel rModel = null;

		GeoPoint start = this.currentStartGP;
		GeoPoint stopover = this.currentStopoverGP;
		GeoPoint dest = this.currentDestinationGP;

		if (start == null) {
			Log.e("GN", "start is null");
			throw new Exception(
					getString(R.string.error_message_empty_start_coordinates));
		}
		if (this.stopOverRow.getVisibility() == View.VISIBLE) {
			if (stopover == null) {
				Log.e(getClass().getSimpleName(), "stopover is null");
				throw new Exception(
						getString(R.string.error_message_empty_stopover_coordinates));
			}
		}
		if (!(this.currentRequestType == getString(R.value.request_type_range))) {
			if (dest == null) {
				Log.e("GN", "destination is null");
				throw new Exception(
						getString(R.string.error_message_empty_destination_coordinates));
			}
		}
		String batteryLevel = String.valueOf(this.currentBatteryLevel) + "";

		rModel = new RequestModel(this, this.currentRequestType,
				this.currentRouteType, start, dest, stopover,
				this.currentRequestType, batteryLevel, this.currentCarType,
				new Date().toString());

		return rModel;

	}
}

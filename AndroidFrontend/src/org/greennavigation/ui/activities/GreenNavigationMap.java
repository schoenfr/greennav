package org.greennavigation.ui.activities;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.greennavigation.model.EventConstants;
import org.greennavigation.model.NavigationModel;
import org.greennavigation.model.RequestModel;
import org.greennavigation.model.ResponseRouteModel;
import org.greennavigation.model.Sector;
import org.greennavigation.model.TurnDirection;
import org.greennavigation.services.RequestManager;
import org.greennavigation.ui.R;
import org.greennavigation.utils.Constants;
import org.greennavigation.utils.HelperClass;
import org.greennavigation.utils.Util;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ArrayCircleOverlay;
import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
import org.mapsforge.android.maps.overlay.Overlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.android.maps.overlay.OverlayWay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MapActivity is the abstract base class which must be extended in order to use
 * a {@link MainActivity}.
 */
public class GreenNavigationMap extends MapActivity implements Observer,
		EventConstants {
	public ResponseRouteModel responseRoute;

	private NavigationModel model;
	private ProgressDialog progressDialog;
	private GeoPoint[][] points;
	private MapView mapView;
	public static int mapid;
	
	private static boolean SECTOR_DEBUG = false;

	private boolean isOverlay;

	// GUI Elements
	private TextView infoText;
	private TextView distanceText;
	private TextView turnText;
	private TextView streetText;
	private ImageView turnImage;
	

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.i("GreenNavigationMap", "handleMessage()");

			if (msg.what == GOT_MODEL) {
				RenderMap();
				progressDialog.dismiss();
			}
			if (msg.what == NO_MODEL) {
				progressDialog.dismiss();
				showError();
			}

		}

	};

	private void closeActivity() {
		super.onBackPressed();
	}

	private void showError() {
		AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
		alertbuilder
				.setMessage("Server nicht erreicht oder ungültige Route erhalten");
		alertbuilder.setTitle("Routen-Fehler");

		AlertDialog alert = alertbuilder.create();
		alert.show();
	}

	private void RenderMap() {
		Paint routePaint = getRoutePaint();

		if (responseRoute != null) {

			GeoPoint[] routepoints = Util.collectionToArray(GeoPoint[].class,
					responseRoute.getRoute());

			points = new GeoPoint[1][routepoints.length];

			for (int i = 0; i < routepoints.length; i++) {
				points[0][i] = routepoints[i];
			}

			if (points != null) {

				// Store values between activities here.
				boolean radiusflag = PreferenceManager
						.getDefaultSharedPreferences(GreenNavigationMap.this)
						.getBoolean(Constants.radiusflag, false);
				if (radiusflag) {
					RenderRange();
				} else {
					RenderRoute();
					RenderPosition();
					if(SECTOR_DEBUG){
						RenderSectors(routepoints);
					}
				}

			}
		} else {
			Log.e(getClass().getSimpleName(), responseRoute
					+ "  'responseRout' :(");
			MapController controller = getMapView().getController();

			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location location = lm
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				double longitude = location.getLongitude();
				double latitude = location.getLatitude();
				controller.setCenter(new GeoPoint(latitude, longitude));
			} else {
				controller.setCenter(new GeoPoint(53.869722, 10.686389));
			}
		}
	}

	private void RenderPosition() {
		if (model != null) {
			if (model.getCurrentLocation() != null) {
				OverlayCircle circleOverlay = new OverlayCircle(
						getCircelPaint(), getCircelOutlinePaint());
				GeoPoint location = new GeoPoint(model.getCurrentLocation()
						.getLatitude(), model.getCurrentLocation()
						.getLongitude());
				circleOverlay.setCircleData(location, 5);
				ArrayCircleOverlay circles = new ArrayCircleOverlay(
						getCircelPaint(), getCircelPaint());
				circles.addCircle(circleOverlay);
				getMapView().getOverlays().add(circles);
			}
		}
	}

	private void RenderRange() {

		GeoPoint rangeradius = MainActivity.rangeradius;
		OverlayCircle circleOverlay = new OverlayCircle(getCircelPaint(),
				getCircelOutlinePaint());
		circleOverlay.setCircleData(rangeradius, points.length / 5);

		ArrayCircleOverlay circles = new ArrayCircleOverlay(getCircelPaint(),
				getCircelPaint());
		circles.addCircle(circleOverlay);

		// create the RouteOverlay and set the way nodes
		OverlayWay routeOverlay = new OverlayWay(points, getRoutePaint(), null);

		ArrayWayOverlay routes = new ArrayWayOverlay(getRoutePaint(),
				getRoutePaint());
		routes.addWay(routeOverlay);

		// routeOverlay.setRouteData(points);

		/*
		 * RouteOverlay clousroute = new RouteOverlay(getRoutePaint()) ;
		 * 
		 * 
		 * GeoPoint first = geopoints.get(0); GeoPoint last =
		 * geopoints.get(geopoints.size()-1); GeoPoint[] clou = new GeoPoint[2];
		 * clou[0]=first; clou[1]=last;
		 * 
		 * clousroute.setRouteData(clou)
		 */

		List<Overlay> overlays = getMapView().getOverlays();

		if (overlays != null) {
			overlays.clear();
			getMapView().getOverlays().add(routes);
			// getMapView().getOverlays().add(clousroute);

			if (rangeradius != null) {
				getMapView().getOverlays().add(circles);
			}

		}

		if (points != null) {
			// GeoPoint pointcenter = points[points.length / 2];
			MapController controller = getMapView().getController();

			@SuppressWarnings("unused")
			GeoPoint mapCenter = getMapView().getMapPosition().getMapCenter();

			if (rangeradius != null) {
				controller.setCenter(rangeradius);
			} else {
				controller.setCenter(new GeoPoint(53.869722, 10.686389));
			}

			controller.setZoom((getMapView().getMapZoomControls()
					.getZoomLevelMin() / 2) + 14);

		}

	}

	private void RenderRoute() {

		// create the RouteOverlay and set the way nodes
		OverlayWay routeOverlay = new OverlayWay(points, getRoutePaint(), null);

		ArrayWayOverlay routes = new ArrayWayOverlay(getRoutePaint(),
				getRoutePaint());
		routes.addWay(routeOverlay);

		// routeOverlay.setRouteData(points);

		List<Overlay> overlays = getMapView().getOverlays();

		if (overlays != null) {
			overlays.clear();
			getMapView().getOverlays().add(routes);
			// getMapView().getOverlays().add(circleOverlay);

		}

		if (points != null) {
			// center map on routstart
			GeoPoint pointcenter = points[0][0];
		//Log.i("POSITION", "Latitute : " + pointcenter.getLatitude());
		//Log.i("POSITION", "Longitude : " + pointcenter.getLongitude());
			MapController controller = getMapView().getController();

			// GeoPoint mapCenter = getMapView().getMapCenter();
			controller.setCenter(pointcenter);

			controller.setZoom((getMapView().getMapZoomControls()
					.getZoomLevelMin() / 2) + 16);

		}

		// Toast.makeText(GreenNavigationMap.this, responseRoute.getRouteInfo(),
		// Toast.LENGTH_LONG).show();

	}
	
	private  void RenderSectors(GeoPoint[] geopoints){
		//RENDER SECTORS
		//Log.i("MAP","RENDER SECTORS");
		ArrayWayOverlay sectors = new ArrayWayOverlay(getSectorPaint(),getSectorPaint());
		
		//sector
		//loop over sectors
		for(int i = 0; i< geopoints.length-1;i += 2){
			Sector sec = new Sector(geopoints[i],geopoints[i+1]);
			GeoPoint[][] secpoints = new GeoPoint[1][4];
			secpoints[0][0] = sec.topleft;
			secpoints[0][1] = sec.bottomleft;
			secpoints[0][2] = sec.bottomright;
			secpoints[0][3] = sec.topright;
			OverlayWay sectorOverlay = new OverlayWay(secpoints,getSectorPaint(),null);
			sectors.addWay(sectorOverlay);
		}
		
		getMapView().getOverlays().add(sectors);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		this.mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		checkMap(mapid);
		setContentView(mapView);

		HelperClass.CreateAppFile(GreenNavigationMap.this);

		if (HelperClass.isAppConnected(GreenNavigationMap.this)) {

			String titel = getString(R.string.calculate_route_progressdialog_string);
			String message = getString(R.string.fetch_addresses_progressdialog_wait_string);
			progressDialog = ProgressDialog.show(GreenNavigationMap.this,
					titel, message, false);
			progressDialog.setCancelable(true);

			RequestModel request = null;
			// Get saved request from intent
			Bundle b = this.getIntent().getExtras();
			if (b != null) {
				request = (RequestModel) b
						.getSerializable(MainActivity.REQUEST_KEY);
			}
			// Use requestSender to send request on a background thread
			new requestSender().execute(request);
		} else {
			Toast.makeText(GreenNavigationMap.this,
					getString(R.string.internetNotpresent_message),
					Toast.LENGTH_LONG).show();
		}

		isOverlay = false;
	}

	@Override
	protected void onStop() {
		// unsubscribe from GPS updates and disconnect the model
		if (model != null) {
			((LocationManager) getSystemService(Context.LOCATION_SERVICE))
					.removeUpdates(model);
			model.deleteObserver(this);
		}
		super.onStop();
	}

	@Override
	protected void onRestart() {
		// Request GPS-Updates and reconnect model
		if (model != null) {
			((LocationManager) getSystemService(Context.LOCATION_SERVICE))
					.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
							model);
			model.addObserver(this);
		}
		super.onRestart();
	}

	/**
	 * {@link Menu}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(org.greennavigation.ui.R.menu.options_menu, menu);

		return true;
	}

	/**
	 * item {@link MenuItem}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.settings:

			String cancel = getString(R.string.button_value_Cancel);

			CharSequence title = getString(R.string.choose_map);
			CharSequence[] items = { "Oberbayern", "Allgaue", "Berlin" };
			// CharSequence text = "text";
			new AlertDialog.Builder(GreenNavigationMap.this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setItems(items, new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							mapid = which;
							checkMap(which);

						}

					}).setTitle(title)
					.setPositiveButton(cancel, new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							if (dialog != null) {
								dialog.dismiss();
							}

						}
					}).create().show();

			break;

		case R.id.routeinfo:
			boolean radiusflag = PreferenceManager.getDefaultSharedPreferences(
					GreenNavigationMap.this).getBoolean(Constants.radiusflag,
					false);
			String string = getString(R.string.keine_info_zurderroute_msg);

			if (this.responseRoute != null) {

				if (radiusflag) {
					String polygonInfo = responseRoute.getPolygonInfo();
					if (polygonInfo != null) {
						HelperClass.getRouteDialog(polygonInfo,
								getString(R.string.info),
								GreenNavigationMap.this).show();
					} else {
						HelperClass.getRouteDialog(
								responseRoute.getRouteInfo(),
								getString(R.string.info),
								GreenNavigationMap.this).show();
					}

				} else {

					HelperClass.getRouteDialog(responseRoute.getRouteInfo(),
							getString(R.string.info), GreenNavigationMap.this)
							.show();
				}

			} else {

				Toast.makeText(GreenNavigationMap.this, string,
						Toast.LENGTH_LONG).show();
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 */
	public void checkMap(int which) {
		// TODO fit to final solution
		if (mapView != null) {
			mapView.setMapFile(new File("/mnt/sdcard/schleswig-holstein.map"));
		}
		// switch (which) {
		// case 0:
		// if (mapView != null) {
		// mapView.setMapFile(new File(Constants.mucundumgebungdata));
		// }
		// break;
		// case 1:
		// if (mapView != null) {
		// mapView.setMapFile(new File(Constants.allgaeumapdatas));
		// }
		// break;
		// case 2:
		// if (mapView != null) {
		// mapView.setMapFile(new File(Constants.berlinmapdatas));
		// }
		// break;
		//
		// default:
		// break;
		// }
	}

	/**
	 * This class is used to send the route-request on a background-thread and
	 * inform the UI after the operation's completion.
	 * 
	 * @author ss13-navigation
	 * 
	 */
	private class requestSender extends
			AsyncTask<RequestModel, Void, ResponseRouteModel> {

		@Override
		protected ResponseRouteModel doInBackground(RequestModel... params) {
			// Send request
			ResponseRouteModel route = RequestManager.getInstance()
					.sendRequestToServer(GreenNavigationMap.this, params[0]);
			if (route != null) {
				return route;
			} else {
				Log.e(getClass().getSimpleName(), route
						+ "  'responseRout'  in run() :(");
			}
			return null;
		}

		@Override
		protected void onPostExecute(ResponseRouteModel result) {
			if (result != null) {
				// use the result to set model for navigation
				responseRoute = result;
				model = new NavigationModel(
						result.getRouteWithTurnInformation());
				// subscribe model to GPS-updates
				((LocationManager) getSystemService(Context.LOCATION_SERVICE))
						.requestLocationUpdates(LocationManager.GPS_PROVIDER,
								0, 0, model);
				model.addObserver(GreenNavigationMap.this);
				handler.sendEmptyMessage(GOT_MODEL);
			} else {
				handler.sendEmptyMessage(NO_MODEL);
			}
		}
	}

	/**
	 * create the paint object for the CircelOutlineOverlay and set all
	 * parameters.
	 * 
	 * @return {@link Paint}
	 */
	private Paint getCircelOutlinePaint() {
		Paint circleOutlinePaint = new Paint();
		circleOutlinePaint.setStyle(Paint.Style.STROKE);
		circleOutlinePaint.setColor(Color.BLACK);
		circleOutlinePaint.setAlpha(255);
		circleOutlinePaint.setStrokeWidth(3);
		return circleOutlinePaint;
	}

	/**
	 * create the paint object for the CircleOverlay and set all parameters. W
	 * 
	 * @return {@link Paint}
	 */
	private Paint getCircelPaint() {
		Paint circlePaint = new Paint();
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(Color.RED);
		circlePaint.setAlpha(255);
		return circlePaint;
	}

	/**
	 * @return
	 */
	private Paint getRoutePaint() {
		// create the paint object for the RouteOverlay and set all parameters
		Paint routePaint = new Paint();
		routePaint.setStyle(Paint.Style.STROKE);
		routePaint.setColor(Color.BLUE);
		routePaint.setAlpha(160);
		routePaint.setStrokeWidth(6);
		routePaint.setStrokeCap(Paint.Cap.ROUND);
		routePaint.setStrokeJoin(Paint.Join.ROUND);

		return routePaint;
	}
	
	/**
	 * @return
	 */
	private Paint getSectorPaint() {
		// create the paint object for the RouteOverlay and set all parameters
		Paint routePaint = new Paint();
		routePaint.setStyle(Paint.Style.STROKE);
		routePaint.setColor(Color.MAGENTA);
		routePaint.setAlpha(160);
		routePaint.setStrokeWidth(3);
		routePaint.setStrokeCap(Paint.Cap.BUTT);
		routePaint.setStrokeJoin(Paint.Join.MITER);

		return routePaint;
	}

	public MapView getMapView() {
		return mapView;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		responseRoute.setRoute(model.getRouteNodesToDestination());
		RenderMap();

		Log.d("Event", arg1.toString());

		if (arg0 == model) {
			updateOverlay(arg1);
			if (!isOverlay && arg1 == SHOW_OVERLAY) {
				insertOverlay();
			}
		}
	}

	/**
	 * This method displays the overlay showing the details of the next turn.
	 */
	private void insertOverlay() {
		LayoutInflater inflater = getLayoutInflater();
		inflater.inflate(org.greennavigation.ui.R.layout.activity_navigation,
				((ViewGroup) findViewById(android.R.id.content)));

		// Connect GUI with layout
		infoText = (TextView) findViewById(R.id.infoText);
		distanceText = (TextView) findViewById(R.id.distanceText);
		turnText = (TextView) findViewById(R.id.turnText);
		streetText = (TextView) findViewById(R.id.streetText);
		turnImage = (ImageView) findViewById(R.id.turnImage);

		infoText.setText(R.string.waitForRoute);
		isOverlay = true;

		updateOverlay(CREATED_OVERLAY);
	}

	/**
	 * This method removes the turn-overlay.
	 */
	private void removeOverlay() {
		ViewGroup removelayout = ((ViewGroup) findViewById(R.id.navigationLayoutTop));
		((ViewGroup) findViewById(android.R.id.content))
				.removeView(removelayout);
		isOverlay = false;
	}

	/**
	 * This method handles model-events for the turn-overlay.
	 * 
	 * @param event
	 *            the model-event received
	 */
	private void updateOverlay(Object event) {
		if (isOverlay) {
			if (event == LOCATION_CHANGED || event == CREATED_OVERLAY) {
				infoText.setText("");
				int distance = (int) model.getDistanceToTurn();
				int distanceN = (int) model.getDistanceToNextTurn();
				distanceText.setText(Integer.toString(distance) + "m");
			}
			if (event == TURN_CHANGED || event == CREATED_OVERLAY) {
				infoText.setText("");
				TurnDirection direction = model.getTurnDirection();
				switch (direction) {
				case LEFT:
					turnText.setText(R.string.turnLeft);
					turnImage.setImageResource(R.drawable.arrow_left);
					break;
				case RIGHT:
					turnText.setText(R.string.turnRight);
					turnImage.setImageResource(R.drawable.arrow_right);
					break;
				case STRAIGHT:
					turnText.setText(R.string.straight);
					turnImage.setImageResource(R.drawable.arrow_straight);
					break;
				}
				streetText.setText(model.getNextTurnStreet());
				int distance = (int) model.getDistanceToTurn();
				int distanceN = (int) model.getDistanceToNextTurn();
				distanceText.setText(Integer.toString(distance) + "m");
			}
			if (event == OFF_ROUTE) {
				infoText.setText(R.string.offRoute);
			}
			if (event == REACHED_TARGET) {
				infoText.setText(R.string.reachedTarget);
				distanceText.setText("");
				turnText.setText("");
				// TODO change image
				turnImage.setImageResource(R.drawable.star_gold);
				streetText.setText(model.getNextStreet());
			}
			if (event == NO_OVERLAY) {
				removeOverlay();
			}
		}
	}

}

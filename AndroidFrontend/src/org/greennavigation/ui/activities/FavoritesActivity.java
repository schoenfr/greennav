package org.greennavigation.ui.activities;

import java.util.ArrayList;

import org.greennavigation.model.FavoritesModel;
import org.greennavigation.services.FavoritesActAdapter;
import org.greennavigation.services.GreenNavDBAdapter;
import org.greennavigation.ui.R;
import org.greennavigation.utils.HelperClass;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

public class FavoritesActivity extends ListActivity {

	// database adapter where data is coming from
	private GreenNavDBAdapter greenNavDBAdapter;
	// cursor that stores all favorites from database
	private Cursor dbCursor;
	public static boolean isDeleteIconVisible;
	private ArrayList<FavoritesModel> favoritesModelList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Remove application title because an actionbar is used
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(org.greennavigation.ui.R.layout.favorites_activity);
		isDeleteIconVisible = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		greenNavDBAdapter = new GreenNavDBAdapter(this);
		greenNavDBAdapter.open();

		favoritesModelList = greenNavDBAdapter.getAllFavoritesAsList();

		dbCursor = greenNavDBAdapter.getAllFavorites();
		startManagingCursor(dbCursor);
		setListAdapter(new FavoritesActAdapter(this,
				org.greennavigation.ui.R.layout.favorites_activity_row,
				dbCursor, new String[] {}, new int[] {}));

	}

	@Override
	protected void onStop() {
		super.onStop();
		this.greenNavDBAdapter.close();
	}

	/**
	 * Method which is specified in actionbar_favorites.xml as 'OnClick'
	 * parameter and will therefore be called when clicking on home button.
	 * Finishes this activity which will bring the parent activity - here main
	 * activity - to foreground.
	 * 
	 * @param v
	 *            - The view that caused the click (<i>unused here</i>).
	 */
	public void switchBackToMainActivity(View v) {
		Log.i(getString(R.constant.LOGTAG) + getClass().getSimpleName(),
				"Home button has been pushed: Switching back to MainActivity.");
		this.finish();
	}

	/**
	 * Checks if a internet connection is currently established and if there's
	 * one, starts the dialog to add a new favorite. If there's currently no
	 * internet connection available, the user will be informed about the lack
	 * of internet.
	 * 
	 * @param v
	 */
	public void addFavorite(View v) {
		Log.i(getString(R.constant.LOGTAG) + getClass().getSimpleName(),
				"Adding favorite.");
		if (HelperClass.isAppConnected(FavoritesActivity.this)) {
			new AddingFavoriteDialog(this).show();
		} else {
			Toast.makeText(this,
					getString(R.string.error_message_network_error),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * * Method which is specified in actionbar_favorites.xml as 'OnClick'
	 * parameter and will therefore be called when clicking on the
	 * 'minus'-symbol (delete favorites).<br>
	 * Checks with state the list currently has and activates or disables all
	 * delete-icons of all children view in the list.
	 * 
	 * @param v
	 */
	public void changeVisibilityDeleteIcon(View v) {

		isDeleteIconVisible = !isDeleteIconVisible;

		ListView listview = getListView();
		int visiblity = 0;

		for (int i = 0; i < listview.getChildCount(); i++) {

			TableLayout itemLayout = (TableLayout) listview.getChildAt(i);

			ImageView ivDelete = (ImageView) itemLayout
					.findViewById(R.id.favorite_delete);
			visiblity = (isDeleteIconVisible) ? View.GONE : View.VISIBLE;
			ivDelete.setVisibility(visiblity);
		}
		listview.invalidate();
		Log.i(getString(R.constant.LOGTAG) + getClass().getSimpleName(),
				"Visibility of deleting images has been set to: "
						+ ((visiblity == 0) ? "Gone" : "Visible"));
	}
	/**
	 * Stores a favorite to database. 
	 * @param fm - the {@link FavoritesModel} to create the favorite of.
	 */
	protected void createFavorite(FavoritesModel fm) {
		Log.i(getString(R.constant.LOGTAG) + getClass().getSimpleName(),
				"CreateFavorite() has been requested");
		this.greenNavDBAdapter.insertFavorite(fm);
		dbCursor.requery();
		favoritesModelList = greenNavDBAdapter.getAllFavoritesAsList();
	}
	/**
	 * Deletes a favorite from database. 
	 * @param id - the id to identify the favorite that is to be deleted. 
	 */
	public void deleteFavorite(long id) {
		this.greenNavDBAdapter.deleteFavoriteById(id);
		dbCursor.requery();
		favoritesModelList = greenNavDBAdapter.getAllFavoritesAsList();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String stringID = v.getTag().toString();
		Log.i(getString(R.constant.LOGTAG) + getClass().getSimpleName(),
				"Choose favorites entry with id: " + stringID);
		Log.i(getString(R.constant.LOGTAG) + getClass().getSimpleName(),
				"List position: " + position);
		FavoritesModel fm = favoritesModelList.get(position);

		Intent intent = new Intent();

		if (!fm.getStart().isEmpty()) {
			intent.putExtra("StartIsExisting", true);
			intent.putExtra("From", fm.getFromAddr());
			intent.putExtra(GreenNavDBAdapter.KEY_FROM_LAT, fm
					.getFromGeoPoint().getLatitude());
			intent.putExtra(GreenNavDBAdapter.KEY_FROM_LON, fm
					.getFromGeoPoint().getLongitude());
		} else {
			intent.putExtra("StartIsExisting", false);
		}
		if (!fm.getDestination().isEmpty()) {
			intent.putExtra("DestinationIsExisting", true);
			intent.putExtra("To", fm.getToAdr());
			intent.putExtra(GreenNavDBAdapter.KEY_TO_LAT, fm.getToGeopoint()
					.getLatitude());
			intent.putExtra(GreenNavDBAdapter.KEY_TO_LON, fm.getToGeopoint()
					.getLongitude());
		} else {
			intent.putExtra("DestinationIsExisting", false);
		}
		intent.putExtra(GreenNavDBAdapter.KEY_BATTERY, fm.getBatteryLevel());
		intent.putExtra(GreenNavDBAdapter.KEY_CAR, fm.getCarType());
		intent.putExtra(GreenNavDBAdapter.KEY_RTYPE, fm.getRoutingType());

		setResult(RESULT_OK, intent);
		finish();
	}

}

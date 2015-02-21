package org.greennavigation.ui.activities;

import java.util.ArrayList;
import java.util.LinkedList;

import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.services.GreenNavAddressListAdapter;
import org.greennavigation.services.GreenNavDBAdapter;
import org.greennavigation.services.AddressThread.AddressResult;
import org.greennavigation.ui.R;
import org.greennavigation.utils.Constants;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

/**
 * this Class retrieves and displays post addresses {@link Activity}.
 */

public class ContactAddressesListActivity extends ListActivity implements
		Runnable {

	private GreenNavAddressListAdapter adapter;

	private ProgressDialog progressDialog;

	// private int pos;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme(android.R.style.Theme_Light);
		super.onCreate(savedInstanceState);

		String titel = getString(R.string.fetch_addresses_progressdialog_feching_addresses_string);
		String message = getString(R.string.fetch_addresses_progressdialog_wait_string);
		progressDialog = ProgressDialog.show(ContactAddressesListActivity.this,
				titel, message, false);
		progressDialog.setCancelable(true);
		Thread thread = new Thread(this);

		thread.start();

		// progressDialog.dismiss();

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setListAdapter(adapter);
			progressDialog.dismiss();

		}
	};

	private static LinkedList<GreenNavAddress> addressesFromAddressBook;

	/**
	 * Call this method to retrieve the postal address of this user_id.
	 * 
	 * @param id
	 * @return
	 */
	private final LinkedList<GreenNavAddress> getAddressFromAddressBook() {

		LinkedList<GreenNavAddress> addresses = new LinkedList<GreenNavAddress>();

		GreenNavAddress gAddress = null;

		Cursor idCursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (idCursor.moveToNext()) {

			gAddress = new GreenNavAddress();

			String inhabitant = ""
					+ idCursor
							.getString(idCursor
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
					+ " ";

			gAddress.setInhabitant(checkEmptyStrring(inhabitant));

			String id = idCursor.getString(idCursor
					.getColumnIndex(ContactsContract.Contacts._ID));

			String street = null;
			String city = null;
			String country = null;
			String postcode = null;
			String land = null;

			String addrPlace = ContactsContract.Data.CONTACT_ID + " = ? AND "
					+ ContactsContract.Data.MIMETYPE + " = ?";
			String[] addrPlaceParams = new String[] {
					id,
					ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
			Cursor addresseCursor = getContentResolver().query(
					ContactsContract.Data.CONTENT_URI, null, addrPlace,
					addrPlaceParams, null);

			while (addresseCursor.moveToNext()) {

				street = ""
						+ addresseCursor
								.getString(addresseCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));

				// if (street != null) {

				gAddress.setStreet(checkEmptyStrring(street));

				city = ""
						+ addresseCursor
								.getString(addresseCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
				gAddress.setCity(checkEmptyStrring(city));

				land = ""
						+ addresseCursor
								.getString(addresseCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
				gAddress.setFederalLand(checkEmptyStrring(land));

				postcode = ""
						+ addresseCursor
								.getString(addresseCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
				gAddress.setPostcode(checkEmptyStrring(postcode));

				country = ""
						+ addresseCursor
								.getString(addresseCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
				gAddress.setCountry(checkEmptyStrring(country));

				addresses.add(gAddress);

			}

			addresseCursor.close();
		}

		idCursor.close();

		return addresses;
	}

	private String checkEmptyStrring(String str) {

		if ((str == null) || (str.equals(null) || (str.equals("")))) {

			return " .";
		}
		return str;

	}

	/**
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 *      android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		try {
			if (v instanceof View) {

				View view = (View) v;

				if (view.getId() == R.id.container) {
					// pos=position;
					// final String stringID =v.getTag().toString();
					// TextView adresseView = (TextView) view
					// .findViewById(R.id.contactEntryText);

					GreenNavAddress gAddress = addressesFromAddressBook
							.get(position);

					ControllAddressDialog dialog = new ControllAddressDialog(
							this, new AddressResult() {
								public void gotErrorMessage(String msg) {
								}

								public void gotAdressCandidates(
										ArrayList<GreenNavAddress> addressCandidatesList) {

									GreenNavAddress gna = addressCandidatesList
											.get(0);

									// currentDestinationGP = gna.getGeoPoint();
									Log.e("GN" + getClass().getSimpleName(),
											"!;)! ++++  " + gna);
									// Intent intent = new Intent();
									Intent intent = new Intent(
											ContactAddressesListActivity.this,
											MainActivity.class);
									// Store values between activities here.
									boolean startflag = PreferenceManager
											.getDefaultSharedPreferences(
													ContactAddressesListActivity.this)
											.getBoolean(
													Constants.startContacAdrtflag,
													false);
									if (startflag) {
										// Toast.makeText(ContactAddressesListActivity.this,
										// "start true",
										// Toast.LENGTH_LONG).show();
										// HelperClass.showText(getApplicationContext(),
										// "start true");

										// Store values between activities here.
										PreferenceManager
												.getDefaultSharedPreferences(
														ContactAddressesListActivity.this)
												.edit()
												.putBoolean(
														Constants.startContacAdrtflag,
														false).commit();

										if (gna != null) {
											intent.putExtra("StartIsExisting",
													true);
											intent.putExtra("From", gna + "");
											intent.putExtra(
													GreenNavDBAdapter.KEY_FROM_LAT,
													gna.getLatitude());
											intent.putExtra(
													GreenNavDBAdapter.KEY_FROM_LON,
													gna.getLongitude());
										} else {
											intent.putExtra("StartIsExisting",
													false);
										}
										setResult(RESULT_OK, intent);
										finish();
									}

									// Store values between activities here.
									boolean destflag = PreferenceManager
											.getDefaultSharedPreferences(
													ContactAddressesListActivity.this)
											.getBoolean(
													Constants.destContactAdrflag,
													false);
									if (destflag) {

										// Store values between activities here.
										PreferenceManager
												.getDefaultSharedPreferences(
														ContactAddressesListActivity.this)
												.edit()
												.putBoolean(
														Constants.destContactAdrflag,
														false).commit();

										if (gna != null) {
											intent.putExtra(
													"DestinationIsExisting",
													true);
											intent.putExtra("To",
													gna.toString());
											intent.putExtra(
													GreenNavDBAdapter.KEY_TO_LAT,
													gna.getLatitude());
											intent.putExtra(
													GreenNavDBAdapter.KEY_TO_LON,
													gna.getLongitude());
										} else {
											intent.putExtra(
													"DestinationIsExisting",
													false);
										}
										setResult(RESULT_OK, intent);

										finish();
									}

									// Store values between activities here.
									boolean overtflag = PreferenceManager
											.getDefaultSharedPreferences(
													ContactAddressesListActivity.this)
											.getBoolean(
													Constants.toOverContacAdrtflag,
													false);
									if (overtflag) {

										PreferenceManager
												.getDefaultSharedPreferences(
														ContactAddressesListActivity.this)
												.edit()
												.putBoolean(
														Constants.toOverContacAdrtflag,
														false).commit();

										if (gna != null) {
											intent.putExtra(
													"DestinationIsExisting",
													true);
											intent.putExtra("To",
													gna.toString());
											intent.putExtra(
													GreenNavDBAdapter.KEY_TO_LAT,
													gna.getLatitude());
											intent.putExtra(
													GreenNavDBAdapter.KEY_TO_LON,
													gna.getLongitude());
										} else {
											intent.putExtra(
													"DestinationIsExisting",
													false);
										}
										setResult(RESULT_OK, intent);

										finish();
									}

								}
							}, gAddress.getAddresseString() + "");
					dialog.show(); // finish();
				}

			}
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e + "");
		}
		super.onListItemClick(l, v, position, id);
	}

	public void run() {
		try {

			if (addressesFromAddressBook == null) {
				addressesFromAddressBook = getAddressFromAddressBook();
			}

			adapter = new GreenNavAddressListAdapter(
					ContactAddressesListActivity.this, addressesFromAddressBook);
			if (handler != null) {
				handler.sendEmptyMessage(0);
			}

		} catch (Exception e) {
			Log.e("GN" + getClass().getSimpleName(), e.toString());
		}

	}

}

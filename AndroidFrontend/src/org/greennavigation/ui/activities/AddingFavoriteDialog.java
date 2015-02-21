package org.greennavigation.ui.activities;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.client.ClientProtocolException;
import org.greennavigation.model.FavoritesModel;
import org.greennavigation.model.GeocodingManager;
import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.ui.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * This class creates a dialog which creates favorites. The class validates
 * addresses and persists them to database.
 */
public class AddingFavoriteDialog extends Dialog implements Runnable {

	private GeocodingManager geocodingManager;
	private FavoritesActivity favoritesActivity;

	private ProgressDialog progressDialog;

	private String startName;
	private String destName;
	private ArrayList<GreenNavAddress> startAddressesCandidates;
	private ArrayList<GreenNavAddress> destinationAddressesCandidates;
	protected String finaleFavName;
	protected String finalCarType;
	protected int finalBatteryLevel;
	protected int finalRType;
	private boolean threadIsStopped = false;

	private final static int NOMINATIM = 0;
	private final static int TOAST = 1;

	/**
	 * Public constructor to create a favorite dialog. 
	 * @param favoritesActivity
	 */
	public AddingFavoriteDialog(FavoritesActivity favoritesActivity) {
		super(favoritesActivity, android.R.style.Theme_Dialog);

		this.geocodingManager = new GeocodingManager(favoritesActivity);
		this.favoritesActivity = favoritesActivity;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(favoritesActivity.getString(R.constant.LOGTAG)
				+ getClass().getSimpleName(),
				"Dialog 'Adding favorite' has been requested.");
		View v = getLayoutInflater().inflate(R.layout.adding_favorite_dialog,
				null);

		setContentView(v);
		setTitle(R.string.adding_favorite_title);

		final RadioGroup radioGroup = (RadioGroup) v
				.findViewById(R.id.FavoriteRadioGroupRouteRange);
		final RadioButton rbRoute = (RadioButton) v
				.findViewById(R.id.RadioButtonRoute);
		final RadioButton rbRange = (RadioButton) v
				.findViewById(R.id.RadioButtonRange);
		final EditText tvFavoriteName = (EditText) v
				.findViewById(R.id.favoriteName);
		final EditText tvFavoriteStart = (EditText) v
				.findViewById(R.id.favoriteStart);
		final ImageView ivFavoriteStartButtonGPS = (ImageView) v
				.findViewById(R.id.favoriteStartButtonGPS);
		final EditText tvFavoriteDestination = (EditText) v
				.findViewById(R.id.favoriteDestination);
		final Spinner spFavoriteCarType = (Spinner) v
				.findViewById(R.id.favoriteCarType);
		final TextView tvBatteryLevelProgressText = (TextView) v
				.findViewById(R.id.favoriteBatteryLevelText);
		final SeekBar sbBatteryLevel = (SeekBar) v
				.findViewById(R.id.favoriteBatteryLevelSeekbar);
		Button buttonOK = (Button) v
				.findViewById(R.id.adding_favorite_button_ok);
		Button buttonCancel = (Button) v
				.findViewById(R.id.adding_favorite_button_ok);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				tvFavoriteDestination.setEnabled(!rbRange.isChecked());
			}
		});

		ivFavoriteStartButtonGPS.setOnClickListener(new View.OnClickListener() {
			// TODO to be implemented
			public void onClick(View v) {
				if (tvFavoriteStart.isEnabled()) {
					tvFavoriteStart.setText(R.string.gps_current_position);
					tvFavoriteStart.setEnabled(false);
				} else {
					tvFavoriteStart.setText(null);
					tvFavoriteStart.setEnabled(true);
				}

			}
		});

		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
				favoritesActivity, R.array.carTypesValues,
				android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spFavoriteCarType.setAdapter(adapter);

		tvBatteryLevelProgressText.setText(favoritesActivity
				.getString(R.default_value.default_battery_level)
				+ " %");

		sbBatteryLevel.setProgress(Integer.valueOf(favoritesActivity
				.getString(R.default_value.default_battery_level)));
		sbBatteryLevel
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {
						// unnecessary
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
						// unnecessary
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						tvBatteryLevelProgressText.setText(String
								.valueOf(progress)
								+ " %");
					}
				});
		buttonOK = (Button) v.findViewById(R.id.adding_favorite_button_ok);
		buttonOK.setText(R.string.button_value_OK);
		buttonOK.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log
						.i(favoritesActivity.getString(R.constant.LOGTAG)
								+ AddingFavoriteDialog.this.getClass()
										.getSimpleName(),
								"Positive Button has been pushed - Trying to create a new favorite");

				finalRType = rbRoute.isChecked() ? 0 : 1;
				finaleFavName = tvFavoriteName.getText().toString();
				AddingFavoriteDialog.this.startName = tvFavoriteStart.getText()
						.toString();
				AddingFavoriteDialog.this.destName = rbRange.isChecked() ? null
						: tvFavoriteDestination.getText().toString();
				finalCarType = (String) spFavoriteCarType.getSelectedItem();
				finalBatteryLevel = sbBatteryLevel.getProgress();

				// check if there's need to validate addresses at all:
				// if there's startName choosen to be current gps position and
				// it's a range prediction, there's no need to validate
				// addresses.
				Log.i(favoritesActivity.getString(R.constant.LOGTAG)
						+ AddingFavoriteDialog.this.getClass().getSimpleName(),
						"startName: " + startName);
				Log.i(favoritesActivity.getString(R.constant.LOGTAG)
						+ AddingFavoriteDialog.this.getClass().getSimpleName(),
						"destName: " + destName);
				if (AddingFavoriteDialog.this.startName
						.equals(favoritesActivity
								.getString(R.string.gps_current_position))) {
					if (AddingFavoriteDialog.this.destName == null) {
						favoritesActivity.createFavorite(new FavoritesModel(
								finaleFavName, new GreenNavAddress(),
								new GreenNavAddress(), finalCarType,
								finalBatteryLevel, finalRType, null, null,
								null, null));
						dismiss();
						return;
					}
				}

				// check if all necessary parameter are given
				// else return an error message
				String errMsg = checkInputParameter(finalRType, finaleFavName,
						startName, destName);
				if (errMsg != null) {
					Toast
							.makeText(favoritesActivity, errMsg,
									Toast.LENGTH_LONG).show();
					return;
				}
				final Thread addressesThread = new Thread(
						AddingFavoriteDialog.this);

				progressDialog = new ProgressDialog(favoritesActivity);
				progressDialog.setMessage(favoritesActivity
						.getString(R.string.waiting_message));
				progressDialog.setTitle(favoritesActivity
						.getString(R.string.check_addresses));
				progressDialog.setOnCancelListener(new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						Log
								.v(favoritesActivity
										.getString(R.constant.LOGTAG)
										+ getClass().getSimpleName(),
										"Thread has been stopped. Address candidates will not be delivered. ");
						threadIsStopped = true;
					}
				});
				progressDialog.show();

				addressesThread.start();

			}
		});
		buttonCancel = (Button) v
				.findViewById(R.id.adding_favorite_button_cancel);
		buttonCancel.setText(R.string.button_value_Cancel);
		buttonCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log
						.i(favoritesActivity.getString(R.constant.LOGTAG)
								+ getClass().getSimpleName(),
								"Negative Button has been pushed - Creating a new favorite has been canceled");
				cancel();

			}
		});

	}

	private String checkInputParameter(int rType, String favName,
			String startName, String destName) {
		Log.i(favoritesActivity.getString(R.constant.LOGTAG)
				+ getClass().getSimpleName(), "Route type: " + rType);
		Log.i(favoritesActivity.getString(R.constant.LOGTAG)
				+ getClass().getSimpleName(), "Favorite name: " + favName);
		Log.i(favoritesActivity.getString(R.constant.LOGTAG)
				+ getClass().getSimpleName(), "Start name: " + startName);
		Log.i(favoritesActivity.getString(R.constant.LOGTAG)
				+ getClass().getSimpleName(), "Destination name: " + destName);

		String errorMessage = favoritesActivity
				.getString(R.string.error_message_adding_favorite);
		ArrayList<String> missing = new ArrayList<String>();

		switch (rType) {
		// ROUTE is chosen
		case 0:
			if (favName.equals(""))
				missing.add(favoritesActivity
						.getString(R.string.favorite_dialog_favorite_name));
			if (startName.equals(""))
				missing.add(favoritesActivity
						.getString(R.string.favorite_dialog_start));
			if (destName.equals(""))
				missing.add(favoritesActivity
						.getString(R.string.favorite_dialog_destination));
			break;
		// RANGE is chosen
		case 1:
			if (favName.equals(""))
				missing.add(favoritesActivity
						.getString(R.string.favorite_dialog_favorite_name));
			if (startName.equals(""))
				missing.add(favoritesActivity
						.getString(R.string.favorite_dialog_start));
			break;
		default:
			break;
		}
		StringBuilder sb = new StringBuilder();

		if (missing.size() == 0)
			return null;

		Iterator<String> iter = missing.iterator();
		do {
			String s = iter.next();
			Log.i(favoritesActivity.getString(R.constant.LOGTAG)
					+ getClass().getSimpleName(),
					"To create a new favorite, the following field must have a value: "
							+ s);
			sb.append(s);
			if (iter.hasNext())
				sb.append(",");
		} while (iter.hasNext());

		return errorMessage + sb.toString();

	}

	public void run() {
		Log.i(favoritesActivity.getString(R.constant.LOGTAG)
				+ getClass().getSimpleName(),
				"Trying to find address candidates.");

		ArrayList<GreenNavAddress> startAddr = null;
		ArrayList<GreenNavAddress> destAddr = null;
		try {
			if (startName != favoritesActivity
					.getString(R.string.gps_current_position)) {
				startAddr = geocodingManager
						.getAddressCandidates(AddingFavoriteDialog.this.startName);
				Log.i(favoritesActivity.getString(R.constant.LOGTAG)
						+ getClass().getSimpleName(),
						"Got start address candidates: " + startAddr.size());
				if (startAddr.size() == 0)
					startAddr = null;
			}

			if (destName != null) {
				destAddr = geocodingManager.getAddressCandidates(destName);
				Log.i(favoritesActivity.getString(R.constant.LOGTAG)
						+ getClass().getSimpleName(),
						"Got destination address candidates: "
								+ destAddr.size());
				if (destAddr.size() == 0)
					destAddr = null;
			}
		} catch (ClientProtocolException e) {
			Log.e(favoritesActivity.getString(R.constant.LOGTAG)
					+ getClass().getSimpleName(), "Network Error: "
					+ e.getMessage());
			progressDialog.dismiss();
			Message msg = new Message();
			msg.what = TOAST;
			msg.arg1 = R.string.error_message_network_error;
			handler.sendMessage(msg);

		} catch (ConnectException e) {
			Log.e(favoritesActivity.getString(R.constant.LOGTAG)
					+ getClass().getSimpleName(), "Network Error: "
					+ e.getMessage());
			Message msg = new Message();
			msg.what = TOAST;
			msg.arg1 = R.string.error_message_network_error;
			handler.sendMessage(msg);

			progressDialog.dismiss();
		} catch (IllegalArgumentException e) {
			Log.e(favoritesActivity.getString(R.constant.LOGTAG)
					+ getClass().getSimpleName(), e.getMessage());
			progressDialog.dismiss();
		}

		if (!threadIsStopped) {
			Message message = new Message();
			startAddressesCandidates = startAddr;
			destinationAddressesCandidates = destAddr;
			message.what = NOMINATIM;
			handler.sendMessage(message);
		}
		threadIsStopped = false;
	}

	Handler handler = new Handler() {
		public void handleMessage(final Message msg) {

			switch (msg.what) {
			case NOMINATIM:
				handleNominatimResult();
				progressDialog.dismiss();
				break;
			case TOAST:
				AddingFavoriteDialog.this.favoritesActivity
						.runOnUiThread(new Runnable() {
							public void run() {
								Toast
										.makeText(
												AddingFavoriteDialog.this.favoritesActivity,
												msg.arg1, Toast.LENGTH_SHORT)
										.show();
							}
						});
				break;
			default:
				break;
			}

		};
	};

	private void showChooseDialog(ArrayAdapter<CharSequence> adapterStart,
			ArrayAdapter<CharSequence> adapterDest) {

		if ((adapterStart == null || adapterStart.getCount() == 0)
				&& (adapterDest == null || adapterDest.getCount() == 0)) {
			Toast
					.makeText(
							favoritesActivity,
							favoritesActivity
									.getString(R.string.error_message_empty_adress_candidates),
							Toast.LENGTH_LONG).show();
			return;
		}
		setContentView(R.layout.choose_address_dialog);

		final Spinner startSpinner = (Spinner) findViewById(R.id.spinnerStartAddresses);
		final TextView tvDestSpinner = (TextView) findViewById(R.id.tvSpinnerDestAddresses);
		final Spinner destSpinner = (Spinner) findViewById(R.id.spinnerDestAddresses);
		final TextView tvStartSpinner = (TextView) findViewById(R.id.tvSpinnerStartAddresses);
		Button buttonOK = (Button) findViewById(R.id.adding_favorite_button_ok);
		Button buttonCancel = (Button) findViewById(R.id.adding_favorite_button_cancel);

		if (adapterStart != null && adapterStart.getCount() != 0) {
			startSpinner.setAdapter(adapterStart);
		} else {
			tvStartSpinner.setVisibility(View.GONE);
			startSpinner.setVisibility(View.GONE);
		}

		if (adapterDest != null && adapterDest.getCount() != 0) {
			destSpinner.setAdapter(adapterDest);
		} else {
			tvDestSpinner.setVisibility(View.GONE);
			destSpinner.setVisibility(View.GONE);
		}

		buttonOK.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log
						.i(favoritesActivity.getString(R.constant.LOGTAG)
								+ getClass().getSimpleName(),
								"Adresses have been selected and commited. Creating favorite!");
				int startIndex = startSpinner.getSelectedItemPosition();
				int destIndex = destSpinner.getSelectedItemPosition();
				GreenNavAddress finalStartAddress;
				if (!startName.equals(favoritesActivity
						.getString(R.string.gps_current_position))) {
					finalStartAddress = AddingFavoriteDialog.this.startAddressesCandidates
							.get(startIndex);
					Log.i(favoritesActivity.getString(R.constant.LOGTAG)
							+ getClass().getSimpleName(), finalStartAddress
							.toString());
				} else {
					finalStartAddress = new GreenNavAddress();
				}

				if (destSpinner.getVisibility() != View.VISIBLE) {

					favoritesActivity.createFavorite(new FavoritesModel(
							finaleFavName, finalStartAddress,
							new GreenNavAddress(), finalCarType,
							finalBatteryLevel, finalRType));

				} else {
					GreenNavAddress finalDestAddress = AddingFavoriteDialog.this.destinationAddressesCandidates
							.get(destIndex);

					Log.i(favoritesActivity.getString(R.constant.LOGTAG)
							+ getClass().getSimpleName(),
							finalStartAddress != null ? finalStartAddress
									.toString() : "");
					Log.i(favoritesActivity.getString(R.constant.LOGTAG)
							+ getClass().getSimpleName(),
							finalDestAddress != null ? finalDestAddress
									.toString() : "");

					favoritesActivity.createFavorite(new FavoritesModel(
							finaleFavName, finalStartAddress, finalDestAddress,
							finalCarType, finalBatteryLevel, finalRType));

				}

				AddingFavoriteDialog.this.dismiss();
			}

		});
		buttonCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.i(favoritesActivity.getString(R.constant.LOGTAG)
						+ getClass().getSimpleName(),
						"Creating favorite has been canceled by User.");
				AddingFavoriteDialog.this.dismiss();
			}
		});
	}

	private void handleNominatimResult() {

		ArrayAdapter<CharSequence> adapterStart = null;
		ArrayAdapter<CharSequence> adapterDest = null;

		String[] startAddressesStrings = null;
		if (this.startAddressesCandidates != null) {
			startAddressesStrings = new String[this.startAddressesCandidates
					.size()];
			for (int i = 0; i < this.startAddressesCandidates.size(); i++) {
				startAddressesStrings[i] = this.startAddressesCandidates.get(i)
						.toString();
			}

			adapterStart = new ArrayAdapter<CharSequence>(favoritesActivity,
					android.R.layout.simple_spinner_item, startAddressesStrings);
		} else {
			if (!AddingFavoriteDialog.this.startName.equals(favoritesActivity
					.getString(R.string.gps_current_position))) {
				Toast
						.makeText(
								favoritesActivity,
								favoritesActivity
										.getString(R.string.error_message_empty_adress_candidates)
										+ startName, Toast.LENGTH_LONG).show();
				return;
			}

		}

		String[] destinationAddressesStrings = null;
		if (finalRType != 1) {

			if (this.destinationAddressesCandidates != null) {
				destinationAddressesStrings = new String[this.destinationAddressesCandidates
						.size()];
				for (int i = 0; i < this.destinationAddressesCandidates.size(); i++) {
					destinationAddressesStrings[i] = this.destinationAddressesCandidates
							.get(i).toString();
				}

				adapterDest = new ArrayAdapter<CharSequence>(favoritesActivity,
						android.R.layout.simple_spinner_item,
						destinationAddressesStrings);

			} else {
				Toast
						.makeText(
								favoritesActivity,
								favoritesActivity
										.getString(R.string.error_message_empty_adress_candidates)
										+ destName, Toast.LENGTH_LONG).show();
				return;
			}
		}
		showChooseDialog(adapterStart, adapterDest);
	}
}

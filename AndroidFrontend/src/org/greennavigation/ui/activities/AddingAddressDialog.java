package org.greennavigation.ui.activities;

import java.util.ArrayList;

import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.services.AddressThread;
import org.greennavigation.services.AddressThread.AddressResult;
import org.greennavigation.ui.R;
import org.greennavigation.utils.HelperClass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * A dialog class which helps to enter and validate addresses.
 */
public class AddingAddressDialog extends Dialog {
	private Context context;
	private LayoutInflater inflater;
	private AddressResult ar;

	View addressDialoglayout;
	EditText etAddress;
	Spinner spAddressCandidates;
	Button buttonOK;
	Button buttonCancel;
	ArrayList<GreenNavAddress> result;

	Handler handler;

	protected AddingAddressDialog(Context context, AddressResult ar) {
		super(context);
		this.context = context;
		this.ar = ar;
		this.inflater = LayoutInflater.from(context);
		this.result = new ArrayList<GreenNavAddress>();
		this.handler = new Handler();
	}

	protected void onCreate(Bundle savedInstanceState) {

		this.addressDialoglayout = this.inflater.inflate(
				R.layout.address_dialog,
				(ViewGroup) findViewById(R.id.ll_address_dialog));

		setContentView(R.layout.address_dialog);

		this.etAddress = (EditText) addressDialoglayout
				.findViewById(R.id.et_address_dialog);
		this.spAddressCandidates = (Spinner) addressDialoglayout
				.findViewById(R.id.sp_address_dialog);
		this.buttonOK = (Button) findViewById(R.id.adding_address_button_ok);
		this.buttonCancel = (Button) findViewById(R.id.adding_address_button_cancel);

		this.buttonOK.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(!HelperClass.isAppConnected(AddingAddressDialog.this.context)){
					Log.e("GN", "No internet connection");
					return;
				}
				etAddress = (EditText) findViewById(R.id.et_address_dialog);
				spAddressCandidates = (Spinner) findViewById(R.id.sp_address_dialog);
				String addressString = etAddress.getText().toString();
				Log.i("GN", "Entered address string: " + addressString);
				if (addressString != null && addressString.length() != 0) {
					if (spAddressCandidates.getVisibility() == View.GONE) {

						Log.i("GN", "Got address string - try to validate it: "
								+ addressString + " [size: "
								+ addressString.length() + " ]");
						final ProgressDialog pd = ProgressDialog.show(
								AddingAddressDialog.this.context,
								AddingAddressDialog.this.context
										.getString(R.string.check_addresses),
								AddingAddressDialog.this.context
										.getString(R.string.waiting_message));
						pd.setCancelable(true);
						AddressResult addressResult = new AddressResult() {

							@Override
							public void gotErrorMessage(final String msg) {
								pd.cancel();
								final Runnable showError = new Runnable() {
									public void run() {
										Toast
												.makeText(
														AddingAddressDialog.this.context,
														msg, 0).show();
									}
								};
								AddingAddressDialog.this.handler
										.post(showError);
							}

							@Override
							public void gotAdressCandidates(
									final ArrayList<GreenNavAddress> addressCandidatesList) {
								AddingAddressDialog.this.result
										.addAll(addressCandidatesList);
								Log.i("GN", "Received address candidates: "
										+ String.valueOf(addressCandidatesList
												.size()));

								if (addressCandidatesList.size() != 0) {
									final Runnable viewChanger = new Runnable() {
										public void run() {
											spAddressCandidates
													.setAdapter(generateAdapter(addressCandidatesList));
											etAddress.setVisibility(View.GONE);
											spAddressCandidates
													.setVisibility(View.VISIBLE);
										}
									};
									AddingAddressDialog.this.handler
											.post(viewChanger);
									pd.dismiss();

								} else {
									final Runnable showError = new Runnable() {
										public void run() {
											Toast
													.makeText(
															AddingAddressDialog.this.context,
															AddingAddressDialog.this.context
																	.getString(R.string.error_message_empty_adress_candidates),
															0).show();
										}
									};
									AddingAddressDialog.this.handler
											.post(showError);
									pd.dismiss();
								}
							}
						};
						new AddressThread(AddingAddressDialog.this.context,
								addressString, addressResult).start();
					} else {
						int i = spAddressCandidates.getSelectedItemPosition();
						Log.i("GN", "Choose address candidate nr: " + i);
						GreenNavAddress gna = AddingAddressDialog.this.result
								.get(i);
						ArrayList<GreenNavAddress> list = new ArrayList<GreenNavAddress>();
						list.add(gna);
						ar.gotAdressCandidates(list);
						dismiss();
					}
				} else {
					final Runnable showError = new Runnable() {
						public void run() {
							Toast
									.makeText(
											AddingAddressDialog.this.context,
											AddingAddressDialog.this.context
													.getString(R.string.error_message_empty_adress_candidates),
											0).show();
						}
					};
					AddingAddressDialog.this.handler.post(showError);
				}

			}
		});

		this.buttonCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				cancel();
			}
		});

		setTitle(R.string.adding_address_dialog_title);

	}

	/**
	 * Generates an arrayAdapter with {@link GreenNavAddress} to choose from.
	 * 
	 * @param addressList
	 *            - an {@link ArrayList} of {@link GreenNavAddress}, which will
	 *            be contained in the finaly {@link ArrayAdapter}
	 * @return an ArrayAdapter to set for a {@link Spinner}
	 */
	private ArrayAdapter<CharSequence> generateAdapter(
			ArrayList<GreenNavAddress> addressList) {

		String[] adapterStringArray = new String[addressList.size()];
		int i = 0;
		for (GreenNavAddress greenNavAddress : addressList) {
			adapterStringArray[i++] = greenNavAddress.toString();
		}
		return new ArrayAdapter<CharSequence>(AddingAddressDialog.this.context,
				android.R.layout.simple_spinner_item, adapterStringArray);
	}

}

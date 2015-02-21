package org.greennavigation.ui.activities;

import java.util.ArrayList;

import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.services.AddressThread;
import org.greennavigation.services.AddressThread.AddressResult;
import org.greennavigation.ui.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ControllAddressDialog extends Dialog {
	private Context context;
	private LayoutInflater inflater;
	private AddressResult addressresult;

	View addressDialoglayout;
	EditText etAddress;
	Spinner spAddressCandidates;
	Button buttonOK;
	Button buttonCancel;
	ArrayList<GreenNavAddress> result;
	private String addressString;
	Handler handler;

	protected ControllAddressDialog(Context context, AddressResult addressresult,
			String addressString) {
		super(context);
		this.context = context;
		this.addressresult = addressresult;
		setAddressString(addressString);
		this.inflater = (LayoutInflater) context
				.getSystemService("layout_inflater");
		this.result = new ArrayList<GreenNavAddress>();
		this.handler = new Handler();
	}

	protected void onCreate(Bundle savedInstanceState) {

		this.addressDialoglayout = this.inflater.inflate(
				R.layout.address_dialog,
				(ViewGroup) findViewById(R.id.ll_address_dialog));

		setContentView(R.layout.controlladdressdialog);

		this.etAddress = (EditText) findViewById(R.id.et_address_dialog);

		this.spAddressCandidates = (Spinner) addressDialoglayout
				.findViewById(R.id.sp_address_dialog);
		this.buttonOK = (Button) findViewById(R.id.adding_address_button_ok);
		this.buttonCancel = (Button) findViewById(R.id.adding_address_button_cancel);
		this.etAddress.setText(getAddressString());
		
		View.OnClickListener OkonClickListener = new View.OnClickListener() {

			public void onClick(View v) {
				 
				  Editable editable = etAddress.getText(); 
		            
		           if (!TextUtils.isEmpty(editable)) {
					//addressString=editable.toString();
					setAddressString(editable.toString());
					checkAddresses(getAddressString());
					Log.i(ControllAddressDialog.this.getClass().getSimpleName()      ,     ""+editable.toString());
				}else {
					Log.i(ControllAddressDialog.this.getClass().getSimpleName(),  "text ist null");
				}
				
				 

			}

		};
		this.buttonOK.setOnClickListener(OkonClickListener);

		View.OnClickListener cancelBtnonClickListener = new View.OnClickListener() {

			public void onClick(View v) {
				cancel();
			}
		};
		this.buttonCancel.setOnClickListener(cancelBtnonClickListener);
		String title = context.getString(R.string.edit_address_dialog_title);
		setTitle("         "+title);

	}

	public void checkAddresses(String addressString) {
		Log.i("GN", "Entered address string");
		//etAddress = (EditText) findViewById(R.id.et_address_dialog);
		spAddressCandidates = (Spinner) findViewById(R.id.sp_address_dialog);
          
		// etAddress.setText(addressString);

		// String addressString = etAddress.getText().toString();
		Log.i("GN", "Entered address string: " + addressString);
		if (addressString != null && addressString.length() != 0) {
			if (spAddressCandidates.getVisibility() == View.GONE) {

				Log.i("GN", "Got address string - try to validate it: "
						+ addressString + " [size: " + addressString.length()
						+ " ]");
				final ProgressDialog pd = ProgressDialog.show(
						ControllAddressDialog.this.context,
						ControllAddressDialog.this.context
								.getString(R.string.check_addresses),
						ControllAddressDialog.this.context
								.getString(R.string.waiting_message));
				pd.setCancelable(true);
				AddressResult addressResult = new AddressResult() {

					@Override
					public void gotErrorMessage(final String msg) {
						pd.cancel();
						final Runnable showError = new Runnable() {
							public void run() {
								Toast.makeText(
										ControllAddressDialog.this.context,
										msg, 0).show();
							}
						};
						ControllAddressDialog.this.handler.post(showError);
					}

					@Override
					public void gotAdressCandidates(
							final ArrayList<GreenNavAddress> addressCandidatesList) {
						ControllAddressDialog.this.result
								.addAll(addressCandidatesList);
						Log.i("GN",
								"Received address candidates: "
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
							ControllAddressDialog.this.handler
									.post(viewChanger);
							pd.dismiss();

						} else {
							final Runnable showError = new Runnable() {
								public void run() {
									Toast.makeText(
											ControllAddressDialog.this.context,
											ControllAddressDialog.this.context
													.getString(R.string.error_message_empty_adress_candidates),
											0).show();
								}
							};
							ControllAddressDialog.this.handler.post(showError);
							pd.dismiss();
						}
					}
				};
				new AddressThread(ControllAddressDialog.this.context,
						addressString, addressResult).start();

				 
			} else {
				int i = spAddressCandidates.getSelectedItemPosition();
				Log.i("GN", "Choose address candidate nr: " + i);
				GreenNavAddress gna = ControllAddressDialog.this.result.get(i);
				ArrayList<GreenNavAddress> list = new ArrayList<GreenNavAddress>();
				list.add(gna);
				addressresult.gotAdressCandidates(list);
				dismiss();
			}
		} else {
			final Runnable showError = new Runnable() {
				public void run() {
					Toast.makeText(
							ControllAddressDialog.this.context,
							ControllAddressDialog.this.context
									.getString(R.string.error_message_empty_adress_candidates),
							0).show();
				}
			};
			ControllAddressDialog.this.handler.post(showError);
		}
	}

	 

	ArrayAdapter<CharSequence> generateAdapter(
			ArrayList<GreenNavAddress> addressList) {

		String[] adapterStringArray = new String[addressList.size()];
		int i = 0;
		for (GreenNavAddress greenNavAddress : addressList) {
			adapterStringArray[i++] = greenNavAddress.toString();
		}
		return new ArrayAdapter<CharSequence>(
				ControllAddressDialog.this.context,
				android.R.layout.simple_spinner_item, adapterStringArray);
	}

	/**
	 * @param addressString
	 *            the addressString to set
	 */
	public void setAddressString(String addressString) {
		this.addressString = addressString;
	}

	/**
	 * @return the addressString
	 */
	public String getAddressString() {
		return this.addressString;
	}
	protected void changeVisibility() {

	}
}

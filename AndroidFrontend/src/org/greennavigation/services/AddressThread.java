package org.greennavigation.services;

import java.net.ConnectException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.greennavigation.model.GeocodingManager;
import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.ui.R;

import android.content.Context;
/**
 * A service class which is validating a given address string. 
 */
public class AddressThread extends Thread {
	
	private Context context;
	private String addressString;
	private AddressResult addressResult;
	private GeocodingManager geocodingManager;
	
	/**
	 * Public constructor to create a AddressThread.
	 * @param context - the context to work on
	 * @param addressString - the address string to resolved
	 * @param ar - the {@link AddressResult} which is to be called when a result has been found
	 */
	public AddressThread(Context context, String addressString,  AddressResult ar) {
		this.context = context;
		this.addressResult = ar;
		this.addressString = addressString;
		this.geocodingManager = new GeocodingManager(context);
	}

	@Override
	public void run() {
		try {
			ArrayList<GreenNavAddress> result = this.geocodingManager.getAddressCandidates(addressString);
			if(result!=null){
				this.addressResult.gotAdressCandidates(result);
			}
			else{
				this.addressResult.gotErrorMessage(context.getString(R.string.error_message_empty_adress_candidates)+"1");
			}
		} catch (ConnectException e) {
			this.addressResult.gotErrorMessage(context.getString(R.string.error_message_network_error));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			this.addressResult.gotErrorMessage(context.getString(R.string.error_message_network_error));
		}
	
	}
	/**
	 * A interface to receive address results and error messages from a address thread. 
	 */
	public static abstract class AddressResult {
		public abstract void gotAdressCandidates(ArrayList<GreenNavAddress> addressCandidatesList);
		public abstract void gotErrorMessage(String msg);
		
	}

}

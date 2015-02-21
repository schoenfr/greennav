package org.greennavigation.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.greennavigation.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class HelperClass {

	private static final String LOG = "HelperClass";

	/**
	 * make sure we have a mounted SDCard + this method creates the App File to
	 * store maps binaries. like mucundumgebung.map (for munich)
	 *  

	 * @param context
	 *            {@link Context}
	 */
	public static void CreateAppFile(Context context) {
		try {

			// make sure we have a mounted SDCard
			if (!Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				// they don't have an SDCard, give them an error message and
				// quit
				showErorrSDCardDialog(context);
				Log.e(LOG,
						"Don't have an SDCard, give them an error message and quit ! ");

			} else {
				// there's an SDCard available, continue
				// File root = Environment.getExternalStorageDirectory();

				String directoryname = context.getString(R.string.app_name);
				String dirName = "/sdcard/" + directoryname + "";
				File root = new File(dirName);
				boolean exists = root.exists();
				if (!exists) {
					root.mkdirs();
				}

			}

		} catch (Exception e) {
			Log.e(LOG, "Exception creating folder " + e.getMessage());
			{

				String message = e.getMessage();

				Log.d(LOG, message);

			}
		}

	}

	/**
	 * 
	 * 
	 * this Method create sd-card's Errors or rather exceptions.
	 * 
	 * @param context
	 *            {@link Context}
	 * @param exceptionstr
	 *            {@link Exception} the message and details of Exception.
	 * @param {@link AlertDialog}
	 */

	public static AlertDialog getErorrSDCardDialog(String text, String title,
			final Context context) {

		String yesstr = context.getString(R.string.yes_message);

		return new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.stat_notify_sdcard)
				.setTitle(title)
				.setMessage(text)
				.setPositiveButton(yesstr,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (context instanceof Activity) {
									Activity activity = (Activity) context;
									activity.finish();
								}

							}
						}).create();

	}
	public static AlertDialog getRouteDialog(String text, String title,
			final Context context) {
		
		String yesstr = context.getString(R.string.yes_message);
		
		return new AlertDialog.Builder(context)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(title)
		.setMessage(text)
		.setPositiveButton(yesstr,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int whichButton) {
				if (dialog!=null) {
					dialog.dismiss();
					
				}
				  
			}
		}).create();
		
	}
	public static AlertDialog getMapsDialog(String text, String title,
			final Context context) {
		
		return new AlertDialog.Builder(context)
		.setIcon(android.R.drawable.ic_dialog_info).setItems(null, new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				 
				
			}
		})
		.setTitle(title)
		.setMessage(text)
		 
		.create();
		
	}

	/**
	 * this dialog shows sd-card's Errors or rather exceptions.
	 * 
	 * @param context
	 *            {@link Context}
	 * @param exceptionstr
	 *            {@link Exception} the message and details of Exception.
	 */
	public static void showErorrSDCardDialog(final Context context) {

		String err = context.getString(R.string.error_string);
		String errtext = context.getString(R.string.sdcarderror_dialog_string);
		getErorrSDCardDialog(errtext, err, context).show();
	}

	/**
	 * this methods checks the Internet connectivity
	 * 
	 * @param context
	 *            {@link Context}
	 * @return {@link Boolean}
	 */
	public static boolean isAppConnected(Context context) {
		try {

			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			// NetworkInfo info= conMgr.getActiveNetworkInfo();

			NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
			if (activeNetworkInfo != null) {

				boolean connected = (activeNetworkInfo != null
						&& activeNetworkInfo.isAvailable() && activeNetworkInfo
						.isConnected());

				// are we connected to the internet.
				if (connected) {
					Log.v("NetworkInfo", "Connected State");
					return true;
				} else {
					/*
					 * String text = context
					 * .getString(R.string.internetNotpresent_message);
					 * showText(context, text);
					 */
					Log.v(LOG, "Internet Connection Not Present :(");

					return false;
				}
			}
		} catch (Exception e) {
			Log.v(LOG, e.getMessage() + " :(");
		}

		return false;

	}

	public static InputStream convertStringToInputstream(String string) {

		if (string != null) {

			// byte[] bytes = string.getBytes(); // Mit Default Encoding
			byte[] bytes;
			try {
				bytes = string.getBytes("UTF-8");
				// Encoding, hier "UTF-8"
				InputStream is = (InputStream) new ByteArrayInputStream(bytes);

				return is;

			} catch (UnsupportedEncodingException e) {
				Log.e(LOG, e.toString());
			} // Mit gesetzen
		}
		return null;

	}

	public static String convertInputStreamToString(InputStream in) {

		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		try {

			int result = bis.read();
			while (result != -1) {
				byte b = (byte) result;
				buf.write(b);
				result = bis.read();
			}

		} catch (IOException e) {
			Log.v(LOG, e.getMessage() + " :(");
			return null;
		}
		return buf.toString();
	}

	/**
	 * @param is
	 * @return
	 */
	public static StringBuilder InputStreamToString(InputStream is) {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e1) {
			Log.e(LOG, e1.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e(LOG, e.getMessage());
			}
		}
		return sb;
	}
}

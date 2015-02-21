package org.greennavigation.services;

import java.util.LinkedList;

import org.greennavigation.model.GreenNavAddress;
import org.greennavigation.ui.R;
import org.greennavigation.ui.activities.ContactAddressesListActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * this class presents an adapter for {@link ContactAddressesListActivity}.
 * Common base class of common implementation for an Adapter that can be used in
 *  ListView (by implementing the specialized ListAdapter interface} .
 */

public class GreenNavAddressListAdapter extends BaseAdapter {

	private LinkedList<GreenNavAddress> addressesLis;

	public GreenNavAddressListAdapter(Context context,
			LinkedList<GreenNavAddress> linkedlist) {
		setContext(context);
		setAdrressesList(linkedlist);
	}

	private Context getContext() {
		return context;
	}

	private void setContext(Context context) {
		this.context = context;
	}

	/**
	 * How many items are in the data set represented by this Adapter
	 */
	public int getCount() {
		return getAdrressesList().size();
	}

	/**
	 * Are all items in this ListAdapter enabled? If yes it means all items are
	 * Selectable and Clickable. Returns True if all items are enabled
	 */
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	/**
	 * 
	 * @return String[] of phoneNr.
	 */
	private LinkedList<GreenNavAddress> getAdrressesList() {
		return addressesLis;
	}

	/**
	 * 
	 * @param numberListArray
	 *            String[] of phoneNr.
	 */
	private void setAdrressesList(LinkedList<GreenNavAddress> addressesLis) {
		this.addressesLis = addressesLis;
	}

	/**
	 * 
	 Get the data item associated with the specified position in the data set.
	 * 
	 * Parameters position Position of the item whose data we want within the
	 * adapter's data set.
	 * 
	 * Returns The data at the specified position.
	 */
	public Object getItem(int position) {
		return position;
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 * 
	 * Parameters position The position of the item within the adapter's data
	 * set whose row id we want.
	 * 
	 * Returns The id of the item at the specified position.
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Get a View that displays the data at the specified position in the data
	 * set. You can either create a View manually or inflate it from an XML
	 * layout file. When the View is inflated, the parent View (GridView,
	 * ListView...) will apply default layout parameters unless you use
	 * inflate(int, android.view.ViewGroup, boolean) to specify a root view and
	 * to prevent attachment to the root.
	 * 
	 * Parameters position The position of the item within the adapter's data
	 * set of the item whose view we want. convertView The old view to reuse, if
	 * possible. Note: You should check that this view is non-null and of an
	 * appropriate type before using. If it is not possible to convert this view
	 * to display the correct data, this method can create a new view. parent
	 * The parent that this view will eventually be attached to
	 * 
	 * Returns A View corresponding to the data at the specified position.
	 */

	public View getView(int position, View convertView, ViewGroup parent) {

		LinkedList<GreenNavAddress> asreesses = getAdrressesList();
		GreenNavAddress aDdresse = asreesses.get(position);

		String adrtext = aDdresse.getAddresseString();

		String name = aDdresse.getInhabitant();

		View container = LayoutInflater.from(getContext()).inflate(
				R.layout.contact_entry, parent, false);

		TextView titelview = (TextView) container
				.findViewById(R.id.contactEntryTitel);

		TextView textview = (TextView) container
				.findViewById(R.id.contactEntryText);

		if (titelview != null) {

			titelview.setText(" " + name);

		}

		if (textview != null) {

			textview.setText(" " + adrtext);

		}
		return container;
	}

	private Context context;
}

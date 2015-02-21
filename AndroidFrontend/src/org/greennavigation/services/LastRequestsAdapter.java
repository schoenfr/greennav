package org.greennavigation.services;

import org.greennavigation.ui.R;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * An adapter class to display all last requests. 
 */
public class LastRequestsAdapter extends SimpleCursorAdapter {

	private Cursor cursor;
	private LayoutInflater inflater;

	private int columnIndexRowID;
	private int columnIndexFromAddrString;
	private int columnIndexToAddrString;
	
	/**
	 * The public constructor to create a {@link LastRequestsAdapter}.
	 * @param context - the context to work on
	 * @param layout - passed through to super class (<i>unused here</i>)
	 * @param c - the cursor with the database entries to fill in the list
	 * @param from - passed through to super class (<i>unused here</i>)
	 * @param to - passed through to super class (<i>unused here</i>)
	 */
	public LastRequestsAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.cursor = c;
		this.inflater = LayoutInflater.from(context);

		this.columnIndexRowID = c.getColumnIndex(GreenNavDBAdapter.KEY_ROWID);
		this.columnIndexFromAddrString = c
				.getColumnIndex(GreenNavDBAdapter.KEY_FROM_ADDR_STRING);
		this.columnIndexToAddrString = c
				.getColumnIndex(GreenNavDBAdapter.KEY_TO_ADDR_STRING);

	}
	/**
	 * Returns a view object for each item in the list. 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("LastRequestAdapter", "getView()");
		ViewHolder viewHolder;

//		if (convertView == null) {
			convertView = this.inflater.inflate(
					R.layout.main_activity_last_request_row, null);
			viewHolder = new ViewHolder();
			viewHolder.id = -1;
			viewHolder.fromAddr = (TextView) convertView
					.findViewById(R.id.from);
			viewHolder.toAddr = (TextView) convertView.findViewById(R.id.to);
			viewHolder.arrow = (ImageView) convertView
					.findViewById(R.id.last_request_arrow);
			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
		this.cursor.moveToPosition(position);

		viewHolder.id = this.cursor.getInt(columnIndexRowID);
		viewHolder.fromAddr.setText(this.cursor
				.getString(this.columnIndexFromAddrString));
		viewHolder.toAddr.setText(this.cursor
				.getString(this.columnIndexToAddrString));
		viewHolder.arrow.setImageResource(R.drawable.arrow_alt);
		Log.i(getClass().getSimpleName(), viewHolder.fromAddr.getText() + " ("
				+ String.valueOf(viewHolder.fromAddr.getVisibility()) + ")");
		Log.i(getClass().getSimpleName(), viewHolder.toAddr.getText() + " ("
				+ String.valueOf(viewHolder.toAddr.getVisibility()) + ")");

		Log.i("LastRequestAdapter", "getView() end");
		return convertView;

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// unnecessary
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return null;
	}
	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}
	@Override
	public Object getItem(int arg0) {
		return arg0;
	}
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	/**
	 * An inner class which holds all Views of a last request row. Additionally, it holds the database id to identify 
	 * @author ME
	 *
	 */
	public class ViewHolder {
		public int id;
		public TextView fromAddr;
		public TextView toAddr;
		public ImageView arrow;
	}

	

}

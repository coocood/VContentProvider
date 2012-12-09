package org.coocood.vproviderdemo.util;

import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CursorBinder {
	private View view;
	private Cursor cursor;

	public CursorBinder(View view, Cursor cursor) {
		this.view = view;
		this.cursor = cursor;
	}

	public CursorBinder bindText(int viewId, String columnName) {
		TextView textView = (TextView) view.findViewById(viewId);
		textView.setText(cursor.getString(cursor.getColumnIndex(columnName)));
		return this;
	}
	public CursorBinder click(int viewId, OnClickListener listener) {
		View currentView = viewId == 0 ? view : view.findViewById(viewId);
		currentView.setOnClickListener(listener);
		return this;
	}
}

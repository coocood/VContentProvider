/*
 * Copyright 2013 - Ewan Chou (coocood@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.coocood.vcontentprovider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.CursorAdapter;

/**
 * This class is used for asynchronous cursor loading, but without the need to 
 * Use compatibility package and implement LoaderCallbacks. 
 * Just query on it, all things have bean taken care of.
 * 
 * Be sure to call changeCursor(null) in onDestory() method in the activity to release resources.
 * 
 * @author Ewan Chou
 *
 */
public abstract class VCursorAdapter extends CursorAdapter {
	private Context context;
	private Uri uri;
	private String[] projection;
	private String selection;
	private String[] selectionArgs;
	private String sortOrder;
	public VCursorAdapter(Context context) {
		super(context, null, false);
		this.context = context;
	}
	
	public void query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		this.uri = uri;
		this.projection = projection;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.sortOrder = sortOrder;
		loadCursor();
	}

	
	@SuppressLint("NewApi")
	private void loadCursor(){
		AsyncTask<Void,Void,Cursor> task = new AsyncTask<Void, Void, Cursor>(){
			@Override
			protected Cursor doInBackground(Void... params) {
				return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
			}
			@Override
			protected void onPostExecute(Cursor result) {
				changeCursor(result);
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			task.execute();
		}
	}
	
	@Override
	protected void onContentChanged() {
		loadCursor();
	}
}

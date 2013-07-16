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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.RemoteException;

public abstract class VContentProvider extends ContentProvider {
	private VSQLiteOpenHelper mOpenHelper;
	private SQLiteDatabase db;
	private static HashMap<String, ArrayList<String>> tableMap = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, ArrayList<String>> viewTablesMap = new HashMap<String, ArrayList<String>>();
	private HashSet<Uri> batchingUris;

	/**
	 * @param dbVersions
	 *            All create table and alter table add column definition goes to
	 *            an upgrade object put every upgrade object into the list, then
	 *            the database version is determined by the greatest upgrade
	 *            version.
	 * @param viewCreatorMap
	 *            ViewCreations will be dropped and recreated when the database
	 *            upgrades.
	 * @return database name
	 */
	protected abstract String addDatabaseVersionsViewsAndGetName(
			ArrayList<VDatabaseVersion> dbVersions,
			HashMap<String, VViewCreation> viewCreationMap);

	/**
	 * see also {@link #updateWithJSONObject}
	 */
	public static ContentProviderResult[] updateWithJSONArray(Context context,
			Uri uri, JSONArray array,
			LinkedHashMap<String, String> subJSONObjectMap)
			throws RemoteException, OperationApplicationException,
			JSONException {
		Uri baseUri = new Uri.Builder().scheme(uri.getScheme())
				.authority(uri.getAuthority()).build();
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			addOperations(operations, uri, baseUri, json, subJSONObjectMap);
		}
		return context.getContentResolver().applyBatch(uri.getAuthority(),
				operations);
	}

	/**
	 * If the JSONObject has any sub JSONObject, create a LinkedHashMap and
	 * put the key to the sub JSONObject and the corresponding table name as value in it.
	 * first in, first out(get updated).
	 */
	public static ContentProviderResult[] updateWithJSONObject(Context context,
			Uri uri, JSONObject json,
			LinkedHashMap<String, String> subJSONObjectMap)
			throws JSONException, RemoteException,
			OperationApplicationException {
		Uri baseUri = new Uri.Builder().scheme(uri.getScheme())
				.authority(uri.getAuthority()).build();
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		addOperations(operations, uri, baseUri, json, subJSONObjectMap);
		return context.getContentResolver().applyBatch(uri.getAuthority(),
				operations);
	}

	private static void addOperations(
			ArrayList<ContentProviderOperation> operations, Uri uri,
			Uri baseUri, JSONObject json,
			LinkedHashMap<String, String> subJSONObjectMap)
			throws JSONException {
		if (subJSONObjectMap != null) {
			for (String key : subJSONObjectMap.keySet()) {
				JSONObject subJson = json.getJSONObject(key);
				String subTable = subJSONObjectMap.get(key);
				operations.add(getOperation(baseUri, subJson, subTable));
			}
		}
		String table = getTableName(uri);
		operations.add(getOperation(baseUri, json, table));
	}

	private static ContentProviderOperation getOperation(Uri baseUri,
			JSONObject json, String table) throws JSONException {
		Uri uri = Uri.withAppendedPath(baseUri, table);
		ArrayList<String> columns = tableMap.get(table);
		ContentValues values = new ContentValues();
		for (int i = 0; i < columns.size(); i++) {
			String column = columns.get(i);
			if (json.has(column)) {
				String value = json.getString(column);
		
				// The id column index in the columns ArrayList is 0.
				// Put "_id" as the local id column name.
				values.put(i == 0 ? "_id" : column, value);
			}
		}
		return ContentProviderOperation.newUpdate(uri).withValues(values)
				.build();
	}

	@Override
	public boolean onCreate() {
		ArrayList<VDatabaseVersion> versions = new ArrayList<VDatabaseVersion>();
		HashMap<String, VViewCreation> viewMap = new HashMap<String, VViewCreation>();
		String databaseName = addDatabaseVersionsViewsAndGetName(versions,
				viewMap);
		Collections.sort(versions);
		mOpenHelper = new VSQLiteOpenHelper(getContext(), databaseName,
				versions, viewMap);
		for (VDatabaseVersion upgrade : versions) {
			for (VTableCreation tableCreation : upgrade.tableCreations) {
				ArrayList<String> columns = new ArrayList<String>();

				/*
				 * Add id column at first so we can retrieve it back later.
				 * later.
				 */
				columns.add(tableCreation.sourceIdName);

				for (VTableColumn column : tableCreation.columns) {
					columns.add(column.name);
				}
				tableMap.put(tableCreation.table, columns);
			}
			for (VTableColumn column : upgrade.newColumns) {
				tableMap.get(column.table).add(column.name);
			}
		}
		for (VViewCreation viewCreation : viewMap.values()) {
			ArrayList<String> viewTables = new ArrayList<String>();
			viewTables.add(viewCreation.childTable.table);
            for (String key : viewCreation.parentTables.keySet()) {
                VTableCreation vTableCreation = viewCreation.parentTables.get(key);
                viewTables.add(vTableCreation.table);
            }
            viewTablesMap.put(viewCreation.viewName, viewTables);
		}
		batchingUris = new HashSet<Uri>();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String table = getTableName(uri);
		String idPath = getIdPath(uri);
		db = mOpenHelper.getReadableDatabase();
		Cursor c = db.query(table, projection,
				finalSelection(idPath, selection), selectionArgs, null, null,
				sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		String table = getTableName(uri);
		String idPath = getIdPath(uri);
		return "vnd.android.cursor." + idPath == null ? "dir" : "item"
				+ "/vnd." + uri.getAuthority() + "." + table;
	}

	/**
	 * This implementation do not accept primary key value. If you want to
	 * insert a record with a primary key, call update instead, it will be
	 * inserted automatically if not exists.
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 *      android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String tableName = getTableName(uri);
		String idPath = getIdPath(uri);
		if (idPath != null)
			throw new IllegalArgumentException("Unknown URI " + uri);
		if (values.containsKey("_id"))
			throw new IllegalArgumentException(
					"insert method do not accept primary key, call update instead.");
		db = mOpenHelper.getWritableDatabase();
		long id = db.insert(tableName, null, values);
		if (id != -1) {
			notifyChange(uri, 1);
			return ContentUris.withAppendedId(uri, id);
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = getTableName(uri);
		String idPath = getIdPath(uri);
		db = mOpenHelper.getWritableDatabase();
		int deleteCount = db.delete(table, finalSelection(idPath, selection),
				selectionArgs);
		notifyChange(uri, deleteCount);
		return deleteCount;
	}

	/**
	 * If the primary key value is given, either in ContentValues or uri, this
	 * implementation will automatically intert the row if not exists.
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 *      android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int affectedRows = 0;
		String tableName = getTableName(uri);
		String idPath = getIdPath(uri);
		if (idPath == null)
			idPath = values.getAsString("_id");
		db = mOpenHelper.getWritableDatabase();

		if (idPath != null) {
			db.beginTransaction();
			try {
				affectedRows = db.update(tableName, values,
						finalSelection(idPath, selection), null);
				if (affectedRows == 0) {
					values.put("_id", Long.parseLong(idPath));
					if (db.insert(tableName, null, values) != -1)
						affectedRows = 1;
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		} else {
			affectedRows = db.update(tableName, values,
					finalSelection(idPath, selection), selectionArgs);
		}
		notifyChange(uri, affectedRows);
		return affectedRows;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		db = mOpenHelper.getWritableDatabase();
		int succeedCount = 0;
		db.beginTransaction();
		batchingUris.add(uri);
		try {
			for (ContentValues value : values) {
				if (insert(uri, value) != null)
					succeedCount++;
			}
			db.setTransactionSuccessful();
		} finally {
			batchingUris.remove(uri);
			db.endTransaction();
		}
		notifyChange(uri, succeedCount);
		return succeedCount;
	}

	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		db = mOpenHelper.getWritableDatabase();
		ContentProviderResult[] results;

		HashSet<Uri> tempUris = new HashSet<Uri>();
		for (ContentProviderOperation operation : operations) {
			tempUris.add(operation.getUri());
		}

		db.beginTransaction();
		batchingUris.addAll(tempUris);
		try {
			results = super.applyBatch(operations);
			db.setTransactionSuccessful();
		} finally {
			batchingUris.removeAll(tempUris);
			db.endTransaction();
		}
		for (Uri uri : tempUris) {
			notifyChange(uri, 1);
		}
		return results;
	}

	private static String getTableName(Uri uri) {
		List<String> paths = uri.getPathSegments();
		if (paths == null || paths.size() == 0)
			throw new IllegalArgumentException("Unknown URI " + uri);
		String tableName = paths.get(0);
		if (tableMap.keySet().contains(tableName)
				|| viewTablesMap.keySet().contains(tableName)) {
			return tableName;
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	private String getIdPath(Uri uri) {
		List<String> paths = uri.getPathSegments();
		if (paths.size() == 1) {
			return null;
		} else if (paths.size() == 2) {
			String idPath = paths.get(1);
			try {
				Long.parseLong(idPath);
				return idPath;
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	private String finalSelection(String idPath, String selection) {
		if (idPath == null)
			return selection;
		String pathWhere = "_id = " + idPath;
		if (selection != null) {
			return pathWhere + " AND " + selection;
		} else {
			return pathWhere;
		}
	}

	private void notifyChange(Uri uri, int count) {
		if (!batchingUris.contains(uri) && count != 0) {
			getContext().getContentResolver().notifyChange(uri, null);

			// Notify not only the table Uri, but also the view Uris which
			// contain the table.
			String table = uri.getPathSegments().get(0);
			for (String view : viewTablesMap.keySet()) {
				if (viewTablesMap.get(view).contains(table)) {
					Uri viewUri = new Uri.Builder().scheme(uri.getScheme())
							.authority(uri.getAuthority()).appendPath(view)
							.build();
					getContext().getContentResolver().notifyChange(viewUri,
							null);
				}
			}
		}

	}
}

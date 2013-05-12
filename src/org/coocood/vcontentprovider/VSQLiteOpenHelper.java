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
import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class VSQLiteOpenHelper extends SQLiteOpenHelper {
	private HashMap<String, VViewCreation> viewCreators;
	private ArrayList<VDatabaseVersion> versions;

	public VSQLiteOpenHelper(Context context, String name,
			ArrayList<VDatabaseVersion> upgrades,
			HashMap<String, VViewCreation> viewCreators) {
		super(context, name, null, upgrades.get(upgrades.size() - 1).version);
		this.viewCreators = viewCreators;
		this.versions = upgrades;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (VDatabaseVersion upgrade : versions) {
			executeUpgrade(db, upgrade);
		}
		createViews(db);
		versions = null;
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly())
			db.execSQL("PRAGMA foreign_keys=ON;");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (VDatabaseVersion vversion : versions) {
			if (vversion.version > oldVersion)
				executeUpgrade(db, vversion);
		}
		createViews(db);
		versions = null;
	}

	private void createViews(SQLiteDatabase db) {
		for (String view : viewCreators.keySet()) {
			db.execSQL("DROP VIEW IF EXISTS " + view);
			db.execSQL(viewCreators.get(view).sqlString());
		}
		viewCreators = null;
	}

	private void executeUpgrade(SQLiteDatabase db, VDatabaseVersion upgrade) {
		for (VTableColumn newColumn : upgrade.newColumns) {
			db.execSQL(newColumn.alterSql());
		}
		for (VTableCreation tableCreation : upgrade.tableCreations) {
			db.execSQL("DROP TABLE IF EXISTS " + tableCreation.table);
			db.execSQL(tableCreation.sqlString());
		}
		upgrade.collectIndices();
		for (String indexSql : upgrade.indices) {
			db.execSQL(indexSql);
		}
	}
}

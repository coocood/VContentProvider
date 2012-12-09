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
import java.util.HashSet;

public final class VTableCreation {
	String table;
	String sourceIdName;
	ArrayList<VTableColumn> columns;
	HashSet<String> indices;
	StringBuilder sb;

	/**
	 * VTableCreation add an integer primary key column "_id" to the table
	 * automatically because CursorAdapter requires it, so do not add
	 * "_id" column again.
	 * 
	 * @param table
	 *            table name
	 * @param sourceIdName
	 *            if the id is not locally generated, the data source id column
	 *            name may not be "_id", if you want to update database with JSONObject, 
	 *            you need to specify a source id column name.
	 *            The default value is "id" if null is provided.
	 */
	public VTableCreation(String table, String sourceIdName) {
		this.table = table;
		this.sourceIdName = sourceIdName!=null?sourceIdName:"id";
		columns = new ArrayList<VTableColumn>();
		indices = new HashSet<String>();
		sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ").append(table)
				.append(" (_id INTEGER PRIMARY KEY NOT NULL");
	}

	public VTableCreation addIntegerColumn(String column, Long defaultValue) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_INTEGER,
				defaultValue, null, false, false, false));
		return this;
	}

	public VTableCreation addIntegerNotNullCollumn(String column) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_INTEGER,
				null, null, false, false, true));
		return this;
	}

	public VTableCreation addTextColumn(String column, String defaultValue,
			boolean collateNoCase) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_TEXT,
				defaultValue, null, false, collateNoCase, false));
		return this;
	}

	public VTableCreation addTextNotNullColumn(String column,
			boolean collateNoCase) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_TEXT,
				null, null, false, collateNoCase, true));
		return this;
	}

	public VTableCreation addRealColumn(String column, Double defaultValue) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_REAL,
				defaultValue, null, false, false, false));
		return this;
	}

	public VTableCreation addRealNotNullColumn(String column) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_REAL,
				null, null, false, false, true));
		return this;
	}

	public VTableCreation addBlobColumn(String column) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_BLOB,
				null, null, false, false, false));
		return this;
	}

	public VTableCreation addIntegerForeignKeyColumn(String column,
			String parentTable, boolean onDeleteCascade) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_INTEGER,
				null, parentTable, onDeleteCascade, false, false));
		createIndex(column, false);
		return this;
	}

	public VTableCreation addIntegerForeignKeyNotNullColumn(String column,
			String parentTable, boolean onDeleteCascade) {
		columns.add(new VTableColumn(table, column, VTableColumn.TYPE_INTEGER,
				null, parentTable, onDeleteCascade, false, true));
		createIndex(column, false);
		return this;
	}

	public VTableCreation addTableColumn(VTableColumn column) {
		columns.add(column);
		if (column.parentTable != null)
			createIndex(column.name, false);
		return this;
	}
	
	public VTableCreation createIndex(String column, boolean unique) {
		indices.add(VTableColumn.createIndexSql(table, column, unique));
		return this;
	}

	String sqlString() {
		if (columns.size() > 0) {
			for (VTableColumn tf : columns) {
				sb.append(", ");
				tf.appendDefinition(sb);
			}
		}
		sb.append(")");
		return sb.toString();
	}

}

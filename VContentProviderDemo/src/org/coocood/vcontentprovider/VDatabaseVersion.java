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

public final class VDatabaseVersion implements Comparable<VDatabaseVersion>{
	static final int TYPE_INTEGER = 1;
	static final int TYPE_TEXT = 2;
	static final int TYPE_REAL = 3;
	int version;
	ArrayList<VTableColumn> newColumns = new ArrayList<VTableColumn>();
	ArrayList<VTableCreation> tableCreations = new ArrayList<VTableCreation>();
	HashSet<String> indices = new HashSet<String>();

	public VDatabaseVersion(int version) {
		this.version = version;
	}

	public VDatabaseVersion newTable(VTableCreation tableCreation) {
		tableCreations.add(tableCreation);
		return this;
	}

	public VDatabaseVersion alterTableAddIntegerColumn(String table, String column,
			Long defaultValue) {
		newColumns.add(new VTableColumn(table, column,
				VTableColumn.TYPE_INTEGER, defaultValue,null,false,false,false));
		return this;
	}

	public VDatabaseVersion alterTableAddTextColumn(String table, String column,
			String defaultValue,boolean collateNoCase) {
		newColumns.add(new VTableColumn(table, column,
				VTableColumn.TYPE_TEXT, defaultValue,null,false,collateNoCase,false));
		return this;
	}

	public VDatabaseVersion alterTableAddRealColumn(String table, String column,
			Double defaultValue) {
		newColumns.add(new VTableColumn(table, column,
				VTableColumn.TYPE_REAL, defaultValue,null,false,false,false));
		return this;
	}

	public VDatabaseVersion alterTableAddBlobColumn(String table, String column) {
		newColumns.add(new VTableColumn(table, column,
				VTableColumn.TYPE_BLOB, null,null,false,false,false));
		return this;
	}

	public VDatabaseVersion createIndex(String table, String column, boolean unique) {
		indices.add(VTableColumn.createIndexSql(table, column, unique));
		return this;
	}	

	public VDatabaseVersion alterTableAddIntegerForeignKeyColumn(String table,
			String column, String parentTable, boolean onDeleteCascade) {
		newColumns.add(new VTableColumn(table, column,
				VTableColumn.TYPE_INTEGER, null,parentTable,onDeleteCascade,false,false));
		indices.add(VTableColumn.createIndexSql(table, column, false));
		return this;
	}
	public VDatabaseVersion alterTableAddColumn(VTableColumn column){
		newColumns.add(column);
		if(column.parentTable!=null)
			indices.add(VTableColumn.createIndexSql(column.table, column.name, false));
		return this;
	}
	
	HashSet<String> collectIndices(){
		for(VTableCreation tableCreation:tableCreations){
			indices.addAll(tableCreation.indices);
		}
		return indices;
	}

	@Override
	public int compareTo(VDatabaseVersion another) {
		return this.version-another.version;
	}
}

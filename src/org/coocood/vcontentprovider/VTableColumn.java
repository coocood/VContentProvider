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

public final class VTableColumn {
	public static final int TYPE_INTEGER = 1;
	public static final int TYPE_TEXT = 2;
	public static final int TYPE_BLOB = 0;
	public static final int TYPE_REAL = 3;
	String table;
	String name;
	Object defaultValue;
	String parentTable;
	boolean cascade;
	boolean nocase;
	boolean notNull;
	int type;

	public VTableColumn(String table, String name, int type,
			Object defaultValue, String parentTable, boolean cascade,
			boolean nocase, boolean notNull) {
		this.table = table;
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.parentTable = parentTable;
		this.cascade = cascade;
		this.nocase = nocase;
	}

	void appendDefinition(StringBuilder sb) {
		sb.append(name);
		switch (type) {
		case TYPE_INTEGER:
			sb.append(" INTEGER");
			break;
		case TYPE_TEXT:
			sb.append(" TEXT");
			if (nocase)
				sb.append(" COLLATE NOCASE");
			break;
		case TYPE_REAL:
			sb.append(" REAL");
			break;
		default:
			sb.append(" BLOB");
			break;
		}
		if (notNull)
			sb.append(" NOT NULL");
		if (defaultValue != null)
			sb.append(" DEFAULT '").append(defaultValue).append("'");
		if (parentTable != null) {
			sb.append(" REFERENCES ").append(parentTable)
					.append(" ON DELETE ");
			if (cascade) {
				sb.append("CASCADE");
			} else {
				sb.append("SET NULL");
			}
		}
	}

	static String createIndexSql(String table, String column, boolean unique) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE");
		if (unique)
			sb.append(" UNIQUE");
		sb.append(" INDEX IF NOT EXISTS ").append("i_").append(table)
				.append("_").append(column).append(" ON ").append(table)
				.append(" (").append(column).append(")");
		return sb.toString();
	}

	String alterSql() {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(table).append(" ADD ");
		appendDefinition(sb);
		return sb.toString();
	}
}

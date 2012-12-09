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

import java.util.HashMap;

public final class VViewCreation {
	String viewName;
	VTableCreation childTable;
	HashMap<String, VTableCreation> parentTables;
	HashMap<String, String> prefixMap;
	HashMap<String, Boolean> joinTypeMap;
	String where;

	public VViewCreation(String viewName, VTableCreation childTable) {
		this.viewName = viewName;
		this.childTable = childTable;
		this.parentTables = new HashMap<String, VTableCreation>();
		this.prefixMap = new HashMap<String, String>();
		this.joinTypeMap = new HashMap<String, Boolean>();
	}

	/**
	 * 
	 * @param onColumn The integer column of the child table.
	 * @param columnAliasPrefix the prefix is used to make the column alias for the parent table.
	 * the generated alias would be: {prefix}+{column}.
	 * @param parentTable will be joined on primary key "_id" .
	 * @param innerJoin
	 * @return 
	 */
	public VViewCreation joinParentTable(String onColumn,
			String columnAliasPrefix, VTableCreation parentTable, boolean innerJoin) {
		parentTables.put(onColumn, parentTable);
		prefixMap.put(onColumn, columnAliasPrefix);
		joinTypeMap.put(onColumn,innerJoin);
		return this;
	}

	public VViewCreation addWhereClause(String where) {
		this.where = where;
		return this;
	}

	String sqlString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE VIEW IF NOT EXISTS ").append(viewName)
				.append(" AS SELECT ").append(childTable.table).append(".").append("_id AS _id, ");
		appendColumnNames(sb, childTable, null);
		for (String foreignKey : parentTables.keySet()) {
			VTableCreation parentTable = parentTables.get(foreignKey);
			String prefix = prefixMap.get(foreignKey);
			appendColumnNames(sb, parentTable, prefix);
		}
		sb.delete(sb.length() - 2, sb.length()).append(" ");
		sb.append("FROM ");
		appendJoinSource(sb);
		if (where != null) {
			sb.append("WHERE ").append(where);
		}
		return sb.toString();
	}

	private static StringBuilder appendColumnNames(StringBuilder sb,
			VTableCreation tableCreation, String prefix) {
		for (VTableColumn column : tableCreation.columns) {
			sb.append(tableCreation.table).append(".").append(column.name).append(" AS ");
			if(prefix!=null)
				sb.append(prefix);
			sb.append(column.name);
			sb.append(", ");
		}
		return sb;
	}

	private StringBuilder appendJoinSource(StringBuilder sb) {
		String leftTableName = childTable.table;
		sb.append("(").append(leftTableName);
		for (String foreignKey : parentTables.keySet()) {
			VTableCreation parentTable = parentTables.get(foreignKey);
			String foreignTableName = parentTable.table;
			if(joinTypeMap.get(foreignKey)){
				sb.append(" INNER JOIN ");
			}else{
				sb.append(" LEFT JOIN ");
			}
			sb.append(foreignTableName).append(" ON ")
					.append(leftTableName).append(".").append(foreignKey)
					.append(" = ").append(foreignTableName).append("._id");
		}
		sb.append(")");
		return sb;
	}
}

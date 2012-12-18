package org.coocood.vcontentprovider;

import java.util.ArrayList;
import java.util.HashMap;

import org.coocood.vcontentprovider.VContentProvider;
import org.coocood.vcontentprovider.VDatabaseVersion;
import org.coocood.vcontentprovider.VTableCreation;
import org.coocood.vcontentprovider.VViewCreation;

import android.net.Uri;

public class SyntaxProvider extends VContentProvider {
	public static final String AUTHORITY = "org.coocood.vcontentprovider.syntax";
	public static final Uri BASE_URI = Uri.parse("content://"+AUTHORITY);
	public static final Uri POST_URI = Uri.parse("content://"+AUTHORITY+"/post");
	public static final Uri COMMENT_URI = Uri.parse("content://"+AUTHORITY + "/comment");
	public static final Uri VIEW_URI = Uri.parse("content://"+AUTHORITY + "/view");
	@Override
	protected String addDatabaseVersionsViewsAndGetName(
			ArrayList<VDatabaseVersion> dbVersions,
			HashMap<String, VViewCreation> viewCreationMap) {
		VDatabaseVersion version = new VDatabaseVersion(1);
		VTableCreation postTable = new VTableCreation("post", "id")
				.addIntegerColumn("int_column", null)
				.addIntegerColumn("int_column2", 5L)
				.addIntegerNotNullCollumn("int_not_null")
				.addRealColumn("real_column", null)
				.addRealColumn("real_column2", 2.0)
				.addRealNotNullColumn("real_not_null")
				.addTextColumn("text_column", "default", true)
				.addTextColumn("text_column2", null, false)
				.addTextNotNullColumn("text_not_null", true)
				.addBlobColumn("blob_column")
				.addTableColumn(new VTableColumn("post", "table_column", VTableColumn.TYPE_TEXT, null, null, false, true, true))
				.createIndex("int_column", true)
				.createIndex("text_column", false);
		VTableCreation commentTable = new VTableCreation("comment", null)
				.addIntegerForeignKeyColumn("post_id", postTable.table, true)
				.addTextColumn("text_column", null, true)
				.createIndex("text_column", false);
		
		
		version.newTable(postTable).newTable(commentTable);
		dbVersions.add(version);
		
		VViewCreation view = new VViewCreation("view", commentTable)
				.joinParentTable("post_id", "post_", postTable, false)
				.addWhereClause("comment.text_column IS NOT NULL");
		viewCreationMap.put(view.viewName, view);
		return "test";
	}

}

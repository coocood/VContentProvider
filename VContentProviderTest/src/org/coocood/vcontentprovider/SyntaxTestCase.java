package org.coocood.vcontentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

public class SyntaxTestCase extends ProviderTestCase2<SyntaxProvider> {

	public SyntaxTestCase() {
		super(SyntaxProvider.class, SyntaxProvider.AUTHORITY);
	}
	public void testSyntax(){
		ContentResolver resolver = getContext().getContentResolver();
		ContentValues postValues = new ContentValues();
		postValues.put("int_not_null", 5);
		postValues.put("text_not_null", "abc");
		postValues.put("real_not_null", 54.7);
		Uri newPostUri = resolver.insert(SyntaxProvider.POST_URI, postValues);
		assertNotNull(newPostUri);
		Cursor cursor = resolver.query(newPostUri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(1, cursor.getCount());
		assertEquals(12,cursor.getColumnCount());
		cursor.moveToFirst();
		long intColumn2Value = cursor.getLong(cursor.getColumnIndex("int_column2"));
		assertEquals(5, intColumn2Value);
		
		ContentValues commentValues = new ContentValues();
		commentValues.put("post_id", newPostUri.getLastPathSegment());
		commentValues.put("text_column", "aaa");
		Uri newCommentUri = resolver.insert(SyntaxProvider.COMMENT_URI, commentValues);
		assertNotNull(newCommentUri);
		Cursor cursor2 = resolver.query(newCommentUri, null, null, null, null);
		assertNotNull(cursor2);
		assertEquals(1, cursor2.getCount());
		
		Cursor cursor3 = resolver.query(SyntaxProvider.VIEW_URI, null, null, null, null);
		assertNotNull(cursor3);
		assertEquals(1, cursor3.getCount());
	}
}

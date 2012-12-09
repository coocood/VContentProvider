package org.coocood.vproviderdemo.adapter;

import org.coocood.vproviderdemo.R;
import org.coocood.vcontentprovider.VCursorAdapter;
import org.coocood.vproviderdemo.model.Comment;
import org.coocood.vproviderdemo.model.Post;
import org.coocood.vproviderdemo.model.User;
import org.coocood.vproviderdemo.util.CursorBinder;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

public class CommentViewAdapter extends VCursorAdapter {

	public CommentViewAdapter(Context context) {
		super(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return View.inflate(context, R.layout.comment_item, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CursorBinder binder = new CursorBinder(view, cursor);
		binder.bindText(R.id.content, Comment.CONTENT)
				.bindText(R.id.title, Comment.POST_PREFIX+Post.TITLE)
				.bindText(R.id.user, Comment.USER_PREFIX+User.NAME);
	}

}

package org.coocood.vproviderdemo.activity;

import org.coocood.vproviderdemo.R;
import org.coocood.vproviderdemo.adapter.CommentViewAdapter;
import org.coocood.vproviderdemo.model.Comment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class CommentViewActivity extends Activity {
	CommentViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		adapter = new CommentViewAdapter(this);
		ListView list = (ListView) findViewById(R.id.listView1);
		list.setAdapter(adapter);
		String selection = null;
		long pid = getIntent().getLongExtra(Comment.POST_ID, 0);
		if (pid != 0)
			selection = Comment.POST_ID + "=" + pid;
		adapter.query(Comment.VIEW_URI, null, selection, null, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.changeCursor(null);
	}
}

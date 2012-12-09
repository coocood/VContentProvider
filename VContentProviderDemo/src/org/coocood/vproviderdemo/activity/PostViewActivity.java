package org.coocood.vproviderdemo.activity;

import org.coocood.vproviderdemo.R;
import org.coocood.vproviderdemo.adapter.PostViewAdapter;
import org.coocood.vproviderdemo.model.Post;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class PostViewActivity extends Activity {
	private PostViewAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		ListView lv = (ListView)findViewById(R.id.listView1);
		adapter = new PostViewAdapter(this);
		lv.setAdapter(adapter);
		long authorId = getIntent().getLongExtra(Post.AUTHOR_ID, 0);
		String selection = null;
		if(authorId!=0)
			selection = Post.AUTHOR_ID + "=" + authorId;
		
		adapter.query(Post.VIEW_URI, null, selection, null, null);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.changeCursor(null);
	}
}

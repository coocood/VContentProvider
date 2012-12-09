package org.coocood.vproviderdemo.activity;

import org.coocood.vproviderdemo.R;
import org.coocood.vproviderdemo.adapter.UserAdapter;
import org.coocood.vproviderdemo.model.User;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class UserActivity extends Activity {
	private UserAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		ListView lv = (ListView)findViewById(R.id.listView1);
		adapter = new UserAdapter(this);
		lv.setAdapter(adapter);
		adapter.query(User.URI, null, null, null, null);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.changeCursor(null);
	}
}

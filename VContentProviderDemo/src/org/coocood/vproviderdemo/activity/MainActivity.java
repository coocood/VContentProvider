package org.coocood.vproviderdemo.activity;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.protocol.HTTP;
import org.coocood.vproviderdemo.DemoProvider;
import org.coocood.vproviderdemo.R;
import org.coocood.vproviderdemo.model.Comment;
import org.coocood.vproviderdemo.model.Post;
import org.coocood.vproviderdemo.model.User;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;

@SuppressLint("NewApi")
public class MainActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.loadJson).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AssetManager assets = getAssets();
				try {
					String userString =convertStreamToString(assets.open("user"));
					JSONArray userJsonArray = new JSONArray(userString);
					DemoProvider.updateWithJSONArray(getApplicationContext(), User.URI, userJsonArray, null, null);
					
					String postString = convertStreamToString(assets.open("post"));
					JSONArray postJsonArray = new JSONArray(postString);
					DemoProvider.updateWithJSONArray(getApplicationContext(), Post.URI, postJsonArray, null, null);
					
					String commentString = convertStreamToString(assets.open("comment"));
					JSONArray commentJsonArray = new JSONArray(commentString);
					DemoProvider.updateWithJSONArray(getApplicationContext(), Comment.URI, commentJsonArray, null, null);
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				}
			}
		});
		findViewById(R.id.allUsers).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),UserActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.allPosts).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),PostViewActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.allComments).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),CommentViewActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private String convertStreamToString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is, HTTP.UTF_8)
				.useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}

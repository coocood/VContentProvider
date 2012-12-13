package org.coocood.vproviderdemo.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

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
					String postString = convertStreamToString(assets.open("post"));
					JSONArray postJsonArray = new JSONArray(postString);
					LinkedHashMap<String, String> authorObjectMap = new LinkedHashMap<String, String>();
					authorObjectMap.put("author", User.TABLE);
					DemoProvider.updateWithJSONArray(getApplicationContext(), Post.URI, postJsonArray, authorObjectMap);
					
					String commentString = convertStreamToString(assets.open("comment"));
					JSONArray commentJsonArray = new JSONArray(commentString);
					LinkedHashMap<String, String> userObjectMap = new LinkedHashMap<String, String>();
					userObjectMap.put("user",User.TABLE);
					DemoProvider.updateWithJSONArray(getApplicationContext(), Comment.URI, commentJsonArray, userObjectMap);
					
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

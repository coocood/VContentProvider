package org.coocood.vproviderdemo.model;

import android.net.Uri;

public class Post {
	public static final String TABLE = "post";
	public static final String VIEW = "post_view";
	public static final String AUTHOR_PREFIX = "author_";
	public static final String TITLE = "title";
	public static final String CONTENT = "content";
	public static final String AUTHOR_ID = "author_id";
	
	public static final Uri URI = Uri.parse("content://" + User.AUTHORITY + "/"
			+ TABLE);
	public static final Uri VIEW_URI = Uri.parse("content://" + User.AUTHORITY + "/"
			+ VIEW);
}

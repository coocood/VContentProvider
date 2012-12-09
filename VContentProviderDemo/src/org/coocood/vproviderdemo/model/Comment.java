package org.coocood.vproviderdemo.model;

import android.net.Uri;

public class Comment {
	public static final String TABLE = "comment";
	public static final String VIEW = "comment_view";
	public static final String USER_PREFIX = "user_";
	public static final String POST_PREFIX = "post_";
	public static final String CONTENT = "content";
	public static final String USER_ID = "user_id";
	public static final String POST_ID = "post_id";
	public static final Uri URI = Uri.parse("content://" + User.AUTHORITY + "/"
			+ TABLE);
	public static final Uri VIEW_URI = Uri.parse("content://" + User.AUTHORITY + "/"
			+ VIEW);
}

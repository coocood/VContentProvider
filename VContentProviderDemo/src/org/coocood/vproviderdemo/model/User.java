package org.coocood.vproviderdemo.model;

import android.net.Uri;

public class User {
	public static final String TABLE = "user";
	public static final String NAME = "name";
	public static final String AUTHORITY = "org.coocood.vproviderdemo";
	public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/"
			+ TABLE);
}

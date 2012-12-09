package org.coocood.vproviderdemo;

import java.util.ArrayList;
import java.util.HashMap;

import org.coocood.vcontentprovider.VContentProvider;
import org.coocood.vcontentprovider.VDatabaseVersion;
import org.coocood.vcontentprovider.VTableCreation;
import org.coocood.vcontentprovider.VViewCreation;
import org.coocood.vproviderdemo.model.Comment;
import org.coocood.vproviderdemo.model.Post;
import org.coocood.vproviderdemo.model.User;


public class DemoProvider extends VContentProvider {

	@Override
	protected String addDatabaseVersionsViewsAndGetName(ArrayList<VDatabaseVersion> allversions,
			HashMap<String, VViewCreation> viewCreationMap) {
		

		VTableCreation userTable = new VTableCreation(User.TABLE,"id")
				.addTextColumn(User.NAME, null, false);

		VTableCreation postTable = new VTableCreation(Post.TABLE,"id")
				.addTextNotNullColumn(Post.TITLE, true)
				.addTextColumn(Post.CONTENT, null, true)
				.addIntegerForeignKeyColumn(Post.AUTHOR_ID, User.TABLE, true);

		VTableCreation commentTable = new VTableCreation(Comment.TABLE,"id")
				.addIntegerForeignKeyColumn(Comment.POST_ID, Post.TABLE, true)
				.addIntegerForeignKeyColumn(Comment.USER_ID, User.TABLE, true)
				.addTextColumn(Comment.CONTENT, null, true);
		
		VDatabaseVersion version = new VDatabaseVersion(1)
				.newTable(userTable)
				.newTable(postTable)
				.newTable(commentTable);
		
		allversions.add(version);
		
		VViewCreation postView = new VViewCreation(Post.VIEW, postTable)
				.joinParentTable(Post.AUTHOR_ID, Post.AUTHOR_PREFIX, userTable, false);
		
		VViewCreation commentView = new VViewCreation(Comment.VIEW, commentTable)
				.joinParentTable(Comment.POST_ID, Comment.POST_PREFIX, postTable, false)
				.joinParentTable(Comment.USER_ID, Comment.USER_PREFIX, userTable, false);
		
		viewCreationMap.put(Post.VIEW, postView);
		viewCreationMap.put(Comment.VIEW, commentView);
		
		VDatabaseVersion version2 = new VDatabaseVersion(5);
		allversions.add(version2);
		return "database";
	}

}

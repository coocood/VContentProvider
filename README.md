Android V Content Provider
================
All in one SQLite database solution for android.


#Features:

- Super light library, only 30KB(uncompressed source files) for version 1.0.

- Creating database tables and views and build Content Provider within a single fucntion.

        protected String addDatabaseVersionsViewsAndGetName(ArrayList<VDatabaseVersion> allversions, 
                HashMap<String, VViewCreation> viewCreationMap){
            VTableCreation userTable = new VTableCreation(User.TABLE,"id")
    			.addTextColumn(User.NAME, null, false);
            VDatabaseVersion version = new VDatabaseVersion(1)
    			.newTable(userTable);
            allversions.add(version);
            return "database";
        }

- Provide APIs to create SQLite databse table and view by method chaining.
        
        VTableCreation postTable = new VTableCreation(Post.TABLE,"id")
    			.addTextNotNullColumn(Post.TITLE, true)
				.addTextColumn(Post.CONTENT, null, true)
				.addIntegerForeignKeyColumn(Post.AUTHOR_ID, User.TABLE, true);
        version.newTable(postTable);

        VViewCreation postView = new VViewCreation(Post.VIEW, postTable)
    			.joinParentTable(Post.AUTHOR_ID, Post.AUTHOR_PREFIX, userTable, false);
        viewCreationMap.put(Post.VIEW, postView);

- SQLite Foreign Key support.

- Managed database versions, without the need to handle upgrade manually.

- Update/Insert database table with JSONObject or JSONArray directly.

        JSONArray userJsonArray = new JSONArray(userString);
    	DemoProvider.updateWithJSONArray(getApplicationContext(), User.URI, userJsonArray, null);

- Implement applyBatch() and bulkInsert() in transaction to improve performance.

- Query on VCursorAdapter asynchronously without the need to use compatibility package and implement LoaderCallbacks.

        adapter = new PostViewAdapter(this);
        adapter.query(Post.VIEW_URI, null, selection, null, null);

#How does it work

I highly recommend you to read this [WIKI](https://github.com/coocood/VContentProvider/wiki/How-does-VContentProvider-work%3F) page before use it.
It explains in detail what problems you are facing when creating database and content provider, and how VContentProvider resolve the problems.
        
#Usage

1. you can either copy the source files into your project or link the library project.

2. Subclass VContentProvider.

3. Implement "addDatabaseVersionsViewsAndGetName" method.

4. In the method create a VDatabaseVersion object.

5. Create VTableCreation objects, define your table.

6. Put VTableCreation objects into a VDatabaseVersion by calling "newTable" method.

7. You can create multiple VDatabaseVersion objects if you need to add new table or add new columns to a existing table.

7. Add all versions to the given ArrayList "allVersions".

8. Create VViewCreation objects, define your view.

9. Put All VViewCreation objects into the given Map "viewCreationMap".

10. return the database name.

11. In your manifest.xml file, define your content provider, set the "android:authorities" attribute, if you don't want to expose your content to other applications.
	set the "android:exported" attribute to "false".



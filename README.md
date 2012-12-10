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
    	DemoProvider.updateWithJSONArray(getApplicationContext(), User.URI, userJsonArray, null, null);

- Implement applyBatch() and bulkInsert() in transaction to improve performance.

- Query on VCursorAdapter asynchronously without the need to use compatibility package and implement LoaderCallbacks.

        adapter = new PostViewAdapter(this);
        adapter.query(Post.VIEW_URI, null, selection, null, null);

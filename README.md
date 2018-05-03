# android.sqlite
A brief Java File to handle SQLite Operations in Android. This lets users copy the database file at startup, register callbacls for the same, open and return a database, close the database and destroy self instance when called.


**Constructor**:
- `DatabaseManager(Context context, String db_filename, boolean debug)`
		Using Activity Context is unnecessary because no UI is involved. Use `getApplicationContext()` instead.


**Interfaces & Callbacks**:
1. `CreationListener`
		 
- `onSuccess()` - Invoked when database is successfully *copied from Asset folder to Internal Memory* .
- `onFailure(String cause)` - Invoked when copy fails.




**Public Methods**: 
- `getDatabase()` - Returns SQLiteDatabase Object or null (in case of failure).
- `createDatabase()` - Copies a database file from Asses folder to Internal Memory of your App.
- `setDatabaseCreationListener(DatabaseManager.CreationListener listener)` - Listen for creation events such as success & failure and take appropriate action.
- `destroy()` - Destroys the internal elements and closes the SQLiteDatabase instance. Once this method is used, the calling instance cannot be reused because listeners & other data are destroyed to free memory. Garbage Collection is indicated at this point. 



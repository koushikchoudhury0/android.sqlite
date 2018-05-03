package Critical;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.support.annotation.Nullable;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import Other.CompanyPreview;
import Other.UserCredential;

public class DatabaseManager extends SQLiteOpenHelper
{

    /* Developer's Note: Use the full name for database file including the extension.
     *                      If the database is a file named app.db the filename should also be "app.db" */

    public interface CreationListener
    {
        public void onSuccess(@Nullable String state);
        public void onFailure(@Nullable String cause);
    }

    public interface QueryListener
    {
        public void onFailure(String cause);
        public void onSuccess();
    }

    private final String TAG  = "DatabaseManager";
    private Boolean debug=false;
    private String db_directory, db_file, db_name;
    private Context context;
    private CreationListener creation_listener = null;
    private QueryListener query_listener = null;
    private SQLiteDatabase db;


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //not required currently
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //not required currently
    }

    @Override
    public synchronized void close()
    {
        super.close();
    }

    public DatabaseManager(Context context, String db_name, Boolean debug)
    {
        super(context, db_name, null, 1);
        this.context = context;
        this.db_name = db_name;
        db_directory = context.getDatabasePath(db_name).getParent();
        Log.d(TAG, "Database Path: "+context.getDatabasePath(db_name).getAbsolutePath());
        db_file = db_directory+File.separator+db_name;
        this.debug = debug;
    }

    public void setDatabaseCreationListener(CreationListener listener)
    {
        this.creation_listener = listener;
    }

    public void setOnQueryListener(QueryListener listener)
    {
        this.query_listener = listener;
    }

    private boolean checkDatabase()
    {
        SQLiteDatabase db = null;
        try
        {
            db = SQLiteDatabase.openDatabase(db_file, null, SQLiteDatabase.OPEN_READWRITE);
        }
        catch(Exception e)
        {
            if (debug) Log.d(TAG, "checkDatabase():: Can't check database because: "+e.getMessage());
            //e.printStackTrace();
            return false;
        }
        if(db != null)
        {
            db.close();
            if (debug) Log.d(TAG, "checkDatabase():: Database found.");
            return true;
        }
        if (debug) Log.d(TAG, "checkDatabase():: Database doesn't exists");
        return false;
    }

    public void createDataBase() /*Copies only when no Database exists*/
    {
        if (!checkDatabase())
        {
            if (debug) Log.d(TAG, "copyDatabase():: No databases were found, copying now");
            try
            {
                File db_dir = new File(db_directory);
                if (!db_dir.exists())
                {
                    if (!db_dir.mkdir())
                    {
                        if (creation_listener!=null) creation_listener.onFailure("Path does not exists");
                        return;
                    }
                }
                InputStream source_stream = context.getAssets().open(db_name);
                OutputStream destination_stream = new FileOutputStream(db_file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = source_stream.read(buffer))>0)
                {
                    destination_stream.write(buffer, 0, length);
                }
                destination_stream.flush();
                destination_stream.close();
                source_stream.close();
                if (debug) Log.d(TAG, "copyDatabase():: Database copied");
                if (creation_listener!=null) creation_listener.onSuccess("fresh");
            }
            catch (Exception e)
            {
                if (debug) Log.d(TAG, "copyDatabase():: Database cannot be copied because: "+e.getMessage());
                e.printStackTrace();
                if (creation_listener!=null) creation_listener.onFailure(e.getMessage());
            }
        }
        else
        {
            if (debug) Log.d(TAG, "copyDataBase():: Database is found, skipped copy");
            if (creation_listener!=null) creation_listener.onSuccess("existing");
        }
    }

    public SQLiteDatabase getDatabase() throws SQLException
    {
        //Open the database
        if (db!=null) return db;
        db = SQLiteDatabase.openDatabase(db_file, null, SQLiteDatabase.OPEN_READWRITE);
        if (db == null)
        {
            if (debug) Log.d(TAG, "openDatabase():: Unopened");
        }
        else
        {
            if (debug) Log.d(TAG, "openDatabase():: Database Opened");
        }
        return db;

    }
    
    public void destroy()
    {
        close();
        db=null;
        query_listener = null;
        creation_listener = null;
    }
}



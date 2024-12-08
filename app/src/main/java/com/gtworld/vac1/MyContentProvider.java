package com.gtworld.vac1;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {
    public MyContentProvider() {
    }
    // defining authority so that other application can access it
    static final String PROVIDER_NAME = "com.demo.user.provider";

    //defining content URI
    static final String URL = "content://"+PROVIDER_NAME+"/contacts";

    //parsing the content URI
    static final Uri CONTENT_URI = Uri.parse(URL);




    static final String id = "id";
    static final String name = "name"; //this will be my key
    static final String phone = "phone";
    static final int uriCode = 1;
    static final int uriAdd = 2;
    static final UriMatcher uriMatcher;
    private static HashMap<String, String> values;
    static {
        // to match the content URI
        // every time user access table under content provider
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


        //to access whole table
        uriMatcher.addURI(PROVIDER_NAME, "contacts", uriCode);

        //to access a particular row of the table
        uriMatcher.addURI(PROVIDER_NAME, "contacts/*", uriAdd);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.

        int count =0;
        switch(uriMatcher.match(uri)){
            case uriCode:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case uriAdd:
                count = db.delete(TABLE_NAME, "id = ?", new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)){
            case uriCode:
                return "vnd.android.cursor.dir/contacts";
            case uriAdd:
                return "vnd.android.cursor.item/vnd.android.cursor.contacts";
            default:
                throw new IllegalArgumentException("Unsupported URI:"+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long rowID = db.insert(TABLE_NAME, "",values);
        if(rowID>0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);//updates if there are any changes
            return _uri;
        }
        throw new SQLiteException("Failed to add a record into "+uri);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        //Creating the database
        Context context = getContext();//gets the context
        DatabaseHelper dbHelper = new DatabaseHelper(context);
         db = dbHelper.getWritableDatabase();//creates the database
if(db!= null){
    return true;//database was created successfully or was opened successfully
}
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);//creates the names name
        switch (uriMatcher.match(uri)){
            case uriCode:
                qb.setProjectionMap(values);//<id, value>,
                break;
                case uriAdd:
                    selection = selection;
                    selectionArgs = selectionArgs;

                break;

            default:
              throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if(sortOrder == null || sortOrder == ""){
            sortOrder = id;
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int count = 0;
        switch(uriMatcher.match(uri)){
            case uriCode:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
                case uriAdd:
                count = db.update(TABLE_NAME, values, "id = ?",new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI "+ uri);
        }
   getContext().getContentResolver().notifyChange(uri, null);//updates the observers
        return count;
    }




    //Creating object of database to perform query
    private SQLiteDatabase db;

    //Declare the name of the database
    static final String DATABASE_NAME = "ContactDB";

    //declaring table name of the database
    static final String TABLE_NAME = "Contacts";

    //declaring version of the database
    static final int DATABASE_VERSION = 1;

    //sql query to create the table
    static final String CREATE_DB_TABLE = "CREATE TABLE "+TABLE_NAME
            +" (id INTEGER PRIMARY KEY AUTOINCREMENT, "+ "name TEXT NOT NULL,"+"phone TEXT NOT NULL);";

    //creating a database
    private static class DatabaseHelper extends SQLiteOpenHelper{

        //defining a constructor
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //Creating a table in the database
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //sql query to drop a table having similar name
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
            onCreate(db);
            //Deletes the old database table then create a new one
        }
    }








}
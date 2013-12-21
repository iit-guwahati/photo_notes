package com.kad.android.photo_notes;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kAd on 24/08/13.
 */
public class Database extends SQLiteOpenHelper {
    static final String TAG = "Notes | Database";
    static final int VERSION = 11;
    static final String NAME = "PhotoNotes";
    static final String TABLE = "Notes";
    static final String FIELD_ID = "id";
    static final String FIELD_TITLE = "title";
    static final String FIELD_TEXT = "text";
    static final String FIELD_IMAGE = "image";
    static final String FIELD_CREATED = "created";
    static final String FIELD_MODIFIED = "modified";

    private void tableCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+TABLE+"" +
                "("+FIELD_ID+" INTEGER PRIMARY KEY, "+
                FIELD_TITLE+" TEXT, "+
                FIELD_TEXT+" TEXT, "+
                FIELD_IMAGE+" TEXT, "+
                FIELD_CREATED+" DATETIME DEFAULT CURRENT_TIMESTAMP, "+
                FIELD_MODIFIED+" DATETIME);");
    }

    private void dropTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
    }

    public void insert(String title, String text, String url){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if( title.length() < 1 )
            title = "#PhotoNoteSnap";
        values.put(FIELD_TITLE, title);
        values.put(FIELD_TEXT, text);
        values.put(FIELD_IMAGE, url);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:mm", Locale.getDefault());
        values.put(FIELD_MODIFIED, df.format(new Date()));
        values.put(FIELD_CREATED, df.format(new Date()));
        long id = db.insert(TABLE, null, values);
        MainActivity.log(TAG, "Insert id = "+ id);
    }

    public Cursor select(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE+" ORDER BY "+FIELD_CREATED+" DESC", null);
        return c;
    }

    public void delete(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE+" WHERE "+FIELD_ID+"="+id);
        MainActivity.log("Database", id);
    }

    public Database(Context context){
        super(context, NAME, null, VERSION);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Database(Context context, DatabaseErrorHandler errorHandler) {
        super(context, NAME, null, VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        tableCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        dropTable(sqLiteDatabase);
        tableCreate(sqLiteDatabase);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        dropTable(db);
        tableCreate(db);
    }
}

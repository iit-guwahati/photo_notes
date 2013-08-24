package com.kad.android.hackathon13_1;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{
    private static final boolean DEBUG = true;
    private static final String TAG = "Notes | MainActivity";
    ArrayList<Note> notes = new ArrayList<Note>();
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lv = (ListView) findViewById(R.id.notes);
        lv.setOnItemClickListener(this);
        notes.clear();
        loadNotesFromDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void loadNotesFromDB() {
        AsyncTask s = new AsyncTask() {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                MainActivity.this.doUIupdate();
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                Database db = new Database(getApplicationContext());
                Cursor c = db.select();
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    for (int i = 0; i < c.getCount(); ++i) {
                        notes.add(new Note(
                                c.getInt(c.getColumnIndex(Database.FIELD_ID)),
                                c.getString(c.getColumnIndex(Database.FIELD_TITLE)),
                                c.getString(c.getColumnIndex(Database.FIELD_TEXT)),
                                c.getString(c.getColumnIndex(Database.FIELD_IMAGE)),
                                c.getString(c.getColumnIndex(Database.FIELD_CREATED)),
                                c.getString(c.getColumnIndex(Database.FIELD_MODIFIED))
                        ));
                        c.moveToNext();
                    }
                }
                c.close();
                db.close();
                return null;
            }
        };
        s.execute();
    }

    private void doUIupdate() {
        NoteListAdapter adapter = new NoteListAdapter(this.getApplicationContext(), R.layout.note, notes);
        lv.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add:
                startActivity(new Intent(this, NewNote.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void log(String tag, String value){
        if(DEBUG)
            Log.v(tag, value);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        log(TAG, "LOADED view: "+i);
        Toast.makeText(getApplicationContext(), "View: "+i, Toast.LENGTH_SHORT).show();
    }

}

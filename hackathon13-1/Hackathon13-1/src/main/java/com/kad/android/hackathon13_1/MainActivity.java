package com.kad.android.hackathon13_1;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, ListView.OnItemClickListener {
    private static final boolean DEBUG = true;
    private static final String TAG = "Notes | MainActivity";
    public static final int NOTE_HEIGHT = 256, NOTE_WIDTH = 256;
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
        notes.clear();
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
        lv.setOnItemClickListener(this);
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
        lv = (ListView) findViewById(R.id.notes);
        loadNotesFromDB();
    }

    public static void log(String tag, String value){
        if(DEBUG)
            Log.v(tag, value);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getApplicationContext(), SingleNote.class);
        Note n = notes.get(i);
        intent.putExtra("id", n.id);
        intent.putExtra("title", n.title);
        intent.putExtra("content", n.content);
        intent.putExtra("created", n.created);
        intent.putExtra("modified", n.modified);
        intent.putExtra("image", n.image);
        startActivity(intent);
    }

}

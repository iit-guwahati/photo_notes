package com.kad.android.photo_notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, ListView.OnItemClickListener, ListView.OnItemLongClickListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "Notes | MainActivity";
    public static final int NOTE_HEIGHT = 256, NOTE_WIDTH = 256;
    public static int SCREEN_HEIGHT = 256, SCREEN_WIDTH = 256;
    ArrayList<Note> notes = new ArrayList<Note>();
    ListView lv;
    NoteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.notes);
        loadNotesFromDB();
        Point size = new Point();
        WindowManager w = getWindowManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        adapter = new NoteListAdapter(this.getApplicationContext(), R.layout.note, notes);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add:
                startActivityForResult(new Intent(this, NewNote.class), 1);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        notes.clear();
        loadNotesFromDB();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
//        Toast.makeText(getApplicationContext(), "Delete: "+1, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Database db = new Database(getApplicationContext());
                db.delete(notes.get(i).id+"");
                db.close();
                notes.remove(i);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Delete note?").setTitle(getString(R.string.app_name));

        AlertDialog dialog = builder.create();

        dialog.show();

        return false;
    }
}

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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final boolean DEBUG = true;
    private static final String TAG = "Notes | MainActivity";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    TextView text;
    List<Note> notes = new ArrayList<Note>();
    private Uri fileUri = Uri.parse("");

    private static Uri getOutputMediaFileUri(int type) {
        File f = getOutputMediaFile(type);
        if (f != null)
            return Uri.fromFile(f);
        else return null;
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        text = (TextView) findViewById(R.id.text);
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
                                c.getString(c.getColumnIndex(Database.FIELD_MODIFIED)),
                                c.getString(c.getColumnIndex(Database.FIELD_CREATED))
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
        Iterator<Note> it = notes.listIterator();
        while (it.hasNext()){
            Note n = it.next();
            text.setText(text.getText().toString() + n.title);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                //TODO insert note here
//                Toast.makeText(this, "Image saved to:\n" +
//                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }

    private void AddImage(byte[] byteArray) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                if (fileUri != null)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.add:
                String title = ((TextView)findViewById(R.id.title)).getText().toString();
                String content = ((TextView)findViewById(R.id.content)).getText().toString();
                log(TAG, fileUri.getEncodedPath());
                Toast.makeText(getApplicationContext(), "Adding", Toast.LENGTH_SHORT).show();
                addNote(title, content, fileUri.getEncodedPath());
        }
    }

    private void addNote(final String title, final String content, final String file){
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Database db = new Database(MainActivity.this.getApplicationContext());
                db.insert(title, content, file);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Note added", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        task.execute();
    }

    public static void log(String tag, String value){
        if(DEBUG)
            Log.v(tag, value);
    }
}

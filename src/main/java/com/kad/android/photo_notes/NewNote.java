package com.kad.android.photo_notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewNote extends Activity implements View.OnClickListener {
    public static final String folder = "Photo-Notes";
    public static final String TAG = "Notes | NewNode";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static Context context;
    private Uri fileUri = Uri.parse(""),
    old = null;

    private static Uri getOutputMediaFileUri(int type) {
        File f = getOutputMediaFile(type);
        if (f != null)
            return Uri.fromFile(f);
        else return null;
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = null;
        if (Environment.getExternalStorageDirectory() != null) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), folder);
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    MainActivity.log(TAG, "failed to create directory in ext storage");
                    return null;
                }
            }
        } else {
            mediaStorageDir = NewNote.context.getDir(folder, Context.MODE_WORLD_READABLE);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    MainActivity.log(TAG, "failed to create directory in internal storage");
                    return null;
                }
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);
        context = getApplicationContext();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                if(old == null) old = fileUri;
                if (fileUri != null)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, )

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.add:
                String title = ((TextView) findViewById(R.id.title)).getText().toString();
                String content = ((TextView) findViewById(R.id.content)).getText().toString();
                MainActivity.log(TAG, fileUri.getPath());
                Toast.makeText(getApplicationContext(), "Adding", Toast.LENGTH_SHORT).show();
                addNote(title, content, fileUri.getEncodedPath());
                this.finish();
                break;
        }
    }

    private void addNote(final String title, final String content, final String file) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Database db = new Database(NewNote.this.getApplicationContext());
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                MainActivity.log(TAG, "Image saved to:\n" +
                        fileUri.getEncodedPath());
                ImageView iv = ((ImageView)findViewById(R.id.image));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                new AsyncTask<Object, Void, Bitmap>(){
                    ImageView iv;
                    @Override
                    protected Bitmap doInBackground(Object... objects) {
                        iv = (ImageView) objects[1];
                        return BitmapFactory.decodeFile((String) objects[0]);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        iv.setImageBitmap(bitmap);
                    }
                }.execute(fileUri.getPath(), iv);
            } else if (resultCode == RESULT_CANCELED) {
                if(old != null) {
                    fileUri = old;
                    old = null;
                }
            } else {
                if(old != null) fileUri = old;
            }
        }
    }


}

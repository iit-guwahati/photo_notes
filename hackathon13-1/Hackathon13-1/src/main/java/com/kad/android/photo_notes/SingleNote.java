package com.kad.android.photo_notes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.*;

public class SingleNote extends Activity implements View.OnTouchListener{
    int photoSize = -1;
    int scale = 1;
    int curScale = 1;
    float oldDistance = 0;
    PointF point = new PointF();
    ImageView iv;
    String url;
    BitmapFactory.Options o = new BitmapFactory.Options();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_note);
        Bundle extra = getIntent().getExtras();
        url = extra.getString("image");
        o.inScaled = true;
        ((TextView)findViewById(R.id.title)).setText(extra.getString("title"));
        ((TextView)findViewById(R.id.content)).setText(extra.getString("content"));
        iv = (ImageView)findViewById(R.id.image);
        if (photoSize < 0) {
            // Determines the size for the photo shown full-screen (without zooming).
            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            photoSize = max(displayMetrics.heightPixels,
                    displayMetrics.widthPixels);
        }
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, o);
        float h = o.outHeight, w = o.outWidth;
        float ratio = 1;
        if(h > w){
            ratio = photoSize / h;
        } else {
            ratio = photoSize / w;
        }
        curScale = scale = ((int) round(pow(2, ceil(log(ratio) / log(ratio))))) * 2;
        o.inScaled = true;
        o.inSampleSize = scale;
        o.inJustDecodeBounds = false;
        loadBitmap(iv, url);
        iv.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getPointerCount() != 2)
            return true;
        MainActivity.log("Touch", motionEvent.toString());
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(oldDistance == 0)
                    oldDistance = spacing(motionEvent);
                MainActivity.log("Touch", oldDistance+"");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                float newDistance = spacing(motionEvent);
                MainActivity.log("Touch", oldDistance+" "+newDistance);
                if(newDistance > 10f){
                    if(newDistance/oldDistance < 1){
                        if(curScale >= scale){
                            curScale = curScale * 2;
                            o.inSampleSize = curScale;
                            loadBitmap(iv, url);
                        }
                    } else {
                        if(curScale > 1){
                            curScale = curScale / 2;
                            o.inSampleSize = curScale;
                            loadBitmap(iv, url);
                        }
                    }
                }
                oldDistance = 0;
        }
        return false;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void loadBitmap(ImageView iv, String url){
        new AsyncTask<Object, Void, Bitmap>() {
            ImageView v;
            @Override
            protected Bitmap doInBackground(Object... params) {
                v = (ImageView) params[0];
                String rowItem = (String) params[1];
                BitmapFactory.Options options = (BitmapFactory.Options) params[2];
                return BitmapFactory.decodeFile(rowItem, options);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (result == null) return;
                // If this item hasn't been recycled already, hide the
                // progress and set and show the image
                v.setImageBitmap(result);
            }
        }.execute(iv, url, o);
    }
}

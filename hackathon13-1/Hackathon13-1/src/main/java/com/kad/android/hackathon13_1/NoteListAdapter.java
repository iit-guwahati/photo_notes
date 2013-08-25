package com.kad.android.hackathon13_1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by kAd on 24/08/13.
 */
public class NoteListAdapter extends ArrayAdapter<Note> {
    public static Context context;

    public NoteListAdapter(Context context, int textViewResourceId, List<Note> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }
    static class ViewHolder{
        public TextView title, created;
        public ImageView image;
        public ProgressBar progress;
        public int position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.note, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.created = (TextView) convertView.findViewById(R.id.created);
            holder.position = position;

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


//        holder.image.setImageURI(Uri.parse(rowItem.image));//setImageBitmap(decodeFile(new File(rowItem.image), 0, 256));

        Note rowItem = getItem(position);
        holder.title.setText(rowItem.title);
        holder.created.setText(rowItem.created);
        new AsyncTask<Object, Void, Bitmap>() {
            private ViewHolder v;
            int position;

            @Override
            protected Bitmap doInBackground(Object... params) {
                v = (ViewHolder) params[0];
                position = (Integer)params[2];
                try {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    Note rowItem = (Note) params[1];
                    BitmapFactory.decodeStream(
                            new FileInputStream(new File(rowItem.image)),
                            null,
                            options
                    );

                    final int height = options.outHeight;
                    final int width = options.outWidth;
                    int inSampleSize = 1;

                    if (height > MainActivity.NOTE_HEIGHT || width > MainActivity.NOTE_WIDTH) {

                        // Calculate ratios of height and width to requested height and width
                        final int heightRatio = Math.round((float) height / (float) MainActivity.NOTE_HEIGHT);
                        final int widthRatio = Math.round((float) width / (float) MainActivity.NOTE_WIDTH);

                        // Choose the smallest ratio as inSampleSize value, this will guarantee
                        // a final image with both dimensions larger than or equal to the
                        // requested height and width.
                        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
                    }
                    options.inSampleSize = inSampleSize;
                    options.inJustDecodeBounds = false;

                    return BitmapFactory.decodeStream(
                            new FileInputStream(new File(rowItem.image)),
                            null,
                            options
                    );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (v.position == position) {
                    if (result == null) return;
                    // If this item hasn't been recycled already, hide the
                    // progress and set and show the image
                    v.progress.setVisibility(View.GONE);
                    v.image.setVisibility(View.VISIBLE);
                    v.image.setImageBitmap(result);
                }
            }
        }.execute(holder, rowItem, position);
        return convertView;
    }
//
//    public static Bitmap decodeFile(File f,int WIDTH,int HEIGHT){
//        try {
//            //Decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
//
//
//            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//            if(WIDTH == 0)
//                WIDTH = metrics.widthPixels;
//            //The new size we want to scale to
//            final int REQUIRED_HEIGHT=HEIGHT;
//            final int REQUIRED_WIDTH=WIDTH;
//
//            //Find the correct scale value. It should be the power of 2.
//            int scale=1;
//            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HEIGHT)
//                scale*=2;
//
//            //Decode with inSampleSize
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize=scale;
//            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
//        }
//        catch (FileNotFoundException e) {}
//        return null;
//    }
}

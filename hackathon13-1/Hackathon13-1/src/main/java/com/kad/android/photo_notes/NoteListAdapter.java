package com.kad.android.photo_notes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
    private List<Note> notes;

    public NoteListAdapter(Context context, int textViewResourceId, List<Note> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        notes = objects;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            ViewHolder holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.note, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.created = (TextView) convertView.findViewById(R.id.created);
            holder.image.setVisibility(View.INVISIBLE);
            convertView.setTag(holder);
        }
        {
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            final Note rowItem = notes.get(position);
            holder.title.setText(rowItem.title);
            holder.image.setVisibility(View.INVISIBLE);
            holder.progress.setVisibility(View.VISIBLE);
            holder.created.setText(rowItem.created);
            holder.position = position;
            new AsyncTask<Object, Void, Bitmap>() {
                private ViewHolder v;
                int position;

                @Override
                protected Bitmap doInBackground(Object... params) {
                    v = (ViewHolder) params[0];
                    position = (Integer) params[2];
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
                    if(position != v.position) return;
                    v.progress.setVisibility(View.GONE);
                    v.image.setVisibility(View.VISIBLE);
                    v.image.setImageBitmap(result);
                }
            }.execute(holder, rowItem, position);
        }

        if(null != convertView && null != parent)
            MainActivity.log("LIST", convertView.findViewById(R.id.image).toString()+" "+parent.getClass()+" "+position+" ");
        return convertView;
    }

    ;

    class ViewHolder {
        public TextView title, created;
        public ImageView image;
        public ProgressBar progress;
        public int position;
    }
}

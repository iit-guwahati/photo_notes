package com.kad.android.hackathon13_1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NoteView holder = null;
        Note rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.note, null);
            holder = new NoteView();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.created = (TextView) convertView.findViewById(R.id.created);

            convertView.setTag(holder);
        } else
            holder = (NoteView) convertView.getTag();

        holder.title.setText(rowItem.title);
        holder.image.setImageURI(Uri.parse(rowItem.image));//setImageBitmap(decodeFile(new File(rowItem.image), 0, 256));
        holder.created.setText(rowItem.created);
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

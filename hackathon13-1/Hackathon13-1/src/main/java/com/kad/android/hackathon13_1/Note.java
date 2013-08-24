package com.kad.android.hackathon13_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kAd on 24/08/13.
 */
public class Note {
    public int id;
    public String title, content, modified, created;
    public String image;

    public Note(int id, String title, String content, String image, String modified, String created) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.modified = modified;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = sdf.parse(created);
            sdf = new SimpleDateFormat("MMM dd, HH:mm");
            created = sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.created = created;
    }
}

package com.kad.android.hackathon13_1;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

/**
 * Created by kAd on 24/08/13.
 */
public class NoteView {
    TextView title, content, created;
    ImageView image;
    public NoteView(Context context,int id, String t_title, String t_content, String t_created, String uri){
        title = new TextView(context);
        content = new TextView(context);
        created = new TextView(context);
        image = new ImageView(context);
        title.setText(t_title);
        content.setText(t_content);
        created.setText(t_created);
        image.setImageBitmap(NewNote.decodeFile(new File(uri), 300, 256));
    }

    public NoteView() {

    }
}

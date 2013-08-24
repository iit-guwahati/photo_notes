package com.kad.android.hackathon13_1;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleNote extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_note);
        Bundle extra = getIntent().getExtras();
        ((TextView)findViewById(R.id.title)).setText(extra.getString("title"));
        ((TextView)findViewById(R.id.content)).setText(extra.getString("content"));
        ((ImageView)findViewById(R.id.image)).setImageURI(Uri.parse(extra.getString("image")));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_note, menu);
        return true;
    }
    
}

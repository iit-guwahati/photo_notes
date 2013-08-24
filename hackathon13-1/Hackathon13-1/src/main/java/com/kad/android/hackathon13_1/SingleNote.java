package com.kad.android.hackathon13_1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SingleNote extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_note);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_note, menu);
        return true;
    }
    
}

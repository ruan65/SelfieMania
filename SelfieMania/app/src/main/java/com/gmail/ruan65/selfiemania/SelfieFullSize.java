package com.gmail.ruan65.selfiemania;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class SelfieFullSize extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_full_size);

        ImageView iv = (ImageView) findViewById(R.id.selfie_full_size);

        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("path"));

        iv.setImageBitmap(bitmap);
    }

}

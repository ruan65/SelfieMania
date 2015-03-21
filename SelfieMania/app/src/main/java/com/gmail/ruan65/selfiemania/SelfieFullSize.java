package com.gmail.ruan65.selfiemania;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class SelfieFullSize extends ActionBarActivity {

    public final static int size = 480;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_full_size);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F38")));

        ImageView iv = (ImageView) findViewById(R.id.selfie_full_size);

        Bitmap bitmap = ImageHelper.decodeSampledBitmapFromResource(
                getIntent().getStringExtra("path"), size, size, Bitmap.Config.RGB_565);

        iv.setImageBitmap(bitmap);
    }

}

package com.gmail.ruan65.selfiemania;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Preview extends ActionBarActivity {

    private static final int CAMERA_RESULT = 0xface;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F38")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView) findViewById(R.id.returned_image);
            imageView.setImageBitmap(bmp);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                imgUri =
                startActivityForResult(intent, CAMERA_RESULT);
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static File getOutputImgFile(String appName) {

        File imgStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

        if (!imgStorageDir.exists())
            if (!imgStorageDir.mkdir()) {
                Log.d("ml", "Failed create directory");
                return null;
            }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(imgStorageDir.getPath() + File.separator + appName + "_" + timeStamp + ".jpg");
    }

    private static Uri getOutputImgUri(String appName) {
        return Uri.fromFile(getOutputImgFile(appName));
    }
}



























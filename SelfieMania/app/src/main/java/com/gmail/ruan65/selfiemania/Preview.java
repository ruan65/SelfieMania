package com.gmail.ruan65.selfiemania;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Preview extends ActionBarActivity {

    private static final int CAMERA_RESULT = 0xface;

    ListView listView;
    SimpleAdapter sAdapter;
    ArrayList<Map<String, Object>> data;

    final String title = "title", prev = "preview", prefName = "selfie", appName = "DailySelfie";

    File extSelfieDir = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            appName);

    String[] from = {title, prev};
    int[] to = {R.id.img_title, R.id.img_prev};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F38")));

        data = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_previews);

        if (!extSelfieDir.exists()) extSelfieDir.mkdir();

        sAdapter = new SimpleAdapter(this, data, R.layout.list_item, from, to);

        listView.setAdapter(sAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        File files[] = extSelfieDir.listFiles();
        Arrays.sort(files, Collections.reverseOrder());

        data.clear();

        for (int i = 0; i < files.length; i++) {

            Map m = new HashMap<>();
            m.put(title, files[i].getName());
            m.put(prev, R.drawable.ic_photo_camera_white_24dp);

            data.add(m);
        }
        sAdapter.notifyDataSetChanged();
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

                if (intent.resolveActivity(getPackageManager()) != null) {

                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(getOutputImgFile(extSelfieDir, appName)));

                    startActivityForResult(intent, CAMERA_RESULT);
                }
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static File getOutputImgFile(File imgStorageDir, String name) {

        if (!imgStorageDir.exists())
            if (!imgStorageDir.mkdir()) {
                Log.d("ml", "Failed create directory");
                return null;
            }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(imgStorageDir.getPath() + File.separator + name + "_" + timeStamp + ".jpg");
    }
}



























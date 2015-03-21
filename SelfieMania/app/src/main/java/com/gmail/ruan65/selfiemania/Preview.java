package com.gmail.ruan65.selfiemania;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class Preview extends ActionBarActivity {

    private static final int CAMERA_RESULT = 0xface;
    private static final int THUMBSIZE = 72;

    ListView listView;

    SelfieAdapter sAdapter;

    final String appName = "DailySelfie";

    File extSelfieDir = new File(Environment.getExternalStorageDirectory(), appName + "/images/");

    File thumbDir = new File(Environment.getExternalStorageDirectory(), appName + "/thumbs/");

    List<ThumbData> thumbsList = new ArrayList<>();

    Bitmap tmp;
    File currentSelfieFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#689F38")));

        listView = (ListView) findViewById(R.id.list_previews);

        tmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_photo_camera_white_24dp);

        if (!extSelfieDir.exists()) extSelfieDir.mkdirs();
        if (!thumbDir.exists()) thumbDir.mkdirs();

        sAdapter = new SelfieAdapter(this, thumbsList);

        listView.setAdapter(sAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Preview.this, SelfieFullSize.class);
                i.putExtra("path", thumbsList.get(position).imgFile.getPath());
                startActivity(i);
            }
        });

        if (!thumbDir.exists()) thumbDir.mkdir();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fillData(extSelfieDir.listFiles(), null);
        sAdapter.notifyDataSetChanged();
        new PrepareAndShowThumbBitmaps().execute();
    }

    private void fillData(File[] files, Bitmap[] imgs) {

        thumbsList.clear();

        for (int i = 0; i < files.length; i++) {

            if (imgs == null) thumbsList.add(new ThumbData(files[i]));
            else thumbsList.add(new ThumbData(files[i], imgs[i]));
        }
    }

    private class PrepareAndShowThumbBitmaps extends AsyncTask<Void, Void, Bitmap[]> {

        File[] imgFiles;
        Bitmap[] thumbs;

        @Override
        protected Bitmap[] doInBackground(Void... params) {

            imgFiles = extSelfieDir.listFiles();
            thumbs = new Bitmap[imgFiles.length];

            Arrays.sort(imgFiles);
            int i = 0;
            for (File f : imgFiles) {

                String path = thumbDir.getPath() + "/" + f.getName();
                Bitmap thumb = BitmapFactory.decodeFile(path);
                thumbs[i++] = thumb == null ? tmp : thumb;
                Log.d("ml", "path: " + path);
            }
            return thumbs;
        }

        @Override
        protected void onPostExecute(Bitmap[] bitmaps) {
            super.onPostExecute(bitmaps);
            fillData(imgFiles, thumbs);
            sAdapter.notifyDataSetChanged();
        }
    }

    private void saveThumbAsync(final File f) {

        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();

                FileOutputStream out = null;
                try {
                    File file = new File(thumbDir, f.getName());
                    out = new FileOutputStream(file);
                    Bitmap thumb = ThumbnailUtils.extractThumbnail(
                            BitmapFactory.decodeFile(f.getPath()), THUMBSIZE, THUMBSIZE);
                    thumb.compress(Bitmap.CompressFormat.JPEG, 80, out);
                } catch (Exception e) {
                    Log.e("ml", e.getMessage());
                } finally {
                    if (out != null) try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
        try {
            t.join();
            new PrepareAndShowThumbBitmaps().execute();
        } catch (InterruptedException e) {
            Log.d("ml", "Something went wrong while updating thumbs: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
            saveThumbAsync(currentSelfieFile);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (currentSelfieFile != null)
            outState.putString("currentSelfieFile", currentSelfieFile.getPath());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null)
            currentSelfieFile = new File(savedInstanceState.getString("currentSelfieFile"));
        super.onRestoreInstanceState(savedInstanceState);
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

                    currentSelfieFile = getOutputImgFile(extSelfieDir, appName);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentSelfieFile));

                    startActivityForResult(intent, CAMERA_RESULT);
                }
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static File getOutputImgFile(File imgStorageDir, String name) {

        if (!imgStorageDir.exists()) imgStorageDir.mkdir();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(imgStorageDir.getPath() + File.separator + name + "_" + timeStamp + ".jpg");
    }

    private class SelfieAdapter extends ArrayAdapter<ThumbData> {

        public SelfieAdapter(Context context, List<ThumbData> thumbs) {
            super(context, 0, thumbs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            ThumbData thumb = thumbsList.get(position);

            if (v == null) v = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            ((TextView) v.findViewById(R.id.img_title)).setText(thumb.imgFile.getName());

            ((ImageView) v.findViewById(R.id.img_prev)).setImageBitmap(thumb.img);

            return v;
        }
    }

    private class ThumbData {

        Bitmap img;
        File imgFile;

        ThumbData(File f) {
            this(f, tmp);
        }

        ThumbData(File f, Bitmap b) {
            imgFile = f;
            img = b;
        }

        public void setB(Bitmap b) {
            img = b;
        }

        public void setImgFile(File f) {
            imgFile = f;
        }
    }

    /**
     * This is for preventing app crash after pressing hardware Menu button
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}



























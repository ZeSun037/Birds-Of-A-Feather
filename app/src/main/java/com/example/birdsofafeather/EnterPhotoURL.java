package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;


import java.io.InputStream;
import java.util.UUID;
import java.util.function.BinaryOperator;

public class EnterPhotoURL extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_photo_url);
        db = AppDatabase.singleton(this);
    }


    public void onUpdateClicked(View view) {
        EditText urlView = (EditText) findViewById(R.id.url);
        String url = urlView.getText().toString();


        new DownloadImageTask((ImageView) findViewById(R.id.profile_picture))
                .execute(url);
    }

    public void onConfirmClicked(View view) {
        EditText urlView = (EditText) findViewById(R.id.url);
        String url = urlView.getText().toString();
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        prefs.edit().putString("profile_url", url).commit();
        prefs.edit().putBoolean("first_run", false).commit();
        BoF user = new BoF(prefs.getString("user_name", "none"), url);
        String uniqueID = UUID.randomUUID().toString();
        user.setUuid(uniqueID);

        // Might be repetitive to save the id?
        long user_id = db.boFDao().addBoF(user);
        prefs.edit().putLong("user_id", user_id).commit();
        Intent mainPage = new Intent(this, CourseEnter.class);
        //searchBof.putExtra("person_id", 12345678);
        startActivity(mainPage);

    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            //Toast.makeText(EnterPhotoURL.this, urldisplay, Toast.LENGTH_SHORT).show();
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Toast.makeText(EnterPhotoURL.this, "Please enter valid URL", Toast.LENGTH_SHORT).show();
            } else {
                bmImage.setImageBitmap(result);
            }
        }
    }








}

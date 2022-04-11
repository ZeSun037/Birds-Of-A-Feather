package com.example.birdsofafeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;

import java.io.InputStream;
import java.util.List;


public class PersonDetailActivity extends AppCompatActivity {
    private AppDatabase db;
    private BoF person;

    private RecyclerView coursesRecyclerView;
    private RecyclerView.LayoutManager coursesLayoutManager;
    private CoursesViewAdapter coursesViewAdapter;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        Intent intent = getIntent();
        long personId = intent.getLongExtra("person_id", 0);

        prefs = getSharedPreferences("app", MODE_PRIVATE);

        this.db = AppDatabase.singleton(this);
        this.person = db.boFDao().get(personId);

        if (person.getWavedTo() == 1) {
            ImageButton hollowHand = findViewById(R.id.hollow_hand);
            ImageButton solidHand = findViewById(R.id.solid_hand);
            hollowHand.setVisibility(View.INVISIBLE);
            solidHand.setVisibility(View.VISIBLE);
        }

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", 1);
        List<Course> viewUserCourses = db.courseDao().getAllCourses(personId);
        List<Course> appUserCourses = db.courseDao().getAllCourses(userId);

        viewUserCourses.removeIf(course -> !appUserCourses.contains(course));

        setTitle(person.getName());   // edit tostring latter
        TextView textView = findViewById(R.id.name_view);
        textView.setText(person.getName());

        // set up the recycler view to show database contents
        coursesRecyclerView = findViewById(R.id.courses_view);
        coursesLayoutManager = new LinearLayoutManager(this);
        coursesRecyclerView.setLayoutManager(coursesLayoutManager);

        coursesViewAdapter = new CoursesViewAdapter(viewUserCourses);
        coursesRecyclerView.setAdapter(coursesViewAdapter);

        String url = person.getProfileImgURL();
        if (url != null) {
            new PersonDetailActivity.DownloadImageTask((ImageView) findViewById(R.id.imageView))
                    .execute(url);
        }

    }

    public void onGoBackClicked(View view) {
        finish();
    }

    public void onWaveSent(View view) {
        ImageButton hollowHand = findViewById(R.id.hollow_hand);
        ImageButton solidHand = findViewById(R.id.solid_hand);
        hollowHand.setVisibility(View.INVISIBLE);
        solidHand.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Wave sent!", Toast.LENGTH_SHORT).show();

        person.setWavedTo(1);
        db.boFDao().updateBoF(person);

        prefs.edit().putString("user_has_waved", person.getUuid()).commit();
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
            if (result != null) {
                bmImage.setImageBitmap(result);
            }
        }
    }
}

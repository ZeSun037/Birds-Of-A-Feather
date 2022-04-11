package com.example.birdsofafeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Session;

import java.util.ArrayList;
import java.util.List;

public class SaveSession extends AppCompatActivity {

    private AppDatabase db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_session);
        db = AppDatabase.singleton(this);

        prefs = getSharedPreferences("app", MODE_PRIVATE);

        List<Course> currentQuarterCourses = db.courseDao().getAllCoursesInQuarter(1, "WI", 2022);
        List<String> courses = new ArrayList<>();
        for (Course c: currentQuarterCourses) {
            courses.add(c.getDepartment() + " " + c.getClassNumber());
        }
        courses.add(0, "Select Course");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courses);
        Spinner courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        courseSpinner.setAdapter(adapter);

    }

    public void onSaveClicked(View view) {
        Button button = (Button) findViewById(R.id.save);
        Spinner courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        TextView inputName = findViewById(R.id.enter_session_name);
        String sessionName = "";

        if (courseSpinner.getSelectedItem().toString().equals("Select Course") && inputName.getText().toString().equals("")) {
            Toast.makeText(this, "Must enter a name or select from the entered course(s)", Toast.LENGTH_SHORT).show();
            return;
        } else if (!courseSpinner.getSelectedItem().toString().equals("Select Course") && !inputName.getText().toString().equals("")) {
            Toast.makeText(this, "Choose only one way to name the session", Toast.LENGTH_SHORT).show();
            return;
        } else if (courseSpinner.getSelectedItem().toString().equals("Select Course")) {
            sessionName = inputName.getText().toString();
        } else {
            sessionName = courseSpinner.getSelectedItem().toString();
        }
        if (db.sessionDao().getSessionByName(sessionName) != null) {
            Toast.makeText(this, "No duplicate session name is allowed!", Toast.LENGTH_SHORT).show();
            return;
        }
//        List<BoF> bofs = db.boFDao().getAll();
//        String concatIds = "";
//        for (BoF bof: bofs) {
//            concatIds += bof.getUserId() + ",";
//        }
//        concatIds = concatIds.substring(0,concatIds.length()-1);    // remove the last comma
        String currSessionName = prefs.getString("session_name", "");
        if (currSessionName.equals(prefs.getString("new_session_name", ""))) {
            Session tempNewSession = db.sessionDao().getSessionByName(prefs.getString("new_session_name", ""));
            Session newSession = new Session(sessionName, tempNewSession.getConcatIds());
            db.sessionDao().addSession(newSession);
            db.sessionDao().deleteSession(tempNewSession);
        }

        // TODO: display the name of session in searchBof
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        prefs.edit().putString("session_name", sessionName).commit();

        this.finish();
    }




}
package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SessionPrompt extends AppCompatActivity {

    protected AppDatabase db;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_prompt);
        db = AppDatabase.singleton(this);
        prefs = getSharedPreferences("app", MODE_PRIVATE);
        List<Session> allSessions = db.sessionDao().getAll();
        createSessionSpinner(allSessions);
    }

    public void onStartNewClicked(View view) {

//        Intent courseEnter = new Intent(this, CourseEnter.class);
//        startActivity(courseEnter);
        //TODO: Tyler work on start new session (reenter courses, etc).
        // TODO: Replace "New Session" with the timestamp of its creation instead
//        Long tsLong = System.currentTimeMillis()/1000;
//        String timestamp = tsLong.toString();
//        Calendar c = Calendar.getInstance();
//        long timestamp = c.getTimeInMillis();
//        Date d = new Date(timestamp );

        long currentDateTime = System.currentTimeMillis();
        Date currentDate = new Date(currentDateTime);
        DateFormat df = new SimpleDateFormat("M/dd/yy h:mm aa");
        String timestamp_date = df.format(currentDate);

        prefs.edit().putBoolean("new_session_started", true).commit();
        prefs.edit().putString("new_session_name", timestamp_date).commit();
        prefs.edit().putString("session_name", timestamp_date).commit();
        db.sessionDao().addSession(new Session(timestamp_date, ""));
        this.finish();
    }

    public void onResumeOldClicked(View view) {
        //TODO: Tyler work on resume old session
        Spinner sessionSpinner = (Spinner) findViewById(R.id.saved_sessions);
        String sessionName = sessionSpinner.getSelectedItem().toString();
//        Intent oldSession = new Intent(this, SearchBof.class);
//        oldSession.putExtra("session_name", sessionName);
//        prefs.edit().putBoolean("session_not_selected", false).commit();
//        startActivity(oldSession);
        prefs.edit().putString("session_name", sessionName).commit();
        prefs.edit().putBoolean("session_resumed", true).commit();
        prefs.edit().putBoolean("session_not_selected", false).commit();
        this.finish();
    }



    private void createSessionSpinner(List<Session> sessionList) {
        ArrayList<String> sessions = new ArrayList<String>();

        sessions.add(0, "Select session");

        for (Session s: sessionList) {
            sessions.add(s.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sessions);

        Spinner spinSession = (Spinner) findViewById(R.id.saved_sessions);

        spinSession.setAdapter(adapter);
    }



}

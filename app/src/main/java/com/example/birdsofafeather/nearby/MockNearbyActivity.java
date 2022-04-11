package com.example.birdsofafeather.nearby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;

import android.os.Bundle;

import com.example.birdsofafeather.R;
import com.example.birdsofafeather.Utility;
import com.example.birdsofafeather.db.Session;
import com.google.android.gms.nearby.messages.Message;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockNearbyActivity extends AppCompatActivity {
    private AppDatabase db;
    private MessageListener mockListener;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_nearby);

        db = AppDatabase.singleton(this);
        prefs = getSharedPreferences("app", MODE_PRIVATE);

        mockListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                byte[] input = message.getContent();
                try {
                    List<Object> info = (List<Object>) Utility.deserialize(input);
                    String bofInfo = (String) info.remove(0);
                    String[] profile = bofInfo.split(";");

                    String uuid = profile[0];
                    String name = profile[1];
                    String url = profile[2];
                    String wavedStatus = profile[3];

                    BoF boFInDb;
                    if (db.boFDao().get(uuid) == null) {
                        boFInDb = new BoF(name, url);
                        boFInDb.setUuid(uuid);
                        boFInDb.setHasWaved(Integer.parseInt(wavedStatus));
                        boFInDb.setUserId(db.boFDao().addBoF(boFInDb));
                    } else {
                        boFInDb = db.boFDao().get(uuid);
                        boFInDb.setName(name);
                        boFInDb.setProfileImgURL(url);
                        boFInDb.setHasWaved(Integer.parseInt(wavedStatus));
                        db.boFDao().updateBoF(boFInDb);
                    }

                    long idIn = boFInDb.getUserId();

                    for (Object o : info) {
                        Course c = (Course) o;
                        c.setPersonId(idIn);

                        Course possibleMatch = db.courseDao().getMatchedCourse(c.getPersonId(),
                                c.getQuarter(), c.getYear(), c.getDepartment(), c.getClassNumber(), c.getSize());

                        if (possibleMatch == null) {
                            db.courseDao().addCourse(c);
                        }
                    }

                    String currSessionName = prefs.getString("session_name", null);
                    Session currSession = db.sessionDao().getSessionByName(currSessionName);
                    String currIds = currSession.getConcatIds();
                    List<String> bofIds = Arrays.asList(currIds.split(","));

                    if (currIds.equals("")) {
                        currIds = idIn + "";
                    } else {
                        if (!bofIds.contains(String.valueOf(idIn))) {
                            currIds += "," + idIn;
                        }
                    }

                    currSession.setConcatIds(currIds);
                    db.sessionDao().updateSession(currSession);

                    Log.d("Success: ", "New BoF found!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLost(@NonNull Message message) {
                Log.d("Lost: ", "Signal Lost.");
            }
        };
    }

    public void onEnterMockClicked(View view) {
        EditText editText = findViewById(R.id.enter_user_info);
        String enteredString = editText.getText().toString();
        editText.setText("");

        if (enteredString.equals("")) {

        } else {
            // split string by newlines and create BoF
            String[] parts = enteredString.split("\n");
            String uuid = parts[0].substring(0, parts[0].indexOf(","));
            String name = parts[1].substring(0, parts[1].indexOf(","));
            String url = parts[2].substring(0, parts[2].indexOf(","));

            // check to see if an optional wave was done
            String[] lastPart = parts[parts.length - 1].split(",");
            int wavedStatus = 0;
            boolean didWave = false;
            if(lastPart[1].equals("wave")) {
                String intendedWaveUuid = lastPart[0];
                didWave = true;

                // real Bluetooth devices have user's uuid, but "abcd"
                // is to mock waving on the mock screen
                if (intendedWaveUuid.equals(db.boFDao().get(1).getUuid())
                        || intendedWaveUuid.equals("abcd")) {
                    wavedStatus = 1;
                }
//                // find BoF that waved and check if it is the user
//                String waving_user_id = lastPart[0];
//                if(waving_user_id.equals(db.boFDao().get(1).getUuid())) {
//                    // then update
//                    wavedStatus = 1;
//                }
            }

//            BoF boF = new BoF(name, url);
//            boF.setUuid(uuid);
            String str = uuid + ";" + name + ";" + url + ";" + String.valueOf(wavedStatus);

            //List<Course> courses = new ArrayList<Course>();
            List<Object> info = new ArrayList<>();      // Make a list of object to store courses and BoF info
            info.add(str);

            // adjusts where loop below will end based off if a wave was included
            int endIndexOfMockedCourses;
            if(didWave) endIndexOfMockedCourses = parts.length - 1;
            else endIndexOfMockedCourses = parts.length;

            // for each line, split by commas
            for (int i = 3; i < endIndexOfMockedCourses; i++) {
                String line = parts[i];

                String[] courseFields = line.split(",");
                Course newCourse = new Course(0, Integer.valueOf(courseFields[0]), courseFields[1],
                        courseFields[2], courseFields[3],courseFields[4]);

                info.add(newCourse);
            }

            try {
                Message testInput = new Message(Utility.serialize(info));
                MessageListener fListener = new FakedMessageListener(mockListener);
                fListener.onFound(testInput);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onGoBackClicked(View view) {
        this.finish();
    }
}
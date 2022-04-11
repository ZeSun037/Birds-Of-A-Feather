package com.example.birdsofafeather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;

import com.example.birdsofafeather.nearby.MockNearbyActivity;
import com.example.birdsofafeather.sorting.PrioritizeMatchNumbers;
import com.example.birdsofafeather.db.Session;

import com.example.birdsofafeather.sorting.PrioritizeRecentCourses;
import com.example.birdsofafeather.sorting.PrioritizeSmallCourses;
import com.example.birdsofafeather.sorting.PrioritizeWaves;
import com.example.birdsofafeather.sorting.SortingStrategy;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
//import com.google.android.gms.nearby.messages.samples.nearbydevices.databinding.ActivityMainBinding;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.example.birdsofafeather.nearby.MessageLogger;
import java.util.concurrent.ScheduledExecutorService;

public class SearchBof extends AppCompatActivity {
    protected PersonsViewAdapter personsViewAdapter;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected RecyclerView personsRecyclerView;

    protected AppDatabase db;

    SharedPreferences prefs;

    private MessageListener mListener;
    private Message currentMessage;

    private Session sn;

    EditText sessionName;

    private MessageLogger mLogger;

    private MessagesClient mClient;

    private static final String TAG = SearchBof.class.getSimpleName();
    private static final int TTL_IN_SECONDS = 10 * 60;

    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(TTL_IN_SECONDS).build();
    private static final String MISSING_API_KEY = "It's possible that you haven't added your" +
            " API-KEY. See  " +
            "https://developers.google.com/nearby/messages/android/get-started#step_4_configure_your_project";

    private ArrayAdapter<String> mNearbyDevicesArrayAdapter;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    //private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bof);

        sessionName = new EditText(this);
        db = AppDatabase.singleton(this);

        SearchBof currentActivity = this;

        mListener = new MessageListener() {
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

                    Boolean waveIncoming = (Boolean) info.get(info.size() - 2);
                    int wavedStatus = 0;

                    if (waveIncoming) {
                        String waveToUuid = (String) info.get(info.size() - 1);
                        if (waveToUuid.equals(db.boFDao().get(1).getUuid())) {
                            wavedStatus = 1;
                        }
                    }

                    // Remove boolean and string fields at the end for wave information
                    info.remove(info.size() - 1);
                    info.remove(info.size() - 1);

                    BoF boFInDb;
                    if (db.boFDao().get(uuid) == null) {
                        boFInDb = new BoF(name, url);
                        boFInDb.setUuid(uuid);
                        boFInDb.setHasWaved(wavedStatus);
                        boFInDb.setUserId(db.boFDao().addBoF(boFInDb));
                    } else {
                        boFInDb = db.boFDao().get(uuid);
                        boFInDb.setName(name);
                        boFInDb.setProfileImgURL(url);
                        // This ensures that if a BoF has already waved, their wave status
                        // does not change if this current message does not carry a wave
                        boFInDb.setHasWaved(Math.max(boFInDb.getHasWaved(), wavedStatus));
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

                    currentActivity.updateBoFList();

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

        mClient = new MessageLogger(Nearby.getMessagesClient(this));
        personsRecyclerView = findViewById(R.id.persons_view);
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);
//
//        // Set the listener for Nearby beacon
//        Button button = findViewById(R.id.start_searching);
//
//        button.setOnClickListener(view1 -> {
//            try {
//                long userId = db.boFDao().get(1).getUserId();
//                String userName = db.boFDao().get(userId).getName();
//                String url = db.boFDao().get(1).getProfileImgURL();
//                String profile = userName + ";" + url;
//
//                Message userMessage = new Message(Utility.serialize(db.courseDao()
//                        .getAllCourses(userId)), profile);
//
//                // creates beacon
//                if (button.getText().equals("Start")) {
//                    Nearby.getMessagesClient(this).publish(userMessage);
//                    Nearby.getMessagesClient(this).subscribe(mListener);
//                    button.setText("Stop");
//                } else {
//                    Nearby.getMessagesClient(this).unpublish(userMessage);
//                    Nearby.getMessagesClient(this).unsubscribe(mListener);
//                    button.setText("Start");
//                }
//            } catch ()

        this.prefs = getSharedPreferences("app", MODE_PRIVATE);
        prefs.edit().putBoolean("session_not_selected", true).commit();

        ArrayList<BoF> bofList = new ArrayList<BoF>();

        /*** if we have a session name to start, then start that session ***/
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sn = db.sessionDao().getSessionByName(extras.getString("session_name"));
            if (sn != null) {
                String[] ids = sn.getConcatIds().split(",");
                for(String id: ids) {
                    BoF b = db.boFDao().get(Long.valueOf(id));
                    bofList.add(b);
                }
            }
        }

        personsViewAdapter = new PersonsViewAdapter(bofList, new PrioritizeMatchNumbers(db),
                                new PrioritizeWaves(db));
        personsRecyclerView.setAdapter(personsViewAdapter);

        ArrayList<String> choices = new ArrayList<>();
        choices.add(0, "Prioritize match numbers");
        choices.add("Prioritize recent");
        choices.add("Prioritize small classes");
        //choices.add("This quarter only");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, choices);

        Spinner choiceSpinner = (Spinner) findViewById(R.id.choice);
        choiceSpinner.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (currentMessage != null) {
            String uuidToWave = prefs.getString("user_has_waved", "");
            if (!uuidToWave.equals("")) {
                this.switchPublishToWave(uuidToWave);
                prefs.edit().putString("user_has_waved", "").commit();
            }
        }

        this.updateBoFList();
    }

    private void updateBoFList() {
        if (this.prefs.getBoolean("new_session_started", false)) {
            prefs.edit().putBoolean("new_session_started", false).commit();
            personsViewAdapter.clearBoFList();
        } else if (this.prefs.getBoolean("session_resumed", false)) {
            prefs.edit().putBoolean("session_resumed", false).commit();
            personsViewAdapter.clearBoFList();
        }

        String sessionName = prefs.getString("session_name", null);
        if (sessionName == null) {
            return;
        }

        Session resumedSession = db.sessionDao().getSessionByName(sessionName);

        String[] ids = resumedSession.getConcatIds().split(",");
        List<BoF> bofsInCurrentSession = new LinkedList<>();
        for(String id : ids) {
            if (!id.equals("")) {
                bofsInCurrentSession.add(db.boFDao().get(Long.valueOf(id)));
            }
        }

        this.zeroMatchingCourses();
        this.updateMatchingCoursesCount();
        this.insertMatchesIntoList(bofsInCurrentSession);
    }


    public void onStartClicked(View view) {
        Button button = (Button) findViewById(R.id.start_searching);
        Message userMessage = this.createMessage(false, "");
        this.currentMessage = userMessage;

        /***
         * choose which session to start if haven't
         * if chosen, then start matching
         * ***/
        if (button.getText().equals("Start")) {
            if (prefs.getBoolean("session_not_selected", true)) {
                this.future = backgroundThreadExecutor.submit(() -> {
                    this.publishSubscribe(userMessage);
                    return null;
                });

                button.setText("Stop");

                Intent prompt_sessions = new Intent(this, SessionPrompt.class);
                startActivity(prompt_sessions);
            }
        }
        /***
         * save session when stop clicked
         */
        else {
            this.future = backgroundThreadExecutor.submit(() -> {
                this.unpublishUnsubscribe(userMessage);
                return null;
            });

            this.currentMessage = null;
            button.setText("Start");

            prefs.edit().putBoolean("session_not_selected", true).commit();

            if (prefs.getString("session_name", null).equals(prefs.getString("new_session_name", ""))) {
                Intent saveSession = new Intent(this, SaveSession.class);
                startActivity(saveSession);
            }
        }
    }

    public void publishSubscribe(Message userMessage) {
        Log.d(TAG, "Publishing");
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.d(TAG, "No longer publishing");
                        //runOnUiThread(() -> binding.publishSwitch.setChecked(false));
                    }
                }).build();

        mClient.publish(userMessage, options);
        mClient.subscribe(mListener);
    }

    public void unpublishUnsubscribe(Message userMessage) {
        mClient.unpublish(userMessage);
        mClient.unsubscribe(mListener);
    }

    public Message createMessage(boolean isWaving, String waveToUuid) {
        Message userMessage = null;
        long userId = db.boFDao().get(1).getUserId();

        String uuid = db.boFDao().get(userId).getUuid();
        String userName = db.boFDao().get(userId).getName();
        String url = db.boFDao().get(1).getProfileImgURL();
        String profile = uuid + ";" + userName + ";" + url;

        List<Object> info = new ArrayList<>();      // Make a list of object to store courses and BoF info
        info.add(profile);

        List<Course> courses = db.courseDao().getAllCourses(userId);
        for (Course course : courses) {
            info.add(course);
        }

        info.add(Boolean.valueOf(isWaving));
        info.add(waveToUuid);

        try {
            byte[] encoded = Utility.serialize(info);
            //String encodedStr = Arrays.toString(encoded);
            userMessage = new Message(encoded);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userMessage;
    }

    public void switchPublishToWave(String uuidToWave) {
        Message message = this.createMessage(true, uuidToWave);

        this.future = backgroundThreadExecutor.submit(() -> {
            this.unpublishUnsubscribe(this.currentMessage);
            this.publishSubscribe(message);

            runOnUiThread(() -> {
                this.currentMessage = message;
            });

            return null;
        });
    }

    /*
        Goes through all the BoFs (in the database), compare their courses to the user, to update
        the number of courses shared for each other user (in reference to the app user) and update
        in the database.
    */
    public void zeroMatchingCourses() {
        List<BoF> allBoFs = db.boFDao().getAll();

        for (BoF boF : allBoFs) {
            boF.setNumCoursesShared(0);
            db.boFDao().updateBoF(boF);
        }
    }

    public void updateMatchingCoursesCount() {
        List<Course> allCourses = db.courseDao().getAllCourses(1);

        for(Course course : allCourses) {
            List<Course> matches = db.courseDao().getMatchingCourses(1,
                    course.getQuarter(), course.getYear(), course.getDepartment(),
                    course.getClassNumber());

            for(Course course2 : matches) {
                // This gets the user associated with the matched course
                BoF matched_user = db.boFDao().get(course2.getPersonId());
                matched_user.setNumCoursesShared(matched_user.getNumCoursesShared() + 1);
                db.boFDao().updateBoF(matched_user);
            }
        }

    }

    /*
        Get all matched users onto the display list. Sort them afterwards in the adapter
     */
    public void insertMatchesIntoList(List<BoF> boFList) {
        for (BoF matched_user : boFList) {
            matched_user = db.boFDao().get(matched_user.getUserId());
            if (matched_user.getNumCoursesShared() > 0) {
                personsViewAdapter.addPerson(matched_user);
            }
        }
    }

    public void onGoToMockClicked(View view) {
        Intent intent = new Intent(this, MockNearbyActivity.class);
        startActivity(intent);

    }

    public void onSyncClicked(View view) {
        Spinner choiceSpinner = (Spinner) findViewById(R.id.choice);
        String choice = choiceSpinner.getSelectedItem().toString();
        SortingStrategy sortingStrategy;
        switch (choice) {
            case "Prioritize recent":
                sortingStrategy = new PrioritizeRecentCourses(db);
                break;
            case "Prioritize small classes":
                sortingStrategy = new PrioritizeSmallCourses(db);
                break;
            case "No sorting or filter":
                sortingStrategy = new PrioritizeMatchNumbers(db);
                break;
            default:
                sortingStrategy = null;
        }
        if (sortingStrategy == null) {
            // Move to Iteration 2
//            List<BoF> bofs = db.boFDao().getAllExceptUser(1);
//            for (BoF matched_user : bofs) {
//                if (matched_user.getNumCoursesShared() > 0) {
//                    personsViewAdapter.addPerson(matched_user);
//                }
//            }
        } else {
            personsViewAdapter.setSortingStrategy(sortingStrategy);
        }
    }


    public void onFavoritesClicked(View view) {
        TextView listName = (TextView) findViewById(R.id.list_name);
        if (listName.getTag() == null || listName.getTag() == "All Bofs") {
            listName.setTag("Favorites");
            listName.setText("Favorites");
        }
        else if (listName.getTag() == "Favorites") {
            listName.setTag("All Bofs");
            listName.setText("All Bofs");
        }
        /***
         personsViewAdapter.updateFavoriteList(db, extras.getString("session_name")); ***/
        personsViewAdapter.switchFavoriteList(db);
    }
}

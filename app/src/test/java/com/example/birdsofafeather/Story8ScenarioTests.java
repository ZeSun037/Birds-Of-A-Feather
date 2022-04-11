package com.example.birdsofafeather;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.AutomaticZenRule;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Session;

// Source: http://robolectric.org/writing-a-test/
@RunWith(RobolectricTestRunner.class)
public class Story8ScenarioTests {

    private AppDatabase db;
    private SessionPrompt activity;
    private SharedPreferences prefs;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestSingleton(context);
        db = AppDatabase.singleton(context);
    }

    @Test
    public void testNoSavedSessions() {
        // No sessions in the database right now
        activity = Robolectric.setupActivity(SessionPrompt.class);

        Spinner selectSessions = activity.findViewById(R.id.saved_sessions);
        assertEquals(1, selectSessions.getAdapter().getCount());
        assertEquals("Select session", selectSessions.getAdapter().getItem(0));

        activity.findViewById(R.id.start_new).performClick();
        assertEquals(1, db.sessionDao().getAll().size());
        prefs = activity.getSharedPreferences("app", MODE_PRIVATE);
        assertEquals(prefs.getString("new_session_name", ""), db.sessionDao().getAll().get(0).getName());
    }

    @Test
    public void testMultipleSessionsSavedResumeOld() {
        // Set up some sessions in the database
        Session session1 = new Session("Session 1", "2,3");
        Session session2 = new Session("Session 2", "");
        Session session3 = new Session("Session 3", "5,16,78,89");

        db.sessionDao().addSession(session1);
        db.sessionDao().addSession(session2);
        db.sessionDao().addSession(session3);

        activity = Robolectric.setupActivity(SessionPrompt.class);

        Spinner selectSessions = activity.findViewById(R.id.saved_sessions);
        assertEquals(4, selectSessions.getAdapter().getCount());
        assertEquals("Select session", selectSessions.getAdapter().getItem(0));
        assertEquals("Session 1", selectSessions.getAdapter().getItem(1));
        assertEquals("Session 2", selectSessions.getAdapter().getItem(2));
        assertEquals("Session 3", selectSessions.getAdapter().getItem(3));

        selectSessions.setSelection(1);
        activity.findViewById(R.id.resume_old).performClick();

        prefs = activity.getSharedPreferences("app", MODE_PRIVATE);
        assertEquals("Session 1", prefs.getString("session_name", null));
    }
}
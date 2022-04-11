package com.example.birdsofafeather;

import android.app.AlertDialog;
import android.app.AutomaticZenRule;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
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

import static org.junit.Assert.*;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;

@RunWith(AndroidJUnit4.class)
public class Story9ScenarioTests {

    private AppDatabase db;
    ActivityScenario<CourseEnter> scenario;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestSingleton(context);
        db = AppDatabase.singleton(context);
        BluetoothAdapter thisDevice = BluetoothAdapter.getDefaultAdapter();
        thisDevice.enable();

        scenario = ActivityScenario.launch(CourseEnter.class);
    }

//    @Test
//    public void testMainActivityBluetooth() {
//        try(ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
//            scenario.onActivity((activity -> {
//                BluetoothAdapter thisDevice = BluetoothAdapter.getDefaultAdapter();
//                thisDevice.disable();
//
//                activity.getWarning().cancel();
//
//                AppCompatImageButton blueTooth = activity.findViewById(R.id.bluetoothButton);
//
//                blueTooth.performClick();
//
//                assertEquals(true, thisDevice.isEnabled());
//            }));
//        }
//    }

    @Test
    public void testAddCourse() {
         scenario.onActivity((activity -> {
             int size = db.courseDao().count();

             Button enter = activity.findViewById(R.id.enter);
             enter.performClick();
             assertEquals(size, db.courseDao().count());

             Spinner yearSpinner = (Spinner) activity.findViewById(R.id.yearSpinner);
             Spinner quarterSpinner = (Spinner) activity.findViewById(R.id.quarterSpinner);
             Spinner sizeSpinner = (Spinner) activity.findViewById(R.id.sizeSpinner);
             EditText subjectView = (EditText) activity.findViewById(R.id.subject);
             EditText cNumberView = (EditText) activity.findViewById(R.id.courseNumber);

             yearSpinner.setSelection(1);
             quarterSpinner.setSelection(1);
             sizeSpinner.setSelection(1);
             subjectView.setText("CSE");
             cNumberView.setText("110");

             enter.performClick();
             assertEquals(size + 1, db.courseDao().count());

             //Test ClassSize feature
             assertEquals("Tiny", db.courseDao().getCourse(1).getSize());
             //
             yearSpinner.setSelection(1);
             quarterSpinner.setSelection(1);
             sizeSpinner.setSelection(2);
             subjectView.setText("CSE");
             cNumberView.setText("120");

             enter.performClick();
             assertEquals(size + 2, db.courseDao().count());
             assertEquals("Small", db.courseDao().getCourse(2).getSize());

             //
             yearSpinner.setSelection(1);
             quarterSpinner.setSelection(1);
             sizeSpinner.setSelection(3);
             subjectView.setText("CSE");
             cNumberView.setText("130");

             enter.performClick();
             assertEquals(size + 3, db.courseDao().count());
             assertEquals("Medium", db.courseDao().getCourse(3).getSize());
            }));
    }
}
package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private RecyclerView coursesRecyclerView;
    private RecyclerView.LayoutManager coursesLayoutManager;
    private CoursesViewAdapter coursesViewAdapter;
    SharedPreferences prefs;
    
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE = 1;
    private AlertDialog warning;
    public BluetoothAdapter thisDevice = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // test only
        db = AppDatabase.singleton(getApplicationContext());

        warning = Utility.alert(this, "TBD", "Warning!");

        setContentView(R.layout.activity_main);



        if (thisDevice == null) {
            warning.setMessage("Bluetooth not supported!");
            warning.show();
        } else if (!thisDevice.isEnabled()) {
            warning.setMessage("Please Activate Bluetooth at Bottom Right Corner!");
            warning.show();
        } else {
            checkBlePermission();

            if (isFirstTimeUser()) {
                // go to name entering page
                Intent intent = new Intent(MainActivity.this, EnterUserName.class);
                startActivity(intent);
            } else if (! coursesEntered()) {
                // TODO: Check if this should be automatically redirecting us after entering profle pic
                Intent intent = new Intent(MainActivity.this, CourseEnter.class);
                startActivity(intent);
            }
        }

//        // mocking db
//        BoF p1 = new BoF("Will","DNE");
//        BoF p2 = new BoF("Tyler","DNE");
//
//        BoF p1 = new BoF("Will","DNE");
//        long p2Id = db.boFDao().addBoF(p2);
//
//        Course c1 = new Course(p1Id, 2021, "Fall", "CSE", "8A");
//        Course c2 = new Course(p2Id, 2022, "Winter", "CSE", "8A");
//
//        db.courseDao().addCourse(c1);
//        db.courseDao().addCourse(c2);
//        // end here
    }

    public boolean isFirstTimeUser() {
        prefs = getSharedPreferences("app", MODE_PRIVATE);
        return prefs.getBoolean("first_run", true);
    }

    public boolean coursesEntered() {
        prefs = getSharedPreferences("app", MODE_PRIVATE);
        return prefs.getBoolean("course_entered", false);
    }



    public void onFindClicked(View view) {
        if (isFirstTimeUser()) {
            // go to name entering page
            Intent intent = new Intent(MainActivity.this, EnterUserName.class);
            startActivity(intent);
        } else if (! coursesEntered()) {
            Intent intent = new Intent(MainActivity.this, CourseEnter.class);
            startActivity(intent);
        } else {
            Intent startSearch = new Intent(this, SearchBof.class);

            //addCourse.putExtra("person_id", 12345678);
            startActivity(startSearch);
        }
    }

    public void onActivateBluetoothClicked(View view) {
        if (!thisDevice.isEnabled()) {
            thisDevice.enable();
            checkBlePermission();
        }
    }
        //Bluetooth function demo, WIP.

    public void checkBlePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH,
                                 Manifest.permission.BLUETOOTH_ADVERTISE,
                                 Manifest.permission.BLUETOOTH_SCAN},
                    1);
            Toast.makeText(MainActivity.this, "Requesting Permission", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Bluetooth Permission Granted", Toast.LENGTH_SHORT).show();
            Log.i("tag", "Bluetooth Permission Granted.");
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


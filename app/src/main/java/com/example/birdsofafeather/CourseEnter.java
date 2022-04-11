package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.Course;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


public class CourseEnter extends AppCompatActivity {
    private Set<String> courses;
    private AppDatabase db;
    private long personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_enter);
        createYearSpinner();
        createSizeSpinner();
        createQuarterSpinner();
        courses = new HashSet<>();

        // Intent intent = getIntent();
        // personId = intent.getIntExtra("person_id",1);

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        personId = prefs.getLong("user_id", 1);

        db = AppDatabase.singleton(this);
    }

    //go back to main screen
    public void onDoneClicked(View view) {

        LinearLayout courseList = (LinearLayout) findViewById(R.id.courseList);
        if (courseList.getChildCount() == 0){
            //error
            AlertDialog alert = (AlertDialog) onCreateDialog("Must enter at least one course");
            alert.show();
        }
        else {
            SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
            prefs.edit().putBoolean("course_entered", true).commit();
            prefs = getSharedPreferences("app", MODE_PRIVATE);
            prefs.edit().putBoolean("session_not_selected", false).commit();
            Intent searchBof = new Intent(this, SearchBof.class);
            //searchBof.putExtra("person_id", 12345678);
            startActivity(searchBof);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void createYearSpinner() {
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = thisYear - 8; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        years.add(0, "Select year");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);

        Spinner spinYear = (Spinner) findViewById(R.id.yearSpinner);

        spinYear.setAdapter(adapter);
    }

    private void createSizeSpinner() {
        ArrayList<String> sizes = new ArrayList<String>();

        sizes.add(0, "size");
        sizes.add("Tiny (<40)");
        sizes.add("Small (40-75)");
        sizes.add("Medium (75-150)");
        sizes.add("Large (150-250)");
        sizes.add("Huge (250-400)");
        sizes.add("Gigantic (400+)");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sizes);

        Spinner spinSize = (Spinner) findViewById(R.id.sizeSpinner);

        spinSize.setAdapter(adapter);
    }


    private void createQuarterSpinner() {
        ArrayList<String> quarters = new ArrayList<String>();

        quarters.add(0, "Select quarter");
        quarters.add("FA");
        quarters.add("WI");
        quarters.add("SP");
        quarters.add("SS1");
        quarters.add("SS2");
        quarters.add("SSS");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quarters);

        Spinner spinQuarter = (Spinner) findViewById(R.id.quarterSpinner);

        spinQuarter.setAdapter(adapter);
    }


    public void onEnterClicked(View view) {
        Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        Spinner quarterSpinner = (Spinner) findViewById(R.id.quarterSpinner);
        Spinner sizeSpinner = (Spinner) findViewById(R.id.sizeSpinner);
        EditText subjectView = (EditText) findViewById(R.id.subject);
        EditText cNumberView = (EditText) findViewById(R.id.courseNumber);
        String year = yearSpinner.getSelectedItem().toString();
        String quarter = quarterSpinner.getSelectedItem().toString();
        String size = sizeSpinner.getSelectedItem().toString().split(" ")[0];
        String subject = subjectView.getText().toString().toUpperCase();
        String course_number = cNumberView.getText().toString().toUpperCase();

        LinearLayout courseList = (LinearLayout) findViewById(R.id.courseList);

        if ( (year.equals("year")) || (quarter.equals("quarter")) || (size.equals("size")) ||
                (course_number.equals("")) || (subject.equals("")) ) {
            //warning: must fill out all fields
            Toast.makeText(this, "Must Fill All Fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Course course = new Course(personId, Integer.parseInt(year), quarter, subject, course_number, size);
        if (courses.contains(course.toString())) {
            // warning for duplicate courses
            Toast.makeText(this, "Duplicate Courses!", Toast.LENGTH_SHORT).show();
        } else {
            TextView new_course = new TextView(this);
            new_course.setText(course.toString());
            courseList.addView(new_course);     // Add courses to courseList_view
            courses.add(course.toString());     // Add courses to course set
            db.courseDao().addCourse(course);   // Add courses to database
        }

    }

    //modified from https://developer.android.com/guide/topics/ui/dialogs
    public Dialog onCreateDialog(String message) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }











}

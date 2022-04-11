package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class EnterUserName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_user_name);

        String defaultUsername = getGoogleUsername();

        if (defaultUsername != null) {
            EditText nameField = (EditText) findViewById(R.id.name_field);
            nameField.setText(defaultUsername);
        }

    }

    public String getGoogleUsername() {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        if (list != null && list.length > 0) {
            if (list[0].type == "com.google") {
                return list[0].name;
            }
        }
        return null;

    }

    public void onEnterClicked(View view) {
        EditText name_field = (EditText) findViewById(R.id.name_field);
        String username = name_field.getText().toString();
        Toast.makeText(EnterUserName.this, username, Toast.LENGTH_SHORT).show();
        if (username.isEmpty()) {
            Toast.makeText(EnterUserName.this, "Please enter a non-empty username", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
            prefs.edit().putString("user_name", username).commit();
            Intent intent = new Intent(EnterUserName.this, EnterPhotoURL.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        Intent addCourse = new Intent(this, CourseEnter.class);
        //addCourse.putExtra("person_id", 12345678);
        startActivity(addCourse);
        super.onDestroy();
    }
}
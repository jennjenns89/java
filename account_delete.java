package com.example.pictureitgrocerylist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.pictureitgrocerylist.list.Lists;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class account_delete extends AppCompatActivity {
Button deleteAccont;
List<User> users = new ArrayList<>();
DatabaseHelper dbHelper;
EditText enter_username;
EditText enter_password;
TextView hint;
String correctUser;
String correctPass;
int correctId;
boolean userFound = false;

boolean isDeleted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_delete);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //use butterknife
        ButterKnife.bind(this);
        //set up database helper object
        dbHelper = new DatabaseHelper(this);
        //set title of screen
        getSupportActionBar().setTitle("Delete Account");

        //find views for objects
        deleteAccont = findViewById(R.id.logIn);
        enter_username = findViewById(R.id.enter_username);
        enter_password = findViewById(R.id.edit_password);
        hint = findViewById(R.id.password_hint);
        //set title of button on screen
        deleteAccont.setText("Log In And Delete Account");

    }

    @OnClick(R.id.logIn)
    void deleteClickHandler() {
        String enteredName = enter_username.getText().toString();
        String enteredPass = enter_password.getText().toString();

        //if user didn't enter information in all fields
        if (TextUtils.isEmpty(enter_username.getText()) || TextUtils.isEmpty(enter_password.getText())) {
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Please enter information in all fields";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();
        }
        if ((users.size()) > 0) {
            users.clear();
        }
        try {
            //get all records from the user table to check
            users = dbHelper.readUserRecords("SELECT * FROM user_table");
            for (User oneUser : users) {
                users.add(new User(oneUser.getUser_id(), oneUser.getUser_name(),
                        oneUser.getPassword(), oneUser.getPassword_hint()));
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < users.size(); i++) {
            User oneUser = users.get(i);
            String user_name = oneUser.getUser_name();
            String pass = oneUser.getPassword();
            String pass_hint = oneUser.getPassword_hint();

            Log.d("accountDelete ", "entered name is: " + enteredName
                    + " database user name is: " + user_name);
            //if both are correct go on to deleting account
            if (enteredName.equals(oneUser.getUser_name()) && enteredPass.equals(oneUser.getPassword())) {
                //if it finds the correct username and password break out of the loop
                Intent returnIntent = new Intent(this, MainActivity.class);
                returnIntent.putExtra("user_id", oneUser.getUser_id());
                returnIntent.putExtra("user_name", oneUser.getUser_name());
                returnIntent.putExtra("user_password", oneUser.getPassword());
                returnIntent.putExtra("user_hint", oneUser.getPassword_hint());
                returnIntent.putExtra("deleted_user", true);
                startActivity(returnIntent);
                correctUser = enteredName;
                correctPass = enteredPass;
                correctId = oneUser.getUser_id();
                userFound = true;
                break;
            }
            //if the username and password are not correct
            else if(!enteredPass.equals(pass) &&!enteredName.equals(oneUser.getUser_name())){

                    View view = findViewById(R.id.coordinatorLayout2);
                    String message = "Username and/or password are incorrect";
                    int duration = Snackbar.LENGTH_LONG;

                    Snackbar.make(view, message, duration).show();
                    userFound = false;

            }
            //if username is correct but password is incorrect
            else if (enteredName.equals(user_name) && !enteredPass.equals(pass)) {
                hint.setText("Password hint: " + pass_hint);
                View view = findViewById(R.id.coordinatorLayout2);
                String message = "Password is incorrect";
                int duration = Snackbar.LENGTH_LONG;

                Snackbar.make(view, message, duration).show();
                userFound = false;
            }
            //if username is incorrect but a password is found
            else if (!enteredName.equals(oneUser.getUser_name()) && enteredPass.equals(oneUser.getPassword())) {
                View view = findViewById(R.id.coordinatorLayout2);
                String message = "Username is incorrect";
                int duration = Snackbar.LENGTH_LONG;
                hint.setText("");
                Snackbar.make(view, message, duration).show();
                userFound = false;
            }


        }


    }


    //This allows the "up" button to act like the "back" button
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

}

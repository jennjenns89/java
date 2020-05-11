package com.example.pictureitgrocerylist;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class account_creator extends AppCompatActivity {
EditText enter_username;
EditText enter_password;
EditText enter_hint;
DatabaseHelper dbHelper;
public final static String TAG = "Account Creator";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //set up database helper
        dbHelper = new DatabaseHelper(this);
        //get views
        enter_hint =  findViewById(R.id.edit_hint);
        enter_password = findViewById(R.id.edit_password);
        enter_username = findViewById(R.id.enter_username);

        //use butterknife
        ButterKnife.bind(this);
        //set title
        getSupportActionBar().setTitle("Account Creator");




    }

    @OnClick(R.id.submit_button)
    void doneClickHandler() {
        boolean foundName = false;
        //example of Polymorphism
        //get list of users to check if username already exists
        List<User> users = new ArrayList<>();
        users = dbHelper.readUserRecords("SELECT * FROM user_table");
        //go through users and see if user name already exists
        for(int i = 0; i < users.size(); i++){
            User oneUser = users.get(i);
            String userName = oneUser.getUser_name();
            String enteredName = enter_username.getText().toString().trim().toUpperCase();
            String upperUserName = userName.trim().toUpperCase();
            Log.d(TAG, "entered name " + enter_username.getText().toString().trim().toUpperCase() );
            Log.d(TAG, "name in database " + userName.trim().toUpperCase());
            if(enteredName.equals(upperUserName)){
                Log.d(TAG, "Same name found!");
                foundName = true;
            }

        }

        //if name was found
        if(foundName){
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Username already exists, please enter a different one.";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();
        }
        //check if userame, password, or password hint was left blank
        else if(TextUtils.isEmpty(enter_username.getText()) || TextUtils.isEmpty(enter_password.getText()) ||
                TextUtils.isEmpty(enter_hint.getText())){

            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Please enter information in all fields";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();
        }
        //check if password length entered is less than 8 characters
        else if(enter_password.getText().toString().length() < 8){

            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Password must be 8 characters or more";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();

        }
        //check if username is just blank spaces
        else if(enter_username.getText().toString()== null ||enter_username.getText().toString().trim().isEmpty()){
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "User name field is blank. Please try again.";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();
        }
        //check if password is just blank spaces
        else if( enter_password.getText().toString()== null ||enter_password.getText().toString().trim().isEmpty()) {
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Password field is blank. Please try again.";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();
        }
        //check if hint is just blank spaces
        else if( enter_hint.getText().toString()== null ||enter_hint.getText().toString().trim().isEmpty()) {
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Password hint field is blank. Please try again.";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();
        }
        else {
            //get text user entered
            String name = enter_username.getText().toString().trim();
            String password = enter_password.getText().toString();
            String hint = enter_hint.getText().toString();

            try {
                //add a record to the table if the user enters some data
                dbHelper.addUserRecord("user_name", name, "password", password, "password_hint", hint);
            } catch (NullPointerException e) {
                Log.d(TAG, "Null Pointer Exception from addUserRecord");

            }
            catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            //close database
            dbHelper.close();
            //go back
            finish();

        }
    }

}

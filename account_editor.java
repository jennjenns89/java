package com.example.pictureitgrocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class account_editor extends AppCompatActivity {
    EditText enter_username;
    EditText enter_password;
    EditText enter_hint;
    DatabaseHelper dbHelper;
    String user_name;
    String user_password;
    String pass_hint;
    int user_id;
    public final static String TAG = "Account Editor";
    Intent receiveIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //set up database helper
        dbHelper = new DatabaseHelper(this);
        //set up intent
        receiveIntent = getIntent();
        //set up views
        enter_hint =  findViewById(R.id.edit_hint);
        enter_password = findViewById(R.id.edit_password);
        enter_username = findViewById(R.id.enter_username);

        //receive variables
        user_name = receiveIntent.getStringExtra("user_name");
        user_password = receiveIntent.getStringExtra("user_password");
        pass_hint = receiveIntent.getStringExtra("user_hint");
        user_id = receiveIntent.getIntExtra("user_id", 0);
        //use butterknife
        ButterKnife.bind(this);
        //set up title
        getSupportActionBar().setTitle("Account Editor For " + user_name);

        //set text
        enter_username.setText(user_name);
        enter_password.setText(user_password);
        enter_hint.setText(pass_hint);
    }

    @OnClick(R.id.submit_button)
    void doneClickHandler() {
        boolean foundName = false;
        //example of Polymorphism
        //get list of users to check if username already exists
        List<User> users = new ArrayList<>();
        users = dbHelper.readUserRecords("SELECT * FROM user_table");
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
                if(user_name.trim().toUpperCase().equals(enteredName.toUpperCase().trim())){
                    foundName = false;
                }
            }

        }

        //if account name already exists
        if(foundName){
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Username already exists," +
                    " please enter a different one.";
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

            String name = enter_username.getText().toString().trim();
            String password = enter_password.getText().toString();
            String hint = enter_hint.getText().toString();
            String idArgs = Integer.toString(user_id);
            String[] whereArgs = {idArgs};
            try {
                //add a record to the table if the user enters some data
                dbHelper.changeUserRecord("user_table", name, password,
                         hint, "user_id = ?", whereArgs );
            } catch (NullPointerException e) {
                Log.d(TAG, "Null Pointer Exception from addUserRecord");

            }
            catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            dbHelper.close();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("account_edited",true);
            startActivity(intent);

        }
    }
    //the "up" button acts like the "back" button
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


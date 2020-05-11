package com.example.pictureitgrocerylist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.pictureitgrocerylist.list.ListViewer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
DatabaseHelper dbHelper;
private EditText user_name;
private EditText password;
private TextView hint;
private List<User> users = new ArrayList<>();
int userId;
String userName;
String userPass;
String passHint;
Intent getIntent;
boolean isDeleted;
boolean isEdited;
public final static String TAG = "Main Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getIntent = getIntent();
        //initialize EditText objects
        user_name = findViewById(R.id.edit_username);
        password = findViewById(R.id.edit_password);
        hint = findViewById(R.id.password_hint);

        //receive variables
        userId = getIntent.getIntExtra("user_id", 0);
        userName = getIntent.getStringExtra("user_name");
        userPass = getIntent.getStringExtra("user_password");
        passHint = getIntent.getStringExtra("user_hint");
        isDeleted = getIntent.getBooleanExtra("deleted_user", false);
        isEdited = getIntent.getBooleanExtra("account_edited", false);

        //use butterknife
        ButterKnife.bind(this);
        //create the database helper object
        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase();
        //create the user table if it's not already created
        dbHelper.createUserTable("user_table");
        dbHelper.createListTable("list_table");
        dbHelper.createCategoryTable("category_table");
        dbHelper.createItemTable("item_table");
        dbHelper.createItemTable("archived_item_table");




        if(isEdited){
            View view2 = findViewById(R.id.coordinatorLayout2);
            String message = "Account Successfully Edited";
            int duration2= Snackbar.LENGTH_LONG;

            Snackbar.make(view2, message, duration2).show();
        }
        if(isDeleted){
            View view = findViewById(R.id.coordinatorLayout2);
            String deleteMessage = "User " + userName + " deleted along with any lists, categories " +
                    "or items.";
            String undoClick = "UNDO";
            int duration = Snackbar.LENGTH_LONG;

            String idArgs = Integer.toString(userId);
            String [] whereArgs = {idArgs};
            //remove user
            dbHelper.removeRecord("user_table", "user_id = ?", whereArgs);

            //Create snackbar with option to undo delete
            Snackbar.make(view, deleteMessage, duration)
                    .setAction(undoClick, v -> {
                        //if user clicks "UNDO" put term back with same id
                        dbHelper.addUserRecordWithID("user_id", userId, "user_name", userName,
                                "password", userPass, "password_hint", passHint);
                        View view2 = findViewById(R.id.coordinatorLayout2);
                        String message = "User not deleted";
                        int duration2= Snackbar.LENGTH_LONG;

                        Snackbar.make(view2, message, duration2).show();


                    }).show();



        }
    }
    //if user clicks the create new account button
    @OnClick(R.id.create_new_account)
    void newAccountClickHandler(){
        Intent passIntent = new Intent(MainActivity.this, account_creator.class);
        startActivity(passIntent);

    }

    //if user clicks the edit account button
    @OnClick(R.id.editAccount)
    void editAccountHandler(){
        Intent editIntent = new Intent(this, account_editor_menu.class);
        startActivity(editIntent);
    }

    //if user clicks the delete account button
    @OnClick(R.id.deleteAccount)
    void deleteAccountHandler(){
        Intent deleteIntent = new Intent(MainActivity.this, account_delete.class);
        startActivity(deleteIntent);
    }
    //if user clicks the submit button to log in
    //currently, this application simply uses a local SQLite version but in order to keep the
    //application scalable there is a log in system that can be used and expanded on if in the
    //future there is a need to migrate to a hosted server
    @OnClick(R.id.submit)
    void loginClickHandler(){
        String enteredName = user_name.getText().toString();
        String enteredPass = password.getText().toString();

        //if user didn't enter information in all fields
        if(TextUtils.isEmpty(user_name.getText()) || TextUtils.isEmpty(password.getText())){
            View view = findViewById(R.id.coordinatorLayout2);
            String message = "Please enter information in all fields";
            int duration = Snackbar.LENGTH_LONG;

            Snackbar.make(view, message, duration).show();
        }
        if((users.size())> 0){
            users.clear();
        }
        try {
            //get all records from the user table to check
            users = dbHelper.readUserRecords("SELECT * FROM user_table");
            for (User oneUser : users) {
                users.add(new User(oneUser.getUser_id(), oneUser.getUser_name(),
                        oneUser.getPassword(), oneUser.getPassword_hint()));
            }
        }
        catch (ConcurrentModificationException e){
            e.printStackTrace();
        }
        for(int i = 0; i < users.size(); i++){
            User oneUser = users.get(i);
            String user_name = oneUser.getUser_name();
            String pass = oneUser.getPassword();
            String pass_hint = oneUser.getPassword_hint();


            //if username is correct but password is incorrect
            if(enteredName.equals(user_name) && !enteredPass.equals(pass)){
                hint.setText("Password hint: " + pass_hint);
                View view = findViewById(R.id.coordinatorLayout2);
                String message = "Password is incorrect";
                int duration = Snackbar.LENGTH_LONG;

                Snackbar.make(view, message, duration).show();
                break;
            }
            //if username is incorrect but a password is found
            else if(!enteredName.equals(oneUser.getUser_name()) && enteredPass.equals(oneUser.getPassword())){
                View view = findViewById(R.id.coordinatorLayout2);
                String message = "Username is incorrect";
                int duration = Snackbar.LENGTH_LONG;
                hint.setText("");
                Snackbar.make(view, message, duration).show();
            }
            //if neither are correct
            else if((!enteredName.equals(oneUser.getUser_name())) && (!enteredPass.equals(oneUser.getPassword()))){
                View view = findViewById(R.id.coordinatorLayout2);
                String message = "Username and password are incorrect";
                int duration = Snackbar.LENGTH_LONG;

                Snackbar.make(view, message, duration).show();
            }

            //if both are correct go to the next activity
            else{
                int user_id = oneUser.getUser_id();

                Intent passIntent = new Intent(this, ListViewer.class);
                passIntent.putExtra("user_id", user_id);
                passIntent.putExtra("user_name", user_name);
                dbHelper.close();
                startActivity(passIntent);

            }


        }



    }

}

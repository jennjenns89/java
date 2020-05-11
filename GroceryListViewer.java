package com.example.pictureitgrocerylist;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.example.pictureitgrocerylist.category.Category;
import com.example.pictureitgrocerylist.category.CategoryCreator;
import com.example.pictureitgrocerylist.category.CategoryViewHolder;
import com.example.pictureitgrocerylist.category.ExpandableAdapter;
import com.example.pictureitgrocerylist.item.Item;
import com.example.pictureitgrocerylist.list.ListViewer;
import com.example.pictureitgrocerylist.list.Lists;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroceryListViewer extends AppCompatActivity {

    public final static String TAG = "GroceryListViewer";
    List<Category> categories = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    List<Lists> lists = new ArrayList<>();
    RecyclerView recyclerView;
    ExpandableAdapter adapter;
    DatabaseHelper dbHelper;
    Intent getIntent;
    int list_id;
    int cat_id;
    String list_name;
    int user_id;
    String user_name;
    String update_time;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //user butterknife
        ButterKnife.bind(this);
        //set up database helper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase();
        //get current date and time from device
        ZonedDateTime zdt = ZonedDateTime.now();
        String currentDate =DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss").format(zdt);


        //set up intent
        getIntent = getIntent();
        //get variables
        list_id = getIntent.getIntExtra("list_id", 0);
        list_name = getIntent.getStringExtra("list_name");
        user_id = getIntent.getIntExtra("user_id", 0);
        user_name = getIntent.getStringExtra("user_name");
        //set up title on toolbar
        getSupportActionBar().setTitle(list_name);


        //get list records from database
        lists = dbHelper.readListRecords("SELECT * FROM list_table WHERE list_id = " + list_id);
        //get time from list
        for(Lists OneList: lists){
            update_time = OneList.getUpdate_time();
        }
        //modify the time on the bottom toolbar
        Toolbar editToolbar = findViewById(R.id.edit_toolbar);
        editToolbar.setTitle("Last Modified " + update_time);
        //set up lists
        List<Item> tempItem = new ArrayList<>();
        List<Category> tempCategory = new ArrayList<>();
        categories.clear();
        tempCategory.clear();
        tempItem.clear();
        //read category records
        tempCategory = dbHelper.readCategoryRecordsWithId("SELECT * FROM category_table WHERE list_id = " + list_id);
        int size =tempCategory.size();
        //go through category records
            for(int i = 0; i < size; i++){

                int cat_id = tempCategory.get(i).getCategory_id();
                String name = tempCategory.get(i).get_name();
                Log.d(TAG, "categories category id is: " + cat_id);
                Log.d(TAG, "category name is: "+ name);
                tempItem = dbHelper.readItemRecords("SELECT * FROM item_table WHERE category_id = " + cat_id);

                Category cat2 = new Category(name, tempItem);
                // categories.add(name, items);
                categories.add(cat2);
            }

        //create the recyclerview
        createRecyclerView();



    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    //This allows the "up" button to act like the "back" button to go back to category creator
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_category:
                Intent catIntent = new Intent(this, CategoryCreator.class);
                catIntent.putExtra("list_id", list_id);
                catIntent.putExtra("list_name", list_name);
                catIntent.putExtra("user_id", user_id);
                catIntent.putExtra("user_name", user_name);
                startActivity(catIntent);
                return true;


            case android.R.id.home:
                Intent listIntent = new Intent(this, ListViewer.class);
                listIntent.putExtra("user_id", user_id);
                listIntent.putExtra("user_name", user_name);
                startActivity(listIntent);
                return true;

        }

        return (super.onOptionsItemSelected(item));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void createRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //instantiate your adapter with the list of genres
        adapter = new ExpandableAdapter(categories, this, this);
        adapter.listID(list_id);
        adapter.userID(user_id);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        for (int i = adapter.getGroups().size()-1; i >=0 ; i--) {
            expandGroup(i);
        }

        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);

        recyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    protected void onResume() {
        //get lists records
        lists = dbHelper.readListRecords("SELECT * FROM list_table WHERE list_id = " + list_id);
        for(Lists OneList: lists){
            update_time = OneList.getUpdate_time();
        }
        //update bottom toolbar
        Toolbar editToolbar = findViewById(R.id.edit_toolbar);
        editToolbar.setTitle("Last Modified " + update_time);

        //set up arraylists
        List<Category> tempCategory = new ArrayList<>();
        List<Item> tempItem = new ArrayList<>();
        categories.clear();
        tempCategory.clear();
        tempItem.clear();
        //get category records
        tempCategory = dbHelper.readCategoryRecordsWithId("SELECT * FROM category_table WHERE list_id = " + list_id);
        int size =tempCategory.size();
        Log.d(TAG, "categories size is: " + tempCategory.size());
        //get category name and id
        for(int i = 0; i < size; i++){

            int cat_id = tempCategory.get(i).getCategory_id();
            String name = tempCategory.get(i).get_name();


            tempItem = dbHelper.readItemRecords("SELECT * FROM item_table WHERE category_id = " + cat_id);

            Category cat2 = new Category(name, tempItem);

            categories.add(cat2);
        }

        //set up recyclerview
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //instantiate your adapter with the list of genres
        adapter = new ExpandableAdapter(categories, this, this);
        adapter.listID(list_id);
        adapter.userID(user_id);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        for (int i = adapter.getGroups().size()-1; i >=0 ; i--) {
            expandGroup(i);
        }



        super.onResume();

    }

    public void expandGroup (int gPos){
        if(adapter.isGroupExpanded(gPos)){
            return;
        }
        adapter.toggleGroup(gPos);
    }


}




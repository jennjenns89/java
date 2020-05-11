package com.example.pictureitgrocerylist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.pictureitgrocerylist.category.Category;
import com.example.pictureitgrocerylist.item.Item;
import com.example.pictureitgrocerylist.item.ItemCreator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class deleteArchivedItem extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ArrayList<String> listItem;
    List<Category> categories;
    List<Item> items;
    int category_id;
    int list_id;
    int item_id;
    String itemName;
    String list_name;
    String itemQuantity;
    String category_name;
    int user_id;
    String user_name;
    ArrayAdapter adapter;
    ListView viewItemList;
    Intent getIntent;
    Button doneButton;
    TextView clickToDelete;
    public final static String TAG = "DeleteArchivedItem";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //set up views
        viewItemList = findViewById(R.id.items_list);
        doneButton = findViewById(R.id.deleteArchivedItem);
        clickToDelete = findViewById(R.id.clickToDelete);
        //set up arraylists
        listItem = new ArrayList<>();
        categories = new ArrayList<>();
        items = new ArrayList<>();
        //set up intent
        getIntent = getIntent();
        //get variables
        category_name = getIntent.getStringExtra("category_name");
        list_id = getIntent.getIntExtra("list_id",0);
        itemName = getIntent.getStringExtra("item_name");
        itemQuantity = getIntent.getStringExtra("quantity");
        user_id = getIntent.getIntExtra("user_id", 0);
        user_name = getIntent.getStringExtra("user_name");
        list_name = getIntent.getStringExtra("list_name");
        //use butterknife
        ButterKnife.bind(this);
        //change text for button
        clickToDelete.setText("Click on an item to delete it!");
        //move button off the screen
        doneButton.setX(-10000);
        //database helper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();
        //view list data
        viewData();


        //when an item in the list is clicked
        viewItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = viewItemList.getItemAtPosition(position).toString();
               //get category items in an arraylist
                List<Item> tempItems = new ArrayList<>();
                categories =dbHelper.readCategoryRecordsWithId("SELECT * FROM category_table " +
                        "WHERE list_id = " + list_id);
                //get size of category
                int cat_size = categories.size();
                //go through category list
                for(int i = 0; i < cat_size; i++){
                    Category OneCategory = categories.get(i);
                    category_id = OneCategory.getCategory_id();
                    //get items from archived_item_table
                    items = dbHelper.readItemRecords("SELECT * FROM archived_item_table WHERE " +
                            "category_id =" + category_id);
                    //go through archived item table
                    for(int j = 0; j < items.size(); j++){
                        Item OneItem = items.get(j);
                        tempItems.add(OneItem);
                        Log.d(TAG, "One Item name:" + OneItem.getItem_name());
                    }

                }


                int size = tempItems.size();
                Log.d(TAG, "temp items size is: "+ size);
                for(int i = 0; i < size; i++){
                    Item oneItem = tempItems.get(i);
                    Log.d(TAG, "item name is: " + oneItem.getItem_name());
                    //if the item matches what we're looking for
                    if(oneItem.getItem_name().toUpperCase().equals(text.toUpperCase())){
                        Log.d(TAG, "Item matched! Sending information...");
                        item_id = oneItem.getItem_id();
                        itemName = oneItem.getItem_name();
                    }
                }

                //set a message that the item is deleted
                View view2 = findViewById(R.id.coordinatorLayout2);
                String message = "Item deleted";
                int duration2= Snackbar.LENGTH_LONG;

                Snackbar.make(view2, message, duration2).show();

                //remove the record
                String idArgs = Integer.toString(item_id);
                String [] whereArgs = {idArgs};
                dbHelper.removeRecord("archived_item_table", "item_id = ?", whereArgs);
                //go back to search activity
                Intent deleteIntent = new Intent(deleteArchivedItem.this, SearchActivity.class);
                deleteIntent.putExtra("category_name", category_name);
                deleteIntent.putExtra("list_id", list_id);
                deleteIntent.putExtra("item_name", itemName);
                deleteIntent.putExtra("quantity", itemQuantity);
                deleteIntent.putExtra("user_name", user_name);
                deleteIntent.putExtra("user_id", user_id);
                deleteIntent.putExtra("archived_item_id", item_id);
                deleteIntent.putExtra("item_deleted", true);
                startActivity(deleteIntent);
            }

        });


    }

    protected void onResume() {
        super.onResume();
    }

    //View the data in list form
    private void viewData(){

                //create an items list
                List<Item>tempItems = new ArrayList<>();
                //get all categories from current list
                categories =dbHelper.readCategoryRecordsWithId("SELECT * FROM category_table " +
                        "WHERE list_id = " + list_id);
                int cat_size = categories.size();
                //go through category list and get items from the current list
                for(int i = 0; i < cat_size; i++) {
                    Category OneCategory = categories.get(i);
                    category_id = OneCategory.getCategory_id();
                    //get items from current category
                    items = dbHelper.readItemRecords("SELECT * FROM archived_item_table WHERE " +
                            "category_id =" + category_id);
                    //go through and get items
                    for (int j = 0; j < items.size(); j++) {
                        Item OneItem = items.get(j);
                        //put item in temporary item list
                        tempItems.add(OneItem);

                    }
                }
                    //get size of items list
                    int size = tempItems.size();
                    //go through list finding those that match and putting them in the listItem list to display
                    for(int j = 0; j < size; j++){
                        Item oneItem = tempItems.get(j);
                    Log.d(TAG, "item name is: " + oneItem.getItem_name());
                    //put correct items in list to display in the list view
                    listItem.add(oneItem.getItem_name());

                    }




        //put items into an array adapter to display
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItem);
        //set adapter
        viewItemList.setAdapter(adapter);
    }

    @OnClick(R.id.deleteArchivedItem)
    public void onClickDoneHandler(){
        Intent backIntent = new Intent(deleteArchivedItem.this, SearchActivity.class);
        backIntent.putExtra("category_name", category_name);
        backIntent.putExtra("list_id", list_id);
        backIntent.putExtra("item_name", itemName);
        backIntent.putExtra("quantity", itemQuantity);
        backIntent.putExtra("user_name", user_name);
        backIntent.putExtra("user_id", user_id);
        backIntent.putExtra("archived_item_id", item_id);
        startActivity(backIntent);
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

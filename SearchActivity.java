package com.example.pictureitgrocerylist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.pictureitgrocerylist.category.Category;
import com.example.pictureitgrocerylist.item.Item;
import com.example.pictureitgrocerylist.item.ItemCreator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ArrayList<String> listItem;
    List<Category> categories;
    List<Item> items;
    boolean isDeleted;
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
    public final static String TAG = "SearchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //get views
        viewItemList = findViewById(R.id.items_list);
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
        isDeleted = getIntent.getBooleanExtra("item_deleted", false);


        //use butterknife
        ButterKnife.bind(this);
        //set up database helper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();
        //view data
        viewData();

        //if item was deleted in deleteArchivedItem let user know
        if(isDeleted){
            View view2 = findViewById(R.id.coordinatorLayout2);
            String message = "Item " + itemName + " is deleted";
            int duration2= Snackbar.LENGTH_LONG;

            Snackbar.make(view2, message, duration2).show();
        }
        //when item is clicked get its information and send it back to item creator
        viewItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = viewItemList.getItemAtPosition(position).toString();
                Log.d(TAG, "Item clicked is: "+ text);
                List<Item>tempItems = new ArrayList<>();
                categories =dbHelper.readCategoryRecordsWithId("SELECT * FROM category_table " +
                        "WHERE list_id = " + list_id);
                Log.d(TAG, "category name is: "+ category_name + " and list id is: "+ list_id);
                int cat_size = categories.size();
                Log.d(TAG, "category size is:" + cat_size);
                for(int i = 0; i < cat_size; i++){
                    Category OneCategory = categories.get(i);
                    category_id = OneCategory.getCategory_id();
                    items = dbHelper.readItemRecords("SELECT * FROM archived_item_table WHERE " +
                            "category_id =" + category_id);
                    Log.d(TAG, "category id is: "+ category_id);
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
                    Log.d(TAG, "item name is: " + oneItem.getItem_name());//oneItem.getItem_name();
                    if(oneItem.getItem_name().toUpperCase().equals(text.toUpperCase())){
                        Log.d(TAG, "Item matched! Sending information...");
                        item_id = oneItem.getItem_id();
                    }
                }
                //go back to item creator
                Intent sendIntent = new Intent(SearchActivity.this, ItemCreator.class);
                sendIntent.putExtra("item_name", itemName);
                sendIntent.putExtra("quantity", itemQuantity);
                sendIntent.putExtra("list_id", list_id);
                sendIntent.putExtra("list_name", list_name);
                sendIntent.putExtra("category_id", category_id);
                sendIntent.putExtra("category_name", category_name);
                sendIntent.putExtra("user_name", user_name);
                sendIntent.putExtra("user_id", user_id);
                sendIntent.putExtra("archived_item_id", item_id);
                startActivity(sendIntent);


            }

        });


    }

    //view archived items from current list
    private void viewData(){


                //create an items list
                List<Item>tempItems = new ArrayList<>();
                //get all categories from current list
                categories =dbHelper.readCategoryRecordsWithId("SELECT * FROM category_table " +
                        "WHERE list_id = " + list_id);
                Log.d(TAG, "List Id is: " + list_id);
                int cat_size = categories.size();
                Log.d(TAG, "category size is: " + cat_size);
                //go through category list and get items from the current list
                for(int i = 0; i < cat_size; i++) {
                    Category OneCategory = categories.get(i);
                    category_id = OneCategory.getCategory_id();
                    //get items from current category
                    items = dbHelper.readItemRecords("SELECT * FROM archived_item_table WHERE " +
                            "category_id =" + category_id);
                    Log.d(TAG, "category id from list is: " + category_id);
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
    //if delete archived item button is clicked
    @OnClick(R.id.deleteArchivedItem)
    public void onDeleteClickHandler(){
        Intent deleteIntent = new Intent(this, deleteArchivedItem.class);
        deleteIntent.putExtra("category_name", category_name);
        deleteIntent.putExtra("list_id", list_id);
        deleteIntent.putExtra("item_name", itemName);
        deleteIntent.putExtra("quantity", itemQuantity);
        deleteIntent.putExtra("user_name", user_name);
        deleteIntent.putExtra("user_id", user_id);
        deleteIntent.putExtra("archived_item_id", item_id);
        startActivity(deleteIntent);
    }
    //set up search icon on the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> itemsList = new ArrayList<>();
                for(String item: listItem){
                    //FIXME added to trim here to see if that helps with the filtering process
                    if(item.toUpperCase().trim().contains(newText.toUpperCase().trim())){
                        itemsList.add(item);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this,
                        android.R.layout.simple_list_item_1, itemsList);
                viewItemList.setAdapter(adapter);


                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //override the back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent backIntent = new Intent(this, ItemCreator.class);
                //backIntent.putExtra("item_name", itemName);
                //backIntent.putExtra("quantity", itemQuantity);
                backIntent.putExtra("list_id", list_id);
                backIntent.putExtra("list_name", list_name);
                backIntent.putExtra("category_id", category_id);
                backIntent.putExtra("category_name", category_name);
                backIntent.putExtra("user_name", user_name);
                backIntent.putExtra("user_id", user_id);
                startActivity(backIntent);
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }


}

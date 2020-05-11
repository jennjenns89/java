package com.example.pictureitgrocerylist;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.pictureitgrocerylist.item.Item;
import com.example.pictureitgrocerylist.item.ItemCreator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends ListActivity {
List<Item> items = new ArrayList<>();
DatabaseHelper dbHelper;
int list_id;
public final static String TAG = "Searchable Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        handleIntent(getIntent());


    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
        }

    public void onListItemClick(ListView l,
                            View v, int position, long id) {
        // call detail activity for clicked entry
        }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        String query =
        intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "query is: "+ query);
        doSearch(query);
            }
        else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        Uri detailUri = intent.getData();
        String id = detailUri.getLastPathSegment();
        Intent detailsIntent = new Intent(getApplicationContext(), ItemCreator.class);
        detailsIntent.putExtra("item_id", id);
        startActivity(detailsIntent);
        finish();
        }
}

    private void doSearch(String queryStr) {
        // get a Cursor, prepare the ListAdapter
        // and set it
        View v = new View(this);
        Log.d(TAG, "query is: "+ queryStr);
        dbHelper = new DatabaseHelper(this);
        items = dbHelper.readItemRecords("SELECT * FROM archived_item_table WHERE item_name = '"
                + queryStr + "'");

        //onListItemClick(items, v, 1,1);
        finish();
    }

    public void getListId (int list_id){
       this.list_id = list_id;
    }
}

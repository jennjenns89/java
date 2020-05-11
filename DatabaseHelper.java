package com.example.pictureitgrocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.pictureitgrocerylist.category.Category;
import com.example.pictureitgrocerylist.item.Item;
import com.example.pictureitgrocerylist.list.Lists;

import java.util.ArrayList;

public class DatabaseHelper  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pictureIt.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");

    }

    public void deleteTable(String tableName){
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    //...................Create table methods.............................................
    public void createUserTable(String tableName){
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(user_id INTEGER " +
                "PRIMARY KEY AUTOINCREMENT, user_name TEXT, password TEXT, password_hint TEXT)");
    }

    public void createListTable(String tableName) {
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(list_id INTEGER " +
                "PRIMARY KEY AUTOINCREMENT, list_name TEXT, user_id INTEGER, update_time TEXT," +
                " CONSTRAINT fk_terms FOREIGN KEY (user_id) REFERENCES user_table(user_id)ON DELETE CASCADE)");

        }
        public void createCategoryTable(String tableName){
            this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(category_id INTEGER " +
                    "PRIMARY KEY AUTOINCREMENT, category_name TEXT, list_id INTEGER," +
                    " CONSTRAINT fk_terms FOREIGN KEY (list_id) REFERENCES list_table(list_id) ON DELETE CASCADE)");
        }
        public void createItemTable(String tableName){
            this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(item_id INTEGER " +
                    "PRIMARY KEY AUTOINCREMENT, item_name TEXT, picture_URI TEXT, item_quantity INTEGER, " +
                    " category_id INTEGER, CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category_table(category_id) ON DELETE CASCADE)");
        }



        //...................User table methods.................................................
    public boolean addUserRecord(String accountKey, String accountValue, String passwordKey, String passwordValue, String passHintKey, String passHintValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(accountKey, accountValue);
        values.put(passwordKey, passwordValue);
        values.put(passHintKey , passHintValue);

        long result = db.insert("user_table", null, values);
        if(result == -1){
            return false;
        }
        return true;
    }

    public boolean addUserRecordWithID(String idKey, int idValue, String accountKey, String accountValue, String passwordKey, String passwordValue, String passHintKey, String passHintValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idKey,idValue);
        values.put(accountKey, accountValue);
        values.put(passwordKey, passwordValue);
        values.put(passHintKey , passHintValue);

        long result = db.insert("user_table", null, values);
        if(result == -1){
            return false;
        }
        return true;
    }

    public int changeUserRecord(String tableName, String userName, String userPass, String passHint, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("user_name", userName);
        cv.put("password", userPass);
        cv.put("password_hint", passHint);


        return db.update(tableName, cv, whereClause, whereArgs);


    }

    public ArrayList<User> readUserRecords(String sqlStmnt){
        ArrayList<User> allUsers = new ArrayList<>();
        int user_id;
        String user_name;
        String password;
        String password_hint;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);

        try{
        while(cursor.moveToNext()){
            user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
            user_name = cursor.getString(cursor.getColumnIndex("user_name"));
            password = cursor.getString(cursor.getColumnIndex("password"));
            password_hint = cursor.getString(cursor.getColumnIndex("password_hint"));


            allUsers.add(new User(user_id, user_name, password, password_hint));

        }

    }
    catch (Exception e){
        e.printStackTrace();
    }
    finally {
        if(cursor != null){
            cursor.close();
        }
    }
        db.close();
        return allUsers;
    }

    //.........................List Methods.............................................
    public ArrayList<Lists> readListRecords(String sqlStmnt){
        ArrayList<Lists> allLists = new ArrayList<>();
        int list_id;
        String list_name;
        int user_id;
        String update_time;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);
        try {
            while (cursor.moveToNext()) {
                list_id = cursor.getInt(cursor.getColumnIndex("list_id"));
                list_name = cursor.getString(cursor.getColumnIndex("list_name"));
                user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
                update_time = cursor.getString(cursor.getColumnIndex("update_time"));


                allLists.add(new Lists(list_id, list_name, user_id, update_time));

            }

        }
        catch (Exception e){
         e.printStackTrace();
        }
        finally {
         if(cursor != null){
                cursor.close();
         }
     }
        db.close();
        return allLists;
    }

    public boolean addListRecord(String nameKey, String nameValue, String userIdKey, int userIdValue, String updateTimeKey, String updateTimeValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(nameKey, nameValue);
        values.put(userIdKey, userIdValue);
        values.put(updateTimeKey, updateTimeValue);


        long result = db.insert("list_table", null, values);
        if(result == -1){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean addListRecordWithID(String idKey, int id, String nameKey, String nameValue, String userIdKey, int userId, String updateTimeKey, String updateTimeValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idKey, id);
        values.put(nameKey, nameValue);
        values.put(userIdKey, userId);
        values.put(updateTimeKey,updateTimeValue);


        long result = db.insert("list_table", null, values);
        if(result == -1){
            db.close();
            return false;
        }
        db.close();
        return true;
    }


    public int changeListRecord(String tblName, String listName, String update_time, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("list_name", listName);
        cv.put("update_time", update_time);


        return db.update(tblName, cv, whereClause, whereArgs);


    }


    //.........................Category Methods..........................................
    public boolean addCategoryRecord(String nameKey, String nameValue, String listIdKey, int listIdValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(nameKey, nameValue);
        values.put(listIdKey, listIdValue);


        long result = db.insert("category_table", null, values);
        if(result == -1){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean addCategoryRecordWithID(String idKey, int id, String nameKey, String nameValue, String listIdKey, int listId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idKey, id);
        values.put(nameKey, nameValue);
        values.put(listIdKey, listId);


        long result = db.insert("category_table", null, values);
        if(result == -1){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public ArrayList<Category> readCategoryRecords(String sqlStmnt){
        ArrayList<Category> allCategories = new ArrayList<>();
        int category_id;
        String category_name;
        int list_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);
        try {
            while (cursor.moveToNext()) {
                category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
                category_name = cursor.getString(cursor.getColumnIndex("category_name"));
                list_id = cursor.getInt(cursor.getColumnIndex("list_id"));


                allCategories.add(new Category(category_name, null));

            }


        }
        catch (Exception e){
            e.printStackTrace();
            }
            finally {
            if(cursor != null){
            cursor.close();
                }
            }
        db.close();
        return allCategories;
    }

    public ArrayList<Category> readCategoryRecordsWithId(String sqlStmnt){
        ArrayList<Category> allCategories = new ArrayList<>();
        int category_id;
        String category_name;
        int list_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);
        try {
            while (cursor.moveToNext()) {
                category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
                category_name = cursor.getString(cursor.getColumnIndex("category_name"));
                list_id = cursor.getInt(cursor.getColumnIndex("list_id"));


                allCategories.add(new Category(category_name, null, category_id, category_name, list_id));

            }
        }
        catch (Exception e){

        }
        finally {
            if(cursor != null){
                cursor.close();
            }
        }
        db.close();
        return allCategories;
    }

    public int changeCategoryRecord(String tblName, String categoryName, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("category_name", categoryName);


        return db.update(tblName, cv, whereClause, whereArgs);


    }
    //..........................Item methods.......................................................
    public boolean addItemRecord(String nameKey, String nameValue, String picURIKey, String picURI,
                                 String quantityKey, int quantityValue, String categoryIdKey, int categoryIdValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(nameKey, nameValue);
        values.put(picURIKey, picURI);
        values.put(quantityKey, quantityValue);
        values.put(categoryIdKey, categoryIdValue);


        long result = db.insert("item_table", null, values);
        if(result == -1){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean addItemRecordWithID(String idKey, int item_id, String nameKey, String nameValue, String picURIKey, String picURI,
                                 String quantityKey, int quantityValue, String categoryIdKey, int categoryIdValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(idKey,item_id);
        values.put(nameKey, nameValue);
        values.put(picURIKey, picURI);
        values.put(quantityKey, quantityValue);
        values.put(categoryIdKey, categoryIdValue);


        long result = db.insert("item_table", null, values);
        if(result == -1){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public boolean addArchivedItemRecord(String nameKey, String nameValue, String picURIKey, String picURI,
                                 String quantityKey, int quantityValue, String categoryIdKey, int categoryIdValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(nameKey, nameValue);
        values.put(picURIKey, picURI);
        values.put(quantityKey, quantityValue);
        values.put(categoryIdKey, categoryIdValue);


        long result = db.insert("archived_item_table", null, values);
        if(result == -1){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public ArrayList<Item> readItemRecords(String sqlStmnt){
        ArrayList<Item> allItems = new ArrayList<>();
        int item_id;
        String item_name;
        String picture_URI;
        int quantity;
        int category_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStmnt, null);
    try{
        while(cursor.moveToNext()){
            item_id = cursor.getInt(cursor.getColumnIndex("item_id"));
            item_name = cursor.getString(cursor.getColumnIndex("item_name"));
            picture_URI = cursor.getString(cursor.getColumnIndex("picture_URI"));
            quantity = cursor.getInt(cursor.getColumnIndex("item_quantity"));
            category_id = cursor.getInt(cursor.getColumnIndex("category_id"));



            allItems.add(new Item(item_id, item_name,picture_URI,quantity,category_id));

        }
    }
    catch (Exception e){
        e.printStackTrace();
    }
    finally {
        if(cursor != null){
            cursor.close();
        }
    }
        db.close();
        return allItems;
    }

    public int changeItemRecord(String tblName, String itemName, String pictureUri, int quantity, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("item_name", itemName);
        cv.put("picture_URI", pictureUri);
        cv.put("item_quantity", quantity);



        return db.update(tblName, cv, whereClause, whereArgs);


    }

    public Cursor getItemsNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT item_name FROM archived_item_table";
        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    //.........................Other Methods.............................................

    public String checkForRows(String sqlStatement){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);
        try{
        if(cursor != null && cursor.moveToFirst()){
            cursor.moveToFirst();
            if(cursor.getInt(0) == 0){
                db.close();
                return "false";
            }
            else{
                db.close();
                return "true";
            }
        }
        }
        catch (Exception e){
        e.printStackTrace();
        }
        finally {
            if(cursor != null){
             cursor.close();
            }
    }
        db.close();

        return "false";

    }

    public int removeRecord(String tableName, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName, whereClause, whereArgs);

    }


}

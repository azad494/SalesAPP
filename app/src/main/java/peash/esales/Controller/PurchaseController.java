package peash.esales.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import peash.esales.Database.DatabaseManager;
import peash.esales.Models.Purchase;

public class PurchaseController {

    private Purchase purchase;
    public PurchaseController(){ purchase=new Purchase(); }


    // create table

    public static String CreateTable() {
        return "CREATE TABLE " + Purchase.TABLE + "("
                + Purchase.KEY_SLID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Purchase.KEY_Name + " TEXT,"
                + Purchase.KEY_Code + " TEXT,"
                + Purchase.KEY_CostPrice + " INTEGER,"
                + Purchase.KEY_Quantity + " INTEGER,"
                + Purchase.KEY_Total + " INTEGER,"
                + Purchase.KEY_ProductID + " INTEGER )";
    }

    //save
    public int Insert(Purchase purchase)
    {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(Purchase.KEY_SLID, purchase.getSLID());
        values.put(Purchase.KEY_Name, purchase.getName());
        values.put(Purchase.KEY_Code, purchase.getCode());
        values.put(Purchase.KEY_CostPrice, purchase.getCostPrice());
        values.put(Purchase.KEY_Quantity, purchase.getQuantity());
        values.put(Purchase.KEY_Total, purchase.getTotal());
        values.put(Purchase.KEY_ProductID, purchase.getTotal());

        id = (int) db.insert(Purchase.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    //Delete
    public void Delete(String id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM Purchase WHERE SLID='" + id + "'";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    //Delete
    public void DeleteAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM Purchase";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM Purchase";
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("SLID", cursor.getString(0));
                map.put( "Name", cursor.getString(1));
                map.put( "Code", cursor.getString(2));
                map.put( "CostPrice", cursor.getString(3));
                map.put( "Quantity", cursor.getString(4));
                map.put( "Total", cursor.getString(5));
                map.put( "ProductID", cursor.getString(6));
                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public Cursor getAllItem() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM Purchase";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }
}

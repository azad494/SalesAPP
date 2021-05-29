package peash.esales.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import peash.esales.Database.DatabaseManager;
import peash.esales.Models.Sales;

public class SalesController {

    private Sales sales;
    public SalesController(){ sales=new Sales(); }


    // create table

    public static String CreateTable() {
        return "CREATE TABLE " + Sales.TABLE + "("
                + Sales.KEY_SLID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Sales.KEY_ProductName + " TEXT,"
                + Sales.KEY_Price + " TEXT,"
                + Sales.KEY_ProductID + " INTEGER,"
                + Sales.KEY_WarehouseID + " INTEGER,"
                + Sales.KEY_OpeningQty + " INTEGER,"
                + Sales.KEY_ReturnQty + " INTEGER,"
                + Sales.KEY_Quantity + " INTEGER,"
                + Sales.KEY_Discount + " TEXT,"
                + Sales.KEY_Total + " TEXT )";
    }

    //save
    public int Insert(Sales sales)
    {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(Sales.KEY_SLID, sales.getSLID());
        values.put(Sales.KEY_ProductName, sales.getProductName());
        values.put(Sales.KEY_ProductID, sales.getProductID());
        values.put(Sales.KEY_WarehouseID, sales.getWarehouseID());
        values.put(Sales.KEY_OpeningQty, sales.getOpeningQty());
        values.put(Sales.KEY_ReturnQty, sales.getReturnQty());
        values.put(Sales.KEY_Price, sales.getPrice());
        values.put(Sales.KEY_Quantity, sales.getQuantity());
        values.put(Sales.KEY_Discount, sales.getDiscount());
        values.put(Sales.KEY_Total, sales.getTotal());

        id = (int) db.insert(Sales.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    public int Update(Sales sales) {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(Sales.KEY_SLID, sales.getSLID());
        values.put(Sales.KEY_ProductName, sales.getProductName());
        values.put(Sales.KEY_ProductID, sales.getProductID());
        values.put(Sales.KEY_WarehouseID, sales.getWarehouseID());
        values.put(Sales.KEY_OpeningQty, sales.getOpeningQty());
        values.put(Sales.KEY_ReturnQty, sales.getReturnQty());
        values.put(Sales.KEY_Price, sales.getPrice());
        values.put(Sales.KEY_Quantity, sales.getQuantity());
        values.put(Sales.KEY_Discount, sales.getDiscount());
        values.put(Sales.KEY_Total, sales.getTotal());
        id = db.update(Sales.TABLE, values, "SLID" + " = ?", new String[]{sales.getSLID()});

        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    //Delete
    public void Delete(int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM Sales WHERE SLID='" + id + "'";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    //Delete
    public void DeleteAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM Sales";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM Sales";
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("SLID", cursor.getString(0));
                map.put( "ProductName", cursor.getString(1));
                map.put( "Price", cursor.getString(2));
                map.put( "Quantity", cursor.getString(3));
                map.put( "Discount", cursor.getString(4));
                map.put( "Total", cursor.getString(5));

                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public Cursor getData() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM Sales";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }

    public ArrayList<HashMap<String, String>> getAllList() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM Sales";

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("SLID", cursor.getString(0));
                map.put( "ProductName", cursor.getString(1));
                map.put( "Price", cursor.getString(2));
                map.put( "Quantity", cursor.getString(3));
                map.put( "Discount", cursor.getString(4));
                map.put( "Total", cursor.getString(5));

                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public List<Sales> GetByID(int id) {

        Sales sales = new Sales();
        List<Sales> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM Sales WHERE SLID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                sales = new Sales();
                sales.setSLID(cursor.getString(cursor.getColumnIndex(Sales.KEY_SLID)));
                sales.setProductName(cursor.getString(cursor.getColumnIndex(Sales.KEY_ProductName)));
                sales.setPrice(cursor.getString(cursor.getColumnIndex(Sales.KEY_Price)));
                sales.setQuantity(cursor.getString(cursor.getColumnIndex(Sales.KEY_Quantity)));
                sales.setDiscount(cursor.getString(cursor.getColumnIndex(Sales.KEY_Discount)));
                sales.setTotal(cursor.getString(cursor.getColumnIndex(Sales.KEY_Total)));
                info.add(sales);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public Cursor getAllItem() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM Sales";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }
}

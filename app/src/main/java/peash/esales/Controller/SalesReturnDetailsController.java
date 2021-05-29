package peash.esales.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import peash.esales.Database.DatabaseManager;
import peash.esales.Models.SalesReturnDetails;

public class SalesReturnDetailsController {

    private SalesReturnDetails salesReturnDetails;
    public SalesReturnDetailsController(){ salesReturnDetails=new SalesReturnDetails(); }


    // create table

    public static String CreateTable() {
        return "CREATE TABLE " + SalesReturnDetails.TABLE + "("
                + SalesReturnDetails.KEY_SLID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SalesReturnDetails.KEY_ProductName + " TEXT,"
                + SalesReturnDetails.KEY_ProductID + " INTEGER,"
                + SalesReturnDetails.KEY_WarehouseID + " INTEGER,"
                + SalesReturnDetails.KEY_Price + " TEXT,"
                + SalesReturnDetails.KEY_Quantity + " INTEGER,"
                + SalesReturnDetails.KEY_Total + " TEXT )";
    }

    //save
    public int Insert(SalesReturnDetails salesReturnDetails)
    {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(SalesReturnDetails.KEY_SLID, salesReturnDetails.getSLID());
        values.put(SalesReturnDetails.KEY_ProductName, salesReturnDetails.getProductName());
        values.put(SalesReturnDetails.KEY_ProductID, salesReturnDetails.getProductID());
        values.put(SalesReturnDetails.KEY_WarehouseID, salesReturnDetails.getWarehouseID());
        values.put(SalesReturnDetails.KEY_Price, salesReturnDetails.getPrice());
        values.put(SalesReturnDetails.KEY_Quantity, salesReturnDetails.getQuantity());
        values.put(SalesReturnDetails.KEY_Total, salesReturnDetails.getTotal());

        id = (int) db.insert(salesReturnDetails.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    public int Update(SalesReturnDetails salesReturnDetails) {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(SalesReturnDetails.KEY_SLID, salesReturnDetails.getSLID());
        //values.put(SalesDetails.KEY_ProductName, salesDetails.getProductName());
        //values.put(SalesDetails.KEY_ProductID, salesDetails.getProductID());
        //values.put(SalesDetails.KEY_WarehouseID, salesDetails.getWarehouseID());
        //values.put(SalesDetails.KEY_OpeningQty, salesDetails.getOpeningQty());
        //values.put(SalesDetails.KEY_ReturnQty, salesDetails.getReturnQty());
        values.put(SalesReturnDetails.KEY_Price, salesReturnDetails.getPrice());
        values.put(SalesReturnDetails.KEY_Quantity, salesReturnDetails.getQuantity());
        values.put(SalesReturnDetails.KEY_Total, salesReturnDetails.getTotal());
        id = db.update(SalesReturnDetails.TABLE, values, "SLID" + " = ?", new String[]{salesReturnDetails.getSLID()});

        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    //Delete
    public void Delete(int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM SalesReturnDetails WHERE SLID='" + id + "'";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    //Delete
    public void DeleteAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM SalesReturnDetails";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM SalesReturnDetails";
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("SLID", cursor.getString(0));
                map.put( "ProductName", cursor.getString(1));
                map.put( "ProductID", cursor.getString(2));
                map.put( "ReturnQty", cursor.getString(3));
                map.put( "Price", cursor.getString(4));
                map.put( "Quantity", cursor.getString(5));
                map.put( "Total", cursor.getString(6));

                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public Cursor getData() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM SalesReturnDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }

    public ArrayList<HashMap<String, String>> getAllList() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM SalesReturnDetails";

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("SLID", cursor.getString(0));
                map.put( "ProductName", cursor.getString(1));
                map.put( "ProductID", cursor.getString(2));
                map.put( "WarehouseID", cursor.getString(3));
                map.put( "Price", cursor.getString(4));
                map.put( "Quantity", cursor.getString(5));
                map.put( "Total", cursor.getString(6));

                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public List<SalesReturnDetails> GetByID(int id) {

        SalesReturnDetails salesReturnDetails = new SalesReturnDetails();
        List<SalesReturnDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM SalesReturnDetails WHERE SLID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                salesReturnDetails = new SalesReturnDetails();
                salesReturnDetails.setSLID(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_SLID)));
                salesReturnDetails.setProductName(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_ProductName)));
                salesReturnDetails.setProductID(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_ProductID)));
                salesReturnDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_WarehouseID)));
                salesReturnDetails.setPrice(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_Price)));
                salesReturnDetails.setQuantity(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_Quantity)));
                salesReturnDetails.setTotal(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_Total)));
                info.add(salesReturnDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public List<SalesReturnDetails> GetByProID(int id) {

        SalesReturnDetails salesReturnDetails = new SalesReturnDetails();
        List<SalesReturnDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM SalesReturnDetails WHERE ProductID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                salesReturnDetails = new SalesReturnDetails();
                salesReturnDetails.setSLID(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_SLID)));
                salesReturnDetails.setProductName(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_ProductName)));
                salesReturnDetails.setProductID(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_ProductID)));
                salesReturnDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_WarehouseID)));
                salesReturnDetails.setPrice(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_Price)));
                salesReturnDetails.setQuantity(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_Quantity)));
                salesReturnDetails.setTotal(cursor.getString(cursor.getColumnIndex(SalesReturnDetails.KEY_Total)));
                info.add(salesReturnDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public Cursor getAllItem() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM SalesReturnDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }
}

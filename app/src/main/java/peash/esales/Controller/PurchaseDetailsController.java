package peash.esales.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import peash.esales.Database.DatabaseManager;
import peash.esales.Models.PurchaseDetails;

public class PurchaseDetailsController {

    private PurchaseDetails purchaseDetails;
    public PurchaseDetailsController(){ purchaseDetails=new PurchaseDetails(); }


    // create table

    public static String CreateTable() {
        return "CREATE TABLE " + PurchaseDetails.TABLE + "("
                + PurchaseDetails.KEY_SLID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PurchaseDetails.KEY_ProductName + " TEXT,"
                + PurchaseDetails.KEY_ProductID + " INTEGER,"
                + PurchaseDetails.KEY_WarehouseID + " INTEGER,"
                + PurchaseDetails.KEY_Price + " INTEGER,"
                + PurchaseDetails.KEY_Quantity + " INTEGER,"
                + PurchaseDetails.KEY_Total + " INTEGER )";
    }

    //save
    public int Insert(PurchaseDetails purchaseDetails)
    {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(PurchaseDetails.KEY_SLID, purchaseDetails.getSLID());
        values.put(PurchaseDetails.KEY_ProductName, purchaseDetails.getProductName());
        values.put(PurchaseDetails.KEY_ProductID, purchaseDetails.getProductID());
        values.put(PurchaseDetails.KEY_WarehouseID, purchaseDetails.getWarehouseID());
        values.put(PurchaseDetails.KEY_Price, purchaseDetails.getPrice());
        values.put(PurchaseDetails.KEY_Quantity, purchaseDetails.getQuantity());
        values.put(PurchaseDetails.KEY_Total, purchaseDetails.getTotal());

        id = (int) db.insert(PurchaseDetails.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    public int Update(PurchaseDetails purchaseDetails) {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(PurchaseDetails.KEY_SLID, purchaseDetails.getSLID());
        values.put(PurchaseDetails.KEY_ProductName, purchaseDetails.getProductName());
        values.put(PurchaseDetails.KEY_ProductID, purchaseDetails.getProductID());
        values.put(PurchaseDetails.KEY_WarehouseID, purchaseDetails.getWarehouseID());
        //values.put(SalesDetails.KEY_OpeningQty, salesDetails.getOpeningQty());
        //values.put(SalesDetails.KEY_ReturnQty, salesDetails.getReturnQty());
        values.put(PurchaseDetails.KEY_Price, purchaseDetails.getPrice());
        values.put(PurchaseDetails.KEY_Quantity, purchaseDetails.getQuantity());
        values.put(PurchaseDetails.KEY_Total, purchaseDetails.getTotal());
        id = db.update(PurchaseDetails.TABLE, values, "SLID" + " = ?", new String[]{purchaseDetails.getSLID()});

        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    //Delete
    public void Delete(int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM PurchaseDetails WHERE SLID='" + id + "'";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    //Delete
    public void DeleteAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM PurchaseDetails";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM PurchaseDetails";
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("SLID", cursor.getString(0));
                map.put( "ProductName", cursor.getString(1));
                map.put( "ProductID", cursor.getString(1));
                map.put( "WarehouseID", cursor.getString(1));
                map.put( "OpeningQty", cursor.getString(1));
                map.put( "ReturnQty", cursor.getString(1));
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
        String sql = "SELECT * FROM PurchaseDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }

    public ArrayList<HashMap<String, String>> getAllList() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM PurchaseDetails";

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

    public List<PurchaseDetails> GetByID(int id) {

        PurchaseDetails purchaseDetails = new PurchaseDetails();
        List<PurchaseDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM PurchaseDetails WHERE SLID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                purchaseDetails = new PurchaseDetails();
                purchaseDetails.setSLID(cursor.getString(cursor.getColumnIndex(PurchaseDetails.KEY_SLID)));
                purchaseDetails.setProductName(cursor.getString(cursor.getColumnIndex(PurchaseDetails.KEY_ProductName)));
                purchaseDetails.setProductID(cursor.getString(cursor.getColumnIndex(PurchaseDetails.KEY_ProductID)));
                purchaseDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(PurchaseDetails.KEY_WarehouseID)));
                purchaseDetails.setPrice(cursor.getString(cursor.getColumnIndex(PurchaseDetails.KEY_Price)));
                purchaseDetails.setQuantity(cursor.getString(cursor.getColumnIndex(PurchaseDetails.KEY_Quantity)));
                purchaseDetails.setTotal(cursor.getString(cursor.getColumnIndex(PurchaseDetails.KEY_Total)));
                info.add(purchaseDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public Cursor getAllItem() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM PurchaseDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }
}

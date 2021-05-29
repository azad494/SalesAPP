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
import peash.esales.Models.SalesDetails;

public class SalesDetailsController {

    private SalesDetails salesDetails;
    public SalesDetailsController(){ salesDetails=new SalesDetails(); }


    // create table

    public static String CreateTable() {
        return "CREATE TABLE " + SalesDetails.TABLE + "("
                + SalesDetails.KEY_SLID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SalesDetails.KEY_ProductName + " TEXT,"
                + SalesDetails.KEY_ProductID + " INTEGER,"
                + SalesDetails.KEY_WarehouseID + " INTEGER,"
                + SalesDetails.KEY_OpeningQty + " INTEGER,"
                + SalesDetails.KEY_ReturnQty + " INTEGER,"
                + SalesDetails.KEY_Price + " TEXT,"
                + SalesDetails.KEY_Quantity + " INTEGER,"
                + SalesDetails.KEY_Discount + " TEXT,"
                + SalesDetails.KEY_Total + " TEXT )";
    }

    //save
    public int Insert(SalesDetails salesDetails)
    {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(SalesDetails.KEY_SLID, salesDetails.getSLID());
        values.put(SalesDetails.KEY_ProductName, salesDetails.getProductName());
        values.put(SalesDetails.KEY_ProductID, salesDetails.getProductID());
        values.put(SalesDetails.KEY_WarehouseID, salesDetails.getWarehouseID());
        values.put(SalesDetails.KEY_OpeningQty, salesDetails.getOpeningQty());
        values.put(SalesDetails.KEY_ReturnQty, salesDetails.getReturnQty());
        values.put(SalesDetails.KEY_Price, salesDetails.getPrice());
        values.put(SalesDetails.KEY_Quantity, salesDetails.getQuantity());
        values.put(SalesDetails.KEY_Discount, salesDetails.getDiscount());
        values.put(SalesDetails.KEY_Total, salesDetails.getTotal());

        id = (int) db.insert(SalesDetails.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    public int Update(SalesDetails salesDetails) {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(SalesDetails.KEY_SLID, salesDetails.getSLID());
        //values.put(SalesDetails.KEY_ProductName, salesDetails.getProductName());
        //values.put(SalesDetails.KEY_ProductID, salesDetails.getProductID());
        //values.put(SalesDetails.KEY_WarehouseID, salesDetails.getWarehouseID());
        //values.put(SalesDetails.KEY_OpeningQty, salesDetails.getOpeningQty());
        //values.put(SalesDetails.KEY_ReturnQty, salesDetails.getReturnQty());
        values.put(SalesDetails.KEY_Price, salesDetails.getPrice());
        values.put(SalesDetails.KEY_Quantity, salesDetails.getQuantity());
        values.put(SalesDetails.KEY_Discount, salesDetails.getDiscount());
        values.put(SalesDetails.KEY_Total, salesDetails.getTotal());
        id = db.update(SalesDetails.TABLE, values, "SLID" + " = ?", new String[]{salesDetails.getSLID()});

        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    //Delete
    public void Delete(int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM SalesDetails WHERE SLID='" + id + "'";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    //Delete
    public void DeleteAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM SalesDetails";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM SalesDetails";
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
        String sql = "SELECT * FROM SalesDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }

    public ArrayList<HashMap<String, String>> getAllList() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM SalesDetails";

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("SLID", cursor.getString(0));
                map.put( "ProductName", cursor.getString(1));
                map.put( "ProductID", cursor.getString(2));
                map.put( "WarehouseID", cursor.getString(3));
                map.put( "OpeningQty", cursor.getString(4));
                map.put( "ReturnQty", cursor.getString(5));
                map.put( "Price", cursor.getString(6));
                map.put( "Quantity", cursor.getString(7));
                map.put( "Discount", cursor.getString(8));
                map.put( "Total", cursor.getString(9));

                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public List<SalesDetails> GetByID(int id) {

        SalesDetails salesDetails = new SalesDetails();
        List<SalesDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM SalesDetails WHERE SLID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                salesDetails = new SalesDetails();
                salesDetails.setSLID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_SLID)));
                salesDetails.setProductName(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_ProductName)));
                salesDetails.setProductID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_ProductID)));
                salesDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_WarehouseID)));
                salesDetails.setOpeningQty(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_OpeningQty)));
                salesDetails.setReturnQty(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_ReturnQty)));
                salesDetails.setPrice(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_Price)));
                salesDetails.setQuantity(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_Quantity)));
                salesDetails.setDiscount(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_Discount)));
                salesDetails.setTotal(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_Total)));
                info.add(salesDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public List<SalesDetails> GetByProID(int id) {

        SalesDetails salesDetails = new SalesDetails();
        List<SalesDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM SalesDetails WHERE ProductID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                salesDetails = new SalesDetails();
                salesDetails.setSLID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_SLID)));
                salesDetails.setProductName(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_ProductName)));
                salesDetails.setProductID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_ProductID)));
                salesDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_WarehouseID)));
                salesDetails.setOpeningQty(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_OpeningQty)));
                salesDetails.setReturnQty(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_ReturnQty)));
                salesDetails.setPrice(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_Price)));
                salesDetails.setQuantity(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_Quantity)));
                salesDetails.setDiscount(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_Discount)));
                salesDetails.setTotal(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_Total)));
                info.add(salesDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public Cursor getAllItem() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM SalesDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }
}

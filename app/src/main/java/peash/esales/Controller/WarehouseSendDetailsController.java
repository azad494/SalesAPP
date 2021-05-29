package peash.esales.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import peash.esales.Database.DatabaseManager;
import peash.esales.Models.WarehouseSendDetails;

public class WarehouseSendDetailsController {

    private WarehouseSendDetails warehouseSendDetails;
    public WarehouseSendDetailsController(){ warehouseSendDetails=new WarehouseSendDetails(); }


    // create table

    public static String CreateTable() {
        return "CREATE TABLE " + WarehouseSendDetails.TABLE + "("
                + WarehouseSendDetails.KEY_SLID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WarehouseSendDetails.KEY_ProductName + " TEXT,"
                + WarehouseSendDetails.KEY_Date + " TEXT,"
                + WarehouseSendDetails.KEY_ProductID + " INTEGER,"
                + WarehouseSendDetails.KEY_FromWarehouseID + " INTEGER,"
                + WarehouseSendDetails.KEY_ToWarehouseID + " INTEGER,"
                + WarehouseSendDetails.KEY_CostPrice + " INTEGER,"
                + WarehouseSendDetails.KEY_Qty + " INTEGER)";
    }

    //save
    public int Insert(WarehouseSendDetails warehouseSendDetails)
    {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(WarehouseSendDetails.KEY_SLID, warehouseSendDetails.getSLID());
        values.put(WarehouseSendDetails.KEY_Date, warehouseSendDetails.getDate());
        values.put(WarehouseSendDetails.KEY_ProductName, warehouseSendDetails.getProductName());
        values.put(WarehouseSendDetails.KEY_ProductID, warehouseSendDetails.getProductID());
        values.put(WarehouseSendDetails.KEY_FromWarehouseID, warehouseSendDetails.getFromWarehouseID());
        values.put(WarehouseSendDetails.KEY_ToWarehouseID, warehouseSendDetails.getToWarehouseID());
        values.put(WarehouseSendDetails.KEY_CostPrice, warehouseSendDetails.getCostPrice());
        values.put(WarehouseSendDetails.KEY_Qty, warehouseSendDetails.getQuantity());

        id = (int) db.insert(WarehouseSendDetails.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    public int Update(WarehouseSendDetails warehouseSendDetails) {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(WarehouseSendDetails.KEY_SLID, warehouseSendDetails.getSLID());
        values.put(WarehouseSendDetails.KEY_Date, warehouseSendDetails.getDate());
        values.put(WarehouseSendDetails.KEY_ProductName, warehouseSendDetails.getProductName());
        values.put(WarehouseSendDetails.KEY_ProductID, warehouseSendDetails.getProductID());
        values.put(WarehouseSendDetails.KEY_FromWarehouseID, warehouseSendDetails.getFromWarehouseID());
        values.put(WarehouseSendDetails.KEY_ToWarehouseID, warehouseSendDetails.getToWarehouseID());
        values.put(WarehouseSendDetails.KEY_CostPrice, warehouseSendDetails.getCostPrice());
        values.put(WarehouseSendDetails.KEY_Qty, warehouseSendDetails.getQuantity());
        id = db.update(WarehouseSendDetails.TABLE, values, "SLID" + " = ?", new String[]{warehouseSendDetails.getSLID()});

        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    //Delete
    public void Delete(int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM WarehouseSendDetails WHERE SLID='" + id + "'";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    //Delete
    public void DeleteAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM WarehouseSendDetails";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM WarehouseSendDetails";
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
        String sql = "SELECT * FROM WarehouseSendDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }

    public ArrayList<HashMap<String, String>> getAllList() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM WarehouseSendDetails";

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("SLID", cursor.getString(0));
                map.put( "Date", cursor.getString(1));
                map.put( "ProductName", cursor.getString(2));
                map.put( "ProductID", cursor.getString(3));
                map.put( "FromWarehouseID", cursor.getString(4));
                map.put( "ToWarehouseID", cursor.getString(5));
                map.put( "Price", cursor.getString(6));
                map.put( "Quantity", cursor.getString(7));

                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public List<WarehouseSendDetails> GetByID(int id) {

        WarehouseSendDetails warehouseSendDetails = new WarehouseSendDetails();
        List<WarehouseSendDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM WarehouseSendDetails WHERE SLID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                warehouseSendDetails = new WarehouseSendDetails();
                warehouseSendDetails.setSLID(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_SLID)));
                warehouseSendDetails.setDate(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_Date)));
                warehouseSendDetails.setProductName(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_ProductName)));
                warehouseSendDetails.setCostPrice(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_CostPrice)));
                warehouseSendDetails.setQuantity(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_Qty)));
                warehouseSendDetails.setFromWarehouseID(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_FromWarehouseID)));
                warehouseSendDetails.setToWarehouseID(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_ToWarehouseID)));
                info.add(warehouseSendDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public List<WarehouseSendDetails> GetByProID(int id) {

        WarehouseSendDetails warehouseSendDetails = new WarehouseSendDetails();
        List<WarehouseSendDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM WarehouseSendDetails WHERE ProductID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                warehouseSendDetails = new WarehouseSendDetails();
                warehouseSendDetails.setSLID(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_SLID)));
                warehouseSendDetails.setDate(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_Date)));
                warehouseSendDetails.setProductName(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_ProductName)));
                warehouseSendDetails.setProductID(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_ProductID)));
                warehouseSendDetails.setCostPrice(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_CostPrice)));
                warehouseSendDetails.setQuantity(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_Qty)));
                warehouseSendDetails.setFromWarehouseID(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_FromWarehouseID)));
                warehouseSendDetails.setToWarehouseID(cursor.getString(cursor.getColumnIndex(WarehouseSendDetails.KEY_ToWarehouseID)));
                info.add(warehouseSendDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public Cursor getAllItem() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM WarehouseSendDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }
}

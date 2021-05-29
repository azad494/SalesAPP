package peash.esales.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import peash.esales.Database.DatabaseManager;
import peash.esales.Models.OrderDetails;
import peash.esales.Models.SalesDetails;

public class OrderDetailsController {

    private OrderDetails orderDetails;
    public OrderDetailsController(){ orderDetails=new OrderDetails(); }


    // create table

    public static String CreateTable() {
        return "CREATE TABLE " + OrderDetails.TABLE + "("
                + OrderDetails.KEY_SLID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + OrderDetails.KEY_ProductName + " TEXT,"
                + OrderDetails.KEY_ProductID + " INTEGER,"
                + SalesDetails.KEY_WarehouseID + " INTEGER,"
                + OrderDetails.KEY_OpeningQty + " INTEGER,"
                + OrderDetails.KEY_ReturnQty + " INTEGER,"
                + OrderDetails.KEY_Price + " INTEGER,"
                + OrderDetails.KEY_Quantity + " INTEGER,"
                + OrderDetails.KEY_Total + " INTEGER )";
    }

    //save
    public int Insert(OrderDetails orderDetails)
    {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(OrderDetails.KEY_SLID, orderDetails.getSLID());
        values.put(OrderDetails.KEY_ProductName, orderDetails.getProductName());
        values.put(OrderDetails.KEY_ProductID, orderDetails.getProductID());
        values.put(SalesDetails.KEY_WarehouseID, orderDetails.getWarehouseID());
        values.put(OrderDetails.KEY_OpeningQty, orderDetails.getOpeningQty());
        values.put(OrderDetails.KEY_ReturnQty, orderDetails.getReturnQty());
        values.put(OrderDetails.KEY_Price, orderDetails.getPrice());
        values.put(OrderDetails.KEY_Quantity, orderDetails.getQuantity());
        values.put(OrderDetails.KEY_Total, orderDetails.getTotal());

        id = (int) db.insert(OrderDetails.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    public int Update(OrderDetails orderDetails) {
        int id;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(OrderDetails.KEY_SLID, orderDetails.getSLID());
        //values.put(SalesDetails.KEY_ProductName, salesDetails.getProductName());
        //values.put(SalesDetails.KEY_ProductID, salesDetails.getProductID());
        //values.put(SalesDetails.KEY_WarehouseID, salesDetails.getWarehouseID());
        //values.put(SalesDetails.KEY_OpeningQty, salesDetails.getOpeningQty());
        //values.put(SalesDetails.KEY_ReturnQty, salesDetails.getReturnQty());
        values.put(OrderDetails.KEY_Price, orderDetails.getPrice());
        values.put(OrderDetails.KEY_Quantity, orderDetails.getQuantity());
        values.put(OrderDetails.KEY_Total, orderDetails.getTotal());
        id = db.update(OrderDetails.TABLE, values, "SLID" + " = ?", new String[]{orderDetails.getSLID()});

        DatabaseManager.getInstance().closeDatabase();
        return id;
    }

    public void UpdateModel(double price, int Model,  int Meter) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        //UPDATE table_name SET [column_name1= value1,... column_nameN = valueN] [WHERE condition]
        String query = "UPDATE OrderDetails SET Price= '" + price + "' WHERE OpeningQty='" + Model + "' and ReturnQty = '" + Meter + "'";
        String query1 = "UPDATE OrderDetails SET Total='" + price + "' * Quantity WHERE OpeningQty='" + Model + "' and ReturnQty = '" + Meter + "'";
        Log.d("UpdatePrice:", query);
        db.execSQL(query);
        db.execSQL(query1);
    }

    //Delete
    public void Delete(int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM OrderDetails WHERE SLID='" + id + "'";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    //Delete
    public void DeleteAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM OrderDetails";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    public ArrayList<HashMap<String, String>> getAll() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM OrderDetails";
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
                map.put( "Total", cursor.getString(4));

                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public Cursor getData() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM OrderDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }

    public ArrayList<HashMap<String, String>> getAllList() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
        String query = "SELECT * FROM OrderDetails";

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
                map.put( "Total", cursor.getString(8));

                Log.d("getAll:", map.toString());
                dataList.add(map);
            }
            while (cursor.moveToNext());
        }
        return dataList;
    }

    public List<OrderDetails> GetByID(int id) {

        OrderDetails orderDetails = new OrderDetails();
        List<OrderDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM OrderDetails WHERE SLID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                orderDetails = new OrderDetails();
                orderDetails.setSLID(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_SLID)));
                orderDetails.setProductName(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ProductName)));
                orderDetails.setProductID(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ProductID)));
                orderDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_WarehouseID)));
                orderDetails.setOpeningQty(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_OpeningQty)));
                orderDetails.setReturnQty(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ReturnQty)));
                orderDetails.setPrice(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Price)));
                orderDetails.setQuantity(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Quantity)));
                orderDetails.setTotal(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Total)));
                info.add(orderDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public List<OrderDetails> GetByProID(int id) {

        OrderDetails orderDetails = new OrderDetails();
        List<OrderDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM OrderDetails WHERE ProductID = '" + id + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                orderDetails = new OrderDetails();
                orderDetails.setSLID(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_SLID)));
                orderDetails.setProductName(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ProductName)));
                orderDetails.setProductID(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ProductID)));
                orderDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_WarehouseID)));
                orderDetails.setOpeningQty(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_OpeningQty)));
                orderDetails.setReturnQty(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ReturnQty)));
                orderDetails.setPrice(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Price)));
                orderDetails.setQuantity(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Quantity)));
                orderDetails.setTotal(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Total)));
                info.add(orderDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public List<OrderDetails> GetByModelMeter(int id, int meter) {

        OrderDetails orderDetails = new OrderDetails();
        List<OrderDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        //SELECT DISTINCT `brand_id`, (SELECT count(`id`) FROM product_infos where `brand_id` = p.brand_id ) as AOP FROM product_infos p
        //String query = "SELECT DISTINCT OpeningQty (SELECT count(ProductID) FROM OrderDetails WHERE OpeningQty = p.OpeningQty ) as AOP FROM OrderDetails ";
        String query = "SELECT * FROM OrderDetails WHERE OpeningQty = '" + id + "' AND ReturnQty = '" + meter + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                orderDetails = new OrderDetails();
                orderDetails.setSLID(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_SLID)));
                orderDetails.setProductName(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ProductName)));
                orderDetails.setProductID(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ProductID)));
                orderDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_WarehouseID)));
                orderDetails.setOpeningQty(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_OpeningQty)));
                orderDetails.setReturnQty(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ReturnQty)));
                orderDetails.setPrice(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Price)));
                orderDetails.setQuantity(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Quantity)));
                orderDetails.setTotal(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Total)));
                info.add(orderDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public List<OrderDetails> GetUpdateQuantity(int id, int meter, int proid) {

        OrderDetails orderDetails = new OrderDetails();
        List<OrderDetails> info = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        //SELECT DISTINCT `brand_id`, (SELECT count(`id`) FROM product_infos where `brand_id` = p.brand_id ) as AOP FROM product_infos p
        //String query = "SELECT DISTINCT OpeningQty (SELECT count(ProductID) FROM OrderDetails WHERE OpeningQty = p.OpeningQty ) as AOP FROM OrderDetails ";
        String query = "SELECT * FROM OrderDetails WHERE OpeningQty = '" + id + "' AND ReturnQty = '" + meter + "' AND ProductID != '" + proid + "'";
        Log.d("Get By SLID:", query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                orderDetails = new OrderDetails();
                orderDetails.setSLID(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_SLID)));
                orderDetails.setProductName(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ProductName)));
                orderDetails.setProductID(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ProductID)));
                orderDetails.setWarehouseID(cursor.getString(cursor.getColumnIndex(SalesDetails.KEY_WarehouseID)));
                orderDetails.setOpeningQty(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_OpeningQty)));
                orderDetails.setReturnQty(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_ReturnQty)));
                orderDetails.setPrice(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Price)));
                orderDetails.setQuantity(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Quantity)));
                orderDetails.setTotal(cursor.getString(cursor.getColumnIndex(OrderDetails.KEY_Total)));
                info.add(orderDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return info;
    }

    public Cursor getAllItem() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String sql = "SELECT * FROM OrderDetails";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c;
    }
}

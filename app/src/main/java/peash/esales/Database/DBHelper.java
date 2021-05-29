package peash.esales.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import peash.esales.App.AppController;
import peash.esales.Controller.OrderDetailsController;
import peash.esales.Controller.PurchaseDetailsController;
import peash.esales.Controller.SalesDetailsController;
import peash.esales.Controller.SalesReturnDetailsController;
import peash.esales.Controller.SessionUserController;
import peash.esales.Controller.WarehouseSendDetailsController;
import peash.esales.Models.OrderDetails;
import peash.esales.Models.PurchaseDetails;
import peash.esales.Models.SalesDetails;
import peash.esales.Models.SalesReturnDetails;
import peash.esales.Models.SessionUser;
import peash.esales.Models.WarehouseSendDetails;

/**
 * Created by DSL-Peash on 7/2/2018.
 */

public class DBHelper  extends SQLiteOpenHelper {

    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.

    private static final int DATABASE_VERSION =12;
    // Database Name
    private static final String DATABASE_NAME = "eSales.db";
    private static final String TAG = DBHelper.class.getSimpleName().toString();

    public DBHelper(Context context ) {
        super(AppController.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SessionUserController.CreateTable());
        db.execSQL(PurchaseDetailsController.CreateTable());
        db.execSQL(SalesDetailsController.CreateTable());
        db.execSQL(OrderDetailsController.CreateTable());
        db.execSQL(SalesReturnDetailsController.CreateTable());
        db.execSQL(WarehouseSendDetailsController.CreateTable());
        Log.e("DB","Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
       // db.execSQL("DROP TABLE IF EXISTS " + AppWitness.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SessionUser.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PurchaseDetails.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SalesDetails.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + OrderDetails.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SalesReturnDetails.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WarehouseSendDetails.TABLE);
        onCreate(db);
    }
}
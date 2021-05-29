package peash.esales.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import peash.esales.Database.DatabaseManager;
import peash.esales.Models.SessionUser;

/**
 * Created by tazulpoet on 11/22/2017.
 */

public class SessionUserController {
    private SessionUser sessionUser;

    public SessionUserController(){
        sessionUser = new SessionUser();
    }

    public static String CreateTable(){
        return "CREATE TABLE " + SessionUser.TABLE + "("
                + SessionUser.KEY_ID  + " INTEGER PRIMARY KEY,"
                + SessionUser.KEY_Name  + " TEXT,"
                + SessionUser.KEY_Email  + " TEXT,"
                + SessionUser.KEY_UID  + " TEXT,"
                + SessionUser.KEY_CreatedAt  + " TEXT,"
                + SessionUser.KEY_EmployeeID  + " INTEGER,"
                + SessionUser.KEY_DesignationID  + " INTEGER,"
                + SessionUser.KEY_CompanyID  + " INTEGER,"
                + SessionUser.KEY_UserName + " TEXT,"
                + SessionUser.KEY_Password + " TEXT)";
    }



    public Boolean isExist(String id)
    {
        sessionUser = new SessionUser();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM SessionUser WHERE ID = '" + id + "'";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount()>0) {
            DatabaseManager.getInstance().closeDatabase();
            Log.d("True", "True");
            return true;
        }
        else{
            DatabaseManager.getInstance().closeDatabase();
            Log.d("False", "False");
            return false;
        }
    }

    public int Insert(SessionUser sessionUser) {
        int courseId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        //values.put(SessionUser.KEY_ID, course.getCourseId());
        values.put(SessionUser.KEY_Name, sessionUser.getName());
        values.put(SessionUser.KEY_Email, sessionUser.getEmail());
        values.put(SessionUser.KEY_UID, sessionUser.getUID());
        values.put(SessionUser.KEY_CreatedAt, sessionUser.getCreatedAt());
        values.put(SessionUser.KEY_EmployeeID, sessionUser.getEmployeeID());
        values.put(SessionUser.KEY_DesignationID, sessionUser.getDesignationID());
        values.put(SessionUser.KEY_CompanyID, sessionUser.getCompanyID());
        values.put(SessionUser.KEY_UserName, sessionUser.getUserName());
        values.put(SessionUser.KEY_Password, sessionUser.getPassword());
        courseId=(int)db.insert(SessionUser.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
        return courseId;
    }

//    public int Update(SessionUser sessionUser) {
//        int courseId;
//        //String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
//        ContentValues values = new ContentValues();
////        values.put(SessionUser.KEY_ID, sessionUser.getID());
////        values.put(SessionUser.KEY_Name, sessionUser.getName());
////        values.put(SessionUser.KEY_Email, sessionUser.getEmail());
////        values.put(SessionUser.KEY_UID, sessionUser.getUID());
////        values.put(SessionUser.KEY_CreatedAt, sessionUser.getCreatedAt());
// //       values.put(SessionUser.KEY_EmployeeID, sessionUser.getEmployeeID());
//        values.put(SessionUser.KEY_DesignationID, sessionUser.getDesignationID());
//        values.put(SessionUser.KEY_ZoneID, sessionUser.getZoneID());
//        values.put(SessionUser.KEY_TerritoryID, sessionUser.getTerritoryID());
//        values.put(SessionUser.KEY_RouteID, sessionUser.getRouteID());
//        courseId=(int)db.update(SessionUser.TABLE, values, "ID" + " = ?", new String[]{sessionUser.getID()});
//        DatabaseManager.getInstance().closeDatabase();
//        return courseId;
//    }

    public void DeleteAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "DELETE FROM SessionUser";
        Log.d("Delete:", query);
        db.execSQL(query);
    }

    public int Update(int zoneID, int territoryID, int routeID, int designationID) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "UPDATE SessionUser SET ZoneID = "+ zoneID +", TerritoryID = "+ territoryID +", RouteID = "+ routeID +", DesignationID = "+ designationID +";";// WHERE ID='"+ id +"'";
        db.execSQL(query);
        return 1;
    }

    public List<SessionUser> GetAll(){
        SessionUser sessionUser = new SessionUser();
        List<SessionUser> sessionUsers = new ArrayList<SessionUser>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query =  "SELECT * FROM SessionUser";
        Log.d("Get All:",query);
        Cursor cursor = db.rawQuery(query, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                sessionUser = new SessionUser();
                sessionUser.setID(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_ID)));
                sessionUser.setName(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_Name)));
                sessionUser.setEmail(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_Email)));
                sessionUser.setUID(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_UID)));
                sessionUser.setCreatedAt(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_CreatedAt)));
                sessionUser.setEmployeeID(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_EmployeeID)));
                sessionUser.setDesignationID(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_DesignationID)));
                sessionUser.setCompanyID(String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_CompanyID))));
                sessionUser.setUserName(String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_UserName))));
                sessionUser.setPassword(String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_Password))));
//                Log.d(SessionUser.KEY_ID,String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_RouteID))));
//                Log.d(SessionUser.KEY_RouteID,String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_RouteID))));
//                Log.d(SessionUser.KEY_TerritoryID,String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_TerritoryID))));
//                Log.d(SessionUser.KEY_ZoneID,String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_ZoneID))));
//                Log.d(SessionUser.KEY_Name,String.valueOf(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_Name))));
//                Log.d(SessionUser.KEY_Email,String.valueOf(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_Email))));
//                Log.d(SessionUser.KEY_EmployeeID,String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_EmployeeID))));
                sessionUsers.add(sessionUser);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return sessionUsers;
    }

    public List<SessionUser> GetByID(String id){
        SessionUser sessionUser = new SessionUser();
        List<SessionUser> sessionUsers = new ArrayList<SessionUser>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query =  "SELECT * FROM SessionUser WHERE ID = '"+ id +"'";
        Log.d("Get By ID:",query);
        Cursor cursor = db.rawQuery(query,  new String[] { String.valueOf(id) });
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                sessionUser= new SessionUser();
                sessionUser.setID(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_ID)));
                sessionUser.setName(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_Name)));
                sessionUser.setEmail(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_Email)));
                sessionUser.setUID(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_UID)));
                sessionUser.setCreatedAt(cursor.getString(cursor.getColumnIndex(SessionUser.KEY_CreatedAt)));
                sessionUser.setEmployeeID(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_EmployeeID)));
                sessionUser.setDesignationID(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_DesignationID)));
                sessionUser.setCompanyID(String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_CompanyID))));
                sessionUser.setUserName(String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_UserName))));
                sessionUser.setPassword(String.valueOf(cursor.getInt(cursor.getColumnIndex(SessionUser.KEY_Password))));
                sessionUsers.add(sessionUser);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return sessionUsers;
    }

    public Boolean isSession()
    {
        sessionUser = new SessionUser();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM SessionUser";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount()>0){
            DatabaseManager.getInstance().closeDatabase();
            Log.d("Email check", "True");
            return true;
        }
        else{
            DatabaseManager.getInstance().closeDatabase();
            Log.d("Email", "False");
            return false;
        }
    }
}

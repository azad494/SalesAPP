package peash.esales.Models;

/**
 * Created by Adi on 11/22/2017.
 */

public class SessionUser {
    public static final String TAG = SessionUser.class.getSimpleName();
    public static final String TABLE = "SessionUser";
    // Labels Table Columns names
    public static final String KEY_ID = "ID";
    public static final String KEY_Name = "Name";
    public static final String KEY_Email = "Email";
    public static final String KEY_UID = "UID";
    public static final String KEY_CreatedAt = "CreatedAt";
    public static final String KEY_EmployeeID = "EmployeeID";
    public static final String KEY_DesignationID = "DesignationID";

    public static final String KEY_CompanyID =  "CompanyID";
    public static final String KEY_UserName=    "UserName";
    public static final String KEY_Password=    "Password";



    private String ID;
    private String Name;
    private String Email;
    private String UID;
    private int     EmployeeID;
    private int     DesignationID;
    private String CreatedAt;
    private String CompanyID ;
    private String UserName;
    private String Password;


    //    private int ZoneID;
//    private int TerritoryID;
//    private int RouteID;


//get

    public String getID() {return ID;}

    public String getName() {return Name;}

    public String getEmail() {return Email;}

    public String getUID() {return UID;}

    public int getEmployeeID() {return EmployeeID;}

    public int getDesignationID() {return DesignationID;}

    public String getCreatedAt() {return CreatedAt;}

    public String getCompanyID() {return CompanyID;}

    public String getUserName() {return UserName;}

    public String getPassword() {return Password;}

    //set

    public void setID(String ID) {this.ID = ID;}

    public void setName(String name) {Name = name;}

    public void setEmail(String email) {Email = email;}

    public void setUID(String UID) {this.UID = UID;}

    public void setEmployeeID(int employeeID) {EmployeeID = employeeID;}

    public void setDesignationID(int designationID) {DesignationID = designationID;}

    public void setCreatedAt(String createdAt) {CreatedAt = createdAt;}

    public void setCompanyID(String companyID) {CompanyID = companyID;}

    public void setUserName(String userName) {UserName = userName;}

    public void setPassword(String password) {Password = password;}
}
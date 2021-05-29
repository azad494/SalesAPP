package peash.esales.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import peash.esales.App.AppController;
import peash.esales.Controller.SessionUserController;
import peash.esales.Helper.Common;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.SessionUser;
import Adi.esales.R;

import static peash.esales.App.AppConfig.URL_Login;

public class
LogoutFragment extends Activity {
    private static final String TAG = LogoutFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    private Button btnLogin;
    private EditText txtemail, txtpassword;
    private SessionManager session;
    private SessionUserController sessionUserController;
    public static String name;
    //Button login;

    @Override
    public void onBackPressed(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        btnLogin=findViewById(R.id.btnLogin);
        Common common = new Common();
        if (common.IsInternetConnected() == false) {
            Toast.makeText(this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(Activity_Employee_Salary.this, MainActivity1.class);
//            startActivity(intent);
        } else {
            txtemail = (EditText) findViewById(R.id.txtEmail);
            txtpassword = (EditText) findViewById(R.id.txtPassword);
            btnLogin = (Button) findViewById(R.id.btnLogin);

            // SQLite database handler
            sessionUserController = new SessionUserController();
            // Session manager
            session = new SessionManager(getApplicationContext());

            // Check if user is already logged in or not
            if (session.isLoggedIn()) {
                // User is already logged in. Take him to main activity
                Intent intent = new Intent(LogoutFragment.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            // Login button Click Event
            btnLogin.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    String email = txtemail.getText().toString().trim();
                    String password = txtpassword.getText().toString().trim();

                    if (!email.isEmpty() && !password.isEmpty()) {
                        checkLogin(email, password);
                    } else {

                        Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
                    }
                }

            });
        }

    }


    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();
        JSONObject Obj = new JSONObject();
        try {
            Obj.put("stremail", email);
            Obj.put("strPassword", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Login URL","LOGIN"+URL_Login + "stremail=" + email + "&strPassword=" + password + "");
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, URL_Login + "stremail=" + email + "&strPassword=" + password + "", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Login Response: " + response.toString());
                pDialog.hide();

                try {
                    if (!response.getString("UserID").toString().equals("")) {
                        Log.d(TAG, "Login Response: " + response.getString("UserID").toString());
                        // Create login session
                        session.setLogin(true);
                        // Now store the user in SQLite
                        SessionUser sessionUser = new SessionUser();
                        // sessionUser.setName(response.getString("FirstName")+" "+response.getString("LastName"));
                        sessionUser.setName(response.getString("UserName"));
                        sessionUser.setUserName(response.getString("UserName"));
                        sessionUser.setEmail(response.getString("email"));
                        sessionUser.setUID(response.getString("UserID"));
                        sessionUser.setCreatedAt(response.getString("CreateDate"));
                        sessionUser.setCompanyID(response.getString("CompanyID"));
                        sessionUser.setPassword(response.getString("Password"));
                        sessionUser.setDesignationID(Integer.parseInt(response.getString("DesignationID")));
                        sessionUser.setEmployeeID(0);
                        sessionUserController.Insert(sessionUser);
                        Intent intent = new Intent(LogoutFragment.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        hideDialog();

                    } else {
                        hideDialog();
                        //Toast.makeText(getApplicationContext(), "Login credentials are wrong. Please try again!", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    hideDialog();
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e(TAG, "Login Error:" + error.getMessage());
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(), "Login credentials are wrong. Please try again!", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_LONG).show();
            }
        }) {

        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

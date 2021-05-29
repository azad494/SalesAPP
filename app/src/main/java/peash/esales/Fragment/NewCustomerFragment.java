package peash.esales.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peash.esales.App.AppConfig;
import peash.esales.App.AppController;
import peash.esales.Controller.OrderDetailsController;
import peash.esales.Controller.SessionUserController;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.OrderDetails;
import peash.esales.Models.SessionUser;
import Adi.esales.R;

import static java.util.Locale.US;
import static peash.esales.App.AppController.TAG;

public class NewCustomerFragment extends Fragment {

    Calendar myCalendar = Calendar.getInstance();
    private SessionManager session;
    private SessionUserController sessionUserController;
    ProgressDialog progressDialog;

    Spinner spCustype;
    Button btnClear, btnSave;
    EditText txtName,txtMobile,txtAddress,txtCommission,txtEmail,txtCraditLimit;


    private List<Map<String, Object>> customerTypeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_fragment, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        txtName = v.findViewById(R.id.txtName);
        txtMobile = v.findViewById(R.id.txtMobile);
        txtAddress = v.findViewById(R.id.txtCusAddress);
        txtCommission = v.findViewById(R.id.txtCommission);
        txtEmail = v.findViewById(R.id.txtEmail);
        txtCraditLimit = v.findViewById(R.id.txtCreditLimit);

        spCustype = v.findViewById(R.id.spCustomerType);
        btnClear = v.findViewById(R.id.btnCancel);
        btnSave = v.findViewById(R.id.btnSave);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnSave();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        LoadCustomerType();
        return v;
    }

    public void LoadCustomerType() {
        progressDialog.setMessage("Loading Customer Types...");
        showDialog();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllCustomerType, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    customerTypeList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("CustomerTypeID", "0");
                    map.put("CustomerTypeName", "Select Price Type");
                    customerTypeList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("CustomerTypeID", object.getString("CustomerTypeID"));
                        map.put("CustomerTypeName", object.getString("CustomerTypeName"));
                        customerTypeList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), customerTypeList, R.layout.spinner_text_colour,
                            new String[]{"CustomerTypeName"}, new int[]{android.R.id.text1});
                    spCustype.setAdapter(arrayAdapter);
                    hideDialog();
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    hideDialog();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Customer Types: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void BtnSave(){
        if(txtName.getText().toString().length() != 0 && txtCraditLimit.getText().toString().length()!=0 && spCustype.getSelectedItemPosition() >0)
        {
            progressDialog.setMessage("Saving...");
            showDialog();

            JSONObject Obj = new JSONObject();
            try{
                sessionUserController=new SessionUserController();
                List<SessionUser>sessionList=sessionUserController.GetAll();
                //-----select data from spinner by id------//
                Map<String, Object> selectedItem = null;
                selectedItem = customerTypeList.get(spCustype.getSelectedItemPosition());

                Obj.put("CustomerID", 0);
                Obj.put("CustomerCode", 0);
                Obj.put("CustomerName", txtName.getText().toString());
                Obj.put("NameExtension", 0);
                Obj.put("CustomerTypeID", (String.valueOf(selectedItem.get("CustomerTypeID"))));
                Obj.put("ContactPerson", "");
                Obj.put("Address", txtAddress.getText().toString());
                Obj.put("DistrictID", 0);
                Obj.put("CountryID", 0);
                Obj.put("Phone", txtMobile.getText().toString());
                Obj.put("Mobile", txtMobile.getText().toString());
                Obj.put("eMail", txtEmail.getText().toString());
                Obj.put("VATRegNo", 1);
                Obj.put("DiscountPercent", txtCommission.getText().toString());
                Obj.put("DistributorPoint", 0);
                Obj.put("Status", "true");
                Obj.put("CompanyID", sessionList.get(0).getCompanyID());
                Obj.put("ParentID", 0);
                Obj.put("ZoneID", 0);
                Obj.put("Price", 0);
                Obj.put("Rent", 0);
                Obj.put("Creator", sessionList.get(0).getEmail());
                Obj.put("CreationDate", getDateTime());
                Obj.put("Modifier", sessionList.get(0).getEmail());
                Obj.put("ModificationDate", getDateTime());
                Obj.put("CustomerDiposite", txtCraditLimit.getText().toString());
                Obj.put("PaymentTypeID", 0);
                Obj.put("CustomerDetails", "");
                //CustomerDetails: $scope.obGrid

                JsonObjectRequest strReq1 = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_AddCustomerInfo, Obj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(ContentValues.TAG, "response" + response.toString());
                        Toast.makeText(AppController.getContext(), "Saved Successfully.", Toast.LENGTH_SHORT).show();
                        clear();
                        hideDialog();

                        Canvas_Fragment canvas_fragment1 = new Canvas_Fragment();
                        android.support.v4.app.FragmentTransaction test1fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        test1fragmentTransaction.replace(R.id.frame,canvas_fragment1);
                        test1fragmentTransaction.commit();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AppController.getContext(), "Failed to save.", Toast.LENGTH_SHORT).show();
                        Log.e(ContentValues.TAG, "Error: " + error.getMessage());
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                        //Toast.makeText(BalanceTransper.this, "Last query is not feedback.", Toast.LENGTH_SHORT).show();
                    }
                });
                //---- Check for Double entry..##stop##---//
                strReq1.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                // Adding request to request queu
                //
                AppController.getInstance().addToRequestQueue(strReq1, null);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
            }
        }
        else {
            //Toast.makeText(getActivity().getApplicationContext(), "Hi", Toast.LENGTH_SHORT).show();
            if (txtName.getText().toString().length() == 0) {
                txtName.requestFocus();
                txtName.setError("Please provide customer name");
            } if (txtCraditLimit.getText().toString().length() == 0){
                txtCraditLimit.requestFocus();
                txtCraditLimit.setError("Please provide credit limit");
            }
            if(spCustype.getSelectedItemPosition() == 0)
            {
                Toast.makeText(AppController.getContext(), "Select Customer Type", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void clear() {
        spCustype.setSelection(0);
        txtName.setText("");
        txtMobile.setText("");
        txtAddress.setText("");
        txtCommission.setText("");
        txtEmail.setText("");
        txtCraditLimit.setText("");

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}


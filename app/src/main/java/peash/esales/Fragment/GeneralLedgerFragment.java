package peash.esales.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import peash.esales.Controller.SessionUserController;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.SessionUser;
import Adi.esales.R;

import static java.util.Locale.US;
import static peash.esales.App.AppController.TAG;

public class GeneralLedgerFragment extends Fragment {

    Calendar myCalendar = Calendar.getInstance();
    ProgressDialog progressDialog;
    private SessionManager session;
    private SessionUserController sessionUserController;
    EditText Amount, ChequeNo, Mdate, txtDescription;
    Spinner spAccountType, spAccountGroup,spAccountHead, spPayType, spBank, spBrance;
    Button btnSave, btnCancel;
    RadioButton rbDebit,rbCredit;

    private List<Map<String, Object>> paymentTypeLists = new ArrayList<>();
    private List<Map<String, Object>> bankList = new ArrayList<>();
    private List<Map<String, Object>> branchList = new ArrayList<>();
    private List<Map<String, Object>> accTypeList = new ArrayList<>();
    private List<Map<String, Object>> accGroupList = new ArrayList<>();
    private List<Map<String, Object>> accHeadLists = new ArrayList<>();
    List<String> accHeadList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.general_ledger, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        Amount = v.findViewById(R.id.txtAmount);
        ChequeNo = v.findViewById(R.id.txtCheque);
        Mdate = v.findViewById(R.id.txtMDate);
        txtDescription = v.findViewById(R.id.txtDescription);


        spAccountType = v.findViewById(R.id.spAccountType);
        spAccountGroup = v.findViewById(R.id.spAccountGroup);
        spAccountHead = v.findViewById(R.id.spAccountHead);
        spPayType = v.findViewById(R.id.spPaymentType);
        spBank = v.findViewById(R.id.spBank);
        spBrance = v.findViewById(R.id.spBrance);

        rbDebit=v.findViewById(R.id.rbDebit);
        rbCredit=v.findViewById(R.id.rbCredit);

        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnSave();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });

        //-------Call Functions-----
        LoadDate();
        addOnAccHeadByID(2);
        //loadAccType();
        //LoadPaymentTyps();

        return v;
    }

    public void LoadPaymentTyps() {

        try {
            paymentTypeLists = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("PaymentTypeID", 0);
            map.put("PaymentTypeName", "Select Payment Type");
            paymentTypeLists.add(map);
            map = new HashMap<>();
            map.put("PaymentTypeID", 1);
            map.put("PaymentTypeName", "Cash");
            paymentTypeLists.add(map);

            map = new HashMap<>();
            map.put("PaymentTypeID", 2);
            map.put("PaymentTypeName", "Bank");
            paymentTypeLists.add(map);

            SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), paymentTypeLists, R.layout.spinner_text_colour,
                    new String[]{"PaymentTypeName"}, new int[]{android.R.id.text1});
            spPayType.setAdapter(arrayAdapter);


            spPayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    Log.e(TAG, "spPayType" + spPayType.getSelectedItemId());

                    if (spPayType.getSelectedItemId() == 0) {
                        ChequeNo.setVisibility(View.GONE);
                        spBank.setVisibility(View.GONE);
                        spBrance.setVisibility(View.GONE);
                        Mdate.setVisibility(View.GONE);
                    } else if (spPayType.getSelectedItemId() == 1) {
                        ChequeNo.setVisibility(View.GONE);
                        spBank.setVisibility(View.GONE);
                        spBrance.setVisibility(View.GONE);
                        Mdate.setVisibility(View.GONE);

                    } else {
                        ChequeNo.setVisibility(View.VISIBLE);
                        spBank.setVisibility(View.VISIBLE);
                        spBrance.setVisibility(View.VISIBLE);
                        Mdate.setVisibility(View.VISIBLE);
                        loadBank();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

        } catch (Exception e) {

            e.printStackTrace();
            // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void LoadDate() {
        //Default date for Maturity date
        String maturityDate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        Mdate.setText(maturityDate);
        setMaturity_date();

        final DatePickerDialog.OnDateSetListener datem = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setMaturity_date();
            }
        };

        //on Change of Maturity Date
        Mdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), datem, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void setMaturity_date() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, US);
        Mdate.setText(sdf.format(myCalendar.getTime()));
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void loadBank() {
        progressDialog.setMessage("Loading...");
        showDialog();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllBank, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    bankList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("BankID", "0");
                    map.put("BankName", "--Select Your Bank--");
                    bankList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("BankID", object.getString("BankID"));
                        map.put("BankName", object.getString("BankName"));
                        bankList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), bankList, R.layout.spinner_text_colour,
                            new String[]{"BankName"}, new int[]{android.R.id.text1});
                    spBank.setAdapter(arrayAdapter);
                    hideDialog();
                    loadBranch();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Bank: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void loadBranch() {
        progressDialog.setMessage("Loading...");
        showDialog();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllBranch, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    branchList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("BranchID", "0");
                    map.put("BranchName", "--Select Branch--");
                    branchList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("BranchID", object.getString("BranchID"));
                        map.put("BranchName", object.getString("BranchName"));
                        branchList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), branchList, R.layout.spinner_text_colour,
                            new String[]{"BranchName"}, new int[]{android.R.id.text1});
                    hideDialog();
                    spBrance.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Bank: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void loadAccType() {
        progressDialog.setMessage("Loading...");
        showDialog();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AccType, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    accTypeList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("AccTypeID", "0");
                    map.put("AccTypeName", "--Select Account Type--");
                    accTypeList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("AccTypeID", object.getString("AccTypeID"));
                        map.put("AccTypeName", object.getString("AccTypeName"));
                        accTypeList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), accTypeList, R.layout.spinner_text_colour,
                            new String[]{"AccTypeName"}, new int[]{android.R.id.text1});
                    spAccountType.setAdapter(arrayAdapter);

                    hideDialog();
                    spAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spAccountType.getSelectedItemId() > 0) {
                                Map<String, Object> selectPruductID = accTypeList.get(spAccountType.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPruductID.get("AccTypeID").toString());
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                LoadAccGroupByID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                //price.setText("");
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Account Type", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }

                    });

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
                Log.e(TAG, "AccType: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void LoadAccGroupByID(int id) {
        progressDialog.setMessage("Loading...");
        showDialog();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AccGroup + id, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    accGroupList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("AccGroupID", "0");
                    map.put("AccGroupName", "--Select Account Group--");
                    accGroupList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("AccGroupID", object.getString("AccGroupID"));
                        map.put("AccGroupName", object.getString("AccGroupName"));
                        accGroupList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), accGroupList, R.layout.spinner_text_colour,
                            new String[]{"AccGroupName"}, new int[]{android.R.id.text1});
                    spAccountGroup.setAdapter(arrayAdapter);

                    hideDialog();
                    spAccountGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spAccountGroup.getSelectedItemId() > 0) {
                                Map<String, Object> selectPruductID = accGroupList.get(spAccountGroup.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPruductID.get("AccGroupID").toString());
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                addOnAccHeadByID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                //price.setText("");
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Account Group", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }

                    });

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "AccGroup: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

//    public void LoadAccHeadByID(int id) {
//        progressDialog.setMessage("Loading Expanse...");
//        showDialog();
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AccHead + id, null, new Response.Listener<JSONArray>() {
//
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    Log.e("response", "" + response.toString());
//
//                    accHeadList = new ArrayList<>();
//
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("AccHeadID", "0");
//                    map.put("AccountName", "--Select Expanse Type--");
//                    accHeadList.add(map);
//
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        map.put("AccHeadID", object.getString("AccHeadID"));
//                        map.put("AccountName", object.getString("AccountName"));
//                        accHeadList.add(map);
//                    }
//                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), accHeadLists, R.layout.spinner_text_colour,
//                            new String[]{"AccountName"}, new int[]{android.R.id.text1});
//                    spAccountHead.setAdapter(arrayAdapter);
//                    hideDialog();
//
//
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    hideDialog();
//                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "AccHead: " + error.getMessage());
//                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }

    public void addOnAccHeadByID(int id) {
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AccHead + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    accHeadLists= new ArrayList<>();
                    accHeadList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("AccHeadID", "0");
                    map.put("AccountName", "Select Expanse Type");
                    accHeadLists.add(map);
                    accHeadList.add("Select Expanse Type");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        //Log.d(TAG, "Response: " + object.toString());
                        map.put("AccHeadID", object.getString("AccHeadID"));
                        //  map.put("CustomerName", object.getString("customerName"));
                        map.put("AccountName", object.getString("AccountName"));// + " " + object.getString("CustomerCode"));

                        accHeadLists.add(map);
                        // stockList.add(object.getString("customerName"));
                        accHeadList.add(object.getString("AccountName"));// + " " + object.getString("CustomerCode"));
                    }
                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, accHeadList);
                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
                    spAccountHead.setAdapter(adapter);
                    hideDialog();



                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    hideDialog();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
           /* @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserName", email);
                params.put("Password", password);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/x-www-form-urlencoded");
                //headers.put("Content-Type", "application/json");
                // Removed this line if you dont need it or Use application/json
                //headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Content-Type", "application/json; charset=utf-8;");
                //headers.put("Token", AppConfig.Token);
                return headers;
            }*/
        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void BtnSave(){

        if (spAccountHead.getSelectedItemId()!=0 && Amount.getText().toString().length()!=0)
        {
            progressDialog.setMessage("Saving...");
            showDialog();

            JSONObject Obj = new JSONObject();
            try{
                sessionUserController=new SessionUserController();
                List<SessionUser>sessionList=sessionUserController.GetAll();
                //-----select data from spinner by id------//
                Map<String, Object> selectedItem = null;
                Obj.put("TrnID", "");
                Obj.put("TrnDate", "");
                Obj.put("VoucherNo", "");
                Obj.put("CompanyID", sessionList.get(0).getCompanyID());
                Obj.put("AccProjectID", "0");
//            if (rbDebit.isChecked()==true)
//            {
                Obj.put("VoucherTypeID","1");
//            }
//            else {
//                Obj.put("VoucherTypeID","2");
//            }
//            if (spPayType.getSelectedItemId()==2)
//            {
//                Obj.put("PaymentTypeID", "2");
//            }
//            else {
                Obj.put("PaymentTypeID", "1");
//            }
                selectedItem = accHeadLists.get(spAccountHead.getSelectedItemPosition());
                Obj.put("AccountHeadID", (String.valueOf(selectedItem.get("AccHeadID"))));
                Obj.put("BatchID", "");
                Obj.put("Description", txtDescription.getText().toString());
                Obj.put("Debit", Amount.getText().toString());
                Obj.put("Credit", Amount.getText().toString());
                Obj.put("QuarterID", 0);
                Obj.put("BudgetID", 0);
                Obj.put("Voucher", "");
                Obj.put("Status", 1);
                Obj.put("Creator", sessionList.get(0).getEmail());
                Obj.put("CreationDate", "");
                Obj.put("Modifier", sessionList.get(0).getEmail());
                Obj.put("ModificationDate", "");
//            if(spPayType.getSelectedItemId()>0)
//            {
//                selectedItem = bankList.get(spBank.getSelectedItemPosition());
//                Obj.put("BankID", (String.valueOf(selectedItem.get("BankID"))));
//                selectedItem = branchList.get(spBrance.getSelectedItemPosition());
//                Obj.put("BranchID", (String.valueOf(selectedItem.get("BranchID"))));
//            }
//            else {
                Obj.put("BankID", spBank.getSelectedItemId());
                Obj.put("BranchID", spBrance.getSelectedItemId());
//            }
                Obj.put("ChequeNo", ChequeNo.getText().toString());
                Obj.put("MaturityDate", Mdate.getText().toString());
                Obj.put("Cheque_Amount", Amount.getText().toString());

                JsonObjectRequest strReq1 = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_AddGeneralLedger, Obj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(ContentValues.TAG, "response" + response.toString());
                        Toast.makeText(AppController.getContext(), "Saved Successfully.", Toast.LENGTH_SHORT).show();
                        Clear();
                        hideDialog();
//                    Intent intent = new Intent(BalanceTransper.this,MainActivity1.class);
//                    startActivity(intent);
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
        else
        {
            if (spAccountHead.getSelectedItemId()==0)
            {
                Toast.makeText(getContext(), "Select Expense Head", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Amount.requestFocus();
                Amount.setError("Please provide an amount");
            }
        }


    }

    public void Clear() {
        txtDescription.setText("");
        spAccountType.setSelection(0);
        spAccountGroup.setSelection(0);
        spAccountHead.setSelection(0);
        spBank.setSelection(0);
        spPayType.setSelection(0);
        Amount.setText("");
        ChequeNo.setText("");
        spBank.setSelection(0);
        spBrance.setSelection(0);

        LoadDate();
    }

    public void ClearAll() {
        ChequeNo.setText("");
        Mdate.setText("");
        spPayType.setSelection(0);
        spBank.setSelection(0);
        spBrance.setSelection(0);
    }


    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}


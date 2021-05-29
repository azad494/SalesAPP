package peash.esales.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peash.esales.App.AppConfig;
import peash.esales.App.AppController;
import peash.esales.Controller.SalesDetailsController;
import peash.esales.Controller.SessionUserController;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.SalesDetails;
import peash.esales.Models.SessionUser;
import Adi.esales.R;

import static java.util.Locale.US;
import static peash.esales.App.AppController.TAG;

public class PaymentReceivedFragment extends Fragment {

    Calendar myCalendar = Calendar.getInstance();
    ProgressDialog progressDialog;
    private SessionManager session;
    private SessionUserController sessionUserController;
    EditText address, previousDue,OverLess, PayAmount, PaidAmount, BlanceDue, ChequeNo, Mdate,
            txtHPrice;
    Spinner spCusName, spSupplier,spPayType, spBank, spBrance, spAccountNo,spAccHead;
    Button btnSave, btnCancel;
    RadioButton rbCustomer,rbSupplier;
    LinearLayout customer,supplier,less;
    String MobileNumberCus,MobileNumberSup;

    private List<Map<String, Object>> paymentTypeLists = new ArrayList<>();
    private List<Map<String, Object>> accounNoList = new ArrayList<>();
    private List<Map<String, Object>> bankList = new ArrayList<>();
    private List<Map<String, Object>> branchList = new ArrayList<>();
    private List<Map<String, Object>> accounHeadList = new ArrayList<>();
    private List<Map<String, Object>> customerLists = new ArrayList<>();
    List<String> customerList;
    private List<Map<String, Object>> supplierLists = new ArrayList<>();
    List<String> supplierList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.payment_received, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        address = v.findViewById(R.id.txtCusAddress);
        previousDue = v.findViewById(R.id.txtPreviousDue);
        OverLess = v.findViewById(R.id.txtLess);
        PayAmount = v.findViewById(R.id.txtPayable);
        PaidAmount = v.findViewById(R.id.txtPaidAmount);
        BlanceDue = v.findViewById(R.id.txtDueAmount);
        ChequeNo = v.findViewById(R.id.txtCheque);
        Mdate = v.findViewById(R.id.txtMDate);


        spCusName = v.findViewById(R.id.spCustomerName);
        spSupplier = v.findViewById(R.id.spSupplierName);
        spPayType = v.findViewById(R.id.spPaymentType);
        spBank = v.findViewById(R.id.spBank);
        spBrance = v.findViewById(R.id.spBrance);
        spAccountNo = v.findViewById(R.id.spAccountNo);
        spAccHead = v.findViewById(R.id.spAccHead);

        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);

        rbCustomer=v.findViewById(R.id.rbCustomer);
        rbSupplier=v.findViewById(R.id.rbSupplier);

        customer = v.findViewById(R.id.customer);
        supplier = v.findViewById(R.id.supplier);
        less = v.findViewById(R.id.less);

        rbCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbCustomer.isChecked()==true)
                {
                    supplier.setVisibility(View.GONE);
                    less.setVisibility(View.VISIBLE);
                    customer.setVisibility(View.VISIBLE);
                    spCusName.setSelection(0);
                    address.setText("");
                    previousDue.setText("");
                    Toast.makeText(AppController.getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
                    TextChangeForPrice();
                }
            }
        });
        rbSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbSupplier.isChecked()==true)
                {
                    customer.setVisibility(View.GONE);
                    supplier.setVisibility(View.VISIBLE);
                    less.setVisibility(View.GONE);
                    spSupplier.setSelection(0);
                    address.setText("");
                    previousDue.setText("");
                    addOnSupplier();
                    //Toast.makeText(AppController.getContext(), "Select Supplier", Toast.LENGTH_SHORT).show();
                    TextChangeForPriceSup();
                }
            }
        });

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
        loadAccHead();
        addOnCustomer();
        LoadPaymentTyps();
        loadAccNo();
        //loadBank();
        if (rbCustomer.isChecked()==true)
        {
            TextChangeForPrice();
        }
        //TextChangeForPrice();
        return v;
    }

    public void LoadPaymentTyps() {

        try {
            paymentTypeLists = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
//            map.put("PaymentTypeID", 0);
//            map.put("PaymentTypeName", "Select Payment Type");
//            paymentTypeLists.add(map);
            map = new HashMap<>();
            map.put("PaymentTypeID", 0);
            map.put("PaymentTypeName", "Cash");
            paymentTypeLists.add(map);

            map = new HashMap<>();
            map.put("PaymentTypeID", 1);
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
                        spAccountNo.setVisibility(View.GONE);
                        spAccHead.setVisibility(View.GONE);
                    }
//                    else if (spPayType.getSelectedItemId() == 1) {
//                        ChequeNo.setVisibility(View.GONE);
//                        spBank.setVisibility(View.GONE);
//                        spBrance.setVisibility(View.GONE);
//                        Mdate.setVisibility(View.GONE);
//
//                    }
                    else {
                        ChequeNo.setVisibility(View.VISIBLE);
                        spBank.setVisibility(View.VISIBLE);
                        spBrance.setVisibility(View.VISIBLE);
                        Mdate.setVisibility(View.VISIBLE);
                        spAccountNo.setVisibility(View.VISIBLE);
                        spAccHead.setVisibility(View.VISIBLE);
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

    public void loadAccNo() {
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllAccNo, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    accounNoList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("BranchID", "0");
                    map.put("AccountNo", "--Select Your Account No--");
                    accounNoList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("BranchID", object.getString("BranchID"));
                        map.put("AccountNo", object.getString("AccountNo"));
                        accounNoList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), accounNoList, R.layout.spinner_text_colour,
                            new String[]{"AccountNo"}, new int[]{android.R.id.text1});
                    spAccountNo.setAdapter(arrayAdapter);

                    spAccountNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spAccountNo.getSelectedItemId() > 0) {
                                Map<String, Object> selectAccountNo = accounNoList.get(spAccountNo.getSelectedItemPosition());
                                String accNo= selectAccountNo.get("AccountNo").toString();
                                //int id = Integer.valueOf(selectPersonID.get("CustomerID").toString());
                                loadBank(accNo);
                                loadBranch(accNo);
                            } else {
//                                mobile.setText("");
//                                address.setText("");
                                Toast.makeText(AppController.getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }

                    });

                    //loadBranch();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Bank: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void loadBank(String accNo) {
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_BankByAccNo + accNo, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    bankList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    //map.put("BankID", "0");
                    //map.put("BankName", "--Select Your Bank--");
                    //bankList.add(map);

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

                    //loadBranch();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Bank: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void loadBranch(String accNo) {
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_BankByAccNo + accNo, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    branchList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
//                    map.put("BranchID", "0");
//                    map.put("BranchName", "--Select Branch--");
//                    branchList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("BranchID", object.getString("BranchID"));
                        map.put("BranchName", object.getString("BranchName"));
                        branchList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), branchList, R.layout.spinner_text_colour,
                            new String[]{"BranchName"}, new int[]{android.R.id.text1});
                    spBrance.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Bank: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void loadAccHead() {
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllAccHead + 1, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    accounHeadList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("AccHeadID", "0");
                    map.put("AccountName", "--Select Your Account Head--");
                    accounHeadList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("AccHeadID", object.getString("AccHeadID"));
                        map.put("AccountName", object.getString("AccountName"));
                        accounHeadList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), accounHeadList, R.layout.spinner_text_colour,
                            new String[]{"AccountName"}, new int[]{android.R.id.text1});
                    spAccHead.setAdapter(arrayAdapter);

                    spAccHead.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spAccHead.getSelectedItemId() > 0) {
                                Map<String, Object> selectAccountNo = accounHeadList.get(spAccountNo.getSelectedItemPosition());
                                String accNo= selectAccountNo.get("AccHeadID").toString();
                                //int id = Integer.valueOf(selectPersonID.get("CustomerID").toString());

                            } else {
//                                mobile.setText("");
//                                address.setText("");
                                Toast.makeText(AppController.getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }

                    });

                    //loadBranch();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Bank: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

//    public void loadCustomer() {
//        progressDialog.setMessage("Loading Customer...");
//        showDialog();
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllCustomer, null, new Response.Listener<JSONArray>() {
//
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    Log.e("response", "" + response.toString());
//
//                    customerList = new ArrayList<>();
//
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("CustomerID", "0");
//                    map.put("CustomerName", "--Select Customer Name--");
//                    customerList.add(map);
//
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        map.put("CustomerID", object.getString("CustomerID"));
//                        map.put("CustomerName", object.getString("CustomerName"));
//                        customerList.add(map);
//                    }
//                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), customerList, R.layout.spinner_text_colour,
//                            new String[]{"CustomerName"}, new int[]{android.R.id.text1});
//                    // arrayAdapter.setDropDownViewResource(R.layout.dropdownlist_style);
//                    spCusName.setAdapter(arrayAdapter);
//
//                    hideDialog();
//                    spCusName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spCusName.getSelectedItemId() > 0) {
//                                Map<String, Object> selectPersonID = customerList.get(spCusName.getSelectedItemPosition());
//                                int id = Integer.valueOf(selectPersonID.get("CustomerID").toString());
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                LoadPersonInfoByID(id);
//                                sessionUserController=new SessionUserController();
//                                List<SessionUser>sessionList=sessionUserController.GetAll();
//                                int id1= Integer.parseInt(sessionList.get(0).getCompanyID());
//                                LoadCusDueInfoByID(id1,id);
//                            } else {
//                                address.setText("");
//                                Toast.makeText(AppController.getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> arg0) {
//                            // TODO Auto-generated method stub
//                        }
//
//                    });
//
//                } catch (JSONException e) {
//                    // JSON error
//                    hideDialog();
//                    e.printStackTrace();
//                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Customer: " + error.getMessage());
//                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }

    public void addOnCustomer() {
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllCustomer, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    customerLists = new ArrayList<>();
                    customerList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("CustomerID", "0");
                    map.put("CustomerName", "Select Customer");
                    map.put("Mobile", "Mobile");
                    customerLists.add(map);
                    customerList.add("Select Customer");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        //Log.d(TAG, "Response: " + object.toString());
                        map.put("CustomerID", object.getString("CustomerID"));
                        //  map.put("CustomerName", object.getString("customerName"));
                        map.put("CustomerName", object.getString("CustomerName"));// + " " + object.getString("CustomerCode"));
                        map.put("Mobile", object.getString("Mobile"));
                        customerLists.add(map);
                        // stockList.add(object.getString("customerName"));
                        customerList.add(object.getString("CustomerName"));// + " " + object.getString("CustomerCode"));
                    }
                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, customerList);
                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
                    spCusName.setAdapter(adapter);
                    hideDialog();

                    spCusName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spCusName.getSelectedItemId() > 0) {
                                Map<String, Object> selectPersonID = customerLists.get(spCusName.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPersonID.get("CustomerID").toString());
                                LoadPersonInfoByID(id);
                                sessionUserController=new SessionUserController();
                                List<SessionUser>sessionList=sessionUserController.GetAll();
                                int id1= Integer.parseInt(sessionList.get(0).getCompanyID());
                                LoadCusDueInfoByID(id1,id);
                            } else {
//                                mobile.setText("");
//                                address.setText("");
                                Toast.makeText(AppController.getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
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

    public void LoadCusDueInfoByID(long id, long Cid) {

        Log.e("Person", "URL_StockByProID" + AppConfig.URL_CusDueInfoByID + id + "/" + Cid);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_CusDueInfoByID + id +"/"+ Cid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    previousDue.setText(response.getString("Code"));
                } catch (JSONException e) {
                    // JSON error
                    //hideDialog();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response", "Response Error: " + error.getMessage());
                // hideDialog();
            }
        }) {

        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

//    public void loadSupplier() {
//        progressDialog.setMessage("Loading Supplier...");
//        showDialog();
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllSupplier, null, new Response.Listener<JSONArray>() {
//
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    Log.e("response", "" + response.toString());
//
//                    supplierList = new ArrayList<>();
//
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("SupplierID", "0");
//                    map.put("SupplierName", "--Select Supplier Name--");
//                    supplierList.add(map);
//
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        map.put("SupplierID", object.getString("SupplierID"));
//                        map.put("SupplierName", object.getString("SupplierName"));
//                        supplierList.add(map);
//                    }
//                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), supplierList, R.layout.spinner_text_colour,
//                            new String[]{"SupplierName"}, new int[]{android.R.id.text1});
//                    // arrayAdapter.setDropDownViewResource(R.layout.dropdownlist_style);
//                    spSupplier.setAdapter(arrayAdapter);
//
//                    hideDialog();
//                    spSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spSupplier.getSelectedItemId() > 0) {
//                                Map<String, Object> selectPersonID = supplierList.get(spSupplier.getSelectedItemPosition());
//                                int id = Integer.valueOf(selectPersonID.get("SupplierID").toString());
//                                //String id1 = String.valueOf(id);
//                                LoadSupplierInfoByID(id);
//                                sessionUserController=new SessionUserController();
//                                List<SessionUser>sessionList=sessionUserController.GetAll();
//                                int id1= Integer.parseInt(sessionList.get(0).getCompanyID());
//                                LoadSupDueInfoByID(id1,id);
//                            } else {
//                                //mobile.setText("");
//                                //address.setText("");
//                                Toast.makeText(AppController.getContext(), "Select Supplier", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> arg0) {
//                            // TODO Auto-generated method stub
//                        }
//
//                    });
//
//                } catch (JSONException e) {
//                    // JSON error
//                    hideDialog();
//                    e.printStackTrace();
//                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Supplier: " + error.getMessage());
//                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }

    public void addOnSupplier() {
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllSupplier, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    supplierLists = new ArrayList<>();
                    supplierList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("SupplierID", "0");
                    map.put("SupplierName", "Select Supplier");
                    supplierLists.add(map);
                    supplierList.add("Select Supplier");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        //Log.d(TAG, "Response: " + object.toString());
                        map.put("SupplierID", object.getString("SupplierID"));
                        //  map.put("CustomerName", object.getString("customerName"));
                        map.put("SupplierName", object.getString("SupplierName"));// + " " + object.getString("CustomerCode"));

                        supplierLists.add(map);
                        // stockList.add(object.getString("customerName"));
                        supplierList.add(object.getString("SupplierName"));// + " " + object.getString("CustomerCode"));
                    }
                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, supplierList);
                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
                    spSupplier.setAdapter(adapter);
                    hideDialog();

                    spSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spSupplier.getSelectedItemId() > 0) {
                                Map<String, Object> selectPersonID = supplierLists.get(spSupplier.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPersonID.get("SupplierID").toString());
                                LoadSupplierInfoByID(id);
                                sessionUserController=new SessionUserController();
                                List<SessionUser>sessionList=sessionUserController.GetAll();
                                int id1= Integer.parseInt(sessionList.get(0).getCompanyID());
                                LoadSupDueInfoByID(id1,id);
                            } else {
                                //mobile.setText("");
                                //address.setText("");
                                Toast.makeText(AppController.getContext(), "Select Supplier", Toast.LENGTH_SHORT).show();
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
                    hideDialog();
                    //Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(ContentValues.TAG, "Login Error: " + e.getMessage());
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

    public void LoadSupDueInfoByID(long id, long sid) {

        Log.e("Person", "URL_StockByProID" + AppConfig.URL_SupDueInfoByID + id + "/" + sid);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_SupDueInfoByID + id +"/"+ sid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    previousDue.setText(response.getString("Code"));
                } catch (JSONException e) {
                    // JSON error
                    //hideDialog();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response", "Response Error: " + error.getMessage());
                // hideDialog();
            }
        }) {

        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void LoadSupplierInfoByID(int id) {

        Log.e("Person", "URL_CustomerInfoByID" + AppConfig.URL_SupplierInfoByID + id);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_SupplierInfoByID + id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // String stock = String.valueOf(response.getInt(Integer.parseInt("Code")));
                    //String stock = response.getString("Code");
                    //Toast.makeText(Activity_Adjustment.this, "Stock"+stock, Toast.LENGTH_SHORT).show();
                    address.setText(response.getString("Address"));
                    MobileNumberSup = (response.getString("Mobile"));

                } catch (JSONException e) {
                    // JSON error
                    //hideDialog();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response", "Response Error: " + error.getMessage());
                // hideDialog();
            }
        }) {

        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void LoadPersonInfoByID(int id) {

        Log.e("Person", "URL_CustomerInfoByID" + AppConfig.URL_CustomerInfoByID + id);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_CustomerInfoByID + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // String stock = String.valueOf(response.getInt(Integer.parseInt("Code")));
                    //String stock = response.getString("Code");
                    //Toast.makeText(Activity_Adjustment.this, "Stock"+stock, Toast.LENGTH_SHORT).show();
                    address.setText(response.getString("Address"));
                    MobileNumberCus = (response.getString("Mobile"));
                    //previousDue.setText(response.getString(""));
                } catch (JSONException e) {
                    // JSON error
                    //hideDialog();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response", "Response Error: " + error.getMessage());
                // hideDialog();
            }
        }) {

        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void TextChangeForPrice() {
        previousDue.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((previousDue.getText().length() > 0) ? previousDue.getText().toString() : "0")) - (Double.parseDouble((OverLess.getText().length() > 0) ? OverLess.getText().toString() : "0"));
                PayAmount.setText(String.format("%.2f", x));
            }
        });

        OverLess.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((previousDue.getText().length() > 0) ? previousDue.getText().toString() : "0")) - (Double.parseDouble((OverLess.getText().length() > 0) ? OverLess.getText().toString() : "0"));
                PayAmount.setText(String.format("%.2f", x));
            }
        });

        PayAmount.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((PayAmount.getText().length() > 0) ? PayAmount.getText().toString() : "0") - Double.parseDouble((PaidAmount.getText().length() > 0) ? PaidAmount.getText().toString() : "0"));
                BlanceDue.setText(String.format("%.2f", x));
            }
        });

        PaidAmount.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((PayAmount.getText().length() > 0) ? PayAmount.getText().toString() : "0") - Double.parseDouble((PaidAmount.getText().length() > 0) ? PaidAmount.getText().toString() : "0"));
                BlanceDue.setText(String.format("%.2f", x));
            }
        });
    }

    public void TextChangeForPriceSup() {
        previousDue.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((previousDue.getText().length() > 0) ? previousDue.getText().toString() : "0"));
                PayAmount.setText(String.format("%.2f", x));
            }
        });
        PayAmount.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((PayAmount.getText().length() > 0) ? PayAmount.getText().toString() : "0") - Double.parseDouble((PaidAmount.getText().length() > 0) ? PaidAmount.getText().toString() : "0"));
                BlanceDue.setText(String.format("%.2f", x));
            }
        });

        PaidAmount.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((PayAmount.getText().length() > 0) ? PayAmount.getText().toString() : "0") - Double.parseDouble((PaidAmount.getText().length() > 0) ? PaidAmount.getText().toString() : "0"));
                BlanceDue.setText(String.format("%.2f", x));
            }
        });
    }

    public void BtnSave(){

        if(rbCustomer.isChecked())
        {
            if(spCusName.getSelectedItemPosition()==0)
            {
                Toast.makeText(AppController.getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(PaidAmount.length()>0)
                {
                    if(spPayType.getSelectedItemId()==1)
                    {
                        if(ChequeNo.getText().toString().length()!=0 && Mdate.getText().toString().length()!=0 && spAccountNo.getSelectedItemId()>0)
                        {
                            save();
                        }
                        else
                        {
                            if(ChequeNo.getText().toString().length()==0)
                            {
                                ChequeNo.requestFocus();
                                ChequeNo.setError("Please provide cheque no");
                            }
                            if(Mdate.getText().toString().length()==0)
                            {
                                Mdate.requestFocus();
                                Mdate.setError("Please select date");
                            }
                            if (spAccountNo.getSelectedItemId()==0)
                            {
                                Toast.makeText(getActivity().getApplicationContext(), "Select Account No", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (spPayType.getSelectedItemId()==0)
                    {
                        save();
                    }
                }
                else
                {
                    PaidAmount.requestFocus();
                    PaidAmount.setError("Please provide an amount");
                }
            }
        }
        else
        {
            if(spSupplier.getSelectedItemPosition()==0)
            {
                Toast.makeText(AppController.getContext(), "Select Supplier", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(PaidAmount.length()>0)
                {
                    if(spPayType.getSelectedItemId()==1)
                    {
                        if(ChequeNo.getText().toString().length()!=0 && Mdate.getText().toString().length()!=0 && spAccountNo.getSelectedItemId()>0)
                        {
                            save();
                        }
                        else
                        {
                            if(ChequeNo.getText().toString().length()==0)
                            {
                                ChequeNo.requestFocus();
                                ChequeNo.setError("Please provide cheque no");
                            }
                            if(Mdate.getText().toString().length()==0)
                            {
                                Mdate.requestFocus();
                                Mdate.setError("Please select date");
                            }
                            if (spAccountNo.getSelectedItemId()==0)
                            {
                                Toast.makeText(getActivity().getApplicationContext(), "Select Account No", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else  //(spPayType.getSelectedItemId()==0)
                    {
                        save();
                    }
                }
                else
                {
                    PaidAmount.requestFocus();
                    PaidAmount.setError("Please provide an amount");
                }
            }
        }


    }

    public void Clear() {
        address.setText("");
        spCusName.setSelection(0);
        spSupplier.setSelection(0);
        previousDue.setText("");
        OverLess.setText("");
        PayAmount.setText("");
        PaidAmount.setText("");
        BlanceDue.setText("");
        ChequeNo.setText("");
        Mdate.setText("");
        spBank.setSelection(0);
        spBrance.setSelection(0);
        spPayType.setSelection(0);
        spAccountNo.setSelection(0);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void save(){
        progressDialog.setMessage("Saving...");
        showDialog();

        JSONObject Obj = new JSONObject();
        try{
            sessionUserController=new SessionUserController();
            List<SessionUser>sessionList=sessionUserController.GetAll();
            //-----select data from spinner by id------//
            Map<String, Object> selectedItem = null;
            if (rbCustomer.isChecked()==true)
            {
                Obj.put("Type","1");
                selectedItem = customerLists.get(spCusName.getSelectedItemPosition());
                Obj.put("CustomerID", (String.valueOf(selectedItem.get("CustomerID"))));
                Obj.put("CustomerName", (String.valueOf(selectedItem.get("CustomerName"))));
                Obj.put("Mobile", MobileNumberCus);
            }
            else {
                Obj.put("Type","2");
                selectedItem = supplierLists.get(spSupplier.getSelectedItemPosition());
                Obj.put("SupplierID", (String.valueOf(selectedItem.get("SupplierID"))));
//                Obj.put("SupplierName", (String.valueOf(selectedItem.get("SupplierName"))));
//                Obj.put("Mobile ", MobileNumberSup);
            }
            Obj.put("CompanyID", sessionList.get(0).getCompanyID());
            if (spPayType.getSelectedItemId()==1)
            {
                Obj.put("PaymentTypeID", "2");
            }
            else {
                Obj.put("PaymentTypeID", "1");
            }
            Obj.put("Less", OverLess.getText().toString());
            Obj.put("Debit", PaidAmount.getText().toString());
            Obj.put("Creator", sessionList.get(0).getEmail());
            if(spPayType.getSelectedItemId()>0)
            {
                selectedItem = accounNoList.get(spAccountNo.getSelectedItemPosition());
                Obj.put("AccNo", (String.valueOf(selectedItem.get("AccountNo"))));
                selectedItem = bankList.get(spBank.getSelectedItemPosition());
                Obj.put("BankID", (String.valueOf(selectedItem.get("BankID"))));
                selectedItem = branchList.get(spBrance.getSelectedItemPosition());
                Obj.put("BranchID", (String.valueOf(selectedItem.get("BranchID"))));
                selectedItem = accounHeadList.get(spAccHead.getSelectedItemPosition());
                Obj.put("HeadID", (String.valueOf(selectedItem.get("AccHeadID"))));
            }
            else {
                Obj.put("AccNo", spAccountNo.getSelectedItemId());
                Obj.put("BankID", spBank.getSelectedItemId());
                Obj.put("BranchID", spBrance.getSelectedItemId());
                Obj.put("HeadID", spAccHead.getSelectedItemId());
            }
            Obj.put("ChequeNo", ChequeNo.getText().toString());
            Obj.put("MaturityDate", Mdate.getText().toString());
            Obj.put("Cheque_Amount", PaidAmount.getText().toString());

            JsonObjectRequest strReq1 = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_AddPaymentReceived, Obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(ContentValues.TAG, "response" + response.toString());
                    Toast.makeText(AppController.getContext(), "Saved.", Toast.LENGTH_SHORT).show();
                    Clear();
                    hideDialog();
//                    Intent intent = new Intent(BalanceTransper.this,MainActivity1.class);
//                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (rbCustomer.isChecked())
                    {
                        Toast.makeText(AppController.getContext(), "Payment receive successful", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AppController.getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                    }
                    Clear();
                    //Toast.makeText(AppController.getContext(), "Not Saved.", Toast.LENGTH_SHORT).show();
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
}


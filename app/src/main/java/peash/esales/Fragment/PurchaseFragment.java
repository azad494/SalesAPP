package peash.esales.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peash.esales.App.AppConfig;
import peash.esales.App.AppController;
import peash.esales.Controller.PurchaseController;
import peash.esales.Controller.PurchaseDetailsController;
import peash.esales.Controller.SessionUserController;
import peash.esales.Helper.Common;
import peash.esales.Models.Purchase;
import peash.esales.Models.PurchaseDetails;
import peash.esales.Models.SessionUser;
import Adi.esales.R;

import static java.util.Locale.US;
import static peash.esales.App.AppController.TAG;

public class PurchaseFragment extends ListFragment {

    ProgressDialog progressDialog;

    private Spinner spSupplier, spProduct, spPaymentType, spWareHouse, spBank, spBrance, spAccountNo,spAccHead;
    private EditText date1, mobile, address, stock, price, qut, ProductCode, disTk, total, previousDue, txtInvoice,
            OverLess, PayAmount, PaidAmount, BlanceDue, ChequeNo, Mdate;
    private List<Map<String, Object>> paymentTypeLists = new ArrayList<>();
    private List<Map<String, Object>> accounNoList = new ArrayList<>();
    private List<Map<String, Object>> bankList = new ArrayList<>();
    private List<Map<String, Object>> branchList = new ArrayList<>();
    private List<Map<String, Object>> accounHeadList = new ArrayList<>();
    private List<Map<String, Object>> supplierLists = new ArrayList<>();
    List<String> supplierList;
    private List<Map<String, Object>> productLists = new ArrayList<>();
    List<String> productList;
    private List<Map<String, Object>> productList1 = new ArrayList<>();
    private List<Map<String, Object>> wearhouseList = new ArrayList<>();
    private String Mobile = "", Code = "";
    private double t = 0, gt = 0;
    TextView txtID;
    String product, msg;
    String listID;
    private int Stock;
    private PurchaseDetails purchaseDetailsModel;
    private SessionUserController sessionUserController;
    private PurchaseDetailsController purchaseDetailsController;
    private Button btnAddList, btnCancel, btnReset, btnSave;
    private TextView txtSLID;
    Calendar myCalendar = Calendar.getInstance();
    private int sum;
    private MediaPlayer pCompleted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.purchase_fragment, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);


        date1 = v.findViewById(R.id.Date1);
        mobile = v.findViewById(R.id.txtMobile);
        address = v.findViewById(R.id.txtCusAddress);
        stock = v.findViewById(R.id.txtStock);
        price = v.findViewById(R.id.txtCostPrice);
        qut = v.findViewById(R.id.txtQuantity);
        ProductCode = v.findViewById(R.id.txtProductCode);
        disTk = v.findViewById(R.id.txtDiscountTk);
        total = v.findViewById(R.id.txtTotal);
        previousDue = v.findViewById(R.id.txtPreviousDue);
        OverLess = v.findViewById(R.id.txtOverLess);
        PayAmount = v.findViewById(R.id.txtPayable);
        PaidAmount = v.findViewById(R.id.txtPaidAmount);
        BlanceDue = v.findViewById(R.id.txtDueAmount);
        ChequeNo = v.findViewById(R.id.txtCheque);
        Mdate = v.findViewById(R.id.txtMDate);
        txtInvoice = v.findViewById(R.id.txtInvoice);


        spSupplier = v.findViewById(R.id.spSupplier);
        spProduct = v.findViewById(R.id.spProduct);
        spWareHouse = v.findViewById(R.id.spWareHouse);
        spPaymentType = v.findViewById(R.id.spPaymentType);
        spBank = v.findViewById(R.id.spBank);
        spBrance = v.findViewById(R.id.spBrance);
        spAccountNo = v.findViewById(R.id.spAccountNo);
        spAccHead = v.findViewById(R.id.spAccHead);

        btnAddList = v.findViewById(R.id.btnAdd);
        btnReset = v.findViewById(R.id.btnReset);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);

        //Helper_Methods();
        purchaseDetailsController = new PurchaseDetailsController();
        purchaseDetailsController.DeleteAll();
        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToList();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteListData();
                Clear();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SavePurchase();
                SaveFinally();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearAll();
            }
        });

        LoadDate();
        loadAccHead();
        addOnSupplier();
        addOnProduct();
        LoadPaymentTyps();
        loadWarehouse();
        TextChangeForPrice();
        return v;
    }

    private void LoadDate() {

        String mydate = java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        date1.setText(mydate);
        setPur_date();

        //Default date for Maturity date
        String maturityDate = java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        Mdate.setText(maturityDate);
        setMaturity_date();

        final DatePickerDialog.OnDateSetListener dat = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setPur_date();
            }
        };

        final DatePickerDialog.OnDateSetListener datem = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setMaturity_date();
            }
        };
        //on Change of Purchase date
        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dat, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //on Change of Maturity Date
        Mdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), datem, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void setPur_date() {

        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, US);
        date1.setText(sdf.format(myCalendar.getTime()));
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
//                    map.put("SupplierName", "Select Supplier");
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
        progressDialog.setMessage("Loding ...");
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
                    mobile.setText(response.getString("Mobile"));
                    address.setText(response.getString("Address"));


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

    public void LoadProductDetailsByID(int id) {

        Log.e("Person", "URL_ProductDetails" + AppConfig.URL_ProductDetails + id);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_ProductDetails + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(AppController.TAG, " Product Info response: " + response.toString());
                try {
                    price.setText(response.getString("RatePerUnit"));
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
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void LoadProductDetailsByID1(int id) {

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_ProductDetails + id, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        price.setText(object.getString("RatePerUnit"));
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Product: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
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
            spPaymentType.setAdapter(arrayAdapter);


            spPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    Log.e(TAG, "spPayType" + spPaymentType.getSelectedItemId());

                    if (spPaymentType.getSelectedItemId() == 0) {
                        ChequeNo.setVisibility(View.GONE);
                        spBank.setVisibility(View.GONE);
                        spBrance.setVisibility(View.GONE);
                        Mdate.setVisibility(View.GONE);
                        spAccountNo.setVisibility(View.GONE);
                        spAccHead.setVisibility(View.GONE);
                    }
//                    else if (spPaymentType.getSelectedItemId() == 1) {
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
                        loadAccNo();
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

//    public void loadProduct() {
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllProduct, null, new Response.Listener<JSONArray>() {
//
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    Log.e("response", "" + response.toString());
//
//                    productList = new ArrayList<>();
//
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("ProductID", "0");
//                    map.put("ProductName", "Select Product");
//                    productList.add(map);
//
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        map.put("ProductID", object.getString("ProductID"));
//                        map.put("ProductName", object.getString("ProductName"));
//                        productList.add(map);
//                    }
//                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), productList, R.layout.spinner_text_colour,
//                            new String[]{"ProductName"}, new int[]{android.R.id.text1});
//                    spProduct.setAdapter(arrayAdapter);
//
//                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spProduct.getSelectedItemId() > 0) {
//                                Map<String, Object> selectPruductID = productList.get(spProduct.getSelectedItemPosition());
//                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//                                //String id1 = String.valueOf(id);
//                                //LoadWhouseByCmpIdAndProID(id);
//                                LoadProductDetailsByID1(id);
//                                //LoadStockByPersonID(id);
//                                //LoadCustomerType();
//                                //price.setText("");
//                            } else {
//                                Toast.makeText(AppController.getContext(), "Select Product", Toast.LENGTH_SHORT).show();
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
//                    e.printStackTrace();
//                    // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Product: " + error.getMessage());
//                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }

    public void addOnProduct() {
        progressDialog.setMessage("Loding ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllProduct, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    productLists = new ArrayList<>();
                    productList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("ProductID", "0");
                    map.put("ProductName", "Select Product");
                    //map.put("CustomerCode", "Customer Code");
                    productLists.add(map);
                    productList.add("Select Product");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        //Log.d(TAG, "Response: " + object.toString());
                        map.put("ProductID", object.getString("ProductID"));
                        //  map.put("CustomerName", object.getString("customerName"));
                        map.put("ProductName", object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
                        //map.put("CustomerCode", object.getString("CustomerCode"));
                        productLists.add(map);
                        // stockList.add(object.getString("customerName"));
                        productList.add(object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
                    }
                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, productList);
                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
                    spProduct.setAdapter(adapter);
                    hideDialog();
                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spProduct.getSelectedItemId() > 0) {
                                Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
                                //String id1 = String.valueOf(id);
                                //LoadWhouseByCmpIdAndProID(id);
                                LoadProductDetailsByID1(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                //price.setText("");
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Product", Toast.LENGTH_SHORT).show();
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
    public void loadWarehouse() {
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_Warehouse, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    wearhouseList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("WarehouseID", "0");
                    map.put("Name", "Select Warehouse");
                    wearhouseList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("WarehouseID", object.getString("WarehouseID"));
                        map.put("Name", object.getString("Name"));
                        wearhouseList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), wearhouseList, R.layout.spinner_text_colour,
                            new String[]{"Name"}, new int[]{android.R.id.text1});
                    spWareHouse.setAdapter(arrayAdapter);

                    spWareHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spWareHouse.getSelectedItemId() > 0) {
                                Map<String, Object> selectPruductID = wearhouseList.get(spWareHouse.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPruductID.get("WarehouseID").toString());

                                Map<String, Object> selectProductID = productLists.get(spProduct.getSelectedItemPosition());
                                int idP = Integer.valueOf(selectProductID.get("ProductID").toString());

                                LoadStockByPersonID(idP,id);
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Product", Toast.LENGTH_SHORT).show();
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

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Product: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void LoadStockByPersonID(long id, long Wid) {

        Log.e("Person", "URL_StockByProID" + AppConfig.URL_StockByProID + id + "/" + Wid);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_StockByProID + id +"/"+ Wid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    // String stock = String.valueOf(response.getInt(Integer.parseInt("Code")));
                    //String stock = response.getString("Code");
                    //Toast.makeText(Activity_Adjustment.this, "Stock"+stock, Toast.LENGTH_SHORT).show();
                    stock.setText(response.getString("Code"));
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
        total.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((total.getText().length() > 0) ? total.getText().toString() : "0") + Double.parseDouble((previousDue.getText().length() > 0) ? previousDue.getText().toString() : "0")) - (Double.parseDouble((OverLess.getText().length() > 0) ? OverLess.getText().toString() : "0"));
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
                double x = (Double.parseDouble((total.getText().length() > 0) ? total.getText().toString() : "0") + Double.parseDouble((previousDue.getText().length() > 0) ? previousDue.getText().toString() : "0")) - (Double.parseDouble((OverLess.getText().length() > 0) ? OverLess.getText().toString() : "0"));
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

    public void AddToList() {
        purchaseDetailsController = new PurchaseDetailsController();
        purchaseDetailsModel = new PurchaseDetails();
        //String aa=btnAddList.getText().toString();
        if (btnAddList.length() == 3) {
            if (spProduct.getSelectedItemPosition() == 0 || spWareHouse.getSelectedItemPosition() == 0 || spSupplier.getSelectedItemPosition() == 0) {
                if (spSupplier.getSelectedItemPosition() == 0) {
                    Toast.makeText(AppController.getContext(), "Select Supplier", Toast.LENGTH_SHORT).show();
                } else if (spProduct.getSelectedItemPosition() == 0) {
                    Toast.makeText(AppController.getContext(), "Select Product", Toast.LENGTH_SHORT).show();
                } else if (spWareHouse.getSelectedItemPosition() == 0) {
                    Toast.makeText(AppController.getContext(), "Select Warehouse", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (price.length() > 0 && qut.length() > 0) {
                    //-----select data from spinner by id------//
                    Map<String, Object> selectedItem = null;
                    selectedItem = productLists.get(spProduct.getSelectedItemPosition());
                    purchaseDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                    //--------------^^^^^^-------------//
                    purchaseDetailsModel.setProductID(String.valueOf(selectedItem.get("ProductID")));
                    selectedItem = wearhouseList.get(spWareHouse.getSelectedItemPosition());
                    purchaseDetailsModel.setWarehouseID(String.valueOf(selectedItem.get("WarehouseID")));

                    //double t = Double.parseDouble((price.getText().length() > 0) ? price.getText().toString() : "0") * Double.parseDouble((qut.getText().length() > 0) ? qut.getText().toString() : "0" );
                    //total.setText(String.valueOf(t));

                    double a = Double.parseDouble(price.getText().toString());
                    double b = Double.parseDouble(qut.getText().toString());

                    if (total.length() > 0) {
                        gt = Double.parseDouble(total.getText().toString());
                    } else {
                        gt = 0;
                    }
                    t = a * b;
                    gt = gt + t;

                    purchaseDetailsModel.setQuantity(qut.getText().toString());
                    purchaseDetailsModel.setPrice(price.getText().toString());
                    purchaseDetailsModel.setTotal(String.valueOf(t));
                    total.setText(String.format("%.2f", gt));

                    int id = purchaseDetailsController.Insert(purchaseDetailsModel);
                    if (id > 0) {
                        Toast.makeText(AppController.getContext(), "Information saved successfully.", Toast.LENGTH_LONG).show();
//            Intent objIntent = new Intent(getApplicationContext(), ItemCardList.class);
//            objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(objIntent);
                        ShowList();
                        Clear();
                    } else {
                        Toast.makeText(AppController.getContext(), "Information save fail.", Toast.LENGTH_LONG).show();
                    }
                    //salesDetailsController.getAll();
                } else {
                    if (price.getText().toString().length() == 0) {
                        price.requestFocus();
                        price.setError("Please provide an amount");
                    } else {
                        qut.requestFocus();
                        qut.setError("Please provide an quantity");
                    }
                }
            }
        } else {
            if (spProduct.getSelectedItemPosition() == 0 || spWareHouse.getSelectedItemPosition() == 0) {
                if (spProduct.getSelectedItemPosition() == 0) {
                    Toast.makeText(AppController.getContext(), "Select Product", Toast.LENGTH_SHORT).show();
                } else if (spWareHouse.getSelectedItemPosition() == 0) {
                    Toast.makeText(AppController.getContext(), "Select Warehouse", Toast.LENGTH_SHORT).show();
                }
            } else {
                //-----select data from spinner by id------//
                Map<String, Object> selectedItem = null;
                selectedItem = productLists.get(spProduct.getSelectedItemPosition());
                purchaseDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                purchaseDetailsModel.setProductID(String.valueOf(selectedItem.get("ProductID")));
                //--------------^^^^^^-------------//
                //purchaseDetailsModel.setProductID(String.valueOf(spProduct.getSelectedItemId()));
                //Map<String, Object> selectedItem = null;
                selectedItem = wearhouseList.get(spWareHouse.getSelectedItemPosition());
                purchaseDetailsModel.setWarehouseID(String.valueOf(selectedItem.get("WarehouseID")));
                //purchaseDetailsModel.setWarehouseID(String.valueOf(spWareHouse.getSelectedItemId()));

                double a = Double.parseDouble(price.getText().toString());
                double b = Double.parseDouble(qut.getText().toString());

                if (total.length() > 0) {
                    gt = Double.parseDouble(total.getText().toString());
                } else {
                    gt = 0;
                }
                t = a * b;
                gt = gt + t;


                purchaseDetailsModel.setSLID(listID);
                //country.setCountryName(cntName.getText().toString());
                purchaseDetailsModel.setQuantity(qut.getText().toString());
                purchaseDetailsModel.setPrice(price.getText().toString());
                purchaseDetailsModel.setTotal(String.valueOf(t));
                total.setText(String.format("%.2f", gt));

                int id = purchaseDetailsController.Update(purchaseDetailsModel);
                if (id > 0) {
                    Toast.makeText(AppController.getContext(), "Information Update successfully.", Toast.LENGTH_LONG).show();
//            Intent objIntent = new Intent(getApplicationContext(), ItemCardList.class);
//            objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(objIntent);
                    btnAddList.setText("Add");
                    btnReset.setText("Reset");
                    ShowList();
                    Clear();
                } else {
                    Toast.makeText(AppController.getContext(), "Information Update fail.", Toast.LENGTH_LONG).show();
                }
                //salesDetailsController.getAll();
            }
        }
    }
    public void ShowList() {

        purchaseDetailsController = new PurchaseDetailsController();

        ArrayList<HashMap<String, String>> InformationLists = purchaseDetailsController.getAllList();
        Log.d("InformationLists:", InformationLists.toString());

        if (InformationLists.size() != 0) {

            ListView lv = getListView();
//            for (int i=0; i<=InformationLists.size();i++)
//            {
//
//            }
            if (btnReset.length() < 6 && btnAddList.length() < 6) {
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        txtID = view.findViewById(R.id.txtSLID);
                        int selectedID = Integer.parseInt(txtID.getText().toString());
                        listID = String.valueOf(selectedID);
                        Toast.makeText(AppController.getContext(), "selectedID" + selectedID, Toast.LENGTH_SHORT).show();

                        double b = 0, c = 0;

                        purchaseDetailsController = new PurchaseDetailsController();
                        List<PurchaseDetails> purchaseDetails = purchaseDetailsController.GetByID(selectedID);
                        Log.d("ww: ", "Retriving Info..");
                        if (purchaseDetails.size() != 0) {
                            b = Double.parseDouble(purchaseDetails.get(0).getTotal());
                            qut.setText(purchaseDetails.get(0).getQuantity());
                            price.setText(purchaseDetails.get(0).getPrice());
                            spWareHouse.setSelection(Integer.parseInt((purchaseDetails.get(0).getWarehouseID())));
                            spProduct.setSelection(Integer.parseInt((purchaseDetails.get(0).getProductID())));

                            //c=Double.parseDouble(salesList.get(0).getDiscount());

                            //dis.setText(String.valueOf(c/(b/100)));

                            //dis.setText(String.format("%.0f",c/(b/100)));

                            //disTk.setText(salesDetails.get(0).getDiscount());

                            btnAddList.setText("Update");
                            btnReset.setText("Delete");
                            product = purchaseDetails.get(0).getProductName();
                            //btnAddList.setBackgroundColor(000);
                        }
                        //double a,b,c;
                        double a = Double.parseDouble(total.getText().toString());
                        if (a == 0) {
                            total.setText("0");
                        } else {
                            total.setText(String.valueOf(a - b));
                        }
                        //Delete(view);
                        ShowList();
                        //String valId = txtSLID.getText().toString();
                        //Intent objIntent = new Intent(getApplicationContext(), InformationUpdate.class);
                        //objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //objIntent.putExtra("slid", valId);
                        //startActivity(objIntent);
                    }
                });
                lv.setEnabled(true);
            } else {
                lv.setEnabled(false);
            }

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.purchase_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "WarehouseID", "Price", "Quantity", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
            //((SimpleAdapter) adapter).setDropDownViewResource(R.layout.spinner_text_colour);
            setListAdapter(adapter);
        } else {
            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.purchase_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "WarehouseID", "Price", "Quantity", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
            setListAdapter(adapter);
        }
    }
    public void SaveFinally() {

        //Create Main jSon object
        if (spSupplier.getSelectedItemId()!=0 && total.getText().toString().length()!=0)
        {
            if(spPaymentType.getSelectedItemId()==1)
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
            if (spSupplier.getSelectedItemId()==0)
            {
                Toast.makeText(getContext(), "Select Supplier", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "There has no product for purchase", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void save(){
        sessionUserController = new SessionUserController();
        List<SessionUser> sessionList = sessionUserController.GetAll();
        JSONObject Obj = new JSONObject();
        progressDialog.setMessage("Processing ...");
        showDialog();
        try {
            Obj.put("PurchaseID", 0);
            //-----select data from spinner by id------//
            Map<String, Object> selectedItem = null;
            selectedItem = supplierLists.get(spSupplier.getSelectedItemPosition());
            //purchaseDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
            Obj.put("SupplierID", (String.valueOf(selectedItem.get("SupplierID"))));
            Obj.put("CompanyID", sessionList.get(0).getCompanyID());
            Obj.put("PurchaserID", 0);
            Obj.put("InvoiceNo", txtInvoice.getText().toString());
            Obj.put("InvoiceDate", date1.getText().toString());
            Obj.put("Job_By", sessionList.get(0).getEmail());
            Obj.put("Purchase_Date", date1.getText().toString());
            Obj.put("Sys_Purchase_Date", date1.getText().toString());
            Obj.put("Note", "");
            Obj.put("Sys_PaymentID", 0);
            Obj.put("User_PaymentID", 0);
            Obj.put("Sys_Payment_Date", date1.getText().toString());
            Obj.put("Payment_Date", date1.getText().toString());
            selectedItem = supplierLists.get(spSupplier.getSelectedItemPosition());
            Obj.put("CustomerID", (String.valueOf(selectedItem.get("SupplierID"))));
            Obj.put("DrAmount", total.getText().toString());
            Obj.put("PayableAmount", PayAmount.getText().toString());
            Obj.put("PreviousDue", previousDue.getText().toString());
            Obj.put("CrAmount", PaidAmount.getText().toString());
            if (spPaymentType.getSelectedItemId() == 1) {
                Obj.put("InvoiceType", "Cheque");
            }
            else {
                Obj.put("InvoiceType", "");
            }
            Obj.put("ChequePayID",0);
            Obj.put("Issue_Date",date1.getText().toString());
            Obj.put("TrnID",txtInvoice.getText().toString());
            if(spPaymentType.getSelectedItemId()>0)
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
                Obj.put("AccountNo", spAccountNo.getSelectedItemId());
                Obj.put("BankID", spBank.getSelectedItemId());
                Obj.put("BranchID", spBrance.getSelectedItemId());
                Obj.put("HeadID", spAccHead.getSelectedItemId());
            }
            Obj.put("ChequeNo", ChequeNo.getText().toString());
            Obj.put("Cheque_Amount", PaidAmount.getText().toString());
            Obj.put("IsVoid", "no");
            Obj.put("MaturityDate", Mdate.getText().toString());
            Obj.put("Status", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Create json array for filter
        JSONArray array = new JSONArray();

        //Create json objects for two filter Ids

        int rows = 0;
        purchaseDetailsController = new PurchaseDetailsController();
        Cursor cursor = purchaseDetailsController.getData();

        cursor.moveToFirst();
        int a = cursor.getCount();

        while (rows < a) {
            JSONObject Param1 = new JSONObject();
            try {
                Param1.put("PurchaseDetailID", 0);
                Param1.put("PurchaseID", 0);
                Param1.put("ProductID", cursor.getInt(cursor.getColumnIndexOrThrow("ProductID")));
                Param1.put("Quantity", cursor.getInt(cursor.getColumnIndexOrThrow("Quantity")));
                Param1.put("Rate", cursor.getInt(cursor.getColumnIndexOrThrow("Price")));
                Param1.put("WarehouseID", cursor.getInt(cursor.getColumnIndexOrThrow("WarehouseID")));
                //Param1.put("ProductCode", cursor.getInt(cursor.getColumnIndexOrThrow("ProductCode")));
                //Param1.put("total", cursor.getInt(cursor.getColumnIndexOrThrow("Total")));
                array.put(Param1);
                a--;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cursor.moveToNext();
        }

        //Add the filter Id object to array
//        array.put(Param1);
//        array.put(Param2);

        //Add array to main json object
        try {
            Obj.put("PurchaseDetails", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_Purchase, Obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(ContentValues.TAG, "response" + response.toString());
                Toast.makeText(AppController.getContext(), "Purchase Successful.", Toast.LENGTH_SHORT).show();
                hideDialog();
                ClearAll();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AppController.getContext(), "Purchase Fall.", Toast.LENGTH_SHORT).show();
                hideDialog();
                Log.e(ContentValues.TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
        //ClearAll();
    }

    public void DeleteListData() {
        if (btnReset.length() == 6) {
            int selectedID = Integer.parseInt(txtID.getText().toString());
            purchaseDetailsController = new PurchaseDetailsController();
            purchaseDetailsController.Delete(selectedID);
            ArrayList<HashMap<String, String>> InformationLists = purchaseDetailsController.getAllList();
            Log.d("InformationLists:", InformationLists.toString());
            if (InformationLists.size() != 0) {
                ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.purchase_item_view, new
                        String[]{"SLID", "ProductName", "ProductID", "WarehouseID", "Price", "Quantity", "Total"},
                        new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
                setListAdapter(adapter);
            } else {
                ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.purchase_item_view, new
                        String[]{"SLID", "ProductName", "ProductID", "WarehouseID", "Price", "Quantity", "Total"},
                        new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
                setListAdapter(adapter);
            }
            btnReset.setText("Reset");
            btnAddList.setText("Add");
            Clear();
            ListView lv = getListView();
            lv.setEnabled(true);
        } else {

        }
    }

    public void Clear() {
        price.setText("");
        qut.setText("");
        stock.setText("");
        price.setText("");
        spWareHouse.setSelection(0);
        spProduct.setSelection(0);
        //mobile.setText("");
        //address.setText("");
    }
    public void ClearAll() {
        mobile.setText("");
        address.setText("");
        total.setText("");
        previousDue.setText("");
        OverLess.setText("");
        PayAmount.setText("");
        PaidAmount.setText("");
        BlanceDue.setText("");
        ChequeNo.setText("");
        price.setText("");
        qut.setText("");
        stock.setText("");
        price.setText("");
        spPaymentType.setSelection(0);
        spSupplier.setSelection(0);
        spWareHouse.setSelection(0);
        spProduct.setSelection(0);
        spBank.setSelection(0);
        spBrance.setSelection(0);
        spAccountNo.setSelection(0);
        purchaseDetailsController = new PurchaseDetailsController();
        purchaseDetailsController.DeleteAll();
        ShowList();
        LoadDate();
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

//    public void Delete(String  valid ) {
//
//        AlertDialog diaBox = AskOption(valid);
//        diaBox.show();
//    }

//    private AlertDialog AskOption(final String delId ) {
//        AlertDialog altbox = new AlertDialog.Builder(getActivity())
//                .setTitle("Remove Item!!!")
//                .setMessage("Are you want to delete this item?")
//                .setIcon(R.drawable.d)
//                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                        //purchaseController = new PurchaseController();
//                        //purchaseController.Delete(delId);
//                        dialog.dismiss();
//                        //ItemLists();
//                        //purchaseController=new PurchaseController();
//                        //ArrayList<HashMap<String, String>> itemList =  purchaseController.getAll();
//                        int total=0;
//                        for (int i = 0; i < itemList.size(); i++) {
//                            // sum=sum+Integer.valueOf(itemList.get(0).get("Total"));
//                            total = total + Integer.valueOf(itemList.get(0).get("Total"));
//                        }
//                        //txtTotal.setText(String.valueOf(total));
//                    }
//                })
//
//                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//
//
//                    }
//                })
//                .create();
//        return altbox;
//    }

}


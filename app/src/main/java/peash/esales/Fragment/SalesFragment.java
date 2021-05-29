package peash.esales.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import peash.esales.Controller.SalesDetailsController;
import peash.esales.Controller.SessionUserController;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.SalesDetails;
import peash.esales.Models.SessionUser;
import Adi.esales.R;

import static java.util.Locale.US;
import static peash.esales.App.AppController.TAG;

public class SalesFragment extends ListFragment {

    Calendar myCalendar = Calendar.getInstance();

    ProgressDialog progressDialog;
    private SessionManager session;
    private SalesDetails salesDetailsModel;
    private SessionUserController sessionUserController;
    private SalesDetailsController salesDetailsController;
    EditText date1, mobile, address, stock, price, qut, dis, disTk, total, previousDue,
            OverLess, PayAmount, PaidAmount, BlanceDue, ChequeNo, Mdate,newCus,txtShippingCost,
            txtHPrice;
    Spinner spCusName, spProduct, spWareHouse, spPriceType, spPayType, spBank, spBrance, spAccountNo,spModel,spPin,spMetter,spAccHead;
    Button btnAddList, btnReset, btnSave, btnCancel,btnNewCust,btnAdd,btnRemove;
    TextView txtID;
    ImageView NewCus,IVcanCus;
    double ListTotal=0;

    private double t = 0, gt = 0;
    String product;
    String listID,s;
    byte[] strtext;
    private ImageView iv;
    int clickcount = 0, checkWarehouseID=0;


    private List<Map<String, Object>> paymentTypeLists = new ArrayList<>();
    private List<Map<String, Object>> accounNoList = new ArrayList<>();
    private List<Map<String, Object>> accounHeadList = new ArrayList<>();
    private List<Map<String, Object>> bankList = new ArrayList<>();
    private List<Map<String, Object>> branchList = new ArrayList<>();
    private List<Map<String, Object>> customerLists = new ArrayList<>();
    List<String> customerList;
    private List<Map<String, Object>> customerTypeList = new ArrayList<>();
    private List<Map<String, Object>> productLists = new ArrayList<>();
    List<String> productList;
    private List<Map<String, Object>> modelLists = new ArrayList<>();
    List<String> modelList;
    private List<Map<String, Object>> pinLists = new ArrayList<>();
    List<String> pinList;
    private List<Map<String, Object>> metterists = new ArrayList<>();
    List<String> metterList;
    private List<Map<String, Object>> wearhouseList = new ArrayList<>();
    List<Map<String, String>> salesList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sales_fragment, container, false);
        //------------GetByteArray from other Fragment--------//
        strtext = getArguments().getByteArray("byteArray");
        s = getArguments().getString("temp");
        //-------------Convert Byte Array to String------//
        String temp= Base64.encodeToString(strtext, Base64.DEFAULT);
        //---------------------------------------------------//

//..................................... Pull refresh..............................................

        swipeContainer=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                addOnCustomer();
                addOnModel();
                LoadPaymentTyps();

                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeContainer.setRefreshing(false);
                    }
                }, 1000); // Delay in millis
            }
        });

        // Scheme colors for animation

        swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
//............................. pull refresh end...................................................





//        Bitmap.Config configBmp = Bitmap.Config.valueOf(b.getConfig().name());
//        Bitmap bitmap_tmp = Bitmap.createBitmap(width, height, configBmp);
//        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
//        bitmap_tmp.copyPixelsFromBuffer(buffer);
//        b=bitmap_tmp;

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        date1 = v.findViewById(R.id.Date1);
        newCus = v.findViewById(R.id.txtNewCus);
        mobile = v.findViewById(R.id.txtMobile);
        address = v.findViewById(R.id.txtCusAddress);
        stock = v.findViewById(R.id.txtStock);
        price = v.findViewById(R.id.txtPrice);
        qut = v.findViewById(R.id.txtQuantity);
        dis = v.findViewById(R.id.txtDiscount);
        disTk = v.findViewById(R.id.txtDiscountTk);
        total = v.findViewById(R.id.txtTotal);
        previousDue = v.findViewById(R.id.txtPreviousDue);
        OverLess = v.findViewById(R.id.txtOverLess);
        PayAmount = v.findViewById(R.id.txtPayable);
        PaidAmount = v.findViewById(R.id.txtPaidAmount);
        BlanceDue = v.findViewById(R.id.txtDueAmount);
        ChequeNo = v.findViewById(R.id.txtCheque);
        Mdate = v.findViewById(R.id.txtMDate);
        txtShippingCost= v.findViewById(R.id.txtShippingCost);
//        NewCus = v.findViewById(R.id.IVnewCus);
//        IVcanCus = v.findViewById(R.id.IVcanCus);

//        NewCus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                newCus.setVisibility(View.VISIBLE);
//                NewCus.setVisibility(View.GONE);
//                IVcanCus.setVisibility(View.VISIBLE);
//                spCusName.setEnabled(false);
//            }
//        });
//
//        IVcanCus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                newCus.setVisibility(View.GONE);
//                NewCus.setVisibility(View.VISIBLE);
//                IVcanCus.setVisibility(View.GONE);
//                spCusName.setEnabled(true);
//            }
//        });

        iv = v.findViewById(R.id.iv);
        //---------Convert String to Bitmap-------------------//
        byte [] encodeByte=Base64.decode(temp,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        //---------------------------------------------------//
        iv.setImageBitmap(bitmap);


        spCusName = v.findViewById(R.id.spCustomerName);
        spProduct = v.findViewById(R.id.spProduct);
        spWareHouse = v.findViewById(R.id.spWareHouse);
        spPriceType = v.findViewById(R.id.spPriceType);
        spPayType = v.findViewById(R.id.spPaymentType);
        spBank = v.findViewById(R.id.spBank);
        spBrance = v.findViewById(R.id.spBrance);
        spAccountNo = v.findViewById(R.id.spAccountNo);
        spModel = v.findViewById(R.id.spModel);
        spPin = v.findViewById(R.id.spPin);
        spMetter = v.findViewById(R.id.spMetter);
        spAccHead = v.findViewById(R.id.spAccHead);

        btnAddList = v.findViewById(R.id.btnAdd);
        btnReset = v.findViewById(R.id.btnReset);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnNewCust = v.findViewById(R.id.btnNew);
        btnAdd = v.findViewById(R.id.btnAddQ);
        btnRemove = v.findViewById(R.id.btnRemove);

        btnNewCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder diaBox = AskOption();
                diaBox.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(qut.getText().toString());
                int b=0;
                if (stock.getText().length() > 0)
                {
                    b = Integer.parseInt(stock.getText().toString());
                }

                if(a<b)
                {
                    a= a+1;
                    qut.setText(Integer.toString(a));
                }
                else
                {
                    qut.setText(Integer.toString(a));
                }
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(qut.getText().toString());
                if(a==1)
                {
                    qut.setText("1");
                }
                else
                {
                    a= a-1;
                    qut.setText(Integer.toString(a));
                }
            }
        });
        //ShowList();
//        salesDetailsController = new SalesDetailsController();
//        salesDetailsController.DeleteAll();
        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        AddToList();
                        TextChangeForPrice();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveFinally();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteListData();
                Clear();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearAll();
            }
        });

        //-------Call Functions-----
        LoadDate();
        //loadCustomer();
        //----------------
        int rows = 0;
        salesDetailsController = new SalesDetailsController();
        Cursor cursor = salesDetailsController.getData();
        cursor.moveToFirst();
        int aa=cursor.getCount();
        //float ListTotal=0;
        while (rows < aa) {
            double tot = cursor.getInt(cursor.getColumnIndexOrThrow("Total"));
            ListTotal =ListTotal + tot;
            aa--;
            cursor.moveToNext();
        }
        //----------------
        addOnCustomer();
        addOnModel();
        //addOnProduct();
        LoadCustomerType();
        LoadPaymentTyps();
        loadAccHead();
        TextChangeForPrice();
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
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
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
    public void LoadPersonInfoByID(int id) {

        Log.e("Person", "URL_CustomerInfoByID" + AppConfig.URL_CustomerInfoByID + id);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_CustomerInfoByID + id, null, new Response.Listener<JSONObject>() {
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
    public void LoadCustomerType() {
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
                    spPriceType.setAdapter(arrayAdapter);

//                    if(spProduct.getSelectedItemId()!=0)
//                    {
//                        Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                        int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//                        LoadProductInfoByID(id);
//                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
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

//    public void loadProduct() {
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllProductForSales, null, new Response.Listener<JSONArray>() {
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
//                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), productLists, R.layout.spinner_text_colour,
//                            new String[]{"ProductName"}, new int[]{android.R.id.text1});
//                    spProduct.setAdapter(arrayAdapter);
//
//                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spProduct.getSelectedItemId() > 0) {
//                                Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                LoadWhouseByCmpIdAndProID(id);
//                                //LoadStockByPersonID(id);
//                                LoadCustomerType();
//                                price.setText("");
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
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_AllProductForSales, null, new Response.Listener<JSONArray>() {
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

                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
                                LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                LoadWhouseByCmpIdAndProID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                price.setText("");
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

    public void addOnModel() {
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_ProductModel, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    modelLists = new ArrayList<>();
                    modelList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("GenericID", "0");
                    map.put("GenericName", "Select Model");
                    //map.put("CustomerCode", "Customer Code");
                    modelLists.add(map);
                    modelList.add("Select Model");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        //Log.d(TAG, "Response: " + object.toString());
                        map.put("GenericID", object.getString("GenericID"));
                        map.put("GenericName", object.getString("GenericName"));
                        //map.put("ProductName", object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
                        //map.put("CustomerCode", object.getString("CustomerCode"));
                        modelLists.add(map);
                        // stockList.add(object.getString("customerName"));
                        modelList.add(object.getString("GenericName"));// + " " + object.getString("CustomerCode"));
                    }
                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, modelList);
                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
                    spModel.setAdapter(adapter);
                    hideDialog();
                    spModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spModel.getSelectedItemId() > 0) {
                                //String Model = spModel.getSelectedItem().toString();
                                //Map<String, Object> selectPruductID = modelLists.get(spModel.getSelectedItemPosition());
                                Map<String, Object> selectPruductID = modelLists.get(spModel.getSelectedItemPosition());
                                String Model = String.valueOf(Integer.valueOf(selectPruductID.get("GenericID").toString()));
                                //int id = Integer.valueOf(Model);

                                addOnPinByModelCode(Model);
                                addOnMetterByModelCode(Model);

                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
                                //LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                //LoadWhouseByCmpIdAndProID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                price.setText("");
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Model", Toast.LENGTH_SHORT).show();
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
    public void addOnPinByModelCode(String id) {
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetPinByModelID + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    pinLists = new ArrayList<>();
                    pinList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("SizeID", "0");
                    map.put("SizeName", "Select Pin");
                    //map.put("CustomerCode", "Customer Code");
                    pinLists.add(map);
                    pinList.add("Select Pin");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        //Log.d(TAG, "Response: " + object.toString());
                        map.put("SizeID", object.getString("SizeID"));
                        //  map.put("CustomerName", object.getString("customerName"));
                        map.put("SizeName", object.getString("SizeName"));// + " " + object.getString("CustomerCode"));
                        //map.put("CustomerCode", object.getString("CustomerCode"));
                        pinLists.add(map);
                        // stockList.add(object.getString("customerName"));
                        pinList.add(object.getString("SizeName"));// + " " + object.getString("SizeID"));
                    }
                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, pinList);
                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
                    spPin.setAdapter(adapter);
                    hideDialog();
                    spPin.setSelection(1);
                    spPin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spPin.getSelectedItemId() > 0) {
                                //String Model = spPin.getSelectedItem().toString();
                                Map<String, Object> selectModel = modelLists.get(spModel.getSelectedItemPosition());
                                String Model = selectModel.get("GenericID").toString();
                                //int id = Integer.valueOf(Model);

                                Map<String, Object> selectPInID = pinLists.get(spPin.getSelectedItemPosition());
                                String pin = selectPInID.get("SizeID").toString();

                                //GetProductByPin(id1,Model);

                                Map<String, Object> selectMetterID = metterists.get(spMetter.getSelectedItemPosition());
                                String id1 = selectMetterID.get("ColorID").toString();

                                //GetProductByMetter(id1, Model);
                                GetProductByModelPinMetter(pin,id1,Model);

                                //LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                //LoadWhouseByCmpIdAndProID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                price.setText("");
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Pin", Toast.LENGTH_SHORT).show();
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
    public void addOnMetterByModelCode(String id)  {
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetMeeterByModel + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    metterists= new ArrayList<>();
                    metterList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("ColorID", "0");
                    map.put("ColorName", "Select Metter");
                    //map.put("CustomerCode", "Customer Code");
                    metterists.add(map);
                    metterList.add("Select Metter");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        //Log.d(TAG, "Response: " + object.toString());
                        map.put("ColorID", object.getString("ColorID"));
                        //  map.put("CustomerName", object.getString("customerName"));
                        map.put("ColorName", object.getString("ColorName"));// + " " + object.getString("CustomerCode"));
                        //map.put("CustomerCode", object.getString("CustomerCode"));
                        metterists.add(map);
                        // stockList.add(object.getString("customerName"));
                        metterList.add(object.getString("ColorName"));// + " " + object.getString("ColorID"));
                    }
                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, metterList);
                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
                    spMetter.setAdapter(adapter);
                    hideDialog();
                    spMetter.setSelection(1);
                    spMetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spMetter.getSelectedItemId() > 0) {
                                //String Model = spPin.getSelectedItem().toString();
                                Map<String, Object> selectModel = modelLists.get(spModel.getSelectedItemPosition());
                                String Model = selectModel.get("GenericID").toString();
                                //int id = Integer.valueOf(Model);

                                Map<String, Object> selectPInID = pinLists.get(spPin.getSelectedItemPosition());
                                String pin = selectPInID.get("SizeID").toString();

                                Map<String, Object> selectMetterID = metterists.get(spMetter.getSelectedItemPosition());
                                String id1 = selectMetterID.get("ColorID").toString();

                                //GetProductByMetter(id1, Model);
                                GetProductByModelPinMetter(pin,id1,Model);

                                price.setText("");
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Meter", Toast.LENGTH_SHORT).show();
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
    public void GetProductByPin(String pin, String model) {
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetProductByPin + pin +"/"+ model, null, new Response.Listener<JSONArray>() {
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
                    spProduct.setSelection(1);
                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spProduct.getSelectedItemId() > 0) {
                                Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());

                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
                                LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                LoadWhouseByCmpIdAndProID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                price.setText("");
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
    public void GetProductByMetter(String Metter, String model) {
        progressDialog.setMessage("Loading ...");
        showDialog();
        Log.e("ProductInfo", "URL_GetProductBymeter" + AppConfig.GetProductBymeter + Metter +"/"+ model);
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.GetProductBymeter + Metter +"/"+ model, null, new Response.Listener<JSONArray>() {
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
                    spProduct.setSelection(1);
                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spProduct.getSelectedItemId() > 0) {
                                Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());

                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());

                                LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                LoadWhouseByCmpIdAndProID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                price.setText("");
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
    public void GetProductByModelPinMetter(String Pin, String Metter, String model) {
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.GetProductByModelPinmeter + Pin +"/" + Metter +"/" + model +"/", null, new Response.Listener<JSONArray>() {
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
                    spProduct.setSelection(1);
                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spProduct.getSelectedItemId() > 0) {
                                Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());

                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
                                LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                LoadWhouseByCmpIdAndProID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                price.setText("");
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

    public void LoadProductInfoByID(int id) {

        Log.e("ProductInfo", "URL_ProductInfoByID" + AppConfig.URL_ProductInfoByID + id);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_ProductInfoByID + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                // String stock = String.valueOf(response.getInt(Integer.parseInt("Code")));
                //String stock = response.getString("Code");
                //Toast.makeText(Activity_Adjustment.this, "Stock"+stock, Toast.LENGTH_SHORT).show();
                //mobile.setText(response.getString("Mobile"));
                //address.setText(response.getString("Address"));

                if(spPriceType.getSelectedItemId()==0)
                {
                    spPriceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spPriceType.getSelectedItemId() == 1) {
                                try {
                                    price.setText(response.getString("RegularMRP"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (spPriceType.getSelectedItemId() == 2) {
                                try {
                                    price.setText(response.getString("WholeSalePrice"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (spPriceType.getSelectedItemId() == 3) {
                                try {
                                    price.setText(response.getString("DealerPrice"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }

                    });
                }
                else {
                    if (spPriceType.getSelectedItemId() == 1) {
                        try {
                            price.setText(response.getString("RegularMRP"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (spPriceType.getSelectedItemId() == 2) {
                        try {
                            price.setText(response.getString("WholeSalePrice"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (spPriceType.getSelectedItemId() == 3) {
                        try {
                            price.setText(response.getString("DealerPrice"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
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
    public void LoadSalerSignByID(int id) {

        Log.e("ProductInfo", "URL_ProductInfoByID" + AppConfig.URL_SalerSignByID + id);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_SalerSignByID + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {

                try {
                    s=response.getString("Image");
                    //ImageView iv = v.findViewById(R.id.iv);
                    //---------Convert String to Bitmap-------------------//
                    byte [] encodeByte=Base64.decode(s,Base64.DEFAULT);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    //---------------------------------------------------//
                    iv.setImageBitmap(bitmap);

                } catch (JSONException e) {
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
    public void LoadStockByProductID(long id, long Wid) {

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
    public void LoadWhouseByCmpIdAndProID(int id) {
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_WhouseByProID + sessionList.get(0).getCompanyID()+ "/" + id, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                if(response.length()>0)
                {
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
                        //spWareHouse.setSelection(1);

                        String myString="{"+"WarehouseID"+"=4, "+"Name"+"="+"Showroom"+"}";
                        //Spinner s = (Spinner) findViewById(R.id.spinner_id);
                        for(int i=0; i < arrayAdapter.getCount(); i++) {
                            String x= arrayAdapter.getItem(i).toString();
                            if(myString.trim().equals(arrayAdapter.getItem(i).toString())){
                                spWareHouse.setSelection(i);
                                break;
                            }
//                            else {
//                                Toast.makeText(AppController.getContext(), "Showroom Not Found", Toast.LENGTH_SHORT).show();
//                            }
                        }



                        spWareHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (spWareHouse.getSelectedItemId() > 0) {
                                    Map<String, Object> selectwearhouseID = wearhouseList.get(spWareHouse.getSelectedItemPosition());
                                    int id1 = Integer.valueOf(selectwearhouseID.get("WarehouseID").toString());

                                    checkWarehouseID =id1;

                                    Map<String, Object> selectProductID = productLists.get(spProduct.getSelectedItemPosition());
                                    int idP = Integer.valueOf(selectProductID.get("ProductID").toString());

                                    //long Pid=spProduct.getSelectedItemId();
                                    //long wid=spWareHouse.getSelectedItemId();
                                    //String id1 = String.valueOf(id);
                                    //LoadPersonInfoByID(id1);
                                    btnAddList.setEnabled(true);
                                    LoadStockByProductID(idP,id1);

                                    //loadProduct();
                                    //LoadPaymentTyps();
                                } else {
//                                mobile.setText("");
//                                address.setText("");
                                    Toast.makeText(AppController.getContext(), "Select Warehouse", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Wearhouse not founds", Toast.LENGTH_SHORT).show();
                    btnAddList.setEnabled(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Wearhouse: " + error.getMessage());
                //Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();


            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void LoadPersonInfoByID1(String setupID) {
        //spPerson = (Spinner) findViewById(R.id.spPerson);
        Log.e(ContentValues.TAG, "Person: " + AppConfig.URL_CustomerInfoByID + String.valueOf(setupID));
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_CustomerInfoByID + String.valueOf(setupID), null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(AppController.getContext(), response.toString(), Toast.LENGTH_LONG).show();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);


                        mobile.setText(obj.getString("Mobile").toString());
                        address.setText(obj.getString("Address").toString());

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
        Log.e(ContentValues.TAG, "response person: " + AppConfig.URL_CustomerInfoByID + String.valueOf(setupID));
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
                double x = (Double.parseDouble((total.getText().length() > 0) ? total.getText().toString() : "0") + Double.parseDouble((previousDue.getText().length() > 0) ? previousDue.getText().toString() : "0")) - (Double.parseDouble((OverLess.getText().length() > 0) ? OverLess.getText().toString() : "0")) + (Double.parseDouble((txtShippingCost.getText().length() > 0) ? txtShippingCost.getText().toString() : "0"));
                PayAmount.setText(String.format("%.2f", x));
            }
        });
        txtShippingCost.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
                double x = (Double.parseDouble((total.getText().length() > 0) ? total.getText().toString() : "0") + Double.parseDouble((previousDue.getText().length() > 0) ? previousDue.getText().toString() : "0")) - (Double.parseDouble((OverLess.getText().length() > 0) ? OverLess.getText().toString() : "0")) + (Double.parseDouble((txtShippingCost.getText().length() > 0) ? txtShippingCost.getText().toString() : "0"));
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
        dis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(dis.getText().toString()==".")
                {
                    dis.requestFocus();
                    dis.setError("Enter 0 then .");
                }
                else
                {
                    double x = ((Double.parseDouble((price.getText().length() > 0) ? price.getText().toString() : "0") * Double.parseDouble((qut.getText().length() > 0) ? qut.getText().toString() : "1")) / 100) * Double.parseDouble((dis.getText().length() > 0) ? dis.getText().toString() : "0");
                    float y = Math.round(x);
                    disTk.setText(String.format("%.2f", x));
                }

            }
        });
        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double x = ((Double.parseDouble((price.getText().length() > 0) ? price.getText().toString() : "0") * Double.parseDouble((qut.getText().length() > 0) ? qut.getText().toString() : "1")) / 100) * Double.parseDouble((dis.getText().length() > 0) ? dis.getText().toString() : "0");
                float y = Math.round(x);
                disTk.setText(String.format("%.2f", x));
            }
        });
        qut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double x = ((Double.parseDouble((price.getText().length() > 0) ? price.getText().toString() : "0") * Double.parseDouble((qut.getText().length() > 0) ? qut.getText().toString() : "1")) / 100) * Double.parseDouble((dis.getText().length() > 0) ? dis.getText().toString() : "0");
                float y = Math.round(x);
                disTk.setText(String.format("%.2f", x));
            }
        });
    }
    public void AddToList() {
        salesDetailsController = new SalesDetailsController();
        salesDetailsModel = new SalesDetails();
        //String aa=btnAddList.getText().toString();

        if (btnAddList.length() == 3)
        {
            if (spProduct.getSelectedItemPosition() == 0 || spPriceType.getSelectedItemPosition() == 0 || spCusName.getSelectedItemPosition() == 0 || spWareHouse.getSelectedItemPosition() ==0 || checkWarehouseID!=4) {// spWareHouse.getSelectedItemPosition() == 0 ||
                if(spCusName.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(AppController.getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
                }
                else if(spProduct.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(AppController.getContext(), "Select Product", Toast.LENGTH_SHORT).show();
                }
                else if(spWareHouse.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(AppController.getContext(), "Select Warehouse as Showroom", Toast.LENGTH_SHORT).show();
                }
                else if(spPriceType.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(AppController.getContext(), "Select Price Type", Toast.LENGTH_SHORT).show();
                }
                else if(checkWarehouseID!=0)
                {
                    Toast.makeText(AppController.getContext(), "Please Select Showroom", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (price.length() > 0 && qut.length() > 0 && stock.length() > 0) {
                    int x = Integer.parseInt(qut.getText().toString());
                    int y = Integer.parseInt(stock.getText().toString());
                    if (x>y)
                    {
                        qut.requestFocus();
                        qut.setError("Quantity is grater then stock");
                    }
                    else {
                        //-----select data from spinner by id------//
                        Map<String, Object> selectedItem = null;
                        selectedItem = productLists.get(spProduct.getSelectedItemPosition());
                        salesDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                        //--------------^^^^^^-------------//
                        salesDetailsModel.setProductID(String.valueOf(selectedItem.get("ProductID")));
                        int proID= Integer.parseInt((String.valueOf(selectedItem.get("ProductID"))));
                        selectedItem = wearhouseList.get(spWareHouse.getSelectedItemPosition());
                        salesDetailsModel.setWarehouseID(String.valueOf(selectedItem.get("WarehouseID")));
                        salesDetailsModel.setOpeningQty("1");
                        salesDetailsModel.setReturnQty("0");

                        //double t = Double.parseDouble((price.getText().length() > 0) ? price.getText().toString() : "0") * Double.parseDouble((qut.getText().length() > 0) ? qut.getText().toString() : "0" );
                        //total.setText(String.valueOf(t));

                        double a = Double.parseDouble(price.getText().toString());
                        double s = Double.parseDouble(stock.getText().toString());
                        double b = Double.parseDouble(qut.getText().toString());
//                    if (b>s)
//                    {
//                        qut.requestFocus();
//                        qut.setError("Quantity is grater then stock");
//                        btnAddList.setEnabled(false);
//                    }
//                    else
//                    {
//                        b = Double.parseDouble(qut.getText().toString());
//                        btnAddList.setEnabled(true);
//                    }

                        if (total.length() > 0) {
                            gt = Double.parseDouble(total.getText().toString());
                        } else {
                            //total.setText(String.valueOf(ListTotal));
                            gt = ListTotal;
                            //gt = 0;
                        }

                        if (dis.length() > 0) {
                            double c = Double.parseDouble(disTk.getText().toString());
                            t = (a * b) - c;
                            gt = gt + t;
//                            double d = Double.parseDouble(dis.getText().toString());
//                            t = ((a * b) / 100) * (100 - d);
//                            //total.setText(String.valueOf(((a*b)/100)*(100-d)));
//                            disTk.setText(String.valueOf(((a * b) / 100) * d));
//                            gt = gt + t;
                        } else if (disTk.length() > 0) {
                            double c = Double.parseDouble(disTk.getText().toString());
                            t = (a * b) - c;
                            gt = gt + t;
                            //total.setText(String.valueOf((a*b)-c));
                        } else {
                            t = a * b;
                            gt = gt + t;
                            //Toast.makeText(AppController.getContext(), "Discount can't be 0.", Toast.LENGTH_SHORT).show();
                        }
                        salesDetailsModel.setQuantity(qut.getText().toString());
                        salesDetailsModel.setPrice(price.getText().toString());
                        //String xx = String.valueOf((int) Math.round(Double.parseDouble(disTk.getText().toString())));
                        //salesDetailsModel.setDiscount(xx);
                        salesDetailsModel.setDiscount(disTk.getText().toString());
                        salesDetailsModel.setTotal(String.valueOf(t));


                        salesDetailsController = new SalesDetailsController();
                        List<SalesDetails> salesDetails = salesDetailsController.GetByProID(proID);
                        if(salesDetails.size()>0)
                        {
                            Toast.makeText(AppController.getContext(), "Product is already in list", Toast.LENGTH_LONG).show();
                        }
                        else {
                            total.setText(String.format("%.2f", gt));
                            int id = salesDetailsController.Insert(salesDetailsModel);
                            if (id > 0) {
                                Toast.makeText(AppController.getContext(), "Product added", Toast.LENGTH_LONG).show();
                                btnSave.setEnabled(true);
                                Clear();
                                ShowList();
                            } else {
                                Toast.makeText(AppController.getContext(), "Failed to add product", Toast.LENGTH_LONG).show();
                            }
                        }
                        //salesDetailsController.getAll();
                    }
                } else {
                    if (price.getText().toString().length() == 0) {
                        price.requestFocus();
                        price.setError("Please provide an amount");
                    } else if (qut.getText().toString().length() == 0) {
                        qut.requestFocus();
                        qut.setError("Please provide an quantity");
                    } else if (stock.getText().toString().length() == 0) {
                        stock.requestFocus();
                        stock.setError("Please provide an stock");
                    }
                }
            }
        } else {
                double a = Double.parseDouble(price.getText().toString());
                double b = Double.parseDouble(qut.getText().toString());

                if (total.length() > 0) {
                    gt = Double.parseDouble(total.getText().toString());
                } else {
                    gt = ListTotal;
                    //gt = 0;
                }

                if (dis.length() > 0) {
                    double c = Double.parseDouble(disTk.getText().toString());
                    t = (a * b) - c;
                    gt = gt + t;
//                    double d = Double.parseDouble(dis.getText().toString());
//                    t = ((a * b) / 100) * (100 - d);
//                    //total.setText(String.valueOf(((a*b)/100)*(100-d)));
//                    disTk.setText(String.valueOf(((a * b) / 100) * d));
//                    gt = gt + t;
                } else if (disTk.length() > 0) {
                    double c = Double.parseDouble(disTk.getText().toString());
                    t = (a * b) - c;
                    gt = gt + t;
                    //total.setText(String.valueOf((a*b)-c));

                } else {
                    t = a * b;
                    gt = gt + t;
                    //Toast.makeText(AppController.getContext(), "Discount can't be 0.", Toast.LENGTH_SHORT).show();
                }

                salesDetailsModel.setSLID(listID);
                //country.setCountryName(cntName.getText().toString());
                salesDetailsModel.setQuantity(qut.getText().toString());
                salesDetailsModel.setPrice(price.getText().toString());
                //String xx = String.valueOf((int) Math.round(Double.parseDouble(disTk.getText().toString())));
                //salesDetailsModel.setDiscount(xx);
                salesDetailsModel.setDiscount(disTk.getText().toString());
                salesDetailsModel.setTotal(String.valueOf(t));
                total.setText(String.format("%.2f", gt));

                int id = salesDetailsController.Update(salesDetailsModel);
                if (id > 0) {
                    Toast.makeText(AppController.getContext(), "Product Updated", Toast.LENGTH_LONG).show();
//            Intent objIntent = new Intent(getApplicationContext(), ItemCardList.class);
//            objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(objIntent);
                    btnAddList.setText("Add");
                    btnReset.setText("Reset");
                    btnSave.setEnabled(true);
                    Clear();
                    ShowList();
                } else {
                    Toast.makeText(AppController.getContext(), "Failed to update product", Toast.LENGTH_LONG).show();
                }
                //salesDetailsController.getAll();

        }
    }
    public void SaveFinally() {
        if (spCusName.getSelectedItemId()!=0 && total.getText().toString().length()!=0)
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
            if (spCusName.getSelectedItemId()==0)
            {
                Toast.makeText(getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "There has no product for sales", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void save() {
        btnSave.setEnabled(false);
        progressDialog.setMessage("Sales in process, Please wait...");
        showDialog();
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();

        salesDetailsController = new SalesDetailsController();
        ArrayList<HashMap<String, String>> InformationLists = salesDetailsController.getAllList();
        Log.d("InformationLists:", InformationLists.toString());

        if (InformationLists.size() != 0) {
            JSONObject Obj = new JSONObject();
            try {
                //Add string params
                //Obj.put(ProfileINfo.KEY_companyID,CompanyID.getText());
                Obj.put("CompanyID", sessionList.get(0).getCompanyID());
                Obj.put("Sales_Date", date1.getText().toString());
                Map<String, Object> selectedItem = null;
                selectedItem = customerLists.get(spCusName.getSelectedItemPosition());
                //purchaseDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                Obj.put("CustomerID", (String.valueOf(selectedItem.get("CustomerID"))));
                Obj.put("CustomerName", (String.valueOf(selectedItem.get("CustomerName"))));
                Obj.put("Mobile", mobile.getText().toString());
                Obj.put("InvoiceNo", "");
                Obj.put("InvoiceDate", getDateTime());
                Obj.put("Job_By", sessionList.get(0).getEmail());
                Obj.put("SaleStatus", 2);
                Obj.put("Remarks", "Sales By APK");
                Obj.put("CrAmount", PaidAmount.getText().toString());
                Obj.put("DrAmount", total.getText().toString());
                Obj.put("AmountPaid", PayAmount.getText().toString());
                Obj.put("Discount", OverLess.getText().toString());
                Obj.put("Vattax", txtShippingCost.getText().toString());
                Obj.put("PreviousDue",previousDue.getText().toString());
                Obj.put("Image", s);
                if (spPayType.getSelectedItemId()==1)
                {
                    Obj.put("InvoiceType", 2);
                }
                else {
                    Obj.put("InvoiceType", 1);
                }
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
                    Obj.put("AccountNo", spAccountNo.getSelectedItemId());
                    Obj.put("BankID", spBank.getSelectedItemId());
                    Obj.put("BranchID", spBrance.getSelectedItemId());
                    Obj.put("HeadID", spAccHead.getSelectedItemId());
                }
                Obj.put("ChequeNo", ChequeNo.getText().toString());
                Obj.put("MaturityDate", Mdate.getText().toString());
                Obj.put("Cheque_Amount", PaidAmount.getText().toString());
                Obj.put("IsVoid", "no");
                Obj.put("ChequePayID", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Create json array for filter
            JSONArray array = new JSONArray();

            //Create json objects for two filter Ids

            int rows = 0;
            salesDetailsController = new SalesDetailsController();
            Cursor cursor = salesDetailsController.getData();

            cursor.moveToFirst();
            int a=cursor.getCount();

            while (rows < a) {
                JSONObject Param1 = new JSONObject();
                try {
                    Param1.put("SalesDetailID", 0);
                    Param1.put("ProductID", cursor.getInt(cursor.getColumnIndexOrThrow("ProductID")));
                    Param1.put("Quantity", cursor.getInt(cursor.getColumnIndexOrThrow("Quantity")));
                    Param1.put("Rate", cursor.getString(cursor.getColumnIndexOrThrow("Price")));
                    Param1.put("WarehouseID", cursor.getInt(cursor.getColumnIndexOrThrow("WarehouseID")));
                    Param1.put("OpeningQty", cursor.getInt(cursor.getColumnIndexOrThrow("OpeningQty")));
                    Param1.put("ReturnQty", cursor.getInt(cursor.getColumnIndexOrThrow("ReturnQty")));
                    Param1.put("Discount", cursor.getString(cursor.getColumnIndexOrThrow("Discount")));
                    Param1.put("Tk", cursor.getString(cursor.getColumnIndexOrThrow("Discount")));
                    Param1.put("TotalAmount", cursor.getString(cursor.getColumnIndexOrThrow("Total")));
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
                Obj.put("SalesDetails", array);
            } catch (JSONException e) {
                e.printStackTrace();
                hideDialog();
            }

            JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_Sales, Obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(ContentValues.TAG, "response" + response.toString());
                    Toast.makeText(AppController.getContext(), "Sales Successful", Toast.LENGTH_SHORT).show();
                    invoice();
                    hideDialog();
                    ClearAll();
                    home();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AppController.getContext(), "Failed to Sales", Toast.LENGTH_SHORT).show();
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
        else {
            Toast.makeText(AppController.getContext(), "Sales item not found", Toast.LENGTH_SHORT).show();
            hideDialog();
        }
    }
    public void ShowList() {


        salesDetailsController = new SalesDetailsController();
        ArrayList<HashMap<String, String>> InformationLists = salesDetailsController.getAllList();
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
                        //Toast.makeText(AppController.getContext(), "selectedID" + selectedID, Toast.LENGTH_SHORT).show();

                        double b = 0, c = 0;

                        salesDetailsController = new SalesDetailsController();
                        List<SalesDetails> salesDetails = salesDetailsController.GetByID(selectedID);
                        Log.d("ww: ", "Retriving Info..");
                        if (salesDetails.size() != 0) {
                            b = Double.parseDouble(salesDetails.get(0).getTotal());
                            qut.setText(salesDetails.get(0).getQuantity());
                            price.setText(salesDetails.get(0).getPrice());

                            //c=Double.parseDouble(salesList.get(0).getDiscount());

                            //dis.setText(String.valueOf(c/(b/100)));

                            //dis.setText(String.format("%.0f",c/(b/100)));

                            disTk.setText(salesDetails.get(0).getDiscount());

                            btnAddList.setText("Update");
                            btnReset.setText("Delete");
                            btnSave.setEnabled(false);
                            product = salesDetails.get(0).getProductName();
                            //btnAddList.setBackgroundColor(000);
                        }
                        //double a,b,c;
                        double a = Double.parseDouble(total.getText().toString());
                        if (a == 0) {
                            //total.setText(String.valueOf(ListTotal));
                            total.setText("0");
                        } else {
                            total.setText(String.valueOf(a - b));
                            //total.setText(String.valueOf(ListTotal+b));
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

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.salse_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "WarehouseID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Discount", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtSLID4, R.id.txtPrice, R.id.txtQuantity, R.id.txtDiscount, R.id.txtTotal});
            //((SimpleAdapter) adapter).setDropDownViewResource(R.layout.spinner_text_colour);
            setListAdapter(adapter);
        } else {
            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.salse_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "WarehouseID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Discount", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtSLID4, R.id.txtPrice, R.id.txtQuantity, R.id.txtDiscount, R.id.txtTotal});
            setListAdapter(adapter);
        }
    }
    public void DeleteListData() {
        if (btnReset.length() == 6) {
            int selectedID = Integer.parseInt(txtID.getText().toString());
            salesDetailsController = new SalesDetailsController();
            salesDetailsController.Delete(selectedID);
            ArrayList<HashMap<String, String>> InformationLists = salesDetailsController.getAllList();
            Log.d("InformationLists:", InformationLists.toString());
            if (InformationLists.size() != 0) {
                btnSave.setEnabled(true);
                ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.salse_item_view, new
                        String[]{"SLID", "ProductName", "ProductID", "WarehouseID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Discount", "Total"},
                        new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtSLID4, R.id.txtPrice, R.id.txtQuantity, R.id.txtDiscount, R.id.txtTotal});
                setListAdapter(adapter);
            } else {
                ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.salse_item_view, new
                        String[]{"SLID", "ProductName", "ProductID", "WarehouseID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Discount", "Total"},
                        new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtSLID4, R.id.txtPrice, R.id.txtQuantity, R.id.txtDiscount, R.id.txtTotal});
                setListAdapter(adapter);
            }
            btnReset.setText("Reset");
            btnAddList.setText("Add");
            Clear();
            ListView lv = getListView();
            lv.setEnabled(true);
        }
        else {

        }
    }

    public void invoice(){
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/GetSalesByMaxInvoiceWise/"+company+"="+a)));

    }

    public void Clear() {
//        price.setText("");
//        qut.setText("1");
//        dis.setText("");
//        disTk.setText("");
//        stock.setText("");
//        //spPriceType.setSelection(0);
//        spPin.setSelection(0);
//        spMetter.setSelection(0);
//        spModel.setSelection(0);
//        spWareHouse.setSelection(1);
//        spProduct.setSelection(0);
        //mobile.setText("");
        //address.setText("");
    }
    public void ClearAll() {
        ListTotal=0;
        txtShippingCost.setText("");
        address.setText("");
        mobile.setText("");
        total.setText("");
        previousDue.setText("");
        OverLess.setText("");
        PayAmount.setText("");
        PaidAmount.setText("");
        BlanceDue.setText("");
        ChequeNo.setText("");
        spPin.setSelection(0);
        spModel.setSelection(0);
        spMetter.setSelection(0);
        spPriceType.setSelection(0);
        spWareHouse.setSelection(0);
        spCusName.setSelection(0);
        spPayType.setSelection(0);
        spBank.setSelection(0);
        spBrance.setSelection(0);
        spAccountNo.setSelection(0);
        salesDetailsController = new SalesDetailsController();
        salesDetailsController.DeleteAll();
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
    public void home() {
        HomeFragment homefragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction homeFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        homeFragmentTransaction.replace(R.id.frame,homefragment);
        homeFragmentTransaction.commit();
    }
    public boolean Validation() {
        if (spProduct.getSelectedItemId() == 0) {
            Toast.makeText(AppController.getContext(), "Select a product.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spPriceType.getSelectedItemId() == 0) {
            Toast.makeText(AppController.getContext(), "Select an Customer Type.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spPriceType.getSelectedItemId() == 0) {
            Toast.makeText(AppController.getContext(), "Select an Customer Type.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (price.getText().toString().length() == 0) {
            price.requestFocus();
            price.setError("Please provide an amount");
            return false;
        }
        return true;
    }
    public void Delete(View view) {
        //AlertDialog diaBox = AskOption();
        //diaBox.show();
    }
    private android.app.AlertDialog.Builder AskOption() {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext());
        alert.setMessage("Do you want to add a new customer?");
        alert.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewCustomerFragment newCustomerFragment = new NewCustomerFragment();
                android.support.v4.app.FragmentTransaction newCustomerFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                newCustomerFragmentTransaction.replace(R.id.frame,newCustomerFragment);
                newCustomerFragmentTransaction.commit();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        return alert;
    }

}


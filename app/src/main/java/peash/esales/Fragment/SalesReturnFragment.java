package peash.esales.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import Adi.esales.R;
import peash.esales.App.AppConfig;
import peash.esales.App.AppController;
import peash.esales.Controller.SalesReturnDetailsController;
import peash.esales.Controller.SessionUserController;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.SalesReturnDetails;
import peash.esales.Models.SessionUser;

import static java.util.Locale.US;
import static peash.esales.App.AppController.TAG;

public class SalesReturnFragment extends ListFragment {

    Calendar myCalendar = Calendar.getInstance();

    ProgressDialog progressDialog;
    private SessionManager session;
    private SalesReturnDetails salesReturnDetailsModel;
    private SessionUserController sessionUserController;
    private SalesReturnDetailsController salesReturnDetailsController;
    EditText txtHmrp,txtHQty,date1,txtMRP,txtQty,total;
    Spinner spCusName,spWareHouse,spSaleInvoice,spProduct;
    TextView txtID;
    Button btnAddList, btnReset, btnSave, btnCancel;
    ImageView NewCus,IVcanCus;
    double totalAmount=0,ListTotal=0;


    private double t = 0, gt = 0;
    String product;
    String listID,s;
    byte[] strtext;
    private ImageView iv;
    int clickcount = 0;


    private List<Map<String, Object>> customerLists = new ArrayList<>();
    List<String> customerList;
    private List<Map<String, Object>> customerTypeList = new ArrayList<>();
    private List<Map<String, Object>> productLists = new ArrayList<>();
    List<String> productList;
    private List<Map<String, Object>> invoiceLists = new ArrayList<>();
    List<String> invoiceList;
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
        View v = inflater.inflate(R.layout.sales_return_fragment, container, false);

        spCusName = v.findViewById(R.id.spCustomerName);
        spWareHouse= v.findViewById(R.id.spWareHouse);
        spSaleInvoice = v.findViewById(R.id.spSaleInvoice);
        spProduct = v.findViewById(R.id.spProduct);

        txtHmrp = v.findViewById(R.id.txtHmrp);
        txtHQty = v.findViewById(R.id.txtHQty);
        txtMRP = v.findViewById(R.id.txtMRP);
        txtQty = v.findViewById(R.id.txtQut);
        total = v.findViewById(R.id.txtHTotal);


//..................................... Pull refresh..............................................

        swipeContainer=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                addOnCustomer();
                loadWarehouse();
                //addOnModel();
                //LoadPaymentTyps();

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



        btnAddList = v.findViewById(R.id.btnAdd);
        btnReset = v.findViewById(R.id.btnReset);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);

        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddToList();
                //TextChangeForPrice();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteListData();
                Clear();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearAll();
            }
        });


        //----------------
        int rows = 0;
        salesReturnDetailsController = new SalesReturnDetailsController();
        Cursor cursor = salesReturnDetailsController.getData();
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

        //-------Call Functions-----

        LoadDate();
        //loadCustomer();
        addOnCustomer();
        loadWarehouse();
        return v;
    }

    private void LoadDate() {

        String mydate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        date1.setText(mydate);
        setPur_date();


        final DatePickerDialog.OnDateSetListener dat = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setPur_date();
            }
        };
        //on Change of Purchase date
        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dat, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void setPur_date() {

        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, US);
        date1.setText(sdf.format(myCalendar.getTime()));
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void addOnCustomer() {
        progressDialog.setMessage("Loding ...");
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
                                //LoadPersonInfoByID(id);
                                sessionUserController=new SessionUserController();
                                List<SessionUser>sessionList=sessionUserController.GetAll();
                                int id1= Integer.parseInt(sessionList.get(0).getCompanyID());
                                LoadeInvoice(id,id1);
                            } else {
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
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void LoadeInvoice(int CustID, int ComID) {
        progressDialog.setMessage("Loding ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetInvoiceNo + CustID +"/"+ ComID, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    invoiceLists = new ArrayList<>();
                    invoiceList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("SalesID", "0");
                    map.put("InvoiceNo", "Select Invoice");
                    //map.put("CustomerCode", "Customer Code");
                    invoiceLists.add(map);
                    invoiceList.add("Select Invoice");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        //Log.d(TAG, "Response: " + object.toString());
                        map.put("SalesID", object.getString("SalesID"));
                        map.put("InvoiceNo", object.getString("InvoiceNo"));
                        //map.put("ProductName", object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
                        //map.put("CustomerCode", object.getString("CustomerCode"));
                        invoiceLists.add(map);
                        // stockList.add(object.getString("customerName"));
                        invoiceList.add(object.getString("InvoiceNo"));// + " " + object.getString("CustomerCode"));
                    }
                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, invoiceList);
                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
                    spSaleInvoice.setAdapter(adapter);
                    hideDialog();
                    spSaleInvoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spSaleInvoice.getSelectedItemId() > 0) {
                                //String Model = spSaleInvoice.getSelectedItem().toString();
                                //Map<String, Object> selectPruductID = modelLists.get(spSaleInvoice.getSelectedItemPosition());
                                Map<String, Object> selectPruductID = invoiceLists.get(spSaleInvoice.getSelectedItemPosition());
                                int SalesID = Integer.valueOf(selectPruductID.get("SalesID").toString());
                                //int id = Integer.valueOf(Model);
                                addOnProduct(SalesID);
                                //addOnPinByModelCode(Model);
                                //addOnMetterByModelCode(Model);

                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
                                //LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                //LoadWhouseByCmpIdAndProID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Invoice", Toast.LENGTH_SHORT).show();
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
    public void addOnProduct(final int SalesID) {
        progressDialog.setMessage("Loding ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetProductBySalesID + SalesID, null, new Response.Listener<JSONArray>() {
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
                                int proID = Integer.valueOf(selectPruductID.get("ProductID").toString());

                                Map<String, Object> selectProductID = invoiceLists.get(spSaleInvoice.getSelectedItemPosition());
                                int SalID = Integer.valueOf(selectProductID.get("SalesID").toString());
                                LoadProductInfoByID(proID,SalID);
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
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }


    public void LoadProductInfoByID(int proID, int SalesID) {
        progressDialog.setMessage("Loding ...");
        showDialog();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetProductInfo + proID + "/" + SalesID, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        txtHmrp.setText(object.getString("Rate"));
                        txtHQty.setText(object.getString("Quantity"));
                        hideDialog();
                    }
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
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void LoadProductInfoByID1(int proID, int SalesID) {

        Log.e("Person", "URL_StockByProID" + AppConfig.URL_GetProductInfo + proID +"/"+ SalesID);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_GetProductInfo + proID + "/" + SalesID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    // String stock = String.valueOf(response.getInt(Integer.parseInt("Code")));
                    //String stock = response.getString("Code");
                    //Toast.makeText(Activity_Adjustment.this, "Stock"+stock, Toast.LENGTH_SHORT).show();
                    //txtHmrp.setText(response.getString("Code"));
                    txtHmrp.setText(response.getString("Mobile"));
                    txtHQty.setText(response.getString("Address"));
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

                                //LoadStockByPersonID(idP,id);
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
    public void AddToList() {
        salesReturnDetailsController = new SalesReturnDetailsController();
        salesReturnDetailsModel = new SalesReturnDetails();
        //String aa=btnAddList.getText().toString();

        if (btnAddList.length() == 3)
        {
            if (spProduct.getSelectedItemPosition() == 0 || spSaleInvoice.getSelectedItemPosition() == 0 || spCusName.getSelectedItemPosition() == 0 ||  spWareHouse.getSelectedItemPosition() == 0) {
                if(spCusName.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(AppController.getContext(), "Select Customer", Toast.LENGTH_SHORT).show();
                }
                else if(spSaleInvoice.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(AppController.getContext(), "Select Invoice", Toast.LENGTH_SHORT).show();
                }
                else if(spProduct.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(AppController.getContext(), "Select Product", Toast.LENGTH_SHORT).show();
                }
                else if(spWareHouse.getSelectedItemPosition() == 0)
                {
                    Toast.makeText(AppController.getContext(), "Select Warehouse", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (txtMRP.length() > 0 && txtQty.length() > 0) {
                    double x = Double.parseDouble(txtQty.getText().toString());
                    double y = Double.parseDouble(txtHQty.getText().toString());
                    if (x>y)
                    {
                        txtQty.requestFocus();
                        txtQty.setError("Return quantity is grater then sale quantity");
                    }
                    else {
                        //-----select data from spinner by id------//
                        Map<String, Object> selectedItem = null;
                        selectedItem = productLists.get(spProduct.getSelectedItemPosition());
                        salesReturnDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                        //--------------^^^^^^-------------//
                        salesReturnDetailsModel.setProductID(String.valueOf(selectedItem.get("ProductID")));
                        int proID= Integer.parseInt((String.valueOf(selectedItem.get("ProductID"))));
                        selectedItem = wearhouseList.get(spWareHouse.getSelectedItemPosition());
                        salesReturnDetailsModel.setWarehouseID(String.valueOf(selectedItem.get("WarehouseID")));
                        salesReturnDetailsModel.setPrice(txtMRP.getText().toString());
                        salesReturnDetailsModel.setQuantity(txtQty.getText().toString());

                        double a = Double.parseDouble(txtMRP.getText().toString());
                        double s = Double.parseDouble(txtQty.getText().toString());
                        t=a*s;
                        salesReturnDetailsModel.setTotal(String.valueOf(t));

                        if (total.length() > 0) {
                            gt = Double.parseDouble(total.getText().toString())+t;
                        } else {
                            gt = ListTotal + t;
                            //gt = 0;
                        }


                        salesReturnDetailsController = new SalesReturnDetailsController();
                        List<SalesReturnDetails> salesReturnDetails = salesReturnDetailsController.GetByProID(proID);
                        if(salesReturnDetails.size()>0)
                        {
                            Toast.makeText(AppController.getContext(), "Product is already in list", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            total.setText(String.format("%.2f", gt));
                            int id = salesReturnDetailsController.Insert(salesReturnDetailsModel);
                            if (id > 0) {
                                Toast.makeText(AppController.getContext(), "Product added", Toast.LENGTH_LONG).show();
                                Clear();
                                ShowList();
                            } else {
                                Toast.makeText(AppController.getContext(), "Product fail to add", Toast.LENGTH_LONG).show();
                            }
                        }
                        //salesDetailsController.getAll();
                    }
                } else {
                    if (txtMRP.getText().toString().length() == 0) {
                        txtMRP.requestFocus();
                        txtMRP.setError("Please provide an amount");
                    } else if (txtQty.getText().toString().length() == 0) {
                        txtQty.requestFocus();
                        txtQty.setError("Please provide an quantity");
                    }
                }
            }
        } else {
                double a = Double.parseDouble(txtMRP.getText().toString());
                double s = Double.parseDouble(txtQty.getText().toString());
                t=a*s;
                if (total.length() > 0) {
                    gt = Double.parseDouble(total.getText().toString())+t;
                } else {
                    gt = ListTotal + t;
                }

                salesReturnDetailsModel.setSLID(listID);
                //country.setCountryName(cntName.getText().toString());
                salesReturnDetailsModel.setQuantity(txtQty.getText().toString());
                salesReturnDetailsModel.setPrice(txtMRP.getText().toString());
                salesReturnDetailsModel.setTotal(String.valueOf(t));
                total.setText(String.format("%.2f", gt));


                int id = salesReturnDetailsController.Update(salesReturnDetailsModel);
                    if (id > 0) {
                        Toast.makeText(AppController.getContext(), "Product Updated", Toast.LENGTH_LONG).show();
    //            Intent objIntent = new Intent(getApplicationContext(), ItemCardList.class);
    //            objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //            startActivity(objIntent);
                        btnAddList.setText("Add");
                        btnReset.setText("Reset");
                        Clear();
                        ShowList();
                    } else {
                        Toast.makeText(AppController.getContext(), "Product Update Fail", Toast.LENGTH_LONG).show();
                    }
                    //salesDetailsController.getAll();

        }
    }
    public void ShowList() {


        salesReturnDetailsController = new SalesReturnDetailsController();
        ArrayList<HashMap<String, String>> InformationLists = salesReturnDetailsController.getAllList();
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

                        salesReturnDetailsController = new SalesReturnDetailsController();
                        List<SalesReturnDetails> salesReturnDetails = salesReturnDetailsController.GetByID(selectedID);
                        Log.d("ww: ", "Retriving Info..");
                        if (salesReturnDetails.size() != 0) {
                            b = Double.parseDouble(salesReturnDetails.get(0).getTotal());
                            txtQty.setText(salesReturnDetails.get(0).getQuantity());
                            txtMRP.setText(salesReturnDetails.get(0).getPrice());

                            //c=Double.parseDouble(salesList.get(0).getDiscount());

                            //dis.setText(String.valueOf(c/(b/100)));

                            //dis.setText(String.format("%.0f",c/(b/100)));


                            btnAddList.setText("Update");
                            btnReset.setText("Delete");
                            product = salesReturnDetails.get(0).getProductName();
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

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.salse_return_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "WarehouseID","Price", "Quantity", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtPrice, R.id.txtQuantity,R.id.txtTotal});
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
                    String[]{"SLID", "ProductName", "ProductID", "WarehouseID","Price", "Quantity", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtPrice, R.id.txtQuantity,R.id.txtTotal});
            setListAdapter(adapter);
        }
    }
    private void save() {
        btnSave.setEnabled(false);
        progressDialog.setMessage("Sales Return in process, Please wait...");
        showDialog();
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();

        salesReturnDetailsController = new SalesReturnDetailsController();
        ArrayList<HashMap<String, String>> InformationLists = salesReturnDetailsController.getAllList();
        Log.d("InformationLists:", InformationLists.toString());

        if (InformationLists.size() != 0) {
            JSONObject Obj = new JSONObject();
            try {
                //Add string params
                //Obj.put(ProfileINfo.KEY_companyID,CompanyID.getText());
                Obj.put("CompanyID", sessionList.get(0).getCompanyID());
                Obj.put("SalesReturn_Date", date1.getText().toString());
                Map<String, Object> selectedItem = null;
                selectedItem = invoiceLists.get(spSaleInvoice.getSelectedItemPosition());
                //purchaseDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                Obj.put("RefSalesID", (String.valueOf(selectedItem.get("InvoiceNo"))));
                selectedItem = customerLists.get(spCusName.getSelectedItemPosition());
                Obj.put("CustomerID", (String.valueOf(selectedItem.get("CustomerID"))));
                Obj.put("SalesReturnID", 0);
                Obj.put("Job_By", sessionList.get(0).getEmail());
                Obj.put("User_PaymentID", "");
                Obj.put("Sys_Payment_Date", getDateTime());
                Obj.put("Payment_Date", date1.getText().toString());
                Obj.put("DrAmount", total.getText().toString());
                Obj.put("CrAmount", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Create json array for filter
            JSONArray array = new JSONArray();

            //Create json objects for two filter Ids

            int rows = 0;
            salesReturnDetailsController = new SalesReturnDetailsController();
            Cursor cursor = salesReturnDetailsController.getData();

            cursor.moveToFirst();
            int a=cursor.getCount();

            while (rows < a) {
                JSONObject Param1 = new JSONObject();
                try {
                    Param1.put("SReturnDetailID", 0);
                    Param1.put("SalesReturnDetailID", 0);
                    Param1.put("Inv_ProductID", cursor.getInt(cursor.getColumnIndexOrThrow("ProductID")));
                    Param1.put("Quantity", cursor.getInt(cursor.getColumnIndexOrThrow("Quantity")));
                    Param1.put("Rate_PerUnit", cursor.getString(cursor.getColumnIndexOrThrow("Price")));
                    Param1.put("WarehouseID", cursor.getInt(cursor.getColumnIndexOrThrow("WarehouseID")));
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
                Obj.put("SalesReturnDetails", array);
            } catch (JSONException e) {
                e.printStackTrace();
                hideDialog();
            }

            JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_SalesReturn, Obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(ContentValues.TAG, "response" + response.toString());
                    Toast.makeText(AppController.getContext(), "Sales Return Successful", Toast.LENGTH_SHORT).show();
                    salesReturnDetailsController = new SalesReturnDetailsController();
                    salesReturnDetailsController.DeleteAll();
                    hideDialog();
                    //ClearAll();
                    home();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AppController.getContext(), "Sales Return Fall", Toast.LENGTH_SHORT).show();
                    Log.e(ContentValues.TAG, "Error: " + error.getMessage());
                    hideDialog();
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
            Toast.makeText(AppController.getContext(), "Sales return item not found", Toast.LENGTH_SHORT).show();
            hideDialog();
        }
    }
    public void DeleteListData() {
        if (btnReset.length() == 6) {
            int selectedID = Integer.parseInt(txtID.getText().toString());
            salesReturnDetailsController = new SalesReturnDetailsController();
            salesReturnDetailsController.Delete(selectedID);
            ArrayList<HashMap<String, String>> InformationLists = salesReturnDetailsController.getAllList();
            Log.d("InformationLists:", InformationLists.toString());
            if (InformationLists.size() != 0) {
                ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.salse_return_item_view, new
                        String[]{"SLID", "ProductName", "ProductID", "WarehouseID","Price", "Quantity", "Total"},
                        new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtPrice, R.id.txtQuantity,R.id.txtTotal});
                setListAdapter(adapter);
            } else {
                ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.salse_return_item_view, new
                        String[]{"SLID", "ProductName", "ProductID", "WarehouseID","Price", "Quantity", "Total"},
                        new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtPrice, R.id.txtQuantity,R.id.txtTotal});
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
        total.setText("");
        salesReturnDetailsController = new SalesReturnDetailsController();
        salesReturnDetailsController.DeleteAll();
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
    private AlertDialog.Builder AskOption() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Do you want to add a new customer?");
        alert.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        return alert;
    }

}


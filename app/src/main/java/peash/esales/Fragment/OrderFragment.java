package peash.esales.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
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

public class OrderFragment extends ListFragment {

    Calendar myCalendar = Calendar.getInstance();

    ProgressDialog progressDialog;
    private SessionManager session;
    private OrderDetails orderDetailsModel;
    private SessionUserController sessionUserController;
    private OrderDetailsController orderDetailsController;
    EditText date1, price, qut,total,Mdate;
    Spinner spProduct,spModel,spPin,spmeter;
    Button btnAddList, btnReset, btnSave, btnCancel,btnAdd,btnRemove;
    TextView txtID,ProductID;
    double ListTotal=0;
    byte[] strtext;
    double Stock=0;

    private double t = 0, gt = 0;
    String product,s;
    int Model,meter,Model1,meter1,proid2;

    String listID,PID,dPrice,rPrice,wPrice;

    private List<Map<String, Object>> productLists = new ArrayList<>();
    List<String> productList;
    private List<Map<String, Object>> modelLists = new ArrayList<>();
    List<String> modelList;
    private List<Map<String, Object>> pinLists = new ArrayList<>();
    List<String> pinList;
    private List<Map<String, Object>> metterists = new ArrayList<>();
    List<String> metterList;

    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.order_fragment, container, false);

        //------------GetByteArray from other Fragment--------//
        strtext = getArguments().getByteArray("byteArray");
        s = getArguments().getString("temp");
        //-------------Convert Byte Array to String------//
        String temp= Base64.encodeToString(strtext, Base64.DEFAULT);
        //---------------------------------------------------//


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        price = v.findViewById(R.id.txtPrice);
        qut = v.findViewById(R.id.txtQuantity);
        total = v.findViewById(R.id.txtTotal);

        spProduct = v.findViewById(R.id.spProduct);
        spModel = v.findViewById(R.id.spModel);
        spPin = v.findViewById(R.id.spPin);
        spmeter = v.findViewById(R.id.spMetter);

        btnAddList = v.findViewById(R.id.btnAdd);
        btnReset = v.findViewById(R.id.btnReset);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnAdd = v.findViewById(R.id.btnAddQ);
        btnRemove = v.findViewById(R.id.btnRemove);

        //orderDetailsController = new OrderDetailsController();
        //orderDetailsController.DeleteAll();
        //


// ..................................... Pull refresh..............................................

        swipeContainer=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                addOnModel();

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


        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddToList();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveFinally();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearAll();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteListData();
                Clear();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(qut.getText().toString());
                int b=0;
                if(a>b)
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

        //-------Call Functions-----

        addOnModel();
        ShowListOnly();
        TextChangeForPrice();
        return v;
    }


    private void LoadDate() {

        String mydate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        date1.setText(mydate);
        setPur_date();

        //Default date for Maturity date
        String maturityDate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
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

    public void addOnModel() {
        progressDialog.setMessage("Loding ...");
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
                                Model = Integer.valueOf(selectPruductID.get("GenericID").toString());
                                //int id = Integer.valueOf(Model);

                                addOnPinByModelCode(Model);
                                addOnmeterByModelCode(Model);

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
    public void addOnPinByModelCode(int id) {
        progressDialog.setMessage("Loding ...");
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
                                String id1 = selectPInID.get("SizeID").toString();

                                GetProductByPin(id1,Model);
                                //LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                //LoadWhouseByCmpIdAndProID(id);
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
    public void addOnmeterByModelCode(int id)  {
        progressDialog.setMessage("Loding ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetMeeterByModel + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    metterists= new ArrayList<>();
                    metterList = new ArrayList<String>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("ColorID", "0");
                    map.put("ColorName", "Select meter");
                    //map.put("CustomerCode", "Customer Code");
                    metterists.add(map);
                    metterList.add("Select meter");
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
                    spmeter.setAdapter(adapter);
                    hideDialog();
                    spmeter.setSelection(1);
                    spmeter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spmeter.getSelectedItemId() > 0) {
                                //String Model = spPin.getSelectedItem().toString();
                                Map<String, Object> selectModel = modelLists.get(spModel.getSelectedItemPosition());
                                String Model = selectModel.get("GenericID").toString();
                                //int id = Integer.valueOf(Model);

                                Map<String, Object> selectPInID = pinLists.get(spPin.getSelectedItemPosition());
                                String pin = selectPInID.get("SizeID").toString();

                                Map<String, Object> selectmeterID = metterists.get(spmeter.getSelectedItemPosition());
                                String id1 = selectmeterID.get("ColorID").toString();
                                meter = Integer.parseInt(selectmeterID.get("ColorID").toString());

                                //GetProductBymeter(id1, Model);

                                if(spPin.getSelectedItemId()==0)
                                {
                                    GetProductBymeter(id1,Model);
                                }
                                else
                                {
                                    GetProductByModelPinmeter(pin,id1,Model);
                                }


                                //LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                //LoadWhouseByCmpIdAndProID(id);
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
    public void GetProductByPin(String pin, String model) {
        progressDialog.setMessage("Loding ...");
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
                                LoadStockByProduectID(id,4);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                //LoadWhouseByCmpIdAndProID(id);
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
    public void GetProductBymeter(String meter, String model) {
        progressDialog.setMessage("Loding ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.GetProductBymeter + meter +"/"+ model, null, new Response.Listener<JSONArray>() {
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
                                LoadStockByProduectID(id,4);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                //LoadWhouseByCmpIdAndProID(id);
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
    public void GetProductByModelPinmeter(String Pin, String meter, String model) {
        progressDialog.setMessage("Loding ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.GetProductByModelPinmeter + Pin +"/" + meter +"/" + model +"/", null, new Response.Listener<JSONArray>() {
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
                                LoadProductInfoByID(id);
                                LoadStockByProduectID(id,4);
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
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

//    public void addOnModel() {
//        progressDialog.setMessage("Loading ...");
//        showDialog();
//
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_ProductModel, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    modelLists = new ArrayList<>();
//                    modelList = new ArrayList<String>();
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("GenericID", "0");
//                    map.put("GenericName", "Select Model");
//                    //map.put("CustomerCode", "Customer Code");
//                    modelLists.add(map);
//                    modelList.add("Select Model");
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        //Log.d(TAG, "Response: " + object.toString());
//                        map.put("GenericID", object.getString("GenericID"));
//                        map.put("GenericName", object.getString("GenericName"));
//                        //map.put("ProductName", object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
//                        //map.put("CustomerCode", object.getString("CustomerCode"));
//                        modelLists.add(map);
//                        // stockList.add(object.getString("customerName"));
//                        modelList.add(object.getString("GenericName"));// + " " + object.getString("CustomerCode"));
//                    }
//                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, modelList);
//                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
//                    spModel.setAdapter(adapter);
//                    hideDialog();
//                    spModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spModel.getSelectedItemId() > 0) {
//                                //String Model = spModel.getSelectedItem().toString();
//                                //Map<String, Object> selectPruductID = modelLists.get(spModel.getSelectedItemPosition());
//                                Map<String, Object> selectPruductID = modelLists.get(spModel.getSelectedItemPosition());
//                                String Model = String.valueOf(Integer.valueOf(selectPruductID.get("GenericID").toString()));
//                                //int id = Integer.valueOf(Model);
//
//                                addOnPinByModelCode(Model);
//                                addOnMetterByModelCode(Model);
//
//                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//                                //LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                //LoadWhouseByCmpIdAndProID(id);
//                                //LoadStockByPersonID(id);
//                                //LoadCustomerType();
//                                price.setText("");
//                            } else {
//                                Toast.makeText(AppController.getContext(), "Select Model", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> arg0) {
//                            // TODO Auto-generated method stub
//                        }
//
//                    });
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    hideDialog();
//                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
//                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//           /* @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("UserName", email);
//                params.put("Password", password);
//                return params;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap<String, String>();
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                //headers.put("Content-Type", "application/json");
//                // Removed this line if you dont need it or Use application/json
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                headers.put("Content-Type", "application/json; charset=utf-8;");
//                //headers.put("Token", AppConfig.Token);
//                return headers;
//            }*/
//        };
//        //Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }
//    public void addOnPinByModelCode(String id) {
//        progressDialog.setMessage("Loading ...");
//        showDialog();
//
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetPinByModelID + id, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    pinLists = new ArrayList<>();
//                    pinList = new ArrayList<String>();
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("SizeID", "0");
//                    map.put("SizeName", "Select Pin");
//                    //map.put("CustomerCode", "Customer Code");
//                    pinLists.add(map);
//                    pinList.add("Select Pin");
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        //Log.d(TAG, "Response: " + object.toString());
//                        map.put("SizeID", object.getString("SizeID"));
//                        //  map.put("CustomerName", object.getString("customerName"));
//                        map.put("SizeName", object.getString("SizeName"));// + " " + object.getString("CustomerCode"));
//                        //map.put("CustomerCode", object.getString("CustomerCode"));
//                        pinLists.add(map);
//                        // stockList.add(object.getString("customerName"));
//                        pinList.add(object.getString("SizeName"));// + " " + object.getString("SizeID"));
//                    }
//                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, pinList);
//                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
//                    spPin.setAdapter(adapter);
//                    hideDialog();
//                    spPin.setSelection(1);
//                    spPin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spPin.getSelectedItemId() > 0) {
//                                //String Model = spPin.getSelectedItem().toString();
//                                Map<String, Object> selectModel = modelLists.get(spModel.getSelectedItemPosition());
//                                String Model = selectModel.get("GenericID").toString();
//                                //int id = Integer.valueOf(Model);
//
//                                Map<String, Object> selectPInID = pinLists.get(spPin.getSelectedItemPosition());
//                                String pin = selectPInID.get("SizeID").toString();
//
//                                //GetProductByPin(id1,Model);
//
//                                Map<String, Object> selectMetterID = metterists.get(spmeter.getSelectedItemPosition());
//                                String id1 = selectMetterID.get("ColorID").toString();
//
//                                //GetProductByMetter(id1, Model);
//                                GetProductByModelPinMetter(pin,id1,Model);
//
//                                //LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                //LoadWhouseByCmpIdAndProID(id);
//                                //LoadStockByPersonID(id);
//                                //LoadCustomerType();
//                                price.setText("");
//                            } else {
//                                Toast.makeText(AppController.getContext(), "Select Pin", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> arg0) {
//                            // TODO Auto-generated method stub
//                        }
//
//                    });
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    hideDialog();
//                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
//                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//           /* @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("UserName", email);
//                params.put("Password", password);
//                return params;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap<String, String>();
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                //headers.put("Content-Type", "application/json");
//                // Removed this line if you dont need it or Use application/json
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                headers.put("Content-Type", "application/json; charset=utf-8;");
//                //headers.put("Token", AppConfig.Token);
//                return headers;
//            }*/
//        };
//        //Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }
//    public void addOnMetterByModelCode(String id)  {
//        progressDialog.setMessage("Loading ...");
//        showDialog();
//
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetMeeterByModel + id, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    metterists= new ArrayList<>();
//                    metterList = new ArrayList<String>();
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("ColorID", "0");
//                    map.put("ColorName", "Select Metter");
//                    //map.put("CustomerCode", "Customer Code");
//                    metterists.add(map);
//                    metterList.add("Select Metter");
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        //Log.d(TAG, "Response: " + object.toString());
//                        map.put("ColorID", object.getString("ColorID"));
//                        //  map.put("CustomerName", object.getString("customerName"));
//                        map.put("ColorName", object.getString("ColorName"));// + " " + object.getString("CustomerCode"));
//                        //map.put("CustomerCode", object.getString("CustomerCode"));
//                        metterists.add(map);
//                        // stockList.add(object.getString("customerName"));
//                        metterList.add(object.getString("ColorName"));// + " " + object.getString("ColorID"));
//                    }
//                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, metterList);
//                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
//                    spmeter.setAdapter(adapter);
//                    hideDialog();
//                    spmeter.setSelection(1);
//                    spmeter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spmeter.getSelectedItemId() > 0) {
//                                //String Model = spPin.getSelectedItem().toString();
//                                Map<String, Object> selectModel = modelLists.get(spModel.getSelectedItemPosition());
//                                String Model = selectModel.get("GenericID").toString();
//                                //int id = Integer.valueOf(Model);
//
//                                Map<String, Object> selectPInID = pinLists.get(spPin.getSelectedItemPosition());
//                                String pin = selectPInID.get("SizeID").toString();
//
//                                Map<String, Object> selectMetterID = metterists.get(spmeter.getSelectedItemPosition());
//                                String id1 = selectMetterID.get("ColorID").toString();
//
//                                //GetProductByMetter(id1, Model);
//                                GetProductByModelPinMetter(pin,id1,Model);
//
//                                price.setText("");
//                            } else {
//                                Toast.makeText(AppController.getContext(), "Select Meter", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> arg0) {
//                            // TODO Auto-generated method stub
//                        }
//
//                    });
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    hideDialog();
//                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
//                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//           /* @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("UserName", email);
//                params.put("Password", password);
//                return params;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap<String, String>();
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                //headers.put("Content-Type", "application/json");
//                // Removed this line if you dont need it or Use application/json
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                headers.put("Content-Type", "application/json; charset=utf-8;");
//                //headers.put("Token", AppConfig.Token);
//                return headers;
//            }*/
//        };
//        //Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }
//    public void GetProductByPin(String pin, String model) {
//        progressDialog.setMessage("Loading ...");
//        showDialog();
//
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetProductByPin + pin +"/"+ model, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    productLists = new ArrayList<>();
//                    productList = new ArrayList<String>();
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("ProductID", "0");
//                    map.put("ProductName", "Select Product");
//                    //map.put("CustomerCode", "Customer Code");
//                    productLists.add(map);
//                    productList.add("Select Product");
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        //Log.d(TAG, "Response: " + object.toString());
//                        map.put("ProductID", object.getString("ProductID"));
//                        //  map.put("CustomerName", object.getString("customerName"));
//                        map.put("ProductName", object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
//                        //map.put("CustomerCode", object.getString("CustomerCode"));
//                        productLists.add(map);
//                        // stockList.add(object.getString("customerName"));
//                        productList.add(object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
//                    }
//                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, productList);
//                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
//                    spProduct.setAdapter(adapter);
//                    hideDialog();
//                    spProduct.setSelection(1);
//                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spProduct.getSelectedItemId() > 0) {
//                                Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//
//                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//                                LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                //LoadWhouseByCmpIdAndProID(id);
//                                //LoadStockByPersonID(id);
//                                //LoadCustomerType();
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
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    hideDialog();
//                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
//                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//           /* @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("UserName", email);
//                params.put("Password", password);
//                return params;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap<String, String>();
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                //headers.put("Content-Type", "application/json");
//                // Removed this line if you dont need it or Use application/json
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                headers.put("Content-Type", "application/json; charset=utf-8;");
//                //headers.put("Token", AppConfig.Token);
//                return headers;
//            }*/
//        };
//        //Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }
//    public void GetProductByMetter(String Metter, String model) {
//        progressDialog.setMessage("Loading ...");
//        showDialog();
//        Log.e("ProductInfo", "URL_GetProductBymeter" + AppConfig.GetProductBymeter + Metter +"/"+ model);
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.GetProductBymeter + Metter +"/"+ model, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    productLists = new ArrayList<>();
//                    productList = new ArrayList<String>();
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("ProductID", "0");
//                    map.put("ProductName", "Select Product");
//                    //map.put("CustomerCode", "Customer Code");
//                    productLists.add(map);
//                    productList.add("Select Product");
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        //Log.d(TAG, "Response: " + object.toString());
//                        map.put("ProductID", object.getString("ProductID"));
//                        //  map.put("CustomerName", object.getString("customerName"));
//                        map.put("ProductName", object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
//                        //map.put("CustomerCode", object.getString("CustomerCode"));
//                        productLists.add(map);
//                        // stockList.add(object.getString("customerName"));
//                        productList.add(object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
//                    }
//                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, productList);
//                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
//                    spProduct.setAdapter(adapter);
//                    hideDialog();
//                    spProduct.setSelection(1);
//                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spProduct.getSelectedItemId() > 0) {
//                                Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//
//                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//
//                                LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                //LoadWhouseByCmpIdAndProID(id);
//                                //LoadStockByPersonID(id);
//                                //LoadCustomerType();
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
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    hideDialog();
//                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
//                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//           /* @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("UserName", email);
//                params.put("Password", password);
//                return params;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap<String, String>();
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                //headers.put("Content-Type", "application/json");
//                // Removed this line if you dont need it or Use application/json
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                headers.put("Content-Type", "application/json; charset=utf-8;");
//                //headers.put("Token", AppConfig.Token);
//                return headers;
//            }*/
//        };
//        //Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }
//    public void GetProductByModelPinMetter(String Pin, String Metter, String model) {
//        progressDialog.setMessage("Loading ...");
//        showDialog();
//
//        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.GetProductByModelPinmeter + Pin +"/" + Metter +"/" + model +"/", null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try {
//                    productLists = new ArrayList<>();
//                    productList = new ArrayList<String>();
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("ProductID", "0");
//                    map.put("ProductName", "Select Product");
//                    //map.put("CustomerCode", "Customer Code");
//                    productLists.add(map);
//                    productList.add("Select Product");
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject object = response.getJSONObject(i);
//                        map = new HashMap<>();
//                        //Log.d(TAG, "Response: " + object.toString());
//                        map.put("ProductID", object.getString("ProductID"));
//                        //  map.put("CustomerName", object.getString("customerName"));
//                        map.put("ProductName", object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
//                        //map.put("CustomerCode", object.getString("CustomerCode"));
//                        productLists.add(map);
//                        // stockList.add(object.getString("customerName"));
//                        productList.add(object.getString("ProductName"));// + " " + object.getString("CustomerCode"));
//                    }
//                    //searchable spinner>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppController.getContext(), R.layout.spinner_text_colour, productList);
//                    adapter.setDropDownViewResource(R.layout.dropdownlist_style);
//                    spProduct.setAdapter(adapter);
//                    hideDialog();
//                    spProduct.setSelection(1);
//                    spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spProduct.getSelectedItemId() > 0) {
//                                Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                                int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//
//                                //Map<String, Object> selectPruductID = productLists.get(spProduct.getSelectedItemPosition());
//                                //int id = Integer.valueOf(selectPruductID.get("ProductID").toString());
//                                LoadProductInfoByID(id);
//                                LoadStockByProduectID(id,4);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                //LoadWhouseByCmpIdAndProID(id);
//                                //LoadStockByPersonID(id);
//                                //LoadCustomerType();
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
//                } catch (JSONException e) {
//                    // JSON error
//                    e.printStackTrace();
//                    hideDialog();
//                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
//                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
//            }
//        }) {
//           /* @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("UserName", email);
//                params.put("Password", password);
//                return params;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap<String, String>();
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                //headers.put("Content-Type", "application/json");
//                // Removed this line if you dont need it or Use application/json
//                //headers.put("Content-Type", "application/x-www-form-urlencoded");
//                headers.put("Content-Type", "application/json; charset=utf-8;");
//                //headers.put("Token", AppConfig.Token);
//                return headers;
//            }*/
//        };
//        //Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }

    public void LoadProductInfoByID(int id) {

        Log.e("ProductInfo", "URL_ProductInfoByID" + AppConfig.URL_ProductInfoByID + id);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_ProductInfoByID + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {

                    dPrice = (response.getString("DealerPrice"));
                    wPrice = (response.getString("WholeSalePrice"));
                    rPrice = (response.getString("RegularMRP"));
                    if(btnAddList.getText().length()==3)
                    {
                        price.setText(response.getString("RegularMRP"));
                    }

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
    public void LoadStockByProduectID(long id, long Wid) {

        Log.e("Person", "URL_StockByProID" + AppConfig.URL_StockByProID + id + "/" + Wid);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_StockByProID + id +"/"+ Wid, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    // String stock = String.valueOf(response.getInt(Integer.parseInt("Code")));
                    //String stock = response.getString("Code");
                    //Toast.makeText(Activity_Adjustment.this, "Stock"+stock, Toast.LENGTH_SHORT).show();
                    Stock= Integer.parseInt(response.getString("Code"));
                    //stock.setText(response.getString("Code"));
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
        qut.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //            "apple".substring(12);
            public void afterTextChanged(Editable s) {
//                double x = (Double.parseDouble((total.getText().length() > 0) ? total.getText().toString() : "0") + Double.parseDouble((previousDue.getText().length() > 0) ? previousDue.getText().toString() : "0")) - (Double.parseDouble((OverLess.getText().length() > 0) ? OverLess.getText().toString() : "0"));
//                PayAmount.setText(String.format("%.2f", x));
                    double x = Double.parseDouble((qut.getText().length() > 0) ? qut.getText().toString() : "1");
                    if(x>Stock){
                        Toast.makeText(AppController.getContext(), "Please lass quantity", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if(x>=2 && x<=4){
                            price.setText(wPrice);
                        }
                        else if(x>=5) {
                            price.setText(dPrice);
                        }
                        else {
                            price.setText(rPrice);
                        }
                    }
            }
        });
    }
    public void AddToList() {
        double q = Double.parseDouble(qut.getText().toString());
        if(q>Stock){
            Toast.makeText(AppController.getContext(), "Please lass quantity", Toast.LENGTH_SHORT).show();
            //btnAddList.setEnabled(false);
        }
        else {
            //btnAddList.setEnabled(true);
            orderDetailsController = new OrderDetailsController();
            orderDetailsModel = new OrderDetails();
            //----------------
            int rows = 0;
            Cursor cursor = orderDetailsController.getData();
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
            //String aa=btnAddList.getText().toString();
            if (btnAddList.length() == 3)
            {
                if (spProduct.getSelectedItemPosition() == 0) {// spWareHouse.getSelectedItemPosition() == 0 ||
                    if(spProduct.getSelectedItemPosition() == 0)
                    {
                        Toast.makeText(AppController.getContext(), "Select Product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (price.length() > 0 && qut.length() > 0) {
                        //-----select data from spinner by id------//
                        Map<String, Object> selectedItem = null;
                        selectedItem = productLists.get(spProduct.getSelectedItemPosition());
                        orderDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                        //--------------^^^^^^-------------//
                        orderDetailsModel.setProductID(String.valueOf(selectedItem.get("ProductID")));
                        int proID= Integer.parseInt((String.valueOf(selectedItem.get("ProductID"))));
                        orderDetailsModel.setOpeningQty(String.valueOf(Model));
                        //orderDetailsModel.setW("1");
                        orderDetailsModel.setReturnQty(String.valueOf(meter));

                        //double t = Double.parseDouble((price.getText().length() > 0) ? price.getText().toString() : "0") * Double.parseDouble((qut.getText().length() > 0) ? qut.getText().toString() : "0" );
                        //total.setText(String.valueOf(t));

                        double a = Double.parseDouble(price.getText().toString());
                        double b = Double.parseDouble(qut.getText().toString());

                        if (total.length() > 0) {
                            gt = Double.parseDouble(total.getText().toString());
                        } else {
                            gt = ListTotal;;
                            //gt = 0;
                        }

                        t = a*b;
                        gt = gt + t;
                        orderDetailsModel.setQuantity(qut.getText().toString());
                        orderDetailsModel.setPrice(price.getText().toString());
                        orderDetailsModel.setTotal(String.valueOf(t));

                        orderDetailsController = new OrderDetailsController();
                        List<OrderDetails> orderDetails = orderDetailsController.GetByProID(proID);
                        if(orderDetails.size()>0)
                        {
                            Toast.makeText(AppController.getContext(), "Product is already in list", Toast.LENGTH_LONG).show();
                        }
                        else {
                            //---------check model-----------
                            double qty=0,avgPrice=0;
                            List<OrderDetails> orderDetails1 = orderDetailsController.GetByModelMeter(Model,meter);
                            if(orderDetails1.size()>0)
                            {
                                for (int c=0; c<orderDetails1.size(); c++)
                                {
                                    qty = qty + Double.parseDouble((orderDetails1.get(0).getQuantity()));
                                }
                                qty= qty + Double.parseDouble(qut.getText().toString());
                                if(qty>=2 && qty<=4){
                                    orderDetailsModel.setPrice(wPrice);
                                    avgPrice = Double.parseDouble(wPrice);
                                    //price.setText(wPrice);
                                }else if(qty>=5) {
                                    orderDetailsModel.setPrice(dPrice);
                                    avgPrice = Double.parseDouble(dPrice);
                                    //price.setText(dPrice);
                                }else {
                                    orderDetailsModel.setPrice(rPrice);
                                    avgPrice = Double.parseDouble(rPrice);
                                    //price.setText(rPrice);
                                }

                                orderDetailsModel.setTotal(String.valueOf(b*avgPrice));

                                //----------Update Price By Model and Meter---------
                                orderDetailsController.UpdateModel(avgPrice,Model,meter);

                                //----------------
                                int rows1 = 0;
                                double ListTotal1=0;
                                Cursor cursor1 = orderDetailsController.getData();
                                cursor1.moveToFirst();
                                int aa1=cursor1.getCount();
                                //float ListTotal=0;
                                while (rows1 < aa1) {
                                    double tot1 = cursor1.getInt(cursor1.getColumnIndexOrThrow("Total"));
                                    ListTotal1 =ListTotal1 + tot1;
                                    aa1--;
                                    cursor1.moveToNext();
                                }
                                //----------------


                                total.setText(String.format("%.2f", ListTotal1 + (b*avgPrice)));
                                int id = orderDetailsController.Insert(orderDetailsModel);
                                if (id > 0) {
                                    Toast.makeText(AppController.getContext(), "Product added", Toast.LENGTH_LONG).show();
                                    ShowList();
                                    Clear();
                                } else {
                                    Toast.makeText(AppController.getContext(), "Failed to add Product", Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                total.setText(String.format("%.2f", gt));
                                int id = orderDetailsController.Insert(orderDetailsModel);
                                if (id > 0) {
                                    Toast.makeText(AppController.getContext(), "Product added", Toast.LENGTH_LONG).show();
                                    ShowList();
                                    Clear();
                                } else {
                                    Toast.makeText(AppController.getContext(), "Failed to add Product", Toast.LENGTH_LONG).show();
                                }
                            }
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
            }
            else {

                double a = Double.parseDouble(price.getText().toString());
                double b = Double.parseDouble(qut.getText().toString());
                t=a*b;
                if (total.length() > 0) {
                    gt = Double.parseDouble(total.getText().toString())+ t;
                } else {
                    gt = ListTotal + t;
                }

                //gt = gt + t;

                orderDetailsModel.setSLID(listID);
                //country.setCountryName(cntName.getText().toString());
                orderDetailsModel.setQuantity(qut.getText().toString());
                orderDetailsModel.setPrice(price.getText().toString());

                orderDetailsController = new OrderDetailsController();
                double qty=0,avgPrice=0;
                //List<OrderDetails> orderDetails1 = orderDetailsController.GetByModelMeter(Model1,meter1);
                List<OrderDetails> orderDetails1 = orderDetailsController.GetUpdateQuantity(Model1,meter1,proid2);
                if(orderDetails1.size()>=0)
                {
//                    if(orderDetails1.size()==1)
//                    {
                        //qty = Double.parseDouble(qut.getText().toString());
                        for (int c=0; c<orderDetails1.size(); c++)
                        {
                            qty = qty + Double.parseDouble((orderDetails1.get(0).getQuantity()));
                        }
                        qty= qty + Double.parseDouble(qut.getText().toString());
                    //}
//                    else {
//                        for (int c=0; c<orderDetails1.size(); c++)
//                        {
//                            qty = qty + Double.parseDouble((orderDetails1.get(0).getQuantity()));
//                        }
//                        qty= qty + Double.parseDouble(qut.getText().toString());
//                    }
                    LoadProductInfoByID(proid2);
                    if(qty>=2 && qty<=4){
                        orderDetailsModel.setPrice(wPrice);
                        avgPrice = Double.parseDouble(wPrice);
                        //price.setText(wPrice);
                    }else if(qty>=5) {
                        orderDetailsModel.setPrice(dPrice);
                        avgPrice = Double.parseDouble(dPrice);
                        //price.setText(dPrice);
                    }else {
                        orderDetailsModel.setPrice(rPrice);
                        avgPrice = Double.parseDouble(rPrice);
                        //price.setText(rPrice);
                    }

                    orderDetailsModel.setTotal(String.valueOf(b*avgPrice));

                    orderDetailsController.UpdateModel(avgPrice,Model1,meter1);


                    int id = orderDetailsController.Update(orderDetailsModel);

                    //----------------
                    int rows1 = 0;
                    double ListTotal1=0;
                    Cursor cursor1 = orderDetailsController.getData();
                    cursor1.moveToFirst();
                    int aa1=cursor1.getCount();
                    //float ListTotal=0;
                    while (rows1 < aa1) {
                        double tot1 = cursor1.getInt(cursor1.getColumnIndexOrThrow("Total"));
                        ListTotal1 =ListTotal1 + tot1;
                        aa1--;
                        cursor1.moveToNext();
                    }
                    //----------------
                    total.setText(String.format("%.2f", ListTotal1));


                    if (id > 0) {
                        Toast.makeText(AppController.getContext(), "Product Updated", Toast.LENGTH_LONG).show();
                        btnAddList.setText("Add");
                        btnReset.setText("Reset");
                        Clear();
                        ShowList();
                    } else {
                        Toast.makeText(AppController.getContext(), "Failed to update Product", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    orderDetailsModel.setTotal(String.valueOf(t));
                    total.setText(String.format("%.2f", gt));
                    int id = orderDetailsController.Update(orderDetailsModel);
                    if (id > 0) {
                        Toast.makeText(AppController.getContext(), "Product Updated", Toast.LENGTH_LONG).show();
                        btnAddList.setText("Add");
                        btnReset.setText("Reset");
                        Clear();
                        ShowList();
                    } else {
                        Toast.makeText(AppController.getContext(), "Failed to update Product", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
    public void SaveFinally() {
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();

        progressDialog.setMessage("Order in process ...");
        showDialog();

        orderDetailsController = new OrderDetailsController();
        ArrayList<HashMap<String, String>> InformationLists = orderDetailsController.getAllList();
        Log.d("InformationLists:", InformationLists.toString());

        if (InformationLists.size() != 0) {
            JSONObject Obj = new JSONObject();
            try {
                //Add string params
                //Obj.put(ProfileINfo.KEY_companyID,CompanyID.getText());
                Obj.put("CompanyID", sessionList.get(0).getCompanyID());
                Obj.put("Sales_Date", getDateTime());
//            Map<String, Object> selectedItem = null;
//            selectedItem = customerList.get(spCusName.getSelectedItemPosition());
                //purchaseDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                Obj.put("CustomerID", sessionList.get(0).getUID());
                Obj.put("InvoiceNo", "");
                Obj.put("InvoiceDate", getDateTime());
                Obj.put("Job_By", sessionList.get(0).getEmail());
                Obj.put("SaleStatus", 0);
                Obj.put("Remarks", "Order From Customer by APK");
                Obj.put("Image", s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Create json array for filter
            JSONArray array = new JSONArray();

            //Create json objects for two filter Ids

            int rows = 0;
            orderDetailsController = new OrderDetailsController();
            Cursor cursor = orderDetailsController.getData();

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
                hideDialog();
                Toast.makeText(AppController.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_Order, Obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(ContentValues.TAG, "response" + response.toString());
                    hideDialog();
                    Toast.makeText(AppController.getContext(), "Order Successfully.", Toast.LENGTH_SHORT).show();
                    //ClearAll();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    hideDialog();
                    Toast.makeText(AppController.getContext(), "Failed To Order.", Toast.LENGTH_SHORT).show();
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
            ClearAll();
        }
        else {
            hideDialog();
            Toast.makeText(AppController.getContext(), "Product not found for order", Toast.LENGTH_SHORT).show();
        }
    }
    public void ShowList() {

        orderDetailsController = new OrderDetailsController();

        ArrayList<HashMap<String, String>> InformationLists = orderDetailsController.getAllList();
        Log.d("InformationLists:", InformationLists.toString());

        if (InformationLists.size() != 0) {

            ListView lv = getListView();
//            orderDetailsController = new OrderDetailsController();
//            List<OrderDetails> orderDetails = orderDetailsController.GetByProID(Model);
//            if(orderDetails.size()>0)
//            {
//            }
            if (btnReset.length() < 6 && btnAddList.length() < 6) {
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        txtID = view.findViewById(R.id.txtSLID);
                        ProductID = view.findViewById(R.id.txtSLID1);
                        int selectedID = Integer.parseInt(txtID.getText().toString());
                        int selectedProductID = Integer.parseInt(ProductID.getText().toString());
                        listID = String.valueOf(selectedID);
                        PID = String.valueOf(selectedProductID);

                        //Toast.makeText(AppController.getContext(), "selectedID" + selectedID, Toast.LENGTH_SHORT).show();

                        double b = 0, c = 0;
                        orderDetailsController = new OrderDetailsController();
                        List<OrderDetails> orderDetails = orderDetailsController.GetByID(selectedID);
                        Log.d("ww: ", "Retriving Info..");
                        if (orderDetails.size() != 0) {
                            b = Double.parseDouble(orderDetails.get(0).getTotal());
                            qut.setText(orderDetails.get(0).getQuantity());
                            price.setText(orderDetails.get(0).getPrice());

                            LoadProductInfoByID(Integer.parseInt(PID));

                            btnAddList.setText("Update");
                            btnReset.setText("Delete");
                            product = orderDetails.get(0).getProductName();
                            Model1= Integer.parseInt(orderDetails.get(0).getOpeningQty());
                            meter1= Integer.parseInt(orderDetails.get(0).getReturnQty());
                            proid2= Integer.parseInt(orderDetails.get(0).getProductID());
                            ListView lv = getListView();
                            lv.setEnabled(false);
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
                        //ShowList();
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

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.order_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
            setListAdapter(adapter);
        } else {
            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.order_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
            setListAdapter(adapter);
        }
    }
    public void ShowListOnly() {

        orderDetailsController = new OrderDetailsController();

        ArrayList<HashMap<String, String>> InformationLists = orderDetailsController.getAllList();
        Log.d("InformationLists:", InformationLists.toString());

        if (InformationLists.size() != 0) {

            //----------------
            int rows = 0;
            Cursor cursor = orderDetailsController.getData();
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
            total.setText(String.format("%.2f", ListTotal));

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.order_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
            setListAdapter(adapter);
        } else {

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.order_item_view, new
                    String[]{"SLID", "ProductName", "ProductID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Total"},
                    new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
            setListAdapter(adapter);
        }
    }
    public void DeleteListData() {
        if (btnReset.length() == 6) {
            int selectedID = Integer.parseInt(txtID.getText().toString());
            orderDetailsController = new OrderDetailsController();
            orderDetailsController.Delete(selectedID);

            double qty=0,avgPrice=0;
            //List<OrderDetails> orderDetails1 = orderDetailsController.GetByModelMeter(Model1,meter1);
            List<OrderDetails> orderDetails = orderDetailsController.GetUpdateQuantity(Model1,meter1,proid2);
            if(orderDetails.size()>=0)
            {
                for (int c=0; c<orderDetails.size(); c++)
                {
                    qty = qty + Double.parseDouble((orderDetails.get(0).getQuantity()));
                }
                LoadProductInfoByID(proid2);
                if(qty>=2 && qty<=4){
                    orderDetailsModel.setPrice(wPrice);
                    avgPrice = Double.parseDouble(wPrice);
                    //price.setText(wPrice);
                }else if(qty>=5) {
                    orderDetailsModel.setPrice(dPrice);
                    avgPrice = Double.parseDouble(dPrice);
                    //price.setText(dPrice);
                }else {
                    orderDetailsModel.setPrice(rPrice);
                    avgPrice = Double.parseDouble(rPrice);
                    //price.setText(rPrice);
                }
                orderDetailsController.UpdateModel(avgPrice,Model1,meter1);

                //----------------
                int rows1 = 0;
                double ListTotal1=0;
                Cursor cursor1 = orderDetailsController.getData();
                cursor1.moveToFirst();
                int aa1=cursor1.getCount();
                //float ListTotal=0;
                while (rows1 < aa1) {
                    double tot1 = cursor1.getInt(cursor1.getColumnIndexOrThrow("Total"));
                    ListTotal1 =ListTotal1 + tot1;
                    aa1--;
                    cursor1.moveToNext();
                }
                //----------------
                total.setText(String.format("%.2f", ListTotal1));

            }


            ArrayList<HashMap<String, String>> InformationLists = orderDetailsController.getAllList();
            Log.d("InformationLists:", InformationLists.toString());
            if (InformationLists.size() != 0) {
                ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.order_item_view, new
                        String[]{"SLID", "ProductName", "ProductID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Total"},
                        new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
                setListAdapter(adapter);
            } else {
                ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.order_item_view, new
                        String[]{"SLID", "ProductName", "ProductID", "OpeningQty", "ReturnQty", "Price", "Quantity", "Total"},
                        new int[]{R.id.txtSLID, R.id.txtProduct, R.id.txtSLID1, R.id.txtSLID2, R.id.txtSLID3, R.id.txtPrice, R.id.txtQuantity, R.id.txtTotal});
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

    public void Clear() {
        //price.setText("");
        qut.setText("1");
    }
    public void ClearAll() {
        ListTotal=0;
        total.setText("");
        orderDetailsController = new OrderDetailsController();
        orderDetailsController.DeleteAll();
        ShowList();
        btnReset.setText("Reset");
        btnAddList.setText("Add");
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


package peash.esales.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
import java.text.ParseException;
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
import peash.esales.Controller.WarehouseSendDetailsController;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.SalesDetails;
import peash.esales.Models.SessionUser;
import peash.esales.Models.WarehouseSendDetails;
import Adi.esales.R;

import static java.util.Locale.US;
import static peash.esales.App.AppController.TAG;

public class WarehouseSend extends ListFragment {

    Calendar myCalendar = Calendar.getInstance();

    ProgressDialog progressDialog;
    private SessionManager session;
    private WarehouseSendDetails warehouseSendDetailsModel;
    private SessionUserController sessionUserController;
    private WarehouseSendDetailsController warehouseSendDetailsController;

    EditText date,stock,costPrice,qut;
    Spinner spProduct, spWsend, spWReceive,spModel,spPin,spMetter;
    Button btnAddList, btnReset, btnSave, btnCancel,btnAdd,btnRemove;
    TextView txtID;
    String listID;
    int xyz;

    private List<Map<String, Object>> productLists = new ArrayList<>();
    List<String> productList;
    private List<Map<String, Object>> modelLists = new ArrayList<>();
    List<String> modelList;
    private List<Map<String, Object>> pinLists = new ArrayList<>();
    List<String> pinList;
    private List<Map<String, Object>> metterists = new ArrayList<>();
    List<String> metterList;
    private List<Map<String, Object>> wearhouseList = new ArrayList<>();
    private List<Map<String, Object>> ReciveWarehouseLists = new ArrayList<>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.warehouse_send , container, false);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        date = v.findViewById(R.id.Date1);
        costPrice = v.findViewById(R.id.txtPrice);
        qut = v.findViewById(R.id.txtQuantity);
        stock = v.findViewById(R.id.txtStock);


        spProduct = v.findViewById(R.id.spProduct);
        spModel = v.findViewById(R.id.spModel);
        spPin = v.findViewById(R.id.spPin);
        spMetter = v.findViewById(R.id.spMetter);
        spWsend = v.findViewById(R.id.spWsend);
        spWReceive = v.findViewById(R.id.spWreceive);

        btnAddList = v.findViewById(R.id.btnAdd);
        btnReset = v.findViewById(R.id.btnReset);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnAdd = v.findViewById(R.id.btnAddQ);
        btnRemove = v.findViewById(R.id.btnRemove);


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
        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddToList();
                //TextChangeForPrice();
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
                Clear();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteListData();
                //Clear();
            }
        });

        //warehouseSendDetailsController = new WarehouseSendDetailsController();
        //warehouseSendDetailsController.DeleteAll();

        LoadDate();
        addOnModel();


        return v;
    }

    private void LoadDate() {

        String mydate = java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        date.setText(mydate);
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
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), dat, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    private void setPur_date() {

        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, US);
        date.setText(sdf.format(myCalendar.getTime()));
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
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
//                                //price.setText("");
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
//                                String id1 = selectPInID.get("SizeID").toString();
//
//                                GetProductByPin(id1,Model);
//                                //LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                //LoadWhouseByCmpIdAndProID(id);
//                                //LoadStockByPersonID(id);
//                                //LoadCustomerType();
//                                //price.setText("");
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
//                    map.put("ColorName", "Select Meter");
//                    //map.put("CustomerCode", "Customer Code");
//                    metterists.add(map);
//                    metterList.add("Select Meter");
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
//                    spMetter.setAdapter(adapter);
//                    hideDialog();
//                    spMetter.setSelection(1);
//                    spMetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                            if (spMetter.getSelectedItemId() > 0) {
//                                //String Model = spPin.getSelectedItem().toString();
//                                Map<String, Object> selectModel = modelLists.get(spModel.getSelectedItemPosition());
//                                String Model = selectModel.get("GenericID").toString();
//                                //int id = Integer.valueOf(Model);
//
//                                Map<String, Object> selectPInID = pinLists.get(spPin.getSelectedItemPosition());
//                                String pin = selectPInID.get("SizeID").toString();
//
//                                Map<String, Object> selectMetterID = metterists.get(spMetter.getSelectedItemPosition());
//                                String id1 = selectMetterID.get("ColorID").toString();
//
//                                //GetProductByMetter(id1, Model);
//
//                                if(spPin.getSelectedItemId()==0)
//                                {
//                                    GetProductByMetter(id1,Model);
//                                }
//                                else
//                                {
//                                    GetProductByModelPinMetter(pin,id1,Model);
//                                }
//
//
//                                //LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                //LoadWhouseByCmpIdAndProID(id);
//                                //LoadStockByPersonID(id);
//                                //LoadCustomerType();
//                                //price.setText("");
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
//        });
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
//                                //LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                LoadWhouseByCmpIdAndProID(id);
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
//        });
//        //Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }
//    public void GetProductByMetter(String Metter, String model) {
//        progressDialog.setMessage("Loading ...");
//        showDialog();
//
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
//                                //LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                LoadWhouseByCmpIdAndProID(id);
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
//        });
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
//                                //LoadProductInfoByID(id);
//                                //String id1 = String.valueOf(id);
//                                //LoadPersonInfoByID(id1);
//                                LoadWhouseByCmpIdAndProID(id);
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
//        });
//        //Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, null);
//    }

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
                                costPrice.setText("");
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
                                costPrice.setText("");
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

                                costPrice.setText("");
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
                                //LoadProductInfoByID(id);
                                //String id1 = String.valueOf(id);
                                //LoadPersonInfoByID(id1);
                                LoadWhouseByCmpIdAndProID(id);
                                //LoadStockByPersonID(id);
                                //LoadCustomerType();
                                costPrice.setText("");
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
                        spWsend.setAdapter(arrayAdapter);

                        String myString="{"+"WarehouseID"+"=1, "+"Name"+"="+"Shilpihal Market"+"}";
                        //Spinner s = (Spinner) findViewById(R.id.spinner_id);
                        for(int i=0; i < arrayAdapter.getCount(); i++) {
                            String x= arrayAdapter.getItem(i).toString();
                            if(myString.trim().equals(arrayAdapter.getItem(i).toString())){
                                spWsend.setSelection(i);
                                break;
                            }
                        }

                        spWsend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (spWsend.getSelectedItemId() > 0) {
                                    Map<String, Object> selectwearhouseID = wearhouseList.get(spWsend.getSelectedItemPosition());
                                    int id1 = Integer.valueOf(selectwearhouseID.get("WarehouseID").toString());

                                    Map<String, Object> selectProductID = productLists.get(spProduct.getSelectedItemPosition());
                                    int idP = Integer.valueOf(selectProductID.get("ProductID").toString());

                                    //long Pid=spProduct.getSelectedItemId();
                                    //long wid=spWareHouse.getSelectedItemId();
                                    //String id1 = String.valueOf(id);
                                    //LoadPersonInfoByID(id1);
                                    btnAddList.setEnabled(true);
                                    LoadStockByPersonID(idP,id1);
                                    GetByReciveWarehouse(id1);

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
                    Toast.makeText(getContext(), "Warehouse not founds", Toast.LENGTH_SHORT).show();
                    btnAddList.setEnabled(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Warehouse: " + error.getMessage());
                //Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();


            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
    public void LoadStockByPersonID(long id, long Wid) {

        Log.e("Stock", "URL_StockByProID" + AppConfig.URL_StockByProID + id + "/" + Wid);
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

    public void GetByReciveWarehouse(int wharehouseID) {
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetByReciveWarehouse + wharehouseID, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                if(response.length()>0)
                {
                    try {
                        Log.e("response", "" + response.toString());

                        ReciveWarehouseLists = new ArrayList<>();

                        Map<String, Object> map = new HashMap<>();
                        map.put("WarehouseID", "0");
                        map.put("Name", "Select Warehouse");
                        ReciveWarehouseLists.add(map);

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);
                            map = new HashMap<>();
                            map.put("WarehouseID", object.getString("WarehouseID"));
                            map.put("Name", object.getString("Name"));
                            ReciveWarehouseLists.add(map);
                        }
                        SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), ReciveWarehouseLists, R.layout.spinner_text_colour,
                                new String[]{"Name"}, new int[]{android.R.id.text1});
                        spWReceive.setAdapter(arrayAdapter);

                        String myString="{"+"WarehouseID"+"=4, "+"Name"+"="+"Showroom"+"}";
                        //Spinner s = (Spinner) findViewById(R.id.spinner_id);
                        for(int i=0; i < arrayAdapter.getCount(); i++) {
                            String x= arrayAdapter.getItem(i).toString();
                            if(myString.trim().equals(arrayAdapter.getItem(i).toString())){
                                spWReceive.setSelection(i);
                                break;
                            }
//                            else {
//                                Toast.makeText(AppController.getContext(), "Showroom Not Found", Toast.LENGTH_SHORT).show();
//                            }
                        }


                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Warehouse not founds", Toast.LENGTH_SHORT).show();
                    btnAddList.setEnabled(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Warehouse: " + error.getMessage());
                //Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();


            }
        });
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void AddToList() {
        if (btnAddList.length() == 3)
        {
            if(spProduct.getSelectedItemId()>0 && spWsend.getSelectedItemId()>0 && spWReceive.getSelectedItemId()>0)
            {
                int x = Integer.parseInt(qut.getText().toString());
                int y = Integer.parseInt(stock.getText().toString());
                if (x>y)
                {
                    qut.requestFocus();
                    qut.setError("Quantity is grater then stock");
                }
                else
                {
                    warehouseSendDetailsController = new WarehouseSendDetailsController();
                    warehouseSendDetailsModel = new WarehouseSendDetails();
                    warehouseSendDetailsModel.setDate(date.getText().toString());
                    //-----select data from spinner by id------//
                    Map<String, Object> selectedItem = null;
                    selectedItem = productLists.get(spProduct.getSelectedItemPosition());
                    warehouseSendDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                    //--------------^^^^^^-------------//
                    warehouseSendDetailsModel.setProductID(String.valueOf(selectedItem.get("ProductID")));
                    int proID= Integer.parseInt((String.valueOf(selectedItem.get("ProductID"))));

                    selectedItem = wearhouseList.get(spWsend.getSelectedItemPosition());
                    warehouseSendDetailsModel.setFromWarehouseID(String.valueOf(selectedItem.get("WarehouseID")));
                    selectedItem = ReciveWarehouseLists.get(spWReceive.getSelectedItemPosition());
                    warehouseSendDetailsModel.setToWarehouseID(String.valueOf(selectedItem.get("WarehouseID")));
                    warehouseSendDetailsModel.setCostPrice(costPrice.getText().toString());
                    warehouseSendDetailsModel.setQuantity(qut.getText().toString());

                    List<WarehouseSendDetails> warehouseSendDetails = warehouseSendDetailsController.GetByProID(proID);
                    if(warehouseSendDetails.size()>0)
                    {
                        Toast.makeText(AppController.getContext(), "Product is already in list", Toast.LENGTH_LONG).show();
                    }
                    else {
                        int id = warehouseSendDetailsController.Insert(warehouseSendDetailsModel);
                        if (id > 0) {
                            Toast.makeText(AppController.getContext(), "Product added", Toast.LENGTH_LONG).show();
                            //Clear();
                            ShowList();
                        } else {
                            Toast.makeText(AppController.getContext(), "Product fail to add", Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }
            else {
                if (spProduct.getSelectedItemId()==0)
                {
                    Toast.makeText(getContext(), "Select Product", Toast.LENGTH_SHORT).show();
                }else if (spWsend.getSelectedItemId()==0)
                {
                    Toast.makeText(getContext(), "Select Sender Warehouse", Toast.LENGTH_SHORT).show();
                }if (spWReceive.getSelectedItemId()==0)
                {
                    Toast.makeText(getContext(), "Select Receiver Warehouse", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            warehouseSendDetailsModel.setSLID(listID);
            warehouseSendDetailsModel.setQuantity(qut.getText().toString());
            warehouseSendDetailsModel.setCostPrice(costPrice.getText().toString());

            int id = warehouseSendDetailsController.Update(warehouseSendDetailsModel);
            if (id > 0) {
                Toast.makeText(AppController.getContext(), "Product Updated", Toast.LENGTH_LONG).show();
//            Intent objIntent = new Intent(getApplicationContext(), ItemCardList.class);
//            objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(objIntent);
                btnAddList.setText("Add");
                btnReset.setText("Reset");
                //Clear();
                ShowList();
            } else {
                Toast.makeText(AppController.getContext(), "Product Update Fail", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void ShowList() {

        warehouseSendDetailsController = new WarehouseSendDetailsController();
        ArrayList<HashMap<String, String>> InformationLists = warehouseSendDetailsController.getAllList();
        Log.d("InformationLists:", InformationLists.toString());

        if (InformationLists.size() != 0) {
            xyz=InformationLists.size();

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
                        warehouseSendDetailsController = new WarehouseSendDetailsController();
                        List<WarehouseSendDetails> salesDetails = warehouseSendDetailsController.GetByID(selectedID);
                        Log.d("ww: ", "Retriving Info..");
                        if (salesDetails.size() != 0) {
                            qut.setText(salesDetails.get(0).getQuantity());
                            costPrice.setText(salesDetails.get(0).getCostPrice());
                            btnAddList.setText("Update");
                            btnReset.setText("Delete");
                        }

                        ShowList();
                    }
                });
                lv.setEnabled(true);
            } else {
                lv.setEnabled(false);
            }

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.wsend_item_view, new
                    String[]{"SLID","Date", "ProductName", "ProductID", "FromWarehouseID", "ToWarehouseID", "Price", "Quantity"},
                    new int[]{R.id.txtSLID, R.id.txtDate, R.id.txtProduct, R.id.txtProductId, R.id.txtWsend, R.id.txtWreceiveId, R.id.txtPrice, R.id.txtQty});
            //((SimpleAdapter) adapter).setDropDownViewResource(R.layout.spinner_text_colour);
            setListAdapter(adapter);
        } else {
            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            ListAdapter adapter = new SimpleAdapter(AppController.getContext(), InformationLists, R.layout.wsend_item_view, new
                    String[]{"SLID","Date", "ProductName", "ProductID", "FromWarehouseID", "ToWarehouseID", "Price", "Quantity"},
                    new int[]{R.id.txtSLID, R.id.txtDate, R.id.txtProduct, R.id.txtProductId, R.id.txtWsend, R.id.txtWreceiveId, R.id.txtPrice, R.id.txtQty});
            setListAdapter(adapter);
        }
    }

    private void save(){
        warehouseSendDetailsController = new WarehouseSendDetailsController();
        ArrayList<HashMap<String, String>> InformationLists = warehouseSendDetailsController.getAllList();
        Log.d("InformationLists:", InformationLists.toString());

        if (InformationLists.size() != 0) {
            sessionUserController = new SessionUserController();
            List<SessionUser> sessionList = sessionUserController.GetAll();
            JSONObject Obj = new JSONObject();
            progressDialog.setMessage("Processing ...");
            showDialog();
            try {
                Obj.put("WSRID",0);
                Obj.put("SRDate", date.getText().toString());
                //-----select data from spinner by id------//
                Map<String, Object> selectedItem = null;
                selectedItem = wearhouseList.get(spWsend.getSelectedItemPosition());
                //purchaseDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                Obj.put("WarehouseSendID", (String.valueOf(selectedItem.get("WarehouseID"))));

                selectedItem = ReciveWarehouseLists.get(spWReceive.getSelectedItemPosition());
                //purchaseDetailsModel.setProductName(String.valueOf(selectedItem.get("ProductName")));
                Obj.put("WarehouseReciveID", (String.valueOf(selectedItem.get("WarehouseID"))));
                Obj.put("CompanyID", sessionList.get(0).getCompanyID());
                Obj.put("ReciveConfirm", 0);
                Obj.put("Remarks","APK");
                Obj.put("Creator", sessionList.get(0).getEmail());
                Obj.put("CreationDate", getDateTime());
                Obj.put("Modifier", sessionList.get(0).getEmail());
                Obj.put("ModificationDate", getDateTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Create json array for filter
            JSONArray array = new JSONArray();

            //Create json objects for two filter Ids

            int rows = 0;
            warehouseSendDetailsController = new WarehouseSendDetailsController();
            Cursor cursor = warehouseSendDetailsController.getData();

            cursor.moveToFirst();
            int a = cursor.getCount();

            while (rows < a) {
                JSONObject Param1 = new JSONObject();
                try {
                    Param1.put("WSRDetailsID", 0);
                    Param1.put("WSRID", 0);
                    Param1.put("ProductID", cursor.getInt(cursor.getColumnIndexOrThrow("ProductID")));
                    Param1.put("Quantity", cursor.getInt(cursor.getColumnIndexOrThrow("Quantity")));
                    Param1.put("Rate", cursor.getInt(cursor.getColumnIndexOrThrow("Price")));
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
                Obj.put("WarehouseSendReciveDetail", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_AddWarehouseSend, Obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(ContentValues.TAG, "response" + response.toString());
                    Toast.makeText(AppController.getContext(), "Warehouse Send Fall.", Toast.LENGTH_SHORT).show();
                    hideDialog();
                    //warehouseSendDetailsController = new WarehouseSendDetailsController();
                    //warehouseSendDetailsController.DeleteAll();
                    //ShowList();

                    //Clear();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AppController.getContext(), "Warehouse Send Successful.", Toast.LENGTH_SHORT).show();
                    hideDialog();
                    warehouseSendDetailsController = new WarehouseSendDetailsController();
                    warehouseSendDetailsController.DeleteAll();
                    ShowList();
                    Clear();
                    invoice();
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
        else
        {
            Toast.makeText(getContext(), "There has no data for save", Toast.LENGTH_SHORT).show();
        }
    }

    public void invoice(){
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/WhInvoice/")));

    }

    public void DeleteListData() {
        if (btnReset.length() == 6) {
            int selectedID = Integer.parseInt(txtID.getText().toString());
            warehouseSendDetailsController = new WarehouseSendDetailsController();
            warehouseSendDetailsController.Delete(selectedID);
            ArrayList<HashMap<String, String>> InformationLists = warehouseSendDetailsController.getAllList();
            Log.d("InformationLists:", InformationLists.toString());
            ShowList();
            btnReset.setText("Reset");
            btnAddList.setText("Add");
            //Clear();
            ShowList();
            qut.setText("1");
            ListView lv = getListView();
            lv.setEnabled(true);
        }
        else {

        }
    }

    public void Clear(){
        warehouseSendDetailsController = new WarehouseSendDetailsController();
        warehouseSendDetailsController.DeleteAll();
        ShowList();
        qut.setText("1");
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


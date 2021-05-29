package peash.esales.Fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.Calendar;
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

public class OrderDetailsFragment extends ListFragment {

    Calendar myCalendar = Calendar.getInstance();

    ProgressDialog progressDialog;
    private SessionManager session;
    private OrderDetails orderDetailsModel;
    private SessionUserController sessionUserController;
    private OrderDetailsController orderDetailsController;
    EditText date1, price, qut,total,Mdate;
    Spinner spProduct;
    Button btnDelivered, btnAddList, btnReset, btnSave, btnOrder,btnAdd,btnRemove;
    TextView txtID,productID,PdtPrice,PdtQut,totalAmount;
    float z=0;
    private double t = 0, gt = 0;
    String CustID,SalesID;
    String listID,PID,pric,qt,dPrice,rPrice;
    LinearLayout LLPrice,LLQuentity,LLButton;

    private List<Map<String, Object>> orderList = new ArrayList<>();
    private List<Map<String, Object>> productLists = new ArrayList<>();
    List<String> productList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.order_details_fragment, container, false);

        CustID = getArguments().getString("CustID");
        SalesID = getArguments().getString("SalesID");


        btnDelivered = v.findViewById(R.id.btnSave);
        btnOrder = v.findViewById(R.id.btnOrder);
        btnAddList = v.findViewById(R.id.btnAdd);
        btnReset = v.findViewById(R.id.btnReset);
        btnAdd = v.findViewById(R.id.btnAddQ);
        btnRemove = v.findViewById(R.id.btnRemove);

        totalAmount = v.findViewById(R.id.totalAmount);

        price = v.findViewById(R.id.txtPrice);
        qut = v.findViewById(R.id.txtQuantity);

        LLPrice = v.findViewById(R.id.LLPrice);
        LLQuentity = v.findViewById(R.id.LLQuantity);
        LLButton = v.findViewById(R.id.LLButton);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        ShowOrderList(CustID,SalesID);

        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnSave();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(qut.getText().toString());
                int b=0;

                if(a>=b)
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


                if(a==0)
                {
                    qut.setText("0");
                }
                else
                {
                    a= a-1;
                    qut.setText(Integer.toString(a));
                }
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryFragment orderFragment = new DeliveryFragment();
                android.support.v4.app.FragmentTransaction a5 = getActivity().getSupportFragmentManager().beginTransaction();
                a5.replace(R.id.frame,orderFragment);
                a5.commit();
            }
        });

        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateList();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteListData();
            }
        });

        TextChangeForPrice();

        return v;
    }


    public void UpdateList() {
        if (btnAddList.length() == 3) {

        }
        else {
            btnAddList.setText("Add");
            btnReset.setText("Reset");
            //LLPrice.setVisibility(View.GONE);
            LLQuentity.setVisibility(View.GONE);
            LLButton.setVisibility(View.GONE);
            String a= qut.getText().toString();
            UpdateQuentity(listID,a);
            ShowOrderList(CustID,SalesID);
        }
    }

    public  void DeleteListData() {
        if (btnReset.length() == 6) {
//            price.setText("");
//            qut.setText("1");
//            btnAddList.setText("Add");
//            btnReset.setText("Reset");
//            //LLPrice.setVisibility(View.GONE);
//            LLQuentity.setVisibility(View.GONE);
//            LLButton.setVisibility(View.GONE);
//            ShowOrderList(CustID,SalesID);
        }
        else
        {
        }
    }


    public void ShowOrderList(String ID, String SID) {
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();
        progressDialog.setMessage("Loading Order List...");
        showDialog();
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_OrderDetailsListByID + ID + "/" +SID, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(ContentValues.TAG, "List Of Data: " + response.toString());
                try {
                    orderList = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("SalesDetailID", object.getString("SalesDetailID"));
                        map.put("InvoiceNo", object.getString("InvoiceNo"));
                        map.put("ProductName", object.getString("ProductName"));
                        map.put("ProductID", object.getString("ProductID"));
                        map.put("Quantity", object.getString("Quantity"));
                        map.put("Rate", object.getString("Rate"));
                        orderList.add(map);
                    }
                    if(orderList.size()>0)
                    {
                        float c=0;z=0;
                        int a = orderList.size();
                        for (int x=0; x<a; x++)
                        {
                            Map<String, Object> selectQuentity = orderList.get(x);
                            float Qty = Float.parseFloat(selectQuentity.get("Quantity").toString());
                            float Rate = Float.parseFloat(selectQuentity.get("Rate").toString());
                            c = Qty*Rate;
                            z = (c+z);
                        }
                        String L = String.valueOf(z);
                        totalAmount.setText(L);
                    }

                    final ListView lv = getListView();
                    Log.e(ContentValues.TAG, "OrderList: " + orderList.toString());
                    final Map<String, Object> finalMap = map;
//                    if (btnReset.length() < 6 && btnAddList.length() < 6) {
//                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                                txtID = view.findViewById(R.id.txtInvoice);
//                                productID = view.findViewById(R.id.txtProductID);
//                                PdtPrice = view.findViewById(R.id.txtPrice);
//                                PdtQut = view.findViewById(R.id.txtQuantity);
//
//                                String selectedID = txtID.getText().toString();
//                                String selectedProductID = productID.getText().toString();
//                                float selectedPrice = Float.parseFloat(PdtPrice.getText().toString());
//                                int selectedQut = Integer.parseInt(PdtQut.getText().toString());
//                                listID = String.valueOf(selectedID);
//                                PID = String.valueOf(selectedProductID);
//                                //pric = String.valueOf(selectedPrice);
//                                qt = String.valueOf(selectedQut);
//                                Toast.makeText(AppController.getContext(), "Sales Detail ID is " + selectedID, Toast.LENGTH_SHORT).show();
//                                //LLPrice.setVisibility(View.VISIBLE);
//                                LoadProductInfoByID(PID);
//                                LLQuentity.setVisibility(View.VISIBLE);
//                                LLButton.setVisibility(View.VISIBLE);
//                                //price.setText(pric);
//                                qut.setText(qt);
//                                btnAddList.setText("Update");
//                                btnReset.setText("Delete");
//                                ListView lv = getListView();
//                                lv.setEnabled(false);
//
//                                ListAdapter Adapter = new SimpleAdapter(AppController.getContext(), orderList, R.layout.order_details_item_view, new
//                                        String[]{"SalesDetailID","InvoiceNo", "ProductName", "Quantity", "Rate", "ProductID"},
//                                        new int[]{R.id.txtInvoice, R.id.txtCusName, R.id.txtProudct, R.id.txtQuantity, R.id.txtPrice, R.id.txtProductID});
//                                ((SimpleAdapter) Adapter).setDropDownViewResource(R.layout.spinner_text_colour);
//                                setListAdapter(Adapter);
//                            }
//                        });
//                        lv.setEnabled(true);
//                    } else {
//                        lv.setEnabled(false);
//                    }

                    hideDialog();
                    ListAdapter Adapter = new SimpleAdapter(AppController.getContext(), orderList, R.layout.order_details_item_view, new
                            String[]{"SalesDetailID","InvoiceNo", "ProductName", "Quantity", "Rate", "ProductID"},
                            new int[]{R.id.txtInvoice, R.id.txtCusName, R.id.txtProudct, R.id.txtQuantity, R.id.txtPrice, R.id.txtProductID});
                    ((SimpleAdapter) Adapter).setDropDownViewResource(R.layout.spinner_text_colour);
                    setListAdapter(Adapter);
                } catch (JSONException e) {
//                    // JSON error
                    hideDialog();
//                    e.printStackTrace();
                    //Toast.makeText(AppController.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(AppController.getContext(), "Data Not Found.", Toast.LENGTH_SHORT).show();
                    ListView lv = getListView();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ContentValues.TAG, "Order List: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
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
                    if(x>4){
                        price.setText(dPrice);
                    }
                    else {
                        price.setText(rPrice);
                    }
            }
        });
    }

    public void LoadProductInfoByID(String id) {

        Log.e("ProductInfo", "URL_ProductInfoByID" + AppConfig.URL_ProductInfoByID + id);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_ProductInfoByID + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    dPrice = (response.getString("DealerPrice"));
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

    public void UpdateQuentity(String SID, String Qty){
        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_SalesDetailsUpdate + SID + "/" +Qty, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(AppController.getContext(), "Product Updated Successfully", Toast.LENGTH_SHORT).show();
                ShowOrderList(CustID,SalesID);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ContentValues.TAG, "Login Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                ShowOrderList(CustID,SalesID);
                hideDialog();
            }
        }) {
        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void BtnSave(){
            progressDialog.setMessage("Saving...");
            showDialog();

            JSONObject Obj = new JSONObject();
            try{
                sessionUserController=new SessionUserController();
                List<SessionUser>sessionList=sessionUserController.GetAll();
                //-----select data from spinner by id------//
                Map<String, Object> selectedItem = null;
                Obj.put("SalesID", SalesID);
                String x;
                if(price.getText().toString().length()<=0){
                    x = "0";
                }
                else {
                    x = price.getText().toString();
                }
                Obj.put("Vattax", x);
                Obj.put("CrAmount", z);

                JsonObjectRequest strReq1 = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_SalesStatusUpdate, Obj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(ContentValues.TAG, "response" + response.toString());
                        Toast.makeText(AppController.getContext(), "Order Successfully Delivered..", Toast.LENGTH_SHORT).show();
                        Delivery();
                        //Clear();
                        hideDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AppController.getContext(), "Failed To Delivery.", Toast.LENGTH_SHORT).show();
                        Log.e(ContentValues.TAG, "Error: " + error.getMessage());
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
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

    public void Delivery(){
        DeliveryFragment DF = new DeliveryFragment();
        android.support.v4.app.FragmentTransaction a3FragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        a3FragmentTransaction.replace(R.id.frame,DF);
        a3FragmentTransaction.commit();
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


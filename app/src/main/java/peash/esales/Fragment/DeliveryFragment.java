package peash.esales.Fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

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

public class DeliveryFragment extends ListFragment {

    Calendar myCalendar = Calendar.getInstance();

    ProgressDialog progressDialog;
    private SessionManager session;
    private OrderDetails orderDetailsModel;
    private SessionUserController sessionUserController;
    private OrderDetailsController orderDetailsController;
    EditText date1, price, qut,total,Mdate;
    Spinner spProduct;
    Button btnAddList, btnReset, btnSave, btnCancel;
    TextView CustID,SalesID;

    private double t = 0, gt = 0;
    String product;
    String CustIDs,SalesIDs;

    private List<Map<String, Object>> orderList = new ArrayList<>();
    private List<Map<String, Object>> productLists = new ArrayList<>();
    List<String> productList;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.delivery_fragment, container, false);

// ..................................... Pull refresh..............................................

        swipeContainer=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                ShowOrderList();

                // To keep animation for 3 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeContainer.setRefreshing(false);
                    }
                }, 3000); // Delay in millis
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


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        ShowOrderList();

        return v;
    }

    public void ShowOrderList() {
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();
        //BU = sessionList.get(0).getConSt();
        progressDialog.setMessage("Loading Order List...");
        showDialog();
        //String URL = AppConfig.URL_OrderList+"uname="+sessionList.get(0).getUname()+"&pass="+sessionList.get(0).getPass()+"&clsID="+clsId +"&groID="+groID+"&secID="+secID+"&date="+date;
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_OrderList, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(ContentValues.TAG, "List Of Data: " + response.toString());
                try {
                    orderList = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("SalesID", object.getString("SalesID"));
                        map.put("CustomerID", object.getString("CustomerID"));
                        map.put("CustomerName", object.getString("CustomerName"));
                        map.put("Mobile", object.getString("Mobile"));
                        map.put("Address", object.getString("Address"));
//                        map.put("InvoiceDate", object.getString("InvoiceDate"));
                        orderList.add(map);
                    }
                    final ListView lv = getListView();
                    Log.e(ContentValues.TAG, "OrderList: " + orderList.toString());
                    final Map<String, Object> finalMap = map;
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            CustID = view.findViewById(R.id.txtCustID);
                            int selectedID = Integer.parseInt(CustID.getText().toString());
                            SalesID = view.findViewById(R.id.txtSalesID);
                            int selectedSalesID = Integer.parseInt(SalesID.getText().toString());
                            CustIDs = String.valueOf(selectedID);
                            SalesIDs = String.valueOf(selectedSalesID);
                            //Toast.makeText(AppController.getContext(), "selectedID" + selectedID, Toast.LENGTH_SHORT).show();

                            //-----------------------------
                            Fragment ODF=new OrderDetailsFragment();
                            FragmentManager fm=getFragmentManager();
                            FragmentTransaction ft=fm.beginTransaction();
                            Bundle args = new Bundle();
                            args.putString("CustID", CustIDs);
                            args.putString("SalesID", SalesIDs);
                           // args.putString("temp", temp);
                            ODF.setArguments(args);
                            ft.replace(R.id.frame, ODF);
                            ft.commit();
                            //-------------------------------

                            ListAdapter Adapter = new SimpleAdapter(AppController.getContext(), orderList, R.layout.order_customer_item_view, new
                                    String[]{"SalesID","CustomerID","CustomerName","Address","Mobile"},
                                    new int[]{R.id.txtSalesID, R.id.txtCustID, R.id.txtCusName, R.id.txtAddress, R.id.txtPhoneNo});
                            ((SimpleAdapter) Adapter).setDropDownViewResource(R.layout.spinner_text_colour);
                            setListAdapter(Adapter);
                        }
                    });
                    hideDialog();
                    ListAdapter Adapter = new SimpleAdapter(AppController.getContext(), orderList, R.layout.order_customer_item_view, new
                            String[]{"SalesID","CustomerID","CustomerName","Address","Mobile"},
                            new int[]{R.id.txtSalesID, R.id.txtCustID, R.id.txtCusName, R.id.txtAddress, R.id.txtPhoneNo});
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



    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}


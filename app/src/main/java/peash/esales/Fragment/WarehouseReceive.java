package peash.esales.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
import peash.esales.Models.SessionUser;
import peash.esales.Models.WarehouseSendDetails;
import Adi.esales.R;

public class WarehouseReceive extends ListFragment {

    Calendar myCalendar = Calendar.getInstance();

    ProgressDialog progressDialog;
    private SessionManager session;
    private WarehouseSendDetails warehouseSendDetailsModel;
    private SessionUserController sessionUserController;
    private WarehouseSendDetailsController warehouseSendDetailsController;

    ListView list;
    TextView txtID;
    EditText txtRemarks;
    int wsrid, a=0;
    Button confirm,Cancel;

    private List<Map<String, Object>> warehouseSendReceiveList = new ArrayList<>();
    private List<Map<String, Object>> warehouseSendReceiveList1 = new ArrayList<>();

    private SwipeRefreshLayout swipeContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.warehouse_receive , container, false);

        //..................................... Pull refresh..............................................

        swipeContainer=(SwipeRefreshLayout)v.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                ShowList();

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


        sessionUserController=new SessionUserController();
        final List<SessionUser>sessionList=sessionUserController.GetAll();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);

        confirm = v.findViewById(R.id.btnConfirm);
        Cancel = v.findViewById(R.id.btnCancel);

        txtRemarks = v.findViewById(R.id.txtRemarks);

        list = v.findViewById(R.id.list);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a=1;
                if(warehouseSendReceiveList.size()!=0){
                    AlertDialog.Builder diaBox = AskOption();
                    diaBox.show();
                    //BtnConfirm(wsrid,sessionList.get(0).getUserName());
                }
                else {
                    Toast.makeText(getContext(), "No data for Confirmation", Toast.LENGTH_SHORT).show();
                }

            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(warehouseSendReceiveList.size()!=0){
                    AlertDialog.Builder diaBox = AskOption();
                    diaBox.show();
                }
                else {
                    Toast.makeText(getContext(), "No data for Cancel", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ShowList();
        //ShowList1();
        return v;
    }

    public void ShowList() {
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();
        //BU = sessionList.get(0).getConSt();
        progressDialog.setMessage("Loading Data...");
        showDialog();
        //String URL = AppConfig.UrlStudentList+"uname="+sessionList.get(0).getUname()+"&pass="+sessionList.get(0).getPass()+"&clsID="+clsId +"&groID="+groID+"&secID="+secID+"&date="+date;
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetAllWarehouseSendReciveInfo1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(ContentValues.TAG, "List Of Data: " + response.toString());
                try {
                    warehouseSendReceiveList1 = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("WSRID", object.getString("WSRID"));
                        //map.put("SRDate", object.getString("SRDate"));
                        map.put("SRDate", ConvertDateTime(object.getString("SRDate")));
                        map.put("WarehouseSend", object.getString("WarehouseSend"));
                        map.put("WarehouseRecive", object.getString("WarehouseRecive"));
                        warehouseSendReceiveList1.add(map);
                    }
                    ListView lv = getListView();
                    Log.e(ContentValues.TAG, "warehouseSendReceiveList1: " + warehouseSendReceiveList1.toString());
                    final Map<String, Object> finalMap = map;
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            txtID = view.findViewById(R.id.txtSLID);
                            int selectedID = Integer.parseInt(txtID.getText().toString());
                            //Toast.makeText(AppController.getContext(), "selectedID" + selectedID, Toast.LENGTH_SHORT).show();
                            ShowList1(selectedID);
                            wsrid =selectedID;
                        }
                    });
                    hideDialog();
                    ListAdapter Adapter = new SimpleAdapter(AppController.getContext(), warehouseSendReceiveList1, R.layout.w_send_receive_item_view, new
                            String[]{"WSRID", "SRDate", "WarehouseSend","WarehouseRecive"},
                            new int[]{R.id.txtSLID,R.id.txtDate,R.id.txtSend,R.id.txtReceive});
                    ((SimpleAdapter) Adapter).setDropDownViewResource(R.layout.spinner_text_colour);
                    setListAdapter(Adapter);



                } catch (JSONException e) {
//                    // JSON error
                    hideDialog();
//                    e.printStackTrace();
//                    Toast.makeText(AppController.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(AppController.getContext(), "Data Not Found.", Toast.LENGTH_SHORT).show();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ContentValues.TAG, "WSR List: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void ShowList1(int id) {
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();
        //BU = sessionList.get(0).getConSt();
        progressDialog.setMessage("Loading Data...");
        showDialog();
        //String URL = AppConfig.UrlStudentList+"uname="+sessionList.get(0).getUname()+"&pass="+sessionList.get(0).getPass()+"&clsID="+clsId +"&groID="+groID+"&secID="+secID+"&date="+date;
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_GetAllWarehouseSendReciveInfo + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(ContentValues.TAG, "List Of Data: " + response.toString());
                try {
                    warehouseSendReceiveList = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("WSRDetailsID", object.getString("WSRDetailsID"));
                        map.put("SRDate", ConvertDateTime(object.getString("SRDate")));
                        map.put("WarehouseSend", object.getString("WarehouseSend"));
                        map.put("WarehouseRecive", object.getString("WarehouseRecive"));
                        map.put("ProductName", object.getString("ProductName"));
                        map.put("Quantity", object.getString("Quantity"));
                        map.put("Rate", object.getString("Rate"));
                        warehouseSendReceiveList.add(map);
                    }
                    //final ListView lv = getListView();
                    Log.e(ContentValues.TAG, "StudentList: " + warehouseSendReceiveList.toString());
                    final Map<String, Object> finalMap = map;
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            txtID = view.findViewById(R.id.txtSLID);
                            int selectedID = Integer.parseInt(txtID.getText().toString());
                            //Toast.makeText(AppController.getContext(), "selectedID" + selectedID, Toast.LENGTH_SHORT).show();
                            //BtnConfirm(selectedID,(sessionList.get(0).getEmail()));
                        }
                    });
                    hideDialog();
                    ListAdapter Adapter = new SimpleAdapter(AppController.getContext(), warehouseSendReceiveList, R.layout.w_receive_item_view, new
                            String[]{"WSRDetailsID", "SRDate", "WarehouseSend","WarehouseRecive", "ProductName", "Quantity", "Rate"},
                            new int[]{R.id.txtSLID,R.id.txtDate,R.id.txtSend,R.id.txtReceive,R.id.txtProduct,R.id.txtQuantity,R.id.txtCostPrice});
                    ((SimpleAdapter) Adapter).setDropDownViewResource(R.layout.spinner_text_colour);
                    list.setAdapter(Adapter);



                } catch (JSONException e) {
//                    // JSON error
                    hideDialog();
//                    e.printStackTrace();
//                    Toast.makeText(AppController.getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(AppController.getContext(), "Data Not Found.", Toast.LENGTH_SHORT).show();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ContentValues.TAG, "data List: " + error.getMessage());
                Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void BtnConfirm(int WSRID, String UserName) {

        progressDialog.setMessage("Loading ...");
        showDialog();

        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_UpdateWarehouseSend + WSRID + "/" + UserName, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(AppController.getContext(), "Confirmation Successfully Done", Toast.LENGTH_SHORT).show();
//                WarehouseReceive warehouseReceive = new WarehouseReceive();
//                android.support.v4.app.FragmentTransaction wr = getActivity().getSupportFragmentManager().beginTransaction();
//                wr.replace(R.id.frame,warehouseReceive);
//                wr.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ContentValues.TAG, "Confirm Error: " + error.getMessage());
                Toast.makeText(AppController.getContext(), "Confirmation Successfully Done", Toast.LENGTH_SHORT).show();
                hideDialog();
                WarehouseReceive warehouseReceive = new WarehouseReceive();
                android.support.v4.app.FragmentTransaction wr = getActivity().getSupportFragmentManager().beginTransaction();
                wr.replace(R.id.frame,warehouseReceive);
                wr.commit();
            }
        }) {
        };
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }

    public void BtnCancle() {
        progressDialog.setMessage("Saving...");
        showDialog();

        JSONObject Obj = new JSONObject();
        try{
            sessionUserController=new SessionUserController();
            List<SessionUser>sessionList=sessionUserController.GetAll();

            Obj.put("WSRID", wsrid);
            Obj.put("Modifier", sessionList.get(0).getEmail());
            Obj.put("Remarks", txtRemarks.getText());

            JsonObjectRequest strReq1 = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_CancelWarehouseSend, Obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(ContentValues.TAG, "response" + response.toString());
                    Toast.makeText(AppController.getContext(), "Cancel.", Toast.LENGTH_SHORT).show();
                    hideDialog();

                    WarehouseReceive warehouseReceive = new WarehouseReceive();
                    android.support.v4.app.FragmentTransaction wr = getActivity().getSupportFragmentManager().beginTransaction();
                    wr.replace(R.id.frame,warehouseReceive);
                    wr.commit();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AppController.getContext(), "Cancel confirm.", Toast.LENGTH_SHORT).show();
                    Log.e(ContentValues.TAG, "Error: " + error.getMessage());
                    hideDialog();
                    WarehouseReceive warehouseReceive = new WarehouseReceive();
                    android.support.v4.app.FragmentTransaction wr = getActivity().getSupportFragmentManager().beginTransaction();
                    wr.replace(R.id.frame,warehouseReceive);
                    wr.commit();
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

    private android.app.AlertDialog.Builder AskOption() {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext());
        if(a==1)
        {
            alert.setMessage("Do you want to approve this transfer?");
        }
        else {
            alert.setMessage("Do you want to cancel this transfer?");
        }
        alert.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(a==1)
                {
                    sessionUserController=new SessionUserController();
                    final List<SessionUser>sessionList=sessionUserController.GetAll();
                    BtnConfirm(wsrid,sessionList.get(0).getUserName());
                }
                else {
                    BtnCancle();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        return alert;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public String ConvertDateTime(String dateTime) {
        //----------------Convert DateTime To Date-------------------
        String datemillis = dateTime;
        SimpleDateFormat  df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = null;
            date = df.parse(datemillis);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String shortTimeStr = sdf.format(date);
            return shortTimeStr;
            //map.put("BillDate", shortTimeStr);
            //Y=shortTimeStr;
        } catch (ParseException e) {
            // To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
            return datemillis;
        }
        //----------------End Convert DateTime To Date-------------------
    }

}


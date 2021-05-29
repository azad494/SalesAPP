package peash.esales.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peash.esales.App.AppConfig;
import peash.esales.App.AppController;
import peash.esales.Controller.SessionUserController;
import peash.esales.Fragment.Canvas_Fragment;
import peash.esales.Fragment.DeliveryFragment;
import peash.esales.Fragment.GeneralLedgerFragment;
import peash.esales.Fragment.HomeFragment;
import peash.esales.Fragment.NewCustomerFragment;
import peash.esales.Fragment.OrderFragment;
import peash.esales.Fragment.PaymentReceivedFragment;
import peash.esales.Fragment.PurchaseFragment;
import peash.esales.Fragment.Report_Fragment;
import peash.esales.Fragment.SalesReturnFragment;
import peash.esales.Fragment.WarehouseReceive;
import peash.esales.Fragment.WarehouseSend;
import peash.esales.Helper.SessionManager;
import peash.esales.Models.SessionUser;
import Adi.esales.R;

public class MainActivity extends AppCompatActivity {


    TextView username,email;
    MenuItem  customer,sales,purchase,paymentRec,order,Delivery,GemeralLedger,WSend,WReceive,Report,salesReturn;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SessionManager session;
    private SessionUserController sessionUserController;


    private List<Map<String, Object>> MenuList = new ArrayList<>();

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.menu = menu;
//        try {
//            getMenuInflater().inflate(R.menu.menu_main,menu);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //Log.i(TAG, "onCreateOptionsMenu: error: "+e.getMessage());
//        }
//        return super.onCreateOptionsMenu(menu);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initializing Toolbar and setting it as the actionbar

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Initializing NavigationView
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        //--------------Get HeaderView from NavigationView-------------//
        View headerView = navigationView.getHeaderView(0);
        username = headerView.findViewById(R.id.username);
        username.setText(sessionList.get(0).getName());
        email =  headerView.findViewById(R.id.email);
        email.setText(sessionList.get(0).getEmail());

        //----------Get Menu from NavigationView--------------//
        Menu menu = navigationView.getMenu();
        customer = menu.findItem(R.id.Customer);
        sales = menu.findItem(R.id.Sales);
        purchase = menu.findItem(R.id.Purchase);
        paymentRec = menu.findItem(R.id.PaymentReceived);
        order = menu.findItem(R.id.Order);
        Delivery = menu.findItem(R.id.Delivery);
        GemeralLedger = menu.findItem(R.id.GemeralLedger);
        WSend = menu.findItem(R.id.Wsend);
        WReceive = menu.findItem(R.id.Wreceive);
        Report = menu.findItem(R.id.Report);
        salesReturn = menu.findItem(R.id.SalesReturn);
        //sales.setVisible(false);


        customer.setVisible(false);
        sales.setVisible(false);
        purchase.setVisible(false);
        paymentRec.setVisible(false);
        WSend.setVisible(false);
        WReceive.setVisible(false);
        GemeralLedger.setVisible(false);
        Report.setVisible(false);
        order.setVisible(true);//406
        Delivery.setVisible(true);//407
        salesReturn.setVisible(true);// 405

        MenuList();

        //menu.findItem(R.id.Sales).setTitle("");
//        MenuItem x = a.findItem(R.id.Sales);
//        x.setVisible(false);

        //Default Fragment
        HomeFragment homefragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
        homeFragmentTransaction.replace(R.id.frame,homefragment);
        homeFragmentTransaction.commit();

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //Checking if the item is in checked state or not, if not make it in checked state
                        if(menuItem.isChecked())

                            menuItem.setChecked(false);

                        else
                            menuItem.setChecked(true);

                        //Closing drawer on item click
                        drawerLayout.closeDrawers();
                        //Check to see which item was being clicked and perform appropriate action
                        switch (menuItem.getItemId()){
                            case R.id.home:
                                HomeFragment homefragment = new HomeFragment();
                                android.support.v4.app.FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
                                homeFragmentTransaction.replace(R.id.frame,homefragment);
                                homeFragmentTransaction.commit();
                                return true;

                            case R.id.Customer:
                                NewCustomerFragment newCustomerFragment = new NewCustomerFragment();
                                android.support.v4.app.FragmentTransaction newCustomerFragment1 = getSupportFragmentManager().beginTransaction();
                                newCustomerFragment1.replace(R.id.frame,newCustomerFragment);
                                newCustomerFragment1.commit();
                                return true;

                           case R.id.Sales:
                               Canvas_Fragment canvas_fragment1 = new Canvas_Fragment();
                               android.support.v4.app.FragmentTransaction test1fragmentTransaction = getSupportFragmentManager().beginTransaction();
                               Bundle args = new Bundle();
                               args.putString("temp", "Sales");
                               canvas_fragment1.setArguments(args);
                               test1fragmentTransaction.replace(R.id.frame,canvas_fragment1);
                               test1fragmentTransaction.commit();
                               return true;
//                                SalesFragment salesFragment = new SalesFragment();
//                                android.support.v4.app.FragmentTransaction a1FragmentTransaction = getSupportFragmentManager().beginTransaction();
//                                a1FragmentTransaction.replace(R.id.frame,salesFragment);
//                                a1FragmentTransaction.commit();
//                                return true;

                            case R.id.Logout:
//                                LogoutFragment logoutFragment=new LogoutFragment();
//                                android.support.v4.app.FragmentTransaction a2FragmentTransaction = getSupportFragmentManager().beginTransaction();
//                                a2FragmentTransaction.replace(R.id.frame,logoutFragment);
//                                a2FragmentTransaction.commit();
                                // SQLite database handler
                                sessionUserController = new SessionUserController();
                                sessionUserController.DeleteAll();
                                session = new SessionManager(getApplicationContext());
                                session.setLogin(false);
                                Intent intent = new Intent(MainActivity.this, LogoutFragment.class);
                                startActivity(intent);
                                SharedPreferences preferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                                finish();
                                return true;

                            case R.id.Purchase:
                                puc();
//                                PurchaseFragment purchaseFragment = new PurchaseFragment();
//                                android.support.v4.app.FragmentTransaction a3FragmentTransaction = getSupportFragmentManager().beginTransaction();
//                                a3FragmentTransaction.replace(R.id.frame,purchaseFragment);
//                                a3FragmentTransaction.commit();
                                return true;

                            case R.id.PaymentReceived:

                                PaymentReceivedFragment paymentReceivedFragment = new PaymentReceivedFragment();
                                android.support.v4.app.FragmentTransaction a4FragmentTransaction = getSupportFragmentManager().beginTransaction();
                                a4FragmentTransaction.replace(R.id.frame,paymentReceivedFragment);
                                a4FragmentTransaction.commit();
                                return true;

                            case R.id.GemeralLedger:

                                GeneralLedgerFragment generalLedgerFragment = new GeneralLedgerFragment();
                                android.support.v4.app.FragmentTransaction testFragmentTransaction = getSupportFragmentManager().beginTransaction();
                                testFragmentTransaction.replace(R.id.frame,generalLedgerFragment);
                                testFragmentTransaction.commit();
                                return true;

                            case R.id.canvas:

                                Canvas_Fragment canvas_fragment = new Canvas_Fragment();
                                android.support.v4.app.FragmentTransaction testfragmentTransaction = getSupportFragmentManager().beginTransaction();
                                testfragmentTransaction.replace(R.id.frame,canvas_fragment);
                                testfragmentTransaction.commit();
                                return true;

                            case R.id.Order:
                                Canvas_Fragment canvas = new Canvas_Fragment();
                                android.support.v4.app.FragmentTransaction tft = getSupportFragmentManager().beginTransaction();
                                Bundle argss = new Bundle();
                                argss.putString("temp", "Order");
                                canvas.setArguments(argss);
                                tft.replace(R.id.frame,canvas);
                                tft.commit();
//                                OrderFragment orderFragment = new OrderFragment();
//                                android.support.v4.app.FragmentTransaction a5 = getSupportFragmentManager().beginTransaction();
//                                a5.replace(R.id.frame,orderFragment);
//                                a5.commit();
                                return true;

                            case R.id.Delivery:
                                //od();
                                //puc();
                                DeliveryFragment deliveryFragment = new DeliveryFragment();
                                android.support.v4.app.FragmentTransaction delivery = getSupportFragmentManager().beginTransaction();
                                delivery.replace(R.id.frame,deliveryFragment);
                                delivery.commit();

//                                PurchaseFragment purchaseFragment = new PurchaseFragment();
//                                android.support.v4.app.FragmentTransaction a3FragmentTransaction = getSupportFragmentManager().beginTransaction();
//                                a3FragmentTransaction.replace(R.id.frame,purchaseFragment);
//                                a3FragmentTransaction.commit();
                                return true;

                            case R.id.Wsend:

                                WarehouseSend warehouseSend = new WarehouseSend();
                                android.support.v4.app.FragmentTransaction ws = getSupportFragmentManager().beginTransaction();
                                ws.replace(R.id.frame,warehouseSend);
                                ws.commit();
                                return true;

                            case R.id.Wreceive:

                                WarehouseReceive warehouseReceive = new WarehouseReceive();
                                android.support.v4.app.FragmentTransaction wr = getSupportFragmentManager().beginTransaction();
                                wr.replace(R.id.frame,warehouseReceive);
                                wr.commit();
                                return true;

                            case R.id.Report:
                                Report_Fragment RE = new Report_Fragment();
                                FragmentTransaction rft = getSupportFragmentManager().beginTransaction();
                                rft.replace(R.id.frame,RE);
                                rft.commit();
                                return true;

                            case R.id.SalesReturn:
                                SalesReturnFragment SRD = new SalesReturnFragment();
                                FragmentTransaction srdft = getSupportFragmentManager().beginTransaction();
                                srdft.replace(R.id.frame,SRD);
                                srdft.commit();
                                return true;

                            default:
                                Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                                return true;
                        }
                    }
                });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void puc(){
        PurchaseFragment purchaseFragment = new PurchaseFragment();
        android.support.v4.app.FragmentTransaction a3FragmentTransaction = getSupportFragmentManager().beginTransaction();
        a3FragmentTransaction.replace(R.id.frame,purchaseFragment);
        a3FragmentTransaction.commit();
    }

    public void MenuList() {
        sessionUserController=new SessionUserController();
        List<SessionUser>sessionList=sessionUserController.GetAll();

        //progressDialog.setMessage("Loading Order List...");
        //showDialog();
         JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_MenuPer + sessionList.get(0).getEmail(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(ContentValues.TAG, "List Of Data: " + response.toString());
                try {
                    MenuList = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("MenuCode", object.getString("MenuCode"));
                        MenuList.add(map);
                    }
                    if(MenuList.size()>0)
                    {
                        int a = MenuList.size();
                        for (int x=0; x<a; x++)
                        {
                            Map<String, Object> selectQuentity = MenuList.get(x);
                            int Code = Integer.parseInt(selectQuentity.get("MenuCode").toString());
                            if(Code==403)
                            {
                                sales.setVisible(true);
                            }
                            else if(Code==301)
                            {
                                purchase.setVisible(true);
                            }
                            else if(Code==606)
                            {
                                paymentRec.setVisible(true);
                            }
                            else if(Code==607)
                            {
                                GemeralLedger.setVisible(true);
                            }else if(Code==1601)
                            {
                                WSend.setVisible(true);
                            }else if(Code==1602)
                            {
                                WReceive.setVisible(true);
                            }else if(Code==1703)
                            {
                                Report.setVisible(true);
                            }else if(Code==406)
                            {
                                order.setVisible(true);
                            }else if(Code==407)
                            {
                                Delivery.setVisible(true);
                            }
                            else{
                            //Toast.makeText(AppController.getContext(), "User not permited.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    //hideDialog();

                } catch (JSONException e) {
                   // hideDialog();
                    Toast.makeText(AppController.getContext(), "Data Not Found.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ContentValues.TAG, "Menu List: " + error.getMessage());
                //Toast.makeText(AppController.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(AppController.getContext(), "Permission not found please login again.", Toast.LENGTH_SHORT).show();
                //hideDialog();
            }
        });
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, null);
    }
}

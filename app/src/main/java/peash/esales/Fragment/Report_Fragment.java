package peash.esales.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import peash.esales.Controller.SessionUserController;
import peash.esales.Models.SessionUser;

import static java.util.Locale.US;
import static peash.esales.App.AppController.TAG;


public class Report_Fragment extends Fragment {

    Calendar myCalendar = Calendar.getInstance();
    private SessionUserController sessionUserController;
    private EditText date1,Mdate,Invoice;
    RadioButton rdDateToDateSales, rdDateToDateExpanse, rdDailyDueCollection,rdDateWiseAllSuppPurchase,rdWarehouseOut,rdDateWiseAllCustSales,rdSalesInvoice,rdWhInInvoiceReport,rdDtoDWarehouseIn;
    Button Preview;
    TextView EndDate;
    LinearLayout llStartDate,llEndDate,llWarehouse,llInvoice;
    private Spinner spWareHouse;
    int WarehouseID;


    private List<Map<String, Object>> warehouseList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_report, container, false);

        date1 = v.findViewById(R.id.Date1);
        Mdate = v.findViewById(R.id.txtMDate);
        EndDate = v.findViewById(R.id.EndDate);
        Invoice = v.findViewById(R.id.txtInvoice);
        Preview = v.findViewById(R.id.btnPreview);

        spWareHouse = v.findViewById(R.id.spWarehouse);

        llStartDate = v.findViewById(R.id.llStartDate);
        llEndDate = v.findViewById(R.id.llEndDate);
        llWarehouse = v.findViewById(R.id.llWarehouse);
        llInvoice = v.findViewById(R.id.LLInvoice);


        rdDateToDateSales = v.findViewById(R.id.rdDtoDSalesWSignature);
        rdDateWiseAllCustSales = v.findViewById(R.id.rdDateWiseAllCustSales);
        rdSalesInvoice = v.findViewById(R.id.rdSalesInvoice);
        rdDateToDateExpanse = v.findViewById(R.id.rdDateToDateExpance);
        rdDailyDueCollection = v.findViewById(R.id.rdDailyDueCollection);
        rdDateWiseAllSuppPurchase = v.findViewById(R.id.rdDateWiseAllSuppPurchase);
        rdWhInInvoiceReport = v.findViewById(R.id.rdWhInInvoiceReport);
        rdWarehouseOut = v.findViewById(R.id.rdWarehouseOut);
        rdDtoDWarehouseIn = v.findViewById(R.id.rdDtoDWarehouseIn);


        rdDateToDateSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llWarehouse.setVisibility(View.GONE);
                llInvoice.setVisibility(View.GONE);
                llStartDate.setVisibility(View.VISIBLE);
                llEndDate.setVisibility(View.VISIBLE);
            }
        });
        rdDateWiseAllCustSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWarehouse.setVisibility(View.GONE);
                llInvoice.setVisibility(View.GONE);
                llStartDate.setVisibility(View.VISIBLE);
                llEndDate.setVisibility(View.GONE);
            }
        });

        rdSalesInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWarehouse.setVisibility(View.GONE);
                llInvoice.setVisibility(View.VISIBLE);
                llStartDate.setVisibility(View.GONE);
                llEndDate.setVisibility(View.GONE);
            }
        });
        rdDateToDateExpanse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWarehouse.setVisibility(View.GONE);
                llInvoice.setVisibility(View.GONE);
                llStartDate.setVisibility(View.VISIBLE);
                llEndDate.setVisibility(View.VISIBLE);
            }
        });

        rdDailyDueCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWarehouse.setVisibility(View.GONE);
                llInvoice.setVisibility(View.GONE);
                llStartDate.setVisibility(View.VISIBLE);
                llEndDate.setVisibility(View.VISIBLE);
            }
        });
        rdDateWiseAllSuppPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWarehouse.setVisibility(View.GONE);
                llInvoice.setVisibility(View.GONE);
                llStartDate.setVisibility(View.VISIBLE);
                llEndDate.setVisibility(View.GONE);
            }
        });

        rdWhInInvoiceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWarehouse.setVisibility(View.GONE);
                llInvoice.setVisibility(View.VISIBLE);
                llStartDate.setVisibility(View.GONE);
                llEndDate.setVisibility(View.GONE);
            }
        });
        rdWarehouseOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWarehouse.setVisibility(View.VISIBLE);
                llInvoice.setVisibility(View.GONE);
                llStartDate.setVisibility(View.VISIBLE);
                llEndDate.setVisibility(View.VISIBLE);
                loadWarehouse();
            }
        });
        rdDtoDWarehouseIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWarehouse.setVisibility(View.VISIBLE);
                llInvoice.setVisibility(View.GONE);
                llStartDate.setVisibility(View.VISIBLE);
                llEndDate.setVisibility(View.VISIBLE);
            }
        });

        Preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rdDateToDateSales.isChecked()==true){
                    report();
                }
                else if(rdDateWiseAllCustSales.isChecked()==true){
                    DateWiseAllCustomerSales();
                }
                else if(rdDateToDateExpanse.isChecked()==true){
                    DateToDateExpense();
                }
                else if(rdSalesInvoice.isChecked()==true){
                    InvoiceWiseSales();
                }
                else if(rdDailyDueCollection.isChecked()==true)
                {
                    DailyDueCollection();
                }
                else if(rdDateWiseAllSuppPurchase.isChecked()==true)
                {
                    DayWiseSuppPurchase();
                }
                else if(rdWhInInvoiceReport.isChecked()==true)
                {
                    WhInvoiceIDWise();
                }
                else if(rdDtoDWarehouseIn.isChecked()==true)
                {
                    DatetoDateWarehouseIn();
                }
                else if(rdWarehouseOut.isChecked()==true)
                {
                    WarehouseOutReport();
                }
            }
        });
        loadWarehouse();
        LoadDate();
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

        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, US);
        date1.setText(sdf.format(myCalendar.getTime()));
    }
    private void setMaturity_date() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, US);
        Mdate.setText(sdf.format(myCalendar.getTime()));
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void report(){
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/DatetoDateSalesWithSignature/"+company+"="+date1.getText().toString()+"="+Mdate.getText().toString())));
        //Reporting/DatetoDateSalesWithSignature/' + CompanyID + '=' + startdate + '=' + enddate
    }
    public void DateToDateExpense(){
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/DateToDateExpanse/"+date1.getText().toString()+"="+Mdate.getText().toString())));
        //'/Reporting/DateToDateExpanse/' + startdate + '=' + endDate);
    }
    public void DailyDueCollection(){
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/DailyDueCollection/"+company+"="+date1.getText().toString()+"="+Mdate.getText().toString())));
        //'/Reporting/DailyDueCollection/' + CompanyID + "=" + startdate
    }
    public void DayWiseSuppPurchase(){
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/DateWiseAllSuppPurPrint/"+company+"="+date1.getText().toString())));
        //'/Reporting/DateWiseAllSuppPurPrint/' + CompanyID + '=' + startdate);
    }
    public void WarehouseOutReport(){
        if(spWareHouse.getSelectedItemId() > 0){
            sessionUserController=new SessionUserController();
            List<SessionUser> sessionList=sessionUserController.GetAll();
            String a =sessionList.get(0).getUID();
            String company = sessionList.get(0).getCompanyID();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/DatetoDateWarehouse/"+company+"="+date1.getText().toString()+"="+Mdate.getText().toString()+"="+WarehouseID+"="+0)));
            //'/Reporting/DatetoDateWarehouse/' + CompanyID + '=' + startdate + '=' + endDate + '=' + $scope.Warehouse + '=' + 0
        }
        else {
            Toast.makeText(AppController.getContext(), "Select Warehouse", Toast.LENGTH_SHORT).show();
        }
        }
    public void DatetoDateWarehouseIn(){
        if(spWareHouse.getSelectedItemId() > 0){
            sessionUserController=new SessionUserController();
            List<SessionUser> sessionList=sessionUserController.GetAll();
            String a =sessionList.get(0).getUID();
            String company = sessionList.get(0).getCompanyID();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/DatetoDateWarehouse/"+company+"="+date1.getText().toString()+"="+Mdate.getText().toString()+"="+WarehouseID+"="+1)));
            //''/Reporting/DatetoDateWarehouse/' + CompanyID + '=' + startdate + '=' + endDate + '=' + $scope.Warehouse + '=' + ReciveConfirm);
        }
        else {
            Toast.makeText(AppController.getContext(), "Select Warehouse", Toast.LENGTH_SHORT).show();
        }
        }
    public void DateWiseAllCustomerSales(){
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/DateWiseAllCustomerSales/"+company+"="+date1.getText().toString()+"="+Mdate.getText().toString())));
        //'/Reporting/DateWiseAllCustomerSales/' + CompanyID + '=' + startdate + '=' + enddate
    }
    public void InvoiceWiseSales(){
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/InvoiceWiseSales/"+company+"="+Invoice.getText().toString())));
        ///Reporting/InvoiceWiseSales/' + CompanyID + "=" + $scope.InvoiceNo
    }
    public void WhInvoiceIDWise(){
        sessionUserController=new SessionUserController();
        List<SessionUser> sessionList=sessionUserController.GetAll();
        String a =sessionList.get(0).getUID();
        String company = sessionList.get(0).getCompanyID();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps-many.com/Reporting/WhInvoiceIDWise/"+Invoice.getText().toString())));
        ///Reporting/WhInvoiceIDWise/' + $scope.ChallanNo
    }

    public void loadWarehouse() {
        JsonArrayRequest strReq = new JsonArrayRequest(Request.Method.GET, AppConfig.URL_Warehouse, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("response", "" + response.toString());

                    warehouseList = new ArrayList<>();

                    Map<String, Object> map = new HashMap<>();
                    map.put("WarehouseID", "0");
                    map.put("Name", "Select Warehouse");
                    warehouseList.add(map);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        map = new HashMap<>();
                        map.put("WarehouseID", object.getString("WarehouseID"));
                        map.put("Name", object.getString("Name"));
                        warehouseList.add(map);
                    }
                    SimpleAdapter arrayAdapter = new SimpleAdapter(AppController.getContext(), warehouseList, R.layout.spinner_text_colour,
                            new String[]{"Name"}, new int[]{android.R.id.text1});
                    spWareHouse.setAdapter(arrayAdapter);

                    spWareHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            if (spWareHouse.getSelectedItemId() > 0) {
                                Map<String, Object> selectPruductID = warehouseList.get(spWareHouse.getSelectedItemPosition());
                                WarehouseID = Integer.valueOf(selectPruductID.get("WarehouseID").toString());

                                //Map<String, Object> selectProductID = productLists.get(spProduct.getSelectedItemPosition());
                                //int idP = Integer.valueOf(selectProductID.get("ProductID").toString());

                                //LoadStockByPersonID(idP,id);
                            } else {
                                Toast.makeText(AppController.getContext(), "Select Warehouse", Toast.LENGTH_SHORT).show();
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


}

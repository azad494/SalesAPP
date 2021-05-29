package peash.esales.Helper;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import peash.esales.Database.ConnectivityReceiver;

import static java.util.Locale.US;

public class Common {
    public boolean IsInternetConnected() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            Log.e("Internet Status", "IsNetConnected: " + isConnected);
            return true;
        } else {
            return false;
        }
    }
    public String GetCurrentDate() {
        final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, US);
        return String.valueOf(sdf.format(myCalendar.getTime()));
    }

    public String GetDateInDBFormat() {
        final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, US);
        return String.valueOf(sdf.format(myCalendar.getTime()));
    }


    public String ConvertDate(String time) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
        // new SimpleDateFormat
        //   SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf1.parse(time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String newDate = sdf2.format(date);
        return newDate;
    }

    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}

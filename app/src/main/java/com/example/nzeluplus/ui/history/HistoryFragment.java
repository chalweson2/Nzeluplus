package com.example.nzeluplus.ui.history;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nzeluplus.Activities;
import com.example.nzeluplus.Orders;
import com.example.nzeluplus.R;
import com.example.nzeluplus.Startup;
import com.example.nzeluplus.databinding.FragmentHistoryBinding;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.widget.NestedScrollView;

import com.example.nzeluplus.MainActivity;
import com.example.nzeluplus.MyModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private FragmentHistoryBinding binding;

    int rpsdatebt = 37562, rpedatebt = 479934, pbbarid = 1921267, rpclickeddatebtn = 0, height = MainActivity.height, lineheight,  width = MainActivity.width, consid =  3411;
    String rpstartdate = "", rpenddate = "", rpstatus = "", statustable  = "",
            android_id = MainActivity.android_id, uid = MainActivity.uref, ds = MainActivity.DisplayString;
    MyModel md = new MyModel();  LinearLayout mainlin, sublin; Calendar c = Calendar.getInstance();
    int rpjavayear = c.get(Calendar.YEAR),  rpjavamonth = c.get(Calendar.MONTH), rpjavaday = c.get(Calendar.DAY_OF_MONTH);
    int fontsmall = md.fontsmall, bigpad = 10, subpad = 7;
    boolean  valeron = md.DbName.equals("valeronpro");

    String DS = MainActivity.DisplayString, Executor = MainActivity.Executor;
    SQLiteDatabase mydatabase;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context cnt = getActivity();
        mainlin = binding.mainlin;

        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        DisplayMetrics metrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;  width = metrics.widthPixels;
        mainlin.setPadding(bigpad, 0, bigpad, 0);
        fontsmall -= 2;

        lineheight = (int)(height * 0.07);

        mainlin.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        width = width - (bigpad * 2);

        mainlin = md. setbackground(mainlin);

        android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        mydatabase = getActivity().openOrCreateDatabase(md.DbName,getActivity().MODE_PRIVATE,null);


        rpstartoff();
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




    boolean withfaves = false, admin = false;
    public void rpstartoff(){

        // Spinner Drop down elements
        ArrayList<String> reporttypes = new ArrayList<String>(), reportmethods = new ArrayList<>();
        reporttypes.add("Menu");reportmethods.add("");
        reporttypes.add("My Account");reportmethods.add("myaccount");
        boolean history = true;

        String histitle = "My Orders History";
        if (history){reporttypes.add(histitle);reportmethods.add("myhistory");}

        reporttypes.add("My Addresses");reportmethods.add("myaddress");

        if (md.appsetting("AddFavouritesButton", MainActivity.DisplayString).toLowerCase().equals("true")){
            reporttypes.add("My Favourites");withfaves =true; reportmethods.add("myfavourites");}

        mainlin.removeAllViews();  Spinner spinner = new Spinner(getActivity());
        spinner.getBackground().setColorFilter(md.black, PorterDuff.Mode.SRC_ATOP);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height / 15)));
        mainlin.addView(spinner);
        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //((TextView) adapterView.getChildAt(0)).setTextColor(md.white);
                if (i == 0){
                    ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(
                            md.AppName  );
                }
                if (i > 0){
                    ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(
                            md.getstring(reporttypes, i)   );
                }

                if (md.getstring(reportmethods, i).equals("myaccount")){    myaccount();   }
                else if (md.getstring(reportmethods, i).equals("myhistory")){   myhistory(true,"CustomerOrders");  }
                else if (md.getstring(reportmethods, i).equals("purchasehistory")){   myhistory(true, "PurchaseOrders");  }
                else if (md.getstring(reportmethods, i).equals("myaddress")){  myaddresses(); }
                else if (md.getstring(reportmethods, i).equals("myfavourites")){   favourites();  }


                spinner.setSelection(0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, reporttypes);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  spinner.setAdapter(dataAdapter);

        NestedScrollView ns  = new NestedScrollView(getActivity());
        ns.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(height * 0.9)));
        mainlin.addView(ns);
        sublin = new LinearLayout(getActivity()); sublin.setOrientation(LinearLayout.VERTICAL);
        sublin.setLayoutParams(new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
        ns.addView(sublin);

    }

    String rpobjtable = "";
    final int rpobjid = 53472, rprunrepid = 64957, scrlid =  392150;
    LinearLayout hislin;
    public void myhistory(boolean neww, String table){

        Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "History",  ds, MainActivity.Executor, table, "", "", ""});
        intent.putExtras(b);
        startActivity(intent);

    }





    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                String[] received = b.getStringArray("key");

                if (received[0].equals(statustable)){

                    if (received[1].equals("")){
                        rpstatus = "";
                    }else{
                        ArrayList<String> thisrow = md.makearrayfromstring(received[1], "|");
                        rpstatus =md.rowoption(thisrow, "ID");
                    }
                }
            }
        }
    }










    public void favourites(){
        Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "Favourites",  ds, MainActivity.Executor, "", "", "", ""});
        intent.putExtras(b);
        startActivity(intent);
    }
    public void myaddresses(){


        Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "Address",  ds, MainActivity.Executor, "1", "", "", ""});
        intent.putExtras(b);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);


    }



    public void myaccount(){

        sublin.removeAllViews();
        ProgressBar pb = new ProgressBar(getActivity());
        sublin.addView(pb);
        getaccount(new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showaccount();
            }
        }, 0);

    }

    String btstring = ""; boolean btsuccess = false;
    public void getaccount( final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(getActivity());

            String url = md.url + "gettable";
            url = url + "?code="+md.auth(getActivity(),  mydatabase, android_id, uid)+"~Users^|Reference^"+ uid;

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  btstring = response; btsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ getaccount(   callBack,  attempt); }
                    else{ btsuccess =false; md.toast(getActivity(),  "Error: Please Try Again"); }
                }
            });
            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }

    public void showaccount(){

        // Display the first 500 characters of the response string.
        if (!btsuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(getActivity(), z);
        }
        else if(btsuccess & sublin != null) {
            sublin.removeAllViews();

            ArrayList<String> list  =  md.makearrayfromstring(btstring, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            if(list.size() > 2){
                int c = 2;
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                String idd =  md.before(md.getstring(list, c), "~");
                String name =  md.coloption(cols, "Name", row).Value
                        + " " +  md.coloption(cols, "Surname", row).Value;
                String email =  md.coloption(cols, "Email", row).Value;
                String created =  md.coloption(cols, "Created", row).Value;


                int lineheight = (int) (0.08*height);
                MaterialTextView mt = new MaterialTextView(getActivity());  mt.setHeight(lineheight);sublin.addView(mt);
                mt.setWidth(width);  mt = md.textviewdesign(getActivity(), mt, "Info",ds);   mt.setText("Name: " + name);

                mt = new MaterialTextView(getActivity());  mt.setHeight(lineheight);sublin.addView(mt);
                mt.setWidth(width);  mt = md.textviewdesign(getActivity(), mt, "Info",ds);   mt.setText("Email: " + email);

                mt = new MaterialTextView(getActivity());  mt.setHeight(lineheight); sublin.addView(mt);
                mt.setWidth(width);  mt = md.textviewdesign(getActivity(), mt, "Info",ds);   mt.setText("Date Registered: " + created);

                MaterialButton btminus = new MaterialButton(getActivity());
                btminus.setText("Log Out"); sublin.addView(btminus);
                btminus = md.buttondesign(getActivity(), btminus, "General", ds);
                btminus.setLayoutParams(new LinearLayout.LayoutParams(width - (subpad * 2) - bigpad, (int)(height * 0.08)));
                btminus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {  logout();  }
                });

                ProgressBar pb = new ProgressBar(getActivity()); pb.setId(pbbarid); sublin.addView(pb); pb.setVisibility(View.GONE);
            }
        }
    }

    String logstring = ""; boolean logsuccess = false;

    public void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alert = builder.create();
        builder.setMessage("Are You Sure You Want To Log Out?")
                .setTitle("Log Out?")
                .setNeutralButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                } )
                .setNegativeButton("Log Out", (dialog, which) -> {

                    logoff(new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() {

                            if (valeron) {
                                md.switchcompany(uid, "", "", mydatabase);

                                Intent intent = new Intent(getActivity(), Startup.class);
                                startActivity(intent);
                            } else {
                            logoff(new MainActivity.VolleyCallBack() {
                                @Override
                                public void onSuccess() {
                                    Intent intent = new Intent(getActivity(), Startup.class);
                                    startActivity(intent);
                                }
                            }, 0);
                        }
                        }
                    }, 0);
                } );
        builder.create();
        alert = builder.create();
        alert.show();


    }

    public void logoff( final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(getActivity());

            String logurl =  md.url + "logoff?code="+ md.auth(getActivity(),  mydatabase, android_id, uid);

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, logurl , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  logstring = response; logsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    logsuccess = false; md.toast(getActivity(),  "Error: Please Try Again");
                }
            });
            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }











    public void aed(String table){

        hislin.removeAllViews();

        MaterialTextView mt = md.maketextview(getActivity(), "Loading "+table+"... Please Wait...", "", 0, "Info", ds, md.fontmedium,
                width, (int)(height * 0.1), true);

        hislin.addView(mt);
        ProgressBar pb = new ProgressBar(getActivity());
        hislin.addView(pb);


        gettable(new MainActivity.VolleyCallBack() {
            @Override public void onSuccess() {  showtable();  } }, 0, getActivity());
    }

    String tbstring =  ""; boolean  tbsuccess =  false;
    public void gettable(  final MainActivity.VolleyCallBack callBack,
                           int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String ur =  md.auth(getActivity(),  mydatabase, android_id, uid)+"~" +rpstartdate +"~" +rpenddate;

        String url = md.url + "gethistory?code=" +  ur;
        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { tbstring = response; tbsuccess = true; callBack.onSuccess(); }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){

                    gettable( callBack,  attempt, cnt);
                }else{
                    tbstring =  "Error: " + error.getMessage();
                    md.toast(getActivity(), "Failed To Reach Server... Retrying");
                    rpstartoff();
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void showtable(){

        if (tbstring.indexOf("|") > -1 & hislin != null ){ }
        else{
            rpstartoff();
        }
    }





























    String transstring ="", substring = "";
    public void exceltransaction(String rowid, String tbname){

        String orderitemtype = "";
        List<String[]> csvdata = new ArrayList<String[]>();

        csvdata = new ArrayList<String[]>();
        ArrayList<String> genlist  =  md.makearrayfromstring( transstring, "¬");
        String genrulestring = genlist.get(0);
        ArrayList<String>genrules = md.makearrayfromstring(genrulestring, "#");
        ArrayList<String> gencols = md.makearrayfromstring(genlist.get(1), "~");
        ArrayList<String> gendisplaycols = md.makearrayfromstring(genrules.get(9), "~");

        for (int i = 2; i < genlist.size(); i++){
            ArrayList<String> row = md.makearrayfromstring(genlist.get(i), "~");
            ArrayList<String> thisrow = md.makearrayfromstring( md.rowstring(gencols, row), "|");
            String rid = md.rowoption(thisrow, "ID");

            if (rid.equals(rowid)){
                if (tbname.equals("CustomerOrderItems") || tbname.equals("CustomerOrders")){
                    orderitemtype = md.rowoption(thisrow, "ItemType");   }

                for (int d = 0; d < gendisplaycols.size(); d++){
                    if (md.before(gendisplaycols.get(d), "^").equals(tbname)){
                        String clstring = md.breakurl(gendisplaycols.get(d), 1, "^");
                        if (clstring.contains("_")){
                            ArrayList<String> cls = md.makearrayfromstring(clstring, "_");

                            for (int c = 0; c < cls.size(); c++){
                                String cl = cls.get(c);
                                String value = md.rowoption(thisrow, cl);
                                boolean go = true;
                                String cdis = md.returndisplay(cls.get(c));
                                if ((cl.equals("Item"))){ cdis = MainActivity.itemname;}
                                if (orderitemtype.equals("2") &  (cl.equals("Size") || cl.equals("ItemNumber")
                                        || cl.equals("ShippingFee")|| cl.equals("SubTotal"))){ go = false;}
                                else if (!orderitemtype.equals("2") & (cl.equals("StartTime") || cl.equals("EndTime") || cl.equals("Executor"))){ go = false;}

                                if (go){
                                    csvdata.add(new String[]{cdis +":",value, "", "", "", "", ""});
                                }

                            }
                        }
                    }
                }
            }
        }


        csvdata.add(new String[]{ "", "", "", "", "", "", ""});

        ArrayList<String> logtbs = md.makearrayfromstring(substring, "|"), itemsadded  = new ArrayList<>();
        if (logtbs.size() == 0){logtbs.add(substring);}

        if (logtbs.size() > 1){
            ArrayList<String> tb1  =  md.makearrayfromstring(logtbs.get(0), "¬");
            ArrayList<String>  cols = md.makearrayfromstring(tb1.get(1), "~");

            String xxname = md.appsetting("ExecutorName", MainActivity.DisplayString);
            if (orderitemtype.equals("2")){
                csvdata.add(new String[]{"Name","Number", "Price ("+ MainActivity.currencysymbol +")", "Start Time","EndTime", xxname,"Total"});
            }else{
                csvdata.add(new String[]{"Name", "Number", "Price ("+ MainActivity.currencysymbol+")", "Quantity", "VAT", "Total", ""});
            }

            String gg = "";
            for (int t = 0; t <logtbs.size(); t++){
                ArrayList<String> tb  =  md.makearrayfromstring(logtbs.get(t), "¬");

                final String tablestring = logtbs.get(t);
                for (int i = 2; i < tb.size(); i++){
                    ArrayList<String> row = md.makearrayfromstring(tb.get(i), "~");
                    ArrayList<String> thisrow = md.makearrayfromstring( md.rowstring(cols, row), "|");
                    String idd = md.rowoption(thisrow, "ID");
                    String itemname = md.rowoption(thisrow, "Item");
                    String itemnumber = md.rowoption(thisrow, "ItemNumber");
                    String quantity = md.rowoption(thisrow, "Quantity");
                    String price = md.rowoption(thisrow, "Price");
                    String total = md.rowoption(thisrow, "Total");
                    String myorderid = md.rowoption(thisrow, "ValueOrder");
                    String itemtype = md.rowoption(thisrow, "ValueItemType");
                    String st = md.fixdateortime(md.rowoption(thisrow, "StartTime"));
                    String et = md.fixdateortime(md.rowoption(thisrow, "EndTime"));
                    String exx = md.fixdateortime(md.rowoption(thisrow, "Executor"));
                    String vat = md.fixdateortime(md.rowoption(thisrow, "VAT"));

                    if (myorderid.equals(rowid) & !itemsadded.contains(idd)){
                        itemsadded.add(idd);

                        if (orderitemtype.equals("2")){
                            csvdata.add(new String[]{itemname ,   quantity, price, st, et,exx, total});
                        }else{
                            csvdata.add(new String[]{itemname ,itemnumber ,  price,  quantity, vat, total, ""});
                        }
                    }
                }
            }


        }

/*



        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/"+md.returndisplay(tbname)+".csv";
        File f = new File(filePath);

        String fileName = md.returndisplay(tbname)+".csv";

        CSVWriter writer = null;
        try {
            File file = f;//new File(fileName +".csv");
            if (file.exists()) { file.delete();   }
            String csv = (filePath); // Here csv file name is MyCsvFile.csv

            writer = new CSVWriter(new FileWriter(csv));

            writer.writeAll(csvdata); // data is adding to csv

            writer.close();

            md.toast(getActivity(), "File Saved In Downloads Folder as " + fileName);

        } catch (IOException e) {
            md.toast(getActivity(), e.getMessage());
        }
*/




    }


}
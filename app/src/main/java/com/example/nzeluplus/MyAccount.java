package com.example.nzeluplus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MyAccount extends AppCompatActivity {

    LinearLayout mainlin;
    final Integer pbarid = 243, ftv = 6390, scrlid = 839152, nscid = 5463, nestlin =  37542;
    boolean dataSuccess = false;
    MyModel md = new MyModel();
    int delay = 5000, height = MainActivity.height, width = MainActivity.width, levell = 0;
    String mode = "", thetable = "",  Executor = "";
    String android_id = MainActivity.android_id, ds = MainActivity.DisplayString, uid = MainActivity.uref;
    ProgressBar pbbar;   String[] receivearr; int textcolor = md.darkgray;

    SQLiteDatabase mydatabase;

    int fontsmall= md.fontsmall, fontmedium = md.fontmedium; boolean smallscreen = false;




    int rpsdatebt = 37562, rpedatebt = 479934, pbbarid = 1921267, rpclickeddatebtn = 0,    consid =  3411;
    String rpstartdate = "", rpenddate = "", rpstatus = "", statustable  = "";
       Calendar c = Calendar.getInstance();
    int rpjavayear = c.get(Calendar.YEAR),  rpjavamonth = c.get(Calendar.MONTH),
            rpjavaday = c.get(Calendar.DAY_OF_MONTH);
    int   bigpad = 10, subpad = 7;
    boolean  valeron = md.DbName.equals("valeronpro");

    String DS = MainActivity.DisplayString;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_account);

        DisplayMetrics metrics = new DisplayMetrics();
        (MyAccount.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;
        width = metrics.widthPixels;

        height = (int) (height*0.97);
        if (height <=md.heightcut){height = (int)(height * 0.91); smallscreen = true; fontsmall -= 1; fontmedium -= 2;}

        mainlin = (LinearLayout) findViewById(R.id.mainlinear);

        mainlin.setPadding(10, 0, 10, 0);
        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        width =  width - 20;
        mainlin = md.setbackground(mainlin);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        receivearr = b.getStringArray("key");

        mode = md.getarray(receivearr, 0);
        ds = md.getarray(receivearr, 1);
        long exc = md.parselong(md.getarray(receivearr, 2));
        if (exc == 0){exc = 3;}
        Executor = String.valueOf(exc);
        if (mode.contains("~")){
            thetable = md.breakurl(mode, 1, "~");
            mode = md.before(mode, "~");
        }
        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);
        startoff();
    }


    @Override
    public void onBackPressed() {
        if (levell == 1){
            startoff();
        }else if (levell == 0){
            finish();
        }
    }



    boolean withfaves = false, admin = false;
    public void startoff(){

        levell = 0;
        // Spinner Drop down elements
        ArrayList<String> reporttypes = new ArrayList<String>(), reportmethods = new ArrayList<>();

        reporttypes.add("My Account");reportmethods.add("myaccount");
        boolean history = true;

        String histitle = "My Orders History";
        if (history){reporttypes.add(histitle);reportmethods.add("myhistory");}

        reporttypes.add("My Addresses");reportmethods.add("myaddress");

        if (md.appsetting("AddFavouritesButton", MainActivity.DisplayString).toLowerCase().equals("true")){
            reporttypes.add("My Favourites");withfaves =true; reportmethods.add("myfavourites");}

        mainlin.removeAllViews();
        int lineheight = (int) (0.08*height);
        for (int i = 0; i < reporttypes.size(); i++){

MaterialButton mb = md.makebutton(MyAccount.this, reporttypes.get(i),
        "", i + 5,
        "General", DS, md.fontmedium, width, lineheight);
            mainlin.addView(mb);

            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int theid = md.parseint(String.valueOf(mb.getId())) - 5;
                    levell = 1;
                    setTitle(md.getstring(reporttypes, theid)   );

                  /*  NestedScrollView ns  = new NestedScrollView(MyAccount.this);
                    ns.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(height * 0.9)));
                    mainlin.addView(ns);
                    sublin = new LinearLayout(MyAccount.this); sublin.setOrientation(LinearLayout.VERTICAL);
                    sublin.setLayoutParams(new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
                    ns.addView(sublin);
*/
                    if (md.getstring(reportmethods, theid).equals("myaccount")){    myaccount();   }
                    else if (md.getstring(reportmethods, theid).equals("myhistory")){   myhistory(true,"CustomerOrders");  }
                    else if (md.getstring(reportmethods, theid).equals("purchasehistory")){   myhistory(true, "PurchaseOrders");  }
                    else if (md.getstring(reportmethods, theid).equals("myaddress")){  myaddresses(); }
                    else if (md.getstring(reportmethods, theid).equals("myfavourites")){   favourites();  }

                }
            });
        }


    }

    String rpobjtable = "";
    final int rpobjid = 53472, rprunrepid = 64957;
    LinearLayout hislin;
    public void myhistory(boolean neww, String table){

        Intent intent = new Intent(MyAccount.this.getApplicationContext(), Activities.class);
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
        Intent intent = new Intent(MyAccount.this.getApplicationContext(), Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "Favourites",  ds, MainActivity.Executor, "", "", "", ""});
        intent.putExtras(b);
        startActivity(intent);
    }
    public void myaddresses(){


        Intent intent = new Intent(MyAccount.this.getApplicationContext(), Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "Address",  ds, MainActivity.Executor, "1", "", "", ""});
        intent.putExtras(b);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);


    }



    public void myaccount(){

        mainlin.removeAllViews();
        ProgressBar pb = new ProgressBar(MyAccount.this);
        mainlin.addView(pb);
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
            RequestQueue queue = Volley.newRequestQueue(MyAccount.this);

            String url = md.url + "gettable";
            url = url + "?code="+md.auth(MyAccount.this,  mydatabase, android_id, uid)+"~Users^|Reference^"+ uid;

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  btstring = response; btsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ getaccount(   callBack,  attempt); }
                    else{ btsuccess =false; md.toast(MyAccount.this,  "Error: Please Try Again"); }
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
            md.toast(MyAccount.this, z);
        }
        else if(btsuccess & mainlin != null) {
            mainlin.removeAllViews();

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
                MaterialTextView mt = new MaterialTextView(MyAccount.this);  mt.setHeight(lineheight);mainlin.addView(mt);
                mt.setWidth(width);  mt = md.textviewdesign(MyAccount.this, mt, "Info",ds);   mt.setText("Name: " + name);

                mt = new MaterialTextView(MyAccount.this);  mt.setHeight(lineheight); mainlin.addView(mt);
                mt.setWidth(width);  mt = md.textviewdesign(MyAccount.this, mt, "Info",ds);   mt.setText("Email: " + email);

                mt = new MaterialTextView(MyAccount.this);  mt.setHeight(lineheight); mainlin.addView(mt);
                mt.setWidth(width);  mt = md.textviewdesign(MyAccount.this, mt, "Info",ds);   mt.setText("Date Registered: " + created);

                MaterialButton btminus = new MaterialButton(MyAccount.this);
                btminus.setText("Log Out"); mainlin.addView(btminus);
                btminus = md.buttondesign(MyAccount.this, btminus, "General", ds);
                btminus.setLayoutParams(new LinearLayout.LayoutParams(width - (subpad * 2) - bigpad, (int)(height * 0.08)));
                btminus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {  logout();  }
                });

                ProgressBar pb = new ProgressBar(MyAccount.this); pb.setId(pbbarid); mainlin.addView(pb); pb.setVisibility(View.GONE);
            }
        }
    }

    String logstring = ""; boolean logsuccess = false;

    public void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MyAccount.this);
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

                                Intent intent = new Intent(MyAccount.this, Startup.class);
                                startActivity(intent);
                            } else {
                                logoff(new MainActivity.VolleyCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        Intent intent = new Intent(MyAccount.this, Startup.class);
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
            RequestQueue queue = Volley.newRequestQueue(MyAccount.this);

            String logurl =  md.url + "logoff?code="+ md.auth(MyAccount.this,  mydatabase, android_id, uid);

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, logurl , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  logstring = response; logsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    logsuccess = false; md.toast(MyAccount.this,  "Error: Please Try Again");
                }
            });
            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }











    public void aed(String table){

        hislin.removeAllViews();

        MaterialTextView mt = md.maketextview(MyAccount.this, "Loading "+table+"... Please Wait...", "", 0, "Info", ds, md.fontmedium,
                width, (int)(height * 0.1), true);

        hislin.addView(mt);
        ProgressBar pb = new ProgressBar(MyAccount.this);
        hislin.addView(pb);


        gettable(new MainActivity.VolleyCallBack() {
            @Override public void onSuccess() {  showtable();  } }, 0, MyAccount.this);
    }

    String tbstring =  ""; boolean  tbsuccess =  false;
    public void gettable(  final MainActivity.VolleyCallBack callBack,
                           int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String ur =  md.auth(MyAccount.this,  mydatabase, android_id, uid)+"~" +rpstartdate +"~" +rpenddate;

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
                    md.toast(MyAccount.this, "Failed To Reach Server... Retrying");
                    startoff();
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void showtable(){

        if (tbstring.indexOf("|") > -1 & hislin != null ){ }
        else{
            startoff();
        }
    }



}
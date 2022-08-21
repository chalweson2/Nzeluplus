package com.example.nzeluplus;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


import androidx.appcompat.app.AppCompatActivity;



import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Startup extends AppCompatActivity {

    LinearLayout linearLayout;
    String   android_id = "", userref = "", companyname  = ""; int width, height;
    MyModel md = new MyModel(); final int pbarid = 432596;
    public static final String EXTRA_MESSAGE = "com.example.android.twoactivities.extra.MESSAGE";
    boolean valeron = false;

    SQLiteDatabase mydatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        android_id = android.provider.Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        linearLayout = (LinearLayout) findViewById(R.id.mainlinear);
        linearLayout.setPadding(10, 0, 10, 0);
        /*linearLayout.setBackgroundColor(md.parsecolor(md.BackgroundColor));

        linearLayout.setBackgroundResource(R.drawable.backgroundxml);  */
        linearLayout.setBackgroundResource(R.drawable.backgroundsolid);
        DisplayMetrics metrics = new DisplayMetrics();
        (Startup.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels; width = metrics.widthPixels;

        TextView tv = new TextView(Startup.this); tv.setLines(2); linearLayout.addView(tv);

        int ww = (int) (width * 0.5);
        RelativeLayout rl = new RelativeLayout(Startup.this);
        rl.setLayoutParams(new LinearLayout.LayoutParams(width,   (int) (height * 0.6)));
        rl.setPadding((int) (ww * 0.5),  (int) (height * 0.2), (int) (ww * 0.5), 0);
        linearLayout.addView(rl);
        ImageView imgView= new ImageView(Startup.this);
        imgView.setImageResource(R.drawable.logotransparent);
        imgView.setLayoutParams(new ViewGroup.LayoutParams(ww, ww));
       // rl.addView(imgView);

        imgView= new ImageView(Startup.this);
        Glide.with(Startup.this).load(R.drawable.squares).into(imgView);
        imgView.setLayoutParams(new ViewGroup.LayoutParams(width, (int)(height * 0.1)));
        linearLayout.addView(imgView); imgView.setId(pbarid);



        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);

        valeron = md.DbName.equals("valeronpro");

        tv = new TextView(Startup.this); tv.setLines(2); linearLayout.addView(tv);

        FirebaseApp.initializeApp(this);

        if (demo()){
            maketable();
        }else{
            tv = new TextView(Startup.this); tv.setHeight((int) (height * 0.05)); linearLayout.addView(tv);
            tv.setText("This Demo Has Expired"); tv.setTextSize(md.fontlarge);
        }
    }



    public MaterialTextView helptextview(){
        MaterialTextView  mt = md.maketextview(Startup.this,
                "Need Help? Please Contact Us",
                "", 0, "Info", displaystring, md.fontsmall, width, (int) (height * 0.08), true);

        mt.setTextColor(md.white);

        mt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://wa.me/" + md.appsetting("WhatsappNumber", displaystring);

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        return mt;
    }


    public interface VolleyCallBack {
        void onSuccess();
    }


    public boolean demo(){
        boolean  proceed = true;

   /*     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime expdate = LocalDateTime.parse( md.demoexpirydate, formatter);
        Calendar expcal = Calendar.getInstance();
        expcal.setTime(Date.from(expdate.atZone(ZoneId.systemDefault()).toInstant()));
        long expmilli = expcal.getTimeInMillis();

        LocalDateTime nowdate = LocalDateTime.now(); Calendar nowcal = Calendar.getInstance();
        nowcal.setTime(Date.from(nowdate.atZone(ZoneId.systemDefault()).toInstant()));
        long nowmilli = nowcal.getTimeInMillis();

        if (nowmilli > expmilli & md.thisisademo){   proceed = false;  }*/

        return proceed;
    }



    public void maketable()   {


        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS  MOBILE (" +
                " ANDROIDID TEXT NOT NULL, REFERENCE TEXT NOT NULL,  LOGGEDIN INT NOT NULL);");

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS  ITEMLIST (ID INT NOT NULL, " +
                "NAME TEXT NOT NULL, ITEMNUMBER TEXT NOT NULL, SELLINGPRICE REAL NOT NULL,  PURCHASEPRICE REAL NOT NULL,  " +
                "ITEMTYPE INT NOT NULL, REFERENCE TEXT NOT NULL,  SIZETEXT TEXT NOT NULL, VAT REAL NOT NULL, " +
                "SITE INT NOT NULL, PIC1 TEXT NOT NULL, ISAVAILABLE INT NOT NULL, SITEREF TEXT NOT NULL);");


        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS  VALERONCURRENT (SITEREF TEXT NOT NULL, POSUSERID TEXT NOT NULL, POSUSER TEXT NOT NULL, " +
                "COMPANYLOCATION TEXT NOT NULL,  COMPANYLOCATIONNAME TEXT NOT NULL, ONLINE TEXT NOT NULL,   " +
                "CURRENCYCODE TEXT NOT NULL, CURRENCYSYMBOL TEXT NOT NULL, TPIN TEXT NOT NULL, COMPANYNAME TEXT NOT NULL, CONTACTNUMBER TEXT NOT NULL" +
                ", THANKYOUMESSAGE TEXT NOT NULL);");

        if (valeron){

            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS  OFFLINESALESCART (ID INTEGER PRIMARY KEY NOT NULL, " +
                    "NAME TEXT NOT NULL, " +
                    "ITEMNUMBER TEXT NOT NULL, " +
                    "SELLINGPRICE REAL NOT NULL,  " +
                    "PURCHASEPRICE REAL NOT NULL,  " +
                    "ITEMID INT NOT NULL, " +
                    "ITEMTYPE INT NOT NULL, " +
                    "REFERENCE TEXT NOT NULL,  " +
                    "SIZETEXT TEXT NOT NULL, " +
                    "VAT REAL NOT NULL, " +
                    "SITE INT NOT NULL, " +
                    "PIC1 TEXT NOT NULL, " +
                    "SITEREF TEXT NOT NULL, " +
                    "QUANTITY REAL NOT NULL,  " +
                    "TOTAL REAL NOT NULL,  " +
                    "EXECUTOR TEXT NOT NULL, " +
                    "EXECUTORNAME TEXT NOT NULL, " +
                    "ISAVAILABLE INT NOT NULL);");


            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS  OFFLINEPURCHASESCART  (ID INTEGER PRIMARY KEY NOT NULL, " +
                    "NAME TEXT NOT NULL, " +
                    "ITEMNUMBER TEXT NOT NULL, " +
                    "SELLINGPRICE REAL NOT NULL,  " +
                    "PURCHASEPRICE REAL NOT NULL,  " +
                    "ITEMID INT NOT NULL, " +
                    "ITEMTYPE INT NOT NULL, " +
                    "REFERENCE TEXT NOT NULL,  " +
                    "SIZETEXT TEXT NOT NULL, " +
                    "VAT REAL NOT NULL, " +
                    "SITE INT NOT NULL, " +
                    "PIC1 TEXT NOT NULL, " +
                    "SITEREF TEXT NOT NULL, " +
                    "QUANTITY REAL NOT NULL,  " +
                    "TOTAL REAL NOT NULL,  " +
                    "ISAVAILABLE INT NOT NULL);");


            mydatabase.execSQL("   CREATE TABLE IF NOT EXISTS  VALERONSITES ( " +
                    "USERREFERENCE TEXT NOT NULL, " +
                    "NAME TEXT NOT NULL, URL TEXT NOT NULL, " +
                    "SITEREFERENCE TEXT NOT NULL, " +
                    "SUPERACCESS INT NOT NULL," +
                    "DBO TEXT NOT NULL, EXECUTOR INT NOT NULL, " +
                    "LOGGEDIN INT NOT NULL);");


            mydatabase.execSQL("   CREATE TABLE IF NOT EXISTS  VALOFFLINESALES ( " +
                    "ORDERNUMBER TEXT NOT NULL, " +
                    "CREATED TEXT NOT NULL, " +
                    "URL TEXT NOT NULL, " +
                    "CODE TEXT NOT NULL, " +
                    "SYNCHED INT NOT NULL," +
                    "TOTAL REAL NOT NULL, " +
                    "CUSTOMER TEXT NOT NULL, " +
                    "SITEREF TEXT NOT NULL,"+
                    "ID INTEGER PRIMARY KEY NOT NULL);");

            mydatabase.execSQL("   CREATE TABLE IF NOT EXISTS  VALOFFLINEPURCHASES ( " +
                    "ORDERNUMBER TEXT NOT NULL, " +
                    "CREATED TEXT NOT NULL, " +
                    "URL TEXT NOT NULL, " +
                    "CODE TEXT NOT NULL, " +
                    "SYNCHED INT NOT NULL," +
                    "TOTAL REAL NOT NULL, " +
                    "SITEREF TEXT NOT NULL,"+
                    "ID INTEGER PRIMARY KEY NOT NULL);");

            mydatabase.execSQL("   CREATE TABLE IF NOT EXISTS  VALSUPPLIERS ( " +
                    "ORDERNUMBER TEXT NOT NULL, " +
                    "CREATED TEXT NOT NULL, " +
                    "SYNCHED INT NOT NULL," +
                    "REFERENCE TEXT NOT NULL, " +
                    "NAME TEXT NOT NULL, " +
                    "SURNAME TEXT NOT NULL, " +
                    "CELL TEXT NOT NULL, " +
                    "EMAIL TEXT NOT NULL, " +
                    "SITEREF TEXT NOT NULL,"+
                    "ID INT NOT NULL);");


            MyModel.option oi = md.valeronuser(mydatabase);

            // md.toast(Startup.this, oi.Option1);
            if (!oi.Option1.equals("")){
                trylogin(oi.Option1);
            }else{ startregister();  }
        }else{
            ArrayList<MyModel.option> recs = new ArrayList<>();
            Cursor rs =  mydatabase.rawQuery( "select * from MOBILE where LOGGEDIN = 1 and REFERENCE != ''", null );
            while ( rs.moveToNext()) {
                recs.add(new MyModel.option("",
                        rs.getString(rs.getColumnIndex("ANDROIDID")),
                        rs.getString(rs.getColumnIndex("REFERENCE")),
                        String.valueOf(rs.getInt(rs.getColumnIndex("LOGGEDIN")))));
            }
            if (recs.size() > 0){
                for (int i = 0; i < recs.size(); i++){
                    if (md.getoption(recs, i).Option1.equals(android_id) & Integer.parseInt(md.getoption(recs, i).Option3) == 1){
                        userref = md.getoption(recs, i).Option2; break;
                    }
                }
                trylogin(userref);
            }else{
                trylogin(userref);
            }
        }
    }


    public void trylogin(String usref){

     /*   String mm = md.auth(mydatabase, android_id ,  usref);

        getlogin(mm, new Startup.VolleyCallBack() {
            @Override
            public void onSuccess() {  showopen();  }
        }, 0, Startup.this);*/





        // FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            getlogin(usref, "" ,  new Startup.VolleyCallBack() {
                                @Override
                                public void onSuccess() {  showopen();  }
                            }, 0, Startup.this);
                            return;
                        }
                        String token = task.getResult();
                        String tk = md.encrypt(Startup.this, token, true, true);
                        getlogin( usref, tk,  new Startup.VolleyCallBack() {
                            @Override
                            public void onSuccess() {  showopen();  }
                        }, 0, Startup.this);
                    }
                });
    }


    public void getcrypt(   final Startup.VolleyCallBack callBack,
                            int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String url = "https://www.valeronpro.com/Valeron/docrypt?code="+ md.encrypt(Startup.this, "www", true, true);

        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                crypstring = response;
                //  crypstring = md.decrypt(response, true);
                crypSuccess = true;
                callBack.onSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (attempt < md.attempts){
                    getcrypt(  callBack,  attempt, cnt);
                }else{
                    //  md.toast(Startup.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    String crypstring =  ""; boolean  crypSuccess =  false;





    public void getlogin(String usref, String token,  final Startup.VolleyCallBack callBack, int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String mm = md.auth(Startup.this, mydatabase, android_id ,  usref);
        if (!token.equals("")){mm = md.auth(Startup.this, mydatabase, android_id ,  usref) + "~" + token; }
        String url = md.url + "appstartup?code="+mm;

        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                responsestring = response;//md.decrypt(response, true, true);
                dataSuccess = true;
                callBack.onSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (attempt < md.attempts){
                    md.toast(Startup.this, "Failed To Reach Server... Retrying"  );
                    getlogin( usref, token,callBack,  attempt, cnt);
                }else{
                    // responsestring =  "Error Starting Up, Please Check Your Internet Connection And Try Again";
                    // pbbar.setVisibility(View.GONE);
                    md.toast(Startup.this, "Failed To Reach Server... No Internet Connection");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    String responsestring =  ""; boolean  dataSuccess =  false; String displaystring = "";
    public void showopen(){

        if (responsestring.indexOf("|") > -1){
            ArrayList<String> lst = md.makearrayfromstring(responsestring, "|");

            if (lst.size() > 0){
                ArrayList<String> fst = md.makearrayfromstring(md.getstring(lst, 0), "~");
                String uref = md.breakurl(md.getstring(fst, 1), 1, "^");
                String useraccess = md.breakurl(md.getstring(fst, 2), 1, "^");

                String appaccess = md.breakurl(md.getstring(fst, 3), 1, "^");
                String exec = md.breakurl(md.getstring(fst, 4), 1, "^");
                String pos = md.breakurl(md.getstring(fst, 5), 1, "^");
                String location = md.breakurl(md.getstring(fst, 6), 1, "^");
                String currencysymbol = md.breakurl(md.getstring(fst, 7), 1, "^");
                String currencycode = md.breakurl(md.getstring(fst, 8), 1, "^");
                companyname = md.breakurl(md.getstring(fst, 9), 1, "^");
                displaystring = md.getstring(lst, 1);


               // linearLayout.addView(md.helptextview(Startup.this, displaystring, width, (int)(height * 0.09)));

                ArrayList<String> disarray = md.makearrayfromstring(displaystring, "~");

                for (int i = 3; i < disarray.size(); i++){
                    String cl = md.before(md.getstring(disarray, i), "^");
                    String vl = md.breakurl(md.getstring(disarray, i), 1, "^");
                    if (cl.equals("PrimaryColor")){
                        if (!vl.equals("")){
                            try{
                                int col = Color.parseColor(vl);
                                Window window = Startup.this.getWindow();
                                // clear FLAG_TRANSLUCENT_STATUS flag:
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            }catch (Exception xx){
                                md.toast(this, xx.getMessage());
                            }
                        }
                    }
                }


                if (useraccess.equals("0") & appaccess.equals("1")){
                    startregister();
                }
                else  if (appaccess.equals("0")){
                    ImageView pbbar = (ImageView) findViewById(pbarid);
                    pbbar.setVisibility(View.GONE);
                    int wd = (int)(width * 0.96), lh = (int)(height * 0.09);
                    if (valeron){
                        linearLayout.removeAllViews();
                        MaterialTextView mt = md.maketextview(Startup.this, "Your License Has Expired", "", 0,
                                "General", displaystring, md.fontsmall, width, lh, true); linearLayout.addView(mt);

                        MaterialButton mb = md.makebutton(Startup.this, "Pay License Fee", "", 0,
                                "Info", displaystring, md.fontsmall, wd, lh   );linearLayout.addView(mb);

                        mb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = "https://www.valeronpro.com/Home/License";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });

                        View v = md.line(Startup.this, width, (int)(height * 0.05), md.transparent); linearLayout.addView(v);

                        mt = md.maketextview(Startup.this, "Have You Paid Your License Fee?", "", 0,
                                "General", displaystring, md.fontsmall, width, (int)(height * 0.09), true); linearLayout.addView(mt);

                        mb = md.makebutton(Startup.this, "Continue To Valeron", "", 0,
                                "Info", displaystring, md.fontsmall, wd, lh   );linearLayout.addView(mb);
                        mb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });

                        v = md.line(Startup.this, width, (int)(height * 0.05), md.transparent); linearLayout.addView(v);

                        mt = md.maketextview(Startup.this, "If You Have An Issue, Please Contact Us", "", 0,
                                "General", displaystring, md.fontsmall, width, (int)(height * 0.09), true); linearLayout.addView(mt);

                        mt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = "https://wa.me/" + md.appsetting("WhatsappNumber", displaystring);

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });
                        mt = md.maketextview(Startup.this, "Or Visit Our Website", "", 0,
                                "General", displaystring, md.fontsmall, width, (int)(height * 0.09), true); linearLayout.addView(mt);


                        mt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = "https://www.valeronpro.com/Home/Valeron";  Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));   startActivity(i); }   });
                    }else {
                        MaterialButton mb = md.makebutton(Startup.this, "App Failed To Load, Please Try Again", "", 0,
                                "Info", displaystring, md.fontsmall, wd, lh   );linearLayout.addView(mb);
                        mb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });
                    }

                }
                else   if (useraccess.equals("1")){
                    Bundle b = new Bundle();
                    b.putStringArray("key", new String[]{ displaystring, uref,  exec, pos,
                            location, currencysymbol, currencycode, companyname, "" });
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(b);

                    finish();
                    startActivity(intent);
                }
            } else { startregister(); }
        } else{ startregister(); }

    }


    public void startregister(){
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{displaystring});
        Intent intent = new Intent(getApplicationContext(), Register.class);
        intent.putExtras(b);
        startActivity(intent);}

}
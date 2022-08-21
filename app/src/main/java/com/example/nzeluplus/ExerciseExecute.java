package com.example.nzeluplus;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ExerciseExecute extends AppCompatActivity {
    LinearLayout mainlin, sublin;
    ArrayList<MyModel.option> imagelist = new ArrayList<>();
    int width = 0, height = 0; MyModel md = new MyModel();
    public static final String EXTRA_MESSAGE = "com.example.android.twoactivities.extra.MESSAGE";
    String DS = "", executor = "", exid = "", ansid = "", profileid = "",   uid = "", android_id = "",
            brand = "", category = "", topic = "";
    int fontsmall = md.fontsmall, questioncount = 0, fontmedium = md.fontmedium,
            currentindex = 0;
    boolean smallscreen  = false, canchangeqeustion = true;
    ProgressBar pbbar; SQLiteDatabase mydatabase;

    int downloadstatusid = 501, aid = 502, pbarid = 503, timetv = 504, qlabelid = 505, bid = 506,
            did = 507, cid = 508, previd = 509, infoid = 510,  skipid = 511, nextid = 512,
            saveid =  513, xp5id = 514, paymentbtn = 515,
            confirmbtn = 516,  corbtnid = 517, profilenameid= 518;

    String radiopre = "600", textboxpre ="700", imagepre ="800", linpre = "900" ;

    Integer[] radioidlist; String[] letters, anslist;

    String exstring = "", answerstring = "", profilestring= "",  subdue ="", currencysymbol = "", currencycode = "";
    int  corindex = 0, wrondex  = 0, congindex = 0, tryagainindex = 0, textcolor = md.darkgray;

    String currentquestiontext = "",  currentquestionparagraph2 = "", currentanswerletter  = "",
            currentexplanation = "", currentanswertext ="", useranswerletter= "", currentquestionpic = "";

    int seconds = 30, qsecs = 30;

    PrimeThread timerthread;
    String paymentrefid = md.randomstring();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_execute);
        android_id = Settings.Secure.getString(ExerciseExecute.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        mainlin = (LinearLayout) findViewById(R.id.mainlinear);
        mainlin.setPadding(10, 0, 10, 5);

        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        MyModel md = new MyModel();
        DisplayMetrics metrics = new DisplayMetrics();
        (ExerciseExecute.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;  width = metrics.widthPixels;
        if (height <= md.heightcut){height = (int)(height * 0.91); smallscreen = true; fontsmall -= 1; fontmedium = md.fontsmall;}
        width = width - 22;

        ProgressBar pbbar = new ProgressBar(ExerciseExecute.this);
        pbbar.setId(pbarid);
        pbbar.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.07)));
        mainlin.addView(pbbar);
        pbbar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String[] rec = b.getStringArray("key");
        if (rec != null){

            DS = md.getarray(rec, 0); // the user reference number
            uid = md.getarray(rec, 1); // the user reference number
            executor = md.getarray(rec, 2);
            exstring = md.getarray(rec, 3);
            exid = md.getarray(rec, 4);
            profileid = md.getarray(rec, 5);
        }else{
            String wb = b.getString("webkey");
            if (wb != null){
                ArrayList<String> ks = md.makearrayfromstring(wb, "|");
                DS = md.getstring(ks, 0);
                uid = md.getstring(ks,1);
                executor = md.getstring(ks, 2);
                exid = md.getstring(ks, 3);
            }
        }

        mainlin.removeAllViews();
        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);

        textcolor = md.parsecolor(md.appsetting("TextColor", DS));

        int topht = (int)(height * 0.07);
        LinearLayout toplin = new LinearLayout(ExerciseExecute.this); mainlin.addView(toplin);
        toplin.setLayoutParams(new LinearLayout.LayoutParams(width, topht));

        ImageView bt = new ImageView(this); bt.setLayoutParams(new LinearLayout.LayoutParams(topht, topht));
        bt.setBackgroundResource(R.drawable.ic_baseline_navigate_before_24); toplin.addView(bt);
        bt.setColorFilter(md.appblue);
        bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) { tryfinish();  } });



        int remwidth = width - topht;//(2*topht) - md.fontlarge;

        MaterialTextView  tv = md.maketextview(ExerciseExecute.this, "",
                "", qlabelid, "Info", DS, fontsmall, (int)(remwidth*0.65),  topht, true);
        tv.setTypeface(null, Typeface.BOLD);  toplin.addView(tv);

        remwidth = (int)((remwidth * 0.35) - topht);

        tv = md.maketextview(ExerciseExecute.this, "", "", timetv, "Info", DS, fontmedium,
                remwidth,  topht, true);toplin.addView(tv); tv.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { exsettings();   }  });

        bt = new ImageView(this); bt.setLayoutParams(new LinearLayout.LayoutParams(topht, topht));
        bt.setBackgroundResource(R.drawable.ic_baseline_timer_24); toplin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {   exsettings(); } });



        NestedScrollView scrl = new NestedScrollView(ExerciseExecute.this);
        //scrl.setScrollbarFadingEnabled(false);
        scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.75))) ;
        mainlin.addView(scrl); scrl.setPadding(0, 4, 0, 4);


        sublin = new LinearLayout(ExerciseExecute.this);
        sublin.setOrientation(LinearLayout.VERTICAL); scrl.addView(sublin);

        tv = md.maketextview(ExerciseExecute.this, "Loading Exercise...",
                "", 0, "Info", DS, fontsmall, width,  topht, true); sublin.addView(tv);
        ProgressBar pb = new ProgressBar(ExerciseExecute.this); pb.setId(pbarid);pb.setVisibility(View.VISIBLE);
        sublin.addView(pb);

        int htt = (int) (height * 0.07);
        LinearLayout botlin = new LinearLayout(ExerciseExecute.this); mainlin.addView(botlin);
        botlin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  htt));

        int hw = (int)(width * 0.15), outerwidth =  (int)(width * 0.2);
        View v = md.line(ExerciseExecute.this,outerwidth, htt, md.transparent); botlin.addView(v);

        bt = new ImageView(this); bt.setLayoutParams(new LinearLayout.LayoutParams(hw, htt));
        bt.setBackgroundResource(R.drawable.ic_baseline_navigate_before_24); botlin.addView(bt);
        bt.setId(previd); bt.setColorFilter(md.appblue); bt.setEnabled(false);

        bt =  new ImageView(this); bt.setLayoutParams(new LinearLayout.LayoutParams(hw, htt));
        bt.setBackgroundResource(R.drawable.ic_baseline_info_24); botlin.addView(bt);
        bt.setId(infoid); bt.setEnabled(false);bt.setColorFilter(md.appblue); bt.setEnabled(false);

        bt = new ImageView(this); bt.setLayoutParams(new LinearLayout.LayoutParams(hw, htt));
        bt.setBackgroundResource(R.drawable.ic_baseline_article_24); botlin.addView(bt);
        bt.setColorFilter(md.appblue); bt.setId(skipid); bt.setEnabled(false);
        bt.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
            skipquestion();
        }  });


        bt = new ImageView(this); bt.setLayoutParams(new LinearLayout.LayoutParams(hw, htt));
        bt.setBackgroundResource(R.drawable.ic_baseline_navigate_next_24); botlin.addView(bt); bt.setId(nextid);
        bt.setColorFilter(md.appblue); bt.setEnabled(false);
        v = md.line(ExerciseExecute.this, outerwidth, htt, md.transparent); botlin.addView(v);

        radioidlist = new Integer[]{aid, bid, cid, did};
        letters = new String[]{"A", "B", "C", "D"};

        Integer[] corgifs = new Integer[]
                {R.drawable.correct1, R.drawable.correct2, R.drawable.correct3, R.drawable.correct4,
                        R.drawable.correct5, R.drawable.correct6, R.drawable.correct7, R.drawable.correct8, R.drawable.correct9,
                        R.drawable.correct11, R.drawable.correct12, R.drawable.correct13, R.drawable.correct14,
                        R.drawable.correct15, R.drawable.correct16, R.drawable.correct17, R.drawable.correct19,
                        R.drawable.correct20,  R.drawable.correct22, R.drawable.correct23, R.drawable.correct24,
                        R.drawable.correct25, R.drawable.correct26};

        Integer[] wrongifs = new Integer[] {R.drawable.wrong1, R.drawable.wrong2, R.drawable.wrong3, R.drawable.wrong4,
                R.drawable.wrong5, R.drawable.wrong6, R.drawable.wrong7, R.drawable.wrong8, R.drawable.wrong9, R.drawable.wrong10,
                R.drawable.wrong11, R.drawable.wrong12, R.drawable.wrong13};

        Integer[] congifs = new Integer[] {R.drawable.congrats1, R.drawable.congrats2, R.drawable.congrats3, R.drawable.congrats4,
                R.drawable.congrats5};

        corindex = corgifs[md.randomint(corgifs.length)];
        wrondex = wrongifs[md.randomint(wrongifs.length)];
        congindex =  congifs [md.randomint(congifs .length)];
        tryagainindex =congindex; if (tryagainindex > 0){tryagainindex--;}else{tryagainindex++;}


        ArrayList<MyModel.option> recs = new ArrayList<>();
        Cursor rs =  mydatabase.rawQuery( "select * from EXERCISEQUESTIONTIMER where USER = '"+uid +"'", null );
        while ( rs.moveToNext()) {
            int aid = rs.getColumnIndex("SECONDS");
            recs.add(new MyModel.option("",
                    rs.getString(aid), "", ""));
        }
        if (recs.size() > 0) {
            seconds = md.parseint(md.getoption(recs, 0).Option1);
        }


        timerthread = new PrimeThread();

        loadexercise( new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() { loadexercisedone(); }
        }, 0, ExerciseExecute.this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        if (currentindex == 0  & keyCode == KeyEvent.KEYCODE_BACK){
            //keyCode == KeyEvent.KEYCODE_MENU ||           keyCode == KeyEvent.KEYCODE_TAB ||
            md.toast(ExerciseExecute.this, "Please Press The Home Button On The Top Right Of The Screen To Exit");
            // return 'true' to prevent further propagation of the key event
            return true;
        }else  if (currentindex > 0  & keyCode == KeyEvent.KEYCODE_BACK){
            nextquestion(currentindex -1);
            return true;
        }

        // let the system handle all other key events
        return super.onKeyDown(keyCode, event);
    }

    LinearLayout cuslin;  Dialog cusdialog;
    public void tryfinish(){
        cusdialog = new Dialog(ExerciseExecute.this);
        cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cusdialog.setContentView(R.layout.custom);
        cusdialog.setCancelable(true);
        cusdialog.setCanceledOnTouchOutside(true);
        Window window = cusdialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
        cuslin.setOrientation(LinearLayout.VERTICAL);
        cuslin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        int  cushit = (int)(height*0.55),  cuswid = (int)(width*0.9);

        cusdialog.show();
        cuslin.setPadding(6, 0, 0, 0);
        int cuslineht = (int)(cushit * 0.12);

        MaterialTextView mt =  md.maketextview(ExerciseExecute.this,  "Are You Sure You Want To Exit This Exercise?", "", 0, "Info",
                DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);

        LinearLayout.LayoutParams pms  = new LinearLayout.LayoutParams(cuswid, cuslineht);
        LinearLayout ln = new LinearLayout(ExerciseExecute.this); ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setLayoutParams(pms);  cuslin.addView(ln);
        MaterialButton mb = md.makebutton (ExerciseExecute.this,"Cancel",
                "", 0, "Cancel", DS, fontsmall, (int)(cuswid*0.48), cuslineht); ln.addView(mb);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cusdialog.cancel();
            }
        });

        View v = md.line(ExerciseExecute.this, (int)(cuswid * 0.01), cuslineht, md.transparent); ln.addView(v);

        mb = md.makebutton (ExerciseExecute.this,"Exit Exercise",
                "", 0, "Go", DS, fontsmall, (int)(cuswid*0.48), cuslineht); ln.addView(mb);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerthread.stopRunning();
                finish();
            }
        });


    }


    String qstring = ""; boolean qsuccess = false;
    public void loadexercise(final MainActivity.VolleyCallBack callBack,  int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "loadexercise?code="+md.auth(ExerciseExecute.this, mydatabase, android_id, uid)+"~"+exid;

        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        qstring  = response;
                        qsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){loadexercise(callBack,  attempt, cnt);
                }else{
                    qsuccess = false;
                    qstring =  "";
                    md.toast(ExerciseExecute.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);

    }

    public void  loadexercisedone(){
        if (qsuccess  & sublin != null){

            ArrayList<String> tables =  md.makearrayfromstring(qstring ,"|");
            exstring = md.getstring(tables, 0);
            profilestring = md.getstring(tables, 1);

            ArrayList<String> exarray = md.makearrayfromstring(exstring, "¬");
            ArrayList<String> exrules = md.makearrayfromstring(md.getstring(exarray, 0), "#");
            ArrayList<String> excols = md.makearrayfromstring( md.getstring(exarray, 1), "~");
            ArrayList<String> curray = md.makearrayfromstring(md.getstring(exrules, 15),"~");
            imagelist.clear();

            ArrayList<String> row = md.makearrayfromstring(md.getstring(exarray, 2), "~");
            brand = md.coloption(excols, "ValueBrand", row).Value;
            category = md.coloption(excols, "ValueCategory", row).Value;
            topic = md.coloption(excols, "ValueTopic", row).Value;

            setTitle(md.coloption(excols, "Name", row).Value);
            for (int c = 1; c <= 20; c++){
                String idd =  md.before(md.getstring(exarray, 2), "~");
                String qpg =  md.coloption(excols, "Q"+ String.valueOf(c) + "Paragraph1", row).Value;
                String pcs =  md.coloption(excols, "Q"+ String.valueOf(c) + "Pic", row).Value;                    String qq =  md.coloption(excols, "Q"+ String.valueOf(c) + "Paragraph1", row).Value;

                if (qpg.equals("")){
                    break;}
                else{
                    questioncount++;
                    if (!pcs.equals("")){
                        String  url = "https://www.valeronpro.com/Content/SiteImages/" + md.Site + "/" + pcs;
                        MyModel.option oi = new MyModel.option();
                        oi.ID = "Q"+ String.valueOf(c) + "Pic"; oi.Option1 = pcs; oi.Option2 = url;
                        imagelist.add(oi);
                    }
                }
            }


            for (int i = 0; i < curray.size(); i++){
                String col = md.before(md.getstring(curray, i), "^"),
                        vl = md.breakurl(md.getstring(curray, i), 2, "^");
                if (col.equals("CurrencySymbol")){currencysymbol = vl;}
                else if (col.equals("CurrencyCode")){currencycode = vl;}
                else if (col.equals("SubscriptionDue")){subdue= vl;}
            }


            boolean gopay = false;

            if ((subdue.toLowerCase().equals("true") || subdue.toLowerCase().equals("1"))){gopay = true;}

            if (gopay){

                sublin.removeAllViews();
                String pm =  md.appsetting("SubscriptionDueMessage", DS);

                View v = md.line(this, width, 4, md.transparent); sublin.addView(v);
                int mc2 =  md.countlines(pm,md.fontmedium, width);
                MaterialTextView mt = md.maketextview(this, pm, "", 0, "Info", DS,
                        md.fontmedium, width, mc2*((int)(md.fontmedium*2.4)), false);
                mt.setLines(mc2);
                sublin.addView(mt);

                MaterialButton bt  = md.makebutton(this, "Pay Now" , "", paymentbtn, "Go", DS,
                        fontsmall, width, (int)(height * 0.08)); sublin.addView(bt);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override  public void onClick(View view) {
                        gotopayment();
                    }});
                v = md.line(this, width, (fontmedium * 2), md.transparent); sublin.addView(v);


                String cns = "Select \"Continue\" If You Have Made Your Payment Already";
                mc2 =  md.countlines(cns,md.fontmedium, width);
                mt = md.maketextview(this, cns, "", 0, "Info", DS,
                        md.fontmedium, width, mc2*((int)(md.fontmedium*2.4)), false);
                mt.setLines(mc2);
                sublin.addView(mt);

                String xn =  md.appsetting("ExercisesName", DS);
                bt  = md.makebutton(this, "Continue To "  +xn, "", confirmbtn, "General",
                        DS, fontsmall, width, (int)(height * 0.08));
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override  public void onClick(View view) {
                        MaterialButton mb = (MaterialButton) findViewById(confirmbtn);
                        MaterialButton  pb = (MaterialButton) findViewById( paymentbtn);
                        if (mb != null & pb != null){mb.setEnabled(false); pb.setEnabled(false);
                        }
                        if(pbbar != null){pbbar.setVisibility(View.VISIBLE);}

                        //"u8v5d3i0h3"
                        //paymentrefid
                        fluttercheck( paymentrefid, new MainActivity.VolleyCallBack() {
                            @Override
                            public void onSuccess() { flutterresult(); }
                        }, 0);
                    }});
                sublin.addView(bt);

                pbbar = new ProgressBar(this); pbbar.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(height * 0.08)));
                pbbar.setVisibility(View.GONE); sublin.addView(pbbar);
                mt = md.helptextview(ExerciseExecute.this, DS, width, (int)(height * 0.08)); sublin.addView(mt);

            }
            else{  downloadpopup(); }
        }
        else  {
            md.toast(ExerciseExecute.this, " Error, please try again");
        }
    }


    public void gotopayment(){

        String sm =  md.appsetting("SubscriptionAmount", DS);
        String rst = md.encrypt(ExerciseExecute.this, "Subscription~"+paymentrefid+"~"+ sm+"~"+currencycode , true, true);
        String ath = md.auth(ExerciseExecute.this, mydatabase, android_id, uid)+"~" + rst;
        String url = "https://"+md.Site+"/Home/Flutterwave?code=" +ath;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    JSONObject ftcheck; boolean ftsuccess = false;
    public void fluttercheck(String reff, final MainActivity.VolleyCallBack callBack, int attempts){
        RequestQueue requstQueue = Volley.newRequestQueue(this);

        String url = "https://api.flutterwave.com/v3/transactions/verify_by_reference?tx_ref=" + reff;
        int attempt = attempts + 1;
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(callBack != null){
                            ftcheck = response;
                            ftsuccess =true;
                            callBack.onSuccess();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (attempt < md.attempts){ fluttercheck(reff,  callBack,  attempt);
                        }else{
                            ftsuccess = false;
                            md.toast(ExerciseExecute.this, "Error: " + error.getMessage());
                        }
                    }
                }
        ){
            //here I want to post data to sever

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", getString(R.string.flutterwavepk));
                return headers;
            }
        };

        jsonobj = md.requestwaitjson(jsonobj); requstQueue.add(jsonobj);

    }

    public void flutterresult(){

        if (ftsuccess){
            try {
                String status = ftcheck.getJSONObject("data").getString("status");
                String flw_ref = ftcheck.getJSONObject("data").getString("flw_ref");
                String amount = ftcheck.getJSONObject("data").getString("amount");
                String amount_settled = ftcheck.getJSONObject("data").getString("amount_settled");


                if (!flw_ref.equals("") & md.parsefloat(amount) > 0){
                    flutterconfirm(flw_ref, status, new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            flutterdone();
                        }
                    }, 0);
                }else{
                    MaterialButton mb = (MaterialButton) findViewById(confirmbtn);
                    MaterialButton  pb = (MaterialButton) findViewById( paymentbtn);
                    if (mb != null & pb != null){mb.setEnabled(true); pb.setEnabled(true);
                    }
                    if(pbbar != null){pbbar.setVisibility(View.VISIBLE);}
                    md.toast(ExerciseExecute.this, "Sorry, Your Payment Could Not Be Confirmed. Please Click \"Continue\" Again");
                }

            } catch (JSONException e) {
                // e.printStackTrace();
            }
        }
        else{
            md.toast(ExerciseExecute.this, "Payment Confirmation Unsuccessful");
        }
    }

    String ddstring = ""; boolean ddsuccess = false;
    public void flutterconfirm(String flutterid, String fluttersuc, final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(ExerciseExecute.this);

            String url = md.url + "flutterwaveconfirm";
            url = url + "?code="+md.auth(ExerciseExecute.this, mydatabase, android_id, uid)+"~" +paymentrefid
                    +"~" +fluttersuc+"~" +flutterid+"~Subscription";

            // md.clipboard(ExerciseExecute.this, url);
            //  md.toast(ExerciseExecute.this, url);
            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  ddstring = response; ddsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ flutterconfirm(flutterid, fluttersuc,  callBack,  attempt); }
                    else{ ddsuccess =false; md.toast(ExerciseExecute.this,  "Failed To Reach Server... Retrying"); }
                }
            });

            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }

    public void flutterdone(){

        // Display the first 500 characters of the response string.
        if(ddsuccess) {
            if (ddstring.equals("success")){
                downloadpopup();
            }
        }
        else  if (!ddsuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(ExerciseExecute.this, z);
        }

    }


    ArrayList<MyModel.option> goload = new ArrayList<>();
    int imageloadedcount = 0;

    public void preload(){
        if (imagelist.size() > 0){
            ArrayList<MyModel.option> recs = new ArrayList<>();
            Cursor rs =  mydatabase.rawQuery( "select * from IMAGES where TABLENAME = 'Exercises' and ROWID = '+exid+'", null );
            while ( rs.moveToNext()) {
                int aid = rs.getColumnIndex("NAME");
                recs.add(new MyModel.option("",  rs.getString(aid), "", ""));
            }
            goload = new ArrayList<>();
            for (int i = 0; i < imagelist.size(); i++){
                boolean gol = true;
                for (int r = 0; r < recs.size(); r++){
                    if (imagelist.get(i).Option1.equals(recs.get(r).Option1)){
                        gol = false;
                    }
                }
                if (gol){
                    goload.add(imagelist.get(i));
                }
            }

            for (int g = 0; g < goload.size(); g++){
                MyModel.option gl = goload.get(g);
                saveimagetodb(gl.Option2, width, (int) (width * 0.9), gl.ID,  gl.Option1, g == (goload.size()-1));
                imageloadedcount++;

                MaterialTextView mt = (MaterialTextView) findViewById(downloadstatusid);
                if (mt != null){
                    mt.setText("Loading Images " + String.valueOf(imageloadedcount) + " of " + String.valueOf(goload.size()));
                }
            }

        }else{
            if (cusdialog != null){cusdialog.cancel();}
            doprofiles( true);
        }
    }



    public void saveimagetodb( String url, int width, int height,   String column,  String name, boolean finald){
        MyModel.LoadImageTask.Listener listener = new MyModel.LoadImageTask.Listener() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] img = bos.toByteArray();

                ContentValues values = new ContentValues();
                values.put("TABLENAME","Exercises");
                values.put("ROWID",exid);
                values.put("COLUMNNAME",column);
                values.put("NAME", name);
                values.put("DATA", img);

                SQLiteDatabase mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);
                try {
                    mydatabase.insert("IMAGES", null, values);

                    for (int g = 0; g < goload.size(); g++){
                        if (goload.get(g).Option1.equals(name)){
                            goload.get(g).Option3 = "1";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mydatabase.close();
                }
                if (finald){
                    if (cusdialog != null){cusdialog.cancel();}
                    doprofiles( true);
                }
            }

            @Override
            public void onError() { md.toast(ExerciseExecute.this, "Error Loading Image"); }
        };
        MyModel.LoadImageTask lit = new MyModel.LoadImageTask(listener, width, height);
        lit.execute(url);
    }



    public void doprofiles( boolean neww){

/*if (md.parseint(profileid) > 0 & !override){
    nextquestion(startindex);
}else{}*/

        if (neww){

            cusdialog = new Dialog(ExerciseExecute.this);
            cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cusdialog.setContentView(R.layout.custom);

            cusdialog.setCancelable(false);
            cusdialog.setCanceledOnTouchOutside(false);
            Window window = cusdialog.getWindow();
            int ww = (int)(width*0.95);
            window.setLayout(ww, AbsListView.LayoutParams.WRAP_CONTENT);

            cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
            cuslin.setOrientation(LinearLayout.VERTICAL);
            cuslin.setLayoutParams(new FrameLayout.LayoutParams(ww,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            cusdialog.show();
        }

        cuslin.removeAllViews();
        int cushit = (int)(height*0.55),  cuswid = (int)(width*0.85);
        cuslin.setPadding(6, 0, 0, 0);
        int cuslineht = (int)(cushit * 0.11);


        String profsingle = md.appsetting("ProfileName", DS);
        String profplur = md.appsetting("ProfilePlural", DS);
        String profmess = md.appsetting("ProfileMessage", DS);
        cusdialog.setTitle("Select " + profsingle);
        LinearLayout lnn = new LinearLayout(ExerciseExecute.this); lnn.setOrientation(LinearLayout.HORIZONTAL);
        lnn.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht)); cuslin.addView(lnn);

        int firstgap = (int)(0.5*(cuswid - cuslineht));
        View  v = md.line(this, firstgap, md.fontmedium, md.transparent);  lnn.addView(v);

        ArrayList<Integer>  des = md.applydesign(ExerciseExecute.this, "General", DS);

        ImageView  img = new ImageButton(this); lnn.addView(img);
        img.setLayoutParams(new LinearLayout.LayoutParams(cuslineht, cuslineht));
        img.setBackgroundResource(R.drawable.ic_baseline_child_care_24);
        img.setColorFilter(md.getint(des,0));



        int mc2 =  md.countlines(profsingle, md.fontlarge, cuswid);
        MaterialTextView nb = md.maketextview(this,  profsingle , "", 0, "Info", DS,
                md.fontlarge, cuswid, mc2*((int)(md.fontlarge*2.4)), true);
        nb.setLines(mc2);cuslin.addView(nb);

        v = md.line(ExerciseExecute.this, width, md.fontmedium, md.transparent); cuslin.addView(v);

        mc2 =  md.countlines(profmess, fontmedium, cuswid);
        nb = md.maketextview(this,  profmess , "", 0, "Info", DS,
                fontmedium, cuswid, mc2*((int)( fontmedium*2.4)), true);
        nb.setLines(mc2);cuslin.addView(nb);

        v = md.line(ExerciseExecute.this, width, 6, md.transparent); cuslin.addView(v);


        NestedScrollView ns = new NestedScrollView(this); cuslin.addView(ns);
        ns.setLayoutParams(new LinearLayout.LayoutParams(cuswid, (int)(cuslineht * 3.5)));

        RadioGroup rg = new RadioGroup(this); ns.addView(rg);

        ArrayList<String> prlist = md.makearrayfromstring(profilestring, "¬");
        ArrayList<String> prcols = md.makearrayfromstring(md.getstring(prlist, 1), "~");
        for (int c = 2; c < prlist.size(); c++)
        {
            ArrayList<String> row = md.makearrayfromstring(md.getstring(prlist, c), "~");
            String idd = md.before(md.getstring(prlist, c), "~");
            String name = md.coloption(prcols, "Name", row).Value;
            RadioButton rb = new RadioButton(this); rb.setText(name); rg.addView(rb);
            rb.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht));
            if (idd.equals(profileid)){rb.setChecked(true);}
            rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){profileid = idd;}
                }
            });
            v = md.line(ExerciseExecute.this, cuswid, 2, textcolor); rg.addView(v);
        }


        v = md.line(ExerciseExecute.this, cuswid, md.fontlarge, md.transparent); rg.addView(v);
        int bh = (int)(cuslineht * 1.5);
        LinearLayout ln = new LinearLayout(this); ln.setOrientation(LinearLayout.HORIZONTAL); cuslin.addView(ln);
        ln.setLayoutParams(new LinearLayout.LayoutParams(cuswid, bh));

        int hw = (int)(cuswid * 0.47);
        MaterialButton bt  = md.makebutton(ExerciseExecute.this, "New " + profsingle, "", 0,
                "General", DS, md.fontsmall, hw, bh); ln.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {  newprofile( cuswid, cuslineht, profsingle) ;}});


        v = md.line(ExerciseExecute.this, (int)(cuswid * 0.03), bh, md.transparent); ln.addView(v);

        bt  = md.makebutton(ExerciseExecute.this, "Start", "", 0,
                "Go", DS, md.fontsmall, hw, bh); ln.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {   if (md.parseint(profileid) <= 0){
                md.toast(ExerciseExecute.this, "Please Select A " + profsingle);
            }else{
                loadprofileanswers(profileid);
            }

            }});


    }




    public void newprofile( int cuswid, int cuslineht, String profsingle){

        cuslin.removeAllViews();
        LinearLayout lnclose = new LinearLayout( this);
        lnclose.setOrientation(LinearLayout.HORIZONTAL);
        lnclose.setLayoutParams(new LinearLayout.LayoutParams( cuswid, cuslineht));
        cuslin.addView(lnclose);

        View v = md.line(ExerciseExecute.this, (int)((0.95)*(cuswid - cuslineht)), cuslineht, md.transparent);
        lnclose.addView(v);


        ImageView mb = new ImageView(this); int cbh = (int) (cuslineht * 0.7);
        mb.setLayoutParams(new FrameLayout.LayoutParams( cbh, cbh));
        mb.setBackgroundResource(R.drawable.close);  lnclose.addView(mb);
        mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
            doprofiles(false);
        } });


        int mc2 =  md.countlines(profsingle, md.fontlarge, cuswid);
        MaterialTextView nb = md.maketextview(this,  "New "+ profsingle , "", 0, "Info", DS,
                md.fontlarge, cuswid, mc2*((int)(md.fontlarge*2.4)), true);
        nb.setLines(mc2);cuslin.addView(nb);

        v = md.line(ExerciseExecute.this, width, md.fontmedium, md.transparent); cuslin.addView(v);


        EditText ed = md.edittext(this, profilenameid, "Name", "", "", cuswid, cuslineht,
                md.fontmedium, DS); cuslin.addView(ed);

        v = md.line(ExerciseExecute.this, width, md.fontmedium, md.transparent); cuslin.addView(v);

        MaterialButton bt  = md.makebutton(ExerciseExecute.this, "Save And Start", "", 0,
                "General", DS, md.fontsmall, (int)(cuswid * 0.94), cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {    cusdialog.cancel();
                checknewprofile();}});

    }

    public void checknewprofile(){


        String nm = "", mss = "";
        EditText nmt =  (EditText) cusdialog.findViewById(profilenameid);
        if (nmt != null){
            nm = nmt.getText().toString();
            mss = md.checkvalue("Name",nm);
            if (!mss.equals("")){
                md.toast(ExerciseExecute.this, mss);
            }else{
                newprofile( nm, new MainActivity.VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        doneprofile();
                    }
                }, 0, this);
            }
        }

    }



    String tbresult = ""; boolean tbsuccess = false;
    public void newprofile( String name, final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        RequestQueue queue = Volley.newRequestQueue(cnt);
        final int attempt = attempts + 1;
        String url = md.url + "newprofile?code="+md.auth(this, mydatabase, android_id, uid)+"~" + name;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tbresult  = response;
                        tbsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tbsuccess = false;
                tbresult =  "Error: " + error.getMessage();
                profileid = "";
                doprofiles(false);
            }
        });

        queue.add(stringRequest);

    }

    public void doneprofile(){
        profileid = tbresult;
        if (md.parseint(profileid) > 0){
            loadprofileanswers(profileid);

        }
    }


    public void loadprofileanswers(String prf){

        if (cusdialog != null){cusdialog.cancel();}

        sublin.removeAllViews();
        MaterialTextView tv = md.maketextview(ExerciseExecute.this, "Loading Exercise...",
                "", 0, "Info", DS, fontsmall, width,  (int)(height * 0.08), true); sublin.addView(tv);
        ProgressBar pb = new ProgressBar(ExerciseExecute.this); pb.setId(pbarid);pb.setVisibility(View.VISIBLE);
        sublin.addView(pb);

        getprofileanswers(prf, new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showprofileanswers();
            }
        }, 0, this);
    }

    String gpresult = ""; boolean gpsuccess = false;
    public void getprofileanswers(String prof, final MainActivity.VolleyCallBack callBack,  int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "getprofileanswers?code="+
                md.auth(ExerciseExecute.this, mydatabase, android_id, uid)+"~"+exid+"~"+prof;

        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        gpresult  = response;
                        gpsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){getprofileanswers(prof,  callBack,  attempt, cnt);
                }else{
                    doprofiles(true);
                    gpsuccess = false;
                    gpresult =  "";
                    md.toast(ExerciseExecute.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);

    }


    public void showprofileanswers(){


        if (gpsuccess ){

            cusdialog.cancel();
            profileid = tbresult;
            answerstring = gpresult;

            int startindex = 0;
            ArrayList<String> anlist = md.makearrayfromstring(answerstring, "¬");
            ArrayList<String> ancols = md.makearrayfromstring(md.getstring(anlist, 1), "~");

            ArrayList<String> anrow = md.makearrayfromstring(md.getstring(anlist, 2), "~");
            ansid =  md.before(md.getstring(anlist , 2), "~");
            for (int c = 0; c < questioncount; c++){
                String qa  = md.coloption(ancols, "Q" + String.valueOf(c)+"Answer", anrow).Value;
                if (qa.equals("")){
                    startindex = c;
                    break;
                }
            }
            nextquestion(startindex);

        }
        else  { md.toast(this, " Error, please try again");  }
    }











    public void enablebuttons(int index){

        String nogms = "Please Wait While Your Answer Saves";
        ImageView info = (ImageView) findViewById(infoid);
        ImageView prev = (ImageView) findViewById(previd); prev.setEnabled(true);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { if (canchangeqeustion){nextquestion(index -1);}
            else{md.toast(ExerciseExecute.this, nogms);}     }  });

        ImageView next = (ImageView) findViewById(nextid); next.setEnabled(true);
        next.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {  if (canchangeqeustion){
                if ((index +1) >= questioncount){finishedexercise();}
                else{nextquestion(index +1);}
            }
            else{md.toast(ExerciseExecute.this, nogms);}  }  });
        if (index <= 0){ prev.setEnabled(false);  }
        else if (index >= questioncount) {
            next.setEnabled(false);
        }


        info.setEnabled(false);
        if (!useranswerletter.equals("")){
            info.setEnabled(true);
            info.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) { info(index); }  });
        }
    }


    public  ArrayList<String> getqrow(){

        ArrayList<String> row = new ArrayList<>();
        ArrayList<String> qlist  =  md.makearrayfromstring(exstring, "¬");
        ArrayList<String> qcols = md.makearrayfromstring(md.getstring(qlist, 1), "~");

        for (int c = 2; c < qlist.size(); c++) {

            if (md.before(md.getstring(qlist, c), "~").equals(exid)){
                row = md.makearrayfromstring(md.getstring(qlist, c), "~");break;
            }
        }
        return row;
    }

    public void nextquestion(int index){
        qsecs = 0;
        timerthread.stopRunning();
        sublin.removeAllViews();
        currentindex = index;
        ArrayList<String> qlist  =  md.makearrayfromstring(exstring, "¬");
        ArrayList<String> qcols = md.makearrayfromstring(md.getstring(qlist, 1), "~");

        ArrayList<String> anlist  =  md.makearrayfromstring(answerstring, "¬");
        ArrayList<String> ancols = md.makearrayfromstring(md.getstring(anlist, 1), "~");


        anslist = new String[]{"", "", "", "", ""};
        currentquestiontext = "";
        currentquestionparagraph2 = "";
        currentanswerletter = "";
        currentanswertext = "";
        useranswerletter= "";
        currentquestionpic = "";
        String topic = "", topicval = "", mp = "",a = "",b = "",c = "",d = "";
        if (index < questioncount & index >= 0){

            ArrayList<String> qrow = getqrow();
            currentquestiontext =  md.coloption(qcols, "Q"+String.valueOf(index+1)+"Paragraph1", qrow).Value;
            currentquestionpic =  md.coloption(qcols, "Q"+String.valueOf(index+1)+"Pic", qrow).Value;
            currentquestionparagraph2 =  md.coloption(qcols, "Q"+String.valueOf(index+1)+"Paragraph2", qrow).Value;
            currentanswerletter = md.coloption(qcols, "Q"+String.valueOf(index+1)+"Answer", qrow).Value;

            ArrayList<String> anrow = md.makearrayfromstring(md.getstring(anlist, 2), "~");
            useranswerletter = md.coloption(ancols, "Q"+String.valueOf(index+1)+"Answer", anrow).Value;

            enablebuttons(index);

            for (int i = 0; i < letters.length; i++){
                anslist[i] = md.coloption(qcols, "Q"+String.valueOf(index+1)+letters[i].toUpperCase(), qrow).Value;

                if ( currentanswerletter.toLowerCase().equals(letters[i].toLowerCase())){ currentanswertext = anslist[i];}
            }
            currentexplanation = md.coloption(qcols, "Q"+String.valueOf(index+1)+"Explanation", qrow).Value;
        }

        int rowheight =  (int)(height * 0.07);
        LinearLayout.LayoutParams pms  = new LinearLayout.LayoutParams(width, rowheight);

        MaterialTextView mtv = (MaterialTextView) findViewById(qlabelid);
        if (mtv != null){mtv.setText("Question: " + String.valueOf((index+1)) + "/" + String.valueOf(questioncount));}

        int mc2 =  md.countlines(currentquestiontext,fontmedium, width);
        if (mc2 == 1){mc2++;}
        MaterialTextView tv = md.maketextview(ExerciseExecute.this,
                currentquestiontext,  "", 0, "Info", DS, fontmedium,
                width, mc2*((int)(fontmedium*2.4)), true);sublin.addView(tv);

        View v = md.line(this, width, fontmedium, md.transparent); sublin.addView(v);

        tv.setLines(mc2);tv.setTypeface(null, Typeface.BOLD);
        tv.setBackgroundResource(R.drawable.bluebutton);tv.setTextColor(md.white);

        if (!currentquestionparagraph2.equals("")){
            mc2 =  md.countlines(currentquestiontext,fontmedium, width);
            tv = md.maketextview(ExerciseExecute.this,currentquestionparagraph2,  "", 0, "Info",
                    DS, fontmedium, width,mc2*((int)(fontmedium*2.4)), true);       sublin.addView(tv);
            tv.setBackgroundResource(R.drawable.bluebutton);
            tv.setLines(mc2);tv.setTypeface(null, Typeface.BOLD); tv.setTextColor(md.white);
        }

        if (!currentquestionpic.equals("")){

            ArrayList<MyModel.option> recs = new ArrayList<>();
            Cursor rs =  mydatabase.rawQuery( "select DATA from IMAGES where TABLENAME = 'Exercises' and NAME = '"+currentquestionpic+"'", null );
            while ( rs.moveToNext()) {
                int aid = rs.getColumnIndex("DATA");

                if (aid >= 0){
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(rs.getBlob(aid));
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                    int ww = (int) (width * 0.8);
                    if (smallscreen){ww = (int) (width * 0.7);}
                    int padd = (int)((width - ww) * 0.5);
                    LinearLayout ll = new LinearLayout(this); ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setLayoutParams( new LinearLayout.LayoutParams(width, ww)); sublin.addView(ll);

                    v = md.line(ExerciseExecute.this, padd, width, md.transparent); ll.addView(v);
                    ImageView  imgView = new ImageView(this);
                    imgView.setLayoutParams(new ViewGroup.LayoutParams(ww, (int) (ww* 0.9)));
                    imgView.setImageBitmap(bmp);
                    ll.addView(imgView);
                    break;
                }
            }
        }

        int useranswerindex = -1;
        for (int i = 0;i < letters.length; i++){
            if (letters[i].toLowerCase().equals(useranswerletter.toLowerCase())){
                useranswerindex = i; break;
            }
        }



        for (int i = 0; i < radioidlist.length; i++){
            int linid = md.parseint(linpre + String.valueOf(radioidlist[i]));
            int rbid = md.parseint(radiopre + String.valueOf(radioidlist[i]));
            int tbid = md.parseint(textboxpre + String.valueOf(radioidlist[i]));
            int imid = md.parseint(imagepre + String.valueOf(radioidlist[i]));
            if (!anslist[i].equals("")){
                LinearLayout reslin = new LinearLayout(this); reslin.setId(linid);
                reslin.setOrientation(LinearLayout.HORIZONTAL); reslin.setBackgroundResource(R.drawable.bronzebutton);
                sublin.addView(reslin); reslin.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight));
                v = md.line(ExerciseExecute.this, width, 4, md.transparent); sublin.addView(v);

                int checkwidth = (int)(width * 0), midwidth = width - (checkwidth + rowheight);
                ImageView bt = new ImageView(this); bt.setId(rbid);
                bt.setLayoutParams(new LinearLayout.LayoutParams(checkwidth, checkwidth));reslin.addView(bt);
                bt.setColorFilter(md.appblue);  bt.setBackgroundResource(R.drawable.ic_baseline_check_box_outline_blank_24);

                MaterialTextView mt =  md.maketextview(this,   anslist[i], "", tbid,
                        "Info", DS, fontmedium, midwidth, rowheight, true); reslin.addView(mt);
                mt.setTypeface(null, Typeface.BOLD);

                int imgwidth = rowheight - 10;

                LinearLayout imlin = new LinearLayout(this); imlin.setOrientation(LinearLayout.VERTICAL);
                imlin.setLayoutParams(new LinearLayout.LayoutParams(rowheight, rowheight)); reslin.addView(imlin);

                v = md.line(ExerciseExecute.this, rowheight, 4, md.transparent); imlin.addView(v);

                ImageView imgView = new ImageView(this); imgView.setId(imid);
                imgView.setLayoutParams(new ViewGroup.LayoutParams(imgwidth, imgwidth));
                imlin.addView(imgView ); imgView.setVisibility(View.INVISIBLE);

                if (useranswerindex > -1){
                    bt.setEnabled(false);
                    mt.setEnabled(false);
                    imgView.setEnabled(false);
                }else{
                    bt.setOnClickListener(new View.OnClickListener() {  @Override
                    public void onClick(View view) {  uncheck(rbid, index);  }  });

                    mt.setOnClickListener(new View.OnClickListener() {   @Override
                    public void onClick(View view) {  uncheck(rbid, index);  } });
                }
            }
        }


        MaterialButton mb = md.makebutton(this, "", "", corbtnid,
                "Info", DS, fontsmall, width,(int) (height * 0.08)); mb.setVisibility(View.GONE);
        sublin.addView(mb);


        if (useranswerindex > -1){ displayresponse(useranswerindex, index,   false, useranswerletter);}
        else{
            timerthread = new PrimeThread();
            timerthread.start();
        }
    }


    public void uncheck(int cid, int questionindex){
        int index = -1;
        for (int i = 0; i < radioidlist.length; i++){

            int rbid = md.parseint(radiopre + String.valueOf(radioidlist[i]));
            int tbid = md.parseint(textboxpre + String.valueOf(radioidlist[i]));
            ImageView rb = (ImageView) findViewById(rbid);
            MaterialTextView mt = (MaterialTextView) findViewById(tbid);
            if (rb != null){
                if (rbid == cid){
                    rb.setBackgroundResource(R.drawable.ic_baseline_check_box_24); index = i;
                }
                // if (rbid == cid){ rb.setTextColor(md.appblue);}
                rb.setEnabled(false);
                mt.setEnabled(false);
                mt.setTextColor(md.appblue);
            }
        }
        if ( index > -1){
            displayresponse(index, questionindex,  true, letters[index]);
        }
    }










    public void skipquestion(){

        cusdialog = new Dialog(ExerciseExecute.this);
        cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cusdialog.setContentView(R.layout.custom);
        cusdialog.setCancelable(true);
        cusdialog.setCanceledOnTouchOutside(true);
        Window window = cusdialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
        cuslin.setOrientation(LinearLayout.VERTICAL);
        cuslin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        int  cushit = (int)(height*0.55),  cuswid = (int)(width*0.95);
        int cuslineht = (int)(cushit * 0.12);

        cusdialog.show();
        cuslin.setPadding(6, 0, 0, 0);


        LinearLayout rl = new LinearLayout(ExerciseExecute.this); cuslin.addView(rl);
        rl.setOrientation(LinearLayout.HORIZONTAL);

        MaterialTextView mt = md.maketextview(ExerciseExecute.this, "Skip To Question", "", 0, "Info",
                DS, fontsmall, cuswid, cuslineht,  true);
        cuslin.addView(mt); mt.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(width * 0.9), (int)(height * 0.75));
        NestedScrollView scrl = new NestedScrollView(ExerciseExecute.this);
        scrl.setLayoutParams(lp) ; cuslin.addView(scrl);

        LinearLayout lne = new LinearLayout(ExerciseExecute.this); lne.setLayoutParams(lp);
        lne.setOrientation(LinearLayout.VERTICAL); scrl.addView(lne);
        scrl.setLayoutParams(new LinearLayout.LayoutParams((int)(width * 0.9), (int)(height * 0.7)));
        lne.setLayoutParams( new FrameLayout.LayoutParams((int)(width * 0.9), (int)(height * 0.75)));

        ArrayList<String> anlist  =  md.makearrayfromstring(answerstring, "¬");
        ArrayList<String> ancols = md.makearrayfromstring(md.getstring(anlist, 1), "~");
        ArrayList<String> anrow = md.makearrayfromstring(md.getstring(anlist, 2), "~");

        for (int i = 0; i < questioncount; i++){
            int index = i;
            int color = textcolor;

            String ans  = md.coloption(ancols, "Q" + String.valueOf((i+1))+"Answer", anrow).Value;
            String cor  = md.coloption(ancols, "Q" + String.valueOf((i+1))+"Correct", anrow).Value;
            if (cor.toLowerCase().equals("true")){cor = "1";}else{cor ="0";}

            MaterialButton mb = md.makebutton(ExerciseExecute.this, "", "", 0, "Info",
                    DS, fontsmall, (int)(cuswid*0.96), cuslineht);  lne.addView(mb);

            String txt = String.valueOf((i+1));
            if (!ans.equals("") & cor.equals("1")){
                txt = String.valueOf((i+1)) + " (Correct)";
                color = md.green; mb.setTypeface(null, Typeface.BOLD); }
            else if (!ans.equals("") & !cor.equals("1")){
                txt = String.valueOf((i+1)) + " (Wrong)";
                color = md.brightred; mb.setTypeface(null, Typeface.BOLD); } mb.setText(txt);
            mb.setTextColor(color);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cusdialog.cancel();
                    nextquestion(index );
                }
            });
        }

    }

    public void info(int index){

        ArrayList<String> anlist  =  md.makearrayfromstring(answerstring, "¬");
        ArrayList<String> ancols = md.makearrayfromstring(md.getstring(anlist, 1), "~");
        ArrayList<String> anrow = md.makearrayfromstring(md.getstring(anlist, 2), "~");
        String ans  = md.coloption(ancols, "Q" + String.valueOf((index+1))+"Answer", anrow).Value;

        boolean gohead = ! ans.equals("");

        if (gohead){
            cusdialog = new Dialog(ExerciseExecute.this);
            cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cusdialog.setContentView(R.layout.custom);
            cusdialog.setCancelable(true);
            cusdialog.setCanceledOnTouchOutside(true);
            Window window = cusdialog.getWindow();
            window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

            cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
            cuslin.setOrientation(LinearLayout.VERTICAL);
            cuslin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            int  cushit = (int)(height*0.55),  cuswid = (int)(width*0.9);

            cusdialog.show();
            cuslin.setPadding(6, 0, 0, 0);
            int cuslineht = (int)(cushit * 0.12);

            MaterialTextView mt =  md.maketextview(ExerciseExecute.this,  "The Correct Answer Is: " + currentanswertext, "", 0, "Info",
                    DS, fontmedium, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);
            mt.setTypeface(null, Typeface.BOLD);

            if (!currentexplanation.equals("")){

                int mc2 =  md.countlines( currentexplanation,fontmedium, width);
                mt =  md.maketextview(ExerciseExecute.this,  currentexplanation, "", 0, "Info",
                        DS, fontmedium, cuswid, mc2*((int)(fontmedium*2.4)), true); cuslin.addView(mt);
                mt.setLines(mc2);
            }

            MaterialButton mb = md.makebutton (ExerciseExecute.this,"Close",
                    "", 0, "Cancel", DS, fontsmall, (int)(cuswid*0.97), cuslineht); cuslin.addView(mb);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { cusdialog.cancel();
                }
            });
        }


    }










    public void finishedexercise(){
        timerthread.stopRunning();
        sublin.removeAllViews();
        ImageView info = (ImageView) findViewById(infoid);
        info.setEnabled(false);

        ArrayList<String> anlist  =  md.makearrayfromstring(answerstring, "¬");
        ArrayList<String> ancols = md.makearrayfromstring(md.getstring(anlist, 1), "~");
        ArrayList<String> anrow = md.makearrayfromstring(md.getstring(anlist, 2), "~");

        int corr = 0;
        for (int i = 1; i <= questioncount; i++){
            String cor = md.coloption(ancols, "Q" + String.valueOf((i))+"Correct", anrow).Value;
            if (cor.toLowerCase().equals("true")){cor = "1";}else{cor = "0";}
            if (cor.equals("1")){ corr++;  }  }

        MaterialButton bt = new MaterialButton(this);bt.setTypeface(null, Typeface.BOLD);
        bt.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.09)));
        bt.setTextSize(md.fontmedium ); bt.setTextColor(md.green); sublin.addView(bt);
        bt.setBackgroundColor(md.transparent);
        String cons = "Exercise Complete!!!   ";
        if (corr == 0){cons = "";}
        bt.setText(cons+String.valueOf(corr)  + " Out Of " + String.valueOf(questioncount));

        int col = md.lightblue, tx = md.white;
        int pct  = (int)(100*((float)(corr) / questioncount));
        if (pct <= 90 & pct > 80){  col = md.green; }
        else  if (pct <= 80 & pct > 70){  col = md.yellow; tx = md.black;}
        else  if (pct <= 70 & pct > 60){  col = md.orange;  }
        else  if (pct <= 60){  col = md.brightred; }

        MaterialButton mb = md.makebutton(ExerciseExecute.this, String.valueOf(pct )+ "%", "",
                0, "Info", DS, md.fontmedium,   width, (int)(height * 0.1)); sublin.addView(mb);
        mb.setBackgroundColor(col); mb.setTextColor(tx); mb.setTypeface(null, Typeface.BOLD);

        if (pct >= 50){
            ImageView imgView = new ImageView(this);
            imgView .setLayoutParams(new LinearLayout.LayoutParams(width, width)); sublin.addView(imgView );
            Glide.with(this).load(congindex).into(imgView);

            MediaPlayer mp = MediaPlayer.create(this, R.raw.completegood);
            if (pct < 80){ mp = MediaPlayer.create(this, R.raw.completebad);}
            mp.start();
        }else{
            ImageView imgView = new ImageView(this);
            imgView .setLayoutParams(new LinearLayout.LayoutParams(width, width)); sublin.addView(imgView );
            Glide.with(this).load(congindex).into(imgView);

            MediaPlayer mp = MediaPlayer.create(this, R.raw.wrongaudio);
            mp.start();
        }

        int rowheight = (int)(height * 0.08), dw = (int)(width * 0.31);
        LinearLayout ll = new LinearLayout(this); ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(width, (rowheight))); sublin.addView(ll);
        mb = md.makebutton(ExerciseExecute.this, "Exit Exercise", "",
                0, "Cancel", DS, fontsmall, dw, rowheight); ll.addView(mb);
        mb.setTypeface(null, Typeface.BOLD);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerthread.stopRunning(); finish(); } });

        mb  = md.makebutton(ExerciseExecute.this, "Share", "",
                0, "Warning", DS, fontsmall,dw, rowheight); ll.addView(mb);
        mb.setTypeface(null, Typeface.BOLD);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerthread.stopRunning();
                md.share(ExerciseExecute.this, DS);
            } });

        mb  = md.makebutton(ExerciseExecute.this, "New Exercise", "",
                0, "Go", DS, fontsmall, dw, rowheight); ll.addView(mb);
        mb.setTypeface(null, Typeface.BOLD);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerthread.stopRunning();

                Bundle b = new Bundle();
                b.putStringArray("key", new String[]{ "Exercises",  DS, uid, executor, "Exercises", brand, category,  topic,  ""});
                Intent intent = new Intent(getApplicationContext(), Activities.class);
                intent.putExtras(b);
                finish();
                startActivity(intent);
            } });




    }

    public void displayresponse( int checkboxindex, int questionindex, boolean goserver, String lett){

        timerthread.stopRunning();
        MaterialTextView mt = (MaterialTextView) findViewById(timetv);
        if (mt != null){
            mt.setText("");
        }
        int rbid = md.parseint(radiopre + String.valueOf(radioidlist[checkboxindex]));
        int imid = md.parseint(imagepre + String.valueOf(radioidlist[checkboxindex]));
        int tbid = md.parseint(textboxpre + String.valueOf(radioidlist[checkboxindex]));
        int lnid = md.parseint(linpre + String.valueOf(radioidlist[checkboxindex]));

        boolean iscorrect =  letters[checkboxindex].toLowerCase().equals(currentanswerletter.toLowerCase());
        String corstring = "0"; if (iscorrect){corstring = "1";}

        LinearLayout lin = (LinearLayout) findViewById(lnid);
        String cstring = "Correct"; int tx = md.green;
        if (!iscorrect){cstring = "Wrong!"
                + System.lineSeparator() + "The Correct Answer Is: " + currentanswertext; tx  = md.brightred;

            mt = (MaterialTextView) findViewById(tbid);
            ImageView rb = (ImageView) findViewById(rbid);
            if (rb != null & mt != null & lin != null){
                rb.setBackgroundResource(R.drawable.ic_baseline_clear_24);
                mt.setTextColor(md.white);
                lin.setBackgroundResource(R.drawable.redbutton);
            }

        }else{
            ImageView rb = (ImageView) findViewById(rbid);
            if (rb != null){
                rb.setBackgroundResource(R.drawable.ic_baseline_check_box_24);
                lin.setBackgroundResource(R.drawable.greenbutton);
            }
        }

        MaterialButton bt = (MaterialButton) findViewById(corbtnid);
        if (bt != null){
            bt.setTextSize(md.fontmedium );
            bt.setTextColor(tx);
            bt.setVisibility(View.VISIBLE);
            bt.setBackgroundColor(md.transparent); bt.setText(anslist[checkboxindex]+  " ("+cstring + ")");

            ImageView imgView =  (ImageView) findViewById(imid );
            if (imgView != null){
                imgView.setVisibility(View.VISIBLE);
                if (iscorrect){ Glide.with(this).load(corindex).into(imgView);
                }else{ Glide.with(this).load(wrondex).into(imgView); }
            }
        }



        if (goserver){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.wrongaudio);
            if (corstring.equals("1")){ mp = MediaPlayer.create(this, R.raw.correctaudio);}
            ArrayList<String> anlist  =  md.makearrayfromstring(answerstring, "¬");
            ArrayList<String> ancols = md.makearrayfromstring(md.getstring(anlist, 1), "~");
            ArrayList<String> anrow = md.makearrayfromstring(md.getstring(anlist, 2), "~");
            canchangeqeustion = false;
            int answered = 0, rectly = 0;
            float mks = 0;
            for (int i = 1; i <=questioncount; i++){
                String qa  = md.coloption(ancols, "Q" + String.valueOf(i)+"Answer", anrow).Value;
                String cr  = md.coloption(ancols, "Q" + String.valueOf(i)+"Correct", anrow).Value;
                if (!qa.equals("")){
                    answered++;
                    if (cr.toLowerCase().equals("true")){
                        rectly++;
                        mks ++;
                    }
                }
            }
            answered++;if (corstring.equals("1")){rectly++; mks += 1;}
            String cors = corstring, ansletter = letters[checkboxindex];

            mp.start();
            answerquestion(String.valueOf((questionindex+1)), ansletter, corstring, String.valueOf(answered), String.valueOf(rectly),  new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() { answered(questionindex, ansletter, cors); }
            }, 0, ExerciseExecute.this);
        }
    }



    String nstring = ""; boolean nsuccess = false;
    public void answerquestion(String qnumber, String ansletter, String correct, String answered, String correctly,
                               final MainActivity.VolleyCallBack callBack,  int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);



        String url = md.url + "answerquestion?code="+md.auth(ExerciseExecute.this, mydatabase, android_id, uid)
                +"~"+ansid+"~"+ qnumber+"~"+ansletter+"~"+correct
                +"~"+answered+"~"+correctly+"~"+String.valueOf(qsecs)+"~"+profileid;

        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nstring  = response;
                        nsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){answerquestion(qnumber, ansletter, correct, answered, correctly,
                        callBack,  attempt, cnt);
                }else{
                    nsuccess = false;
                    nstring =  "";
                    md.toast(ExerciseExecute.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);

    }

    public void answered(int questionindex, String ansletter, String correct){

        if (nsuccess  & sublin != null){

            answerstring = nstring;

            canchangeqeustion = true;
        }
        else  {
            md.toast(ExerciseExecute.this, " Error, please try again");
        }
    }





    class PrimeThread extends Thread {
        int i=seconds;
        boolean running = false, isred = false;
        public void run() {
            running = true;
            while(running){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String ii =  String.valueOf((i % 60));
                        if (ii.length() == 1){ii = "0" +ii;}
                        String m = String.valueOf(((i % 3600) / 60));

                        MaterialTextView mt = (MaterialTextView) findViewById(timetv);
                        mt.setTextColor(md.brightred); mt.setTypeface(null, Typeface.BOLD);
                        mt.setTextColor(md.brightred); mt.setBackgroundColor(md.transparent);
                        if (i >= 0){
                            mt.setText(m+":"+ii);
                        }else{
                            if (isred){
                                mt.setTextColor(md.brightred); mt.setBackgroundColor(md.transparent);
                                isred = false;
                            }else if (!isred){
                                mt.setTextColor(md.white); mt.setBackgroundColor(md.brightred);
                                isred = true;
                            }
                        }
                    }
                });
                qsecs++;
                i--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void stopRunning(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MaterialTextView mt = (MaterialTextView) findViewById(timetv);
                    mt.setText("");
                }
            });
            running = false;
        }
    }



















    public void downloadpopup(){


        cusdialog = new Dialog(ExerciseExecute.this);
        cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cusdialog.setContentView(R.layout.custom);
        cusdialog.setCancelable(false);
        cusdialog.setCanceledOnTouchOutside(false);
        Window window = cusdialog.getWindow();
        int ww = (int)(width*0.95);
        window.setLayout(ww, AbsListView.LayoutParams.WRAP_CONTENT);

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
        cuslin.setOrientation(LinearLayout.VERTICAL);
        cuslin.setLayoutParams(new FrameLayout.LayoutParams(ww,
                ViewGroup.LayoutParams.MATCH_PARENT));
        int cushit = (int)(height*0.55),  cuswid = (int)(width*0.85);

        cusdialog.show();
        cusdialog.setTitle("Starting Exercise");
        cuslin.setPadding(6, 0, 0, 0);
        int cuslineht = (int)(cushit * 0.12);

        String updm = "Downloading Exercise Pictures... Please Wait";
        int mc2 =  md.countlines(updm,md.fontmedium, cuswid);
        MaterialTextView nb = md.maketextview(ExerciseExecute.this, updm, "", downloadstatusid, "Info", DS,
                md.fontmedium, cuswid, mc2*((int)(md.fontmedium*2.4)), true);
        nb.setLines(mc2);cuslin.addView(nb);

        View v = md.line(ExerciseExecute.this, width, 6, md.transparent); cuslin.addView(v);

        ProgressBar pb = new ProgressBar(ExerciseExecute.this); pb.setVisibility(View.VISIBLE);
        pb.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht)); cuslin.addView(pb);


        new Thread() {
            public void run() {
                try {
                    ExerciseExecute.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            preload();
                            cusdialog.cancel();
                        }
                    });
                } catch (Exception e) {
                    md.toast(ExerciseExecute.this, e.getMessage());
                    cusdialog.cancel();
                    e.printStackTrace();
                }
                cusdialog.cancel();
            }
        }.start();
    }




    public void exsettings(){
        cusdialog = new Dialog(this);
        cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cusdialog.setContentView(R.layout.custom);
        cusdialog.setCancelable(true);
        cusdialog.setCanceledOnTouchOutside(true);
        Window window = cusdialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
        cuslin.setOrientation(LinearLayout.VERTICAL);
        cuslin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        int cushit = (int)(height*0.55);
        int cuswid = (int)(width*0.95);
        int cuslineht = (int)(cushit * 0.12);

        cusdialog.show();
        cuslin.setPadding(6, 0, 0, 0);

        MaterialTextView mt =  md.maketextview(this,  "Settings", "", 0, "Info",
                DS, md.fontmedium, (int)(cuswid), cuslineht, true); cuslin.addView(mt);

        View v = md.line(ExerciseExecute.this, width, 2, textcolor); cuslin.addView(v);
        LinearLayout ln = new LinearLayout(this); cuslin.addView(ln); ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht));


        mt =  md.maketextview(this,  "Question Timer", "", 0, "Info",
                DS, md.fontmedium, (int)(cuswid*0.48), cuslineht, true); ln.addView(mt);


        ArrayList<String> times = new ArrayList<>(); times.add("Select Timer");
        times.add("30 Seconds"); times.add("1 Minute"); times.add("90 Seconds"); times.add("2 Minutes");
        times.add("3 Minutes"); times.add("5 Minutes");

        Spinner spinner = new Spinner(ExerciseExecute.this);
        spinner.getBackground().setColorFilter(md.black, PorterDuff.Mode.SRC_ATOP);
        spinner.setLayoutParams(new LinearLayout.LayoutParams((int)(cuswid*0.48), cuslineht ));
        ln.addView(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            //((TextView) adapterView.getChildAt(0)).setTextColor(md.white);
            if (i > 0){

                String secs = "30";
                if (i == 2){secs = "60";} else if (i == 3){secs = "90";} else if (i == 4){secs = "120";}
                else if (i == 5){secs = "180";} else if (i == 6){secs = "300";}

                ArrayList<MyModel.option> recs = new ArrayList<>();
                Cursor rs =  mydatabase.rawQuery( "select * from EXERCISEQUESTIONTIMER where USER = '"+uid +"'", null );
                while ( rs.moveToNext()) {
                    int aid = rs.getColumnIndex("SECONDS");
                    recs.add(new MyModel.option("",
                            rs.getString(aid), "", ""));
                }
                if (recs.size() > 0){
                    mydatabase.execSQL("UPDATE EXERCISEQUESTIONTIMER SET SECONDS = '"+secs+"' where USER = '"+uid +"'");
                }else{
                    mydatabase.execSQL("INSERT INTO EXERCISEQUESTIONTIMER VALUES('"+uid+"',  '"+secs +"')");
                }



            }
        }
            @Override    public void onNothingSelected(AdapterView<?> adapterView) { }  });
        ArrayAdapter<String> dataAdapter =  new ArrayAdapter<String>(ExerciseExecute.this,
                android.R.layout.simple_spinner_item, times);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  spinner.setAdapter(dataAdapter);





    }





}
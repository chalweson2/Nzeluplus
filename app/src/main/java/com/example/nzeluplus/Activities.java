package com.example.nzeluplus;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.internal.StringResourceValueReader;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LifecycleOwner;


import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import java.nio.ByteBuffer;

import static android.graphics.ImageFormat.YUV_420_888;
import static android.graphics.ImageFormat.YUV_422_888;

public class Activities extends AppCompatActivity {


    LinearLayout lin; RelativeLayout menulin;
    final Integer pbarid = 243, ftv = 6390, scrlid = 839152, nscid = 5463, nestlin =  37542;
    boolean dataSuccess = false;
    MyModel md = new MyModel();
    int delay = 5000, height = MainActivity.height, width = MainActivity.width, level = 1;
    String mode = "", thetable = "",  Executor = "";
    String android_id = MainActivity.android_id, ds = MainActivity.DisplayString, uid = MainActivity.uref;
    ProgressBar pbbar;   String[] receivearr; int textcolor = md.darkgray;

    SQLiteDatabase mydatabase;

    int fontsmall= md.fontsmall, fontmedium = md.fontmedium; boolean smallscreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activities);

        DisplayMetrics metrics = new DisplayMetrics();
        (Activities.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;
        width = metrics.widthPixels;

        height = (int) (height*0.97);
        if (height <=md.heightcut){height = (int)(height * 0.91); smallscreen = true; fontsmall -= 1; fontmedium -= 2;}

        lin = (LinearLayout) findViewById(R.id.mainlinear);

        lin.setPadding(10, 0, 10, 0);
        lin.setBackgroundResource(R.drawable.backgroundxml);
        width =  width - 20;
        lin = md. setbackground(lin);


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
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        if (mode.equals( "ItemLists") & level == 2  & keyCode == KeyEvent.KEYCODE_BACK){
            //keyCode == KeyEvent.KEYCODE_MENU ||           keyCode == KeyEvent.KEYCODE_TAB ||
            itemslist();
            // return 'true' to prevent further propagation of the key event
            return true;
        }else  if (mode.equals( "Favourites") & level == 2  & keyCode == KeyEvent.KEYCODE_BACK){
            //keyCode == KeyEvent.KEYCODE_MENU ||           keyCode == KeyEvent.KEYCODE_TAB ||
            favourites();
            // return 'true' to prevent further propagation of the key event
            return true;
        }

        // let the system handle all other key events
        return super.onKeyDown(keyCode, event);
    }





    public RelativeLayout rellayout(boolean add, int id, int wid, int ht){
        RelativeLayout rel = new RelativeLayout(this);
        rel.setLayoutParams(new LinearLayout.LayoutParams(wid, ht));
        if (add){lin.addView(rel);}
        return rel;
    }

    public void startoff(){

        if (mode.equals("notificationsets")){notificationsets();}
        else if (mode.equals("ProductDetails")){productdetails();}
        else if (mode.equals("ItemLists")){itemslist();}
        else if (mode.equals("Address")){myaddresses();}
        else if (mode.equals("Chat")){chat();}
        else if (mode.equals("Favourites")){favourites();}
        else if (mode.equals("PointOfSaleSignIn")){pointofsalesignin();}
        else if (mode.equals("OnOffline")){onoffline();}
        else if (mode.equals("History")){
            String rptb =  md.getarray(receivearr, 3);
            myhistory(true, rptb);}
        else if (mode.equals("DynamicAdd")){
            dynamictable =  md.getarray(receivearr, 3);
            dnrowid =  md.getarray(receivearr, 4);
            dynamiccolval =   md.getarray(receivearr, 5);
            dynamicadd();}

    }

































    public void notificationsets(){

        lin.removeAllViews();
        pbbar = new ProgressBar(this);
        lin.addView(pbbar);
        //pbbar.setVisibility(View.GONE);


        LinearLayout linlay = new LinearLayout(Activities.this); lin.addView(linlay);

        md.textview(Activities.this, linlay, "Loading Notification Settings... Please Wait", md.white, md.appblue,
                md.fontmedium, 0, width,  0, 1, Gravity.CENTER, Typeface.BOLD);


        setTitle(md.returndisplay(thetable));
        mode = "notificationsets";

        getnotificationsets(  thetable,  new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() { shownotificationsets();  }
        }, 0);
    }


    boolean notiSuccess = false; String notistring = ""; ArrayList<MyModel.option> noticols = new ArrayList<>();

    public void getnotificationsets(String table, final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://www.alcbanking.com/App/getnotificationsets?code="+android_id+"~"+uid+"~"+table;

            final int attempt = attempts + 1;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    notistring = response; notiSuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ getnotificationsets(   table, callBack,  attempt); }
                    else{ md.toast(Activities.this,  "Failed To Reach Server... Retrying"); }
                }
            });
            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }

    public void shownotificationsets(){
        if (notiSuccess & notistring.indexOf("¬") > -1){
            lin.removeAllViews();

            LinearLayout linlay = new LinearLayout(Activities.this); lin.addView(linlay);

            md.textview(Activities.this, linlay, "Notification Settings", md.white, md.appblue,
                    md.fontlarge, 0, width, 0, 1, Gravity.CENTER, Typeface.BOLD);

            linlay = new LinearLayout(Activities.this); lin.addView(linlay);
            md.textview(Activities.this, linlay, "Indicate Which Notifications You Want To Receive", md.white, md.appblue,
                    md.fontmedium, 0, width,  0, 4, Gravity.CENTER, Typeface.BOLD);

            NestedScrollView scrl = new NestedScrollView(this); scrl.setId(scrlid);
            scrl.setPadding(0, 20, 0, 5);
            scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.65))); ;
            lin.addView(scrl);

            pbbar = new ProgressBar(this);
            lin.addView(pbbar);
            pbbar.setId(pbarid);
            pbbar.setVisibility(View.GONE);

            LinearLayout ln = new LinearLayout(this);
            ln.setOrientation(LinearLayout.VERTICAL); scrl.addView(ln);

            ArrayList<String> list  =  md.makearrayfromstring(notistring, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");
            ArrayList<String> row = md.makearrayfromstring(md.getstring(list, 2), "~");

            ArrayList<String> nocols  = md.makearrayfromstring(md.getstring(rules, 1), "~");

            for (int i = 1; i < cols.size(); i++){
                String cl = md.before(md.getstring(cols, i), "^");
                String dt = md.breakurl(md.getstring(cols, i), 1, "^");
                boolean go = true;
                for (int n = 0; n < nocols.size(); n++){
                    String tb = md.before(md.getstring(nocols, n), "^"), ncl = md.breakurl(md.getstring(nocols, n), 1, "^");
                    if ((tb.equals("") || tb.equals(thetable) ) & ncl.equals(cl)){  go = false; }
                }
                if (cl.length() > 5){  if (cl.substring(0, 5).equals("Value")) {  go = false;} }
                if ( cl.length() > 6){  if (cl.substring(0, 6).equals("Minute")) {  go = false;} }

                if (go){
                    String vl = md.getstring(row, i);
                    md.br(Activities.this, new LinearLayout(Activities.this), 4);

                    LinearLayout lnr  = new LinearLayout(Activities.this);  ln.addView(lnr);
                    lnr = new LinearLayout(Activities.this);
                    ln.addView(lnr);  lnr.setPadding(0, 8, 0, 0);
                    final int IDD = md.parseint("2"+String.valueOf(i)  + "6655");

                    CheckBox cb =   md.checkbox(Activities.this,  md.returndisplay(cl), vl, md.brightred,
                            md.white,IDD, md.fontmedium,   width, 0); lnr.addView(cb);
                    cb.setBackgroundColor(md.white);

                    noticols.add(new MyModel.option(String.valueOf(IDD), cl, "", ""));
                }

                ln  = new LinearLayout(Activities.this);  lin.addView(ln);
                ln.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.2)));
                int sid = 26692;
                Button bt = new Button(this); bt.setId(sid); bt.setText("Save");  bt.setTextColor(md.white);
                bt.setWidth(width); ln.addView(bt); bt.setBackgroundResource(R.drawable.lightbluebutton);
                bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) { savenotisettings();  } });

            }


        }
        else  {
            md.toast(Activities.this, "Notification Settings Failed To Load, Please Try Again");
        }


    }

    String notisaveresult = "", notisavestring = "", notimss = "";

    public void savenotisettings(){
        notisaveresult = ""; notisavestring = "";
        for (int i = 0; i < noticols.size(); i++){
            String cl = md.getoption(noticols, i).Option1, vl = "0";
            final int IDD = md.parseint(md.getoption(noticols, i).ID);
            CheckBox cb = (CheckBox) findViewById(IDD); if (cb.isChecked()){ vl = "1";  }
            notisavestring += "|" + cl+ "¬" + vl;
        }

        try{ pbbar = (ProgressBar) findViewById(pbarid); pbbar.setVisibility(View.VISIBLE); }catch (Exception xx){}

        startnotisave(thetable, notisavestring,    new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {  finishnotisave();   }
        }, 0, Activities.this);

    }

    public void startnotisave(String table, String vals, final MainActivity.VolleyCallBack callBack,
                              int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = "https://www.alcbanking.com/App/savenotificationsets";

        url = url + "?code=" + android_id+"~"+uid+"~"+table+"~"+vals;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        notisaveresult = response;
                        dataSuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dataSuccess = false;
                notisaveresult = "";
                md.toast(Activities.this, "Failed To Reach Server... Retrying");

            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void finishnotisave(){

        try{   pbbar = (ProgressBar) findViewById(pbarid); pbbar.setVisibility(View.GONE);  }catch (Exception xx){}
        if (dataSuccess ){
            md.toast(Activities.this, " Notification Settings Saved!");
            Intent intent = new Intent();
            intent.putExtra("key", notisaveresult);
            setResult(RESULT_OK, intent);
            finish();
        }
        else  {
            md.toast(Activities.this, " Error, please try again");
        }
    }
















    String imresult = "", imcol = "", imval = "", imoffset = "0", imfetch = "10", imorder = "Name", imdesc = "0", immax = "";
    boolean imsuccess = false;
    int impagedownid = 4295035, impageup = 3649352, impagedisid = 942325234, imspinnerid = 66284312;

    public void navpages(int num){
        int max = md.parseint(immax), offset = md.parseint(imoffset), fetch = md.parseint(imfetch);
        if (num == 1){
            if (offset < max){
                if ((offset + fetch) >= max){ offset = max - fetch; }
                else if ((offset + fetch) < max){ offset = offset + fetch;}
                imoffset = String.valueOf(offset);
                itemslist();
            }
        }else if (num == -1){
            if (offset > 0){
                if ((offset - fetch) < 0){offset = 0;}
                else{offset = offset - fetch;}
                imoffset = String.valueOf(offset);
                itemslist();
            }
        }
    }

    public void setitemorder(String col){
        imorder = col; imoffset = "0"; itemslist();
    }

    public RelativeLayout imrellayout(int id, int wid, int ht){
        RelativeLayout rel = new RelativeLayout(this);
        rel.setLayoutParams(new LinearLayout.LayoutParams(wid, ht));
        return rel;
    }

    public void itemslist(){
        lin.removeAllViews();
        level = 1;
        imcol = md.getarray(receivearr, 4); imval = md.getarray(receivearr, 5);

        String imgo = "|"+imcol +"^"+  imval;
        if (imval.contains("~")){
            imgo = "";
            ArrayList<String> vals = md.makearrayfromstring(imval, "~");
            for (int i = 0; i < vals.size(); i++){
                imgo += "|"+imcol +"^Or^=^"+  md.getstring(vals, i);
            }
        }

        String itemname = md.appsetting("ItemNamePlural", ds);

        MaterialTextView tv = md.maketextview(Activities.this, "Loading " + itemname,
                "", 0, "Info", ds, md.fontmedium, width, (int)(height * 0.1), true);

        lin.addView(tv);
        pbbar = new ProgressBar(Activities.this); pbbar.setId(pbarid); lin.addView(pbbar);

        String url = md.auth(Activities.this, mydatabase, android_id, uid)+"~Items^" + imgo;

        url = url + "|" + imorder + "^Order^" + imdesc;

        url = url + "~" + imoffset + "^" + imfetch;


        getitemlist( url, new MainActivity.VolleyCallBack() {
            @Override  public void onSuccess() { showitemlist(); }
        }, 0, Activities.this);

    }

    public void getitemlist(String ur, final MainActivity.VolleyCallBack callBack,
                            int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url+"gettable?code=" + ur;

        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        imresult  = response;
                        imsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imsuccess = false;

                if (attempt < md.attempts){
                    getitemlist(ur,  callBack,  attempt, cnt);
                }else{
                    imresult =  "";
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }
    int impre  = 7519223;
    public void showitemlist(){

        if (!imsuccess){
            // md.toast(Activities.this, imresult);
        }
        else if (imsuccess ){

            int lineheight = (int) (height * 0.08);
            lin.removeAllViews();
            LinearLayout lnr = new LinearLayout(Activities.this); lnr.setOrientation(LinearLayout.HORIZONTAL);
            lnr.setLayoutParams(new LinearLayout.LayoutParams(width, lineheight)); lin.addView(lnr);


            ArrayList<String> ordercols = new ArrayList<>(), orderdisp = new ArrayList<>();
            ordercols.add("Name"); ordercols.add("SellingPrice");  orderdisp.add("Order By");
            for (int i = 0; i < ordercols.size(); i++){orderdisp.add(md.returndisplay(md.getstring(ordercols, i)));}
            Spinner spinner = new Spinner(Activities.this); spinner.setId(imspinnerid);
            spinner.getBackground().setColorFilter(md.black, PorterDuff.Mode.SRC_ATOP);
            spinner.setLayoutParams(new LinearLayout.LayoutParams((int)(width*0.45), lineheight ));
            lnr.addView(spinner);

            // Spinner click listener
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //((TextView) adapterView.getChildAt(0)).setTextColor(md.white);
                if (i > 0){setitemorder(md.getstring(ordercols, (i-1)));}  spinner.setSelection(0);
            }
                @Override    public void onNothingSelected(AdapterView<?> adapterView) { }  });

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter =  new ArrayAdapter<String>(Activities.this,
                    android.R.layout.simple_spinner_item,  orderdisp );
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);




            NestedScrollView ns  = new NestedScrollView(Activities.this);
            ns.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(height* 0.85)));
            lin.addView(ns);
            LinearLayout nsln = new LinearLayout(Activities.this); nsln.setOrientation(LinearLayout.VERTICAL);
            nsln.setId(linid);
            nsln.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ns.addView(nsln);


            ArrayList<String>  imlist = md.makearrayfromstring(imresult, "¬");
            String imrulestring = md.getstring(imlist, 0);
            ArrayList<String>  imrules = md.makearrayfromstring(imrulestring, "#");
            ArrayList<String>  imcols = md.makearrayfromstring(md.getstring(imlist,1), "~");

            String rowcount = md.getstring(imrules, 14);

            if (rowcount.contains("^")){
                immax = rowcount.substring(rowcount.indexOf("^")+1);

                int max = md.parseint(immax), ftint = md.parseint(imfetch), offint = md.parseint(imoffset);

                String pgs = "";
                if (ftint < max){
                    int smx = (offint + ftint); if (smx > max){smx = max;}
                    pgs = String.valueOf((offint + 1)) +" - "+String.valueOf(smx)+" Of " +  immax;

                    int smallwid = (int)(width * 0.12), midwid = (int) (width * 0.3);

                    MaterialButton mb = new MaterialButton(Activities.this);
                    mb = md.buttondesign(Activities.this, mb, "Info", ds);
                    mb.setBackgroundResource(R.drawable.ic_baseline_navigate_before_24);
                    mb.setLayoutParams(new LinearLayout.LayoutParams(smallwid, (int)(lineheight)));
                    lnr.addView(mb); mb.setTextSize(fontsmall);  mb.setId(impagedownid );
                    mb.setOnClickListener(new View.OnClickListener() {
                        @Override   public void onClick(View view) {
                            navpages(-1);
                        }   });

                    Button tv = new Button (Activities.this); tv.setText(pgs);
                    tv.setTextSize(md.fontmedium); tv.setBackgroundColor(md.transparent);
                    tv.setLayoutParams(new LinearLayout.LayoutParams(midwid, (int)(lineheight)));
                    lnr.addView(tv);  tv.setId(impagedisid  );
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setfetchnumber();
                        }
                    });




                    mb = new MaterialButton(Activities.this);
                    mb = md.buttondesign(Activities.this, mb, "Info", ds);
                    mb.setBackgroundResource(R.drawable.ic_baseline_navigate_next_24);
                    mb.setLayoutParams( new LinearLayout.LayoutParams(smallwid, (int)(lineheight)));
                    lnr.addView(mb); mb.setTextSize(fontsmall);  mb.setId(impageup );
                    mb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            navpages(1);
                        }
                    });
                }
            }



            for (int c = 2; c < imlist.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(imlist, c), "~");
                final String idd =  md.before(md.getstring(imlist, c), "~");
                final String name =  md.coloption(imcols, "Name", row).Value;
                final String inum =  md.coloption(imcols, "ItemNumber", row).Value;
                String prc =  md.coloption(imcols, "SellingPrice", row).Value;
                String vit =  md.coloption(imcols, "ValueItemType", row).Value;
                String sztxt =  md.coloption(imcols, "SizeText", row).Value;
                String pic1 =  md.coloption(imcols, "Pic1", row).Value;


                final String price = String.valueOf(md.round(md.parsefloat(prc), 2));
                int nid = impre + (c - 2), reprelid = 5432534 + (c-2);

                int blockheight = (int)(width * 0.8), withfaveheight = width;
                LinearLayout  repln = new LinearLayout(Activities.this); repln.setOrientation(LinearLayout.HORIZONTAL);
                repln.setLayoutParams(new LinearLayout.LayoutParams(width, blockheight)); repln.setId(reprelid);

                nsln.addView(repln); repln.setPadding(5, 10, 5, 10);
                final int topleftt = 105 + c + 40,  imageid = 203 + c + 43,
                        labelid  = 303 + c + 43, pricetagid = 403 + c + 43, addtocartid =  503 + c + 43, addtofavid =  603 + c + 43;

                LinearLayout pln = new LinearLayout(Activities.this); pln.setId(topleftt);
                RelativeLayout.LayoutParams tr = new RelativeLayout.LayoutParams((int) (width * 0.48),
                        ViewGroup.LayoutParams.MATCH_PARENT);
                pln.setLayoutParams(tr);
                pln = md.productdisplay(Activities.this, pln, idd,  name, inum, price, sztxt, pic1, (int) (width * 0.48), blockheight,
                        ds, imageid, labelid, pricetagid, addtocartid, addtofavid, withfaveheight, smallscreen);
                repln.addView(pln);

                ImageView img = (ImageView) findViewById(imageid); img.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {  productdets(idd, imresult, true); }  });

                TextView tv= (TextView) findViewById(labelid);  tv.setOnClickListener(new View.OnClickListener() {
                    @Override  public void onClick(View view) {  productdets(idd, imresult, true);  }  });

                MaterialTextView mt = (MaterialTextView) findViewById(pricetagid);   mt.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) { clicktocart(idd, name, price, "1", price, inum); }  });

                MaterialButton mb = (MaterialButton) findViewById(addtocartid);    mb.setOnClickListener(new View.OnClickListener() {
                    @Override  public void onClick(View view) { clicktocart(idd, name, price, "1", price, inum); }  });

                boolean addfaves = md.appsetting("AddFavouritesButton", ds).toLowerCase().equals("true");
                if (addfaves){
                    mb = (MaterialButton) findViewById(addtofavid);    mb.setOnClickListener(new View.OnClickListener() {
                        @Override  public void onClick(View view) {  clicktofav(idd, name, price, inum); }  });
                }

                if (c < (imlist.size() - 1)){
                    c = c + 1;
                    final int  imageid2 = imageid + 3, labelid2  = labelid + 43, pricetagid2 = pricetagid + 43, addtocartid2 =  addtocartid + 43, addtofavid2 =  addtofavid + 43;

                    final String idd2 =  md.before(md.getstring(imlist, c), "~");
                    row = md.makearrayfromstring(md.getstring(imlist, c), "~");
                    final String name2 =  md.coloption(imcols, "Name", row).Value;
                    final String inum2 =  md.coloption(imcols, "ItemNumber", row).Value;
                    prc =  md.coloption(imcols, "SellingPrice", row).Value;
                    pic1 =  md.coloption(imcols, "Pic1", row).Value;
                    sztxt = md.coloption(imcols, "SizeText", row).Value;
                    final String price2 = String.valueOf(md.round(md.parsefloat(prc), 2));


                    View vw = new View(Activities.this); vw.setLayoutParams(new LinearLayout.LayoutParams(
                            (int) (width * 0.02), ViewGroup.LayoutParams.MATCH_PARENT
                    )); repln.addView(vw);

                    pln = new LinearLayout(Activities.this);
                    pln.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.48), ViewGroup.LayoutParams.MATCH_PARENT));
                    pln = md.productdisplay(Activities.this, pln, idd2, name2, inum2, price2, sztxt, pic1, (int) (width * 0.48), blockheight,
                            ds, imageid2, labelid2, pricetagid2, addtocartid2, addtofavid2, withfaveheight,  smallscreen);
                    repln.addView(pln);


                    img = (ImageView) findViewById(imageid2); img.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View view) {  productdets(idd2, imresult, true); }  });

                    tv= (TextView) findViewById(labelid2);  tv.setOnClickListener(new View.OnClickListener() {
                        @Override  public void onClick(View view) {  productdets(idd2, imresult, true);  }  });

                    mt = (MaterialTextView) findViewById(pricetagid2);   mt.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View view) { clicktocart(idd2, name2, price2, "1", price2, inum2); }  });

                    mb = (MaterialButton) findViewById(addtocartid2);    mb.setOnClickListener(new View.OnClickListener() {
                        @Override  public void onClick(View view) { clicktocart(idd2, name2, price2, "1", price2, inum2); }  });

                    if (addfaves){

                        mb = (MaterialButton) findViewById(addtofavid);    mb.setOnClickListener(new View.OnClickListener() {
                            @Override  public void onClick(View view) {  clicktofav(idd2, name2, price2, inum2); }  });
                    }
                }


            }

        }
        else  {
            md.toast(Activities.this, " Error, please try again");
        }
    }

    public void clicktocart(String itemid, String name, String price, String qt, String total, String itemnumber){

        boolean gotrans = true;

        if (Executor.equals("2") &  md.appsetting("AllowExecutorBooking", ds).toLowerCase()
                .equals("false")){
            gotrans = false;
        }

        if (!gotrans){
            md.toast(Activities.this, "Sorry, A " +
                    md.appsetting("ExecutorName", ds) + " Cannot Place Orders");
        }else{

            md.toast(Activities.this,   " Adding "+name+" To Cart!" );

            addcart(itemid, price, qt, total, itemnumber, new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    showadded();
                }
            }, 0, Activities.this);
        }


    }
    String adstring = ""; boolean addsuccess = false;

    public void addcart(String item, String price, String qty, String total, String itemnumber,
                        final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "appaddcart?code="+md.auth(Activities.this, mydatabase, android_id, uid)+"~"+
                item +"~"+price+"~"+   qty+"~"+total + "~" + itemnumber;

        final int attempt = attempts + 1; final String kr = url;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { adstring  = response; addsuccess = true; callBack.onSuccess(); }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){ addcart(item, price, qty, total, itemnumber, callBack,  attempt, cnt);
                }else{
                    addsuccess = false;
                    adstring =  "";
                    md.toast(Activities.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showadded(){
        if ( addsuccess){
            md.toast(Activities.this,  "Added To Cart!" );
        }
        else  {
            md.toast(Activities.this,  "Failed To Add To Cart: "+  adstring );
        }
    }

    public void setfetchnumber(){showDialog(222);}
















    public void favourites(){
        lin.removeAllViews();
        level = 1;
        String itemname = md.appsetting("ItemNamePlural", ds);

        MaterialTextView tv = md.maketextview(Activities.this, "Loading " + itemname,
                "", 0, "Info", ds, md.fontmedium, width, (int)(height * 0.1), true);

        lin.addView(tv);
        pbbar = new ProgressBar(Activities.this); pbbar.setId(pbarid); lin.addView(pbbar);

        getfavourites(  new MainActivity.VolleyCallBack() {
            @Override  public void onSuccess() { showfavourites(); }
        }, 0, Activities.this);
    }

    boolean favsuccess = false; String favresult = "";
    public void getfavourites(  final MainActivity.VolleyCallBack callBack,
                                int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "getfavourites?code=" + md.auth(Activities.this, mydatabase, android_id, uid);

        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        favresult  = response;
                        favsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                favsuccess = false;

                if (attempt < md.attempts){
                    getfavourites(   callBack,  attempt, cnt);
                }else{
                    favresult =  "";
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showfavourites(){


        if ( favsuccess & lin != null){

            lin.removeAllViews();
            int  linpad = 6, gapht = 10, picwidth = (int) (height * 0.13), iconwidth = (int) (height * 0.1), iconht = (int)(width * 0.1),
                    gapsum = picwidth,
                    vwid = width  - (linpad*2); lin.setPadding(linpad, 0, linpad, 0);

            ArrayList<String> list  =  md.makearrayfromstring(favresult, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            if (list.size() <= 2){

                MaterialTextView nb = md.maketextview(Activities.this,  "You Do Not Have Any Favourites Saved","", 0, "Info",
                        ds, fontsmall, width, picwidth, true);lin.addView(nb);
            }else{
                for (int c = 2; c < list.size(); c++){
                    ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                    String rowid =  md.before(md.getstring(list, c), "~");
                    String idd =  md.coloption(cols, "ValueItem", row).Value;
                    String price =  md.coloption(cols, "ItemSellingPrice", row).Value;
                    String inumber =  md.coloption(cols, "ItemItemNumber", row).Value;
                    String name =  md.coloption(cols, "Item", row).Value;
                    String pic1 =  md.coloption(cols, "ItemPic1", row).Value;

                    gapsum += gapht;
                    int nameid = 1111+c,   imgid = 1501+c, smallpad = 4
                            ,  namewidth = (int)(vwid - ( iconht* 3)) - (smallpad * 2);

                    View vw = new View(Activities.this); vw.setLayoutParams(new LinearLayout.LayoutParams(width, (gapht-2)));
                    lin.addView(vw);
                    vw = new View(Activities.this); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2));
                    vw.setBackgroundColor(md.lightgray); lin.addView(vw);

                    LinearLayout ln1 = new LinearLayout(Activities.this); ln1.setOrientation(LinearLayout.HORIZONTAL);
                    ln1.setLayoutParams(new LinearLayout.LayoutParams(vwid, picwidth)); ln1.setPadding(0, 6, 0, 0);


                    ln1.setPadding(smallpad, 2, smallpad, 2); lin.addView(ln1);
                    if (!pic1.equals("")){
                        namewidth -= picwidth;

                        ImageView imgView = md.image(Activities.this, imgid, ds);
                        imgView.setLayoutParams(new ViewGroup.LayoutParams(picwidth, (int)(picwidth*0.9)));
                        ln1.addView(imgView);
                        String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ pic1;
                        imgView = md.loadimage(getResources() ,imgView, url,picwidth, (int)(picwidth*0.9) );
                        imgView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                productdets(idd, "", true);
                            }
                        });
                    }

                    String shownums  = md.appsetting("ShowItemNumber", ds), nm = name;
                    if (shownums.toLowerCase().equals("true")){
                        nm += System.lineSeparator() + inumber;
                    }
                    nm += System.lineSeparator() + inumber;

                    MaterialTextView nb = md.maketextview(Activities.this,  nm,"", nameid, "Info",
                            ds, fontsmall, namewidth, picwidth, true);ln1.addView(nb);
                    nb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            productdets(idd, "", true);
                        }
                    });

                    int space = (int)(0.5 * (iconwidth - iconht)), halficonspace = (int)(iconht * 0.5);

                    LinearLayout lnadd = new LinearLayout(Activities.this);
                    lnadd.setOrientation(LinearLayout.VERTICAL);
                    lnadd.setLayoutParams(new LinearLayout.LayoutParams(halficonspace, iconwidth));
                    lnadd.setPadding(0, space, 0, 0); ln1.addView(lnadd);

                    lnadd = new LinearLayout(Activities.this);
                    lnadd.setOrientation(LinearLayout.VERTICAL);
                    lnadd.setLayoutParams(new LinearLayout.LayoutParams( iconht, iconwidth));
                    lnadd.setPadding(0, space, 0, 0); ln1.addView(lnadd);

                    MaterialButton mb = new MaterialButton(Activities.this);
                    mb.setLayoutParams(new FrameLayout.LayoutParams(iconht, (int)(iconht*0.9)));
                    mb = md.addtocarticon(mb, ds);
                    mb.setPadding(10, 15, 10, 10); lnadd.addView(mb);
                    mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                        clicktocart(idd, name, price, "1", price, inumber);
                    } });

                    lnadd = new LinearLayout(Activities.this);   lnadd.setOrientation(LinearLayout.VERTICAL);
                    lnadd.setLayoutParams(new LinearLayout.LayoutParams(halficonspace, iconwidth));
                    lnadd.setPadding(0, space, 0, 0); ln1.addView(lnadd);

                    LinearLayout lnclose = new LinearLayout(Activities.this);   lnclose.setOrientation(LinearLayout.VERTICAL);
                    lnclose.setLayoutParams(new LinearLayout.LayoutParams( iconht, iconwidth));
                    lnclose.setPadding(0, space, 0, 0); ln1.addView(lnclose);

                    mb = new MaterialButton(Activities.this);
                    mb.setLayoutParams(new FrameLayout.LayoutParams( iconht,  iconht));
                    mb.setBackgroundColor(md.brightred);
                    mb.setBackgroundResource(R.drawable.ic_baseline_close_24);
                    mb.setPadding(10, 15, 10, 10); lnclose.addView(mb);
                    mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                        removefav(idd, name);
                    } });

                }


                View vw = new View(Activities.this); vw.setLayoutParams(new LinearLayout.LayoutParams(width, (gapht-2)));
                lin.addView(vw);
                vw = new View(Activities.this); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2));
                vw.setBackgroundColor(md.lightgray); lin.addView(vw);
            }



            lin.setPadding(linpad, 4, linpad, gapsum);

        }
        else  {
            md.toast(Activities.this,  "Failed To Add To Load Favouites, Please Try Again" );
        }
    }

    public  void removefav(String itemid, String itemname){
        md.toast(Activities.this,   "Removing "+itemname+" From Favourites!" );

        remfav(itemid,   new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showremovedfav(itemname);
                favourites();
            }
        }, 0, Activities.this);
    }
    String rfstring = ""; boolean rfsuccess = false;

    public void remfav(String item,
                       final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "removefavourite?code="+md.auth(Activities.this, mydatabase, android_id, uid)+"~"+  item ;

        final int attempt = attempts + 1; final String kr = url;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { rfstring  = response; rfsuccess = true; callBack.onSuccess(); }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){ remfav(item, callBack,  attempt, cnt);
                }else{
                    rfsuccess = false;
                    rfstring =  "";
                    md.toast(Activities.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showremovedfav(String itemname){
        if ( rfsuccess){
            md.toast(Activities.this,  itemname + " Removed From Favourites!" );
        }
        else  {
            md.toast(Activities.this,  "Failed To Remove From Favourites. Please Try Again" );
        }
    }

    public void clicktofav(String itemid, String name, String price,  String itemnumber){

        md.toast(Activities.this,   " Adding "+name+" To Favourites!" );

        addfav(itemid, price, itemnumber, new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showaddedfav();
            }
        }, 0, Activities.this);
    }
    String afstring = ""; boolean afsuccess = false;


    public void addfav(String item, String price, String itemnumber,
                       final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "appaddfav?code="+md.auth(Activities.this, mydatabase, android_id, uid)+"~"+
                item +"~"+price+"~"+ itemnumber;

        final int attempt = attempts + 1; final String kr = url;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { afstring  = response; afsuccess = true; callBack.onSuccess(); }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){ addfav(item, price, itemnumber, callBack,  attempt, cnt);
                }else{
                    afsuccess = false;
                    afstring =  "";
                    md.toast(Activities.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showaddedfav(){
        if ( afsuccess){
            md.toast(Activities.this,  "Added To Favourites!" );
        }
        else  {
            md.toast(Activities.this,  "Failed To Add To Favourites. Please Try Again" );
        }
    }




    int additemqty = 0, itpbarid  = 234525233, linid  = 895321;

    int itprid = 832241, itqminusid = 6678242, itqtyid  = 42854334, itqplusid = itqtyid - 5, itqaddid = itqplusid  - 5;



    public void productdetails(){

        String pid = md.getarray(receivearr, 3);
        String res = md.getarray(receivearr, 4);

        productdets(pid, res, false);
    }

    boolean ptsuccess = false; String ptresult = "";
    public void productdets(String id, String pdetailstring, boolean increaselevel){
        additemqty = 0;
        ptresult = pdetailstring;
        ptsuccess = false;
        if (increaselevel){level = 2;}
        lin.removeAllViews();
        if (pdetailstring.equals("")){

            getproductdets(id , new MainActivity.VolleyCallBack() {
                @Override  public void onSuccess() {  showproductdets(id); }
            }, 0, Activities.this);

        }else{
            ptsuccess = true;
            showproductdets(id);
        }
    }
    public void getproductdets( String pid, final MainActivity.VolleyCallBack callBack,
                                int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        final int attempt = attempts + 1;
        String url = md.url + "gettable?code="+md.auth(Activities.this, mydatabase, android_id, uid)+"~Items^|ID^=^"+pid;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ptresult  = response;
                        ptsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ptsuccess = false;

                if (attempt < md.attempts){
                    getproductdets(pid,   callBack,  attempt, cnt);
                }else{
                    ptresult =  "";
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }




    public void showproductdets(String pid)
    {
        if ( ptsuccess & lin != null){
            lin.removeAllViews();
            ArrayList<String>  ptlist = md.makearrayfromstring(ptresult,"¬");
            ArrayList<String>  ptcols = md.makearrayfromstring(md.getstring(ptlist, 1), "~");

            for (int c = 2; c < ptlist.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(ptlist, c), "~");
                String idd =  md.before(md.getstring(ptlist, c), "~");
                if (idd.equals(pid)){
                    String name =  md.coloption(ptcols, "Name", row).Value;
                    String inum =  md.coloption(ptcols, "ItemNumber", row).Value;
                    String prc =  md.coloption(ptcols, "SellingPrice", row).Value;
                    String isv =  md.coloption(ptcols, "IsAvailable", row).Value;
                    int isvtype= md.green;
                    if (isv.toLowerCase().equals("false")){isv = "Not Available"; isvtype = md.brightred;}
                    else{isv = "Available";}

                    ArrayList<String> haspics = new ArrayList<>();
                    for (int i = 1; i <= 5; i++){
                        String pic =  md.coloption(ptcols, "Pic" + String.valueOf(i), row).Value;
                        if (!pic.equals("")){haspics.add(pic);}
                    }
                    int rowheight = (int) (0.07 * height), descripht = height;

                    if (haspics.size() > 0){
                        int hw = (int)(width * 0.7);
                        if (smallscreen){hw = (int)(width * 0.55);}
                        descripht -= hw;
                        HorizontalScrollView ns  = new HorizontalScrollView(Activities.this);
                        ns.setScrollbarFadingEnabled(false);
                        ns.setLayoutParams(new LinearLayout.LayoutParams(width, hw)); lin.addView(ns);

                        LinearLayout nsln = new LinearLayout(Activities.this); nsln.setOrientation(LinearLayout.HORIZONTAL);
                        nsln.setId(linid);
                        nsln.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        ns.addView(nsln);

                        for (int i = 0; i < haspics.size(); i++){
                            int itimgid = 2934132 + i;
                            ImageView imgView = md.image(Activities.this, itimgid, ds);
                            imgView.setLayoutParams(new ViewGroup.LayoutParams(hw, hw));
                            nsln.addView(imgView);
                            String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ md.getstring(haspics, i);
                            imgView = md.loadimage(getResources() ,imgView, url,hw, hw );
                            imgView.setPadding(0, 10, 0, 0);
                        }
                    }

                    View vw = new View(Activities.this); vw.setBackgroundColor(md.darkgray);
                    vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2)); lin.addView(vw);

                    MaterialTextView mt = md.maketextview(Activities.this, name, "", 0, "Info",
                            ds, fontmedium, width, rowheight, false); mt.setTypeface(null, Typeface.BOLD);
                    lin.addView(mt);

                    descripht -= (rowheight + 2);

                    if (!inum.equals("")){ mt.setHeight(rowheight*2);
                        mt.setLines(2); mt.setText(name + System.lineSeparator() + inum);}

                    int avh =(int)(rowheight * 0.6);
                    TextView tv  = new MaterialTextView(Activities.this);  tv.setHeight(avh);
                    tv.setWidth(width);  tv.setText(isv); tv.setTextColor(isvtype);
                    lin.addView(tv); tv.setTextSize(fontsmall); tv.setPadding(0, 0, 0, 0);

                    descripht -= avh;

                    LinearLayout lr2 = new LinearLayout(Activities.this); lr2.setBackgroundColor(md.transparent);
                    lr2.setOrientation(LinearLayout.HORIZONTAL);
                    lr2.setLayoutParams(new LinearLayout.LayoutParams(width,  rowheight));
                    descripht -= (rowheight);
                    lin.addView(lr2); lr2.setPadding(0, 0, 0,0);

                    float prf = md.round(md.parsefloat(prc), 2);

                    mt = md.maketextview(Activities.this, MainActivity.currencysymbol+ String.valueOf(prf), String.valueOf(prf), itprid,
                            "Info", ds, fontsmall,  (int) (width * 0.4), rowheight, true); lr2.addView(mt);

                    md.button(Activities.this, lr2, "-", "", itqminusid,"Cancel", ds,
                            md.fontmedium,   (int) (width * 0.12),  rowheight);
                    MaterialButton btminus = (MaterialButton) findViewById( itqminusid); btminus.setHeight(rowheight);
                    btminus.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View view) {  addqty( itqtyid, -1,prc );   } });

                    md.button(Activities.this, lr2, "1", "",  itqtyid,"Info", ds,  md.fontmedium,
                            (int) (width * 0.12),  rowheight);
                    MaterialButton edt = (MaterialButton) findViewById( itqtyid);   edt.setHeight(rowheight);

                    md.button(Activities.this, lr2, "+", "",   itqplusid,"Warning", ds, md.fontmedium,
                            (int) (width * 0.12),  rowheight);
                    MaterialButton btplus = (MaterialButton) findViewById( itqplusid); btplus.setHeight(rowheight);
                    btplus.setOnClickListener(new View.OnClickListener() {
                        @Override  public void onClick(View view) {  addqty( itqtyid, 1, prc); }  });


                    md.button(Activities.this, lr2, "", "",   itqaddid,"Go", ds, md.fontmedium,
                            (int) (width * 0.12),  rowheight);
                    MaterialButton btadd = (MaterialButton) findViewById( itqaddid); btadd.setHeight(rowheight);
                    btadd = md.addtocarticon(btadd, ds);
                    btadd.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View view) {  itempagetocart(idd, name, prc, inum);  } });



                    String url = md.auth(Activities.this, mydatabase, android_id, uid)+"~ItemDescriptions^|Item^"+  idd;



                    vw = new View(Activities.this); vw.setBackgroundColor(md.lightgray);
                    vw.setLayoutParams(new LinearLayout.LayoutParams(width, 3)); lin.addView(vw);

                    descripht = (int)( descripht * 0.95);
                    int mp = ViewGroup.LayoutParams.MATCH_PARENT;
                    NestedScrollView nsv = new NestedScrollView(Activities.this);
                    nsv.setLayoutParams(new LinearLayout.LayoutParams(mp, descripht ));
                    lin.addView(nsv);

                    LinearLayout itlin = new LinearLayout(Activities.this); itlin.setOrientation(LinearLayout.VERTICAL);
                    itlin.setLayoutParams(new FrameLayout.LayoutParams(mp,  descripht)); nsv.addView(itlin);
                    itlin.setId(itemdesciptlinid);

                    ProgressBar pb = new ProgressBar(Activities.this); pb.setId(itpbarid); itlin.addView(pb);
                    pb.setVisibility(View.VISIBLE);


                    getitdescrip( url, new MainActivity.VolleyCallBack() {
                        @Override  public void onSuccess() { showitemdetails(); }
                    }, 0, Activities.this);
                    break;
                }
            }
        }
        else  {
            md.toast(Activities.this,  "Failed To Add To Load Favouites, Please Try Again" );
        }

    }



    public void itempagetocart(String idd, String name, String prc, String itemnumber){
        float total =  (float) (additemqty * md.parsefloat(prc));
        total = md.round(total, 2);
        clicktocart(idd, name, prc, String.valueOf(additemqty), String.valueOf(total), itemnumber);
    }

    public void addqty(int qtid, int number, String price){
        MaterialButton ed = (MaterialButton) findViewById(qtid);
        String qt = ed.getText().toString();
        int numb = 0;
        try{  numb = md.parseint(qt);  }catch (NumberFormatException nf){ }
        numb = numb + number;
        if (numb < 0){numb = 0;}
        ed.setText(String.valueOf(numb));

        additemqty =numb;
    }







    boolean scsuccess = false; String scresult = ""; int itemdesciptlinid  = 9425798;
    public void getitdescrip(String ur, final MainActivity.VolleyCallBack callBack,
                             int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "gettable?code=" + ur;

        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        scresult  = response;
                        scsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                scsuccess = false;

                if (attempt < md.attempts){
                    getitdescrip(ur,  callBack,  attempt, cnt);
                }else{
                    scresult =  "";
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");

                    ProgressBar pb = (ProgressBar) findViewById(itpbarid); if (pb != null){  pb.setVisibility(View.GONE);}

                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showitemdetails(){
        LinearLayout itlin = findViewById(itemdesciptlinid);

        if (!scsuccess){
            //md.toast(Activities.this, scresult);
        }
        else if (scsuccess  & itlin != null){

            itlin.removeAllViews();
            int lineheight = (int) (height * 0.06);

            ArrayList<String>  sclist = md.makearrayfromstring(scresult, "¬");
            ArrayList<String>  sccols = md.makearrayfromstring(md.getstring(sclist, 1), "~");

            for (int c = 2; c < sclist.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(sclist, c), "~");
                String idd =  md.before(md.getstring(sclist, c), "~");
                String name =  md.coloption(sccols, "Name", row).Value;
                String desc =  md.coloption(sccols, "Description", row).Value;

                MaterialTextView mt = md.maketextview(Activities.this, name, "", 0,"Info", ds,
                        md.fontmedium, width, lineheight, false); mt.setTypeface(null, Typeface.BOLD);
                itlin.addView(mt);

                mt = md.maketextview(Activities.this, desc, "", 0,"Info", ds,
                        fontsmall, width, (int)(lineheight*0.9), false); mt.setTextColor(md.darkgray);
                itlin.addView(mt);


            }

        }
        else  {
            md.toast(Activities.this, " Error, please try again");
        }
    }


















    int locsavebtnid = 4455992; boolean addressreturn = false;
    public void myaddresses(){

        try{

            if (md.getarray(receivearr, 3).equals("1")){addressreturn = true;}

        }catch (Exception xx){}

        lin.removeAllViews();
        ProgressBar pb = new ProgressBar(Activities.this);
        lin.addView(pb);

        getaddresses(new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showaddresses();
            }
        }, 0);

    }

    String ddstring = ""; boolean ddsuccess = false; int addresscount = 0, locbtnid =  41515210;
    ArrayList<MyModel.option> addinputs = new ArrayList<>();
    public void getaddresses( final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(Activities.this);

            String url = md.url + "getaddresses";
            url = url + "?code="+md.auth(Activities.this, mydatabase, android_id, uid)+"~1";

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  ddstring = response; ddsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ getaddresses(   callBack,  attempt); }
                    else{ ddsuccess =false; md.toast(Activities.this,  "Failed To Reach Server... Retrying"); }
                }
            });

            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }

    public void showaddresses(){

        // Display the first 500 characters of the response string.
        if (!ddsuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(Activities.this, z);
        }
        else if(ddsuccess) {
            lin.removeAllViews();

            ArrayList<String> list  =  md.makearrayfromstring(ddstring, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            ArrayList<String> discols =new ArrayList<>(), dss = md.makearrayfromstring(md.getstring(rules, 9), "~");;

            for (int i =0; i < dss.size(); i++){
                if (md.before(md.getstring(dss, i), "^").equals("UserAddresses")){
                    discols = md.makearrayfromstring(md.breakurl(md.getstring(dss, i), 1, "^"), "_");
                    break;
                }
            }

            if (list.size() > 2){
                String rowid = "";
                for (int c = 2; c < list.size(); c++){
                    addresscount++;
                    ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                    String idd =  md.before(md.getstring(list, c), "~");
                    rowid = idd;
                    int lineheight = (int) (height * 0.085);
                    for (int d = 0; d <  discols.size(); d++){
                        int dislid = 1231932 + d, contid = 2231932 + d;
                        RelativeLayout rl = new RelativeLayout(Activities.this); lin.addView(rl);
                        rl.setLayoutParams(new LinearLayout.LayoutParams(width, lineheight));

                        MaterialTextView  mt = new MaterialTextView(Activities.this);  mt.setHeight(lineheight); rl.addView(mt); mt.setId(dislid);
                        mt.setWidth((int)(width*.49));  mt = md.textviewdesign(Activities.this, mt, "Info", ds);   mt.setText(md.returndisplay(md.getstring(discols, d) + ": "));

                        RelativeLayout.LayoutParams tr = new RelativeLayout.LayoutParams((int) (width * 0.49), lineheight);
                        tr.addRule(RelativeLayout.RIGHT_OF, dislid);
                        if (md.getstring(discols, d).equals("Location")){
                            String loc = md.coloption(cols, md.getstring(discols, d), row).Value;
                            Button edt = new Button(Activities.this); edt.setId(locbtnid); edt.setTextSize(md.fontmedium);
                            rl.addView(edt); edt.setTag(loc); edt.setTextColor(md.white);
                            if (loc.equals("")){edt.setText("Not Set");edt.setBackgroundColor(md.brightred);
                            }else{edt.setText("Location Set"); edt.setBackgroundColor(md.green);}

                            edt.setLayoutParams(tr); edt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    setlocation( "UserAddresses",idd, "Location",  loc);
                                }
                            });
                            contid = locbtnid;
                        }

                        else{
                            EditText edt = new EditText(Activities.this); edt.setId(contid); edt.setTextSize(md.fontmedium);
                            rl.addView(edt);edt.setHint(md.returndisplay(md.getstring(discols, d)));
                            edt.setText(md.coloption(cols, md.getstring(discols, d), row).Value);
                            edt.setLayoutParams(tr);
                        }
                        MyModel.option op = new MyModel.option("", md.getstring(discols, d), "", "");
                        op.ValueInt = contid;
                        addinputs.add(op);

                    }
                }

                final  String rid = rowid;
                MaterialButton btminus = md.makebutton(Activities.this, "Save", "",
                        locsavebtnid, "Go", ds, fontmedium, width, (int)(height * 0.08));
                lin.addView(btminus);
                btminus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveaddress(rid);
                    }   });
            }
            pbbar = new ProgressBar(Activities.this); pbbar.setId(pbarid);
            lin.addView(pbbar); pbbar.setVisibility(View.GONE);
        }
    }

    public void saveaddress(String rowid) {

        ArrayList<String> list = md.makearrayfromstring(ddstring, "¬");
        String rulestring = md.getstring(list, 0);
        ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
        ArrayList<String> discols = new ArrayList<>(), dss = md.makearrayfromstring(md.getstring(rules, 9), "~");

        for (int i =0; i < dss.size(); i++){
            if (md.before(md.getstring(dss, i), "^").equals("UserAddresses")){
                discols = md.makearrayfromstring(md.breakurl(md.getstring(dss, i), 1, "^"), "_");
                break;
            }
        }
        String vals = "";
        for (int d = 0; d <  addinputs.size(); d++) {
            if (md.getoption(addinputs, d).Option1.equals("Location")){
                Button edt = (Button) findViewById(locbtnid);
                vals += "|" + md.getoption(addinputs, d).Option1+ "^" + edt.getTag().toString();
            }else{

                EditText edt = (EditText) findViewById(md.getoption(addinputs, d).ValueInt);
                vals += "|" + md.getoption(addinputs, d).Option1 + "^" + edt.getText().toString();
            }
        }

        multiset("UserAddresses", rowid, vals,    new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                fianaladdress();
            }
        }, 0, Activities.this);

    }


    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public void setlocation( String table, String rowid, String column, String value)
    {
        GpsTracker gpsTracker = new GpsTracker(Activities.this);
        if(gpsTracker.canGetLocation()){


            Bundle b = new Bundle();
            b.putStringArray("key", new String[]{"Set",  ds, uid, Executor, table, rowid,column, value  });
            Intent intent = new Intent(getApplicationContext(), SetMap.class);
            intent.putExtras(b);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);


        }else{
            md.toast(Activities.this, "Failed To Get Your Current Location");
            gpsTracker.showSettingsAlert();
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activities.this.RESULT_OK) {
                Bundle b = data.getExtras();
                String[] received = b.getStringArray("key");

                if (md.getarray(received, 0).equals("Location")){
                    Button bt = (Button) Activities.this.findViewById(locbtnid); bt.setTag(md.getarray(received, 1));
                    bt.setText("Location Set"); bt.setBackgroundColor(md.green);
                }

            }
        }
    }

    String msresult = ""; boolean mssuccess = false;
    public void multiset(String table,  String rowid, String values, final MainActivity.VolleyCallBack callBack,
                         int attempts, Context cnt) {

        ProgressBar pb = (ProgressBar) findViewById(pbarid); pb.setVisibility(View.VISIBLE);
        MaterialButton mb = (MaterialButton) findViewById(locsavebtnid); mb.setEnabled(false);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "multiset";

        url = url + "?code="+md.auth(Activities.this, mydatabase, android_id, uid)+"~"+table+"~"+rowid+"~"+values;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        msresult = response;
                        mssuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mssuccess = false;
                msresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    multiset( table,rowid,  values, callBack,  attempt, cnt);
                }else{
                    msresult =  "";
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void fianaladdress(){

        MaterialButton mb = (MaterialButton) findViewById(locsavebtnid); mb.setEnabled(true);
        pbbar = (ProgressBar)findViewById(pbarid); pbbar.setVisibility(View.GONE);

        if (mssuccess ){
            md.toast(Activities.this, "Address Saved!");

            if (addressreturn ){
                Bundle b = new Bundle();
                b.putStringArray("key", new String[]{"Address", "Saved"});
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
            }
            finish();

        }
        else  { md.toast(Activities.this, " Error, please try again");  }
    }



















    public void chat(){

        lin.removeAllViews();
        ProgressBar pb = new ProgressBar(Activities.this);
        lin.addView(pb);

        getchats(new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showchats();
            }
        }, 0);

    }

    String chstring = "", chmessage ="", chreference  = ""; boolean chsuccess = false; int chbtnid =  41515210, chlinid = chbtnid+1, chinput = chlinid+1;
    LinearLayout chlin;
    public void getchats( final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(Activities.this);

            String url = md.url + "getchats?code=" +
                    md.auth(Activities.this, mydatabase, android_id, uid) +"~"+uid+"~1~";

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  chstring = response; chsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ getaddresses(   callBack,  attempt); }
                    else{ chsuccess =false;
                        md.toast(Activities.this,  "Failed To Reach Server... Retrying"); }
                }
            });

            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }

    public void showchats(){

        // Display the first 500 characters of the response string.
        if (!chsuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(Activities.this, z);
        }
        else if(chsuccess) {
            lin.removeAllViews();

            int topheight = (int) (height * 0.9), boheight = height - topheight;

            NestedScrollView ns = new NestedScrollView(Activities.this);
            ns.setLayoutParams(new LinearLayout.LayoutParams(width, topheight));
            lin.addView(ns);

            chlin = new LinearLayout(Activities.this); ns.addView(chlin);
            chlin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)); chlin.setId(chlinid);
            chlin.setOrientation(LinearLayout.VERTICAL);

            int boxdim = (int)(width * 0.12);

            RelativeLayout rl = new RelativeLayout(Activities.this);
            rl.setLayoutParams(new LinearLayout.LayoutParams(width,  boheight));  lin.addView(rl);
            int tvw = width - boxdim;

            EditText edt = new EditText(Activities.this); edt.setId(chinput); edt.setTextSize(md.fontmedium);
            edt.setLayoutParams(new RelativeLayout.LayoutParams(tvw, boxdim));
            rl.addView(edt);

            Button bt = new Button(Activities.this); bt.setId(chbtnid);
            RelativeLayout.LayoutParams tm = new RelativeLayout.LayoutParams(boxdim, boxdim);
            tm.addRule(RelativeLayout.RIGHT_OF, chinput); bt.setLayoutParams(tm); rl.addView(bt);
            bt.setBackgroundResource(R.drawable.ic_baseline_send_24);  bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditText ed = (EditText) findViewById(chinput);
                    if (ed != null){
                        if (!ed.getText().toString().equals("")){
                            chmessage = ed.getText().toString();
                            ed.setText("");
                            chreference = md.randomstring();
                            sendchat();
                        }
                    }

                }
            });
            displaychat();
        }
    }

    ArrayList<String> inchats = new ArrayList<>();

    public void displaychat(){

        if (chsuccess & chstring.contains("¬")& chstring.contains("~"))
        {
            chmessage  = "";
            ArrayList<String> list  =  md.makearrayfromstring(chstring, "¬");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list,1), "~");
            int pad = (int)(width * 0.25),  firstpad = 5;
            int controlwidth = width - (firstpad * 2) - pad;

            for (int c = 2; c < list.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                String idd =  md.before(md.getstring(list, c), "~");

                if (!inchats.contains(idd)){
                    inchats.add(idd);
                    String message = md.coloption(cols, "Message", row).Value;
                    boolean response = md.coloption(cols, "Response", row).Value.toLowerCase().equals("true");

                    int lines = md.countlines(message, md.fontmedium+2, controlwidth);
                    if (lines <1){lines = 1;}

                    MaterialTextView  mt = new MaterialTextView(Activities.this);
                    if (!response){   mt.setPadding(firstpad, 4, pad, 4);
                        mt.setBackgroundResource(R.drawable.lightbluebutton);}
                    else{ mt.setPadding(pad, 4, firstpad, 4);
                        mt.setBackgroundResource(R.drawable.whitebutton);}


                    mt.setWidth(width);
                    mt.setLines(lines);
                    mt.setWidth(controlwidth);  mt.setText(message);

                    chlin.addView(mt);


                    View vw = new View(Activities.this);
                    vw.setLayoutParams(new LinearLayout.LayoutParams(width, 8)); chlin.addView(vw);
                }
            }
        }
        else{
            EditText ed = (EditText) findViewById(chinput);
            if (ed != null){
                ed.setText(chmessage );
            }
            md.toast(Activities.this,  "Error: Please Try Again");

        }
    }

    public void sendchat() {
        if (!chmessage.equals("")){
            addtochats( new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    displaychat();
                }
            }, 0);
        }
    }


    public void addtochats(final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(Activities.this);

            String url = md.url + "addtochat?code=" +
                    md.auth(Activities.this, mydatabase, android_id, uid) +"~"+uid+"~1~~" + chmessage + "~0~"+chreference;

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  chstring = response; chsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    chsuccess =false;
                    EditText ed = (EditText) findViewById(chinput);
                    if (ed != null){
                        ed.setText(chmessage );
                    }
                    md.toast(Activities.this,  "Failed To Reach Server... Retrying");
                }
            });

            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }























    String poslocation, posuserid, poslocationname, posusername, selectedposlocation = "", posenddirection = ""; int pospinid = 121, possaveid = 125;

    public void pointofsalesignin(){
        setTitle("POS Sign In/Out");
        poslocation = md.getarray(receivearr, 3);
        posuserid = md.getarray(receivearr, 4);
        poslocationname = md.getarray(receivearr, 5);
        posusername = md.getarray(receivearr, 6);
        posenddirection= md.getarray(receivearr, 7);

        lin.removeAllViews();
        level = 1;


        MaterialTextView tv = md.maketextview(Activities.this, "Loading Branches...",
                "", 0, "Info", ds, md.fontmedium, width, (int)(height * 0.1), true);

        lin.addView(tv);
        pbbar = new ProgressBar(Activities.this); pbbar.setId(pbarid); lin.addView(pbbar);

        if (poslocation.equals("")){
            getbranches(  new MainActivity.VolleyCallBack() {
                @Override  public void onSuccess() { showbranches(true); }
            }, 0, Activities.this);
        }else{
            possuccess = true;showbranches(false);
        }
    }

    boolean possuccess = false; String posresult = "";
    public void getbranches(  final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);


        String url = md.url + "gettable?code=" +  md.auth(Activities.this, mydatabase,  android_id, uid)+"~CompanyLocations";
        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        posresult  = response;
                        possuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                possuccess = false;

                if (attempt < md.attempts){
                    getbranches(   callBack,  attempt, cnt);
                }else{
                    posresult =  "";
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showbranches(boolean showbranches){
        if (possuccess & lin != null){

            lin.removeAllViews();
            int  rowheight = (int)(height * 0.09), midheight = (int)(height * 0.67);

            ArrayList<String> list  =  md.makearrayfromstring(posresult, "¬");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            if (list.size() <= 2 & showbranches){

                MaterialTextView nb = md.maketextview(Activities.this,  "You Do Not Have Any Branches Saved","", 0, "Info",
                        ds, fontsmall, width, rowheight, true);lin.addView(nb);
            }else{

                String mss = "No Point Of Sale User Or Branch Assigned";
                if (!poslocation.equals("") & !posuserid.equals("")){ mss = posusername+" Is Currently Signed Into "+ poslocationname;
                    selectedposlocation = poslocation;
                }

                MaterialTextView nb = md.maketextview(Activities.this,  mss,"", 0, "Info",
                        ds, fontsmall, width, rowheight, true);lin.addView(nb);

                View vw =  md.line(Activities.this, width, 2, md.lightgray); lin.addView(vw);

                LinearLayout pinlin = new LinearLayout(Activities.this); lin.addView(pinlin);
                pinlin.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight)); pinlin.setOrientation(LinearLayout.HORIZONTAL);

                nb =  md.maketextview(Activities.this,  "Pin: ","", 0, "Info",
                        ds, fontsmall, (int)(width*0.3), rowheight, true); pinlin.addView(nb);

                EditText pint = md.edittext(Activities.this, pospinid, "Enter Your Pin", "",
                        "Number",(int)(width*0.66), rowheight, fontmedium, ds ); pinlin.addView(pint);

                vw = md.line(Activities.this, width, 2, md.lightgray); lin.addView(vw);

                if (showbranches){
                    NestedScrollView ns = new NestedScrollView(Activities.this);lin.addView(ns);
                    ns.setLayoutParams(new LinearLayout.LayoutParams(width, midheight));
                    LinearLayout poslin = new LinearLayout(Activities.this); ns.addView(poslin);
                    poslin.setLayoutParams(new FrameLayout.LayoutParams(width, midheight));

                    RadioGroup rg = new RadioGroup(Activities.this); poslin.addView(rg);
                    for (int c = 2; c < list.size(); c++){
                        ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                        String rowid =  md.before(md.getstring(list, c), "~");
                        String name =  md.coloption(cols, "Name", row).Value;

                        vw = new View(Activities.this); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2));
                        vw.setBackgroundColor(md.lightgray); lin.addView(vw);

                        RadioButton rb = new RadioButton(Activities.this); rb.setText(name);
                        rb.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight));
                        rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                selectedposlocation = rowid;
                            }
                        });
                        if (rowid.equals(poslocation)){rb.setChecked(true);}
                        rg.addView(rb);
                    }
                }

                String style = "Cancel", act = "Sign Out", dir = "0";
                if (showbranches){style = "Go"; act = "Sign In";dir = "1";} String fd = dir;

                LinearLayout lastlin = new LinearLayout(Activities.this);
                lastlin.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight));
                lin.addView(lastlin); lastlin.setOrientation(LinearLayout.HORIZONTAL);

                MaterialButton outbtn = md.makebutton(Activities.this,act, "", possaveid, style, ds,
                        md.fontmedium,(int)(width * 0.95), rowheight);
                outbtn.setOnClickListener(new View.OnClickListener() {
                    @Override  public void onClick(View view) { dopossignchange(fd, showbranches);   }  });
                lastlin.addView(outbtn);

                pbbar = new ProgressBar(Activities.this);
                pbbar.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight));
                lastlin.addView(pbbar); pbbar.setVisibility(View.GONE);

                vw = md.line(Activities.this, width, 2, md.lightgray); lin.addView(vw);
            }
        }
        else  {
            md.toast(Activities.this,  "Failed To Add To Load Favouites, Please Try Again" );
        }
    }

    public  void dopossignchange( String signdir, boolean showbranches){

        String pin = "", ms = "";

        EditText pt  = (EditText) findViewById(pospinid);
        if (pt != null){  pin = pt.getText().toString();   }


        if (pin.equals("")){
            ms += "Please Enter Your Pin" + System.lineSeparator();
        }

        if (selectedposlocation.equals("") & showbranches){
            ms += "Please Select A Location" + System.lineSeparator();
        }
        if (!ms.equals("")){
            md.toast(Activities.this,  ms );

        }else  if (ms.equals("") & !selectedposlocation.equals("")){
            possignchange(pin, selectedposlocation, signdir,   new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    showpossignchange(signdir);
                }
            }, 0, Activities.this);
        }


    }

    public void   possignchange(String pin, String companylocation, String signdir,
                                final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        MaterialButton mb = (MaterialButton) findViewById(possaveid);
        if (mb != null & pbbar != null){
            mb.getLayoutParams().width = 0;
            pbbar.setVisibility(View.VISIBLE);
        }

        RequestQueue queue = Volley.newRequestQueue(cnt);

        String vls = md.auth(Activities.this, mydatabase, android_id, uid)+"~"+
                md.encrypt(Activities.this, pin, true, true)+"~"+
                md.encrypt(Activities.this, companylocation, true, true)
                +"~"+ md.encrypt(Activities.this, signdir, true, true);

        String url = "https://www.valeronpro.com/App/possignchange?code=" +vls ;

        final int attempt = attempts + 1; final String kr = url;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { posresult  = response; possuccess = true; callBack.onSuccess(); }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){ possignchange(pin, companylocation, signdir, callBack,  attempt, cnt);
                }else{

                    showpossavebtn();
                    possuccess = false;
                    posresult =  "";
                    md.toast(Activities.this,  "Failed To Reach Server... Please Try Again" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showpossignchange(String direction){

        if ( possuccess){
            ArrayList<String> pores = md.makearrayfromstring(posresult, "~");
            if (pores.size() > 4 & direction.equals("1")){
                Bundle b = new Bundle();
                b.putStringArray("key",  new String[]{"PointOfSaleUser",md.getstring(pores, 0), md.getstring(pores, 1),
                        md.getstring(pores, 2),
                        md.getstring(pores, 3), md.getstring(pores, 4), posenddirection});
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
            else  if (pores.size() > 4 & !direction.equals("1")){

                ArrayList<MyModel.option> values = new ArrayList<>();
                values.add(new MyModel.option("POSUSERID", "", "", ""));
                values.add(new MyModel.option("POSUSER", "", "", ""));
                values.add(new MyModel.option("COMPANYLOCATION", "", "", ""));
                values.add(new MyModel.option("COMPANYLOCATIONNAME", "", "", ""));

                md.sqlupdate(values,  mydatabase);
                finish();
            }
            else{
                showpossavebtn();
                md.toast(Activities.this,  "Failed To Sign in");
            }
        }
        else  {
            showpossavebtn();
            md.toast(Activities.this,  "Failed To Remove From Favourites. Please Try Again" );
        }
    }

    public void showpossavebtn(){
        MaterialButton mb = (MaterialButton) findViewById(possaveid);
        if (mb != null & pbbar != null){
            mb.getLayoutParams().width = (int)(width*0.96);
            pbbar.setVisibility(View.GONE);
        }
    }









    //

    boolean online  = false; int onoffupid = 101, onoffstatusid = 102; boolean onoffbreak = false;
    String onoffcusmeth = "poscustomerupload";
    ArrayList<MyModel.option> onoffsales = new ArrayList<>(), onoffpurchases = new ArrayList<>(), onofftrans = new ArrayList<>();
    public void onoffline(){
        setTitle("Online/Offline Mode");
        online  =  md.getarray(receivearr, 3).equals("1");

        int rh = (int)(height * 0.08), tw = (int)(width * 0.32);
        String ms = "Switch ONLINE mode on if you want your transactions (purchases and sales) to save and upload immediately, or switch to OFFLINE mode if you want to save transactions to your phone now so that you can upload them later.";
        lin.removeAllViews();
        View vw = md.line(Activities.this, width, (int)(height * 0.02), md.transparent);
        lin.addView(vw);

        MaterialTextView tv = md.maketextview(Activities.this, ms,
                "", 0, "Info", ds, md.fontmedium, width, (int)(height * 0.1), true);

        lin.addView(tv);

        boolean swc = true; String ono = "Online";  if (!online){ono = "Offline"; swc = false;}
        LinearLayout ln = new LinearLayout(Activities.this); ln.setLayoutParams(new LinearLayout.LayoutParams(width, rh));
        ln.setOrientation(LinearLayout.HORIZONTAL); lin.addView(ln);
        Switch sw = new  Switch(Activities.this); sw.setLayoutParams(new LinearLayout.LayoutParams(tw, rh));
        sw.setText(ono); sw.setChecked(swc);
        ln.addView(sw); sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                online = b;
                if (b){sw.setText("Online");}else{ sw.setText("Offline");}
            }
        });

        vw = md.line(Activities.this, tw, rh, md.transparent); ln.addView(vw);

        MaterialButton mb = md.makebutton(Activities.this, "Save", "", 0,"Go", ds,
                fontmedium,tw, rh);
        ln.addView(mb);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveonoffline();
            }
        });


        vw = md.line(Activities.this, width, 2, textcolor); lin.addView(vw);
        vw = md.line(Activities.this, width, (int)(height * 0.02), md.transparent); lin.addView(vw);


        ms = "There Are No Transactions To Upload";
        vw = md.line(Activities.this, width, (int)(height * 0.02), md.transparent);
        lin.addView(vw);


        tv = md.maketextview(Activities.this, ms,
                "", onoffstatusid, "Info", ds, md.fontmedium, width, (int)(height * 0.1), true);


        lin.addView(tv);


        String siteref = md.Site;
        if (md.DbName.equals("valeronpro")){ MyModel.option oi = md.valeronuser(mydatabase); siteref = oi.Option2; }

        Cursor rs =  mydatabase.rawQuery(
                "select * from VALOFFLINESALES where SITEREF = '"+siteref+"' and SYNCHED = 0", null );

        while ( rs.moveToNext()) {
            MyModel.option oi = new MyModel.option("", "" , "",  "");
            oi.ID = "VALOFFLINESALES";
            oi.Option1 = rs.getString(rs.getColumnIndex("URL"));
            oi.Option2 = rs.getString(rs.getColumnIndex("CODE"));
            oi.Option3 = rs.getString(rs.getColumnIndex("ORDERNUMBER"));
            oi.Option4 = rs.getString(rs.getColumnIndex("CREATED"));
            oi.Option5 = rs.getString(rs.getColumnIndex("SYNCHED"));
            oi.Option6 = rs.getString(rs.getColumnIndex("TOTAL"));
            oi.Option7 = rs.getString(rs.getColumnIndex("CUSTOMER"));
            onoffsales.add(oi);
        }


        onoffpurchases = new ArrayList<>();
        rs =  mydatabase.rawQuery(
                "select * from VALOFFLINEPURCHASES where SITEREF = '"+siteref+"' and SYNCHED = 0", null );

        while ( rs.moveToNext()) {
            MyModel.option oi = new MyModel.option("", "" , "",  "");
            oi.ID = "VALOFFLINEPURCHASES";
            oi.Option1 = rs.getString(rs.getColumnIndex("URL"));
            oi.Option2 = rs.getString(rs.getColumnIndex("CODE"));
            oi.Option3 = rs.getString(rs.getColumnIndex("ORDERNUMBER"));
            oi.Option4 = rs.getString(rs.getColumnIndex("CREATED"));
            oi.Option5 = rs.getString(rs.getColumnIndex("SYNCHED"));
            oi.Option6 = rs.getString(rs.getColumnIndex("TOTAL"));
          /*  oi.Option7 = rs.getString(rs.getColumnIndex("SUPPLIER"));
            oi.Option8 = rs.getString(rs.getColumnIndex("SUPPLIERNAME"));*/
            onoffpurchases.add(oi);
        }




        for (int i = 0; i < onoffpurchases.size(); i++){
            onofftrans.add(onoffpurchases.get(i));
        }
        for (int i = 0; i < onoffsales.size(); i++){
            onofftrans.add(onoffsales.get(i));
            if (!onoffsales.get(i).Option7.equals("")){
                MyModel.option oi = new MyModel.option("", onoffcusmeth, onoffsales.get(i).Option7, onoffsales.get(i).Option3);
                onofftrans.add(oi);
            }
        }
        if ( onofftrans.size() > 0){
            tv.setText(String.valueOf(String.valueOf(onofftrans.size()) + " Transactions Need To Be Uploaded"));
        }


        mb = md.makebutton(Activities.this, "Upload Now", "", onoffupid,"Go", ds,
                fontmedium,(int)(width * 0.98), rh); lin.addView(mb);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onoffupload();
            }
        });
        pbbar = new ProgressBar(Activities.this); pbbar.setLayoutParams(new LinearLayout.LayoutParams(width, rh));
        lin.addView(pbbar); pbbar.setVisibility(View.GONE);

    }


    public void saveonoffline(){
        String onoff = "1"; if (!online){onoff = "0";}
        Bundle b = new Bundle();
        b.putStringArray("key",  new String[]{"OnOffline", onoff});
        Intent intent = new Intent();
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
        finish();
    }


    public void onoffupload() {

        TextView tv = (TextView) findViewById(onoffstatusid);
        if (tv != null) {
            tv.setText(String.valueOf(onofftrans.size()) + " Transactions Need To Be Uploaded");
        }
        if (onofftrans.size() > 0){
            String vs = onoffsales.get(0).Option2;
            if (onoffsales.get(0).Option1.equals(onoffcusmeth)){vs = onoffsales.get(0).Option3 + "~" + onoffsales.get(0).Option2;}
            onoffup(onofftrans .get(0).Option1, vs,   new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    onoffdone(onofftrans.get(0).ID, onofftrans.get(0).Option3);
                }
            }, 0, Activities.this);
        }else{
            MaterialButton mb = (MaterialButton) findViewById(onoffupid);
            if (mb != null & pbbar != null){
                mb.setEnabled(false);
                pbbar.setVisibility(View.GONE);
            }
            if (tv != null) {
                tv.setText(" Transactions Upload Complete");
            }
        }
    }


    String onoffresult = ""; boolean onoffsuccess = false;
    public void  onoffup(String url, String code,
                         final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        MaterialButton mb = (MaterialButton) findViewById(onoffupid);
        if (mb != null & pbbar != null){
            mb.setEnabled(false);
            pbbar.setVisibility(View.VISIBLE);
        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String vls = md.auth(Activities.this, mydatabase, android_id, uid)+"~"+ code;

        url = "https://www.valeronpro.com/Valeron/"+url+"?code=" +vls ;

        final int attempt = attempts + 1; final String kr = url;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { onoffresult  = response; onoffsuccess = true; callBack.onSuccess(); }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){

                }else{
                    if (mb != null & pbbar != null){
                        mb.setEnabled(true);
                        pbbar.setVisibility(View.GONE);
                    }
                    onoffbreak = true;
                    onoffsuccess = false;
                    onoffresult =  "";
                    md.toast(Activities.this,  "Failed To Reach Server... Please Try Again" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void onoffdone(String table, String ordernum){

        if (!table.equals("")){
            mydatabase.execSQL("UPDATE "+table+" SET SYNCHED = 1 where ORDERNUMBER = '"+ordernum+"'");
        }

        for (int i = 0; i < onofftrans.size(); i++){
            if (onofftrans.get(i).Option3.equals(ordernum)){
                onofftrans.remove(i);
            }
        }

        if ( onoffsuccess ){
            onoffupload();
        }
        else  {
            MaterialButton mb = (MaterialButton) findViewById(onoffupid);
            if (mb != null & pbbar != null){
                onoffbreak = true;
                mb.setEnabled(true);
                pbbar.setVisibility(View.GONE);
            }
            md.toast(Activities.this,  "Failed To Remove From Favourites. Please Try Again" );
        }
    }


























    String rpobjtable = ""; boolean rpviewingreport = false;
    final int rpobjid = 53472, rprunrepid = 64957;
    LinearLayout hislin;


    int rpsdatebt = 37562, rpedatebt = 479934, rpclickeddatebtn = 0;
    String rpstartdate = "", rpenddate = "", rpstatus = "";
    Calendar c = Calendar.getInstance();
    int rpjavayear = c.get(Calendar.YEAR),  rpjavamonth = c.get(Calendar.MONTH), rpjavaday = c.get(Calendar.DAY_OF_MONTH);

    public void myhistory(boolean neww, String table){
        setTitle(md.returndisplay(table) + " History");
        rpobjtable = table;
        rpviewingreport = false;
        int lineheight = (int)(height * 0.08), subpad = 7;
        if (neww){  rpstartdate =""; rpenddate =""; rpstatus = ""; }

        int wd = width - (subpad * 2) ;
        lin.removeAllViews();
        MaterialTextView tv = md.maketextview(Activities.this, "Select Start & End Dates", "", 0, "Info",
                ds, md.fontsmall, wd, (int)(height * 0.08), true);
        lin.addView(tv);

        LinearLayout  lnh = new LinearLayout(Activities.this); lnh.setLayoutParams(new FrameLayout.LayoutParams(width, lineheight));
        lin.addView(lnh); lnh.setOrientation(LinearLayout.HORIZONTAL); lnh.setPadding(4, 0, 4, 0);

        int topwidth = (int) (wd * 0.325);
        MaterialButton t = md.makebutton(Activities.this, "Start Date", "", rpsdatebt, "General",
                ds, md.fontsmall, topwidth, lineheight);

        lnh.addView(t);
        t.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {
            rpclickeddatebtn = rpsdatebt;   DatePickerDialog d = new DatePickerDialog(Activities.this,
                    R.style.Widget_AppCompat_ActionBar_Solid, rpmDateSetListener, rpjavayear, rpjavamonth, rpjavaday);
            d.show(); } });

        MaterialButton b = md.makebutton(Activities.this, "End Date", "", rpedatebt, "General",
                ds, md.fontsmall, topwidth, lineheight);

        lnh.addView(b);
        b.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {
            rpclickeddatebtn = rpedatebt;  DatePickerDialog d = new DatePickerDialog(Activities.this,
                    R.style.Widget_AppCompat_ActionBar_Solid, rpmDateSetListener, rpjavayear, rpjavamonth, rpjavaday);
            d.show(); } });

        int scid = rpedatebt+10;
        MaterialButton m = md.makebutton(Activities.this, "Search", "", scid, "Go",
                ds, md.fontsmall, topwidth, lineheight);
        lnh.addView(m);
        m.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {
            runreport(table); } });

        NestedScrollView scrl = new NestedScrollView(Activities.this); scrl.setId(scrlid);
        scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.82))) ;


        lin.addView(scrl); scrl.setPadding(subpad, 4, subpad, 4);

        hislin = new LinearLayout(Activities.this);
        hislin.setOrientation(LinearLayout.VERTICAL); scrl.addView(hislin);
    }


    String selecctortype = "";

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int yr, int mn, int dy) {

            String ss = "Start: ";
            MaterialButton BD = (MaterialButton) Activities.this.findViewById(rpclickeddatebtn);
            ArrayList<String> cs = md.correctdate(yr, mn, dy);
            BD.setText(md.getstring(cs, 0) + "-" +md.getstring(cs, 1)+ "-" + md.getstring(cs, 2));
            BD.setTag(md.getstring(cs, 0) + "-" +md.getstring(cs, 1)+ "-" + md.getstring(cs, 2));
            if (rpclickeddatebtn == rpsdatebt){
                rpstartdate = md.getstring(cs, 0) + "-" +md.getstring(cs, 1)+ "-" + md.getstring(cs, 2);
            }
            else{ss = "End: "; rpenddate= md.getstring(cs, 0) + "-" +md.getstring(cs, 1)+ "-" + md.getstring(cs, 2);}
            BD.setText(ss +md.getstring(cs, 0) + "-" +md.getstring(cs, 1)+ "-" + md.getstring(cs, 2));

        }
    };

    int rpmyear, rpmMonth, rpmDay, rpmYear; String transDateString;
    DatePickerDialog.OnDateSetListener rpmDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            rpmYear = year;
            rpmMonth = monthOfYear;
            rpmDay = dayOfMonth;
            updateDate();
        }
    };

    private void updateDate() {
        GregorianCalendar c = new GregorianCalendar(rpmYear, rpmMonth, rpmDay);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");

        sdf = new SimpleDateFormat("yyyy-MM-dd");

        String ss = "Start: ";
        if (rpclickeddatebtn == rpsdatebt){
            rpstartdate  = sdf.format(c.getTime()); ss = ss + rpstartdate;
        }
        else{rpenddate  = sdf.format(c.getTime()); ss = "End: " + rpenddate;}

        MaterialButton BD = (MaterialButton) Activities.this.findViewById(rpclickeddatebtn);
        BD.setText(ss);

    }

    public void runreport(String table){

        if (rpstartdate.equals("")){
            md.toast(Activities.this, "Please Select A Start Date");
        }else  if (rpenddate.equals("")){
            md.toast(Activities.this, "Please Select An End Date");
        }
        else {
            hislin.removeAllViews();

            MaterialTextView mt = md.maketextview(Activities.this, "Loading History... Please Wait", "", 0, "Info", ds, md.fontmedium,
                    width, (int)(height * 0.1), true);

            hislin.addView(mt);

            ProgressBar pb = new ProgressBar(Activities.this);
            hislin.addView(pb);

            rpparams = md.auth(Activities.this, mydatabase, android_id, uid) +"~" +rpstartdate +"~" +rpenddate;
            if (!rpstatus.equals("")){  rpparams += "~" +rpstatus; }

            getattreport(table, new MainActivity.VolleyCallBack() {
                @Override public void onSuccess() {  showreport(table);  } }, 0, Activities.this);
        }
    }

    String rpparams ="";
    public void getattreport( String table, final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String mth = "gethistory";
        if (table.equals("PurchaseOrders")){mth = "getpurchasehistory";}

        String url = md.url + mth +"?code=" +  rpparams;
        final int attempt = attempts + 1;

        md.clipboard(this, url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { rpfleetstring = response; rpfleetSuccess = true; callBack.onSuccess(); }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){

                    getattreport( table, callBack,  attempt, cnt);
                }else{
                    rpfleetstring =  "Error: " + error.getMessage();
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");
                    myhistory(false, rpobjtable);
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    final ArrayList<Integer> rpovids = new ArrayList<>();
    List<String> rpreporttypes = new ArrayList<String>();
    String rpfleetstring =  ""; boolean  rpfleetSuccess =  false; final int rpreplinid = 375362;
    public void showreport(String table){

        if (rpfleetstring.indexOf("|") > -1 & hislin != null ){

            finalattendancetable(table);
        }
        else{
            String errormessage = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            // md.toast(Activities.this, errormessage);
            //  md.toast(Activities.this, rpfleetstring);
            //  rpstartoff();
        }
    }

    ArrayList<String> logtbs = new ArrayList<>(), cols = new ArrayList<>();
    ArrayList<MyModel.option> orders = new ArrayList<>(); int prelog = 90543, linelog = 10113;

    public void finalattendancetable(String table){

        hislin.removeAllViews();
        int lineheight = (int) (height * 0.08);
        hislin.setPadding(0, 0, 0, (int) (height * 0.1));
        logtbs = md.makearrayfromstring(rpfleetstring, "|");

        String sub = "CustomerOrderItems";
        if (table.equals("PurchaseOrders")){sub = "PurchaseOrderItems";}
        String subb = sub;
        if (logtbs.size() <= 1){
            md.toast(Activities.this, "Error Loading Report Data");
            myhistory(false, rpobjtable);
        }
        else if (logtbs.size() > 1){

            ArrayList<String> tb1  =  md.makearrayfromstring(md.getstring(logtbs, 0), "¬");
            cols = md.makearrayfromstring(md.getstring(tb1, 1), "~");

            for (int t = 0; t <logtbs.size(); t++){

                String datestring = "Created"; if (t > 0){datestring = "StartDate";}
                ArrayList<String> tb  =  md.makearrayfromstring(md.getstring(logtbs, t), "¬");

                for (int i = 2; i < tb.size(); i++){
                    ArrayList<String> row = md.makearrayfromstring(md.getstring(tb, i), "~");
                    ArrayList<String> thisrow = md.makearrayfromstring(md.rowstring(cols, row), "|");

                    String type = md.rowoption(thisrow, "TransactionType");
                    String orderid = md.rowoption(thisrow, "ValueOrder");
                    String ordername = md.rowoption(thisrow, "Order");
                    String username = md.rowoption(thisrow, "User");
                    String created = md.fixdateortime(md.rowoption(thisrow, datestring));
                    String mincreated = md.rowoption(thisrow, "Minute" + datestring);
                    boolean addit = true;
                    for (int r = 0; r < orders.size(); r++){
                        if (md.getoption(orders, r).ID.equals(orderid)){
                            addit = false;
                        }
                    }
                    if (addit){
                        MyModel.option oi = new MyModel.option(orderid, type + " " +created + ": "+username + " (" +ordername+")" , created, "0");
                        oi.Value = md.parsefloat(mincreated);
                        orders.add(oi);
                    }
                }
            }


            orders = md.sortArray(orders);

            int bigpad = 10, subpad = 7;
            final int reprelid = 31494, wd = width - (subpad * 2) - bigpad;
            LinearLayout  lnl = new LinearLayout(Activities.this);
            for (int s = (orders.size() - 1); s >= 0; s--){
                final int topleftt = 70325 + s + 400, topmidtv = 80325 + s + 43;
                lnl = new LinearLayout(Activities.this); lnl.setId(reprelid); lnl.setOrientation(LinearLayout.HORIZONTAL);
                lnl.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.1)));
                hislin.addView(lnl);
                MaterialButton mb = new MaterialButton(Activities.this);  mb.setHeight(lineheight);
                mb.setText(md.getoption(orders, s).Option1); mb.setLines(2);
                mb.setWidth((int)(0.85 *  wd)); mb = md.buttondesign(Activities.this, mb, "Info", ds);
                mb.setId(topleftt);  lnl.addView(mb); mb.setTextSize(md.fontsmall);
                final String orderid = md.getoption(orders, s).ID;
                mb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doactions(orderid,
                                table,  "",
                                subb, rpfleetstring);
                    }
                });

                int rightwidth = (int)(0.14 *  wd);

                mb = new MaterialButton(Activities.this);
                mb.setLayoutParams(new LinearLayout.LayoutParams(rightwidth, ViewGroup.LayoutParams.MATCH_PARENT));
                mb.getLayoutParams().height = (int)(lineheight*0.8);

                mb.setWidth(rightwidth); mb = md.buttondesign(Activities.this, mb, "Info", ds);
                mb.setId(topmidtv);  lnl.addView(mb); mb.setTextSize(md.fontsmall);
                mb.setBackgroundResource(R.drawable.ic_baseline_article_24);
                mb.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) { expandorder(orderid); }
                });

                ArrayList<String> itemsadded = new ArrayList<>();
                for (int t = 0; t <logtbs.size(); t++){
                    String datestring = "Created"; if (t > 0){datestring = "StartDate";}
                    ArrayList<String> tb  =  md.makearrayfromstring(md.getstring(logtbs, t), "¬");

                    final String tablestring = md.getstring(logtbs, t);
                    for (int i = 2; i < tb.size(); i++){
                        ArrayList<String> row = md.makearrayfromstring(md.getstring(tb, i), "~");
                        ArrayList<String> thisrow = md.makearrayfromstring( md.rowstring(cols, row), "|");
                        String rowid = md.rowoption(thisrow, "ID");
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

                        final int toprighttv = prelog + md.parseint(rowid), bordl =  linelog  +  md.parseint(rowid);

                        if (myorderid.equals(orderid) & !itemsadded.contains(rowid)){
                            itemsadded.add(rowid);
                            if (!itemnumber.equals("")){itemnumber = " (" + itemnumber + ")";}
                            String xs = itemnumber;

                            if (itemtype.equals("2")){ xs = " (" + st + " - " + et + ")";
                            }else   if (itemtype.equals("1")){  xs = " (" + itemnumber + ")"; }

                            MaterialTextView mt  = md.maketextview(Activities.this, "", "", toprighttv,
                                    "Info", ds, md.fontsmall,  wd, lineheight, true);
                            hislin.addView(mt); mt.setVisibility(View.GONE);

                            String vt  = itemname +  xs;
                            if (itemtype.equals("2")){
                                mt.setLines(2);  vt = vt + System.lineSeparator() + exx;
                                mt.setHeight((int)(lineheight*2)); }
                            else  if (itemtype.equals("1")){
                                mt.setLines(2);  vt = vt + System.lineSeparator() + quantity + " X " + MainActivity.currencysymbol + price + " = " + total;
                                mt.setHeight((int)(lineheight*2)); }

                            mt.setText(vt);
                            mt.setOnClickListener(   new View.OnClickListener() {  @Override
                            public void onClick(View view) {
                                doactions( rowid, subb, tablestring, "", "" ); }
                            } );
                            View vw = new View(Activities.this); vw.setLayoutParams(new LinearLayout.LayoutParams( wd-rightwidth, 2));
                            vw.setBackgroundColor(md.lightgray); hislin.addView(vw); vw.setVisibility(View.GONE); vw.setId(bordl);
                        }
                    }
                }



            }
        }
    }

    public void expandorder(String orderid){

        int visibility  = View.GONE;
        String newval = "0";
        for (int r = 0; r < orders.size(); r++){
            if (md.getoption(orders, r).ID.equals(orderid)){
                if (md.getoption(orders, r).Option3.equals("0")){
                    newval = "1";
                    visibility  = View.VISIBLE;
                } else  if (md.getoption(orders, r).Option3.equals("1")){
                    newval = "0";
                    visibility  = View.GONE;
                }
                md.getoption(orders, r).Option3 = newval;
            }
        }


        for (int t = 0; t <logtbs.size(); t++) {

            ArrayList<String> tb = md.makearrayfromstring(md.getstring(logtbs, t), "¬");

            for (int i = 2; i < tb.size(); i++) {
                ArrayList<String> row = md.makearrayfromstring(md.getstring(tb,i), "~");
                ArrayList<String> thisrow = md.makearrayfromstring(md.rowstring(cols, row), "|");
                String rowid = md.rowoption(thisrow, "ID");
                String myorderid = md.rowoption(thisrow, "ValueOrder");
                final int toprighttv = prelog + md.parseint(rowid), brdl  =linelog + md.parseint(rowid);

                if (myorderid.equals(orderid)) {
                    MaterialTextView mt = Activities.this.findViewById(toprighttv);
                    if (mt != null){   mt.setVisibility(visibility);  }
                    View vw = Activities.this.findViewById(brdl);
                    if (vw != null){vw.setVisibility(visibility);}
                }
            }
        }
    }

    public void doactions( String rowid, String tablename, String tablestring, String subtablename, String subtablestring){

        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ ds, uid, Executor,tablename,
                rowid,tablestring, subtablename, subtablestring});
        Intent intent = new Intent(Activities.this.getApplicationContext(), Orders.class);
        intent.putExtras(b);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }


















    ArrayList<MyModel.option> dynamicols  = new ArrayList<>();
    String dynamiccolval = "", dnrowid = "0", dnpre = "1010", dynamictable = "";int dynamicsaveid = 131, dynamiclinid = 132;
    public void dynamicadd(){
        lin.removeAllViews();
        setTitle(md.returndisplay(dynamictable));

        int hw  = (int)(width * 0.5);
        MaterialTextView mt = md.maketextview(Activities.this, "Loading "+md.returndisplay(dynamictable)
                , "", 0, "Info", ds, fontmedium,  width, (int)(height * 0.1), true);  lin.addView(mt);

        ProgressBar pbbar = new ProgressBar(Activities.this); pbbar.setId(pbarid);
        pbbar.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(height * 0.01)));
        lin.addView(pbbar);pbbar.setVisibility(View.VISIBLE);
        dngettable(   new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                dnshowtable();
            }
        }, 0, Activities.this);

    }


    String dnresult = ""; boolean dnsuccess = false;
    ArrayList<MyModel.option> dncols = new ArrayList<>();
    public void dngettable( final MainActivity.VolleyCallBack callBack, int attempts, Context cnt)
    {
        RequestQueue queue = Volley.newRequestQueue(cnt);
        final int attempt = attempts + 1;
        String mm = md.auth(Activities.this, mydatabase,  android_id, uid)+"~"+dynamictable + "^|ID^=^" + dnrowid;
        String url = md.url + "gettable?code=" + mm;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dnresult  = response;
                        dnsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dnsuccess = false;
                dnresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    dngettable(   callBack,  attempt, cnt);
                }else{
                    dnresult =  "Error: " + error.getMessage();
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }


    public void dnshowtable(){

        if (dnsuccess  & lin != null){
            lin.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(width * 0.99), (int)(height * 0.88));
            NestedScrollView scrl = new NestedScrollView(Activities.this); scrl.setLayoutParams(lp);
            scrl.setLayoutParams(lp) ;
            lin.addView(scrl);

            LinearLayout lns = new LinearLayout(Activities.this); lns.setId( dynamiclinid );
            lns.setLayoutParams(lp);
            lns.setOrientation(LinearLayout.VERTICAL); scrl.addView(lns);

            ArrayList<String> list  =  md.makearrayfromstring(dnresult, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");
            ArrayList<String> dcols = md.makearrayfromstring(md.getstring(rules, 16), "~");

            dncols.clear(); ArrayList<String> cos = new ArrayList();
            for (int i = 0; i < dcols.size(); i++){
                String tb = md.before(md.getstring(dcols, i), "^");
                if (tb.equals(dynamictable)){
                    String ccs = md.breakurl(md.getstring(dcols, i), 1, "^");
                    cos = md.makearrayfromstring(ccs, "-");

                    int  lineheight = (int)(height * 0.08), fw = (int)(width * 0.43), iw = (int)(width * 0.55);
                    for (int s = 0; s < cos.size(); s++){
                        String checkinput = "", col = md.getstring(cos, s);
                        if (col.contains("_")){
                            checkinput = "1"; col = col.substring(0, col.indexOf("_"));
                        }

                        int dnid = md.parseint(dnpre + String.valueOf(s));
                        String dt = "";
                        for (int g = 0; g < cols.size(); g++){
                            if (md.getstring(cols, g).contains("^")){
                                if (md.before(md.getstring(cols, g), "^").equals(col)){
                                    dt = md.breakurl(md.getstring(cols, g), 1, "^");
                                }
                            }
                        }
                        dncols.add(new MyModel.option(String.valueOf(dnid), col, dt, checkinput));
                        String dsp = md.returndisplay(col), vl= "";

                        LinearLayout lastlin = new LinearLayout(Activities.this);
                        lastlin.setLayoutParams(new LinearLayout.LayoutParams(width, lineheight));
                        lastlin.setOrientation(LinearLayout.HORIZONTAL); lns.addView(lastlin);

                        MaterialTextView mt = md.maketextview(Activities.this, md.returndisplay(col),
                                "", 0, "Info", ds, fontsmall, fw, lineheight, true);
                        lastlin.addView(mt);

                        if (dt.equals("nvarchar")){
                            String it = "";if (col.equals("Cell") || col.equals("Pin")){it = "Number";}
                            EditText ed = md.edittext(Activities.this, dnid,dsp, vl, it, iw, lineheight,
                                    fontmedium, ds);
                            lastlin.addView(ed);
                        }
                        else   if (dt.equals("bigint")){
                            if (vl.equals("")){vl = dsp;}
                            MaterialButton mb =  md.makebutton(Activities.this,vl, "", dnid, "General", ds,
                                    md.fontmedium, iw, lineheight);
                            String cl = col, dtp  = dt;
                            mb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loadpopup(dsp, dynamictable, dnid, cl, dtp,false);
                                }
                            });
                            lastlin.addView(mb);
                        }
                        else  if (dt.equals("datetime")){
                            EditText ed = md.edittext(Activities.this, dnid,dsp, vl, "Date", iw, lineheight,
                                    fontmedium, ds);
                            lastlin.addView(ed);
                        }
                        else if (dt.equals("money")
                                || dt.equals("int")
                                || dt.equals("float")){
                            EditText ed = md.edittext(Activities.this, dnid,dsp, vl, "Number", iw, lineheight,
                                    fontmedium, ds);
                            lastlin.addView(ed);
                        }
                        else if (dt.equals("bit")){
                            if (vl.toLowerCase().equals("true")){vl = "1";}
                            else{vl = "0";}
                            CheckBox cb =   md.checkbox(Activities.this,  dsp, vl, md.lightgray,
                                    md.white, dnid, fontmedium,   iw, lineheight); lastlin.addView(cb);
                        }
                    }


                    LinearLayout lastlin = new LinearLayout(Activities.this);
                    lastlin.setLayoutParams(new LinearLayout.LayoutParams(width, lineheight));
                    lastlin.setOrientation(LinearLayout.HORIZONTAL);
                    lin.addView(lastlin);
                    int lastwidth = (int)(width * 0.98);

                    pbbar = new ProgressBar(Activities.this); pbbar.setVisibility(View.GONE);
                    lastlin.addView(pbbar); pbbar.setLayoutParams(new LinearLayout.LayoutParams(lastwidth, lineheight));

                    MaterialButton savebtn =  md.makebutton(Activities.this,"Save", "", dynamicsaveid, "Go", ds,
                            md.fontmedium, lastwidth, lineheight);
                    savebtn.setOnClickListener(new View.OnClickListener() {
                        @Override  public void onClick(View view) { dnsave();    }  });
                    lastlin.addView(savebtn);

                    break;
                }
            }
        }
        else  {
            md.toast(Activities.this, " Error, please try again");
        }
    }


    public  void dnsave(){
        String err = "", vals = "";
        for (int i = 0; i < dncols.size(); i++){
            int id  = md.parseint(dncols.get(i).ID);

            String dt = md.getoption(dncols, i).Option2, ck = md.getoption(dncols, i).Option3,
                    cl = md.getoption(dncols, i).Option1,
                    vl = "";

            if (dt.equals("bigint")){
                MaterialButton mb =  (MaterialButton ) findViewById(id);
                vl = mb.getTag().toString();
            }
            else if (dt.equals("bit")){
                CheckBox cb = (CheckBox) findViewById(id);
                vl = "0"; if (cb.isChecked()){vl = "1";}
            }
            else {
                EditText ed = (EditText) findViewById(id);
                vl = ed.getText().toString();
            }

            String nochar = md.checknochars(md.returndisplay(cl), vl);
            vals += "|" + cl+ "^" + vl;
            if (ck.equals("1") & vl.equals("")){
                err += System.lineSeparator() + "Please Enter A Value For " + md.returndisplay(cl);
            }else if (!nochar.equals("")){
                err +=  System.lineSeparator() +nochar;
            }
        }
        if (err.equals("")){
            if (!dynamiccolval.equals("")){
                vals += "|" + dynamiccolval;
            }

            String dnd = "";
            if (md.parseint(dnrowid) > 0){dnd = dnrowid;}
            dynamicset( dnd, vals,    new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    dynamicdone();
                }
            }, 0, Activities.this);
        }else{
            md.toast(Activities.this, err);
        }


    }


    public void dynamicset( String rowid,  String values, final MainActivity.VolleyCallBack callBack,
                            int attempts, Context cnt) {
        MaterialButton savebtn = (MaterialButton) findViewById(dynamicsaveid);

        if (savebtn != null){
            savebtn.setEnabled(false);

            md.toast(Activities.this, "Saving "+md.returndisplay(dynamictable));
            if (pbbar != null){ pbbar.setVisibility(View.VISIBLE);}
        }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String url = md.url + "multiset?code="+md.auth(Activities.this, mydatabase, android_id, uid)+"~"+dynamictable+"~"+rowid+"~"+values;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        msresult = response;
                        mssuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mssuccess = false;
                msresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    if (!rowid.equals("")){
                        dynamicset(  rowid,   values, callBack,  attempt, cnt);
                    }else{
                        msresult =  "";
                        md.toast(Activities.this, "Failed To Reach Server... Please Try Again");
                    }
                }else{
                    msresult =  "";
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void  dynamicdone(){

        MaterialButton savebtn = (MaterialButton) findViewById(dynamicsaveid);
        if (savebtn != null){savebtn.setEnabled(true);
            if (pbbar != null){ pbbar.setVisibility(View.GONE);}
        }

        if (mssuccess ){
            md.toast(Activities.this, "Saved!");
            ArrayList<String> list  =  md.makearrayfromstring(msresult, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            if (list.size() > 2){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, 2), "~");
                String idd =  md.before(md.getstring(list, 2), "~");

                String rs = "|ID^" + idd;
                if (dynamictable.equals("Items")){
                    rs += "|Name^" + md.coloption(cols, "Name", row).Value;
                    rs += "|ItemNumber^" + md.coloption(cols, "ItemNumber", row).Value;
                    rs += "|SellingPrice^" + md.coloption(cols, "SellingPrice", row).Value;
                    rs += "|PurchasePrice^" + md.coloption(cols, "PurchasePrice", row).Value;
                    synchproducts( new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() {  finalsynchproducts(); } }, 0);
                }else if (dynamictable.equals("Users")){
                    rs += "|Name^" + md.coloption(cols, "Name", row).Value;
                    rs += "|Surname^" + md.coloption(cols, "Surname", row).Value;
                    rs += "|Cell^" + md.coloption(cols, "Cell", row).Value;
                    rs += "|Email^" + md.coloption(cols, "Email", row).Value;
                }
                else if (dynamictable.equals("Brands") || dynamictable.equals("Categories") || dynamictable.equals("Organizations")){
                    rs += "|Name^" + md.coloption(cols, "Name", row).Value;
                }

                Bundle b = new Bundle();
                b.putStringArray("key", new String[]{"DynamicAdd", dynamictable, rs});
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();


            }

        }
        else  { md.toast(Activities.this, " Error, please try again");  }
    }























    boolean fkSuccess = false; String fkresult = "", fkcol = "", fkid = "", fktb = "", fktitle = ""; int fkcontrol = 0;
    public void loadpopup(String title, String table, int controlid, String col, String datatype, boolean istime) {
        fkcontrol = controlid;
        if (datatype.equals("bigint")){
            fkcol = col;  fktb  = table;  fktitle = title;
            md.toast(Activities.this, "Loading " + md.returndisplay(col) + " Options" );

            getfks(table, col,  new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    showfks();
                }
            }, 0, Activities.this);

        }else  if (datatype.equals("datetime") & !istime){
            showDialog(111);
        } else  if (istime){
            showDialog(333);
        }
    }


    public void getfks(String table, String col, final MainActivity.VolleyCallBack callBack,
                       int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "fklist";

        url = url + "?code="+md.auth(Activities.this, mydatabase,android_id, uid)+"~"+table+"~"+col;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fkresult = response;
                        fkSuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){
                    getfks( table, col, callBack,  attempt, cnt);
                }else{
                    fkSuccess = false;
                    fkresult =  "";
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    ArrayList<String> fkviewlist = new ArrayList<>();
    ArrayList<String> fkidlist = new ArrayList<>();
    public void showfks(){
        if (fkSuccess  & fkidlist != null){
            fkidlist.clear();
            fkviewlist.clear();
            ArrayList<String> jj =  md.makearrayfromstring(fkresult, "¬");
            for (int i = 0; i < jj.size(); i++)
            {
                String id = md.before(jj.get(i),  "~");
                String vl = md.breakurl(jj.get(i), 1, "~");
                fkviewlist.add(vl);
                fkidlist.add(id);
            }
            showDialog(444);
        }
        else  {
            md.toast(Activities.this, " Error, please try again");
        }
    }





    @Override
    protected Dialog onCreateDialog(int id) {

        //   dialogcancel();
        // TODO Auto-generated method stub
        android.app.AlertDialog.Builder  builder = new android.app.AlertDialog.Builder(this);

        if (id == 444){
            LinearLayout ll = new LinearLayout(Activities.this);
            ll.setOrientation(LinearLayout.VERTICAL);
            final ListView lv = new ListView(Activities.this);
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(Activities.this,
                    android.R.layout.simple_list_item_1,  fkviewlist );
            lv.setAdapter(itemsAdapter); ll.addView(lv);
            builder.setView(ll);  builder.setTitle(fktitle);

            AlertDialog dialog = builder.create();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,  long id) {
                    try{
                        fkid = md.getstring(fkidlist, position);
                        String fkdisplay = md.getstring(fkviewlist, position);
                        MaterialButton mb = (MaterialButton) findViewById(fkcontrol);
                        if (mb != null){ mb.setTag(fkid);  mb.setText(fkdisplay); }
                        dialog.cancel();

                    }catch(Exception c){
                       md.toast(Activities.this, "ERROR: " + c.getMessage());
                    }

                }
            });


            return  dialog;

        }

        return null;
    }





    String sncstring; boolean sncSuccess;
    public void synchproducts(final MainActivity.VolleyCallBack callBack, int attempt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(Activities.this);
        String url = md.url + "gettable?code="+md.auth(Activities.this, mydatabase, android_id, uid)+"~Items";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        sncstring = response;
                        sncSuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (attempt < md.attempts){
                    synchproducts(  callBack,  attempt);
                }else{
                    md.toast(Activities.this, "Failed To Reach Server... Retrying");

                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);
    }

    public void finalsynchproducts(){
        // Display the first 500 characters of the response string.
        if (!sncSuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(Activities.this, z);
        }
        else if(sncSuccess ) {
            md.synchproducts(sncstring, mydatabase);
            md.toast(Activities.this, "Items Synchronized Successfully");
        }
    }

}





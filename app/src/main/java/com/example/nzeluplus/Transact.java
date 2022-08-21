package com.example.nzeluplus;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;


import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Transact extends AppCompatActivity {
    ArrayList<MyModel.option> controls  = new ArrayList<>(); LinearLayout mainlin, ln;
    String android_id = "", responsestring = "",  table = "Carts", exn = "";
    int width = 0, height = 0; boolean dataSuccess = false;  MyModel md = new MyModel();
    ArrayList<MyModel.CADT> columns = new ArrayList();
    public static final String EXTRA_MESSAGE = "com.example.android.twoactivities.extra.MESSAGE";
    final int pbarid =  2534, addbtnid =  3239643, savebtnid = 31759269,
            scrlid = 9983241, locbtnid = 55729932; String uid; String[] rec;
    ArrayList<String>   list = new ArrayList<>();
    boolean edit = false, smallscreen =  false,
            showenddate = false, showexec = false, pos = false;

    String DS = MainActivity.DisplayString, posuser = "",poscustomer = "", poscusname = "", poscellnum = "",
            transtype = "", Executor = "", transactionid = md.randomstring(), storename = "", online = "1",
            companyname = "", thankyou = "", contacts = "", tpin = "";

    int fontsmall = md.fontsmall, fontmedium = md.fontmedium;

    ArrayList<MyModel.CartItem> cartitems = new ArrayList<>();
    SQLiteDatabase mydatabase;

    ProgressBar cusload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transact);
        android_id = Settings.Secure.getString(Transact.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        mainlin = (LinearLayout) findViewById(R.id.mainlinear);
       // mainlin.setBackgroundColor(md.white);
        mainlin.setPadding(10, 0, 10, 5);

        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        MyModel md = new MyModel();
        DisplayMetrics metrics = new DisplayMetrics();
        (Transact.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;  width = metrics.widthPixels;
        if (height <= md.heightcut){height = (int)(height * 0.91); smallscreen = true; fontsmall -= 1; fontmedium = md.fontsmall;}
        width = width - 22;

        ProgressBar pbbar = new ProgressBar(Transact.this);
        pbbar.setId(pbarid);
        pbbar.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.07)));
        mainlin.addView(pbbar);
        pbbar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        rec = b.getStringArray("key");
        uid = md.getarray(rec, 0); // the user reference number
        Executor = md.getarray(rec, 1);
        DS = md.getarray(rec, 2);

        pos = md.getarray(rec, 3).toLowerCase().equals("true");
        CompanyLocation = md.getarray(rec, 4);
        currencysymbol= md.getarray(rec, 5);
        currencycode= md.getarray(rec, 6);
        posuser = md.getarray(rec, 7);
        online = md.getarray(rec, 8);

        mainlin.removeAllViews();
        setTitle("My Cart");

        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);

startoff();
    }


    public void startoff(){

        LinearLayout toplin = new LinearLayout(Transact.this); mainlin.addView(toplin);
        toplin.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.08)));

        boolean promos = md.appsetting("AddPromotionalCodes", DS).toLowerCase().equals("true");
        transtype = "1";
        int topwidth = (int)(width * 0.5); if (promos){topwidth = (int)(width *0.33);}
        MaterialButton bt = new MaterialButton(this); bt.setId(addbtnid); bt.setText("Add " + MainActivity.itemname);
        bt.setWidth(width); toplin.addView(bt); bt = md.buttondesign(Transact.this,bt, "Go", DS);
        bt.setLayoutParams(new LinearLayout.LayoutParams(topwidth, (int) (height * 0.08)));
        bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) { addpopup(); } });
        if (promos){
            bt = new MaterialButton(this); bt.setId(locbtnid); bt.setText("Promo Code");
            bt.setLayoutParams(new LinearLayout.LayoutParams(topwidth, (int) (height * 0.08)));
            bt.setWidth(topwidth); toplin.addView(bt);  bt = md.buttondesign(Transact.this, bt, "Warning", DS); bt.setTag("");
            bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {
            } });
        }

        String lastbtntitle = "Set Location";

        bt = new MaterialButton(this); bt.setId(locbtnid); bt.setText(lastbtntitle );
        bt.setLayoutParams(new LinearLayout.LayoutParams(topwidth, (int) (height * 0.08)));
        bt.setWidth(topwidth); toplin.addView(bt);  bt = md.buttondesign(Transact.this,bt, "General", DS); bt.setTag("");
        bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {
            gotoaddresses(false);
        } });

        NestedScrollView scrl = new NestedScrollView(Transact.this); scrl.setId(scrlid);
        scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.75))) ;
        mainlin.addView(scrl); scrl.setPadding(0, 4, 0, 4);
        setTitle("My Cart");

        ln = new LinearLayout(Transact.this);
        ln.setOrientation(LinearLayout.VERTICAL); scrl.addView(ln);

        ProgressBar pb = new ProgressBar(Transact.this); pb.setId(pbarid);pb.setVisibility(View.GONE);
        mainlin.addView(pb);

        int htt = (int) (height * 0.11);
        LinearLayout botlin = new LinearLayout(Transact.this); mainlin.addView(botlin);
        botlin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  htt));

        MaterialTextView tv = md.maketextview(Transact.this, "", "", totalid, "Info", DS, fontmedium,
                (int) (width * 0.5), htt, true);botlin.addView(tv);


        bt = new MaterialButton(this); bt.setId(savebtnid); bt.setText("Place " + MainActivity.ordername);
        bt.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.5), htt));
        botlin.addView(bt);  bt = md.buttondesign(Transact.this,bt, "Go", DS);
        bt.setEnabled(false); bt.setTextSize(fontmedium);
        bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) { gosave();  } });
        showenddate = md.appsetting("IncludeEndDateInOrderItems", DS).toLowerCase()
                .equals("true");

        showexec = md.appsetting("ChooseExecutorOnOrders", DS).toLowerCase()
                .equals("true");

        exn = md.appsetting("ExecutorName", DS);

        loading("Loading Your Cart... Please Wait");

        servergetcart( new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() { showloaded(); }
        }, 0, Transact.this);
    }



    public interface VolleyCallBack { void onSuccess(); }

    public void loading(String message){

        ln.removeAllViews();

        MaterialTextView tv =  md.maketextview(Transact.this,
                message,
                "", 0,  "Info",
                DS, md.fontlarge, width, (int)(height * 0.07), true);  ln.addView(tv);

        ProgressBar pbj = new ProgressBar(Transact.this);
        pbj.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.07)));
        ln.addView(pbj); pbj.setVisibility(View.VISIBLE);

        MaterialButton bt = (MaterialButton) findViewById(savebtnid);
        if (bt != null){bt.setEnabled(false);}

    }

    String currencysymbol = "", currencycode = "", locationid = "", locv = "", shipperid = "", isshipping = "0", CompanyLocation = "";
    public void servergetcart( final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "appgetcart?code="+md.auth(Transact.this, mydatabase, android_id, uid);

        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responsestring = response;
                        dataSuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){ servergetcart(  callBack,  attempt, cnt);
                }else{
                    dataSuccess = false;
                    responsestring =  "Error: " + error.getMessage();
                    md.toast(Transact.this, responsestring);
                }
            }
        });

        stringRequest = md.requestwait(stringRequest); queue.add(stringRequest);

    }


    ArrayList<String> tables = new ArrayList<>(), cart  = new ArrayList<>(), cartcols = new ArrayList<>(), cartrules = new ArrayList<>();
    public void showloaded(){

        if (dataSuccess & responsestring.indexOf("¬") > -1){

            tables =  md.makearrayfromstring(responsestring, "|");

            cart = md.makearrayfromstring( md.getstring(tables, 1), "¬");
            cartrules = md.makearrayfromstring( md.getstring(cart, 0), "#");
            cartcols = md.makearrayfromstring( md.getstring(cart, 1), "~");

            ArrayList<String> curray = md.makearrayfromstring(md.getstring(cartrules, 15),"~");

            for (int i = 0; i < curray.size(); i++){
                String col = md.before(md.getstring(curray, i), "^"), vl = md.breakurl(md.getstring(curray, i), 2, "^");
                if (col.equals("CurrencySymbol")){currencysymbol = vl;}
                else if (col.equals("CurrencyCode")){currencycode = vl;}
                else if (col.equals("TaxpayerIdentificationNumber")){tpin= vl;}
                else if (col.equals("ContactNumbers")){contacts= vl;}
                else if (col.equals("OrderThankYouMessage")){thankyou= vl;}
                else if (col.equals("Name")){companyname = vl;}
                else if (col.equals("PointOfSaleOn")){pos = vl.toLowerCase().equals("true")
                        & (Executor.equals("1") || Executor.equals("2"));}
            }


            ArrayList<String> site = md.makearrayfromstring(md.getstring(tables, 0), "¬");
            ArrayList<String> sitecols = md.makearrayfromstring(md.getstring(site, 1), "~");

            for (int c = 2; c < site.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(site, c), "~");
                String idd =  md.before(md.getstring(site, c), "~");
                storename =  md.coloption(sitecols, "Name", row).Value;
            }

            showcart();
        }
        else  {
            md.toast(Transact.this,  "Failed To Load Cart: "+ responsestring);
        }
    }




    int totalid = 843952;
    int bigpad = 5, vpad = 5, hpad = 7;
    public void showcart(){
        ln.removeAllViews(); ln.setPadding(bigpad , 0, bigpad , 0);
        if (ln != null){
            MaterialButton bt = (MaterialButton) findViewById(savebtnid);
            if (bt != null){bt.setEnabled(true);}
            if (cart.size() == 2){


                TextView tv = new TextView(Transact.this); ln.addView(tv);
                tv.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.2)));
                String th ="There Are No Items In Your Cart. Select \"Add Items\" To Add Items To Your Cart";



                tv.setText(th);
                tv.setTextSize(fontmedium); tv.setLines(3);

                ArrayList<Integer> des = md.applydesign(Transact.this, "Info", DS);
                tv.setBackgroundColor(md.getint(des,0));
                tv.setTextColor(md.getint(des,1));


            }
            else{
                cartitems.clear();
                for (int i = 2; i < cart.size(); i++){
                    ArrayList<String> row = md.makearrayfromstring(md.getstring(cart, i), "~");
                    ArrayList<String> thisrow = md.makearrayfromstring(md.rowstring(cartcols, row), "|");

                    float prc = 0, qty = 1;
                    String idd =  md.before(md.getstring(cart, i), "~");
                    String itemid = md.rowoption(thisrow, "ValueItem");
                    String name = md.rowoption(thisrow, "Item");
                    prc = md.parsefloat(md.rowoption(thisrow, "Price"));
                    String pic1 = md.rowoption(thisrow, "ItemPic1");
                    String inum = md.rowoption(thisrow, "ItemItemNumber");
                    String itype = md.rowoption(thisrow, "ItemItemType");
                    String exid = md.rowoption(thisrow, "ValueExecutor");
                    String exdis = md.rowoption(thisrow, "Executor");
                    String sztxt = md.rowoption(thisrow, "SizeText");
                    String vat = md.rowoption(thisrow, "VAT");
                    if (exdis.equals("")){exdis = exn;}
                    qty = md.parsefloat( md.rowoption(thisrow, "Quantity"));
                    showcartitem((i - 2), itemid, qty, prc, vat, inum, itype, name, pic1, sztxt, idd, exid, exdis);
                }
                finishcartdisplay();
            }
        }
    }

    public void finishcartdisplay(){
        LinearLayout lr3 = new LinearLayout(Transact.this); lr3.setBackgroundColor(md.transparent);
        lr3.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.08)));
        ln.addView(lr3);  lr3.setPadding(0, 8, 0, 0);

        int btnfnt = fontsmall, loweraddid = 9921331;
        if (smallscreen){btnfnt = btnfnt  - 3;}
        md.button(Transact.this, lr3, "Add " + MainActivity.itemname,
                "", loweraddid, "General", DS, btnfnt,   (width - (bigpad*2) ), (int) (height * 0.08));
        MaterialButton bt = (MaterialButton) findViewById(loweraddid);
        if (bt != null){

            bt.setPadding(0, 0, 0, 0);
            bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {
                addpopup();
            } });
        }

        calculatetotal("","", "", 0, 0, false);
    }


    public void showcartitem(int i, String itemid, float qty, float prc, String vat, String inum, String itype,
                             String name, String pic1, String sztxt, String cartid, String exid, String exdis){

        LinearLayout lr = new LinearLayout(Transact.this);lr.setBackgroundColor(md.transparent);
        lr.setOrientation(LinearLayout.HORIZONTAL);
        lr.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.017))); ln.addView(lr);

        boolean addqty = true;
        for (int t = 0; t < cartitems.size(); t++){
            if (md.getcartitem(cartitems, t).ID.equals(itemid)){
                qty = md.parsefloat(md.getcartitem(cartitems, t).Quantity); addqty = false;
            }
        }
        float total = md.round((prc * (float)(qty)), 2);
        if (addqty){
            cartitems.add( new MyModel.CartItem(name, String.valueOf(prc), String.valueOf(total), itemid,
                    String.valueOf(qty), "", inum, vat, pic1, itype, sztxt, cartid, exid, exdis));
        }

        if (prc <= 0){prc = 0;} final float price = prc;
        String dsp = "",vl = "";
        md.br(Transact.this, new LinearLayout(Transact.this), 1);

        int rowheight = (int)(height * 0.1);
        int pricerowht = (int) (height * 0.07), blockheight = rowheight + pricerowht;
        int opht = (int) (height * 0.08);
        if (itype.equals("2")){ blockheight += opht;}
        LinearLayout blocklin = new LinearLayout(Transact.this); blocklin.setOrientation(LinearLayout.VERTICAL);
        ln.addView(blocklin);

        blockheight += (vpad * 2) + 2; int blockwidth = width - (hpad * 2) - (bigpad*2);

        blocklin.setLayoutParams(new LinearLayout.LayoutParams(width -  (bigpad*2), blockheight));
        blocklin.setBackgroundResource(R.drawable.whitebutton); blocklin.setPadding(hpad, vpad, hpad, vpad);

        LinearLayout ln1 = new LinearLayout(Transact.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(blockwidth, rowheight));
        lp.setMargins(0, 0, 0, 0);  ln1.setOrientation(LinearLayout.HORIZONTAL);
        ln1.setPadding(0, 2, 0, 2);  blocklin.addView(ln1);ln1.setLayoutParams(lp);

        int nameid = 1111+i, qtyid = 2222+i, qplusid = 3333+i, qminusid = 4444+i, prid = 5555+ i,
                imgid = 1501+i,
                sdid = 6666 + i, stid = 7777+i, edid = 8888  + i, etid = 9999 + i, xcid = 8899+i,
                sw = (int)(width*0.5), remwidth = (int)(width * 0.1);

        int namewidth = (int)(blockwidth) - remwidth;
        if (!pic1.equals("")){
            namewidth -= rowheight;

            ImageView imgView = md.image(Transact.this, imgid, DS);
            imgView.setLayoutParams(new ViewGroup.LayoutParams(rowheight, (int)(rowheight*0.9)));
            ln1.addView(imgView);
            String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ pic1;
            imgView = md.loadimage(getResources(), imgView, url ,rowheight,(int)(rowheight*0.9));
        }

        MaterialTextView tv = md.maketextview(Transact.this,name, "",
                nameid, "Info", DS, fontsmall,(int) namewidth, rowheight, true );
        ln1.addView(tv);

        int space = (int)(0.1 * (rowheight - remwidth));
        LinearLayout lnclose = new LinearLayout(Transact.this);
        lnclose.setOrientation(LinearLayout.VERTICAL);
        lnclose.setLayoutParams(new LinearLayout.LayoutParams( remwidth, rowheight));
        lnclose.setPadding(0, space, 0, 0); ln1.addView(lnclose);

        MaterialButton mb = new MaterialButton(Transact.this);
        mb.setLayoutParams(new FrameLayout.LayoutParams( remwidth,  remwidth));
        //mb = md.buttondesign(mb, "Cancel", DS );
        mb.setBackgroundResource(R.drawable.ic_baseline_close_24);
        mb.setPadding(10, 15, 10, 10); lnclose.addView(mb);
        mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
            removecart(itemid, name);
        } });


        LinearLayout ln2 = new LinearLayout(Transact.this); ln2.setBackgroundColor(md.transparent);
        ln2.setOrientation(LinearLayout.HORIZONTAL);

        lp = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(blockwidth, pricerowht));
        lp.setMargins(0, 0, 0, 0);  ln.setPadding(0, 0, 0, 0);
        ln2.setLayoutParams(lp);
        blocklin.addView(ln2);

        tv = md.maketextview(Transact.this,currencysymbol+ String.valueOf(prc) + " " + sztxt, String.valueOf(prc) ,
                prid, "Info", DS, fontsmall,(int) (blockwidth * 0.4), pricerowht, true );
        ln2.addView(tv);

        tv = md.maketextview(Transact.this,"-", "",
                qminusid, "Info", DS, md.fontmedium,(int) (blockwidth * 0.2), pricerowht, true );
        ln2.addView(tv);

        MaterialTextView btminus = (MaterialTextView) findViewById(qminusid);
        if (btminus != null){
            btminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean gomin = true;
                    MaterialTextView mt = (MaterialTextView) findViewById(qtyid);
                    if (mt != null){String mqt = mt.getText().toString(); if ((md.parsefloat(mqt) - 1) <= 0){gomin = false;} }

                    if (gomin){
                        calculatetotal(cartid, itemid, inum, price, -1, false);
                    }
                }
            });
        }

        tv = md.maketextview(Transact.this,String.valueOf(qty), "",
                qtyid, "Info", DS, fontsmall,(int) (blockwidth * 0.2), pricerowht , true);
        ln2.addView(tv);

        int iconht = (int)(pricerowht * 0.7);
        space = (int)(0.5 * (pricerowht  - iconht));
        LinearLayout lnplus = new LinearLayout(Transact.this);
        lnplus.setOrientation(LinearLayout.VERTICAL);
        lnplus.setLayoutParams(new LinearLayout.LayoutParams( (int) (blockwidth * 0.2), pricerowht));
        lnplus.setPadding(0, space, 0, 0); ln2.addView(lnplus);


        tv = md.maketextview(Transact.this, "+", "",
                qplusid, "Warning", DS, md.fontmedium,iconht, iconht, true );

        lnplus.addView(tv);

        MaterialTextView btplus = (MaterialTextView ) findViewById(qplusid);
        if (btplus != null){
            btplus.setHeight(pricerowht);
            btplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    calculatetotal(cartid, itemid, inum, price, 1, false);
                }
            });
        }

        if (itype.equals("2")){
            int numberofbuttons = 4;
            if(!showenddate){
                numberofbuttons--;
            }

            if(!showexec){
                numberofbuttons--;
            }

            sw = (int) (blockwidth/ numberofbuttons); int twid = (int) (blockwidth / numberofbuttons);


            LinearLayout ln3 = new LinearLayout(Transact.this); ln3.setBackgroundColor(md.transparent);
            lp = new LinearLayout.LayoutParams(blockwidth, opht); lp.setMargins(0, 0, 0, 0);
            ln3.setLayoutParams(lp);
            ln3.setPadding(0, 2, 0, 4); ln3.setOrientation(LinearLayout.HORIZONTAL);
            blocklin.addView(ln3);

            int btnfnt = fontsmall;
            if (smallscreen){btnfnt = btnfnt  - 3;} String startdatestring = "Date";
            if(showenddate){ startdatestring = "StartDate";  }
            MaterialButton bt = md.makebutton(Transact.this, startdatestring , "", sdid, "General", DS, btnfnt,  sw, opht);
            ln3.addView(bt);
            bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) { loadpopup(sdid, "StartDate", "datetime", false); } });

            bt = md.makebutton(Transact.this,  "Start Time", "",stid, "General", DS, btnfnt, twid, opht);
            ln3.addView(bt);
            bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) { loadpopup(stid, "StartTime", "datetime", true); } });

            if(showenddate){
                bt = md.makebutton(Transact.this, "End Date", "", edid,"Warning", DS, btnfnt,  sw, opht);
                ln3.addView(bt);
                bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) { loadpopup(edid, "EndDate", "datetime", false); } });
            }

            if(showexec){
                bt = md.makebutton(Transact.this,  exdis, exid, xcid,"General", DS, btnfnt,  sw, opht);
                ln3.addView(bt);
                bt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {
                  /*  Intent intent = new Intent(Transact.this, SetMap.class);
                    Bundle b = new Bundle();
                    b.putStringArray("key", new String[]{ "Executors",  DS, Executor,
                            itemid, cartid, String.valueOf(xcid), ""});
                    intent.putExtras(b);
                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);

*/




                    boolean getpermission = false;
                    GpsTracker gpsTracker = new GpsTracker(Transact.this);
                    if(gpsTracker.canGetLocation()){

                        if (ActivityCompat.checkSelfPermission(Transact.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(Transact.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                        PackageManager.PERMISSION_GRANTED) {

                            Bundle b = new Bundle();
                            b.putStringArray("key", new String[]
                                    {"Executors",  DS, uid, Executor, "UserItems", itemid,"Item", itemid+"~"+cartid+"~"+String.valueOf(xcid)  });
                            Intent intent = new Intent(Transact.this, SetMap.class);
                            intent.putExtras(b);
                            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);


                        }else{
                            getpermission = true;
                        }

                    }else{
                        getpermission = true;
                    }
                    if (getpermission){
                        md.toast(Transact.this, "Failed To Get Your Current Location");
                        gpsTracker.showSettingsAlert();
                    }


                } });
            }
        }
    }

    float itemstotal, grandtotal = 0, shippingcost;
    public String  calculatetotal(String rowid, String itemid, String inumber, float prc, int amount, boolean showerrors){

        String error = "", rss = "", newqty = "", newtotal = "";
        grandtotal = 0; itemstotal = 0;
        for (int i = 0; i < cartitems.size(); i++) {
            LinearLayout lr = new LinearLayout(Transact.this);
            lr.setBackgroundColor(md.transparent);
            lr.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.01)));
            ln.addView(lr);

            float total = 0, price = 0, qty = 1,  vat=0; String startdate = "", starttime = "", enddate = "", endtime = "",
                    executor = "";
            String size = "";
            String rowitemid = cartitems.get(i).ID;// = md.rowoption(thisrow, "ValueItem");
            String name = cartitems.get(i).Name;// = md.rowoption(thisrow, "Item");
            String inum = cartitems.get(i).ItemNumber;// = md.rowoption(thisrow, "ItemNumber");
            vat = md.parsefloat(cartitems.get(i).VAT);// = md.parsefloat(md.rowoption(thisrow, "VAT"));
            String tpe = cartitems.get(i).ItemType;// = md.rowoption(thisrow, "ValueItemType");

            int nameid = 1111+i, qtyid = 2222+i, qplusid = 3333+i, qminusid = 4444+i, prid = 5555+ i,
                    imgid = 1501+i,
                    sdid = 6666 + i, stid = 7777+i, edid = 8888  + i, etid = 9999 + i, xcid = 8899+i;


            MaterialTextView  pb = (MaterialTextView ) findViewById(prid);
            if (pb != null){price = md.parsefloat(pb.getTag().toString());}
            MaterialTextView  edt = (MaterialTextView ) findViewById(qtyid);
            if (edt != null){qty = md.parsefloat(edt.getText().toString());}

            if (rowitemid.equals(itemid)){
                qty += amount;
                if (qty < 0){
                    qty = 0;
                    md.toast(this,  "The Number For "+name+" Is Already Zero");
                }
                edt.setText(String.valueOf(qty));

                for (int t = 0; t < cartitems.size(); t++){
                    if (md.getcartitem(cartitems, t).ID.equals(itemid)){
                        md.getcartitem(cartitems, t).Quantity = String.valueOf(qty);
                    }
                }
                newqty = String.valueOf(qty);
            }

            MaterialButton bt = (MaterialButton) findViewById(sdid);
            if (bt != null){; startdate =bt.getTag().toString();
                bt = (MaterialButton) findViewById(stid);  starttime = bt.getTag().toString();endtime =starttime;

                if(showenddate){
                    bt = (MaterialButton) findViewById(edid);  enddate = bt.getTag().toString();
                }else{
                    enddate =startdate;
                }

                /* bt = (MaterialButton) findViewById(etid);  endtime = bt.getTag().toString();*/
                if(showexec){
                    bt = (MaterialButton) findViewById(xcid);  executor = bt.getTag().toString();
                }
            }

            total = qty * price; if (!rowid.equals("")){newtotal = String.valueOf(total);}

            if (tpe.equals("2")){

                long startmilli = 0, endmilli = 0;
                if (!startdate.equals("") & !starttime.equals("")){
                    startmilli = md.getdatemilli(startdate, starttime);
                }
                if (startdate.equals("")){error += System.lineSeparator() +"Please Enter A Start Date For " + name;}

                if (starttime.equals("")){error +=System.lineSeparator() +"Please Enter A Start Time For " + name;}


                if (md.parseint(executor) <= 0 & showexec){error +=System.lineSeparator() +"Please Select A "+exn+" For " + name;}


                if (!enddate.equals("") & !endtime.equals("")){  endmilli = md.getdatemilli(enddate, endtime); }

                 if (enddate.equals("") & showenddate){error += System.lineSeparator() + "Please Enter An End Date For " + name;}
                //  else if (endtime.equals("")){err += System.lineSeparator() +"Please Enter A End Time For " + name;}

                if (startmilli >= endmilli){
                    //err += System.lineSeparator() +"The Start Time For " + name + " Is After The End Time";
                }
                if (startmilli > 0 & endmilli > 0 & endmilli > startmilli){

                    float starthour = startmilli / 3600000;
                    float endhour = endmilli / 3600000;

                    float hourdif = (endhour - starthour);

                    md.toast(this, String.valueOf(hourdif));
                    total *= hourdif;
                }
            }


           /* if (vat > 0){
                vat = 1 + (vat / 100);
                total = total * vat;
            }*/

            rss += "`|Item^" + rowitemid + "|ItemName^" + name+ "|ItemNumber^" +
                    inum+ "|Size^" + size +
                    "|Quantity^" + qty + "|Price^" + price +"|VAT^" + vat+"|ItemType^" + tpe
                    +"|Total^" + total +"|StartDate^" +startdate+"|StartTime^" +starttime+
                    "|EndDate^" +enddate+"|EndTime^" +endtime+"|Executor^" +executor;

            itemstotal += total;

            MaterialTextView nbt = (MaterialTextView) findViewById(nameid );
            nbt.setText( name+ " (" +currencysymbol+String.valueOf( md.round(total, 2) + ")"));
        }
        grandtotal =  shippingcost + itemstotal;
        MaterialTextView bt = (MaterialTextView) findViewById(totalid);
        if (bt != null){
            bt.setText("Total: " +currencysymbol+String.valueOf( md.round(grandtotal, 2)));
        }
        if (!error.equals("") & showerrors){
            rss = "";
            md.toast(this, error);
        }

        if (!rowid.equals("") & !newqty.equals("")){

            String vals = "|Quantity^" + newqty + "|Total^" + newtotal;
            multiset("Carts", rowid, vals,    new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    multifinishset(md.appsetting("QuantityName", DS));
                }
            }, 0, Transact.this);
        }

        return rss;
    }


    String saveresult = "", savestring = "", mss = "", transactiongateway = "", paymentreference = "", addressid = "";

    public void gosave(){

        save();
    }


    public void save(){

        if (cartitems.size() == 0){
            md.toast(Transact.this, "Please Add Items Before Continuing.");
        }
        else{
            String err = "";
            MaterialButton bt = (MaterialButton) findViewById(locbtnid);
            if (bt != null){locv = bt.getTag().toString();}
            else{
                err  += System.lineSeparator() +"Please Enter A Location For Your Booking";
            }

            if (err.equals("")){
                doenable(false);
                getaddresses(    new MainActivity.VolleyCallBack() {
                    @Override
                    public void onSuccess() {  showaddresses();   }
                }, 0, Transact.this);
            }else if (!err.equals("")){
                md.toast(Transact.this, err);
            }
        }

    }



    String ddstring = ""; boolean ddsuccess = false;
    public void getaddresses( final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {
        try{
            RequestQueue queue = Volley.newRequestQueue(Transact.this);

            String url = md.url + "getaddresses?code="+
                    md.auth(Transact.this, mydatabase, android_id, uid)+"~0";

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  ddstring = response; ddsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ getaddresses(   callBack,  attempt, cnt); }
                    else{ ddsuccess =false; md.toast(Transact.this,  "Failed To Reach Server... Retrying"); }
                }
            });
            stringRequest = md.requestwait(stringRequest); queue.add(stringRequest);
        }
        catch(NullPointerException npx){
            doenable(true);
        }
    }

    public void showaddresses(){

        // Display the first 500 characters of the response string.
        if (!ddsuccess){
            doenable(true);
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(Transact.this, z);
        }
        else if(ddsuccess) {

            ArrayList<String> list  =  md.makearrayfromstring(ddstring, "¬");

            if (list.size() > 2){

                if (md.thisisademo){

                    md.toast(Transact.this, "Sorry You Cannot Place Orders In Demo Mode");

                }
                else if (!md.thisisademo){

                    doenable(true);

                    androidx.appcompat.app.AlertDialog.Builder builder =
                            new androidx.appcompat.app.AlertDialog.Builder(Transact.this);
                    androidx.appcompat.app.AlertDialog alert = builder.create();
                    builder.setMessage("")
                            .setTitle("Would You Like To Change Address/Location For This Order?")
                            .setNeutralButton("Change Address/Location", (dialog, which) -> {

                                dialog.cancel();
                                gotoaddresses(true);
                            } )
                            .setNegativeButton("Continue", (dialog, which) -> {
                                dialog.cancel();
                                startsave(false, "");

                            } );
                    builder.create();
                    alert = builder.create();
                    alert.show();

                }

            }else{
                doenable(true);
                gotoaddresses(true);
            }
        }
    }



    public void gotoaddresses(boolean forresult){

        String res = "1"; if (!forresult){res = "0";}
        Intent intent = new Intent(Transact.this, Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "Address",  DS, Executor, res, "", "", ""});
        intent.putExtras(b);
        if (forresult){
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        }else{
            startActivity(intent);
        }

    }



    boolean savesuccess = false;
    String poscustomerstring = "";
    public void startsave(boolean gotoserver, String paymentconfirmed){

        boolean usepay =  md.appsetting("UsePaymentOptions", DS).toLowerCase().equals("true");
        String res = calculatetotal("","", "", 0, 0, true);

        if (usepay & !gotoserver & !res.equals("")){
            cusdialog = new Dialog(Transact.this);
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
            int cushit = (int)(height*0.55),  wd = (int)(width*0.85);

            cusdialog.show();
            cusdialog.setTitle(companyname + " (Help)");
            cuslin.setPadding(6, 0, 0, 0);
            int rowheight = (int)(cushit * 0.12);


            String pm =  md.appsetting("CartPaymentMessage", DS);

            View v = md.line(this, wd, md.fontmedium, md.transparent);  cuslin.addView(v);
            LinearLayout lnn = new LinearLayout(Transact.this); lnn.setOrientation(LinearLayout.HORIZONTAL);
            lnn.setLayoutParams(new LinearLayout.LayoutParams(wd, rowheight)); cuslin.addView(lnn);

            int firstgap = (int)(0.5*(wd - rowheight));
            v = md.line(this, firstgap, md.fontmedium, md.transparent);  lnn.addView(v);

            ArrayList<Integer>  des = md.applydesign(Transact.this, "General", DS);

            ImageView  img = new ImageButton(this); lnn.addView(img);
            img.setLayoutParams(new LinearLayout.LayoutParams(rowheight, rowheight));
            img.setBackgroundResource(R.drawable.ic_baseline_monetization_on_24);
             img.setColorFilter(md.getint(des,0));

            v = md.line(this, width, rowheight, md.transparent); cuslin.addView(v);

            String tp = "Proceed To Payment";
            int mc2 =  md.countlines(tp, md.fontlarge, width);
            MaterialTextView  mx = md.maketextview(this, tp, "", 0, "Info", DS,
                    md.fontlarge, wd, mc2*((int)(md.fontlarge*2.4)), true);
            mx.setLines(mc2);  mx.setTypeface(null, Typeface.BOLD);cuslin.addView(mx);
           // md.toast(Transact.this, String.valueOf(mc2));

            v = md.line(this, width, md.fontmedium, md.transparent); cuslin.addView(v);
            mc2 =  md.countlines(pm,md.fontmedium, width);
            MaterialTextView mt = md.maketextview(this, pm, "", 0, "Info", DS,
                    md.fontmedium, wd, mc2*((int)(md.fontmedium*2.4)), false);
            mt.setLines(mc2); cuslin.addView(mt);

            v = md.line(this, width, md.fontmedium, md.transparent); cuslin.addView(v);
            MaterialButton bt  = md.makebutton(this, "Pay Now" , "", 0, "Go", DS,
                    fontsmall, wd, rowheight); cuslin.addView(bt);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override  public void onClick(View view) { gotopayment(); }});
            v = md.line(this,width, rowheight, md.transparent); cuslin.addView(v);


            String cns = "Select \"Continue\" Once Payment Is Done";
            mc2 =  md.countlines(cns,md.fontmedium, width);
            mt = md.maketextview(this, cns, "", 0, "Info", DS,
                    md.fontmedium, wd, mc2*((int)(md.fontmedium*2.4)), false);
            mt.setLines(mc2);
            cuslin.addView(mt);

            v = md.line(this, width, md.fontmedium, md.transparent); cuslin.addView(v);
            bt  = md.makebutton(this, "Continue", "", 0, "General",
                    DS, fontsmall, wd, rowheight);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override  public void onClick(View view) {
                    cusload.setVisibility(View.VISIBLE);

                    fluttercheck(transactionid, new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() { flutterresult(); }
                    }, 0);
                }});
            cuslin.addView(bt);

            cusload = new ProgressBar(this); cuslin.addView(cusload); cusload.setVisibility(View.GONE);
            cusload.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight));

           v = md.line(this, width, rowheight, md.transparent); cuslin.addView(v);
        }else if (!res.equals("")) {

            if (cusdialog != null){ cusdialog.cancel();}

           // String res = calculatetotal("","", "", 0, 0, true);
            savestring =  "|Address^" + addressid
                    +   "|OrderNumber^" + transactionid
                    +   "|Created^" + md.noww(false, true).Value
                    +   "|Location^" + locv
                    +   "|ItemsTotal^" + String.valueOf(itemstotal)
                    +   "|SubTotal^" + String.valueOf(itemstotal)
                    +   "|ShippingFee^" + String.valueOf(shippingcost)
                    +   "|Total^" + String.valueOf(grandtotal)
                    +   "|LocationID^" + locationid
                    +   "|ShippingVendor^" + shipperid
                    +   "|IsShipping^" + isshipping
                    +   "|PaymentConfirmed^" + transactiongateway
                    +   "|PointOfSaleUser^" + posuser
                    +   "|CompanyLocation^" + CompanyLocation
                    +   "|PaymentReference^" + paymentreference
                    +   "|PaymentConfirmed^" + paymentconfirmed
                    +   "|PointOfSaleCustomer^" + poscustomer
                    +   "|TransactionType^" +transtype;
            savestring += "~" + res;

            serverstartsave(savestring,    new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {  finishsave();   }
            }, 0, Transact.this);
        }

    }

    public void serverstartsave(String vals, final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {
        doenable(false);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String url = md.url + "appplaceorder?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~"+vals ;

        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        saveresult = response;
                        savesuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                doenable(true);

                savesuccess = false;
                saveresult =  "Error: " + error.getMessage();
                md.toast(Transact.this, "Failed To Reach Server... Retrying");

            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);
    }

    public void finishsave(){
        doenable(true);
        if (savesuccess){
            if (!saveresult.equals("Success")){
                doenable(true);
                md.toast(Transact.this, "Order Placing Failed, Please Try Again");
            }
            else if ( saveresult.equals("Success") & mainlin != null){
                finishsavedisplay();
            }
        }
        else  {
            md.toast(Transact.this, " Error, please try again");
        }
    }

    public void finishsavedisplay(){

        mainlin.removeAllViews();
        MaterialButton bt = new MaterialButton(this);
        bt.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.1)));
        bt.setTextSize(md.fontlarge + 4); bt.setTextColor(md.green); mainlin.addView(bt);
        bt.setBackgroundColor(md.transparent); bt.setText(MainActivity.ordername + " Complete!!");

        bt = new MaterialButton(this);
        bt.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        bt.setTextSize(md.fontlarge + 4); bt.setTextColor(md.green); mainlin.addView(bt);
        bt.setBackgroundColor(md.transparent); bt.setBackgroundResource(R.drawable.ic_baseline_check_circle_24);

        bt = new MaterialButton(this);
        bt.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.2)));
        bt.setTextSize(md.fontlarge); bt.setTextColor(md.green); mainlin.addView(bt);
        bt.setLines(3);
        bt.setBackgroundColor(md.transparent); bt.setText("Go To 'My Account' To View Your " + MainActivity.ordername);



       Handler handler = new Handler(); handler.postDelayed(new Runnable() { @Override public void run()
        { finish(); } }, 4000);

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
                            md.toast(Transact.this, "Error: " + error.getMessage());
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
                            flutterdone(flw_ref);
                        }
                    }, 0);
                }else{
                    md.toast(Transact.this, "Sorry, Your Payment Could Not Be Confirmed");
                }

            } catch (JSONException e) {
                if (cusload != null){ cusload.setVisibility(View.GONE);}
                // e.printStackTrace();
            }
        }
        else{
            if (cusload != null){ cusload.setVisibility(View.GONE);}
            md.toast(Transact.this, "Payment Confirmation Unsuccessful");
        }
    }

    String flstring = ""; boolean flsuccess = false;
    public void flutterconfirm(String flutterid, String fluttersuc, final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(Transact.this);

            String url = md.url + "flutterwaveconfirm";
            url = url + "?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~" +transactionid
                    +"~" +fluttersuc+"~" +flutterid+"~CustomerOrders";

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  flstring = response; flsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ flutterconfirm(flutterid, fluttersuc,  callBack,  attempt); }
                    else{ flsuccess =false; md.toast(Transact.this,  "Failed To Reach Server... Retrying"); }
                }
            });

            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }

    public void flutterdone(String pcon){
        // Display the first 500 characters of the response string.
        if(flsuccess) {
            if (flstring.equals("success")){
                startsave(true, pcon);
            }
        }
        else  if (!flsuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(Transact.this, z);
        }

    }


















    public void doenable(boolean enabled){

        ProgressBar pbbar = (ProgressBar) findViewById(pbarid);
        if (pbbar != null){

            // RelativeLayout pbarrel = (RelativeLayout) findViewById(pbarrelid);
            if (!enabled){
                pbbar.setVisibility(View.VISIBLE);
                //   pbarrel.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.1)));
            }else{
                pbbar.setVisibility(View.GONE);
                // pbarrel.setLayoutParams(new LinearLayout.LayoutParams(width, 0));
            }

            MaterialButton btn = (MaterialButton) findViewById(savebtnid);
            btn.setEnabled(enabled);
        }

    }


    String fkresult = "", fkcol = "", fkid = "";
    public void loadpopup(int controlid, String col, String datatype, boolean istime){
        fkcontrol = controlid;
        if (datatype.equals("bigint")){
            fkcol = col;
            doenable(false);
            md.toast(Transact.this, "Loading " + md.returndisplay(fkcol) + " Options" );
            getfks(fkcol,  new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    showfks();
                }
            }, 0, Transact.this);
        }else  if (datatype.equals("datetime") & !istime){
            showDialog(111);
        } else  if (istime){
            showDialog(333);
        }
    }


    public void getfks(String col, final MainActivity.VolleyCallBack callBack,
                       int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "fklist";

        url = url + "?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~"+table+"~"+col;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fkresult = response;
                        dataSuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){
                    getfks( col, callBack,  attempt, cnt);
                }else{
                    dataSuccess = false;
                    fkresult =  "Error: " + error.getMessage();
                    md.toast(Transact.this, "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest); queue.add(stringRequest);

    }



    public void showfks(){
        if (dataSuccess ){
            fkidlist.clear();
            fkviewlist.clear();
            ArrayList<String> jj =  md.makearrayfromstring(fkresult, "¬");
            for (int i = 0; i < jj.size(); i++)
            {
                String id = md.before(md.getstring(jj, i),  "~");
                String vl = md.breakurl(md.getstring(jj,i), 1, "~");
                fkviewlist.add(vl);
                fkidlist.add(id);
            }
            doenable(true);

            showDialog(222);
        }
        else  {
            md.toast(Transact.this, " Error, please try again");
        }
    }







    String fkselected = "", fkdisplay  = "";
    int fkcontrol = -1;
    ArrayList<String> fkviewlist = new ArrayList<>();
    ArrayList<String> fkidlist = new ArrayList<>();
    Calendar c = Calendar.getInstance();
    String javayear = String.valueOf(c.get(Calendar.YEAR));
    String javamonth = String.valueOf(c.get(Calendar.MONTH));
    String javaday = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
    String javahour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
    String javaminute = String.valueOf(c.get(Calendar.MINUTE));


    public void setupedit(){

        MaterialButton bt = (MaterialButton) findViewById(fkcontrol);
        bt.setTag(fkselected);
        bt.setText(fkdisplay);
        //  dialogcancel();
    }


    @Override
    protected Dialog onCreateDialog(int id) {

        //   dialogcancel();
        // TODO Auto-generated method stub
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);

        if (id == 111) {
            return new DatePickerDialog(this, myDateListener, md.parseint(javayear), md.parseint(javamonth),  md.parseint(javaday));
        }
        else if (id == 333) {
            return new TimePickerDialog(this, myTimeListener, md.parseint(javahour), md.parseint(javaminute), true);

        }
        else if (id == 222){


            LinearLayout ll = new LinearLayout(Transact.this);
            ll.setOrientation(LinearLayout.VERTICAL);
            final ListView lv = new ListView(Transact.this);
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(Transact.this, android.R.layout.simple_list_item_1, fkviewlist);
            lv.setAdapter(itemsAdapter);
            ll.addView(lv);
            builder.setView(ll);

            AlertDialog dialog = builder.create();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,  long id) {
                    try{
                        fkselected = md.getstring(fkidlist, position);
                        fkdisplay = md.getstring(fkviewlist, position);
                        doenable(true);

                        dialog.cancel();
                        setupedit();
                        md.toast(Transact.this, fkdisplay + " Selected" );
                    }catch(Exception c){
                        md.toast(Transact.this, "ERROR: " + c.getMessage());
                    }

                }
            });


            return  dialog;

        }
        else if (id == 444){

            LayoutInflater inflater = this.getLayoutInflater();
            dialogView = inflater.inflate(R.layout.custom, null);
            builder.setView(dialogView);
            cuslin = (LinearLayout) dialogView.findViewById(R.id.customlinear);

            cusdialog = builder.create();


            return  cusdialog;

        }
        return null;
    }

    LinearLayout cuslin;  Dialog cusdialog;   View dialogView;
    int searchbarheight = (int) (height * 0.07), cuswid = 0, cushit = 0, cusedid = 4891542,
            cusbid = 5212594, cuspbid = 1583223, cussaveid = cuspbid + 1,cuscrlid = 4829236, cussubid = 8423462, searchpre = 7519223;


    public void transtypepopup(boolean gosave){

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
        cushit = (int)(height*0.55);  cuswid = (int)(width*0.92);

        cusdialog.show();

        startsearch();
    }




    public void addpopup(){

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
        cushit = (int)(height*0.55);  cuswid = (int)(width*0.92);

        cusdialog.show();

        startsearch();
    }

    public void dynamicadd(String tp){
        cusdialog.cancel();
        String dcv = "ItemType^" + tp;
        Intent intent = new Intent(Transact.this.getApplicationContext(), Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "DynamicAdd",  DS, MainActivity.Executor, "Items", "0", dcv, ""});
        intent.putExtras(b);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }


    public void startsearch(){

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
        cuslin.removeAllViews();

        LinearLayout rl = new LinearLayout(Transact.this); cuslin.addView(rl);
        rl.setOrientation(LinearLayout.HORIZONTAL);

        EditText edt = new EditText(Transact.this);
        edt.setWidth((int) (width * 0.83)); edt.setId( cusedid );
        rl.addView(edt);edt.setHint("Search " + MainActivity.itemnameplural);

        Button bt = new Button(Transact.this); bt.setId(cusbid);
        bt.setWidth((int) (width * 0.17)); rl.addView(bt);
        bt.setBackgroundResource(R.drawable.ic_baseline_search_24);
        bt.setPadding(2, 2, 2, 2);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { search();  }   });

        ProgressBar pbbar = new ProgressBar(Transact.this); pbbar.setId(cuspbid);
        cuslin.addView(pbbar);pbbar.setVisibility(View.GONE);
        pbbar.setLayoutParams(new LinearLayout.LayoutParams((int)(width * 0.9), (int)(height * 0.07)));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(width * 0.9), (int)(height * 0.75));
        NestedScrollView scrl = new NestedScrollView(Transact.this); scrl.setId(cuscrlid);
        scrl.setLayoutParams(lp) ;
        cuslin.addView(scrl);

        LinearLayout lne = new LinearLayout(Transact.this); lne.setId(cussubid);
        lne.setLayoutParams(lp);
        lne.setOrientation(LinearLayout.VERTICAL); scrl.addView(lne);
        scrl.setLayoutParams(new LinearLayout.LayoutParams((int)(width * 0.9), (int)(height * 0.7)));
        lne.setLayoutParams( new FrameLayout.LayoutParams((int)(width * 0.9), (int)(height * 0.75)));


    }


    String searchvalue = "";
    public void search(){
        EditText ed = (EditText) cusdialog.findViewById(cusedid);
        searchvalue  = ed.getText().toString();
        if (!searchvalue.equals("") & !searchvalue.contains("~")
                & !searchvalue.contains("^")& !searchvalue.contains("|") & !searchvalue.contains(";")){
            ProgressBar pbbar = (ProgressBar)  cusdialog.findViewById(cuspbid);
            pbbar.setVisibility(View.VISIBLE);

            LinearLayout lne = (LinearLayout) cusdialog.findViewById(cussubid); lne.removeAllViews();

            ed.setText("");

            getsearch( searchvalue , new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {   searchloaded(); } }, 0);
        }
    }



    String searchstring; boolean searchSuccess;
    public void getsearch(String searchval ,final MainActivity.VolleyCallBack callBack, int attempt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(Transact.this);


        String pms = "?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~Items";
        if (!searchval.equals("") ){

            String shownums  = md.appsetting("ShowItemNumber", DS);
            if (shownums.toLowerCase().equals("true") & !searchval.equals("") & table.equals("Items")){
                pms +=  "^|Name_ItemNumber^Or^like^"+searchval;
            }else{
                pms +=  "^|Name^like^"+searchval;
            }
        }


        String url = md.url + "gettable" + pms;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        searchstring = response;
                        searchSuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (attempt < md.attempts){
                    getsearch( searchval, callBack,  attempt);
                }else{
                    md.toast(Transact.this, "Failed To Reach Server... Retrying");

                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);
    }



    ArrayList<String>   searchlist = new ArrayList<>(), searchcols = new ArrayList<>(), searchrules = new ArrayList<>();
    String searchrulestring = "";

    public void searchloaded(){
        // Display the first 500 characters of the response string.
        ProgressBar pbbar = (ProgressBar) cusdialog.findViewById(cuspbid);
        if (!searchSuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(Transact.this, z);
        }
        else if(searchSuccess & pbbar  != null) {
            String ftstring = searchstring;
            searchlist  =  md.makearrayfromstring(ftstring, "¬");
            searchrulestring = md.getstring(searchlist, 0);
            searchrules = md.makearrayfromstring(searchrulestring, "#");
            searchcols = md.makearrayfromstring(md.getstring(searchlist, 1), "~");



            if (pbbar != null){
                pbbar.setVisibility(View.GONE);
                LinearLayout lne = (LinearLayout) cusdialog.findViewById(cussubid); lne.removeAllViews();

                for (int c = 2; c < searchlist.size(); c++){
                    ArrayList<String> row = md.makearrayfromstring(md.getstring(searchlist, c), "~");
                    String idd =  md.before(md.getstring(searchlist, c), "~");
                    String name =  md.coloption(searchcols, "Name", row).Value;
                    String pic =  md.coloption(searchcols, "Pic1", row).Value;

                    String prc =  md.coloption(searchcols, "SellingPrice", row).Value;
                    String vit =  md.coloption(searchcols, "ValueItemType", row).Value;
                    String inumber =  md.coloption(searchcols, "ItemNumber", row).Value;

                    final String price = String.valueOf(md.round(md.parsefloat(prc), 2));
                    int nid = searchpre + (c - 2);

                    LinearLayout lnh = new LinearLayout(Transact.this); lnh.setOrientation(LinearLayout.HORIZONTAL);
                    int wd = lne.getLayoutParams().width, ht = lne.getLayoutParams().height;

                    int picwidth = (int)(ht * 0.13), imgid = 5545 + md.parseint(idd), nameid = 9432 + md.parseint(idd);
                    int namewidth = wd - ((int)(picwidth * 2)), iconwd = (int)(picwidth * 0.5);

                    lnh.setLayoutParams(new LinearLayout.LayoutParams(wd, picwidth)); lne.addView(lnh);

                    ImageView imgView = md.image(Transact.this, imgid, DS);
                    imgView.setLayoutParams(new ViewGroup.LayoutParams(picwidth, (int)(picwidth*0.9)));
                    lnh.addView(imgView);
                    String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ pic;
                    imgView = md.loadimage(getResources() ,imgView, url, picwidth, (int)(picwidth*0.9) );

                    String shownums  = md.appsetting("ShowItemNumber", DS), nm = name;
                    if (shownums.toLowerCase().equals("true")){
                        nm += System.lineSeparator() + inumber;
                    }
                    nm += System.lineSeparator() + inumber;

                    MaterialTextView nb = md.maketextview(Transact.this,  nm,"", nameid, "Info",
                            MainActivity.DisplayString, fontsmall, namewidth, picwidth, true);lnh.addView(nb);

                    int topspace = (int)(0.5 * (picwidth - iconwd)), acticon = (int)(iconwd * 0.8);
                    LinearLayout lnadd = new LinearLayout(Transact.this);
                    lnadd.setOrientation(LinearLayout.VERTICAL);
                    lnadd.setLayoutParams(new LinearLayout.LayoutParams(iconwd, picwidth));
                    lnadd.setPadding(0, topspace, 4, 0); lnh.addView(lnadd);

                    MaterialButton mb = new MaterialButton(Transact.this);
                    mb.setLayoutParams(new FrameLayout.LayoutParams(acticon, acticon));
                    mb = md.addtocarticon(mb, DS);
                    mb.setPadding(10, 15, 10, 10); lnadd.addView(mb);
                    mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {

                        md.toast(Transact.this, "Adding " + name);
                        pbbar.setVisibility(View.VISIBLE);
                        lne.removeAllViews();
                        cusdialog.cancel();
                        addcart(idd, inumber, String.valueOf(price), new MainActivity.VolleyCallBack() {
                            @Override  public void onSuccess() { showadded(idd); } }, 0, Transact.this);

                    } });

                    LinearLayout lnclose = new LinearLayout(Transact.this);
                    lnclose.setOrientation(LinearLayout.VERTICAL);
                    lnclose.setLayoutParams(new LinearLayout.LayoutParams(iconwd, picwidth));
                    lnclose.setPadding(4, topspace, 0, 0); lnh.addView(lnclose);

                    mb = new MaterialButton(Transact.this);
                    mb.setLayoutParams(new FrameLayout.LayoutParams(acticon, acticon));
                    mb.setBackgroundColor(md.brightred);
                    mb.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                    mb.setPadding(10, 15, 10, 10); lnclose.addView(mb);
                    mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                        clicktofav(idd, name, price, inumber);
                    } });

                }
            }
        }
    }


    public void clicktofav(String itemid, String name, String price,  String itemnumber){

        md.toast(Transact.this,   " Adding "+name+" To Favourites!" );

        addfav(itemid, price, itemnumber, new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showaddedfav();
            }
        }, 0, Transact.this);
    }
    String afstring = ""; boolean afsuccess = false;


    public void addfav(String item, String price, String itemnumber,
                       final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "appaddfav?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~"+
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
                    md.toast(Transact.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest); queue.add(stringRequest);

    }

    public void showaddedfav(){
        if ( afsuccess){
            md.toast(Transact.this,  MainActivity.itemname + " Added To Favourites!" );
        }
        else  {
            md.toast(Transact.this,  "Failed To Add To Favourites. Please Try Again" );
        }
    }





    public void valsearch(ArrayList<MyModel.CartItem> res){
        // Display the first 500 characters of the response string.
        ProgressBar pbbar = (ProgressBar) cusdialog.findViewById(cuspbid);
        if(pbbar  != null) {
            pbbar.setVisibility(View.GONE);
            LinearLayout lne = (LinearLayout) cusdialog.findViewById(cussubid); lne.removeAllViews();
            for (int c = 0; c < res.size(); c++){
                String idd =  res.get(c).ID;
                String name = res.get(c).Name;
                String pic =  res.get(c).Pic1;

                String prc =  res.get(c).SellingPrice;
                String vit =  res.get(c).ItemType;
                String inumber =  res.get(c).ItemNumber;

                final String price = String.valueOf(md.round(md.parsefloat(prc), 2));
                int nid = searchpre + (c - 2);

                LinearLayout lnh = new LinearLayout(Transact.this); lnh.setOrientation(LinearLayout.HORIZONTAL);
                int wd = lne.getLayoutParams().width, ht = lne.getLayoutParams().height;

                int picwidth = (int)(ht * 0.13), imgid = 5545 + md.parseint(idd), nameid = 9432 + md.parseint(idd);
                int namewidth = wd - ((int)(picwidth * 2)), iconwd = (int)(picwidth * 0.5);

                lnh.setLayoutParams(new LinearLayout.LayoutParams(wd, picwidth)); lne.addView(lnh);

                ImageView imgView = md.image(Transact.this, imgid, DS);
                imgView.setLayoutParams(new ViewGroup.LayoutParams(picwidth, (int)(picwidth*0.9)));
                lnh.addView(imgView);
                String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ pic;
                imgView = md.loadimage(getResources() ,imgView, url, picwidth, (int)(picwidth*0.9) );

                String shownums  = md.appsetting("ShowItemNumber", DS), nm = name;
                if (shownums.toLowerCase().equals("true")){
                    nm += System.lineSeparator() + inumber;
                }
                nm += System.lineSeparator() + inumber;

                MaterialTextView nb = md.maketextview(Transact.this,  nm,"", nameid, "Info",
                        MainActivity.DisplayString, fontsmall, namewidth, picwidth, true);lnh.addView(nb);

                int topspace = (int)(0.5 * (picwidth - iconwd)), acticon = (int)(iconwd * 0.8);
                LinearLayout lnadd = new LinearLayout(Transact.this);
                lnadd.setOrientation(LinearLayout.VERTICAL);
                lnadd.setLayoutParams(new LinearLayout.LayoutParams(iconwd, picwidth));
                lnadd.setPadding(0, topspace, 4, 0); lnh.addView(lnadd);

                MaterialButton mb = new MaterialButton(Transact.this);
                mb.setLayoutParams(new FrameLayout.LayoutParams(acticon, acticon));
                mb = md.addtocarticon(mb, DS);
                mb.setPadding(10, 15, 10, 10); lnadd.addView(mb);
                mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {

                    md.toast(Transact.this, "Adding " + name);
                    pbbar.setVisibility(View.VISIBLE);
                    lne.removeAllViews();
                    cusdialog.cancel();

                    addcart(idd, inumber, String.valueOf(price), new MainActivity.VolleyCallBack() {
                        @Override  public void onSuccess() { showadded(idd); } }, 0, Transact.this);

                } });

                LinearLayout lnclose = new LinearLayout(Transact.this);
                lnclose.setOrientation(LinearLayout.VERTICAL);
                lnclose.setLayoutParams(new LinearLayout.LayoutParams(iconwd, picwidth));
                lnclose.setPadding(4, topspace, 0, 0); lnh.addView(lnclose);


            }
        }
    }











    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {

            String hr = String.valueOf(i), mn = String.valueOf(i1);

            if (hr.length() == 1){hr = "0" + hr;} if (mn.length() == 1){mn = "0" + mn;}

            String tm = hr+ ":" +mn;

            MaterialButton BD = (MaterialButton) findViewById(fkcontrol); BD.setText( tm);   BD.setTag(tm);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int yr, int mn, int dy) {

            MaterialButton BD = (MaterialButton) findViewById(fkcontrol);
            ArrayList<String> cs = md.correctdate(yr, mn, dy);
            BD.setText(md.getstring(cs,0) + "-" + md.getstring(cs,1) + "-" +md.getstring(cs,2));
            BD.setTag(md.getstring(cs, 0) + "-" +md.getstring(cs, 1)+ "-" + md.getstring(cs, 2));

        }
    };

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public void setlocation(String controlid, String column, String value)
    {
        String nm = "";
        final String golocation = "Set¬" +nm + "¬" + table +"¬"+value + "¬" +String.valueOf(controlid)+ "¬1";
        Intent intent = new Intent(Transact.this.getApplicationContext(), SetMap.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE,  golocation);

        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);

    }

    public void setroute(String controlid, String column, String value)
    {
        String nm = "";
        final String golocation = "Route¬" +nm + "¬" + table +"¬"+value + "¬" +String.valueOf(controlid)+ "¬1";
        Intent intent = new Intent(Transact.this.getApplicationContext(), SetMap.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE,  golocation);

        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }






    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                String[] received = b.getStringArray("key");

                if (md.getarray(received, 0).equals("Items")){
                    ArrayList<String> thisrow = md.makearrayfromstring(md.getarray(received, 1), "|");
                    float prc = 0; int qty = 1;
                    String id = md.rowoption(thisrow, "ID");
                    String num = md.rowoption(thisrow, "ItemNumber");
                    prc = md.parsefloat(md.rowoption(thisrow, "SellingPrice"));
                    String vit = md.rowoption(thisrow, "ValueItemType");
                    loading("Adding To Your Cart... Please Wait");

                    addcart(id,  num, String.valueOf(prc), new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            showadded(id);
                        }
                    }, 0, Transact.this);
                }
                else if (md.getarray(received, 0).equals("DynamicAdd")){

                    ArrayList<String> vals = md.makearrayfromstring(md.getarray(received, 2), "|");
                    String idd = "", inumber = "", price = "";
                    for (int i = 0; i < vals.size(); i++){
                        String vl = md.breakurl(vals.get(i), 1, "^");
                        if (md.before(vals.get(i), "^").equals("ID")){ idd = vl; }
                        else if (md.before(vals.get(i), "^").equals("ItemNumber")){ inumber = vl;  }
                        else if (md.before(vals.get(i), "^").equals("SellingPrice")){price = vl; }
                    }
                    String id = idd;


                    addcart(idd, inumber, String.valueOf(price), new MainActivity.VolleyCallBack() {
                        @Override  public void onSuccess() { showadded(id); } }, 0, Transact.this);
                }
                else if (md.getarray(received, 0).equals("Executors")){
                    //"Executor", userid, xcitem, xcrowid, xccontrolid, name

                    String itemid = md.getarray(received, 2);
                    String xcrowid = md.getarray(received, 3);
                    String xccontrolid = md.getarray(received, 4);

                    int xcon = md.parseint(xccontrolid);
                    MaterialButton mb = (MaterialButton) findViewById(xcon);
                    mb.setText(md.getarray(received, 5));
                    mb.setTag( md.getarray(received, 1));

                    String vals = "|Executor^" + md.getarray(received, 1);
                    multiset("Carts", xcrowid, vals,    new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            multifinishset(md.getarray(received, 5));
                        }
                    }, 0, Transact.this);

                }
                else if (md.getarray(received, 0).equals("Location")){
                    MaterialButton bt = (MaterialButton) findViewById(locbtnid); bt.setTag(md.getarray(received, 1));
                }
                else if (md.getarray(received, 0).equals("Address")){
                    md.toast(Transact.this, md.getarray(received, 1));

                    startsave(false, "");
                }
                else if (md.getarray(received, 0).equals("Pops")){
                    md.toast(Transact.this, md.getarray(received, 1));

                    startsave(false, "");
                }

            }
        }
    }



    public void gotopayment(){

        String rst = md.encrypt(Transact.this, "CustomerOrders~"+transactionid+"~"+grandtotal +"~"+currencycode , true, true);
        String ath = md.auth(Transact.this,  mydatabase, android_id, uid)+"~" + rst;
        String url = "https://"+md.Site+"/Home/Flutterwave?code=" +ath;


        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    public void removecart(String itemid, String name){

        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(Transact.this);
        androidx.appcompat.app.AlertDialog alert = builder.create();
        builder.setMessage("")
                .setTitle("Remove " + name + " From This Order?")
                .setNeutralButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                } )
                .setNegativeButton("Remove " + name, (dialog, which) -> {
                    dialog.cancel();
                    doenable(false);


                    doremovecart(itemid,    new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            doenable(true);
                            finishremove(itemid);   }
                    }, 0, Transact.this);

                } );
        builder.create();
        alert = builder.create();
        alert.show();
    }

    boolean remsuccess = false; String remstring ="";
    public void doremovecart(String item,  final MainActivity.VolleyCallBack callBack,
                             int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "appremovecart?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~"+item ;
        final int attempt = attempts + 1; final String kr = url;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        remstring  = response;
                        remsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){ doremovecart(item, callBack,  attempt, cnt);
                }else{
                    doenable(true);
                    remsuccess = false;
                    remstring =  "";
                    md.toast(Transact.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest); queue.add(stringRequest);
    }


    public void finishremove(String remid){

        if (remsuccess){
            ArrayList<MyModel.CartItem> remcart = new ArrayList<>();
            for (int t = 0; t < cartitems.size(); t++){
                if (!md.getcartitem(cartitems, t).ID.equals( remid)){
                    remcart.add(md.getcartitem(cartitems, t));
                }
            }
            cartitems = remcart;
            loading("Loading Your Cart... Please Wait");

            servergetcart( new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() { showloaded(); }
            }, 0, Transact.this);
        }
        else  {
            md.toast(Transact.this,  "Failed To Reload Cart");
        }

    }



    String adstring = ""; boolean addsuccess = false;
    public void addcart(String item, String itemnumber, String price, final MainActivity.VolleyCallBack callBack,
                        int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "appaddcart?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~"+item +"~"+price+"~1~"+price + "~"+itemnumber;

        final int attempt = attempts + 1; final String kr = url;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        adstring  = response;
                        addsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){ addcart(item, itemnumber, price, callBack,  attempt, cnt);
                }else{
                    addsuccess = false;
                    adstring =  "";
                    md.toast(Transact.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);

    }

    public void showadded(String addedid){
        if ( addsuccess){
            for (int t = 0; t < cartitems.size(); t++){
                if (md.getcartitem(cartitems, t).ID.equals( addedid)){
                    md.getcartitem(cartitems, t).Quantity = String.valueOf(md.parsefloat(md.getcartitem(cartitems, t).Quantity) + 1);
                }
            }

            loading("Loading Your Cart... Please Wait");
            servergetcart( new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() { showloaded(); }
            }, 0, Transact.this);
        }
        else  {
            md.toast(Transact.this,  "Failed To Load Cart: "+  adstring );
        }
    }













    String msresult = ""; boolean mssuccess = false;
    public void multiset(String table,  String rowid, String values, final MainActivity.VolleyCallBack callBack,
                         int attempts, Context cnt) {

        MaterialButton svbtn = (MaterialButton) findViewById(savebtnid);
        if (svbtn != null){svbtn.setEnabled(false);}
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "multiset";

        url = url + "?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~"+table+"~"+rowid+"~"+values;
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

                    if (svbtn != null){svbtn.setEnabled(true);}
                    msresult =  "";
                    md.toast(Transact.this, "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest); queue.add(stringRequest);


    }

    public void multifinishset(String column){

        MaterialButton svbtn = (MaterialButton) findViewById(savebtnid);
        if (svbtn != null){svbtn.setEnabled(true);}
        if (mssuccess ){
            // md.toast(Transact.this, column+ " Saved!");

        }
        else  { md.toast(Transact.this, " Error, please try again");  }
    }










    public void askpos(){

        cusdialog = new Dialog(this);  cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cusdialog.setContentView(R.layout.custom); cusdialog.setCancelable(true);   cusdialog.setCanceledOnTouchOutside(true);
        Window window = cusdialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
        cuslin.setOrientation(LinearLayout.VERTICAL);
        cuslin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cushit = (int)(height*0.75);  cuswid = (int)(width*0.92); int cuslineht = (int)(cushit * 0.12);

        cuslin.setPadding(6, 0, 0, 0);

        MaterialButton bt  = md.makebutton(Transact.this, "Save Sale With Customer Details", "", 0,
                "General", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { saveposuser(); }   });

        bt  = md.makebutton(Transact.this, "Save Without Details", "", 0,
                "General", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {  cusdialog.cancel();  save();  }});

        bt  = md.makebutton(Transact.this, "Cancel", "", 0,
                "Cancel", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { cusdialog.cancel(); }});

        cusdialog.show();
    }


    int poscunameid = 1429867, poscellnumid = poscunameid + 1, poscusrnameid = poscellnumid+1, posemail = poscunameid + 1, cuslineht;
    public void saveposuser(){

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear); cuslin.removeAllViews();
        cuslin.setOrientation(LinearLayout.VERTICAL);
        cuslin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cushit = (int)(height*0.55);  cuswid = (int)(width*0.92); cuslineht = (int)(height * 0.08);

        EditText ed = md.edittext(Transact.this, poscunameid, "Customer Name", "", "text",
                cuswid, cuslineht, fontmedium, DS); cuslin.addView(ed);

        ed = md.edittext(Transact.this, poscusrnameid, "Customer Surname", "", "text",
                cuswid, cuslineht, fontmedium, DS); cuslin.addView(ed);

        ed = md.edittext(Transact.this, poscellnumid, "Customer Cell", "", "number",
                cuswid, cuslineht, fontmedium, DS); cuslin.addView(ed);

        ed = md.edittext(Transact.this, posemail, "Customer Email", "", "text",
                cuswid, cuslineht, fontmedium, DS); cuslin.addView(ed);

        LinearLayout ln = new LinearLayout(Transact.this); ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht)); cuslin.addView(ln);

        MaterialButton  bt  = md.makebutton(Transact.this, "Save", "", cussaveid,
                "Go", DS, fontsmall, cuswid, cuslineht); ln.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {  posuser();  }});

        ProgressBar pbbar = new ProgressBar(Transact.this); pbbar.setId(cuspbid);
        ln.addView(pbbar);pbbar.setVisibility(View.GONE);

    }


    public void posuser(){

        EditText ed = (EditText) cusdialog.findViewById(poscunameid);
        if (ed != null){

            poscusname = ed.getText().toString();
            ed = (EditText) cusdialog.findViewById(poscusrnameid);
            String sn = ed.getText().toString();

            ed = (EditText) cusdialog.findViewById(poscellnumid);
            poscellnum = ed.getText().toString();

            ed = (EditText) cusdialog.findViewById(posemail);
            String em = ed.getText().toString();

            String vals = "|Name^" + poscusname + "|Surname^" + sn+ "|Cell^" + poscellnum+ "|Email^" + em+ "|Executor^3";


            MaterialButton bt = (MaterialButton) cusdialog.findViewById(cussaveid);
            ProgressBar pbbar = (ProgressBar) cusdialog.findViewById(cuspbid);
            if (pbbar != null){ bt.getLayoutParams().width = 0;pbbar.setVisibility(View.VISIBLE);}


            saveposuser(   vals,    new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    newcustomer();
                }
            }, 0, Transact.this);
        }

    }


    String psresult = ""; boolean pssuccess = false;
    public void saveposuser( String values, final MainActivity.VolleyCallBack callBack,
                             int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "addposcustomer";

        url = url + "?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~Users~~"+values;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        psresult = response;
                        pssuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pssuccess = false;
                psresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    saveposuser(   values, callBack,  attempt, cnt);
                }else{
                    msresult =  "";
                    md.toast(Transact.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);
    }


    public void newcustomer(){

        if (pssuccess ){

            ArrayList<String>  ptlist = md.makearrayfromstring(psresult,"¬");
            ArrayList<String>  ptcols = md.makearrayfromstring(md.getstring(ptlist, 1), "~");

            if (ptlist.size() > 2){

                poscustomer =  md.before(md.getstring(ptlist, 2), "~");
                cusdialog.cancel();
                save();
            }else{

                MaterialButton bt = (MaterialButton) cusdialog.findViewById(cussaveid);
                ProgressBar pbbar = (ProgressBar) cusdialog.findViewById(cuspbid);
                if (pbbar != null){ bt.getLayoutParams().width = cuswid;pbbar.setVisibility(View.GONE);}
            }
        }
        else  { md.toast(Transact.this, " Error, please try again");  }
    }























    String sncstring; boolean sncSuccess;
    public void synchproducts(final MainActivity.VolleyCallBack callBack, int attempt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(Transact.this);

        String url = md.url + "gettable?code="+md.auth(Transact.this, mydatabase, android_id, uid)+"~Items";
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
                    md.toast(Transact.this, "Failed To Reach Server... Retrying");

                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);
    }

    public void finalsynchproducts(){
        // Display the first 500 characters of the response string.
        if (!sncSuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(Transact.this, z);
        }
        else if(sncSuccess ) {

            md.synchproducts(sncstring, mydatabase);
            md.toast(Transact.this, "Items Synchronized Successfully");
        }
    }









    public void offlineaddcart(String itemid, int qtyid, boolean add){

        float qty = 1;
        if (qtyid > 0){
            MaterialTextView mt = (MaterialTextView) findViewById(qtyid);
            if (mt != null){
                if (add){qty = md.parsefloat(mt.getText().toString()) + 1;}
                else{qty = md.parsefloat(mt.getText().toString()) - 1;}
                if (qty <= 0){qty = 1;}
                mt.setText(String.valueOf(qty));
            }
        }



        String siteref = md.Site;
        if (md.DbName.equals("valeronpro")){ MyModel.option oi = md.valeronuser(mydatabase); siteref = oi.Option2; }

        ArrayList<MyModel.CartItem> recs = new ArrayList<>();
        Cursor rs =  mydatabase.rawQuery( "select * from ITEMLIST where SITEREF = '"+siteref+"' and   ID = "+itemid+"", null );
        while ( rs.moveToNext()) {
            recs.add(new MyModel.CartItem(rs.getString(rs.getColumnIndex("NAME")),
                    rs.getString(rs.getColumnIndex("SELLINGPRICE")),   "",
                    rs.getString(rs.getColumnIndex("ID")),"1",
                    rs.getString(rs.getColumnIndex("PURCHASEPRICE")),
                    rs.getString(rs.getColumnIndex("ITEMNUMBER")),
                    rs.getString(rs.getColumnIndex("VAT")),
                    rs.getString(rs.getColumnIndex("PIC1")),
                    rs.getString(rs.getColumnIndex("ITEMTYPE")),
                    rs.getString(rs.getColumnIndex("SIZETEXT")),"", "", "" ));
        }
        if (recs.size() > 0){

            ArrayList<MyModel.CartItem> carts = new ArrayList<>();
            rs =  mydatabase.rawQuery(
                    "select * from OFFLINESALESCART where SITEREF = '"+siteref+"' and ITEMID = "+itemid+"", null );

            while ( rs.moveToNext()) {
                carts.add(new MyModel.CartItem(rs.getString(rs.getColumnIndex("NAME")),
                        rs.getString(rs.getColumnIndex("SELLINGPRICE")),   "0",
                        rs.getString(rs.getColumnIndex("ITEMID")),
                        rs.getString(rs.getColumnIndex("QUANTITY")),
                        "0",
                        rs.getString(rs.getColumnIndex("ITEMNUMBER")),
                        rs.getString(rs.getColumnIndex("VAT")),
                        rs.getString(rs.getColumnIndex("PIC1")),
                        rs.getString(rs.getColumnIndex("ITEMTYPE")),
                        rs.getString(rs.getColumnIndex("SIZETEXT")),
                        rs.getString(rs.getColumnIndex("ID")),
                        rs.getString(rs.getColumnIndex("EXECUTOR")),
                        rs.getString(rs.getColumnIndex("EXECUTORNAME"))
                ));

       /*     String name, String sellprice, String total,String id, String qty, String purchase,
                    String num, String vat, String pic1, String type, String sizetext, String cartid,
                    String executor, String executorname*/
            }
            if (carts.size() > 0){
                mydatabase.execSQL("UPDATE OFFLINESALESCART SET QUANTITY = "+String.valueOf(qty)+" " +
                        "where SITEREF = '"+siteref+"' and ITEMID = "+itemid+"");
            }else{
                float tot = md.parsefloat(recs.get(0).SellingPrice) + qty; tot = md.round(tot, 2);
                mydatabase.execSQL("INSERT INTO OFFLINESALESCART ( NAME, ITEMNUMBER  , SELLINGPRICE, PURCHASEPRICE, ITEMID, ITEMTYPE, REFERENCE, SIZETEXT, VAT, "  +
                        "                    SITE, PIC1, SITEREF, QUANTITY, TOTAL, EXECUTOR, EXECUTORNAME, ISAVAILABLE) VALUES('"+recs.get(0).Name+"', '"+recs.get(0).ItemNumber+"', "
                        +recs.get(0).SellingPrice+  ", "+recs.get(0).PurchasePrice+", "+itemid+", "+recs.get(0).ItemType+
                        ", '', '"+recs.get(0).SizeText+"', "+recs.get(0).VAT+ ", 1, '"+recs.get(0).Pic1+"', '"+siteref+"',"+
                        String.valueOf(qty)+",  "+  tot+", '', '', 1)");

            }
        }
        getofflinecart();
    }

    public void offlineremovecart(String itemid){

        String siteref = md.Site;
        if (md.DbName.equals("valeronpro")){ MyModel.option oi = md.valeronuser(mydatabase); siteref = oi.Option2; }

        mydatabase.execSQL("UPDATE OFFLINESALESCART SET QUANTITY = 0 " +
                "where SITEREF = '"+siteref+"' and ITEMID = "+itemid+"");

        getofflinecart();
    }

    public void getofflinecart(){
        loading("Loading Your Cart... Please Wait");

        String siteref = md.Site;
        if (md.DbName.equals("valeronpro")){ MyModel.option oi = md.valeronuser(mydatabase); siteref = oi.Option2; }

        ArrayList<MyModel.CartItem> recs = new ArrayList<>();
        Cursor rs =  mydatabase.rawQuery(
                "select * from OFFLINESALESCART where SITEREF = '"+siteref+"' and QUANTITY > 0", null );

        while ( rs.moveToNext()) {
            recs.add(new MyModel.CartItem(rs.getString(rs.getColumnIndex("NAME")),
                    rs.getString(rs.getColumnIndex("SELLINGPRICE")),   "0",
                    rs.getString(rs.getColumnIndex("ITEMID")),
                    rs.getString(rs.getColumnIndex("QUANTITY")),
                    "0",
                    rs.getString(rs.getColumnIndex("ITEMNUMBER")),
                    rs.getString(rs.getColumnIndex("VAT")),
                    rs.getString(rs.getColumnIndex("PIC1")),
                    rs.getString(rs.getColumnIndex("ITEMTYPE")),
                    rs.getString(rs.getColumnIndex("SIZETEXT")),
                    rs.getString(rs.getColumnIndex("ID")),
                    rs.getString(rs.getColumnIndex("EXECUTOR")),
                    rs.getString(rs.getColumnIndex("EXECUTORNAME"))
            ));

       /*     String name, String sellprice, String total,String id, String qty, String purchase,
                    String num, String vat, String pic1, String type, String sizetext, String cartid,
                    String executor, String executorname*/
        }

        ln.removeAllViews(); ln.setPadding(bigpad , 0, bigpad , 0);

        cartitems.clear();
        if (ln != null){
            MaterialButton bt = (MaterialButton) findViewById(savebtnid);
            if (bt != null){bt.setEnabled(true);}
            if (recs.size() <= 0){
                TextView tv = new TextView(Transact.this); ln.addView(tv);
                tv.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.2)));
                String th ="There Are No Items In Your Cart. Select \"Add Items\" To Add Items To Your Cart";

                th +=  System.lineSeparator() +System.lineSeparator() + "Click The Button Below To Check Out Video Tutorials On Youtube";


                tv.setText(th);
                tv.setTextSize(fontmedium); tv.setLines(3);

                ArrayList<Integer> des = md.applydesign(Transact.this, "Info", DS);
                tv.setBackgroundColor(md.getint(des,0));
                tv.setTextColor(md.getint(des,1));


                MaterialButton tt = md.makebutton(Transact.this, "Check Youtube", "",
                        0, "General", DS,
                        fontmedium, (int)(width * 0.98), (int) (height * 0.1));   ln.addView(tt);

                tt.setOnClickListener(new View.OnClickListener() {  public void onClick(View view) {

                    String url = "https://www.youtube.com/channel/UC_s4AJjGPoz98-i1BFQiOLg";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                } });



            }else{
                for (int i = 0; i < recs.size(); i++){
                    showcartitem(i, recs.get(i).ID,md.parsefloat(recs.get(i).Quantity), md.parsefloat(recs.get(i).SellingPrice), recs.get(i).VAT,
                            recs.get(i).ItemNumber, recs.get(i).ItemType, recs.get(i).Name, recs.get(i).Pic1, recs.get(i).SizeText,
                            recs.get(i).CartID, recs.get(i).Executor, recs.get(i).ExecutorName);
                }
            }
        }
        finishcartdisplay();
    }








}
package com.example.nzeluplus;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/*public class Orders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
    }
} */



public class Orders extends AppCompatActivity {

    LinearLayout lin; RelativeLayout menulin;
    final Integer pbarid = 243, ftv = 6390, scrlid = 839152, nscid = 5463, nestlin =  37542;
    boolean dataSuccess = false;
    MyModel md = new MyModel();
    int delay = 5000, height = MainActivity.height, width = MainActivity.width, level = 1;
    String  displaystring = "", rowid, tablestring = "", orderitemtype = "",
            tbname  = "",  genrulestring ="", subtbname = "", substring = "";

    String android_id = MainActivity.android_id,
            ds = MainActivity.DisplayString, Executor = MainActivity.Executor, uid = MainActivity.uref;
    ProgressBar pbbar;   String[] rec;

    SQLiteDatabase mydatabase;

    int fontsmall= md.fontsmall, fontmedium = md.fontmedium; boolean smallscreen = false;
    boolean valeron = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activities);

        DisplayMetrics metrics = new DisplayMetrics();
        (Orders.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;
        width = metrics.widthPixels;

        height = (int) (height*0.97);
        if (height <= md.heightcut){height = (int)(height * 0.91);
        smallscreen = true; fontsmall -= 1; fontmedium -= 2;}

        lin = (LinearLayout) findViewById(R.id.mainlinear);

        lin.setBackgroundResource(R.drawable.backgroundxml);
        lin.setPadding(10, 0, 10, 0);
        width =  width - 20;
        lin = md. setbackground(lin);


       /* Intent intent = getIntent();
        Bundle b = intent.getExtras();
        receivearray = b.getStringArray("key");

        themode = md.getarray(receivearray, 0);
        displaystring = md.getarray(receivearray, 1);
        long exc = md.parselong(md.getarray(receivearray, 2));
        if (exc == 0){exc = 3;}
        Executor = String.valueOf(exc);
        if (themode.contains("~")){
            table = md.breakurl(themode, 1, "~");
            themode = md.before(themode, "~");
        }*/

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        rec = b.getStringArray("key");
        if (rec != null){

            displaystring = md.getarray(rec, 0); // the user reference number
            uid = md.getarray(rec, 1); // the user reference number
            Executor = md.getarray(rec, 2);

            tbname = md.getarray(rec, 3);
            rowid = md.getarray(rec, 4);
            subtbname =  md.getarray(rec, 5);
            substring =  md.getarray(rec, 6);

            if (!md.getarray(rec, 5).equals("")){
                tablestring = md.getarray(rec, 5);
            }

        }else{
            String wb = b.getString("webkey");
            if (wb != null){
                ArrayList<String> ks = md.makearrayfromstring(wb, "|");
                displaystring = md.getstring(ks, 0);
                uid = md.getstring(ks,1);
                Executor = md.getstring(ks, 2);

                tbname = md.getstring(ks, 3);
                rowid = md.getstring(ks, 4);
                subtbname =  md.getstring(ks, 5);
                substring =  md.getstring(ks, 6);

                if (!md.getstring(ks, 5).equals("")){
                    tablestring = md.getstring(ks, 5);
                }

            }
        }

        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);

        valeron = md.DbName.equals("valeronpro");
        startoff();
    }






    int linid = 895321, lineheight = (int) (height * 0.05);
   ArrayList<String> genlist  = new ArrayList<>(), genrules = new ArrayList<>(), gencols = new ArrayList<>(), gendisplaycols = new ArrayList<>();

    public void startoff(){

        if (lin != null){

            lin.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            lin.removeAllViews();
            NestedScrollView scrl = new NestedScrollView(this); scrl.setId(scrlid);
            scrl.setPadding(0, 20, 0, 5);
            scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)); ;
            lin.addView(scrl);

            LinearLayout ln = new LinearLayout(this); ln.setId(linid);
            ln.setOrientation(LinearLayout.VERTICAL); scrl.addView(ln);


            if (tablestring.equals("")){

                MaterialTextView mt = new MaterialTextView(this);  mt.setHeight(lineheight);
                mt.setText("Loading " + md.returndisplay(tbname));  mt.setWidth(width);
                mt = md.textviewdesign(Orders.this, mt, "Info", ds);  ln.addView(mt); mt.setTextSize(md.fontmedium);

                ProgressBar pb = new ProgressBar(this); pb.setId(pbarid); pb.setVisibility(View.VISIBLE);
                ln.addView(pb);

                redogeneral();

            }else{

                genlist  =  md.makearrayfromstring(tablestring, "¬");
                genrulestring = genlist.get(0);
                genrules = md.makearrayfromstring(genrulestring, "#");
                gencols = md.makearrayfromstring(genlist.get(1), "~");
                gendisplaycols = md.makearrayfromstring(genrules.get(9), "~");


                String bookingstatus = "";
                for (int i = 2; i < genlist.size(); i++){
                    ArrayList<String> row = md.makearrayfromstring(genlist.get(i), "~");
                    ArrayList<String> thisrow = md.makearrayfromstring( md.rowstring(gencols, row), "|");
                    String rid = md.rowoption(thisrow, "ID");

                    if (rid.equals(rowid)){
                        if (tbname.equals("CustomerOrderItems") || tbname.equals("CustomerOrders")){
                            orderitemtype = md.rowoption(thisrow, "ValueItemType");   }
                        String vitemtype = md.rowoption(thisrow, "ValueItemType");
                        bookingstatus  = md.rowoption(thisrow, "ValueBookingStatus");

                        boolean showitemnum = md.appsetting("ShowItemNumber", displaystring).toLowerCase()
                                .equals("true");

                        for (int d = 0; d < gendisplaycols.size(); d++){
                            boolean addit = true;
                            if (gendisplaycols.get(d).equals("Status") & vitemtype.equals("2")){ addit =false;  }
                            else  if (gendisplaycols.get(d).equals("BookingStatus") &
                                    vitemtype.equals("1")){addit =false; }
                            else  if (gendisplaycols.get(d).equals("ItemNumber") & !showitemnum){addit =false;   }
                            if (md.before(gendisplaycols.get(d), "^").equals(tbname)){
                                String clstring = md.breakurl(gendisplaycols.get(d), 1, "^");
                                if (clstring.contains("_")){
                                    ArrayList<String> cls = md.makearrayfromstring(clstring, "_");

                                    for (int c = 0; c < cls.size(); c++){
                                        String cl = cls.get(c);
                                        String value = md.rowoption(thisrow, cl);
                                        boolean go = addit;
                                        String cdis = md.returndisplay(cls.get(c));
                                        if ((cl.equals("Quantity"))){ cdis = md.appsetting("QuantityName", displaystring);}
                                        else if ((cl.equals("Item"))){ cdis = md.appsetting("ItemNameSingular", displaystring);}
                                        else if ((cl.equals("Executor"))){ cdis = md.appsetting("ExecutorName", displaystring);}
                                        else if (vitemtype.equals("2") &  (cl.equals("Size") || cl.equals("ItemNumber")
                                                || cl.equals("ShippingFee")|| cl.equals("SubTotal")|| cl.equals("Status"))){ go = false;}
                                        else if (!vitemtype.equals("2") &
                                                (cl.equals("StartTime") || cl.equals("EndTime") || cl.equals("Executor"))){ go = false;}

                                        if (go){
                                            MaterialTextView mt = new MaterialTextView(this);  mt.setHeight(lineheight);
                                            mt.setText(cdis +  ": " + value);  mt.setWidth(width);
                                            mt = md.textviewdesign(Orders.this, mt, "Info", ds);
                                            mt.setGravity(Gravity.CENTER_HORIZONTAL);
                                            ln.addView(mt); mt.setTextSize(md.fontmedium);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!orderitemtype.equals("") || tbname.equals("PurchaseOrders")){editorderitem();}
            }
        }

    }



    int ratid1 = 7762490, ratid2 = ratid1 + 1, ratid3 = ratid2 + 1, ratid4 = ratid3 + 1, ratid5 = ratid4 + 1, cmtid = 8941532;
    int[] stars = new int[]{ratid1, ratid2, ratid3, ratid4, ratid5};
    public void editorderitem(){

        LinearLayout lin = (LinearLayout) findViewById(linid );

        if (!Executor.equals("2")){

            if (!valeron){

                MaterialTextView mt = new MaterialTextView(this);  mt.setHeight(lineheight);
                mt.setText("Please Rate This " + MainActivity.itemname);  mt.setWidth(width);
                mt = md.textviewdesign(Orders.this, mt, "Info", ds); mt.setGravity(Gravity.CENTER_HORIZONTAL);
                lin.addView(mt); mt.setTextSize(md.fontmedium);

                int outer =  (int)  (width * 0.2), inner =  (int)(0.25 * (width - (outer * 2) - ((lineheight * 1.2) * 5)));
                LinearLayout lnr = new LinearLayout(this);
                lnr.setLayoutParams(new LinearLayout.LayoutParams(width, lineheight));
                lnr.setPadding(outer, 0,outer , 0);
                lin.addView(lnr);


                int rating = 0; String comments = "";

                genlist  =  md.makearrayfromstring(tablestring, "¬");
                genrulestring = genlist.get(0);
                genrules = md.makearrayfromstring(genrulestring, "#");
                gencols = md.makearrayfromstring(genlist.get(1), "~");
                gendisplaycols = md.makearrayfromstring(genrules.get(9), "~");

                for (int i = 2; i < genlist.size(); i++){
                    ArrayList<String> row = md.makearrayfromstring(genlist.get(i), "~");
                    ArrayList<String> thisrow = md.makearrayfromstring( md.rowstring(gencols, row), "|");
                    String rid = md.rowoption(thisrow, "ID");

                    if (rid.equals(rowid)){
                        rating = md.parseint(md.rowoption(thisrow, "Rating"));
                        comments = md.rowoption(thisrow, "Comments");
                    }
                }

                for (int i   = 0; i < stars.length; i++){
                    final int starid = stars[i], index = i;
                    Button mb = new Button(this);  mb.setId(starid);
                    if ((i + 1) <= rating){
                        mb.setBackgroundResource(R.drawable.ic_baseline_star_rate_24);
                    }else{
                        mb.setBackgroundResource(R.drawable.ic_baseline_dullstar_rate_24);
                    }

                    mb.setOnClickListener(new View.OnClickListener() {   @Override public void onClick(View view)
                    { dorate(rowid,  index); } });
                    if (i > 0){
                        View vw = new View(Orders.this); vw.setLayoutParams(new LinearLayout.LayoutParams(inner, lineheight));
                        lnr.addView(vw);
                    }

                    lnr.addView(mb);
                    mb.setLayoutParams(new LinearLayout.LayoutParams((int)((lineheight * 1.2)), lineheight));

                }

                EditText eddt = new EditText(this);  eddt.setHint("Comments");  eddt.setId(cmtid);lin.addView(eddt);

                ArrayList<Integer> des = md.applydesign(Orders.this, "Input", ds);  eddt.setBackgroundColor(des.get(0));
                eddt.setTextColor(des.get(1)); eddt.setLines(4); eddt.setText(comments);

                MaterialButton mb = new MaterialButton(this);  mb.setHeight(lineheight);  mb.setText("Save Comments" );
                mb.setWidth((int)(width)); mb = md.buttondesign(Orders.this, mb, "Go", ds); lin.addView(mb); mb.setTextSize(fontsmall);
                mb.setOnClickListener(   new View.OnClickListener() {  @Override
                                         public void onClick(View view) {

                                             EditText ed = (EditText) findViewById(cmtid); String cms = ed.getText().toString();
                                             if (!cms.equals("")){
                                                 appsettable(tbname, "Comments", rowid, cms, cms, "added",    new MainActivity.VolleyCallBack() {
                                                     @Override
                                                     public void onSuccess() {
                                                         String rm = "Comments Saved!!";
                                                         if (!md.appsetting("CommentsThankYouMessage",ds).equals("")){
                                                             rm = md.appsetting("CommentsThankYouMessage", ds);
                                                         }

                                                         finishset(rm);
                                                     }
                                                 }, 0, Orders.this);
                                             }

                                         }
                                         }
                );
            }


            if (!substring.equals("")){
                int ww = (int)(0.98*(width * 0.5)), bth = (int)(height * 0.09);
                if(valeron){
                    ww = (int)(0.98*(width * 0.32));
                    bth = (int)(height * 0.09);
                }
                lineheight = (int) (height * 0.07);

                /*    MaterialButton mb = new MaterialButton(this);  mb.setHeight(bth);
                mb.setText("Download" );
                mb.setWidth(ww); mb = md.buttondesign(Orders.this, mb, "Go", ds); lin.addView(mb); mb.setTextSize(fontsmall);
                mb.setOnClickListener(   new View.OnClickListener() {  @Override  public void onClick(View view) {
                    exporttoexcel(0); }   }  );*/

                MaterialButton   mb = new MaterialButton(this);  mb.setHeight(bth);  mb.setText("Send To Email" );
                mb.setWidth(ww); mb = md.buttondesign(Orders.this, mb, "Go", ds); lin.addView(mb); mb.setTextSize(fontsmall);
                mb.setOnClickListener(   new View.OnClickListener() {  @Override  public void onClick(View view) {
                    exporttoexcel(1); }   }  );


                if (valeron){
                    mb = new MaterialButton(this);  mb.setHeight(bth);  mb.setText("Copy To Clipboard" );
                    mb.setWidth(ww); mb = md.buttondesign(Orders.this, mb, "Go", ds); lin.addView(mb); mb.setTextSize(fontsmall);
                    mb.setOnClickListener(   new View.OnClickListener() {  @Override  public void onClick(View view) {
                        exporttoexcel(2); }   }  );

                }
            }

            ProgressBar pb = new ProgressBar(this); pb.setId(pbarid); lin.addView(pb); pb.setVisibility(View.GONE);

            String ctype = "Cancel", cms = "Cancel This "+
                    md.appsetting("ItemNameSingular", displaystring), vb = "Cancel", val = "6";

        }

        if (!valeron ){
            MaterialButton mb = new MaterialButton(this);  mb.setHeight(lineheight);  mb.setText("Set Status");
            mb.setWidth((int)(width)); mb = md.buttondesign(Orders.this, mb, "Cancel", ds); lin.addView(mb); mb.setTextSize(fontsmall);
            mb.setOnClickListener(   new View.OnClickListener() {  @Override
                                     public void onClick(View view) {

                                         if (tbname.equals("CustomerOrderItems")){
                                             startsetstatus("Set Status", tbname, 0, "BookingStatus", "bigint", false);

                                         }
else if (tbname.equals("CustomerOrders")){
                                             startsetstatus("Set Status", tbname, 0, "Status", "bigint", false);

                                         }
                                     }
                                     }
            );
        }
    }


    boolean fkSuccess = false; String fkresult = "", fkcol = "", fkid = "", fkdisp = "", fktb = "", fktitle = "";
    int fkcontrol = 0;
    public void startsetstatus(String title, String table, int controlid, String col, String datatype, boolean istime){
        fkcontrol = controlid;
        fkcol = col;  fktb  = table;  fktitle = title;
        md.toast(Orders.this, "Loading " + md.returndisplay(col) + " Options");
        getfks(table, col,  new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showfks();
            }
        }, 0, Orders.this);
    }


    public void getfks(String table, String col, final MainActivity.VolleyCallBack callBack,
                       int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "getfkfulltable";

        url = url + "?code="+md.auth(Orders.this, mydatabase,android_id, uid)+"~"+table+"~"+col;
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
                    md.toast(Orders.this, "Failed To Reach Server... Retrying");
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
            /*ArrayList<String> jj =  md.makearrayfromstring(fkresult, "¬");
            for (int i = 0; i < jj.size(); i++)
            {
                String id = md.before(jj.get(i),  "~");
                String vl = md.breakurl(jj.get(i), 1, "~");
                fkviewlist.add(vl);
                fkidlist.add(id);
            }*/


            ArrayList<String> list  =  md.makearrayfromstring(fkresult, "¬");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            for (int c = 2; c < list.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                String idd =  md.before(md.getstring(list, c), "~");
                String name =  md.coloption(cols, "Name", row).Value;
                boolean go = true;
                if (tbname.equals("CustomerOrders")) {
                    String admin =  md.coloption(cols, "Admin", row).Value;
                    if (admin.toLowerCase().equals("true") & md.parseint(Executor) > 2){
                        go = false;
                    }
                }

                if (go){
                    fkviewlist.add(name);
                    fkidlist.add(idd);
                }
            }

            showDialog(555);
        }
        else  {
        md.toast(Orders.this, " Error, please try again");
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {

        //   dialogcancel();
        // TODO Auto-generated method stub
        android.app.AlertDialog.Builder  builder = new android.app.AlertDialog.Builder(this);

        if (id == 555){
            LinearLayout ll = new LinearLayout(Orders.this);
            ll.setOrientation(LinearLayout.VERTICAL);
            final ListView lv = new ListView(Orders.this);
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(Orders.this,
                    android.R.layout.simple_list_item_1,  fkviewlist );
            lv.setAdapter(itemsAdapter);
            ll.addView(lv);
            builder.setView(ll);

            builder.setTitle(fktitle);

            AlertDialog dialog = builder.create();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,  long id) {
                    try{
                        fkid = fkidlist.get(position);
                        fkdisp = fkviewlist.get(position);
                        md.toast(Orders.this, fkdisp);
                        dialog.cancel();
                        appsetstatus(fktb, fkcol, rowid, fkid, fkdisp, "changed",    new MainActivity.VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                finishstatus( md.returndisplay(fkcol)+" Saved!");
                                redogeneral();
                            }
                        }, 0, Orders.this);

                    }catch(Exception c){
                       md.toast(Orders.this, "ERROR: " + c.getMessage());
                    }

                }
            });
            return  dialog;
        }

        return null;
    }

    String stresult = ""; boolean stsuccess = false;
    public void appsetstatus(String table,  String column, String rowid,String value, String disp, String verb, final MainActivity.VolleyCallBack callBack,
                            int attempts, Context cnt) {

        ProgressBar pb = (ProgressBar) findViewById(pbarid); pb.setVisibility(View.VISIBLE);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "set";

        url = url + "?code="+md.auth(Orders.this, mydatabase, android_id, uid)
                +"~"+table+"~"+column+"~"+rowid+"~"+value  +"~"+disp+"~"+verb;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stresult = response;
                        stsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stsuccess = false;
                stresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    appsetstatus( table,column, rowid,  value,disp, verb, callBack,  attempt, cnt);
                }else{
                    stresult =  "";
                  md.toast(Orders.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void finishstatus(String savemessage){

        try{   pbbar = (ProgressBar) findViewById(pbarid); pbbar.setVisibility(View.GONE);  }catch (Exception xx){}
        if (setsuccess ){
            md.toast(Orders.this, savemessage);
        }
        else  {
            md.toast(Orders.this, " Error, please try again");
        }
    }

















    public void dorate(String rowid,int index){
        for (int i   = 0; i < stars.length; i++){
            final int starid = stars[i];
            Button mb = (Button) findViewById(starid);
            if (i <= index){
                mb.setBackgroundResource(R.drawable.ic_baseline_star_rate_24);
            }else{
                mb.setBackgroundResource(R.drawable.ic_baseline_dullstar_rate_24);
            }
        }

        appsettable(tbname, "Rating", rowid, String.valueOf((index + 1)), String.valueOf((index + 1)), "set",    new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess()

            {
                String rm = "Rating Saved!";
                if (!md.appsetting("RatingsThankYouMessage",ds).equals("")){
                    rm = md.appsetting("RatingsThankYouMessage", ds);
                }
                finishset(rm);
            }
        }, 0, Orders.this);
    }

    String setresult = ""; boolean setsuccess = false;
    public void appsettable(String table,  String column, String rowid,String value, String display, String verb, final MainActivity.VolleyCallBack callBack,
                            int attempts, Context cnt) {

        ProgressBar pb = (ProgressBar) findViewById(pbarid); pb.setVisibility(View.VISIBLE);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "set";

        url = url + "?code="+md.auth(Orders.this, mydatabase, android_id, uid)
                +"~"+table+"~"+column+"~"+rowid+"~"+value+"~"+display+"~"+ verb;
        final int attempt = attempts + 1;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setresult = response;
                        setsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setsuccess = false;
                setresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    appsettable( table,column, rowid,  value, display, verb, callBack,  attempt, cnt);
                }else{
                    setresult =  "";
                    md.toast(Orders.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void finishset(String savemessage){

        try{   pbbar = (ProgressBar) findViewById(pbarid); pbbar.setVisibility(View.GONE);  }catch (Exception xx){}
        if (setsuccess ){
           md.toast(Orders.this, savemessage);
        }
        else  {
            md.toast(Orders.this, " Error, please try again");
        }
    }

    String tbresult = ""; boolean tbsuccess = false;
    public void gettable(String table,   String rowid,  final MainActivity.VolleyCallBack callBack,
                         int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "gettable";

        final int attempt = attempts + 1;

        url = url + "?code="+md.auth(Orders.this, mydatabase, android_id, uid)+"~"+table + "^|ID^"+ rowid;

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
                if (attempt < md.attempts){
                    gettable( table, rowid,  callBack,  attempt, cnt);
                }else{
                    tbresult =  "";
                    md.toast(Orders.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void redogeneral(){
        try{ProgressBar pbbar = (ProgressBar) findViewById(pbarid); pbbar.setVisibility(View.GONE);  }catch (Exception xx){}

        gettable(tbname, rowid,    new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                if (tbsuccess ){
                    tablestring = tbresult;
                    startoff();
                }
                else  {
                    md.toast(Orders.this, " Error, please try again");
                }
            }
        }, 0, this);

    }































    List<String[]> csvdata = new ArrayList<String[]>();
    public void exporttoexcel(int sendtype){
        csvdata = new ArrayList<String[]>(); String styles = "", clp = "";
        genlist  =  md.makearrayfromstring(tablestring, "¬");
        genrulestring = genlist.get(0);
        genrules = md.makearrayfromstring(genrulestring, "#");
        gencols = md.makearrayfromstring(genlist.get(1), "~");
        gendisplaycols = md.makearrayfromstring(genrules.get(9), "~");
        String totalv = "";
        if (tbname.equals("CustomerOrders")){

            MyModel.option oi = md.sqlupdate(new ArrayList<>(),  mydatabase);
            String tpin = oi.Option9;
            String companyname = oi.Option10;
            String contacts = oi.Option11;

            csvdata.add(new String[]{companyname,"", "", "", "", "", ""});
            csvdata.add(new String[]{"TPIN:",tpin, "", "", "", "", ""});
            csvdata.add(new String[]{"Contacts:",contacts, "", "", "", "", ""});

            clp += companyname + "\n";
            clp += "TPIN: "+tpin + "\n";
            clp += "Contacts: "+contacts + "\n";
        }

        int topstyle = 0;
        for (int i = 2; i < genlist.size(); i++){
            ArrayList<String> row = md.makearrayfromstring(genlist.get(i), "~");
            ArrayList<String> thisrow = md.makearrayfromstring( md.rowstring(gencols, row), "|");
            String rid = md.rowoption(thisrow, "ID");

            if (rid.equals(rowid)){
                if (tbname.equals("CustomerOrderItems") || tbname.equals("CustomerOrders")){
                    orderitemtype = md.rowoption(thisrow, "ValueItemType");   }

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
                                    if (cls.get(c).equals("Total")){
                                        totalv = value;
                                    }else{
                                        csvdata.add(new String[]{cdis +":",value, "", "", "", "", ""});
                                        clp += cdis +":"+value + "\n";
                                        if (topstyle < 3){
                                            styles += "^"+String.valueOf((csvdata.size() - 1));
                                            topstyle++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        csvdata.add(new String[]{ "", "", "", "", "", "", ""});
        clp +=  "\n";


        ArrayList<String> logtbs = md.makearrayfromstring(substring, "|"), itemsadded  = new ArrayList<>();
        if (logtbs.size() == 0){logtbs.add(substring);}

        if (logtbs.size() > 1){

            ArrayList<String> tb1  =  md.makearrayfromstring(logtbs.get(0), "¬");
            ArrayList<String>  cols = md.makearrayfromstring(tb1.get(1), "~");


            String xxname = md.appsetting("ExecutorName", displaystring);
            if (orderitemtype.equals("2")){

                csvdata.add(new String[]{"Name","Number", "Price ("+ MainActivity.currencysymbol +")", "Start Time","EndTime", xxname,"Total"});
            }else{
                csvdata.add(new String[]{"Name", "Number", "Price ("+ MainActivity.currencysymbol +")", "Quantity", "VAT", "Total", ""});
            }
            styles += "^"+String.valueOf((csvdata.size() - 1));

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
                        price  = String.valueOf(md.round(md.parsefloat(price), 2));

                        if (orderitemtype.equals("2")){
                            csvdata.add(new String[]{itemname ,   quantity, price, st, et,exx, total});
                        }else{
                            csvdata.add(new String[]{itemname ,itemnumber ,  price,  quantity, vat, total, ""});
                        }
                        String vt = ""; if (md.parsefloat(vat) > 0){vt = " (incl. "+vat+"% VAT )";}
                        clp += itemname +" x "+ quantity + " @" +price+ vt+" = "+total+"\n";
                        if (i % 2 != 0){
                            styles += "^"+String.valueOf((csvdata.size() - 1));
                        }
                    }
                }
            }
        }
        csvdata.add(new String[]{"Total:",totalv, "", "", "", "", ""}); styles += "^"+String.valueOf((csvdata.size() - 1));
        clp += "Total: "+totalv ;

        if (sendtype == 0){
            String pms = "?code="+md.auth(Orders.this, mydatabase, android_id, uid)+"~"+md.returndisplay(tbname)+"~";
            for (int i  = 0;i < csvdata.size(); i++){
                pms +=  "|";
                for (int c = 0; c < csvdata.get(i).length; c++){
                    pms += "^" + csvdata.get(i)[c];
                }
            }
            pms += "~" + styles;
            String url = md.url + "appfiledownload" + pms;

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        else if (sendtype == 1){

            sendcsvreport(md.returndisplay(tbname), csvdata, styles,  new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {  csvsent(); } }, 0);
        }else{
            md.clipboard(Orders.this, clp);
            md.toast(Orders.this, md.returndisplay(tbname) + " Copied To Clipboard");
        }

        /*String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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

            md.toast(Orders.this, "File Saved In Downloads Folder as " + fileName);


        } catch (IOException e) {
            md.toast(Orders.this, e.getMessage());
        }*/
    }

    String csvstring; boolean csvSuccess;
    public void sendcsvreport(String name, List<String[]> csvdata, String styles, final MainActivity.VolleyCallBack callBack, int attempt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(Orders.this);
        String pms = "?code="+md.auth(Orders.this, mydatabase, android_id, uid)+"~"+name+"~";
        for (int i  = 0;i < csvdata.size(); i++){
            pms +=  "|";
            for (int c = 0; c < csvdata.get(i).length; c++){
                //if (!csvdata.get(i)[c].equals("")){}
                pms += "^" + csvdata.get(i)[c];
            }
        }
        pms += "~" + styles;
        String url = md.url + "sendcsvreport" + pms;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        csvstring = response;
                        csvSuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){
                    sendcsvreport(name, csvdata, styles,  callBack,  attempt);
                }else{
                    md.toast(Orders.this, "Failed To Reach Server... Retrying");
                }

            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void csvsent(){
        // Display the first 500 characters of the response string.
        if (!csvSuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(Orders.this, z);
        }
        else if(csvSuccess) {
            md.toast(Orders.this, csvstring);
        }
    }





}

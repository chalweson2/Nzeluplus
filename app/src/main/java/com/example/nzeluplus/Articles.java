package com.example.nzeluplus;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.core.widget.NestedScrollView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class Articles extends AppCompatActivity {



    int height = 0, width = 0, arcatid = 406,arnameid = 407;
    String  android_id = "", uid = "", Executor  = "", an = "";
    MyModel md = new MyModel();  LinearLayout mainlin, sublin;
    boolean smallscreen =  false; int fontsmall = md.fontsmall;
    ProgressBar pbbar;
    String DS = "";
    SQLiteDatabase mydatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        DisplayMetrics metrics = new DisplayMetrics();
        (Articles.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;
        width = metrics.widthPixels;

        mainlin = (LinearLayout) findViewById(R.id.mainlinear);
        mainlin.setPadding(10, 0, 10, 0);
        mainlin.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        //mainlin = md. setbackground(mainlin);
        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        if (height <= md.heightcut){height = (int)(height * 0.91); smallscreen = true; fontsmall -= 1;}
        else{fontsmall = md.fontmedium;}

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        width = width - 20;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        String[] rec = b.getStringArray("key");
        if (rec != null){
            DS =  md.getarray(rec, 0);
            uid = md.getarray(rec, 1);
            Executor = md.getarray(rec, 2);
        }else{
            String wb = b.getString("webkey");
            if (wb != null){
                ArrayList<String> ks = md.makearrayfromstring(wb, "|");
                DS = md.getstring(ks, 0);
                uid = md.getstring(ks,1);
                Executor = md.getstring(ks, 2);
            }
        }

        setTitle(md.appsetting("ArticlesSearchName", DS) );

        an = md.appsetting("ArticlesName", DS);

        mydatabase = this.openOrCreateDatabase(md.DbName,this.MODE_PRIVATE,null);

        int topwidth = (int)(width * 0.96), topht = (int) (height * 0.08);

        MaterialButton bt = md.makebutton(this, "Search " + an
                , "", 0, "Go", DS, md.fontmedium,   topwidth,  topht); mainlin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searcharticles();
            }
        });


        NestedScrollView scrl = new NestedScrollView(this);
        scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height - topht)) ;
        mainlin.addView(scrl); scrl.setPadding(0, 4, 0, 4);

        sublin = new LinearLayout(this);
        sublin.setOrientation(LinearLayout.VERTICAL); scrl.addView(sublin);

        startoff();
    }





    String searchcategory  = "", searchname = "";
    public void startoff(){

        sublin.removeAllViews();
        MaterialTextView mt  = md.maketextview(this, "Loading " + md.appsetting("ArticlesName", DS)
                , "", 0, "Info", DS, md.fontmedium,   width,  (int) (height * 0.08), true);
        sublin.addView(mt);

        ProgressBar pb = new ProgressBar(this); pb.setVisibility(View.VISIBLE);
        sublin.addView(pb); pb.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(height * 0.08)));

        getarticles(searchcategory, searchname, new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() { showarticles(); }
        }, 0, this);
    }



    String adstring = ""; boolean addsuccess = false;
    public void getarticles(String category, String name, final MainActivity.VolleyCallBack callBack,
                            int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String   url = md.url + "getarticles?code="+md.auth(Articles.this, mydatabase, android_id, uid) + "~" +name + "~" + category;

        final int attempt = attempts + 1;
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
                if (attempt < md.attempts){getarticles(category, name, callBack,  attempt, cnt);
                }else{
                    addsuccess = false;
                    adstring =  "";
                    md.toast(Articles.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);

    }

    public void showarticles(){

        if (addsuccess  & sublin != null){
            ArrayList<String> list  =  md.makearrayfromstring(adstring, "¬");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            sublin.removeAllViews();

            if (list.size() <= 2){

            }
            else if (list.size() > 2){

                View vw = new View(this); vw.setBackgroundColor(md.lightgray);
                vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2)); sublin.addView(vw);

                int rowheight = (int)(height * 0.16);
                for (int c = 2; c < list.size(); c++){
                    int sid = 2+c+4592;
                    ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                    String artid =  md.before(md.getstring(list, c), "~");

                    String aid = artid;
                    String date = md.coloption(cols, "Created", row).Value;
                    String title = md.coloption(cols, "Title", row).Value;
                    String pic = md.coloption(cols, "Pic", row).Value;

                    date = date.substring(0, date.indexOf(" "));

                    LinearLayout lnh = new LinearLayout(this); lnh.setOrientation(LinearLayout.HORIZONTAL);

                    int namewidth = width - rowheight;

                    lnh.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight )); sublin.addView(lnh);

                    ImageView imgView = md.image(this, 0, DS);
                    imgView.setLayoutParams(new ViewGroup.LayoutParams(rowheight, (int)(rowheight*0.95))); lnh.addView(imgView);
                    String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ pic;
                    imgView = md.loadimage(getResources(), imgView, url ,rowheight,(int)(rowheight*0.9));
                    imgView.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                        viewarticle(aid );  } });

                    MaterialTextView nb = md.maketextview(this,  title,"", 0, "Info",
                            DS, fontsmall, namewidth, rowheight, true);lnh.addView(nb);
                    nb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                        viewarticle(aid );   } });


                    vw = md.line(this, width, 2, md.textcolor(DS)); sublin.addView(vw);
                }

                vw = md.line(this, width, rowheight, md.transparent); sublin.addView(vw);
            }
        }
        else  {
            md.toast(this, " Error, please try again");
        }


    }

    public void viewarticle(String articleid){

        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{uid, DS, Executor, articleid, adstring});
        Intent intent = new Intent(this.getApplicationContext(), ArticleView.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    LinearLayout cuslin;  Dialog cusdialog;
    public void searcharticles(){

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
        int  cushit = (int)(height*0.55),  cuswid = (int)(width*0.9);
        int cuslineht = (int)(cushit * 0.12);

        cusdialog.show();
        cuslin.setPadding(6, 0, 0, 0);



        MaterialTextView mt =  md.maketextview(this,  "Search " + an, "", 0, "Info",
                DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);mt.setTypeface(null, Typeface.BOLD);

        mt =  md.maketextview(this,  "Select " + an + " Category", "", 0, "Info",
                DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);

        MaterialButton bt  = md.makebutton(this, "Category"
                , "", arcatid,
                "General", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { gofk("ArticleCategory", arcatid);  }});


        mt =  md.maketextview(this,  "Or Search By Name", "", 0, "Info",
                DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);mt.setTypeface(null, Typeface.BOLD);


        EditText ed = md.edittext(this, arnameid, "Title", "", "", cuswid, cuslineht,
                fontsmall, DS); cuslin.addView(ed);

        bt  = md.makebutton(this, "Search" , "", 0,
                "Go", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {  finalsearcharticle();  }});

        View v = md.line(this, width, (int)(cuslineht * 0.4), md.transparent); cuslin.addView(v);


        pbbar = new ProgressBar(this); pbbar.setVisibility(View.GONE);
        pbbar.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht)); cuslin.addView(pbbar);

    }

    public void finalsearcharticle(){

        EditText ed = (EditText) cusdialog.findViewById(arnameid);
        if (ed != null){
            searchname = ed.getText().toString();
        }
        MaterialButton mb = (MaterialButton) cusdialog.findViewById(arnameid);
        if (mb != null){
            searchcategory = mb.getTag().toString();
        }

        cusdialog.cancel();
        startoff();
    }



    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    public void gofk(String col, int btnid){

        Intent intent = new Intent(this, Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "ForeignKey",  DS, uid, Executor,  "Articles", col, String.valueOf(btnid), ""});
        intent.putExtras(b);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);

       /* if (col.equals(("Topic"))){

            String cat = "";

            MaterialButton mb = (MaterialButton) cusdialog.findViewById(excatid);
            if (mb != null){
                cat = mb.getTag().toString();
            }
            if (!cat.equals("")){
                Intent intent = new Intent(this, Activities.class);
                Bundle b = new Bundle();
                b.putStringArray("key", new String[]{ "GetTable",  DS, MainActivity.Executor,  "Topics", "^|Category^=^"+cat, String.valueOf(btnid), ""});
                intent.putExtras(b);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }else{
                md.toast(this,"Please Select A " + md.appsetting("CategoryName", DS));
            }
        }else{

        }*/
    }

    // This method is called when the second activity finishes
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == this.RESULT_OK) {
                Bundle b = data.getExtras();
                String[] received = b.getStringArray("key");
                if (md.getarray(received, 0).equals("ForeignKey")){
                    String idd = md.getarray(received, 1);
                    String name = md.getarray(received, 2);
                    String fkcontrolid = md.getarray(received, 3);

                    MaterialButton mb = (MaterialButton) cusdialog.findViewById(md.parseint(fkcontrolid));
                    if (mb != null){   mb.setTag(idd); mb.setText(name);  }
                }else if (md.getarray(received, 0).equals("GetTable")){
                    String idd = md.getarray(received, 1);
                    String name = md.getarray(received, 2);
                    String fkcontrolid = md.getarray(received, 3);

                    MaterialButton mb = (MaterialButton) cusdialog.findViewById(md.parseint(fkcontrolid));
                    if (mb != null){   mb.setTag(idd); mb.setText(name);  }
                }
            }
        }
    }





}
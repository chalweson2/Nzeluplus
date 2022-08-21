package com.example.nzeluplus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ArticleView extends AppCompatActivity {


    LinearLayout mainlin, sublin, toplin;
    final Integer pbarid = 243, ftv = 6390, scrlid = 839152, nscid = 5463, nestlin =  37542;
    boolean dataSuccess = false;
    MyModel md = new MyModel();
    int delay = 5000, height = 0, width =  0, level = 1;
    String artid = "", articlestring = "",  Executor = "";
    String android_id = "", ds = "", uid = "";
    ProgressBar pbbar;   String[] receivearr; int textcolor = md.darkgray;

    SQLiteDatabase mydatabase;

    int fontsmall= md.fontsmall, fontmedium = md.fontmedium; boolean smallscreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        android_id = Settings.Secure.getString(ArticleView.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        mainlin = (LinearLayout) findViewById(R.id.mainlinear);
        mainlin.setPadding(10, 0, 10, 5);
        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        MyModel md = new MyModel();
        DisplayMetrics metrics = new DisplayMetrics();
        (ArticleView.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;  width = metrics.widthPixels;
        if (height <= md.heightcut){height = (int)(height * 0.91); smallscreen = true; fontsmall -= 1; fontmedium = md.fontsmall;}
        width = width - 22;

        ProgressBar pbbar = new ProgressBar(ArticleView.this);
        pbbar.setId(pbarid);
        pbbar.setLayoutParams(new LinearLayout.LayoutParams(width, (int) (height * 0.07)));
        mainlin.addView(pbbar);
        pbbar.setVisibility(View.VISIBLE);


        mainlin.removeAllViews();
        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        receivearr = b.getStringArray("key");

        //MainActivity.uref, ds, MainActivity.Executor, articleid, adstring
        uid =  md.getarray(receivearr, 0);
        ds = md.getarray(receivearr, 1);
        Executor = md.getarray(receivearr, 2);
        artid = md.getarray(receivearr, 3);
        articlestring = md.getarray(receivearr, 4);


        int topht = (int)(height * 0.07);
        toplin = new LinearLayout(ArticleView.this); mainlin.addView(toplin);
        toplin.setLayoutParams(new LinearLayout.LayoutParams(width, topht));


        NestedScrollView scrl = new NestedScrollView(ArticleView.this);
        //scrl.setScrollbarFadingEnabled(false);
        scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.9))) ;
        mainlin.addView(scrl); scrl.setPadding(0, 4, 0, 4);


        sublin = new LinearLayout(ArticleView.this);
        sublin.setOrientation(LinearLayout.VERTICAL); scrl.addView(sublin);

        MaterialTextView tv = md.maketextview(ArticleView.this, "Loading...",
                "", 0, "Info", ds, fontsmall, width,  topht, true); sublin.addView(tv);
        ProgressBar pb = new ProgressBar(ArticleView.this); pb.setId(pbarid);pb.setVisibility(View.VISIBLE);
        sublin.addView(pb);

     /*   int htt = (int) (height * 0.07);
        LinearLayout botlin = new LinearLayout(ArticleView.this); mainlin.addView(botlin);
        botlin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  htt));

        int hw = (int)(width * 0.15), outerwidth =  (int)(width * 0.2);
        View v = md.line(ArticleView.this,outerwidth, htt, md.transparent); botlin.addView(v);

        MaterialButton bt = md.makebutton(ArticleView.this, "", "", 0, "General", ds, fontmedium,(int) hw, htt);
        bt.setBackgroundResource(R.drawable.ic_baseline_search_24); botlin.addView(bt);

        bt = md.makebutton(ArticleView.this, "", "", 0, "General", ds, fontmedium,(int) hw, htt);
        bt.setBackgroundResource(R.drawable.ic_baseline_favorite_24); botlin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savearticle( new MainActivity.VolleyCallBack() {
                    @Override
                    public void onSuccess() { finishsavearticle(); }
                }, 0, ArticleView.this);
            }
        });

        bt = md.makebutton(ArticleView.this, "", "", 0, "General", ds, fontmedium,(int) hw, htt);
        bt.setBackgroundResource(R.drawable.ic_baseline_send_24); botlin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {  }  });

        bt = md.makebutton(ArticleView.this, "", "", 0, "General", ds, fontmedium,(int) hw, htt);
        bt.setBackgroundResource(R.drawable.ic_baseline_explore_24); botlin.addView(bt);
        v = md.line(ArticleView.this, outerwidth, htt, md.transparent); botlin.addView(v);*/


        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);

        String gtr = "0", artcat = "";
        if (articlestring.equals("")){gtr = "1";}
        getarticle( gtr, artcat, new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() { showarticle(); }
        }, 0, ArticleView.this);


    }

    String qstring = ""; boolean qsuccess = false;
    public void getarticle(String getarticlerow, String articlecategory, final MainActivity.VolleyCallBack callBack,  int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "articlehit?code="+md.auth(ArticleView.this, mydatabase, android_id, uid)+"~"+artid+"~"+getarticlerow
                + "~" + articlecategory;

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
                if (attempt < md.attempts){getarticle(getarticlerow, articlecategory, callBack,  attempt, cnt);
                }else{
                    qsuccess = false;
                    qstring =  "";
                    md.toast(ArticleView.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);
    }

    String extrastring = "", catstring = "";
    public void  showarticle(){
        if (qsuccess  & sublin != null){


            ArrayList<String> tables =  md.makearrayfromstring(qstring ,"|");

            if (!md.getstring(tables, 0).equals("")){articlestring =md.getstring(tables, 0); }

            if (!md.getstring(tables, 1).equals("")){extrastring =md.getstring(tables, 1); }

            if (!md.getstring(tables, 0).equals("")){catstring =md.getstring(tables, 0); }

            startoff();
        }
        else  {
            md.toast(ArticleView.this, " Error, please try again");
        }
    }

    public void startoff() {


        sublin.removeAllViews();

        ArrayList<String> ptlist = md.makearrayfromstring(articlestring, "¬");
        ArrayList<String> ptcols = md.makearrayfromstring(md.getstring(ptlist, 1), "~");

        int rowheight = (int) (height * 0.08);
        for (int c = 2; c < ptlist.size(); c++) {
            ArrayList<String> row = md.makearrayfromstring(md.getstring(ptlist, c), "~");
            String idd = md.before(md.getstring(ptlist, c), "~");
            if (idd.equals(artid)) {
                String title = md.coloption(ptcols, "Title", row).Value;
                String pic = md.coloption(ptcols, "Pic", row).Value;
                String subt = md.coloption(ptcols, "Subtitle", row).Value;
                String date = md.coloption(ptcols, "Created", row).Value;
                if (date.contains(" ")) {
                    date = date.substring(0, date.indexOf(" "));
                }

                MaterialTextView mt = md.maketextview(this, title, "", 0, "Info", ds,
                        md.fontlarge, width, rowheight, false);
                mt.setTypeface(null, Typeface.BOLD);
                sublin.addView(mt);
                mt = md.setlines(mt, title, md.fontlarge, width, rowheight);

                if (!subt.equals("")) {
                    mt = md.maketextview(this, subt, "", 0, "Info", ds,
                            md.fontmedium, width, rowheight, false);
                    sublin.addView(mt);
                    mt = md.setlines(mt, subt, md.fontmedium, width, rowheight);
                }

                mt = md.maketextview(this, date, "", 0, "Info", ds,
                        md.fontsmall, width, rowheight, false);
                sublin.addView(mt);

                ImageView imgView = md.image(this, 0, ds);
                imgView.setLayoutParams(new ViewGroup.LayoutParams(width, (int) (width * 0.9)));
                sublin.addView(imgView);
                String url = "https://www.valeronpro.com/Content/SiteImages/" + md.Site + "/" + pic;
                imgView = md.loadimage(getResources(), imgView, url, width, (int) (width * 0.9));

                for (int i = 1; i <= 20; i++) {
                    String ipic = md.coloption(ptcols, "Pic" + String.valueOf(i), row).Value;
                    String spc = md.coloption(ptcols, "SpacesBefore" + String.valueOf(i), row).Value;
                    String itm = md.coloption(ptcols, "Item" + String.valueOf(i), row).Value;
                    String lnk = md.coloption(ptcols, "Link" + String.valueOf(i), row).Value;
                    if (ipic.equals("") & !itm.equals("")) {
                        View v = md.line(this, width, 4, md.transparent);
                        int mc2 =  md.countlines(itm,fontmedium, width);
                        mt = md.maketextview(this, itm, "", 0, "Info", ds,
                                fontmedium, width, mc2*((int)(fontmedium*2.4)), false);
                        mt.setLines(mc2);
                        sublin.addView(mt);
                        if (!lnk.equals("")) {
                            ArrayList<Integer> des = md.applydesign(this, "General", ds);
                            mt.setTypeface(null, Typeface.BOLD);
                            mt.setTextColor(des.get(0));
                        }
                    } else if (ipic.length() > 0) {
                        imgView = md.image(this, 0, ds);
                        imgView.setLayoutParams(new ViewGroup.LayoutParams(width, (int) (width * 0.9)));
                        sublin.addView(imgView);
                        url = "https://www.valeronpro.com/Content/SiteImages/" + md.Site + "/" + ipic;
                        imgView = md.loadimage(getResources(), imgView, url, width, (int) (width * 0.9));
                        if (!itm.equals("")) {
                            mt = md.maketextview(this, itm, "", 0, "Info", ds,
                                    fontsmall, width, rowheight, false);
                            sublin.addView(mt);
                            mt = md.setlines(mt, itm, fontsmall, width, rowheight);
                        }
                    }
                }
            }
        }


        View v = md.line(this, width, 2, textcolor); sublin.addView(v);
        v = md.line(this, width, 6, md.transparent);sublin.addView(v);


        MaterialTextView mt = md.maketextview(this, "You Might Also Like", "", 0, "Info", ds,
                md.fontlarge, width, rowheight, false);
        mt.setTypeface(null, Typeface.BOLD); sublin.addView(mt);
        ArrayList<String>  xtlist = md.makearrayfromstring(extrastring, "¬");
        ArrayList<String>  xtcols = md.makearrayfromstring(md.getstring(xtlist, 1), "~");

        for (int c = 2; c < xtlist.size(); c+= 2) {

            ArrayList<String> row = md.makearrayfromstring(md.getstring(xtlist, c), "~");
            String idd = md.before(md.getstring(xtlist, c), "~");


            String title = md.coloption(xtcols, "Title", row).Value;
            String pic = md.coloption(xtcols, "Pic", row).Value;

            ImageView imgView = md.image(this, 0, ds);
            imgView.setLayoutParams(new ViewGroup.LayoutParams(width, (int) (width * 0.9)));
            sublin.addView(imgView);
            String url = "https://www.valeronpro.com/Content/SiteImages/" + md.Site + "/" + pic;
            imgView = md.loadimage(getResources(), imgView, url, width, (int) (width * 0.9));
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewarticle(idd);
                }
            });

            int mc2 =  md.countlines(title,fontmedium, width);
            mt = md.maketextview(this, title, "", 0, "Info", ds,
                    fontmedium, width, mc2*((int)(fontmedium*2.4)), false); mt.setLines(mc2);
            mt.setTypeface(null, Typeface.BOLD);
            sublin.addView(mt);
            mt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewarticle(idd);
                }
            });


            v = md.line(this, width, 4, md.transparent);
        }



    }

    public void viewarticle(String articleid){

        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{uid, ds, Executor, articleid, extrastring});
        Intent intent = new Intent(this, ArticleView.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    String sstring = ""; boolean ssuccess = false;
    public void savearticle( final MainActivity.VolleyCallBack callBack,  int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "savearticle?code="+md.auth(ArticleView.this,mydatabase, android_id, uid)+"~"+artid;

        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        sstring  = response;
                        ssuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){savearticle( callBack,  attempt, cnt);
                }else{
                    ssuccess = false;
                    sstring =  "";
                    md.toast(ArticleView.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);
    }

    public void  finishsavearticle(){
        if (ssuccess ){

            md.toast(this, "Saved!");
        }
        else  {
            md.toast(ArticleView.this, " Error, please try again");
        }
    }

}
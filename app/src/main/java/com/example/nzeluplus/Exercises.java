package com.example.nzeluplus;



        import android.app.Dialog;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.graphics.PorterDuff;
        import android.graphics.Typeface;
        import android.provider.Settings;
        import androidx.appcompat.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.DisplayMetrics;
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
        import android.widget.TextView;
        import android.widget.Toast;

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

public class Exercises extends AppCompatActivity {

    int height = 0, width = 0, exnameid = 111, sublinid = 112,
            exsaveid = 113, exbrandid = 114, excatid = 115, extopid = 116, exminid = 117, excodeid =  118,
            exstdrunid = 119, exstdsearchid= 120, profilenameid = 121, profilebtnid = 122, textcolor = 0;
    String  profileid = "", profilename  = "", android_id = "", uid ="", Executor  = "";
    MyModel md = new MyModel();  LinearLayout mainlin, sublin;
    boolean  smallscreen =  false; int fontsmall = md.fontsmall;
    ProgressBar pbbar;
    ArrayList<MyModel.option> BrandList = new ArrayList<>();
    String DS = "";
    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        mainlin = (LinearLayout) findViewById(R.id.mainlinear);
        android_id = Settings.Secure.getString(Exercises.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;  width = metrics.widthPixels;

        mainlin.setPadding(10, 0, 10, 0);
        mainlin.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        // mainlin.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        //    mainlin = md. setbackground(mainlin);
        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        if (height <= md.heightcut){height = (int)(height * 0.91); smallscreen = true; fontsmall -= 1;}
        else{fontsmall = md.fontmedium;}

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        width = width - 20;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        String[] rec = b.getStringArray("key");
        if (rec != null){
            DS = md.getarray(rec, 0);
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

        setTitle(md.appsetting("ExercisesSearchName", DS));


        mydatabase = this.openOrCreateDatabase(md.DbName,this.MODE_PRIVATE,null);


        textcolor = md.parsecolor(md.appsetting("TextColor", DS));

        startoff();
    }

    public void startoff(){

        int topwidth = (int)(width * 0.98), rowheight = (int) (height * 0.08);

        String mss = "All " + md.appsetting("ProfilePlural", DS);

        LinearLayout toplin = new LinearLayout(this); toplin.setOrientation(LinearLayout.HORIZONTAL);
        toplin.setLayoutParams(new LinearLayout.LayoutParams(topwidth, rowheight)); mainlin.addView(toplin);
        MaterialButton bt = md.makebutton(this, mss
                , "", profilebtnid, "Go", DS, md.fontmedium,   (int)(topwidth - rowheight - 6),  rowheight); toplin.addView(bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { doprofiles(true);
            }
        });

        View v = md.line(Exercises.this, 2, rowheight, md.transparent); toplin.addView(v);

        MaterialButton   bb = md.makebutton(this,  ""
                , "", 0, "General", DS, md.fontmedium,   rowheight, rowheight); toplin.addView(bb);
        bb.setBackgroundResource(R.drawable.ic_baseline_add_box_24);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putStringArray("key", new String[]{ "Exercises",  DS, uid, Executor, "Exercises",  "",  "",   "",  ""});
                Intent intent = new Intent(getApplicationContext(), Activities.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });




        NestedScrollView scrl = new NestedScrollView(this);
        scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.88))) ;
        mainlin.addView(scrl); scrl.setPadding(0, 4, 0, 4);

        sublin = new LinearLayout(this); sublin.setId(sublinid);
        sublin.setOrientation(LinearLayout.VERTICAL); scrl.addView(sublin);

        loadexercises();
    }

    public void loadexercises(){
        sublin.removeAllViews();
        MaterialTextView mt  = md.maketextview(this, "Loading " + md.appsetting("ExercisesName", DS)
                , "", 0, "Info", DS, md.fontmedium,   width,  (int) (height * 0.08), true);
        sublin.addView(mt);


        ProgressBar pb = new ProgressBar(this); pb.setVisibility(View.VISIBLE);
        sublin.addView(pb); pb.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(height * 0.08)));

        getmyexercises( new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() { showexercises(); }
        }, 0, this);
    }

    String adstring = ""; boolean addsuccess = false;
    public void getmyexercises(final MainActivity.VolleyCallBack callBack,
                               int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String   url = md.url + "getmyexercises?code="+md.auth(Exercises.this, mydatabase, android_id, uid);

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
                if (attempt < md.attempts){getmyexercises(callBack,  attempt, cnt);
                }else{
                    addsuccess = false;
                    adstring =  "";
                    md.toast(Exercises.this,  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);  queue.add(stringRequest);

    }

    public void showexercises(){

        if (addsuccess  & sublin != null){
            String ads = adstring;
            ArrayList<String> list  =  md.makearrayfromstring(adstring, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            sublin.removeAllViews();

            int gocount = 0;
            if (list.size() == 2){
                View vw = new View(this); vw.setBackgroundColor(md.lightgray);
                vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2)); sublin.addView(vw);
            }
            else if (list.size() > 2){
                View vw = new View(this); vw.setBackgroundColor(md.lightgray);
                vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2)); sublin.addView(vw);

                for (int c = 2; c < list.size(); c++){
                    int sid = 2+c+4592;
                    ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                    String exid =  md.before(md.getstring(list, c), "~");
                    String extracolname = "", exercisecolname = "Name", exercisefk = "", extraending = "";
                    if (Executor.equals("3")){extracolname = "Exercise";exercisecolname = "Exercise"; exercisefk = "ValueExercise";
                        exid = md.coloption(cols, "ValueExercise", row).Value;
                        extraending = "Name";
                    }
                    String exd = exid;
                    String name =  md.coloption(cols,exercisecolname, row).Value;
                    String prf = md.coloption(cols, "ValueProfile", row).Value;
                    boolean go = true;
                    if (!profileid.equals("")){
                        if (!profileid.equals(prf)){go = false;}}

                    if (go){
                        gocount++;

                        String date = md.coloption(cols, "Created", row).Value;
                        String edt = md.coloption(cols, extracolname+"Editable", row).Value;
                        String avg = md.coloption(cols, extracolname+"AverageScore", row).Value;
                        String category = md.coloption(cols, extracolname +"Category" + extraending, row).Value;
                        String brand = md.coloption(cols, extracolname  +"Brand"+extraending, row).Value;
                        String topic = md.coloption(cols, extracolname  +"Topic"+extraending, row).Value;
                        String code = md.coloption(cols, extracolname+"Code", row).Value;
                        String mins = md.coloption(cols, extracolname+"NumberOfMinutes", row).Value;

                        String categoryval = md.coloption(cols, exercisefk +"Category", row).Value;
                        String brandval = md.coloption(cols, exercisefk +"Brand", row).Value;
                        String topicval = md.coloption(cols, exercisefk +"Topic", row).Value;

                        date = date.substring(0, date.indexOf(" "));

                        if (edt.toLowerCase().equals("true")){edt = "1";}else{edt = "0";}
                        String editable = edt;

                        LinearLayout lnh = new LinearLayout(this); lnh.setOrientation(LinearLayout.HORIZONTAL);

                        int rowheight = (int)(height * 0.12);
                        int imgid = 5545 + md.parseint(exid), nameid = 9432 + md.parseint(exid);
                        int namewidth = (int)(width * 0.3), iconwd = (int)((width-rowheight -namewidth) * 0.33);

                        lnh.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight )); sublin.addView(lnh);

                        ImageView imgView = md.imagename(this, category,imgid, rowheight , (int)(rowheight *0.9) );
                        lnh.addView(imgView);
                        imgView.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                            viewexercise(ads, exd );  } });

                        MaterialTextView nb = md.maketextview(this,  name,"", nameid, "Info",
                                DS, fontsmall, namewidth, rowheight, true);lnh.addView(nb);
                        nb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                            viewexercise(ads, exd);  } });

                        nb = md.maketextview(this, date , "", 0, "Info", DS, fontsmall,
                                iconwd*2, rowheight, false);  lnh.addView(nb);
                        nb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                            viewexercise(ads, exd);  } });


                        int pencilwidth = (int)(0.6*iconwd);
                        if (Executor.equals("3")){

                            int pct = 0;
                            // int answerd = md.parseint(md.coloption(cols, "QuestionsAnswered", row).Value);
                            int corrects = md.parseint(md.coloption(cols, "QuestionsCorrectlyAnswered", row).Value);
                            int all = md.parseint(md.coloption(cols, "QuestionsAnswered", row).Value);

                            int col = md.lightblue, tx = md.white;
                            if (corrects > 0){
                                pct = (int)(100*((float)(corrects) / all));
                                if (pct <= 90 & pct > 80){  col = md.green; }
                                else  if (pct <= 80 & pct > 70){  col = md.yellow; tx = md.black;}
                                else  if (pct <= 70 & pct > 60){  col = md.orange;  }
                                else  if (pct <= 60){  col = md.brightred; }
                            }

                            MaterialButton mb = md.makebutton(this, String.valueOf(pct )+ "%", "",
                                    0, "Info", DS, md.fontmedium, iconwd, iconwd); lnh.addView(mb);
                            mb.setBackgroundColor(col); mb.setTextColor(tx);

                            ArrayList<Integer> des = md.applydesign(this, "General", DS);
                            mb.setCornerRadius(des.get(2));
                            mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                                if (!editable.toLowerCase().equals("true")){md.toast(Exercises.this, "Cannot Edit This Entry");}
                                else{
                                    viewexercise(ads, exd);
                                }  } });

                        }else{
                            MaterialButton mb = md.makebutton(this, "", "", 0, "Info", DS, md.fontmedium, pencilwidth, pencilwidth);
                            mb.setBackgroundResource(R.drawable.ic_baseline_edit_24); lnh.addView(mb);
                            String editstring = "0"; if (editable.toLowerCase().equals("true")){editstring = "1";}
                            String eds = editstring;
                            mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                                if (!editable.toLowerCase().equals("true")){md.toast(Exercises.this, "Cannot Edit This Entry");}
                                else{
                                    newexercise(exd, name,  brand, category, topic, brandval, categoryval, topicval, code, mins, false, eds);
                                }  } });

                        }
                        vw = new View(this); vw.setBackgroundColor(md.lightgray);
                        vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2)); sublin.addView(vw);
                    }
                }

                LinearLayout lnh = new LinearLayout(this);   int rowheight = (int)(height * 0.12);
                lnh.setLayoutParams(new LinearLayout.LayoutParams(width, rowheight )); sublin.addView(lnh);

            }
            if (gocount == 0){
                String nn = "There Are No " + md.appsetting("ExercisesName", DS) + " Entries";
                if (!profilename.equals("")){nn += " For " + profilename;}
                nn += ". Click the + Button Above To Start A New " + md.appsetting("ExercisesName", DS);

                int mc2 =  md.countlines(nn, md.fontmedium, width);
                MaterialTextView nb = md.maketextview(this,  nn , "", 0, "Info", DS,
                        md.fontmedium, width, mc2*((int)(md.fontmedium*2.4)), true);
                nb.setLines(mc2); sublin.addView(nb);
            }
        }
        else  {
            md.toast(this, " Error, please try again");
        }

    }


    public void viewexercise(String exstring, String exid){

        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{DS, uid, Executor, exstring, exid, profileid});
        Intent intent = new Intent(this.getApplicationContext(), ExerciseExecute.class);
        intent.putExtras(b);
        startActivity(intent);
    }


    LinearLayout cuslin;  Dialog cusdialog;
    int cuswid = 0, cuslineht = 0, cushit = 0;
    public void startcusdialog(){

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
        cushit = (int)(height*0.55);
        cuswid = (int)(width*0.95);
        cuslineht = (int)(cushit * 0.12);

        cusdialog.show();
        cuslin.setPadding(6, 0, 0, 0);
    }

    public void exsettings(){
        startcusdialog();

        MaterialTextView mt =  md.maketextview(this,  "Settings", "", 0, "Info",
                DS, md.fontmedium, (int)(cuswid), cuslineht, true); cuslin.addView(mt);

        View v = md.line(Exercises.this, width, 2, textcolor); cuslin.addView(v);
        LinearLayout ln = new LinearLayout(this); cuslin.addView(ln); ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht));


        mt =  md.maketextview(this,  "Question Timer", "", 0, "Info",
                DS, md.fontmedium, (int)(cuswid*0.48), cuslineht, true); ln.addView(mt);


        ArrayList<String> times = new ArrayList<>(); times.add("Select Timer");
        times.add("30 Seconds"); times.add("1 Minute"); times.add("90 Seconds"); times.add("2 Minutes");
        times.add("3 Minutes"); times.add("5 Minutes");

        Spinner spinner = new Spinner(Exercises.this);
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
        ArrayAdapter<String> dataAdapter =  new ArrayAdapter<String>(Exercises.this,
                android.R.layout.simple_spinner_item, times);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  spinner.setAdapter(dataAdapter);





    }




    public void newexercise(String idd, String name, String brand, String category, String topic,
                            String brandval, String categoryval, String topicval, String code, String mins, boolean open, String editable){

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

        cusdialog.show();
        cuslin.setPadding(6, 0, 0, 0);
        int cuslineht = (int)(cushit * 0.12);


        String bn= md.appsetting("BrandName", DS), cn =
                md.appsetting("CategoryName", DS), xn =
                md.appsetting("ExercisesName", DS);

        String isnew = "New"; if (md.parseint(idd) > 0 & !Executor.equals("3")){isnew = "Edit";}
        MaterialTextView mt =  md.maketextview(this,  isnew + " " + xn, "", 0, "Info",
                DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);mt.setTypeface(null, Typeface.BOLD);



        LinearLayout.LayoutParams pms  = new LinearLayout.LayoutParams(cuswid, cuslineht);
        LinearLayout ln = new LinearLayout(this); ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setLayoutParams(pms);  cuslin.addView(ln);
        MaterialTextView tv = md.maketextview  (this,"Name",
                "", 0, "Info", DS, fontsmall, (int)(cuswid*0.3), cuslineht, false); ln.addView(tv);
        EditText ed  = md. edittext(this, exnameid, "Enter Name", name, "",
                (int)(cuswid*0.7), cuslineht, md.fontsmall, DS);  ln.addView(ed);


        ln = new LinearLayout(this); ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setLayoutParams(pms);  cuslin.addView(ln);
        tv = md.maketextview  (this,"Duration (Min)",
                "", 0, "Info", DS, fontsmall, (int)(cuswid*0.4), cuslineht, false); ln.addView(tv);
        ed  = md. edittext(this, exminid, "Duration (Minutes)", mins, "number",
                (int)(cuswid*0.6), cuslineht, md.fontsmall, DS);  ln.addView(ed);


        MaterialButton bt  = md.makebutton(this, md.appsetting("BrandName", DS)
                        + ": " + brand, brandval,
                exbrandid, "General", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { gofk("Brand", exbrandid);  }});


        bt  = md.makebutton(this, md.appsetting("CategoryName", DS)
                        + ": " + category, categoryval, excatid,
                "General", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { gofk("Category", excatid);  }});


        String tp = md.appsetting("TopicName", DS)
                + ": " + topic;
        if (Executor.equals("3")){tp = md.appsetting("TopicName", DS) + " (Optional)";}
        bt  = md.makebutton(this, tp, topicval, extopid,
                "General", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { gofk("Topic", extopid); }});

        if (code.equals("")){code = md.randomstring(); if (code.length() > 5){code = code.substring(0, 5);}}String cd  = code;
        ln = new LinearLayout(this); ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setLayoutParams(pms);  cuslin.addView(ln);
        tv = md.maketextview  (this,"Code: ",
                "", 0, "Info", DS, fontsmall, (int)(cuswid*0.3), cuslineht, false); ln.addView(tv);
        tv = md.maketextview  (this,code,
                "", 0, "Info", DS, fontsmall, (int)(cuswid*0.4), cuslineht, false); ln.addView(tv);
        bt  = md.makebutton(this, "", "", 0,"General", DS, fontsmall,  cuslineht, cuslineht);
        ln.addView(bt); bt.setBackgroundResource(R.drawable.ic_baseline_content_copy_24);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) { md.clipboard(Exercises.this, cd); md.toast(Exercises.this, "Code Copied"); }});

        pbbar = new ProgressBar(this); pbbar.setVisibility(View.GONE);
        pbbar.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht)); cuslin.addView(pbbar);

        String act = "Create";
        if (!open){act = "Save";}
        if (Executor.equals("3")){act = "Start";}
        bt  = md.makebutton(this, act , "", exsaveid,
                "Go", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {  createexercise(idd, cd, editable, open);  }});
    }

    public void createexercise(String idd, String code, String editable, boolean open){
        String nm = "", min = "";
        EditText ed = (EditText) cusdialog.findViewById(exnameid);
        if (ed != null){nm = ed.getText().toString();}
        String vals = "|Name^" + nm;

        ed = (EditText) cusdialog.findViewById(exminid);
        if (ed != null){min = ed.getText().toString();}
        vals += "|NumberOfMinutes^" + min;

        MaterialButton mb = (MaterialButton) cusdialog.findViewById(exbrandid);
        if (mb != null){
            vals += "|" + "Brand^" + mb.getTag().toString();
            mb = (MaterialButton) cusdialog.findViewById(excatid);
            if (mb != null){
                vals += "|" + "Category^" + mb.getTag().toString();
                mb = (MaterialButton) cusdialog.findViewById(extopid);
                if (mb != null){
                    vals += "|" + "Topic^" + mb.getTag().toString();
                }
            }
            vals += "|" + "Code^" + code;
            vals += "|" + "Editable^"+ editable;

            String chars = "";
            chars += md.checknochars("Name", nm);
            chars += md.checknochars("Duration", min);

            if (nm.equals("")){
                md.toast(this, "Please Enter A Name");
            }
            else if (min.equals("")){
                md.toast(this,  "Please Enter A Duration");
            }
            else if (!chars.equals("")){
                md.toast(this,  chars);
            }
            else if (!nm.equals("") & chars.equals("") & !min.equals("")){
                dynamicset("Exercises", idd, vals,    new MainActivity.VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        dynamicdone("Exercises", open);
                    }
                }, 0, this);
            }
        }

    }

    String dnresult = ""; boolean dnsuccess = false;
    public void dynamicset( String table, String rowid,  String values, final MainActivity.VolleyCallBack callBack,
                            int attempts, Context cnt) {
        MaterialButton savebtn = (MaterialButton) cusdialog.findViewById(exsaveid);

        if (savebtn != null){
            savebtn.setEnabled(false);
            md.toast(this, "Saving "+md.returndisplay(table));
            if (pbbar != null){ pbbar.setVisibility(View.VISIBLE);}
        }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String url = md.url + "multiset?code="+md.auth(Exercises.this, mydatabase, android_id, uid)+"~"+table+"~"+rowid+"~"+values;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dnresult = response;
                        dnsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dnsuccess = false;
                dnresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    if (!rowid.equals("")){
                        dynamicset( table, rowid,   values, callBack,  attempt, cnt);
                    }else{
                        dnresult =  "";
                        md.toast(Exercises.this, "Failed To Reach Server... Please Try Again");
                    }
                }else{
                    dnresult =  "";
                    md.toast(Exercises.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void  dynamicdone(String table, boolean open){

        MaterialButton savebtn = (MaterialButton) cusdialog.findViewById(exsaveid);
        if (savebtn != null){savebtn.setEnabled(true);
            if (pbbar != null){ pbbar.setVisibility(View.GONE);}
        }

        if (dnsuccess ){
            md.toast(this, "Saved!");
            cusdialog.cancel();
            ArrayList<String> list  =  md.makearrayfromstring(dnresult, "¬");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            if (list.size() > 2){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, 2), "~");
                String idd =  md.before(md.getstring(list, 2), "~");

                if (table.equals("Exercises") & open){
                    viewexercise(dnresult, idd);
                }

            }

        }
        else  { md.toast(this, " Error, please try again");  }
    }

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    public void gofk(String col, int btnid){


        if (col.equals(("Topic"))){

            String cat = "";

            MaterialButton mb = (MaterialButton) cusdialog.findViewById(excatid);
            if (mb != null){
                cat = mb.getTag().toString();
            }
            if (!cat.equals("")){
                Intent intent = new Intent(this, Activities.class);
                Bundle b = new Bundle();
                b.putStringArray("key", new String[]{ "GetTable",  DS, uid, Executor,  "Topics", "^|Category^=^"+cat, String.valueOf(btnid), ""});
                intent.putExtras(b);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }else{
                md.toast(this,"Please Select A " + md.appsetting("CategoryName", DS));
            }
        }else{
            Intent intent = new Intent(this, Activities.class);
            Bundle b = new Bundle();
            b.putStringArray("key", new String[]{ "ForeignKey",  DS, uid, Executor,  "Exercises", col, String.valueOf(btnid), ""});
            intent.putExtras(b);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        }
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










    public void findexcbybrand(){
        String brand = "", cat = "", topic = "";
        MaterialButton mb = (MaterialButton) cusdialog.findViewById(exbrandid);
        if (mb != null){
            brand =  mb.getTag().toString();
            mb = (MaterialButton) cusdialog.findViewById(excatid);
            if (mb != null){
                cat =  mb.getTag().toString();
                mb = (MaterialButton) cusdialog.findViewById(extopid);
                if (mb != null){
                    topic =  mb.getTag().toString();
                }
            }

            /* String bn= md.appsetting("BrandName", DS), cn =
                    md.appsetting("CategoryName", DS);

          if (brand.equals("")){md.toast(this,"Please Select A " + bn );}
            else if (cat.equals("")){md.toast(this,"Please Select A " + cn );}
            if (!brand.equals("") &  !cat.equals("")){

                searchexercises("", brand, cat, topic,    new MainActivity.VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        foundexercises(false);
                    }
                }, 0, this);
            }*/

            Bundle b = new Bundle();
            b.putStringArray("key", new String[]{ "Exercises",  DS, uid, Executor, "Exercises", brand, cat,  topic,  ""});
            Intent intent = new Intent(getApplicationContext(), Activities.class);
            intent.putExtras(b);
            startActivity(intent);

        }
    }

    public void findexcbycode(){
        String code = "";
        EditText ed = (EditText) cusdialog.findViewById(excodeid);
        if (ed != null){code = ed.getText().toString();}

        String chars = "";
        chars += md.checknochars("Code", code);

        if (!chars.equals("")){md.toast(this, chars);}
        else if (code.equals("")){md.toast(this, "Please Enter A Code");}
        if (!code.equals("") & chars.equals("")){
            searchexercises(code, "", "", "",    new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    foundexercises(true);
                }
            }, 0, this);
        }
    }

    String xcresult = ""; boolean xcsuccess = false;
    public void searchexercises( String code, String brand,  String category, String topic, final MainActivity.VolleyCallBack callBack,
                                 int attempts, Context cnt) {
        MaterialButton savebtn = (MaterialButton) cusdialog.findViewById(exstdrunid);
        MaterialButton sv2 = (MaterialButton) cusdialog.findViewById(exstdsearchid);

        if (savebtn != null & sv2 != null){  savebtn.setEnabled(false); sv2.setEnabled(false);
            if (pbbar != null){ pbbar.setVisibility(View.VISIBLE);}  }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "searchexercises?code="+md.auth(Exercises.this, mydatabase, android_id, uid)+"~"+code+"~"+brand+"~"+category+"~"+topic;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        xcresult = response;
                        xcsuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                xcsuccess = false;
                xcresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    searchexercises( code, brand, category, topic, callBack,  attempt, cnt);
                }else{
                    xcresult =  "";
                    md.toast(Exercises.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void  foundexercises(boolean usecode){

        MaterialButton savebtn = (MaterialButton) cusdialog.findViewById(exstdrunid);
        MaterialButton sv2 = (MaterialButton) cusdialog.findViewById(exstdsearchid);
        int  cushit = (int)(height*0.75), cuslineht = (int)(cushit * 0.09),  cuswid = (int)(width*0.9);

        if (savebtn != null & sv2 != null){  savebtn.setEnabled(true); sv2.setEnabled(true);
            if (pbbar != null){ pbbar.setVisibility(View.GONE);}  }

        if (xcsuccess ){
            String xn =  md.appsetting("ExercisesName", DS), bn= md.appsetting("BrandName", DS), cn =
                    md.appsetting("CategoryName", DS);
            cusdialog.cancel();
            ArrayList<String> tables =   md.makearrayfromstring(xcresult, "|");
            ArrayList<String> xlist  =  md.makearrayfromstring(md.getstring(tables, 0), "¬");
            ArrayList<String> xcols = md.makearrayfromstring(md.getstring(xlist, 1), "~");
            ArrayList<String> rlist  =  md.makearrayfromstring(md.getstring(tables, 1), "¬");
            ArrayList<String> rcols = md.makearrayfromstring(md.getstring(rlist, 1), "~");

            if (xlist.size() <= 2){
                md.toast(this, "Sorry, No " + xn+" Has Been Found");
            }
            if (xlist.size() > 2){
                ArrayList<String> xrow = md.makearrayfromstring(md.getstring(xlist, 2), "~");
                String xid =  md.before(md.getstring(xlist, 2), "~");
                String name= md.coloption(xcols, "Name", xrow).Value;
                String category = md.coloption(xcols, "Category", xrow).Value;
                String edt= md.coloption(xcols, "Editable", xrow).Value;
                if (edt.toLowerCase().equals("1")){edt = "1";}else{edt = "0";}
                String editable = edt;

                cuslin.removeAllViews();
                if (!usecode){

                    viewexercise(md.getstring(tables, 0) , xid );
                }
                else if (usecode){


                    MaterialTextView mt =  md.maketextview(this,  "Found " + xn, "", 0, "Info",
                            DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);mt.setTypeface(null, Typeface.BOLD);

                    mt =  md.maketextview(this,  bn +": "+  md.coloption(xcols, "Brand", xrow).Value, "", 0, "Info",
                            DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);

                    mt =  md.maketextview(this,  cn +": "+ md.coloption(xcols, "Category", xrow).Value, "", 0, "Info",
                            DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);

                    String top = md.coloption(xcols, "Category", xrow).Value;
                    if (!top.equals("")){

                        mt =  md.maketextview(this,  "Topic: "+ md.coloption(xcols, "Topic", xrow).Value, "", 0, "Info",
                                DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);
                    }
                    String tques =  md.coloption(xcols, "NumberOfQuestions", xrow).Value;

                    String gobtn = "Start";
                    if (rlist.size() > 2){
                        gobtn = "Continue";
                        ArrayList<String> rrow = md.makearrayfromstring(md.getstring(rlist, 2), "~");
                        String ridd =  md.before(md.getstring(rlist, 2), "~");
                        String qans =  md.coloption(rcols, "QuestionsAnswered", rrow).Value;
                        mt =  md.maketextview(this,  "Questions Answered: "+ qans + " of " + tques, "", 0, "Info",
                                DS, fontsmall, cuswid, (int)(cuslineht * 1), true); cuslin.addView(mt);
                    }

                    String xs = md.getstring(tables, 0);
                    MaterialButton bt  = md.makebutton(this, gobtn , "", 0, "General", DS, fontsmall, cuswid, cuslineht); cuslin.addView(bt);
                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override  public void onClick(View view) {
                            viewexercise(xs, xid);
                        }});



                }


            }

        }
        else  { md.toast(this, " Error, please try again");  }
    }








    public void doprofiles(boolean neww){

        if (neww){

            cusdialog = new Dialog(Exercises.this);
            cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cusdialog.setContentView(R.layout.custom);
            cusdialog.setCancelable(true);
            cusdialog.setCanceledOnTouchOutside(true);
            cusdialog.show();
        }
        Window window = cusdialog.getWindow();

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
        cuslin.setOrientation(LinearLayout.VERTICAL);

        int cuswid = (int)(width*0.95);
        window.setLayout(cuswid, AbsListView.LayoutParams.WRAP_CONTENT);
        cuslin.setLayoutParams(new FrameLayout.LayoutParams(cuswid,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cuslin.removeAllViews();
        int cuslineht = (int)(height * 0.07);

        String profplur = "Loading " + md.appsetting("ProfilePlural", DS);
        int mc2 =  md.countlines( profplur, md.fontlarge, cuswid);
        MaterialTextView nb = md.maketextview(this,   profplur, "", 0, "Info", DS,
                md.fontlarge, cuswid, mc2*((int)(md.fontlarge*2.4)), true);
        nb.setLines(mc2);cuslin.addView(nb);

        ProgressBar pb = new ProgressBar(Exercises.this); cuslin.addView(pb);
        pb.setVisibility(View.VISIBLE); pb.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht));

        getprofiles(  new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showprofiles();
            }
        }, 0, this);
    }


    String prresult = ""; boolean prsuccess   = false;
    public void getprofiles(final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String url = md.url + "getuserprofiles?code="+md.auth(Exercises.this, mydatabase, android_id, uid);
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        prresult  = response;
                        prsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prsuccess = false;
                if (attempt < md.attempts){
                    getprofiles(  callBack,  attempt, cnt);
                }else{
                    prresult=  "";
                    md.toast(Exercises.this, "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void showprofiles(){

        if (!prsuccess){
            // md.toast(Activities.this, imresult);
        }
        else if (prsuccess & cuslin != null){

            cuslin.removeAllViews();
            int cushit = (int)(height*0.55),  cuswid = (int)(width*0.85);
            cuslin.setPadding(6, 0, 0, 0);
            int cuslineht = (int)(cushit * 0.11);


            String profsingle = md.appsetting("ProfileName", DS);
            String profplur = md.appsetting("ProfilePlural", DS);
            String profmess = md.appsetting("ProfileMessage", DS);
            cusdialog.setTitle("View Exercises For Each " + profsingle);
            LinearLayout lnn = new LinearLayout(Exercises.this); lnn.setOrientation(LinearLayout.HORIZONTAL);
            lnn.setLayoutParams(new LinearLayout.LayoutParams(cuswid, cuslineht)); cuslin.addView(lnn);

            int firstgap = (int)(0.5*(cuswid - cuslineht));
            View  v = md.line(this, firstgap, md.fontmedium, md.transparent);  lnn.addView(v);

            ArrayList<Integer>  des = md.applydesign(Exercises.this, "General", DS);

            ImageView img = new ImageButton(this); lnn.addView(img);
            img.setLayoutParams(new LinearLayout.LayoutParams(cuslineht, cuslineht));
            img.setBackgroundResource(R.drawable.ic_baseline_child_care_24);
            img.setColorFilter(md.getint(des,0));



            int mc2 =  md.countlines(profsingle, md.fontlarge, cuswid);
            MaterialTextView nb = md.maketextview(this,  profsingle , "", 0, "Info", DS,
                    md.fontlarge, cuswid, mc2*((int)(md.fontlarge*2.4)), true);
            nb.setLines(mc2);cuslin.addView(nb);

            v = md.line(Exercises.this, width, md.fontmedium, md.transparent); cuslin.addView(v);

            mc2 =  md.countlines(profmess, md.fontmedium, cuswid);
            nb = md.maketextview(this,  profmess , "", 0, "Info", DS,
                    md.fontmedium, cuswid, mc2*((int)( md.fontmedium*2.4)), true);
            nb.setLines(mc2);cuslin.addView(nb);

            v = md.line(Exercises.this, width, 6, md.transparent); cuslin.addView(v);


            NestedScrollView ns = new NestedScrollView(this); cuslin.addView(ns);
            ns.setLayoutParams(new LinearLayout.LayoutParams(cuswid, (int)(cuslineht * 3.5)));

            LinearLayout rg = new LinearLayout(this); ns.addView(rg); rg.setOrientation(LinearLayout.VERTICAL);
            MaterialTextView rb = md.maketextview(Exercises.this, "All " + profplur, "", 0, "Info", DS,
                    md.fontmedium, cuswid, cuslineht, true);

            rg.addView(rb);  rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileid = "";
                    profilename = "";
                    loadexercises();
                }
            });

            ArrayList<String> odlist = md.makearrayfromstring(prresult, "¬");
            ArrayList<String> odcols = md.makearrayfromstring(md.getstring(odlist, 1), "~");
            for (int c = 2; c < odlist.size(); c++)
            {
                ArrayList<String> row = md.makearrayfromstring(md.getstring(odlist, c), "~");
                String idd = md.before(md.getstring(odlist, c), "~");
                String name = md.coloption(odcols, "Name", row).Value;
                rb = md.maketextview(Exercises.this, name, "", 0, "Info", DS,
                        md.fontmedium, cuswid, cuslineht, true);

                rg.addView(rb);  rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileid = idd;
                    profilename = name;
                    if (cusdialog != null){cusdialog.cancel();}
                    MaterialButton mb = (MaterialButton) findViewById(profilebtnid);
                    if (mb != null){mb.setText("Exercises For " + profilename);}
                    loadexercises();
                }
            });

                v = md.line(Exercises.this, cuswid, 2, textcolor); rg.addView(v);
            }


            v = md.line(Exercises.this, cuswid, md.fontlarge, md.transparent); rg.addView(v);
            int bh = (int)(cuslineht * 1.5);

            MaterialButton bt  = md.makebutton(Exercises.this, "New " + profsingle, "", 0,
                    "General", DS, md.fontsmall, cuswid, bh); cuslin.addView(bt);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override  public void onClick(View view) {  newprofile(cuswid, cuslineht, profsingle) ;}});

        }
        else  {
            md.toast(Exercises.this, " Error, please try again");
        }
    }





    public void newprofile( int cuswid, int cuslineht, String profsingle){

        cuslin.removeAllViews();

        LinearLayout lnclose = new LinearLayout( this);
        lnclose.setOrientation(LinearLayout.HORIZONTAL);
        lnclose.setLayoutParams(new LinearLayout.LayoutParams( cuswid, cuslineht));
        cuslin.addView(lnclose);

        View v = md.line(Exercises.this, (int)((0.95)*(cuswid - cuslineht)), cuslineht, md.transparent);
        lnclose.addView(v);

        ImageView mb = new ImageView(this);int cbh = (int) (cuslineht * 0.7);
        mb.setLayoutParams(new FrameLayout.LayoutParams( cbh, cbh));
        mb.setBackgroundResource(R.drawable.close);  lnclose.addView(mb);
        mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
            doprofiles(false);
        } });


        int mc2 =  md.countlines(profsingle, md.fontlarge, cuswid);
        MaterialTextView nb = md.maketextview(this,  "New "+ profsingle , "", 0, "Info", DS,
                md.fontlarge, cuswid, mc2*((int)(md.fontlarge*2.4)), true);
        nb.setLines(mc2);cuslin.addView(nb);

        v = md.line(Exercises.this, width, md.fontmedium, md.transparent); cuslin.addView(v);


        EditText ed = md.edittext(this, profilenameid, "Name", "", "", cuswid, cuslineht,
                md.fontmedium, DS); cuslin.addView(ed);

        v = md.line(Exercises.this, width, md.fontmedium, md.transparent); cuslin.addView(v);

        MaterialButton bt  = md.makebutton(Exercises.this, "Save " +profsingle, "", 0,
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
                md.toast(Exercises.this, mss);
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

                if(cusdialog != null){cusdialog.cancel();}
                profileid = tbresult;
                doprofiles(true);
            }
        });

        queue.add(stringRequest);

    }

    public void doneprofile(){
        if(cusdialog != null){cusdialog.cancel();}
        profileid = tbresult;
        doprofiles(true);
    }




}
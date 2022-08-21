package com.example.nzeluplus;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;


public class Register extends AppCompatActivity {

    private String android_id;
    LinearLayout linearLayout;  ArrayList<String> arr ;
    private int year, month, day; ProgressBar pbbar;

    MyModel md = new MyModel();
    public static final String EXTRA_MESSAGE = "com.example.android.twoactivities.extra.MESSAGE";
    private Calendar calendar;
    String Admin = "Developer3flags$Admin";
    SQLiteDatabase mydatabase;
    boolean valeron = false;

    final int   name = 122, surname  = 222, email = 322, cell= 422, pbarid = 26349, finregid = 297535, password = 94338
            , store = 94777, country =  95111, code = 95222, finconid = 95333;

    int idv = 1, reglevel = 0;
    String  RegType = "1",   displaystring  = "", execreg ="1",
            supreg = "1", execname = "", execaltname = "", supname =  "", stname = "";


    int rowheight, height, width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        android_id = Settings.Secure.getString(Register.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        linearLayout = (LinearLayout) findViewById(R.id.mainlinear);
        linearLayout.setPadding(25, 0, 25, 0);
        linearLayout = md. setbackground(linearLayout);

        linearLayout.setBackgroundResource(R.drawable.backgroundxml);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String[] receivearr = b.getStringArray("key");
        displaystring  = receivearr[0];

        DisplayMetrics metrics = new DisplayMetrics();
        (Register.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels; width = metrics.widthPixels;
        rowheight = (int) (height * 0.08);
        valeron = md.DbName.equals("valeronpro");

        ArrayList<String> disarray = md.makearrayfromstring(displaystring, "~");

        for (int i = 3; i < disarray.size(); i++){
            String cl = md.before(md.getstring(disarray, i), "^");
            String vl = md.breakurl(md.getstring(disarray, i), 1, "^");
            if (cl.equals("AllowExecutorReg")){
                if (vl.toLowerCase().equals("true")){execreg= "2";}
            } else  if (cl.equals("AllowSupplierReg")){
                if (vl.toLowerCase().equals("true")){supreg= "2";}
            }
        }

        if (execreg.equals("2")){
            for (int i = 3; i < disarray.size(); i++){
                String cl = md.before(md.getstring(disarray, i), "^");
                String vl = md.breakurl(md.getstring(disarray, i), 1, "^");
                if (cl.equals("ExecutorName")){  execname = vl;  }
                else if (cl.equals("AlternateExecutorName")){  execaltname = vl;  }
            }
        }

        if (supreg.equals("2")){
            for (int i = 3; i < disarray.size(); i++){
                String cl = md.before(md.getstring(disarray, i), "^");
                String vl = md.breakurl(md.getstring(disarray,i), 1, "^");
                if (cl.equals("SupplierName")){  supname = vl;  }
            }
        }

        setTitle("Login/Register");

        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);

        startreg();


    }


    @Override
    public void onBackPressed() {
        if (reglevel == 1){
            startreg();
        }else if (reglevel == 0){
            Register.this.moveTaskToBack(true);
        } else  if (reglevel == 2){
            lastreg();
        }
    }


    public void goreg(){
        gettable( new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showdialogresults();
            }
        }, 0, Register.this);
    }


    String tbresult = ""; boolean tbsuccess = false;
    public void gettable( final MainActivity.VolleyCallBack callBack,
                          int attempts, Context cnt) {

        RequestQueue queue = Volley.newRequestQueue(cnt);
        final int attempt = attempts + 1;
        String url = md.url + "gettable?code=";// + md.auth(mydatabase,  android_id, uid)+"~Appsettings";

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
                    gettable(   callBack,  attempt, cnt);
                }else{
                    tbresult =  "Error: " + error.getMessage();
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    int searchpbid = 8425266;
    public void showdialogresults(){

        if (tbsuccess  & linearLayout != null){
            ArrayList<String> list  =  md.makearrayfromstring(tbresult, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");


            for (int c = 2; c < list.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                String idd =  md.before(md.getstring(list, c), "~");
                String vl = md.coloption(cols, "", row).Value;
            }
            startreg();
        }
        else  {
            md.toast(Register.this, " Error, please try again");
        }
    }


    public void startreg(){

        reglevel = 0;
        linearLayout.removeAllViews();
        blankspace();
        textview( "New Registration",  md.fontlarge); idv++;
        blankspace();

        button("Register New Account", "General", displaystring, idv);
        MaterialButton newbtn = (MaterialButton) findViewById(idv);
        newbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {  RegType = "1";lastreg(); } });

        if (valeron){
            idv++; blankspace();
            textview( "Complete Email Confirmation?",  md.fontlarge);
            idv++; blankspace();
            button("Enter Confirmation Code", "General", displaystring, idv);
            MaterialButton conbtn = (MaterialButton) findViewById(idv);
            conbtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {   RegType = "2"; lastreg();} });
        }



        idv++; blankspace();
        textview( "Already A User?",  md.fontlarge);
        idv++; blankspace();
        button("Login", "Go", displaystring, idv);
        MaterialButton logbtn = (MaterialButton) findViewById(idv);
        logbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {   RegType = "0"; lastreg();} });


        MaterialTextView mt = helptextview();
       // linearLayout.addView(mt);

        linearLayout.addView(forgotpassword());

        linearLayout.addView(md.helptextview(Register.this, displaystring, width, (int)(height * 0.09)));

    }



    int xcrid = 2859432, nxcrid = 133492, sprid = 29845723;
    public void lastreg(){
        reglevel = 1;

        linearLayout.removeAllViews();

        ProgressBar pbbar = new ProgressBar(Register.this);
        pbbar.setId(pbarid);
        linearLayout.addView(pbbar);
        pbbar.setVisibility(View.GONE);

        String regmes = "New Registration";

        if (RegType.equals("0")){ regmes = "Log in";  }
        else if (RegType.equals("2")){ regmes = "Please Enter The Confirmation Code";  }

        blankspace();
        textview(   regmes ,  md.fontmedium);

        RadioGroup rg = new RadioGroup(Register.this);

        if ((supreg.equals("2") || execreg.equals("2")) & RegType.equals("1")){

            linearLayout.addView(rg);
        }

        if (execreg.equals("2") & RegType.equals("1")){

            RadioButton rb = new RadioButton(Register.this);   rb.setText("Register As " + execaltname);
            rb.setId(nxcrid); rb.setChecked(true);   rg.addView(rb); rb.setTextSize(md.fontmedium);

            rb = new RadioButton(Register.this);  rb.setText("Register As " + execname);
            rb.setId(xcrid); rb.setChecked(false);rg.addView(rb); rb.setTextSize(md.fontmedium);
        }

        if (supreg.equals("2") & RegType.equals("1")){
            RadioButton rb = new RadioButton(Register.this);  rb.setText("Register As " + supname);
            rb.setId(sprid); rb.setChecked(false);rg.addView(rb); rb.setTextSize(md.fontmedium);
        }

        blankspace();
        if (RegType.equals("1")){
            blankspace();
            edittext("Name",  name, "");
            EditText edt  = (EditText) findViewById(name);
            idv++;
            blankspace();
            edittext("Surname",   surname, "");
            edt  = (EditText) findViewById(surname);
            idv++;
            blankspace();
            edittext("Cell",  cell, "Number");
            edt  = (EditText) findViewById(cell);
            idv++;
        }
        if (RegType.equals("2")){
            blankspace();
            edittext("Code",  code, "");
            EditText edt  = (EditText) findViewById(code);
            idv++;
        }

        if (RegType.equals("0")){
            textview( "Please Enter Your Email And Password",md.fontmedium);
        }

        if (!RegType.equals("2")){
            blankspace();
            edittext("Email", email, "Email");
            EditText edt  = (EditText) findViewById(email);
            idv++;
            blankspace();
            edittext("Password", password, "Password");
            edt  = (EditText) findViewById(password);
            idv++;
        }

        if (valeron & RegType.equals("1")){

            blankspace();
            edittext("Store Name", store, "Text");
            idv++;

            blankspace();
            button("Country", "General", displaystring, country);
            MaterialButton mb = (MaterialButton) findViewById(country); mb.setTag("");
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(555);
                }
            });
            idv++;
        }
        blankspace();
        button("Continue", "Go", displaystring, finregid);

        MaterialTextView mt = md.maketextview(Register.this, "I accept Privacy Policy & Terms of Use",
                "", 0, "Info", displaystring, md.fontsmall, width, rowheight, true);
        mt.setLines(2);

        mt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url =   md.appsetting("TermsOfUseLink", displaystring);

                if (!url.startsWith("http://") && !url.startsWith("https://"))
                {
                url = "http://" + url;
                } else  if (url.startsWith("https://"))
                {

                  url = url.replaceAll("https://", "http://");

                }

           //     url = "http://docs.google.com/document/d/16TOuIGEPp0VmCwA1ud2z7ckxff3uJCJp/edit?usp=drivesdk&ouid=115810852585554046011&rtpof=true&sd=true";
               // String url = "https://"+md.Site+"/Home/Legal";
             //  Intent i = new Intent(Intent.ACTION_VIEW);
              //  i.setData(Uri.parse(url));   startActivity(i);


              Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
        linearLayout.addView(mt);

        mt = helptextview();
        //linearLayout.addView(mt);


        linearLayout.addView(forgotpassword());

        MaterialButton regbtn = (MaterialButton) findViewById(finregid);
        regbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (RegType.equals("2")){

                    String cd = "", mss = "";
                    EditText nmt =  (EditText) findViewById(code);
                    cd = nmt.getText().toString();
                    mss += md.checkvalue("Code",cd);
                    if (!mss.equals("")){
                        md.toast(Register.this, mss);
                    }else{

                        finishcon(md.encrypt(Register.this, cd, true, true), new MainActivity.VolleyCallBack() {
                            @Override public void onSuccess() {  conresult();  }   }, 0);
                    }
                }
                else{
                    String nm = "", sn = "", pn = "",   em  = "",  mss = "", cl = "", st = "", cn = "";
                    if (RegType.equals("1")){
                        EditText nmt =  (EditText) findViewById(name);
                        EditText snt =  (EditText) findViewById(surname);
                        EditText emt =  (EditText) findViewById(email);
                        EditText clt =  (EditText) findViewById(cell);
                        nm = nmt.getText().toString();
                        sn =  snt.getText().toString();
                        em = emt.getText().toString();
                        cl = clt.getText().toString();

                        mss += md.checkvalue("Name",nm);
                        mss += md.checkvalue("Surname", sn);
                        mss += md.checkemail( em);
                        mss += md.checkvalue("Cell", cl);
                        if (valeron){
                            EditText str =  (EditText) findViewById(store);
                            st = str.getText().toString();
                            mss += md.checkvalue("Store Name",st);

                            MaterialButton cnt =  (MaterialButton ) findViewById(country);
                            cn = cnt.getTag().toString();
                            if (cn.equals("")){
                                mss +="Please Select A Country";
                            }
                        }
                    }

                    EditText clt =  (EditText) findViewById(email);
                    EditText pnn =  (EditText) findViewById(password);

                    pn = pnn.getText().toString(); em = clt.getText().toString();

                    mss += md.checkvalue("Email", em);
                    mss += md.checkvalue("Password", pn);

                    if (!mss.equals("")){
                        md.toast(Register.this, mss);
                    }else{
                        String xcvl = "3";if (valeron){xcvl = "1";}
                        RadioButton xr = (RadioButton) findViewById(xcrid);
                        if (xr != null){
                            if (xr.isChecked()){xcvl = "2";}
                        }

                        xr = (RadioButton) findViewById(sprid);
                        if (xr != null){
                            if (xr.isChecked()){xcvl = "4";}
                        }


                        String sender = md.V + "~"+md.Site+ "~"+android_id+  "~"+RegType+  "~"+
                                md.encrypt(Register.this, nm, true, true) +  "~" +
                                md.encrypt(Register.this, sn, true, true) +
                                "~" +md.encrypt(Register.this, cl, true, true) +
                                "~" +md.encrypt(Register.this, em, true, true)+
                                "~" +md.encrypt(Register.this, pn, true, true)+
                                "~" +md.encrypt(Register.this, xcvl, true, true);
                        if (valeron){
                            sender += "~" +md.encrypt(Register.this, st, true, true);
                            sender += "~" +md.encrypt(Register.this, cn, true, true);
                            stname = st;
                        }

                        if (android_id.equals("b9d0dd9de99962ab")){
                      //      md.clipboard(Register.this, sender);
                        }
                        finishreg(sender, new MainActivity.VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                regresult(RegType);
                            }
                        }, 0);
                    }
                }
            }
        });


    }







    boolean dataSuccess  = false; String responsestring = "";
    public void finishreg(String regs, final MainActivity.VolleyCallBack callBack, int attempts) {

        ProgressBar pbbar = (ProgressBar) findViewById(pbarid);
        pbbar.setVisibility(View.VISIBLE);

        MaterialButton rgbtn = (MaterialButton) findViewById(finregid);
        rgbtn.setVisibility(View.GONE);

        final int attempt = attempts + 1;

        String pre = "https://www.valeronpro.com/App/appregister";

        if (valeron){pre = "https://www.valeronpro.com/App/valappregister";}

        String url = pre + "?code="+regs;

        RequestQueue queue = Volley.newRequestQueue(Register.this);
        final int att = attempts +1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responsestring = response;//md.decrypt( response, true, true);
                        dataSuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){
                    md.toast(Register.this, "Failed To Reach Server... Retrying");

                    finishreg( regs,callBack,  attempt);
                }else{
                    //responsestring =  "Error: " + error.getMessage();
                    md.toast(Register.this, "Failed To Sign In"
                            + System.lineSeparator() + responsestring);
                }
            }
        });




        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }


    public void regresult(String regtype){

        ProgressBar pbbar = (ProgressBar) findViewById(pbarid);
        pbbar.setVisibility(View.GONE);
        MaterialButton regbtn = (MaterialButton) findViewById(finregid); regbtn.setVisibility(View.VISIBLE);
        if (dataSuccess){
            if (responsestring.indexOf("~") > -1){
                ArrayList<String> vll = md.makearrayfromstring(responsestring, "~");

                if ( vll.size()  > 3){
                    String userref = md.getstring(vll,0),
                            access = md.getstring(vll, 1),
                            executor = md.getstring(vll,2),
                            siteref = md.getstring(vll,4),
                            dbo = md.getstring(vll,5);

                    if (!userref.equals("")& access.equals("1")){

                        ArrayList<MyModel.option> recs = new ArrayList<>();
                        Cursor rs =  mydatabase.rawQuery( "select * from MOBILE where ANDROIDID = '"+android_id +"' and REFERENCE = '"+
                                userref+"'", null );
                        while ( rs.moveToNext()) {
                            recs.add(new MyModel.option("",
                                    rs.getString(rs.getColumnIndex("ANDROIDID")),
                                    rs.getString(rs.getColumnIndex("REFERENCE")),
                                    String.valueOf(rs.getInt(rs.getColumnIndex("LOGGEDIN")))));
                        }

                        mydatabase.execSQL("UPDATE MOBILE SET LOGGEDIN = 0");
                        if (recs.size() > 0 & !userref.equals("")){
                            mydatabase.execSQL("UPDATE MOBILE SET LOGGEDIN = 1 where ANDROIDID = '"+android_id +"' and REFERENCE = '"+ userref+"'");
                        }else{
                            if (!userref.equals(""))
                            {
                                mydatabase.execSQL("INSERT INTO MOBILE VALUES('"+android_id +"', '"+userref+"',  1)");
                            }
                        }


                        if(valeron){
                            if (regtype.equals("1")){
                                confirmreg(userref);
                            }else{
                                finalvalsignin(userref, siteref,executor, dbo, "");
                            }
                        }else{
                            startapp(userref, executor, "");
                        }
                    } else  {
                        md.toast(Register.this, "Access Denied For This User");
                    }
                }
                else if (vll.size() == 3){
                    if (!md.getstring(vll,2).equals("")){
                        md.toast(Register.this, md.getstring(vll,2));
                    }else{
                        md.toast(Register.this, "Failed To Register");
                    }
                }
            }
            else{
                md.toast(Register.this,
                        "Error In Registration:" + responsestring
                        );
            }
        }  else{
            md.toast(Register.this,

                    "Server Error In Registration.. Please Try Again"

                    );
        }
    }




    public void confirmreg(String userref){
        reglevel = 2;

        linearLayout.removeAllViews();
        ProgressBar pbbar = new ProgressBar(Register.this);
        pbbar.setId(pbarid);
        linearLayout.addView(pbbar);
        pbbar.setVisibility(View.GONE);

        blankspace();
        textview(  "Please Enter The Confirmation Code Sent To Your Email" , md.fontmedium);

        blankspace(); blankspace();
        edittext("Confirmation Code", code, "");
        EditText edt  = (EditText) findViewById(name);
        idv++;

        blankspace();
        button("Continue", "Go", displaystring, finconid);

        MaterialButton regbtn = (MaterialButton) findViewById(finconid);
        regbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String cd = "", mss = "";
                EditText nmt =  (EditText) findViewById(code);
                cd = nmt.getText().toString();
                mss += md.checkvalue("Code",cd);
                if (!mss.equals("")){
                    md.toast(Register.this, mss);
                }else{

                    finishcon(md.encrypt(Register.this, cd, true, true), new MainActivity.VolleyCallBack() {
                        @Override public void onSuccess() {  conresult();  }   }, 0);
                }
            }
        });
    }



    public void firebasecon(String regs, String email){

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.valeronpro.com/App/firebaseconfirm?code="+regs)
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    boolean consuccess  = false; String constring = "";
    public void finishcon(String code, final MainActivity.VolleyCallBack callBack, int attempts) {

        ProgressBar pbbar = (ProgressBar) findViewById(pbarid);
        if (pbbar != null){pbbar.setVisibility(View.VISIBLE);}

        MaterialButton rgbtn = (MaterialButton) findViewById(finconid);
        if (rgbtn != null){rgbtn.setVisibility(View.GONE);}

        final int attempt = attempts + 1;

        String sender = code+"~" + android_id +"~"
                + android.os.Build.MANUFACTURER + android.os.Build.PRODUCT + android.os.Build.MODEL;


        String url =  "https://www.valeronpro.com/App/valeronappconfirm?code="+sender;

        RequestQueue queue = Volley.newRequestQueue(Register.this);

        final int att = attempts +1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        constring = response;//md.decrypt( response, true, true);
                        consuccess = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (attempt < md.attempts){
                    md.toast(Register.this, "Failed To Reach Server... Retrying");

                    finishcon(code,callBack,  attempt);
                }else{
                    md.toast(Register.this, "Failed To Sign In"
                            + System.lineSeparator() +constring);
                }
            }
        });




        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }


    public void conresult(){
        ProgressBar pbbar = (ProgressBar) findViewById(pbarid);
        if (pbbar != null){ pbbar.setVisibility(View.GONE);}
        MaterialButton regbtn = (MaterialButton) findViewById(finconid);
        if (regbtn != null){regbtn.setVisibility(View.VISIBLE);}
        if (consuccess){
            if (constring.indexOf("~") > -1){
                ArrayList<String> vl = md.makearrayfromstring(constring, "~");
                if (vl.size() > 2){
                    String recuser = md.getstring(vl,0);
                    String recsite = md.getstring(vl,1);
                    String dbo = md.getstring(vl,2);
                    String cpn = md.getstring(vl,3);
                    finalvalsignin(recuser,  recsite, "1", dbo, cpn);
                }else{
                    md.toast(Register.this,
                            "Registration Unsuccessful.. Please Try Again Or Contact Us"
                            );
                }
            }
            else{
                md.toast(Register.this,
                        "Registration Unsuccessful.. Please Try Again Or Contact Us"
                        );
            }
        }
    }


    public void finalvalsignin(String recuser, String recsite, String executor, String dbo, String companyname){

        boolean go = md.switchcompany(recuser, recsite, stname, mydatabase);

        if (go){
            startapp(recuser,executor, companyname);
        }else{
            md.toast(Register.this, "Login Failed, Please Contact Us For Assistance");
        }

    }



    public void startapp(String userref, String executor, String companyname) {

        Bundle b = new Bundle();
        if (valeron){
            ArrayList<MyModel.option> values = new ArrayList<>();
            values.add(new MyModel.option("POSUSERID", "1", "", ""));
            values.add(new MyModel.option("COMPANYLOCATION", "1", "", ""));
            values.add(new MyModel.option("COMPANYNAME", companyname, "", ""));
            MyModel.option recs = md.sqlupdate(values,  mydatabase);
            b.putStringArray("key", new String[]{ displaystring, userref,executor, recs.Option2,
                    recs.Option4,recs.Option8,  recs.Option7,   recs.Option10, "1" });
        }else{
            b.putStringArray("key", new String[]{ displaystring, userref, executor });
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }



    public void blankspace(){
        TextView textView = new TextView(Register.this);
        textView.setText("");
        textView.setHeight((int) (height * 0.01));

        linearLayout.addView(textView);

    }

    public void textview(String text, int fontsize){
        TextView welcome = new TextView(Register.this);
        welcome.setText(text);
        welcome.setTextSize(fontsize);
        welcome.setGravity(Gravity.CENTER);
        welcome.setHeight(rowheight);
        ArrayList<Integer> des = md.applydesign(Register.this, "Info", displaystring);
        welcome.setBackgroundColor(md.getint(des,0));
        welcome.setTextColor(md.getint(des,1));

        linearLayout.addView(welcome);
    }

    public void button(String text, String type, String displaystring, int id){
        MaterialButton btn = new MaterialButton(Register.this);
        btn.setText(text);
        btn.setId(id);
        btn.setPadding(10, 10, 10, 10); // in pixels (left, top, right, bottom)

        ArrayList<Integer> des = md.applydesign(Register.this, type, displaystring);
        btn.setBackgroundColor(md.getint(des,0));
        btn.setTextColor(md.getint(des,1));
        btn.setCornerRadius(md.getint(des,2));

        linearLayout.addView(btn);
    }

    public void edittext(String text, int id, String type){

        EditText eddt = new EditText(Register.this);
        eddt.setHint(text);
        eddt.setId(id);

        ArrayList<Integer> des = md.applydesign(Register.this, "Input", displaystring);
        eddt.setBackgroundColor(md.getint(des, 0));
        eddt.setTextColor(md.getint(des, 1));
        eddt.setHintTextColor(md.getint(des, 1));


        eddt.setPadding(10, 10, 10, 10); // in pixels (left, top, right, bottom)
        if (type.equals("Phone")){ eddt.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (type.equals("Number")){ eddt.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (type.equals("Date")){ eddt.setInputType(InputType.TYPE_CLASS_DATETIME);
        } else if (type.equals("Password")){
            eddt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eddt.setTransformationMethod(new PasswordTransformationMethod());
        }
        linearLayout.addView(eddt);

    }




    public MaterialTextView helptextview(){
        MaterialTextView  mt = md.maketextview(Register.this,
                "Need Help? Please Contact Us",
                "", 0, "Info", displaystring, md.fontsmall, width, rowheight, true);

        mt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://wa.me/message/GOF5TLWHJXGJP1";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        return mt;
    }


    public MaterialTextView forgotpassword(){
        MaterialTextView  mt = md.maketextview(Register.this,
                "Forgot Your Password?",
                "", 0, "Info", displaystring, md.fontsmall, width, rowheight, true);

        mt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://"+md.Site+"/Home/ForgotPassword";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        return mt;
    }







    String selectedcountry  = "";
    @Override
    protected Dialog onCreateDialog(int id) {

        // TODO Auto-generated method stub
        android.app.AlertDialog.Builder  builder = new android.app.AlertDialog.Builder(this);

        if (id == 555){

            ArrayList<MyModel.option> cc =  md.Countries(); ArrayList<String> fkviewlist =  new ArrayList<>(), fkidlist = new ArrayList<>();

            for (int i = 0; i < cc.size(); i++){
                fkviewlist.add(md.getoption(cc, i).Option1);
                fkidlist.add(md.getoption(cc, i).ID);
            }

            LinearLayout ll = new LinearLayout(Register.this);
            ll.setOrientation(LinearLayout.VERTICAL);
            final ListView lv = new ListView(Register.this);
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(Register.this,
                    android.R.layout.simple_list_item_1,  fkviewlist );
            lv.setAdapter(itemsAdapter);
            ll.addView(lv);
            builder.setView(ll);

            builder.setTitle("Country");

            AlertDialog dialog = builder.create();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,  long id) {
                    try{
                        selectedcountry   = md.getstring(fkidlist, position);
                        dialog.cancel();

                        MaterialButton mb = (MaterialButton)findViewById(country);
                        mb.setTag( md.getstring(fkidlist, position));
                        mb.setText( md.getstring(fkviewlist, position));


                    }catch(Exception c){
                        md.toast(Register.this, "ERROR: " + c.getMessage());
                    }

                }
            });


            return  dialog;

        }

        return null;
    }



}
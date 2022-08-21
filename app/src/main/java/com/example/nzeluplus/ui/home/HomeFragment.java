package com.example.nzeluplus.ui.home;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.RequestManager;
import com.example.nzeluplus.Activities;
import com.example.nzeluplus.GpsTracker;
import com.example.nzeluplus.MainActivity;
import com.example.nzeluplus.MyModel;
import com.example.nzeluplus.Orders;
import com.example.nzeluplus.R;
import com.example.nzeluplus.Register;
import com.example.nzeluplus.SetMap;
import com.example.nzeluplus.Startup;
import com.example.nzeluplus.Transact;
import com.example.nzeluplus.databinding.FragmentHomeBinding;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    LinearLayout mainlin, sublin;
    boolean dataSuccess = false;
    Handler h = new Handler(); MyModel md = new MyModel();
    int  searchid = 84322032, sbtnid = searchid + 1, height = MainActivity.height, width = MainActivity.width;
    int dellay = 5000, scrlid = 103, sublinid = 113;
    String android_id = MainActivity.android_id, uid = MainActivity.uref;
    boolean showsearch = false, showlogo  = false, whatsapp = false, call = false, showcats = false,
            canshoworders = false, smallscreen =  false; int fontsmall = md.fontsmall, fontmedium = md.fontmedium;
    String searchmess = "", CompanyLocation = "", companyname = MainActivity.companyname, posuserid = "",
            locationname = "", posusername = "", online = "1";

    String DS = MainActivity.DisplayString, Executor = MainActivity.Executor, currencysymbol = MainActivity.currencysymbol;
    SQLiteDatabase mydatabase;
    LinearLayout cuslin;  Dialog cusdialog;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =  new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainlin = binding.mainlin;
        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        DisplayMetrics metrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;width = metrics.widthPixels;

        if (height <= md.heightcut){height = (int)(height * 0.91);
        smallscreen = true; fontsmall -= 1;fontmedium -= 2;}

        LinearLayout.LayoutParams linpam = new LinearLayout.LayoutParams(width, height);
        linpam.setMargins(0, 0, 0, 0);
        mainlin.setLayoutParams(linpam);
        mainlin.setPadding(30, 0, 30, 0);
        android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        width = width  - 60;

        ArrayList<String> disarray = md.makearrayfromstring(MainActivity.DisplayString, "~");

        mydatabase = getActivity().openOrCreateDatabase(md.DbName,getActivity().MODE_PRIVATE,null);

        for (int i = 3; i < disarray.size(); i++){
            String cl = md.before(md.getstring(disarray, i), "^");
            String vl = md.breakurl(md.getstring(disarray, i), 1, "^");
            if (cl.equals("ShowHomeSearchBar")){
                showsearch = vl.toLowerCase().equals("true");
                if (Executor.equals("2") &  md.appsetting("AllowExecutorBooking",
                        MainActivity.DisplayString).toLowerCase()
                        .equals("false")){
                    showsearch = false;
                }
            } else  if (cl.equals("HomeShowLogo")){
                showlogo =vl.toLowerCase().equals("true");
            }else  if (cl.equals("HomeShowWhatsapp")){
                whatsapp =vl.toLowerCase().equals("true");
            }else  if (cl.equals("HomeShowCall")){
                call = vl.toLowerCase().equals("true");
            }else  if (cl.equals("HomeSearchBarMessage")){
                searchmess = vl;
            }else  if (cl.equals("HomeShowPopularCategories")){
                showcats =  vl.toLowerCase().equals("true");

            }else  if (cl.equals("ShowTodayOrdersOnHomeScreen")){
                canshoworders =  vl.toLowerCase().equals("true");
            }
        }

        startoff();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public void onResume() {
       startoff();
        super.onResume();
    }


    int boxdim = (int)(width * 0.15), lineheight = (int) (height * 0.08), toprowheight = (int) (height * 0.15);
    boolean insearch = false, startoffused = false;

    public void startoff(){
        int fh = height;


        if (!startoffused){
 startoffused = true;
            mainlin.removeAllViews();
            if (showsearch){
                fh = (int) (0.8 * height);
                LinearLayout lr = new LinearLayout(getActivity());
                lr.setLayoutParams(new LinearLayout.LayoutParams(width,  toprowheight));  mainlin.addView(lr);
                int tvw = width - boxdim;

                EditText edt = new EditText(getActivity()); edt.setId(searchid); edt.setTextSize(md.fontmedium);
                edt.setLayoutParams(new LinearLayout.LayoutParams(tvw, boxdim));  lr.addView(edt);edt.setHint( searchmess);
                edt.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if(i == KeyEvent.KEYCODE_DEL) {
                            insearch = false;
                            Button bt = (Button) getActivity().findViewById(sbtnid );
                            bt.setBackgroundResource(R.drawable.ic_baseline_search_24);
                        }
                        return false;
                    }
                });
                edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        Button bt = (Button) getActivity().findViewById(sbtnid );
                        if (bt != null){
                            bt.setBackgroundResource(R.drawable.ic_baseline_search_24);
                        }
                    }
                });

                Button bt = new Button(getActivity()); bt.setId(sbtnid );
                bt.setLayoutParams( new LinearLayout.LayoutParams(boxdim, boxdim)); lr.addView(bt);
                bt.setBackgroundResource(R.drawable.ic_baseline_search_24);  bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (! insearch){
                            search();
                        } else{
                            insearch = false;
                            bt.setBackgroundResource(R.drawable.ic_baseline_search_24);
                            showhome();
                        }
                    }
                });
            }

            NestedScrollView scrl = new NestedScrollView(getActivity()); scrl.setId(scrlid);
            scrl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  fh)) ;
            mainlin.addView(scrl);

            sublin = new LinearLayout(getActivity()); sublin.setId(sublinid);
            sublin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  fh)) ;
            sublin.setOrientation(LinearLayout.VERTICAL); scrl.addView(sublin);
            sublin.setPadding(0, 0, 0, (int)(lineheight*2));


            if (canshoworders){
                odresult = ""; odsuccess = false; bkadded = new ArrayList<>();
                ProgressBar pb = new ProgressBar(getActivity()); pb.setId(odpbid); sublin.addView(pb);
                getbookinglist(  new MainActivity.VolleyCallBack() {
                    @Override  public void onSuccess() { showbookings(); }
                }, 0, getActivity());
            }
            showhome();
        }else{
            startoffused = false;
        }


        if (md.appsetting("AskForLocationAtAllTimes", DS).toLowerCase().equals("true")){
            getlocation();
        }
    }


    double mylatitude = 1000, mylongitude = 1000; boolean mapSuccess  = false; String mapstring = "";
    private GpsTracker gpsTracker;
    Handler hdl = new Handler();

    public void getlocation(){
        hdl.postDelayed(new Runnable(){
            public void run(){
                dellay =  300000;
                gpsTracker = new GpsTracker(getActivity());
                if(gpsTracker.canGetLocation()){
                    mylatitude = gpsTracker.getLatitude();
                    mylongitude = gpsTracker.getLongitude();

                    getmapdata(new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() { showmap();  }
                    }, 0);
                }else{
                    md.toast(getActivity(), "Please Enable Your GPS In Order To Set Your Location");
                    gpsTracker.showSettingsAlert();
                }
                //hdl.postDelayed(this, dellay);
            }
        }, dellay);
    }
    public void getmapdata( final MainActivity.VolleyCallBack callBack,  int attempts) {

        try{
            RequestQueue queue = Volley.newRequestQueue(getActivity());

            String locc = String.valueOf(mylatitude) + "," + String.valueOf(mylongitude);
            if ((mylatitude > 0 || mylatitude < 0) & (mylongitude > 0 || mylongitude < 0)){

                String url = md.url + "setandgetlocation?code="+
                        md.auth(getActivity(), mydatabase, android_id, uid)+"~"+locc+"~~Just~~";

                final int attempt = attempts + 1;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                mapstring = response;
                                mapSuccess = true;
                                callBack.onSuccess();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (attempt < md.attempts){
                            getmapdata(   callBack,  attempt);
                        }else{
                            mapstring =  "Error: " + error.getMessage();
                        }
                    }
                });

                queue.add(stringRequest);
            }

        }
        catch(NullPointerException npx){

        }
    }

    ArrayList<String> vees = new ArrayList<>(),  cols = new ArrayList<>();
    String otherid = "121", myid = "122";
    public void showmap(){


    }




    public  void showhome(){
        sublin.removeAllViews();
        if (showlogo){
            int ww = (int) (width * 0.5);
            LinearLayout lr = new LinearLayout(getActivity());
            lr.setLayoutParams(new LinearLayout.LayoutParams(width,   ww));
            lr.setPadding((int) (ww * 0.5),  0, (int) (ww * 0.5), 0);
            sublin.addView(lr);
            ImageView imgView= new ImageView(getActivity());
            imgView.setImageResource(R.drawable.logosmall);
            imgView.setLayoutParams(new ViewGroup.LayoutParams(ww, ww));
            lr.addView(imgView);
        }


        imresult = ""; imsuccess = false;
        gethomescreen(  new MainActivity.VolleyCallBack() {
            @Override  public void onSuccess() { showhomescreen(); }
        }, 0, getActivity());
    }



    public void search(){
        EditText edt =  (EditText) getActivity().findViewById(searchid);

        String srst = "";
        if (edt != null){srst = edt.getText().toString();}
        if (srst.equals("")){
            md.toast(getActivity(), "Please Enter A Name In The Search Bar");
        }
        else{
            insearch = true;
            Button bb = (Button) getActivity().findViewById(sbtnid );
            if (bb != null){ bb.setBackgroundResource(R.drawable.ic_baseline_clear_24);}

            sublin.removeAllViews();
            ProgressBar pb = new ProgressBar(getActivity()); pb.setVisibility(View.VISIBLE); sublin.addView(pb);


            MaterialTextView mt = new MaterialTextView(getActivity());  mt.setHeight(lineheight);
            mt.setWidth(width);  mt = md.textviewdesign(getActivity(), mt, "Info",DS);
            sublin.addView(mt); mt.setTextSize(md.fontmedium);     mt.setPadding(0, 0, 0, 0);
            mt.setText("Searching " +  MainActivity.itemname + "... Please Wait");


            gettable("Items", srst,   new MainActivity.VolleyCallBack() {
                @Override
                public void onSuccess() {
                    showdialogresults();
                }
            }, 0, getActivity());
        }
    }


    String tbresult = ""; boolean tbsuccess = false;
    public void gettable(String table,   String value, final MainActivity.VolleyCallBack callBack,
                         int attempts, Context cnt) {

        RequestQueue queue = Volley.newRequestQueue(cnt);

        final int attempt = attempts + 1;

        String mm = md.auth(getActivity(),  mydatabase,  android_id, uid)+"~"+table;
        if (MainActivity.shownumber.equals("1")){ mm +=  "^|Name_ItemNumber^Or^like^"+ value; }
        else{
            mm =  mm  + "^|Name^like^"+ value;
        }
        String url = md.url + "gettable?code=" + mm;

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
                    gettable( table, value,  callBack,  attempt, cnt);
                }else{
                    tbresult =  "Error: " + error.getMessage();
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    int searchpbid = 8425266;
    public void showdialogresults(){

        if (tbsuccess  & sublin != null){

            ArrayList<String> list  =  md.makearrayfromstring(tbresult, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            ArrayList<String> nodisp = md.makearrayfromstring(md.getstring(rules, 2), "~");
            ArrayList<String>  displaycols = md.makearrayfromstring(md.getstring(rules, 9), "~");
            sublin.removeAllViews();
            ProgressBar pb = new ProgressBar(getActivity()); pb.setVisibility(View.GONE);
            pb.setId(searchpbid ); sublin.addView(pb);

            int searchpre = 92463;

            if (list.size() > 2){

                View vw = new View(getActivity()); vw.setBackgroundColor(md.lightgray);
                vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2)); sublin.addView(vw);
            }
            for (int c = 2; c < list.size(); c++){
                int sid = 2+c+4592;
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                String idd =  md.before(md.getstring(list, c), "~");
                String name =  md.coloption(cols, "Name", row).Value;
                String inumber = md.coloption(cols, "ItemNumber", row).Value;
                String prc = md.coloption(cols, "SellingPrice", row).Value;
                String pic = md.coloption(cols, "Pic1", row).Value;
                String st = md.coloption(cols, "SizeText", row).Value;

                if (!st.equals("")){prc = prc + " " + st;}  final String price = prc;

                LinearLayout lnh = new LinearLayout(getActivity()); lnh.setOrientation(LinearLayout.HORIZONTAL);

                int picwidth = (int)(height * 0.13), imgid = 5545 + md.parseint(idd), nameid = 9432 + md.parseint(idd);
                int namewidth = width - ((int)(picwidth * 2)), iconwd = (int)(picwidth * 0.5);

                lnh.setLayoutParams(new LinearLayout.LayoutParams(width, picwidth)); sublin.addView(lnh);

                ImageView imgView =  md.image(getActivity(),imgid, DS );



                imgView.setLayoutParams(new ViewGroup.LayoutParams(picwidth, (int)(picwidth*0.9)));
                lnh.addView(imgView);
                String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ pic;
                imgView = md.loadimage(getResources() ,imgView, url, picwidth, (int)(picwidth*0.9) );

                String shownums  = md.appsetting("ShowItemNumber", DS), nm = name;
                if (shownums.toLowerCase().equals("true")){  nm += System.lineSeparator() + inumber; }
                nm += System.lineSeparator() + inumber;

                MaterialTextView nb = md.maketextview(getActivity(),  nm,"", nameid, "Info",
                        MainActivity.DisplayString, fontsmall, namewidth, picwidth, true);lnh.addView(nb);

                int topspace = (int)(0.5 * (picwidth - iconwd)), acticon = (int)(iconwd * 0.6);
                LinearLayout lnadd = new LinearLayout(getActivity());
                lnadd.setOrientation(LinearLayout.VERTICAL);
                lnadd.setLayoutParams(new LinearLayout.LayoutParams(iconwd, picwidth));
                lnadd.setPadding(4, topspace, 4, 0); lnh.addView(lnadd);

                MaterialButton mb = new MaterialButton(getActivity());
                mb.setLayoutParams(new LinearLayout.LayoutParams(acticon, acticon));
                mb = md.addtocarticon(mb, DS);
                mb.setPadding(10, 15, 10, 10); lnadd.addView(mb);
                mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                    addtocart(idd, name, 0, "1", price, inumber);  } });

                LinearLayout lnclose = new LinearLayout(getActivity());
                lnclose.setOrientation(LinearLayout.VERTICAL);
                lnclose.setLayoutParams(new LinearLayout.LayoutParams(iconwd, picwidth));
                lnclose.setPadding(6, topspace, 0, 0); lnh.addView(lnclose);

                mb = new MaterialButton(getActivity());
                mb.setLayoutParams(new LinearLayout.LayoutParams(acticon, acticon));
                mb.setBackgroundColor(md.brightred);
                mb.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                mb.setPadding(10, 15, 10, 10); lnclose.addView(mb);
                mb.setOnClickListener(new View.OnClickListener() {  @Override  public void onClick(View view) {
                    clicktofav(idd, name, price, inumber); } });


                View vw = new View(getActivity()); vw.setBackgroundColor(md.lightgray);
                vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2)); sublin.addView(vw);
            }
        }
        else  {
            md.toast(getActivity(), " Error, please try again");
        }


    }

    public void addqty(int qtid, int number){
        MaterialButton ed = getActivity().findViewById(qtid);
        String qt =  "";
        if (ed != null){
            qt = ed.getText().toString();
            int numb = 0;
            try{  numb = md.parseint(qt);  }catch (NumberFormatException nf){ }
            numb = numb + number;
            if (numb < 0){numb = 0;}
            ed.setText(String.valueOf(numb));
        }
    }

    public void  addtocart(String itemid, String name, int quantityid, String quantityamount, String price, String itemnumber){
        ProgressBar pb = (ProgressBar) getActivity().findViewById(searchpbid);
        if (pb != null){ pb.setVisibility(View.VISIBLE);}


        md.toast(getActivity(),   " Adding "+name+" To Cart!" );
        String qt = quantityamount;
        if (quantityid > 0){
            MaterialButton ed = getActivity().findViewById(quantityid);

            if (ed != null){   qt = ed.getText().toString();}
        }
        String total = "";

        total = String.valueOf((md.round((md.parsefloat(price) * md.parsefloat(qt)), 2)));

        addcart(itemid, price, qt, total, itemnumber, new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showadded(name);
            }
        }, 0, getActivity());

    }


    String adstring = ""; boolean addsuccess = false;
    public void addcart(String item, String price, String qty, String total, String itemnumber,
                        final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String cd = md.auth(getActivity(),  mydatabase, android_id, uid)+"~"+item +"~"+price+"~"+ qty+"~"+total + "~" + itemnumber;

        String url = md.url + "appaddcart?code="+cd;

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
                    adstring =  "Error: " + error.getMessage();
                    md.toast(getActivity(),  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }

    public void showadded(String name){
        if ( addsuccess){
            md.toast(getActivity(),   name+" Addded To Cart!" );
            ProgressBar pb = (ProgressBar) getActivity().findViewById(searchpbid);
            if (pb != null){
                pb.setVisibility(View.GONE);
                md.toast(getActivity(),  MainActivity.itemname + " Added To Cart!" );
            }
        }
        else  {
            md.toast(getActivity(),  "Failed To Add To Cart: "+  adstring );
        }
    }











    String imresult = ""; boolean imsuccess = false;

    public void gethomescreen(final MainActivity.VolleyCallBack callBack,
                              int attempts, Context cnt) {

        String t =  md.auth(getActivity(),  mydatabase,  android_id, uid);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "gethomescreen?code=" + t;

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
                    gethomescreen(  callBack,  attempt, cnt);
                }else{
                    imresult =  "Error: " + error.getMessage();
                    md.toast(getActivity(), "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showhomescreen(){

        if (!imsuccess){
            md.toast(getActivity(), imresult);
        }
        else if (imsuccess & sublin != null){
            int lineheight = (int) (height * 0.08);
            ArrayList<String> hometables = md.makearrayfromstring(imresult, "|");

            if (hometables.size() > 0){
                ArrayList<String>  splist = md.makearrayfromstring(md.getstring(hometables, 0), "¬");
                ArrayList<String>  spcols = md.makearrayfromstring(md.getstring(splist, 1), "~");

                int impre = 23783;
                boolean gospecials = true;
                if (Executor.equals("2") & md.appsetting("ShowExecutorSpecials", DS).toLowerCase().equals("false")){
                    gospecials = false;
                }

             if (gospecials){
                 for (int c = 2; c < splist.size(); c++){
                     ArrayList<String> row = md.makearrayfromstring(md.getstring(splist, c), "~");
                     String idd =  md.before(md.getstring(splist, c), "~");
                     int imgid = impre + md.parseint(idd);
                     String pic1 =  md.coloption(spcols, "Pic1", row).Value;

                     ImageView imgView = md.image(getActivity(), imgid, DS);
                     imgView.setLayoutParams(new ViewGroup.LayoutParams(width, width));
                     sublin.addView(imgView);
                     String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ pic1;
                     imgView = md.loadimage(getActivity().getResources(), imgView, url, width, width);
                     imgView.setPadding(0, 10, 0, 0);

                     imgView.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                             specialselected(idd);
                         }
                     });
                 }
             }

                ArrayList<String>  ss = new ArrayList<>();
                ss.add("ShowHomeScreenCategories");ss.add("ShowHomeScreenBrands");
                ss.add("ShowHomeScreenTopSellers");ss.add("ShowNewItemsOnHomeScreen");

                boolean topsellers = false, newitems = false;
                if (hometables.size() > 1){

                    for (int h = 1; h < hometables.size(); h++){
                        boolean go = false;String tablestring = md.getstring(hometables, h);
                        splist = md.makearrayfromstring(tablestring, "¬");
                        spcols = md.makearrayfromstring(md.getstring(splist, 1), "~");
                        ArrayList<String> sprules = md.makearrayfromstring(md.getstring(splist, 0), "#");
                        if (sprules.size() >= 8){
                            String tb = md.getstring(sprules, 7), title = "";
                            for (int s = 0; s < ss.size(); s++){
                                boolean strue = md.appsetting(md.getstring(ss, s), DS).toLowerCase().equals("true");
                                if (strue){
                                    if  (tb.equals("Categories") & md.getstring(ss, s).equals("ShowHomeScreenCategories"))
                                    { title = md.appsetting("HomeScreenCategoriesTitle", DS); go = true;
                                        if (Executor.equals("2") & md.appsetting("ShowExecutorCategories", DS).toLowerCase().equals("false")){
                                            go = false;
                                        }
                                    }
                                    else   if (tb.equals("Brands") & md.getstring(ss, s).equals("ShowHomeScreenBrands"))
                                    {title = md.appsetting("HomeScreenBrandsTitle", DS);  go = true;
                                        if (Executor.equals("2") & md.appsetting("ShowExecutorBrands", DS).toLowerCase().equals("false")){
                                            go = false;
                                        }
                                    }
                                    else   if (tb.equals("Items") & md.getstring(ss, s).equals("ShowHomeScreenTopSellers") & !topsellers )
                                    { title = md.appsetting("HomeScreenTopSellersTitle", DS); go = true; topsellers = true; break;}
                                    else if (tb.equals("Items") & md.getstring(ss, s).equals("ShowNewItemsOnHomeScreen") & !newitems)
                                    { title = md.appsetting("NewItemsOnHomeScreenTitle", DS); go = true; newitems = true; break;}
                                }
                            }

                            if ((tb.equals("Brands") || tb.equals("Categories")) & go)
                            {
                                String cl = "Brand"; if (tb.equals("Categories")){cl = "Category";} String col = cl;
                                View vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 6));
                                sublin.addView(vw);
                                if (!title.equals("")){
                                    MaterialTextView tv = md.maketextview(getActivity(), title, "", 0, "Info", DS, md.fontmedium,
                                            width, (int)(height * 0.08), true);tv.setTypeface(null, Typeface.BOLD); sublin.addView(tv);
                                }
                                vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2));
                                vw.setBackgroundColor(md.lightgray); sublin.addView(vw);

                                ArrayList<MyModel.option> brats = new ArrayList<>();
                                for (int c = 2; c < splist.size(); c++){
                                    ArrayList<String> row = md.makearrayfromstring(md.getstring(splist, c), "~");
                                    final String idd =  md.before(md.getstring(splist, c), "~");
                                    final String name =  md.coloption(spcols, "Name", row).Value;
                                    String pic1 =  md.coloption(spcols, "Pic1", row).Value;
                                    brats.add(new MyModel.option(idd, name, pic1, ""));
                                }

                                int bratht = (int)(width * 0.5), bratline  = (int)(height * 0.08);

                                HorizontalScrollView ns  = new HorizontalScrollView(getActivity());
                                ns.setScrollbarFadingEnabled(false);
                                ns.setLayoutParams(new LinearLayout.LayoutParams(width, bratht + bratline)); sublin.addView(ns);
                                LinearLayout nsln = new LinearLayout(getActivity()); nsln.setOrientation(LinearLayout.HORIZONTAL);
                                nsln.setLayoutParams(new LinearLayout.LayoutParams(width, bratht + bratline));
                                ns.addView(nsln);

                                for (int i = 0; i < brats.size(); i++){
                                    String bcid = md.getoption(brats, i).ID, bcnm = md.getoption(brats, i).Option1;
                                    LinearLayout iln = new LinearLayout(getActivity()); iln.setOrientation(LinearLayout.VERTICAL);
                                    iln.setLayoutParams(new LinearLayout.LayoutParams(bratht , bratht + bratline));nsln.addView(iln);

                                    int itimgid = h +2932 + md.parseint(md.getoption(brats, i).ID);



                                    ImageView imgView = md.image(getActivity(), itimgid, DS );


                                    imgView.setLayoutParams(new ViewGroup.LayoutParams(bratht, bratht));
                                    iln.addView(imgView);
                                    String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ md.getoption(brats, i).Option2;
                                    imgView = md.loadimage(getResources() ,imgView, url, bratht, bratht );
                                    imgView.setPadding(0, 10, 0, 0);
                                    imgView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {  brandcatselected(bcid, col, bcnm); }  });
                                    MaterialTextView tv = md.maketextview(getActivity(), bcnm, "", 0, "Info",
                                            DS, md.fontmedium,  bratht, bratline, true);
                                    iln.addView(tv); tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            brandcatselected(bcid, col, bcnm);
                                        }
                                    });

                                }

                                vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2));
                                vw.setBackgroundColor(md.lightgray); sublin.addView(vw);

                            }
                            else if (tb.equals("Items") & go){
                                if (!title.equals("")){
                                    View vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 6));
                                    sublin.addView(vw);

                                    MaterialTextView tv = md.maketextview(getActivity(), title, "", 0, "Info", DS, md.fontmedium,
                                            width, (int)(height * 0.08), true);tv.setTypeface(null, Typeface.BOLD); sublin.addView(tv);

                                    vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2));
                                    vw.setBackgroundColor(md.lightgray); sublin.addView(vw);
                                }

                                for (int c = 2; c < splist.size(); c++){
                                    ArrayList<String> row = md.makearrayfromstring(md.getstring(splist, c), "~");
                                    final String idd =  md.before(md.getstring(splist, c), "~");
                                    final String name =  md.coloption(spcols, "Name", row).Value;
                                    final String inum =  md.coloption(spcols, "ItemNumber", row).Value;
                                    String prc =  md.coloption(spcols, "SellingPrice", row).Value;
                                    String vit =  md.coloption(spcols, "ValueItemType", row).Value;
                                    String sztxt =  md.coloption(spcols, "SizeText", row).Value;
                                    String pic1 =  md.coloption(spcols, "Pic1", row).Value;

                                    final String price = String.valueOf(md.round(md.parsefloat(prc), 2));
                                    int nid = impre + (c - 2), reprelid = 5432534 + (c-2);

                                    int blockheight = (int)(width * 0.8), withfaveheight = width;
                                    LinearLayout  repln = new LinearLayout(getActivity()); repln.setOrientation(LinearLayout.HORIZONTAL);
                                    repln.setLayoutParams(new LinearLayout.LayoutParams(width, blockheight)); repln.setId(reprelid);

                                    sublin.addView(repln); repln.setPadding(5, 10, 5, 10);
                                    final int topleftt = 103 + h + c ,  imageid = 203 + h + c ,
                                            labelid  = 303 +h +  c , pricetagid = 403 +h +  c, addtocartid =  503 + h + c,
                                            addtofavid =  603 + h + c ;

                                    LinearLayout pln = new LinearLayout(getActivity()); pln.setId(topleftt);
                                    LinearLayout.LayoutParams rt = new LinearLayout.LayoutParams((int) (width * 0.48),
                                            ViewGroup.LayoutParams.MATCH_PARENT);
                                    pln.setLayoutParams(rt);
                                    pln = md.productdisplay(getActivity(), pln, idd,  name, inum, price, sztxt, pic1, (int) (width * 0.48), blockheight,
                                            DS, imageid, labelid, pricetagid, addtocartid, addtofavid, withfaveheight, smallscreen);
                                    repln.addView(pln);

                                    final String qty  = "1";
                                    ImageView img1 = (ImageView) getActivity().findViewById(imageid);
                                    TextView tv1 = (TextView) getActivity().findViewById(labelid);
                                    MaterialTextView mt1 = (MaterialTextView)getActivity().findViewById(pricetagid);
                                    MaterialButton mb1 = (MaterialButton) getActivity().findViewById(addtocartid);
                                    MaterialButton mb1a = (MaterialButton) getActivity().findViewById(addtofavid);
                                    if (img1 != null & tv1 != null & mt1 != null & mb1 != null & mb1a != null){
                                        img1.setOnClickListener(new View.OnClickListener() {
                                            @Override public void onClick(View view) {   productdets(idd, tablestring); }  });
                                        tv1.setOnClickListener(new View.OnClickListener() {
                                            @Override  public void onClick(View view) {  productdets(idd, tablestring);  }  });
                                        mt1.setOnClickListener(new View.OnClickListener() {
                                            @Override public void onClick(View view) { addtocart(idd, name, 0, qty, price,  inum); }  });
                                        mb1.setOnClickListener(new View.OnClickListener() {
                                            @Override  public void onClick(View view) {addtocart(idd, name, 0, qty, price,  inum);}  });
                                        mb1a.setOnClickListener(new View.OnClickListener() {
                                            @Override  public void onClick(View view) {  clicktofav(idd, name, price, inum); }  });
                                    }

                                    if (c < (splist.size() - 1)){  c = c + 1;
                                        final int  imageid2 = imageid + 3,   labelid2  = labelid + 3, pricetagid2 = pricetagid + 3,
                                                addtocartid2 =  addtocartid + 3, addtofavid2 =  addtofavid + 3;

                                        final String idd2 =  md.before(md.getstring(splist, c), "~");
                                        row = md.makearrayfromstring(md.getstring(splist, c), "~");
                                        final String name2 =  md.coloption(spcols, "Name", row).Value;
                                        final String inum2 =  md.coloption(spcols, "ItemNumber", row).Value;
                                        prc =  md.coloption(spcols, "SellingPrice", row).Value;
                                        pic1 =  md.coloption(spcols, "Pic1", row).Value;
                                        sztxt = md.coloption(spcols, "SizeText", row).Value;
                                        final String price2 = String.valueOf(md.round(md.parsefloat(prc), 2));

                                        View vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(
                                                (int) (width * 0.02), ViewGroup.LayoutParams.MATCH_PARENT)); repln.addView(vw);

                                        pln = new LinearLayout(getActivity());
                                        pln.setLayoutParams(new LinearLayout.LayoutParams((int) (width * 0.48), ViewGroup.LayoutParams.MATCH_PARENT));
                                        pln = md.productdisplay(getActivity(), pln, idd2, name2, inum2, price2, sztxt, pic1, (int) (width * 0.48), blockheight,
                                                DS, imageid2, labelid2, pricetagid2, addtocartid2, addtofavid2, withfaveheight, smallscreen);
                                        repln.addView(pln);

                                        ImageView img = (ImageView) getActivity().findViewById(imageid2);
                                        TextView tv= (TextView) getActivity().findViewById(labelid2);
                                        MaterialTextView mt = (MaterialTextView) getActivity().findViewById(pricetagid2);
                                        MaterialButton mb = (MaterialButton) getActivity().findViewById(addtocartid2);
                                        MaterialButton mb2 = (MaterialButton) getActivity().findViewById(addtofavid2);

                                        if (img != null & tv != null & mt != null & mb != null & mb2 != null){
                                            img.setOnClickListener(new View.OnClickListener() {
                                                @Override public void onClick(View view) {  productdets(idd2, tablestring); }  });
                                            tv.setOnClickListener(new View.OnClickListener() {
                                                @Override  public void onClick(View view) {  productdets(idd2, tablestring);  }  });
                                            mt.setOnClickListener(new View.OnClickListener() {
                                                @Override public void onClick(View view) { addtocart(idd2, name2, 0, qty, price2,  inum2); }  });
                                            mb.setOnClickListener(new View.OnClickListener() {
                                                @Override  public void onClick(View view) { addtocart(idd2, name2, 0, qty, price2,  inum2);  }  });
                                            mb2.setOnClickListener(new View.OnClickListener() {
                                                @Override  public void onClick(View view) {  clicktofav(idd2, name2, price2, inum2); }  });
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }


        }
        else  {
            md.toast(getActivity(), " Error, please try again");
        }
    }

    public void productdets(String id, String resultstring){
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "ProductDetails",  DS, MainActivity.Executor, id, resultstring, "",  "",  ""});
        Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void clicktofav(String itemid, String name, String price,  String itemnumber){

        md.toast(getActivity(),   " Adding "+name+" To Favourites!" );

        addfav(itemid, price, itemnumber, new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showaddedfav();
            }
        }, 0, getActivity());
    }
    String afstring = ""; boolean afsuccess = false;


    public void addfav(String item, String price, String itemnumber,
                       final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "appaddfav?code="+md.auth(getActivity(),  mydatabase,  android_id, uid)+   item +"~"+price+"~"+ itemnumber;

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
                    md.toast(getActivity(),  "Failed To Reach Server... Retrying" );
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showaddedfav(){
        if ( afsuccess){
            md.toast(getActivity(),  MainActivity.itemname + " Added To Favourites!" );
        }
        else  {
            md.toast(getActivity(),  "Failed To Add To Favourites. Please Try Again" );
        }
    }


    boolean bcsuccess = false; String bcresult = "";

    public void brandcatselected(String id, String column, String name){

        md.toast(getActivity(), "Loading " + name);
        loadbrandcatitems(id, column,  new MainActivity.VolleyCallBack() {
            @Override  public void onSuccess() { showbrandcatitems(); }
        }, 0, getActivity());
    }
    public void loadbrandcatitems(String ids, String column, final MainActivity.VolleyCallBack callBack,
                                  int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String ur =  md.auth(getActivity(),  mydatabase,  android_id, uid)+"~Items^|"+column+"^"+ ids;

        String url = md.url + "gettable?code=" + ur;


        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        bcresult  = response;
                        bcsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bcsuccess = false;

                if (attempt < md.attempts){
                    loadbrandcatitems(ids, column,  callBack,  attempt, cnt);
                }else{
                    psresult =  "Error: " + error.getMessage();
                    md.toast(getActivity(), "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showbrandcatitems(){

        if (!bcsuccess){
            md.toast(getActivity(),"Failed To Load, Please Close The App & Try Again");
        }
        else if (bcsuccess ){

            ArrayList<String>  bclist = md.makearrayfromstring(bcresult, "¬");
            ArrayList<String>  bccols = md.makearrayfromstring(md.getstring(bclist, 1), "~");

            String items = "";
            for (int c = 2; c < bclist.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(bclist, c), "~");
                String idd =  md.before(md.getstring(bclist, c), "~");
                items += "~" +idd;
            }

            if (!items.equals("")){
                Bundle b = new Bundle();
                b.putStringArray("key", new String[]{ "ItemLists",  DS, MainActivity.Executor, "Items", "ID", items,  "",  ""});
                Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        }
        else  {
            md.toast(getActivity(), " Error, please try again");
        }
    }








    public void specialselected(String specialid){
         /*   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            AlertDialog alert = builder.create();
            builder.setMessage("")
                    .setTitle("");
            builder.create();
            alert = builder.create();


            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.setContentView(R.layout.custom);
            alert.setCancelable(true);
            alert.setCanceledOnTouchOutside(true);
            Window window = alert.getWindow();
            window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

            LinearLayout cuslin = (LinearLayout) alert.findViewById(R.id.customlinear);
            cuslin.setOrientation(LinearLayout.VERTICAL);
            cuslin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,
                    ViewGroup.LayoutParams.MATCH_PARENT  ));

            ProgressBar pbbar = new ProgressBar(getContext());
            cuslin.addView(pbbar);pbbar.setVisibility(View.VISIBLE);

            alert.show();

    */


        loadspecialitems(specialid,  new MainActivity.VolleyCallBack() {
            @Override  public void onSuccess() { showspeciaitems(); }
        }, 0, getActivity());
    }

    boolean pssuccess = false; String psresult = "";
    public void loadspecialitems(String specialid, final MainActivity.VolleyCallBack callBack,
                                 int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);
        String ur =  md.auth(getActivity(),  mydatabase,  android_id, uid)+"~SpecialsItems^|Special^"+ specialid;


        String url = md.url + "gettable?code=" + ur;
        final int attempt = attempts + 1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        psresult  = response;
                        pssuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pssuccess = false;

                if (attempt < md.attempts){
                    loadspecialitems(specialid,  callBack,  attempt, cnt);
                }else{
                    psresult =  "Error: " + error.getMessage();
                    md.toast(getActivity(), "Failed To Reach Server... Retrying");
                }
            }
        });

        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }

    public void showspeciaitems(){

        if (!pssuccess){
            md.toast(getActivity(), psresult);

        }
        else if (pssuccess ){

            ArrayList<String>  pslist = md.makearrayfromstring(psresult, "¬");
            ArrayList<String>  imcols = md.makearrayfromstring(md.getstring(pslist, 1), "~");

            String items = "";
            for (int c = 2; c < pslist.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(pslist, c), "~");
                String idd =  md.before(md.getstring(pslist, c), "~");
                String item =  md.coloption(imcols, "ValueItem", row).Value;
                items += "~" +item;
            }

            if (!items.equals("")){
                Bundle b = new Bundle();
                b.putStringArray("key", new String[]{ "ItemLists",  DS, MainActivity.Executor, "Items", "ID", items,  "",  ""});
                Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        }
        else  {
            md.toast(getActivity(), " Error, please try again");
        }
    }























    String odresult = ""; boolean odsuccess = false; int odpbid = 111197;

    public void getbookinglist(final MainActivity.VolleyCallBack callBack,
                               int attempts, Context cnt) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(cnt);

        String cd = md.auth(getActivity(),  mydatabase, android_id, uid);
        String ourl = md.url + "gettodayorders?code=" + cd;
        final int attempt = attempts + 1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ourl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        odresult  = response;
                        odsuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                odsuccess = false;
                if (attempt < md.attempts){
                    getbookinglist( callBack,  attempt, cnt);
                }else{
                    odresult =  "Error: " + error.getMessage();
                    md.toast(getActivity(), "Failed To Reach Server... Retrying");
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
    }



    ArrayList<MyModel.option> bkadded = new ArrayList<>();
    public void showbookings(){

        if (!odsuccess){
            md.toast(getActivity(), odresult);
        }
        else if (odsuccess & sublin != null){

            ArrayList<String>  bklist = md.makearrayfromstring(odresult, "¬");
            ArrayList<String>  bkcols = md.makearrayfromstring(md.getstring(bklist,1), "~");

            ProgressBar pbb = (ProgressBar) getActivity().findViewById(odpbid);

            if (bklist.size() == 2 ){
                if (pbb != null){
                pbb.setVisibility(View.GONE);}
                Button tv = new Button(getActivity()); tv.setBackgroundColor(md.transparent);
                tv.setTextSize(md.fontmedium);
                tv.setText(md.appsetting("TodayNoOrdersMessage", DS));
                tv.setLayoutParams(new LinearLayout.LayoutParams(width,  lineheight ));sublin.addView(tv);
            }
            else if (bklist.size() > 2) {
                if (pbb != null){
                    pbb.setVisibility(View.GONE);}
                for (int c = 2; c < bklist.size(); c++){
                    ArrayList<String> row = md.makearrayfromstring(md.getstring(bklist, c), "~");
                    String idd =  md.before(md.getstring(bklist, c), "~");
                    String itp =  md.coloption(bkcols, "ValueItemType", row).Value;
                    String oid =  md.coloption(bkcols, "ValueOrder", row).Value;

                    View vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 6));
                    sublin.addView(vw);

                    LinearLayout ln = new LinearLayout(getActivity());ln.setOrientation(LinearLayout.VERTICAL);
                    ln.setBackgroundResource(R.drawable.whitebutton); sublin.addView(ln);
                    int lnpad = 6, linwidth = width - (lnpad * 2);
                    ln.setPadding(lnpad, 5, lnpad, 5);

                    if (itp.equals("2")){
                        String nm =  md.coloption(bkcols, "Item", row).Value;
                        String exc = md.coloption(bkcols, "Executor", row).Value;
                        String user = md.coloption(bkcols, "User", row).Value;
                        String sdt = md.coloption(bkcols, "StartDate", row).Value;
                        String edt = md.coloption(bkcols, "EndDate", row).Value;
                        String stm = md.coloption(bkcols, "StartTime", row).Value;
                        String edm = md.coloption(bkcols, "EndTime", row).Value;
                        String bks = md.coloption(bkcols, "BookingStatus", row).Value;

                        MaterialButton mb = new MaterialButton(getActivity()); mb.setText(nm);
                        mb = md.buttondesign(getActivity(), mb, "Warning", DS); mb.setTextSize(md.fontsmall);
                        mb.setTextColor(md.white);
                        mb.setLayoutParams(new LinearLayout.LayoutParams(linwidth,  lineheight ));

                        String dsn = "", dsmess = "", dsd = "";


                        if (Executor.equals("3")){
                            dsn= exc;
                            dsd = md.coloption(bkcols, "ValueExecutor", row).Value;
                            if (exc.equals("")){dsmess  = "Tutor To Be Confirmed";}else {dsmess = "Tutor: " + exc;}
                        } else if (Executor.equals("1") || Executor.equals("2")){
                            dsmess = user; dsn = user;
                            dsd = md.coloption(bkcols, "ValueUser", row).Value;
                        }
                        final String dsname = dsn, dsid = dsd, itemname = md.appsetting("ItemNameSingular", DS);

                        ln.addView(mb);mb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (!dsid.equals("")){

                                    androidx.appcompat.app.AlertDialog.Builder builder =
                                            new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                                    androidx.appcompat.app.AlertDialog alert = builder.create();
                                    builder.setMessage("")
                                            .setTitle(nm)
                                            .setNeutralButton("View "+ itemname + " Details", (dialog, which) -> {

                                                dialog.cancel();
                                                vieworder( idd,  "CustomerOrderItems");
                                            } )
                                            .setNegativeButton("View Location For "+ dsname, (dialog, which) -> {

                                                dialog.cancel();

                                                boolean getpermission = false;
                                                GpsTracker gpsTracker = new GpsTracker(getActivity());
                                                if(gpsTracker.canGetLocation()){

                                                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                                                            PackageManager.PERMISSION_GRANTED &&
                                                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                                                    PackageManager.PERMISSION_GRANTED) {


                                                        Bundle b = new Bundle();
                                                        b.putStringArray("key", new String[]
                                                                {"View",  DS, uid, Executor, "Users", dsid,"Location", ""  });
                                                        Intent intent = new Intent(getActivity(), SetMap.class);
                                                        intent.putExtras(b);
                                                        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);

                                                    }else{
                                                        getpermission = true;
                                                    }

                                                }else{
                                                    getpermission = true;
                                                }
                                                if (getpermission){
                                                    md.toast(getActivity(), "Failed To Get Your Current Location");
                                                    gpsTracker.showSettingsAlert();
                                                }

                                            } );
                                    builder.create();
                                    alert = builder.create();
                                    alert.show();

                                }else{
                                    vieworder( idd,  "CustomerOrderItems");

                                }
                            }
                        });


                        TextView tv = new TextView(getActivity()); tv.setBackgroundColor(Color.TRANSPARENT);
                        tv.setTextSize(md.fontmedium); ln.addView(tv); tv.setText(dsmess);


                        String time = stm + " - " + edm;
                        if (!sdt.equals(edt)){time = sdt + " "+stm + " - " + edt + " "+edm;}
                        tv = new TextView(getActivity()); tv.setBackgroundColor(Color.TRANSPARENT);
                        tv.setText(time); tv.setTextSize(md.fontmedium);ln.addView(tv);

                        if (!bks.equals("")){
                            tv = new TextView(getActivity()); tv.setBackgroundColor(Color.TRANSPARENT);
                            tv.setText(bks);tv.setTextSize(md.fontmedium); ln.addView(tv);
                        }



                    }

                    else if (itp.equals("1")){

                        String onum =  md.coloption(bkcols, "Order", row).Value;
                        boolean ado  = true;
                        for (int h = 0;h <bkadded.size(); h++){
                            if (md.getoption(bkadded, h).ID.equals(oid)){ado = false;}
                        }
                        if (ado){
                            bkadded.add(new MyModel.option(oid, "", "", ""));

                            String time = md.coloption(bkcols, "Created", row).Value;
                            String bks = md.coloption(bkcols, "Status", row).Value;

                            MaterialButton mb = new MaterialButton(getActivity()); mb.setText(onum);
                            mb = md.buttondesign(getActivity(), mb, "Info", DS); mb.setTextSize(md.fontmedium);
                            mb.setTextColor(md.white);
                            mb.setLayoutParams(new LinearLayout.LayoutParams(width,  lineheight ));
                            ln.addView(mb);mb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    vieworder( oid,  "CustomerOrders");
                                }  });

                            TextView tv = new TextView(getActivity()); tv.setBackgroundColor(Color.TRANSPARENT);
                            tv.setText(time); tv.setTextSize(md.fontmedium);ln.addView(tv);

                            if (!bks.equals("")){
                                tv = new TextView(getActivity()); tv.setBackgroundColor(Color.TRANSPARENT);
                                tv.setText(bks);tv.setTextSize(md.fontmedium); ln.addView(tv);
                            }
                        }
                    }
                }
            }

        }

    }

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public void vieworder( String rowid,  String tablename){

        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ DS,  MainActivity.uref, Executor, tablename, rowid, "",
                "CustomerOrderItems", ""});
        Intent intent = new Intent(getActivity().getApplicationContext(), Orders.class);
        intent.putExtras(b);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    public void bookingselected(String orderid, String orderitemid){
         /*   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            AlertDialog alert = builder.create();
            builder.setMessage("")
                    .setTitle("");
            builder.create();
            alert = builder.create();

            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.setContentView(R.layout.custom);
            alert.setCancelable(true);
            alert.setCanceledOnTouchOutside(true);
            Window window = alert.getWindow();
            window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

            LinearLayout cuslin = (LinearLayout) alert.findViewById(R.id.customlinear);
            cuslin.setOrientation(LinearLayout.VERTICAL);
            cuslin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,
                    ViewGroup.LayoutParams.MATCH_PARENT  ));

            ProgressBar pbbar = new ProgressBar(getContext());
            cuslin.addView(pbbar);pbbar.setVisibility(View.VISIBLE);

            alert.show();

    */
           /* loadspecialitems(specialid,  new MainActivity.VolleyCallBack() {
                @Override  public void onSuccess() { showspeciaitems(); }
            }, 0, getActivity());*/
    }















    int dashboardid = 10101,  gapht  = 0, lowidth = 0, vdashheight = 0;


    public void gohistory(String table){
        if (Executor.equals("1")){
            Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
            Bundle b = new Bundle();
            b.putStringArray("key", new String[]{ "History",  DS, MainActivity.Executor, table, "", "", ""});
            intent.putExtras(b);
            startActivity(intent);
        }else{
            md.toast(getActivity(), "Sorry, Only Admin Staff Can View Transaction History");
        }
    }


    public void gouser(){


        md.toast(getActivity(), "Only Admin Can View And Add Staff");
    }




    public void goonoffline(){
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "OnOffline",  DS, MainActivity.Executor, online,
                "",  "", "", ""});
        Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
        intent.putExtras(b);
        //  startActivity(intent);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    public void askposlogin(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alert = builder.create();
        builder.setMessage(message)
                .setTitle("Sign In")
                .setNeutralButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                } )
                .setNegativeButton("Sign In", (dialog, which) -> {
                    gopossignin();
                } );
        builder.create();
        alert = builder.create();
        alert.show();

    }


    public void gopossignin(){
        Bundle b = new Bundle();

        MyModel.option   recs = md.sqlupdate(new ArrayList<MyModel.option>(),  mydatabase);

        posuserid = recs.Option2;// = rs.getString(rs.getColumnIndex("POSUSERID"));
        posusername =  recs.Option3;// = rs.getString(rs.getColumnIndex("POSUSER"));
        CompanyLocation =  recs.Option4;
        locationname = recs.Option5 ;//= rs.getString(rs.getColumnIndex("COMPANYLOCATIONNAME"));

        b.putStringArray("key", new String[]{ "PointOfSaleSignIn",  DS, MainActivity.Executor, CompanyLocation, posuserid,
                locationname, posusername,  transactiontype});
        Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
        intent.putExtras(b);
        //  startActivity(intent);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    public void gosale(){
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ MainActivity.uref, Executor, MainActivity.DisplayString
                ,MainActivity.POS,
                CompanyLocation, MainActivity.currencysymbol,MainActivity.currencycode, posuserid, online});
        Intent intent = new Intent(getActivity(), Transact.class);
        intent.putExtras(b);
        startActivity(intent);
    }





    String transactiontype = "";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bundle b = data.getExtras();
                String[] received = b.getStringArray("key");
                if (received[0].equals("PointOfSaleUser")){
                    posuserid = md.getarray(received, 2);
                    CompanyLocation = md.getarray(received, 3);
                    posusername = md.getarray(received, 4);
                    locationname = md.getarray(received, 5);
                    String godirection = md.getarray(received, 6);

                    ArrayList<MyModel.option> values = new ArrayList<>();
                    values.add(new MyModel.option("POSUSERID", posuserid, "", ""));
                    values.add(new MyModel.option("POSUSER", posusername, "", ""));
                    values.add(new MyModel.option("COMPANYLOCATION", CompanyLocation, "", ""));
                    values.add(new MyModel.option("COMPANYLOCATIONNAME", locationname, "", ""));

                    md.sqlupdate(values,  mydatabase);

                    if (md.getarray(received, 1).equals("1")){
                        md.toast(getActivity(), posusername  + " Has Signed Into "+  locationname);
                    }else  if (md.getarray(received, 1).equals("0")){
                        md.toast(getActivity(), posusername  + " Has Signed Out Of Point Of Sale");
                    }
                    if (godirection.equals("Sale")){  gosale();  }
                }
                else  if (received[0].equals("OnOffline")){
                    online = md.getarray(received, 1);

                    ArrayList<MyModel.option> values = new ArrayList<>();
                    values.add(new MyModel.option("ONLINE", online, "", ""));
                    md.sqlupdate(values,  mydatabase);

                    if (online.equals("1")){
                        md.toast(getActivity(), "Online Mode Activated");
                    }else   if (online.equals("0")){
                        md.toast(getActivity(), "Device Turned Offline");
                    }
                }

            }
        }
    }









    String dsresult = ""; boolean dssuccess = false;
    public void getdash(final MainActivity.VolleyCallBack callBack,
                        int attempts, Context cnt) {

        RequestQueue queue = Volley.newRequestQueue(cnt);

        final int attempt = attempts + 1;

        String url = md.url + "getvaldashboard?code=" + md.auth(getActivity(),  mydatabase,  android_id, uid);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dsresult  = response;
                        dssuccess  = true;
                        callBack.onSuccess();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dssuccess = false;
                dsresult =  "Error: " + error.getMessage();
                if (attempt < md.attempts){
                    getdash(  callBack,  attempt, cnt);
                }else{
                    dsresult =  "Error: " + error.getMessage();
                }
            }
        });
        stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);

    }



}
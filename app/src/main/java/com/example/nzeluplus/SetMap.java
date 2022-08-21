package com.example.nzeluplus;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.nzeluplus.databinding.ActivitySetMapBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;


public class SetMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivitySetMapBinding binding;


    String mode = "", DisplayString = "", uref = "", table = "", rowid = "", column = "", values = "",
            android_id = MainActivity.android_id, executor = "";
    boolean mapnew;
    int height, width;
    Handler hdl = new Handler();
    int delay = 5000;
    MyModel md = new MyModel();

    double mylatitude = 1000, mylongitude = 1000;

    SQLiteDatabase mydatabase;

    float zoom = 14.0f;

    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySetMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DisplayMetrics metrics = new DisplayMetrics();
        (SetMap.this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String[] receive = b.getStringArray("key");
        mode = receive[0];
        DisplayString = receive[1];
        uref = receive[2];
        executor = receive[3];
        table = receive[4];
        rowid = receive[5];
        column = receive[6];
        values = receive[7];


        mydatabase = openOrCreateDatabase(md.DbName,MODE_PRIVATE,null);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {


            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);  mapFragment.getMapAsync(this);
        }else{
            Intent inti = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(inti);
        }
    }



    public void getLocation(GoogleMap googleMap){
        gpsTracker = new GpsTracker(SetMap.this);
        if(gpsTracker.canGetLocation()){
            mylatitude = gpsTracker.getLatitude();
            mylongitude = gpsTracker.getLongitude();

            if (mode.equals("Set")){

                googleMap.clear();

                LatLng sy = new LatLng(mylatitude, mylongitude);
                mMap.addMarker(new MarkerOptions().position(sy).title("My Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sy));

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylatitude,
                        mylongitude), zoom));


                md.toast(SetMap.this, "Tap On The Map To Set Your Location");
            }

        }else{
            md.toast(SetMap.this, "Failed To Get Your Current Location");
            gpsTracker.showSettingsAlert();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        md.toast(SetMap.this, "Loading Locations... Please Wait");


        if (mode.equals("View") || mode.equals("Executors")){
            // Setting a custom info window adapter for the google map
            MarkerInfoWindowAdapter markerInfoWindowAdapter =
                    new MarkerInfoWindowAdapter(SetMap.this);
            googleMap.setInfoWindowAdapter(markerInfoWindowAdapter);

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    popupwindow(marker);
                    return true; } });


            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
              /*      new PurchaseConfirmationDialogFragment().show(
                            getChildFragmentManager(), "");*/
                }
            });


          /*  googleMap.setOnCameraChangeListener(pos -> {
                if (pos.zoom != zoom){
                    zoom = pos.zoom;
                }
            });
*/

            mapnew = true;
            hdl.postDelayed(new Runnable(){
                public void run(){
                    getLocation(googleMap);
                    getmapdata(new MainActivity.VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            showmap(googleMap);
                        }
                    }, 0);
                    hdl.postDelayed(this, delay);
                }
            }, delay);
        }
        else{


            hdl.postDelayed(new Runnable(){
                public void run(){
                    getLocation(googleMap);
                }
            }, delay);

            mMap.setOnMapClickListener(latLng -> {
                LatLng sydney = new LatLng( latLng.latitude,  latLng.longitude);
                lat = String.valueOf(latLng.latitude); lon = String.valueOf(latLng.longitude);
                /*googleMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .title("My Location"));*/

                showDialog(222);
            });
        }
    }
    String lat = "", lon = "";



    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        if (id == 222){
            return new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage("Set this as location?")
                    .setTitle("Set As My Location")
                    //.setNeutralButton("Cancel", (dialog, which) -> {} )
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Bundle b = new Bundle();
                            b.putStringArray("key", new String[]{"Location", lat + "," + lon});
                            Intent intent = new Intent();
                            intent.putExtras(b);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                        }
                    })
                    .create();
        }

        return null;
    }










    boolean mapSuccess = false; String mapstring = "";

    public void getmapdata( final MainActivity.VolleyCallBack callBack,  int attempts) {

        try{
            RequestQueue queue = Volley.newRequestQueue(SetMap.this);

            String locc = String.valueOf(mylatitude) + "," + String.valueOf(mylongitude);

            if ((mylatitude > 0 || mylatitude < 0) & (mylongitude > 0 || mylongitude < 0)){

                String url = md.url + "setandgetlocation?code="+
                        md.auth(SetMap.this, mydatabase, android_id, uref)+"~"+locc+"~"+rowid+"~"+mode+"~"+table+"~"+column;

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
            md.toast(SetMap.this, String.valueOf(npx.getMessage()));
        }
    }

    ArrayList<String> vees = new ArrayList<>(),  cols = new ArrayList<>();
    String otherid = "121", myid = "122";
    public void showmap(GoogleMap googleMap){


        if (mapstring.indexOf("¬") > -1){
            if (!mapnew){
                googleMap.clear();
            }
            vees = md.makearrayfromstring(mapstring, "¬");
            cols = md.makearrayfromstring(vees.get(1), "~");

            boolean gozoom = true;
            Location locc = new Location("");


            int myimage = R.drawable.graduated, otherimage = R.drawable.teacher;
            if (executor.equals("3") & mode.equals("View"))
            {myimage = R.drawable.teacher; otherimage = R.drawable.graduated;}

            MarkerOptions mk = new MarkerOptions()
                    .position(new LatLng(mylatitude, mylongitude))
                    .title("Me")
                    .icon(bitmapDescriptorFromVector(SetMap.this, myimage));
            mk.snippet("My Location");
            Marker marker = googleMap.addMarker(mk);
            marker.setTag(md.parseint(myid));



            for (int i = 2; i < vees.size(); i++){
                ArrayList<String> row = md.makearrayfromstring(vees.get(i), "~");
                String vid =  md.before(vees.get(i), "~");

                String precol = "";
                if (mode.equals("Executors")){ precol = "User";}

                String name =  md.coloption(cols, precol +"Name", row).Value + " " +
                        md.coloption(cols, precol +"Surname", row).Value;
                String loc =  md.coloption(cols, precol +"Location", row).Value;
                String loctime =  md.coloption(cols, precol +"LocationTime", row).Value;
                String access =  md.coloption(cols, precol +"Access", row).Value;
                String markerid = otherid;
                if (mode.equals("Executors")){
                  markerid =  md.coloption(cols, "ValueUser", row).Value;
                }

                boolean canshow = true;


                if (mode.equals("Executors")){name = name + " (Location At " + loctime + ")";
                if (!access.toLowerCase().equals("true")){canshow = false;}
                }

                if (loc.indexOf(",") > -1 & loc.length() > 10 & canshow){

                    Double lat = md.parsedouble(md.before(loc, ",")),
                            lon = md.parsedouble(md.breakurl(loc, 1, ","));
                    locc.setLatitude(lat); locc.setLongitude(lon);
                    try{
                        MarkerOptions  mv = new MarkerOptions()
                                .position(new LatLng(lat, lon))
                                .title(name)

                                .icon(bitmapDescriptorFromVector(SetMap.this, otherimage));
                        mv.snippet(name);
                        Marker mark = googleMap.addMarker(mv);
                        mark.setTag(md.parseint( markerid));
                    }catch(Exception xx){}

                    if (mapnew ){ googleMap.moveCamera(CameraUpdateFactory.newLatLng(new
                            LatLng(mylatitude, mylongitude)));}
                }
                else{
                  if (mode.equals("View")){
                      gozoom = false;
                      md.toast(this, "Location Not Found For " + name);
                  }
                }
            }
            if (mapnew  & gozoom ){
                if (locc.getLongitude() != 0 & mode.equals("View")){
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locc.getLatitude(),
                            locc.getLongitude()), zoom));
                }else{
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylatitude,
                            mylongitude), zoom));
                }
                mapnew = false;
                delay = 5000;
            }
        }
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        int ht = (int) (height * 0.1), wd = (int) (width * 0.15);
        Bitmap bitmap;
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, wd, ht);
        bitmap = Bitmap.createBitmap(wd, ht, Bitmap.Config.ARGB_8888);
        bitmap = Bitmap.createBitmap(wd, ht, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




    Integer selectedvehicle = 0;
    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private Context context;
        public MarkerInfoWindowAdapter(Context context) { this.context = context.getApplicationContext(); }

        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }


        @Override
        public View getInfoContents(Marker mk) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v =  inflater.inflate(R.layout.custom, null);

            LinearLayout clin = (LinearLayout) v.findViewById(R.id.customlinear);
            clin.setBackgroundResource(R.drawable.backgroundxml);

            TextView tv = new TextView(SetMap.this); tv.setText(mk.getTitle());
            tv.setPadding(2, 2, 2, 4);
            tv.setTextSize(md.fontsmall); tv.setTypeface(null, Typeface.BOLD);
            clin.addView(tv);tv.setTextColor(md.white); tv.setBackgroundColor(md.appblue);

            return v;
        }
    }








    LinearLayout cuslin;  Dialog cusdialog;
    public void popupwindow(Marker marker){

        String mid = String.valueOf(marker.getTag());
        if (mapstring.indexOf("¬") > -1){
            vees = md.makearrayfromstring(mapstring, "¬");
            cols = md.makearrayfromstring(vees.get(1), "~");

            if (mode.equals("Executors")){
                for (int i = 2; i < vees.size(); i++){
                    ArrayList<String> row = md.makearrayfromstring(vees.get(i), "~");
                    String vid =  md.before(vees.get(i), "~");
                    String precol = "User";

                    String uid =  md.coloption(cols, "ValueUser", row).Value;
                    if (mid.equals(uid)){

                        cusdialog = new Dialog(SetMap.this);
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



                        String name =  md.coloption(cols, precol +"Name", row).Value + " " +
                                md.coloption(cols, precol +"Surname", row).Value;

                        String loc =  md.coloption(cols, precol +"Location", row).Value;
                        String loctime =  md.coloption(cols, precol +"LocationTime", row).Value;
                        String exec =  md.coloption(cols, precol +"ValueExecutor", row).Value;


                        String cell =  md.coloption(cols, precol +"Cell", row).Value;
                        String eml =  md.coloption(cols, precol +"Email", row).Value;
                        String pic =  md.coloption(cols, precol +"Pic", row).Value;
                        String rating =  md.coloption(cols, precol +"Rating", row).Value;
                        String profile =  md.coloption(cols, precol +"Profile", row).Value;


                        int blockheight = (int)(height * 0.25);
                        LinearLayout   reprel = new LinearLayout(SetMap.this);
                        reprel.setLayoutParams(new LinearLayout.LayoutParams(cuswid, blockheight));
                        cuslin.addView(reprel);
                        reprel.setBackgroundResource(R.drawable.whitebutton);
                        reprel.setPadding(5, 10, 5, 10);
                        final int topleftt = 70325 + i + 400, topmidtv = 80325 + i + 43;

                        LinearLayout pln = new LinearLayout(SetMap.this); pln.setId(topleftt);
                        LinearLayout.LayoutParams tr = new LinearLayout.LayoutParams((int) (width), blockheight);
                        pln.setLayoutParams(tr); pln.setOrientation(LinearLayout.HORIZONTAL);  reprel.addView(pln);

                        int ll = (int) ( cuswid* 0.3), rr = (int)((cuswid - ll)), rowheight =   (int)(blockheight * 0.15);

                        ImageView imgView = md.image(SetMap.this, topmidtv, DisplayString);
                        imgView.setLayoutParams(new ViewGroup.LayoutParams(ll, (int)(blockheight)));
                        pln.addView(imgView);

                        String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ pic;
                        imgView = md.loadimage(getResources() ,imgView, url,ll, (int)(blockheight) );

                        LinearLayout rln = new LinearLayout(SetMap.this); rln.setId(topleftt);
                        rln.setPadding(3, 6, 3, 2);
                        rln.setLayoutParams(new LinearLayout.LayoutParams((int) rr, blockheight));
                        rln.setOrientation(LinearLayout.VERTICAL);  pln.addView(rln);

                        TextView tv = new TextView(SetMap.this);
                        tv.setText(name); tv.setTypeface(null, Typeface.BOLD);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(rr, (int)(rowheight)));
                        rln.addView(tv); tv.setTextSize(md.fontsmall);tv.setBackgroundColor(md.transparent);


                        tv = new TextView(SetMap.this);
                        tv.setText(cell + System.lineSeparator() + eml + System.lineSeparator() +
                                "Average Rating: " + rating + "/5"+ System.lineSeparator() +System.lineSeparator() +profile);// tv.setTypeface(null, Typeface.BOLD);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(rr, (int)(blockheight - rowheight)));
                        rln.addView(tv); tv.setTextSize(md.fontsmall);tv.setBackgroundColor(md.transparent);

                        Button tb = new Button(SetMap.this);
                      tb = md.makebutton(SetMap.this, "Select " + md.appsetting("ExecutorName", DisplayString),
                              "", 0,
                                "Go", DisplayString, md.fontmedium, cuswid, cuslineht);
                        cuslin.addView(tb);
                        tb.setOnClickListener(new View.OnClickListener()
                        {  @Override   public void onClick(View view) {  clickexecutor(mid, name);   }   });



                    }
                }

            }
        }
    }






    public void clickexecutor(String userid, String name){

        if (cusdialog != null){cusdialog.cancel();}

        ArrayList<String> vls = md.makearrayfromstring(values, "~");
        String xcitem = md.getstring(vls, 0), xcrowid = md.getstring(vls, 1), xccontrolid =
                md.getstring(vls, 2);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{"Executors", userid, xcitem, xcrowid, xccontrolid, name});
        Intent intent = new Intent();
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
        finish();
    }



}
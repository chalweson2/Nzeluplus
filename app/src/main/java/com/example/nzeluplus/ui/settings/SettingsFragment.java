package com.example.nzeluplus.ui.settings;



import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nzeluplus.Activities;
import com.example.nzeluplus.MainActivity;
import com.example.nzeluplus.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nzeluplus.MyModel;
import com.example.nzeluplus.Register;
import com.example.nzeluplus.Startup;
import com.example.nzeluplus.Transact;
import com.example.nzeluplus.databinding.FragmentSettingsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class SettingsFragment extends Fragment {

    private SettingsViewModel slideshowViewModel;
    private FragmentSettingsBinding binding;

    int height = MainActivity.height, width = MainActivity.width, pbbarid = 82492411, savebtnid = 4254326;
    String  android_id = MainActivity.android_id, uid = MainActivity.uref;
    MyModel md = new MyModel();  LinearLayout mainlin; Context cnt;
    Calendar c = Calendar.getInstance();
    int rpjavayear = c.get(Calendar.YEAR),  rpjavamonth = c.get(Calendar.MONTH), rpjavaday = c.get(Calendar.DAY_OF_MONTH);

    int bigpad = 10;
    String ds = MainActivity.DisplayString;
    SQLiteDatabase mydatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context cnt = getActivity();
        mainlin = binding.mainlin;

        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        mainlin.setPadding(10, 0, 10, 0);
        width = width -(bigpad*2);
        mainlin.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        mainlin = md. setbackground(mainlin);

        android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        mydatabase = getActivity().openOrCreateDatabase(md.DbName,getActivity().MODE_PRIVATE,null);

        startoff();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




    public void startoff(){

        mainlin.removeAllViews();
        ProgressBar pb = new ProgressBar(getActivity());
        mainlin.addView(pb);
        getabout(new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showabout();
            }
        }, 0);

    }


    String btstring = ""; boolean btsuccess = false;
    public void getabout( final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            RequestQueue queue = Volley.newRequestQueue(getActivity());

            String url = md.url + "gettable";

            url = url + "?code="+md.auth(getActivity(),mydatabase, android_id, uid)+"~Sites";

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {  btstring = response; btsuccess = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (attempt < md.attempts){ getabout(   callBack,  attempt); }
                    else{ btsuccess =false; md.toast(getActivity(),  "Error: Please Try Again"); }
                }
            });
            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }

    public void showabout(){

        // Display the first 500 characters of the response string.
        if (!btsuccess){
            String z = "Error Connecting To Servers, Please Check Your Internet Connection & Reload Screen";
            md.toast(getActivity(), z);
        }
        else if(btsuccess & mainlin != null) {
            mainlin.removeAllViews();

            ArrayList<String> list  =  md.makearrayfromstring(btstring, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");

            if(list.size() > 2){
                int c = 2;
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                String idd =  md.before(md.getstring(list, c), "~");
                String addr =  md.coloption(cols, "PhysicalAddress", row).Value;
                String email =  md.coloption(cols, "Email", row).Value;
                String phone =  md.coloption(cols, "ContactNumbers", row).Value;

                View vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 10));
                mainlin.addView(vw);

                int lnh = (int) (0.08 * height), wd = width - (bigpad * 2), iw = (int) (0.05 * height),
                        fw = (int)(wd * 0.2), sw = wd - fw - iw, lpd = 4;

                String btt = "Chat With Us";


                LinearLayout ln = new LinearLayout(getActivity());
                ln.setLayoutParams(new LinearLayout.LayoutParams(wd, lnh));
                ln.setOrientation(LinearLayout.HORIZONTAL); mainlin.addView(ln); ln.setPadding(lpd, 0, 0, 0);
                ImageView im = new ImageView(getActivity()); im.setLayoutParams(new LinearLayout.LayoutParams( iw,  iw));
                im.setBackgroundResource(R.drawable.ic_baseline_home_24); ln.addView(im);
                MaterialTextView  mt =  md.maketextview(getActivity(), "Address: ", "", 0, "Info", ds, md.fontmedium,
                        fw, lnh, true); mt.setTypeface(null, Typeface.BOLD); ln.addView(mt);
                mt =  md.maketextview(getActivity(), addr, "", 0, "Info", ds, md.fontmedium,
                        sw, lnh, false); ln.addView(mt);


                ln = new LinearLayout(getActivity()); ln.setLayoutParams(new LinearLayout.LayoutParams(wd, lnh));
                ln.setOrientation(LinearLayout.HORIZONTAL); mainlin.addView(ln);ln.setPadding(lpd, 0, 0, 0);
                im = new ImageView(getActivity()); im.setLayoutParams(new LinearLayout.LayoutParams( iw,  iw));
                im.setBackgroundResource(R.drawable.ic_baseline_mail_outline_24); ln.addView(im);
                mt =  md.maketextview(getActivity(), "Email: ", "", 0, "Info", ds, md.fontmedium,
                        fw, lnh, true); mt.setTypeface(null, Typeface.BOLD); ln.addView(mt);
                mt =  md.maketextview(getActivity(), email, "", 0, "Info", ds, md.fontmedium,
                        sw, lnh, false); ln.addView(mt);


                ln = new LinearLayout(getActivity()); ln.setLayoutParams(new LinearLayout.LayoutParams(wd, lnh));
                ln.setOrientation(LinearLayout.HORIZONTAL); mainlin.addView(ln); ln.setPadding(lpd, 0, 0, 0);
                im = new ImageView(getActivity()); im.setLayoutParams(new LinearLayout.LayoutParams( iw,  iw));
                im.setBackgroundResource(R.drawable.ic_baseline_local_phone_24); ln.addView(im);
                mt =  md.maketextview(getActivity(), "Phone: ", "", 0, "Info", ds, md.fontmedium,
                        fw, lnh, true); mt.setTypeface(null, Typeface.BOLD); ln.addView(mt);
                mt =  md.maketextview(getActivity(), phone, "", 0, "Info", ds, md.fontmedium,
                        sw, lnh, false); ln.addView(mt);

                vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 10));
                mainlin.addView(vw);

                MaterialButton btminus = new MaterialButton(getActivity());
                btminus.setText(btt); mainlin.addView(btminus);
                btminus.setLayoutParams(new LinearLayout.LayoutParams(width - (bigpad * 2) , (int)(height * 0.08)));
                btminus = md.buttondesign(getActivity(), btminus, "General", ds);

                btminus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        chat();
                    }
                });
                ProgressBar pb = new ProgressBar(getActivity()); pb.setId(pbbarid); mainlin.addView(pb);
                pb.setVisibility(View.GONE);
            }
        }
    }










    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    public void chat(){
        Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{ "Chat",  MainActivity.Executor, ds, "", "", "", ""});
        intent.putExtras(b);
        startActivity(intent);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bundle b = data.getExtras();
                String[] received = b.getStringArray("key");

                if (received[0].equals("Address")){
                    md.toast(getActivity(), received[1]);
                }

            }
        }
    }





































    String pinstring = ""; boolean pinsuccess = false;
    public void changepin(String oldpin, String newpin, final MainActivity.VolleyCallBack callBack, int attempts) {
        try{
            pinsuccess = false;
            RequestQueue queue = Volley.newRequestQueue(getActivity());

            String url = "https://www.valeronpro.com/App/changepin";

            url = url + "?code=" + android_id+"~"+uid+"~"+oldpin+"~"+newpin;

            final int attempt = attempts + 1;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {   pinstring = response;  pinsuccess  = true; callBack.onSuccess();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pinsuccess =false; md.toast(getActivity(),  "Error: Please Try Again");
                }
            });
            stringRequest = md.requestwait(stringRequest);queue.add(stringRequest);
        }
        catch(NullPointerException npx){  }
    }



}
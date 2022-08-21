package com.example.nzeluplus.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nzeluplus.Activities;
import com.example.nzeluplus.MainActivity;
import com.example.nzeluplus.MyModel;
import com.example.nzeluplus.R;
import com.example.nzeluplus.Transact;
import com.example.nzeluplus.databinding.FragmentSettingsBinding;
import com.example.nzeluplus.ui.settings.SettingsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Categories extends Fragment {

    private CategoriesViewModel slideshowViewModel;
    private FragmentSettingsBinding binding;

    int height = MainActivity.height, width = MainActivity.width, pbbarid = 82492411, linid = 4254326;
    String  android_id = MainActivity.android_id, uid = MainActivity.uref;
    MyModel md = new MyModel();  LinearLayout mainlin, lin; Context cnt;
    boolean rpviewingreport = false, smallscreen =  false; int fontsmall = md.fontsmall;
    ProgressBar pbbar;
    Calendar c = Calendar.getInstance();
    ArrayList<MyModel.option> CategoryList = new ArrayList<>();
    String DS = MainActivity.DisplayString;

    public static Categories newInstance() {
        return new Categories();
    }


    SQLiteDatabase mydatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        slideshowViewModel =
                new ViewModelProvider(this).get(CategoriesViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context cnt = getActivity();
        mainlin = binding.mainlin;
        mainlin.setBackgroundResource(R.drawable.backgroundxml);
        mainlin.setPadding(10, 0, 10, 0);
        width = width - 20;
        mainlin.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        mainlin = md. setbackground(mainlin);

        if (height <= md.heightcut){height = (int)(height * 0.91); smallscreen = true; fontsmall -= 1;}
        else{fontsmall = md.fontmedium;}


        android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);


        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(
                md.appsetting("CategorySearchName", MainActivity.DisplayString)
        );

        mydatabase = getActivity().openOrCreateDatabase(md.DbName,getActivity().MODE_PRIVATE,null);

        startoff();
        return root;
    }


    public RelativeLayout rellayout(boolean add, int id, int wid, int ht){

        RelativeLayout rel = new RelativeLayout(getActivity());
        rel.setLayoutParams(new LinearLayout.LayoutParams(wid, ht));
        if (add){mainlin.addView(rel);}
        return rel;
    }


    public void startoff(){

        MaterialTextView tv = md.maketextview(getActivity(), "Loading " + md.appsetting("CategorySearchName", DS),
                "", 0, "Info", DS, md.fontmedium, width, (int)(height * 0.1), true);
        mainlin.addView(tv);
        pbbar = new ProgressBar(getActivity()); pbbar.setId(pbbarid); mainlin.addView(pbbar);

        getcategories( new MainActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                showcategories();
            }
        }, 0, getActivity());


    }

    String tbresult = ""; boolean tbsuccess = false;
    public void getcategories( final MainActivity.VolleyCallBack callBack, int attempts, Context cnt) {

        RequestQueue queue = Volley.newRequestQueue(cnt);
        String url = md.url + "gettable";

        final int attempt = attempts + 1;
        String pms = "?code="+md.auth(getActivity(),mydatabase, android_id, uid)+"~Categories";

        url = url + pms;

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
                    getcategories(  callBack,  attempt, cnt);
                }else{
                    tbresult =  "Error: " + error.getMessage();
                }
            }
        });

        queue.add(stringRequest);

    }

    int rowheight = (int) (height * 0.13);
    public void showcategories(){

        boolean usedd = false;
        if (tbsuccess & mainlin != null ){

            ArrayList<String> list  =  md.makearrayfromstring(tbresult, "¬");
            String rulestring = md.getstring(list, 0);
            ArrayList<String> rules = md.makearrayfromstring(rulestring, "#");
            ArrayList<String> cols = md.makearrayfromstring(md.getstring(list, 1), "~");


            for (int c = 2; c < list.size(); c++){
                ArrayList<String> row = md.makearrayfromstring(md.getstring(list, c), "~");
                String idd =  md.before(md.getstring(list, c), "~");
                String name =  md.coloption(cols, "Name", row).Value;
                String pic1 =  md.coloption(cols, "Pic1", row).Value;

                long subcatof = -1; String subs = "";
                subcatof = md.parselong(md.coloption(cols, "ValueSubCategoryOf", row).Value);
                if (subcatof > 0){subs = String.valueOf(subcatof);  usedd =true;}

                CategoryList.add(new MyModel.option(idd, name, subs, pic1));
            }
        }
        else  {
            md.toast(getActivity(), " Error, please try again");
        }




        if (mainlin != null){
            mainlin.removeAllViews();

            int remht = height, lineheight = (int) (height * 0.1);

            RelativeLayout rel = rellayout(true, 0, width, (int) (height /10));

            if (usedd){
                ArrayList<String> CategoryIDs = new ArrayList<>(), CategoryNames = new ArrayList<>();
                for (int i = 0; i <  CategoryList.size(); i++){
                    if (md.getoption(CategoryList, i).Option2.equals("")){
                        CategoryIDs.add(md.getoption(CategoryList, i).ID); CategoryNames.add(md.getoption(CategoryList, i).Option1);
                    }
                }

                remht -= lineheight;
                Spinner spinner = new Spinner(getActivity());
                spinner.getBackground().setColorFilter(md.black, PorterDuff.Mode.SRC_ATOP);
                spinner.setLayoutParams(new LinearLayout.LayoutParams(width, lineheight ));
                rel.addView(spinner);

                // Spinner click listener
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //((TextView) adapterView.getChildAt(0)).setTextColor(md.white);
                    getsubcategories(md.getstring(CategoryIDs, i));   }
                    @Override    public void onNothingSelected(AdapterView<?> adapterView) { }  });


                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter =  new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,  CategoryNames );
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  spinner.setAdapter(dataAdapter);

            }


            NestedScrollView ns  = new NestedScrollView(getActivity());
            ns.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(remht * 0.99)));
            mainlin.addView(ns);
            lin = new LinearLayout(getActivity()); lin.setOrientation(LinearLayout.VERTICAL); lin.setId(linid);
            lin.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(remht * 0.99)));
            lin.setPadding(linpad, 4, linpad, rowheight);
            //lin.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ns.addView(lin);

            if (!usedd){ getsubcategories(""); }

        }
    }
    int linpad = 20;

    public void getsubcategories(String category){

        LinearLayout lin = getActivity().findViewById(linid); lin.removeAllViews();
        int gapht = 12, gapsum = rowheight, vwid = lin.getLayoutParams().width  - (linpad*2);

        for (int i = 0; i <  CategoryList.size(); i++){
            if (md.getoption(CategoryList, i).Option2.equals(category) & lin != null){

                gapsum += gapht;
                int nameid = 1111+i,   imgid = 1501+i, smallpad = 4,  namewidth = (int)(vwid- (smallpad * 2)) - 6;

                View vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, (gapht-2)));
                lin.addView(vw);
                vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2));
                vw.setBackgroundColor(md.lightgray); lin.addView(vw);

                LinearLayout ln1 = new LinearLayout(getActivity()); ln1.setOrientation(LinearLayout.HORIZONTAL);
                ln1.setLayoutParams(new LinearLayout.LayoutParams(vwid, rowheight));
                // ln1.setBackgroundResource(R.drawable.whitebutton);
                ln1.setPadding(smallpad, 2, smallpad, 2); lin.addView(ln1);
                if (!md.getoption(CategoryList ,i).Option3.equals("")){
                    namewidth -= rowheight;

                    ImageView imgView = md.image(getActivity(), imgid, DS);
                    imgView.setLayoutParams(new ViewGroup.LayoutParams(rowheight, (int)(rowheight*0.9)));
                    ln1.addView(imgView);
                    String url = "https://www.valeronpro.com/Content/SiteImages/" +md.Site+"/"+ md.getoption(CategoryList, i).Option3;
                    imgView = md.loadimage(getActivity().getResources(), imgView, url ,rowheight, (int)(rowheight*0.9));
                }

                MaterialTextView nb = md.maketextview(getActivity(),  md.getoption(CategoryList, i).Option1,"", nameid, "Info",
                        MainActivity.DisplayString, fontsmall, namewidth, rowheight, true);ln1.addView(nb);

                final String rowid = md.getoption(CategoryList, i).ID;
                nb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle b = new Bundle();
                        b.putStringArray("key", new String[]{ "ItemLists",  DS, MainActivity.Executor
                                , "Items", "Category", rowid, ""});
                        Intent intent = new Intent(getActivity().getApplicationContext(), Activities.class);
                        intent.putExtras(b);
                        startActivity(intent);

                    }
                });
            }
        }

        View vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, (gapht-2)));
        lin.addView(vw);
        vw = new View(getActivity()); vw.setLayoutParams(new LinearLayout.LayoutParams(width, 2));
        vw.setBackgroundColor(md.lightgray); lin.addView(vw);

        lin.setPadding(linpad, 4, linpad, gapsum);
    }


}
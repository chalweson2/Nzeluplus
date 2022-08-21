package com.example.nzeluplus;


import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nzeluplus.databinding.ActivityMainBinding;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nzeluplus.ui.home.HomeFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.nzeluplus.databinding.ActivityMainBinding;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;LinearLayout lin;

    public static  String uref, utype = "", android_id,companyname = "", android_ref = "", DisplayString = "", Executor = "",
            POS = "", articlepage = "0", exercisepage = "0", CompanyLocation ="";
    public static int height, width;
    public static final String EXTRA_MESSAGE = "com.example.android.twoactivities.extra.MESSAGE";
    public static MyModel md = new MyModel();
    NavigationView navigationView;


    public static String searchname = "Search", floaticon = "1",  helpname = "", AppName = "", whatsappnumber = "",
            shownumber ="0",  categoryname = "", itemname = "", itemnameplural = "", ordername = "", historyname = "",
            currencysymbol = "", categorysearchname = "", brandsearchname = "", categorypage ="0", brandpage ="0", currencycode = "";

    int menucolor = -1;
    public static boolean  popuphelp = false;

    SQLiteDatabase mydatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String[] rec = b.getStringArray("key");

        DisplayString =  md.getarray(rec, 0);
        uref = md.getarray(rec, 1);
        Executor = md.getarray(rec, 2);
        POS = md.getarray(rec, 3);
        CompanyLocation  = md.getarray(rec, 4);
        currencysymbol= md.getarray(rec, 5);
        currencycode = md.getarray(rec, 6);
        companyname = md.getarray(rec, 7);
        popuphelp = md.getarray(rec, 8).equals("1");

        ArrayList<String> disarray = md.makearrayfromstring(DisplayString, "~");

        for (int i = 3; i < disarray.size(); i++){
            String cl = md.before(disarray.get(i), "^");
            String vl = md.breakurl(disarray.get(i), 1, "^");

            if (cl.equals("ShowItemNumber")){
                if (vl.toLowerCase().equals("true")){     shownumber ="1";}
            }else  if (cl.equals("CategorySearchOption")){
                if (vl.toLowerCase().equals("true")){     categorypage ="1";

                if (Executor.equals("2") & md.appsetting("ShowExecutorCategories", DisplayString).toLowerCase().equals("false")){
                    categorypage = "0";
                }

                }
            }else  if (cl.equals("BrandSearchOption")){
                if (vl.toLowerCase().equals("true")){     brandpage ="1";}

                if (Executor.equals("2") & md.appsetting("ShowExecutorBrands", DisplayString).toLowerCase().equals("false")){
                    brandpage  = "0";
                }
            }else  if (cl.equals("ArticlesSearchOption")){
                if (vl.toLowerCase().equals("true")){     articlepage ="1";}
            }else  if (cl.equals("ExercisesSearchOption")){
                if (vl.toLowerCase().equals("true")){     exercisepage ="1";}
            }
        }

        whatsappnumber = md.appsetting("WhatsappNumber", DisplayString);
        floaticon = md.appsetting("ValueFloatButtonIcon", DisplayString);
        itemname = md.appsetting("ItemNameSingular", DisplayString);
        itemnameplural =md.appsetting("ItemNamePlural", DisplayString);
        categoryname =md.appsetting("CategoryName", DisplayString);
        categorysearchname =md.appsetting("CategorySearchName", DisplayString);
        brandsearchname =md.appsetting("BrandSearchName", DisplayString);
        ordername =md.appsetting("OrderName", DisplayString);
        helpname =md.appsetting("HelpSettingsPageName", DisplayString);
        AppName =md.appsetting("AppName", DisplayString);



        menucolor = md. parsecolor( md.appsetting("MenuBackgroundColor", DisplayString));

        DisplayMetrics metrics = new DisplayMetrics();
        (this).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height =  metrics.heightPixels;

        Resources resources = MainActivity.this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = MainActivity.height - (int)(2.5*resources.getDimensionPixelSize(resourceId));  }
        width = metrics.widthPixels;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        if (floaticon.equals("2")){
            binding.appBarMain.fab.setImageResource(R.drawable.ic_baseline_add_circle_24);
        }
        else if (floaticon.equals("3")){
            binding.appBarMain.fab.setImageResource(R.drawable.ic_baseline_check_circle_24);
        }
        else if (floaticon.equals("4")){
            binding.appBarMain.fab.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
        }
        else  {
            binding.appBarMain.fab.setVisibility(View.GONE);
        }

        Resources res = getResources();
        int id = res.getIdentifier("appmaintitle", "id", getPackageName());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        TextView avatarContainer = (TextView ) v.findViewById(id);

        //avatarContainer.setText(AppName);

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean gotrans = true;
                if (Executor.equals("2") &  md.appsetting("AllowExecutorBooking", DisplayString).toLowerCase()
                        .equals("false")){ gotrans = false;
                }

                if (!gotrans){
                    md.toast(MainActivity.this, "Sorry, A " +
                            md.appsetting("ExecutorName", DisplayString) + " Cannot Place Orders");
                }
                if (gotrans){
                    Bundle b = new Bundle();
                    b.putStringArray("key", new String[]{ MainActivity.uref, Executor, DisplayString, POS, CompanyLocation, currencysymbol,currencycode});
                    Intent intent = new Intent(MainActivity.this, Transact.class);
                    intent.putExtras(b);
                    startActivity(intent);
                }

            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,  R.id.nav_brands,  R.id.nav_categories, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        //R.id.nav_historical,

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        Menu menu = navigationView.getMenu();
        if (navigationView != null) {
            // menu.findItem(R.id.nav_profile).setTitle("My Account");

            int navid = R.id.nav_home;

            if (categorypage.equals("0")){
                menu.findItem(R.id.nav_categories).setVisible(false);
            }else{
                menu.findItem(R.id.nav_categories).setTitle(categorysearchname);
            }

            if (helpname.equals("")  ){
                menu.findItem(R.id.nav_settings).setVisible(false);
            }else{
                menu.findItem(R.id.nav_settings).setTitle(helpname);
            }

            if (brandpage.equals("0")){
                menu.findItem(R.id.nav_brands).setVisible(false);
            }else{
                menu.findItem(R.id.nav_brands).setTitle(brandsearchname);
            }

            if (articlepage.equals("0")  ){
                menu.findItem(R.id.nav_articles).setVisible(false);

            }else{
                menu.findItem(R.id.nav_articles).setTitle(md.appsetting("ArticlesSearchName", DisplayString));

                menu.findItem(R.id.nav_articles).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        drawer.closeDrawer(GravityCompat.START);
                        gomenuitem("Articles", "");
                        return false;
                    }
                });
            }

            menu.findItem(R.id.nav_topics).setVisible(false);


            menu.findItem(R.id.nav_chat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    drawer.closeDrawer(GravityCompat.START);
                    String url = "https://wa.me/" + whatsappnumber;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return false;
                }
            });


            menu.findItem(R.id.nav_share).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    drawer.closeDrawer(GravityCompat.START);
                    md.share(MainActivity.this, DisplayString);
                    return false;
                }
            });

            if (exercisepage.equals("0")  ){
                menu.findItem(R.id.nav_exercises).setVisible(false);
            }else{
                menu.findItem(R.id.nav_exercises).setTitle(md.appsetting("ExercisesSearchName", DisplayString));
                menu.findItem(R.id.nav_exercises).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        drawer.closeDrawer(GravityCompat.START);
                        gomenuitem("Exercises", "");
                        return false;
                    }
                });
            }

           //menu.findItem(R.id.nav_orders).setTitle(md.appsetting("ExercisesSearchName", DisplayString));
            menu.findItem(R.id.nav_orders).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    drawer.closeDrawer(GravityCompat.START);

                    Intent intent = new Intent(getApplicationContext(), Activities.class);
                    Bundle b = new Bundle();
                    b.putStringArray("key", new String[]{ "History", DisplayString, Executor, "CustomerOrders", "", "", ""});
                    intent.putExtras(b);
                    startActivity(intent);

                    return false;
                }
            });



            menu.findItem(R.id.nav_historical).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    drawer.closeDrawer(GravityCompat.START);

                    Intent intent = new Intent(getApplicationContext(), MyAccount.class);
                    Bundle b = new Bundle();
                    b.putStringArray("key", new String[]{ "", DisplayString, Executor, "", "", "", ""});
                    intent.putExtras(b);
                    startActivity(intent);

                    return false;
                }
            });


            navController.navigate(navid);

        }



/*

        // Create periodic task requests that are executed every 30 minutes
        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(NotiWorker.class, 16, TimeUnit.MINUTES)
                .build();
        // Add a task to the queue, the task will execute if the condition is met
        WorkManager.getInstance().enqueue(request);
*/

    }




    public void gomenuitem(String mode, String extra){
        Bundle b = new Bundle();
        b.putStringArray("key", new String[]{  DisplayString,uref,  Executor, extra, "", "",  "",  ""});
        Intent intent = new Intent(this, Articles.class);

        if(mode.equals("Exercises")){intent = new Intent(this, Exercises.class);}
        intent.putExtras(b);
        startActivity(intent);





    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public interface VolleyCallBack {
        void onSuccess();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_BACK ) {
            //keyCode == KeyEvent.KEYCODE_MENU ||           keyCode == KeyEvent.KEYCODE_TAB ||

            // return 'true' to prevent further propagation of the key event
            return true;
        }

        // let the system handle all other key events
        return super.onKeyDown(keyCode, event);
    }


    public void valhelp(){

        LinearLayout cuslin;  Dialog cusdialog;
        cusdialog = new Dialog(MainActivity.this);
        cusdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cusdialog.setContentView(R.layout.custom);
        cusdialog.setCancelable(true);
        cusdialog.setCanceledOnTouchOutside(true);
        Window window = cusdialog.getWindow();
        int ww = (int)(width*0.95);
        window.setLayout(ww, AbsListView.LayoutParams.WRAP_CONTENT);

        cuslin = (LinearLayout) cusdialog.findViewById(R.id.customlinear);
        cuslin.setOrientation(LinearLayout.VERTICAL);
        cuslin.setLayoutParams(new FrameLayout.LayoutParams(ww,
                ViewGroup.LayoutParams.MATCH_PARENT));
        int cushit = (int)(height*0.55),  cuswid = (int)(width*0.94);

        cusdialog.show();
        cusdialog.setTitle(companyname + " (Help)");
        cuslin.setPadding(6, 0, 0, 0);
        int cuslineht = (int)(cushit * 0.12);

        MaterialTextView nb = md.maketextview(MainActivity.this,  companyname + " (Help)","", 0, "Info",
                MainActivity.DisplayString, md.fontsmall, cuswid, cuslineht, true); cuslin.addView(nb);

        MaterialButton bt  = md.makebutton(MainActivity.this, "Chat With Us", "", 0,
                "Info", DisplayString, md.fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {
                String url = "https://wa.me/message/GOF5TLWHJXGJP1";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);  }});

        bt  = md.makebutton(MainActivity.this, "Watch Youtube How To Videos", "", 0,
                "Info",  DisplayString, md.fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {

                String url = "https://www.youtube.com/channel/UC_s4AJjGPoz98-i1BFQiOLg";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);   }});

        bt  = md.makebutton(MainActivity.this, "Check For Updates", "", 0,
                "Info",  DisplayString, md.fontsmall, cuswid, cuslineht); cuslin.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override  public void onClick(View view) {

                String url = "https://play.google.com/store/apps/details?id=com.valeronandroid.valeronandroid";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);   }});





    }


    @Override
    protected Dialog onCreateDialog(int id) {

        // TODO Auto-generated method stub
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);

        if (id == 222){
            ArrayList<String> fkviewlist = new ArrayList<>(), modes = new ArrayList<>();
            LinearLayout ll = new LinearLayout(MainActivity.this);
            ll.setOrientation(LinearLayout.VERTICAL);
            final ListView lv = new ListView(MainActivity.this);
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_list_item_1, fkviewlist);
            lv.setAdapter(itemsAdapter);
            ll.addView(lv);
            builder.setView(ll);

            AlertDialog dialog = builder.create();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,  long id) {
                    try{


                    }catch(Exception c){
                        md.toast(MainActivity.this, "ERROR: " + c.getMessage());
                    }

                }
            });

            /*Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
*/

            return  dialog;

        }
        return null;
    }



}
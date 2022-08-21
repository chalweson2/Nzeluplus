package com.example.nzeluplus;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.Comparator;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.ParseException;



public class MyModel {
    /*Setup Area*/
    public String Site = "www.nzelupluszm.com";
    public String AppName = "Nzelu+";
    public String DbName = "nzelupluszm";
    public String BackgroundColor = "#7abaff";
    public String demoexpirydate = "2022-02-11 00:00";
    public String V = "1";
    public String Controller = "Valeron";
    public  String url = "https://www.valeronpro.com/Valeron/";
    public String EncryptVersion = "2";
    public boolean Crypting = false;
    public boolean thisisademo = false;

    public String[] NoChars = {"~", "¬", "|", "^", "<", ">", "*", ";", "\"", "'"};
    private final String characterEncoding = "UTF-8";
    private final String cipherTransformation = "AES/CBC/PKCS7Padding";
    private final String aesEncryptionAlgorithm = "AES";

    public int heightcut = 1200;

    public option valeronuser(   SQLiteDatabase mydatabase ){
        String tst ="";
        ArrayList<MyModel.option> recs = new ArrayList<>();
        Cursor rs =  mydatabase.rawQuery( "select * from VALERONSITES where LOGGEDIN = 1", null );
        while ( rs.moveToNext()) {
            recs.add(new MyModel.option("",
                    rs.getString(rs.getColumnIndex("USERREFERENCE")),
                    rs.getString(rs.getColumnIndex("SITEREFERENCE")),
                    String.valueOf(rs.getInt(rs.getColumnIndex("LOGGEDIN")))));

            tst = rs.getString(rs.getColumnIndex("USERREFERENCE")) + System.lineSeparator() +
                    rs.getString(rs.getColumnIndex("NAME")) + System.lineSeparator() +
                    rs.getString(rs.getColumnIndex("SITEREFERENCE")) + System.lineSeparator() +
                    rs.getString(rs.getColumnIndex("SUPERACCESS")) + System.lineSeparator() +
                    rs.getString(rs.getColumnIndex("DBO")) + System.lineSeparator() +
                    rs.getString(rs.getColumnIndex("LOGGEDIN")) + System.lineSeparator();
        }

        option oi = new option("", "", "", "");
        if (recs.size() > 0){
            oi.Option1 = getoption(recs, 0).Option1;
            oi.Option2 = getoption(recs, 0).Option2;
        }
        oi.Option4  = tst;
        return oi;
    }

    public String auth(Context cnt, SQLiteDatabase mydatabase ,  String deviceref, String userref){

        /* return   V  +"~" + Site + "~" + deviceref + "~" +  userref;*/

        String site = Site  ;
        if (DbName.equals("valeronpro")){
            option oi = valeronuser(mydatabase); site = oi.Option2;
        }



        return  encrypt( cnt, V, true, true) +"~" +
                encrypt(cnt, site, true, true)+ "~" +
                deviceref +"~" +  userref;
    }



    public void share(Context cnt, String displaystring){
        String sharemessage = appsetting("AndroidShareMessage", displaystring);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, AppName);
        String shareMessage= "\n"+sharemessage+"\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        cnt.startActivity(Intent.createChooser(shareIntent, "choose one"));

    }


    public ImageView imagename(Context cnt, String name, int id, int width, int height){
//getActivity(), category,imgid, rowheight , (int)(rowheight *0.9)
        ImageView imgView = new ImageView(cnt);
        imgView.setId(id);
        imgView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        ArrayList<option> c = Images();
        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               // sound(cnt, "");
                return false;
            }
        });

        name = name.toLowerCase();
        name = name.replaceAll("\\s+","");
        for (int i = 0; i < c.size(); i++){
            String cc = c.get(i).ID.toLowerCase();
            cc = cc.replaceAll("\\s+","");
            if (cc.equals(name)){
                imgView.setImageResource(c.get(i).ValueInt);
            }
        }

        return imgView;
    }

    public ArrayList<option> Images()
    {
        ArrayList<option> b = Brands();
        ArrayList<option> c = Categories();

        for (int i = 0; i < c.size(); i++){
            b.add(c.get(i));
        }

        return b;
    }

    public ArrayList<option> Brands()
    {
        ArrayList<option> c = new ArrayList<>();

        c.add(new option(R.drawable.english, "English", "3"));
        c.add(new option(R.drawable.maths, "Maths", "5"));
        c.add(new option(R.drawable.science, "Science", "6"));

       /* c.add(new option(R.drawable.grade8, "Grade 8", "8"));
        c.add(new option(R.drawable.grade9, "Grade 9", "7"));
        c.add(new option(R.drawable.grade10, "Grade 10", "6"));
        c.add(new option(R.drawable.grade11, "Grade 11", "5"));
        c.add(new option(R.drawable.grade12, "Grade 12", "4"));

        c.add(new option(R.drawable.grade9gce, "Grade 9 GCE", ""));
        c.add(new option(R.drawable.grade12gce, "Grade 12 GCE", "2"));

        c.add(new option(R.drawable.igcse, "IGCSE", ""));
        c.add(new option(R.drawable.alevel, "A Level", "18"));
        c.add(new option(R.drawable.aslevel, "AS Level", "17"));*/
        return c;
    }
    public ArrayList<option> Categories()
    {
        ArrayList<option> c = new ArrayList<>();

      //  c.add(new option(R.drawable.reception, "Reception", "19"));
        c.add(new option(R.drawable.grade1, "Grade 1", "1"));
        c.add(new option(R.drawable.grade2, "Grade 2", "2"));
        c.add(new option(R.drawable.grade3, "Grade 3", "3"));
        c.add(new option(R.drawable.grade4, "Grade 4", "4"));
        c.add(new option(R.drawable.grade5, "Grade 5", "5"));
        c.add(new option(R.drawable.grade6, "Grade 6", "6"));
        c.add(new option(R.drawable.grade7, "Grade 7", "7"));


        //   c.add(new option(R.drawable.accountingandbusiness, "Accounting And Business", "9"));
        //   c.add(new option(R.drawable.agriculture, "Agriculture", "61"));
        //   c.add(new option(R.drawable.artanddrama, "Art And Drama", "62"));
        //   c.add(new option(R.drawable.biology, "Biology", "59"));
        //   c.add(new option(R.drawable.businessstudies, "Business Studies", "55"));
        //   c.add(new option(R.drawable.chemistry, "Chemistry", "57"));
        //   c.add(new option(R.drawable.civiceducation, "Civic Education", "8"));
        //   c.add(new option(R.drawable.designandtechnology, "Design And Technology", "66"));
        //   c.add(new option(R.drawable.foodandnutrition, "Food And Nutrition", "64"));
        //   c.add(new option(R.drawable.french, "French", "54"));
        //   c.add(new option(R.drawable.geography, "Geography", "2"));
        //   c.add(new option(R.drawable.history, "History", "60"));
        //   c.add(new option(R.drawable.homeeconomics, "Home Economics", "69"));
       // c.add(new option(R.drawable.ict, "ICT", "6"));
        //   c.add(new option(R.drawable.locallanguages, "Local Languages", "68"));
        //   c.add(new option(R.drawable.music, "Music", "63"));
        //   c.add(new option(R.drawable.physicaleducation, "Physical Education", "67"));
        //   c.add(new option(R.drawable.physics, "Physics", "58"));
        //   c.add(new option(R.drawable.religiousstudies, "Religious Studies", "65"));
        //   c.add(new option(R.drawable.socialstudies, "Social Studies", "3"));
        //   c.add(new option(R.drawable.specialpaperone, "Special Paper One", "56"));


        return c;
    }



    public int textcolor(String displaystring){
        return intcolor(appsetting("GeneralButtonTextColor", displaystring));
    }










    public String encrypt(Context cnt, String plainText, boolean crypt, boolean encode){
        String enstring  = plainText;
        if (crypt){
            try{

                if (encode){
                    enstring = URLEncoder.encode(enstring, java.nio.charset.StandardCharsets.UTF_8.toString());
                }
                // throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
                byte [] plainTextbytes = plainText.getBytes(characterEncoding);
                byte [] keyBytes = getKeyBytes(cnt.getString(R.string.valeronkey));
                enstring = Base64.encodeToString(doencrypt(plainTextbytes,keyBytes, keyBytes), Base64.DEFAULT);

            }catch(Exception xx){}
        }
        return enstring;
    }

    public byte[] doencrypt(byte[] plainText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher .ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        plainText = cipher.doFinal(plainText);
        return plainText;
    }


    public  byte [] dodecrypt( byte[] cipherText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher .DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    public String decrypt(Context cnt, String encryptedText,  boolean crypt, boolean decode)
    //throws KeyException, GeneralSecurityException, GeneralSecurityException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException
    {
        String dstring = encryptedText;

        if ( crypt ){
            try{
                byte [] cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT);
                byte[] keyBytes = getKeyBytes(cnt.getString(R.string.valeronkey));
                dstring = new  String (dodecrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);

                if (decode){

                    dstring = URLDecoder.decode(dstring,java.nio.charset.StandardCharsets.UTF_8.toString() );
                }

            }catch (Exception xx){ dstring = encryptedText;}
        }
        return dstring;
    }


    private byte[] getKeyBytes(String key) throws UnsupportedEncodingException{
        byte [] keyBytes= new byte[16];
        byte [] parameterKeyBytes= key.getBytes(characterEncoding);
        System .arraycopy(parameterKeyBytes, 0 , keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }















    public LinearLayout setbackground(LinearLayout mainlin){
        String bcg = appsetting("BackgroundColor", MainActivity.DisplayString);
        if (DbName.equals("valeronpro")){bcg = BackgroundColor;}
        if (!bcg.equals("")){
            //mainlin.setBackgroundColor(parsecolor(bcg));
        }
        // mainlin.setBackgroundResource(R.drawable.backgroundsolid);
        return mainlin;
    }



    public java.util.UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public int countlines(String message, int fontsize, int controlwidth){

        int totalwidth = message.length() * fontsize;

        int rt = (int)(parsefloat(String.valueOf(totalwidth)) / parsefloat(String.valueOf(controlwidth)));
        if (rt <= 0){rt = 1;}
        return rt;

    }


    public MaterialTextView helptextview(Context cnt, String displaystring, int width, int rowheight){
        MaterialTextView  mt = maketextview(cnt, "Need Help? Please Contact Us",
                "", 0, "Info", displaystring, fontmedium, width, rowheight, true);

        String Whatsapp = appsetting("WhatsappNumber", displaystring);

        mt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://wa.me/"+Whatsapp;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));


             cnt.startActivity(i);
            }
        });
        return mt;
    }



    public MaterialTextView setlines(MaterialTextView mt, String txt, int fontsize, int width, int height){

        int lns = countlines(txt, fontsize, width);
        mt.setLines(lns);

        mt.setHeight((lns+1) * height);

        return mt;

    }

    public double parsedouble(String value){
        double sp = 0;
        try{   sp = Double.parseDouble(value);
        }catch (NumberFormatException nf){ }
        return sp;
    }

    public float parsefloat(String value){

        float sp = 0;
        try{   sp = Float.parseFloat(value);
        }catch (Exception nf){ }
        return sp;
    }
    public int parseint(String value){

        int sp = 0;
        try{   sp = Integer.parseInt(value);
        }catch (Exception nf){ }
        return sp;
    }
    public Long parselong(String value){

        Long sp = 0L;
        try{   sp = Long.parseLong(value);
        }catch (Exception nf){ }
        return sp;
    }

    public int parsecolor(String value){
        int mcolor = white;
        try{    if (!value.equals("")){mcolor = Color.parseColor(value);}
        }catch (NumberFormatException nf){ mcolor = white;}
        return  mcolor;
    }

    public Long getdatemilli(String datestring, String timestring){

        String year = before(datestring,  "-");
        String month = breakurl(datestring, 1, "-");
        String day = breakurl(datestring, 2, "-");

        if (month.length() == 1){month = "0" + month;} if (day.length() == 1){day = "0" + day;}

        datestring = year + "-" + month + "-"+day;

        String fstring = datestring; if (timestring != ""){fstring = fstring + " " + timestring;}
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime expdate = LocalDateTime.parse( fstring, formatter);
        Calendar expcal = Calendar.getInstance();
        expcal.setTime(Date.from(expdate.atZone(ZoneId.systemDefault()).toInstant()));
        long expmilli = expcal.getTimeInMillis();
        return expmilli;
    }





    public  Integer colourarray(){
        ArrayList<Integer> i = new ArrayList<>();
        i.add(Color.parseColor("#2b2d42"));
        i.add(Color.parseColor("#8d99ae"));
        i.add(Color.parseColor("#227d2e"));
        i.add(Color.parseColor("#f5c400"));
        i.add(Color.parseColor("#edf2f4"));
        i.add(Color.parseColor("#ef233c"));
        i.add(Color.parseColor("#d90429"));
        i.add(Color.parseColor("#BFBFBF"));
        i.add(Color.parseColor("#62704d"));
        i.add(Color.parseColor("#ff8308"));
        i.add(Color.parseColor("#54c3ff"));
        i.add(Color.parseColor("#245a78"));
        i.add(Color.parseColor("#782443"));
        i.add(Color.parseColor("#ff82b0"));
        i.add(Color.parseColor("#18a141"));
        i.add(Color.parseColor("#34649e"));
        i.add(Color.parseColor("#b7db00"));
        i.add(Color.parseColor("#db2800"));
        i.add(Color.parseColor("#6e2515"));
        i.add(Color.parseColor("#00b04c"));
        i.add(Color.parseColor("#ad9947"));

        Random rand = new Random(); //instance of random class
        int upperbound = i.size();
        int int_random = rand.nextInt(upperbound);

        return i.get(int_random);
    }


    public String stringf(float ff){
        return  String.valueOf(ff);
    }

    public int darkgreen = 0xFF007832,white = 0xFFFFFFFF, fontsmall = 12, fontmedium = 16,
            mediumgray = 0xFF6E6E6E,
            darkgray = 0xFF3D3D3D, brightred = 0xFFFF1C37, yellow = 0xFFFFF41C,
            transparent = Color.TRANSPARENT,
            green = 0xFF65C865, lightblue = 0xFF5D98C9, black = 0xFF000000, appblue = 0xFF052C3C,
            orange = 0xFFFFA826, fontlarge = 20, fontxl = 24, lightgray = 0xFFE8E8E8, skyblue = Color.parseColor("#b8e1e6");

    public double menuheight = 0.15, mainheight = 0.85;


    public class Colour{
        public int Main = -1;
        public int Text = -1;
    }

    public ArrayList<Integer> bluearray(){
        ArrayList<Integer> i = new ArrayList<>();

        i.add(Color.parseColor("#0c2e4d")); i.add(Color.parseColor("#0f3b63")); i.add(Color.parseColor("#144c80")); i.add(Color.parseColor("#1b5f9e")); i.add(Color.parseColor("#2173bf"));
        i.add(Color.parseColor("#2785db")); i.add(Color.parseColor("#359dfc")); i.add(Color.parseColor("#69b5fa")); i.add(Color.parseColor("#6194c2")); i.add(Color.parseColor("#5c87ad"));
        i.add(Color.parseColor("#4f6f8c")); i.add(Color.parseColor("#405e7a")); i.add(Color.parseColor("#2e5478")); i.add(Color.parseColor("#214970")); i.add(Color.parseColor("#11375c"));
        return i;
    }



    public int intcolor(String value){

        if (value.equals("")){
            return Color.parseColor("#000000");
        }else{
            return  Color.parseColor(value);
        }
    }
    public   RandomColor colourarray(int previndex){
        ArrayList<Integer> i = new ArrayList<>();
        i.add(Color.parseColor("#2b2d42"));
        i.add(Color.parseColor("#8d99ae"));
        i.add(Color.parseColor("#227d2e"));
        i.add(Color.parseColor("#f5c400"));
        i.add(Color.parseColor("#edf2f4"));
        i.add(Color.parseColor("#ef233c"));
        i.add(Color.parseColor("#d90429"));
        i.add(Color.parseColor("#BFBFBF"));
        i.add(Color.parseColor("#62704d"));
        i.add(Color.parseColor("#ff8308"));
        i.add(Color.parseColor("#54c3ff"));
        i.add(Color.parseColor("#245a78"));
        i.add(Color.parseColor("#782443"));
        i.add(Color.parseColor("#ff82b0"));
        i.add(Color.parseColor("#18a141"));
        i.add(Color.parseColor("#34649e"));
        i.add(Color.parseColor("#b7db00"));
        i.add(Color.parseColor("#db2800"));
        i.add(Color.parseColor("#6e2515"));
        i.add(Color.parseColor("#00b04c"));
        i.add(Color.parseColor("#ad9947"));

        Random rand = new Random(); //instance of random class
        int upperbound = i.size();
        int int_random = randomint(i.size());

        while (previndex == int_random){
            int_random = randomint(i.size());
        }

        RandomColor rc = new RandomColor(int_random, i.get(int_random));

        return rc;
    }

    public int randomint(int max){
        Random rand = new Random(); //instance of random class
        int upperbound = max;
        return rand.nextInt(upperbound);
    }

    public class RandomColor{
        public int index = -1;
        public int color = -1;
        public RandomColor(int ind, int col){
            index = ind; color = col;
        }
    }



    public Colour getcolour(String Name){

        Colour cl = new Colour();
        int col = lightgray, t = black;

        if ( Name.equals("Queued")) {col = lightgray; t = black; }
        else if ( Name.equals("Executing")) {col = lightblue; t = black; }
        else if   ( Name.equals("Postponed")) {col = orange; t = black; }
        else if ( Name.equals("Cancelled")) {col = brightred; t = white; }
        else if ( Name.equals("Completed")) {col = yellow; t = black; }
        else if ( Name.equals("Signed Off")) {col = green; t = white; }
        else if ( Name.equals("Low")) {col = green; t = black; }
        else if ( Name.equals("Medium")) {col = yellow; t = black; }
        else if ( Name.equals("High")) {col = brightred; t = white; }
        else if ( Name.equals("Not Set")) {col = brightred; t = white; }
        else if   ( Name.equals("Set")) {col = green; t = black; }
        else if ( Name.equals("View")) {col = lightblue; t = black; }
        else if  ( Name.equals("Sent")) {col = green; t = black; }
        else if ( Name.equals("Not Sent")) {col = brightred; t = white; }
        cl.Main = col; cl.Text = t;

        return cl;
    }


    private static final String CREATE1 =
            "CREATE TABLE Settings (ID INTEGER PRIMARY KEY, CellNumber TEXT, Registration TEXT, CompanyCode TEXT, PROMPT TEXT, ERRORMESSAGE TEXT, Device TEXT, Date datetime, Distance TEXT) ";
    private static final String CREATE2 =
            "CREATE TABLE VehicleLog(ID INTEGER PRIMARY KEY, LOCATION TEXT, CHARGING TEXT, SPEED TEXT, SPEEDFROMANDROID TEXT, TIME TEXT, DISTANCE TEXT, TOTALDAILYDISTANCE TEXT, SYNCHED TEXT)";



    private static final String DELETE1 =  "DROP TABLE IF EXISTS Settings";
    private static final String DELETE2 = "DROP TABLE IF EXISTS VehicleLog";

    public class theDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Tracker2.db";

        public theDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE1);
            db.execSQL(CREATE2);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(DELETE1);
            db.execSQL(DELETE2);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public ArrayList<String> read(Context cnt, String table, String[] columns){
        ArrayList<String> ret = new ArrayList<>();


        theDbHelper dbHelper = new theDbHelper(cnt);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, columns, "", new String[] {}, null,null, "");

        for (int i = 0; i < columns.length; i++){
            ret.add("");
        }
        while(cursor.moveToNext()) {
            //   long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("ID"));

            for (int i = 0; i < columns.length; i++){
                ret.set(i, cursor.getString(cursor.getColumnIndexOrThrow(columns[i])));
            }

        }
        cursor.close();
        return ret;
    }

    public long inserter( SQLiteDatabase db ,String table, ArrayList<option> vals){

        String f = "INSERT INTO "+table+" (", s = " VALUES(";
        ContentValues values = new ContentValues();
        for (int i = 0; i < vals.size(); i++){

            f += vals.get(i).Option1;
            if (i < (vals.size() - 1)){f += ", ";}


            s += "\""+vals.get(i).Option2 + "\"";
            if (i < (vals.size() - 1)){s += ", ";}

            values.put(vals.get(i).Option1.toString(), vals.get(i).Option2.toString());
        }
        f += ") " + s + ");";
// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(table, null, values);

        return newRowId;
    }

    public void dropcreate(Context cnt){

        MyModel.theDbHelper dbHelper = new MyModel.theDbHelper(cnt);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(DELETE2);
        db.execSQL(CREATE2);
    }

    public String cols(String table, Context cnt){
        String cl = "";
        MyModel.theDbHelper dbHelper = new MyModel.theDbHelper(cnt);
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor dbCursor = db.query(table, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();

        for (int i = 0; i < columnNames.length; i++){
            cl += "    _" +columnNames[i].toString();
        }

        return cl;
    }














    public String randomstring() {
        String generatedString = "";
        for (int i = 0; i < 5; i++){
            Random rand = new Random();
            int n = rand.nextInt(25);
            generatedString = generatedString + alphabet().get(n);
            n = rand.nextInt(10);
            generatedString = generatedString + String.valueOf(n);
        }

        return generatedString;
    }

    public ArrayList<String> alphabet()
    {
        ArrayList<String> a = new ArrayList<String>();
        a.add("a");  a.add("b");  a.add("c");  a.add("d");  a.add("e");  a.add("f");  a.add("g");  a.add("h");  a.add("i");  a.add("j");  a.add("k");
        a.add("l");  a.add("m");  a.add("n");  a.add("o");  a.add("p");  a.add("q");  a.add("r");  a.add("s");  a.add("t");  a.add("u");  a.add("v");
        a.add("w");  a.add("x");  a.add("y");  a.add("z");

        return a;
    }

    public  int attempts = 5;


    public static class   CartItem
    {
        public String ID = "";
        public String Name = "";
        public String VAT = "";
        public String ItemNumber = "";
        public String SizeText = "";
        public String Quantity = "";
        public String PurchasePrice = "";
        public String SellingPrice = "";
        public String Total = "";
        public String Pic1 = "";
        public String ItemType = "";
        public String CartID = "";
        public String Executor = "";
        public String ExecutorName = "";

        public CartItem(){    }
        public CartItem(String name, String sellprice, String total,String id, String qty, String purchase,
                        String num, String vat, String pic1, String type, String sizetext, String cartid,
                        String executor, String executorname)
        {
            Name = name;
            SellingPrice = sellprice;
            PurchasePrice = purchase;
            Total = total;
            ID = id;
            Quantity = qty;
            ItemNumber = num;
            VAT = vat;
            Pic1 = pic1;
            ItemType = type;
            SizeText = sizetext;
            CartID = cartid;
            Executor = executor;
            ExecutorName = executorname;
        }

        public CartItem(String id, String qty)
        {
            ID = id;
            Quantity = qty;
        }
    }


    public String fixdateortime(String initial){
        if (initial.contains((" "))){
            initial = initial.substring(0, initial.indexOf(" "));
        }
        return initial;
    }


    public ArrayList<option> sortArray(ArrayList<option> ops)
    {
        for (int i = 1; i < ops.size(); i++)
        {
            int j = i;
            option a = ops.get(i);
            while ((j > 0) && (ops.get((j-1)).Value > a.Value))   //returns true when both conditions are true
            {
                ops.set(j, ops.get((j-1)));
                j--;
            }
            ops.set(j, a);
        }
        return ops;
    }



    public String checkemail(String email) {
        String mss = "";

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
        {

            mss += "You have entered an invalid Email" +System.lineSeparator();
        }

        return mss;
    }


    public String checkvalue(String fieldname, String entry){
        String mss = "";
        if (entry.equals("")){
            mss = "Please Enter A " + fieldname;
        }


        mss += checknochars(fieldname, entry);

        return mss;
    }

    public String checknochars(String fieldname, String entry){
        String mss = "";

        for (int i = 0; i < NoChars.length; i++){
            if (entry.indexOf(NoChars[i]) > -1){
                mss += "You have entered an invalid character (~, ¬, |, ^, $, £, <, >, * ;) for " + fieldname;
            }
        }

        return mss;
    }


    public JsonObjectRequest requestwaitjson(JsonObjectRequest jsRequest){

        jsRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return jsRequest;
    }


    public StringRequest requestwait( StringRequest stringRequest){

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return stringRequest;
    }

    public  StringRequest longrequestwait( StringRequest stringRequest){

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        return stringRequest;
    }

    public  void newcurrent(SQLiteDatabase mydatabase, String posuser, String posusername,
                            String loc,String locname, String code, String symbol, String companyname){
        String siteref = Site;
        if (DbName.equals("valeronpro")){ option oi = valeronuser(mydatabase); siteref = oi.Option2; }


        MyModel.option recs = new MyModel.option("", "", "", "");
        Cursor rs =  mydatabase.rawQuery(
                "select * from VALERONCURRENT where SITEREF = '"+siteref+"'", null );

        while ( rs.moveToNext()) {
            recs.Option1 = rs.getString(rs.getColumnIndex("SITEREF"));
            recs.Option2 = rs.getString(rs.getColumnIndex("POSUSERID"));
            recs.Option3 = rs.getString(rs.getColumnIndex("POSUSER"));
            recs.Option4 = rs.getString(rs.getColumnIndex("COMPANYLOCATION"));
            recs.Option5 = rs.getString(rs.getColumnIndex("COMPANYLOCATIONNAME"));
            recs.Option6 = rs.getString(rs.getColumnIndex("ONLINE"));
            recs.Option7 = rs.getString(rs.getColumnIndex("CURRENCYCODE"));
            recs.Option8 = rs.getString(rs.getColumnIndex("CURRENCYSYMBOL"));
            recs.Option9 = rs.getString(rs.getColumnIndex("TPIN"));
            recs.Option10 = rs.getString(rs.getColumnIndex("COMPANYNAME"));
            recs.Option11 = rs.getString(rs.getColumnIndex("CONTACTNUMBER"));
            recs.Option12 = rs.getString(rs.getColumnIndex("THANKYOUMESSAGE"));
        }

        if (recs.Option1.equals("")){
            mydatabase.execSQL("INSERT INTO VALERONCURRENT VALUES('"+siteref+"', '"+
                    posuser+"', '"+posusername+"', '"+loc+"', '"+
                    locname+"', '1', '"+code+"', '"+symbol+"', '', '"+companyname+"', '', '')");
        }
    }

    public  MyModel.option  sqlupdate(ArrayList<MyModel.option> values,  SQLiteDatabase mydatabase){

        String siteref = Site;
        if (DbName.equals("valeronpro")){ option oi = valeronuser(mydatabase); siteref = oi.Option2; }

        for (int i = 0;i  < values.size();i++){
            mydatabase.execSQL("UPDATE  VALERONCURRENT  SET "+values.get(i).ID+" = '"+ values.get(i).Option1+"' where SITEREF = '"+siteref+"'");
        }

        MyModel.option recs = new MyModel.option("", "", "", "");
        Cursor rs =  mydatabase.rawQuery(
                "select * from VALERONCURRENT where SITEREF = '"+siteref+"'", null );

        while ( rs.moveToNext()) {
            recs.Option1 = rs.getString(rs.getColumnIndex("SITEREF"));
            recs.Option2 = rs.getString(rs.getColumnIndex("POSUSERID"));
            recs.Option3 = rs.getString(rs.getColumnIndex("POSUSER"));
            recs.Option4 = rs.getString(rs.getColumnIndex("COMPANYLOCATION"));
            recs.Option5 = rs.getString(rs.getColumnIndex("COMPANYLOCATIONNAME"));
            recs.Option6 = rs.getString(rs.getColumnIndex("ONLINE"));
            recs.Option7 = rs.getString(rs.getColumnIndex("CURRENCYCODE"));
            recs.Option8 = rs.getString(rs.getColumnIndex("CURRENCYSYMBOL"));

            recs.Option9 = rs.getString(rs.getColumnIndex("TPIN"));
            recs.Option10 = rs.getString(rs.getColumnIndex("COMPANYNAME"));
            recs.Option11 = rs.getString(rs.getColumnIndex("CONTACTNUMBER"));
            recs.Option12 = rs.getString(rs.getColumnIndex("THANKYOUMESSAGE"));
        }
        return recs;
    }




    public void addofflinesale(String savestring,  String ordernumber,  SQLiteDatabase mydatabase, String url, float total,
                               String customer){

        String siteref = Site;
        if (DbName.equals("valeronpro")){ option oi = valeronuser(mydatabase); siteref = oi.Option2; }

        ArrayList<MyModel.option> recs = new ArrayList<>();
        Cursor rs =  mydatabase.rawQuery(
                "select * from VALOFFLINESALES where SITEREF = '"+siteref+"' and ORDERNUMBER = '"+ordernumber+"'", null );

        while ( rs.moveToNext()) {
            recs.add(new MyModel.option("",
                    rs.getString(rs.getColumnIndex("ID")),
                    rs.getString(rs.getColumnIndex("ORDERNUMBER")),  ""));
        }

        String nw = noww(false, true).Value;
        if (recs.size() > 0){
            mydatabase.execSQL("UPDATE VALOFFLINESALES SET CREATED = '"+
                    nw+"', URL = '"+url+"', CODE = "+savestring+
                    ", SYNCHED = 0, TOTAL = "+String.valueOf(total)+", CUSTOMER = '"+customer+"' " +
                    "where SITEREF = '"+siteref+"' and ORDERNUMBER = '"+ordernumber+"'");
        }else{
            mydatabase.execSQL("INSERT INTO VALOFFLINESALES  (ORDERNUMBER, CREATED, URL, CODE, SYNCHED, TOTAL, CUSTOMER, SITEREF) " +
                    "VALUES('"+ordernumber+"', '"+nw+"', '"+url+"', '"+savestring+
                    "', 0, "+String.valueOf(total)+", '"+customer+"', '"+siteref+"')");

            mydatabase.execSQL("UPDATE OFFLINESALESCART SET QUANTITY = 0 where SITEREF = '"+siteref+"'");
        }
    }

    public void addofflinepurchase(String savestring,  String ordernumber,  SQLiteDatabase mydatabase, String url,
                                   float total){

        String siteref = Site;
        if (DbName.equals("valeronpro")){ option oi = valeronuser(mydatabase); siteref = oi.Option2; }

        ArrayList<MyModel.option> recs = new ArrayList<>();
        Cursor rs =  mydatabase.rawQuery(
                "select * from VALOFFLINEPURCHASES where SITEREF = '"+siteref+"' and ORDERNUMBER = '"+ordernumber+"'", null );

        while ( rs.moveToNext()) {
            recs.add(new MyModel.option("",
                    rs.getString(rs.getColumnIndex("ID")),
                    rs.getString(rs.getColumnIndex("ORDERNUMBER")),""));
        }

        String nw = noww(false, true).Value;
        if (recs.size() > 0){
            mydatabase.execSQL("UPDATE  VALOFFLINEPURCHASES SET CREATED = '"+
                    nw+"', URL = '"+url+"', CODE = "+savestring+
                    ", SYNCHED = 0, TOTAL = "+String.valueOf(total)+" where SITEREF = '"+siteref+"' and ORDERNUMBER = '"+ordernumber+"'");
        }else{
            mydatabase.execSQL("INSERT INTO VALOFFLINEPURCHASES (ORDERNUMBER, CREATED, URL, CODE, SYNCHED, TOTAL, SITEREF) VALUES('"+ordernumber+"', '"+nw+"', '"+url+"', '"+savestring+
                    "', 0, "+String.valueOf(total)+", '"+siteref+"')");

            mydatabase.execSQL("UPDATE OFFLINEPURCHASESCART SET QUANTITY = 0 where SITEREF = '"+siteref+"'");
        }
    }


    public void synchproducts(String sncstring,    SQLiteDatabase mydatabase){

        ArrayList<String> snclist  =  makearrayfromstring(sncstring, "¬");
        ArrayList<String> snccols = makearrayfromstring(getstring(snclist, 1), "~");

        for (int c = 2; c < snclist.size(); c++){
            ArrayList<String> row = makearrayfromstring(getstring(snclist, c), "~");
            String idd =  before(getstring(snclist, c), "~");
            String name =  coloption(snccols, "Name", row).Value;
            String num =  coloption(snccols, "ItemNumber", row).Value;
            String sell =  coloption(snccols, "SellingPrice", row).Value;
            String pur =  coloption(snccols, "PurchasePrice", row).Value;
            String tpe =  coloption(snccols, "ValueItemType", row).Value;
            String ref =  coloption(snccols, "Reference", row).Value;
            String txt =  coloption(snccols, "TextSize", row).Value;
            String vat =  coloption(snccols, "VAT", row).Value;
            String site =  coloption(snccols, "ValueSite", row).Value;
            String pic1 =  coloption(snccols, "Pic1", row).Value;
            String isav =  coloption(snccols, "IsAvailable", row).Value;
            if (isav.toLowerCase().equals("true")){isav = "1";}else{isav = "0";}

            String siteref = Site;
            if (DbName.equals("valeronpro")){ option oi = valeronuser(mydatabase); siteref = oi.Option2; }

            ArrayList<MyModel.option> recs = new ArrayList<>();
            Cursor rs =  mydatabase.rawQuery(
                    "select * from ITEMLIST where SITEREF = '"+siteref+"' and REFERENCE = '"+ref+"'", null );

            while ( rs.moveToNext()) {
                recs.add(new MyModel.option("",
                        rs.getString(rs.getColumnIndex("ID")),
                        rs.getString(rs.getColumnIndex("REFERENCE")),
                        String.valueOf(rs.getInt(rs.getColumnIndex("ITEMNUMBER")))));
            }

            if (recs.size() > 0){
                mydatabase.execSQL("UPDATE ITEMLIST SET NAME = '"+name+"', ITEMNUMBER = '"+num+"', SELLINGPRICE = "+sell+
                        ", PURCHASEPRICE = "+pur+", ITEMTYPE = "+tpe+", SIZETEXT = '"+txt+"', VAT = "+vat+
                        ", SITE = "+site+", PIC1 = '"+pic1+"', ISAVAILABLE = "+isav+" where REFERENCE = '"+ ref+"' and  SITEREF = '"+siteref+"'");
            }else{
                mydatabase.execSQL("INSERT INTO ITEMLIST " +
                        "(ID, NAME, ITEMNUMBER, SELLINGPRICE,  PURCHASEPRICE, ITEMTYPE, REFERENCE,  SIZETEXT, VAT, SITE, PIC1, ISAVAILABLE, SITEREF)" +
                        " VALUES("+idd+", '"+name+"', '"+num+"', "+sell+
                        ", "+pur+", "+tpe+", '"+ref+"', '"+txt+"', "+vat+ ", "+site+", '"+pic1+"', "+isav+",  '"+siteref+"')");

/*                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS  ITEMLIST (ID INT NOT NULL, " +
                        "NAME TEXT NOT NULL, ITEMNUMBER TEXT NOT NULL, SELLINGPRICE REAL NOT NULL,  PURCHASEPRICE REAL NOT NULL,
                         " +
                        "ITEMTYPE INT NOT NULL, REFERENCE TEXT NOT NULL,  SIZETEXT TEXT NOT NULL, VAT REAL NOT NULL, " +
                        "SITE INT NOT NULL, PIC1 TEXT NOT NULL, ISAVAILABLE INT NOT NULL, SITEREF TEXT NOT NULL);");*/
            }
        }
    }

    public ArrayList<MyModel.CartItem> searchproducts(String searchvalue,    SQLiteDatabase mydatabase){

        String siteref = Site;
        if (DbName.equals("valeronpro")){ option oi = valeronuser(mydatabase); siteref = oi.Option2; }

        ArrayList<MyModel.CartItem> recs = new ArrayList<>();
        Cursor rs =  mydatabase.rawQuery( "select * from ITEMLIST where SITEREF = '"+siteref+"' and  (NAME like '%"+searchvalue+"%' or  ITEMNUMBER like '%"+searchvalue+"%')", null );
        while ( rs.moveToNext()) {
            recs.add(new MyModel.CartItem(rs.getString(rs.getColumnIndex("NAME")),
                    rs.getString(rs.getColumnIndex("SELLINGPRICE")),   "",
                    rs.getString(rs.getColumnIndex("ID")),"1",
                    rs.getString(rs.getColumnIndex("PURCHASEPRICE")),
                    rs.getString(rs.getColumnIndex("ITEMNUMBER")),
                    rs.getString(rs.getColumnIndex("VAT")),
                    rs.getString(rs.getColumnIndex("PIC1")),
                    rs.getString(rs.getColumnIndex("ITEMTYPE")),
                    rs.getString(rs.getColumnIndex("SIZETEXT")),"", "", "" ));
        }

/*        CartItem(String name, String sellprice, String total,String id, String qty, String purchase,
                String num, String vat, String pic1, String type, String sizetext, String cartid,
                String executor, String executorname)*/

        return recs;
    }



    public static class option
    {
        public float Value  = 0;
        public int ValueInt  = 0;
        public String Table  = "";
        public String ID  = "";
        public String Column  = "";
        public String Option1  = "";
        public String Option2  = "";
        public String Option3  = "";
        public String Option4  = "";
        public String Option5  = "";
        public String Option6  = "";
        public String Option7  = "";
        public String Option8  = "";
        public String Option9  = "";
        public String Option10  = "";
        public String Option11  = "";
        public String Option12  = "";
        public String Option13  = "";
        public String Option14  = "";
        public String Option15  = "";
        public String Option16  = "";
        public String Option17  = "";
        public String Option18  = "";
        public String Option19  = "";
        public String Option20  = "";
        public String Option21  = "";
        public String Option22  = "";
        public String Option23  = "";
        public String Option24  = "";
        public String Option25  = "";
        public String Option26  = "";
        public String Option27  = "";
        public String Option28  = "";
        public String Option29  = "";
        public String Option30  = "";
        public String Option31 = "";
        public String Option32 = "";
        public String Option33 = "";
        public String Option34 = "";
        public String Option35 = "";
        public String Option36 = "";
        public String Option37 = "";
        public String Option38 = "";
        public String Option39 = "";
        public String Option40 = "";
        public ArrayList<String> ExtraList = new ArrayList<String>();
        public option()  {   }



        public option(int val, String id, String o1)  {
            ValueInt = val;
            ID = id;
            Option1 = o1;
        }


        public option(String id, String o1, String o2, String o3)  {
            ID = id;
            Option1 = o1;
            Option2 = o2;
            Option3 = o3;
        }

        public option(String table, String id, String o1, String o2, String o3, String o4) {
            Table = table;
            ID = id;
            Option1 = o1;
            Option2 = o2;
            Option3 = o3;
            Option4 = o4;
        }

        public option(String id, String o1, String o2, String o3, String o4, String o5, String o6)
        {
            ID = id;
            Option1 = o1;
            Option2 = o2;
            Option3 = o3;
            Option4 = o4;
            Option5 = o5;
            Option6 = o6;
        }

        public option(String id, String o1, String o2, String o3, String o4, String o5,
                      String o6, String o7)
        {
            ID = id;
            Option1 = o1;
            Option2 = o2;
            Option3 = o3;
            Option4 = o4;
            Option5 = o5;
            Option6 = o6;
            Option7 = o7;
        }


        public option(String id, String o1, String o2, String o3, String o4,
                      String o5, String o6, String o7, String o8, String o9, String o10,
                      String o11, String o12, String o13, String o14, String o15, String o16
                , String o17, String o18, String o19, String o20,
                      String o21, String o22, String o23, String o24, String o25, String o26
                , String o27, String o28, String o29, String o30,
                      String o31, String o32, String o33, String o34, String o35, String o36
                , String o37, String o38, String o39, String o40)
        {
            ID = id;
            Option1 = o1;
            Option2 = o2;
            Option3 = o3;
            Option4 = o4;
            Option5 = o5;
            Option6 = o6;
            Option7 = o7;
            Option8 = o8;
            Option9 = o9;
            Option10 = o10;
            Option11 = o11;
            Option12 = o12;
            Option13 = o13;
            Option14 = o14;
            Option15 = o15;
            Option16 = o16;
            Option17 = o17;
            Option18 = o18;
            Option19 = o19;
            Option20 = o20;
            Option21 = o21;
            Option22 = o22;
            Option23 = o23;
            Option24 = o24;
            Option25 = o25;
            Option26 = o26;
            Option27 = o27;
            Option28 = o28;
            Option29 = o29;
            Option30 = o30;
            Option31 = o31;
            Option32 = o32;
            Option33 = o33;
            Option34 = o34;
            Option35 = o35;
            Option36 = o36;
            Option37 = o37;
            Option38 = o38;
            Option39 = o39;
            Option40 = o40;
        }

    }


    public adate noww(Boolean underscore, boolean withtime){
        String bkr = "-"; String cln = ":"; String spc = " ";
        if (underscore){
            bkr = "_";
            cln = "_";
            spc = "_";
        }
        String nn  = "";
        Calendar calendar;
        calendar = Calendar.getInstance();
        ArrayList<String> cd = correctdate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        nn = cd.get(0) + bkr +
                doubledigit(cd.get(1))  + bkr +
                doubledigit(cd.get(2)) +  spc;
        if (withtime){
            nn +=  doubledigit(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))) +cln +
                    doubledigit(String.valueOf(calendar.get(Calendar.MINUTE))) +cln+
                    doubledigit(String.valueOf(calendar.get(Calendar.SECOND)));
        }

        adate dd = new adate(nn,calendar.get(Calendar.YEAR), parseint(cd.get(1)), parseint(cd.get(2)),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        return dd;
    }


    public static class adate
    {
        public String Value  = "";
        public int Year  = 2022;
        public int Month  = 1;
        public int Day  = 1;
        public int Hour  = 0;
        public int Minute = 0;
        public adate(){  }

        public adate(String value, int year, int month, int day, int hour, int minute){
            Value = value;
            Year = year;
            Month = month;
            Day = day;
            Hour = hour;
            Minute = minute;
        }

        public adate(int year, int month, int day){
            Year = year;
            Month = month;
            Day = day;

            Value = String.valueOf(year)+ "-" +new MyModel().doubledigit(String.valueOf(month)) + "-" +
                    new MyModel().doubledigit(String.valueOf(day));
        }
        public adate(int year, int month, int day, int hour, int minute){
            Year = year;
            Month = month;
            Day = day;
            Hour = hour;
            Minute = minute;

            Value = String.valueOf(Year )+ "-" +String.valueOf(Month) + "-" + String.valueOf(Day)
                    + " "  + new MyModel().doubledigit(String.valueOf(hour)) + ":"  +  new MyModel().doubledigit(String.valueOf(minute)) ;
        }
    }


    public String adddays(String startdate, int days){

        // create old date in string format
        if (startdate.equals("")){startdate = "1970-01-01";}

        // create instance of the SimpleDateFormat that matches the given date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //create instance of the Calendar class and set the date to the given date
        Calendar cal = Calendar.getInstance();
        try{  cal.setTime(sdf.parse(startdate));
        }catch(ParseException e){   }

        // use add() method to add the days to the given date
        // cal.add(Calendar.DAY_OF_MONTH, 3);

        cal.add(Calendar.DATE, days);
        String dateAfter = sdf.format(cal.getTime());

        return dateAfter;
    }

    public long daysbetween(String startdate, String enddate){

        // create old date in string format
        if (startdate.equals("")){startdate = "1970-01-01";}

        if (startdate.contains(" ")){startdate = startdate.substring(0, startdate.indexOf(" "));}
        if (enddate.contains(" ")){enddate = enddate.substring(0, enddate.indexOf(" "));}

        startdate = startdate.replace("/", "-");
        enddate = enddate.replace("/", "-");

        startdate = correctdate(startdate);
        enddate = correctdate(enddate);

        //Parsing the date
        LocalDate dateBefore = LocalDate.parse(startdate);
        LocalDate dateAfter = LocalDate.parse(enddate);

        //calculating number of days in between
        long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);

        return noOfDaysBetween;
    }


    public String before(String og, String charecter)
    {
        if (og.indexOf(charecter) > -1)
        {
            og = og.substring(0, og.indexOf(charecter));
            //if (og.charAt((og.length() - 1)))
        }
        return og;
    }

    public String breakurl(String URL, int times, String charecter)
    {

        if (URL.contains(charecter))
        {
            for (int i = 0; i < times; i++)
            {
                URL = URL.substring(URL.indexOf(charecter) + 1);
            }
            if (URL.contains(charecter))
            {
                URL = URL.substring(0, URL.indexOf(charecter));
            }
        }
        return URL;
    }


    public ArrayList<String> makearrayfromstring(String og, String charecter)
    {
        // THIS METHOD BREAKS DOWN A STRING INTO AN ARRAY BASED ON HOW MANY TIMES A SPECIFIC CHARECTER APPEARS
        ArrayList<String> ret = new ArrayList<>();
        while (og.indexOf(charecter) > -1 & !charecter.equals(""))
        {
            if (og.indexOf(charecter) > 0 & ret.size() == 0)
            {
                ret.add(og.substring(0, og.indexOf(charecter)));
            }
            String URL = og.substring(og.indexOf(charecter) + 1);
            URL = before(URL, charecter);
            ret.add(URL);
            og = og.substring(og.indexOf(charecter) + 1);
        }
        if(ret.size() == 0)
        {
            if (og.length() > 0) { ret.add(og); }
        }
        return ret;
    }


    public String returndisplay(String cool)
    {
        // THIS SEPARATES NAMES OF TABLES, COLUMNS AND OTHER ITEMS ACCORDING TO CAPITAL LETTERS
        String addon = "";
        if (cool.length() > 0)
        {
            ArrayList<String> aroy = new ArrayList<>();  int laststart = 0;

            for (int i = 1; i < cool.length(); i++)
            {
                if (Character.isUpperCase(cool.charAt(i)))
                {
                    String mm = cool.substring(laststart, i);
                    aroy.add(mm);
                    laststart = i;
                }
            }
            if (laststart < cool.length()){
                aroy.add(cool.substring(laststart, cool.length()));
            }

            cool = aroy.get(0); for (int i = 1; i < aroy.size(); i++) {   cool += ' ' + aroy.get(i);  } cool += addon;
        }


        return cool;
    }

    public String displaybit(String cld){

        if (cld.toLowerCase().equals("true") || cld.equals("1") ){cld = "Yes";}
        else if ( cld.toLowerCase().equals("false") || cld.equals("0")){ cld = "No";}
        return cld;

    }




    public int charecteroccurence(String val, String charecter)
    {
        int vl = 0;

        if (val != null)
        {
            for (int i = 0; i < val.length(); i++)
            {
                if (String.valueOf(val.charAt(i)).equals(charecter)) { vl++; }
            }
        }

        return vl;
    }


    public static class CADT
    {
        public String Table = "";
        public String TableID = "";
        public String ID = "";
        public String Column = "";
        public String Value = "";
        public String Display = "";
        public String DataType = "";
        public double Length = 0;

        public CADT(String col, String val, String disp, String datatype)
        {
            Column = col;
            Value = val;
            Display = disp;
            DataType = datatype;
        }

        public CADT(String table, String tableid, String id, String column, String val, String display)
        {
            Table = table;
            TableID = tableid;
            ID = id;
            Column = column;
            Value = val;
            Display = display;
        }

    }


    public  CADT coloption(ArrayList<String> columns, String column, ArrayList<String> values) {
        CADT colop = new CADT("", "", "", "");
        for (int i = 0; i < columns.size(); i++) {
            String cl = columns.get(i);
            if (cl.indexOf("^") > -1) {
                colop.Column = before(cl, "^");
                colop.DataType = breakurl(cl, 1, "^");
            }else{colop.Column =cl;}

            if (colop.Column.equals(column) & values.size() > i) {
                colop.Value = values.get(i);
            }
        }
        return colop;
    }

    public  String rowstring(ArrayList<String> columns, ArrayList<String> values) {
        String colop = "";
        for (int i = 0; i < columns.size(); i++) {
            String cl = columns.get(i);
            if (cl.indexOf("^") > -1) {  cl = before(cl, "^");  }
            colop += "|"+cl +"^" + values.get(i);
        }
        return colop;
    }

    public  String rowoption(ArrayList<String> rowstring, String column) {
        String colop = "";
        for (int i = 0; i < rowstring.size(); i++) {
            String cl = rowstring.get(i);
            if (cl.indexOf("^") > -1) {
                cl = before(cl, "^");
            }

            if (cl.equals(column)) {
                colop =  breakurl( rowstring.get(i), 1, "^");
            }
        }
        return colop;
    }

    public ArrayList<String> correctdate(int year, int month, int day){
        ArrayList<String> dd = new ArrayList<>();
        month++;
        dd.add(String.valueOf(year));
        dd.add(String.valueOf(month));
        dd.add(String.valueOf(day));

        return  dd;
    }





    public void br(Context cnt,  LinearLayout lnr, int times){

        for(int i = 0; i < times; i++){}
        textview(cnt, lnr, "", white, white, 0, 0, 0,  0, times, 0, 0);

    }

    public RelativeLayout.LayoutParams params(int leftid, int belowid){
        RelativeLayout.LayoutParams tparams0 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (leftid > 0){ tparams0.addRule(RelativeLayout.RIGHT_OF,  leftid);   }
        else{ tparams0.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);}
        if (belowid > 0){ tparams0.addRule(RelativeLayout.BELOW, belowid); }
        return tparams0;
    }


    public void textview(Context cnt, LinearLayout rll, String text, int backcolor, int textcolor, int size, int id,
                         int width, int height, int lines, int textalign, int style){

        TextView welcome = new TextView(cnt);
        welcome.setText(text);
        welcome.setTextSize(size);
        welcome.setId(id);
        welcome.setTypeface(null, style);
        welcome.setTextColor(textcolor);
        welcome.setBackgroundColor(backcolor);
        if (textalign > 0){welcome.setGravity(textalign);}
        if (lines > 0){ welcome.setLines(lines); }
        if (height == 0){
            welcome.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        }else{
            welcome.setWidth(width);
        }
        rll.addView(welcome);
    }



    public void progressbar(Context cnt, RelativeLayout rll, int id, double width){
        ProgressBar pbbar = new ProgressBar(cnt);
        pbbar.setId(id);

        rll.addView(pbbar);
        pbbar.setVisibility(View.VISIBLE);
    }


    public void button(Context cnt, LinearLayout rll, String text, String tag,  int id,
                       String type, String displaystring, int size, int width, int height){

        MaterialButton btn = new MaterialButton(cnt);
        btn.setText(text);
        btn.setTag(tag);
        btn.setTextSize(size);
        btn.setId(id);

        btn.setPadding(2, 2, 2, 2);
        btn.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        ArrayList<Integer> des = applydesign(cnt, type, displaystring);
        btn.setBackgroundColor(des.get(0));
        btn.setTextColor(des.get(1));
        btn.setCornerRadius(des.get(2));

        rll.addView(btn);
    }

    public MaterialButton  makebutton(Context cnt, String text, String tag,  int id,
                                      String type, String displaystring, int size, int width, int height){

        MaterialButton btn = new MaterialButton(cnt);
        btn.setText(text);
        btn.setTag(tag);
        btn.setTextSize(size);
        btn.setId(id);

        btn.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(0, 0, 0, 0);
        btn.setLayoutParams(lp);

        ArrayList<Integer> des = applydesign(cnt, type, displaystring);
        btn.setBackgroundColor(des.get(0));
        btn.setTextColor(des.get(1));
        btn.setCornerRadius(des.get(2));

        return btn;
    }


    public void toast(Context cnt, String message){
        try { Toast.makeText(cnt, message, Toast.LENGTH_SHORT).show();  }catch (Exception x){}
    }

    public void clipboard(Context cnt, String message){
        ClipboardManager clipboard = (ClipboardManager) cnt.getSystemService(cnt.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Saved to clip board", message);
        clipboard.setPrimaryClip(clip);

    }

    public ImageView image(Context cnt, int id, String displaystring){

        ImageView imgView = new ImageView(cnt); imgView.setId(id );
        String loadim = appsetting("ImageLoading", displaystring);

       try{
           Glide.with(cnt).load(R.drawable.loading).into(imgView);
           if (loadim.equals("2")){
               Glide.with(cnt).load(R.drawable.infinity).into(imgView);
           }else  if (loadim.equals("3")){
               Glide.with(cnt).load(R.drawable.squares).into(imgView);
           }else  if (loadim.equals("4")){
               Glide.with(cnt).load(R.drawable.triangles).into(imgView);
           }
       }catch(Exception xx){}

        return imgView;
    }

    public MaterialButton addtocarticon(MaterialButton mb, String displaystring){

        mb.setBackgroundResource(R.drawable.ic_baseline_add_box_24);
        if (appsetting("ValueAddToCartIcon", displaystring).equals("1")){
            mb.setBackgroundResource(R.drawable.ic_baseline_shopping_cart_24);   }
        else  if (appsetting("ValueAddToCartIcon", displaystring).equals("2")){
            mb.setBackgroundResource(R.drawable.ic_baseline_add_box_24);  }
        else  if (appsetting("ValueAddToCartIcon", displaystring).equals("3")){
            mb.setBackgroundResource(R.drawable.ic_baseline_check_box_24);  }

        return mb;

    }

    public MaterialTextView  maketextview(Context cnt, String text, String tag,  int id,
                                          String type, String displaystring, int size, int width, int height, boolean btnstyle){

        MaterialTextView tv = new MaterialTextView(cnt);
        tv.setText(text);
        tv.setTag(tag);
        tv.setTextSize(size);
        tv.setId(id);

        int tgap = 0, lgap = 0, rgap = 0, bgap = 0;

        if (btnstyle){  tv = buttonstyle(tv); }

        tv.setPadding(lgap, tgap, rgap, bgap);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(0, 0, 0, 0);
        tv.setLayoutParams(lp);

        ArrayList<Integer> des = applydesign(cnt,type, displaystring);
        if(des.size() > 0){
            tv.setBackgroundColor(des.get(0));
            tv.setTextColor(des.get(1));
        }
        //tv.setCornerRadius(des.get(2));

        return tv;
    }


    public MaterialTextView buttonstyle(MaterialTextView mt){
        mt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        return mt;
    }
    public TextView buttonstyle(TextView mt){
        mt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        return mt;
    }

    public MaterialButton buttondesign(Context cnt, MaterialButton btn, String type, String displaystring){

        ArrayList<Integer> des = applydesign(cnt, type, displaystring);
        btn.setBackgroundColor(des.get(0));
        btn.setTextColor(des.get(1));
        btn.setCornerRadius(des.get(2));
        return btn;
    }

    public MaterialTextView textviewdesign(Context cnt,MaterialTextView tv, String type, String displaystring){

        ArrayList<Integer> des = applydesign(cnt,type, displaystring);
        tv.setBackgroundColor(des.get(0));
        tv.setTextColor(des.get(1));
        return tv;
    }


    public void textview(Context cnt, RelativeLayout rll, String text,  String type, String displaystring, int size, int id,
                         int width, int leftid, int belowid, int lines, int textalign, int style){

        MaterialTextView welcome = new MaterialTextView(cnt);
        welcome.setText(text);
        welcome.setTextSize(size);
        welcome.setId(id);
        welcome.setTypeface(null, style);

        ArrayList<Integer> des = applydesign(cnt,type, displaystring);
        welcome.setBackgroundColor(des.get(0));
        welcome.setTextColor(des.get(1));

        if (textalign > 0){welcome.setGravity(textalign);}
        if (lines > 0){ welcome.setLines(lines); }
        welcome.setLayoutParams(params(leftid, belowid));
        welcome.getLayoutParams().width= width;

        rll.addView(welcome);
    }







    public ArrayList<Integer> applydesign(Context cnt, String type, String displaystring){

        int  gobtncolor = intcolor(appsetting("GoButtonColor", displaystring)),
                gobtntext = intcolor(appsetting("GoButtonTextColor", displaystring)),
                gobtnradi = parseint(appsetting("GoButtonCornerRadius", displaystring)),

                genbtncolor = intcolor(appsetting("GeneralButtonColor", displaystring)),
                genbtntext = intcolor(appsetting("GeneralButtonTextColor", displaystring)),
                genbtnradi = parseint(appsetting("GeneralButtonCornerRadius", displaystring)),

                canbtncolor = intcolor(appsetting("CancelButtonColor", displaystring)),
                canbtntext = intcolor(appsetting("CancelButtonTextColor", displaystring)),
                canbtnradi = parseint(appsetting("CancelButtonCornerRadius", displaystring)),

                infobtncolor = transparent,
              //  infobtncolor = intcolor(appsetting("InfoButtonColor", displaystring)),
                infobtntext = intcolor(appsetting("InfoButtonTextColor", displaystring)),
                infobtnradi = parseint(appsetting("InfoButtonCornerRadius", displaystring)),

                warnbtncolor = intcolor(appsetting("WarningButtonColor", displaystring)),
                warnbtntext = intcolor(appsetting("WarningButtonTextColor", displaystring)),
                warnbtnradi = parseint(appsetting("WarningButtonCornerRadius", displaystring)),

                inputcolor = intcolor(appsetting("InputBackgroundColor", displaystring)),
                inputtext = intcolor(appsetting("InputTextColor", displaystring)),
                inputradius = parseint(appsetting("InputCornerRadius", displaystring));

        if (Site.equals("www.valeronpro.com")){
            int rd = 7;
            gobtncolor = intcolor("#872f79"); gobtntext = intcolor("#ffffff");  gobtnradi = rd;

            genbtncolor = intcolor("#ad6da3");  genbtntext = intcolor("#ffffff"); genbtnradi = rd;

            canbtncolor = intcolor("#4d494c");  canbtntext = intcolor("#ffffff"); canbtnradi = rd;

            infobtncolor = intcolor("#ffffff");  infobtntext = intcolor("#383838"); infobtnradi = rd;

            warnbtncolor = intcolor("#4d494c"); warnbtntext = intcolor("#ffffff");  warnbtnradi = rd;

            inputcolor = intcolor("#ffffff");   inputtext = intcolor("#383838");  inputradius = rd;
        }


        int nightModeFlags =
                cnt.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:

                //  infobtncolor = intcolor("#000000");  infobtntext = intcolor("#ffffff");
                break;

            case Configuration.UI_MODE_NIGHT_NO:  break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED: break;
        }


        ArrayList<Integer> fints = new ArrayList<>();
        if (type.equals("Go")){  fints.add(gobtncolor); fints.add(gobtntext); fints.add(gobtnradi); }
        else if (type.equals("General"))
        {  fints.add(genbtncolor); fints.add(genbtntext); fints.add(genbtnradi); }
        else if (type.equals("Cancel"))
        {  fints.add(canbtncolor); fints.add(canbtntext); fints.add(canbtnradi); }
        else if (type.equals("Info"))
        {  fints.add(infobtncolor); fints.add(infobtntext); fints.add(infobtnradi); }
        else if (type.equals("Warning"))
        {  fints.add(warnbtncolor); fints.add(warnbtntext); fints.add(warnbtnradi); }
        else if (type.equals("Input"))
        {  fints.add(inputcolor); fints.add(inputtext); fints.add(inputradius);}

        return fints;
    }





    public ImageView loadimage(Resources res, ImageView imgView, String url, int width, int height){

        MyModel.LoadImageTask.Listener listener = new MyModel.LoadImageTask.Listener() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                try{imgView.setImageBitmap(bitmap);}catch (Exception xx){}
            }

            @Override
            public void onError() {  imgView.setImageResource(R.drawable.logosmall); }
        };
        MyModel.LoadImageTask lit = new MyModel.LoadImageTask(listener, width, height);
        lit.execute(url);

        return imgView;
    }


    public static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        public LoadImageTask(Listener listener, int width, int height) {  mListener = listener;  }

        public interface Listener{    void onImageLoaded(Bitmap bitmap);  void onError();  }

        private Listener mListener;
        public int  reqWidth;
        public int reqHeight;

        public static int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }





        @Override
        protected Bitmap doInBackground(String... args) {

            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;

                return BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent(), null, options);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {
                mListener.onImageLoaded(bitmap);

            } else {
                mListener.onError();
            }
        }
    }





    public String appsetting(String column, String DisplayString){

        ArrayList<String> disarray = makearrayfromstring(DisplayString, "~");
        String vl ="";
        for (int i = 0; i < disarray.size(); i++){
            String cl = before(disarray.get(i), "^");
            if (cl.equals(column)){
                vl = breakurl(disarray.get(i), 1, "^");
            }
        }
        return vl;
    }


    public View line(Context cnt, int width, int height, int color){

        View    vw = new View(cnt); vw.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        vw.setBackgroundColor(color); return vw;
    }

    public CheckBox  checkbox(Context cnt,  String text, String tag, int backcolor, int textcolor, int id, int size,
                              int width, int height){

        CheckBox editCheckBox = new CheckBox(cnt);
        editCheckBox.setId(id);
        editCheckBox.setText(text);
        if (tag.equals("1") ||tag.equals("True") || tag.toLowerCase().equals("true"))  {editCheckBox.setChecked(true);}
        editCheckBox.setTextColor(textcolor);
        editCheckBox.setBackgroundColor(backcolor); // hex color 0xAARRGGBB
        editCheckBox.setTextSize(size);
        editCheckBox.setTag(tag);
        editCheckBox.setPadding(10, 10, 10, 10); // in pixels (left, top, right, bottom)

        if (height == 0){
            editCheckBox.setWidth(width);
        }else{
            editCheckBox.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        }
        return editCheckBox;
    }

    public EditText  edittext(Context cnt, int ID, String placeholder, String value, String inputtype,
                              int width, int height, int size, String displaystring){

        EditText editText1 = new EditText(cnt);
        editText1.setId(ID);
        editText1.setText(value);
        editText1.setTextSize(size);
        editText1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        editText1.setHint(placeholder);
        editText1.setBackgroundColor(0xFFF2F2F2); // hex color 0xAARRGGBB
        editText1.setTextColor(0xFF000000);
        editText1.setPadding(10, 10, 10, 10); // in pixels (left, top, right, bottom)

        ArrayList<Integer> des = applydesign(cnt,"Input", displaystring);
        editText1.setBackgroundColor(des.get(0));
        editText1.setTextColor(des.get(1));

        if (inputtype.toLowerCase().equals("phone")){ editText1.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (inputtype.toLowerCase().equals("number")){ editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (inputtype.toLowerCase().equals("date")){ editText1.setInputType(InputType.TYPE_CLASS_DATETIME);
        } else if (inputtype.toLowerCase().equals("password")){
            editText1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText1.setTransformationMethod(new PasswordTransformationMethod());
        }

        if (height == 0){editText1.setWidth(width);}
        else{
            editText1.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        }
        return editText1;
    }



    public int getint(ArrayList<Integer> arr, int index){

        int ret = -1;
        if (arr.size() >= (index + 1))
        {
            ret = arr.get(index);
        }
        return ret;
    }


    public String getstring(ArrayList<String> arr, int index){

        String ret = "";
        if (arr.size() >= (index + 1))
        {
            ret = arr.get(index);
        }
        return ret;
    }


    public option getoption(ArrayList<option> arr, int index){

        option ret = new option("", "", "", "");
        if (arr.size() >= (index + 1))
        {
            ret = arr.get(index);
        }
        return ret;
    }

    public CartItem getcartitem(ArrayList<CartItem> arr, int index){

        CartItem ret = new CartItem("", "0");
        if (arr.size() >= (index + 1))
        {
            ret = arr.get(index);
        }
        return ret;
    }


    public String getarray(String[] arr, int index){
        String ret = "";
        if (arr.length >= (index + 1))
        {
            ret = arr[index];
        }
        return ret;
    }



    public LinearLayout productdisplay(Context cnt, LinearLayout pln, String id, String name, String number, String price,
                                       String sizetext,  String pic, int blockwidth, int blockheight, String displaystring,
                                       int imageid, int labelid, int pricetagid, int addtocartid, int addtofavid, int withfaveheight,
                                       boolean smallscreen){

        pln.setOrientation(LinearLayout.VERTICAL); pln.setBackgroundResource(R.drawable.whitebutton);
        pln.setPadding(10, 5, 0, 2);

        boolean addfaves = appsetting("AddFavouritesButton", displaystring).toLowerCase().equals("true");

        int ww = (int) ( blockwidth * 0.95) - 5, nameheight = (int)(blockheight * 0.13), priceheight =
                (int)(blockheight * 0.14), picheight = (int)(blockheight * 0.56), lastheight = nameheight;
        int lw = (int)(ww*0.79), rw = (int)(ww *0.2);

        if (addfaves){
            blockheight = withfaveheight;
            picheight = (int)(blockheight * 0.39);
            nameheight = (int)(blockheight * 0.11);
            priceheight = (int)(blockheight * 0.07);
            rw = (int)(ww *0.18);
        }

        ImageView imgView = image(cnt, imageid, displaystring);
        imgView.setLayoutParams(new ViewGroup.LayoutParams(ww, picheight));

        String url = "https://www.valeronpro.com/Content/SiteImages/" +Site+"/"+ pic;
        imgView = loadimage(cnt.getResources() ,imgView, url, ww, picheight );
        pln.addView(imgView);




        TextView tv = new MaterialButton(cnt);
        int lnht = 2;
        if (appsetting("ShowItemNumber", displaystring).toLowerCase().equals("true")){

            if (!number.equals("")){number = System.lineSeparator() + number; lnht = 3;}
            name = name + number;
        }
        tv.setText(name); tv.setTypeface(null, Typeface.BOLD); tv.setId(labelid);
        tv.setLayoutParams(new RelativeLayout.LayoutParams(ww , (int)(nameheight*lnht)));
        pln.addView(tv); tv.setTextSize(fontsmall);

        LinearLayout replin  = new LinearLayout(cnt); replin.setOrientation(LinearLayout.HORIZONTAL);
        replin.setLayoutParams(new LinearLayout.LayoutParams(ww , priceheight));pln.addView(replin);

        if (addfaves){lw = ww; }

        String prc = appsetting("Currency", displaystring)+ price;

        int ctid = 11362 + parseint(id);
        MaterialTextView mt = maketextview(cnt, prc, "",pricetagid, "Info", displaystring, fontsmall,
                lw, priceheight, true);

        int pricecolor = parseint(appsetting("PriceColor", displaystring));
        if (pricecolor <= 0){  pricecolor = brightred;    }
        mt.setTextColor(pricecolor); mt.setTypeface(null, Typeface.BOLD);
        replin.addView(mt);


        int iconheight = (int)(priceheight*0.9);
        MaterialButton mb = new MaterialButton(cnt); mb.setId(addtocartid );
        mb.setLayoutParams(new FrameLayout.LayoutParams(rw, iconheight));
        mb = addtocarticon(mb, displaystring);

        mb.setPadding(10, 15, 10, 10);  mb.setTextColor(pricecolor); mb.setTextSize(fontsmall);

        if (!addfaves){
            mb.setLayoutParams(new RelativeLayout.LayoutParams(rw, iconheight));
            replin.addView(mb);
        }
        else if (addfaves){
            iconheight = (int)(lastheight*0.8);
            mb.setLayoutParams(new FrameLayout.LayoutParams(rw, iconheight));
            // reprel = imrellayout( 0, ww, lineheight); pln.addView(reprel);
            LinearLayout lml = new LinearLayout(cnt); lml.setOrientation(LinearLayout.HORIZONTAL);
            lml.setLayoutParams(new FrameLayout.LayoutParams(ww, lastheight)); pln.addView(lml);
            lml.setPadding(15, 0, 15, 4); lml.addView(mb);

            int midwidth = (int)(0.95*(ww - (rw * 2))) - 30;
            View bb = new View(cnt);
            LinearLayout.LayoutParams tr = new LinearLayout.LayoutParams(midwidth, iconheight);
            bb.setLayoutParams(tr); //bb.setBackgroundColor(transparent);
            lml.addView(bb);

            mb = new MaterialButton(cnt); mb.setId(addtofavid);
            mb.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
            mb.setPadding(10, 15, 10, 10);  mb.setTextColor(pricecolor); mb.setTextSize(fontsmall);

            mb.setLayoutParams(new LinearLayout.LayoutParams(rw, iconheight));
            lml.addView(mb);
        }

        return pln;
    }

















    public ArrayList<String> concatentingsms(ArrayList<String> ms, String text){
        int curindex  = ms.size() - 1;
        if ((ms.get(curindex).length() + text.length()) < 160){
            ms.set(curindex, ms.get(curindex) + text);
        }else  if ((ms.get(curindex).length() + text.length()) >= 160){
            ms.add(text);
        }
        return ms;
    }


    public String defaultvalues( String Datatype){
        String R = "";
        if (Datatype.equals("int") || Datatype.equals("float") || Datatype.equals("money") || Datatype.equals("bit")){
            R = "0";
        }else{
            if (Datatype.equals("datetime")){
                R = noww(false, true).Value;
            }
        }
        return R;
    }




    public String doubledigit(String og){
        if (og.length() == 1){
            og = "0" + og;
        }
        return og;
    }

    public String correctdate(String dv){
        String rt = "";
        ArrayList<String> dts = makearrayfromstring(dv, "-");
        for (int i = 0; i < dts.size(); i++){
            String vv = doubledigit(dts.get(i));
            rt += vv;
            if (i < (dts.size() - 1)){
                rt += "-";
            }
        }
        return rt;
    }






    public String smss(Context cnt, String number, String customername, String displaystring,
                       ArrayList<CartItem> items, float amountoff, String storename,
                       String companyname, String tpin, String contacts)
    {
        String z = "", curr = appsetting("Currency", displaystring),
                thanksmess = appsetting("TextThankYouMessage", displaystring);

        ArrayList<String> ms  = new ArrayList<>();

        ms = concatentingsms(ms,  companyname + ": "+storename+"." + "\n");
        ms = concatentingsms(ms,  "TPIN: "+tpin+"." + "\n");
        ms = concatentingsms(ms,  "Contact: "+contacts+"." + "\n");
        if (!customername.equals("")){
            ms = concatentingsms(ms, "Customer: "+customername+ "\n");
        }
        ms = concatentingsms(ms,  "Date: " + noww(false, true) + "\n");

        float subtotal = 0;
        for (int n = 0; n < items.size(); n++){
            String prc = getcartitem(items, n).SellingPrice, qty = getcartitem(items, n).Quantity;
            float total = round((parsefloat(prc))*parsefloat(qty), 2);

            String nm = getcartitem(items, n).Name + " (x "+qty+")"  + " @ "+curr+prc
                    + " = "+curr+ String.valueOf(total)  + "\n";
            ms = concatentingsms( ms,  nm);
            subtotal += total;
        }
        ms = concatentingsms( ms, "Sub Total: "+curr+ String.valueOf(subtotal) + "\n");


        if (amountoff > 0){
            subtotal -= amountoff;
            ms = concatentingsms( ms, "Discount: "+ curr + String.valueOf(amountoff)+ "\n");
        }

        ms = concatentingsms( ms,  "Total: K" +String.valueOf(subtotal)+ "\n");
        ms = concatentingsms(ms,   thanksmess  );

        for (int u = 0; u < ms.size(); u++){
            Boolean sendit = true;
            if (u > 0){ if (ms.get(u).equals(ms.get((u - 1)))){ sendit = false;  } }

            if (sendit)
            {
                try{
                 /* String SENT = "SMS_SENT";
                  PendingIntent sentPI = PendingIntent.getBroadcast(cnt, 0,new Intent(SENT), 0);
                  SmsManager smsManager = SmsManager.getDefault();
                  smsManager.sendTextMessage(number, null, ms.get(u), null, null);
*/

             /*     Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                  smsIntent.setData(Uri.parse("smsto:"));
                  smsIntent.setType("vnd.android-dir/mms-sms");
                  smsIntent.putExtra("address"  , new String(number));
                  smsIntent.putExtra("sms_body"  , ms.get(u));

                  try {
                      startActivity(smsIntent);
                    //  finish();
                  } catch (android.content.ActivityNotFoundException ex) {
                     toast(cnt, "SMS failed, please try again later.");
                  }*/

                }catch (Exception xx){}
            }
        }





        return z;

    }



    public option setoption(option og, String property, String value){

        Field f = null;
        try {
            f = og.getClass().getDeclaredField(property);
            f.setAccessible(true);
            f.set(og, value);



            Field field = og.getClass().getField(property);
            field.set(og, value);

        } catch (NoSuchFieldException | IllegalAccessException e) {   }

        return og;
    }

    public String getoption(option og, String property){
        String op = "";   Field f = null;
        try {
            Field field = og.getClass().getField(property);
            op = String.valueOf(field.get(og));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // e.printStackTrace();
        }

        return op;
    }


    public boolean switchcompany(String recuser, String recsite, String stname, SQLiteDatabase mydatabase){

        boolean go = false;

        mydatabase.execSQL("UPDATE VALERONSITES SET LOGGEDIN = 0  where USERREFERENCE = '"+recuser+"' ");

        if (!recsite.equals("")){
            ArrayList<MyModel.option> recs = new ArrayList<>();
            Cursor rs =  mydatabase.rawQuery( "select * from VALERONSITES where USERREFERENCE = '"+recuser+"' and SITEREFERENCE = '"+recsite+"'", null );
            while ( rs.moveToNext()) {
                recs.add(new MyModel.option("",
                        rs.getString(rs.getColumnIndex("SITEREFERENCE")),
                        rs.getString(rs.getColumnIndex("DBO")),
                        String.valueOf(rs.getInt(rs.getColumnIndex("LOGGEDIN")))));
            }

            if (recs.size() > 0){
                mydatabase.execSQL("UPDATE VALERONSITES SET LOGGEDIN = 1 where USERREFERENCE = '"+ recuser+"' and SITEREFERENCE = '"+recsite+"'");
                go = true;
            }else{
                if (!recsite.equals("") & !recuser.equals("") ){
                    mydatabase.execSQL("INSERT INTO VALERONSITES VALUES('"+recuser+"',  '"+stname+"', '"+recsite+"', '"+recsite+"', 1, '"+recsite+"', 1, 1)");
                    go = true;
                }
            }
        }

        return go;
    }


    public ArrayList<MyModel.option> Countries()
    {
        ArrayList<MyModel.option> l = new   ArrayList<MyModel.option>();

        l.add(new MyModel.option("230", "Afghanistan", "", ""));
        l.add(new MyModel.option("231", "Albania", "", ""));
        l.add(new MyModel.option("232", "Algeria", "", ""));
        l.add(new MyModel.option("5", "American Samoa", "", ""));
        l.add(new MyModel.option("234", "Andorra", "", ""));
        l.add(new MyModel.option("235", "Angola", "", ""));
        l.add(new MyModel.option("236", "Anguilla", "", ""));
        l.add(new MyModel.option("237", "Antarctica", "", ""));
        l.add(new MyModel.option("238", "Antigua and Barbuda", "", ""));
        l.add(new MyModel.option("239", "Argentina", "", ""));
        l.add(new MyModel.option("240", "Armenia", "", ""));
        l.add(new MyModel.option("241", "Aruba", "", ""));
        l.add(new MyModel.option("242", "Australia", "", ""));
        l.add(new MyModel.option("243", "Austria", "", ""));
        l.add(new MyModel.option("244", "Azerbaijan", "", ""));
        l.add(new MyModel.option("245", "Bahamas", "", ""));
        l.add(new MyModel.option("246", "Bahrain", "", ""));
        l.add(new MyModel.option("247", "Bangladesh", "", ""));
        l.add(new MyModel.option("248", "Barbados", "", ""));
        l.add(new MyModel.option("249", "Belarus", "", ""));
        l.add(new MyModel.option("250", "Belgium", "", ""));
        l.add(new MyModel.option("251", "Belize", "", ""));
        l.add(new MyModel.option("252", "Benin", "", ""));
        l.add(new MyModel.option("253", "Bermuda", "", ""));
        l.add(new MyModel.option("254", "Bhutan", "", ""));
        l.add(new MyModel.option("255", "Bolivia", "", ""));
        l.add(new MyModel.option("256", "Bosnia and Herzegovina", "", ""));
        l.add(new MyModel.option("28", "Botswana", "", ""));
        l.add(new MyModel.option("258", "Bouvet Island", "", ""));
        l.add(new MyModel.option("259", "Brazil", "", ""));
        l.add(new MyModel.option("260", "British Indian Ocean Territory", "", ""));
        l.add(new MyModel.option("261", "Brunei Darussalam", "", ""));
        l.add(new MyModel.option("262", "Bulgaria", "", ""));
        l.add(new MyModel.option("263", "Burkina Faso", "", ""));
        l.add(new MyModel.option("264", "Burundi", "", ""));
        l.add(new MyModel.option("265", "Cambodia", "", ""));
        l.add(new MyModel.option("266", "Cameroon", "", ""));
        l.add(new MyModel.option("267", "Canada", "", ""));
        l.add(new MyModel.option("268", "Cape Verde", "", ""));
        l.add(new MyModel.option("269", "Cayman Islands", "", ""));
        l.add(new MyModel.option("270", "Central African Republic", "", ""));
        l.add(new MyModel.option("271", "Chad", "", ""));
        l.add(new MyModel.option("272", "Chile", "", ""));
        l.add(new MyModel.option("273", "China", "", ""));
        l.add(new MyModel.option("274", "Christmas Island", "", ""));
        l.add(new MyModel.option("275", "Cocos (Keeling) Islands", "", ""));
        l.add(new MyModel.option("276", "Colombia", "", ""));
        l.add(new MyModel.option("277", "Comoros", "", ""));
        l.add(new MyModel.option("280", "Cook Islands", "", ""));
        l.add(new MyModel.option("281", "Costa Rica", "", ""));
        l.add(new MyModel.option("282", "Côte d'Ivoire", "", ""));
        l.add(new MyModel.option("283", "Croatia", "", ""));
        l.add(new MyModel.option("284", "Cuba", "", ""));
        l.add(new MyModel.option("285", "Cyprus", "", ""));
        l.add(new MyModel.option("286", "Czech Republic", "", ""));
        l.add(new MyModel.option("279", "Democratic Republic of the Congo", "", ""));
        l.add(new MyModel.option("287", "Denmark", "", ""));
        l.add(new MyModel.option("288", "Djibouti", "", ""));
        l.add(new MyModel.option("289", "Dominica", "", ""));
        l.add(new MyModel.option("290", "Dominican Republic", "", ""));
        l.add(new MyModel.option("291", "Ecuador", "", ""));
        l.add(new MyModel.option("292", "Egypt", "", ""));
        l.add(new MyModel.option("293", "El Salvador", "", ""));
        l.add(new MyModel.option("294", "Equatorial Guinea", "", ""));
        l.add(new MyModel.option("295", "Eritrea", "", ""));
        l.add(new MyModel.option("296", "Estonia", "", ""));
        l.add(new MyModel.option("430", "Eswatini", "", ""));
        l.add(new MyModel.option("297", "Ethiopia", "", ""));
        l.add(new MyModel.option("298", "Falkland Islands (Malvinas)", "", ""));
        l.add(new MyModel.option("299", "Faroe Islands", "", ""));
        l.add(new MyModel.option("300", "Fiji", "", ""));
        l.add(new MyModel.option("301", "Finland", "", ""));
        l.add(new MyModel.option("302", "France", "", ""));
        l.add(new MyModel.option("303", "French Guiana", "", ""));
        l.add(new MyModel.option("304", "French Polynesia", "", ""));
        l.add(new MyModel.option("305", "French Southern Territories", "", ""));
        l.add(new MyModel.option("306", "Gabon", "", ""));
        l.add(new MyModel.option("307", "Gambia", "", ""));
        l.add(new MyModel.option("308", "Georgia", "", ""));
        l.add(new MyModel.option("309", "Germany", "", ""));
        l.add(new MyModel.option("310", "Ghana", "", ""));
        l.add(new MyModel.option("311", "Gibraltar", "", ""));
        l.add(new MyModel.option("312", "Greece", "", ""));
        l.add(new MyModel.option("313", "Greenland", "", ""));
        l.add(new MyModel.option("314", "Grenada", "", ""));
        l.add(new MyModel.option("315", "Guadeloupe", "", ""));
        l.add(new MyModel.option("316", "Guam", "", ""));
        l.add(new MyModel.option("82", "Guam", "", ""));
        l.add(new MyModel.option("317", "Guatemala", "", ""));
        l.add(new MyModel.option("318", "Guinea", "", ""));
        l.add(new MyModel.option("319", "Guinea-Bissau", "", ""));
        l.add(new MyModel.option("320", "Guyana", "", ""));
        l.add(new MyModel.option("321", "Haiti", "", ""));
        l.add(new MyModel.option("322", "Heard Island and Mcdonald Islands", "", ""));
        l.add(new MyModel.option("323", "Honduras", "", ""));
        l.add(new MyModel.option("324", "Hong Kong", "", ""));
        l.add(new MyModel.option("325", "Hungary", "", ""));
        l.add(new MyModel.option("326", "Iceland", "", ""));
        l.add(new MyModel.option("327", "India", "", ""));
        l.add(new MyModel.option("328", "Indonesia", "", ""));
        l.add(new MyModel.option("329", "Iran, Islamic Republic of", "", ""));
        l.add(new MyModel.option("330", "Iraq", "", ""));
        l.add(new MyModel.option("331", "Ireland", "", ""));
        l.add(new MyModel.option("332", "Israel", "", ""));
        l.add(new MyModel.option("333", "Italy", "", ""));
        l.add(new MyModel.option("334", "Jamaica", "", ""));
        l.add(new MyModel.option("335", "Japan", "", ""));
        l.add(new MyModel.option("336", "Jordan", "", ""));
        l.add(new MyModel.option("337", "Kazakhstan", "", ""));
        l.add(new MyModel.option("338", "Kenya", "", ""));
        l.add(new MyModel.option("339", "Kiribati", "", ""));
        l.add(new MyModel.option("340", "Korea, Democratic People's Republic of", "", ""));
        l.add(new MyModel.option("341", "Korea, Republic of", "", ""));
        l.add(new MyModel.option("342", "Kuwait", "", ""));
        l.add(new MyModel.option("343", "Kyrgyzstan", "", ""));
        l.add(new MyModel.option("344", "Lao People's Democratic Republic", "", ""));
        l.add(new MyModel.option("345", "Latvia", "", ""));
        l.add(new MyModel.option("346", "Lebanon", "", ""));
        l.add(new MyModel.option("347", "Lesotho", "", ""));
        l.add(new MyModel.option("348", "Liberia", "", ""));
        l.add(new MyModel.option("349", "Libyan Arab Jamahiriya", "", ""));
        l.add(new MyModel.option("350", "Liechtenstein", "", ""));
        l.add(new MyModel.option("351", "Lithuania", "", ""));
        l.add(new MyModel.option("352", "Luxembourg", "", ""));
        l.add(new MyModel.option("353", "Macao", "", ""));
        l.add(new MyModel.option("465", "Macau", "", ""));
        l.add(new MyModel.option("354", "Macedonia, the Former Yugoslav Republic of", "", ""));
        l.add(new MyModel.option("355", "Madagascar", "", ""));
        l.add(new MyModel.option("356", "Malawi", "", ""));
        l.add(new MyModel.option("357", "Malaysia", "", ""));
        l.add(new MyModel.option("358", "Maldives", "", ""));
        l.add(new MyModel.option("359", "Mali", "", ""));
        l.add(new MyModel.option("360", "Malta", "", ""));
        l.add(new MyModel.option("361", "Marshall Islands", "", ""));
        l.add(new MyModel.option("362", "Martinique", "", ""));
        l.add(new MyModel.option("363", "Mauritania", "", ""));
        l.add(new MyModel.option("364", "Mauritius", "", ""));
        l.add(new MyModel.option("365", "Mayotte", "", ""));
        l.add(new MyModel.option("366", "Mexico", "", ""));
        l.add(new MyModel.option("132", "Micronesia, Federated States of", "", ""));
        l.add(new MyModel.option("368", "Moldova, Republic of", "", ""));
        l.add(new MyModel.option("369", "Monaco", "", ""));
        l.add(new MyModel.option("370", "Mongolia", "", ""));
        l.add(new MyModel.option("466", "Montenegro", "", ""));
        l.add(new MyModel.option("371", "Montserrat", "", ""));
        l.add(new MyModel.option("372", "Morocco", "", ""));
        l.add(new MyModel.option("373", "Mozambique", "", ""));
        l.add(new MyModel.option("374", "Myanmar", "", ""));
        l.add(new MyModel.option("375", "Namibia", "", ""));
        l.add(new MyModel.option("376", "Nauru", "", ""));
        l.add(new MyModel.option("377", "Nepal", "", ""));
        l.add(new MyModel.option("378", "Netherlands", "", ""));
        l.add(new MyModel.option("379", "New Caledonia", "", ""));
        l.add(new MyModel.option("380", "New Zealand", "", ""));
        l.add(new MyModel.option("381", "Nicaragua", "", ""));
        l.add(new MyModel.option("382", "Niger", "", ""));
        l.add(new MyModel.option("383", "Nigeria", "", ""));
        l.add(new MyModel.option("384", "Niue", "", ""));
        l.add(new MyModel.option("385", "Norfolk Island", "", ""));
        l.add(new MyModel.option("151", "Northern Mariana Islands", "", ""));
        l.add(new MyModel.option("387", "Norway", "", ""));
        l.add(new MyModel.option("388", "Oman", "", ""));
        l.add(new MyModel.option("389", "Pakistan", "", ""));
        l.add(new MyModel.option("390", "Palau", "", ""));
        l.add(new MyModel.option("391", "Palestinian Territory, Occupied", "", ""));
        l.add(new MyModel.option("392", "Panama", "", ""));
        l.add(new MyModel.option("393", "Papua New Guinea", "", ""));
        l.add(new MyModel.option("394", "Paraguay", "", ""));
        l.add(new MyModel.option("395", "Peru", "", ""));
        l.add(new MyModel.option("396", "Philippines", "", ""));
        l.add(new MyModel.option("397", "Pitcairn", "", ""));
        l.add(new MyModel.option("398", "Poland", "", ""));
        l.add(new MyModel.option("399", "Portugal", "", ""));
        l.add(new MyModel.option("165", "Puerto Rico", "", ""));
        l.add(new MyModel.option("401", "Qatar", "", ""));
        l.add(new MyModel.option("278", "Republic of Congo", "", ""));
        l.add(new MyModel.option("402", "Romania", "", ""));
        l.add(new MyModel.option("403", "Russian Federation", "", ""));
        l.add(new MyModel.option("404", "Rwanda", "", ""));
        l.add(new MyModel.option("405", "Saint Helena", "", ""));
        l.add(new MyModel.option("406", "Saint Kitts and Nevis", "", ""));
        l.add(new MyModel.option("407", "Saint Lucia", "", ""));
        l.add(new MyModel.option("408", "Saint Pierre and Miquelon", "", ""));
        l.add(new MyModel.option("409", "Saint Vincent and the Grenadines", "", ""));
        l.add(new MyModel.option("410", "Samoa", "", ""));
        l.add(new MyModel.option("411", "San Marino", "", ""));
        l.add(new MyModel.option("412", "Sao Tome and Principe", "", ""));
        l.add(new MyModel.option("413", "Saudi Arabia", "", ""));
        l.add(new MyModel.option("414", "Senegal", "", ""));
        l.add(new MyModel.option("415", "Serbia", "", ""));
        l.add(new MyModel.option("416", "Seychelles", "", ""));
        l.add(new MyModel.option("417", "Sierra Leone", "", ""));
        l.add(new MyModel.option("418", "Singapore", "", ""));
        l.add(new MyModel.option("419", "Slovakia", "", ""));
        l.add(new MyModel.option("420", "Slovenia", "", ""));
        l.add(new MyModel.option("421", "Solomon Islands", "", ""));
        l.add(new MyModel.option("422", "Somalia", "", ""));
        l.add(new MyModel.option("188", "South Africa", "", ""));
        l.add(new MyModel.option("424", "South Georgia and the South Sandwich Islands", "", ""));
        l.add(new MyModel.option("425", "Spain", "", ""));
        l.add(new MyModel.option("426", "Sri Lanka", "", ""));
        l.add(new MyModel.option("427", "Sudan", "", ""));
        l.add(new MyModel.option("428", "Suriname", "", ""));
        l.add(new MyModel.option("429", "Svalbard and Jan Mayen", "", ""));
        l.add(new MyModel.option("431", "Sweden", "", ""));
        l.add(new MyModel.option("432", "Switzerland", "", ""));
        l.add(new MyModel.option("433", "Syrian Arab Republic", "", ""));
        l.add(new MyModel.option("434", "Taiwan, Province of China", "", ""));
        l.add(new MyModel.option("435", "Tajikistan", "", ""));
        l.add(new MyModel.option("436", "Tanzania", "", ""));
        l.add(new MyModel.option("437", "Thailand", "", ""));
        l.add(new MyModel.option("438", "Timor-Leste", "", ""));
        l.add(new MyModel.option("439", "Togo", "", ""));
        l.add(new MyModel.option("440", "Tokelau", "", ""));
        l.add(new MyModel.option("441", "Tonga", "", ""));
        l.add(new MyModel.option("442", "Trinidad and Tobago", "", ""));
        l.add(new MyModel.option("443", "Tunisia", "", ""));
        l.add(new MyModel.option("444", "Turkey", "", ""));
        l.add(new MyModel.option("445", "Turkmenistan", "", ""));
        l.add(new MyModel.option("210", "Turks and Caicos Islands", "", ""));
        l.add(new MyModel.option("447", "Tuvalu", "", ""));
        l.add(new MyModel.option("448", "Uganda", "", ""));
        l.add(new MyModel.option("449", "Ukraine", "", ""));
        l.add(new MyModel.option("450", "United Arab Emirates", "", ""));
        l.add(new MyModel.option("451", "United Kingdom", "", ""));
        l.add(new MyModel.option("216", "United States", "", ""));
        l.add(new MyModel.option("453", "United States Minor Outlying Islands", "", ""));
        l.add(new MyModel.option("454", "Uruguay", "", ""));
        l.add(new MyModel.option("455", "Uzbekistan", "", ""));
        l.add(new MyModel.option("456", "Vanuatu", "", ""));
        l.add(new MyModel.option("457", "Venezuela", "", ""));
        l.add(new MyModel.option("458", "Viet Nam", "", ""));
        l.add(new MyModel.option("222", "Virgin Islands, British", "", ""));
        l.add(new MyModel.option("223", "Virgin Islands, U.s.", "", ""));
        l.add(new MyModel.option("461", "Wallis and Futuna", "", ""));
        l.add(new MyModel.option("462", "Western Sahara", "", ""));
        l.add(new MyModel.option("463", "Yemen", "", ""));
        l.add(new MyModel.option("1", "Zambia", "", ""));
        l.add(new MyModel.option("464", "Zimbabwe", "", ""));
        return l;
    }


}


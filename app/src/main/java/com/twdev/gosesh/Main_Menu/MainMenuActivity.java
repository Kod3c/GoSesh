package com.twdev.gosesh.Main_Menu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.View;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.twdev.gosesh.CodeClasses.ApiRequest;
import com.twdev.gosesh.CodeClasses.Callback;
import com.twdev.gosesh.CodeClasses.ContextWrapper;
import com.twdev.gosesh.CodeClasses.Functions;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.CodeClasses.VersionChecker;
import com.twdev.gosesh.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainMenuActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private MainMenuFragment mainMenuFragment;
    long mBackPressed;


    public static SharedPreferences sharedPreferences;
    public static String user_id;
    public static String user_name;
    public static String user_pic;
    public static String birthday;
    public static String token;


    DatabaseReference rootref;

    BillingProcessor billingProcessor;

    public static boolean purduct_purchase=false;


    public static String action_type="none";
    public static String receiverid="none";
    public static String title="none";
    public static String Receiver_pic="none";

    public  static  MainMenuActivity mainMenuActivity;

    Snackbar snackbar;


    @Override
    protected void attachBaseContext(Context newBase) {

        String[] language_array = newBase.getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);
        sharedPreferences = newBase.getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        String language = sharedPreferences.getString(Variables.selected_language, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && language_code.contains(language)) {
            Locale newLocale = new Locale(language);
            Context context = ContextWrapper.wrap(newBase, newLocale);
            super.attachBaseContext(context);
        }
        else {
            super.attachBaseContext(newBase);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            set_language_local();
        }

        setContentView(R.layout.activity_main_menu);

        mainMenuActivity=this;

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Variables.uid, "null");
        user_name = sharedPreferences.getString(Variables.f_name, "") + " " +
                sharedPreferences.getString(Variables.l_name, "");
        birthday=sharedPreferences.getString(Variables.birth_day,"");
        user_pic=sharedPreferences.getString(Variables.u_pic,"null");

        token=FirebaseInstanceId.getInstance().getToken();
        if(token==null || (token.equals("")||token.equals("null")))
        token=sharedPreferences.getString(Variables.device_token,"null");


        rootref= FirebaseDatabase.getInstance().getReference();


        if(getIntent().hasExtra("action_type")){
            action_type=getIntent().getExtras().getString("action_type");
            receiverid=getIntent().getExtras().getString("receiverid");
            title=getIntent().getExtras().getString("title");
            Receiver_pic=getIntent().getExtras().getString("icon");
        }


        // we will get the user billing status in local if it not present inn local then we will intialize the billing

          purduct_purchase=sharedPreferences.getBoolean(Variables.ispuduct_puchase,false);

        // check user if subscript or not both status we will save
            billingProcessor = new BillingProcessor(this, Variables.licencekey, this);
            billingProcessor.initialize();


        // get version of currently running app
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Variables.versionname=packageInfo.versionName;


        try {
            if (savedInstanceState == null) {
                initScreen();

            } else {
                mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().getFragments().get(0);
            }
        }catch (Exception e){}



        Functions.RegisterConnectivity(this, new Callback() {
            @Override
            public void Responce (String response) {
                if(response.equalsIgnoreCase("disconnected")) {
                    snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                }else {

                    if(snackbar!=null)
                        snackbar.dismiss();
                }
            }
        });


    }


    private void initScreen() {
        mainMenuFragment = new MainMenuFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuFragment)
                .commit();

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(MainMenuActivity.this);
            }
        });
    }


    // onstart we will save the latest token into the firebase
    @Override
    protected void onStart() {
        super.onStart();

        rootref.child("Users").child(user_id).child("token").setValue(token);

    }




    @Override
    protected void onResume() {
        super.onResume();
        Check_version();
    }


    int count=0;
    @Override
    public void onBackPressed() {
        if (!mainMenuFragment.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                   mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();

            }
        }

    }


    public void set_language_local(){
        String [] language_array=getResources().getStringArray(R.array.language_code);
        List <String> language_code= Arrays.asList(language_array);

        String language=sharedPreferences.getString(Variables.selected_language,"");


        if(language_code.contains(language)) {
            Locale myLocale = new Locale(language);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = new Configuration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            onConfigurationChanged(conf);

        }



    }



    // below all method is belong to get the info that user is subscribe our app or not
    // keep in mind it is a listener so we will close the listener after checking in onBillingInitialized method
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
        purduct_purchase=true;
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

        if(billingProcessor.loadOwnedPurchasesFromGoogle()){
            if((billingProcessor.isSubscribed(Variables.product_ID) ||
                    billingProcessor.isSubscribed(Variables.product_ID2)) || billingProcessor.isSubscribed(Variables.product_ID3)){

                sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
                purduct_purchase=true;
                billingProcessor.release();
                Call_Api_For_update_purchase("1");

            }else {

                sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,false).commit();
                purduct_purchase=false;
                Call_Api_For_update_purchase("0");

            }



    }
}

    @Override
    protected void onDestroy() {

        if (billingProcessor != null) {
            billingProcessor.release();
        }


        Functions.unRegisterConnectivity(this);
        super.onDestroy();


    }


    // this method will get the version of app from play store and complate it with our present app version
    // and show the update message to update the application
    public void Check_version(){
        VersionChecker versionChecker = new VersionChecker(this);
        versionChecker.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }



    private void Call_Api_For_update_purchase(String purchase_value) {

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("purchased",purchase_value);

        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(this, Variables.update_purchase_Status, parameters, null);


    }

}

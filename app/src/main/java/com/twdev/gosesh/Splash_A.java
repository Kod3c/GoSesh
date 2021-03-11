package com.twdev.gosesh;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.twdev.gosesh.Accounts.Enable_location_A;
import com.twdev.gosesh.Accounts.Login_A;
import com.twdev.gosesh.CodeClasses.ContextWrapper;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.Main_Menu.MainMenuActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Splash_A extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    SharedPreferences sharedPreferences;

    Handler max_handler;
    Runnable max_runable;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            set_language_local();
        }


        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

            // here we check the user is already login or not
        new Handler().postDelayed(new Runnable() {
                public void run() {

                    if (sharedPreferences.getBoolean(Variables.islogin, false)) {
                        // if user is already login then we get the current location of user
                        if(getIntent().hasExtra("action_type")){
                            Intent intent= new Intent(Splash_A.this, MainMenuActivity.class);
                            String action_type=getIntent().getExtras().getString("action_type");
                            String receiverid=getIntent().getExtras().getString("senderid");
                            String title=getIntent().getExtras().getString("title");
                            String icon=getIntent().getExtras().getString("icon");

                            intent.putExtra("icon",icon);
                            intent.putExtra("action_type",action_type);
                            intent.putExtra("receiverid",receiverid);
                            intent.putExtra("title",title);


                            startActivity(intent);
                            finish();
                        }
                        else
                        GPSStatus();

                    } else {

                        // else we will move the user to login screen
                        startActivity(new Intent(Splash_A.this, Login_A.class));
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        finish();

                    }
                }
            }, 2000);

        Get_screen_size();


        max_handler=new Handler();
        max_runable=new Runnable() {
            @Override
            public void run() {

                if (sharedPreferences.getString(Variables.current_Lat, "").equals("") || sharedPreferences.getString(Variables.current_Lon, "").equals("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Variables.current_Lat, Variables.default_lat);
                    editor.putString(Variables.current_Lon, Variables.default_lon);
                    editor.commit();
                }

                startActivity(new Intent(Splash_A.this, MainMenuActivity.class));
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
            }
        };
        max_handler.postDelayed(max_runable,15000);
    }


    public void set_language_local(){
        String [] language_array=getResources().getStringArray(R.array.language_code);
        List<String> language_code= Arrays.asList(language_array);

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


    // get the Gps status to check that either a mobile gps is on or off
    public void GPSStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus)
        {
            enable_location();

        }else {

            // if on then get the location of the user and save the location into the local database

            GetCurrentlocation();
        }
    }


    // if the Gps is successfully on then we will on the again check the Gps status
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            GPSStatus();
        }
    }



    public void Get_screen_size(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Variables.screen_height = displayMetrics.heightPixels;
        Variables.screen_width = displayMetrics.widthPixels;
    }



    // if user does not permitt the app to get the location then we will go to the enable location screen to enable the location permission
    private void enable_location() {
        startActivity(new Intent(this, Enable_location_A.class));
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finishAffinity();
    }






    private FusedLocationProviderClient mFusedLocationClient;

    private void GetCurrentlocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here if user did not give the permission of location then we move user to enable location screen
            enable_location();
            return;
        }

        buildGoogleApiClient();
        createLocationRequest();

    }


    public void Go_Next(Location location){

        if (location != null) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Variables.current_Lat, "" + location.getLatitude());
            editor.putString(Variables.current_Lon, "" + location.getLongitude());
            editor.commit();
            startActivity(new Intent(Splash_A.this, MainMenuActivity.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();

        } else {
            // else we will use the basic location

            if (sharedPreferences.getString(Variables.current_Lat, "").equals("") || sharedPreferences.getString(Variables.current_Lon, "").equals("")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.current_Lat, Variables.default_lat);
                editor.putString(Variables.current_Lon, Variables.default_lon);
                editor.commit();
            }
            startActivity(new Intent(Splash_A.this, MainMenuActivity.class));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            finish();
        }
    }



    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 3000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 0;
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    LocationCallback locationCallback;
    protected void startLocationUpdates() {
        mGoogleApiClient.connect();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        Go_Next(location);
                        stopLocationUpdates();

                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback
                , Looper.myLooper());

    }


    protected void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onDestroy() {
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }

        if(max_handler!=null && max_runable!=null){
            max_handler.removeCallbacks(max_runable);
        }

        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnected(Bundle arg0) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }




}

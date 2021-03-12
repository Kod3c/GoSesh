package com.twdev.gosesh.CodeClasses;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AQEEL on 10/23/2018.
 */

public class Variables {


    public static String pref_name="pref_name";
    public static String f_name="f_name";
    public static String l_name="l_name";
    public static String birth_day="birth_day";
    public static String gender="gender";
    public static String uid="uid";
    public static String u_pic="u_pic";
    public static String islogin="islogin";

    public static String current_Lat ="current_Lat";
    public static String current_Lon ="current_Lon";

    public static String seleted_Lat ="seleted_Lat";
    public static String selected_Lon ="selected_Lon";
    public static String is_seleted_location_selected ="is_seleted_location_selected";

    public static String selected_location_string ="selected_location_string";


    public static String device_token="device_token";
    public static String ispuduct_puchase="ispuduct_puchase";
    public static String user_like_limit="user_like_limit";
    public static String user_superlike_limit="user_superlike_limit";

    public static String Boost_On_Time="Boost_On_Time";
    public static int Boost_Time=1800000;

    public static String video_calling_used_time="video_calling_used_time";
    public static String voice_calling_used_time="voice_calling_used_time";


    public static String versionname="1.0";

    public static boolean is_reload_users =false;
    public static String show_me="show_me";
    public static String max_distance="max_distance";
    public static String min_age="min_age";
    public static String max_age="max_age";
    public static String show_me_on_binder="show_me_on_tinder";
    public static String hide_age="hide_age";
    public static String hide_Distance="Hide_Distance";
    public static String selected_language="selected_language";





    public static final boolean calling_limit=false;
    public static final int max_video_calling_time=60000;
    public static final int max_voice_calling_time=60000;


    public static final int default_distance=10000;
    public static final int min_default_age =18;
    public static final int max_default_age =75;



    public static final int permission_camera_code=786;
    public static final int permission_write_data=788;
    public static final int permission_Read_data=789;
    public static final int permission_Recording_audio=790;
    public static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 791;

    public static int screen_height=0;
    public static int screen_width=0;



    public static String default_lat ="40.6976701";
    public static String default_lon ="-74.2598737";



    public static String tag="binder_";

    public static String gif_firstpart="https://media.giphy.com/media/";
    public static String gif_secondpart="/100w.gif";

    public static String gif_firstpart_chat="https://media.giphy.com/media/";
    public static String gif_secondpart_chat="/200w.gif";



    public static String gif_api_key1="61gYDkPMZdjHo4giPudhQxsD3AyeqN7F";


    // Bottom two variable Related with in App Subscription
    //First step get licencekey
    public static String licencekey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwUzfMx+oV2HQkvSDMz3Hsl2DfBMjdIjEoalfn/zmFxPzhz0lrrGwy4+p9y8gc0j0pkuMKZmyK42JuibAapof6rAXivtseW3rk1+8HgfBSzepRZh7HhDin0x5UDekd33+QrAbuDwEzCEa4+xmuFKGCDSqnoLGDmVBVCmtop6bamUwn1h1JcNg6M79CdeHEw5ML6IhnEk55gI+wOldyEGdsf/EQBhzjGAl4Seso6Eq59f3sIsT0HYMf+JbC6CySKFa4w6zthJTWPzZOaAK7pJYrN5ODSrnSIOG+zxn15VAGury4wFIso7eU78g+GwO+7HmTJc41SF6lNzwbRz+OMwtsQIDAQAB";

    //create the Product id or in app subcription id
    public static String product_ID="com.twdev.gosesh.pro";
    public static String product_ID2="com.twdev.gosesh.pro";
    public static String product_ID3="com.twdev.gosesh.pro";



    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);



    public final static int Select_image_from_gallry_code=3;


   
     public static String domain="https://app.growweedoregon.com/API/index.php?p=";
     public final static String image_base_url="https://app.growweedoregon.com/API/";

    // public static final String TWILIO_ACCESS_TOKEN_SERVER = "twilio_access_token_here";
    public static final String TWILIO_ACCESS_TOKEN_SERVER ="https://flavescent-sheep-3624.twil.io/video-Token";


    public final static String privacy_policy="https://termsfeed.com/privacy-policy/";

    public final static String SignUp=domain+"signup";

    public final static String Edit_profile=domain+"edit_profile";

    public final static String getUserInfo=domain+"getUserInfo";

    public final static String uploadImages=domain+"uploadImages";

    public final static String deleteImages=domain+"deleteImages";

    public final static String userNearByMe=domain+"userNearByMe";

    public final static String flat_user=domain+"flat_user";

    public final static String myMatch=domain+"myMatch";

    public final static String firstchat=domain+"firstchat";

    public final static String mylikies=domain+"mylikies";

    public final static String unMatch=domain+"unMatch";

    public final static String show_or_hide_profile=domain+"show_or_hide_profile";

    public final static String update_purchase_Status=domain+"update_purchase_Status";

    public final static String deleteAccount=domain+"deleteAccount";

    public final static String sendPushNotification=domain+"sendPushNotification";

    public final static String boostProfile=domain+"boostProfile";



}

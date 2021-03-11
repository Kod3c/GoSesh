package com.twdev.gosesh.Settings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.appyvet.materialrangebar.RangeBar;
import com.twdev.gosesh.Accounts.Login_A;
import com.twdev.gosesh.CodeClasses.ApiRequest;
import com.twdev.gosesh.CodeClasses.Callback;
import com.twdev.gosesh.CodeClasses.Functions;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.GoogleMap.MapsActivity;
import com.twdev.gosesh.Main_Menu.MainMenuActivity;
import com.twdev.gosesh.R;
import com.twdev.gosesh.Splash_A;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.zhouyou.view.seekbar.SignSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */



// All the setting values will be used when we will get the Nearby user then we pass
    // that setting value as a parameter to the api


public class Setting_F extends Fragment implements View.OnClickListener{


    SwitchCompat current_loction_switch,selected_location_switch;

    SwitchCompat men_switch,women_switch,show_me_on_binder,hide_age,hide_distance;


    SignSeekBar distance_bar;

    RangeBar age_seekbar;
    TextView age_range_txt,distance_txt;


    View view;
    Context context;

    ExpandableRelativeLayout expandable_layout;

    TextView new_location_txt,selected_language_txt;

    public Setting_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_setting, container, false);
        context=getContext();






        expandable_layout=view.findViewById(R.id.expandable_layout);
        view.findViewById(R.id.one_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(expandable_layout.isExpanded()){

                    expandable_layout.collapse();
                }
                else
                expandable_layout.expand();

            }
        });


        current_loction_switch=view.findViewById(R.id.current_loction_switch);
        selected_location_switch=view.findViewById(R.id.selected_location_switch);

        if(MainMenuActivity.sharedPreferences.getBoolean(Variables.is_seleted_location_selected,false)){
            selected_location_switch.setChecked(true);
        }else {
            current_loction_switch.setChecked(true);
        }



        current_loction_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Variables.is_reload_users=true;

                if(isChecked){
                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.is_seleted_location_selected,false).commit();
                    selected_location_switch.setChecked(false);
                }else {

                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.is_seleted_location_selected,true).commit();
                    selected_location_switch.setChecked(true);
                }

            }
        });

        selected_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Variables.is_reload_users=true;

                if(isChecked){
                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.is_seleted_location_selected,true).commit();
                    current_loction_switch.setChecked(false);
                }else {
                    MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.is_seleted_location_selected,false).commit();
                    current_loction_switch.setChecked(true);
                }


            }
        });


        new_location_txt=view.findViewById(R.id.new_location_txt);
        new_location_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              Openlocation_picker();

            }
        });


        if(!MainMenuActivity.sharedPreferences.getString(Variables.selected_location_string,"").equals("")){
            new_location_txt.setText(MainMenuActivity.sharedPreferences.getString(Variables.selected_location_string,""));
        }else {
            selected_location_switch.setClickable(false);
            current_loction_switch.setClickable(false);
        }




        men_switch=view.findViewById(R.id.men_switch);
        women_switch=view.findViewById(R.id.women_switch);

        if(MainMenuActivity.sharedPreferences.getString(Variables.show_me,"all").equals("all")){
            men_switch.setChecked(true);
            women_switch.setChecked(true);
        }
        else if(MainMenuActivity.sharedPreferences.getString(Variables.show_me,"all").equals("male")){
            men_switch.setChecked(true);
        }
        else {
            women_switch.setChecked(true);
        }



        men_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Variables.is_reload_users =true;

                if(!isChecked && !women_switch.isChecked()){
                    women_switch.setChecked(true);
                }else {
                    set_Show_me_data();
                }
            }
        });


        women_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Variables.is_reload_users =true;

                if(!isChecked && !men_switch.isChecked()){
                    men_switch.setChecked(true);
                }else {
                    set_Show_me_data();
                }
            }
        });





        distance_bar=view.findViewById(R.id.distance_bar);
        distance_txt=view.findViewById(R.id.distance_txt);
        distance_bar.setProgress(MainMenuActivity.sharedPreferences.getInt(Variables.max_distance,Variables.default_distance));
        distance_txt.setText(MainMenuActivity.sharedPreferences.getInt(Variables.max_distance,Variables.default_distance)+" "+context.getResources().getString(R.string.miles));

        distance_bar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                distance_txt.setText(progress+" "+context.getResources().getString(R.string.miles));
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                Variables.is_reload_users =true;
                MainMenuActivity.sharedPreferences.edit().putInt(Variables.max_distance,progress).commit();
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {

                }
        });



       // this is the age seek bar. when we change the progress of seek bar it will be locally save
        age_seekbar=view.findViewById(R.id.age_seekbar);
        age_range_txt=view.findViewById(R.id.age_range_txt);
        age_range_txt.setText(MainMenuActivity.sharedPreferences.getInt(Variables.min_age,Variables.min_default_age)+" - "+
                MainMenuActivity.sharedPreferences.getInt(Variables.max_age,Variables.max_default_age)+" "+context.getResources().getString(R.string.years));
        age_seekbar.setRangePinsByValue(MainMenuActivity.sharedPreferences.getInt(Variables.min_age,Variables.min_default_age),
                MainMenuActivity.sharedPreferences.getInt(Variables.max_age,Variables.max_default_age));
        age_seekbar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                Variables.is_reload_users =true;

                SharedPreferences.Editor editor=MainMenuActivity.sharedPreferences.edit();
                editor.putInt(Variables.min_age,Integer.parseInt(leftPinValue));
                editor.putInt(Variables.max_age,Integer.parseInt(rightPinValue));
                editor.commit();

                age_range_txt.setText(leftPinValue+" - "+rightPinValue+" "+context.getResources().getString(R.string.years));


            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }
        });






        show_me_on_binder=view.findViewById(R.id.show_me_on_binder);
        show_me_on_binder.setChecked(MainMenuActivity.sharedPreferences.getBoolean(Variables.show_me_on_binder,true));
        show_me_on_binder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.show_me_on_binder,isChecked).commit();
                Variables.is_reload_users =true;
                Call_Api_For_setting();
            }
        });


        hide_age=view.findViewById(R.id.hide_age);
        hide_age.setChecked(MainMenuActivity.sharedPreferences.getBoolean(Variables.hide_age,false));
        hide_age.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.hide_age,isChecked).commit();

                Call_Api_For_setting();
            }
        });

        hide_distance=view.findViewById(R.id.hide_distance);
        hide_distance.setChecked(MainMenuActivity.sharedPreferences.getBoolean(Variables.hide_Distance,false));
        hide_distance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.hide_Distance,isChecked).commit();
                Call_Api_For_setting();
            }
        });


        selected_language_txt=view.findViewById(R.id.selected_language_txt);

        List<String> language_names= Arrays.asList(getResources().getStringArray(R.array.language_names_for_show));
        List <String> language_code= Arrays.asList(getResources().getStringArray(R.array.language_code));

        String language= Locale.getDefault().getLanguage();
        if(MainMenuActivity.sharedPreferences.getString(Variables.selected_language,null)!=null)
            language=MainMenuActivity.sharedPreferences.getString(Variables.selected_language,context.getResources().getString(R.string.english));

        if(language_code.contains(language)){
            selected_language_txt.setText(language_names.get(language_code.indexOf(language)));
        }
        else {
            selected_language_txt.setText("english");
        }


        view.findViewById(R.id.back_btn).setOnClickListener(this::onClick);
        view.findViewById(R.id.langage_layout).setOnClickListener(this::onClick);
        view.findViewById(R.id.privacy_policy_txt).setOnClickListener(this::onClick);
        view.findViewById(R.id.logout_btn).setOnClickListener(this::onClick);
        view.findViewById(R.id.delete_account_btn).setOnClickListener(this::onClick);



        return view;

    }


    public void Open_Language_dialog(){
        List<String> language_names=  Arrays.asList(getResources().getStringArray(R.array.language_names_for_show));
        List <String> language_code=  Arrays.asList(getResources().getStringArray(R.array.language_code));

        final CharSequence[] options = getResources().getStringArray(R.array.language_names_for_show);
        Functions.Show_Options(getContext(), options, new Callback() {
            @Override
            public void Responce(String resp) {


                MainMenuActivity.sharedPreferences.edit().putString(Variables.selected_language,language_code.get(language_names.indexOf(resp))).commit();

                getActivity().finish();
                startActivity(new Intent(getActivity(), Splash_A.class));


            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.back_btn:
                getActivity().onBackPressed();
                break;

            case R.id.langage_layout:
                Open_Language_dialog();
                break;

            case R.id.delete_account_btn:
                Show_delete_account_alert();
                break;

            case R.id.logout_btn:
                Logout_User();
                break;

            case R.id.privacy_policy_txt:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Variables.privacy_policy));
                startActivity(browserIntent);
                break;
        }
    }



    // below two methods are related with "save the value in database" that he is want to show
    // as a public or keep it private
    public void set_Show_me_data(){

        if(men_switch.isChecked() && women_switch.isChecked()){
            MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"all").commit();

        }else if(!men_switch.isChecked() && women_switch.isChecked()){
            MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"female").commit();
        }else if(men_switch.isChecked() && !women_switch.isChecked()){
            MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"male").commit();
        }
    }

    private void Call_Api_For_setting() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

            if(show_me_on_binder.isChecked())
            parameters.put("status","1");
            else
                parameters.put("status","0");

            if(hide_age.isChecked())
                parameters.put("hide_age","1");
            else
                parameters.put("hide_age","0");

            if(hide_distance.isChecked())
                parameters.put("hide_location","1");
            else
                parameters.put("hide_location","0");



        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context,Variables.show_or_hide_profile, parameters,null);

    }


    public void Show_delete_account_alert(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context,R.style.DialogStyle);
        alert.setTitle(context.getResources().getString(R.string.are_you_sure))
                .setMessage(context.getResources().getString(R.string.delete_account_description))
                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call_api_to_Delete_Account();
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        alert.setCancelable(true);
        alert.show();
    }



    // below two method is used get the user pictures and about text from our server
    private void Call_api_to_Delete_Account() {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.Show_loader(context,false,false);

        ApiRequest.Call_Api(context, Variables.deleteAccount, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");
                    if(code.equals("200")){

                        Logout_User();

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        });

    }




    public  void Logout_User(){
        SharedPreferences.Editor editor=MainMenuActivity.sharedPreferences.edit();

        editor.putBoolean(Variables.islogin,false);
        editor.putString(Variables.f_name,"");
        editor.putString(Variables.l_name,"");
        editor.putString(Variables.birth_day,"");
        editor.putString(Variables.u_pic,"");

        editor.commit();
        startActivity(new Intent(getActivity(), Login_A.class));
        getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        getActivity().finish();
    }


    public void Openlocation_picker(){

        startActivityForResult(new Intent(getContext(),MapsActivity.class),111);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if(resultCode == RESULT_OK) {
                String latSearch = data.getStringExtra("lat");
                String longSearch = data.getStringExtra("lng");

                String location_string = data.getStringExtra("location_string");


                new_location_txt.setText(location_string);
                selected_location_switch.setClickable(true);
                current_loction_switch.setClickable(true);

                MainMenuActivity.sharedPreferences.edit().putString(Variables.seleted_Lat,latSearch).commit();
                MainMenuActivity.sharedPreferences.edit().putString(Variables.selected_Lon,longSearch).commit();

                MainMenuActivity.sharedPreferences.edit().putString(Variables.selected_location_string,location_string).commit();

                }
        }
    }

}

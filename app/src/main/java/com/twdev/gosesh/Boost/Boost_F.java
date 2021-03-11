package com.twdev.gosesh.Boost;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twdev.gosesh.CodeClasses.ApiRequest;
import com.twdev.gosesh.CodeClasses.Callback;
import com.twdev.gosesh.CodeClasses.Functions;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.Main_Menu.MainMenuActivity;
import com.twdev.gosesh.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.twdev.gosesh.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class Boost_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;
    CircularProgressBar circularProgressBar;



    public Boost_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=getContext();

       if(!Check_IS_Boost_On()) {
            view = inflater.inflate(R.layout.fragment_boost, container, false);

            view.findViewById(R.id.boost_btn).setOnClickListener(this);

       }

       else {

           view = inflater.inflate(R.layout.fragment_boost_on, container, false);
           circularProgressBar = view.findViewById(R.id.circularProgressBar);
           view.findViewById(R.id.okay_btn).setOnClickListener(this);
           Set_Progress();
       }


        view.findViewById(R.id.transparent_layout).setOnClickListener(this);

        return view;
    }



    long time_gone;
    public boolean Check_IS_Boost_On(){

        long requesttime= Long.parseLong(MainMenuActivity.sharedPreferences.getString(Variables.Boost_On_Time,"0"));
        long currenttime=System.currentTimeMillis();

        time_gone=(currenttime-requesttime);

        if(requesttime==0){

            return false;
        }
        else if(time_gone<Variables.Boost_Time){

          return true;

        }
        else {

            return false;

        }


    }



    public void Set_Progress(){
        long requesttime= Long.parseLong(MainMenuActivity.sharedPreferences.getString(Variables.Boost_On_Time,"0"));
        long currenttime=System.currentTimeMillis();

        time_gone=(currenttime-requesttime);


        Start_Timer();
    }


    CountDownTimer timer;
    public void Start_Timer(){
        long time_left=Variables.Boost_Time-time_gone;
        timer=new CountDownTimer(time_left,1000) {
            @Override
            public void onTick(long l) {
                long millis = l;

                String time_string= Functions.convertSeconds((int) (millis/1000));
                TextView textView=view.findViewById(R.id.remaining_txt);
                textView.setText(time_string+" Remaining");

                float progress=  ((l*100)/Variables.Boost_Time);
                circularProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {

                Stop_timer();
            }
        };

        timer.start();
    }

    public void Stop_timer(){

        if(timer!=null)
            timer.cancel();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.boost_btn:
                Call_Api_For_BoostProfile();
                break;

            case R.id.transparent_layout:
                getActivity().onBackPressed();
                break;

            case R.id.okay_btn:
                getActivity().onBackPressed();
                break;

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Stop_timer();
    }

    private void Call_Api_For_BoostProfile() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("mins", "30");
            parameters.put("promoted", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context,false,false);
        ApiRequest.Call_Api(context, Variables.boostProfile, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

                Functions.cancel_loader();

                try {
                    JSONObject jsonObject=new JSONObject(resp);

                    long min = System.currentTimeMillis();

                    MainMenuActivity.sharedPreferences.edit().putString(Variables.Boost_On_Time,""+min).commit();

                    getActivity().onBackPressed();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


}

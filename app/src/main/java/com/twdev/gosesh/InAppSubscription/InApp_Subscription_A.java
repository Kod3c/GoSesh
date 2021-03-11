package com.twdev.gosesh.InAppSubscription;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.twdev.gosesh.CodeClasses.ApiRequest;
import com.twdev.gosesh.CodeClasses.Callback;
import com.twdev.gosesh.CodeClasses.Functions;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.Main_Menu.MainMenuActivity;
import com.twdev.gosesh.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.twdev.gosesh.R;
import com.twdev.gosesh.Splash_A;
import com.google.android.material.tabs.TabLayout;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class InApp_Subscription_A extends RootFragment implements BillingProcessor.IBillingHandler,View.OnClickListener {

    BillingProcessor bp;
    SharedPreferences sharedPreferences;
    View view;
    Context context;
    Button purchase_btn;

    TextView Goback;

    LinearLayout sub_layout1,sub_layout2,sub_layout3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate
        // the layout for this fragment
        view= inflater.inflate(R.layout.activity_in_app_subscription, container, false);
        context=getContext();

        // get the sharepreference
        sharedPreferences=context.getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        purchase_btn=view.findViewById(R.id.purchase_btn);
        purchase_btn.setOnClickListener(this);


        Goback=view.findViewById(R.id.Goback);
        Goback.setOnClickListener(this);

        Set_Slider();


        sub_layout1= view.findViewById(R.id.sub_layout1);
        sub_layout2= view.findViewById(R.id.sub_layout2);
        sub_layout3= view.findViewById(R.id.sub_layout3);


        Select_one(2);

        sub_layout1.setOnClickListener(this);
        sub_layout2.setOnClickListener(this);
        sub_layout3.setOnClickListener(this);

        return view;
    }


    int subscription_position;
    public void Select_one(int position){
        subscription_position=position;

        sub_layout1.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_border));
        sub_layout2.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_border));
        sub_layout3.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_border));

        if(position==1){
            sub_layout1.setBackground(context.getResources().getDrawable(R.drawable.d_round_pink_border));

        }
        else if(position==2){
            sub_layout2.setBackground(context.getResources().getDrawable(R.drawable.d_round_pink_border));

        }
        else if(position==3){
           sub_layout3.setBackground(context.getResources().getDrawable(R.drawable.d_round_pink_border));

        }


    }


    public void initlize_billing(){


        Functions.Show_loader(context,false,false);

        bp = new BillingProcessor(context, Variables.licencekey, this);
        bp.initialize();


    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        // if the user is subcripbe successfully then we will store the data in local and also call the api
        sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
        MainMenuActivity.purduct_purchase=true;
        Call_Api_For_update_purchase("1");

    }


    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        // on billing intialize we will get the data from google
        Functions.cancel_loader();

        if(bp.loadOwnedPurchasesFromGoogle()){
            // check user is already subscribe or not
        if((bp.isSubscribed(Variables.product_ID) || bp.isSubscribed(Variables.product_ID2))
                || bp.isSubscribed(Variables.product_ID3)){
            // if already subscribe then we will change the static variable and goback
            MainMenuActivity.purduct_purchase=true;
            Call_Api_For_update_purchase("1");
        }

        }
    }



    // when we click the continue btn this method will call
    public void Puchase_item() {
        boolean isAvailable = BillingProcessor.isIabServiceAvailable(getActivity());
        if(isAvailable) {

            if(subscription_position==1){
                bp.subscribe(getActivity(), Variables.product_ID);
            }else if(subscription_position==2){
                bp.subscribe(getActivity(), Variables.product_ID2);
            }
            else if(subscription_position==3){
                bp.subscribe(getActivity(), Variables.product_ID3);
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Variables.tag, "onActivity Result Code : " + resultCode);
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 300);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    initlize_billing();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 300);
        }
    }


    // on destory we will release the billing process
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }


    public void Goback() {

        startActivity(new Intent(getActivity(), Splash_A.class));
        getActivity().finish();

    }


    // when user subscribe the app then this method will call that will store the status of user
    // into the database
    private void Call_Api_For_update_purchase(String purchase_value) {

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("purchased",purchase_value);

        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context,false,false);

        ApiRequest.Call_Api(context, Variables.update_purchase_Status, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
               Functions.cancel_loader();
                try {
                    JSONObject jsonObject=new JSONObject(resp);

                    sharedPreferences.edit().putBoolean(Variables.ispuduct_puchase,true).commit();
                    MainMenuActivity.purduct_purchase=true;

                    Goback();

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    private ViewPager mPager;
    private ArrayList<Integer> ImagesArray;
    public void Set_Slider(){

        ImagesArray=new ArrayList<>();
        ImagesArray.add(0);
        ImagesArray.add(1);
        ImagesArray.add(2);
        mPager = (ViewPager) view.findViewById(R.id.image_slider_pager);

        try {
            mPager.setAdapter(new SlidingImageAdapter(getContext(),ImagesArray));
        }
        catch (NullPointerException e){
            e.getCause();
        }

        mPager.setCurrentItem(0);

        TabLayout indicator = (TabLayout) view.findViewById(R.id.indicator);
        indicator.setupWithViewPager(mPager, true);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.purchase_btn:
                Puchase_item();
                break;

            case R.id.Goback:
                getActivity().onBackPressed();
                break;

            case R.id.sub_layout1:
                Select_one(1);
                break;

            case R.id.sub_layout2:
                Select_one(2);
                break;

            case R.id.sub_layout3:
                Select_one(3);
                break;

        }
    }

}

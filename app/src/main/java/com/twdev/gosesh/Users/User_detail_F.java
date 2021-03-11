package com.twdev.gosesh.Users;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.twdev.gosesh.CodeClasses.ApiRequest;
import com.twdev.gosesh.CodeClasses.Callback;
import com.twdev.gosesh.CodeClasses.Functions;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.Main_Menu.MainMenuActivity;
import com.twdev.gosesh.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.twdev.gosesh.R;
import com.google.android.material.tabs.TabLayout;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */

// in this class we will get all the details of spacific user and show it here
public class User_detail_F extends RootFragment implements View.OnClickListener {


    View view;
    Context context;

    ImageButton  move_downbtn;

    RelativeLayout  username_layout;
    ScrollView scrollView;

    TextView username_txt, bottom_age,bottom_job_txt,bottom_school_txt,bottom_location_text,bottom_about_txt;

    TextView bottom_report_txt;



    Nearby_User_Get_Set data_item;


    ImageButton profile_menu;

    String from_where;
    public User_detail_F() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_detail_, container, false);
        context = getContext();



        Bundle bundle=getArguments();
        if(bundle!=null)
        {
         data_item= (Nearby_User_Get_Set) bundle.getSerializable("data");
            from_where=bundle.getString("from_where");
        }

        scrollView = view.findViewById(R.id.scrollView);
        username_layout = view.findViewById(R.id.username_layout);

        move_downbtn = view.findViewById(R.id.move_downbtn);
        move_downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getActivity().onBackPressed();


            }
        });



        YoYo.with(Techniques.BounceInDown)
                .duration(800)
                .playOn(move_downbtn);

        init_bottom_view();


        if(from_where!=null && from_where.equals("user_list")){
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)scrollView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            scrollView.setLayoutParams(params);
        }


        return view;

    }


    // this method will initialize all the views and set the data in that view
    public void init_bottom_view() {

        profile_menu=view.findViewById(R.id.profile_menu);
        profile_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v);
            }
        });


        username_txt = view.findViewById(R.id.username_txt);
        username_txt.setText(data_item.getFirst_name());


        bottom_age = view.findViewById(R.id.bottom_age);


        if(!data_item.getBirthday().equals(""))
        bottom_age.setText(", "+data_item.getBirthday());


        bottom_job_txt=view.findViewById(R.id.bottom_job_txt);
        if(data_item.getJob_title().equals("") & !data_item.getCompany().equals("")){

            bottom_job_txt.setText(data_item.getJob_title());

        }else if(data_item.getCompany().equals("") && !data_item.getJob_title().equals("") ){

            bottom_job_txt.setText(data_item.getJob_title());

        }
        else if(data_item.getCompany().equals("") && data_item.getJob_title().equals("") ){
            view.findViewById(R.id.job_layout).setVisibility(View.GONE);
        }
        else {
            bottom_job_txt.setText(data_item.getJob_title()+" at "+data_item.getSchool());

        }


        bottom_school_txt=view.findViewById(R.id.bottom_school_txt);
        if(data_item.getSchool().equals("")){
            view.findViewById(R.id.school_layout).setVisibility(View.GONE);
        }else {
            bottom_school_txt.setText(data_item.getSchool());
        }




        bottom_location_text=view.findViewById(R.id.bottom_location_txt);
        bottom_location_text.setText(data_item.getLocation());

        bottom_about_txt=view.findViewById(R.id.bottom_about_txt);
        if(data_item.getAbout().equals("")){
            bottom_about_txt.setVisibility(View.GONE);
        }
        bottom_about_txt.setText(data_item.getAbout());


        bottom_report_txt=view.findViewById(R.id.bottom_report_txt);
        bottom_report_txt.setText(context.getResources().getString(R.string.report)+" "+data_item.getFirst_name());
        bottom_report_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report_User_alert();
            }
        });
    }


    // when all the animation is done then we will place a data into the view
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation anim= MoveAnimation.create(MoveAnimation.UP, enter, 200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    // when animation is done then we will show the picture slide
                    // becuase animation in that case will show fulently

                    Set_Slider();
                    //fill_data();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            return anim;

        } else {
            return MoveAnimation.create(MoveAnimation.DOWN, enter, 200);
        }
    }



    private ViewPager mPager;
    public void Set_Slider(){

        mPager = (ViewPager) view.findViewById(R.id.image_slider_pager);

        try {

            mPager.setAdapter(new Images_sliding_adapter(getContext(),data_item.getImagesurl()));
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
        int id = v.getId();
        switch (id) {
        }
    }


    // this will show an alert will is show when we click to Report a User
    public void Report_User_alert(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(context,R.style.DialogStyle);
        alert.setTitle(context.getResources().getString(R.string.report))
                .setMessage(context.getResources().getString(R.string.are_you_sure_report_user))
                .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Send_report();
                    }
                });

        alert.setCancelable(true);
        alert.show();
    }


    // below two method will get all the  new user that is nearby of us  and parse the data in dataset
    // in that case which has a name of Nearby get set
    private void Send_report() {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("my_id", MainMenuActivity.user_id);
            parameters.put("fb_id", data_item.getFb_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.Show_loader(context,false,false);
        ApiRequest.Call_Api(context, Variables.flat_user, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();

                try {
                    JSONObject response = new JSONObject(resp);
                    JSONArray jsonArray=response.optJSONArray("msg");
                    Toast.makeText(context, ""+jsonArray.optJSONObject(0).optString("response"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }


    PopupWindow popup;
    private void displayPopupWindow(View anchorView) {
        popup = new PopupWindow(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.item_menu_popup_option, null);

        TextView report=layout.findViewById(R.id.report);
        popup.setContentView(layout);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
               Report_User_alert();
            }
        });


        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView,anchorView.getWidth(),anchorView.getHeight()- (Functions.convertDpToPx(context,60)));

    }


}
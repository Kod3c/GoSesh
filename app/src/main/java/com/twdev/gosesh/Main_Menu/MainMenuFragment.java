package com.twdev.gosesh.Main_Menu;


import android.content.Context;
import android.os.Bundle;

import com.twdev.gosesh.UsersLikes.User_likes_F;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.twdev.gosesh.Chat.Chat_Activity;
import com.twdev.gosesh.Inbox.Inbox_F;
import com.twdev.gosesh.Main_Menu.RelateToFragment_OnBack.OnBackPressListener;
import com.twdev.gosesh.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.twdev.gosesh.Profile.Profile_F;
import com.twdev.gosesh.R;
import com.twdev.gosesh.Users.Users_F;

import java.util.ArrayList;
import java.util.List;


public class MainMenuFragment extends RootFragment implements View.OnClickListener {


    protected Custom_ViewPager pager;

    private ViewPagerAdapter adapter;
    Context context;



    ImageButton profile_btn,star_btn,binder_btn,message_btn;
    LinearLayout binder_btn_layout,star_btn_layout;

    public MainMenuFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        context=getContext();

        pager = view.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(3);
        pager.setPagingEnabled(false);
        view.setOnClickListener(this);



        profile_btn=view.findViewById(R.id.profile_btn);
        profile_btn.setOnClickListener(this);


        binder_btn=view.findViewById(R.id.binder_btn);
        binder_btn.setOnClickListener(this);
        binder_btn_layout=view.findViewById(R.id.binder_btn_layout);
        binder_btn_layout.setOnClickListener(this);

        star_btn=view.findViewById(R.id.star_btn);
        star_btn_layout=view.findViewById(R.id.start_btn_layout);
        star_btn.setOnClickListener(this);
        star_btn_layout.setOnClickListener(this);


        message_btn=view.findViewById(R.id.message_btn);
        message_btn.setOnClickListener(this);


        return view;
    }



    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){

            case R.id.profile_btn:
                Click_Profile();
                pager.setCurrentItem(0);

                break;

            case R.id.binder_btn_layout:
                pager.setCurrentItem(1);
                Click_binder();
                break;


            case R.id.start_btn_layout:
                pager.setCurrentItem(2);
                Click_star();
                break;


            case R.id.binder_btn:
                pager.setCurrentItem(1);
                Click_binder();
                break;


            case R.id.star_btn:
                pager.setCurrentItem(2);
                Click_star();
                break;

            case R.id.message_btn:
                Click_message();
                pager.setCurrentItem(3);
                break;

        }

    }


    public void Click_Profile(){

        profile_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile_color));
        binder_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_binder_gray));
        message_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_gray));

        if(pager.getCurrentItem()==1){
            star_btn_layout.setVisibility(View.GONE);
        }
        else if(pager.getCurrentItem()==2){
            binder_btn_layout.setVisibility(View.GONE);
        }

        binder_btn_layout.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_color_shape));
        star_btn_layout.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_color_shape));
    }


    public void Click_binder(){

        profile_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile_gray));
        binder_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_binder_color));
        message_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_gray));


        binder_btn_layout.setVisibility(View.VISIBLE);
        star_btn_layout.setVisibility(View.VISIBLE);

        binder_btn_layout.setBackground(context.getResources().getDrawable(R.drawable.d_round_color_background));
        star_btn_layout.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_color_shape));
    }

    public void Click_star(){

        profile_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile_gray));
        binder_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_binder_gray));
        message_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_gray));

        binder_btn_layout.setVisibility(View.VISIBLE);
        star_btn_layout.setVisibility(View.VISIBLE);

        binder_btn_layout.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_color_shape));
        star_btn_layout.setBackground(context.getResources().getDrawable(R.drawable.d_round_color_background));
    }

    public void Click_message(){

        profile_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile_gray));
        binder_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_binder_gray));
        message_btn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_message_color));

        if(pager.getCurrentItem()==1){
            star_btn_layout.setVisibility(View.GONE);
        }else if(pager.getCurrentItem()==2){
            binder_btn_layout.setVisibility(View.GONE);
        }

        binder_btn_layout.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_color_shape));
        star_btn_layout.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_color_shape));
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Note that we are passing childFragmentManager, not FragmentManager
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);

        setupTabIcons();



    }


    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(pager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }


    private void setupTabIcons() {

        adapter.addFrag(new Profile_F());
        adapter.addFrag(new Users_F());
        adapter.addFrag(new User_likes_F(null,true));
        adapter.addFrag(new Inbox_F());

        pager.setCurrentItem(1);

        adapter.notifyDataSetChanged();

        if(MainMenuActivity.action_type.equals("message")){
            pager.setCurrentItem(3);
            chatFragment();
        }

        else if(MainMenuActivity.action_type.equals("match")){
            pager.setCurrentItem(3);
        }

        else {
            pager.setCurrentItem(1);
        }


    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();


        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);

        }

        public void replaceFrag(int pos,Fragment fragment) {
            registeredFragments.remove(pos);
            mFragmentList.remove(pos);
            mFragmentList.add(pos,fragment);

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }


    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(){
        Chat_Activity chat_activity = new Chat_Activity();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id",MainMenuActivity.user_id);
        args.putString("Receiver_Id",MainMenuActivity.receiverid);
        args.putString("name",MainMenuActivity.title);
        args.putString("picture",MainMenuActivity.Receiver_pic);
        args.putBoolean("is_match_exits",false);
        chat_activity.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
    }



}
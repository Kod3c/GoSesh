package com.twdev.gosesh.UsersLikes;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.twdev.gosesh.CodeClasses.AdapterClickListener;
import com.twdev.gosesh.CodeClasses.ApiRequest;
import com.twdev.gosesh.CodeClasses.Callback;
import com.twdev.gosesh.CodeClasses.Fragment_Callback;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.InAppSubscription.InApp_Subscription_A;
import com.twdev.gosesh.Main_Menu.MainMenuActivity;
import com.twdev.gosesh.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.twdev.gosesh.R;
import com.twdev.gosesh.Users.Nearby_User_Get_Set;
import com.twdev.gosesh.Users.User_detail_F;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class User_likes_F extends RootFragment implements View.OnClickListener{

    View view;
    Context context;

    ArrayList<Nearby_User_Get_Set> data_list;
    RecyclerView recyclerView;
    User_Like_Adapter adapter;

    String likes_count;
    TextView title_txt;
    ProgressBar progress_bar;
    DatabaseReference rootref;

    Boolean is_view_created=false;

    public User_likes_F() {
    }

    Fragment_Callback fragment_callback;
    Boolean is_from_tab=false;
    public User_likes_F(Fragment_Callback fragment_callback,Boolean is_from_tab) {
      this.fragment_callback=fragment_callback;
      this.is_from_tab=is_from_tab;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_user_list, container, false);
        context=getContext();


        rootref= FirebaseDatabase.getInstance().getReference();



        progress_bar=view.findViewById(R.id.progress_bar);

        Bundle bundle = getArguments();
        if (bundle != null) {
            likes_count = bundle.getString("like_count");
        }

        title_txt=view.findViewById(R.id.title_txt);
        title_txt.setText(likes_count);

        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragment_callback!=null){
                    fragment_callback.Responce(null);
                }
                getActivity().onBackPressed();
            }
        });


        data_list=new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));

        recyclerView.setHasFixedSize(true);

        adapter=new User_Like_Adapter(context, data_list, new AdapterClickListener() {
            @Override
            public void onItemClick(int pos, Object item, View view) {

                if(MainMenuActivity.purduct_purchase) {
                    open_user_detail((Nearby_User_Get_Set) item);
                }


            }

            @Override
            public void onLongItemClick(int pos, Object item, View view) {

            }
        });

        recyclerView.setAdapter(adapter);


        if(is_from_tab){


            view.findViewById(R.id.appbar).setVisibility(View.GONE);
            view.findViewById(R.id.toolbar).setVisibility(View.GONE);
            view.findViewById(R.id.top_layout).setVisibility(View.GONE);

            view.findViewById(R.id.cardview).
                    setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
        }

        else {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            view.findViewById(R.id.appbar).setVisibility(View.VISIBLE);
            view.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
            view.findViewById(R.id.top_layout).setVisibility(View.VISIBLE);

            GetPeople_nearby();
        }

        is_view_created=true;
        return view;
    }



    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN |ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(context, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            if( MainMenuActivity.sharedPreferences.getInt(Variables.user_like_limit,0)>0){
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
            else
               return 0;
        }
        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

            Log.d("resp",""+swipeDir);

            int position = viewHolder.getAdapterPosition();

            Nearby_User_Get_Set item=data_list.get(position);
            data_list.remove(position);


            if(swipeDir==8){
                updatedata_onrightSwipe(item);
            }else if(swipeDir==4){
                updatedata_onLeftSwipe(item);
            }

            adapter.notifyItemRemoved(position);
            adapter.notifyItemChanged(position);

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);
            Log.d("resp",""+dX);

            if(dX<0.0){
                viewHolder.itemView.findViewById(R.id.left_overlay).setVisibility(View.VISIBLE);
                viewHolder.itemView.findViewById(R.id.right_overlay).setVisibility(View.GONE);
            }else if(dX>0.0) {
                viewHolder.itemView.findViewById(R.id.left_overlay).setVisibility(View.GONE);
                viewHolder.itemView.findViewById(R.id.right_overlay).setVisibility(View.VISIBLE);

            }else {
                viewHolder.itemView.findViewById(R.id.left_overlay).setVisibility(View.GONE);
                viewHolder.itemView.findViewById(R.id.right_overlay).setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if((is_view_created && isVisibleToUser)&& is_from_tab){
            GetPeople_nearby();

            if(!MainMenuActivity.purduct_purchase){

                    view.findViewById(R.id.subscribe_txt).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.subscribe_btn).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.subscribe_btn).setOnClickListener(this);

            }else {
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);

                view.findViewById(R.id.subscribe_txt).setVisibility(View.GONE);
                view.findViewById(R.id.subscribe_btn).setVisibility(View.GONE);
            }

        }

    }

    private void GetPeople_nearby() {

        progress_bar.setVisibility(View.VISIBLE);

        String latlong="";
        if(MainMenuActivity.sharedPreferences.getBoolean(Variables.is_seleted_location_selected,false)){

            latlong=MainMenuActivity.sharedPreferences.getString(Variables.seleted_Lat,"33.738045")+", "+MainMenuActivity.sharedPreferences.getString(Variables.selected_Lon,"73.084488");
        }else {
            latlong=MainMenuActivity.sharedPreferences.getString(Variables.current_Lat,"33.738045")+", "+MainMenuActivity.sharedPreferences.getString(Variables.current_Lon,"73.084488");
        }

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("lat_long", latlong);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.mylikies, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

                progress_bar.setVisibility(View.GONE);

                Parse_user_info(resp);
            }
        });


    }


    public void Parse_user_info(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                data_list.clear();
                JSONArray msg=jsonObject.getJSONArray("msg");

                for (int i=0; i<msg.length();i++){
                    JSONObject userdata=msg.getJSONObject(i);
                    Nearby_User_Get_Set item=new Nearby_User_Get_Set();
                    item.setFb_id(userdata.optString("fb_id"));
                    item.setFirst_name(userdata.optString("first_name"));
                    item.setLast_name(userdata.optString("last_name"));
                    item.setName(userdata.optString("first_name")+" "+userdata.optString("last_name"));
                    item.setJob_title(userdata.optString("job_title"));
                    item.setCompany(userdata.optString("company"));
                    item.setSchool(userdata.optString("school"));
                    item.setBirthday(userdata.optString("birthday"));
                    item.setAbout(userdata.optString("about_me"));
                    item.setLocation(userdata.optString("distance"));
                    item.setGender(userdata.optString("gender"));
                    item.setSwipe(userdata.optString("swipe"));


                    ArrayList<String> images=new ArrayList<>();

                    images.add(userdata.optString("image1"));

                    if(!userdata.optString("image2").equals(""))
                        images.add(userdata.optString("image2"));

                    if(!userdata.optString("image3").equals(""))
                        images.add(userdata.optString("image3"));

                    if(!userdata.optString("image4").equals(""))
                        images.add(userdata.optString("image4"));

                    if(!userdata.optString("image5").equals(""))
                        images.add(userdata.optString("image5"));

                    if(!userdata.optString("image6").equals(""))
                        images.add(userdata.optString("image6"));

                    item.setImagesurl(images);

                    data_list.add(item);
                }

                if(data_list.isEmpty()){
                    view.findViewById(R.id.nodata_found_txt).setVisibility(View.VISIBLE);
                }else {
                    view.findViewById(R.id.nodata_found_txt).setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            view.findViewById(R.id.nodata_found_txt).setVisibility(View.VISIBLE);

        }


    }


    // when we swipe left , right or reverse then this method is call and update the value in firebase database
    public void updatedata_onLeftSwipe(final Nearby_User_Get_Set item){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh");
        final String formattedDate = df.format(c);


        Map mymap=new HashMap<>();
        mymap.put("match","false");
        mymap.put("type","dislike");
        mymap.put("status","0");
        mymap.put("time",formattedDate);
        mymap.put("name",item.getName());
        mymap.put("effect","true");


        Map othermap=new HashMap<>();
        othermap.put("match","false");
        othermap.put("type","dislike");
        othermap.put("status","0");
        othermap.put("time",formattedDate);
        othermap.put("name",MainMenuActivity.user_name);
        othermap.put("effect","false");

        rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).updateChildren(mymap);
        rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).updateChildren(othermap);


        SendPushNotification(item.fb_id,MainMenuActivity.user_name+" has Dislike your Profile");

    }



    public void updatedata_onrightSwipe(final Nearby_User_Get_Set item){

        MainMenuActivity.sharedPreferences.edit()
                .putInt(Variables.user_like_limit,
                        (MainMenuActivity.sharedPreferences.getInt
                                (Variables.user_like_limit,0)-1))
                .commit();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh");
        final String formattedDate = df.format(c);

        Query query=rootref.child("Match").child(item.getFb_id()).child(MainMenuActivity.user_id);
        query.keepSynced(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Map mymap=new HashMap<>();
                    mymap.put("match","true");
                    mymap.put("type","like");
                    mymap.put("status","0");
                    mymap.put("time",formattedDate);
                    mymap.put("name",item.getName());
                    mymap.put("effect","true");

                    Map othermap=new HashMap<>();
                    othermap.put("match","true");
                    othermap.put("type","like");
                    othermap.put("status","0");
                    othermap.put("time",formattedDate);
                    othermap.put("name",MainMenuActivity.user_name);
                    othermap.put("effect","false");

                    rootref.child("Match").child(MainMenuActivity.user_id+"/"+item.getFb_id()).updateChildren(mymap);
                    rootref.child("Match").child(item.getFb_id()+"/"+MainMenuActivity.user_id).updateChildren(othermap);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SendPushNotification(item.fb_id,MainMenuActivity.user_name+" has Likes your Profile");


    }


    // when this screen open it will send the notification to other user that
    // both are like the each other and match will build between the users
    public void SendPushNotification(final String receverid, final String message){

        rootref.child("Users").child(receverid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String token=dataSnapshot.child("token").getValue().toString();
                    JSONObject notimap=new JSONObject();
                    try {
                        notimap.put("title",MainMenuActivity.user_name);
                        notimap.put("message",message);
                        notimap.put("icon", MainMenuActivity.user_pic);
                        notimap.put("tokon",token);
                        notimap.put("senderid",MainMenuActivity.user_id);
                        notimap.put("receiverid", receverid);
                        notimap.put("action_type", "Like_Dislike");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ApiRequest.Call_Api(context,Variables.sendPushNotification, notimap,null);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void open_user_detail(Nearby_User_Get_Set item){

        User_detail_F user_detail_f = new User_detail_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("data",item);
        args.putString("from_where","user_list");
        user_detail_f.setArguments(args);
        transaction.addToBackStack(null);

        if(is_from_tab)
           transaction.replace(R.id.MainMenuFragment, user_detail_f).commit();
         else
            transaction.replace(R.id.User_likes_F, user_detail_f).commit();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.subscribe_btn:
                open_subscription_view();
                break;
        }
    }

    public void open_subscription_view(){

        InApp_Subscription_A inApp_subscription_a = new InApp_Subscription_A();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.MainMenuFragment, inApp_subscription_a)
                .addToBackStack(null)
                .commit();

    }


}

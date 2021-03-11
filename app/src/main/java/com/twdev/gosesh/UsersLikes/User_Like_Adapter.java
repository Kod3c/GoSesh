package com.twdev.gosesh.UsersLikes;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.twdev.gosesh.CodeClasses.AdapterClickListener;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.Main_Menu.MainMenuActivity;
import com.twdev.gosesh.R;
import com.twdev.gosesh.Users.Nearby_User_Get_Set;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.postprocessors.BlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by AQEEL on 3/20/2018.
 */

public class User_Like_Adapter extends RecyclerView.Adapter<User_Like_Adapter.CustomViewHolder >{
    public Context context;
    ArrayList<Nearby_User_Get_Set> dataList = new ArrayList<>();


    AdapterClickListener adapterClickListener;

    Integer today_day=0;
    int width;

    String current_time;


    public User_Like_Adapter(Context context, ArrayList<Nearby_User_Get_Set> user_dataList,AdapterClickListener adapterClickListener) {

        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         current_time=dateFormat.format(calendar.getTime());

        this.context = context;
        this.dataList=user_dataList;

        width=(Variables.screen_width/2)-20;
        // get the today as a integer number to make the dicision the chat date is today or yesterday
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

        this.adapterClickListener=adapterClickListener;

    }

    @Override
    public User_Like_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_layout3,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(width, Variables.screen_width-300));
        User_Like_Adapter.CustomViewHolder viewHolder = new User_Like_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView name,age,distance_txt;
        public SimpleDraweeView image;
        public FrameLayout left_overlay,right_overlay;

        ImageView supperlike_img;

        public CustomViewHolder(View view) {
            super(view);
            name=view.findViewById(R.id.username);
            image=view.findViewById(R.id.image);
            supperlike_img=view.findViewById(R.id.supperlike_img);
            distance_txt=view.findViewById(R.id.distance_txt);
            left_overlay=view.findViewById(R.id.left_overlay);
            right_overlay=view.findViewById(R.id.right_overlay);
        }

        public void bind(final int pos,final Nearby_User_Get_Set item,
                         final AdapterClickListener adapterClickListener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterClickListener.onItemClick(pos,item,v);
                }
            });

        }


    }


    @Override
    public void onBindViewHolder(final User_Like_Adapter.CustomViewHolder holder, final int i) {

        final Nearby_User_Get_Set item=dataList.get(i);

        holder.bind(i,item,adapterClickListener);

        if(MainMenuActivity.purduct_purchase) {
            holder.name.setText(item.getFirst_name());
            holder.distance_txt.setText(item.getLocation());
        }
        else {
            holder.name.setText("");
            holder.distance_txt.setText("");
        }

        if(item.getImagesurl().get(0).equals("")){

        }
        else if(!MainMenuActivity.purduct_purchase){

            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(item.getImagesurl().get(0)))
                    .setPostprocessor(new BlurPostProcessor(25, context))
                    .build();


            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(holder.image.getController())
                    .build();

            holder.image.setController(controller);
        }

        else {

            Uri uri;
            if(item.getImagesurl().get(0).contains("http"))
                uri = Uri.parse(item.getImagesurl().get(0));
            else
                uri = Uri.parse(Variables.image_base_url+item.getImagesurl().get(0));

            holder.image.setImageURI(uri);


        }


        if(item.getSwipe()!=null && item.getSwipe().equals("superLike")){
            holder.supperlike_img.setVisibility(View.VISIBLE);
        }else {
            holder.supperlike_img.setVisibility(View.GONE);
        }


        holder.right_overlay.setVisibility(View.GONE);
        holder.left_overlay.setVisibility(View.GONE);


   }



}
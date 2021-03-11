package com.twdev.gosesh.Users;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by AQEEL on 10/15/2018.
 */

public class User_Adapter  extends ArrayAdapter<Nearby_User_Get_Set> {

    Context context;
    public User_Adapter(Context context) {
        super(context, 0);
        this.context=context;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.item_user_layout, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        }
        else {
            holder = (ViewHolder) contentView.getTag();
        }

        Nearby_User_Get_Set spot = getItem(position);

        holder.distance_txt.setText(spot.getLocation());
        holder.name.setText(spot.getFirst_name());

        if(spot.getImagesurl().get(0).equals("")){

        }else {
            Uri uri;
            if(spot.getImagesurl().get(0).contains("http"))
                uri = Uri.parse(spot.getImagesurl().get(0));
            else
                uri = Uri.parse(Variables.image_base_url+spot.getImagesurl().get(0));

            Log.d(Variables.tag,Variables.image_base_url+spot.getImagesurl().get(0));
            holder.image.setImageURI(uri);
        }

        if(spot.getSwipe().equals("superLike")) {
            holder.superlike_image.setVisibility(View.VISIBLE);
            holder.info_layout.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
        }else {
            holder.superlike_image.setVisibility(View.GONE);
            holder.info_layout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }


        return contentView;
    }

    private static class ViewHolder {
        public TextView name,age,distance_txt;
        public ImageView superlike_image;
        public LinearLayout info_layout;

        SimpleDraweeView image;

        public ViewHolder(View view) {
            info_layout=view.findViewById(R.id.info_layout);
            superlike_image=view.findViewById(R.id.superlike_image);
            name=view.findViewById(R.id.username);
            image=view.findViewById(R.id.image);
            distance_txt=view.findViewById(R.id.distance_txt);
        }
    }

}

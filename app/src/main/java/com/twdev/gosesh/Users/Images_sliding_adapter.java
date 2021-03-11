package com.twdev.gosesh.Users;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


/**
 * Created by AQEEL on 3/8/2018.
 */


// this class is belong to show the login slider

public class Images_sliding_adapter extends PagerAdapter {



    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> arrayList;

    public Images_sliding_adapter(Context context,ArrayList<String> image_list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        arrayList=image_list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_sliding_image_layout, view, false);

        if(imageLayout!=null) {

            final SimpleDraweeView imageView =  imageLayout
                    .findViewById(R.id.image);

            if(arrayList.get(position).equals("")){

            }else {

                Uri uri;
                if(arrayList.get(position).contains("http"))
                    uri = Uri.parse(arrayList.get(position));
                else
                    uri = Uri.parse(Variables.image_base_url+arrayList.get(position));


                imageView.setImageURI(uri);
            }

            view.addView(imageLayout, 0);
        }

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
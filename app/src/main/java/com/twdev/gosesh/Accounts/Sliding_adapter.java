package com.twdev.gosesh.Accounts;

import android.content.Context;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twdev.gosesh.R;

import java.util.ArrayList;


/**
 * Created by AQEEL on 3/8/2018.
 */


// this class is belong to show the login slider

public class Sliding_adapter extends PagerAdapter {


    private ArrayList<Login_screens_Get_Set> data_list;

    private LayoutInflater inflater;
    private Context context;


    public Sliding_adapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        data_list=new ArrayList<>();
        data_list.add(new Login_screens_Get_Set(context.getString(R.string.login_description_1),R.drawable.ic_login_1));
        data_list.add(new Login_screens_Get_Set(context.getString(R.string.login_description_2),R.drawable.ic_login_2));
        data_list.add(new Login_screens_Get_Set(context.getString(R.string.login_description_3),R.drawable.ic_login_3));

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return data_list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_slider_layout, view, false);

        if(imageLayout!=null) {

            final ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.image);
            imageView.setImageResource(data_list.get(position).image);
            TextView title_txt=imageLayout.findViewById(R.id.title);
            title_txt.setText(data_list.get(position).name);

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

 public class Login_screens_Get_Set{
        String name;
        Integer image;

    public Login_screens_Get_Set(String name, Integer image) {
        this.name = name;
        this.image = image;
    }
}
}
package com.twdev.gosesh.Inbox;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.twdev.gosesh.CodeClasses.AdapterClickListener;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by AQEEL on 3/20/2018.
 */

public class Matches_Adapter extends RecyclerView.Adapter<Matches_Adapter.CustomViewHolder > implements Filterable{
    public Context context;
    ArrayList<Match_Get_Set> inbox_dataList = new ArrayList<>();
    ArrayList<Match_Get_Set> inbox_dataList_filter = new ArrayList<>();

    AdapterClickListener adapterClickListener;
    Integer today_day=0;



    public Matches_Adapter(Context context, ArrayList<Match_Get_Set> user_dataList, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.inbox_dataList=user_dataList;
        this.inbox_dataList_filter=user_dataList;
        this.adapterClickListener=adapterClickListener;

        // get the today as a integer number to make the dicision the chat date is today or yesterday
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Matches_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_matchs_layout,null);
        Matches_Adapter.CustomViewHolder viewHolder = new Matches_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return inbox_dataList_filter.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        SimpleDraweeView user_image;

        public CustomViewHolder(View view) {
            super(view);
            username=view.findViewById(R.id.username);
            user_image=view.findViewById(R.id.user_image);
        }

        public void bind(final int pos,final Match_Get_Set item, final AdapterClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(pos,item,v);
                }
            });


        }

    }


    @Override
    public void onBindViewHolder(final Matches_Adapter.CustomViewHolder holder, final int i) {
        final Match_Get_Set item=inbox_dataList_filter.get(i);
        holder.username.setText(item.getUsername());

        if(!item.getPicture().equals("") && item.getPicture()!=null) {

            Uri uri;
            if(item.getPicture().contains("http"))
                uri = Uri.parse(item.getPicture());
            else
                uri = Uri.parse(Variables.image_base_url+item.getPicture());

            holder.user_image.setImageURI(uri);
        }


        holder.bind(i,item,adapterClickListener);
   }




    // that function will filter the result
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    inbox_dataList_filter = inbox_dataList;
                } else {
                    ArrayList<Match_Get_Set> filteredList = new ArrayList<>();
                    for (Match_Get_Set row : inbox_dataList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    inbox_dataList_filter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = inbox_dataList_filter;
                return filterResults;

            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                inbox_dataList_filter = (ArrayList<Match_Get_Set>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
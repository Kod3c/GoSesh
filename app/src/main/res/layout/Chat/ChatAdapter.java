package com.twdev.com.Anonymousmessaging.Chat;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twdev.com.Anonymousmessaging.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by AQEEL on 4/3/2018.
 */

class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet> mDataSet;
    String myID;
    private static final int mychat = 1;
    private static final int friendchat = 2;
    private static final int mychatimage=3;
    private static final int otherchatimage=4;
    private static final int alert_message = 5;
    Context context;
    Integer today_day=0;

    private com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnItemClickListener listener;
    private com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnLongClickListener long_listener;

    public interface OnItemClickListener {
        void onItemClick(com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet item, View view);
    }

    public interface OnLongClickListener {
        void onLongclick (com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet item, View view);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     *      Device id
     */

    ChatAdapter(List<com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet> dataSet, String id, Context context, com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnItemClickListener listener, com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnLongClickListener long_listener) {
        mDataSet = dataSet;
        this.myID=id;
        this.context=context;
        this.listener = listener;
        this.long_listener=long_listener;
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View v = null;
        switch (viewtype){
            // we have 4 type of layout in chat activity text chat of my and other and also
            // image layout of my and other
            case mychat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                Chatviewholder mychatHolder = new Chatviewholder(v);
                return mychatHolder;
            case friendchat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                Chatviewholder friendchatHolder = new Chatviewholder(v);
                return friendchatHolder;
            case mychatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_my, viewGroup, false);
                Chatimageviewholder mychatimageHolder = new Chatimageviewholder(v);
                return mychatimageHolder;
            case otherchatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_other, viewGroup, false);
                Chatimageviewholder otherchatimageHolder = new Chatimageviewholder(v);
                return otherchatimageHolder;

            case alert_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                Alertviewholder alertviewholder = new Alertviewholder(v);
                return alertviewholder;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet chat = mDataSet.get(position);
        if(chat.getType().equals("text")){
            Chatviewholder chatviewholder=(Chatviewholder) holder;
        // check if the message is from sender or receiver
        if(chat.getSender_id().equals(myID) && chat.getStatus().equals("1")){
            chatviewholder.message_seen.setText("Seen at "+chat.getTime());
            chatviewholder.message_seen.setVisibility(View.VISIBLE);
        }else {
            chatviewholder.message_seen.setVisibility(View.GONE);
        }
        // make the group of message by date set the gap of 1 min
        // means message send with in 1 min will show as a group
        if (position != 0) {
            com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet chat2 = mDataSet.get(position - 1);
            if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                chatviewholder.datetxt.setVisibility(View.GONE);
            } else {
                chatviewholder.datetxt.setVisibility(View.VISIBLE);
                chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
            }
            chatviewholder.message.setText(chat.getText());
        }else {
            chatviewholder.datetxt.setVisibility(View.VISIBLE);
            chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
            chatviewholder.message.setText(chat.getText());
        }

        chatviewholder.bind(chat,long_listener);

        }

        else if(chat.getType().equals("image")){
            Chatimageviewholder chatimageholder=(Chatimageviewholder) holder;
            // check if the message is from sender or receiver
            if(chat.getSender_id().equals(myID) && chat.getStatus().equals("1")){
                chatimageholder.message_seen.setText("Seen at "+chat.getTime());
                chatimageholder.message_seen.setVisibility(View.VISIBLE);
            }else {
                chatimageholder.message_seen.setVisibility(View.GONE);
            }
            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                    chatimageholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatimageholder.datetxt.setVisibility(View.VISIBLE);
                    chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
                Picasso.with(context).load(chat.getPic_url()).placeholder(R.drawable.image_placeholder).resize(200,200).into(chatimageholder.chatimage);
            }else {
                chatimageholder.datetxt.setVisibility(View.VISIBLE);
                chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                Picasso.with(context).load(chat.getPic_url())
                        .placeholder(R.drawable.image_placeholder).resize(200,200).into(chatimageholder.chatimage);
            }

            chatimageholder.bind(mDataSet.get(position),listener,long_listener);
        }

        else if(chat.getType().equals("delete")){
            Alertviewholder alertviewholder=(Alertviewholder) holder;
            alertviewholder.message.setTextColor(context.getResources().getColor(R.color.delete_message_text));
            alertviewholder.message.setBackground(context.getResources().getDrawable(R.drawable.d_round_gray_background_2));

            alertviewholder.message.setText( "This message is deleted by "+chat.getSender_name());

            if (position != 0) {
                com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(11, 13).equals(chat.getTimestamp().substring(11, 13))) {
                    alertviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    alertviewholder.datetxt.setVisibility(View.VISIBLE);
                    alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }

            }else {
                alertviewholder.datetxt.setVisibility(View.VISIBLE);
                alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

            }
        }

    }
    @Override
    public int getItemViewType(int position) {
        // get the type it view ( given message is from sender or receiver)
        if(mDataSet.get(position).getType().equals("text")){
         if (mDataSet.get(position).sender_id.equals(myID)) {
            return mychat;
            }

        return friendchat;
        }
        else if(mDataSet.get(position).getType().equals("image")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return mychatimage;
            }

            return otherchatimage;
        }else {
            return alert_message;
        }
    }

    /**
     * Inner Class for a recycler view
     */

    class Chatviewholder extends RecyclerView.ViewHolder {
        TextView message,datetxt,message_seen;
        View view;
        public Chatviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.message = view.findViewById(R.id.msgtxt);
            this.datetxt=view.findViewById(R.id.datetxt);
            message_seen=view.findViewById(R.id.message_seen);
        }

        public void bind(final com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet item, final com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnLongClickListener long_listener) {
            message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongclick(item,v);
                    return false;
                }
            });
        }
    }

    class Chatimageviewholder extends RecyclerView.ViewHolder {
        ImageView chatimage;
        TextView datetxt,message_seen;
        View view;
        public Chatimageviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.chatimage = view.findViewById(R.id.chatimage);
            this.datetxt=view.findViewById(R.id.datetxt);
            message_seen=view.findViewById(R.id.message_seen);
        }

        public void bind(final com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet item, final com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnItemClickListener listener, final com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnLongClickListener long_listener) {

            chatimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,v);
                }
            });

            chatimage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long_listener.onLongclick(item,v);
                    return false;
                }
            });
        }

    }



    class Alertviewholder extends RecyclerView.ViewHolder {
        TextView message,datetxt;
        View view;
        public Alertviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.message = view.findViewById(R.id.message);
            this.datetxt = view.findViewById(R.id.datetxt);
        }

    }



    // change the date into (today ,yesterday and date)
    public String ChangeDate(String date){
        //current date in millisecond
        long currenttime= System.currentTimeMillis();

        //database date in millisecond
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        long databasedate = 0;
        Date d = null;
        try {
            d = f.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
       long difference=currenttime-databasedate;
       if(difference<86400000){
           int chatday= Integer.parseInt(date.substring(0,2));
           SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
           if(today_day==chatday)
           return "Today "+sdf.format(d);
           else if((today_day-chatday)==1)
           return "Yesterday "+sdf.format(d);
       }
       else if(difference<172800000){
           int chatday= Integer.parseInt(date.substring(0,2));
           SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
           if((today_day-chatday)==1)
           return "Yesterday "+sdf.format(d);
       }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");
        return sdf.format(d);
    }


}

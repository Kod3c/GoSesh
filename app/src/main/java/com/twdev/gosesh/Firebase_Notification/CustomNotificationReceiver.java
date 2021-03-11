package com.twdev.gosesh.Firebase_Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.Video_Calling.VideoActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CustomNotificationReceiver extends BroadcastReceiver {

   String name,message,type;

    DatabaseReference rootref;

    public Context context;


    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(final Context context, Intent intent) {

        this.context=context;

        sharedPreferences=context.getSharedPreferences(Variables.pref_name,Context.MODE_PRIVATE);

        Bundle extras = intent.getExtras();
        rootref= FirebaseDatabase.getInstance().getReference();

        if (extras!=null) {

              if (sharedPreferences.getBoolean(Variables.islogin,false)) {


                    name = extras.getString("title");
                    message = extras.getString("body");
                    type = extras.getString("type");

                    if (type!=null && (type.equals("video_call") || type.equals("voice_call"))) {

                        String action =extras.getString("action");

                        if (action.equals(VideoActivity.Call_Receive) || VideoActivity.is_calling_activity_open) {

                            Intent i = new Intent(context, VideoActivity.class);
                            i.putExtra("id", extras.getString("senderId"));
                            i.putExtra("name", name);
                            i.putExtra("image", extras.getString("senderImage"));
                            i.putExtra("status", action);
                            i.putExtra("call_type",type);
                            i.putExtra("roomname", extras.getString("message"));
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            context.startActivity(i);

                        }

                    }

           }

        }

    }



}

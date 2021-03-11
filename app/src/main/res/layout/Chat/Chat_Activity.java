package com.twdev.com.Anonymousmessaging.Chat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twdev.com.Anonymousmessaging.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.twdev.com.Anonymousmessaging.Peoples.AllPeople_GetSet;
import com.twdev.com.Anonymousmessaging.R;
import com.twdev.com.Anonymousmessaging.See_Full_Image_F;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class Chat_Activity extends RootFragment {

    DatabaseReference rootref;
    String senderid = "";
    String Receiverid = "";
    EditText message;

    private DatabaseReference Adduser_to_inbox;

    private DatabaseReference mchatRef_reteriving;
    private DatabaseReference send_typing_indication;
    private DatabaseReference receive_typing_indication;

    RecyclerView chatrecyclerview;
    TextView user_name;
    private List<com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet> mChats=new ArrayList<>();
    com.twdev.com.Anonymousmessaging.Chat.ChatAdapter mAdapter;
    ProgressBar p_bar;

    Query query_getchat;

    AllPeople_GetSet senderdataobject,receiverdataobject;

    ImageView profileimage;

    public static String senderid_for_check_notification="";

    Context context;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_chat, container, false);

        context=getContext();


        // intialize the database refer
        rootref = FirebaseDatabase.getInstance().getReference();
        message = (EditText) view.findViewById(R.id.msgedittext);

        user_name=view.findViewById(R.id.username);
        profileimage=view.findViewById(R.id.profileimage);

        // the send id and reciever id from the back activity in which we come from
        Bundle bundle = getArguments();
        if (bundle != null) {
            senderid = bundle.getString("Sender_Id");
            Receiverid = bundle.getString("Receiver_Id");
            senderid_for_check_notification=Receiverid;
            // these two method will get other datial of user like there profile pic link and username
            getSenderData(senderid);
            getreceiverData(Receiverid);
        }


        Adduser_to_inbox=FirebaseDatabase.getInstance().getReference();


        p_bar=view.findViewById(R.id.progress_bar);

        //set layout manager to chat recycler view and get all the privous chat of th user which spacifc user
        chatrecyclerview = (RecyclerView) view.findViewById(R.id.chatlist);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        layout.setStackFromEnd(true);
        chatrecyclerview.setLayoutManager(layout);
        chatrecyclerview.setHasFixedSize(false);
        mAdapter = new com.twdev.com.Anonymousmessaging.Chat.ChatAdapter(mChats, senderid, context, new com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet item, View view) {

                   OpenfullsizeImage(item);
            }
        } ,new com.twdev.com.Anonymousmessaging.Chat.ChatAdapter.OnLongClickListener() {
            @Override
            public void onLongclick(com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet item, View view) {
                if (view.getId() == R.id.msgtxt) {
                    if(senderid.equals(item.getSender_id()) && istodaymessage(item.getTimestamp()))
                    delete_Message(item);
                } else if (view.getId() == R.id.chatimage) {
                    if(senderid.equals(item.getSender_id()) && istodaymessage(item.getTimestamp()))
                    delete_Message(item);
                }
            }
        });


        chatrecyclerview.setAdapter(mAdapter);

        // this the send btn action in that mehtod we will check message field is empty or not
        // if not then we call a method and pass the message
        view.findViewById(R.id.sendbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!TextUtils.isEmpty(message.getText().toString())){

               if(senderdataobject!=null && receiverdataobject!=null){
                   SendMessage(message.getText().toString());
                   message.setText(null);
               }

               }
            }
        });

        view.findViewById(R.id.uploadimagebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStoragepermission();
            }
        });

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    SendTypingIndicator(false);
                }
            }
        });


       // this is the message field event lister which tells the second user either the user is typing or not
        // most importent to show type indicator to second user
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if(count==0){
                   SendTypingIndicator(false);
               }
               else {
                   SendTypingIndicator(true);
               }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // this method receiver the type indicator of second user to tell that his friend is typing or not
        ReceivetypeIndication();

        return view;
    }

    ValueEventListener valueEventListener;

    ChildEventListener eventListener;
    @Override
    public void onStart() {
        super.onStart();
        mChats.clear();
        mchatRef_reteriving = FirebaseDatabase.getInstance().getReference();
        query_getchat = mchatRef_reteriving.child("chat").child(senderid + "-" + Receiverid);


        // this will get all the messages between two users
      eventListener=new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
             try {
                 com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet model = dataSnapshot.getValue(com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet.class);
                 mChats.add(model);
                 mAdapter.notifyDataSetChanged();
                 chatrecyclerview.scrollToPosition(mChats.size() - 1);
             }
             catch (Exception ex) {
                 Log.e("", ex.getMessage());
             }
          ChangeStatus();
     }

     @Override
     public void onChildChanged(DataSnapshot dataSnapshot, String s) {


         if (dataSnapshot != null && dataSnapshot.getValue() != null) {

             try {
                 com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet model = dataSnapshot.getValue(com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet.class);

                 for (int i=mChats.size()-1;i>=0;i--){
                     if(mChats.get(i).getTimestamp().equals(dataSnapshot.child("timestamp").getValue())){
                         mChats.remove(i);
                         mChats.add(i,model);
                       break;
                     }
                 }
                  mAdapter.notifyDataSetChanged();
                }
             catch (Exception ex) {
                 Log.e("", ex.getMessage());
             }
         }
     }

     @Override
     public void onChildRemoved(DataSnapshot dataSnapshot) {

     }

     @Override
     public void onChildMoved(DataSnapshot dataSnapshot, String s) {

     }

     @Override
     public void onCancelled(DatabaseError databaseError) {
         Log.d("", databaseError.getMessage());
     }
 };

      // this will check the two user are do chat before or not
       valueEventListener = new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             if(dataSnapshot.hasChild(senderid + "-" + Receiverid)){
                 p_bar.setVisibility(View.GONE);
                 query_getchat.removeEventListener(valueEventListener);
             }
             else {
                 p_bar.setVisibility(View.GONE);
                 query_getchat.removeEventListener(valueEventListener);
             }
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     };

       query_getchat.addChildEventListener(eventListener);

       mchatRef_reteriving.child("chat").addValueEventListener(valueEventListener);
    }

    // this method will change the status to ensure that
    // user is seen all the message or not (in both chat node and Chatinbox node)
    public void ChangeStatus(){
        final Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Query query1 = reference.child("chat").child(Receiverid+"-"+senderid).orderByChild("status").equalTo("0");
        final Query query2 = reference.child("chat").child(senderid+"-"+Receiverid).orderByChild("status").equalTo("0");

        final DatabaseReference inbox_change_status_1=reference.child("Inbox").child(senderid+"/"+Receiverid);
        final DatabaseReference inbox_change_status_2=reference.child("Inbox").child(Receiverid+"/"+senderid);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if(!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)){
                    String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                    String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("status", "1");
                    result.put("time",sdf.format(c));
                    reference.child(path).updateChildren(result);
                }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if(!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)){
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time",sdf.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        inbox_change_status_1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("rid").getValue().equals(Receiverid)){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        inbox_change_status_1.updateChildren(result);

                }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        inbox_change_status_2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("rid").getValue().equals(Receiverid)){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        inbox_change_status_2.updateChildren(result);

                }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // this will add the new message in chat node and update the ChatInbox by new message by present date
    public void SendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final String formattedDate = df.format(c);

        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        DatabaseReference reference = rootref.child("chat").child(senderid + "-" + Receiverid).push();
        final String pushid = reference.getKey();
        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", Receiverid);
        message_user_map.put("sender_id", senderid);
        message_user_map.put("chat_id",pushid);
        message_user_map.put("text", message);
        message_user_map.put("type","text");
        message_user_map.put("pic_url","");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", senderdataobject.getUsername());
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);
        user_map.put(chat_user_ref + "/" + pushid, message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
             //if first message then set the visibility of whoops layout gone
              String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
              String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;


                HashMap<String,String> sendermap=new HashMap<>();
                sendermap.put("rid",senderid);
                sendermap.put("msg",message);
                sendermap.put("status","0");
                sendermap.put("date",formattedDate);

                HashMap<String,String> receivermap=new HashMap<>();
                receivermap.put("rid",Receiverid);
                receivermap.put("msg",message);
                receivermap.put("status","1");
                receivermap.put("date",formattedDate);

                HashMap both_user_map = new HashMap<>();
                both_user_map.put(inbox_sender_ref , receivermap);
                both_user_map.put(inbox_receiver_ref , sendermap);

                Adduser_to_inbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SendPushNotification(message);
                    }
                });
            }
        });
    }


    // this method will upload the image in chhat
    public void UploadImage(ByteArrayOutputStream byteArrayOutputStream){
        byte[] data = byteArrayOutputStream.toByteArray();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final String formattedDate = df.format(c);

        StorageReference reference= FirebaseStorage.getInstance().getReference();
        DatabaseReference dref=rootref.child("chat").child(senderid+"-"+Receiverid).push();
        final String key=dref.getKey();
        reference.child("images").child(key+".jpg").putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
                String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

                HashMap message_user_map = new HashMap<>();
                message_user_map.put("receiver_id", Receiverid);
                message_user_map.put("sender_id", senderid);
                message_user_map.put("chat_id",key);
                message_user_map.put("text", "");
                message_user_map.put("type","image");
                message_user_map.put("pic_url",taskSnapshot.getDownloadUrl().toString());
                message_user_map.put("status", "0");
                message_user_map.put("time", "");
                message_user_map.put("sender_name", senderdataobject.getUsername());
                message_user_map.put("timestamp", formattedDate);
                HashMap user_map = new HashMap<>();

                user_map.put(current_user_ref + "/" + key, message_user_map);
                user_map.put(chat_user_ref + "/" + key, message_user_map);

                rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                        String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;


                        HashMap<String,String> sendermap=new HashMap<>();
                        sendermap.put("rid",senderid);
                        sendermap.put("msg","Send an image...");
                        sendermap.put("status","0");
                        sendermap.put("date",formattedDate);

                        HashMap<String,String> receivermap=new HashMap<>();
                        receivermap.put("rid",Receiverid);
                        receivermap.put("msg","Send an image...");
                        receivermap.put("status","1");
                        receivermap.put("date",formattedDate);

                        HashMap both_user_map = new HashMap<>();
                        both_user_map.put(inbox_sender_ref , receivermap);
                        both_user_map.put(inbox_receiver_ref , sendermap);

                        Adduser_to_inbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                SendPushNotification("Send an image...");
                            }
                        });
                    }
                });
            }
        });
    }




    private void delete_Message(final com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet chat_getSet) {

        final CharSequence[] options = { "Delete this message","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Delete this message"))

                {
                    update_message(chat_getSet);

                }


                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    public void update_message(com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet item){
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;


        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", item.getReceiver_id());
        message_user_map.put("sender_id", item.getSender_id());
        message_user_map.put("chat_id",item.getChat_id());
        message_user_map.put("text", "Delete this message");
        message_user_map.put("type","delete");
        message_user_map.put("pic_url","");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", senderdataobject.getUsername());
        message_user_map.put("timestamp", item.getTimestamp());

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + item.getChat_id(), message_user_map);
        user_map.put(chat_user_ref + "/" + item.getChat_id(), message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });

    }

    public boolean istodaymessage(String date) {
        Calendar cal = Calendar.getInstance();
        int today_day = cal.get(Calendar.DAY_OF_MONTH);
        //current date in millisecond
        long currenttime = System.currentTimeMillis();

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
        long difference = currenttime - databasedate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if (today_day == chatday)
                return true;
            else
                return false;
        }

        return false;
    }





    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };



        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {
                    getcamrapermission();

                }

                else if (options[item].equals("Choose from Gallery"))

                {

                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);

                }

                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    private void getcamrapermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }

        } else {
           requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    786);
        }
    }

    private void getStoragepermission(){
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            selectImage();
        }
        else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        787 );
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 786) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }

            } else {

                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show();

            }
        }

        if (requestCode == 787) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }
        }
            }

    //on image select activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                //path from full size image
                Cursor cursor = getActivity().getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Images.ImageColumns.ORIENTATION
                        },
                        MediaStore.Images.Media.DATE_ADDED,
                        null,
                        "date_added DESC");

                Bitmap fullsize = null;
                if (cursor != null && cursor.moveToFirst()) {
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    String photoPath = uri.toString();
                    cursor.close();
                    if (photoPath != null) {
                        System.out.println("path: "+photoPath); //path from image full size
                        fullsize = decodeSampledBitmap(photoPath);//here is the bitmap of image full size
                    }
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                fullsize.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);
            }

            else if (requestCode == 2) {

                Uri selectedImage = data.getData();

                InputStream imageStream = null;
                try {
                    imageStream =getActivity().getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagebitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);

            }

        }

    }



    // send the type indicator if the user is typing message
    public void SendTypingIndicator(boolean indicate){
        // if the type incator is present then we remove it if not then we create the typing indicator
        if(indicate){
            final HashMap message_user_map = new HashMap<>();
            message_user_map.put("receiver_id", Receiverid);
            message_user_map.put("sender_id", senderid);

             send_typing_indication=FirebaseDatabase.getInstance().getReference().child("typing_indicator");
            send_typing_indication.child(senderid+"-"+Receiverid).setValue(message_user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                send_typing_indication.child(Receiverid+"-"+senderid).setValue(message_user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
        }

        else {
            send_typing_indication=FirebaseDatabase.getInstance().getReference().child("typing_indicator");

            send_typing_indication.child(senderid+"-"+Receiverid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                       send_typing_indication.child(Receiverid+"-"+senderid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {

                           }
                       });

                        }
                    });

        }

    }


    // receive the type indication to show that your friend is typing or not
    LinearLayout mainlayout;
    public void ReceivetypeIndication(){
        mainlayout=view.findViewById(R.id.typeindicator);

        receive_typing_indication=FirebaseDatabase.getInstance().getReference().child("typing_indicator");
        receive_typing_indication.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot.child(Receiverid+"-"+senderid).exists()){
                          String receiver= String.valueOf(dataSnapshot.child(Receiverid+"-"+senderid).child("sender_id").getValue());
                           if(receiver.equals(Receiverid)){
                               mainlayout.setVisibility(View.VISIBLE);
                           }
                       }
                       else {
                           mainlayout.setVisibility(View.GONE);
                       }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    // on destory delete the typing indicator
    @Override
    public void onDestroy() {
        super.onDestroy();
        SendTypingIndicator(false);
        query_getchat.removeEventListener(eventListener);
    }


    // on stop delete the typing indicator and remove the value event listener
    @Override
    public void onStop() {
        super.onStop();
        SendTypingIndicator(false);
        query_getchat.removeEventListener(eventListener);
        senderid_for_check_notification="";
    }


    // these two method get the both user data ( like username, profile pic link etc.)
    public void getSenderData(String uid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                         senderdataobject = dataSnapshot.getValue(AllPeople_GetSet.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void getreceiverData(String uid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                 receiverdataobject = dataSnapshot.getValue(AllPeople_GetSet.class);

                user_name.setText(receiverdataobject.getUsername());
                Picasso.with(context).load(receiverdataobject.getPicture())
                        .placeholder(R.drawable.image_placeholder).resize(100,100)
                        .into(profileimage);
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //this method will big the size of image in private chat
    public void OpenfullsizeImage(com.twdev.com.Anonymousmessaging.Chat.Chat_GetSet item){
        See_Full_Image_F see_image_f = new See_Full_Image_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle args = new Bundle();
        args.putSerializable("image_url", item.getPic_url());
        args.putSerializable("chat_id", item.getChat_id());
        see_image_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Chat_F, see_image_f).commit();

    }


    public void SendPushNotification(String message){
        Map<String,String> notimap= new HashMap<>();
        notimap.put("name",senderdataobject.getUsername());
        notimap.put("message",message);
        notimap.put("picture",senderdataobject.getPicture());
        notimap.put("token",receiverdataobject.getToken());
        notimap.put("receiverid", Receiverid);
        rootref.child("notifications").child(senderid).push().setValue(notimap);
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmap(String pathName) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return decodeSampledBitmap(pathName, width, height);
    }

    private Bitmap decodeSampledBitmap(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }




}

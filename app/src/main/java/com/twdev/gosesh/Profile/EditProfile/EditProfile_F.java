package com.twdev.gosesh.Profile.EditProfile;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.twdev.gosesh.CodeClasses.ApiRequest;
import com.twdev.gosesh.CodeClasses.Callback;
import com.twdev.gosesh.CodeClasses.Functions;
import com.twdev.gosesh.CodeClasses.Variables;
import com.twdev.gosesh.Main_Menu.MainMenuActivity;
import com.twdev.gosesh.Profile.Profile_F;
import com.twdev.gosesh.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;
import com.wonshinhyo.dragrecyclerview.SimpleDragListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.twdev.gosesh.CodeClasses.Variables.Select_image_from_gallry_code;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile_F extends Fragment {

    View view;
    Context context;

    DragRecyclerView profile_photo_list;


    ImageButton back_btn;


    EditText about_edit,job_title_edit,company_edit,school_edit,dateofbrith_edit,racial_edit;
    RadioButton male_btn,female_btn;


    String image_bas64;


    Profile_photos_Adapter profile_photos_adapter;

     ArrayList<String> images_list;



     TextView done_txt,profile_name_txt;

    public EditProfile_F() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_edit_profile, container, false);
        context=getContext();


        profile_name_txt=view.findViewById(R.id.profile_name_txt);
        profile_name_txt.setText(context.getResources().getString(R.string.about)+" "+MainMenuActivity.user_name);


        images_list=new ArrayList<>();


        profile_photo_list=view.findViewById(R.id.Profile_photos_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
        profile_photo_list.setLayoutManager(layoutManager);
        profile_photo_list.setHasFixedSize(false);

          profile_photos_adapter=new Profile_photos_Adapter(context, images_list, new Profile_photos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item,int postion, View view) {
                if(view.getId()==R.id.cross_btn ){
                    if(item.equals("")){
                        selectImage();
                    }else {
                        Call_Api_For_deletelink(item);
                        profile_photos_adapter.notifyDataSetChanged();
                    }
                }
            }
        });



        profile_photos_adapter.setOnItemDragListener(new SimpleDragListener() {

            @Override
            public void onDrop(int fromPosition, int toPosition) {
                super.onDrop(fromPosition, toPosition);
                Log.d("resp", ""+ fromPosition+"--"+toPosition);
                String from_image=images_list.get(fromPosition);
                String to_image=images_list.get(toPosition);
                if(to_image.equals("")||from_image.equals("")){
                    images_list.remove(toPosition);
                    images_list.add(toPosition,from_image);

                    images_list.remove(from_image);
                    images_list.add(fromPosition,to_image);
                }
                profile_photos_adapter.notifyDataSetChanged();

            }

            @Override
            public void onSwiped(int pos) {
                super.onSwiped(pos);
                Log.d("resp", ""+ pos);

            }
        });

        profile_photo_list.setAdapter(profile_photos_adapter);


        about_edit=view.findViewById(R.id.about_user);
        job_title_edit=view.findViewById(R.id.jobtitle_edit);
        company_edit=view.findViewById(R.id.company_edit);
        school_edit=view.findViewById(R.id.school_edit);
        dateofbrith_edit=view.findViewById(R.id.dateofbirth_edit);

        male_btn=view.findViewById(R.id.male_btn);
        female_btn=view.findViewById(R.id.female_btn);

        back_btn=view.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });




        dateofbrith_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.Opendate_picker(context,dateofbrith_edit);
            }
        });



        done_txt=view.findViewById(R.id.done_txt);
        done_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call_Api_For_edit();
            }
        });

        Get_User_info();

        return view;
    }



    // open the gallery when user press button to upload a picture
    private void selectImage() {
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Select_image_from_gallry_code);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == RESULT_OK) {

            if (requestCode == Select_image_from_gallry_code) {

                Uri selectedImage = data.getData();
                beginCrop(selectedImage);
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                handleCrop(result.getUri());
            }

        }
    }



    // botoom there function are related to crop the image
    private void beginCrop(Uri source) {

        CropImage.activity(source)
                .start(getActivity());


    }

    private void handleCrop( Uri userimageuri) {

            InputStream imageStream = null;
            try {
                imageStream =getActivity().getContentResolver().openInputStream(userimageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

            String path=userimageuri.getPath();
            Matrix matrix = new Matrix();
            android.media.ExifInterface exif = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                try {
                    exif = new android.media.ExifInterface(path);
                    int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            image_bas64=Functions.Bitmap_to_base64(getActivity(),rotatedBitmap);
           // image_byteArray = out.toByteArray();


           // SavePicture();
         Call_Api_For_uploadLink();
    }



    // after save the image in firebase we will save the image url in our server
    private void Call_Api_For_uploadLink() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            JSONObject file_data=new JSONObject();
            file_data.put("file_data",image_bas64);
            parameters.put("image",file_data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.generateNoteOnSD("parameters_uploadImages",parameters.toString());

        Functions.Show_loader(context,false,false);
        ApiRequest.Call_Api(context, Variables.uploadImages, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
               Functions.cancel_loader();

                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");
                    if(code.equals("200")){
                        Get_User_info();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }



    // this method will call when we click for delete the profile images
    private void Call_Api_For_deletelink(String link) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("image_link",link);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context,false,false);
        ApiRequest.Call_Api(context, Variables.deleteImages, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

               Functions.cancel_loader();

                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");
                    if(code.equals("200")){
                        Get_User_info();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }



    // below two method is used get the user pictures and about text from our server
    private void Get_User_info() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context,false,false);
        ApiRequest.Call_Api(context, Variables.getUserInfo, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Parse_user_info(resp);
            }
        });


    }


    public void Parse_user_info(String loginData){

        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);


                images_list.clear();
                images_list.add(userdata.optString("image1"));
                images_list.add(userdata.optString("image2"));
                images_list.add(userdata.optString("image3"));
                images_list.add(userdata.optString("image4"));
                images_list.add(userdata.optString("image5"));
                images_list.add(userdata.optString("image6"));

                about_edit.setText(userdata.optString("about_me"));
                job_title_edit.setText(userdata.optString("job_title"));
                company_edit.setText(userdata.optString("company"));
                school_edit.setText(userdata.optString("school"));
                dateofbrith_edit.setText(userdata.optString("birthday"));

                if(userdata.optString("gender").toLowerCase().equals("male")){
                    male_btn.setChecked(true);
                }else if(userdata.optString("gender").toLowerCase().equals("female")){
                    female_btn.setChecked(true);
                }


                profile_photos_adapter.notifyDataSetChanged();



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // on done btn press this method will call
    // below two mehtod is user for save the change in our profile which we have done
    private void Call_Api_For_edit() {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

            List<String> images=new ArrayList<>();

            List<String> adapter_images=profile_photos_adapter.getData();
            for (int i=0;i<adapter_images.size();i++){
                if(!adapter_images.get(i).equals(MainMenuActivity.user_pic)){
                    images.add(adapter_images.get(i));
                }
            }

            parameters.put("image1",adapter_images.get(0));
            parameters.put("image2",adapter_images.get(1));
            parameters.put("image3",adapter_images.get(2));
            parameters.put("image4",adapter_images.get(3));
            parameters.put("image5",adapter_images.get(4));
            parameters.put("image6",adapter_images.get(5));

            parameters.put("about_me",about_edit.getText().toString());
            parameters.put("job_title",job_title_edit.getText().toString());
            parameters.put("company",company_edit.getText().toString());
            parameters.put("school",school_edit.getText().toString());
            parameters.put("birthday",dateofbrith_edit.getText().toString());


            if(male_btn.isChecked()){
                parameters.put("gender","Male");

            }else if(female_btn.isChecked()){
                parameters.put("gender","Female");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context,false,false);
        ApiRequest.Call_Api(context, Variables.Edit_profile, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Parse_edit_data(resp);
            }
        });



    }

    public void Parse_edit_data(String loginData){

        try {

            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){

                        JSONArray msg=jsonObject.getJSONArray("msg");
                        JSONObject userdata=msg.getJSONObject(0);

                        MainMenuActivity.sharedPreferences.edit().putString(Variables.birth_day,userdata.optString("age")).commit();
                        MainMenuActivity.birthday=userdata.optString("age");


                       Profile_F.age.setText(MainMenuActivity.birthday);

                       if(!MainMenuActivity.user_pic.equals(userdata.optString("image1"))) {
                           MainMenuActivity.sharedPreferences.edit().putString(Variables.u_pic, userdata.optString("image1")).commit();
                           MainMenuActivity.user_pic = userdata.optString("image1");

                           if(MainMenuActivity.user_pic!=null  && !MainMenuActivity.user_pic.equals("") ){

                               Uri uri;
                               if(MainMenuActivity.user_pic.contains("http"))
                                   uri = Uri.parse(MainMenuActivity.user_pic);
                               else
                                   uri = Uri.parse(Variables.image_base_url+MainMenuActivity.user_pic);

                               Profile_F.profile_image.setImageURI(uri);
                           }

                       }

                       getActivity().onBackPressed();

            }
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

    }



}

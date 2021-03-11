package com.twdev.gosesh.CodeClasses;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.twdev.gosesh.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiRequest {

    public static void Call_Api (final Context context, String url, JSONObject jsonObject,
                                 final Callback callback){

        Log.d(Variables.tag,url);
        Log.d(Variables.tag,jsonObject.toString());

         JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(Variables.tag,response.toString());

                        if(callback!=null)
                        callback .Responce(response.toString());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Variables.tag,error.toString());

                if(callback!=null)
                  callback .Responce(error.toString());

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);
    }



    public static void Send_Notification(final Context context, final JSONObject jsonObject, final Callback callback ){

        Log.d(Variables.tag,jsonObject.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send", jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(Variables.tag,response.toString());

                        if(callback!=null)
                            callback.Responce(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if(callback!=null)
                    callback.Responce(error.toString());

                Log.d(Variables.tag+" error",error.toString());
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Authorization","key="+context.getResources().getString(R.string.firebase_server_key));
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };


            RequestQueue requestQueue = Volley.newRequestQueue(context);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.getCache().clear();
            requestQueue.add(jsonObjReq);

    }


}

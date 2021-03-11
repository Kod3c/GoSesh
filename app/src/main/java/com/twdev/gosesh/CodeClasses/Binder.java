package com.twdev.gosesh.CodeClasses;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;

/**
 * Created by AQEEL on 10/19/2018.
 */

public class Binder extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Fresco.initialize(this);

    }



}

package com.twdev.gosesh.CodeClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;

import com.twdev.gosesh.R;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by AQEEL on 12/4/2018.
 */

// this class will get the latest version of app from playtore if the app is publish into playstore

public class VersionChecker extends AsyncTask<String, String, String> {

    private String newVersion;

    private Activity context;

    public VersionChecker(Activity context) {
       this.context=context;
    }


    @Override
    protected String doInBackground(String... params) {

        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id="+context.getPackageName()+"&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".IQ1z0d .htlgb")
                    .get(7)
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        // get version of currently running app
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionname=packageInfo.versionName;

        String latestVersion = newVersion;

        if(!versionname.equals(latestVersion) && (latestVersion!=null)){

            AlertDialog.Builder alert=new AlertDialog.Builder(context,R.style.AlertDialogCustom);
            alert.setTitle("Update "+context.getResources().getString(R.string.app_name))
                    .setMessage("Please update the "+context.getResources().getString(R.string.app_name)+" app. you have an old version.")
                    .setNegativeButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+context.getPackageName())));
                            context.finish();
                        }
                    });

           //alert.setCancelable(false);
            alert.show();
    }

}

}
package com.example.meetup.Helper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class MyUtils {

    public MyUtils() {

    }


    public static void zamjeniFragment(Activity activity, int lokacijaID, Fragment fragment) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.getBackStackEntryCount();
        fragmentTransaction.replace(lokacijaID, fragment);

        fragmentTransaction.commit();
    }

    public static boolean pristupInternetu(Context context){
        boolean imaWiFi=false;
        boolean imaMP=false;

        ConnectivityManager connectivityManager =(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo=connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info:networkInfo ) {
            if(info.getTypeName().equalsIgnoreCase("WIFI")) {
                if (info.isConnected())
                    imaWiFi = true;
            }

            if(info.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (info.isConnected())
                    imaMP = true;
            }
        }
        return imaWiFi || imaMP;
    }


    public static void HideBar(Activity activity){
        activity.getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }
}

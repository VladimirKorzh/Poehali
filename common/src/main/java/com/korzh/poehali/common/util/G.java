package com.korzh.poehali.common.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.korzh.poehali.common.R;

import org.w3c.dom.Document;

/**
 * Created by vladimir on 7/1/2014.
 */
public class G {
    private static G instance = null;
    public static LocationManager locationManager = null;
    public SharedPreferences settings = null;

    public Document currentRoute = null;

    public String userId;

    public TextToSpeech textToSpeech;



    public static G getInstance(){
        return instance;
    }

    public G(Context c){
        instance = this;
        userId = Settings.Secure.getString( c.getContentResolver(), Settings.Secure.ANDROID_ID);
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        settings = c.getSharedPreferences(C.PREFERENCES_FILENAME, 0);


//        textToSpeech=new TextToSpeech(c,
//                new TextToSpeech.OnInitListener() {
//                    @Override
//                    public void onInit(int status) {
//                        if(status != TextToSpeech.ERROR){
//                            textToSpeech.setLanguage(l);
//                        }
//                    }
//                });
    }

    public void checkGPSEnabled(final Context context){
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(context, context.getString(R.string.toast_gps_is_enabled), Toast.LENGTH_SHORT).show();
        } else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(context.getString(R.string.msgGpsIsDisabled))
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.dlgBtnEnableGps),
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    context.startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton(context.getString(R.string.dlgBtnCancel), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    public Location getLastKnownLocation(){
        Location myLocation;

        // get the latest known location
        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null){
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return myLocation;
    }



    public void showInfoDialog(Context c, String title, String msg){
        new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(msg)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}

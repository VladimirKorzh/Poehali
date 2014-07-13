package com.korzh.poehali.common.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.text.format.Time;
import android.widget.Toast;

import com.korzh.poehali.common.R;
import com.korzh.poehali.common.interfaces.LocationBroadcaster;
import com.korzh.poehali.common.network.MQBinding;
import com.korzh.poehali.common.network.RabbitMQHelper;

import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by vladimir on 7/1/2014.
 */
public class G {
    private static G instance = null;
    public static LocationManager locationManager = null;
    public SharedPreferences settings = null;
    public String userId;
    public NodeList currentNavigationRoute = null;
    public MQBinding mqBinding = null;

    public static G getInstance(){
        return instance;
    }

    public G(Context c){
        instance = this;
        userId = Settings.Secure.getString( c.getContentResolver(), Settings.Secure.ANDROID_ID);
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        settings = c.getSharedPreferences(C.PREFERENCES_FILENAME, 0);

        RabbitMQHelper rabbitMQHelper = RabbitMQHelper.getInstance();
        mqBinding = new MQBinding();
        ArrayList<String> binds = mqBinding.getConsumerBindingKeysList(mqBinding.getCityCenterFromExchange(mqBinding.getTargetExchange()), getLastKnownLocation(), 3);
        binds.add("message."+userId);
        mqBinding.ChangeBinds(binds);
        mqBinding.Start();

        LocationBroadcaster locationBroadcaster = new LocationBroadcaster();
        locationBroadcaster.StartBroadcast();

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

    public float getStartingFare(String distance){
        String[] strings = distance.split(" ");
        strings[0] = strings[0].replace(",",".");
        float result = Float.valueOf(strings[0])*2;
        return result;
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
        Location lastGPSfix, lastNetworkFix;

        // get the latest known locations
        lastGPSfix = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lastNetworkFix = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // if we don't have any GPS data, just return whatever we have
        if (lastGPSfix == null) {
            U.Log("getLastKnownLocation","Using network data");
            return lastNetworkFix;
        }

        // get their respective times
        Time accepted = new Time();
        accepted.set(System.currentTimeMillis() - C.LASTLOCATION_TIMEDIFFERENCE);

        Time GPSFixTime = new Time();
        GPSFixTime.set(lastGPSfix.getTime());

        Time NetworkFixTime = new Time();
        NetworkFixTime.set(lastNetworkFix.getTime());

        // return whichever is the latest
        if (NetworkFixTime.after(GPSFixTime)) {
            U.Log("getLastKnownLocation","Using network data");
            return lastNetworkFix;
        }
        else {
            U.Log("getLastKnownLocation","Using gps data");
            return lastGPSfix;
        }

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

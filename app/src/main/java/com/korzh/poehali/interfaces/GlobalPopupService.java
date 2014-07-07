package com.korzh.poehali.interfaces;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.korzh.poehali.R;
import com.korzh.poehali.activities.PhoneNumberValidation;

public class GlobalPopupService extends Service
{
    protected boolean foreground = false;
    protected boolean cancelNotification = false;
    private Notification notification;
    private View myView;
    protected int id = 0;
    private WindowManager wm;
    private WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // System.exit(0);
        Toast.makeText(getBaseContext(),"onCreate", Toast.LENGTH_SHORT).show();
        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity=Gravity.TOP| Gravity.LEFT;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflateview();
        foregroundNotification(1);
        //moveToForeground(1,n,true);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(0);
        Toast.makeText(getBaseContext(),"onDestroy", Toast.LENGTH_SHORT).show();
        if(myView != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(myView);
            myView = null;
        }
    }


    protected Notification foregroundNotification(int notificationId)
    {
        notification = new Notification(R.drawable.ic_launcher, "my Notification", System.currentTimeMillis());
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_ONLY_ALERT_ONCE;
        notification.setLatestEventInfo(this, "my Notification", "my Notification", notificationIntent());
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(id, notification);
        return notification;
    }
    private PendingIntent notificationIntent() {
        Intent intent = new Intent(this, PhoneNumberValidation.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pending;
    }
    public void inflateview()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        myView = inflater.inflate(R.layout.row_of_drawer, null);
        myView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(getBaseContext(),"onToasttt", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // Add layout to window manager
        wm.addView(myView, params);
    }
}
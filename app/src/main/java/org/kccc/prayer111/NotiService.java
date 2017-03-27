package org.kccc.prayer111;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.json.JSONException;

import java.net.MalformedURLException;

public class NotiService extends Service {

    SendPushNotification sendPushNotification;

    public NotiService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

//        registerReceivers();
//        PushManager pushManager = PushManager.getInstance(this);
//
//        pushManager.setNotificationFactory(new NotificationFactory());
//
//
//        processPermissions();
//
//        try {
//            pushManager.onStartup(this);
//        } catch (Exception e) {
//            Log.d("하이", e.getLocalizedMessage());
//        }
//        pushManager.registerForPushNotifications();


        sendPushNotification = new SendPushNotification();
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    sendPushNotification.SendPush();

                } catch (MalformedURLException e) {

                } catch (JSONException e) {

                }

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

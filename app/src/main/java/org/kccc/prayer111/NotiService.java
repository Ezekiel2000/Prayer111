package org.kccc.prayer111;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotiService extends Service {

    boolean isRunning;
    SimpleDateFormat today;

    public NotiService() {
        super();
    }

    @Override
    public void onCreate() {

        isRunning = true;


        new Thread() {
            @Override
            public void run() {
                while (isRunning) {

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);

                    SimpleDateFormat dd = new SimpleDateFormat("hh:mm", Locale.KOREA);
                    String day = dd.format(date);



//                    if ( day.equals("04:03")) {
//
//                        Log.d("하이", "서비스 시간 : " + day);
//                        isRunning = false;
//
//                    }
//
                    try {
                        SendPushNotification sendPushNotification = new SendPushNotification();
                        sendPushNotification.SendPush();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

            }
        }.start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

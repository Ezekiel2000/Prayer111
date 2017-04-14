package org.kccc.prayer111;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pushwoosh.notification.AbsNotificationFactory;
import com.pushwoosh.notification.PushData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ezekiel on 2017. 3. 7..
 */

public class NotificationFactory extends AbsNotificationFactory {

    @Override
    public Notification onGenerateNotification(PushData pushData) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());

        builder.setContentTitle(getContentFromHtml(pushData.getHeader()));

        builder.setContentText(getContentFromHtml(pushData.getMessage()));

        builder.setSmallIcon(pushData.getSmallIcon());

        builder.setTicker(getContentFromHtml(pushData.getTicker()));

        builder.setWhen(System.currentTimeMillis());

        if (pushData.getBigPicture() != null)
        {
            //set big image if available
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(pushData.getBigPicture()).setSummaryText(getContentFromHtml(pushData.getMessage())));
        }
        else
        {
            //otherwise it's big text style
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getContentFromHtml(pushData.getMessage())));
        }

        //support icon background color
        if (pushData.getIconBackgroundColor() != null)
        {
            builder.setColor(pushData.getIconBackgroundColor());
        }

        //support custom icon
        if (null != pushData.getLargeIcon())
        {
            builder.setLargeIcon(pushData.getLargeIcon());
        }

        //build the notification
        final Notification notification = builder.build();

        //add sound
        addSound(notification, pushData.getSound());

        //add vibration
        addVibration(notification, pushData.getVibration());

        //make it cancelable
        addCancel(notification);

        //all done!
        return notification;
    }

    private void addRemoteActions(NotificationCompat.Builder notificationBuilder, PushData pushData) {

        String action = pushData.getExtras().getString("my_actions");

        Log.d("하이", "넘어오는 액션 값 :" + action);

        if (action != null) {
            try {
                JSONArray jsonArray = new JSONArray(action);
                for (int i = 0; i < jsonArray.length() ; i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    String title = json.getString("title");
                    String url = json.getString("url");
                    Intent actionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    notificationBuilder.addAction(new NotificationCompat.Action(0, title, PendingIntent.getActivity(getContext(), 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPushReceived(PushData pushData) {

    }

    @Override
    public void onPushHandle(Activity activity) {

    }
}

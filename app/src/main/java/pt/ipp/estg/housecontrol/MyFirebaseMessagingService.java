package pt.ipp.estg.housecontrol;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "pt.ipp.estg.housecontrol.mychannel";
    private static String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        setChannels();

        //show the message received in the tray

        NotificationManager myManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int nNotificationId = new Random().nextInt(60000);

        Uri mySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(mySound);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(nNotificationId, notificationBuilder.build());

        Log.d(TAG, "From: " + remoteMessage.getFrom());

//         Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Title:  " + title);
            Log.d(TAG,"Body:  " + message);

            Intent myIntent = new Intent(this, MainActivity.class);
            myIntent.putExtra("Title", title);
            myIntent.putExtra("Body", message);
            startActivity(myIntent);
        }

//        super.onMessageReceived(remoteMessage);
//        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());


    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Novo token: " + token);
//        enviarTokenParaServidor(token);
        super.onNewToken(token);
        getSharedPreferences("newtoken", MODE_PRIVATE).edit().putString("token",token).apply();

        Log.e("NEW_TOKEN",token);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setChannels() {
        NotificationChannel myChannel = new NotificationChannel(ADMIN_CHANNEL_ID, "MyChannel",
                NotificationManager.IMPORTANCE_LOW);
        myChannel.setDescription("Description");
        myChannel.enableLights(true);
        myChannel.setLightColor(Color.RED);
        myChannel.enableVibration(true);
        NotificationManager myManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        myManager.createNotificationChannel(myChannel);
    }

}

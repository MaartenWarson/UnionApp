package be.pxl.unionapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import be.pxl.unionapp.R;
import be.pxl.unionapp.activities.MainActivity;

// Klasse om notification verstuurd via FireBase af te handelen
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        generateNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
    }

    private void generateNotification(String body, String title) {
        Intent intent = new Intent(this, MainActivity.class); // Naar MainActivty gaan wanneer op de notification geklikt wordt
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Vorm en inhoud geven aan de notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService((Context.NOTIFICATION_SERVICE));

        if (NOTIFICATION_ID > 1073741824) {
            NOTIFICATION_ID = 0;
        }

        manager.notify(NOTIFICATION_ID++, notificationBuilder.build());
    }
}
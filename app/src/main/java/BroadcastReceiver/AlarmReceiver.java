package BroadcastReceiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rishonlovesanimals.MainActivity;
import com.example.rishonlovesanimals.R;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    private String ChannelId = "countingToTask";
    private int NOTIFICATION_ID = 1;


    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = context.getSystemService(NotificationManager.class);
        NotificationChannel channel = createNotificationChannel();

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.notif_task_layout);
        remoteViews.setTextViewText(R.id.notif_title,intent.getStringExtra("title"));
        remoteViews.setTextViewText(R.id.notif_content,intent.getStringExtra("content"));

        String notificationTitle = intent.getStringExtra("title");
        String notificationContent = intent.getStringExtra("content");
        Intent intent1 = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("scrollToTasks",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,ChannelId)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());
    }

    private NotificationChannel createNotificationChannel()
    {

        CharSequence name = "channelName";
        String description = "channelDescription";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(ChannelId,name,importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
        return channel;
    }
}

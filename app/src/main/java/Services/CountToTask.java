package Services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.rishonlovesanimals.MainActivity;

public class CountToTask extends Service {

    private NotificationManager notificationManager;
    private String ChannelId = "countingToTask";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = getSystemService(NotificationManager.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        long time  = intent.getLongExtra("date",-1);
        if(time > -1)
        {
            NotificationChannel channel = createNotificationChannel();
            Intent intent1 = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.putExtra("scrollToTasks",true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,ChannelId)
                    .setSmallIcon(android.R.drawable.star_on)
                    .setContentTitle("my notification")
                    .setContentText("my notification text")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP,time,);
        }
        return super.onStartCommand(intent, flags, startId);
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

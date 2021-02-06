package Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import BroadcastReceiver.AlarmReceiver;
import Tasks.Task;


public class DownloadCurrentUserTaskService extends Service implements Runnable{

    private ArrayList<Task> tasks;
    private boolean onLine;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onLine = true;
        tasks = new ArrayList<>();
        Thread downloadingTasks = new Thread(this);
        downloadingTasks.setName("Downloading Current User Tasks");
        downloadingTasks.start();
        return super.onStartCommand(intent, flags, startId);
    }


    private void setAlarm()
    {
        Intent callAlarm = new Intent(this, AlarmReceiver.class);
        if(!tasks.isEmpty())
        {
            callAlarm.putExtra("taskName",tasks.get(0).getTask());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,callAlarm,PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);

            String taskTime = tasks.get(0).getTaskTime();
            StringBuilder day = new StringBuilder();
            StringBuilder month = new StringBuilder();
            StringBuilder year = new StringBuilder();
            StringBuilder hour = new StringBuilder();
            StringBuilder minute = new StringBuilder();
            int i;
            for(i = 0;taskTime.charAt(i)!='/';i++) {
                day.append(taskTime.charAt(i));
            }
            for(i+=1;taskTime.charAt(i)!='/';i++) {
                month.append(taskTime.charAt(i));
            }
            for(i+=1;taskTime.charAt(i)!= 'a';i++) {
                if(taskTime.charAt(i)!=' ')
                    year.append(taskTime.charAt(i));
            }
            i++;
            for (i+=1;taskTime.charAt(i)!=':';i++) {
                if(taskTime.charAt(i)!=' ')
                    hour.append(taskTime.charAt(i));
            }
            for (i+=1;i<taskTime.length();i++) {
                minute.append(taskTime.charAt(i));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt((day.toString())));
            calendar.set(Calendar.MONTH,Integer.parseInt(month.toString())-1);
            calendar.set(Calendar.YEAR,Integer.parseInt(year.toString()));
            calendar.set(Calendar.MINUTE,Integer.parseInt(minute.toString()));
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hour.toString()));
            calendar.set(Calendar.SECOND,0);

            assert alarmManager != null;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }
    }

    @Override
    public void run() {
        while(onLine)
        {
            DownloadTask();
        }
    }

    private void DownloadTask()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String userId = user.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + userId + "/tasks");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tasks = new ArrayList<>();
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        Task task = new Task();
                        task.setTask(snapshot1.getKey());
                        task.setTaskTime((String)snapshot1.getValue());
                        tasks.add(task);
                    }
                    setAlarm();
                    NotifyActivity();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERROR","downloading self tasks stopped");
                    error.toException().printStackTrace();
                }
            });
        }
        else
            Log.d("ERROR","user is null in DownloadCurrentUserTaskService");
    }

    private void NotifyActivity()
    {
        Intent activityNotifyIntent = new Intent("currentUserTaskList");
        activityNotifyIntent.putExtra("tasks",tasks);
        LocalBroadcastManager.getInstance(this).sendBroadcast(activityNotifyIntent);
    }


}

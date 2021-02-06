package Services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rishonlovesanimals.MainActivity;
import com.example.rishonlovesanimals.Profile;
import com.example.rishonlovesanimals.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import BroadcastReceiver.AlarmReceiver;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import Tasks.Task;

public class DownloadTasksService extends Service implements Runnable{

    private NotificationManager notificationManager;
    private String ChannelId = "countingToTask";
    private int NOTIFICATION_ID = 1;
    private ArrayList<Task>taskArrayList;
    private ArrayList<String>userKeys;
    private int position;
    private Intent intent;
    private boolean b;
    private ArrayList<Profile>users;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        //should download all the list of the tasks
        taskArrayList = new ArrayList<>();
        userKeys = new ArrayList<>();
        position = intent.getIntExtra("position",-1);
        if(intent.hasExtra("keys")){
            userKeys = intent.getStringArrayListExtra("keys");
            users = intent.getParcelableArrayListExtra("users");
            b = true;
        }
        else b = false;
        this.intent = intent;
        Thread downloadTasksThread = new Thread(this);
        downloadTasksThread.setName("downloadTaskThread");
        downloadTasksThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void DownloadTasks()
    {
        DatabaseReference databaseReference;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//should be showing list of user tapped
        if(user!=null)
        {
            if(position!=-1) {
                databaseReference = FirebaseDatabase.getInstance().getReference("users/" + userKeys.get(position) + "/tasks");
            }
            else {
                databaseReference = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/tasks");
            }

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    taskArrayList.clear();
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
                        Object checked = ds.getValue();
                        Task task = new Task();
                        assert checked != null;
                        task.setTask(ds.getKey());
                        task.setTaskTime(checked + "");

                        taskArrayList.add(task);
                    }
                    if(!taskArrayList.isEmpty())
                    {
                        //setCountDown();
                        if(position>=0 && userKeys.size()>0 && userKeys.get(position).equals(user.getUid()))//possible change if elseif to one if with or clause
                        {
                            setCountDown();
                        }
                        else if(position == -1)
                            setCountDown();
                    }
                    BroadcastToActivity();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }



    private void setCountDown()//sets the countdown to the next task
    {
        Intent intent = new Intent(this, AlarmReceiver.class);
        if(taskArrayList.size()!=0)
        intent.putExtra("taskName",taskArrayList.get(0).getTask());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        String taskTime = taskArrayList.get(0).getTaskTime();
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
        System.out.println("this is the time: " + calendar.getTimeInMillis());
        System.out.println("this is the hour1: " + calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println("this is the min: " + calendar.get(Calendar.MINUTE));
        assert alarmManager != null;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    @Override
    public void run() {
        DownloadTasks();
    }

    private void BroadcastToActivity()
    {
        Intent intent = new Intent("taskList");
        intent.putExtra("tasks",taskArrayList);
        intent.putExtra("all",b);
        intent.putExtra("position",position);
        intent.putParcelableArrayListExtra("users",users);
        intent.putStringArrayListExtra("keys",userKeys);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



}

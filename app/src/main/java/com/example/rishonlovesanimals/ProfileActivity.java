package com.example.rishonlovesanimals;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import Adapters.TaskListAdapter;
import Fragments.TaskFragment;
import Tasks.Task;

public class ProfileActivity extends AppCompatActivity implements TaskFragment.TaskFragmentListener {

    private int dayOfMonth,year,month,hourOfDay,minute;
    private String taskName,userKey="";
    private boolean notCurrentUser;
    private String currentUserPosition;
    private ListView listView;
    //int year, int month, int dayOfMonth,int hourOfDay, int minute
    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        TextView profile_name = findViewById(R.id.profile_name);
        TextView profile_age = findViewById(R.id.profile_age);
        TextView profile_position = findViewById(R.id.profile_position);
        TextView profile_arriving = findViewById(R.id.profile_arriving);
        final Button newTaskBtn = findViewById(R.id.profile_setNewTask_Btn);
       // final Button button = findViewById(R.id.setAlarm_Btn);
        Button openEditProfileBtn = findViewById(R.id.profile_openEditProfileBtn);
         listView = findViewById(R.id.profile_tasks);
        profile_name.setText(getIntent().getStringExtra("profile_name"));
        profile_age.setText(getIntent().getStringExtra("profile_age"));
        profile_position.setText(getIntent().getStringExtra("profile_position"));
        profile_arriving.setText(getIntent().getStringExtra("profile_arriving"));
        String admin = getIntent().getStringExtra("admin");
        boolean currentUser = getIntent().getBooleanExtra("currentUser",false);
        notCurrentUser = !currentUser;
        String pressedUserUid = getIntent().getStringExtra("Uid");
        userKey = pressedUserUid;
        if(admin!=null) {
            if (!admin.equals("admin")) {
                newTaskBtn.setVisibility(View.GONE);
               // button.setVisibility(View.GONE);
            }
        }
        if(admin!=null){
            if (!currentUser && !admin.equals("admin")) {
                openEditProfileBtn.setVisibility(View.INVISIBLE);
            }
        }
        //ShapeableImageView shapeableImageView = findViewById(R.id.profile_image);
        //Button newTaskBtn = findViewById(R.id.profile_setNewTask_Btn);

        newTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens new TaskFragment
                TaskFragment taskFragment = new TaskFragment();
                taskFragment.show(getSupportFragmentManager(),"profileActivityFragmentPart");

            }
        });
        DownloadTasks(pressedUserUid);
        openEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,ProfileEditActivity.class);
                startActivity(intent);
                finish();
            }
        });



        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTask(year,month,dayOfMonth,hourOfDay,minute);
                finish();
            }
        });*/
    }
    
    @SuppressWarnings("unchecked")
    private void DownloadTasks(String pressedUserUid)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/"+ pressedUserUid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object> map;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    map = (HashMap<String, Object>) dataSnapshot.getValue();
                    if(map!=null && dataSnapshot.getKey()!=null)
                    {
                        if(dataSnapshot.getKey().equals("tasks")) {
                            //HashMap<String, String> tasks = (HashMap<String, String>) map.get("tasks");
                            ArrayList<Task> taskList = new ArrayList<>();
                            Set<String>mapKeys = map.keySet();
                            for (String mapKey : mapKeys) {
                                Task task = new Task();
                                task.setTaskTime((String) map.get(mapKey));
                                task.setTask(mapKey);
                                taskList.add(task);
                            }
                            TaskListAdapter taskListAdapter = new TaskListAdapter(taskList);
                            listView.setAdapter(taskListAdapter);
                            taskListAdapter.setListener(new TaskListAdapter.DoneWithTask() {
                                @Override
                                public void onDoneWithTask(int position) {
                                    Toast.makeText(ProfileActivity.this, "Done with task in ProfileActivity", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNotDoneWithTask(int position) {
                                    Toast.makeText(ProfileActivity.this, "Not done with task in profile activity", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public void newTask(String taskName,int dayOfMonth,int month,int year,int hourOfDay,int minute) {
        //saves new task to user who's profile is this and creates a service that counts to this task beginning in the background

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);
        long calendarTimeInMilliseconds = calendar.getTimeInMillis();
        long nowTimeInMilliseconds = System.currentTimeMillis();
        if(nowTimeInMilliseconds < calendarTimeInMilliseconds)//checks if the time set has already passed
        {
            this.taskName = taskName;
            ProfileActivity.this.year = year;
            ProfileActivity.this.month = month;
            ProfileActivity.this.dayOfMonth = dayOfMonth;
            ProfileActivity.this.hourOfDay = hourOfDay;
            ProfileActivity.this.minute = minute;
            setTask(year,month,dayOfMonth,hourOfDay,minute);
            Toast.makeText(ProfileActivity.this,"Time and Date set to: " + dayOfMonth + "/" + month + "/" + year + " " + hourOfDay + ":" + minute,Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(ProfileActivity.this,"Time cannot be set to prior time",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void newTaskCancelled() {
        Toast.makeText(ProfileActivity.this,"new Task will not be set",Toast.LENGTH_SHORT).show();
    }


    private void setTask(int year, int month, int dayOfMonth, int hourOfDay, int minute)//works
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.SECOND,0);
       /* Intent intent = new Intent(ProfileActivity.this, AlarmReceiver.class);
        //Intent intent = new Intent(ProfileActivity.this, DownloadTasksService.class);
        intent.putExtra("title","Task to do");
        intent.putExtra("content",taskName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ProfileActivity.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)ProfileActivity.this.getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        Toast.makeText(ProfileActivity.this,"time set",Toast.LENGTH_SHORT).show();*/

        //uploads task to cloud

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            DatabaseReference reference;
            if(!notCurrentUser) {
                 reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/tasks");
            }else{
                reference = FirebaseDatabase.getInstance().getReference("users/" + userKey + "/tasks");
            }
            HashMap<String,Object> map = new HashMap<>();
            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
            map.put(taskName, format.format(calendar.getTime()));
            reference.updateChildren(map);
        }
        /*Intent serviceIntent = new Intent(ProfileActivity.this,CountToTask.class);
        serviceIntent.putExtra("date",calendar.getTimeInMillis());
        startService(serviceIntent);*/
    }
}

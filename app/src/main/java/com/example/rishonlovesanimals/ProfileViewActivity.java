package com.example.rishonlovesanimals;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.Objects;
import java.util.Set;

import Adapters.TaskListAdapter;
import Adapters.UserProfileAdapter;
import Fragments.TaskFragment;
import Tasks.Task;

public class ProfileViewActivity extends AppCompatActivity implements TaskFragment.TaskFragmentListener{

    private String taskName,userKey="";
    private boolean notCurrentUser;
    private ListView taskList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view_layout);

        UserProfileAdapter adapter = new UserProfileAdapter();
        ArrayList<String> userInfo = new ArrayList<>();
        userInfo.add(getIntent().getStringExtra("profile_name"));
        userInfo.add(getIntent().getStringExtra("profile_position"));
        String admin = getIntent().getStringExtra("admin");
        boolean currentUser = getIntent().getBooleanExtra("currentUser",false);
        if(getIntent().getStringExtra("profile_arriving") != null)
        {
            if(Objects.equals(getIntent().getStringExtra("profile_arriving"), "arriving true"))
            {
                userInfo.add(getResources().getString(R.string.arriving));
            }
            else
                userInfo.add(getResources().getString(R.string.not_arriving));
            //userInfo.add(getIntent().getStringExtra("profile_arriving"));
        }

        adapter.setUserInfo(userInfo);

        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        taskList = findViewById(R.id.taskList);

        Button setTaskBtn = findViewById(R.id.setAlarm);
        Button editProfileBtn = findViewById(R.id.editProfile);

        setTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskFragment taskFragment = new TaskFragment();
                taskFragment.show(getSupportFragmentManager(),"profileActivityFragmentPart");
            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileViewActivity.this,ProfileEditActivity.class);
                startActivity(intent);
            }
        });
        notCurrentUser = !currentUser;
        String pressedUserUid = getIntent().getStringExtra("Uid");
        userKey = pressedUserUid;
        if(admin!=null) {
            if (!admin.equals("admin")) {
                setTaskBtn.setVisibility(View.GONE);
                // button.setVisibility(View.GONE);
            }
        }
        if(admin!=null){
            if (!currentUser && !admin.equals("admin")) {
                editProfileBtn.setVisibility(View.INVISIBLE);
            }
        }
        if(pressedUserUid==null)
            pressedUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DownloadTasks(pressedUserUid);
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
                            Set<String> mapKeys = map.keySet();
                            for (String mapKey : mapKeys) {
                                Task task = new Task();
                                task.setTaskTime((String) map.get(mapKey));
                                task.setTask(mapKey);
                                taskList.add(task);
                            }
                            TaskListAdapter taskListAdapter = new TaskListAdapter(taskList);
                            taskListAdapter.setProfile(true);
                            ProfileViewActivity.this.taskList.setAdapter(taskListAdapter);
                            taskListAdapter.setListener(new TaskListAdapter.DoneWithTask() {
                                @Override
                                public void onDoneWithTask(int position) {
                                    Toast.makeText(ProfileViewActivity.this, "Done with task in ProfileViewActivity", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNotDoneWithTask(int position) {
                                    Toast.makeText(ProfileViewActivity.this, "Not done with task in profileViewActivity", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileViewActivity.this, "error in downloading tasks in ProfileViewActivity", Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
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
            setTask(year,month,dayOfMonth,hourOfDay,minute);
            Toast.makeText(ProfileViewActivity.this,"Time and Date set to: " + dayOfMonth + "/" + month + "/" + year + " " + hourOfDay + ":" + minute,Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(ProfileViewActivity.this,"Time cannot be set to prior time",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void newTaskCancelled() {
        Toast.makeText(ProfileViewActivity.this,"new Task will not be set",Toast.LENGTH_SHORT).show();
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
    }
}

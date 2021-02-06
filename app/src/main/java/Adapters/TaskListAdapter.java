package Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import BroadcastReceiver.AlarmReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rishonlovesanimals.ProfileActivity;
import com.example.rishonlovesanimals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import Services.DownloadTasksService;
import Tasks.Task;

public class TaskListAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<Task> tasks;
    private DatabaseReference database;
    private HashMap<String,Object> checked;
    private BroadcastReceiver taskListFromService;
    private boolean profile = false;

    public interface DoneWithTask{
        void onDoneWithTask(int position);
        void onNotDoneWithTask(int position);
    }
    private DoneWithTask callback;

    public void setListener(DoneWithTask listener)
    {
        callback = listener;
    }

    public void setTasks(ArrayList<Task>tasks){
        this.tasks = tasks;
    }

    public TaskListAdapter(ArrayList<Task> tasks){
        this.tasks = tasks;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        checked = new HashMap<>();
        if(user!=null) {
            database = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/tasks");
        }
    }

    public void setProfile(boolean profile){this.profile = profile;}

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            convertView = layoutInflater.inflate(R.layout.task_cell, parent, false);
            TextView whatToDo = convertView.findViewById(R.id.task_cell_TV);
            CheckBox taskDone = convertView.findViewById(R.id.task_cell_CB);
            TextView time = convertView.findViewById(R.id.task_cell_Time);
            taskDone.setTag(position);
            whatToDo.setText(tasks.get(position).getTask());
            //taskDone.setOnCheckedChangeListener(this);
            taskDone.setOnClickListener(this);
            taskDone.setChecked(tasks.get(position).isDone());
            time.setText(tasks.get(position).getTaskTime());
            //should set alarm for nearest task
            if(profile){
                taskDone.setVisibility(View.GONE);
            }

        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        CheckBox cb = v.findViewById(R.id.task_cell_CB);
        int position = (Integer)cb.getTag();
        if(!cb.isChecked())
        {
            tasks.get(position).setDone(false);
            cb.setChecked(false);
            callback.onNotDoneWithTask(position);
        }
        else
        {
            tasks.get(position).setDone(true);
            cb.setChecked(true);
            callback.onDoneWithTask(position);
            //should delete information from database

        }
        database.updateChildren(checked);
    }


}

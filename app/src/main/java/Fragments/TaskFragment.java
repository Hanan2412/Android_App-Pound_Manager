package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.rishonlovesanimals.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class TaskFragment extends DialogFragment {

    public interface TaskFragmentListener{
        void newTask(String taskName,int day,int month,int year,int hour,int minute);
        void newTaskCancelled();
    }

    private TaskFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (TaskFragmentListener)context;
        }catch (ClassCastException e){
            e.printStackTrace();
            throw new ClassCastException("activity must implement TaskFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.new_task_fragment_layout,null);
        final TextInputEditText taskName = view.findViewById(R.id.new_task_fragment_layout_taskName);
        final DatePicker datePicker = view.findViewById(R.id.taskDatePicker);
        final TimePicker timePicker = view.findViewById(R.id.taskTimePicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        timePicker.setIs24HourView(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getResources().getString(R.string.add_task), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(taskName.getText()!=null )
                    listener.newTask(taskName.getText().toString(),datePicker.getDayOfMonth(),datePicker.getMonth(),datePicker.getYear(),timePicker.getHour(),timePicker.getMinute());
                else
                    listener.newTask(getResources().getString(R.string.name_required),0,0,0,0,0);

            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.newTaskCancelled();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(true);
        getDialog().setCancelable(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

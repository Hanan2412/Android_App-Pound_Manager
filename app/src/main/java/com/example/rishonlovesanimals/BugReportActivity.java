package com.example.rishonlovesanimals;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class BugReportActivity extends AppCompatActivity implements Runnable{

    private EditText bugText;
    private Button reportOk;
    private boolean bug = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bug_report);
        bugText = findViewById(R.id.bugReport);
        reportOk = findViewById(R.id.reportOk);
        reportOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("app/bug_report");
                HashMap<String,Object>map = new HashMap<>();
                map.put("bugReport " + System.currentTimeMillis(),bugText.getText().toString());
                map.put("UID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                reference.updateChildren(map);
                bugText.setText("");
            }
        });
        Thread thread = new Thread(this);
        thread.setName("checkTextNot0");
        thread.start();
    }

    @Override
    public void run() {
        while(bug){
            if(bugText.getText().toString().equals(""))
                reportOk.setClickable(false);
            else
                reportOk.setClickable(true);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bug = false;
    }
}

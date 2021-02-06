package com.example.rishonlovesanimals;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SuggestionActivity extends AppCompatActivity implements Runnable {

    private  TextView suggestion;
    private Button button;
    private boolean sug = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestions);
        suggestion = findViewById(R.id.suggestionText);
        button = findViewById(R.id.suggestionOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("app/suggestion");
                HashMap<String,Object>map = new HashMap<>();
                map.put("UID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                map.put("Suggestion " + System.currentTimeMillis(),suggestion.getText().toString());
                reference.updateChildren(map);
            }
        });
        Thread thread = new Thread(this);
        thread.setName("suggestionThread");
        thread.start();
    }

    @Override
    public void run() {
        while(sug)
        {
            if(suggestion.getText().toString().equals(""))
                button.setClickable(false);
            else
                button.setClickable(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sug = false;
    }
}

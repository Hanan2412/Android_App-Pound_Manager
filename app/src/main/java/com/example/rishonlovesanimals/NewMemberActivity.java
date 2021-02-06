package com.example.rishonlovesanimals;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class NewMemberActivity extends AppCompatActivity {

    TextInputEditText email,name,age;
    TextInputEditText password;//,position;
    private Spinner positionSpinner;
    private ArrayAdapter<CharSequence>arrayAdapter;
    private String user_position = "volunteer";



   //private String userId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_member_layout);
         email = findViewById(R.id.new_member_email);
         password = findViewById(R.id.new_member_password);
         //position = findViewById(R.id.new_member_position);
        name = findViewById(R.id.new_member_name);
        age = findViewById(R.id.new_member_age);
        positionSpinner = findViewById(R.id.new_member_position_spinner);
        arrayAdapter = ArrayAdapter.createFromResource(this,R.array.position,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(arrayAdapter);
        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user_position = (String) parent.getItemAtPosition(position);
                Toast.makeText(NewMemberActivity.this,"position chosen: " + user_position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private String IdGenerator()
    {
        //gets the number of people with accounts and creates a new id based on that number
        return "tmp";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_member_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_new_member)
        {
            //update firebase here
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if(email.getText()!=null && password.getText()!=null)
            {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(NewMemberActivity.this,"new member was added successfully",Toast.LENGTH_SHORT).show();
                            if(task.getResult()!=null)
                                setUserData(Objects.requireNonNull(task.getResult().getUser()).getUid());
                            mAuth.signOut();
                            LogInToCurrentUser();
                        }
                        else
                            Toast.makeText(NewMemberActivity.this,"failed to create a new member",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            }

        return super.onOptionsItemSelected(item);
    }

    private void LogInToCurrentUser()
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("details",MODE_PRIVATE);
        String password = sharedPreferences.getString("password","000000");
        String email = sharedPreferences.getString("email","example@gmail.com");
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(NewMemberActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    System.out.println("reSigned in");
                else
                    System.out.println("error in reSigned in");
            }
        });
    }

    private void setUserData(final String userId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + userId + "/userData");
        HashMap<String, Object> hashMap = new HashMap<>();
        if (name.getText()!=null && age.getText()!=null) {
            hashMap.put("position", user_position);
            hashMap.put("name",name.getText().toString());
            if(age!=null && age.getText()!=null)
                hashMap.put("age",age.getText().toString());
            else
                hashMap.put("age",1);
            reference.setValue(hashMap);
        }
    }
}

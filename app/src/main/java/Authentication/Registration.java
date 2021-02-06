package Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rishonlovesanimals.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import Fragments.RegistrationFragment;

public class Registration extends AppCompatActivity implements RegistrationFragment.RegistrationDialogListener {
    private FirebaseAuth mAuth;
    private String email,password;
    private String TAG  = "TASK RESULT";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        createAccount();
    }
    private void createAccount()//gets the email and password from the new user
    {
        RegistrationFragment registrationFragment = new RegistrationFragment();
        String registerFragmentTag = "REGISTER FRAGMENT";
        registrationFragment.show(getSupportFragmentManager(), registerFragmentTag);
    }

    @Override
    public void onRegisterPositiveButtonClicked(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d(TAG,"user created successfully");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(Registration.this, MainActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                }else
                {
                    System.out.println("user creation failed" + task.getException());
                    Log.w(TAG,"user creation failed",task.getException());
                    Toast.makeText(Registration.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRegisterNegativeButtonClicked() {
       Toast.makeText(Registration.this,"registration cancelled",Toast.LENGTH_SHORT).show();
       finish();
    }
}

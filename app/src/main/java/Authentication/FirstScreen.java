package Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rishonlovesanimals.MainActivity;
import com.example.rishonlovesanimals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Fragments.RegistrationFragment;
import Fragments.SignInFragment;

public class FirstScreen extends AppCompatActivity implements SignInFragment.SignInFragmentListener, RegistrationFragment.RegistrationDialogListener{
    private FirebaseAuth mAuth;
    private String TAG  = "TASK RESULT";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen_layout);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button registerBtn = findViewById(R.id.registerBtn);
        mAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment signInFragment = new SignInFragment();
                String TAG = "LOGIN_TAG";
                signInFragment.show(getSupportFragmentManager(),TAG);
                /*Intent loginIntent = new Intent(FirstScreen.this, Login.class);
                startActivity(loginIntent);*/
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegistrationFragment registrationFragment = new RegistrationFragment();
                String registerFragmentTag = "REGISTER FRAGMENT";
                registrationFragment.show(getSupportFragmentManager(), registerFragmentTag);
                /*Intent registerIntent = new Intent(FirstScreen.this, Registration.class);
                startActivity(registerIntent);*/
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checks if user is already signed in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            Intent intent = new Intent(FirstScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
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
                    Intent intent = new Intent(FirstScreen.this, MainActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                }else
                {
                    System.out.println("user creation failed" + task.getException());
                    Log.w(TAG,"user creation failed",task.getException());
                    Toast.makeText(FirstScreen.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onRegisterNegativeButtonClicked() {
        Toast.makeText(FirstScreen.this,"registration cancelled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSingInPositiveButtonClicked(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("Login: sign in was successful");
                    SharedPreferences sharedPreferences = getSharedPreferences("details",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email",email);
                    editor.putString("password",password);
                    editor.apply();
                    //Log.d(TAG, "Login: sign in was successful");
                    Intent intent = new Intent(FirstScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    //Log.w(TAG,"Login: sign in wasn't successful");
                    System.out.println("Login: sign in was not successful");
                    Toast.makeText(FirstScreen.this,"failed to log in",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onSignInNegativeButtonClicked() {
        Toast.makeText(FirstScreen.this,"Sign in cancelled",Toast.LENGTH_SHORT).show();
    }
}

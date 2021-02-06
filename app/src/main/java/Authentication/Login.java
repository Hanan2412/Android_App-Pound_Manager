package Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Fragments.SignInFragment;

public class Login extends AppCompatActivity implements SignInFragment.SignInFragmentListener {
    private FirebaseAuth mAuth;
    private String email,password;
    private String TAG  = "TASK RESULT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        getLogInInfo();
    }
    private void getLogInInfo()
    {
        SignInFragment signInFragment = new SignInFragment();
        String TAG = "LOGIN_TAG";
        signInFragment.show(getSupportFragmentManager(),TAG);
    }

    @Override
    public void onSingInPositiveButtonClicked(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "Login: sign in was successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Log.w(TAG,"Login: sign in wasn't successful");
                    Toast.makeText(Login.this,"failed to log in",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onSignInNegativeButtonClicked() {
        Toast.makeText(Login.this,"Sign in cancelled",Toast.LENGTH_SHORT).show();
    }
}

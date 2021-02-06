package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rishonlovesanimals.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignInFragment extends DialogFragment {

    public interface SignInFragmentListener{
        void onSingInPositiveButtonClicked(String email, String password);
        void onSignInNegativeButtonClicked();
    }
    SignInFragmentListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            callback = (SignInFragmentListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException("SignInFragmentListener must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_log_in_layout,null);
        final TextInputEditText email = view.findViewById(R.id.fragment_LogIn_email);
        final TextInputEditText password = view.findViewById(R.id.fragment_LogIn_password);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(email.getText()!=null && password.getText()!=null)
                    callback.onSingInPositiveButtonClicked(email.getText().toString(),password.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onSignInNegativeButtonClicked();
            }
        });
        builder.setView(view);
        return builder.create();
    }
}

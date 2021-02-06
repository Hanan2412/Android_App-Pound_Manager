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

public class RegistrationFragment extends DialogFragment {

    public interface RegistrationDialogListener{
        void onRegisterPositiveButtonClicked(String email, String password);
        void onRegisterNegativeButtonClicked();
    }
    RegistrationDialogListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            callback = (RegistrationDialogListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException("RegistrationDialogListener interface must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_registar_layout,null);
        final TextInputEditText email = view.findViewById(R.id.fragment_register_email);
        final TextInputEditText password = view.findViewById(R.id.fragment_register_password);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(email.getText()!=null && password.getText()!=null)
                    callback.onRegisterPositiveButtonClicked(email.getText().toString(),password.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onRegisterNegativeButtonClicked();
            }
        });
        builder.setView(view);
        return builder.create();
    }
}

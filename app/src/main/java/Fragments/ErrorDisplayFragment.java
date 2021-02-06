package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rishonlovesanimals.R;

/*
This fragment is purely to display an error to the user when something goes wrong
it should be used in every possibility of an error instead of a toast message
 */
public class ErrorDisplayFragment extends DialogFragment {


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.error_layout,null);
        TextView errorTextView = view.findViewById(R.id.errorText);
        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if(bundle!=null)
        {
            errorTextView.setText(bundle.getString("errorMessage"));
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //does nothing
                }
            });
            builder.setView(view);
        }
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}

package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rishonlovesanimals.R;
import com.google.android.material.textfield.TextInputEditText;


public class SearchAnimalFragment extends DialogFragment {

    private boolean search_by;
    public SearchAnimalFragment() {search_by = true;}

    public interface SearchDecision{
        void onStartSearching(String searchQuery,boolean search_by);
        void onSearchCancelled();
    }

    private SearchDecision search;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            search = (SearchDecision) context;
        }catch (ClassCastException c){
            throw new ClassCastException("Activity must implement SearchAnimalFragment interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.search_animal_fragment_layout,null);
        final TextInputEditText textInputEditText = view.findViewById(R.id.search_animal_tv);
        RadioGroup radioGroup = view.findViewById(R.id.search_radio_btn);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.search_by_breed_rb:{
                        Toast.makeText(requireContext(), "Choose search by breed", Toast.LENGTH_SHORT).show();
                        search_by = false;
                        break;
                    }
                    case R.id.search_by_name_rb:{
                        Toast.makeText(requireContext(), "Choose search by name", Toast.LENGTH_SHORT).show();
                        search_by = true;
                        break;
                    }
                    case -1:{
                        Toast.makeText(requireContext(), "Cleared all", Toast.LENGTH_SHORT).show();
                    }
                    default:{
                        Toast.makeText(requireContext(), "Error in search animal fragment", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(textInputEditText.getText()!=null)
                            search.onStartSearching(textInputEditText.getText().toString(),search_by);
                        else
                            Toast.makeText(getContext(), "you must write something for me to search", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                search.onSearchCancelled();
            }
        }).setCancelable(true);
        return builder.create();
    }




}

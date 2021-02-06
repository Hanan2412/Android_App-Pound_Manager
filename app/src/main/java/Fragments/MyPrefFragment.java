package Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;
import com.example.rishonlovesanimals.R;

public class MyPrefFragment extends PreferenceFragment {
   /* @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }*/
    public MyPrefFragment(){}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

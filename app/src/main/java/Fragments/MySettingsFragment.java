package Fragments;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.rishonlovesanimals.R;

public class MySettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);
        addPreferencesFromResource(R.xml.preferences);
    }
}

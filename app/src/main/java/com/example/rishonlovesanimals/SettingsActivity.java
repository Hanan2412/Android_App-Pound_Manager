package com.example.rishonlovesanimals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import Fragments.MyPrefFragment;
import Fragments.MySettingsFragment;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportFragmentManager().beginTransaction().replace(R.id.settings_container,new MySettingsFragment()).commit();
        getFragmentManager().beginTransaction().add(android.R.id.content,new MyPrefFragment()).commit();

    }

}

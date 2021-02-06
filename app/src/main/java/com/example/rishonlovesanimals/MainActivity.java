package com.example.rishonlovesanimals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import Authentication.FirstScreen;
import Enums.Tabs;
import Fragments.SearchAnimalFragment;
import Fragments.TabFragment;


public class MainActivity extends AppCompatActivity implements TabFragment.FragmentListener, SearchAnimalFragment.SearchDecision {

    private DrawerLayout drawerLayout;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private View headerView;
    private final int SETTINGS_REQUEST = 4;
    private Profile profile;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = findViewById(R.id.coordinator1);
        TabLayout tabLayout = findViewById(R.id.tabs_layout);
        viewPager = findViewById(R.id.viewPager);
        NavigationView navigationView = findViewById(R.id.navigationView);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        headerView = navigationView.getHeaderView(0);
        fab = findViewById(R.id.floatingActionButton);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.viewProfile: {
                        /*opens the profile page*/
                        Intent intent = new Intent(MainActivity.this, ProfileViewActivity.class);
                        TextView name = headerView.findViewById(R.id.header_name);
                        TextView position = headerView.findViewById(R.id.header_position);
                        intent.putExtra("profile_name", name.getText().toString());
                        intent.putExtra("profile_position", position.getText().toString());
                        intent.putExtra("currentUser", true);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    }
                    case R.id.newMember: {
                        /*open a page to add a new member*/
                        if (profile.getProfile_position().equals("admin")) {
                            Intent intent = new Intent(MainActivity.this, NewMemberActivity.class);
                            startActivity(intent);
                        } else
                            Snackbar.make(coordinatorLayout, getResources().getString(R.string.only_admin), Snackbar.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    }
                    case R.id.settings: {
                        /*opens the preferences page*/
                        startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), SETTINGS_REQUEST);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    }
                    case R.id.disconnect: {
                        FirebaseAuth.getInstance().signOut();
                        Snackbar.make(coordinatorLayout, R.string.dc, Snackbar.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, FirstScreen.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case R.id.arriving: {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        if (user != null) {
                            DatabaseReference reference = database.getReference("users/" + user.getUid() + "/userData");
                            HashMap<String, Object> userData = new HashMap<>();
                            boolean b = !item.isChecked();
                            userData.put("arriving", String.valueOf(b));
                            reference.updateChildren(userData);
                            if (item.isChecked())
                                item.setChecked(false);
                            else
                                item.setChecked(true);
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    }
                    case R.id.openList: {
                        Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    }
                }

                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adding new animals to the list - opens a new window in witch you can add animal information and picture
                final Intent intent = new Intent(MainActivity.this, NewAnimalActivity.class);
                intent.putExtra("userUid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/userData");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            if (snapshot1.getKey() != null && snapshot1.getKey().equals("name")) {
                                if (snapshot1.getValue() != null) {
                                    intent.putExtra("name", snapshot1.getValue().toString());
                                    startActivity(intent);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0)
                    fab.hide();
                else
                    fab.show();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        profile = new Profile();
        if (user != null) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("users/" + user.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String age = ds.child("age").getValue(String.class);
                        String name = ds.child("name").getValue(String.class);
                        TextView header_name = headerView.findViewById(R.id.header_name);
                        header_name.setText(name);
                        /*TextView header_age = headerView.findViewById(R.id.header_age);
                        header_age.setText(age);*/
                        TextView header_position = headerView.findViewById(R.id.header_position);
                        header_position.setText(ds.child("position").getValue(String.class));
                        profile.setProfile_position(ds.child("position").getValue(String.class));
                        profile.setProfile_name(name);
                        profile.setProfile_age(age);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("failed to read value");
                }
            });
            String email = user.getEmail();
            profile.setEmail(email);
        } else
            Snackbar.make(coordinatorLayout, "couldn't retrieve user information", Snackbar.LENGTH_SHORT).show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            }
            case R.id.search_animal: {
                viewPager.setCurrentItem(0, true);
                SearchAnimalFragment searchAnimalFragment = new SearchAnimalFragment();
                searchAnimalFragment.show(getSupportFragmentManager(), "Search Fragment");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST)//getting the settings selection and input data
        {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            Map<String, ?> preferenceMap = sp.getAll();
            Set<String> set = preferenceMap.keySet();
            Iterator<String> iterator = set.iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                switch (key) {
                    case "list_preference": {
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("Settings1").putExtra("cardsNumber", (String) preferenceMap.get(key)));
                        break;
                    }
                    case "autoUpdate": {
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("Settings1").putExtra("update", (Boolean) preferenceMap.get(key)));
                        break;
                    }
                    case "showInList": {
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("Settings1").putExtra("showInList", (Boolean) preferenceMap.get(key)));
                        break;
                    }
                    case "autoDoneTask": {
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("Settings1").putExtra("autoDoneTask", (Boolean) preferenceMap.get(key)));
                        break;
                    }
                    case "notifications": {
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("Settings1").putExtra("notification", (Boolean) preferenceMap.get(key)));
                        break;
                    }
                    default: {
                        Toast.makeText(MainActivity.this, "Settings Error", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }


    @Override
    public void onAnimalClicked(int position) {
        Snackbar.make(coordinatorLayout, "Clicked on animal", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onAnimalLongClicked(int position) {
        Snackbar.make(coordinatorLayout, "Long clicked on animal", Snackbar.LENGTH_SHORT).show();
    }

    ///////////////////////////////////////////////////////
    //Search fragment listener
    @Override
    public void onStartSearching(String searchQuery, boolean search_by) {
        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("Search query").putExtra("searchQuery", searchQuery).putExtra("search_by", search_by));
    }

    ///////////////////////////////////////////////////////
    //Search fragment listener
    @Override
    public void onSearchCancelled() {
        Snackbar.make(coordinatorLayout, "search cancelled", Snackbar.LENGTH_SHORT).show();
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return TabFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return Tabs.values().length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return Tabs.values()[position].toString();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("scrollToTasks")) {
            viewPager.setCurrentItem(1, true);
        }

    }
}
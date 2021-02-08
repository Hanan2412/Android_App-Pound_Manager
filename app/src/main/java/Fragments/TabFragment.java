package Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rishonlovesanimals.Profile;
import com.example.rishonlovesanimals.ProfileViewActivity;
import com.example.rishonlovesanimals.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Adapters.TaskListAdapter;
import Adapters.UserListAdapter;
import Animals.AnimalProfile;


import Services.DownloadDogsInformation;
import Enums.Tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import Animals.Dog;
import Adapters.DogsAdapter;

import Tasks.Task;

import BroadcastReceiver.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

//this class manages all the tabs in the app
public class TabFragment extends Fragment {

    private DogsAdapter adapter;
    private ArrayList<Dog> dogsList;
    private List<Profile> users;
    private ListView listView;
    private UserListAdapter userListAdapter;
    private View view;
    private String position;
    private int currentUserPosition = 0;
    private BroadcastReceiver dogsInfoBroadcast;
    private boolean createNotification = false;
    private BroadcastReceiver searchReceiver;
    private Intent dogsInfoService;

    private TaskListAdapter taskListAdapter;
    // private AnimalProfileListAdapter animalProfileListAdapter;

    private ArrayList<Task> taskArrayList;


    private boolean update = true;
    private boolean showInList = true;
    private boolean autoDoneWithTask = false;
    private int showOnScreen = 2;

    //listener for mainActivity to "see" what card was clicked on
    public interface FragmentListener {
        void onAnimalClicked(int position);

        void onAnimalLongClicked(int position);
    }

    FragmentListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("activity must implement FragmentListener interface");
        }
    }

    private TabFragment() {
    }

    public static TabFragment newInstance(int tabNumber) {
        TabFragment tabFragment = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("tabNumber", tabNumber);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        dogsList = new ArrayList<>();
        String currentUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DownloadCurrentUser(currentUserUid);
        DownloadSettingsConfiguration();
        SearchForAnimalBroadcast();
        settingsBroadcasts();
        if (getArguments() != null) {
            Tabs tabs = Tabs.values()[getArguments().getInt("tabNumber")];
            switch (tabs) {
                case animals: {
                    view = inflater.inflate(R.layout.recycle_view_layout, container, false);
                    RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setItemViewCacheSize(20);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    callForDogsInfoBroadcast();
                    dogsInfoService = new Intent(getContext(), DownloadDogsInformation.class);
                    requireContext().startService(dogsInfoService);
                    //DownloadInformation();
                    adapter = new DogsAdapter(dogsList);
                    adapter.setListener(new DogsAdapter.DogListener() {
                        @Override
                        public void onDogClicked(int position, View view) {
                            //Intent intent = new Intent(getContext(), AnimalInfo.class);
                            Intent intent = new Intent(getContext(), AnimalProfile.class);
                            intent.putExtra("animalName", dogsList.get(position).getName());
                            intent.putExtra("animalAge", dogsList.get(position).getAge());
                            intent.putExtra("animalKind", dogsList.get(position).getKind());
                            intent.putExtra("animalMedical", dogsList.get(position).getMedical());
                            intent.putExtra("animalHistory", dogsList.get(position).getHistory());
                            intent.putExtra("animalNote", dogsList.get(position).getNotes());
                            intent.putExtra("pictureLink", dogsList.get(position).getImageUri());
                            startActivity(intent);
                            //callback.onAnimalClicked(position);
                        }

                        @Override
                        public void onDogLongClicked(int position, View view) {
                            //callback.onAnimalLongClicked(position);
                        }
                    });
                    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                            if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
                                final RecyclerView.ViewHolder viewHolder1 = viewHolder;
                                if (position != null && (position.equals("admin") || position.equals("Admin"))) {
                                    AlertDialog.Builder areYouSureDialog = new AlertDialog.Builder(requireContext());
                                    areYouSureDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int position = viewHolder1.getAdapterPosition();
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("animals/");
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                            StorageReference pictureReference = storageReference.child("animal_pictures/" + parsePictureName(dogsList.get(position).getFireBasePath()) + "." + "");
                                            pictureReference.delete();//removes the picture from the storage
                                            reference.child(dogsList.get(position).getFireBasePath()).removeValue();//removes the data from the database
                                            dogsList.remove(position);
                                            adapter.setDogsList(dogsList);
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getContext(), R.string.deletion_cancel, Toast.LENGTH_SHORT).show();
                                        }
                                    }).setTitle(R.string.confirm_del)
                                            .setCancelable(true)
                                            .setMessage(R.string.are_you_sure)
                                            .show();
                                } else {
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), getResources().getString(R.string.only_admin), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    };
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                    recyclerView.setAdapter(adapter);
                    break;
                }

                case tasks: {
                    view = inflater.inflate(R.layout.task_layout, container, false);
                    listView = view.findViewById(R.id.taskList);
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DownloadTasks(currentUserUid);
                    break;
                }
                case users: {
                    view = inflater.inflate(R.layout.users_list_layout, container, false);
                    listView = view.findViewById(R.id.users_list);
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DownloadAllUsers(currentUserUid);
                    users = new ArrayList<>();
                    userListAdapter = new UserListAdapter(users);
                    listView.setAdapter(userListAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //final Intent openUserProfile = new Intent(getActivity(), ProfileActivity.class);
                            Intent openUserProfile = new Intent(getActivity(), ProfileViewActivity.class);
                            openUserProfile.putExtra("profile_name", users.get(position).getProfile_name());
                            openUserProfile.putExtra("profile_age", users.get(position).getProfile_age());
                            openUserProfile.putExtra("profile_arriving", users.get(position).getArriving());
                            openUserProfile.putExtra("profile_position", users.get(position).getProfile_position());
                            openUserProfile.putExtra("Uid", users.get(position).getProfile_id());
                            openUserProfile.putExtra("admin", TabFragment.this.position);
                            if (currentUserPosition == position) {
                                Toast.makeText(requireContext(), "pressed on current user " + users.get(position).getProfile_name(), Toast.LENGTH_SHORT).show();
                                openUserProfile.putExtra("currentUser", true);
                            } else {
                                Toast.makeText(requireContext(), "pressed on another user " + users.get(position).getProfile_name(), Toast.LENGTH_SHORT).show();
                                openUserProfile.putExtra("currentUser", false);
                            }
                            startActivity(openUserProfile);
                        }
                    });
                    userListAdapter.notifyDataSetChanged();
                }
            }
        }
        return view;
    }

    @SuppressWarnings("unchecked")
    private void DownloadCurrentUser(String currentUserUid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + currentUserUid + "/userData");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                if (map != null) {
                    //senderName = (String) map.get("name");
                    position = (String) map.get("position");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "Error in downloading current user in tabFragment");
                error.toException().printStackTrace();
            }
        });
    }


    private void setAlarm() {
        if (!taskArrayList.isEmpty()) {
            if (taskArrayList.get(0) != null) {
                String taskTime = taskArrayList.get(0).getTaskTime();
                if (!taskTime.equals("none")) {
                    Intent broadcastIntent = new Intent(getActivity(), AlarmReceiver.class);
                    broadcastIntent.putExtra("title", "stuff to do");
                    broadcastIntent.putExtra("content", taskArrayList.get(0).getTask());
                    PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(getContext(), 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(ALARM_SERVICE);

                    StringBuilder day = new StringBuilder();
                    StringBuilder month = new StringBuilder();
                    StringBuilder year = new StringBuilder();
                    StringBuilder hour = new StringBuilder();
                    StringBuilder minute = new StringBuilder();
                    int i;
                    for (i = 0; taskTime.charAt(i) != '/'; i++) {
                        day.append(taskTime.charAt(i));
                    }
                    for (i += 1; taskTime.charAt(i) != '/'; i++) {
                        month.append(taskTime.charAt(i));
                    }
                    for (i += 1; taskTime.charAt(i) != 'a'; i++) {
                        if (taskTime.charAt(i) != ' ')
                            year.append(taskTime.charAt(i));
                    }
                    i++;
                    for (i += 1; taskTime.charAt(i) != ':'; i++) {
                        if (taskTime.charAt(i) != ' ')
                            hour.append(taskTime.charAt(i));
                    }
                    for (i += 1; i < taskTime.length(); i++) {
                        minute.append(taskTime.charAt(i));
                    }
                    Calendar calendar = Calendar.getInstance();
                    if (calendar.get(Calendar.YEAR) == Integer.parseInt(year.toString())) {
                        if (calendar.get(Calendar.MONTH) <= Integer.parseInt(month.toString()) - 1) {
                            if (calendar.get(Calendar.DAY_OF_MONTH) <= Integer.parseInt(day.toString()))
                                if (calendar.get(Calendar.HOUR_OF_DAY) <= Integer.parseInt(hour.toString()))
                                    if (calendar.get(Calendar.MINUTE) <= Integer.parseInt(minute.toString())) {
                                        System.out.println("date check works");
                                        calendar.set(Calendar.MINUTE, Integer.parseInt(minute.toString()));
                                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.toString()));
                                        calendar.set(Calendar.YEAR, Integer.parseInt(year.toString()));
                                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.toString()));
                                        calendar.set(Calendar.MONTH, Integer.parseInt(month.toString()) - 1);
                                        calendar.set(Calendar.SECOND, 0);
                                        long time = getFirstTask();
                                        if (alarmManager != null) {
                                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, "TimeTag", new AlarmManager.OnAlarmListener() {
                                                @Override
                                                public void onAlarm() {

                                                }
                                            }, null);
                                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, broadcastPendingIntent);
                                            Toast.makeText(getContext(), "setAlarm is set", Toast.LENGTH_LONG).show();
                                        }
                                    }
                        }
                    }

                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void DownloadTasks(final String currentUserUid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + currentUserUid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> map;
                final ArrayList<Task> taskList = new ArrayList<>();
                taskArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    map = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (map != null && dataSnapshot.getKey() != null) {
                        if (dataSnapshot.getKey().equals("tasks")) {
                            Set<String> mapKeys = map.keySet();
                            for (String mapKey : mapKeys) {
                                Task task = new Task();
                                task.setTaskTime((String) map.get(mapKey));
                                task.setTask(mapKey);
                                taskList.add(task);
                                taskArrayList.add(task);
                            }
                        }
                    }
                }
                taskListAdapter = new TaskListAdapter(taskList);
                listView.setAdapter(taskListAdapter);
                taskListAdapter.setListener(new TaskListAdapter.DoneWithTask() {
                    @Override
                    public void onDoneWithTask(int position) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + currentUserUid + "/tasks/" + taskList.get(position).getTask());
                        databaseReference.removeValue();
                        taskList.remove(position);
                        taskArrayList.remove(position);
                        taskListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNotDoneWithTask(int position) {
                        Toast.makeText(requireContext(), "Not done with task in profile activity", Toast.LENGTH_SHORT).show();
                    }
                });
                setAlarm();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressWarnings("unchecked")
    private void DownloadAllUsers(final String currentUserUid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, Object> map;
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey() != null && snapshot.getKey() != null) {
                        if (dataSnapshot.getKey().equals("userData")) {

                            map = (HashMap<String, Object>) dataSnapshot.getValue();
                            Profile profile = new Profile();
                            if (map != null) {
                                profile.setProfile_name((String) map.get("name"));
                                profile.setProfile_position((String) map.get("position"));
                                profile.setProfile_age((String) map.get("age"));
                                profile.setArriving((String) map.get("arriving"));
                                profile.setProfile_id(snapshot.getKey());
                                users.add(profile);
                                userListAdapter.notifyDataSetChanged();
                                if (snapshot.getKey().equals(currentUserUid)) {
                                    currentUserPosition = counter;
                                }
                                counter++;
                            }
                        }
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Toast.makeText(requireContext(), "User data was changed successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Toast.makeText(requireContext(), "User was removed successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(requireView(), "An error has occurred", Snackbar.LENGTH_SHORT).setAction("Submit feedback", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //should send an error report to server
                    }
                }).show();
                Toast.makeText(requireContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
            }
        });
    }


    private void callForDogsInfoBroadcast() {
        dogsInfoBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dogsList = intent.getParcelableArrayListExtra("dogsList");
                if (adapter != null) {
                    adapter.setDogsList(dogsList);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(dogsInfoBroadcast, new IntentFilter("dogsInfo"));
    }

    private void SearchForAnimalBroadcast() {
        searchReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (dogsList != null && !dogsList.isEmpty()) {
                    String searchQuery = intent.getStringExtra("searchQuery");
                    boolean search_by = intent.getBooleanExtra("search_by", true);
                    for (Dog dog : dogsList) {
                        if (search_by) {
                            if (dog.getName().equals(searchQuery)) {
                                ArrayList<Dog> searchDog = new ArrayList<>();
                                searchDog.add(dog);
                                adapter.setDogsList(searchDog);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        } else {
                            if (dog.getKind().equals(searchQuery)) {
                                ArrayList<Dog> searchDog = new ArrayList<>();
                                searchDog.add(dog);
                                adapter.setDogsList(searchDog);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(searchReceiver, new IntentFilter("Search query"));
    }

    private void settingsBroadcasts()//all the settings changes are received here
    {
        //opens a simple error fragment to show the user that an error has happened
        BroadcastReceiver settingsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle intentBundle = intent.getExtras();
                if (intentBundle != null) {
                    Set<String> keys = intentBundle.keySet();
                    Iterator<String> iterator = keys.iterator();
                    //noinspection WhileLoopReplaceableByForEach
                    while (iterator.hasNext()) {
                        String settingsChange = iterator.next();
                        if (settingsChange != null)
                            switch (settingsChange) {
                                case "update": {
                                    if (TabFragment.this.update) {
                                        if (!intent.getBooleanExtra("update", true)) {
                                            requireContext().stopService(dogsInfoService);
                                            TabFragment.this.update = false;
                                        }
                                    } else {
                                        if (intent.getBooleanExtra("update", true)) {
                                            requireContext().startService(dogsInfoService);
                                            TabFragment.this.update = true;
                                        }
                                    }
                                    break;
                                }
                                case "showInList": {
                                    TabFragment.this.showInList = intent.getBooleanExtra("showInList", true);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/userSettings/");
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("showMe", showInList);
                                    reference.updateChildren(map);
                                    break;
                                }
                                case "autoDoneTask": {
                                    TabFragment.this.autoDoneWithTask = intent.getBooleanExtra("autoDoneTask", true);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/userSettings/");
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("autoDone", autoDoneWithTask);
                                    reference.updateChildren(map);
                                    taskListAdapter.autoDone(TabFragment.this.autoDoneWithTask);
                                    break;
                                }
                                case "cardsNumber": {
                                    TabFragment.this.showOnScreen = intent.getIntExtra("cardsNumber", 0);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/userSettings/");
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("cardNumber", showOnScreen);
                                    reference.updateChildren(map);
                                    /*if(adapter!=null)
                                        adapter.changeCardSize(TabFragment.this.showOnScreen);*/
                                    break;
                                }
                                case "notification": {
                                    TabFragment.this.createNotification = intent.getBooleanExtra("notification", true);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/userSettings/");
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("notification", createNotification);
                                    reference.updateChildren(map);
                                    break;
                                }
                                default: {
                                    //opens a simple error fragment to show the user that an error has happened
                                    createAnError("error applying settings configuration");
                                }
                            }
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(settingsBroadcastReceiver, new IntentFilter("Settings1"));
    }


    private String parsePictureName(String fireBasePath)//picture name is the standAlone number in the animal entry line in firebase database
    {
        StringBuilder pictureName = new StringBuilder();
        for (int size = fireBasePath.length() - 1; fireBasePath.charAt(size) != ' '; size--) {
            pictureName.append(fireBasePath.charAt(size));
        }
        pictureName.reverse();
        return pictureName.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(dogsInfoBroadcast);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(searchReceiver);
    }

    private long getFirstTask() {

        ArrayList<Long> times = new ArrayList<>();
        StringBuilder day = new StringBuilder();
        StringBuilder month = new StringBuilder();
        StringBuilder year = new StringBuilder();
        StringBuilder hour = new StringBuilder();
        StringBuilder minute = new StringBuilder();
        for (int j = 0; j < taskArrayList.size(); j++) {
            day.setLength(0);
            month.setLength(0);
            year.setLength(0);
            hour.setLength(0);
            minute.setLength(0);
            String taskTime = taskArrayList.get(j).getTaskTime();
            int i;
            for (i = 0; taskTime.charAt(i) != '/'; i++) {
                day.append(taskTime.charAt(i));
            }
            for (i += 1; taskTime.charAt(i) != '/'; i++) {
                month.append(taskTime.charAt(i));
            }
            for (i += 1; taskTime.charAt(i) != 'a'; i++) {
                if (taskTime.charAt(i) != ' ')
                    year.append(taskTime.charAt(i));
            }
            i++;
            for (i += 1; taskTime.charAt(i) != ':'; i++) {
                if (taskTime.charAt(i) != ' ')
                    hour.append(taskTime.charAt(i));
            }
            for (i += 1; i < taskTime.length(); i++) {
                minute.append(taskTime.charAt(i));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(year.toString()));
            calendar.set(Calendar.MONTH, Integer.parseInt(month.toString()));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.toString()));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.toString()));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute.toString()));
            calendar.set(Calendar.SECOND, 0);
            times.add(calendar.getTimeInMillis());
        }
        Object[] time = times.toArray();
        Arrays.sort(time);
        // @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong((String) time[0]));
        return calendar.getTimeInMillis();
        //return format.format(calendar.getTime());
    }

    private void DownloadSettingsConfiguration() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/userSettings/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey() != null) {
                    HashMap<String, Object> settingsMap;
                    //noinspection unchecked
                    settingsMap = (HashMap<String, Object>) snapshot.getValue();
                    if (settingsMap!=null) {
                        if (settingsMap.containsKey("autoDone")) {
                            TabFragment.this.autoDoneWithTask = (boolean) settingsMap.get("autoDone");

                        }
                        if(settingsMap.containsKey("cardNumber"))
                            TabFragment.this.showOnScreen = Integer.parseInt(String.valueOf(settingsMap.get("cardNumber")));
                        if(settingsMap.containsKey("showMe"))
                            TabFragment.this.showInList = (boolean)settingsMap.get("showMe");
                        if(settingsMap.containsKey("notification"))
                            TabFragment.this.createNotification = (boolean)settingsMap.get("notification");
                    }
                    else
                        createAnError("error retrieving settings from server");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void createAnError(String errorMessage) {
        String TAG = "errorFragment";
        ErrorDisplayFragment errorDisplayFragment = new ErrorDisplayFragment();
        Bundle errorMessageBundle = new Bundle();
        errorMessageBundle.putString("errorMessage", errorMessage);
        errorDisplayFragment.setArguments(errorMessageBundle);
        errorDisplayFragment.show(getParentFragmentManager(), TAG);
    }
}

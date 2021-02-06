package Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rishonlovesanimals.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DownloadUsersService extends Service implements Runnable{

    private ArrayList<Profile>users;
    private ArrayList<String>userKeys;
    private Thread downloadUsersThread;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadUsersThread = new Thread(this);
        downloadUsersThread.setName("downloadUsersThread");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        users = new ArrayList<>();
        userKeys = new ArrayList<>();
        if(downloadUsersThread.getState() == Thread.State.NEW)
            downloadUsersThread.start();
        else
            DownloadUsers();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void run() {
        DownloadUsers();
    }

    private void DownloadUsers()
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    String key = ds.getKey();
                    userKeys.add(key);
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("users/" + key + "/userData");
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Profile profile = new Profile();
                            for(DataSnapshot dataSnapshot: snapshot.getChildren())
                            {
                                String snapshotKey = dataSnapshot.getKey();
                                Object snapshotValue = dataSnapshot.getValue();
                                if(snapshotKey!=null && snapshotValue!=null) {
                                    switch (snapshotKey) {
                                        case "name":
                                            profile.setProfile_name(snapshotValue.toString());
                                            break;
                                        case "age":
                                            profile.setProfile_age(snapshotValue.toString());
                                            break;
                                        case "position":
                                            profile.setProfile_position(snapshotValue.toString());
                                            break;
                                        case "arriving":
                                            profile.setArriving(snapshotValue.toString());
                                    }
                                }
                            }
                            users.add(profile);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                BroadcastToActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void BroadcastToActivity()
    {
        Intent intent = new Intent("usersList");
        intent.putParcelableArrayListExtra("users",users);
        intent.putStringArrayListExtra("keys",userKeys);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

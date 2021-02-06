package Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Animals.Dog;

public class DownloadDogsInformation extends Service implements Runnable{

    private ArrayList<Dog> dogsList;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dogsList = new ArrayList<>();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread downloadDogsInformation = new Thread(this);
        downloadDogsInformation.setName("downloadDogsInformation");
        downloadDogsInformation.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    public void run() {
        DownloadInformation();
    }

    private void DownloadInformation()
    {
        dogsList.clear();
         databaseReference = FirebaseDatabase.getInstance().getReference("animals");
          valueEventListener = new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 dogsList.clear();
                 for(DataSnapshot dataSnapshot:snapshot.getChildren())
                 {
                     String name = dataSnapshot.child("name").getValue(String.class);
                     String age = dataSnapshot.child("age").getValue(String.class);
                     String medical = dataSnapshot.child("medical").getValue(String.class);
                     String history = dataSnapshot.child("history").getValue(String.class);
                     String type = dataSnapshot.child("type").getValue(String.class);
                     String pictureLink = dataSnapshot.child("PictureLink").getValue(String.class);
                     String path = dataSnapshot.child("path").getValue(String.class);
                     Dog dog = new Dog(name,type);
                     dog.setAge(age);
                     dog.setMedical(medical);
                     dog.setHistory(history);
                     dog.setImageUri(pictureLink);
                     dog.setFireBasePath(path);
                     dogsList.add(dog);
                 }
                 SendBroadcast();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {
                 System.out.println("error downloading information from database in adapter " + error.getMessage() +" " + error.toException());
             }
         };
         databaseReference.addValueEventListener(valueEventListener);
       /* databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dogsList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String age = dataSnapshot.child("age").getValue(String.class);
                    String medical = dataSnapshot.child("medical").getValue(String.class);
                    String history = dataSnapshot.child("history").getValue(String.class);
                    String type = dataSnapshot.child("type").getValue(String.class);
                    String pictureLink = dataSnapshot.child("PictureLink").getValue(String.class);
                    String path = dataSnapshot.child("path").getValue(String.class);
                    Dog dog = new Dog(name,type);
                    dog.setAge(age);
                    dog.setMedical(medical);
                    dog.setHistory(history);
                    dog.setImageUri(pictureLink);
                    dog.setFireBasePath(path);
                    dogsList.add(dog);
                }
                SendBroadcast();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error downloading information from database in adapter " + error.getMessage() +" " + error.toException());
            }
        });*/
    }

    private void SendBroadcast()
    {
        Intent intent = new Intent("dogsInfo");
        intent.putParcelableArrayListExtra("dogsList",dogsList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}

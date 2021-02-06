package Services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rishonlovesanimals.MainActivity;
import com.example.rishonlovesanimals.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import BroadcastReceiver.NotificationReceiver;
import Comunication.Chat;


@SuppressLint("SpecifyJobSchedulerIdRange")
public class JobChatBoardMessagingService extends JobService {

    private static final String TAG = "jobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"job started");
        doBackGroundWork(params);
        return true;
    }

    private void doBackGroundWork(final JobParameters params)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
            //thread downloads the information
                if(jobCancelled)
                    return;
                System.out.println("gayyyyyyyyyyyyyyyyyyyyyyyyyy");
                DownloadChatBoardMessages(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                //next line is when the thread is done
                jobFinished(params,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"job stopped before completion");
        jobCancelled = true;
        return true;//gonna try again
    }


    @SuppressWarnings("unchecked")
    private void DownloadChatBoardMessages(final String currentUserUid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, Object> map;
                Chat chat = new Chat();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    map = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (map != null) {
                        chat.setSenderName((String) map.get("senderName"));
                        chat.setSender((String) map.get("sender"));
                        chat.setIsSeen((boolean) map.get("isSeen"));
                        chat.setTimeCode((long) map.get("timeCode"));
                        long x = (long) map.get("type");
                        chat.setType(Math.toIntExact(x));
                        if (map.containsKey("readBy"))
                            chat.setReadBy((ArrayList<String>) map.get("readBy"));
                        //chat.setType(Math.toIntExact((Long)map.get("type")));
                        chat.setMessage((String) map.get("message"));
                    }
                }
                for (int i = 0; i < chat.getReadBy().size(); i++) {
                    if (chat.getSpecificUserReadBy(i).equals(currentUserUid))
                        chat.setIsSeen(true);
                }
                if (!chat.getIsSeen()) {
                    ChatBoardNotification(chat);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Toast.makeText(JobChatBoardMessagingService.this, "User was removed successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void ChatBoardNotification(Chat chat) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(JobChatBoardMessagingService.this, "new message")
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle("New Message")
                .setContentText(chat.getSenderName())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(chat.getMessage()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        CharSequence name = "Messages Channel";
        String description = "Notifies the user about new messages in the chat board";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel("new message", name, importance);
        notificationChannel.setDescription(description);
        NotificationManager notificationManager = JobChatBoardMessagingService.this.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);

            Intent intent = new Intent(JobChatBoardMessagingService.this, MainActivity.class);
            intent.putExtra("Pressed on Notification", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(JobChatBoardMessagingService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(JobChatBoardMessagingService.this);

            Intent markAsRead = new Intent(JobChatBoardMessagingService.this, NotificationReceiver.class);
            PendingIntent markAsReadPendingIntent = PendingIntent.getBroadcast(JobChatBoardMessagingService.this, 0, markAsRead, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.addAction(R.drawable.ic_baseline_done_all_24, "mark as read", markAsReadPendingIntent);
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE).setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            notificationManagerCompat.notify(100, builder.build());
        }


    }

}

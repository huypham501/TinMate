package com.hcmus.tinuni.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {
    private ValueEventListener valueEventListenerFriendRequest;
    private DatabaseReference databaseReferenceFriendRequest;

    private String  userId;

    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();

        // GET USER ID
        getUserId();

        valueEventListenerFriendRequest = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
//                DatabaseReference databaseReferenceFriendRequest =
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        };

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void getUserId() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

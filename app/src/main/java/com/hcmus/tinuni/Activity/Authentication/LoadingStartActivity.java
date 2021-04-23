package com.hcmus.tinuni.Activity.Authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Admin.AdminInitialActivity;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class LoadingStartActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_start);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager) LoadingStartActivity.this.getSystemService(Service.CONNECTIVITY_SERVICE);
                System.out.println("FIRSTTTTT");
                if (connectivityManager != null) {
                    if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
                        System.out.println("FIREBASE USERRRR");
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        moveActivity(LoadingStartActivity.this, MainActivity.class);
                                    } else {
                                        moveActivity(LoadingStartActivity.this, AdminInitialActivity.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            moveActivity(LoadingStartActivity.this, SignInActivity.class);
                        }
                    } else {
                        System.out.println("DIALOGGGGGG");
                    new AlertDialog.Builder(LoadingStartActivity.this)
                            .setTitle("DISCONNECTED")
                            .setMessage("You are not connect to the internet")
                            .setPositiveButton("OK", null)
                            .show();
                    }
                } else {
                    System.out.println("TOASTTTT");
                    Toast.makeText(LoadingStartActivity.this, "Error CONNECTIVITY SERVICE", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }
}
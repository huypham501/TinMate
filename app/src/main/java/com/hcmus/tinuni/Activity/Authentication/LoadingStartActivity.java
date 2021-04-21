package com.hcmus.tinuni.Activity.Authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Trace;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.internal.Sleeper;
import com.hcmus.tinuni.Activity.Admin.AdminInitialActivity;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.R;

public class LoadingStartActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_start);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread(new Loading(this));
        thread.start();

    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }

    class Loading implements Runnable {
        Context context;

        Loading(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();
                        if (FirebaseDatabase.getInstance().getReference("Users").child(userId) != null) {
                            moveActivity(LoadingStartActivity.this, MainActivity.class);
                        } else {
                            moveActivity(LoadingStartActivity.this, AdminInitialActivity.class);
                        }
                    } else {
                        moveActivity(LoadingStartActivity.this, SignInActivity.class);
                    }
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("DISCONNECTED")
                            .setMessage("You are not connect to the internet")
                            .setPositiveButton("OK", null);
                }
            } else {
                Toast.makeText(context, "Error CONNECTIVITY SERVICE", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
package com.hcmus.tinuni.Activity.Authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
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
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    moveActivity(LoadingStartActivity.this, MainActivity.class);
                } else {
                    moveActivity(LoadingStartActivity.this, SignInActivity.class);
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("DISCONNECTED")
                        .setMessage("You are not connect to the internet")
                        .setPositiveButton("OK", null);
            }
        } else {
            Toast.makeText(this, "Error CONNECTIVITY SERVICE", Toast.LENGTH_SHORT).show();
        }
    }

//    // [START auth_with_google]
//    private void signInWithGoogle(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                            DatabaseReference Ref = FirebaseDatabase.getInstance()
//                                    .getReference("Users")
//                                    .child(firebaseUser.getUid());
//
//                            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (!snapshot.exists()) {
//                                        // Storing account signed in with google into Realtime
//                                        // Create User class to put into Database
//                                        User user = new User(firebaseUser.getUid(),
//                                                firebaseUser.getDisplayName(),
//                                                firebaseUser.getEmail(),
//                                                firebaseUser.getPhotoUrl().toString(),
//                                                firebaseUser.getPhoneNumber(), "False");
//
//                                        Ref.setValue(user)
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> taskSetValue) {
//                                                        if (!taskSetValue.isSuccessful()) {
//                                                            // If sign in fails, display a message to the user.
//                                                            Toast.makeText(SignInActivity.this, "Sign in failed.",
//                                                                    Toast.LENGTH_SHORT).show();
////                                                            mProgressBar.setVisibility(View.INVISIBLE);
//                                                            alertDialog.dismiss();
//                                                        }
//                                                    }
//                                                });
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                }
//                            });
//
//                            moveActivity(SignInActivity.this, MainActivity.class);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(SignInActivity.this, task.getException().toString(),
//                                    Toast.LENGTH_SHORT).show();
////                            mProgressBar.setVisibility(View.INVISIBLE);
//                            alertDialog.show();
//                        }
//                    }
//                });
//    }
//    // [END auth_with_google]

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }
}
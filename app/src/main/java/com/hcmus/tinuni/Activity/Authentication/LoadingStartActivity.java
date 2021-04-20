package com.hcmus.tinuni.Activity.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.Model.User;
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
FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
firebaseUser.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            moveActivity(LoadingStartActivity.this, MainActivity.class);
        } else {
            moveActivity(LoadingStartActivity.this, SignInActivity.class);
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
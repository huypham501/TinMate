package com.hcmus.tinuni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends Activity {
    private TextInputLayout mEdtEmail, mEdtPassword, mEdtConfirmPassword, mEdtUsername;
    private Button mBtnGoBack, mBtnSignup;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize ID
        initializeID();

        // Initialize Firebase Authentication
        initializeFireBaseAuth();

    }

    private void initializeFireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }

    private void initializeID() {
        mEdtEmail = (TextInputLayout) findViewById(R.id.edtEmail);
        mEdtPassword = (TextInputLayout) findViewById(R.id.edtPassword);
        mEdtConfirmPassword = (TextInputLayout) findViewById(R.id.edtConfirmPassword);
        mEdtUsername = (TextInputLayout) findViewById(R.id.edtUsername);


        mBtnGoBack = (Button) findViewById(R.id.btnGoBack);
        mBtnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(SignUpActivity.this, SignInActivity.class);

            }
        });

        mBtnSignup = (Button) findViewById(R.id.btnSignUp);
        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEdtEmail.getEditText().getText().toString();
                final String password = mEdtPassword.getEditText().getText().toString();
                final String confirmPassword = mEdtConfirmPassword.getEditText().getText().toString();
                final String username = mEdtUsername.getEditText().getText().toString();

                if(TextUtils.isEmpty(email)) {
                    mEdtEmail.setError("Please fill in email!");
                } else if(TextUtils.isEmpty(username)) {
                    mEdtUsername.setError("Please fill in username!");
                } else if(TextUtils.isEmpty(password)){
                    mEdtPassword.setError("Please fill in password!");
                } else if(password.length() < 6) {
                    mEdtPassword.setError("6 chars minimum!");
                } else if(!TextUtils.equals(password, confirmPassword)){
                    // If sign up fails, display a message to the user.
                    mEdtConfirmPassword.setError("Password don't be matched. Please check again!");
                } else {
                    signUpAccount(email, password, username);
                }
            }
        });

    }

    private void signUpAccount(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information

                            // Get User ID in Realtime Database
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            mRef = FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(firebaseUser.getUid());

                            // Create HashMap to put into Database
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", firebaseUser.getUid());
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");

                            // Put into Database
                            mRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Sign up failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }
}
package com.hcmus.tinuni;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends Activity {
    private TextInputLayout mEdtEmail, mEdtPassword, mEdtConfirmPassword, mEdtUsername;
    private Button mBtnGoBack, mBtnSignup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
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
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
                return;
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

                if(password.length() < 6) {
                    mEdtPassword.setError("6 chars minimum!");
                } else {
                    if(confirmPassword.equals(password)) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, update UI with the signed-in user's information
                                    String userId = mAuth.getCurrentUser().getUid();
                                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                    currentUserDb.setValue(username);
                                } else {
                                    // If sign up fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Sign up failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(SignUpActivity.this, "Passwords don't match. Please check again!",
                                Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

    }
}
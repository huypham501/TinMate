package com.hcmus.tinuni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.User;

import java.util.HashMap;

public class SignUpActivity extends Activity {
    private TextInputLayout mEdtEmail, mEdtPassword, mEdtConfirmPassword, mEdtUsername;
    private Button mBtnGoBack, mBtnSignup, mBtnSignupGoogle;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    private ProgressBar mProgressBar;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize ID
        initializeID();

        // Initialize Firebase Authentication
        initializeFireBaseAuth();

        // [START config_signup]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

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
        mProgressBar = findViewById(R.id.progressBar);


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

                if (TextUtils.isEmpty(email)) {
                    mEdtEmail.setError("Please fill in email!");
                } else if (TextUtils.isEmpty(username)) {
                    mEdtUsername.setError("Please fill in username!");
                } else if (TextUtils.isEmpty(password)) {
                    mEdtPassword.setError("Please fill in password!");
                } else if (!TextUtils.equals(password, confirmPassword)) {
                    // If sign up fails, display a message to the user.
                    mEdtConfirmPassword.setError("Password don't be matched. Please check again!");
                } else {
                    signUpAccount(email, password, username);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mBtnSignupGoogle = (Button) findViewById(R.id.btnSignUpGoogle);
        mBtnSignupGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

    }

    private void signUpAccount(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                            User user = new User(firebaseUser.getUid(), username, email, "default");

                            // Put into Database
                            mRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            // If sign up fails, display a message to the user.
//                            Toast.makeText(SignUpActivity.this, "Sign up failed.",
//                                    Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthWeakPasswordException)
                                mEdtPassword.setError(task.getException().getMessage());
                            else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                mEdtEmail.setError(task.getException().getMessage());
                        }
                    }
                });
    }

    // [START signin]
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                signInWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign up failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void signInWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            DatabaseReference Ref = FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(firebaseUser.getUid());

                            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        // Storing account signed in with google into Realtime
                                        // Create User class to put into Database
                                        User user = new User(firebaseUser.getUid(),
                                                firebaseUser.getDisplayName(),
                                                firebaseUser.getEmail(),
                                                firebaseUser.getPhotoUrl().toString());
                                        Ref.setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSetValue) {
                                                        if (!taskSetValue.isSuccessful()) {
                                                            // If sign in fails, display a message to the user.
                                                            Toast.makeText(SignUpActivity.this, "Sign up failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });


                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    // [END auth_with_google]

}
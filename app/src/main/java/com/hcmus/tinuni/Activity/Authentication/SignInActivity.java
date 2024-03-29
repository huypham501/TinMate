package com.hcmus.tinuni.Activity.Authentication;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Admin.AdminInitialActivity;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;


public class SignInActivity extends Activity {
    private TextInputLayout mEdtEmail, mEdtPassword;
    private Button mBtnSignIn, mBtnSignUp, mBtnForgot, mBtnSignInGoogle;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    private AlertDialog alertDialog;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        // Initialize ID
        initializeID();
//

        // Initialize Firebase Authentication
        initializeFireBaseAuth();

//        moveActivityDependOnLackingDataAccount();

        //Init Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();

        // [START config_signin]
        initializeFireBaseGoogleAuth();
        // [END config_signin]
    }

    private void initializeFireBaseGoogleAuth() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // When initializing Activity, check to see if the user is currently signed in.
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            FirebaseDatabase.getInstance().getReference("Roles").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String role = snapshot.getValue(String.class);
                    if (role != null && role.equals("admin")){
                        Intent i = new Intent(SignInActivity.this, AdminInitialActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        //finish(); //Do da co flag
                    } else {
                        //Gia su tai khoan nay da dc dang nhap tren app truoc do, thi ham nay se auto link toi Main Activity luon
                        //Nhung neu tai khoan da bi ban, thi khong cho no link qua MainActivity, chi hien AlertDialog thoi.

                        FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("banned").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String banned = snapshot.getValue(String.class);
                                if (banned != null && banned.equals("True")){
                                    new AlertDialog.Builder(SignInActivity.this)
                                            .setIcon(R.drawable.ic_report)
                                            .setTitle("Your account has been banned")
                                            .setMessage("Your TinMate profile has been banned for activity that violates our Term of Use.")
                                            .setPositiveButton("Yes", null)
                                            .show();
                                } else
                                    moveActivity(SignInActivity.this, MainActivity.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


        }
    }

    private void moveActivityDependOnLackingDataAccount() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference roles_ref = FirebaseDatabase.getInstance().getReference("Roles").child(userId);
            roles_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String role = snapshot.getValue(String.class);
                    if (role != null && role.equals("admin")){
                        alertDialog.dismiss();
                        Intent i = new Intent(SignInActivity.this, AdminInitialActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        //finish(); //Da co flag nen k can
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, "Error get data user", Toast.LENGTH_SHORT).show();
                        System.out.println(task.getException());
                    } else {
                        if (task.getResult().getValue() == null) {
                            // Do nothing keep in sign in activity
                        } else if (task.getResult().child("userName") == null) {
                            moveActivity(SignInActivity.this, SignUpProcessPersonalActivity.class);
                        } else {
                            FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("banned").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String banned = snapshot.getValue(String.class);
                                    if (banned != null && banned.equals("True")){
                                        //Tat cai Please wait! This may take a moment di
                                        alertDialog.dismiss();
                                        new AlertDialog.Builder(SignInActivity.this)
                                                .setIcon(R.drawable.ic_report)
                                                .setTitle("Your account has been banned")
                                                .setMessage("Your TinMate profile has been banned for activity that violates our Term of Use.")
                                                .setPositiveButton("Yes", null)
                                                .show();
                                    } else
                                        moveActivity(SignInActivity.this, MainActivity.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
//                            moveActivity(SignInActivity.this, MainActivity.class);
                        }
                    }
                }
            });
        }
    }

    private void initializeFireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initializeID() {
        mEdtEmail = (TextInputLayout) findViewById(R.id.edtEmail);
        mEdtPassword = (TextInputLayout) findViewById(R.id.edtPassword);
        mProgressBar = findViewById(R.id.progressBar);
        mBtnSignIn = (Button) findViewById(R.id.btnSignIn);
        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEdtEmail.getEditText().getText().toString();
                final String password = mEdtPassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignInActivity.this, "Please fill in email", Toast.LENGTH_SHORT).show();
                    //mEdtEmail.setError("Please fill in email!");
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignInActivity.this, "Please fill in password!", Toast.LENGTH_SHORT).show();
                    //mEdtPassword.setError("Please fill in password!");
                } else {
//                    mProgressBar.setVisibility(View.VISIBLE);
                    alertDialog.show();
                    signInWithEmailAndPassword(email, password);
                }
            }
        });

        mBtnSignInGoogle = findViewById(R.id.btnSignInGoogle);
        mBtnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mProgressBar.setVisibility(View.VISIBLE);
                alertDialog.show();
                signInWithGoogle();
            }
        });

        mBtnSignUp = (Button) findViewById(R.id.btnSignUp);
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBtnForgot = (Button) findViewById(R.id.btnForgot);
        mBtnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
                finish();
            }
        });

    }

    private void signInWithEmailAndPassword(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    alertDialog.dismiss();
                    moveActivityDependOnLackingDataAccount();
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(SignInActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
//                    mProgressBar.setVisibility(View.INVISIBLE);
                    alertDialog.dismiss();
                }
            }
        });
    }

    // [START signin]
    private void signInWithGoogle() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN); // ?????????????
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
                alertDialog.dismiss();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                signInWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
//                mProgressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
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
                            //---------------------------------------------------------------------------

                            //---------------------------------------------------------------------------
                            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        // Storing account signed in with google into Realtime
                                        // Create User class to put into Database
                                        User user = new User(firebaseUser.getUid(),
                                                firebaseUser.getDisplayName(),
                                                firebaseUser.getEmail(),
                                                firebaseUser.getPhotoUrl().toString(),
                                                firebaseUser.getPhoneNumber(), "False");

                                        Ref.setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSetValue) {
                                                        if (!taskSetValue.isSuccessful()) {
                                                            // If sign in fails, display a message to the user.
                                                            Toast.makeText(SignInActivity.this, "Sign in failed.",
                                                                    Toast.LENGTH_SHORT).show();
//                                                            mProgressBar.setVisibility(View.INVISIBLE);
                                                            alertDialog.dismiss();
                                                        }
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("banned").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String banned = snapshot.getValue(String.class);
                                    if (banned != null && banned.equals("True")){
                                        //Tat cai Please wait! This may take a moment di
                                        new AlertDialog.Builder(SignInActivity.this)
                                                .setIcon(R.drawable.ic_report)
                                                .setTitle("Your account has been banned")
                                                .setMessage("Your TinMate profile has been banned for activity that violates our Term of Use.")
                                                .setPositiveButton("Yes", null)
                                                .show();
                                    } else
                                        moveActivity(SignInActivity.this, MainActivity.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

//                            moveActivity(SignInActivity.this, MainActivity.class);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
//                            mProgressBar.setVisibility(View.INVISIBLE);
                            alertDialog.show();
                        }
                    }
                });
    }
    // [END auth_with_google]

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }



}
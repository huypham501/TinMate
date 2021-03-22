package com.hcmus.tinuni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.Activity.Profile.UserProfileActitivy;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText edtOldPassword, edtNewPassword, edtReconfirm;
    private Button btnConfirm, btnGoBack;
    String id;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtReconfirm = findViewById(R.id.edtReconfirm);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnGoBack = findViewById(R.id.btnGoBack);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        id = mUser.getUid();


        //CONFIRM
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtNewPassword.getText().toString().matches(edtReconfirm.getText().toString())) {
                    AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), edtOldPassword.getText().toString());
                    mUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mUser.updatePassword(edtNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(ChangePasswordActivity.this, "Change Password successfully !", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Wrong old password, try again !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Re-confirm and New Password is not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_goBack = new Intent(ChangePasswordActivity.this, MainActivity.class);
                intent_goBack.putExtra("id", id);
                startActivity(intent_goBack);
            }
        });
    }

}
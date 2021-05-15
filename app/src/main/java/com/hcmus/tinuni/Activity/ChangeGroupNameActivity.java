package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.AdminAction;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangeGroupNameActivity extends Activity {
    ImageView btnGoBack, ivAvatar;
    EditText edtGroupName;
    TextView btnSave;

    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group_name);

        ivAvatar = findViewById(R.id.ivAvatar);
        edtGroupName = findViewById(R.id.edtGroupName);
        btnSave = findViewById(R.id.btnSave);
        btnGoBack = findViewById(R.id.btnGoBack);

        Intent i = getIntent();
        String groupId = i.getStringExtra("groupId");
        String groupName = i.getStringExtra("groupName");
        String img_link = i.getStringExtra("imageURL");
        String signal = i.getStringExtra("signal");

        System.out.println("name: " + groupName);
        System.out.println("url: " + img_link);
        edtGroupName.setText(groupName);
        Glide.with(ChangeGroupNameActivity.this)
                .load(img_link)
                .into(ivAvatar);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_groupName = edtGroupName.getText().toString();
                if (!new_groupName.equals("")) {
                    // 5. Confirm success
                    new SweetAlertDialog(ChangeGroupNameActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure to save?")
                            .setConfirmText("Yes")
                            .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    if (signal.equals("admin")) {

                                        //Write it down to admin diary.

                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference root = db.getReference().child("Groups").child(groupId);

                                        root.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                group = snapshot.getValue(Group.class);

                                                String currentMillis = String.valueOf(System.currentTimeMillis());
                                                AdminAction adminAction = new AdminAction(currentMillis,
                                                        "Change name of group chat",
                                                        "Change Name Of " + group.toString() + "New name: " + new_groupName + "\n");
                                                db.getReference().child("AdminActions").push().setValue(adminAction);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("name");
                                        groupRef.setValue(new_groupName);

                                        sDialog
                                                .setTitleText("Saved!")
                                                .setContentText("Change group name successfully")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                    }

                                    ChangeGroupNameActivity.this.onBackPressed();
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(ChangeGroupNameActivity.this, "Please fill in the name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeGroupNameActivity.this.onBackPressed();
            }
        });
    }
}
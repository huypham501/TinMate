package com.hcmus.tinuni.Activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hcmus.tinuni.Activity.Profile.EditProfileActivity;
import com.hcmus.tinuni.Activity.Profile.ShowProfileActitivy;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingGroupActivity extends Activity {
    private ImageView ivAvatar, btnAdd, btnGoBack;
    private TextView txtName;

    private RelativeLayout layoutMembers, layoutChangeName, layoutLeave, layoutChangeAvatar;

    private String groupId, img_link;

    private FirebaseUser mUser;
    private DatabaseReference mRef;

    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_group);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ivAvatar = findViewById(R.id.ivAvatar);
        btnAdd = findViewById(R.id.btnAdd);
        btnGoBack = findViewById(R.id.btnGoBack);
        txtName = findViewById(R.id.name);

        layoutMembers = findViewById(R.id.layoutMembers);
        layoutChangeName = findViewById(R.id.layoutChangeName);
        layoutLeave = findViewById(R.id.layoutLeave);
        layoutChangeAvatar = findViewById(R.id.layoutChangeAvatar);

        //Init AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingGroupActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();

        Intent i = getIntent();
        groupId = i.getStringExtra("groupId");
        Log.e("SettingActivity: ", groupId);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                img_link = group.getImageURL();
                txtName.setText(group.getName());
                Glide.with(SettingGroupActivity.this)
                        .load(group.getImageURL())
                        .into(ivAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(SettingGroupActivity.this, AddToGroupActivity.class);
                add.putExtra("groupId", groupId);
                startActivity(add);
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingGroupActivity.this.onBackPressed();
            }
        });

        layoutMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewMembers = new Intent(SettingGroupActivity.this, ViewMembersGroupActivity.class);
                viewMembers.putExtra("groupId", groupId);
                startActivity(viewMembers);
            }
        });

        layoutChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeName = new Intent(SettingGroupActivity.this, ChangeGroupNameActivity.class);
                changeName.putExtra("groupId", groupId);
                changeName.putExtra("groupName", txtName.getText());
                changeName.putExtra("imageURL", img_link);
                startActivity(changeName);
            }
        });

        layoutChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        layoutLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sweet Alert start
                new SweetAlertDialog(SettingGroupActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure to leave the group?")
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
                                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Participants").child(mUser.getUid());
                                groupRef.removeValue();

                                sDialog
                                        .setTitleText("Group left !")
                                        .setContentText("Group left !")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                Intent i = new Intent(SettingGroupActivity.this, MainActivity.class);
                                startActivity(i);
                            }
                        })
                        .show();
                //Sweet Alert end
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivAvatar.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        StorageReference storageRef = storageReference.child("images/groups/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String old_img = img_link;
                //add new image
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        img_link = uri.toString();
                        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("imageURL");
                        groupRef.setValue(img_link);
                        //delete old image
                        if (!old_img.matches("default") && old_img.contains("firebasestorage")) {
                            StorageReference deleteRef = storage.getReferenceFromUrl(old_img);
                            deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("Delete old image successfully !");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Delete old image failed !");
                                }
                            });
                        }
                        alertDialog.dismiss();
                        Toast.makeText(SettingGroupActivity.this, "Upload SUCCESS", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                alertDialog.show();
//                Toast.makeText(SettingGroupActivity.this, "Uploadinggg", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
                Toast.makeText(SettingGroupActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
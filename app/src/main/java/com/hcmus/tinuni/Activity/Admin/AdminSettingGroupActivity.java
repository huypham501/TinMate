package com.hcmus.tinuni.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.hcmus.tinuni.Activity.AddToGroupActivity;
import com.hcmus.tinuni.Activity.ChangeGroupNameActivity;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.Activity.ViewMembersGroupActivity;
import com.hcmus.tinuni.Model.AdminAction;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminSettingGroupActivity extends Activity {

    private ImageView ivAvatar, btnAdd, btnGoBack;
    private TextView txtName;

    private RelativeLayout layoutMembers, layoutChangeName, layoutLeave, layoutChangeAvatar;

    private String groupId, img_link;

    private FirebaseUser mUser;
    private DatabaseReference mRef;

    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root =  db.getReference().child("Groups");
    private AlertDialog alertDialog;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_setting_group);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ivAvatar = findViewById(R.id.admin_setting_group_ava);
        btnGoBack = findViewById(R.id.admin_setting_group_go_back);
        txtName = findViewById(R.id.admin_setting_group_name);

        layoutMembers = findViewById(R.id.admin_view_member);
        layoutChangeName = findViewById(R.id.admin_change_group_chat_name);
        layoutChangeAvatar = findViewById(R.id.admin_change_group_chat_ava);

        //Init AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminSettingGroupActivity.this);
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
                group = snapshot.getValue(Group.class);

                img_link = group.getImageURL();
                txtName.setText(group.getName());
                Glide.with(AdminSettingGroupActivity.this)
                        .load(group.getImageURL())
                        .into(ivAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminSettingGroupActivity.this.onBackPressed();
            }
        });

        layoutMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewMembers = new Intent(AdminSettingGroupActivity.this, ViewMembersGroupActivity.class);
                viewMembers.putExtra("groupId", groupId);
                viewMembers.putExtra("userId", mUser.getUid());
                viewMembers.putExtra("signal", "admin");
                startActivity(viewMembers);
            }
        });

        layoutChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeName = new Intent(AdminSettingGroupActivity.this, ChangeGroupNameActivity.class);
                changeName.putExtra("groupId", groupId);
                changeName.putExtra("groupName", txtName.getText());
                changeName.putExtra("imageURL", img_link);
                changeName.putExtra("signal", "admin");
                startActivity(changeName);
            }
        });

        layoutChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
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
                        Toast.makeText(AdminSettingGroupActivity.this, "Upload SUCCESS", Toast.LENGTH_SHORT).show();

                        //Write it down to admin diary.

                        String currentMillis = String.valueOf(System.currentTimeMillis());
                        AdminAction adminAction = new AdminAction(currentMillis, "Change avatar of group chat", "Change Avatar Of " + group.toString());
                        db.getReference().child("AdminActions").push().setValue(adminAction);
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                alertDialog.show();
//                Toast.makeText(AdminSettingGroupActivity.this, "Uploadinggg", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
                Toast.makeText(AdminSettingGroupActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
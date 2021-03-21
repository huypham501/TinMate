package com.hcmus.tinuni.Activity.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtFullname, edtEmail, edtPhone, edtSchool;
    private ImageView ivAvatar;
    private Uri imageUri;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnEdit, btnGoBack;
    String id, img;
    private DatabaseReference mRef;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtSchool = findViewById(R.id.edtSchool);
        ivAvatar = findViewById(R.id.ivAvatar);

        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        btnEdit = findViewById(R.id.btnSave);
        btnGoBack = findViewById(R.id.btnGoBack);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                edtFullname.setText(user.getUsername());
                edtEmail.setText(user.getEmail());
                Glide.with(EditProfileActivity.this)
                        .load(user.getImageURL())
                        .into(ivAvatar);
                img = user.getImageURL();


                if (user.getPhone() == null) {
                    edtPhone.setText("");
                } else {
                    edtPhone.setText(user.getPhone());
                }
                if (user.getGender() == null) {
                    rbMale.setChecked(false);
                    rbFemale.setChecked(false);
                } else {
                    if (user.getGender().matches("Male")) {
                        rbMale.setChecked(true);
                    } else if (user.getGender().matches("Female")) {
                        rbFemale.setChecked(true);
                    }
                }

                if (user.getSchool() == null) {
                    edtSchool.setText("");
                } else {
                    edtSchool.setText(user.getSchool());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtFullname.getText().toString();
                String email = edtEmail.getText().toString();
                String phone = edtPhone.getText().toString();
                String school = edtSchool.getText().toString();

                int gender_index = rgGender.getCheckedRadioButtonId();
                RadioButton rd_gender = findViewById(gender_index);

                if (TextUtils.isEmpty(username)) {
                    edtFullname.setError("Username can't be empty");
                } else if (TextUtils.isEmpty(phone)) {
                    edtPhone.setError("Phone can't be empty");
                } else if (TextUtils.isEmpty(school)) {
                    edtSchool.setError("School can't be empty");
                } else if (rgGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(EditProfileActivity.this, "Gender can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    String gender = rd_gender.getText().toString();
                    System.out.println("-----------IMG--------- " + img);
                    User new_user = new User(id, username, email, img, phone, gender, school);
                    mRef.setValue(new_user);
                    Toast.makeText(EditProfileActivity.this, "Save successfully !", Toast.LENGTH_SHORT).show();

                    Intent go_back = new Intent(EditProfileActivity.this, UserProfileActitivy.class);
                    go_back.putExtra("id", id);
                    startActivity(go_back);
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_back = new Intent(EditProfileActivity.this, UserProfileActitivy.class);
                go_back.putExtra("id", id);
                startActivity(go_back);
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
//        final ProgressDialog pd = new ProgressDialog(EditProfileActivity.this);
//        pd.setTitle("Uploading Image ...");
//        pd.show();
//        final String randomKey = UUID.randomUUID().toString();
//        StorageReference storageRef = storageReference.child("images/avatars/" + randomKey);
//        String link = storageRef.getDownloadUrl().toString();
//        System.out.println("------link----" + link);
//
//        storageRef.putFile(imageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                img = uri.toString();
//                                System.out.println("-----------uri----------: " + uri);
//                            }
//                        });
//                        pd.dismiss();
//                        Toast.makeText(EditProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        pd.dismiss();
//                        Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                        pd.setMessage("Progress: " + (int) progressPercent + "%");
//                    }
//                }
        StorageReference storageRef = storageReference.child("images/avatars/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        img = uri.toString();
                        Toast.makeText(EditProfileActivity.this, "Upload SUCCESS", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                Toast.makeText(EditProfileActivity.this, "Uploadinggg", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
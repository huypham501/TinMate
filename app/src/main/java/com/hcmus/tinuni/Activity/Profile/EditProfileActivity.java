package com.hcmus.tinuni.Activity.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class EditProfileActivity extends Activity {

    private EditText edtFullname, edtEmail, edtPhone, edtSchool, edtMajor, edtBeginYear;
    private ImageView ivAvatar, btnGoBack;
    private Uri imageUri;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnEdit;
    String id, img_link;
    private DatabaseReference mRef;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtSchool = findViewById(R.id.edtSchool);
        edtMajor = findViewById(R.id.edtMajor);
        edtBeginYear = findViewById(R.id.edtBeginYear);
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

        System.out.println("***************************");
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Init Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setCancelable(false);
                builder.setView(R.layout.layout_loading_dialog);
                alertDialog = builder.create();

                User user = snapshot.getValue(User.class);
                edtFullname.setText(user.getUserName());
                edtEmail.setText(user.getEmail());
                if (user.getImageURL().matches("default")) {
                    ivAvatar.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(EditProfileActivity.this)
                            .load(user.getImageURL())
                            .into(ivAvatar);
                }
                img_link = user.getImageURL();

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
                if (user.getSchoolName() == null) {
                    edtSchool.setText("");
                } else {
                    edtSchool.setText(user.getSchoolName());
                }
                if (user.getMajor() == null) {
                    edtMajor.setText("");
                } else {
                    edtMajor.setText(user.getMajor());
                }
                if (user.getYearBegins() == null) {
                    edtBeginYear.setText("");
                } else {
                    edtBeginYear.setText(user.getYearBegins());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mRef.addValueEventListener(valueEventListener);

        //SAVE
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtFullname.getText().toString();
                String email = edtEmail.getText().toString();
                String phone = edtPhone.getText().toString();
                String school = edtSchool.getText().toString();
                String major = edtMajor.getText().toString();
                String beginYear = edtBeginYear.getText().toString();
                String gender;

                int gender_index = rgGender.getCheckedRadioButtonId();
                RadioButton rd_gender = findViewById(gender_index);

                if (TextUtils.isEmpty(username)) {
                    edtFullname.setError("Username can't be empty");
                } else {
                    if (TextUtils.isEmpty(phone)) {
//                    edtPhone.setError("Phone can't be empty");
                        phone = "";
                    }
                    if (TextUtils.isEmpty(school)) {
//                    edtSchool.setError("School can't be empty");
                        school = "";
                    }
                    if (TextUtils.isEmpty(major)) {
//                    edtMajor.setError("Major can't be empty");
                        major = "";
                    }
                    if (TextUtils.isEmpty(beginYear)) {
//                    edtBeginYear.setError("Begin Year can't be empty");
                        beginYear = "";
                    }
                    if (rgGender.getCheckedRadioButtonId() == -1) {
//                    Toast.makeText(EditProfileActivity.this, "Gender can't be empty", Toast.LENGTH_SHORT).show();
                        gender = "";
                    } else {
                        gender = rd_gender.getText().toString();
                    }

                    System.out.println("-----------IMG link----------- " + img_link);
                    User new_user = new User(id, username, email, img_link, phone, gender, school, major, beginYear, "False");
                    mRef.setValue(new_user);
                    Toast.makeText(EditProfileActivity.this, "Save successfully !", Toast.LENGTH_SHORT).show();
                    Intent go_back = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                    go_back.putExtra("id", id);
                    startActivity(go_back);
                }
            }
        });

        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_back = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                go_back.putExtra("id", id);
                startActivity(go_back);
                finish();
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
        StorageReference storageRef = storageReference.child("images/avatars/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String old_img = img_link;
                //add new image
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        img_link = uri.toString();
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
                        Toast.makeText(EditProfileActivity.this, "Upload SUCCESS", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                alertDialog.show();
//                Toast.makeText(EditProfileActivity.this, "Uploadinggg", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
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
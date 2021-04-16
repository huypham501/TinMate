package com.hcmus.tinuni.Activity.Profile;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Authentication.SignInActivity;
import com.hcmus.tinuni.Activity.ShowVideoActivity;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

public class UserProfileActitivy extends Activity {

    private TextView tvName, tvFullname, tvEmail, tvPhone, tvGender, tvSchool, tvMajor, tvBeginYear;
    private ImageView ivAvatar, btnGoBack;
    private Button btnEdit, btnChangePassword, btnSignOut;
    private String id;

    private DatabaseReference mRef;

    private ExpandableLinearLayout linearLayout;
    private Button btnArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Initial variables
        tvName = findViewById(R.id.tvName);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvGender = findViewById(R.id.tvGender);
        tvSchool = findViewById(R.id.tvSchool);
        tvMajor = findViewById(R.id.tvMajor);
        tvBeginYear = findViewById(R.id.tvBeginYear);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnEdit = findViewById(R.id.btnEdit);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnSignOut = findViewById(R.id.btnSignOut);


        linearLayout = (ExpandableLinearLayout) findViewById(R.id.expandedLayout);
        btnArrow = findViewById(R.id.btnArrow);
        btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(linearLayout.getVisibility() == View.GONE) {
//                    Fade fadeIn = new Fade(Fade.IN);
//                    TransitionManager.beginDelayedTransition(linearLayout, fadeIn);
//                    linearLayout.setVisibility(View.VISIBLE);
//                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_up);
//                } else {
//                    Fade fadeOut = new Fade(Fade.OUT);
//                    TransitionManager.beginDelayedTransition(linearLayout, fadeOut);
//                    linearLayout.setVisibility(View.GONE);
//                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_down);
//                }
                linearLayout.toggle();
                if (linearLayout.isExpanded()) {
                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_down);

                } else {
                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_up);

                }

            }
        });

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        System.out.println(id);
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                System.out.println(user);
                tvName.setText(user.getUserName());
                tvFullname.setText(user.getUserName());
                tvEmail.setText(user.getEmail());

                if (user.getImageURL().matches("default")) {
                    ivAvatar.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(UserProfileActitivy.this)
                            .load(user.getImageURL())
                            .into(ivAvatar);
                }
                if (user.getPhone() == null) {
                    tvPhone.setText("");
                } else {
                    tvPhone.setText(user.getPhone());
                }
                if (user.getGender() == null) {
                    tvGender.setText("");
                } else {
                    tvGender.setText(user.getGender());
                }
                if (user.getGender() == null) {
                    tvSchool.setText("");
                } else {
                    tvSchool.setText(user.getSchoolName());
                }
                if (user.getMajor() == null) {
                    tvMajor.setText("");
                } else {
                    tvMajor.setText(user.getMajor());
                }
                if (user.getYearBegins() == null) {
                    tvBeginYear.setText("");
                } else {
                    tvBeginYear.setText(user.getYearBegins());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //EDIT
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_edit = new Intent(UserProfileActitivy.this, EditProfileActivity.class);
                intent_edit.putExtra("id", id);
                startActivity(intent_edit);
                finish();
            }
        });

        //CHANGE PASSWORD
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test show video here
                Intent intent_changePassword = new Intent(UserProfileActitivy.this, ShowVideoActivity.class);
                startActivity(intent_changePassword);
            }
        });

        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent_goBack = new Intent(UserProfileActitivy.this, MainActivity.class);
//                intent_goBack.putExtra("id", id);
//                startActivity(intent_goBack);
                UserProfileActitivy.super.onBackPressed();
                finish();
            }
        });

        //SIGN OUT
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserProfileActitivy.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                System.out.println("***********************************************");
                                Intent intent = new Intent(UserProfileActitivy.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}
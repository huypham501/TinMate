package com.hcmus.tinuni.Activity.Profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Admin.UserReportActivity;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ShowProfileActivity extends Activity {

    private TextView tvName, tvFullname, tvEmail, tvPhone, tvGender, tvSchool, tvMajor, tvBeginYear;
    private ImageView ivAvatar, btnGoBack;
    private Button btnAddFriend, btnReport, btnDecline;
    private String id;

    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private ExpandableLinearLayout linearLayout;
    private Button btnArrow;

    private Boolean isFriend, isRequested, isRequesting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
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
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnReport = findViewById(R.id.btnReport);
        btnDecline = findViewById(R.id.btnDecline);

        linearLayout = (ExpandableLinearLayout) findViewById(R.id.expandedLayout);
        btnArrow = findViewById(R.id.btnArrow);
        Intent intent = getIntent();
        id = intent.getStringExtra("userId");
        System.out.println(id);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        isFriend = false;
        isRequested = false;
        isRequesting = false;
        getIsFriend();
        getIsRequested();
        getIsRequesting();

        btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.toggle();
                if (linearLayout.isExpanded()) {
                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_down);

                } else {
                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_up);
                }
            }
        });

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
                    Glide.with(ShowProfileActivity.this)
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
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (!isFriend) {
                        if (!isRequested) {
                            //add friend start
                            new SweetAlertDialog(ShowProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Are you sure to add friend?")
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
                                            DatabaseReference opponent_friendRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(id).child(mUser.getUid()).child("id");
                                            opponent_friendRef.setValue(mUser.getUid());

                                            sDialog
                                                    .setTitleText("Request sent!")
                                                    .setContentText("Send request successfully !")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(null)
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    })
                                    .show();
                            //add friend end
                        } else {
                            //accept start
                            new SweetAlertDialog(ShowProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Are you sure to accept?")
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
                                            DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(mUser.getUid()).child(id);
                                            requestRef.removeValue();
                                            DatabaseReference addFriendRef1 = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid()).child(id).child("id");
                                            DatabaseReference addFriendRef2 = FirebaseDatabase.getInstance().getReference("Friends").child(id).child(mUser.getUid()).child("id");

                                            addFriendRef1.setValue(id);
                                            addFriendRef2.setValue(mUser.getUid());
                                            sDialog
                                                    .setTitleText("Accepted!")
                                                    .setContentText("Accept successfully !")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(null)
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    })
                                    .show();
                            //accept end
                        }

                    } else if (isFriend) {
                        //unfriend start
                        new SweetAlertDialog(ShowProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure to unfriend?")
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
                                        DatabaseReference unFriendRef1 = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid()).child(id);
                                        DatabaseReference unFriendRef2 = FirebaseDatabase.getInstance().getReference("Friends").child(id).child(mUser.getUid());

                                        unFriendRef1.removeValue();
                                        unFriendRef2.removeValue();
                                        sDialog
                                                .setTitleText("Unfriend!")
                                                .setContentText("Unfriend successfully !")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        finish();
                                        startActivity(getIntent());
                                    }

                                })
                                .show();
                        //unfriend end
                    }
                }
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRequesting) {
                    //decline start
                    new SweetAlertDialog(ShowProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure to decline?")
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
                                    DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(mUser.getUid()).child(id);
                                    requestRef.removeValue();

                                    sDialog
                                            .setTitleText("Decline!")
                                            .setContentText("Decline successfully !")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    finish();
                                    startActivity(getIntent());
                                }

                            })
                            .show();
                    //decline end
                } else {
                    //cancel request start
                    new SweetAlertDialog(ShowProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure to cancel request?")
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
                                    DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(id).child(mUser.getUid());
                                    requestRef.removeValue();

                                    sDialog
                                            .setTitleText("Request Canceled!")
                                            .setContentText("Cancel request successfully !")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    finish();
                                    startActivity(getIntent());
                                }

                            })
                            .show();
                    //cancel request end
                }

            }
        });

        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent_goBack = new Intent(UserProfileActitivy.this, MainActivity.class);
//                intent_goBack.putExtra("id", id);
//                startActivity(intent_goBack);
                ShowProfileActivity.super.onBackPressed();
                finish();
            }
        });


        //REPORT
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_report = new Intent(ShowProfileActivity.this, UserReportActivity.class);
                intent_report.putExtra("target", (String)tvEmail.getText());
                startActivity(intent_report);
            }
        });
    }

    private void getIsFriend() {
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid()).child(id);
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isFriend = true;
                    btnAddFriend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unfriend, 0, 0, 0);
                    btnAddFriend.setText("unfriend");
                } else {
                    isFriend = false;
                    btnAddFriend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_friend, 0, 0, 0);
                    btnAddFriend.setText("add friend");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getIsRequested() {
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(mUser.getUid()).child(id);
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isRequested = true;
                    btnAddFriend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                    btnAddFriend.setText("Accept");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getIsRequesting() {
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(id).child(mUser.getUid());
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isRequesting = true;
                    btnAddFriend.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    btnAddFriend.setText("Waiting for response");
                    btnAddFriend.setClickable(false);
                    btnDecline.setText("Cancel request");
                    btnDecline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unfriend, 0, 0, 0);
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
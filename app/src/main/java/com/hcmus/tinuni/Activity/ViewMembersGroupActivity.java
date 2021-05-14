package com.hcmus.tinuni.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Profile.ShowProfileActivity;
import com.hcmus.tinuni.Adapter.UserAdapter;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewMembersGroupActivity extends FragmentActivity {

    private ImageView imageViewBack;
    private TextView textViewAdd;
    private ViewPager viewPager;
    private ViewMemberPagerAdapter viewMemberPagerAdapter;
    private TabLayout tabLayout;
    ValueEventListener valueEventListenerAllMember, valueEventListenerAdminMember;

    private String groupId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_member_group);

        //GET GROUP ID AND USER ID IN INTENT
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        userId = intent.getStringExtra("userId");

        //SETUP ID WIDGET
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // CREATE ARRAY FRAGMENT
        ViewMemberFragment viewMemberFragmentAllMember = new ViewMemberFragment("All", groupId, userId);
        ViewMemberFragment viewMemberFragmentAdminMember = new ViewMemberFragment("Admin", groupId, userId);

        ArrayList<ViewMemberFragment> viewMemberFragmentArrayList = new ArrayList<>();
        viewMemberFragmentArrayList.add(viewMemberFragmentAllMember);
        viewMemberFragmentArrayList.add(viewMemberFragmentAdminMember);

        // SET ARRAY FRAGMENT TO ADAPTER
        viewMemberPagerAdapter = new ViewMemberPagerAdapter(getSupportFragmentManager(), viewMemberFragmentArrayList);
        viewPager.setAdapter(viewMemberPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicator(R.drawable.indicator_shape_activated);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //SETUP TEXT VIEW ADD
        textViewAdd = findViewById(R.id.textViewAdd);
        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ViewMembersGroupActivity.this, AddToGroupActivity.class);
                intent1.putExtra("groupId", groupId);
                startActivity(intent1);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();

        //SET UP IMAGE VIEW BACK
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public static class ViewMemberFragment extends Fragment {
        private ValueEventListener valueEventListener;
        private DatabaseReference databaseReference;

        private String title, groupId, userId;
        private RecyclerView recyclerView;
        private UserAdapterCustom userAdapter;

        private BottomSheetDialog bottomSheetDialog;

        public ViewMemberFragment() {

        }

        public ViewMemberFragment(String title, String groupId, String userId) {
            this.title = title;
            this.groupId = groupId;
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.recyclerview, container, false);

            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // CREATE BOTTOMSHEETDIALOG
            bottomSheetDialog = new BottomSheetDialog(getContext());

            // SETUP DATABASE REFERENCE
            databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Participants");

            // SETUP LISTENER
            if (title.equals("All")) {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ArrayList<User> userArrayList = new ArrayList<>();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                DatabaseReference databaseReferenceTemp = FirebaseDatabase.getInstance().getReference("Users").child(dataSnapshot.getKey());
                                databaseReferenceTemp.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshotUser) {
                                        if (snapshotUser.exists()) {
                                            User user = snapshotUser.getValue(User.class);
                                            userArrayList.add(user);
                                        }

                                        userAdapter = new UserAdapterCustom(view.getContext(), userArrayList, false, null, groupId, userId, bottomSheetDialog);
                                        recyclerView.setAdapter(userAdapter);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError errorUser) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                };
            } else {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ArrayList<User> userArrayList = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (!dataSnapshot.child("role").getValue().toString().equals("member")) {
                                    DatabaseReference databaseReferenceTemp = FirebaseDatabase.getInstance().getReference("Users").child(dataSnapshot.getKey());
                                    databaseReferenceTemp.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshotUser) {
                                            if (snapshotUser.exists()) {
                                                User user = snapshotUser.getValue(User.class);
                                                userArrayList.add(user);
                                            }

                                            userAdapter = new UserAdapterCustom(view.getContext(), userArrayList, false, null, groupId, userId, bottomSheetDialog);
                                            recyclerView.setAdapter(userAdapter);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError errorUser) {

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                };
            }

            return view;
        }

        @Override
        public void onStart() {
            super.onStart();
            databaseReference.addValueEventListener(valueEventListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            bottomSheetDialog.dismiss();
        }

        @Override
        public void onStop() {
            super.onStop();
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}

class UserAdapterCustom extends UserAdapter {
    private View viewSheet;
    private BottomSheetDialog bottomSheetDialog;

    private String groupId;
    private String currUserId;
    private boolean currUserRole;

    public UserAdapterCustom(Context context, List<User> mUsers, boolean isChat, List<Boolean> mIsSeen) {
        super(context, mUsers, isChat, mIsSeen);
    }

    public UserAdapterCustom(Context context, List<User> mUsers, boolean isChat, List<Boolean> mIsSeen, String groupId, String currUserId, BottomSheetDialog bottomSheetDialog) {
        super(context, mUsers, isChat, mIsSeen);
        this.groupId = groupId;
        this.currUserId = currUserId;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewSheet = LayoutInflater.from(getContext()).inflate(R.layout.view_user_modal_bottom_sheet, null);

        // GET CURR USER ROLE
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                .child("Participants").child(currUserId)
                .child("role");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getValue().equals("member")) {
                        currUserRole = false;
                    } else {
                        currUserRole = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        User watchedUser = getmItems().get(position);

        // ROLE FALSE == member | ROLE TRUE = admin - owner

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SETUP BOTTOM SHEET
                // SET NAME SHEET
                TextView textView = viewSheet.findViewById(R.id.textViewUserName);
                textView.setText(watchedUser.getUserName());

                // SET MESS MOVE INTENT
                LinearLayout linearLayoutMessage = viewSheet.findViewById(R.id.linearLayoutMessage);
                linearLayoutMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra("userId", watchedUser.getId());
                        intent.putExtra("groupId", "");
                        getContext().startActivity(intent);
                    }
                });

                if (watchedUser.getId().equals(currUserId)) {
                        linearLayoutMessage.setVisibility(View.GONE);
                } else {
                    linearLayoutMessage.setVisibility(View.VISIBLE);
                }

                viewSheet.findViewById(R.id.linearLayoutViewProfile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ShowProfileActivity.class);
                        intent.putExtra("userId", watchedUser.getId());
                        getContext().startActivity(intent);
                    }
                });



                // GET WATCHED USER ROLE
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("Participants").child(watchedUser.getId())
                        .child("role");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean watchedUserRole = false;
                            if (!snapshot.getValue().equals("member")) {
                                watchedUserRole = true;
                            }

                            // SETUP REMOVE USER FROM GROUP
                            LinearLayout linearLayoutRemoveFromGroup = viewSheet.findViewById(R.id.linearLayoutRemoveFromGroup);
                            linearLayoutRemoveFromGroup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Remove " + watchedUser.getUserName() + " from group")
                                            .setMessage("Are you sure ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    DatabaseReference databaseReferenceParticipants = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                                                    databaseReferenceParticipants.child("Participants").child(watchedUser.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(Task<Void> task) {
                                                            // COMPLETE REMOVE USER BEHAVIOR
                                                            if (task.isSuccessful()) {
                                                                DatabaseReference databaseReferenceChatList = FirebaseDatabase.getInstance().getReference("ChatList")
                                                                        .child(watchedUser.getId()).child(groupId);
                                                                databaseReferenceChatList.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // IF USER REMOVE IS MAIN USER MOVE TO CHAT
                                                                            if (watchedUser.getId().equals(currUserId)) {
                                                                                Intent i = new Intent(getContext(), MainActivity.class);
                                                                                i.putExtra("tabPosition", 2);
                                                                                getContext().startActivity(i);
                                                                                ((Activity) getContext()).finish();
                                                                            } else {
                                                                                bottomSheetDialog.dismiss();
                                                                            }
                                                                        } else {
                                                                            // NOT SUCCESSFUL CODE
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                // NOT SUCCESSFUL CODE
                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    bottomSheetDialog.dismiss();
                                                }
                                            }).show();
                                }
                            });

                            if (currUserRole) {
                                linearLayoutRemoveFromGroup.setVisibility(View.VISIBLE);
                            } else {
                                linearLayoutRemoveFromGroup.setVisibility(View.GONE);
                            }

                            // SETUP REMOVE ADMIN
                            LinearLayout linearLayoutRemoveAdmin = viewSheet.findViewById(R.id.linearLayoutRemoveAdmin);
                            linearLayoutRemoveAdmin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Remove admin" + watchedUser.getUserName())
                                            .setMessage("Are you sure ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                                                    databaseReference.child("Participants").child(watchedUser.getId()).child("role").setValue("member").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(Task<Void> task) {
                                                            // COMPLETE REMOVE ADMIN BEHAVIOR
                                                            bottomSheetDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    bottomSheetDialog.dismiss();
                                                }
                                            }).show();
                                }
                            });

                            if (currUserRole && watchedUserRole) {
                                linearLayoutRemoveAdmin.setVisibility(View.VISIBLE);
                            } else {
                                linearLayoutRemoveAdmin.setVisibility(View.GONE);
                            }

                            //REMOVE MAKE ADMIN
                            LinearLayout linearLayoutMakeAdmin = viewSheet.findViewById(R.id.linearLayoutMakeAdmin);
                            linearLayoutMakeAdmin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Make " + watchedUser.getUserName() + " admin")
                                            .setMessage("Are you sure ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                                                    databaseReference.child("Participants").child(watchedUser.getId()).child("role").setValue("admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(Task<Void> task) {
                                                            // COMPLETE MAKE ADMIN BEHAVIOR
                                                            bottomSheetDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    bottomSheetDialog.dismiss();
                                                }
                                            }).show();
                                }
                            });

                            if (currUserRole && !watchedUserRole) {
                                linearLayoutMakeAdmin.setVisibility(View.VISIBLE);
                            } else {
                                linearLayoutMakeAdmin.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

                bottomSheetDialog.setContentView(viewSheet);
                bottomSheetDialog.show();
            }
        });
    }
}

class ViewMemberPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<ViewMembersGroupActivity.ViewMemberFragment> fragmentArrayList;

    public ViewMemberPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentArrayList = new ArrayList<>();
    }

    public ViewMemberPagerAdapter(FragmentManager fm, ArrayList<ViewMembersGroupActivity.ViewMemberFragment> fragmentArrayList) {
        super(fm);
        this.fragmentArrayList = fragmentArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentArrayList.get(position).getTitle();
    }
}
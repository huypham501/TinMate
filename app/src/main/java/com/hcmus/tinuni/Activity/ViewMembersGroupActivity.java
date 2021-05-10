package com.hcmus.tinuni.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Adapter.UserAdapter;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

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

        private View viewSheet;
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

                                        userAdapter = new UserAdapterCustom(view.getContext(), userArrayList, false, null);
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

                                            userAdapter = new UserAdapterCustom(view.getContext(), userArrayList, false, null);
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
        public void onResume() {
            super.onResume();
            databaseReference.addListenerForSingleValueEvent(valueEventListener);
        }
    }
}

class UserAdapterCustom extends UserAdapter {
    private View viewSheet;
    private BottomSheetDialog bottomSheetDialog;
    private Context context;

    public UserAdapterCustom(Context context, List<User> mUsers, boolean isChat, List<Boolean> mIsSeen) {
        super(context, mUsers, isChat, mIsSeen);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewSheet = LayoutInflater.from(context).inflate(R.layout.view_user_modal_bottom_sheet, null);

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(viewSheet);

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
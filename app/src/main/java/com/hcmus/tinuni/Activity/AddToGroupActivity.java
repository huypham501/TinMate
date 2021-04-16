package com.hcmus.tinuni.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Adapter.AddUserAdapter;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class AddToGroupActivity extends Activity {

    private RecyclerView recyclerView;
    private AddUserAdapter addUserAdapter;
    private ImageView btnGoBack;
    private String groupId;
    private FirebaseUser mUser;
    private List<User> mUsers;
    private List<String> usersOfGroup;
    private List<String> friendsOfUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_group);

        btnGoBack = findViewById(R.id.btnGoBack);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddToGroupActivity.this));

        mUser = FirebaseAuth.getInstance()
                .getCurrentUser();

        Intent i = getIntent();
        groupId = i.getStringExtra("groupId");


        usersOfGroup = new ArrayList<>();
        mUsers = new ArrayList<>();
        friendsOfUser = new ArrayList<>();

        getUsersOfGroup();

        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToGroupActivity.super.onBackPressed();
                finish();
            }
        });
    }


    private void getUsersOfGroup() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Groups")
                .child(groupId)
                .child("Participants");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersOfGroup.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    Log.e("user id in group: ", userId);
                    usersOfGroup.add(userId);
                }
                getFriendsOfUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFriendsOfUser() {
        DatabaseReference friendRef = FirebaseDatabase
                .getInstance()
                .getReference("Friends")
                .child(mUser.getUid());
        friendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsOfUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.e("friend of user: ", dataSnapshot.getKey());

                    friendsOfUser.add(dataSnapshot.getKey());
                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUsers() {
        DatabaseReference usersRef = FirebaseDatabase
                .getInstance()
                .getReference("Users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    // Không phải là user hiện tại, không tồn tại trong group trước đó
                    // và là bạn của user hiện tại
                    if (!user.getId().equals(mUser.getUid()) &&
                            !usersOfGroup.contains(user.getId()) &&
                            friendsOfUser.contains(user.getId())) {
                        mUsers.add(user);
                    }
                }

                addUserAdapter = new AddUserAdapter(AddToGroupActivity.this, mUsers, groupId);
                recyclerView.setAdapter(addUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
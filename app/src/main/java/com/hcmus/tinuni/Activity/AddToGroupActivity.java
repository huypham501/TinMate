package com.hcmus.tinuni.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Adapter.AddUserAdapter;
import com.hcmus.tinuni.Adapter.UserAdapter;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class AddToGroupActivity extends Activity {

    private RecyclerView recyclerView;
    private AddUserAdapter addUserAdapter;
    private List<User> mUsers;
    private String groupId;
    private FirebaseUser mUser;
    private ArrayList<String> mUsersGroupId;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_group);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddToGroupActivity.this));

        mUser = FirebaseAuth.getInstance()
                .getCurrentUser();

        Intent i = getIntent();
        groupId = i.getStringExtra("groupId");

        mRef = FirebaseDatabase.getInstance()
                .getReference("Groups")
                .child(groupId).child("Participants");
        mUsersGroupId = new ArrayList<>();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mUsersGroupId.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    Log.e("user id in group: ", userId);
                    mUsersGroupId.add(userId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mUsers = new ArrayList<>();
        getUsers();
    }

    private void getUsers() {
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("Users");
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                        if (!user.getId().equals(mUser.getUid()) && !mUsersGroupId.contains(user.getId())) {
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
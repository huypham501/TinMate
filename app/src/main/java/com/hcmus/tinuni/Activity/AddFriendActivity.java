package com.hcmus.tinuni.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import com.hcmus.tinuni.Adapter.FriendRecommendAdapter;
import com.hcmus.tinuni.Adapter.FriendRequestAdapter;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends Activity {
    private ImageView btnGoBack;
    private RecyclerView recyclerViewFriendRequest, recyclerViewFriendRecommend;
    private FriendRequestAdapter friendRequestAdapter;
    private FriendRecommendAdapter friendRecommendAdapter;

    private List<User> friendRecommends, friendRequests, friends;

    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        btnGoBack = findViewById(R.id.btnGoBack);
        recyclerViewFriendRequest = findViewById(R.id.recyclerViewFriendRequest);
        recyclerViewFriendRequest.setHasFixedSize(true);
        recyclerViewFriendRequest.setLayoutManager(new LinearLayoutManager(AddFriendActivity.this));

        recyclerViewFriendRecommend = findViewById(R.id.recyclerViewFriendRecommend);
        recyclerViewFriendRecommend.setHasFixedSize(true);
        recyclerViewFriendRecommend.setLayoutManager(new LinearLayoutManager(AddFriendActivity.this));

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendActivity.this.onBackPressed();
            }
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        getFriends(mUser.getUid());

        getFriendRequests(mUser.getUid());

        getFriendRecommends(mUser.getUid());




    }

    private void getFriendRecommends(String uid) {
        friendRecommends = new ArrayList<>();

        DatabaseReference friendRecommendRef = FirebaseDatabase
                .getInstance()
                .getReference("Users");

        friendRecommendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRecommends.clear();
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    if(!user.getId().equals(uid)) {
                        friendRecommends.add(user);
                    }
                }

                friendRecommendAdapter = new FriendRecommendAdapter(AddFriendActivity.this, friendRecommends);
                recyclerViewFriendRecommend.setAdapter(friendRecommendAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFriendRequests(String uid) {
        DatabaseReference friendRequestRef = FirebaseDatabase
                .getInstance()
                .getReference("FriendRequests")
                .child(uid);
    }

    private void getFriends(String uid) {
        DatabaseReference friendRef = FirebaseDatabase
                .getInstance()
                .getReference("Friends")
                .child(uid);
    }
}
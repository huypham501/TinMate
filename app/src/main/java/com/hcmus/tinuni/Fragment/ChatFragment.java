package com.hcmus.tinuni.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Adapter.UserAdapter;
import com.hcmus.tinuni.Model.ChatList;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<Object> mItems;
    private List<ChatList> mChatLists;

    private FirebaseUser mUser;
    private DatabaseReference mRef;

    private RecyclerView recyclerView;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat,
                container,
                false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(mUser.getUid());

//        mItems = new ArrayList<>();
        mChatLists = new ArrayList<>();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChatLists.clear();

                // Loop for all users
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    mChatLists.add(chatList);
                }

                getChatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void getChatList() {
        // Getting all chats
        mItems = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference("Users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mItems.clear();

                // Get Users
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for(ChatList chatList : mChatLists) {
                        if(user.getId().equals(chatList.getId())) {
                            mItems.add(user);
                        }
                    }
                }

                // Get Groups
                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups");
                groupRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        List<Object> mUsers = new ArrayList<>(mItems);

                        mItems.clear();
                        for(DataSnapshot dataSnapshot: snapshot2.getChildren()) {
                            Group group = dataSnapshot.getValue(Group.class);

                            for(ChatList chatList : mChatLists) {
                                if(group.getId().equals(chatList.getId())) {
                                    mItems.add(group);
                                }
                            }

                        }
                        mItems.addAll(mUsers);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                userAdapter = new UserAdapter(getContext(), mItems, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
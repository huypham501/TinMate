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
import com.hcmus.tinuni.Adapter.GroupAdapter;
import com.hcmus.tinuni.Model.ChatList;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private GroupAdapter groupAdapter;
    private List<Group> mItems;
    private List<ChatList> mChatLists;

    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private ValueEventListener valueEventListener;
    private RecyclerView recyclerView;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group,
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
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    mChatLists.add(chatList);
                }

                getChatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mItems.clear();

                // Get Groups
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);

                    for (ChatList chatList : mChatLists) {
                        if (group.getId().equals(chatList.getId())) {
                            mItems.add(group);
                        }
                    }
                }

                groupAdapter = new GroupAdapter(getContext(), mItems, true);
                recyclerView.setAdapter(groupAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        return view;
    }

    private void getChatList() {
        // Getting all chats
        mItems = new ArrayList<>();

        mRef = FirebaseDatabase
                .getInstance()
                .getReference("Groups");

        mRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRef.removeEventListener(valueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRef.addValueEventListener(valueEventListener);

    }
}
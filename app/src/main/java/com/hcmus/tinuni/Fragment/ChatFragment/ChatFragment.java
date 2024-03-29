package com.hcmus.tinuni.Fragment.ChatFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.hcmus.tinuni.Model.Chat;
import com.hcmus.tinuni.Model.ChatList;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<User> mItems;
    private List<ChatList> mChatLists;
    private List<Long> mRecentTimes;
    private List<Boolean> mIsSeen;

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

        return view;
    }

    private void getChatList() {
        // Getting all chats
        mItems = new ArrayList<>();
        mRecentTimes = new ArrayList<>();
        mIsSeen = new ArrayList<>();
//        List<Object> mUsers = new ArrayList<>(mItems);
//        List<Object> mGroups = new ArrayList<>(mItems);

        mRef = FirebaseDatabase
                .getInstance()
                .getReference("Users");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mItems.clear();
                mRecentTimes.clear();
                mIsSeen.clear();
                // Get Users
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for (ChatList chatList : mChatLists) {
                        if (user.getId().equals(chatList.getId())) {
                            //mItems.add(user);
                            getLastMessageTimeFromUser(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), mItems, true, mIsSeen);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void getLastMessageTimeFromUser(User user) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        String id = user.getId();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long time=0;
                Boolean isSeen = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if ((chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(id)) ||
                            (chat.getSender().equals(id) && chat.getReceiver().equals(firebaseUser.getUid()))) {
                        time = Long.parseLong(chat.getTime());

                        isSeen = chat.getSender().equals(firebaseUser.getUid()) ?true : chat.isSeen();
                    }

                }
                insert(user, time, isSeen);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void insert(User user, long time, Boolean isSeen){
        if (isSeen == null)
            isSeen = false;
        if (mItems.size() == 0){
            mItems.add(user);
            mRecentTimes.add(time);
            mIsSeen.add(isSeen);
            return;
        }
        for (int i=0;i<mItems.size();i++) {
            if (mItems.get(i).getId().equals(user.getId())) {
                mItems.remove(i);
                mRecentTimes.remove(i);
                mIsSeen.remove(i);
                break;
            }
        }
        for (int i=0;i<mItems.size();i++){
            if (mRecentTimes.get(i) < time){
                mItems.add(i,user);
                mRecentTimes.add(i,time);
                mIsSeen.add(i,isSeen);
                return;
            }
        }
        mItems.add(user);
        mRecentTimes.add(time);
        mIsSeen.add(isSeen);
    }
}
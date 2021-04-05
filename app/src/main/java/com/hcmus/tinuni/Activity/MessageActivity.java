package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Adapter.MessageAdapter;
import com.hcmus.tinuni.Model.Chat;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends Activity {

    private TextView username;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private EditText txtSend;
    private Button btnSend;

    private FirebaseUser mUser;
    private DatabaseReference mRef;

    MessageAdapter messageAdapter;
    List<Chat> mChats;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageView = findViewById(R.id.imageView);
        username = findViewById(R.id.username);
        recyclerView = findViewById(R.id.recyclerView);
        txtSend = findViewById(R.id.txtSend);
        btnSend = findViewById(R.id.btnSend);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        // Receiver
        Intent i = getIntent();
        userId = i.getStringExtra("userId");

        // Sender
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mUser = FirebaseAuth
                .getInstance()
                .getCurrentUser();
        mRef = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(userId);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUserName());

                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(MessageActivity.this)
                            .load(user.getImageURL())
                            .into(imageView);
                }

                readMessages(mUser.getUid(), userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = txtSend.getText().toString();
                if(!msg.equals("")) {
                    sendMessage(mUser.getUid(), userId, msg);

                    txtSend.setText("");
                }
            }
        });

    }

    private void sendMessage(String sender, String receiver, String msg) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        Chat chat = new Chat(sender, receiver, msg);

        reference.push().setValue(chat);

        // Get the latest chat message
        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(mUser.getUid())
                .child(userId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessages(String myId, String userId, String imgURL) {
        mChats = new ArrayList<>();

        mRef= FirebaseDatabase.getInstance().getReference("Chats");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if((chat.getReceiver().equals(myId) && chat.getSender().equals(userId)) ||
                        (chat.getReceiver().equals(userId) && chat.getSender().equals(myId))) {
                        mChats.add(chat);
                    }

                }
                messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imgURL);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
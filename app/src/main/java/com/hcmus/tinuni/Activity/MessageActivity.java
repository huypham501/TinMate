package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hcmus.tinuni.Activity.Profile.ShowProfileActivity;
import com.hcmus.tinuni.Adapter.MessageAdapter;
import com.hcmus.tinuni.Model.Chat;
import com.hcmus.tinuni.Model.ChatGroup;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends Activity {

    private TextView username;
    private ImageView imageView, btnGoBack, btnSendImage;
    private RecyclerView recyclerView;
    private EditText txtSend;
    private Button btnSend;
    private Uri imageUri;
    private RelativeLayout setting;

    private FirebaseUser mUser;
    private DatabaseReference mRef;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private AlertDialog alertDialog;

    MessageAdapter messageAdapter;

    List<Object> mItems;
    List<String> imgURLs;

    String userId;
    String groupId;

    String file_link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageView = findViewById(R.id.imageView);
        username = findViewById(R.id.groupName);
        recyclerView = findViewById(R.id.recyclerView);
        txtSend = findViewById(R.id.txtSend);
        btnSend = findViewById(R.id.btnSend);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnSendImage = findViewById(R.id.btnSendImage);
        setting = findViewById(R.id.setting);

        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mItems = new ArrayList<>();
        imgURLs = new ArrayList<>();

        // Receiver
        Intent i = getIntent();
        userId = i.getStringExtra("userId");
        groupId = i.getStringExtra("groupId");

        // Sender
        mUser = FirebaseAuth
                .getInstance()
                .getCurrentUser();

        if (!userId.isEmpty()) {
            mRef = FirebaseDatabase
                    .getInstance()
                    .getReference("Users")
                    .child(userId);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username.setText(user.getUserName());

                    if (user.getImageURL().equals("default")) {
                        imageView.setImageResource(R.drawable.profile_image);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(user.getImageURL())
                                .into(imageView);
                    }

                    readMessagesFromUser(user.getImageURL());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            mRef.addValueEventListener(valueEventListener);
        } else {
            mRef = FirebaseDatabase
                    .getInstance()
                    .getReference("Groups")
                    .child(groupId);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Group group = snapshot.getValue(Group.class);
                    username.setText(group.getName());

                    if (group.getImageURL().equals("default")) {
                        imageView.setImageResource(R.drawable.profile_image);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(group.getImageURL())
                                .into(imageView);
                    }

                    readMessagesFromGroup();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            mRef.addListenerForSingleValueEvent(valueEventListener);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = txtSend.getText().toString();
                String time = String.valueOf(System.currentTimeMillis());

                if (!msg.equals("")) {
                    sendMessage(msg, time, "text");
                    txtSend.setText("");
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
                MessageActivity.super.onBackPressed();
            }
        });

        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupId.equals("")) {
                    Intent intent = new Intent(MessageActivity.this, ShowProfileActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(MessageActivity.this, SettingGroupActivity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
            }
        });

    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadFile();
        }
    }

    private void uploadFile() {
        StorageReference storageRef = storageReference.child("files/chats/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //add new image
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        file_link = uri.toString();
                        System.out.println("File link: " + file_link);
                        String time = String.valueOf(System.currentTimeMillis());

                        if (getFileExtension(imageUri).contains("mp")) {
                            sendMessage(file_link, time, "media");
                        } else {
                            sendMessage(file_link, time, "image");
                        }

                        alertDialog.dismiss();
                        Toast.makeText(MessageActivity.this, "Upload SUCCESS", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                alertDialog.show();
//              Toast.makeText(EditProfileActivity.this, "Uploadinggg", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
                Toast.makeText(MessageActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendMessage(String msg, String time, String type) {
        if (!userId.isEmpty()) {
            sendMessageToUser(msg, time, type);
        } else {
            sendMessageToGroup(msg, time, type);
        }
    }

    private void sendMessageToUser(String msg, String time, String type) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        // Create chat
        Chat chat = new Chat(mUser.getUid(), userId, msg, time, false, type);

        reference.push().setValue(chat);

        // Get the latest chat message for sender
        final DatabaseReference chatSenderRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(mUser.getUid())
                .child(userId);

        chatSenderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatSenderRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Get the latest chat message for receiver
        final DatabaseReference chatReceiverRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(userId)
                .child(mUser.getUid());

        chatReceiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatReceiverRef.child("id").setValue(mUser.getUid());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessageToGroup(String msg, String time, String type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");

        ChatGroup chatGroup = new ChatGroup(mUser.getUid(), msg, time, type);
        reference.child(groupId)
                .child("Messages")
                .push().setValue(chatGroup);

        DatabaseReference chatListRef = FirebaseDatabase.getInstance().getReference("ChatList");

        reference.child(groupId).child("Participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String user_id = dataSnapshot.getKey();

                    chatListRef.child(user_id).child(groupId);
                    chatListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                chatListRef.child("id").setValue(groupId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                    chatListRef.child(user_id).child(groupId).child("id").setValue(groupId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // Read message listener from personal chat
    ValueEventListener readMessagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            mItems.clear();

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Chat chat = dataSnapshot.getValue(Chat.class);

                if ((chat.getReceiver().equals(mUser.getUid()) && chat.getSender().equals(userId)) ||
                        (chat.getReceiver().equals(userId) && chat.getSender().equals(mUser.getUid()))) {
                    mItems.add(chat);
                    if (chat.getSender().equals(userId))
                        dataSnapshot.getRef().child("seen").setValue(true);
                }

            }

            messageAdapter = new MessageAdapter(MessageActivity.this, mItems, imgURLs);
            recyclerView.setAdapter(messageAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    // Read message listener from group chat
    ValueEventListener readGroupMessagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            mItems.clear();
            imgURLs.clear();

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                ChatGroup chatGroup = dataSnapshot.getValue(ChatGroup.class);
                dataSnapshot.getRef().child("seen").child(mUser.getUid()).setValue(true);
                mItems.add(chatGroup);

            }
            getImgURLs();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    // Read messages function
    private void readMessagesFromUser(String imgURL) {
        imgURLs.add(imgURL);

        mRef = FirebaseDatabase.getInstance().getReference("Chats");
        mRef.addValueEventListener(readMessagesListener);
    }

    private void readMessagesFromGroup() {

        mRef = FirebaseDatabase.getInstance()
                .getReference("Groups")
                .child(groupId)
                .child("Messages");
        mRef.addValueEventListener(readGroupMessagesListener);
    }

    private void getImgURLs() {
        mRef = FirebaseDatabase
                .getInstance()
                .getReference("Users");
        List<User> users = new ArrayList<>();
        // Get list user
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imgURLs.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }

                for (Object item : mItems) {
                    ChatGroup chatGroup = (ChatGroup) item;

                    for(User user : users) {
                        if (chatGroup.getSender().equals(user.getId())) {
                            imgURLs.add(user.getImageURL());
                            Log.e("IMAGE>> ", user.getImageURL());
                        }

                    }

                }
                messageAdapter = new MessageAdapter(MessageActivity.this, mItems, imgURLs);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("TAG>>", String.valueOf(error.toException()));
            }
        });


    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!userId.isEmpty())
            mRef.removeEventListener(readMessagesListener);
        else mRef.removeEventListener(readGroupMessagesListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userId.isEmpty())
            FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(readMessagesListener);
        else FirebaseDatabase.getInstance()
                .getReference("Groups")
                .child(groupId)
                .child("Messages").addValueEventListener(readGroupMessagesListener);
    }

}

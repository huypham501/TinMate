package com.hcmus.tinuni.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.Chat;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import java.util.HashMap;
import java.util.Map;

public class AddGroupActivity extends Activity {
    ImageView btnGoBack;
    EditText edtGroupName;
    Button btnSave;

    FirebaseUser mUser;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);


        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupActivity.super.onBackPressed();
            }
        });

        edtGroupName = findViewById(R.id.edtGroupName);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Groups");

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = edtGroupName.getText().toString();
                if(!groupName.equals("")) {
                    String groupId = String.valueOf(System.currentTimeMillis());
                    Group group = new Group(groupId, groupName, "https://firebasestorage.googleapis.com/v0/b/tinuni.appspot.com/o/images%2Favatars%2Fdefault_group.png?alt=media&token=ac09066b-0948-4d70-a73d-ea96e3967470");

                    mRef.child(groupId).setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Create Participants
                            Map<String, String> map = new HashMap<>();
                            map.put("id", mUser.getUid());
                            map.put("role", "owner");

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                            reference
                                    .child(groupId)
                                    .child("Participants")
                                    .child(mUser.getUid()).setValue(map);


                            // Create ChatList
                            final DatabaseReference chatListRef = FirebaseDatabase.getInstance()
                                    .getReference("ChatList")
                                    .child(mUser.getUid())
                                    .child(groupId);

                            chatListRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists()){
                                        chatListRef.child("id").setValue(groupId);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });
                    edtGroupName.setText("");
                }
            }
        });


    }
}
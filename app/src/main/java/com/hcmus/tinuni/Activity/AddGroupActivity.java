package com.hcmus.tinuni.Activity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
                    Group group = new Group(groupName, "default");

                    String groupId = String.valueOf(System.currentTimeMillis());
                    mRef.child(groupId).setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String, String> map = new HashMap<>();
                            map.put("id", mUser.getUid());
                            map.put("role", "owner");

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                            reference
                                    .child(groupId)
                                    .child("Participants")
                                    .child(mUser.getUid()).setValue(map);

                        }
                    });
                }
            }
        });


    }
}
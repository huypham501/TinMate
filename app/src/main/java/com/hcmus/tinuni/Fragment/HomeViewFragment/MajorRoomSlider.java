package com.hcmus.tinuni.Fragment.HomeViewFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class MajorRoomSlider extends Fragment {

    RecyclerView majorRoomRecyclerView;
    ArrayList<MainModel> mainModels;
    MainAdapter mainAdapter;
    TextView textViewListRoom;
    private DatabaseReference mRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.major_rooms, container, false);
        majorRoomRecyclerView = view.findViewById(R.id.recyclerViewMajorRooms);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        majorRoomRecyclerView.setLayoutManager(layoutManager);
        majorRoomRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mainModels = new ArrayList<>();

        mRef = FirebaseDatabase.getInstance().getReference("Groups");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Group group = data.getValue(Group.class);
                    long membersCount = data.child("Participants").getChildrenCount();
                    mainModels.add(new MainModel(group.getId(),group.getImageURL(),group.getName(),membersCount));
                }
                mainAdapter = new MainAdapter(mainModels, getContext());
                majorRoomRecyclerView.setAdapter(mainAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


//        Integer[] studyRoom = {R.drawable.android, R.drawable.ba, R.drawable.software_design,R.drawable.java,
//                R.drawable.software_technology};
//
//        String[] roomName = {"Phat trien ung dung di dong", "Phan tich va quan ly yeu cau phan mem","Thiet ke phan mem",
//                "Lap trinh Java", "Nhap mon cong nghe phan mem"};
//
//        String[] amount = {"60","60","60","60","60"};

        return view;
    }
}



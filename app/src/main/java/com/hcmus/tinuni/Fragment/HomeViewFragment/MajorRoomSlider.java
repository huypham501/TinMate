package com.hcmus.tinuni.Fragment.HomeViewFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class MajorRoomSlider extends Fragment {

    RecyclerView majorRoomRecyclerView;
    ArrayList<MainModel> mainModels;
    MainAdapter mainAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.major_rooms, container, false);
        majorRoomRecyclerView = view.findViewById(R.id.recyclerViewMajorRooms);
        Integer[] studyRoom = {R.drawable.android, R.drawable.ba, R.drawable.software_design,R.drawable.java,
                R.drawable.software_technology};

        String[] roomName = {"Phat trien ung dung di dong", "Phan tich va quan ly yeu cau phan mem","Thiet ke phan mem",
                "Lap trinh Java", "Nhap mon cong nghe phan mem"};

        String[] amount = {"60","60","60","60","60"};
        mainModels = new ArrayList<>();
        for(int i=0;i<studyRoom.length;i++){
            MainModel model = new MainModel(studyRoom[i],roomName[i],amount[i]);
            mainModels.add(model);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        majorRoomRecyclerView.setLayoutManager(layoutManager);
        majorRoomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mainAdapter = new MainAdapter(mainModels, getContext());
        majorRoomRecyclerView.setAdapter(mainAdapter);

        return view;
    }
}

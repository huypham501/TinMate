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

import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class GeneralRoomSlider extends Fragment {

    RecyclerView majorRoomRecyclerView;
    ArrayList<MainModel> mainModels;
    MainAdapter mainAdapter;
    TextView textViewListRoom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.major_rooms, container, false);
        majorRoomRecyclerView = view.findViewById(R.id.recyclerViewMajorRooms);
//
//        Integer[] studyRoom = {R.drawable.triethoc, R.drawable.kinh_te_chinh_tri, R.drawable.chu_nghia_xa_hoi,
//                R.drawable.lich_su_dang, R.drawable.phap_luat_dai_cuong, R.drawable.xac_suat_thong_ke};
//
//        String[] roomName = {"Triet hoc", "Kinh te chinh tri","Chu nghia xa hoi khoa hoc",
//                "Lich su Dang", "Phap luat dai cuong","Xac suat thong ke"};
//
//        String[] amount = {"60","60","60","60","60","60"};
//        mainModels = new ArrayList<>();
//        for(int i=0;i<studyRoom.length;i++){
//            MainModel model = new MainModel(studyRoom[i],roomName[i],amount[i]);
//            mainModels.add(model);
//        }
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
//                LinearLayoutManager.HORIZONTAL,
//                false);
//        majorRoomRecyclerView.setLayoutManager(layoutManager);
//        majorRoomRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mainAdapter = new MainAdapter(mainModels, getContext());
//        majorRoomRecyclerView.setAdapter(mainAdapter);
//        textViewListRoom = view.findViewById(R.id.textViewMajorRoomsName);
//        textViewListRoom.setText("General Subjects");
       

        return view;
    }
}

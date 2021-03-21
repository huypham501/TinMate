package com.hcmus.tinuni.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hcmus.tinuni.R;

import java.util.Random;

public class MatchingFragment extends Fragment {
    private final Integer rooms[] = {
            R.drawable.room_1,
            R.drawable.room_2,
            R.drawable.room_3,
            R.drawable.room_4,
            R.drawable.room_5,
            R.drawable.room_6,
            R.drawable.room_7,
            R.drawable.room_8,
            R.drawable.room_9,
            R.drawable.room_10
    };
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matching, container, false);

        imageView = view.findViewById(R.id.imageRoom);

        // Random room image
        Random r = new Random();
        int num = r.nextInt(rooms.length);

        imageView.setBackgroundResource(rooms[num]);

        return view;
    }
}
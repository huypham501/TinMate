package com.hcmus.tinuni.Fragment.Admin;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hcmus.tinuni.R;

public class AdminHomeFragment extends Fragment {
    //-----------------------------------------------------
    private AdminHomeFragmentListener listener;
    private ImageView manage_room;
    private  ImageView manage_user;
    private ImageView view_diary;
    private  ImageView view_report;
    //-----------------------------------------------------
    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public interface AdminHomeFragmentListener{
        void onInputHomeSent(CharSequence input);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        //Animated background
        RelativeLayout relativeLayout = view.findViewById(R.id.admin_home_animated_gradient_bg);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        //Set up buttons
        manage_room = view.findViewById(R.id.manage_room);
        manage_user = view.findViewById(R.id.manage_user);
        view_diary = view.findViewById(R.id.admin_diary);
        view_report = view.findViewById(R.id.user_report);
        //------------------------------------------------------------------------------
        //Send message to AdminInitialActivity so it'll navigate to corresponding Manage fragment
        manage_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to manage room activity
                CharSequence input = "MANAGE ROOM";
                listener.onInputHomeSent(input);
            }
        });
        manage_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to manage user activity
                CharSequence input = "MANAGE USER";
                listener.onInputHomeSent(input);
            }
        });
        view_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to manage demand activity
                CharSequence input = "ADMIN DIARY";
                listener.onInputHomeSent(input);
            }
        });
        view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to manage demand activity
                CharSequence input = "USER REPORT";
                listener.onInputHomeSent(input);
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AdminHomeFragmentListener) {
            listener = (AdminHomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
            + "must implement AdminHomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
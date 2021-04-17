package com.hcmus.tinuni.Fragment.Admin;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcmus.tinuni.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminRoomFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminRoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminRoomFragment newInstance(String param1, String param2) {
        AdminRoomFragment fragment = new AdminRoomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_room, container, false);

        //Animated background
//        RelativeLayout relativeLayout = view.findViewById(R.id.animated_gradient_bg);
//        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
//        animationDrawable.setEnterFadeDuration(2000);
//        animationDrawable.setExitFadeDuration(4000);
//        animationDrawable.start();

        //Toolbar
        Toolbar toolbar = view.findViewById(R.id.admin_toolbar);
        TextView toolbar_title = view.findViewById(R.id.admin_toolbar_title);
        toolbar_title.setText("MANAGE ROOM");
        ImageView log_out = view.findViewById(R.id.admin_log_out);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log out
            }
        });

        //Button
        ImageView manage_room = view.findViewById(R.id.manage_room);
        ImageView manage_user = view.findViewById(R.id.manage_user);
        ImageView manage_demand = view.findViewById(R.id.manage_demand);
        //System.out.println("----------------------------------------------");
        return view;
    }
}
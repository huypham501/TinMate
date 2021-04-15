package com.hcmus.tinuni.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

class MatchingAdapter extends PagerAdapter {

    private Context mContext;
    public int rooms[] = {
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


    public MatchingAdapter(Context context){
        mContext = context;
    }

    public MatchingAdapter(MatchingFragment matchingFragment) {
    }

    @Override
    public int getCount() {
        return rooms.length ;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.room_matching,container,false);

        ImageView imageView = view.findViewById(R.id.imageRoom);


        imageView.setImageResource(rooms[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}


public class MatchingFragment extends Fragment {

    public MatchingFragment() {
        // Required empty public constructor
    }

    public ViewPager viewPager;
    MatchingAdapter adapter;

    private int mCurrentPage;

    private FirebaseUser mUser;
    private DatabaseReference mRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_matching, container, false);

//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        TextView tabName = (TextView) toolbar.getChildAt(1);
//        tabName.setText("Home");

        mUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        mRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mUser.getUid());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

//                ImageView imageView = (ImageView) toolbar.getChildAt(0);
//                if (user.getImageURL().equals("default")) {
//                    imageView.setImageResource(R.drawable.profile_image);
//                } else {
//                    Glide.with(HomeFragment.this)
//                            .load(user.getImageURL())
//                            .into(imageView);
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewPager = view.findViewById(R.id.viewPagerMatching);

        adapter = new MatchingAdapter(getContext());

        viewPager.setAdapter(adapter);

        return view;
    }




}
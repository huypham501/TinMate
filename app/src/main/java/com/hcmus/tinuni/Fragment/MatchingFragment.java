package com.hcmus.tinuni.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

class MatchingAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Group> arrayListGroup = new ArrayList<>();

    public MatchingAdapter(Context context, ArrayList<Group> arrayListGroup) {
        this.context = context;
        this.arrayListGroup = arrayListGroup;
    }

    public MatchingAdapter(Context context) {
        this.context = context;
    }

    public MatchingAdapter(MatchingFragment matchingFragment) {
    }

    @Override
    public int getCount() {
        return arrayListGroup.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        Group group = arrayListGroup.get(position);

        View view = layoutInflater.inflate(R.layout.room_matching, container, false);
        ImageView imageView = view.findViewById(R.id.imageRoom);

        TextView textViewSchool = view.findViewById(R.id.textViewSchool);
        TextView textViewSubject = view.findViewById(R.id.textViewSubject);
        TextView textViewMajor = view.findViewById(R.id.textViewMajor);

        if (group.getImageURL().equals("default")) {
            imageView.setImageResource(R.drawable.room_1);
        } else {
            Glide.with(context)
                    .load(group.getImageURL())
                    .into(imageView);
        }

        textViewSubject.setText(group.getSubject());
        textViewSchool.setText(group.getSchool());
        textViewMajor.setText(group.getMajor());

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}


public class MatchingFragment extends Fragment {

    private ViewPager viewPager;
    private MatchingAdapter matchingAdapter;

    private int currentPagePosition;
    private String userId;

    public MatchingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_matching, container, false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        viewPager = view.findViewById(R.id.viewPagerMatching);

        loadGroupSuggest();

        return view;
    }

    private void loadGroupSuggest() {
        DatabaseReference databaseReferenceSuggests = FirebaseDatabase.getInstance().getReference("Suggests").child(userId);
        databaseReferenceSuggests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                } else {

                    ArrayList<Group> arrayListGroup = new ArrayList<>();


                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String strGroupId = dataSnapshot.getKey();

                        DatabaseReference databaseReferenceGroups = FirebaseDatabase.getInstance().getReference("Groups").child(strGroupId);
                        databaseReferenceGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot groupSnapshot) {
                                if (!groupSnapshot.exists()) {

                                } else {
                                    System.out.println(groupSnapshot.getValue());
                                    Group group = groupSnapshot.getValue(Group.class);

                                    System.out.println(group.toString());
//                                String strId = snapshot.child("id").getValue().toString();
//                                String strName = snapshot.child("name").getValue().toString();
//                                String strImageURL = snapshot.child("imageURL").getValue().toString();
//                                String strSubject = snapshot.child("subject").getValue().toString();
//                                String strMajor = snapshot.child("major").getValue().toString();
//                                String strSchool = snapshot.child("school").getValue().toString();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
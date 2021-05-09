package com.hcmus.tinuni.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Admin.AdminInitialActivity;
import com.hcmus.tinuni.Activity.Authentication.SignInActivity;
import com.hcmus.tinuni.Activity.Profile.UserProfileActivity;
import com.hcmus.tinuni.Fragment.ConversationFragment;
import com.hcmus.tinuni.Fragment.HomeFragment;
import com.hcmus.tinuni.Fragment.MatchingFragment;
import com.hcmus.tinuni.Fragment.FriendsFragment;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private FirebaseUser mUser;
    private DatabaseReference mRef;

    TabLayout tabLayout;
    CustomViewPager viewPager;
    TextView tabName;

    ImageView imageViewManageDemand, addGroup, addFriend, imageView;
    private final int[] tabIcons = {
            R.drawable.home,
            R.drawable.fire,
            R.drawable.chat,
            R.drawable.users
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        mUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        if (mUser == null) {
            Intent i = new Intent(MainActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else
            mRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(mUser.getUid());

        imageView = (ImageView) toolbar.getChildAt(0);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(MainActivity.this)
                            .load(user.getImageURL())
                            .into(imageView);
//                    Glide.with(getApplicationContext())
//                            .load(user.getImageURL())
//                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tabName = (TextView) toolbar.findViewById(R.id.textViewTabName);
        imageViewManageDemand = (ImageView) toolbar.findViewById(R.id.imageViewManageDemand);
        addGroup = (ImageView) toolbar.findViewById(R.id.imageViewAddGroup);
        addFriend = (ImageView) toolbar.findViewById(R.id.imageViewAddFriend);

        imageViewManageDemand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DemandManageActivity.class);
                startActivity(intent);
            }
        });

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddGroupActivity.class);
                startActivity(intent);
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_profile = new Intent(MainActivity.this, UserProfileActivity.class);
                intent_profile.putExtra("id", mUser.getUid());
                startActivity(intent_profile);
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        viewPager.setSwipeable(false);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new HomeFragment(), "Home");
        viewPageAdapter.addFragment(new MatchingFragment(), "Match");
        viewPageAdapter.addFragment(new ConversationFragment(), "Chat");
        viewPageAdapter.addFragment(new FriendsFragment(), "Friends");

        viewPager.setAdapter(viewPageAdapter);

        tabLayout.setupWithViewPager(viewPager);

        //Icons for TabLayout
        setTabIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getText().toString()) {
                    case "Home":
                        tabName.setText("Home");
                        addGroup.setVisibility(View.GONE);
                        imageViewManageDemand.setVisibility(View.GONE);
                        addFriend.setVisibility(View.VISIBLE);
                        break;
                    case "Match":
                        tabName.setText("Match");
                        imageViewManageDemand.setVisibility(View.VISIBLE);
                        addGroup.setVisibility(View.GONE);
                        addFriend.setVisibility(View.GONE);
                        break;
                    case "Chat":
                        tabName.setText("Chat");
                        addGroup.setVisibility(View.VISIBLE);
                        imageViewManageDemand.setVisibility(View.GONE);
                        addFriend.setVisibility(View.GONE);
                        break;
                    case "Friends":
                        tabName.setText("Friends");
                        imageViewManageDemand.setVisibility(View.GONE);
                        addFriend.setVisibility(View.GONE);
                        break;
                    default:
                        throw null;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setTabIcons() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

    // Create Menu: Profile, Sign Out
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    //    // Listening Menu Item selected
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.profile:
//                Intent intent_profile = new Intent(MainActivity.this, UserProfileActitivy.class);
//                intent_profile.putExtra("id", mUser.getUid());
//                startActivity(intent_profile);
//                return true;
//            case R.id.signOut:
//                FirebaseAuth.getInstance().signOut();
//                System.out.println("***********************************************");
//                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
//                startActivity(intent);
//                finish();
//                return true;
//            default:
//                break;
//        }
//        return false;
//    }

    // ViewPageAdapter
    static class ViewPageAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        public ViewPageAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    // Tạo ra ViewPager riêng để tắt chức năng quẹt trái/phải để chuyển fragment
    public static class CustomViewPager extends ViewPager {
        private boolean swipeable = false;

        public CustomViewPager(Context context) {
            super(context);
        }

        public CustomViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        // Call this method in your motion events when you want to disable or enable
        // It should work as desired.
        public void setSwipeable(boolean swipeable) {
            this.swipeable = swipeable;
        }
//
//        @Override
//        public boolean onInterceptTouchEvent(MotionEvent arg0) {
//            return (this.swipeable) ? super.onInterceptTouchEvent(arg0) : false;
//        }

        @Override
        public boolean onTouchEvent(MotionEvent arg0) {
            return (this.swipeable) && super.onTouchEvent(arg0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("Roles").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                if (role != null && role.equals("admin")) {
                    Intent i = new Intent(MainActivity.this, AdminInitialActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
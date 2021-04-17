package com.hcmus.tinuni.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hcmus.tinuni.Fragment.Admin.AdminDemandFragment;
import com.hcmus.tinuni.Fragment.Admin.AdminHomeFragment;
import com.hcmus.tinuni.Fragment.Admin.AdminRoomFragment;
import com.hcmus.tinuni.Fragment.Admin.AdminUserFragment;
import com.hcmus.tinuni.R;

import static android.widget.Toast.LENGTH_SHORT;

public class InitialActivity extends FragmentActivity {

    protected BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.adminHome:
                            selectedFragment = new AdminHomeFragment();
                            break;
                        case R.id.manageRoomFragment:
                            selectedFragment = new AdminRoomFragment();
                            break;
                        case R.id.manageUserFragment:
                            selectedFragment = new AdminUserFragment();
                            break;
                        case R.id.manageDemandFragment:
                            selectedFragment = new AdminDemandFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_initial);

//        //Set up Toolbar
//        Toolbar toolbar = findViewById(R.id.admin_toolbar);
//        TextView toolbar_title = findViewById(R.id.admin_toolbar_title);
//        toolbar_title.setText("MANAGE");
//        ImageView log_out = findViewById(R.id.admin_log_out);
//        log_out.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Log out
//            }
//        });

        //Set up navigation bar
        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Stick home fragment to screen
        Fragment home_fragment = new AdminHomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container, home_fragment).commit();
        }
}

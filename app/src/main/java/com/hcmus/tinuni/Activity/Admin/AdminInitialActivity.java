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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hcmus.tinuni.Activity.Authentication.SignInActivity;
import com.hcmus.tinuni.Activity.Profile.UserProfileActitivy;
import com.hcmus.tinuni.Fragment.Admin.AdminDemandFragment;
import com.hcmus.tinuni.Fragment.Admin.AdminHomeFragment;
import com.hcmus.tinuni.Fragment.Admin.AdminRoomFragment;
import com.hcmus.tinuni.Fragment.Admin.AdminUserFragment;
import com.hcmus.tinuni.R;

import static android.widget.Toast.LENGTH_SHORT;

public class AdminInitialActivity extends FragmentActivity implements AdminHomeFragment.AdminHomeFragmentListener{
    //---------------------------------------------
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView log_out_btn;

    private BottomNavigationView bottomNav;

    private AdminHomeFragment homeFrag;
    private AdminRoomFragment roomFrag;
    private  AdminUserFragment userFrag;
    private  AdminDemandFragment demandFrag;
    //---------------------------------------------
    protected BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.adminHome:
                            selectedFragment = new AdminHomeFragment();
                            toolbar_title.setText("MANAGE");
                            break;
                        case R.id.manageRoomFragment:
                            selectedFragment = new AdminRoomFragment();
                            toolbar_title.setText("MANAGE ROOM");
                            break;
                        case R.id.manageUserFragment:
                            selectedFragment = new AdminUserFragment();
                            toolbar_title.setText("MANAGE USER");
                            break;
                        case R.id.manageDemandFragment:
                            selectedFragment = new AdminDemandFragment();
                            toolbar_title.setText("MANAGE DEMAND");
                            break;
                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_initial);

        //Set up Toolbar
        toolbar = findViewById(R.id.admin_toolbar);
        toolbar_title = findViewById(R.id.admin_toolbar_title);
        toolbar_title.setText("MANAGE");
        log_out_btn = findViewById(R.id.admin_log_out);
        log_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log out
                new AlertDialog.Builder(AdminInitialActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                System.out.println("***********************************************");
                                Intent intent = new Intent(AdminInitialActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        //Set up navigation bar
        bottomNav = findViewById(R.id.admin_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Allocate needed fragments
        homeFrag = new AdminHomeFragment();
        roomFrag = new AdminRoomFragment();
        userFrag = new AdminUserFragment();
        demandFrag = new AdminDemandFragment();

        //Stick AdminHomeFragment to screen
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container, homeFrag).commit();
    }

    //Listen to message send from AdminHomeFragment
    @Override
    public void onInputHomeSent(CharSequence input) {
        String message = input.toString();
        Fragment selectedFrag = null;
        int navItemIndex = -1;

        if (message.equals("ROOM")){
            selectedFrag = roomFrag;
            navItemIndex = 1;
        }
        if (message.equals("USER")){
            selectedFrag = userFrag;
            navItemIndex = 2;
        }
        if (message.equals("DEMAND")){
            selectedFrag = demandFrag;
            navItemIndex = 3;
        }

        message = "MANAGE " + message;
        toolbar_title.setText(message);
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container, selectedFrag).commit();
        bottomNav.getMenu().getItem(navItemIndex).setChecked(true);
    }
}

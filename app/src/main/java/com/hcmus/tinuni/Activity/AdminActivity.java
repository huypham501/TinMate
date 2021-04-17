package com.hcmus.tinuni.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hcmus.tinuni.R;

public class AdminActivity extends Activity {
    private ImageButton btn_menu;
    private ImageButton btn_cancel;
    private RelativeLayout toggle_menu;
    private RelativeLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setComponents();

    }
    void setComponents(){
        btn_menu = findViewById(R.id.btn_menu);
        btn_cancel = findViewById(R.id.btn_cancel);
        toggle_menu = findViewById(R.id.toggle_menu);
        container = findViewById(R.id.container);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fade fadeIn = new Fade(Fade.IN);
                TransitionManager.beginDelayedTransition(toggle_menu, fadeIn);
                btn_menu.setVisibility(View.GONE);
                toggle_menu.setVisibility(View.VISIBLE);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fade fadeOut = new Fade(Fade.OUT);
                TransitionManager.beginDelayedTransition(toggle_menu, fadeOut);
                btn_menu.setVisibility(View.VISIBLE);
                toggle_menu.setVisibility(View.GONE);
            }
        });

    }
}
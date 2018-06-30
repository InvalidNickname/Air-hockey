
/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 19:45
 */

package hockey.airhockey;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import static hockey.airhockey.MainActivity.HIDE_FLAGS;
import static hockey.airhockey.MainActivity.settings;

public class WinActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(0, 0);
        TextView winText = findViewById(R.id.winText);
        Intent intent = getIntent();
        int winner = intent.getIntExtra("winner", 0);
        if (winner == 1 & !intent.getBooleanExtra("multiplayer", true)) {
            winText.setText(R.string.lose_ai);
        } else if (!intent.getBooleanExtra("multiplayer", true)) {
            winText.setText(R.string.win_ai);
        }
        ConstraintLayout layout = findViewById(R.id.layout);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) layout.getLayoutParams();
        params.height = settings.height / 3;
        layout.setLayoutParams(params);
        if (intent.getBooleanExtra("firstWin", false)) {
            ObjectAnimator set = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.slide);
            set.setFloatValues(settings.width, 0);
            set.setTarget(layout);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ObjectAnimator set = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.fade_in);
                    if (getIntent().getIntExtra("winner", 0) == 1) {
                        set.setTarget(findViewById(R.id.lowerBlocker));
                        set.start();
                    } else {
                        set.setTarget(findViewById(R.id.upperBlocker));
                        set.start();
                    }
                }
            });
            set.start();
        } else {
            if (getIntent().getIntExtra("winner", 0) == 1) {
                findViewById(R.id.lowerBlocker).setAlpha(1);
            } else {
                findViewById(R.id.upperBlocker).setAlpha(1);
            }
        }
        setListener(findViewById(R.id.restart));
        setListener(findViewById(R.id.menuButton));
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    public void onBackPressed() {
        exitWithAnimation(new Intent(WinActivity.this, GameCustomActivity.class));
    }

    // анимированный выход из activity
    private void exitWithAnimation(final Intent intent) {
        ObjectAnimator set = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.fade_out);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator set = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.slide);
                set.setFloatValues(0, settings.width);
                set.setTarget(findViewById(R.id.layout));
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        startActivity(intent);
                        finish();
                    }
                });
                set.start();
            }
        });
        if (getIntent().getIntExtra("winner", 0) == 1) {
            set.setTarget(findViewById(R.id.lowerBlocker));
            set.start();
        } else {
            set.setTarget(findViewById(R.id.upperBlocker));
            set.start();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restart: // нажатие на кнопку рестарта
                exitWithAnimation(new Intent(WinActivity.this, GameCustomActivity.class));
                break;
            case R.id.menuButton: // нажатие на кнопку меню
                exitWithAnimation(new Intent(WinActivity.this, MainActivity.class));
                break;
        }
    }

    private void setListener(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.small_decrement);
                    set.setTarget(view);
                    set.start();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.small_increment);
                    set.setTarget(view);
                    set.start();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
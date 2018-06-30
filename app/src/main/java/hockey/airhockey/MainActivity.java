/*
 * Created by Alexey Kiselev
 * Copyright (c) 2018 . All rights reserved.
 * Last modified 30.06.18 17:03
 */

package hockey.airhockey;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "preferences";
    static final int HIDE_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //
            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    static final int PAINT_FLAGS = Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG;
    private static final String APP_PREFERENCES_VOLUME = "volume";
    static float volume;
    static Settings settings;
    private ImageView volumeButton;
    private ImageView start, credits;
    private boolean isAnimation;
    private SharedPreferences preferences;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        settings = new Settings(this);
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        isAnimation = false;
        volume = preferences.getFloat(APP_PREFERENCES_VOLUME, 1);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);
        start = findViewById(R.id.start);
        credits = findViewById(R.id.credits);
        setListener(start);
        setListener(credits);
        TextView versionText = findViewById(R.id.version);
        String versionName = "unknown";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionText.setText(String.format(getResources().getString(R.string.about_version), versionName));
        volumeButton = findViewById(R.id.volumeButton);
        if (volume == 0) {
            volumeButton.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_volume_off, null));
        }
        drawGates();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
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

    private void drawGates() {
        ImageView upperGate = findViewById(R.id.upper_gate);
        ImageView lowerGate = findViewById(R.id.lower_gate);
        ConstraintLayout.LayoutParams upperParams = (ConstraintLayout.LayoutParams) upperGate.getLayoutParams();
        upperParams.height = settings.gateHeight;
        upperParams.width = (int) (settings.width * 0.74) - (int) (settings.width * 0.26);
        upperGate.setLayoutParams(upperParams);
        ConstraintLayout.LayoutParams lowerParams = (ConstraintLayout.LayoutParams) lowerGate.getLayoutParams();
        lowerParams.height = settings.gateHeight;
        lowerParams.width = (int) (settings.width * 0.74) - (int) (settings.width * 0.26);
        lowerGate.setLayoutParams(lowerParams);
    }

    public void onClick(View view) {
        if (!isAnimation) {
            switch (view.getId()) {
                case R.id.start: // нажатие на кнопку старта
                    exitWithAnimation(new Intent(MainActivity.this, GameCustomActivity.class));
                    break;
                case R.id.credits: // нажатие на кнопку инфо
                    exitWithAnimation(new Intent(MainActivity.this, CreditsActivity.class));
                    break;
                case R.id.volumeButton: // нажатие на кнопку изменения громкости
                    if (volume == 1) {
                        volumeButton.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_volume_off, null));
                    } else {
                        volumeButton.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_volume_up, null));
                    }
                    volume = ++volume % 2;
                    break;
            }
        }
    }

    // анимированный выход из activity
    private void exitWithAnimation(final Intent intent) {
        ObjectAnimator left = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.slide);
        left.setFloatValues(start.getX(), -start.getWidth());
        left.setTarget(start);
        left.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                start.setVisibility(GONE);
                credits.setVisibility(GONE);
                startActivity(intent);
                finish();
            }
        });
        ObjectAnimator right = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.slide);
        right.setFloatValues(credits.getX(), settings.width);
        right.setTarget(credits);
        right.start();
        left.start();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(APP_PREFERENCES_VOLUME, volume);
        editor.apply();
    }
}

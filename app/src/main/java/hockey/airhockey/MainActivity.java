package hockey.airhockey;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private Animation left, right;
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
        start.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startDecrementAnimation(start);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startIncrementAnimation(start);
                }
                return false;
            }
        });
        credits.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startDecrementAnimation(credits);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startIncrementAnimation(credits);
                }
                return false;
            }
        });
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
            volumeButton.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.volume_off, null));
        }
        left = AnimationUtils.loadAnimation(this, R.anim.go_left);
        right = AnimationUtils.loadAnimation(this, R.anim.go_right);
        drawGates();
    }

    private void hideSystemUI() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    private void startDecrementAnimation(View view) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.small_decrement);
        set.setTarget(view);
        set.start();
    }

    private void startIncrementAnimation(View view) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.small_increment);
        set.setTarget(view);
        set.start();
    }

    private void drawGates() {
        ImageView upperGate = findViewById(R.id.upper_gate);
        ImageView lowerGate = findViewById(R.id.lower_gate);
        ConstraintLayout.LayoutParams upperParams = new ConstraintLayout.LayoutParams((int) (settings.width * 0.74) - (int) (settings.width * 0.26), settings.gateHeight);
        upperParams.leftToLeft = R.id.main;
        upperParams.rightToRight = R.id.main;
        upperParams.topToTop = R.id.main;
        upperParams.bottomToBottom = R.id.main;
        upperParams.verticalBias = 0;
        upperGate.setLayoutParams(upperParams);
        ConstraintLayout.LayoutParams lowerParams = new ConstraintLayout.LayoutParams((int) (settings.width * 0.74) - (int) (settings.width * 0.26), settings.gateHeight);
        lowerParams.leftToLeft = R.id.main;
        lowerParams.rightToRight = R.id.main;
        lowerParams.topToTop = R.id.main;
        lowerParams.bottomToBottom = R.id.main;
        lowerParams.verticalBias = 1;
        lowerGate.setLayoutParams(lowerParams);
    }

    public void onClick(View view) {
        if (!isAnimation) {
            switch (view.getId()) {
                case R.id.start: // нажатие на кнопку старта
                    setAnimationListener(new Intent(MainActivity.this, GameCustomActivity.class));
                    break;
                case R.id.credits: // нажатие на кнопку инфо
                    setAnimationListener(new Intent(MainActivity.this, CreditsActivity.class));
                    break;
                case R.id.volumeButton: // нажатие на кнопку изменения громкости
                    if (volume == 1) {
                        volumeButton.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.volume_off, null));
                    } else {
                        volumeButton.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.volume_up, null));
                    }
                    volume = ++volume % 2;
                    break;
            }
        }
    }

    private void setAnimationListener(final Intent intent) {
        left.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                start.setVisibility(GONE);
                credits.setVisibility(GONE);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        start.startAnimation(left);
        credits.startAnimation(right);
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

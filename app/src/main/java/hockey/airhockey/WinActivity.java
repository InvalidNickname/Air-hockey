package hockey.airhockey;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static hockey.airhockey.MainActivity.HIDE_FLAGS;

public class WinActivity extends AppCompatActivity {

    private ImageView menu, restart;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(0, 0);
        ImageView upperBlocker = findViewById(R.id.upperBlocker);
        ImageView lowerBlocker = findViewById(R.id.lowerBlocker);
        TextView winText = findViewById(R.id.winText);
        winText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/aldrich.ttf"));
        Intent intent = getIntent();
        restart = findViewById(R.id.restart);
        menu = findViewById(R.id.menuButton);
        restart.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startDecrementAnimation(restart);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startIncrementAnimation(restart);
                }
                return false;
            }
        });
        menu.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startDecrementAnimation(menu);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startIncrementAnimation(menu);
                }
                return false;
            }
        });
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        int winner = intent.getIntExtra("winner", 0);
        if (winner == 1) {
            if (!intent.getBooleanExtra("multiplayer", true)) {
                winText.setText(R.string.lose_ai);
            }
            upperBlocker.setVisibility(View.INVISIBLE);
            lowerBlocker.startAnimation(fade);
        } else {
            if (!intent.getBooleanExtra("multiplayer", true)) {
                winText.setText(R.string.win_ai);
            }
            lowerBlocker.setVisibility(View.INVISIBLE);
            upperBlocker.startAnimation(fade);
        }
    }

    private void hideSystemUI() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(HIDE_FLAGS);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WinActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restart: // нажатие на кнопку рестарта
                Intent intent = new Intent(WinActivity.this, GameCustomActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menuButton: // нажатие на кнопку меню
                onBackPressed();
                break;
        }
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
package hockey.airhockey;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WinActivity extends AppCompatActivity {

    ImageView upperBlocker, lowerBlocker;
    TextView winText;
    Animation fade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(0, 0);
        upperBlocker = findViewById(R.id.upperBlocker);
        lowerBlocker = findViewById(R.id.lowerBlocker);
        winText = findViewById(R.id.winText);
        winText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/aldrich.ttf"));
        Intent intent = getIntent();
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        int winner = intent.getIntExtra("winner", 0);
        if (winner == 1) {
            upperBlocker.setImageAlpha(0);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    lowerBlocker.setImageAlpha(255);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            lowerBlocker.startAnimation(fade);
        } else {
            lowerBlocker.setImageAlpha(0);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    upperBlocker.setImageAlpha(255);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            upperBlocker.startAnimation(fade);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void goToMenu(View view) {
        Intent intent = new Intent(WinActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void restartGame(View view) {
        Intent intent = new Intent(WinActivity.this, GameCustomActivity.class);
        startActivity(intent);
        finish();
    }
}
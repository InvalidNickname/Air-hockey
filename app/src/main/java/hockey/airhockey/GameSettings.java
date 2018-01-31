package hockey.airhockey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class GameSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);
    }

    public void startGame(View view) {
        Intent intent = new Intent(GameSettings.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

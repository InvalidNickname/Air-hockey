package hockey.airhockey;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private static int sessionDepth = 0;

    @Override
    protected void onResume() {
        super.onResume();
        sessionDepth++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sessionDepth > 0) {
            sessionDepth--;
        }
        if (sessionDepth == 0) {
            stopService(new Intent(this, MusicService.class));
        }
    }

}
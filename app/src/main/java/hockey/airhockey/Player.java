package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.playerScale;
import static hockey.airhockey.MainActivity.width;

class Player {

    int x, y, mass;
    private int xp, yp;
    float vX, vY;
    private VectorDrawableCompat drawable;

    Player(int resId, Context context, int num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        mass = context.getResources().getInteger(R.integer.player_mass);
        vX = 0;
        vY = 0;
        x = width / 2;
        if (num == 1) {
            y = 2 * playerScale;
        } else {
            y = height - 2 * playerScale;
        }
        if (drawable != null) {
            drawable.setBounds(x - playerScale, y - playerScale, x + playerScale, y + playerScale);
        }
        xp = x;
        yp = y;
    }

    void drawPlayer(Canvas canvas) {
        drawable.draw(canvas);
    }

    void update() {
        drawable.setBounds(x - playerScale, y - playerScale, x + playerScale, y + playerScale);
    }

    void setV(long sec, long psec) {
        vX = (xp - x) / (sec - psec);
        vY = (yp - y) / (sec - psec);
        xp = x;
        yp = y;
    }

}
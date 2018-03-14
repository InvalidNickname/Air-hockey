package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.playerScale;
import static hockey.airhockey.MainActivity.width;

class Player {

    int x, y;
    private int xp, yp;
    Vector v;
    private VectorDrawableCompat drawable;

    Player(int resId, Context context, int num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        v = new Vector(0, 0);
        x = width / 2;
        if (num == 1) {
            y = (int) (1.4 * playerScale);
        } else {
            y = height - (int) (1.4 * playerScale);
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
        v.x = (x - xp + 0.) / (sec - psec);
        v.y = (y - yp + 0.) / (sec - psec);
        v.setVector(v.x, v.y);
        xp = x;
        yp = y;
    }

}

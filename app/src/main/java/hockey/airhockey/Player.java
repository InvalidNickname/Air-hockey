package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.playerScale;
import static hockey.airhockey.MainActivity.width;

class Player {

    double x, y;
    private double xp, yp;
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
            drawable.setBounds((int) x - playerScale, (int) y - playerScale, (int) x + playerScale, (int) y + playerScale);
        }
        xp = x;
        yp = y;
    }

    void draw(Canvas canvas) {
        drawable.draw(canvas);
    }

    void update(long delta, boolean isAnimation) {
        if (isAnimation) {
            x += v.x * delta;
            y += v.y * delta;
        }
        drawable.setBounds((int) x - playerScale, (int) y - playerScale, (int) x + playerScale, (int) y + playerScale);
    }

    void setV(long delta) {
        v.x = (x - xp) / delta;
        v.y = (y - yp) / delta;
        v.setVector(v.x, v.y);
        xp = x;
        yp = y;
    }

}

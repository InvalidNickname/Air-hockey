package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.settings;

class Player {

    final Vector v;
    private final VectorDrawableCompat drawable;
    double x, y;
    private double xp, yp;

    Player(int resId, Context context, int num) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        v = new Vector(0, 0);
        x = settings.width / 2;
        if (num == 1) {
            y = (int) (1.4 * settings.playerScale);
        } else {
            y = settings.height - (int) (1.4 * settings.playerScale);
        }
        if (drawable != null) {
            drawable.setBounds((int) x - settings.playerScale, (int) y - settings.playerScale, (int) x + settings.playerScale, (int) y + settings.playerScale);
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
        drawable.setBounds((int) x - settings.playerScale, (int) y - settings.playerScale, (int) x + settings.playerScale, (int) y + settings.playerScale);
    }

    void setV(long delta) {
        v.x = (x - xp) / delta;
        v.y = (y - yp) / delta;
        v.setVector(v.x, v.y);
        xp = x;
        yp = y;
    }

}

package hockey.airhockey;

import android.content.Context;
import android.graphics.Canvas;
import android.support.graphics.drawable.VectorDrawableCompat;

import static hockey.airhockey.MainActivity.height;
import static hockey.airhockey.MainActivity.puckScale;
import static hockey.airhockey.MainActivity.width;

class Puck {

    int x, y;
    Vector v;
    private VectorDrawableCompat drawable;

    Puck(int resId, Context context) {
        drawable = VectorDrawableCompat.create(context.getResources(), resId, null);
        v = new Vector(0, 0);
        x = width / 2;
        y = height / 2;
        if (drawable != null) {
            drawable.setBounds(x - puckScale, y - puckScale, x + puckScale, y + puckScale);
        }
    }

    void update(long sec, long psec) {
        x += v.x * (sec - psec);
        y += v.y * (sec - psec);
       /* if (vX != 0 & vY != 0) {
            boolean upOrDown = vY > 0;
            double alpha = Math.acos(vX / Math.sqrt(Math.pow(vX, 2) + Math.pow(vY, 2)));
            double v = Math.sqrt(Math.pow(vX, 2) + Math.pow(vY, 2));
            vX = 0.999 * Math.cos(alpha) * v;
            vY = 0.999 * Math.sin(alpha) * v;
            if (!upOrDown) {
                vY = -vY;
            }
        } else {
            vY *= 0.999;
            vX *= 0.999;
        }*/
        drawable.setBounds(x - puckScale, y - puckScale, x + puckScale, y + puckScale);
    }

    void drawPuck(Canvas canvas) {
        drawable.draw(canvas);
    }
}
